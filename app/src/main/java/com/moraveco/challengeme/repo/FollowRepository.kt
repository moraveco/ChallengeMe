package com.moraveco.challengeme.repo

import com.moraveco.challengeme.data.AcceptRequest
import com.moraveco.challengeme.data.FollowRequest

interface FollowRepository {
    suspend fun getRequests(uid: String) : List<FollowRequest>
    suspend fun acceptFollow(id: AcceptRequest)
}