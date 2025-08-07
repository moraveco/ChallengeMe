package com.moraveco.challengeme.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class BlockUser(
    val id: String = UUID.randomUUID().toString(),
    val uid: String,
    val blockUid: String,
    val time: String
)
