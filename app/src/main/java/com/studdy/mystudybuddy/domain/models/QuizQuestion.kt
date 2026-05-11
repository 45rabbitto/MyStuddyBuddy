package com.studdy.mystudybuddy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val topic: String
) : Parcelable

data class QuizSession(
    val questions: List<QuizQuestion>,
    val currentIndex: Int = 0,
    val answers: MutableMap<String, Int> = mutableMapOf(),
    val startTime: Long = System.currentTimeMillis()
)

data class QuizResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val score: Int,
    val timeSpentSeconds: Long,
    val answers: List<QuestionResult>
)

data class QuestionResult(
    val question: QuizQuestion,
    val selectedAnswer: Int,
    val isCorrect: Boolean
)