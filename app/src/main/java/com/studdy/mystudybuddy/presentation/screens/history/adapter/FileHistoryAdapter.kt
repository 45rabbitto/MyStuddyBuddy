package com.studdy.mystudybuddy.presentation.screens.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.databinding.ItemHistoryFileBinding
import com.studdy.mystudybuddy.presentation.screens.history.model.FileHistoryModel

class FileHistoryAdapter(
    private val list: List<FileHistoryModel>,
    private val onItemClick: (FileHistoryModel) -> Unit
) : RecyclerView.Adapter<FileHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemHistoryFileBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = ItemHistoryFileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.binding.tvFileName.text =
            item.fileName

        holder.binding.tvDate.text =
            item.date

        // klik seluruh item
        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}