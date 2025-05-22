package com.moraveco.challengeme.ui.home

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moraveco.challengeme.user_settings.UserSettings
import com.moraveco.challengeme.user_settings.toUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val dataStore: DataStore<UserSettings>): ViewModel() {
    val authState = dataStore.data.map {
        it.toUser().uid
    }
    fun deleteUser(){
        viewModelScope.launch {
            dataStore.updateData { it.copy(uid = "-1") }

        }
    }
}