package com.studdy.mystudybuddy.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    object Home : Screen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    object Summary : Screen(
        route = "summary",
        title = "Summary",
        icon = Icons.Default.School
    )

    object Quiz : Screen(
        route = "quiz",
        title = "Quiz",
        icon = Icons.Default.Quiz
    )

    object Chatbot : Screen(
        route = "chatbot",
        title = "Chat",
        icon = Icons.Default.Chat
    )

    object Progress : Screen(
        route = "progress",
        title = "Progress",
        icon = Icons.Default.ShowChart
    )

    object Profile : Screen(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )

    object History : Screen(
        route = "history",
        title = "History",
        icon = Icons.Default.History
    )

    object QuizHistory : Screen(
        route = "quiz_history",
        title = "Quiz History",
        icon = Icons.Default.History
    )
}