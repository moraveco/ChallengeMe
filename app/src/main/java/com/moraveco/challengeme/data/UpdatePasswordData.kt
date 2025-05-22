package com.moraveco.challengeme.data

data class UpdatePasswordData(
    val uid: String,
    val password: String,
    val oldpassword: String
)

data class SendPasswordData(
    val email: String
)
