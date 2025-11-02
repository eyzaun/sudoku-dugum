package com.extremesudoku.presentation.game.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.extremesudoku.R
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.LocalThemeColors
import com.extremesudoku.utils.formatTime

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onQuit: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    
    AlertDialog(
        onDismissRequest = onResume,
        title = { Text(stringResource(R.string.game_paused), color = themeColors.text) },
        text = { Text(stringResource(R.string.pause_dialog_message), color = themeColors.textSecondary) },
        confirmButton = {
            TextButton(onClick = onResume) {
                Text(stringResource(R.string.resume), color = themeColors.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onQuit) {
                Text(stringResource(R.string.quit), color = themeColors.textSecondary)
            }
        },
        containerColor = themeColors.surface
    )
}

@Composable
fun CompletionDialog(
    time: Long,
    moves: Int,
    onNewGame: () -> Unit,
    onHome: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    
    AlertDialog(
        onDismissRequest = onHome,
        containerColor = themeColors.surface,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = themeColors.winColor,
                    modifier = Modifier.size(AppDimensions.dialogIconSize)
                )
                Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))
                Text(
                    stringResource(R.string.completion_dialog_title),
                    textAlign = TextAlign.Center,
                    color = themeColors.text
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.completion_dialog_message),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = themeColors.text
                )
                Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatTime(time),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(stringResource(R.string.stat_time), style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = moves.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(stringResource(R.string.stat_moves), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onNewGame) {
                Text(stringResource(R.string.new_game))
            }
        },
        dismissButton = {
            TextButton(onClick = onHome) {
                Text(stringResource(R.string.home))
            }
        }
    )
}
