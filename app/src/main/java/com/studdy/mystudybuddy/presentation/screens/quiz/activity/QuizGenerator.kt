package com.studdy.mystudybuddy.presentation.screens.quiz.activity

import com.studdy.mystudybuddy.presentation.screens.quiz.model.QuizQuestion
import kotlin.random.Random

object QuizGenerator {

    fun generateFromText(
        materi: String,
        jumlahSoal: Int
    ): List<QuizQuestion> {

        val result =
            mutableListOf<QuizQuestion>()

        val sentences =
            materi.split(
                ".",
                "\n",
                ";",
                ":"
            )
                .map { it.trim() }
                .filter { it.length > 25 }

        if (sentences.isEmpty()) {
            return emptyList()
        }

        val shuffled =
            sentences.shuffled()

        for (sentence in shuffled) {

            if (result.size >= jumlahSoal)
                break

            val words =
                sentence.split(" ")

            if (words.size < 4)
                continue

            val keyword =
                words.take(3)
                    .joinToString(" ")

            val wrong1 =
                "Informasi yang tidak sesuai materi"

            val wrong2 =
                "Penjelasan yang berbeda"

            val wrong3 =
                "Konsep lain yang tidak dibahas"

            val options =
                mutableListOf(
                    sentence,
                    wrong1,
                    wrong2,
                    wrong3
                )

            options.shuffle()

            val correctIndex =
                options.indexOf(sentence)

            result.add(

                QuizQuestion(

                    question =
                        "Apa yang dijelaskan pada materi tentang \"$keyword\"?",

                    options =
                        options,

                    correctAnswer =
                        correctIndex
                )
            )
        }

        while (
            result.size < jumlahSoal &&
            result.isNotEmpty()
        ) {

            val original =
                result.random()

            result.add(

                QuizQuestion(

                    question =
                        original.question +
                                " (${result.size + 1})",

                    options =
                        original.options,

                    correctAnswer =
                        original.correctAnswer
                )
            )
        }

        return result
    }
}