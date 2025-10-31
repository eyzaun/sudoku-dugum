package com.extremesudoku.data.models.pvp

/**
 * Bir match'teki oyuncu verisi
 */
data class PlayerMatchData(
    val userId: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val status: PlayerStatus = PlayerStatus.READY,
    val joinedAt: Long = System.currentTimeMillis(),
    val result: PlayerResult? = null
)
