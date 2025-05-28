package com.moraveco.challengeme.repo_impl

import android.util.Log
import com.moraveco.challengeme.api.ApiService
import com.moraveco.challengeme.data.DeleteAccountData
import com.moraveco.challengeme.data.UpdatePasswordData
import com.moraveco.challengeme.data.UpdateProfileData
import com.moraveco.challengeme.repo.EditProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditProfileRepositoryImpl @Inject constructor(private val apiService: ApiService) : EditProfileRepository {
    override suspend fun editProfile(updateProfileData: UpdateProfileData) {
        return withContext(Dispatchers.IO){
            try {
                apiService.updateProfile(updateProfileData, "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")
            }catch (e: Exception){
                Log.v("error", e.toString())
            }
        }
    }

    override suspend fun deleteAccount(deleteAccountData: DeleteAccountData) {
        return withContext(Dispatchers.IO){
            try {
                apiService.deleteAccount(deleteAccountData, "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")
            }catch (e: Exception){
                Log.v("error", e.toString())
            }
        }
    }

    override suspend fun updatePassword(updatePasswordData: UpdatePasswordData) {
        return withContext(Dispatchers.IO){
            try {
                apiService.updatePass(updatePasswordData, "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")
            }catch (e: Exception){
                Log.v("error", e.toString())
            }
        }
    }
}