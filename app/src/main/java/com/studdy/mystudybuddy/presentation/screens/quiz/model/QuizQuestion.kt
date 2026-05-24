package com.studdy.mystudybuddy.presentation.screens.quiz.model

data class QuizQuestion(

    val question: String = "",

    val options: List<String> = listOf(),

    val correctAnswer: Int = -1
)