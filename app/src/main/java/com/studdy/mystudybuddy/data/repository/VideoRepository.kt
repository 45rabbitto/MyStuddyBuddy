package com.studdy.mystudybuddy.data.repository

import android.content.Context
import android.util.Log
import com.studdy.mystudybuddy.data.model.VideoModel
import com.studdy.mystudybuddy.network.YouTubeApiService
import com.studdy.mystudybuddy.utils.KeywordExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VideoRepository(private val context: Context) {

    private val youtubeApi: YouTubeApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YouTubeApiService::class.java)
    }

    private val apiKey: String by lazy {
        loadApiKeyFromAssets()
    }

    private fun loadApiKeyFromAssets(): String {
        return try {
            val inputStream = context.assets.open("youtube_api_key.txt")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val key = String(buffer, Charsets.UTF_8).trim()

            if (key.isEmpty() || key == "your_api_key_here") {
                Log.e("VideoRepo", "YouTube API Key kosong atau default")
                "MISSING_KEY"
            } else {
                Log.d("VideoRepo", "API Key loaded, length: ${key.length}")
                key
            }
        } catch (e: Exception) {
            Log.e("VideoRepo", "Error loading API Key: ${e.message}")
            "MISSING_KEY"
        }
    }

    suspend fun searchVideos(text: String): List<VideoModel> {
        return withContext(Dispatchers.IO) {
            try {
                if (apiKey == "MISSING_KEY") {
                    Log.e("VideoRepo", "YouTube API Key tidak ditemukan")
                    return@withContext emptyList()
                }

                val keywords = KeywordExtractor.extractKeywords(text)
                val searchQuery = keywords.take(3).joinToString(" ")

                if (searchQuery.isEmpty()) {
                    return@withContext emptyList()
                }

                Log.d("VideoRepo", "Searching: $searchQuery")

                val response = youtubeApi.searchVideos(
                    query = searchQuery,
                    maxResults = 10,
                    apiKey = apiKey
                )

                response.items?.mapNotNull { item ->
                    val videoId = item.id?.videoId
                    val snippet = item.snippet
                    if (videoId != null && snippet != null) {
                        VideoModel(
                            videoId = videoId,
                            title = snippet.title ?: "Untitled",
                            description = snippet.description ?: "",
                            thumbnailUrl = snippet.thumbnails?.medium?.url
                                ?: snippet.thumbnails?.high?.url
                                ?: "",
                            channelName = snippet.channelTitle ?: "Unknown"
                        )
                    } else null
                } ?: emptyList()

            } catch (e: Exception) {
                Log.e("VideoRepo", "Error: ${e.message}")
                emptyList()
            }
        }
    }
}