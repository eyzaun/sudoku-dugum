package com.extremesudoku.data.models.pvp

/**
 * Move history for undo/redo
 */
data class GameMove(
    val row: Int,
    val col: Int,
    val oldValue: Int,
    val newValue: Int
)
