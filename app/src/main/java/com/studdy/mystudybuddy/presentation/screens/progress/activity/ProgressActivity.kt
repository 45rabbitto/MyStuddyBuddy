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

    private val auth =
        FirebaseAuth.getInstance()

    private val database =
        FirebaseDatabase.getInstance().reference

    private val progressList =
        mutableListOf<ProgressModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_progres
        )

        initViews()
        setupRecycler()
        setupBackButton()
        loadProgress()
    }

    private fun initViews() {

        btnBack =
            findViewById(R.id.btnBack)

        recycler =
            findViewById(R.id.progressContainer)

        tvTotalProgress =
            findViewById(R.id.tvTotalProgress)
    }

    private fun setupRecycler() {

        recycler.layoutManager =
            LinearLayoutManager(this)

        recycler.adapter =
            ProgressAdapter(
                progressList
            )
    }

    private fun setupBackButton() {

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadProgress() {

        val uid =
            auth.currentUser?.uid
                ?: return

        progressList.clear()

        database.child("UploadedMaterials")
            .child(uid)
            .addListenerForSingleValueEvent(

                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        var totalProgress = 0
                        var totalMateri = 0

                        if (!snapshot.exists()) {

                            tvTotalProgress.text =
                                "Total Progress : 0%"

                            return
                        }

                        val totalFile =
                            snapshot.childrenCount.toInt()

                        var processedFile = 0

                        for (data in snapshot.children) {

                            val fileName =

                                data.child(
                                    "fileName"
                                )
                                    .getValue(
                                        String::class.java
                                    )
                                    ?: "Materi"

                            database.child(
                                "QuizHistory"
                            )

                                .child(uid)

                                .orderByChild(
                                    "fileName"
                                )

                                .equalTo(
                                    fileName
                                )

                                .addListenerForSingleValueEvent(

                                    object :
                                        ValueEventListener {

                                        override fun onDataChange(
                                            quizSnapshot: DataSnapshot
                                        ) {

                                            var score = 0

                                            var pernahQuiz =
                                                false

                                            for (quiz in quizSnapshot.children) {

                                                score =
                                                    quiz.child(
                                                        "score"
                                                    )

                                                        .getValue(
                                                            Int::class.java
                                                        )
                                                        ?: 0

                                                pernahQuiz =
                                                    true
                                            }

                                            /*
                                            Progress:
                                            Ringkasan dibaca = 30
                                            Quiz = 70
                                            */

                                            val progress =

                                                if (
                                                    pernahQuiz
                                                ) {

                                                    30 +
                                                            ((score * 70) / 100)

                                                } else {

                                                    30
                                                }

                                            totalProgress +=
                                                progress

                                            totalMateri++

                                            progressList.add(

                                                ProgressModel(

                                                    title =
                                                        fileName,

                                                    progress =
                                                        progress
                                                )
                                            )

                                            processedFile++

                                            if (
                                                processedFile ==
                                                totalFile
                                            ) {

                                                recycler.adapter?.notifyDataSetChanged()

                                                val average =

                                                    if (
                                                        totalMateri == 0
                                                    ) {
                                                        0
                                                    } else {

                                                        totalProgress /
                                                                totalMateri
                                                    }

                                                tvTotalProgress.text =

                                                    "Total Progress : $average%"
                                            }
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