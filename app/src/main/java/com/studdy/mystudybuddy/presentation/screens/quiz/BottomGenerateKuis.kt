package com.studdy.mystudybuddy.presentation.screens.quiz.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.quiz.activity.QuizActivity

class BottomGenerateKuis : BottomSheetDialogFragment() {

    private var jumlahSoal = 5

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.bottom_generate_kuis,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val btnMinus =
            view.findViewById<Button>(R.id.btnMinus)

        val btnPlus =
            view.findViewById<Button>(R.id.btnPlus)

        val btnStartQuiz =
            view.findViewById<Button>(R.id.btnStartQuiz)

        val tvJumlahSoal =
            view.findViewById<TextView>(R.id.tvJumlahSoal)

        tvJumlahSoal.text =
            jumlahSoal.toString()

        btnPlus.setOnClickListener {

            jumlahSoal++

            tvJumlahSoal.text =
                jumlahSoal.toString()
        }

        btnMinus.setOnClickListener {

            if (jumlahSoal > 1) {

                jumlahSoal--

                tvJumlahSoal.text =
                    jumlahSoal.toString()
            }
        }

        btnStartQuiz.setOnClickListener {

            val intent =
                Intent(
                    requireContext(),
                    QuizActivity::class.java
                )

            intent.putExtra(
                "FILE_NAME",
                arguments?.getString("FILE_NAME")
            )

            intent.putExtra(
                "RINGKASAN",
                arguments?.getString("RINGKASAN")
            )

            intent.putExtra(
                "MATERI_ASLI",
                arguments?.getString(
                    "MATERI_ASLI"
                )
            )

            intent.putExtra(
                "JUMLAH_SOAL",
                jumlahSoal
            )

            startActivity(intent)

            dismiss()
        }
    }
}