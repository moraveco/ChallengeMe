package com.moraveco.challengeme.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.google.auth.oauth2.GoogleCredentials
import com.moraveco.challengeme.BuildConfig
import com.moraveco.challengeme.api.ApiService
import com.moraveco.challengeme.constants.Constants.Companion.BASE_URL
import com.moraveco.challengeme.notifications.NotificationAPI
import com.moraveco.challengeme.notifications.NotificationConstants
import com.moraveco.challengeme.user_settings.UserSettings
import com.moraveco.challengeme.user_settings.UserSettingsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.ByteArrayInputStream
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private fun getAccessToken(): String? {
        return try {
            val serviceAccount = """
            {
              "type": "service_account",
              "project_id": "${BuildConfig.FIREBASE_PROJECT_ID}",
              "private_key_id": "${BuildConfig.FIREBASE_PRIVATE_KEY_ID}",
              "private_key": "${BuildConfig.FIREBASE_PRIVATE_KEY}",
              "client_email": "${BuildConfig.FIREBASE_CLIENT_EMAIL}",
              "client_id": "${BuildConfig.FIREBASE_CLIENT_ID}",
              "auth_uri": "https://accounts.google.com/o/oauth2/auth",
              "token_uri": "https://oauth2.googleapis.com/token",
              "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
              "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-xjo2r%40challnageme.iam.gserviceaccount.com",
              "universe_domain": "googleapis.com"
            }
        """.trimIndent()

            val stream = ByteArrayInputStream(serviceAccount.toByteArray())
            val googleCredentials = GoogleCredentials.fromStream(stream)
                .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
            googleCredentials.refreshIfExpired()
            googleCredentials.accessToken.tokenValue
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Provides
    @Singleton
    fun provideUserSettingsDataStore(@ApplicationContext context: Context): DataStore<UserSettings> {
        return  DataStoreFactory.create(
            serializer = UserSettingsSerializer,
            produceFile = {
                context.dataStoreFile(
                    fileName = "app_user_settings"
                )
            }
        )
    }

    @Provides
    @Singleton
    fun provideApiService(): ApiService {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${getAccessToken()}")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideNotificationService(client: OkHttpClient): NotificationAPI {
        return Retrofit.Builder()
            .baseUrl(NotificationConstants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }

}