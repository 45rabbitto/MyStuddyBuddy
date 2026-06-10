package com.studdy.mystudybuddy.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class StreakManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("streak_prefs", Context.MODE_PRIVATE)

    fun updateStreak(): Int {

        val today = getToday()
        val lastDate = prefs.getString("last_date", null)
        var streak = prefs.getInt("streak_count", 0)

        if (lastDate == null) {
            // first time
            streak = 1
        } else {
            val diff = daysBetween(lastDate, today)

            when {
                diff == 0 -> {
                }
                diff == 1 -> {
                    streak += 1
                }
                else -> {
                    streak = 1
                }
            }
        }

        prefs.edit()
            .putString("last_date", today)
            .putInt("streak_count", streak)
            .apply()

        return streak
    }

    fun getStreak(): Int {
        return prefs.getInt("streak_count", 0)
    }

    private fun getToday(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun daysBetween(date1: String, date2: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val d1 = sdf.parse(date1)
        val d2 = sdf.parse(date2)

        val diff = d2.time - d1.time
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }
}