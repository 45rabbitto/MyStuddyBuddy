package com.studdy.mystudybuddy.presentation.screens.upload.model

class ChatMessageModel {
    import java.util.Date

    data class ChatMessageModel(
        val id: String = "",
        val documentId: String = "",
        val question: String = "",
        val answer: String = "",
        val timestamp: Date = Date(),
        val isFromUser: Boolean = true
    )
}