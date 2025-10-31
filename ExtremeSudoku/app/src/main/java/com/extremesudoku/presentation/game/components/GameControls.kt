package com.extremesudoku.presentation.game.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.LocalThemeColors

@Composable
fun GameControls(
    isNoteMode: Boolean,
    canUndo: Boolean = true,
    canRedo: Boolean = true,
    onNoteModeToggle: () -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onHintClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ControlButton(
            icon = Icons.Default.Undo,
            label = "Undo",
            onClick = onUndoClick,
            enabled = canUndo
        )
        
        ControlButton(
            icon = Icons.Default.Redo,
            label = "Redo",
            onClick = onRedoClick,
            enabled = canRedo
        )
        
        ControlButton(
            icon = Icons.Default.Delete,
            label = "Erase",
            onClick = onDeleteClick
        )
        
        ControlButton(
            icon = if (isNoteMode) Icons.Default.Edit else Icons.Outlined.Edit,
            label = "Notes",
            onClick = onNoteModeToggle,
            isSelected = isNoteMode
        )
        
        ControlButton(
            icon = Icons.Default.Lightbulb,
            label = "Hint",
            onClick = onHintClick
        )
    }
}

@Composable
fun ControlButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    enabled: Boolean = true
) {
    val themeColors = LocalThemeColors.current
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(enabled = enabled, onClick = onClick)
            .padding(AppDimensions.gameControlPadding)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (!enabled)
                themeColors.textSecondary.copy(alpha = 0.38f)
            else if (isSelected) 
                themeColors.primary 
            else 
                themeColors.iconTint,
            modifier = Modifier.size(AppDimensions.gameControlIconSize)
        )
        Spacer(modifier = Modifier.height(AppDimensions.spacingExtraSmall))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (!enabled)
                themeColors.textSecondary.copy(alpha = 0.38f)
            else if (isSelected) 
                themeColors.primary 
            else 
                themeColors.textSecondary
        )
    }
}
