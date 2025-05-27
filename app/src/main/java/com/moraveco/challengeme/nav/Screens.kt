package com.moraveco.challengeme.nav

import kotlinx.serialization.Serializable

sealed class Screens{
    @Serializable
    object Register


    @Serializable
    object Login


    @Serializable
    object Home

    @Serializable
    object Add

    @Serializable
    object Scoreboard

    @Serializable
    object Profile

    @Serializable
    data class Post(val postId: String)

    @Serializable
    object Search

    @Serializable
    object Request

    @Serializable
    object Menu

    @Serializable
    data class UserProfile(val userId: String)

    @Serializable
    data class EditProfile(val userId: String)
}