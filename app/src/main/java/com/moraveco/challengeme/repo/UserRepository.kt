package com.moraveco.challengeme.repo

import com.moraveco.challengeme.data.BlockUser
import com.moraveco.challengeme.data.ProfileUser
import com.moraveco.challengeme.data.SendEmail
import com.moraveco.challengeme.data.UpdateToken
import com.moraveco.challengeme.data.User


interface UserRepository {
    suspend fun getUserById(uid: String) : Result<ProfileUser, Error>
    suspend fun getAllUsers() : List<User>
    //suspend fun updateStatus(updateStatus: UpdateStatus)
    suspend fun updateToken(updateToken: UpdateToken)
    suspend fun blockUser(blockUser: BlockUser)

    suspend fun sendEmail(sendEmail: SendEmail) : Result<Unit, Error>
}