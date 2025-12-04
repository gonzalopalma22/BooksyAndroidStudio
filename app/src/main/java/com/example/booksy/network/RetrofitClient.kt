package com.example.booksy.network

import android.content.Context
import android.util.Log
import com.example.booksy.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit



object RetrofitClient {


    private const val BASE_URL = "https://booksydesarrollo.onrender.com/api/"


    private lateinit var sessionManager: SessionManager


    private val authInterceptor: AuthInterceptor by lazy {
        AuthInterceptor(sessionManager)
    }


    fun initialize(context: Context) {
        sessionManager = SessionManager(context.applicationContext)
    }

    fun fetchToken(): String? {
        return if (::sessionManager.isInitialized) {
            sessionManager.fetchAuthToken()
        } else {
            null
        }
    }

    fun saveToken(token: String) {
        if (::sessionManager.isInitialized) {
            sessionManager.saveAuthToken(token)
        } else {
            Log.e("RetrofitClient", "SessionManager no inicializado.")
        }
    }

    fun clearSession() {
        if (::sessionManager.isInitialized) {
            sessionManager.clearAuthToken()
        }
    }


    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Nivel de detalle del log
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Interceptor de token
            .addInterceptor(loggingInterceptor) // Interceptor de log
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }


    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}