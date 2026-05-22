package com.studdy.mystudybuddy.presentation.screens.recommendation.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity
import com.studdy.mystudybuddy.presentation.screens.upload.activity.UploadActivity

class AlurActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var imgLogo: ImageView
    private lateinit var tvRekomendasi: TextView
    private lateinit var btnKuis: Button
    private lateinit var tvNextStep: TextView
    private lateinit var tvFile: TextView

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
        btnKuis = findViewById(R.id.btnBukaQuiz) // ❗ PASTIKAN ID XML ADA
        tvNextStep = findViewById(R.id.tvMulaiLatihan)
        tvFile = findViewById(R.id.tvFile)
    }

    private fun setupListeners() {

        btnBack.setOnClickListener { finish() }

        imgLogo.setOnClickListener {
            Toast.makeText(this, "Rekomendasi AI aktif", Toast.LENGTH_SHORT).show()
        }

        btnKuis.setOnClickListener {
            val fileName = intent.getStringExtra("FILE_NAME")

            startActivity(
                Intent(this, QuizActivity::class.java).apply {
                    putExtra("FILE_NAME", fileName)
                }
            )
        }

        tvNextStep.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
    }

    private fun loadRekomendasi() {

        val fileName = intent.getStringExtra("FILE_NAME")
        tvFile.text = fileName ?: "Belum ada file"

        val prefs = getSharedPreferences("progress_data", MODE_PRIVATE)
        val avg = prefs.getInt("avg_score", 0)

        tvRekomendasi.text = when {
            avg < 60 -> "Pelajari ulang konsep dasar"
            avg < 80 -> "Latihan soal tingkat menengah"
            else -> "Lanjut studi kasus lanjutan"
        }
    }
}