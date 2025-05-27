package com.moraveco.challengeme.repo_impl

import android.util.Log
import com.moraveco.challengeme.api.ApiService
import com.moraveco.challengeme.data.AcceptRequest
import com.moraveco.challengeme.data.Follow
import com.moraveco.challengeme.data.FollowRequest
import com.moraveco.challengeme.data.Friend
import com.moraveco.challengeme.data.UpdateProfileData
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.repo.FollowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FollowRepositoryImpl @Inject constructor(private val apiService: ApiService) : FollowRepository {
    override suspend fun getFriends(uid: String): List<Friend> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getFriends(uid, "3e38*#^#kds82K")
            response.body() ?: emptyList()// Assuming parseMessagesList expects a String
        }
    }

    override suspend fun deleteFriend(id: String) {
        return withContext(Dispatchers.IO){
            try {
                apiService.deleteFriend(AcceptRequest(id), "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")
            }catch (e: Exception){
                Log.v("error", e.toString())
            }
        }
    }

    override suspend fun acceptFollow(id: AcceptRequest) {
        return withContext(Dispatchers.IO){
            try {
                apiService.acceptFollow(id, "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")
            }catch (e: Exception){
                Log.v("acceptFriend", e.toString())
            }
        }
    }

    override suspend fun followUser(followRequest: Follow) {
        return withContext(Dispatchers.IO){
            try {
                apiService.followUser(followRequest, "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")
            }catch (e: Exception){
                Log.v("error", e.toString())
            }
        }
    }
}