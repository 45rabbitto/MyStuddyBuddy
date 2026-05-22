package com.studdy.mystudybuddy.presentation.screens.quiz.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.home.activity.DashboardActivity
import java.text.SimpleDateFormat
import java.util.*

class HasilKuisActivity : AppCompatActivity() {

    private lateinit var btnDashboard: Button
    private lateinit var tvSkor: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_kuis)

        btnDashboard = findViewById(R.id.btnDashboard)
        tvSkor = findViewById(R.id.tvSkor)

        val score = intent.getIntExtra("SCORE", 0)
        val fileName = intent.getStringExtra("FILE_NAME") ?: "Materi"

        tvSkor.text = score.toString()

        updateProgress(score)
        saveToHistory(fileName)
        markMateri(fileName)

        btnDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    private fun updateProgress(score: Int) {

        val prefs = getSharedPreferences("progress_data", MODE_PRIVATE)

        val quiz = prefs.getInt("quiz_count", 0)
        val avg = prefs.getInt("avg_score", 0)

        val newAvg = if (quiz == 0) score else (avg + score) / 2

        prefs.edit()
            .putInt("quiz_count", quiz + 1)
            .putInt("avg_score", newAvg)
            .apply()
    }

    private fun saveToHistory(fileName: String) {

        val prefs = getSharedPreferences("history_data", MODE_PRIVATE)

        val set = prefs.getStringSet("files", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        set.add("$fileName|$date")

        prefs.edit().putStringSet("files", set).apply()
    }

    private fun markMateri(fileName: String) {

        val prefs = getSharedPreferences("progress_data", MODE_PRIVATE)

        prefs.edit()
            .putString("last_file", fileName)
            .putInt("materi_count", prefs.getInt("materi_count", 0) + 1)
            .apply()
    }
}