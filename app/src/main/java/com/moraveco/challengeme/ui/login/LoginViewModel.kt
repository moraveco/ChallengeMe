package com.moraveco.challengeme.ui.login

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moraveco.challengeme.data.LoginRequest
import com.moraveco.challengeme.data.LoginResult
import com.moraveco.challengeme.data.SendPasswordData
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.repo_impl.RegisterRepositoryImpl
import com.moraveco.challengeme.user_settings.UserSettings
import com.moraveco.challengeme.user_settings.toUserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.prefs.Preferences
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: RegisterRepositoryImpl, private val dataStore: DataStore<UserSettings>)  : ViewModel() {
    var uiState by mutableStateOf(LoginUiState())
        private set

    fun loginUser2() {
        viewModelScope.launch {
            uiState = uiState.copy(isAuthenticating = true, authErrorMessage = null)

            when (val result = repository.loginUser(LoginRequest(uiState.email, md5(uiState.password)))) {
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


    fun resetPassword(resetPasswordData: SendPasswordData) = viewModelScope.launch {
        repository.sendPassword(resetPasswordData)
    }


    private fun toUserSettings(uid: String) {
        viewModelScope.launch {
            dataStore.updateData { currentSettings ->
                currentSettings.copy(uid = uid)
            }
        }
    }


    fun updateEmail(input: String){
        uiState = uiState.copy(email = input)
    }

    fun updatePassword(input: String){
        uiState = uiState.copy(password = input)
    }



}



data class LoginUiState(
    var email: String = "",
    var password: String = "",
    var isAuthenticating: Boolean = false,
    var authErrorMessage: String? = null,
    var authenticationSucceed: Boolean = false
)

fun md5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(input.toByteArray())
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}