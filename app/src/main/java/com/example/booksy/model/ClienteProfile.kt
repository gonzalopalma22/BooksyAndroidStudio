package com.example.booksyapp.model // Tu paquete

data class ClienteProfile(
    val _id: String,
    val user: String, //
    val nombre: String?,
    val telefono: String?,
    val direccion: String?,
    val preferenciasLectura: String? //
)