package com.studdy.mystuddybuddy.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.databinding.ActivityDashboardBinding
import com.studdy.mystudybuddy.presentation.history.activity.FileHistoryActivity
import com.studdy.mystudybuddy.presentation.screens.profile.activity.ProfileActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClick()
    }

    private fun setupClick() {

        // MENU HISTORY
        binding.history.setOnClickListener {
            startActivity(Intent(this, FileHistoryActivity::class.java))
        }

        // MENU ALUR BELAJAR
        binding.alur.setOnClickListener {
            // nanti bisa ke ProgressActivity
        }

        // MENU PROGRES BELAJAR
        binding.progres.setOnClickListener {
            // nanti bisa ke Quiz/ProgressActivity
        }
    }
}