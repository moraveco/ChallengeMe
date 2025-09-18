package com.moraveco.challengeme.ui.register

import android.content.Context
import android.net.Uri
import android.util.Patterns
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: RegisterRepositoryImpl,
    private val dataStore: DataStore<UserSettings>
) : ViewModel() {
    // Registration UI State
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    // Validation state
    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
        validateEmail(email)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
        validatePassword(password)
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
        validateName(name)
    }

    fun updateLastName(lastName: String) {
        _uiState.value = _uiState.value.copy(lastName = lastName)
        validateLastName(lastName)
    }

    fun updateCountry(country: String) {
        _uiState.value = _uiState.value.copy(country = country)
    }

    fun updateTermsAccepted(accepted: Boolean) {
        _uiState.value = _uiState.value.copy(termsAccepted = accepted)
    }

    private fun validateEmail(email: String) {
        val errors = _validationErrors.value.toMutableMap()
        when {
            email.isBlank() -> errors["email"] = "Email je povinný"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                errors["email"] = "Neplatný formát emailu"
            else -> errors.remove("email")
        }
        _validationErrors.value = errors
    }

    private fun validatePassword(password: String) {
        val errors = _validationErrors.value.toMutableMap()
        when {
            password.isBlank() -> errors["password"] = "Heslo je povinné"
            password.length < 8 -> errors["password"] = "Heslo musí mít alespoň 8 znaků"
            !password.any { it.isDigit() } ->
                errors["password"] = "Heslo musí obsahovat alespoň jednu číslici"
            !password.any { it.isUpperCase() } ->
                errors["password"] = "Heslo musí obsahovat alespoň jedno velké písmeno"
            else -> errors.remove("password")
        }
        _validationErrors.value = errors
    }

    private fun validateName(name: String) {
        val errors = _validationErrors.value.toMutableMap()
        when {
            name.isBlank() -> errors["name"] = "Jméno je povinné"
            name.length < 2 -> errors["name"] = "Jméno musí mít alespoň 2 znaky"
            else -> errors.remove("name")
        }
        _validationErrors.value = errors
    }

    private fun validateLastName(lastName: String) {
        val errors = _validationErrors.value.toMutableMap()
        when {
            lastName.isBlank() -> errors["lastName"] = "Příjmení je povinné"
            lastName.length < 2 -> errors["lastName"] = "Příjmení musí mít alespoň 2 znaky"
            else -> errors.remove("lastName")
        }
        _validationErrors.value = errors
    }

    fun isFormValid(): Boolean {
        val state = _uiState.value
        validateEmail(state.email)
        validatePassword(state.password)
        validateName(state.name)
        validateLastName(state.lastName)

        return _validationErrors.value.isEmpty() && state.termsAccepted
    }

    fun registerUser(registerData: RegisterData) {
        if (!isFormValid()) {
            _uiState.value = _uiState.value.copy(
                authErrorMessage = "Vyplňte správně všechna povinná pole"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAuthenticating = true,
                authErrorMessage = null
            )

            when (val result = repository.registerUser(registerData)) {
                is LoginResult.Success -> {
                    // Auto-login after successful registration
                    saveUserToDataStore(result.userId, registerData.name)

                    _uiState.value = _uiState.value.copy(
                        authenticationSucceed = true,
                        isAuthenticating = false,
                        authErrorMessage = null
                    )
                }
                is LoginResult.EmailNotFound -> {
                    _uiState.value = _uiState.value.copy(
                        authenticationSucceed = false,
                        isAuthenticating = false,
                        authErrorMessage = "Email již existuje v systému"
                    )
                }
                is LoginResult.AuthenticationFailed -> {
                    _uiState.value = _uiState.value.copy(
                        authenticationSucceed = false,
                        isAuthenticating = false,
                        authErrorMessage = "Registrace selhala. Zkuste to prosím znovu."
                    )
                }
                is LoginResult.UnexpectedError -> {
                    _uiState.value = _uiState.value.copy(
                        authenticationSucceed = false,
                        isAuthenticating = false,
                        authErrorMessage = result.message ?: "Nastala neočekávaná chyba"
                    )
                }
            }
        }
    }

    private suspend fun saveUserToDataStore(uid: String, name: String) {
        dataStore.updateData { currentSettings ->
            currentSettings.copy(uid = uid, name = name)
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

    fun clearState() {
        _uiState.value = RegistrationUiState()
        _validationErrors.value = emptyMap()
    }
}
data class RegistrationUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val lastName: String = "",
    val country: String = "Czech Republic",
    val termsAccepted: Boolean = false,
    val isAuthenticating: Boolean = false,
    val authErrorMessage: String? = null,
    val authenticationSucceed: Boolean = false
)