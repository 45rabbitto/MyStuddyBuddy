package com.studdy.mystudybuddy.presentation.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.databinding.ItemHistorySummaryBinding
import com.studdy.mystudybuddy.presentation.history.model.SummaryHistoryModel

class SummaryHistoryAdapter(
    private val list: MutableList<SummaryHistoryModel>,
    private val onDeleteClick: (SummaryHistoryModel) -> Unit
) : RecyclerView.Adapter<SummaryHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemHistorySummaryBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = ItemHistorySummaryBinding.inflate(
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

        holder.binding.tvTitle.text = item.title
        holder.binding.tvDate.text = item.date

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount() = list.size
}