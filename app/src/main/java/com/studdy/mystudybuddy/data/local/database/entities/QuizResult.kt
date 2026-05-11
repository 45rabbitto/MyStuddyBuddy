package com.studdy.mystudybuddy.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "quiz_results")
data class QuizResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val documentId: Long,
    val topic: String,
    val score: Int, // percentage
    val totalQuestions: Int,
    val correctAnswers: Int,
    val timeSpent: Long, // in seconds
    val takenAt: Date = Date()
)