package com.moraveco.challengeme.ui.register

import android.content.Context
import android.net.Uri
import android.util.Log
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
import com.moraveco.challengeme.ui.login.md5
import com.moraveco.challengeme.user_settings.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.util.UUID
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

    fun updateProfileImageUri(uri: Uri?) {
        _uiState.value = _uiState.value.copy(profileImageUri = uri)
    }

    fun updateSecondImageUri(uri: Uri?) {
        _uiState.value = _uiState.value.copy(secondImageUri = uri)
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

    fun registerUser(
        context: Context,
        email: String,
        password: String,
        name: String,
        lastName: String,
        country: String,
        profileImageUri: Uri?,
        secondImageUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isAuthenticating = true,
                    authErrorMessage = null
                )            // Generate unique ID
                val uid = UUID.randomUUID().toString()            // Hash the password with MD5 (as your backend expects)
                val hashedPassword = md5(password)            // Prepare image parts
                var profileImagePart: MultipartBody.Part? = null
                var secondImagePart: MultipartBody.Part? = null            // Process profile image if exists
                profileImageUri?.let { uri ->
                    try {
                        val compressedFile = MediaCompressionUtil.compressImageFromUri(
                            context = context,
                            imageUri = uri,
                            maxWidth = 800,
                            maxHeight = 800,
                            quality = 85,
                            maxSizeKB = 500
                        )
                        compressedFile?.let { file ->
                            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            profileImagePart = MultipartBody.Part.createFormData(
                                "profileImage",
                                "profile_${System.currentTimeMillis()}.jpg",
                                requestBody
                            )
                            Log.d("RegisterVM", "Profile image prepared: ${file.length()} bytes")
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterVM", "Error processing profile image", e)
                    }
                }            // Process second image if exists
                secondImageUri?.let { uri ->
                    try {
                        val compressedFile = MediaCompressionUtil.compressImageFromUri(
                            context = context,
                            imageUri = uri,
                            maxWidth = 1080,
                            maxHeight = 1080,
                            quality = 85,
                            maxSizeKB = 800
                        )
                        compressedFile?.let { file ->
                            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            secondImagePart = MultipartBody.Part.createFormData(
                                "secondImage",
                                "second_${System.currentTimeMillis()}.jpg",
                                requestBody
                            )
                            Log.d("RegisterVM", "Second image prepared: ${file.length()} bytes")
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterVM", "Error processing second image", e)
                    }
                }            // Create register data
                val registerData = RegisterData(
                    uid = uid,
                    email = email,
                    password = hashedPassword,
                    name = name,
                    lastName = lastName,
                    country = country,
                    profileImage = profileImagePart,
                    secondImage = secondImagePart
                )            // Call repository
                when (val result = repository.registerUser(registerData)) {
                    is LoginResult.Success -> {
                        // Save to DataStore for auto-login
                        saveUserToDataStore(uid, name)
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
            } catch (e: Exception) {
                Log.e("RegisterVM", "Registration error", e)
                _uiState.value = _uiState.value.copy(
                    authenticationSucceed = false,
                    isAuthenticating = false,
                    authErrorMessage = "Chyba při registraci: ${e.message}"
                )
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
    val profileImageUri: Uri? = null,
    val secondImageUri: Uri? = null,
    val isAuthenticating: Boolean = false,
    val authErrorMessage: String? = null,
    val authenticationSucceed: Boolean = false
)