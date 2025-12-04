package com.example.booksy.viewmodel

import com.example.booksy.model.Libro
import com.example.booksy.repository.LibroRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class LibroViewModelTest : StringSpec() {

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf

    private val testDispatcher = StandardTestDispatcher()
    private val mockRepository: LibroRepository = mockk()


    private val libroFantasia = Libro(
        _id = "1", titulo = "Harry Potter", genero = "692d146e526baef8a83dd11e", // ID de Fantasía según tu ViewModel
        precio = 20.0, isbn = "1111", imagen = ""
    )
    private val libroCienciaFiccion = Libro(
        _id = "2", titulo = "Dune", genero = "692da69c633a7e8c0666631d", // ID de Ciencia Ficción
        precio = 50.0, isbn = "2222", imagen = ""
    )
    private val listaPrueba = listOf(libroFantasia, libroCienciaFiccion)

    init {
        coroutineTestScope = true
        Dispatchers.setMain(testDispatcher)

        afterSpec {
            Dispatchers.resetMain()
        }

        "1. El estado inicial debe ser lista vacía y no cargando" {
            println("\n➤ EJECUTANDO TEST 1: Verificar estado inicial...")
            val viewModel = LibroViewModel(mockRepository)

            viewModel.libros.value.shouldBe(emptyList())
            viewModel.isLoading.value.shouldBe(false)
            println("   ✔ ÉXITO: El ViewModel inicia vacío y sin cargar.")
        }


        "2. cargarLibros debe actualizar la lista y el estado de carga exitosamente" {
            println("\n➤ EJECUTANDO TEST 2: Simular carga exitosa de libros...")
            runTest(testDispatcher) {
                coEvery { mockRepository.obtenerLibros(any()) } returns listaPrueba
                val viewModel = LibroViewModel(mockRepository)

                println("   ...Llamando a cargarLibros con token válido")
                viewModel.cargarLibros("token_valido")
                testDispatcher.scheduler.advanceUntilIdle()

                viewModel.libros.value.shouldBe(listaPrueba)
                viewModel.isLoading.value.shouldBe(false)
                println("   ✔ ÉXITO: La lista se actualizó con ${listaPrueba.size} libros.")
            }
        }


        "3. cargarLibros debe manejar excepciones sin crashear" {
            println("\n➤ EJECUTANDO TEST 3: Simular error de API (Sin Internet/Caída)...")
            runTest(testDispatcher) {
                coEvery { mockRepository.obtenerLibros(any()) } throws Exception("Error API")
                val viewModel = LibroViewModel(mockRepository)

                viewModel.cargarLibros("token_valido")
                testDispatcher.scheduler.advanceUntilIdle()

                viewModel.libros.value.shouldBe(emptyList())
                viewModel.isLoading.value.shouldBe(false)
                println("   ✔ ÉXITO: El error fue capturado y la app no crasheó.")
            }
        }


        "4. cargarLibros NO debe llamar al repositorio si el token está vacío" {
            println("\n➤ EJECUTANDO TEST 4: Validar token vacío...")
            runTest(testDispatcher) {
                val viewModel = LibroViewModel(mockRepository)

                viewModel.cargarLibros("") // Token vacío
                testDispatcher.scheduler.advanceUntilIdle()


                coVerify(exactly = 0) { mockRepository.obtenerLibros(any()) }
                viewModel.libros.value.shouldBe(emptyList())
                println("   ✔ ÉXITO: No se hizo llamada a la API porque el token estaba vacío.")
            }
        }


        "5. aplicarFiltros debe filtrar correctamente por título" {
            println("\n➤ EJECUTANDO TEST 5: Filtrar por Título ('Harry')...")
            runTest(testDispatcher) {
                coEvery { mockRepository.obtenerLibros(any()) } returns listaPrueba
                val viewModel = LibroViewModel(mockRepository)

                viewModel.cargarLibros("token")
                testDispatcher.scheduler.advanceUntilIdle()

                viewModel.aplicarFiltros(query = "Harry", category = "Todas", minPrice = 0.0, maxPrice = 100.0)

                viewModel.libros.value.size.shouldBe(1)
                viewModel.libros.value.first().titulo.shouldBe("Harry Potter")
                println("   ✔ ÉXITO: Se encontró 'Harry Potter' correctamente.")
            }
        }


        "6. aplicarFiltros debe filtrar por categoría usando el mapa de IDs correcto" {
            println("\n➤ EJECUTANDO TEST 6: Filtrar por Categoría ('Fantasia')...")
            runTest(testDispatcher) {
                coEvery { mockRepository.obtenerLibros(any()) } returns listaPrueba
                val viewModel = LibroViewModel(mockRepository)

                viewModel.cargarLibros("token")
                testDispatcher.scheduler.advanceUntilIdle()

                viewModel.aplicarFiltros(query = "", category = "Fantasia", minPrice = 0.0, maxPrice = 100.0)

                viewModel.libros.value.size.shouldBe(1)
                viewModel.libros.value.first().genero.shouldBe("692d146e526baef8a83dd11e")
                println("   ✔ ÉXITO: Se filtró la categoría Fantasía usando su ID interno.")
            }
        }


        "7. aplicarFiltros debe filtrar libros dentro del rango de precio" {
            println("\n➤ EJECUTANDO TEST 7: Filtrar por Precio (Max 30.0)...")
            runTest(testDispatcher) {
                coEvery { mockRepository.obtenerLibros(any()) } returns listaPrueba
                val viewModel = LibroViewModel(mockRepository)

                viewModel.cargarLibros("token")
                testDispatcher.scheduler.advanceUntilIdle()


                viewModel.aplicarFiltros(query = "", category = "Todas", minPrice = 0.0, maxPrice = 30.0)

                viewModel.libros.value.size.shouldBe(1)
                viewModel.libros.value.first().precio.shouldBe(20.0)
                println("   ✔ ÉXITO: Solo quedó el libro de precio 20.0 (Harry Potter).")
            }
        }
    }
}