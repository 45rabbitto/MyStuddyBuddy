package com.studdy.mystudybuddy.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.studdy.mystudybuddy.data.local.database.entities.ChatMessage

@Dao
interface ChatMessageDao {

    @Insert
    suspend fun insertMessage(chatMessage: ChatMessage)

    @Query("SELECT * FROM chat_messages")
    suspend fun getAllMessages(): List<ChatMessage>
}