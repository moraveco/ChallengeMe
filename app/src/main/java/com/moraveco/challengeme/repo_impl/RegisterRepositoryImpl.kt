package com.moraveco.challengeme.repo_impl

import android.util.Log
import com.moraveco.challengeme.api.ApiService
import com.moraveco.challengeme.data.LoginRequest
import com.moraveco.challengeme.data.LoginResponse
import com.moraveco.challengeme.data.LoginResult
import com.moraveco.challengeme.data.RegisterData
import com.moraveco.challengeme.data.SendPasswordData
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.repo.RegisterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(private val apiService: ApiService) : RegisterRepository {
    override suspend fun registerUser(registerData: RegisterData) : LoginResult {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(registerData, "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")
                if (response.isSuccessful) {
                    LoginResult.Success(registerData.uid, "")

                } else {
                    when (response.code()) {
                        401 -> LoginResult.AuthenticationFailed // HTTP 401 for unauthorized
                        else -> LoginResult.UnexpectedError("Error code: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                LoginResult.UnexpectedError(e.message)
            }
        }
    }

    override suspend fun insertUser(user: User) : LoginResult {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.insertNewUser(user, "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")
                if (response.isSuccessful) {
                    LoginResult.Success(user.uid, "")
                } else {
                    when (response.code()) {
                        401 -> LoginResult.AuthenticationFailed // HTTP 401 for unauthorized
                        else -> LoginResult.UnexpectedError("Error code: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                LoginResult.UnexpectedError(e.message)
            }
        }
    }

    override suspend fun sendPassword(sendPasswordData: SendPasswordData) {
        return withContext(Dispatchers.IO){
            try {
                apiService.sendPassword(sendPasswordData)
            }catch (e: Exception){
                Log.v("errorRegister", e.toString())
            }
        }
    }

    override suspend fun loginUser(loginData: LoginRequest): LoginResult {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login("278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf", loginData)
                if (response.isSuccessful) {
                    val uid = response.body()?.uid
                    val name = response.body()?.name
                    if (!uid.isNullOrEmpty() && !name.isNullOrEmpty()) {
                        LoginResult.Success(uid, name)
                    } else {
                        LoginResult.EmailNotFound // No UID indicates email doesn't exist
                    }
                } else {
                    when (response.code()) {
                        401 -> LoginResult.AuthenticationFailed // HTTP 401 for unauthorized
                        else -> LoginResult.UnexpectedError("Error code: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                LoginResult.UnexpectedError(e.message)
            }
        }
    }


}