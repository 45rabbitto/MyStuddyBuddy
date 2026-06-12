package com.studdy.mystudybuddy.presentation.screens.history.model

data class FileHistoryModel(
    val fileName: String,
    val date: String,
    val documentId: String = ""
)