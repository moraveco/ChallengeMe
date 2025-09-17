package com.moraveco.challengeme.data

import okhttp3.MultipartBody

data class RegisterData(
    val uid: String,
    val email: String,
    val password: String,
    val name: String,
    val lastName: String,
    val country: String,
    val profileImage: MultipartBody.Part? = null,
    val secondImage: MultipartBody.Part? = null
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: User? = null
)




