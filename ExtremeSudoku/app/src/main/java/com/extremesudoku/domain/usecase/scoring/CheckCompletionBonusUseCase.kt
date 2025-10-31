package com.extremesudoku.domain.usecase.scoring

import com.extremesudoku.data.models.Cell
import com.extremesudoku.data.models.scoring.CompletionEvent
import com.extremesudoku.data.models.scoring.CompletionType
import com.extremesudoku.data.models.scoring.ScoringConstants
import javax.inject.Inject

/**
 * Kutu/Sıra/Sütun tamamlanma kontrolü ve bonus hesaplama
 */
class CheckCompletionBonusUseCase @Inject constructor() {
    
    /**
     * Bir hamleden sonra tamamlanan bölgeleri kontrol et
     * @param grid Mevcut oyun grid'i
     * @param row Son hamlenin satırı
     * @param col Son hamlenin sütunu
     * @param completedBoxes Zaten tamamlanmış kutular (set)
     * @param completedRows Zaten tamamlanmış satırlar (set)
     * @param completedColumns Zaten tamamlanmış sütunlar (set)
     * @return Yeni tamamlanan bölgeler listesi
     */
    operator fun invoke(
        grid: Array<Array<Cell>>,
        row: Int,
        col: Int,
        completedBoxes: Set<Int>,
        completedRows: Set<Int>,
        completedColumns: Set<Int>
    ): List<CompletionEvent> {
        val events = mutableListOf<CompletionEvent>()
        val timestamp = System.currentTimeMillis()
        
        // 1. BOX TAMAMLANMA KONTROLÜ
        val boxIndex = (row / 3) * 3 + (col / 3)
        if (!completedBoxes.contains(boxIndex) && isBoxComplete(grid, boxIndex)) {
            events.add(CompletionEvent(
                type = CompletionType.BOX,
                index = boxIndex,
                timestamp = timestamp,
                bonusEarned = ScoringConstants.BOX_COMPLETE_BONUS
            ))
        }
        
        // 2. ROW TAMAMLANMA KONTROLÜ
        if (!completedRows.contains(row) && isRowComplete(grid, row)) {
            events.add(CompletionEvent(
                type = CompletionType.ROW,
                index = row,
                timestamp = timestamp,
                bonusEarned = ScoringConstants.ROW_COMPLETE_BONUS
            ))
        }
        
        // 3. COLUMN TAMAMLANMA KONTROLÜ
        if (!completedColumns.contains(col) && isColumnComplete(grid, col)) {
            events.add(CompletionEvent(
                type = CompletionType.COLUMN,
                index = col,
                timestamp = timestamp,
                bonusEarned = ScoringConstants.COLUMN_COMPLETE_BONUS
            ))
        }
        
        return events
    }
    
    /**
     * 3x3 kutu tamamlandı mı?
     */
    private fun isBoxComplete(grid: Array<Array<Cell>>, boxIndex: Int): Boolean {
        val startRow = (boxIndex / 3) * 3
        val startCol = (boxIndex % 3) * 3
        
        for (r in startRow until startRow + 3) {
            for (c in startCol until startCol + 3) {
                if (grid[r][c].value == 0) {
                    return false
                }
            }
        }
        return true
    }
    
    /**
     * Satır tamamlandı mı?
     */
    private fun isRowComplete(grid: Array<Array<Cell>>, row: Int): Boolean {
        for (col in 0..8) {
            if (grid[row][col].value == 0) {
                return false
            }
        }
        return true
    }
    
    /**
     * Sütun tamamlandı mı?
     */
    private fun isColumnComplete(grid: Array<Array<Cell>>, col: Int): Boolean {
        for (row in 0..8) {
            if (grid[row][col].value == 0) {
                return false
            }
        }
        return true
    }
}
