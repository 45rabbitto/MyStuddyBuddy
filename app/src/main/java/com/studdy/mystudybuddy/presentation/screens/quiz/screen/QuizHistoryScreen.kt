package com.studdy.mystudybuddy.presentation.screens.quiz.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuizHistoryScreen() {

    val historyList = listOf(
        "Quiz Matematika - 90",
        "Quiz Bahasa Inggris - 85",
        "Quiz Pemrograman - 95"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        items(historyList) { item ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {

                Text(
                    text = item,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}