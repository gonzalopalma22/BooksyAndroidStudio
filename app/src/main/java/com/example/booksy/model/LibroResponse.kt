package com.example.booksy.model
import com.google.gson.annotations.SerializedName

data class LibroResponse(
    val success: Boolean,
    @SerializedName("data") val data: List<Libro>
)