package com.moraveco.challengeme.repo

import com.moraveco.challengeme.data.Follow


interface ProfileRepository {
    suspend fun getFollows(uid: String) : List<Follow>
    suspend fun followUser(follow: Follow)
    suspend fun unfollowUser(id: String)
}