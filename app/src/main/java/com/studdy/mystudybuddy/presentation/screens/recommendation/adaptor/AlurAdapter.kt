package com.studdy.mystudybuddy.presentation.screens.recommendation.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.R

class AlurAdapter(
    private val alurList: List<String>
) : RecyclerView.Adapter<AlurAdapter.AlurViewHolder>() {

    class AlurViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView){

        val tvStep: TextView =
            itemView.findViewById(
                R.id.tvStep
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlurViewHolder {

        val view =
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_alur,
                parent,
                false
            )

        return AlurViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AlurViewHolder,
        position: Int
    ) {

        holder.tvStep.text =
            "${position + 1}. ${alurList[position]}"
    }

    override fun getItemCount(): Int {

        return alurList.size
    }
}