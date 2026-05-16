package com.studdy.mystudybuddy.presentation.history.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.studdy.mystudybuddy.databinding.ActivityHistorySummaryBinding
import com.studdy.mystudybuddy.presentation.history.adapter.SummaryHistoryAdapter
import com.studdy.mystudybuddy.presentation.history.model.SummaryHistoryModel

class SummaryHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistorySummaryBinding
    private lateinit var adapter: SummaryHistoryAdapter
    private val summaryList = mutableListOf<SummaryHistoryModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistorySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        summaryList.add(
            SummaryHistoryModel(
                "Ringkasan Bab 1 AI",
                "12 Mei 2026"
            )
        )

        summaryList.add(
            SummaryHistoryModel(
                "Ringkasan NLP",
                "11 Mei 2026"
            )
        )

        setupRecycler()
    }

    private fun setupRecycler() {

        adapter = SummaryHistoryAdapter(summaryList) { item ->
            summaryList.remove(item)
            adapter.notifyDataSetChanged()
        }

        binding.recyclerHistory.layoutManager =
            LinearLayoutManager(this)

        binding.recyclerHistory.adapter =
            adapter
    }
}