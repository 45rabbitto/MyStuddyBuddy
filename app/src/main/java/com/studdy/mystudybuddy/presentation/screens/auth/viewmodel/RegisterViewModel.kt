package com.studdy.mystudybuddy.presentation.screens.auth.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun updateUsername(username: String) {
        _uiState.value =
            _uiState.value.copy(
                username = username
            )
    }

    fun updateEmail(email: String) {
        _uiState.value =
            _uiState.value.copy(
                email = email
            )
    }

    fun updatePassword(password: String) {
        _uiState.value =
            _uiState.value.copy(
                password = password
            )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value =
            _uiState.value.copy(
                confirmPassword = confirmPassword
            )
    }

    fun register() {

        if (_uiState.value.password != _uiState.value.confirmPassword) {

            _uiState.value =
                _uiState.value.copy(
                    errorMessage = "Password tidak cocok"
                )

            return
        }

        _uiState.value =
            _uiState.value.copy(
                isSuccess = true
            )
    }
}