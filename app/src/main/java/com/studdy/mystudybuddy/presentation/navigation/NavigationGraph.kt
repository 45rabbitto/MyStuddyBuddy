package com.studdy.mystudybuddy.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.studdy.mystudybuddy.presentation.screens.chatbot.ChatbotScreen
import com.studdy.mystudybuddy.presentation.screens.home.HomeScreen
import com.studdy.mystudybuddy.presentation.screens.progress.ProgressScreen
import com.studdy.mystudybuddy.presentation.screens.quiz.QuizScreen
import com.studdy.mystudybuddy.presentation.screens.summary.SummaryScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Summary : Screen("summary", "Summary", Icons.Default.Description)
    object Quiz : Screen("quiz", "Quiz", Icons.Default.Quiz)
    object Chatbot : Screen("chatbot", "AI Tutor", Icons.Default.Chat)
    object Progress : Screen("progress", "Progress", Icons.Default.TrendingUp)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Summary.route) {
            SummaryScreen(navController)
        }
        composable(Screen.Quiz.route) {
            QuizScreen(navController)
        }
        composable(Screen.Chatbot.route) {
            ChatbotScreen(navController)
        }
        composable(Screen.Progress.route) {
            ProgressScreen(navController)
        }
    }
}