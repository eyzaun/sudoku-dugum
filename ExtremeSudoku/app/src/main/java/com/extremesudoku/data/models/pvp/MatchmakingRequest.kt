package com.extremesudoku.data.models.pvp

/**
 * Matchmaking kuyruğuna giriş talebi
 */
data class MatchmakingRequest(
    val userId: String = "",
    val playerName: String = "",
    val rating: Int = 1000, // ELO rating
    val mode: PvpMode = PvpMode.BLIND_RACE,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "searching", // searching, matched, cancelled
    val matchId: String? = null // Eşleşme bulununca set edilir
)
