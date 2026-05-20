package com.studdy.mystudybuddy.presentation.screens.quiz.model

import java.io.Serializable

data class QuizResult(

    val totalQuestion: Int,

    val correctAnswer: Int,

    val wrongAnswer: Int,

    val score: Int,

    val explanations: List<String>

) : Serializable