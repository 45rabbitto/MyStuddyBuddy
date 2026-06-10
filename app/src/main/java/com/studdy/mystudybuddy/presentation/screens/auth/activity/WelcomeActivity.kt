package com.studdy.mystudybuddy.presentation.screens.auth.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.home.activity.DashboardActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var btnLogin: Button
    private lateinit var btnMulai: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        btnLogin = findViewById(R.id.btnLogin)
        btnMulai = findViewById(R.id.btnMulai)
    }


    private fun setupClickListeners() {
        btnLogin.setOnClickListener {

            // user bukan guest
            val prefs = getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

            prefs.edit()
                .putBoolean("isGuest", false)
                .apply()

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )
        }

        btnMulai.setOnClickListener {

            val prefs = getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

            prefs.edit()
                .putBoolean("isGuest", true)
                .apply()

            startActivity(
                Intent(
                    this,
                    DashboardActivity::class.java
                )
            )
        }
    }
}