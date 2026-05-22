package com.studdy.mystudybuddy.presentation.screens.profile.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.profile.activity.EditProfileActivity
import com.studdy.mystudybuddy.presentation.screens.auth.activity.LoginActivity

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

        btnBack = findViewById(R.id.btnBack)
        btnEditProfile = findViewById(R.id.btnEditProfile)
        menuLogout = findViewById(R.id.menuLogout)
    }

    private fun setupListeners() {

        // back ke halaman sebelumnya
        btnBack.setOnClickListener {
            finish()
        }

        // edit profile
        btnEditProfile.setOnClickListener {
            startActivity(
                Intent(this, EditProfileActivity::class.java)
            )
        }

        // logout
        menuLogout.setOnClickListener {

            val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)


            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }
}