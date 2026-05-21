package com.studdy.mystudybuddy.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    object Home : Screen(
        "home",
        "Home",
        Icons.Default.Home
    )

    object Summary : Screen(
        "summary",
        "Summary",
        Icons.Default.School
    )

    object Quiz : Screen(
        "quiz",
        "Quiz",
        Icons.Default.Assignment
    )

    object Chatbot : Screen(
        "chatbot",
        "Chat",
        Icons.Default.Chat
    )

    object Progress : Screen(
        "progress",
        "Progress",
        Icons.Default.ShowChart
    )

    object Profile : Screen(
        "profile",
        "Profile",
        Icons.Default.Person
    )

    object History : Screen(
        "history",
        "History",
        Icons.Default.History
    )

    object QuizHistory : Screen(
        "quiz_history",
        "Quiz History",
        Icons.Default.History
    )
}