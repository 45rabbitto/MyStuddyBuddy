package com.studdy.mystudybuddy.presentation.history.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.studdy.mystudybuddy.databinding.ActivityHistoryFileBinding
import com.studdy.mystudybuddy.presentation.history.adapter.FileHistoryAdapter
import com.studdy.mystudybuddy.presentation.history.model.FileHistoryModel

class FileHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryFileBinding
    private lateinit var adapter: FileHistoryAdapter
    private val fileList = mutableListOf<FileHistoryModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        // dummy data
        fileList.add(FileHistoryModel("Materi AI.pdf", "12 Mei 2026"))
        fileList.add(FileHistoryModel("Catatan NLP.docx", "10 Mei 2026"))

        setupRecycler()
    }

    private fun setupRecycler() {

        adapter = FileHistoryAdapter(fileList) { item ->

            fileList.remove(item)
            adapter.notifyDataSetChanged()
        }

        binding.recyclerHistory.layoutManager =
            LinearLayoutManager(this)

        binding.recyclerHistory.adapter = adapter

    }
}