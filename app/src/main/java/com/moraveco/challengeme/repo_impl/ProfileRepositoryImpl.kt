package com.moraveco.challengeme.repo_impl

import android.util.Log
import com.moraveco.challengeme.api.ApiService
import com.moraveco.challengeme.data.Follow
import com.moraveco.challengeme.repo.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import retrofit2.await
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(private val apiService: ApiService) : ProfileRepository {
    override suspend fun getFollows(uid: String): List<Follow> {
        return withContext(Dispatchers.IO){
            val response = apiService.getFollows(uid, "?*K.sj-.*;P//§.OrA?")
            response.body() ?: emptyList()
        }
    }

    override suspend fun followUser(follow: Follow) {
        try {
            apiService.followUser(follow)
        } catch (e: Exception) {
            // Handle error, e.g., log or throw a custom exception
            Log.v("error", e.toString())
        }
    }

    override suspend fun unfollowUser(id: String) {
        try {
            //val unfollowUserData = UnfollowUserData(id)
            //apiService.unfollowUser(unfollowUserData)
        } catch (e: Exception) {
            // Handle error, e.g., log or throw a custom exception
            Log.v("error", e.toString())
        }
    }
}