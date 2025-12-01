package com.example.booksy.model

import com.google.gson.annotations.SerializedName


data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData
)


data class LoginData(
    val user: Usuario,
    @SerializedName(value = "access_token", alternate = ["token"])
    val access_token: String
)