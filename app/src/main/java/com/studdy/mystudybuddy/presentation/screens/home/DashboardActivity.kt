package com.studdy.mystuddybuddy.presentation.screens.home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.history.activity.FileHistoryActivity
import com.studdy.mystudybuddy.presentation.screens.progress.activity.ProgresActivity
import com.studdy.mystudybuddy.presentation.screens.profile.ProfileActivity
import com.studdy.mystudybuddy.presentation.screens.upload.activity.UploadActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var history: ImageView
    private lateinit var alur: ImageView
    private lateinit var progres: ImageView
    private lateinit var upload: ImageView
    private lateinit var profile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        history = findViewById(R.id.history)
        alur = findViewById(R.id.alur)
        progres = findViewById(R.id.progres)
        upload = findViewById(R.id.upload)
        profile = findViewById(R.id.profile)


    history.setOnClickListener {
        Toast.makeText(this,"History diklik",Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, FileHistoryActivity::class.java))
    }

    progres.setOnClickListener {
        Toast.makeText(this,"Progres diklik",Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, ProgresActivity::class.java))
    }

    upload.setOnClickListener {
        Toast.makeText(this,"Upload diklik",Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, UploadActivity::class.java))
    }

    profile.setOnClickListener {
        Toast.makeText(this,"Profile diklik",Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    alur.setOnClickListener {
        Toast.makeText(this,"Alur diklik",Toast.LENGTH_SHORT).show()
    }
}
}