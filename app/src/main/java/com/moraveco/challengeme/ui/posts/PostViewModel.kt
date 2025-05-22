package com.moraveco.challengeme.ui.posts

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moraveco.challengeme.data.Comment
import com.moraveco.challengeme.data.CommentData
import com.moraveco.challengeme.data.Like
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.data.UpdatePost
import com.moraveco.challengeme.data.UploadResponse
import com.moraveco.challengeme.repo_impl.PostRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONException
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDateTime
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(private val repository: PostRepositoryImpl) : ViewModel() {
    private val _post = MutableStateFlow(Post.empty())
    val post: StateFlow<Post> get() = _post

    private val _profilePosts = MutableStateFlow(emptyList<Post>())
    val profilePosts: StateFlow<List<Post>> get() = _profilePosts

    private val _historyPosts = MutableStateFlow(emptyList<Post>())
    val historyPosts: StateFlow<List<Post>> get() = _historyPosts

    private val _publicPosts = MutableStateFlow(emptyList<Post>())
    val publicPosts: StateFlow<List<Post>> get() = _publicPosts

    private val _friendsPosts = MutableStateFlow(emptyList<Post>())
    val friendsPosts: StateFlow<List<Post>> get() = _friendsPosts

    private val _comments = MutableStateFlow(emptyList<Comment>())
    val comments: StateFlow<List<Comment>> get() = _comments

    private val _likes = MutableStateFlow(emptyList<Like>())
    val likes: StateFlow<List<Like>> get() = _likes

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uploadResponse = MutableLiveData<UploadResponse?>()
    val uploadResponse: LiveData<UploadResponse?> = _uploadResponse

    fun getPostById(userId: String) {
        viewModelScope.launch {
            try {
                val post = repository.getPostById(userId)
                _post.value = post
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }

    fun getPostsById(uid: String){
        viewModelScope.launch {
            try {
                val posts = repository.getPostsById(uid)
                _profilePosts.value = posts
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }

    fun getHistoryPosts(hisUid: String){
        viewModelScope.launch {
            try {
                val posts = repository.getHistoryPosts(hisUid)
                _historyPosts.value = posts
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }

    fun getFriendsPosts(hisUid: String){
        viewModelScope.launch {
            try {
                val posts = repository.getFriendsPosts(hisUid)
                _friendsPosts.value = posts
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }

    fun getPublicPosts(hisUid: String){
        viewModelScope.launch {
            try {
                val posts = repository.getPublicPosts(hisUid)
                _publicPosts.value = posts
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }

    fun getComments(id: String){
        viewModelScope.launch {
            try {
                val comments = repository.getComments(id)
                _comments.value = comments
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }

    }

    fun updatePost(updatePost: UpdatePost, onSuccess: () -> Unit){
        viewModelScope.launch {
            try {
                repository.updatePost(updatePost)
                onSuccess()
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }

    fun deletePost(id: String, onSuccess: () -> Unit){
        viewModelScope.launch {
            try {
                repository.deletePost(id)
                onSuccess()
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }

    fun uploadPhoto(photoFile: File) {
        viewModelScope.launch(Dispatchers.Main) {
            _isLoading.value = true
            val file = File(photoFile.path)
            val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val response = repository.uploadPhoto(multipartBody)
            if (response.isSuccessful) {
                _uploadResponse.value = UploadResponse(response.isSuccessful, response.message(), response.body() ?: "")
            } else {
                _uploadResponse.value = UploadResponse(!response.isSuccessful, response.message(), "")
            }
            _isLoading.value = false
        }
    }

    fun clearUploadResult() {
        _uploadResponse.value = null
    }



    fun sendComment(commentData: CommentData, onSuccess: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.sendComment(commentData)
                onSuccess()
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }

    fun getLikes(uid: String) {
        viewModelScope.launch {
            try {
                val likes = repository.getLikes(uid)
                _likes.value = likes
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }

    fun insertLike(like: Like, onSuccess: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insertLike(like)
                onSuccess()
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }

    fun deleteLike(id: String, onSuccess: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteLike(id)
                onSuccess()
            } catch (e: Exception) {
                // Handle error, e.g., log or show an error message to the user
                Log.v("error", e.toString())
            }
        }
    }


}