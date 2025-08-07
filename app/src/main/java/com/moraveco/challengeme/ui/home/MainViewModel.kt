package com.moraveco.challengeme.ui.home

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moraveco.challengeme.data.BlockUser
import com.moraveco.challengeme.data.ProfileUser
import com.moraveco.challengeme.data.UpdateToken
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.repo_impl.UserRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val repository: UserRepositoryImpl) : ViewModel(){
    private val _users = MutableStateFlow(emptyList<User>())
    val users: StateFlow<List<User>> get() = _users.asStateFlow()
    private val _user = MutableStateFlow(ProfileUser.empty())
    val user: StateFlow<ProfileUser> get() = _user.asStateFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()

    fun fetchAllUsersData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val users = repository.getAllUsers()
                _users.value = users
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }finally {
                _isLoading.value = false
            }
        }
    }



    @OptIn(DelicateCoroutinesApi::class)
    fun getUserById(uid: String){
        viewModelScope.launch {
            try {
                when(val result = repository.getUserById(uid)){
                    is com.moraveco.challengeme.repo.Result.Error -> Log.v("error", result.error.message.toString())
                    is com.moraveco.challengeme.repo.Result.Success -> {
                        _user.value = result.data
                    }
                }
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }

    fun deleteUserState(){
        _user.value = ProfileUser.empty()
    }



    /*fun updateStatus(updateStatus: UpdateStatus){
        viewModelScope.launch {
            repository.updateStatus(updateStatus)
        }
    }*/

    fun updateToken(updateToken: UpdateToken){
        viewModelScope.launch {
            repository.updateToken(updateToken)
        }
    }

    fun blockUser(blockUser: BlockUser){
        viewModelScope.launch {
            repository.blockUser(blockUser)
        }
    }





}