package com.moraveco.challengeme.ui.profile

import android.content.ContentValues.TAG
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moraveco.challengeme.data.Follow
import com.moraveco.challengeme.repo_impl.ProfileRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import java.io.OutputStreamWriter
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepositoryImpl) : ViewModel() {

    private val _follows = MutableStateFlow(emptyList<Follow>())
    val follows: StateFlow<List<Follow>> get() = _follows



    fun fetchFollowsPeriodically(uid: String, intervalMillis: Long = 5000){
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {  // Continue polling indefinitely
                getFollows(uid)

                // Delay for the specified interval before making the next request
                delay(intervalMillis)
            }
        }
    }

    fun followUser(
        follow: Follow,
        onFollowSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.followUser(follow)
                onFollowSuccess()
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }

    fun unfollowUser(
        id: String,
        onUnfollowSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.unfollowUser(id)
                onUnfollowSuccess()
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }

    fun getFollows(uid: String) {
        viewModelScope.launch {
            try {
                val followList = repository.getFollows(uid)
                _follows.value = followList
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }







}