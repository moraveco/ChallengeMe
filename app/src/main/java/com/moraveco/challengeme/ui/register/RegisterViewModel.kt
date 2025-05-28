package com.moraveco.challengeme.ui.register

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
import com.moraveco.challengeme.repo_impl.RegisterRepositoryImpl
import com.moraveco.challengeme.ui.login.LoginUiState
import com.moraveco.challengeme.ui.login.md5
import com.moraveco.challengeme.user_settings.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: RegisterRepositoryImpl, private val dataStore: DataStore<UserSettings>) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun registerUser(registerData: RegisterData, user: User) {
        viewModelScope.launch {
            uiState = uiState.copy(isAuthenticating = true, authErrorMessage = null)

            when (val result = repository.registerUser(registerData)) {
                is LoginResult.Success -> {
                    uiState = uiState.copy(
                        authenticationSucceed = true,
                        isAuthenticating = false,
                        authErrorMessage = null
                    )
                    toUserSettings(result.userId)
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

            when (val result = repository.insertUser(user)) {
                is LoginResult.Success -> {
                    uiState = uiState.copy(
                        authenticationSucceed = true,
                        isAuthenticating = false,
                        authErrorMessage = null
                    )
                    toUserSettings(result.userId)
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

    private fun toUserSettings(uid: String) {
        viewModelScope.launch {
            dataStore.updateData { currentSettings ->
                currentSettings.copy(uid = uid)
            }
        }
    }




}