package com.moraveco.challengeme.data

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileData(
    val uid: String,
    val name: String,
    val lastName: String,
    val email: String,
    val profileImageUrl: String
)
