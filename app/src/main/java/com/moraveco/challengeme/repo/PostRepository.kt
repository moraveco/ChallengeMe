package com.moraveco.challengeme.repo

import com.moraveco.challengeme.data.Comment
import com.moraveco.challengeme.data.CommentData
import com.moraveco.challengeme.data.Like
import com.moraveco.challengeme.data.LikeResponse
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.data.UpdatePost
import okhttp3.MultipartBody
import retrofit2.Response
import java.io.File
import kotlin.Result

interface PostRepository {
    suspend fun getPostsById(uid: String) : List<Post>
    suspend fun getHistoryPosts(uid: String) : List<Post>
    suspend fun getPublicPosts(uid: String) : List<Post>
    suspend fun getFriendsPosts(uid: String) : List<Post>
    suspend fun getPostById(id: String) : Post
    suspend fun getProfilePosts(hisUid: String, myUid: String) : List<Post>
    suspend fun getComments(id: String): List<Comment>
    suspend fun sendComment(comment: CommentData)
    suspend fun getLikes(uid: String) : List<Like>
    suspend fun handleLike(like: Like) : Result<LikeResponse>
    suspend fun deleteLike(id: String)
    suspend fun uploadPhoto(file: MultipartBody.Part) : Response<String>
    suspend fun createPost(post: Post)
    suspend fun updatePost(updatePost: UpdatePost)
    suspend fun deletePost(id: String)
    suspend fun reportPost(email: String, myUid: String, postId: String, image: String)
}