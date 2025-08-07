package com.moraveco.challengeme.notifications

data class FcmMessage(
    val message: Message
)

data class Message(
    val token: String? = null,
    val data: HashMap<String, String>,
)

data class Notification(
    val title: String,
    val body: String
)