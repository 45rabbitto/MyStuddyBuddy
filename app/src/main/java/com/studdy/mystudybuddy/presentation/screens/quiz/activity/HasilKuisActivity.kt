package com.studdy.mystudybuddy.presentation.screens.quiz.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.home.activity.DashboardActivity
import com.studdy.mystudybuddy.presentation.screens.quiz.model.QuizResult

class HasilKuisActivity : AppCompatActivity() {

    private lateinit var tvBenar: TextView
    private lateinit var tvSalah: TextView
    private lateinit var tvSkor: TextView
    private lateinit var btnDashboard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_kuis)

        initViews()

        val prefs = getSharedPreferences("progress_data", MODE_PRIVATE)

        val quiz = prefs.getInt("quiz_count", 0)
        val oldAvg = prefs.getInt("avg_score", 0)

        val result = getQuizResult()

        // kalau result null, jangan crash
        val newScore = result?.score ?: 0
        val newAvg = if (quiz == 0) newScore else (oldAvg + newScore) / 2

        prefs.edit()
            .putInt("quiz_count", quiz + 1)
            .putInt("avg_score", newAvg)
            .putInt("last_score", newScore)
            .apply()

        // tampilkan UI (SAFE)
        result?.let {
            tvBenar.text = it.correctAnswer.toString()
            tvSalah.text = it.wrongAnswer.toString()
            tvSkor.text = it.score.toString()
        } ?: run {
            tvBenar.text = "0"
            tvSalah.text = "0"
            tvSkor.text = "0"
        }

        btnDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    private fun initViews() {
        tvBenar = findViewById(R.id.tvBenar)
        tvSalah = findViewById(R.id.tvSalah)
        tvSkor = findViewById(R.id.tvSkor)
        btnDashboard = findViewById(R.id.btnDashboard)
    }

    private fun getQuizResult(): QuizResult? {
        return intent.getSerializableExtra("QUIZ_RESULT") as? QuizResult
    }
}