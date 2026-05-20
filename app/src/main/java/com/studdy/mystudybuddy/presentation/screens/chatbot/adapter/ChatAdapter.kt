package com.studdy.mystudybuddy.presentation.screens.chatbot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.R
import com.studdy.mystuddybuddy.presentation.chatbot.model.ChatMessage

class ChatAdapter(
    private val messages: MutableList<ChatMessage>
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvMessage: TextView =
            itemView.findViewById(R.id.tvUserMessage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {

        val layout = if (viewType == 0) {
            R.layout.item_chat_user
        } else {
            R.layout.item_chat_ai
        }

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    layout,
                    parent,
                    false
                )

        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int
    ) {

        val item = messages[position]

        holder.tvMessage.text =
            item.message
    }

    override fun getItemCount(): Int {

        return messages.size
    }

    override fun getItemViewType(
        position: Int
    ): Int {

        return if (messages[position].isUser) {
            0
        } else {
            1
        }
    }

    fun addMessage(
        message: ChatMessage
    ) {

        messages.add(message)

        notifyItemInserted(
            messages.size - 1
        )
    }
}