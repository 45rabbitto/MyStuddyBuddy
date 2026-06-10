package com.studdy.mystudybuddy.presentation.screens.auth.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun updateUsername(username: String) {
        _uiState.value =
            _uiState.value.copy(
                username = username
            )
    }

    fun updatePassword(password: String) {
        _uiState.value =
            _uiState.value.copy(
                password = password
            )
    }

    fun login() {

        _uiState.value =
            _uiState.value.copy(
                isLoading = true
            )
        if (
            _uiState.value.username == "admin" &&
            _uiState.value.password == "123"
        ) {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )

        } else {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Username atau password salah"
                )
        }
    }
}