package com.studdy.mystudybuddy.presentation.screens.history.activity

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

class FileHistoryDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvFileName: TextView
    private lateinit var btnBukaRingkasan: Button
    private lateinit var btnBukaQuiz: Button
    private lateinit var tvInfo: TextView

    private var fileName = ""

    // Firebase
    private val auth =
        FirebaseAuth.getInstance()

    private val database =
        FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_file_history
        )

        initViews()
        getData()
        setupListeners()
        loadDetailHistory()
    }

    private fun initViews() {

        btnBack =
            findViewById(R.id.btnBack)

        tvFileName =
            findViewById(R.id.tvFileName)

        btnBukaRingkasan =
            findViewById(R.id.btnBukaRingkasan)

        btnBukaQuiz =
            findViewById(R.id.btnBukaQuiz)

        tvInfo =
            findViewById(R.id.tvInfo)
    }

    private fun getData() {

        fileName =
            intent.getStringExtra(
                "FILE_NAME"
            ) ?: "Dokumen"

        tvFileName.text =
            fileName
    }

    private fun setupListeners() {

        // tombol kembali
        btnBack.setOnClickListener {
            finish()
        }

        // buka history ringkasan
        btnBukaRingkasan.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SummaryHistoryActivity::class.java
                ).apply {

                    putExtra(
                        "FILE_NAME",
                        fileName
                    )
                }
            )
        }

        // buka history quiz
        btnBukaQuiz.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    QuizHistoryActivity::class.java
                ).apply {

                    putExtra(
                        "FILE_NAME",
                        fileName
                    )
                }
            )
        }
    }

    private fun loadDetailHistory() {

        val uid =
            auth.currentUser?.uid

        if (uid == null) {

            Toast.makeText(
                this,
                "User belum login",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        database.child("QuizHistory")
            .child(uid)
            .orderByChild("fileName")
            .equalTo(fileName)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        if (!snapshot.exists()) {

                            tvInfo.text =
                                "Belum ada history quiz"

                            return
                        }

                        var latestScore = 0
                        var latestDate = "-"

                        for (data in snapshot.children) {

                            latestScore =
                                data.child("score")
                                    .getValue(Int::class.java)
                                    ?: 0

                            latestDate =
                                data.child("date")
                                    .getValue(String::class.java)
                                    ?: "-"
                        }

                        tvInfo.text =
                            """
                            Nama File:
                            $fileName
                            
                            Skor Terakhir:
                            $latestScore
                            
                            Tanggal:
                            $latestDate
                            """.trimIndent()
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        tvInfo.text =
                            "Gagal memuat detail"
                    }
                }
            )
    }
}