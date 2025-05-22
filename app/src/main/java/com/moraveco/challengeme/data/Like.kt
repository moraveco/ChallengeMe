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

@Serializable
data class DeleteLike(
    val id: String
)

fun List<Like>.containsPostId(id: String) : Boolean {
    return id == find { it.postId == id }?.postId
}
