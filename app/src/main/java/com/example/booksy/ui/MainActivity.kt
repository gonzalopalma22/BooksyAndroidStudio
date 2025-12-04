package com.example.booksy

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.booksy.network.RetrofitClient
import com.example.booksy.ui.HomeScreen
import com.example.booksy.ui.LoginScreen
import com.example.booksy.ui.ProfileScreen
import com.example.booksy.ui.RegisterScreen
import com.example.booksy.ui.theme.BooksyTheme
import android.content.Context

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        RetrofitClient.initialize(this)


        val loadedToken = RetrofitClient.fetchToken()
        val savedName = loadUserName(this)

        val startScreen = if (!loadedToken.isNullOrEmpty()) "home" else "login"

        setContent {
            BooksyTheme {

                var currentScreen by remember { mutableStateOf(startScreen) }

                // Usamos los datos cargados al inicio
                var userToken by remember { mutableStateOf(loadedToken ?: "") }
                var userName by remember { mutableStateOf(savedName ?: "Invitado") }
                var userPhotoUri by remember { mutableStateOf(loadPhotoUri(this)) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        "login" -> {
                            LoginScreen(

                                onLoginSuccess = { token, name ->

                                    RetrofitClient.saveToken(token)
                                    saveUserName(this@MainActivity, name) //

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

                                onBackToLogin = {
                                    currentScreen = "login"
                                }
                            )
                        }

                        "home" -> {
                            HomeScreen(
                                nombreUsuario = userName,
                                userToken = userToken,
                                userPhotoUri = userPhotoUri,
                                onLogout = {


                                    RetrofitClient.clearSession()
                                    clearUserName(this@MainActivity)

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


    private fun saveUserName(context: Context, name: String) {
        val sharedPref = context.getSharedPreferences("BooksyPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("auth_name", name)
            apply()
        }
    }

    private fun loadUserName(context: Context): String? {
        val sharedPref = context.getSharedPreferences("BooksyPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("auth_name", null)
    }

    private fun clearUserName(context: Context) {
        val sharedPref = context.getSharedPreferences("BooksyPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("auth_name")
            apply()
        }
    }


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