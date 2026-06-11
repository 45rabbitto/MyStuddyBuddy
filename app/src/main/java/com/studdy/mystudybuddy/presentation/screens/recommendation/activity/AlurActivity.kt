package com.studdy.mystudybuddy.presentation.screens.recommendation.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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

    private val auth =
        FirebaseAuth.getInstance()

    private val database =
        FirebaseDatabase.getInstance().reference

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

        tvRekomendasi =
            findViewById(R.id.tvRekomendasi)

        btnKuis =
            findViewById(R.id.btnBukaQuiz)

        tvNextStep =
            findViewById(R.id.tvMulaiLatihan)

        tvFile =
            findViewById(R.id.tvFile)
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        imgLogo.setOnClickListener {

            Toast.makeText(
                this,
                "Rekomendasi AI aktif",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnKuis.setOnClickListener {

            val fileName =
                intent.getStringExtra("FILE_NAME")

            startActivity(
                Intent(
                    this,
                    QuizActivity::class.java
                ).apply {

                    putExtra(
                        "FILE_NAME",
                        fileName
                    )
                }
            )
        }

        tvNextStep.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    UploadActivity::class.java
                )
            )
        }
    }

    private fun loadRekomendasi() {

        val fileName =
            intent.getStringExtra("FILE_NAME")

        tvFile.text =
            fileName ?: "Belum ada file"

        val uid =
            auth.currentUser?.uid ?: return

        database.child("QuizHistory")
            .child(uid)
            .limitToLast(1)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        var score = 0

                        for (data in snapshot.children) {

                            score =
                                data.child("score")
                                    .getValue(Int::class.java)
                                    ?: 0
                        }

                        val rekomendasi =
                            when {

                                score < 60 -> {
                                    """
                                    📚 Pelajari ulang konsep dasar.

                                    Fokus pada:
                                    • Membaca ulang materi
                                    • Memahami istilah penting
                                    • Menonton video pembelajaran dasar
                                    """.trimIndent()
                                }

                                score < 80 -> {
                                    """
                                    ✏️ Kemampuan sudah cukup baik.

                                    Rekomendasi:
                                    • Latihan soal tingkat menengah
                                    • Mulai membuat rangkuman sendiri
                                    • Pelajari studi kasus sederhana
                                    """.trimIndent()
                                }

                                else -> {
                                    """
                                    🚀 Kemampuan sangat baik!

                                    Lanjutkan ke:
                                    • Studi kasus lanjutan
                                    • Project mini
                                    • Latihan soal HOTS
                                    """.trimIndent()
                                }
                            }

                        tvRekomendasi.text =
                            rekomendasi
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        tvRekomendasi.text =
                            "Gagal memuat rekomendasi"
                    }
                }
            )
    }
}