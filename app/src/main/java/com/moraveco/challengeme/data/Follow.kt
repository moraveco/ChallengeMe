package com.moraveco.challengeme.data

import kotlinx.serialization.Serializable

@Serializable
data class Follow(
    val id: String,
    val senderUid: String,
    val receiverUid: String,
    val isAccept: String,
    val time: String
)

@Serializable
data class AcceptRequest(
    val id: String
)
