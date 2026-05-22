package com.studdy.mystudybuddy.presentation.screens.chatbot.activity

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R

class ChatbotActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private var tvFileName: TextView? = null

    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var tvChatResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        initViews()
        setupData()
        setupListeners()
    }

    private fun initViews() {

        btnBack = findViewById(R.id.btnBack)
        tvFileName = findViewById(R.id.tvFileName)

        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)
        tvChatResult = findViewById(R.id.tvChatResult)
    }

    private fun setupData() {

        val fileUriString = intent.getStringExtra("FILE_URI")

        if (fileUriString == null) {
            Toast.makeText(this, "File tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val uri = Uri.parse(fileUriString)
        val fileName = uri.lastPathSegment ?: "Dokumen PDF"

        tvFileName?.text = fileName
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

            val response = when {

                message.contains("apa", true) ->
                    "Ini adalah penjelasan dari dokumen yang kamu upload."

                message.contains("ringkasan", true) ->
                    "Dokumen ini berisi materi pembelajaran penting."

                else ->
                    "Saya masih mempelajari dokumen ini, coba pertanyaan lain."
            }

            tvChatResult.text = response
            etMessage.setText("")
        }
    }
}
