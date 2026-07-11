package com.studdy.mystudybuddy.utils

object KeywordExtractor {

    private val stopWords = setOf(
        "yang", "dan", "di", "ke", "dari", "pada", "ini", "itu", "untuk", "dengan",
        "oleh", "sebagai", "atau", "dalam", "dari", "kepada", "bagi", "tanpa",
        "adalah", "ialah", "yaitu", "yakni", "dll", "dsb"
    )

    fun extractKeywords(text: String): List<String> {
        val cleaned = text
            .replace(Regex("[^a-zA-Z\\s]"), " ")
            .lowercase()
            .trim()

        val words = cleaned.split(Regex("\\s+"))

        val filtered = words
            .filter { it.length > 3 }
            .filter { it !in stopWords }

        val frequency = filtered.groupingBy { it }.eachCount()

        return frequency.entries
            .sortedByDescending { it.value }
            .map { it.key }
            .take(5)
    }
}