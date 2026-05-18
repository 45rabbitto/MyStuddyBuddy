package com.studdy.mystudybuddy.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.studdy.mystudybuddy.R

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FB))
            .padding(24.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.dashboard),
            contentDescription = "Welcome Image",
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "MyStudyBuddy",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Belajar lebih cerdas dan terorganisir",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onLoginClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors()
        ) {
            Text("Masuk")
        }

        Button(
            onClick = onRegisterClick,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Daftar")
        }
    }
}