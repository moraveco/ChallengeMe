package com.moraveco.challengeme.repo

import com.moraveco.challengeme.data.AcceptRequest
import com.moraveco.challengeme.data.Follow
import com.moraveco.challengeme.data.FollowRequest
import com.moraveco.challengeme.data.Friend
import com.moraveco.challengeme.data.User

interface FollowRepository {
    suspend fun getFriends(uid: String) : List<Friend>
    suspend fun deleteFriend(id: String)
    suspend fun acceptFollow(id: AcceptRequest)
    suspend fun followUser(followRequest: Follow)
}