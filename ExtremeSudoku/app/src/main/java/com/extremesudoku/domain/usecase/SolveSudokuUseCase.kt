package com.extremesudoku.domain.usecase

import javax.inject.Inject

class SolveSudokuUseCase @Inject constructor() {
    operator fun invoke(puzzle: String): String? {
        val grid = puzzleToGrid(puzzle)
        if (solve(grid)) {
            return gridToPuzzle(grid)
        }
        return null
    }
    
    private fun solve(grid: Array<IntArray>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] == 0) {
                    for (num in 1..9) {
                        if (isValid(grid, row, col, num)) {
                            grid[row][col] = num
                            if (solve(grid)) return true
                            grid[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }
    
    private fun isValid(grid: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        // Satır kontrolü
        if (grid[row].contains(num)) return false
        
        // Sütun kontrolü
        for (i in 0..8) {
            if (grid[i][col] == num) return false
        }
        
        // 3x3 kutu kontrolü
        val boxRow = row / 3 * 3
        val boxCol = col / 3 * 3
        for (i in boxRow until boxRow + 3) {
            for (j in boxCol until boxCol + 3) {
                if (grid[i][j] == num) return false
            }
        }
        
        return true
    }
    
    private fun puzzleToGrid(puzzle: String): Array<IntArray> {
        return Array(9) { row ->
            IntArray(9) { col ->
                puzzle[row * 9 + col].toString().toIntOrNull() ?: 0
            }
        }
    }
    
    private fun gridToPuzzle(grid: Array<IntArray>): String {
        return grid.joinToString("") { row ->
            row.joinToString("")
        }
    }
}
