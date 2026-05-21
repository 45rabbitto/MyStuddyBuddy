package com.studdy.mystudybuddy.presentation.screens.recommendation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.databinding.ItemAlurFileBinding
import com.studdy.mystudybuddy.presentation.screens.recommendation.model.AlurFile

class AlurFileAdapter(
    private val fileList: List<AlurFile>,
    private val onItemClick: (AlurFile) -> Unit
) : RecyclerView.Adapter<AlurFileAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemAlurFileBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            ItemAlurFileBinding.inflate(
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

        val item = fileList[position]

        holder.binding.tvNamaFile.text =
            item.fileName

        holder.binding.icFile.setImageResource(
            item.icon
        )

        holder.binding.root.setOnClickListener {

            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {

        return fileList.size
    }
}