package com.moraveco.challengeme.data

import kotlinx.serialization.Serializable

@Serializable
data class Follow(
    val id: String,
    val followUid: String,
    val followerUid: String,
    val isAccept: String
)

@Serializable
data class AcceptRequest(
    val id: String
)
