package com.example.booksy.repository

import com.example.booksy.model.Libro
import com.example.booksy.network.ApiService

class LibroRepository(private val apiService: ApiService) {

    suspend fun obtenerLibros(token: String): List<Libro> {


        val tokenFormateado = "Bearer $token"

        println("📡 REPOSITORIO: Solicitando lista de libros...")

        return try {
            // 2. LLAMAMOS A LA API
            val response = apiService.getLibros(tokenFormateado)

            if (response.isSuccessful) {

                val respuesta = response.body()
                val lista = respuesta?.data ?: emptyList()

                println("Éxito: ${lista.size} libros encontrados.")
                return lista
            } else {
                println("Error API: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("🔥 Error de conexión: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}