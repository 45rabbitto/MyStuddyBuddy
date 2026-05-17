package com.studdy.mystudybuddy.presentation.screens.profile.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.study.mystuddybuddy.R
import com.study.mystuddybuddy.presentation.main.MainActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var menuEditProfile: LinearLayout
    private lateinit var menuLogout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        btnBack = findViewById(R.id.btnBack)
        menuEditProfile = findViewById(R.id.menuEditProfile)
        menuLogout = findViewById(R.id.menuLogout)

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