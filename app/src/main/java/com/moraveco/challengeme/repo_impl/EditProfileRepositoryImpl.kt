package com.moraveco.challengeme.repo_impl

import android.util.Log
import com.moraveco.challengeme.api.ApiService
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
                apiService.editProfile(updateProfileData)
            }catch (e: Exception){
                Log.v("error", e.toString())
            }
        }
    }

    override suspend fun updatePassword(updatePasswordData: UpdatePasswordData) {
        return withContext(Dispatchers.IO){
            try {
                apiService.updatePass(updatePasswordData)
            }catch (e: Exception){
                Log.v("error", e.toString())
            }
        }
    }
}