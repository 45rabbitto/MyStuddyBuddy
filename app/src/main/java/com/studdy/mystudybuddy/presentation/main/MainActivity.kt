package com.studdy.mystuddybuddy.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.databinding.ActivityDashboardBinding
import com.studdy.mystudybuddy.presentation.history.activity.FileHistoryActivity
import com.studdy.mystudybuddy.presentation.screens.profile.activity.ProfileActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClick()
    }

    private fun setupClick() {

        // MENU HISTORY
        binding.history.setOnClickListener {
            startActivity(Intent(this, FileHistoryActivity::class.java))
        }

        // MENU ALUR BELAJAR
        binding.alur.setOnClickListener {
            // nanti bisa ke ProgressActivity
        }

        // MENU PROGRES BELAJAR
        binding.progres.setOnClickListener {
            // nanti bisa ke Quiz/ProgressActivity
=======
package com.studdy.mystudybuddy.presentation.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HistoryScreen() {

    val historyList = listOf(
        "Matematika",
        "Pemrograman",
        "Bahasa Inggris"
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