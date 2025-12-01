package com.example.booksy.model

data class Usuario(
    val _id: String,
    val email: String,
    val role: String,
    val nombre: String?,
    val telefono: String?
)