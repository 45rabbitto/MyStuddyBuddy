package com.studdy.mystudybuddy.presentation.screens.video

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.studdy.mystudybuddy.R

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var tvTitle: TextView
    private lateinit var tvChannel: TextView
    private lateinit var btnBack: ImageView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        val videoId = intent.getStringExtra("VIDEO_ID") ?: ""
        val videoTitle = intent.getStringExtra("VIDEO_TITLE") ?: "Video"
        val videoChannel = intent.getStringExtra("VIDEO_CHANNEL") ?: ""

        initViews(videoTitle, videoChannel)
        setupWebView(videoId)
    }

    private fun initViews(title: String, channel: String) {
        webView = findViewById(R.id.webView)
        tvTitle = findViewById(R.id.tvVideoTitle)
        tvChannel = findViewById(R.id.tvVideoChannel)
        btnBack = findViewById(R.id.btnBack)

        tvTitle.text = title
        tvChannel.text = channel
        btnBack.setOnClickListener { finish() }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(videoId: String) {
        webView.apply {
            settings.javaScriptEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.domStorageEnabled = true

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    // Biarkan YouTube menangani navigasi sendiri
                    return false
                }
            }

            webChromeClient = WebChromeClient()
        }

        // 🔥 HTML untuk embed YouTube
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
                <style>
                    body { margin:0; padding:0; background:#000; }
                    iframe { 
                        width:100%; 
                        height:100%; 
                        border:none;
                        position:absolute;
                        top:0;
                        left:0;
                    }
                    .container {
                        position:relative;
                        width:100%;
                        height:0;
                        padding-bottom:56.25%;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <iframe 
                        src="https://www.youtube.com/embed/$videoId?playsinline=1&rel=0&modestbranding=1&autoplay=0&controls=1&enablejsapi=1"
                        frameborder="0"
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                        allowfullscreen>
                    </iframe>
                </div>
            </body>
            </html>
        """.trimIndent()

        // 🔥 PENTING: baseUrl HARUS https://www.youtube.com
        webView.loadDataWithBaseURL(
            "https://www.youtube.com",
            html,
            "text/html",
            "UTF-8",
            null
        )
    }
}