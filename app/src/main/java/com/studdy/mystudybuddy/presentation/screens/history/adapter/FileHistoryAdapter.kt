package com.studdy.mystudybuddy.presentation.screens.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.databinding.ItemHistoryFileBinding
import com.studdy.mystudybuddy.presentation.screens.history.model.FileHistoryModel

class FileHistoryAdapter(
    private var list: MutableList<FileHistoryModel>,
    private val onItemClick: (FileHistoryModel) -> Unit,
    private val onDelete: (FileHistoryModel) -> Unit
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

        holder.binding.tvFileName.text = item.fileName
        holder.binding.tvDate.text = item.date

        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }

        holder.binding.btnDelete.setOnClickListener {
            onDelete(item)
        }
    }

    override fun getItemCount(): Int = list.size
}