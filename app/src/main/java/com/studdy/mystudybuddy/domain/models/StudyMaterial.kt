package com.studdy.mystudybuddy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StudyMaterial(
    val id: Long,
    val title: String,
    val content: String,
    val summary: String?,
    val keyPoints: List<String>,
    val difficulty: DifficultyLevel,
    val estimatedTimeMinutes: Int
) : Parcelable

enum class DifficultyLevel {
    BEGINNER, INTERMEDIATE, ADVANCED
}