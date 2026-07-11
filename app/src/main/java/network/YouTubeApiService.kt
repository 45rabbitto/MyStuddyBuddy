package com.studdy.mystudybuddy.network

import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {

    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 10,
        @Query("type") type: String = "video",
        @Query("videoDuration") duration: String = "medium",
        @Query("key") apiKey: String
    ): YouTubeSearchResponse
}

data class YouTubeSearchResponse(
    val items: List<YouTubeVideoItem>? = null
)

data class YouTubeVideoItem(
    val id: YouTubeVideoId? = null,
    val snippet: YouTubeVideoSnippet? = null
)

data class YouTubeVideoId(
    val videoId: String? = null
)

data class YouTubeVideoSnippet(
    val title: String? = null,
    val description: String? = null,
    val thumbnails: YouTubeThumbnails? = null,
    val channelTitle: String? = null
)

data class YouTubeThumbnails(
    val medium: YouTubeThumbnail? = null,
    val high: YouTubeThumbnail? = null
)

data class YouTubeThumbnail(
    val url: String? = null
)