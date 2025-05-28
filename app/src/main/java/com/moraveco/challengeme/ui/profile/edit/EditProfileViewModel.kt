package com.moraveco.challengeme.ui.profile.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moraveco.challengeme.data.DeleteAccountData
import com.moraveco.challengeme.data.UpdatePasswordData
import com.moraveco.challengeme.data.UpdateProfileData
import com.moraveco.challengeme.data.UploadResponse
import com.moraveco.challengeme.repo_impl.EditProfileRepositoryImpl
import com.moraveco.challengeme.repo_impl.PostRepositoryImpl
import com.moraveco.challengeme.repo_impl.ProfileRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(private val repository: EditProfileRepositoryImpl, private val postRepository: PostRepositoryImpl) : ViewModel()  {

    private val _uploadResponse = MutableLiveData<UploadResponse>()
    val uploadResponse: LiveData<UploadResponse> = _uploadResponse

    private val _uploadSecondResponse = MutableLiveData<UploadResponse>()
    val uploadSecondResponse: LiveData<UploadResponse> = _uploadSecondResponse

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    fun updateProfile(updateProfileData: UpdateProfileData) = viewModelScope.launch {
        repository.editProfile(updateProfileData)
    }
    fun updatePass(updatePasswordData: UpdatePasswordData) = viewModelScope.launch {
        repository.updatePassword(updatePasswordData)
    }

    fun uploadPhoto(photoFile: File){
        viewModelScope.launch {
            _isLoading.value = true
            val file = File(photoFile.path)
            val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val response = postRepository.uploadPhoto(multipartBody)
            if (response.isSuccessful) {
                _uploadResponse.value = UploadResponse(response.isSuccessful, response.message(), response.body() ?: "")
            } else {
                _uploadResponse.value = UploadResponse(!response.isSuccessful, response.message(), "")
            }
            _isLoading.value = false
        }
    }

    fun uploadSecondPhoto(photoFile: File){
        viewModelScope.launch {
            _isLoading.value = true
            val file = File(photoFile.path)
            val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val response = postRepository.uploadPhoto(multipartBody)
            if (response.isSuccessful) {
                _uploadSecondResponse.value = UploadResponse(response.isSuccessful, response.message(), response.body() ?: "")
            } else {
                _uploadSecondResponse.value = UploadResponse(!response.isSuccessful, response.message(), "")
            }
            _isLoading.value = false
        }
    }

    fun deleteAccount(uid: String){
        viewModelScope.launch {
            repository.deleteAccount(DeleteAccountData(uid))
        }
    }

}