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

        val prefs = getSharedPreferences(
            "history_data",
            MODE_PRIVATE
        )

        val rawSet =
            prefs.getStringSet("files", emptySet()) ?: emptySet()

        val historyList = rawSet.mapNotNull { item ->

            val p = item.split("|")

            if (p.size >= 3) {

                FileHistoryModel(
                    p[0], // file name
                    p[1]  // date
                )

            } else {
                null
            }
        }

        val adapter = FileHistoryAdapter(

            historyList.toMutableList(),

            onItemClick = { file ->

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
            },

            onDelete = { file ->

                val current =
                    prefs.getStringSet(
                        "files",
                        mutableSetOf()
                    )?.toMutableSet() ?: mutableSetOf()

                // Cari item lengkap yang cocok
                val target = current.find {

                    val p = it.split("|")

                    p.size >= 2 &&
                            p[0] == file.fileName &&
                            p[1] == file.date
                }

                target?.let {
                    current.remove(it)
                }

                prefs.edit()
                    .putStringSet("files", current)
                    .apply()

                loadHistory()
            }
        )

        rvHistory.layoutManager =
            LinearLayoutManager(this)

        rvHistory.adapter = adapter
    }
}