package com.extremesudoku.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.extremesudoku.data.models.Sudoku

@Entity(tableName = "sudokus")
data class SudokuEntity(
    @PrimaryKey val id: String,
    val puzzle: String,
    val solution: String,
    val difficulty: String,
    val category: String,
    val isXSudoku: Boolean,
    val rating: Double,
    val isSynced: Boolean = false
)

fun SudokuEntity.toDomain(): Sudoku {
    return Sudoku(
        id = id,
        puzzle = puzzle,
        solution = solution,
        difficulty = difficulty,
        category = category,
        isXSudoku = isXSudoku,
        rating = rating
    )
}

fun Sudoku.toEntity(): SudokuEntity {
    return SudokuEntity(
        id = id,
        puzzle = puzzle,
        solution = solution,
        difficulty = difficulty,
        category = category,
        isXSudoku = isXSudoku,
        rating = rating
    )
}
