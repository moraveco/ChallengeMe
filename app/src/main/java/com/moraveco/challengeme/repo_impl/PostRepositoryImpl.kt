package com.moraveco.challengeme.repo_impl

import android.util.Log
import com.moraveco.challengeme.api.ApiService
import com.moraveco.challengeme.data.Comment
import com.moraveco.challengeme.data.CommentData
import com.moraveco.challengeme.data.DeleteLike
import com.moraveco.challengeme.data.DeletePost
import com.moraveco.challengeme.data.Like
import com.moraveco.challengeme.data.LikeResponse
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.data.ReportData
import com.moraveco.challengeme.data.UpdatePost
import com.moraveco.challengeme.repo.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Response
import retrofit2.await
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(private val apiService: ApiService) : PostRepository {
    override suspend fun getPostsById(uid: String): List<Post> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getPostsById(uid, "3e38*#^#kds82K")
            response.body() ?: emptyList()// Assuming parseMessagesList expects a String
        }
    }

    override suspend fun getHistoryPosts(uid: String): List<Post> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getHistoryPosts(uid)
            response.body() ?: emptyList()// Assuming parseMessagesList expects a String
        }
    }

    override suspend fun getPublicPosts(uid: String): List<Post> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getPublicPosts(uid)
            response.body() ?: emptyList()// Assuming parseMessagesList expects a String
        }
    }

    override suspend fun getFriendsPosts(uid: String): List<Post> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getFriendsPosts(uid, "3e38*#^#kds82K")
            Log.v("server", response.body()?.first()?.name ?: "nic")
            response.body() ?: emptyList()// Assuming parseMessagesList expects a String
        }
    }

    override suspend fun getPostById(id: String): Post {
        return withContext(Dispatchers.IO) {
            val response = apiService.getPostByID(id, "3e38*#^#kds82K")
            response.body()?.first() ?: Post.empty()// Assuming parseMessagesList expects a String
        }
    }

    override suspend fun getProfilePosts(hisUid: String, myUid: String): List<Post> {
        /*return withContext(Dispatchers.IO) {
            //val response = apiService.getProfilePosts(hisUid, "?*K.sj-.*;P//§.OrA?", myUid)
           // response.body() ?: emptyList()// Assuming parseMessagesList expects a String
        }*/
        return emptyList()
    }

    override suspend fun getComments(id: String): List<Comment> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getComments(id, "3e38*#^#kds82K")
            response.body() ?: emptyList() // Assuming parseMessagesList expects a String
        }
    }

    override suspend fun sendComment(comment: CommentData) {
        try {
            apiService.sendComment(comment, "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")
        } catch (e: Exception) {
            // Handle error, e.g., log or throw a custom exception
            Log.v("error", e.toString())
        }
    }

    override suspend fun getLikes(uid: String): List<Like> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getLikes(uid, "3e38*#^#kds82K")
            response.body() ?: emptyList()// Assuming parseMessagesList expects a String
        }
    }

    override suspend fun handleLike(
        like: Like
    ): Result<LikeResponse> = withContext(Dispatchers.IO) {
        try {


            val response = apiService.handleLike(like = like, auth = "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")

            if (response.isSuccessful) {
                response.body()?.let { body ->
                    Result.success(body)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteLike(id: String) {
        try {
            apiService.deleteLike(DeleteLike(id), "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")
        } catch (e: Exception) {
            // Handle error, e.g., log or throw a custom exception
            Log.v("error", e.toString())
        }
    }

    override suspend fun uploadPhoto(file: MultipartBody.Part): Response<String> {
        return try {
            apiService.uploadPhoto(file)
        } catch (e: Exception) {
            Response.error(500,
                "Network call has failed".toResponseBody("text/plain".toMediaTypeOrNull())
            )
            // Create a custom error response
        }
    }

    override suspend fun createPost(post: Post) {
        try {
            apiService.createPost(post, "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")
        }catch (e: Exception){
            Log.v("postError", e.toString())
        }
    }

    override suspend fun updatePost(updatePost: UpdatePost) {
        try {
            apiService.updatePost(updatePost)
        }catch (e: Exception){
            Log.v("postError", e.toString())

        }
    }

    override suspend fun deletePost(id: String) {
        try {
            apiService.deletePost(DeletePost(id), "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")
        }catch (e: Exception){
            Log.v("postError", e.toString())

        }
    }

    override suspend fun reportPost(email: String, myUid: String, postId: String, image: String) {
        try {
            apiService.sendReport(ReportData(
                sender = email,
                message = """
                    $myUid want to report this post as abusive. Check until next 24 hours.
                    Image: $image
                    Id: $postId
                """.trimIndent()
            ))
        }catch (e: Exception){
            Log.v("postError", e.toString())

        }
    }
}