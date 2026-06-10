package com.studdy.mystudybuddy.data.model

import java.util.Date

data class SummaryModel(
    val id: String = "",
    val documentId: String = "",
    val fileName: String = "",
    val summary: String = "",
    val summaryLength: Int = 0,
    val originalLength: Int = 0,
    val createdAt: Date = Date(),
    val userId: String = ""
)