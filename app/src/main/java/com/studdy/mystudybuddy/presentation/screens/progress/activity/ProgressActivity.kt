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

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val progressList = mutableListOf<ProgressModel>()

    private var isGuest = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progres)

        val session = getSharedPreferences(
            "user_session",
            MODE_PRIVATE
        )

        isGuest =
            session.getBoolean(
                "isGuest",
                false
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
            ProgressAdapter(progressList)
    }

    private fun setupBackButton() {

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadProgress() {

        if (isGuest || auth.currentUser == null) {

            progressList.clear()

            recycler.adapter?.notifyDataSetChanged()

            tvTotalProgress.text =
                "Total Progress : 0%"

            return
        }

        val uid =
            auth.currentUser!!.uid

        progressList.clear()

        database.child("UploadedMaterials")
            .child(uid)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        if (!snapshot.exists()) {

                            tvTotalProgress.text =
                                "Total Progress : 0%"

                            return
                        }

                        val totalFile =
                            snapshot.childrenCount.toInt()

                        var totalProgress = 0
                        var processed = 0

                        for (data in snapshot.children) {

                            val fileName =
                                data.child("fileName")
                                    .getValue(String::class.java)
                                    ?: "Materi"

                            database.child("ReadingProgress")
                                .child(uid)
                                .orderByChild("fileName")
                                .equalTo(fileName)
                                .addListenerForSingleValueEvent(
                                    object : ValueEventListener {

                                        override fun onDataChange(
                                            readSnapshot: DataSnapshot
                                        ) {

                                            var progress = 0


                                            var readCompleted = false

                                            for (read in readSnapshot.children) {

                                                readCompleted =
                                                    read.child("completed")
                                                        .getValue(Boolean::class.java)
                                                        ?: false
                                            }

                                            if (readCompleted) {
                                                progress += 70
                                            }

                                            database.child("QuizHistory")
                                                .child(uid)
                                                .orderByChild("fileName")
                                                .equalTo(fileName)
                                                .addListenerForSingleValueEvent(
                                                    object : ValueEventListener {

                                                        override fun onDataChange(
                                                            quizSnapshot: DataSnapshot
                                                        ) {

                                                            var bestScore = 0

                                                            for (quiz in quizSnapshot.children) {

                                                                val score =
                                                                    quiz.child("score")
                                                                        .getValue(Int::class.java)
                                                                        ?: 0

                                                                if (score > bestScore) {
                                                                    bestScore = score
                                                                }
                                                            }

                                                            if (bestScore >= 70) {
                                                                progress += 30
                                                            }

                                                            progressList.add(
                                                                ProgressModel(
                                                                    title = fileName,
                                                                    progress = progress
                                                                )
                                                            )

                                                            totalProgress += progress
                                                            processed++

                                                            if (processed == totalFile) {

                                                                recycler.adapter?.notifyDataSetChanged()

                                                                val average =
                                                                    totalProgress / totalFile

                                                                tvTotalProgress.text =
                                                                    "Total Progress : $average%"
                                                            }
                                                        }

                                                        override fun onCancelled(
                                                            error: DatabaseError
                                                        ) {
                                                        }
                                                    }
                                                )
                                        }

                                        override fun onCancelled(
                                            error: DatabaseError
                                        ) {
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