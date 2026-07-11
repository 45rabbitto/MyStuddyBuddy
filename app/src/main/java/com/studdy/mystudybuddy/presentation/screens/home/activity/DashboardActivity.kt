package com.studdy.mystudybuddy.presentation.screens.home.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.auth.activity.LoginActivity
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

    private lateinit var auth: FirebaseAuth
    private var isGuest = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()

        //  CEK SESSION GUEST
        val session = getSharedPreferences("user_session", MODE_PRIVATE)
        isGuest = session.getBoolean("isGuest", false)

        //  JIKA BUKAN GUEST DAN BELUM LOGIN → KEMBALI KE LOGIN
        if (!isGuest && auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        initViews()
        setupStreak()
        setupMenu()
    }

    private fun initViews() {
        tvStreak = findViewById(R.id.tvStreak)

        history = findViewById(R.id.history)
        alur = findViewById(R.id.alur)
        progres = findViewById(R.id.progres)
        upload = findViewById(R.id.upload)
        profile = findViewById(R.id.profile)
        home = findViewById(R.id.home)
    }

    private fun setupStreak() {
        //  GUEST ATAU BELUM LOGIN → STREAK 0
        if (isGuest || auth.currentUser == null) {
            tvStreak.text = "0"
            return
        }

        val streakManager = StreakManager(this)
        val streak = streakManager.updateStreak()
        tvStreak.text = streak.toString()

        Toast.makeText(this, "Streak kamu: $streak", Toast.LENGTH_SHORT).show()
    }

    private fun setupMenu() {
        history.setOnClickListener {
            //  CEK LOGIN
            if (!isUserLoggedIn()) {
                showLoginMessage()
                return@setOnClickListener
            }
            startActivity(Intent(this, FileHistoryActivity::class.java))
        }

        alur.setOnClickListener {
            if (!isUserLoggedIn()) {
                showLoginMessage()
                return@setOnClickListener
            }
            startActivity(Intent(this, AlurActivity::class.java))
        }

        progres.setOnClickListener {
            if (!isUserLoggedIn()) {
                showLoginMessage()
                return@setOnClickListener
            }
            startActivity(Intent(this, ProgresActivity::class.java))
        }

        upload.setOnClickListener {
            //  UPLOAD BOLEH UNTUK GUEST (DENGAN PERINGATAN)
            if (isGuest) {
                Toast.makeText(this, "Mode Guest: Data tidak akan tersimpan permanen", Toast.LENGTH_SHORT).show()
            }
            startActivity(Intent(this, UploadActivity::class.java))
        }

        profile.setOnClickListener {
            if (!isUserLoggedIn()) {
                showLoginMessage()
                return@setOnClickListener
            }
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        home.setOnClickListener {
            Toast.makeText(this, "Kamu sudah di Home", Toast.LENGTH_SHORT).show()
        }
    }

    //  CEK APAKAH USER LOGIN
    private fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null && !isGuest
    }

    private fun showLoginMessage() {
        Toast.makeText(this, "Silakan login untuk menggunakan fitur ini", Toast.LENGTH_SHORT).show()
    }
}