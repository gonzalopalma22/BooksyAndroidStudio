package com.example.booksy.network

import com.example.booksy.model.LibroResponse
import com.example.booksy.model.LoginRequest
import com.example.booksy.model.LoginResponse
import com.example.booksy.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    @GET("api/libro")
    suspend fun getLibros(@Header("Authorization") token: String): Response<LibroResponse>
}