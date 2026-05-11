package com.studdy.mystudybuddy.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.studdy.mystudybuddy.data.local.database.dao.*
import com.studdy.mystudybuddy.data.local.database.entity.*

@Database(
    entities = [
        Document::class,
        QuizResult::class,
        UserProgress::class,
        ChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun quizResultDao(): QuizResultDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "study_buddy_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}