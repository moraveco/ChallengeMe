package com.moraveco.challengeme.data

import kotlinx.serialization.Serializable

@Serializable
data class FollowRequest(
    val id: String,
    val uid: String,
    val name: String,
    val profileImageUrl: String? = null
)