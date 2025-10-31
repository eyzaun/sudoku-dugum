package com.extremesudoku.data.models

data class LeaderboardEntry(
    val userId: String = "",
    val username: String = "",
    val profilePictureUrl: String = "",
    val bestTime: Long = 0,
    val gamesCompleted: Int = 0,
    val rank: Int = 0
)
