package com.studdy.mystudybuddy.presentation.screens.progress.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.progress.model.ProgressModel

class ProgressAdapter(
    private val list: List<ProgressModel>
) : RecyclerView.Adapter<ProgressAdapter.VH>() {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val progressText: TextView = itemView.findViewById(R.id.tvProgressText)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_progress, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val item = list[position]

        holder.title.text = item.title
        holder.progressBar.progress = item.progress
        holder.progressText.text = "${item.progress}%"

        holder.status.text = if (item.progress >= 100) "Completed" else "In Progress"
    }

    override fun getItemCount() = list.size
}