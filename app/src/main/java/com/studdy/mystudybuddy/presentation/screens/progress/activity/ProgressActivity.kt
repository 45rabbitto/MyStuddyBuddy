package com.studdy.mystudybuddy.presentation.screens.progress.activity

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R

class ProgresActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView

    private lateinit var tvMateri: TextView
    private lateinit var tvKuis: TextView
    private lateinit var tvRataRata: TextView
    private lateinit var tvSubject: TextView
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progres)

        initViews()
        setupBackButton()
        loadProgress()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)

        tvMateri = findViewById(R.id.tvMateri)
        tvKuis = findViewById(R.id.tvQuiz)
        tvRataRata = findViewById(R.id.tvAverage)
        tvSubject = findViewById(R.id.tvSubject)
        tvStatus = findViewById(R.id.tvStatus)
    }

    private fun setupBackButton() {
        btnBack.setOnClickListener {
            finish() // kembali ke halaman sebelumnya (Dashboard)
        }
    }

    private fun loadProgress() {

        val prefs = getSharedPreferences("progress_data", Context.MODE_PRIVATE)

        val materi = prefs.getInt("materi_count", 0)
        val quiz = prefs.getInt("quiz_count", 0)
        val avg = prefs.getInt("avg_score", 0)
        val lastFile = prefs.getString("last_file", "Belum ada")

        tvMateri.text = materi.toString()
        tvKuis.text = quiz.toString()
        tvRataRata.text = "$avg%"

        tvSubject.text = lastFile

        tvStatus.text = if (materi > 0) "Sudah dipelajari" else "Upload dulu"
    }
}