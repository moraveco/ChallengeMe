package com.moraveco.challengeme.repo

import com.moraveco.challengeme.notifications.FcmMessage


interface NotificationRepository {
    suspend fun sendNotification(message: FcmMessage)
}