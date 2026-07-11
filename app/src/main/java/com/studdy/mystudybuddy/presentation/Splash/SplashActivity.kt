package com.studdy.mystudybuddy.presentation.Splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.studdy.mystudybuddy.presentation.screens.auth.activity.WelcomeActivity
import com.studdy.mystudybuddy.presentation.screens.home.activity.DashboardActivity

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen()
        }

        //  CEK SESSION GUEST
        val session = getSharedPreferences("user_session", MODE_PRIVATE)
        val isGuest = session.getBoolean("isGuest", false)

        Handler(Looper.getMainLooper()).postDelayed({

            //  JIKA USER SUDAH LOGIN ATAU GUEST → LANGSUNG KE DASHBOARD
            if (FirebaseAuth.getInstance().currentUser != null || isGuest) {
                startActivity(Intent(this, DashboardActivity::class.java))
            } else {
                //  BELUM LOGIN DAN BUKAN GUEST → KE WELCOME
                startActivity(Intent(this, WelcomeActivity::class.java))
            }

            finish()

        }, 2000)
    }
}