package com.moraveco.challengeme.data

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val uid: String?,
    val name: String?
)

sealed class LoginResult {
    data class Success(val userId: String, val name: String) : LoginResult()
    data object EmailNotFound : LoginResult()
    data object AuthenticationFailed : LoginResult() // General failure to authenticate
    data class UnexpectedError(val message: String?) : LoginResult()
}

