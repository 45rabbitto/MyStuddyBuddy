package com.studdy.mystudybuddy.presentation.screens.quiz.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FinalResultScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(text = "Quiz Selesai")

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Nilai Akhir: 95")

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {

                    }
                ) {
                    Text("Kembali")
                }
            }
        }
    }
}