package com.moraveco.challengeme.api

import com.moraveco.challengeme.constants.Constants.Companion.ACCEPT_FOLLOW
import com.moraveco.challengeme.constants.Constants.Companion.ALL_POSTS
import com.moraveco.challengeme.constants.Constants.Companion.ALL_USERS
import com.moraveco.challengeme.constants.Constants.Companion.BLOCK_USER
import com.moraveco.challengeme.constants.Constants.Companion.DELETE_ACCOUNT
import com.moraveco.challengeme.constants.Constants.Companion.DELETE_FRIEND
import com.moraveco.challengeme.constants.Constants.Companion.DELETE_LIKE
import com.moraveco.challengeme.constants.Constants.Companion.DELETE_POST
import com.moraveco.challengeme.constants.Constants.Companion.FETCH_COMMENTS
import com.moraveco.challengeme.constants.Constants.Companion.FETCH_DAILY_CHALLENGE
import com.moraveco.challengeme.constants.Constants.Companion.FETCH_FOLLOW
import com.moraveco.challengeme.constants.Constants.Companion.FETCH_FRIENDS
import com.moraveco.challengeme.constants.Constants.Companion.FETCH_FRIENDS_LEADERBOARD
import com.moraveco.challengeme.constants.Constants.Companion.FETCH_GLOBAL_LEADERBOARD
import com.moraveco.challengeme.constants.Constants.Companion.FETCH_LIKES
import com.moraveco.challengeme.constants.Constants.Companion.FETCH_NOTIFICATIONS
import com.moraveco.challengeme.constants.Constants.Companion.FETCH_REQUESTS
import com.moraveco.challengeme.constants.Constants.Companion.FETCH_TODAY_LEADERBOARD
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
import com.moraveco.challengeme.constants.Constants.Companion.UPDATE_PASS
import com.moraveco.challengeme.constants.Constants.Companion.UPDATE_POST
import com.moraveco.challengeme.constants.Constants.Companion.UPDATE_PROFILE
import com.moraveco.challengeme.constants.Constants.Companion.UPDATE_TOKEN
import com.moraveco.challengeme.constants.Constants.Companion.USER_BY_ID
import com.moraveco.challengeme.data.AcceptRequest
import com.moraveco.challengeme.data.BlockUser
import com.moraveco.challengeme.data.Comment
import com.moraveco.challengeme.data.CommentData
import com.moraveco.challengeme.data.DailyChallenge
import com.moraveco.challengeme.data.DeleteAccountData
import com.moraveco.challengeme.data.DeleteLike
import com.moraveco.challengeme.data.DeletePost
import com.moraveco.challengeme.data.Follow
import com.moraveco.challengeme.data.FollowRequest
import com.moraveco.challengeme.data.Friend
import com.moraveco.challengeme.data.LeadeboardUser
import com.moraveco.challengeme.data.Like
import com.moraveco.challengeme.data.LoginRequest
import com.moraveco.challengeme.data.LoginResponse
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.data.ProfileUser
import com.moraveco.challengeme.data.RegisterData
import com.moraveco.challengeme.data.SendPasswordData
import com.moraveco.challengeme.data.UpdatePasswordData
import com.moraveco.challengeme.data.UpdatePost
import com.moraveco.challengeme.data.UpdateProfileData
import com.moraveco.challengeme.data.UpdateToken
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
        @Body follow: Follow,
        @Header("X-Authorization") auth: String
    ) : ResponseBody


    @GET(FETCH_COMMENTS)
    suspend fun getComments(
        @Query("postId") postId: String,
        @Header("X-Authorization") auth: String
    ) : Response<List<Comment>>

    @POST(SEND_COMMENT)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun sendComment(
        @Body comment: CommentData,
        @Header("X-Authorization") auth: String
    ) : ResponseBody

    @GET(FETCH_LIKES)
    suspend fun getLikes(
        @Query("uid") uid: String,
        @Header("X-Authorization") auth: String
    ) : Response<List<Like>>

    @GET(FETCH_FRIENDS)
    suspend fun getFriends(
        @Query("uid") uid: String,
        @Header("X-Authorization") auth: String
    ) : Response<List<Friend>>

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

    @POST(UPDATE_PROFILE)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun updateProfile(
        @Body id: UpdateProfileData,
        @Header("X-Authorization") auth: String
    ) : ResponseBody

    @Multipart
    @POST("uploadPhotoRequest.php")
    suspend fun uploadPhoto(
        @Part photo: MultipartBody.Part
    ): Response<String>

    @Multipart
    @POST("uploadPostRequest.php")
    suspend fun uploadPostPhoto(
        @Part photo: MultipartBody.Part
    ): Response<String>

    @Multipart
    @POST("uploadPostRequest.php")
    suspend fun uploadPostVideo(
        @Part video: MultipartBody.Part
    ): Response<String>

    @POST("insertPost.php")
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun createPost(
        @Body post: Post,
        @Header("X-Authorization") auth: String
    )

    @GET(FETCH_REQUESTS)
    suspend fun getRequestFollows(
        @Query("uid") uid: String,
        @Query("token") token: String
    ) : Response<List<FollowRequest>>

    @POST(ACCEPT_FOLLOW)
    suspend fun acceptFollow(
        @Body id: AcceptRequest,
        @Header("X-Authorization") auth: String
    )

    @POST(DELETE_FRIEND)
    suspend fun deleteFriend(
        @Body id: AcceptRequest,
        @Header("X-Authorization") auth: String
    )

    @POST(REGISTER)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun register(
        @Body registerData: RegisterData,
        @Header("X-Authorization") auth: String,
        ): Response<LoginResponse>

    @POST(LOGIN)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun login(
        @Header("X-Authorization") auth: String,
        @Body loginRequest: LoginRequest,
    ): Response<LoginResponse>


    @POST(INSERT_USER)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun insertNewUser(
        @Body user: User,
        @Header("X-Authorization") auth: String,
        ) : Response<LoginResponse>

    @GET(FETCH_TODAY_LEADERBOARD)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun getTodayLeaderboard(
        @Header("X-Authorization") auth: String,
        ) : Response<List<LeadeboardUser>>

    @GET(FETCH_GLOBAL_LEADERBOARD)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun getGlobalLeaderboard(
        @Header("X-Authorization") auth: String,
    ) : Response<List<LeadeboardUser>>

    @GET(FETCH_FRIENDS_LEADERBOARD)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun getFriendsLeaderboard(
        @Query("uid") uid: String,
        @Header("X-Authorization") auth: String,
    ) : Response<List<LeadeboardUser>>

    @GET(FETCH_DAILY_CHALLENGE)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun getDailyChallenge(
    ) : Response<List<DailyChallenge>>

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
        @Body id: DeletePost,
        @Header("X-Authorization") auth: String
    )

    @POST(BLOCK_USER)
    suspend fun blockUser(
        @Body blockUser: BlockUser,
        @Header("X-Authorization") auth: String
    )

    @POST(UPDATE_TOKEN)
    suspend fun updateToken(
        @Body updateToken: UpdateToken,
        @Header("X-Authorization") auth: String
    )


    @POST(UPDATE_PASS)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun updatePass(
        @Body updatePass: UpdatePasswordData,
        @Header("X-Authorization") auth: String
    )

    @POST(SEND_PASS)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun sendPassword(
        @Body sendPasswordData: SendPasswordData
    )

    @POST(DELETE_ACCOUNT)
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun deleteAccount(
        @Body uid: DeleteAccountData,
        @Header("X-Authorization") auth: String
    )

}