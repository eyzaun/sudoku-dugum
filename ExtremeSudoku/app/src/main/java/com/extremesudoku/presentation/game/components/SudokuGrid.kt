package com.extremesudoku.presentation.game.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.extremesudoku.data.models.Cell
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.AppTypography
import com.extremesudoku.presentation.theme.LocalThemeColors

@Suppress("UNUSED_PARAMETER")
@Composable
fun SudokuGrid(
    grid: Array<Array<Cell>>,
    selectedCell: Pair<Int, Int>?,
    highlightedNumber: Int?,
    conflictCells: Set<Pair<Int, Int>> = emptySet(),
    opponentCells: Map<Pair<Int, Int>, Int> = emptyMap(),  // Rakip hücreleri ve sayıları (PVP için)
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    isXSudoku: Boolean = false,
    showTimer: Boolean = true,
    highlightConflicts: Boolean = true,
    highlightRow: Boolean = true,
    highlightColumn: Boolean = true,
    highlightBox: Boolean = true,
    autoRemoveNotes: Boolean = true,
    showAffectedAreas: Boolean = false,  // Number pad'den mi yoksa grid'den mi geldiğini belirler
    colorizeNumbers: Boolean = true  // NEW: Colorize correct/wrong numbers
) {
    val themeColors = LocalThemeColors.current
    
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val cellSize = maxWidth / 9
        
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val row = (offset.y / cellSize.toPx()).toInt().coerceIn(0, 8)
                        val col = (offset.x / cellSize.toPx()).toInt().coerceIn(0, 8)
                        onCellClick(row, col)
                    }
                }
        ) {
            // Background - Grid background color
            drawRect(
                color = themeColors.gridBackground,
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height)
            )
            
            // Background highlights (çizim sırası: box -> row/col -> selected cell)
            selectedCell?.let { (selRow, selCol) ->
                // 1. Highlight 3x3 box (en altta) - Eğer ayar açıksa
                if (highlightBox) {
                    val boxRow = selRow / 3
                    val boxCol = selCol / 3
                    drawRect(
                        color = themeColors.selectedCellBox,
                        topLeft = Offset(boxCol * 3 * cellSize.toPx(), boxRow * 3 * cellSize.toPx()),
                        size = Size(3 * cellSize.toPx(), 3 * cellSize.toPx())
                    )
                }
                
                // 2. Highlight row and column (ortada) - Eğer ayarlar açıksa
                if (highlightRow) {
                    drawRect(
                        color = themeColors.selectedCellRow,
                        topLeft = Offset(0f, selRow * cellSize.toPx()),
                        size = Size(size.width, cellSize.toPx())
                    )
                }
                if (highlightColumn) {
                    drawRect(
                        color = themeColors.selectedCellRow,
                        topLeft = Offset(selCol * cellSize.toPx(), 0f),
                        size = Size(cellSize.toPx(), size.height)
                    )
                }
                
                // 3. Highlight selected cell (EN ÜSTTE - EN BELİRGİN) - Her zaman göster
                drawRect(
                    color = themeColors.selectedCell,
                    topLeft = Offset(selCol * cellSize.toPx(), selRow * cellSize.toPx()),
                    size = Size(cellSize.toPx(), cellSize.toPx())
                )
            }
            
            // Highlight cells with same number
            highlightedNumber?.let { num ->
                if (num != 0) {
                    // Etkilenen alanları sadece showAffectedAreas = true ise göster (number pad'den)
                    if (showAffectedAreas) {
                        // Önce etkilenen alanları bul ve highlight et
                        val affectedCells = mutableSetOf<Pair<Int, Int>>()
                        
                        grid.forEachIndexed { row, cells ->
                            cells.forEachIndexed { col, cell ->
                                if (cell.value == num) {
                                    // Bu hücrenin satırı, sütunu ve box'ını ekle
                                    for (i in 0..8) {
                                        affectedCells.add(Pair(row, i)) // Satır
                                        affectedCells.add(Pair(i, col)) // Sütun
                                    }
                                    // Box
                                    val boxRow = row / 3
                                    val boxCol = col / 3
                                    for (r in boxRow * 3 until (boxRow + 1) * 3) {
                                        for (c in boxCol * 3 until (boxCol + 1) * 3) {
                                            affectedCells.add(Pair(r, c))
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Etkilenen alanları özel renk ile highlight et
                        affectedCells.forEach { (row, col) ->
                            drawRect(
                                color = themeColors.affectedAreaCell,
                                topLeft = Offset(col * cellSize.toPx(), row * cellSize.toPx()),
                                size = Size(cellSize.toPx(), cellSize.toPx())
                            )
                        }
                    } else {
                        // Grid'den geliyorsa, sadece seçili hücrenin etkilediği alanları göster
                        selectedCell?.let { (selRow, selCol) ->
                            val selCell = grid[selRow][selCol]
                            if (selCell.value == num) {
                                // Sadece seçili hücrenin satır, sütun ve box'ı - özel renk ile
                                for (i in 0..8) {
                                    drawRect(
                                        color = themeColors.affectedAreaCell,
                                        topLeft = Offset(i * cellSize.toPx(), selRow * cellSize.toPx()),
                                        size = Size(cellSize.toPx(), cellSize.toPx())
                                    )
                                    drawRect(
                                        color = themeColors.affectedAreaCell,
                                        topLeft = Offset(selCol * cellSize.toPx(), i * cellSize.toPx()),
                                        size = Size(cellSize.toPx(), cellSize.toPx())
                                    )
                                }
                                // Box
                                val boxRow = selRow / 3
                                val boxCol = selCol / 3
                                drawRect(
                                    color = themeColors.affectedAreaCell,
                                    topLeft = Offset(boxCol * 3 * cellSize.toPx(), boxRow * 3 * cellSize.toPx()),
                                    size = Size(3 * cellSize.toPx(), 3 * cellSize.toPx())
                                )
                            }
                        }
                    }
                    
                    // Sonra aynı sayıları daha belirgin highlight et (her durumda)
                    grid.forEachIndexed { row, cells ->
                        cells.forEachIndexed { col, cell ->
                            if (cell.value == num) {
                                drawRect(
                                    color = themeColors.sameNumberCell,
                                    topLeft = Offset(col * cellSize.toPx(), row * cellSize.toPx()),
                                    size = Size(cellSize.toPx(), cellSize.toPx())
                                )
                            }
                        }
                    }
                }
            }
            
            // Highlight opponent cells (PVP Live Battle) - Daha belirgin!
            opponentCells.forEach { (position, _) ->
                val (row, col) = position
                drawRect(
                    color = themeColors.tertiary.copy(alpha = 0.5f),  // 0.3 → 0.5 daha belirgin
                    topLeft = Offset(col * cellSize.toPx(), row * cellSize.toPx()),
                    size = Size(cellSize.toPx(), cellSize.toPx())
                )
            }
            
            // Highlight conflict cells
            conflictCells.forEach { (row, col) ->
                drawRect(
                    color = themeColors.conflictCell,
                    topLeft = Offset(col * cellSize.toPx(), row * cellSize.toPx()),
                    size = Size(cellSize.toPx(), cellSize.toPx())
                )
            }
            
            // Grid lines
            for (i in 0..9) {
                val strokeWidth = if (i % 3 == 0) AppDimensions.gridThickLineWidth.toPx() else AppDimensions.gridLineWidth.toPx()
                val color = if (i % 3 == 0) themeColors.gridThickLine else themeColors.gridLine
                
                // Vertical lines
                drawLine(
                    color = color,
                    start = Offset(i * cellSize.toPx(), 0f),
                    end = Offset(i * cellSize.toPx(), size.height),
                    strokeWidth = strokeWidth
                )
                
                // Horizontal lines
                drawLine(
                    color = color,
                    start = Offset(0f, i * cellSize.toPx()),
                    end = Offset(size.width, i * cellSize.toPx()),
                    strokeWidth = strokeWidth
                )
            }
            
            // X-Sudoku diagonal lines
            if (isXSudoku) {
                // Main diagonal (top-left to bottom-right)
                drawLine(
                    color = themeColors.primary,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = AppDimensions.gridConflictLineWidth.toPx()
                )
                
                // Anti-diagonal (top-right to bottom-left)
                drawLine(
                    color = themeColors.primary,
                    start = Offset(size.width, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = AppDimensions.gridConflictLineWidth.toPx()
                )
            }
        }
        
        // Cell content (numbers and notes)
        grid.forEachIndexed { row, cells ->
            cells.forEachIndexed { col, cell ->
                Box(
                    modifier = Modifier
                        .offset(
                            x = cellSize * col,
                            y = cellSize * row
                        )
                        .size(cellSize),
                    contentAlignment = Alignment.Center
                ) {
                    // Rakip hücresi varsa önce onu göster
                    val opponentValue = opponentCells[row to col]
                    
                    when {
                        opponentValue != null -> {
                            // Rakip bu hücreye sayı yerleştirmiş
                            Text(
                                text = opponentValue.toString(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Normal,
                                color = themeColors.tertiary  // Rakip sayısı
                            )
                        }
                        cell.value != 0 -> {
                            // Kendi sayımız
                            val numberColor = if (colorizeNumbers) {
                                // Renklendirme açık: Hata/Hint/Initial/User renklerini kullan
                                when {
                                    cell.isError -> themeColors.wrongCell
                                    cell.isHint -> themeColors.hintCell
                                    cell.isInitial -> themeColors.initialNumberText
                                    else -> themeColors.userNumberText
                                }
                            } else {
                                // Renklendirme kapalı: Sadece initial ve user ayrımı
                                if (cell.isInitial) themeColors.initialNumberText 
                                else themeColors.userNumberText
                            }
                            
                            Text(
                                text = cell.value.toString(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = if (cell.isInitial) FontWeight.Bold else FontWeight.Normal,
                                color = numberColor
                            )
                        }
                        cell.notes.isNotEmpty() -> {
                            NoteGrid(notes = cell.notes, cellSize = cellSize)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteGrid(notes: Set<Int>, cellSize: androidx.compose.ui.unit.Dp) {
    val themeColors = LocalThemeColors.current
    
    Box(modifier = Modifier.size(cellSize)) {
        notes.forEach { note ->
            val row = (note - 1) / 3
            val col = (note - 1) % 3
            Text(
                text = note.toString(),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .offset(
                        x = (cellSize / 3) * col,
                        y = (cellSize / 3) * row
                    )
                    .size(cellSize / 3)
                    .wrapContentSize(Alignment.Center),
                fontSize = AppTypography.sudokuNotesSize,
                color = themeColors.notesText
            )
        }
    }
}
