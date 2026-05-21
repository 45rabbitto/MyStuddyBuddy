package com.studdy.mystudybuddy.presentation.screens.profile.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.profile.activity.EditProfileActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnEditProfile: LinearLayout
    private lateinit var btnBack: ImageView
    private lateinit var menuLogout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        initViews()
        setupListeners()
    }

    private fun initViews() {

        btnBack =
            findViewById(R.id.btnBack)

        btnEditProfile =
            findViewById(R.id.btnEditProfile)

        menuLogout =
            findViewById(R.id.menuLogout)
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        btnEditProfile.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    EditProfileActivity::class.java
                )
            )
        }

        menuLogout.setOnClickListener {

            finishAffinity()

        }
    }
}