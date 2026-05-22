package com.studdy.mystudybuddy.presentation.screens.home.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.home.DashboardItem
import com.studdy.mystudybuddy.presentation.screens.home.adapter.DashboardAdapter
import com.studdy.mystudybuddy.presentation.screens.history.activity.FileHistoryActivity
import com.studdy.mystudybuddy.presentation.screens.progress.activity.ProgresActivity
import com.studdy.mystudybuddy.presentation.screens.profile.activity.ProfileActivity
import com.studdy.mystudybuddy.presentation.screens.upload.activity.UploadActivity
import com.studdy.mystudybuddy.presentation.screens.recommendation.activity.AlurActivity
import com.studdy.mystudybuddy.utils.StreakManager

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dashboardAdapter: DashboardAdapter

    private lateinit var tvStreak: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        recyclerView = findViewById(R.id.rvDashboard)
        tvStreak = findViewById(R.id.tvStreak)

        setupStreak()
        setupDashboard()
    }

    // 🔥 STREAK SYSTEM
    private fun setupStreak() {
        val streakManager = StreakManager(this)
        val streak = streakManager.updateStreak()

        tvStreak.text = " $streak "

        Toast.makeText(
            this,
            " $streak ",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setupDashboard() {

        val menuList = listOf(
            DashboardItem("History", R.drawable.history),
            DashboardItem("Alur", R.drawable.alur),
            DashboardItem("Progress", R.drawable.progres),
            DashboardItem("Upload", R.drawable.upload),
            DashboardItem("Profile", R.drawable.profil)
        )

        dashboardAdapter = DashboardAdapter(menuList) { item ->

            when (item.title) {

                "History" -> {
                    startActivity(
                        Intent(this, FileHistoryActivity::class.java)
                    )
                }

                "Alur" -> {
                    startActivity(
                        Intent(this, AlurActivity::class.java)
                    )
                }

                "Progress" -> {
                    startActivity(
                        Intent(this, ProgresActivity::class.java)
                    )
                }

                "Upload" -> {
                    Toast.makeText(this, "Membuka Upload", Toast.LENGTH_SHORT).show()

                    startActivity(
                        Intent(this, UploadActivity::class.java)
                    )
                }

                "Profile" -> {

                    try {
                        startActivity(
                            Intent(this, ProfileActivity::class.java)
                        )
                    } catch (e: Exception) {
                        Toast.makeText(
                            this,
                            "Profile belum tersedia",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = dashboardAdapter
    }
}