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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepositoryImpl
) : ViewModel() {

    // ------------------------- HOME SCREEN STATE -------------------------
    private val _homeUiState = MutableStateFlow(PostsUiState())
    val homeUiState: StateFlow<PostsUiState> = _homeUiState.asStateFlow()

    private val _profilePosts = MutableStateFlow(emptyList<Post>())
    val profilePosts: StateFlow<List<Post>> get() = _profilePosts

    fun getPostsById(uid: String){
        viewModelScope.launch {
            try {
                val posts = repository.getPostsById(uid)
                _profilePosts.value = posts
            } catch (e: Exception) {
                Log.v("error", e.toString())
            }
        }
    }

    fun loadHomePosts(myUid: String) {
        viewModelScope.launch {
            _homeUiState.value = _homeUiState.value.copy(isLoading = true,)
            try {
                val friends = repository.getFriendsPosts(myUid)
                val public = repository.getPublicPosts(myUid)
                val history = repository.getHistoryPosts(myUid)
                val likes = repository.getLikes(myUid)

                val todayLike = likes.likedPost(myUid)
                val todayLikePostId = todayLike?.postId

                _homeUiState.value = PostsUiState(
                    friendsPosts = friends,
                    publicPosts = public,
                    historyPosts = history,
                    todayLikePostId = todayLikePostId,
                    hasLikedToday = todayLike != null,
                    userLikes = likes, // Add likes to state
                    isLoading = false
                )
            } catch (e: Exception) {
                _homeUiState.value = _homeUiState.value.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                )
            }
        }
    }

    // ------------------------- POST DETAIL SCREEN STATE -------------------------
    private val _postDetailState = MutableStateFlow(PostDetailUiState())
    val postDetailState: StateFlow<PostDetailUiState> = _postDetailState.asStateFlow()

    fun loadPostDetail(postId: String) {
        viewModelScope.launch {
            try {
                _postDetailState.value = _postDetailState.value.copy(isLoading = true)
                val post = repository.getPostById(postId)
                val comments = repository.getComments(postId)
                val likes = repository.getLikes(postId)
                _postDetailState.value = _postDetailState.value.copy(
                    post = post,
                    comments = comments,
                    likes = likes,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("PostViewModel", "loadPostDetail error", e)
                _postDetailState.value = _postDetailState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    // Helper function to check if a post is from today
    private fun isPostFromToday(postTime: String): Boolean {
        return try {
            val postDate = LocalDate.parse(postTime)
            val today = LocalDate.now()
            postDate.isEqual(today)
        } catch (e: DateTimeParseException) {
            false
        }
    }

    // Helper function to check if user already liked a post
    private fun hasUserLikedPost(likes: List<Like>, postId: String, userId: String): Boolean {
        return likes.any { it.postId == postId && it.likeUid == userId }
    }

    // Check if user can like a post based on all conditions
    fun canLikePost(post: Post, myUid: String, hasLikedToday: Boolean, userLikes: List<Like>): Boolean {
        // Can't like your own posts
        if (post.uid == myUid) return false

        // Can't like if already liked today
        if (hasLikedToday) return false

        // Can only like posts from today
        if (!isPostFromToday(post.time)) return false

        // Can't like if already liked this specific post
        if (hasUserLikedPost(userLikes, post.id, myUid)) return false

        // Check if post is an ad (assuming you have this field)
        // if (post.isAd == true) return false

        return true
    }

    // Check if user can unlike a post
    fun canUnlikePost(post: Post, myUid: String, userLikes: List<Like>): Boolean {
        // Can only unlike if you previously liked it
        return hasUserLikedPost(userLikes, post.id, myUid) && isPostFromToday(post.time)
    }

    fun toggleLikeOnPost(post: Post, currentUserUid: String) {
        viewModelScope.launch {
            try {
                val like = Like(
                    id = UUID.randomUUID().toString(),
                    posterUid = post.uid,
                    likeUid = currentUserUid,
                    postId = post.id,
                    time = LocalDate.now().toString()
                )

                val result = repository.handleLike(like)

                // Reload both home posts and post detail if we're viewing detail
                loadHomePosts(currentUserUid)

                // If we're in post detail, reload that too
                if (_postDetailState.value.post?.id == post.id) {
                    loadPostDetail(post.id)
                }

            } catch (e: Exception) {
                Log.e("PostViewModel", "toggleLikeOnPost error", e)
                _homeUiState.value = _homeUiState.value.copy(error = e.message)
                _postDetailState.value = _postDetailState.value.copy(error = e.message)
            }
        }
    }

    fun sendComment(commentData: CommentData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.sendComment(commentData)
                val comments = repository.getComments(commentData.postId)
                _postDetailState.value = _postDetailState.value.copy(comments = comments)
            } catch (e: Exception) {
                Log.e("PostViewModel", "sendComment error", e)
            }
        }
    }

    fun deletePost(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.deletePost(id)
                onSuccess()
            } catch (e: Exception) {
                Log.e("PostViewModel", "deletePost error", e)
            }
        }
    }

    fun sendReport(email: String, myUid: String, postId: String, image: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.reportPost(email, myUid, postId, image)
                onSuccess()
            } catch (e: Exception) {
                Log.e("PostViewModel", "sendReport error", e)
            }
        }
    }
}

// ------------------------- UI STATE DATA CLASSES -------------------------
data class PostsUiState(
    val publicPosts: List<Post> = emptyList(),
    val historyPosts: List<Post> = emptyList(),
    val friendsPosts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val todayLikePostId: String? = "",
    val hasLikedToday: Boolean = false,
    val userLikes: List<Like> = emptyList() // Add user likes to state
)

data class PostDetailUiState(
    val post: Post? = null,
    val likes: List<Like> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)