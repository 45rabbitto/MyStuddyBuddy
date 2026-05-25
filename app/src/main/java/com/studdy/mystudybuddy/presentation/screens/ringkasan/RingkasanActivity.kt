package com.studdy.mystudybuddy.presentation.screens.ringkasan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity

class RingkasanActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvRingkasan: TextView
    private lateinit var btnGenerate: Button
    private lateinit var btnFinishRingkasan: Button

    // Firebase
    private lateinit var auth: FirebaseAuth

    private var fileUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ringkasan)

        // Firebase
        auth = FirebaseAuth.getInstance()

        initViews()
        setupClickListeners()
        setupData()
    }

    private fun initViews() {

        btnBack = findViewById(R.id.btnBack)
        tvRingkasan = findViewById(R.id.tvRingkasan)
        btnGenerate = findViewById(R.id.btnGenerate)

        // tombol selesai
        btnFinishRingkasan =
            findViewById(R.id.btnFinishRingkasan)
    }

    private fun setupClickListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        // =========================
        // TOMBOL SELESAI
        // =========================

        btnFinishRingkasan.setOnClickListener {

            saveProgressMateri()

            Toast.makeText(
                this,
                "Materi selesai dipelajari",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnGenerate.setOnClickListener {

            val text = tvRingkasan.text.toString()

            if (
                text.isEmpty() ||
                text.contains("Belum")
            ) {

                Toast.makeText(
                    this,
                    "Ringkasan belum tersedia",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Simpan ringkasan ke Firebase
            saveSummaryToFirebase(text)

            // Pindah ke Quiz
            val intent =
                Intent(
                    this,
                    QuizActivity::class.java
                ).apply {

                    putExtra(
                        "RINGKASAN",
                        text
                    )

                    putExtra(
                        "FILE_URI",
                        fileUri
                    )
                }

            startActivity(intent)
        }
    }

    private fun setupData() {

        fileUri =
            intent.getStringExtra("FILE_URI")

        if (fileUri != null) {

            val fileName =
                Uri.parse(fileUri)
                    .lastPathSegment ?: "File PDF"

            tvRingkasan.text =
                """
                File berhasil diterima:
                
                Nama file: $fileName
                
                🔹 Ringkasan:
                (sementara ini dummy, nanti bisa AI / parsing PDF)
                
                - Materi 1
                - Materi 2
                - Materi 3
                """.trimIndent()

        } else {

            tvRingkasan.text =
                "Tidak ada file yang dikirim dari UploadActivity"
        }
    }

    // ===================================
    // SIMPAN RINGKASAN KE FIREBASE
    // ===================================

    private fun saveSummaryToFirebase(
        summaryText: String
    ) {

        val userId =
            auth.currentUser?.uid

        if (userId == null) {

            Toast.makeText(
                this,
                "User belum login",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val database =
            FirebaseDatabase
                .getInstance()
                .getReference("Summaries")
                .child(userId)

        val summaryId =
            database.push().key

        if (summaryId == null) {

            Toast.makeText(
                this,
                "Gagal membuat ID",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val summaryMap =
            HashMap<String, Any>()

        summaryMap["summaryText"] =
            summaryText

        summaryMap["createdAt"] =
            System.currentTimeMillis()

        database.child(summaryId)
            .setValue(summaryMap)

            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Ringkasan berhasil disimpan",
                    Toast.LENGTH_SHORT
                ).show()
            }

            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Gagal menyimpan ringkasan",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // ===================================
    // SIMPAN PROGRESS MATERI
    // ===================================

    private fun saveProgressMateri() {

        val userId =
            auth.currentUser?.uid
                ?: return

        val fileName =
            Uri.parse(fileUri)
                .lastPathSegment ?: "Materi"

        val progressMap =
            HashMap<String, Any>()

        progressMap["fileName"] =
            fileName

        progressMap["materi_selesai"] =
            true

        progressMap["progress"] =
            30

        progressMap["updatedAt"] =
            System.currentTimeMillis()

        FirebaseDatabase
            .getInstance()
            .getReference("Progress")
            .child(userId)
            .child(fileName)
            .setValue(progressMap)
    }
}