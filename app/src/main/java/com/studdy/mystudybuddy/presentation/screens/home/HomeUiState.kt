package com.studdy.mystudybuddy.presentation.screens.home

import com.studdy.mystudybuddy.presentation.screens.home.DashboardItem

data class HomeUiState(

    val isLoading: Boolean = false,

    val userName: String = "Kharisma",

    val greeting: String = "Semangat belajar hari ini!!!",

    val menuList: List<DashboardItem> = emptyList(),

    val error: String? = null
)