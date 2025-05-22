package com.moraveco.challengeme.repo

import com.moraveco.challengeme.data.ProfileUser
import com.moraveco.challengeme.data.User


interface UserRepository {
    suspend fun getUserById(uid: String) : Result<ProfileUser, Error>
    suspend fun getAllUsers() : List<User>
    //suspend fun updateStatus(updateStatus: UpdateStatus)
    //suspend fun updateToken(updateToken: UpdateToken)
}