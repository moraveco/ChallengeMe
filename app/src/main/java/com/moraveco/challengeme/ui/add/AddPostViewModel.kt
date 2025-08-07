package com.moraveco.challengeme.ui.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moraveco.challengeme.data.DailyChallenge
import com.moraveco.challengeme.data.Friend
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.data.UploadResponse
import com.moraveco.challengeme.notifications.FcmMessage
import com.moraveco.challengeme.notifications.Message
import com.moraveco.challengeme.repo_impl.DailyChallengeRepositoryImpl
import com.moraveco.challengeme.repo_impl.NotificationRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class AddPostViewModel @Inject constructor(private val repository: DailyChallengeRepositoryImpl, private val notificationRepository: NotificationRepositoryImpl) :
    ViewModel() {
    private val _dailyChallenge = MutableStateFlow(
        DailyChallenge(
            "1",
            "No challenge available for thid day",
            "Žádný úkol na dnešek",
            "Žiadna úloha na dnešok",
            LocalDate.now().toString()
        )
    )
    val dailyChallenge: StateFlow<DailyChallenge> get() = _dailyChallenge.asStateFlow()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uploadResponse = MutableLiveData<UploadResponse?>()
    val uploadResponse: LiveData<UploadResponse?> = _uploadResponse

    init {
        getDailyChallenge()
    }

    private fun getDailyChallenge() {
        viewModelScope.launch {
            val dailyChallenge = repository.getDailyChallenge()
            _dailyChallenge.value = dailyChallenge
        }
    }

    fun uploadPhoto(photoFile: File) {
        viewModelScope.launch(Dispatchers.Main) {
            _isLoading.value = true
            val file = File(photoFile.path)
            val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val response = repository.uploadPhoto(multipartBody)
            if (response.isSuccessful) {
                _uploadResponse.value =
                    UploadResponse(response.isSuccessful, response.message(), response.body() ?: "")
            } else {
                _uploadResponse.value =
                    UploadResponse(!response.isSuccessful, response.message(), "")
            }
            _isLoading.value = false
        }
    }

    fun clearUploadResult() {
        _uploadResponse.value = null
    }

    fun uploadVideo(photoFile: File) {
        viewModelScope.launch(Dispatchers.Main) {
            _isLoading.value = true
            val file = File(photoFile.path)
            val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val response = repository.uploadVideo(multipartBody)
            if (response.isSuccessful) {
                _uploadResponse.value =
                    UploadResponse(response.isSuccessful, response.message(), response.body() ?: "")
            } else {
                _uploadResponse.value =
                    UploadResponse(!response.isSuccessful, response.message(), "")
            }
            _isLoading.value = false
        }
    }

    fun addPost(friends: List<Friend>, updatePost: Post, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                if (_uploadResponse.value != null) {
                    _isLoading.value = true
                    repository.createPost(updatePost)
                    onSuccess()
                    if (friends.isNotEmpty()){
                        friends.forEach {
                            notificationRepository.sendNotification(
                                FcmMessage(
                                    Message(
                                        token = it.token,
                                        data = hashMapOf("title" to updatePost.name!!, "body" to "přidal nový příspěvek")
                                    )
                                )
                            )
                        }
                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }
}