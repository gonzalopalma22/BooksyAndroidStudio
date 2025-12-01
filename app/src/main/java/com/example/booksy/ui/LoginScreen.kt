package com.example.booksy.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.booksy.model.LoginRequest
import com.example.booksy.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (String, String) -> Unit,
    onGoToRegister: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val apiService = RetrofitClient.instance

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Estado para pintar de rojo los campos si hay error
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Bienvenido a Booksy", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Inicia sesión para continuar", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        // CAMPO EMAIL
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                isError = false // Si escribe, quitamos el error rojo
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = isError, // Se pone rojo si falla
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email) // Teclado con @
        )

        Spacer(modifier = Modifier.height(16.dp))

        // CAMPO CONTRASEÑA
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                isError = false
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Mensaje de error visible en texto rojo
        if (isError) {
            Text(
                text = "Correo o contraseña incorrectos",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Validación local
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                    isError = true
                } else {
                    scope.launch {
                        isLoading = true
                        isError = false // Reseteamos error antes de intentar
                        try {
                            val request = LoginRequest(email, password)
                            val response = apiService.login(request)

                            if (response.isSuccessful && response.body() != null) {
                                val loginResponse = response.body()!!

                                // ÉXITO
                                onLoginSuccess(
                                    loginResponse.data.access_token,
                                    loginResponse.data.user.nombre ?: "Usuario"
                                )
                            } else {
                                // ERROR (401, 400, etc)
                                isError = true
                                // Intentamos leer el mensaje de error del backend, si no usamos uno genérico
                                Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Ingresar")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "¿No tienes cuenta? ")
            Text(
                text = "Regístrate aquí",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.clickable { onGoToRegister() }
            )
        }
    }
}