package com.moraveco.challengeme.repo_impl

import android.util.Log
import android.util.MalformedJsonException
import com.moraveco.challengeme.api.ApiService
import com.moraveco.challengeme.data.LeadeboardUser
import com.moraveco.challengeme.repo.ScoreboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ScoreboardRepositoryIml @Inject constructor(private val apiService: ApiService) : ScoreboardRepository {
    override suspend fun getToday(): List<LeadeboardUser> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTodayLeaderboard("3e38*#^#kds82K")
                response.body() ?: emptyList()// Assuming parseMessagesList expects a String
            }catch (e: MalformedJsonException) {
                Log.e("API_ERROR", "Malformed JSON: ${e.message}", e)
                emptyList<LeadeboardUser>()
                // Handle the case where JSON is malformed
                // Inform the user or try a fallback
            }
        }
    }

    override suspend fun getGlobal(): List<LeadeboardUser> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getGlobalLeaderboard("3e38*#^#kds82K")
                response.body() ?: emptyList()// Assuming parseMessagesList expects a String
            }catch (e: MalformedJsonException) {
                Log.e("API_ERROR", "Malformed JSON: ${e.message}", e)
                emptyList<LeadeboardUser>()
                // Handle the case where JSON is malformed
                // Inform the user or try a fallback
            }

        }
    }

    override suspend fun getFriends(uid: String): List<LeadeboardUser> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getFriendsLeaderboard(uid, "3e38*#^#kds82K")
                response.body() ?: emptyList()// Assuming parseMessagesList expects a String
            }catch (e: MalformedJsonException) {
                Log.e("API_ERROR", "Malformed JSON: ${e.message}", e)
                emptyList<LeadeboardUser>()
                // Handle the case where JSON is malformed
                // Inform the user or try a fallback
            }
        }
    }
}