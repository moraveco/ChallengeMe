package com.moraveco.challengeme.repo_impl

import android.util.Log
import com.moraveco.challengeme.api.ApiService
import com.moraveco.challengeme.data.DailyChallenge
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.di.MediaCompressionUtil
import com.moraveco.challengeme.repo.DailyChallengeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.time.LocalDate
import javax.inject.Inject

class DailyChallengeRepositoryImpl @Inject constructor(private val apiService: ApiService) : DailyChallengeRepository {
    override suspend fun getDailyChallenge(): DailyChallenge {
        return withContext(Dispatchers.IO) {
            val response = apiService.getDailyChallenge()
            response.body()?.first() ?: DailyChallenge("1", "No challenge available for thid day", "Žádný úkol na dnešek", "Žiadna úloha na dnešok", LocalDate.now().toString())// Assuming parseMessagesList expects a String
        }
    }

    override suspend fun uploadPhoto(file: MultipartBody.Part): Response<String> {
        return try {
            apiService.uploadPostPhoto(file)
        } catch (e: Exception) {
            Response.error(500,
                "Network call has failed".toResponseBody("text/plain".toMediaTypeOrNull())
            )
            // Create a custom error response
        }
    }

    override suspend fun uploadVideo(file: MultipartBody.Part): Response<String> {
        return try {
            apiService.uploadPostVideo(file)
        } catch (e: Exception) {
            Response.error(500,
                "Network call has failed".toResponseBody("text/plain".toMediaTypeOrNull())
            )
            // Create a custom error response
        }
    }

    override suspend fun createPost(post: Post) {
        try {
            apiService.createPost(post, "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")
        }catch (e: Exception){
            Log.v("postError", e.toString())
        }
    }
}