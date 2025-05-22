package com.moraveco.challengeme.api

import com.moraveco.challengeme.constants.Constants.Companion.ACCEPT_FOLLOW
import com.moraveco.challengeme.constants.Constants.Companion.ALL_POSTS
import com.moraveco.challengeme.constants.Constants.Companion.ALL_USERS
import com.moraveco.challengeme.constants.Constants.Companion.DELETE_LIKE
import com.moraveco.challengeme.constants.Constants.Companion.DELETE_POST
import com.moraveco.challengeme.constants.Constants.Companion.EDIT_PROFILE
import com.moraveco.challengeme.constants.Constants.Companion.FETCH_COMMENTS
import com.moraveco.challengeme.constants.Constants.Companion.FETCH_FOLLOW
import com.moraveco.challengeme.constants.Constants.Companion.FETCH_LIKES
import com.moraveco.challengeme.constants.Constants.Companion.FETCH_NOTIFICATIONS
import com.moraveco.challengeme.constants.Constants.Companion.FETCH_REQUESTS
import com.moraveco.challengeme.constants.Constants.Companion.FOLLOW_USER
import com.moraveco.challengeme.constants.Constants.Companion.FRIEND_POSTS
import com.moraveco.challengeme.constants.Constants.Companion.INSERT_LIKE
import com.moraveco.challengeme.constants.Constants.Companion.INSERT_USER
import com.moraveco.challengeme.constants.Constants.Companion.LOGIN
import com.moraveco.challengeme.constants.Constants.Companion.POST_BY_ID
import com.moraveco.challengeme.constants.Constants.Companion.PROFILE_POSTS
import com.moraveco.challengeme.constants.Constants.Companion.PUBLIC_POSTS
import com.moraveco.challengeme.constants.Constants.Companion.REGISTER
import com.moraveco.challengeme.constants.Constants.Companion.SEND_COMMENT
import com.moraveco.challengeme.constants.Constants.Companion.SEND_PASS
import com.moraveco.challengeme.constants.Constants.Companion.UNFOLLOW_USER
import com.moraveco.challengeme.constants.Constants.Companion.UPDATE_PASS
import com.moraveco.challengeme.constants.Constants.Companion.UPDATE_POST
import com.moraveco.challengeme.constants.Constants.Companion.UPDATE_READ
import com.moraveco.challengeme.constants.Constants.Companion.UPDATE_TOKEN
import com.moraveco.challengeme.constants.Constants.Companion.USER_BY_ID
import com.moraveco.challengeme.data.AcceptRequest
import com.moraveco.challengeme.data.Comment
import com.moraveco.challengeme.data.CommentData
import com.moraveco.challengeme.data.DeleteLike
import com.moraveco.challengeme.data.DeletePost
import com.moraveco.challengeme.data.Follow
import com.moraveco.challengeme.data.FollowRequest
import com.moraveco.challengeme.data.Like
import com.moraveco.challengeme.data.LoginRequest
import com.moraveco.challengeme.data.LoginResponse
import com.moraveco.challengeme.data.Notification
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.data.ProfileUser
import com.moraveco.challengeme.data.RegisterData
import com.moraveco.challengeme.data.SendPasswordData
import com.moraveco.challengeme.data.UpdatePasswordData
import com.moraveco.challengeme.data.UpdatePost
import com.moraveco.challengeme.data.UpdateProfileData
import com.moraveco.challengeme.data.UpdateRead
import com.moraveco.challengeme.data.User
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @GET(USER_BY_ID)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun getUserById(
        @Query("uid") uid: String,
        @Header("X-Authorization") auth: String
    ) : Response<List<ProfileUser>>

    @GET(ALL_USERS)
    suspend fun getAllUsers(
    ) : Response<List<User>>

    @GET(FRIEND_POSTS)
    suspend fun getFriendsPosts(
        @Query("uid") uid: String,
        @Header("X-Authorization") auth: String
    ) : Response<List<Post>>

    @GET(ALL_POSTS)
    suspend fun getHistoryPosts(
        @Query("uid") uid: String,
    ) : Response<List<Post>>

    @GET(PUBLIC_POSTS)
    suspend fun getPublicPosts(
        @Query("uid") uid: String,
    ) : Response<List<Post>>

    @GET(POST_BY_ID)
    suspend fun getPostByID(
        @Query("id") id: String,
        @Header("X-Authorization") auth: String
    ) : Response<List<Post>>


    @GET(PROFILE_POSTS)
    suspend fun getPostsById(
        @Query("uid") uid: String,
        @Header("X-Authorization") auth: String
    ) : Response<List<Post>>


    @GET(FETCH_FOLLOW)
    suspend fun getFollows(
        @Query("uid") uid: String,
        @Query("token") token: String
    ) : Response<List<Follow>>

    @POST(FOLLOW_USER)
    suspend fun followUser(
        @Body follow: Follow
    ) : ResponseBody

   /* @POST(UNFOLLOW_USER)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun unfollowUser(
        @Body unfollowUserData: UnfollowUserData
    ) : ResponseBody*/

    @GET(FETCH_COMMENTS)
    suspend fun getComments(
        @Query("postId") postId: String
    ) : Response<List<Comment>>

    @POST(SEND_COMMENT)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun sendComment(
        @Body comment: CommentData
    ) : ResponseBody

    @GET(FETCH_LIKES)
    suspend fun getLikes(
        @Query("uid") uid: String,
        @Header("X-Authorization") auth: String
    ) : Response<List<Like>>

    @POST(INSERT_LIKE)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun insertLike(
        @Body like: Like,
        @Header("X-Authorization") auth: String
    ) : ResponseBody

    @POST(DELETE_LIKE)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun deleteLike(
        @Body id: DeleteLike,
        @Header("X-Authorization") auth: String
    ) : ResponseBody

    @Multipart
    @POST("uploadPhotoRequest.php")
    suspend fun uploadPhoto(
        @Part photo: MultipartBody.Part
    ): Response<String>

    @Multipart
    @POST("uploadMessagesPhotos.php")
    suspend fun uploadMessagesPhoto(
        @Part photo: MultipartBody.Part
    ): Response<String>

    @POST("newPost.php")
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun createPost(
        @Body post: Post
    )

    @GET(FETCH_REQUESTS)
    suspend fun getRequestFollows(
        @Query("uid") uid: String,
        @Query("token") token: String
    ) : Response<List<FollowRequest>>

    @POST(ACCEPT_FOLLOW)
    suspend fun acceptFollow(
        @Body id: AcceptRequest
    )

    @POST(REGISTER)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun register(
        @Body registerData: RegisterData,
    )

    @POST(LOGIN)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun login(
        @Header("X-Authorization") auth: String,
        @Body loginRequest: LoginRequest,
    ): Response<LoginResponse>


    @POST(INSERT_USER)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun insertNewUser(
        @Body user: User
    )

    @POST(EDIT_PROFILE)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun editProfile(
        @Body user: UpdateProfileData
    )

    @GET(FETCH_NOTIFICATIONS)
    suspend fun getNotifications(
        @Query("uid") uid: String,
        @Query("token") token: String
    ) : Response<List<Notification>>

    @POST(UPDATE_POST)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun updatePost(
        @Body id: UpdatePost
    )

    /*@POST("uploadAPI/updateStatus.php")
    suspend fun updateStatus(
        @Body updateStatus: UpdateStatus
    )*/

    @POST(DELETE_POST)
    suspend fun deletePost(
        @Body id: DeletePost
    )

    /*@POST(UPDATE_TOKEN)
    suspend fun updateToken(
        @Body updateToken: UpdateToken
    )*/


    @POST(UPDATE_PASS)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun updatePass(
        @Body updatePass: UpdatePasswordData
    )

    @POST(SEND_PASS)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun sendPassword(
        @Body sendPasswordData: SendPasswordData
    )

}