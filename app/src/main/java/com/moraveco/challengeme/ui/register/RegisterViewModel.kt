package com.moraveco.challengeme.ui.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moraveco.challengeme.data.RegisterData
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.repo_impl.RegisterRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: RegisterRepositoryImpl) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()

    private val _succeed = MutableStateFlow(false)
    val succeed: StateFlow<Boolean> get() = _succeed.asStateFlow()

    private val _error = MutableStateFlow(false)
    val error: StateFlow<Boolean> get() = _error.asStateFlow()

    fun registerUser(registerData: RegisterData, user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.registerUser(registerData = registerData
                )
                repository.insertUser(user)
            }catch (e: Exception){
                _error.value = true
            }finally {
                _isLoading.value = false
                _succeed.value = true
                _error.value = false
            }




        }
    }




}