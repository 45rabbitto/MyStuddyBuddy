package com.studdy.mystudybuddy.presentation.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class HistoryItem(
    val title: String,
    val date: String
)

@Composable
fun HistoryScreen() {

    val historyList = listOf(
        HistoryItem("Materi AI.pdf", "13 Mei 2026"),
        HistoryItem("Machine Learning.docx", "12 Mei 2026"),
        HistoryItem("Pemrograman Mobile.pdf", "10 Mei 2026")
    )

    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        items(historyList) { item ->
            HistoryCard(item)
        }
    }
}

@Composable
fun HistoryCard(item: HistoryItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
            Text(text = item.date, style = MaterialTheme.typography.bodyMedium)
        }
    }
}