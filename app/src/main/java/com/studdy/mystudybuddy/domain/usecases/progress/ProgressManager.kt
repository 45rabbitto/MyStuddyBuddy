package com.studdy.mystudybuddy.domain

class ProgressManager {

    fun getUploadProgress(): Int {
        return 80
    }

    fun getSummaryProgress(): Int {
        return 65
    }

    fun getQuizProgress(): Int {
        return 90
    }

    fun getChatbotProgress(): Int {
        return 70
    }

    fun getTotalProgress(): Int {

        return (
                getUploadProgress() +
                        getSummaryProgress() +
                        getQuizProgress() +
                        getChatbotProgress()
                ) / 4
    }
}