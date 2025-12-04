package com.example.booksy.network

import com.example.booksy.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = sessionManager.fetchAuthToken()

        val newRequest = if (token != null) {

            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {

            request
        }

        return chain.proceed(newRequest)
    }
}