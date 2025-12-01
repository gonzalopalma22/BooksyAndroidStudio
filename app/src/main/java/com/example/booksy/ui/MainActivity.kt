package com.example.booksy

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.booksy.ui.HomeScreen
import com.example.booksy.ui.LoginScreen
import com.example.booksy.ui.ProfileScreen
import com.example.booksy.ui.RegisterScreen
import com.example.booksy.ui.theme.BooksyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val sessionData = loadSession(this)
        val savedToken = sessionData.first
        val savedName = sessionData.second


        val startScreen = if (!savedToken.isNullOrEmpty()) "home" else "login"

        setContent {
            BooksyTheme {

                var currentScreen by remember { mutableStateOf(startScreen) }


                var userToken by remember { mutableStateOf(savedToken ?: "") }
                var userName by remember { mutableStateOf(savedName ?: "") }


                var userPhotoUri by remember { mutableStateOf(loadPhotoUri(this)) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        "login" -> {
                            LoginScreen(
                                onLoginSuccess = { token, name ->

                                    saveSession(this@MainActivity, token, name)

                                    userToken = token
                                    userName = name
                                    currentScreen = "home"
                                },
                                onGoToRegister = { currentScreen = "register" }
                            )
                        }
                        "register" -> {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    currentScreen = "login"
                                },
                                // 👇 ESTO ES LO IMPORTANTE:
                                onBackToLogin = {
                                    currentScreen = "login" // Esto cambia la pantalla
                                }
                            )
                        }

                        "home" -> {
                            HomeScreen(
                                nombreUsuario = userName,
                                userToken = userToken,
                                userPhotoUri = userPhotoUri,
                                onLogout = {

                                    clearSession(this@MainActivity)

                                    userToken = ""
                                    currentScreen = "login"
                                },
                                onNavigateToProfile = { currentScreen = "profile" }
                            )
                        }
                        "profile" -> {
                            ProfileScreen(
                                nombreUsuario = userName,
                                currentImageUri = userPhotoUri,
                                onBack = { currentScreen = "home" },
                                onImageSaved = { newUri ->
                                    userPhotoUri = newUri
                                    savePhotoUri(this@MainActivity, newUri)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    private fun saveSession(context: Context, token: String, name: String) {
        val sharedPref = context.getSharedPreferences("BooksyPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("auth_token", token)
            putString("auth_name", name)
            apply() // Guardar cambios
        }
    }

    private fun loadSession(context: Context): Pair<String?, String?> {
        val sharedPref = context.getSharedPreferences("BooksyPrefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", null)
        val name = sharedPref.getString("auth_name", null)
        return Pair(token, name)
    }

    private fun clearSession(context: Context) {
        val sharedPref = context.getSharedPreferences("BooksyPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("auth_token")
            remove("auth_name")
            apply()
        }
    }

    // B. FOTO DE PERFIL
    private fun savePhotoUri(context: Context, uri: Uri) {
        val sharedPref = context.getSharedPreferences("BooksyPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("profile_photo", uri.toString())
            apply()
        }
    }

    private fun loadPhotoUri(context: Context): Uri? {
        val sharedPref = context.getSharedPreferences("BooksyPrefs", Context.MODE_PRIVATE)
        val uriString = sharedPref.getString("profile_photo", null)
        return uriString?.let { Uri.parse(it) }
    }
}