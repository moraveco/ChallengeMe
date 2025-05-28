package com.moraveco.challengeme.repo

import com.moraveco.challengeme.data.DeleteAccountData
import com.moraveco.challengeme.data.UpdatePasswordData
import com.moraveco.challengeme.data.UpdateProfileData


interface EditProfileRepository {
    suspend fun editProfile(updateProfileData: UpdateProfileData)
    suspend fun deleteAccount(deleteAccountData: DeleteAccountData)
    suspend fun updatePassword(updatePasswordData: UpdatePasswordData)
}