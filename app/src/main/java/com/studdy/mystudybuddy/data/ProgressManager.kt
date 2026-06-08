package com.studdy.mystudybuddy.data

class ProgressManager(private val repo: ProgressRepository) {

    private val TARGET_UPLOAD = 5
    private val TARGET_SUMMARY = 5
    private val TARGET_QUIZ = 5
    private val TARGET_CHATBOT = 5

    fun getUploadProgress(): Int =
        (repo.getUploadCount() * 100 / TARGET_UPLOAD).coerceAtMost(100)

    fun getSummaryProgress(): Int =
        (repo.getSummaryCount() * 100 / TARGET_SUMMARY).coerceAtMost(100)

    fun getQuizProgress(): Int =
        (repo.getQuizCount() * 100 / TARGET_QUIZ).coerceAtMost(100)

    fun getChatbotProgress(): Int =
        (repo.getChatbotCount() * 100 / TARGET_CHATBOT).coerceAtMost(100)

    fun getTotalProgress(): Int {
        val uploadWeight = getUploadProgress() * 0.20
        val summaryWeight = getSummaryProgress() * 0.20
        val quizWeight = getQuizProgress() * 0.30
        val chatbotWeight = getChatbotProgress() * 0.30

        return (uploadWeight + summaryWeight + quizWeight + chatbotWeight).toInt()
    }
}