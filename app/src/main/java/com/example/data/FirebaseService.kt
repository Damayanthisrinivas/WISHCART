package com.example.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Moshi Serializable Data Classes for Firebase REST API ---

data class SignUpRequest(
    val email: String,
    val password: String,
    val returnSecureToken: Boolean = true
)

data class SignUpResponse(
    val idToken: String?,
    val email: String?,
    val refreshToken: String?,
    val expiresIn: String?,
    val localId: String?
)

data class SignInRequest(
    val email: String,
    val password: String,
    val returnSecureToken: Boolean = true
)

data class SignInResponse(
    val idToken: String?,
    val email: String?,
    val refreshToken: String?,
    val expiresIn: String?,
    val localId: String?,
    val displayName: String?,
    val registered: Boolean?
)

data class ResetPasswordRequest(
    val requestType: String = "PASSWORD_RESET",
    val email: String
)

data class ResetPasswordResponse(
    val email: String?
)

interface FirebaseApiService {
    @POST("v1/accounts:signUp")
    suspend fun signUp(
        @Query("key") apiKey: String,
        @Body request: SignUpRequest
    ): SignUpResponse

    @POST("v1/accounts:signInWithPassword")
    suspend fun signInWithPassword(
        @Query("key") apiKey: String,
        @Body request: SignInRequest
    ): SignInResponse

    @POST("v1/accounts:sendOobCode")
    suspend fun sendOobCode(
        @Query("key") apiKey: String,
        @Body request: ResetPasswordRequest
    ): ResetPasswordResponse
}

object FirebaseClient {
    private const val BASE_URL = "https://identitytoolkit.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: FirebaseApiService = retrofit.create(FirebaseApiService::class.java)

    /**
     * Gets the configured Firebase Web API Key if available.
     */
    fun getApiKey(): String {
        return try {
            val key = BuildConfig.FIREBASE_API_KEY
            if (key.isEmpty() || key == "YOUR_FIREBASE_API_KEY" || key == "FIREBASE_API_KEY") "" else key
        } catch (e: Exception) {
            ""
        }
    }
}
