package com.moraveco.challengeme.ui.posts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moraveco.challengeme.data.Comment
import com.moraveco.challengeme.data.CommentData
import com.moraveco.challengeme.data.Like
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.data.likedPost
import com.moraveco.challengeme.repo_impl.PostRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepositoryImpl,
    val likeManager: LikeManager
) : ViewModel() {
    // Home screen state
    private val _homeUiState = MutableStateFlow(PostsUiState())
    val homeUiState: StateFlow<PostsUiState> = _homeUiState.asStateFlow()

    // Profile posts
    private val _profilePosts = MutableStateFlow(emptyList<Post>())
    val profilePosts: StateFlow<List<Post>> = _profilePosts.asStateFlow()

    // Post detail state
    private val _postDetailState = MutableStateFlow(PostDetailUiState())
    val postDetailState: StateFlow<PostDetailUiState> = _postDetailState.asStateFlow()

    // Combine like manager state with UI state
    val combinedHomeState: StateFlow<CombinedHomeState> = combine(
        homeUiState,
        likeManager.likesState,
        likeManager.todayLikedPostId
    ) { homeState, likesState, todayLikedPostId ->
        CombinedHomeState(
            posts = homeState,
            likesState = likesState,
            todayLikedPostId = todayLikedPostId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CombinedHomeState()
    )

    fun loadHomePosts(myUid: String) {
        viewModelScope.launch {
            _homeUiState.value = _homeUiState.value.copy(isLoading = true)

            try {
                // Load all data in parallel
                val friends = repository.getFriendsPosts(myUid)
                val public = repository.getPublicPosts(myUid)
                val history = repository.getHistoryPosts(myUid)
                val likes = repository.getLikes(myUid)

                // Initialize like manager with fresh data
                val allPosts = friends + public + history
                likeManager.initializeLikes(likes, allPosts, myUid)

                _homeUiState.value = PostsUiState(
                    friendsPosts = friends,
                    publicPosts = public,
                    historyPosts = history,
                    isLoading = false,
                    currentUserId = myUid
                )
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error loading home posts", e)
                _homeUiState.value = _homeUiState.value.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                )
            }
        }
    }

    fun toggleLike(post: Post, currentUserId: String) {
        val currentLikeState = likeManager.getLikeState(post.id)
        val isCurrentlyLiked = currentLikeState?.isLiked == true

        // Check permissions
        val canPerformAction = if (isCurrentlyLiked) {
            likeManager.canUnlikePost(post, currentUserId)
        } else {
            likeManager.canLikePost(post, currentUserId)
        }

        if (!canPerformAction) {
            Log.d("PostViewModel", "Cannot perform like action on post ${post.id}")
            return
        }

        // Perform optimistic update
        val likeId = if (isCurrentlyLiked) {
            currentLikeState?.likeId
        } else {
            UUID.randomUUID().toString()
        }

        if (isCurrentlyLiked) {
            likeManager.optimisticUnlike(post.id)
        } else {
            likeManager.optimisticLike(post.id, likeId!!)
        }

        // Send to backend
        viewModelScope.launch {
            try {
                val like = Like(
                    id = likeId ?: UUID.randomUUID().toString(),
                    posterUid = post.uid,
                    likeUid = currentUserId,
                    postId = post.id,
                    time = java.time.LocalDate.now().toString()
                )

                val result = repository.handleLike(like)

                if (result.isSuccess) {
                    likeManager.confirmLikeAction(post.id, true)

                    // If viewing post detail, update it
                    if (_postDetailState.value.post?.id == post.id) {
                        updatePostDetailLikes(post.id, currentUserId)
                    }
                } else {
                    // Revert optimistic update
                    likeManager.confirmLikeAction(post.id, false)
                    _homeUiState.value = _homeUiState.value.copy(
                        error = "Failed to update like"
                    )
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error toggling like", e)
                // Revert optimistic update
                likeManager.confirmLikeAction(post.id, false)
                _homeUiState.value = _homeUiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun loadPostDetail(postId: String) {
        viewModelScope.launch {
            try {
                _postDetailState.value = _postDetailState.value.copy(isLoading = true)

                val post = repository.getPostById(postId)
                val comments = repository.getComments(postId)
                val likes = repository.getLikes(postId)

                _postDetailState.value = PostDetailUiState(
                    post = post,
                    comments = comments,
                    likes = likes,
                    likeState = likeManager.getLikeState(postId),
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error loading post detail", e)
                _postDetailState.value = _postDetailState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private suspend fun updatePostDetailLikes(postId: String, currentUserId: String) {
        try {
            val likes = repository.getLikes(postId)
            _postDetailState.value = _postDetailState.value.copy(
                likes = likes,
                likeState = likeManager.getLikeState(postId)
            )
        } catch (e: Exception) {
            Log.e("PostViewModel", "Error updating post detail likes", e)
        }
    }

    fun sendComment(commentData: CommentData) {
        viewModelScope.launch {
            try {
                repository.sendComment(commentData)
                val comments = repository.getComments(commentData.postId)
                _postDetailState.value = _postDetailState.value.copy(comments = comments)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error sending comment", e)
            }
        }
    }

    fun deletePost(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.deletePost(id)
                onSuccess()
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error deleting post", e)
            }
        }
    }

    fun sendReport(
        email: String,
        myUid: String,
        postId: String,
        image: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.reportPost(email, myUid, postId, image)
                onSuccess()
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error sending report", e)
            }
        }
    }

    fun getPostsById(uid: String) {
        viewModelScope.launch {
            try {
                val posts = repository.getPostsById(uid)
                _profilePosts.value = posts
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error getting posts by id", e)
            }
        }
    }
}

data class CombinedHomeState(
    val posts: PostsUiState = PostsUiState(),
    val likesState: Map<String, LikeManager.LikeState> = emptyMap(),
    val todayLikedPostId: String? = null
)

// ------------------------- UI STATE DATA CLASSES -------------------------
data class PostsUiState(
    val publicPosts: List<Post> = emptyList(),
    val historyPosts: List<Post> = emptyList(),
    val friendsPosts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUserId: String = ""
)

data class PostDetailUiState(
    val post: Post? = null,
    val likes: List<Like> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val likeState: LikeManager.LikeState? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)