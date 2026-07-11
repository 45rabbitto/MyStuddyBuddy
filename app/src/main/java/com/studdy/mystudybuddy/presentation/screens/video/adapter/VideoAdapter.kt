package com.studdy.mystudybuddy.presentation.screens.video.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.data.model.VideoModel

class VideoAdapter(
    private val videos: List<VideoModel>,
    private val onItemClick: (VideoModel) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
        holder.itemView.setOnClickListener { onItemClick(videos[position]) }
    }

    override fun getItemCount(): Int = videos.size

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivThumbnail: ImageView = itemView.findViewById(R.id.ivThumbnail)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvChannel: TextView = itemView.findViewById(R.id.tvChannel)

        fun bind(video: VideoModel) {
            tvTitle.text = video.title
            tvChannel.text = video.channelName

            Glide.with(itemView.context)
                .load(video.thumbnailUrl)
                .placeholder(R.drawable.ic_video_placeholder)
                .into(ivThumbnail)
        }
    }
}