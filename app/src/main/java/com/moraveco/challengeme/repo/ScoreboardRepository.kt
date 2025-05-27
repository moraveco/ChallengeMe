package com.moraveco.challengeme.repo

import com.moraveco.challengeme.data.LeadeboardUser

interface ScoreboardRepository {
    suspend fun getToday() : List<LeadeboardUser>
    suspend fun getGlobal() : List<LeadeboardUser>
    suspend fun getFriends(uid: String) : List<LeadeboardUser>
}