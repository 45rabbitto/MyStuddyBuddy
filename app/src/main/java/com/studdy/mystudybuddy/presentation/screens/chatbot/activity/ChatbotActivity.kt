package com.studdy.mystudybuddy.presentation.screens.chatbot.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.chatbot.adapter.ChatAdapter
import com.studdy.mystudybuddy.presentation.chatbot.model.ChatMessage
import com.studdy.mystudybuddy.utils.ChatbotApiService
import kotlinx.coroutines.launch

class ChatbotActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvFileName: TextView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var rvChat: RecyclerView

    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val apiService = ChatbotApiService()

    private var currentSummaryId: String? = null
    private var currentSummaryText: String = ""
    private var currentFileName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initViews()
        setupRecyclerView()
        loadSummaryFromFirestore()
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

    /**
     * Ambil ringkasan dari Firestore (bukan file PDF asli)
     * Ringkasan dikirim dari RingkasanActivity saat upload PDF
     */
    private fun loadSummaryFromFirestore() {
        // Ambil summaryId dari intent (dikirim dari RingkasanActivity)
        currentSummaryId = intent.getStringExtra("SUMMARY_ID")
        val fileName = intent.getStringExtra("FILE_NAME")

        if (currentSummaryId == null) {
            Toast.makeText(this, "Tidak ada ringkasan yang dipilih", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvFileName.text = fileName ?: "Materi"

        // Ambil ringkasan dari Firestore
        val userId = auth.currentUser?.uid ?: return

        lifecycleScope.launch {
            try {
                val doc = firestore.collection("summaries")
                    .document(currentSummaryId!!)
                    .get()
                    .await()

                if (doc.exists()) {
                    currentSummaryText = doc.getString("summary") ?: ""

                    if (currentSummaryText.isEmpty()) {
                        tvFileName.text = "⚠️ Ringkasan kosong"
                        Toast.makeText(this@ChatbotActivity,
                            "Ringkasan tidak ditemukan", Toast.LENGTH_SHORT).show()
                    } else {
                        // Tambahkan pesan selamat datang
                        val welcomeMessage = ChatMessage(
                            message = "Halo! Saya AI Tutor. Saya sudah membaca ringkasan materi \"${fileName}\". Ada yang ingin kamu tanyakan?",
                            isUser = false
                        )
                        messages.add(welcomeMessage)
                        chatAdapter.notifyItemInserted(messages.size - 1)
                    }
                } else {
                    Toast.makeText(this@ChatbotActivity,
                        "Ringkasan tidak ditemukan di database", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ChatbotActivity,
                    "Gagal memuat ringkasan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnSend.setOnClickListener {
            val message = etMessage.text.toString().trim()
            if (message.isEmpty()) {
                Toast.makeText(this, "Tulis pertanyaan dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendMessageToAI(message)
        }
    }

    private fun sendMessageToAI(question: String) {
        // Tambahkan pesan user ke chat
        val userMessage = ChatMessage(
            message = question,
            isUser = true
        )
        messages.add(userMessage)
        chatAdapter.notifyItemInserted(messages.size - 1)
        rvChat.scrollToPosition(messages.size - 1)

        // Clear input
        etMessage.setText("")

        // Nonaktifkan send button selama loading
        btnSend.isEnabled = false

        // Tambahkan loading indicator
        val loadingMessage = ChatMessage(
            message = "✍️ Mengetik...",
            isUser = false
        )
        messages.add(loadingMessage)
        chatAdapter.notifyItemInserted(messages.size - 1)
        rvChat.scrollToPosition(messages.size - 1)

        // Panggil API dengan konteks ringkasan
        lifecycleScope.launch {
            try {
                val answer = apiService.chatWithSummary(
                    question = question,
                    summaryContext = currentSummaryText
                )

                // Hapus loading message
                messages.removeAt(messages.size - 1)
                chatAdapter.notifyItemRemoved(messages.size)

                // Tambahkan jawaban AI
                val aiMessage = ChatMessage(
                    message = answer,
                    isUser = false
                )
                messages.add(aiMessage)
                chatAdapter.notifyItemInserted(messages.size - 1)
                rvChat.scrollToPosition(messages.size - 1)

            } catch (e: Exception) {
                // Hapus loading message
                messages.removeAt(messages.size - 1)
                chatAdapter.notifyItemRemoved(messages.size)

                // Tambahkan pesan error
                val errorMessage = ChatMessage(
                    message = "Maaf, terjadi kesalahan: ${e.message}\n\nCoba lagi nanti.",
                    isUser = false
                )
                messages.add(errorMessage)
                chatAdapter.notifyItemInserted(messages.size - 1)
                rvChat.scrollToPosition(messages.size - 1)
            } finally {
                btnSend.isEnabled = true
            }
        }
    }
}