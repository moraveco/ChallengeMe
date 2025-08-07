package com.moraveco.challengeme.repo_impl

import android.util.Log
import com.moraveco.challengeme.api.ApiService
import com.moraveco.challengeme.data.BlockUser
import com.moraveco.challengeme.data.DeletePost
import com.moraveco.challengeme.data.ProfileUser
import com.moraveco.challengeme.data.UpdateToken
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.repo.Result
import com.moraveco.challengeme.repo.UserRepository
import io.reactivex.plugins.RxJavaPlugins.onError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val apiService: ApiService) : UserRepository {
    override suspend fun getUserById(uid: String) : Result<ProfileUser, Error>{
        return withContext(Dispatchers.IO){
            val response = apiService.getUserById(uid, "3e38*#^#kds82K")
            if(response.isSuccessful){
                Result.Success(response.body()?.first() ?: ProfileUser.empty())
            }else{
                Result.Error(Error("There was an error"))
            }
        }
    }

    override suspend fun getAllUsers() : List<User> {
        return withContext(Dispatchers.IO){
            val response = apiService.getAllUsers()
            response.body() ?: emptyList()
        }
    }

    /*override suspend fun updateStatus(updateStatus: UpdateStatus) {
        try {
        apiService.updateStatus(updateStatus)
        }catch (e: Exception){
            Log.v("update", e.toString())
        }
    }*/

    override suspend fun updateToken(updateToken: UpdateToken) {
        try {
            apiService.updateToken(updateToken, "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")
        }catch (e: Exception){
            Log.v("update", e.toString())
        }
    }

    override suspend fun blockUser(blockUser: BlockUser) {
        try {
            apiService.blockUser(blockUser, "278c3ec18cb1bbb92262fabe72a20ebe1813dec3792043be303b82a3ea245ecf")
        }catch (e: Exception){
            Log.v("update", e.toString())
        }
    }
}