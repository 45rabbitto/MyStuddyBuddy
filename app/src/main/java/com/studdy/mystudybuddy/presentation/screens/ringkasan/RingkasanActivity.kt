package com.studdy.mystudybuddy.presentation.screens.ringkasan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.studdy.mystudybuddy.utils.PdfBoxHelper

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
            showFeedbackDialog()
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

            val extractedText =
                PdfBoxHelper.extractTextFromPdf(
                    this,
                    Uri.parse(fileUri)
                )

            tvRingkasan.text =
                """
                Nama File:
                $fileName
                
                Isi PDF:
                ${extractedText.take(3000)}
                """.trimIndent()

        } else {

            tvRingkasan.text =
                "Tidak ada file yang dikirim dari UploadActivity"
        }
    }
    private fun showFeedbackDialog() {

        val view =
            layoutInflater.inflate(
                R.layout.avtivity_feedback,
                null
            )

        val ivLike =
            view.findViewById<ImageView>(
                R.id.ivLike
            )

        val ivDislike =
            view.findViewById<ImageView>(
                R.id.ivDislike
            )

        val dialog =
            AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create()

        dialog.show()

        dialog.window?.setBackgroundDrawableResource(
            android.R.color.transparent
        )

        ivLike.setOnClickListener {

            saveFeedback("LIKE")

            saveProgressMateri()

            Toast.makeText(
                this,
                "Terima kasih atas feedback Anda 😊",
                Toast.LENGTH_SHORT
            ).show()

            dialog.dismiss()
        }

        ivDislike.setOnClickListener {

            saveFeedback("DISLIKE")

            saveProgressMateri()

            Toast.makeText(
                this,
                "Feedback tersimpan",
                Toast.LENGTH_SHORT
            ).show()

            dialog.dismiss()
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

    private fun saveFeedback(
        feedback: String
    ) {

        val userId =
            auth.currentUser?.uid
                ?: return

        val fileName =
            intent.getStringExtra("FILE_NAME")
                ?: Uri.parse(fileUri)
                    .lastPathSegment
                ?: "Materi"

        val database =
            FirebaseDatabase
                .getInstance()
                .getReference("Feedback")
                .child(userId)

        val feedbackId =
            database.push().key
                ?: return

        val feedbackMap =
            HashMap<String, Any>()

        feedbackMap["feedback"] =
            feedback

        feedbackMap["fileName"] =
            fileName

        feedbackMap["createdAt"] =
            System.currentTimeMillis()

        database.child(feedbackId)
            .setValue(feedbackMap)
    }

    // ===================================
    // SIMPAN PROGRESS MATERI
    // ===================================

    private fun saveProgressMateri() {

        val userId =
            auth.currentUser?.uid
                ?: return

        val fileName =
            intent.getStringExtra("FILE_NAME")
                ?: Uri.parse(fileUri)
                    .lastPathSegment
                ?: "Materi"

        val database =
            FirebaseDatabase
                .getInstance()
                .getReference("ReadingProgress")
                .child(userId)

        // cek apakah file sudah ada
        database.orderByChild("fileName")
            .equalTo(fileName)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        // jika sudah pernah selesai baca
                        if (snapshot.exists()) {

                            Toast.makeText(
                                this@RingkasanActivity,
                                "Materi sudah selesai dipelajari",
                                Toast.LENGTH_SHORT
                            ).show()

                            return
                        }

                        val id =
                            database.push().key
                                ?: return

                        val progressMap =
                            HashMap<String, Any>()

                        progressMap["fileName"] =
                            fileName

                        progressMap["completed"] =
                            true

                        progressMap["updatedAt"] =
                            System.currentTimeMillis()

                        database.child(id)
                            .setValue(progressMap)
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {
                    }
                }
            )
    }
}