package com.moraveco.challengeme.ui.posts

import com.moraveco.challengeme.data.Like
import com.moraveco.challengeme.data.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikeManager @Inject constructor() {
    private val _likesState = MutableStateFlow<Map<String, LikeState>>(emptyMap())
    val likesState: StateFlow<Map<String, LikeState>> = _likesState.asStateFlow()

    private val _todayLikedPostId = MutableStateFlow<String?>(null)
    val todayLikedPostId: StateFlow<String?> = _todayLikedPostId.asStateFlow()

    data class LikeState(
        val postId: String,
        val isLiked: Boolean,
        val likeCount: Int,
        val likeId: String? = null,
        val isProcessing: Boolean = false
    )

    /**
     * Initialize likes state from backend data
     */
    fun initializeLikes(likes: List<Like>, posts: List<Post>, currentUserId: String) {
        val newState = mutableMapOf<String, LikeState>()

        // Find today's liked post
        val todayLike = likes.find { like ->
            like.likeUid == currentUserId && isToday(like.time)
        }
        _todayLikedPostId.value = todayLike?.postId

        // Build like state for each post
        posts.forEach { post ->
            val postLikes = likes.filter { it.postId == post.id }
            val userLike = postLikes.find { it.likeUid == currentUserId }

            newState[post.id] = LikeState(
                postId = post.id,
                isLiked = userLike != null,
                likeCount = post.likes_count?.toIntOrNull() ?: postLikes.size,
                likeId = userLike?.id
            )
        }

        _likesState.value = newState
    }

    /**
     * Check if user can like a specific post
     */
    fun canLikePost(post: Post, currentUserId: String): Boolean {
        // Rule 1: Cannot like your own post
        if (post.uid == currentUserId) return false

        // Rule 2: Cannot like if already liked a post today
        if (_todayLikedPostId.value != null) return false

        // Rule 3: Cannot like ads (assuming there's an isAd field)
        // if (post.isAd == true) return false

        // Rule 4: Can only like posts from today
        if (!isPostFromToday(post.time)) return false

        // Rule 5: Cannot like if already liked this post
        val likeState = _likesState.value[post.id]
        if (likeState?.isLiked == true) return false

        return true
    }

    /**
     * Check if user can unlike a specific post
     */
    fun canUnlikePost(post: Post, currentUserId: String): Boolean {
        // Can only unlike if:
        // 1. User has liked this post
        val likeState = _likesState.value[post.id]
        if (likeState?.isLiked != true) return false

        // 2. The post is from today (cannot unlike old posts)
        if (!isPostFromToday(post.time)) return false

        // 3. This is the post liked today
        if (_todayLikedPostId.value != post.id) return false

        return true
    }

    /**
     * Optimistically update like state
     */
    fun optimisticLike(postId: String, likeId: String) {
        val currentState = _likesState.value[postId] ?: return

        _likesState.value = _likesState.value.toMutableMap().apply {
            this[postId] = currentState.copy(
                isLiked = true,
                likeCount = currentState.likeCount + 1,
                likeId = likeId,
                isProcessing = true
            )
        }

        _todayLikedPostId.value = postId
    }

    /**
     * Optimistically update unlike state
     */
    fun optimisticUnlike(postId: String) {
        val currentState = _likesState.value[postId] ?: return

        _likesState.value = _likesState.value.toMutableMap().apply {
            this[postId] = currentState.copy(
                isLiked = false,
                likeCount = (currentState.likeCount - 1).coerceAtLeast(0),
                likeId = null,
                isProcessing = true
            )
        }

        _todayLikedPostId.value = null
    }

    /**
     * Confirm like/unlike action after backend response
     */
    fun confirmLikeAction(postId: String, success: Boolean) {
        val currentState = _likesState.value[postId] ?: return

        if (success) {
            // Just remove processing flag
            _likesState.value = _likesState.value.toMutableMap().apply {
                this[postId] = currentState.copy(isProcessing = false)
            }
        } else {
            // Revert the optimistic update
            _likesState.value = _likesState.value.toMutableMap().apply {
                this[postId] = currentState.copy(
                    isLiked = !currentState.isLiked,
                    likeCount = if (currentState.isLiked) {
                        currentState.likeCount - 1
                    } else {
                        currentState.likeCount + 1
                    },
                    isProcessing = false
                )
            }

            // Revert today's liked post
            if (currentState.isLiked) {
                _todayLikedPostId.value = null
            } else {
                _todayLikedPostId.value = postId
            }
        }
    }

    /**
     * Get like state for a specific post
     */
    fun getLikeState(postId: String): LikeState? {
        return _likesState.value[postId]
    }

    private fun isPostFromToday(postTime: String): Boolean {
        return try {
            val postDate = LocalDate.parse(postTime)
            val today = LocalDate.now()
            postDate.isEqual(today)
        } catch (e: DateTimeParseException) {
            false
        }
    }

    private fun isToday(dateString: String): Boolean {
        return try {
            val date = LocalDate.parse(dateString)
            val today = LocalDate.now()
            date.isEqual(today)
        } catch (e: DateTimeParseException) {
            false
        }
    }
}