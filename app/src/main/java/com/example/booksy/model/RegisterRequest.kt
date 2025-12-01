package com.example.booksy.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val nombre: String,
    val telefono: String,
    val role: String = "CLIENTE"
)