package com.studdy.mystudybuddy.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.studdy.mystudybuddy.presentation.screens.chatbot.ChatbotScreen
import com.studdy.mystudybuddy.presentation.screens.history.HistoryScreen
import com.studdy.mystudybuddy.presentation.screens.home.HomeScreen
import com.studdy.mystudybuddy.presentation.screens.progress.ProgressScreen
import com.studdy.mystudybuddy.presentation.screens.quiz.QuizScreen
import com.studdy.mystudybuddy.presentation.screens.quiz_history.QuizHistoryScreen
import com.studdy.mystudybuddy.presentation.screens.summary.SummaryScreen

// PENTING: pakai Screen dari Screen.kt (jangan buat ulang)

@Composable
fun NavigationGraph(
    navController: NavHostController
) {
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

        composable(Screen.History.route) {
            HistoryScreen()
        }

        composable(Screen.QuizHistory.route) {
            QuizHistoryScreen()
        }
    }
}