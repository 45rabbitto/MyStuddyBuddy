package com.studdy.mystudybuddy.presentation.screens.profile.activity

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class ProfileActivity : AppCompatActivity() {

    private lateinit var appPref: SharedPreferences
    private lateinit var userPref: SharedPreferences

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var imgProfile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        appPref = getSharedPreferences("app_settings", MODE_PRIVATE)
        userPref = getSharedPreferences("USER_PREF", MODE_PRIVATE)

        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        imgProfile = findViewById(R.id.imgProfile)

        val menuEdit = findViewById<LinearLayout>(R.id.menuEditProfile)
        val menuTheme = findViewById<LinearLayout>(R.id.menuTheme)
        val menuLanguage = findViewById<LinearLayout>(R.id.menuLanguage)
        val menuNotif = findViewById<LinearLayout>(R.id.menuNotif)
        val menuLogout = findViewById<LinearLayout>(R.id.menuLogout)

        menuEdit.setOnClickListener { openEditProfile() }
        menuTheme.setOnClickListener { showThemeDialog() }
        menuLanguage.setOnClickListener { showLanguageDialog() }
        menuNotif.setOnClickListener { toggleNotification() }
        menuLogout.setOnClickListener { logout() }

        loadProfile()
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun loadProfile() {
        val name = userPref.getString("name", "Risma Student")
        val email = userPref.getString("email", "risma@email.com")
        val photoUri = userPref.getString("photo", null)

        tvName.text = name
        tvEmail.text = email

        if (photoUri != null) {
            imgProfile.setImageURI(Uri.parse(photoUri))
        }
    }

    private fun openEditProfile() {
        startActivity(Intent(this, EditProfileActivity::class.java))
    }

    private fun showThemeDialog() {
        val options = arrayOf("Light Mode", "Dark Mode")

        AlertDialog.Builder(this)
            .setTitle("Pilih Tema")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        appPref.edit().putString("theme", "light").apply()
                    }
                    1 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        appPref.edit().putString("theme", "dark").apply()
                    }
                }
            }.show()
    }

    private fun showLanguageDialog() {
        val options = arrayOf("Indonesia", "English")

        AlertDialog.Builder(this)
            .setTitle("Pilih Bahasa")
            .setItems(options) { _, which ->
                val lang = if (which == 0) "id" else "en"
                appPref.edit().putString("language", lang).apply()

                AlertDialog.Builder(this)
                    .setMessage("Restart aplikasi untuk menerapkan bahasa")
                    .setPositiveButton("OK", null)
                    .show()
            }.show()
    }

    private fun toggleNotification() {
        val isOn = appPref.getBoolean("notif", true)
        appPref.edit().putBoolean("notif", !isOn).apply()

        val status = if (!isOn) "Notifikasi Aktif 🔔" else "Notifikasi Dimatikan 🔕"

        AlertDialog.Builder(this)
            .setMessage(status)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun logout() {
        AlertDialog.Builder(this)
            .setTitle("Keluar")
            .setMessage("Yakin ingin logout?")
            .setPositiveButton("Ya") { _, _ ->
                val loginPref = getSharedPreferences("login_session", MODE_PRIVATE)
                loginPref.edit().clear().apply()

                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}