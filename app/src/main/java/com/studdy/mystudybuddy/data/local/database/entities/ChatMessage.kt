package com.studdy.mystudybuddy.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val message: String,

    val sender: String,

    val timestamp: Long
)