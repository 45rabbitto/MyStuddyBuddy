package com.studdy.mystudybuddy.presentation.history.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.studdy.mystudybuddy.databinding.ActivityHistoryQuizBinding
import com.studdy.mystudybuddy.presentation.history.adapter.QuizHistoryAdapter
import com.studdy.mystudybuddy.presentation.history.model.QuizHistoryModel

class QuizHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryQuizBinding
    private lateinit var adapter: QuizHistoryAdapter
    private val quizList = mutableListOf<QuizHistoryModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        // Dummy data
        quizList.add(
            QuizHistoryModel(
                "Machine Learning",
                10,
                80,
                "12 Mei 2026"
            )
        )

        quizList.add(
            QuizHistoryModel(
                "Deep Learning",
                15,
                90,
                "10 Mei 2026"
            )
        )

        setupRecycler()
    }

    private fun setupRecycler() {

        adapter = QuizHistoryAdapter(
            quizList,
            onDeleteClick = { item ->
                quizList.remove(item)
                adapter.notifyDataSetChanged()
            },
            onRepeatClick = { item ->
                Toast.makeText(
                    this,
                    "Ulang quiz: ${item.title}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.recyclerHistory.layoutManager =
            LinearLayoutManager(this)

        binding.recyclerHistory.adapter =
            adapter
    }
}