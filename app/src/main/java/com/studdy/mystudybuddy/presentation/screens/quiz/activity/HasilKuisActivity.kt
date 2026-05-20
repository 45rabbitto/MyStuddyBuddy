package com.studdy.mystudybuddy.presentation.screens.quiz.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.quiz.model.QuizResult
import com.studdy.mystuddybuddy.presentation.screens.home.DashboardActivity

class HasilKuisActivity : AppCompatActivity() {

    private lateinit var tvBenar: TextView
    private lateinit var tvSalah: TextView
    private lateinit var tvSkor: TextView

    private lateinit var pembahasanContainer: LinearLayout

    private lateinit var btnDashboard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_hasil_kuis
        )

        initViews()

        val result = getQuizResult()

        result?.let {

            tvBenar.text =
                it.correctAnswer.toString()

            tvSalah.text =
                it.wrongAnswer.toString()

            tvSkor.text =
                it.score.toString()

            tampilkanPembahasan(
                it.explanations
            )
        }

        btnDashboard.setOnClickListener {

            val intent = Intent(
                this,
                DashboardActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP

            startActivity(intent)

            finish()
        }
    }

    private fun initViews() {

        tvBenar =
            findViewById(R.id.tvBenar)

        tvSalah =
            findViewById(R.id.tvSalah)

        tvSkor =
            findViewById(R.id.tvSkor)

        pembahasanContainer =
            findViewById(
                R.id.pembahasanContainer
            )

        btnDashboard =
            findViewById(
                R.id.btnDashboard
            )
    }

    private fun getQuizResult(): QuizResult? {

        return if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            intent.getSerializableExtra(
                "QUIZ_RESULT",
                QuizResult::class.java
            )

        } else {

            @Suppress("DEPRECATION")
            intent.getSerializableExtra(
                "QUIZ_RESULT"
            ) as? QuizResult
        }
    }

    private fun tampilkanPembahasan(
        pembahasanList: List<String>
    ) {

        pembahasanContainer.removeAllViews()

        for (item in pembahasanList) {

            val textView = TextView(this)

            textView.text = item

            textView.textSize = 15f

            textView.setPadding(
                20,
                20,
                20,
                20
            )

            textView.background =
                getDrawable(
                    R.drawable.kontainer
                )

            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            params.setMargins(
                0,
                10,
                0,
                10
            )

            textView.layoutParams =
                params

            pembahasanContainer.addView(
                textView
            )
        }
    }
}