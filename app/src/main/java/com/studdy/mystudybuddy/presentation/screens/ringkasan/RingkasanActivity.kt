package com.studdy.mystudybuddy.presentation.screens.ringkasan

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity


class RingkasanActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvRingkasan: TextView
    private lateinit var btnGenerate: Button

    private var fileUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringkasan)

        initViews()
        setupClickListeners()
        setupData()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvRingkasan = findViewById(R.id.tvRingkasan)
        btnGenerate = findViewById(R.id.btnGenerate)
    }

    private fun setupClickListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        btnGenerate.setOnClickListener {

            val text = tvRingkasan.text.toString()

            if (text.isEmpty() || text.contains("Belum")) {
                Toast.makeText(this, "Ringkasan belum tersedia", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra("RINGKASAN", text)
                putExtra("FILE_URI", fileUri)
            }

            startActivity(intent)
        }
    }

    private fun setupData() {

        fileUri = intent.getStringExtra("FILE_URI")

        if (fileUri != null) {

            val fileName = Uri.parse(fileUri).lastPathSegment ?: "File PDF"

            tvRingkasan.text = """
                File berhasil diterima:
                
                Nama file: $fileName
                
                🔹 Ringkasan:
                (sementara ini dummy, nanti bisa AI / parsing PDF)
                
                - Materi 1
                - Materi 2
                - Materi 3
            """.trimIndent()

        } else {
            tvRingkasan.text = "Tidak ada file yang dikirim dari UploadActivity"
        }
    }
}