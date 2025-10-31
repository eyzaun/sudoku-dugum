package com.extremesudoku.data.models.pvp

/**
 * PvP oyununda yapılan bir hamle (Live Battle modunda kullanılır)
 */
data class PvpMove(
    val moveId: String = "",
    val playerId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val row: Int = 0,
    val col: Int = 0,
    val value: Int = 0,
    val isCorrect: Boolean = false,
    val moveNumber: Int = 0 // Sıralı hamle numarası
)
