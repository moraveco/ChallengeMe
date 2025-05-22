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
    object Trending

    @Serializable
    object Profile

    @Serializable
    data class Post(val postId: String)
}