package com.moraveco.challengeme.ui.register

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moraveco.challengeme.data.LoginRequest
import com.moraveco.challengeme.data.LoginResult
import com.moraveco.challengeme.data.RegisterData
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.di.MediaCompressionUtil
import com.moraveco.challengeme.repo_impl.RegisterRepositoryImpl
import com.moraveco.challengeme.ui.login.LoginUiState
import com.moraveco.challengeme.user_settings.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: RegisterRepositoryImpl, private val dataStore: DataStore<UserSettings>) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun registerUser(registerData: RegisterData) {
        viewModelScope.launch {
            uiState = uiState.copy(isAuthenticating = true, authErrorMessage = null)

            when (val result = repository.registerUser(registerData)) {
                is LoginResult.Success -> {
                    uiState = uiState.copy(
                        authenticationSucceed = true,
                        isAuthenticating = false,
                        authErrorMessage = null
                    )
                }
                is LoginResult.EmailNotFound -> {
                    uiState = uiState.copy(
                        authenticationSucceed = false,
                        isAuthenticating = false,
                        authErrorMessage = "The email address was not found."
                    )
                }
                is LoginResult.AuthenticationFailed -> {
                    uiState = uiState.copy(
                        authenticationSucceed = false,
                        isAuthenticating = false,
                        authErrorMessage = "Authentication failed. Please check your credentials."
                    )
                }
                is LoginResult.UnexpectedError -> {
                    uiState = uiState.copy(
                        authenticationSucceed = false,
                        isAuthenticating = false,
                        authErrorMessage = "An unexpected error occurred: ${result.message}"
                    )
                }
            }
        }
    }

    private fun toUserSettings(uid: String, name: String) {
        viewModelScope.launch {
            dataStore.updateData { currentSettings ->
                currentSettings.copy(uid = uid, name = name)
            }
        }
    }


    suspend fun toMultiPart(context: Context, imageUri: Uri): MultipartBody.Part? {
        val compressedFile = MediaCompressionUtil.compressImageFromUri(
            context = context,
            imageUri = imageUri
        ) ?: return null

        val requestBody = compressedFile
            .asRequestBody("image/jpeg".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            name = "file",
            filename = compressedFile.name,
            body = requestBody
        )
    }






}