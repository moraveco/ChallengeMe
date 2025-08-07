package com.moraveco.challengeme.notifications

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    //@POST("v1/projects/chat-app-19cd2/messages:send")
    @Headers("Content-Type: application/json")
    @POST("v1/projects/challnageme/messages:send")
    suspend fun postNotification(
        @Body message: FcmMessage
    ): Response<Unit>
}