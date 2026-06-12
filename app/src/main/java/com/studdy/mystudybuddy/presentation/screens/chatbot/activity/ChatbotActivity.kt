package com.studdy.mystudybuddy.presentation.screens.chatbot.activity

import android.os.Bundle
import android.util.Log
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
    private var currentDocumentText: String = ""
    private var currentFileName: String = ""
    private var currentDocumentId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        apiService = ChatbotApiService(this)
        initViews()
        setupRecyclerView()
        loadDocument()
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

    // 🔥 AMBIL DOKUMEN ASLI DARI FIRESTORE (collection "documents")
    private fun loadDocument() {
        currentDocumentId = intent.getStringExtra("DOCUMENT_ID") ?: ""
        currentFileName = intent.getStringExtra("FILE_NAME") ?: "Materi"

        Log.d("CHATBOT", "DOCUMENT_ID: $currentDocumentId")
        Log.d("CHATBOT", "FILE_NAME: $currentFileName")

        if (currentDocumentId.isEmpty()) {
            Toast.makeText(this, "Tidak ada dokumen", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvFileName.text = currentFileName

        lifecycleScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"

                // 🔥 AMBIL DARI COLLECTION "documents" (BUKAN summaries)
                val doc = FirebaseFirestore.getInstance()
                    .collection("PdfContents")
                    .document(userId)
                    .collection("documents")
                    .document(currentDocumentId)
                    .get()
                    .await()

                currentDocumentText = doc.getString("content") ?: ""
                Log.d("CHATBOT", "Document loaded, length: ${currentDocumentText.length}")

                if (currentDocumentText.isNotEmpty()) {
                    messages.add(ChatMessage(
                        "👋 Halo! Saya AI Tutor. Saya sudah membaca dokumen \"$currentFileName\". Ada yang ingin kamu tanyakan tentang materinya?",
                        false
                    ))
                    chatAdapter.notifyItemInserted(0)
                } else {
                    Toast.makeText(this@ChatbotActivity, "Dokumen kosong", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CHATBOT", "Error: ${e.message}", e)
                Toast.makeText(this@ChatbotActivity, "Gagal memuat dokumen: ${e.message}", Toast.LENGTH_SHORT).show()
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

        messages.add(ChatMessage("✍️ AI sedang mengetik...", false))
        chatAdapter.notifyItemInserted(messages.size - 1)
        rvChat.scrollToPosition(messages.size - 1)

        lifecycleScope.launch {
            val answer = apiService.chatWithSummary(question, currentDocumentText)
            messages.removeAt(messages.size - 1)
            chatAdapter.notifyItemRemoved(messages.size)
            messages.add(ChatMessage(answer, false))
            chatAdapter.notifyItemInserted(messages.size - 1)
            rvChat.scrollToPosition(messages.size - 1)
            btnSend.isEnabled = true
        }
    }
}