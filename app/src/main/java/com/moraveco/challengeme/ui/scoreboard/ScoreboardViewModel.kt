package com.moraveco.challengeme.ui.scoreboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moraveco.challengeme.data.LeadeboardUser
import com.moraveco.challengeme.nav.Screens.Scoreboard
import com.moraveco.challengeme.repo_impl.ScoreboardRepositoryIml
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreboardViewModel @Inject constructor(private val repository: ScoreboardRepositoryIml) : ViewModel() {
    private val _today = MutableStateFlow<List<LeadeboardUser>>(emptyList())
    val today: StateFlow<List<LeadeboardUser>> get() = _today.asStateFlow()

    private val _global = MutableStateFlow<List<LeadeboardUser>>(emptyList())
    val global: StateFlow<List<LeadeboardUser>> get() = _global.asStateFlow()

    private val _friends = MutableStateFlow<List<LeadeboardUser>>(emptyList())
    val friends: StateFlow<List<LeadeboardUser>> get() = _friends.asStateFlow()

    fun getToday(){
        viewModelScope.launch {
            val scoreboard = repository.getToday()
            _today.value = scoreboard
        }
    }

    fun getGlobal(){
        viewModelScope.launch {
            val scoreboard = repository.getGlobal()
            _global.value = scoreboard
        }
    }

    fun getFriends(uid: String){
        viewModelScope.launch {
            val scoreboard = repository.getFriends(uid)
            _friends.value = scoreboard
        }
    }

}