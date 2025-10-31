package com.extremesudoku.data.models.pvp

/**
 * PvP match'de kullanılan sudoku puzzle
 */
data class PvpPuzzle(
    val puzzleString: String = "", // 81 karakter (0 = boş hücre)
    val solution: String = "",     // 81 karakter (çözüm)
    val difficulty: String = "medium"
)
