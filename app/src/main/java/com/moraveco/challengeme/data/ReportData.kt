package com.moraveco.challengeme.data

data class ReportData(
    val sender: String,
    val message: String,
    val app: String = "ChallengeMe"
)

data class SendEmail(
    val email: String,
    val message: String,
    val subject: String,
    val app: String = "ChallengeMe"
)
