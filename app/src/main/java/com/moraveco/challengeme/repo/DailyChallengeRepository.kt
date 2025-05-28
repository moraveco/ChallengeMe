package com.moraveco.challengeme.repo

import com.moraveco.challengeme.data.DailyChallenge
import com.moraveco.challengeme.data.Post
import okhttp3.MultipartBody
import retrofit2.Response

interface DailyChallengeRepository {
    suspend fun getDailyChallenge() : DailyChallenge
    suspend fun uploadPhoto(file: MultipartBody.Part) : Response<String>
    suspend fun uploadVideo(file: MultipartBody.Part) : Response<String>
    suspend fun createPost(post: Post)
}