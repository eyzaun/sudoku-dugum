package com.extremesudoku.presentation.pvp.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.extremesudoku.data.models.Cell
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.AppShapes
import com.extremesudoku.presentation.theme.AppTypography
import com.extremesudoku.presentation.theme.LocalThemeColors

/**
 * Shared PvP Game Components
 */

@Composable
fun PvpGameNumberPad(
    onNumberClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(AppDimensions.spacingSmall)) {
            for (row in 0..2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0..2) {
                        val number = row * 3 + col + 1
                        FilledTonalButton(
                            onClick = { onNumberClick(number) },
                            modifier = Modifier.weight(1f).padding(AppDimensions.spacingExtraSmall)
                        ) {
                            Text(number.toString(), fontSize = AppTypography.sudokuNumberSize, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PvpGameControls(
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onErase: () -> Unit,
    canUndo: Boolean,
    canRedo: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = onUndo, enabled = canUndo) {
            Icon(
                Icons.Default.Undo,
                contentDescription = "Geri Al",
                tint = if (canUndo) MaterialTheme.colorScheme.primary 
                      else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
        IconButton(onClick = onRedo, enabled = canRedo) {
            Icon(
                Icons.Default.Redo,
                contentDescription = "İleri Al",
                tint = if (canRedo) MaterialTheme.colorScheme.primary 
                      else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
        IconButton(onClick = onErase) {
            Icon(Icons.Default.Clear, contentDescription = "Sil", tint = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun PvpGameFinishedOverlay(
    isWinner: Boolean?,
    elapsedTime: Long,
    reason: String? = null  // Yeni: Özel mesaj (örn: "Rakip oyundan ayrıldı")
) {
    val themeColors = LocalThemeColors.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColors.modalScrim),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f).padding(AppDimensions.spacingMedium),
            colors = CardDefaults.cardColors(
                containerColor = when (isWinner) {
                    true -> MaterialTheme.colorScheme.primaryContainer
                    false -> MaterialTheme.colorScheme.errorContainer
                    null -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(AppDimensions.dialogPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (isWinner) {
                        true -> "🏆"
                        false -> "😔"
                        null -> "⏱️"
                    },
                    fontSize = AppTypography.fontSizeDisplay
                )
                Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
                Text(
                    text = when (isWinner) {
                        true -> "KAZANDIN!"
                        false -> "KAYBETTİN"
                        null -> "BERABERE"
                    },
                    fontSize = AppTypography.fontSizeExtraLarge,
                    fontWeight = FontWeight.Bold
                )
                
                // Özel mesaj varsa göster
                if (reason != null) {
                    Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))
                    Text(
                        text = reason,
                        fontSize = AppTypography.fontSizeMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))
                Text(
                    text = "Süre: ${com.extremesudoku.utils.formatTime(elapsedTime)}",
                    fontSize = AppTypography.fontSizeLarge
                )
            }
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
fun RowScope.PvpSudokuCell(
    cell: Cell,
    row: Int,
    col: Int,
    isSelected: Boolean,
    isHighlighted: Boolean,
    isConflict: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isHighlighted -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        isConflict -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        else -> Color.Transparent
    }
    
    val textColor = when {
        cell.isInitial -> MaterialTheme.colorScheme.onSurface
        cell.isError -> MaterialTheme.colorScheme.error
        cell.isHint -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
    
    val borderWidth = if (row % 3 == 0 && row != 0) AppDimensions.pvpBorderWidth else AppDimensions.gridLineWidth
    
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .background(backgroundColor)
            .border(borderWidth, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            .clickable(enabled = !cell.isInitial) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (cell.value != 0) {
            Text(
                text = cell.value.toString(),
                fontSize = AppTypography.sudokuNumberSize,
                fontWeight = if (cell.isInitial) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        }
    }
}
