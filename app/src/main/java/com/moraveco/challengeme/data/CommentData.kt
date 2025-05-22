package com.moraveco.challengeme.data

import kotlinx.serialization.Serializable

@Serializable
data class CommentData(
    val id: String,
    val posterUid: String,
    val postId: String,
    val comment: String,
)
