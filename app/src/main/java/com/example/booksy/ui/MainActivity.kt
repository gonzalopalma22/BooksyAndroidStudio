package com.example.booksy

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.booksy.data.local.SessionManager
import com.example.booksy.model.Libro
import com.example.booksy.network.RetrofitClient
import com.example.booksy.ui.*
import com.example.booksy.ui.theme.BooksyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        RetrofitClient.initialize(this)
        val sessionManager = SessionManager(this)
        val loadedToken = sessionManager.fetchAuthToken()


        val startScreen = if (!loadedToken.isNullOrEmpty()) "home" else "login"

        setContent {
            BooksyTheme {

                var currentScreen by remember { mutableStateOf(startScreen) }
                var userName by remember { mutableStateOf(sessionManager.fetchUserName() ?: "Usuario") }
                var userPhotoUri by remember { mutableStateOf(sessionManager.fetchPhotoUri()) }


                var selectedBook by remember { mutableStateOf<Libro?>(null) }
                val cartItems = remember { mutableStateListOf<Libro>() }
                val libraryBooks = remember { mutableStateListOf<Libro>() }

                val context = LocalContext.current

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when (currentScreen) {
                        "login" -> LoginScreen(
                            onLoginSuccess = { token, name ->
                                sessionManager.saveAuthToken(token)
                                sessionManager.saveUserName(name)
                                RetrofitClient.saveToken(token)
                                userName = name
                                currentScreen = "home"
                            },
                            onGoToRegister = { currentScreen = "register" }
                        )
                        "register" -> RegisterScreen(
                            onRegisterSuccess = { currentScreen = "login" },
                            onBackToLogin = { currentScreen = "login" }
                        )
                        "home" -> HomeScreen(
                            nombreUsuario = userName,
                            userToken = loadedToken ?: "",
                            userPhotoUri = userPhotoUri,
                            onLogout = {
                                sessionManager.clearAuthToken()
                                RetrofitClient.clearSession()
                                currentScreen = "login"
                            },
                            onNavigateToProfile = { currentScreen = "profile" },
                            onBookClick = { libro ->
                                selectedBook = libro
                                currentScreen = "detail"
                            },
                            onNavigateToLibrary = { currentScreen = "library" }
                        )
                        "detail" -> BookDetailScreen(
                            libro = selectedBook,
                            onBack = { currentScreen = "home" },
                            onAddToCart = {
                                selectedBook?.let {
                                    cartItems.add(it)
                                    Toast.makeText(context, "Agregado al carrito", Toast.LENGTH_SHORT).show()
                                    currentScreen = "cart"
                                }
                            }
                        )
                        "cart" -> CartScreen(
                            cartItems = cartItems,
                            onBack = { currentScreen = "detail" },
                            onCheckout = { currentScreen = "checkout" }
                        )
                        "checkout" -> CheckoutScreen(
                            onBack = { currentScreen = "cart" },
                            onConfirm = {

                                libraryBooks.addAll(cartItems)
                                cartItems.clear()
                                currentScreen = "success"
                            }
                        )
                        "success" -> OrderSuccessScreen(
                            onGoLibrary = { currentScreen = "library" }
                        )
                        "library" -> MyLibraryScreen(
                            librosComprados = libraryBooks,
                            onBack = { currentScreen = "home" },
                            onReadBook = { currentScreen = "reader" }
                        )
                        "reader" -> ReaderScreen(
                            onClose = { currentScreen = "library" }
                        )
                        "profile" -> ProfileScreen(
                            nombreUsuario = userName,
                            currentImageUri = userPhotoUri,
                            onBack = { currentScreen = "home" },
                            onImageSaved = { uri ->
                                userPhotoUri = uri
                                sessionManager.savePhotoUri(uri)
                            }
                        )
                    }
                }
            }
        }
    }
}