package com.moraveco.challengeme.data

import kotlinx.serialization.Serializable


@Serializable
data class DailyChallenge(
    val id: String,
    val en: String,
    val cs: String,
    val sk: String,
    val date: String
)
