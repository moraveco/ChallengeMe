package com.moraveco.challengeme.data

data class Notification(
    val id: String,
    val postId: String,
    val likeUid: String? = null,
    val fotka: String? = null,
    val profileImageUrl: String? = null,
    val time: String,
    val isRead: Boolean,
    val comment: String? = null,
    val name: String

)
