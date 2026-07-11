package com.studdy.mystudybuddy.presentation.screens.video

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.data.model.VideoModel
import com.studdy.mystudybuddy.data.repository.VideoRepository
import com.studdy.mystudybuddy.presentation.screens.video.adapter.VideoAdapter
import kotlinx.coroutines.launch

class VideoRecommendationActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var rvVideos: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvTitle: TextView
    private lateinit var tvEmpty: TextView

    // 🔥 PASS CONTEXT KE REPOSITORY
    private val repository by lazy { VideoRepository(this) }
    private lateinit var adapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_recommendation)

        val text = intent.getStringExtra("TEXT") ?: ""

        initViews()
        setupRecyclerView()
        setupListeners()

        if (text.isNotEmpty()) {
            searchVideos(text)
        } else {
            tvEmpty.text = "Tidak ada teks untuk dicari"
            tvEmpty.visibility = android.view.View.VISIBLE
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        rvVideos = findViewById(R.id.rvVideos)
        progressBar = findViewById(R.id.progressBar)
        tvTitle = findViewById(R.id.tvTitle)
        tvEmpty = findViewById(R.id.tvEmpty)

        tvTitle.text = " Video Rekomendasi"
    }

    private fun setupRecyclerView() {
        adapter = VideoAdapter(emptyList()) { video ->
            playVideo(video)
        }
        rvVideos.layoutManager = LinearLayoutManager(this)
        rvVideos.adapter = adapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }
    }

    private fun searchVideos(text: String) {
        progressBar.visibility = android.view.View.VISIBLE
        tvEmpty.visibility = android.view.View.GONE

        lifecycleScope.launch {
            val videos = repository.searchVideos(text)

            progressBar.visibility = android.view.View.GONE

            if (videos.isNotEmpty()) {
                adapter = VideoAdapter(videos) { video ->
                    playVideo(video)
                }
                rvVideos.adapter = adapter
                rvVideos.visibility = android.view.View.VISIBLE
            } else {
                tvEmpty.text = "Tidak ditemukan video terkait"
                tvEmpty.visibility = android.view.View.VISIBLE
            }
        }
    }

    private fun playVideo(video: VideoModel) {
        val intent = Intent(this, VideoPlayerActivity::class.java).apply {
            putExtra("VIDEO_ID", video.videoId)
            putExtra("VIDEO_TITLE", video.title)
            putExtra("VIDEO_CHANNEL", video.channelName)
        }
        startActivity(intent)
    }
}