package com.moraveco.challengeme.ui.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moraveco.challengeme.data.AcceptRequest
import com.moraveco.challengeme.data.Follow
import com.moraveco.challengeme.data.Friend
import com.moraveco.challengeme.notifications.FcmMessage
import com.moraveco.challengeme.notifications.Message
import com.moraveco.challengeme.repo_impl.FollowRepositoryImpl
import com.moraveco.challengeme.repo_impl.NotificationRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FriendViewModel @Inject constructor(private val repository: FollowRepositoryImpl, private val notificationRepository: NotificationRepositoryImpl) :
    ViewModel() {
    private val _friends = MutableStateFlow(emptyList<Friend>())
    val friends: StateFlow<List<Friend>> get() = _friends


    fun getFriends(uid: String) = viewModelScope.launch {
        _friends.value = repository.getFriends(uid)

    }

    fun acceptRequest(name: String, token: String?, id: String) {
        viewModelScope.launch {
            repository.acceptFollow(AcceptRequest(id))
            val friend = _friends.value.find { it.id == id }
            if (friend != null){
                _friends.value -= friend
                _friends.value += friend.copy(isAccept = true)
            }
            notificationRepository.sendNotification(
                FcmMessage(
                    Message(
                        token = token,
                        data = hashMapOf("title" to name, "body" to "přijal tvou žádost o přátelství")
                    )
                )
            )

        }
    }

    fun deleteFriend(id: String) {
        viewModelScope.launch {
            repository.deleteFriend(id)
            val friend = _friends.value.find { it.id == id }
            if (friend != null) {
                _friends.value -= friend
            }
        }
    }

    fun addFriend(name: String, token: String?, follow: Follow) = viewModelScope.launch {
        repository.followUser(follow)
        notificationRepository.sendNotification(
            FcmMessage(
                Message(
                    token = token,
                    data = hashMapOf("title" to name, "body" to "ti poslal žádost o přátelství")
                )
            )
        )
    }

    fun getMyFriendRequest(myUid: String, hisUid: String): Friend? {
        return _friends.value.findLast { (it.senderUid == myUid && it.receiverUid == hisUid) || (it.senderUid == hisUid && it.receiverUid == myUid) }
    }
}