package com.studdy.mystudybuddy.presentation.screens.history.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.history.adapter.FileHistoryAdapter
import com.studdy.mystudybuddy.presentation.screens.history.model.FileHistoryModel
import com.studdy.mystudybuddy.presentation.screens.ringkasan.RingkasanActivity

class FileHistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_history_file
        )

        initViews()
        setupRecyclerView()
        setupListener()
    }

    private fun initViews() {

        rvHistory =
            findViewById(R.id.rvHistory)

        btnBack =
            findViewById(R.id.btnBack)
    }

    private fun setupListener() {

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {

        val historyList = listOf(

            FileHistoryModel(
                fileName = "Biologi Sel.pdf",
                date = "20 Mei 2026"
            ),

            FileHistoryModel(
                fileName = "Matematika Diskrit.pdf",
                date = "18 Mei 2026"
            ),

            FileHistoryModel(
                fileName = "Algoritma Dasar.pdf",
                date = "15 Mei 2026"
            )

        )

        val adapter =
            FileHistoryAdapter(
                historyList
            ){ file ->

                startActivity(
                    Intent(
                        this,
                        FileHistoryDetailActivity::class.java
                    ).apply {

                        putExtra(
                            "FILE_NAME",
                            file.fileName
                        )
                    }
                )
            }

        rvHistory.layoutManager =
            LinearLayoutManager(this)

        rvHistory.adapter =
            adapter
    }
}