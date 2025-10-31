package com.extremesudoku.domain.usecase

import javax.inject.Inject

class ValidateMoveUseCase @Inject constructor() {
    operator fun invoke(
        grid: Array<IntArray>,
        row: Int,
        col: Int,
        num: Int,
        isXSudoku: Boolean = false
    ): Boolean {
        if (num == 0) return true // Silme işlemi her zaman geçerli
        
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
        
        // X-Sudoku için diagonal kontrol (extreme variation)
        if (isXSudoku) {
            // Ana diagonal (sol üstten sağ alta)
            if (row == col) {
                for (i in 0..8) {
                    if (grid[i][i] == num) return false
                }
            }
            
            // Ters diagonal (sağ üstten sol alta)
            if (row + col == 8) {
                for (i in 0..8) {
                    if (grid[i][8 - i] == num) return false
                }
            }
        }
        
        return true
    }
}
