package com.example.booksy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.booksy.model.Libro
import com.example.booksy.repository.LibroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LibroViewModel(private val repository: LibroRepository) : ViewModel() {

    private val _libros = MutableStateFlow<List<Libro>>(emptyList())
    val libros: StateFlow<List<Libro>> = _libros

    private var listaOriginal: List<Libro> = emptyList()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun cargarLibros(token: String) {
        viewModelScope.launch {
            if (token.isNotEmpty()) {
                _isLoading.value = true
                try {
                    val resultado = repository.obtenerLibros(token)
                    listaOriginal = resultado
                    _libros.value = resultado
                } catch (e: Exception) {
                    println("Error cargando libros: ${e.message}")
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun aplicarFiltros(query: String, category: String, minPrice: Double, maxPrice: Double) {
        var listaFiltrada = listaOriginal


        if (query.isNotEmpty()) {
            listaFiltrada = listaFiltrada.filter { libro ->
                libro.titulo.contains(query, ignoreCase = true)
            }
        }


        if (category != "Todas") {


            val mapaCategorias = mapOf(
                "Fantasia"        to "692d146e526baef8a83dd11e",
                "Ciencia Ficción" to "692da69c633a7e8c0666631d",
                "Historia"        to "692da973633a7e8c0666631f",
                "Programación"    to "692da9a3633a7e8c06666321"
            )

            val idBuscado = mapaCategorias[category]

            if (idBuscado != null) {
                listaFiltrada = listaFiltrada.filter { libro ->

                    libro.genero.equals(idBuscado, ignoreCase = true)
                }
            } else {

                listaFiltrada = emptyList()
            }
        }

        // 3. Precio
        listaFiltrada = listaFiltrada.filter { libro ->
            val precioLibro = libro.precio ?: 0.0
            precioLibro in minPrice..maxPrice
        }

        _libros.value = listaFiltrada
    }
}

class LibroViewModelFactory(private val repository: LibroRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LibroViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}