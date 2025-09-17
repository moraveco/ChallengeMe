package com.moraveco.challengeme.data

import kotlinx.serialization.Serializable

@Serializable
data class Like(
    val id: String,
    val posterUid: String,
    val likeUid: String,
    val postId: String,
    val time: String = java.time.LocalDate.now().toString()
)



data class LikeResponse(
    val status: String,
    val message: String,
    val error: String? = null
)

fun List<Like>.containsPostId(id: String) : Boolean {
    return id == find { it.postId == id }?.postId
}

fun List<Like>.likedPost(uid: String) : Like? {
    return find { it.time == java.time.LocalDate.now().toString() && it.likeUid == uid }
}

@Serializable
data class DeleteLike(
    val id: String
)


