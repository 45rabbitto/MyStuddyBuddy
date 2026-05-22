package com.studdy.mystudybuddy.presentation.screens.quiz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.R

class HasilKuisAdapter(
    private val pembahasanList: List<String>
) : RecyclerView.Adapter<HasilKuisAdapter.HasilViewHolder>() {

    class HasilViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val tvPembahasan: TextView =
            itemView.findViewById(R.id.tvPembahasan)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HasilViewHolder {

        val view = LayoutInflater.from(
            parent.context
        ).inflate(
            R.layout.item_pembahasan_kuis,
            parent,
            false
        )

        return HasilViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HasilViewHolder,
        position: Int
    ) {

        holder.tvPembahasan.text =
            pembahasanList[position]
    }

    override fun getItemCount(): Int {
        return pembahasanList.size
    }
}