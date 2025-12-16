package com.example.booksy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksy.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()


    fun onNombreChange(v: String) { _uiState.value = _uiState.value.copy(nombre = v, errorMessage = null) }
    fun onEmailChange(v: String) { _uiState.value = _uiState.value.copy(email = v, errorMessage = null) }
    fun onTelefonoChange(v: String) { _uiState.value = _uiState.value.copy(telefono = v, errorMessage = null) }
    fun onPasswordChange(v: String) { _uiState.value = _uiState.value.copy(password = v, errorMessage = null) }
    fun onConfirmPasswordChange(v: String) { _uiState.value = _uiState.value.copy(confirmPassword = v, errorMessage = null) }

    fun register() {
        val state = _uiState.value


        if (state.nombre.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Faltan campos obligatorios")
            return
        }

        if (state.password != state.confirmPassword) {
            _uiState.value = state.copy(errorMessage = "Las contraseñas no coinciden")
            return
        }


        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            try {
                val response = repository.register(
                    nombre = state.nombre,
                    email = state.email,
                    pass = state.password,
                    telefono = state.telefono
                )

                if (response.isSuccessful) {
                    _uiState.value = state.copy(isLoading = false, isSuccess = true)
                } else {
                    val errorCode = response.code()
                    _uiState.value = state.copy(isLoading = false, errorMessage = "Error en el registro ($errorCode)")
                }
            } catch (e: Exception) {
                Log.e("REGISTER_VM", "Error", e)
                _uiState.value = state.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState()
    }
}