package com.moraveco.challengeme.data

import kotlinx.serialization.Serializable

data class UpdateProfileData(
    val uid: String,
    val name: String,
    val bio: String,
    val profileImageUrl: String
)

@Serializable
data class UpdateRead(
    val id: String
)
