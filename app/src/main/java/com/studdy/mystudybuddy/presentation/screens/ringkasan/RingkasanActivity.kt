package com.studdy.mystudybuddy.presentation.screens.ringkasan

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R

class RingkasanActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvFileName: TextView
    private lateinit var tvRingkasan: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringkasan)

        initViews()
        setupData()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvFileName = findViewById(R.id.tvFileName)
        tvRingkasan = findViewById(R.id.tvRingkasan)
    }

    private fun setupData() {

        val fileUriString = intent.getStringExtra("FILE_URI")

        if (fileUriString == null) {
            Toast.makeText(this, "File tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val uri = Uri.parse(fileUriString)

        val fileName = uri.lastPathSegment ?: "Dokumen PDF"

        tvFileName.text = fileName

        // 🔥 sementara dummy ringkasan
        tvRingkasan.text = """
            Ringkasan Dokumen:

            - Dokumen ini membahas materi pembelajaran.
            - Berisi konsep-konsep penting.
            - Perlu dianalisis lebih lanjut untuk detail lengkap.
        """.trimIndent()
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }
}