package com.moraveco.challengeme.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String,
    val name: String,
    val lastName: String,
    val bio: String,
    val profileImageUrl: String? = null,
    val secondImageUrl: String? = null,
    val token: String? = null,
    val country: String,
    val email: String
){
    companion object{
        fun empty() : User{
            return User("", "", "", "", "", "","", "", "")
        }
    }



}

@Serializable
data class Friend(
    val id: String,
    val uid: String,
    val name: String,
    val lastName: String,
    val profileImageUrl: String? = null,
    val senderUid: String,
    val receiverUid: String,
    val isAccept: Boolean,
    val time: String,
    val token: String? = null,
){
    companion object{
        fun empty() : Friend{
            return Friend("", "", "", "", "", "", "", false, "")
        }
    }



}

@Serializable
data class LeadeboardUser(
    val id: String,
    val uid: String,
    val profileImageUrl: String? = null,
    val name: String,
    val likes_count: String,
    val streaks: String,
){
    companion object{
        fun empty() : Friend{
            return Friend("", "", "", "", "", "", "", false, "")
        }
    }



}

@Serializable
data class ProfileUser(
    val uid: String,
    val name: String,
    val lastName: String,
    val bio: String,
    val profileImageUrl: String? = null,
    val secondImageUrl: String? = null,
    val token: String? = null,
    val country: String,
    val email: String,
    val follow: Int,
    val likes: Int,
    val streaks: Int,
){
    companion object{
        fun empty() : ProfileUser{
            return ProfileUser("", "", "", "", "", "","", "", "", 0, 0, 0)
        }
    }

}

fun ProfileUser.toUser() : User{
    return User(uid, name, lastName, bio, profileImageUrl, secondImageUrl, token, country, email)
}
