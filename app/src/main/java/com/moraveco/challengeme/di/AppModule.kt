package com.moraveco.challengeme.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.google.auth.oauth2.GoogleCredentials
import com.google.gson.GsonBuilder
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
            // Path to your service account key JSON file
            val serviceAccount = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"challnageme\",\n" +
                    "  \"private_key_id\": \"328f3daca084b4a115122da1e7545777fcd8c8c3\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCyv/tq1fb3TjOM\\n5avJWJi9GQvOV+0D23vuXc+mmDn5cQbJxTQLCkJ1NnBNc2WWH9VvpUepz5rM4xA+\\ngTrOm2V6uHHU15asibuyldoa597KAzuH8Wcnfa/rB6/CtLVzzbr3+ecWTBqO20iO\\ncB6SGjWqrQdsygWEMSHNM+HProZa/mlNIVEEXcHWzg54qytrwFyvexp5yBQrCL/T\\n5lX0YKNibNSePO0RISgw5CXkUk8rMRRnAZOhc2gKUTkWb8eVPeBrUUdrhsxPAzoy\\njp6UN8Z/kBF9+Ug0tUfGEpvi7/d0ZkQisfZ3uujU66CI/7hkuRlZ2idfebuS+02Q\\nCmd3yKQ9AgMBAAECggEAKfySb9vmcNy7myLvoRHgfrp9XXClybwqdiku93DWXVNB\\nRu+9c8JrLeElq+upNwQvPFRUavzK3cmQLT5ps9IomhTtGBOz3r9TiXVNj/hbCpSL\\nzTjVBB+vzMM3c8LCxHrUqh7XOxORQXEcr+iCJctmQ7r0/YbOmOh5ihvftMrH4GNx\\nZCxINXBNzkuw+qfb3suG4RJp4BDw58q2la6iXfx8m8hTdZEBJLCx0C99Cs+WwaC5\\nZlARJ7dajRV1zMcYeizJUspRPfRrtHHbxkwF17dhmBuqSJ0iFd6tgTgBEFAxDXGT\\nKunzxS2U0Mttb5oZgiinkckNobOodeyW+kmo6SnUeQKBgQDjoskoPE6+vuouAQLV\\nh1mfMldW7BkMPMDRwAdIAXlMP8i71WrbTvgiVR0dv6yaYzreLt4AmAib9PgPgU6O\\nD5jh8lGusHaM77/VXmpXFl3acx3Dk328c27M4ZbBvIvv53ZXT9xsujfCRHkU1x6L\\n9eFlTTELv4I+FtjSHuk15pjeQwKBgQDJBdBjBhzcVfYMdthrmZO2dgisurQriizz\\nOe7zqdFhCs+/BK4rmzc/6lLV0PSSeFi+RHy4H/33i+tB9TxG+JoxZnqOpgQDfYDr\\nsbimBSGkN1ShU6BRzMU+QwQczBHa9JBkne68zvOAhogxpMhxcU0U0/FCBvvgtKAf\\nVMgjL2yLfwKBgDr5iqoM7c3HQn6GrohJl7OB5FTVuCuOddohqQFDuHxDrirTwOpu\\njCTA7lkttncNLEwx0jxPJzPYhIfn1UVCzzYChJ0AhMZAuu95lY1YcxpMZrKrvJCd\\nS2BzNY6d3Zda3TMDsrTMjfObr87xaK2UF0AafpFvnTueya+uaHcZdhNnAoGAO2hk\\ndV9RGCyvUm+s9d+lL78iQSnMJTHoptjwLUJ6hltTmfggIPL2GQV/BYGDReYbE9/Z\\nm/CYdZf8Jrn63l6J8x9+CLo3ZXCpsP4Mu8O4dXeEo0i7kHEbuZLEnF4SyMqOAa5K\\n57GhdNJNXZ59Pkllpkoceo7l0TqQN7Iuk3dodLECgYEAsmEUQJlFF0MCU1brrCS+\\nyZ3uYxeVScLk9y+oSbIdS3Et24TGCXJ1QTip8pvgvVhsS4FL4Y5Gx21YrUDCawg8\\n9waHeuxONOKGtbocvpXfzNbY7d3PXnVGBDGN0BxIW3ERmqj1p/lIgTmnN4TltUv9\\ndtrMXWrCxv/iSmap5dakoh4=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-xjo2r@challnageme.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"100198110186176656757\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-xjo2r%40challnageme.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}"
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