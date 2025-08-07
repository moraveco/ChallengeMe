package com.moraveco.challengeme.data

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Post(
    val id: String,
    val uid: String,
    val image: String,
    val description: String,
    val time: String,
    val isPublic: String,
    val isVideo: String,
    val profileImageUrl: String? = null,
    val name: String? = null,
    val lastName: String? = null,
    val likes_count: String? = "0",
    val comments_count: String? = "0",
    val token: String? = null

){
    companion object{
        fun empty() : Post{
            return Post("", "", "", "", "", "false", "false")
        }
    }

}
data class UploadResponse(
    val success: Boolean,
    val message: String,
    val file_path: String
)

// NewPostResponse.kt
data class NewPostResponse(
    val success: Boolean,
    val message: String
)

data class UpdatePost(
    val id: String,
    val description: String,
    val fotka: String? = null
)

data class DeletePost(
    val id: String
)
