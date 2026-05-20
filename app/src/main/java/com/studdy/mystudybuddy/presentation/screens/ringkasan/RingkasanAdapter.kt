package com.studdy.mystudybuddy.presentation.screens.ringkasan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.databinding.ItemRingkasanBinding

class RingkasanAdapter(
    private val ringkasanList: List<String>
) : RecyclerView.Adapter<RingkasanAdapter.RingkasanViewHolder>() {

    inner class RingkasanViewHolder(
        private val binding: ItemRingkasanBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            ringkasan: String,
            position: Int
        ) {

            binding.tvNomor.text =
                "${position + 1}"

            binding.tvRingkasanIsi.text =
                ringkasan
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RingkasanViewHolder {

        val binding =
            ItemRingkasanBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )

        return RingkasanViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(
        holder: RingkasanViewHolder,
        position: Int
    ) {

        holder.bind(
            ringkasanList[position],
            position
        )
    }

    override fun getItemCount(): Int {
        return ringkasanList.size
    }
}