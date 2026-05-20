package com.studdy.mystuddybuddy.presentation.screens.upload.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.databinding.ItemUploadBinding
import com.studdy.mystuddybuddy.presentation.screens.upload.model.UploadFile

class UploadAdapter(
    private val fileList: MutableList<UploadFile>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<UploadAdapter.UploadViewHolder>() {

    inner class UploadViewHolder(
        private val binding: ItemUploadBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(file: UploadFile) {

            binding.tvFileName.text =
                file.fileName

            binding.btnDelete.setOnClickListener {

                onDeleteClick(
                    adapterPosition
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UploadViewHolder {

        val binding =
            ItemUploadBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )

        return UploadViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(
        holder: UploadViewHolder,
        position: Int
    ) {

        holder.bind(
            fileList[position]
        )
    }

    override fun getItemCount(): Int {

        return fileList.size
    }

    fun removeItem(position: Int){

        fileList.removeAt(position)

        notifyItemRemoved(position)

        notifyItemRangeChanged(
            position,
            fileList.size
        )
    }
}