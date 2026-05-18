package com.studdy.mystudybuddy.presentation.screens.profile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.main.MainActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var menuEditProfile: LinearLayout
    private lateinit var menuLogout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        menuEditProfile = findViewById(R.id.menuEditProfile)
        menuLogout = findViewById(R.id.menuLogout)
    }

    private fun setupClickListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        menuEditProfile.setOnClickListener {
            // buka EditProfileActivity nanti
        }

        menuLogout.setOnClickListener {

            val intent = Intent(
                this,
                MainActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }
}