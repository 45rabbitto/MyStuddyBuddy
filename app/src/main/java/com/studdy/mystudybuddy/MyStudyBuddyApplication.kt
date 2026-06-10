package com.studdy.mystudybuddy

import android.app.Application
import com.google.firebase.FirebaseApp
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this)

        // Inisialisasi PDFBox
        PDFBoxResourceLoader.init(this)
    }
}