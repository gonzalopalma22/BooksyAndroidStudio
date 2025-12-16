package com.example.booksy.repository

import com.example.booksy.model.LoginRequest
import com.example.booksy.model.LoginResponse
import com.example.booksy.model.RegisterRequest
import com.example.booksy.network.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Response

class AuthRepository {
    private val apiService = RetrofitClient.instance

    suspend fun login(email: String, pass: String): Response<LoginResponse> {
        val request = LoginRequest(email, pass)
        return apiService.login(request)
    }


    suspend fun register(nombre: String, email: String, pass: String, telefono: String): Response<ResponseBody> {

        val request = RegisterRequest(
            email = email,
            password = pass,
            nombre = nombre,
            telefono = telefono
        )
        return apiService.register(request)
    }
}