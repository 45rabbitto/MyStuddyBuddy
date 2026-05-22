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

class FileHistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_file)

        initViews()
        setupListener()
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    private fun initViews() {
        rvHistory = findViewById(R.id.rvHistory)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupListener() {
        btnBack.setOnClickListener { finish() }
    }

    private fun loadHistory() {

        val prefs = getSharedPreferences("history_data", MODE_PRIVATE)

        val rawSet = prefs.getStringSet("files", emptySet()) ?: emptySet()

        val historyList = rawSet.mapNotNull { item ->
            val parts = item.split("|")
            if (parts.size >= 2) {
                FileHistoryModel(
                    fileName = parts[0],
                    date = parts[1]
                )
            } else null
        }.toMutableList()

        val adapter = FileHistoryAdapter(
            historyList,
            onItemClick = { file ->
                startActivity(
                    Intent(this, FileHistoryDetailActivity::class.java).apply {
                        putExtra("FILE_NAME", file.fileName)
                    }
                )
            },
            onDelete = { file ->

                val current = prefs.getStringSet("files", mutableSetOf())?.toMutableSet()
                    ?: mutableSetOf()

                current.remove("${file.fileName}|${file.date}")

                prefs.edit()
                    .putStringSet("files", current)
                    .apply()

                loadHistory() // refresh tanpa recreate
            }
        )

        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = adapter
    }
}