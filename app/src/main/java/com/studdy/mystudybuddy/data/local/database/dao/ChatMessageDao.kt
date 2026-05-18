package com.studdy.mystudybuddy.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.studdy.mystudybuddy.data.local.database.entity.ChatMessage

@Dao
interface ChatMessageDao {

    @Insert
    suspend fun insertMessage(
        message: ChatMessage
    )

    @Query("SELECT * FROM chat_messages")
    suspend fun getAllMessages(): List<ChatMessage>

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllMessages()
}