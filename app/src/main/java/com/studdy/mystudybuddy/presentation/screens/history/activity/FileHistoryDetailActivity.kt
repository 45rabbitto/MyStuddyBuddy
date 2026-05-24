package com.studdy.mystudybuddy.presentation.screens.history.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.HasilKuisActivity
import com.studdy.mystudybuddy.presentation.screens.ringkasan.RingkasanActivity

class FileHistoryDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvFileName: TextView
    private lateinit var tvRingkasan: TextView
    private lateinit var tvSkor: TextView
    private lateinit var btnBukaRingkasan: Button
    private lateinit var btnBukaQuiz: Button

    private var fileName = ""
    private var fileUri = ""

    // skor global
    private var latestScore = 0

    // Firebase
    private val auth = FirebaseAuth.getInstance()

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

        tvRingkasan =
            findViewById(R.id.tvRingkasan)

        tvSkor =
            findViewById(R.id.tvSkor)

        btnBukaRingkasan =
            findViewById(R.id.btnBukaRingkasan)

        btnBukaQuiz =
            findViewById(R.id.btnBukaQuiz)
    }

    private fun getData() {

        fileName =
            intent.getStringExtra(
                "FILE_NAME"
            ) ?: "Dokumen"

        fileUri =
            intent.getStringExtra(
                "FILE_URI"
            ) ?: ""

        tvFileName.text = fileName
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        // tombol ringkasan
        btnBukaRingkasan.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RingkasanActivity::class.java
                ).apply {

                    putExtra(
                        "FILE_NAME",
                        fileName
                    )

                    putExtra(
                        "FILE_URI",
                        fileUri
                    )
                }
            )
        }

        // tombol hasil quiz
        btnBukaQuiz.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    HasilKuisActivity::class.java
                ).apply {

                    putExtra(
                        "FILE_NAME",
                        fileName
                    )

                    putExtra(
                        "FILE_URI",
                        fileUri
                    )

                    putExtra(
                        "SCORE",
                        latestScore
                    )
                }
            )
        }
    }

    private fun loadDetailHistory() {

        val uid =
            auth.currentUser?.uid ?: return

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

                            tvSkor.text =
                                "Skor : Belum ada"

                            tvRingkasan.text =
                                "Belum ada ringkasan"

                            return
                        }

                        latestScore = 0

                        for (data in snapshot.children) {

                            latestScore =
                                data.child("score")
                                    .getValue(
                                        Int::class.java
                                    ) ?: 0
                        }

                        tvSkor.text =
                            "Skor : $latestScore"

                        tvRingkasan.text =
                            "Klik tombol untuk melihat ringkasan materi"
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        tvSkor.text =
                            "Gagal memuat skor"

                        tvRingkasan.text =
                            "Gagal memuat data"
                    }
                }
            )
    }
}