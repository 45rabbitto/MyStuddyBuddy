package com.studdy.mystudybuddy.presentation.screens.ringkasan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.studdy.mystudybuddy.presentation.screens.ringkasan.RingkasanViewModel
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity
import com.studdy.mystudybuddy.presentation.screens.chatbot.activity.ChatbotActivity

class RingkasanActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView

    private lateinit var tvJudul: TextView

    private lateinit var rvRingkasan: RecyclerView

    private lateinit var btnChatbot: Button
    private lateinit var btnQuiz: Button

    private lateinit var adapter: RingkasanAdapter

    private val viewModel: RingkasanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_ringkasan
        )

        initViews()

        setupRecyclerView()

        setupListeners()

        observeData()

        viewModel.loadRingkasan()
    }

    private fun initViews() {

        btnBack =
            findViewById(R.id.btnBack)


        rvRingkasan =
            findViewById(R.id.tvRingkasan)

        btnChatbot =
            findViewById(R.id.btnChatbot)

        btnQuiz =
            findViewById(R.id.btnMulaiKuis)
    }

    private fun setupRecyclerView() {

        adapter =
            RingkasanAdapter(
                emptyList()
            )

        rvRingkasan.layoutManager =
            LinearLayoutManager(this)

        rvRingkasan.adapter =
            adapter
    }

    private fun observeData() {

        lifecycleScope.launch {

            viewModel.uiState.collect { state ->

                tvJudul.text =
                    state.judul

                adapter =
                    RingkasanAdapter(
                        state.ringkasanList
                    )

                rvRingkasan.adapter =
                    adapter
            }
        }
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {

            finish()
        }

        btnChatbot.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ChatbotActivity::class.java
                )
            )
        }

        btnQuiz.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    QuizActivity::class.java
                )
            )
        }
    }
}