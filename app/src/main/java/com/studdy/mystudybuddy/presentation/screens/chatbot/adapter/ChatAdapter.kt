package com.studdy.mystudybuddy.presentation.screens.chatbot.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.R
import com.studdy.mystudybuddy.presentation.screens.chatbot.model.ChatMessage

class ChatAdapter(private val messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    override fun getItemViewType(position: Int): Int = if (messages[position].isUser) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = if (viewType == 0) R.layout.item_chat_user else R.layout.item_chat_ai
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = messages[position]
        if (msg.isUser) {
            holder.tvUserMessage?.text = msg.message
        } else {
            holder.tvAiMessage?.text = msg.message
        }
    }

    override fun getItemCount(): Int = messages.size

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val tvUserMessage: TextView? = itemView.findViewById(R.id.tvUserMessage)
        val tvAiMessage: TextView? = itemView.findViewById(R.id.tvAiMessage)
    }
}