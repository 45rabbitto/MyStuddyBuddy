package com.studdy.mystudybuddy.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(
                todayProgress = 65,
                recommendations = listOf(
                    "Android Coroutines Deep Dive",
                    "Clean Architecture Guide",
                    "Jetpack Compose Mastery"
                ),
                recentDocuments = listOf(
                    "Kotlin Advanced Notes.pdf",
                    "MVVM Architecture.docx",
                    "Dependency Injection Guide.pdf"
                ),
                xpPoints = 1250,
                streakDays = 7
            )
        }
    }

    data class HomeUiState(
        val todayProgress: Int = 0,
        val recommendations: List<String> = emptyList(),
        val recentDocuments: List<String> = emptyList(),
        val isLoading: Boolean = false,
        val xpPoints: Int = 0,
        val streakDays: Int = 0
    )
}