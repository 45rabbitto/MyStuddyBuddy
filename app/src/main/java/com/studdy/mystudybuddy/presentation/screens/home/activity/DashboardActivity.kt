package com.studdy.mystudybuddy.presentation.screens.home.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.history.activity.FileHistoryActivity
import com.studdy.mystudybuddy.presentation.screens.profile.activity.ProfileActivity
import com.studdy.mystudybuddy.presentation.screens.progress.activity.ProgresActivity
import com.studdy.mystudybuddy.presentation.screens.recommendation.activity.AlurActivity
import com.studdy.mystudybuddy.presentation.screens.upload.activity.UploadActivity
import com.studdy.mystudybuddy.utils.StreakManager

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvStreak: TextView

    private lateinit var history: ImageView
    private lateinit var alur: ImageView
    private lateinit var progres: ImageView
    private lateinit var upload: ImageView
    private lateinit var profile: ImageView
    private lateinit var home: ImageView

    // cek guest
    private var isGuest = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_dashboard
        )

        // Session guest
        val session =
            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

        isGuest =
            session.getBoolean(
                "isGuest",
                false
            )

        // STREAK
        tvStreak =
            findViewById(R.id.tvStreak)

        // MENU
        history =
            findViewById(R.id.history)

        alur =
            findViewById(R.id.alur)

        progres =
            findViewById(R.id.progres)

        upload =
            findViewById(R.id.upload)

        profile =
            findViewById(R.id.profile)

        home =
            findViewById(R.id.home)

        setupStreak()
        setupMenu()
    }

    private fun setupStreak() {

        if (isGuest) {

            tvStreak.text = "0"
            return
        }

        val streakManager =
            StreakManager(this)

        val streak =
            streakManager.updateStreak()

        tvStreak.text =
            streak.toString()

        Toast.makeText(
            this,
            "Streak kamu: $streak ",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setupMenu() {

        history.setOnClickListener {

            if (isGuest) {
                showLoginMessage()
                return@setOnClickListener
            }

            startActivity(
                Intent(
                    this,
                    FileHistoryActivity::class.java
                )
            )
        }

        // ALUR
        alur.setOnClickListener {

            if (isGuest) {
                showLoginMessage()
                return@setOnClickListener
            }

            startActivity(
                Intent(
                    this,
                    AlurActivity::class.java
                )
            )
        }

        progres.setOnClickListener {

            if (isGuest) {
                showLoginMessage()
                return@setOnClickListener
            }

            startActivity(
                Intent(
                    this,
                    ProgresActivity::class.java
                )
            )
        }

        upload.setOnClickListener {


            startActivity(
                Intent(
                    this,
                    UploadActivity::class.java
                )
            )
        }

        profile.setOnClickListener {

            if (isGuest) {
                showLoginMessage()
                return@setOnClickListener
            }

            try {

                startActivity(
                    Intent(
                        this,
                        ProfileActivity::class.java
                    )
                )

            } catch (e: Exception) {

                Toast.makeText(
                    this,
                    "Profile belum tersedia",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        home.setOnClickListener {

            Toast.makeText(
                this,
                "Kamu sudah di Home",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showLoginMessage() {

        Toast.makeText(
            this,
            "Silakan login untuk menggunakan fitur ini",
            Toast.LENGTH_SHORT
        ).show()
    }
}