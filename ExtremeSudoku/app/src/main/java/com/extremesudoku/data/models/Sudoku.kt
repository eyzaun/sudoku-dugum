package com.extremesudoku.data.models

import com.google.firebase.Timestamp

data class Sudoku(
    val id: String = "",
    val puzzle: String = "", // 81 karakter (0 = boş hücre)
    val solution: String = "", // 81 karakter (çözüm)
    val difficulty: String = "medium", // Default: medium (easy, medium, hard, expert)
    val category: String = "classic", // classic, extreme, daily
    val isXSudoku: Boolean = false, // X-Sudoku için diagonal kontrol
    val rating: Double = 0.0,
    val source: String = "", // Dataset source (puzzles0_kaggle, puzzles4_forum_hardest_1905, etc.)
    val createdAt: Timestamp? = null // Firebase timestamp
)
