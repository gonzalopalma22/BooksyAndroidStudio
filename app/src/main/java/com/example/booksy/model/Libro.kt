package com.example.booksy.model
import com.google.gson.annotations.SerializedName

data class Libro(
    val _id: String,
    val titulo: String,

    @SerializedName("categoria")
    val genero: String,

    val precio: Double?,
    val isbn: String?,
    val imagen: String?
)