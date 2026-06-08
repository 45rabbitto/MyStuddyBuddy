package com.studdy.mystudybuddy.presentation.screens.chatbot.activity

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.chatbot.adapter.ChatAdapter
import com.studdy.mystudybuddy.presentation.screens.chatbot.model.ChatMessage
import com.studdy.mystudybuddy.utils.ChatbotApiService
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ChatbotActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvFileName: TextView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var rvChat: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var apiService: ChatbotApiService
    private var currentSummaryText: String = ""
    private var currentFileName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        apiService = ChatbotApiService(this)
        initViews()
        setupRecyclerView()
        loadSummary()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvFileName = findViewById(R.id.tvFileName)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)
        rvChat = findViewById(R.id.rvChat)
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messages)
        rvChat.layoutManager = LinearLayoutManager(this)
        rvChat.adapter = chatAdapter
    }

    private fun loadSummary() {
        val summaryId = intent.getStringExtra("SUMMARY_ID")
        currentFileName = intent.getStringExtra("FILE_NAME") ?: "Materi"

        if (summaryId == null) {
            Toast.makeText(this, "Tidak ada ringkasan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvFileName.text = currentFileName

        lifecycleScope.launch {
            try {
                val doc = FirebaseFirestore.getInstance().collection("summaries").document(summaryId).get().await()
                currentSummaryText = doc.getString("summary") ?: ""

                if (currentSummaryText.isNotEmpty()) {
                    messages.add(ChatMessage("👋 Halo! Saya AI Tutor. Ada yang ingin kamu tanyakan tentang \"$currentFileName\"?", false))
                    chatAdapter.notifyItemInserted(0)
                }
            } catch (e: Exception) {
                Toast.makeText(this@ChatbotActivity, "Gagal memuat: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        btnSend.setOnClickListener {
            val question = etMessage.text.toString().trim()
            if (question.isEmpty()) {
                Toast.makeText(this, "Tulis pertanyaan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendMessage(question)
        }
    }

    private fun sendMessage(question: String) {
        messages.add(ChatMessage(question, true))
        chatAdapter.notifyItemInserted(messages.size - 1)
        rvChat.scrollToPosition(messages.size - 1)
        etMessage.setText("")
        btnSend.isEnabled = false

        messages.add(ChatMessage("✍️ Mengetik...", false))
        chatAdapter.notifyItemInserted(messages.size - 1)
        rvChat.scrollToPosition(messages.size - 1)

        lifecycleScope.launch {
            val answer = apiService.chatWithSummary(question, currentSummaryText)
            messages.removeAt(messages.size - 1)
            chatAdapter.notifyItemRemoved(messages.size)
            messages.add(ChatMessage(answer, false))
            chatAdapter.notifyItemInserted(messages.size - 1)
            rvChat.scrollToPosition(messages.size - 1)
            btnSend.isEnabled = true
        }
    }
}
