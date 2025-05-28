package com.moraveco.challengeme.repo

import com.moraveco.challengeme.data.LoginRequest
import com.moraveco.challengeme.data.LoginResult
import com.moraveco.challengeme.data.RegisterData
import com.moraveco.challengeme.data.SendPasswordData
import com.moraveco.challengeme.data.User

interface RegisterRepository {
    suspend fun loginUser(loginData: LoginRequest): LoginResult
    suspend fun registerUser(registerData: RegisterData) : LoginResult
    suspend fun insertUser(user: User) : LoginResult
    suspend fun sendPassword(sendPasswordData: SendPasswordData)
}