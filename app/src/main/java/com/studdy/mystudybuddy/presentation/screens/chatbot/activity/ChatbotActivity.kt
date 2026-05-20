package com.studdy.mystudybuddy.presentation.screens.chatbot.activity

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R

class ChatbotActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnSend: ImageButton
    private lateinit var etMessage: EditText
    private lateinit var chatContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chatbot)

        initViews()
        setupListeners()
    }

    private fun initViews() {

        btnBack =
            findViewById(R.id.btnBack)

        btnSend =
            findViewById(R.id.btnSend)

        etMessage =
            findViewById(R.id.etMessage)

        chatContainer =
            findViewById(R.id.chatContainer)
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        btnSend.setOnClickListener {

            val message =
                etMessage.text.toString().trim()

            if(message.isNotEmpty()){

                addUserMessage(message)

                val botReply =
                    getBotResponse(message)

                addBotMessage(botReply)

                etMessage.text.clear()
            }
        }
    }

    private fun addUserMessage(message: String){

        val tv = TextView(this)

        tv.text = "Anda: $message"

        tv.textSize = 16f

        tv.setPadding(
            20,
            20,
            20,
            20
        )

        chatContainer.addView(tv)
    }

    private fun addBotMessage(message: String){

        val tv = TextView(this)

        tv.text = "StudyBuddy AI: $message"

        tv.textSize = 16f

        tv.setPadding(
            20,
            20,
            20,
            20
        )

        chatContainer.addView(tv)
    }

    private fun getBotResponse(
        message: String
    ): String {

        return when {

            message.contains(
                "algoritma",
                true
            ) ->
                "Algoritma adalah langkah sistematis untuk menyelesaikan masalah."

            message.contains(
                "java",
                true
            ) ->
                "Java adalah bahasa pemrograman berbasis OOP."

            message.contains(
                "python",
                true
            ) ->
                "Python sering digunakan untuk AI dan machine learning."

            else ->
                "Saya siap membantu proses belajar Anda."
        }
    }
}