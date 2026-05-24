package com.studdy.mystudybuddy.presentation.screens.progress.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.progress.adapter.ProgressAdapter
import com.studdy.mystudybuddy.presentation.screens.progress.model.ProgressModel

class ProgresActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var recycler: RecyclerView
    private lateinit var tvTotalProgress: TextView

    // Firebase
    private val auth =
        FirebaseAuth.getInstance()

    private val database =
        FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progres)

        initViews()
        setupRecycler()
        setupBackButton()
        loadProgress()
    }

    private fun initViews() {

        btnBack = findViewById(R.id.btnBack)

        recycler =
            findViewById(R.id.progressContainer)

        tvTotalProgress =
            findViewById(R.id.tvTotalProgress)
    }

    private fun setupBackButton() {

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecycler() {

        recycler.layoutManager =
            LinearLayoutManager(this)
    }

    private fun loadProgress() {

        val uid =
            auth.currentUser?.uid ?: return

        database.child("Uploads")
            .child(uid)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        val progressList =
                            mutableListOf<ProgressModel>()

                        var totalProgress = 0
                        var totalMateri = 0

                        for (data in snapshot.children) {

                            val fileName =
                                data.child("fileName")
                                    .getValue(String::class.java)
                                    ?: "Materi"

                            // Ambil progress quiz
                            database.child("QuizHistory")
                                .child(uid)
                                .orderByChild("fileName")
                                .equalTo(fileName)
                                .addListenerForSingleValueEvent(
                                    object : ValueEventListener {

                                        override fun onDataChange(
                                            quizSnapshot: DataSnapshot
                                        ) {

                                            var score = 0

                                            for (quiz in quizSnapshot.children) {

                                                score =
                                                    quiz.child("score")
                                                        .getValue(Int::class.java)
                                                        ?: 0
                                            }

                                            // Rumus progress:
                                            // quiz 70% + baca materi 30%

                                            val progress =
                                                ((score * 70) / 100) + 30

                                            totalProgress += progress
                                            totalMateri++

                                            progressList.add(
                                                ProgressModel(
                                                    title = fileName,
                                                    progress = progress
                                                )
                                            )

                                            recycler.adapter =
                                                ProgressAdapter(
                                                    progressList
                                                )

                                            val average =
                                                if (totalMateri == 0) {
                                                    0
                                                } else {
                                                    totalProgress / totalMateri
                                                }

                                            tvTotalProgress.text =
                                                "Total Progress: $average%"
                                        }

                                        override fun onCancelled(
                                            error: DatabaseError
                                        ) {

                                            tvTotalProgress.text =
                                                "Gagal memuat progress"
                                        }
                                    }
                                )
                        }
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        tvTotalProgress.text =
                            "Gagal memuat data"
                    }
                }
            )
    }
}