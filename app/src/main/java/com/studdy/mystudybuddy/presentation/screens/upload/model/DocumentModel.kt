package com.studdy.mystudybuddy.presentation.screens.upload.model

class DocumentModel {
    import java.util.Date

    data class DocumentModel(
        val id: String = "",
        val fileName: String = "",
        val fileUri: String = "",
        val extractedText: String = "",
        val summary: String = "",
        val summaryLength: Int = 0,
        val uploadedAt: Date = Date(),
        val userId: String = "",
        val isProcessed: Boolean = false
    )
    )
}