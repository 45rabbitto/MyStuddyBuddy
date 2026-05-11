package com.studdy.mystudybuddy.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "documents")
data class Document(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileName: String,
    val filePath: String,
    val fileType: String, // pdf, txt, docx
    val content: String,
    val summary: String? = null,
    val keyPoints: List<String>? = null,
    val createdAt: Date = Date(),
    val lastOpened: Date? = null
)