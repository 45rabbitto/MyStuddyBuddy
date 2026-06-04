package com.studdy.mystudybuddy.presentation.screens.quiz.model

import java.io.Serializable

data class QuizResult(

    val totalQuestion: Int = 0,

    val correctAnswer: Int = 0,

    val wrongAnswer: Int = 0,

    val score: Int = 0,

    val explanations: ArrayList<String> = arrayListOf()

) : Serializable