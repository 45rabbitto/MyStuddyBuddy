package com.studdy.mystudybuddy.presentation.screens.recommendation.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.home.activity.DashboardActivity
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity
import com.studdy.mystudybuddy.presentation.screens.recommendation.adaptor.AlurAdapter
import com.studdy.mystudybuddy.presentation.screens.ringkasan.RingkasanActivity
import com.studdy.mystudybuddy.presentation.screens.upload.activity.UploadActivity

class AlurActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var imgLogo: ImageView
    private lateinit var tvRekomendasi: TextView
    private lateinit var tvNextStep: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alur)

        initViews()
        setupListeners()
        loadRekomendasi()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        imgLogo = findViewById(R.id.imgLogo)
        tvRekomendasi = findViewById(R.id.tvRekomendasi)
        tvNextStep = findViewById(R.id.tvMulaiLatihan)
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        imgLogo.setOnClickListener {
            Toast.makeText(this, "Rekomendasi belajar AI aktif", Toast.LENGTH_SHORT).show()
        }

        // lanjut belajar
        tvNextStep.setOnClickListener {

            startActivity(
                Intent(this, UploadActivity::class.java)
            )
        }
    }

    private fun loadRekomendasi() {

        val prefs = getSharedPreferences("progress_data", MODE_PRIVATE)

        val avg = prefs.getInt("avg_score", 0)
        val last = prefs.getInt("last_score", 0)

        val text = when {
            last < 60 -> "Ulangi materi terakhir"
            avg < 70 -> "Latihan dasar"
            avg < 85 -> "Materi menengah"
            else -> "Materi lanjutan & project"
        }

        findViewById<TextView>(R.id.tvRekomendasi).text = text
    }
}