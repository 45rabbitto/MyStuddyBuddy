package com.studdy.mystudybuddy.presentation.screens.history.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R

class SummaryHistoryActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvFileName: TextView
    private lateinit var tvSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_history_summary
        )

        initViews()
        loadData()
        setupListener()
    }

    private fun initViews() {

        btnBack =
            findViewById(R.id.btnBack)

        tvFileName =
            findViewById(R.id.tvFileName)

        tvSummary =
            findViewById(R.id.tvSummary)
    }

    private fun loadData() {

        val fileName =
            intent.getStringExtra("FILE_NAME")
                ?: "Dokumen"

        tvFileName.text =
            fileName

        // sementara data dummy
        tvSummary.text =
            """
            Ringkasan dari $fileName

            Materi membahas:

            • Pengertian dasar
            • Konsep utama
            • Struktur materi
            • Contoh penerapan
            • Kesimpulan materi

            Ringkasan ini nantinya dapat diganti menggunakan hasil AI dari file upload.
            """.trimIndent()
    }

    private fun setupListener() {

        btnBack.setOnClickListener {
            finish()
        }
    }
}