package com.studdy.mystudybuddy.presentation.screens.progress.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.progress.adapter.ProgressAdapter
import com.studdy.mystudybuddy.presentation.screens.progress.model.ProgressModel

class ProgresActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var recycler: RecyclerView

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

        recycler = findViewById(R.id.progressContainer)
    }

    private fun setupBackButton() {

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecycler() {

        recycler.layoutManager = LinearLayoutManager(this)
    }

    private fun loadProgress() {

        val prefs = getSharedPreferences(
            "progress_data",
            MODE_PRIVATE
        )

        // ===== Ambil list materi upload =====
        val uploadedMaterials =
            prefs.getStringSet(
                "uploaded_materials",
                emptySet()
            ) ?: emptySet()

        val progressList = mutableListOf<ProgressModel>()

        uploadedMaterials.forEach { materiName ->

            progressList.add(
                ProgressModel(
                    title = materiName,
                    progress = 100
                )
            )
        }

        val adapter = ProgressAdapter(progressList)

        recycler.adapter = adapter
    }
}