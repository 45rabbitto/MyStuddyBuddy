package com.studdy.mystudybuddy

import android.app.Application
import com.google.firebase.FirebaseApp
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        PDFBoxResourceLoader.init(this)
    }
}