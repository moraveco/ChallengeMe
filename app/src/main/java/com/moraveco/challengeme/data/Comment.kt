package com.moraveco.challengeme.data

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: String,
    val posterUid: String,
    val postId: String,
    val comment: String,
    val name: String,
    val profileImageUrl: String
)
