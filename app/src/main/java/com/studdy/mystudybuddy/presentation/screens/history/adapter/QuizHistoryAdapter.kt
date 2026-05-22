package com.studdy.mystudybuddy.presentation.screens.history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.R

class QuizHistoryAdapter(
    private val questions: List<Triple<String, String, String>>
) : RecyclerView.Adapter<QuizHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvQuestion: TextView =
            itemView.findViewById(R.id.tvQuestion)

        val tvCorrect: TextView =
            itemView.findViewById(R.id.tvCorrectAnswer)

        val tvWrong: TextView =
            itemView.findViewById(R.id.tvWrongAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_quiz, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = questions[position]

        holder.tvQuestion.text =
            "${position + 1}. ${item.first}"

        holder.tvCorrect.text =
            item.second

        holder.tvWrong.text =
            item.third
    }

    override fun getItemCount(): Int = questions.size
}