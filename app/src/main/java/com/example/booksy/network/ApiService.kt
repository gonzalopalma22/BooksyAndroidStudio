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
import okhttp3.ResponseBody
interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ResponseBody>

    @GET("libro")
    suspend fun getLibros(@Header("Authorization") token: String): Response<LibroResponse>
}