package com.moraveco.challengeme.user_settings

import com.moraveco.challengeme.data.User
import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val uid: String ="-1",
    val name: String = "",
    val lastname: String = "",
    val bio: String = "",
    val profileImageUrl: String? = null,
    val secondImageUrl: String? = null,
    val token: String= "-1",
    val country: String = "",
    val email: String = ""
)

fun UserSettings.toUser(): User {
    return User(uid, name, lastname, bio, profileImageUrl, secondImageUrl, token, country, email)
}

fun User.toUserSettings(): UserSettings{
    return UserSettings(
        uid, name, lastname, bio, profileImageUrl, secondImageUrl, country, email
    )
}