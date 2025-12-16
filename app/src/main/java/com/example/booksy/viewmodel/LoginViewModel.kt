package com.example.booksy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksy.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado de la UI: Define cómo se ve la pantalla en cada momento
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel : ViewModel() {

    // Inyectamos el repositorio
    private val repository = AuthRepository()

    // StateFlow para manejar el estado reactivo
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, isError = false)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, isError = false)
    }

    fun login(onSuccess: (String, String) -> Unit) {
        val currentState = _uiState.value

        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            _uiState.value = currentState.copy(isError = true, errorMessage = "Campos vacíos")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true)
            try {
                val response = repository.login(currentState.email, currentState.password)
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!.data
                    onSuccess(data.access_token, data.user.nombre ?: "Usuario")
                } else {
                    _uiState.value = _uiState.value.copy(isError = true, errorMessage = "Credenciales inválidas")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isError = true, errorMessage = "Error de conexión")
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}