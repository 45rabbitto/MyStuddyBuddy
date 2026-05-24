package com.studdy.mystudybuddy.presentation.screens.quiz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.databinding.ItemQuizBinding
import com.studdy.mystudybuddy.presentation.screens.quiz.model.QuizQuestion

class QuizAdapter(
    private val questionList: List<QuizQuestion>,
    private val onOptionClick: (Int, Int) -> Unit
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    inner class QuizViewHolder(
        private val binding: ItemQuizBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            question: QuizQuestion,
            position: Int
        ) {

            binding.tvQuestion.text =
                "${position + 1}. ${question.question}"

            binding.optionA.text =
                question.options[0]

            binding.optionB.text =
                question.options[1]

            binding.optionC.text =
                question.options[2]

            binding.optionD.text =
                question.options[3]

            binding.optionA.setOnClickListener {
                onOptionClick(position,0)
            }

            binding.optionB.setOnClickListener {
                onOptionClick(position,1)
            }

            binding.optionC.setOnClickListener {
                onOptionClick(position,2)
            }

            binding.optionD.setOnClickListener {
                onOptionClick(position,3)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuizViewHolder {

        val binding = ItemQuizBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return QuizViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: QuizViewHolder,
        position: Int
    ) {

        holder.bind(
            questionList[position],
            position
        )
    }

    override fun getItemCount(): Int {
        return questionList.size
    }
}