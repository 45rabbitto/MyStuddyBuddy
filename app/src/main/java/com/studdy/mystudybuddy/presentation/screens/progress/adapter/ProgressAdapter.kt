package com.studdy.mystudybuddy.presentation.progress.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.progress.model.ProgressModel

class ProgressAdapter(
    private val progressList: List<ProgressModel>
) : RecyclerView.Adapter<ProgressAdapter.ViewHolder>() {

    class ViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val tvTitle: TextView =
            itemView.findViewById(R.id.tvTitle)

        val tvStatus: TextView =
            itemView.findViewById(R.id.tvStatus)

        val tvPercent: TextView =
            itemView.findViewById(R.id.tvPercent)

        val progressBar: ProgressBar =
            itemView.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_progress_row,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = progressList[position]

        holder.tvTitle.text =
            item.title

        holder.tvStatus.text =
            item.status

        holder.tvPercent.text =
            "${item.progress}%"

        holder.progressBar.progress =
            item.progress
    }

    override fun getItemCount(): Int {
        return progressList.size
    }
}