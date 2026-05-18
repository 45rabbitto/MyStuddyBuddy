package com.studdy.mystudybuddy.presentation.screens.auth.viewmodel

data class RegisterUiState(

    val username: String = "",

    val email: String = "",

    val password: String = "",

    val confirmPassword: String = "",

    val isLoading: Boolean = false,

    val isSuccess: Boolean = false,

    val errorMessage: String? = null
)