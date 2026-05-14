package com.studdy.mystudybuddy.presentation.profile

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.data.ProgressRepository

class ProfileActivity : AppCompatActivity() {

    private lateinit var repo: ProgressRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        repo = ProgressRepository(this)

        loadStatistics()
        setupLogout()
    }

    private fun loadStatistics() {
        val uploadCount = repo.getUploadCount()
        val summaryCount = repo.getSummaryCount()
        val quizCount = repo.getQuizCount()
        val chatCount = repo.getChatbotCount()

        findViewById<TextView>(R.id.tvUploadCount).text =
            "File diupload : $uploadCount"

        findViewById<TextView>(R.id.tvSummaryCount).text =
            "Ringkasan dibuat : $summaryCount"

        findViewById<TextView>(R.id.tvQuizCount).text =
            "Quiz dikerjakan : $quizCount"

        findViewById<TextView>(R.id.tvChatCount).text =
            "Chat AI : $chatCount"
    }

    private fun setupLogout() {
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnLogout.setOnClickListener {
            // sementara hanya menutup aplikasi
            finishAffinity()
        }
    }
}