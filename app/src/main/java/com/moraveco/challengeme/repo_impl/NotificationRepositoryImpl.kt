package com.moraveco.challengeme.repo_impl

import android.util.Log
import com.moraveco.challengeme.notifications.FcmMessage
import com.moraveco.challengeme.notifications.NotificationAPI
import com.moraveco.challengeme.repo.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(private val apiService: NotificationAPI) : NotificationRepository {
    override suspend fun sendNotification(message: FcmMessage) {
        try {
            val response = apiService.postNotification(message)
            if (response.isSuccessful) {
                Log.v("notifi", "success")
            }else{
                Log.v("notifi", response.message())

            }
        } catch (e: Exception) {
            // Handle error, e.g., log or throw a custom exception
            Log.v("error", e.toString())
        }
    }
}