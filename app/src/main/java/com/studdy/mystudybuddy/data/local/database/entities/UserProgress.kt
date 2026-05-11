package com.studdy.mystudybuddy.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Date,
    val studyTimeMinutes: Int,
    val documentsRead: Int,
    val quizzesTaken: Int,
    val averageScore: Float,
    val streakDays: Int,
    val xpEarned: Int
)