package com.moraveco.challengeme.data

data class ReportData(
    val sender: String,
    val message: String,
    val app: String = "ChallengeMe"
)
