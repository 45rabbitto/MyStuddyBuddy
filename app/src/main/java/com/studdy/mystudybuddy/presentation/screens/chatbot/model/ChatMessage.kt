package com.studdy.mystudybuddy.presentation.screens.chatbot.model

data class ChatMessage(
    val message: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)