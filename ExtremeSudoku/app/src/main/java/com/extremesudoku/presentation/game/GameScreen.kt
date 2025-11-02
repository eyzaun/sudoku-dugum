package com.extremesudoku.presentation.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.extremesudoku.R
import com.extremesudoku.presentation.game.components.*
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.AppShapes
import com.extremesudoku.presentation.theme.LocalThemeColors
import com.extremesudoku.ui.components.GameCompleteDialog
import com.extremesudoku.presentation.settings.SettingToggle
import com.extremesudoku.utils.formatTime
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel(),
    preferencesViewModel: com.extremesudoku.presentation.settings.SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val preferences by preferencesViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val themeColors = LocalThemeColors.current
    var showSettingsDialog by remember { mutableStateOf(false) }
    
    // **SCORING SYSTEM** - Collect scoring states
    val gameScore by viewModel.gameScore.collectAsState()
    val bonusEvents = viewModel.bonusEvents
    
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }
    
    Scaffold(
        containerColor = themeColors.background,
        topBar = {
            GameTopBar(
                onPauseClick = { viewModel.onPauseGame() },
                onBackClick = onNavigateBack,
                onSettingsClick = { showSettingsDialog = true }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.sudoku != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = AppDimensions.spacingMedium, vertical = AppDimensions.spacingSmall),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
                ) {
                    // Stats Row (Time, Moves, Hints)
                    GameStatsRow(
                        moves = uiState.moves,
                        hintsUsed = uiState.hintsUsed,
                        maxHints = 3,
                        elapsedTime = if (preferences.showTimer) uiState.elapsedTime else null
                    )
                    
                    // Score ve Streak - Stats row'un hemen altında
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = LocalThemeColors.current.surface.copy(alpha = 0.5f),
                                shape = AppShapes.small
                            )
                            .padding(horizontal = AppDimensions.spacingMedium, vertical = AppDimensions.spacingSmall),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Score and Streak - Conditional
                        if (preferences.showScoreAndStreak) {
                            // Bonus popup için event dinle
                            val bonusList by bonusEvents.collectAsState()
                            val currentBonus = bonusList.firstOrNull()
                            
                            // Bonus varsa otomatik dismiss
                            currentBonus?.let { bonus ->
                                LaunchedEffect(bonus) {
                                    delay(1500)
                                    viewModel.dismissBonusEvent(bonus)
                                }
                            }
                            
                            // Toplam Puan + Bonus Animasyonu
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.score_label),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LocalThemeColors.current.text.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "${gameScore.finalScore}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = LocalThemeColors.current.primary
                                )
                                
                                // Bonus animasyonu - score'un yanında
                                AnimatedVisibility(
                                    visible = currentBonus != null,
                                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { -20 }) + scaleIn(initialScale = 0.8f),
                                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -30 }) + scaleOut(targetScale = 0.5f)
                                ) {
                                    currentBonus?.let { bonus ->
                                        Text(
                                            text = stringResource(R.string.bonus_points_gain, bonus.points),
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = themeColors.bonusGold,
                                            modifier = Modifier
                                                .background(
                                                    color = themeColors.bonusGold.copy(alpha = 0.2f),
                                                    shape = AppShapes.small
                                                )
                                                .padding(horizontal = AppDimensions.spacingSmall, vertical = AppDimensions.spacingExtraSmall)
                                        )
                                    }
                                }
                            }
                            
                            // Streak
                            if (gameScore.currentStreak > 0) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingExtraSmall),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.streak_label_short),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        text = stringResource(R.string.streak_value, gameScore.currentStreak),
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = themeColors.streakOrange
                                    )
                                }
                            }
                        }
                    }
                
                // Sudoku Grid
                SudokuGrid(
                    grid = uiState.currentGrid,
                    selectedCell = uiState.selectedCell,
                    highlightedNumber = if (preferences.highlightSameNumbers) uiState.highlightedNumber else null,
                    conflictCells = if (preferences.highlightConflicts) uiState.conflictCells else emptySet(),
                    onCellClick = { row, col ->
                        viewModel.onCellSelected(row, col)
                    },
                    modifier = Modifier.weight(1f),
                    isXSudoku = uiState.sudoku?.isXSudoku ?: false,
                    showAffectedAreas = uiState.showAffectedAreas && preferences.showAffectedAreas,
                    highlightRow = preferences.highlightSelectedArea,
                    highlightColumn = preferences.highlightSelectedArea,
                    highlightBox = preferences.highlightSelectedArea,
                    autoRemoveNotes = preferences.autoRemoveNotes,
                    colorizeNumbers = preferences.colorizeNumbers  // NEW: Number coloring
                )
                
                // Game Controls
                GameControls(
                    isNoteMode = uiState.isNoteMode,
                    canUndo = uiState.canUndo,
                    canRedo = uiState.canRedo,
                    onNoteModeToggle = { viewModel.toggleNoteMode() },
                    onUndoClick = { viewModel.onUndoPressed() },
                    onRedoClick = { viewModel.onRedoPressed() },
                    onHintClick = { viewModel.onHintRequested() },
                    onDeleteClick = { viewModel.onDeletePressed() }
                )
                
                // Number Pad
                NumberPad(
                    onNumberClick = { number ->
                        viewModel.onNumberSelected(number)
                    },
                    remainingNumbers = if (preferences.showRemainingNumbers) uiState.remainingNumbers else emptyMap()
                )
                }
                
                // Bonus notifications - REMOVED (artık score'un yanında gösterilecek)
                
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.error_loading_sudoku))
                }
            }
        
        // Pause Dialog
        if (uiState.isPaused) {
            PauseDialog(
                onResume = { viewModel.onResumeGame() },
                onQuit = onNavigateBack
            )
        }
        
        // Completion Dialog - Enhanced with scoring breakdown
        if (uiState.isCompleted) {
            GameCompleteDialog(
                score = gameScore,
                isNewHighScore = false, // TODO: Check against user's high score
                earnedBadges = emptyList(), // TODO: Implement badge system
                onPlayAgain = { /* Navigate to new game */ },
                onMainMenu = onNavigateBack,
                onShare = { /* TODO: Implement share functionality */ }
            )
        }
        
        // Settings Dialog
        if (showSettingsDialog) {
            InGameSettingsDialog(
                preferences = preferences,
                preferencesViewModel = preferencesViewModel,
                onDismiss = { showSettingsDialog = false }
            )
        }
        
        // Error Snackbar
        uiState.error?.let { error ->
            Snackbar(
                modifier = Modifier.padding(AppDimensions.spacingMedium)
            ) {
                Text(error)
            }
        }
        } // End of Box
    } // End of Scaffold padding lambda
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTopBar(
    onPauseClick: () -> Unit,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.game_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
            }
            IconButton(onClick = onPauseClick) {
                Icon(Icons.Default.Pause, contentDescription = stringResource(R.string.pause))
            }
        }
    )
}

@Composable
fun GameStatsRow(
    moves: Int,
    hintsUsed: Int,
    maxHints: Int,
    elapsedTime: Long? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (elapsedTime != null) {
            StatItem(
                icon = Icons.Default.Timer,
                label = stringResource(R.string.stat_time),
                value = formatTime(elapsedTime)
            )
        }
        StatItem(
            icon = Icons.Default.Edit,
            label = stringResource(R.string.stat_moves),
            value = moves.toString()
        )
        StatItem(
            icon = Icons.Default.Lightbulb,
            label = stringResource(R.string.stat_hints),
            value = stringResource(R.string.stat_hints_value, hintsUsed, maxHints)
        )
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    val themeColors = LocalThemeColors.current
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = themeColors.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = themeColors.text
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = themeColors.textSecondary
        )
    }
}

@Composable
fun InGameSettingsDialog(
    preferences: com.extremesudoku.presentation.settings.SettingsUiState,
    preferencesViewModel: com.extremesudoku.presentation.settings.SettingsViewModel,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.spacingMedium),
            shape = AppShapes.card
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Title
                Text(
                    text = stringResource(R.string.game_settings_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(AppDimensions.spacingMedium)
                )
                
                Divider()
                
                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = AppDimensions.dialogMaxHeight)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = AppDimensions.spacingMedium),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingExtraSmall)
                ) {
                    SettingToggle(
                        icon = Icons.Default.Timer,
                        title = stringResource(R.string.game_settings_show_timer_title),
                        subtitle = stringResource(R.string.game_settings_show_timer_description),
                        checked = preferences.showTimer,
                        onCheckedChange = { preferencesViewModel.toggleShowTimer() }
                    )

                    SettingToggle(
                        icon = Icons.Default.Lightbulb,
                        title = stringResource(R.string.game_settings_auto_check_mistakes_title),
                        subtitle = stringResource(R.string.game_settings_auto_check_mistakes_description),
                        checked = preferences.autoCheckMistakes,
                        onCheckedChange = { preferencesViewModel.toggleAutoCheckMistakes() }
                    )

                    SettingToggle(
                        icon = Icons.Default.Warning,
                        title = stringResource(R.string.game_settings_highlight_conflicts_title),
                        subtitle = stringResource(R.string.game_settings_highlight_conflicts_description),
                        checked = preferences.highlightConflicts,
                        onCheckedChange = { preferencesViewModel.toggleHighlightConflicts() }
                    )

                    SettingToggle(
                        icon = Icons.Default.Visibility,
                        title = stringResource(R.string.game_settings_highlight_same_numbers_title),
                        subtitle = stringResource(R.string.game_settings_highlight_same_numbers_description),
                        checked = preferences.highlightSameNumbers,
                        onCheckedChange = { preferencesViewModel.toggleHighlightSameNumbers() }
                    )

                    SettingToggle(
                        icon = Icons.Default.FormatListNumbered,
                        title = stringResource(R.string.game_settings_show_remaining_numbers_title),
                        subtitle = stringResource(R.string.game_settings_show_remaining_numbers_description),
                        checked = preferences.showRemainingNumbers,
                        onCheckedChange = { preferencesViewModel.toggleShowRemainingNumbers() }
                    )

                    SettingToggle(
                        icon = Icons.Default.VolumeUp,
                        title = stringResource(R.string.game_settings_vibration_title),
                        subtitle = stringResource(R.string.game_settings_vibration_description),
                        checked = preferences.vibrationEnabled,
                        onCheckedChange = { preferencesViewModel.toggleVibration() }
                    )

                    SettingToggle(
                        icon = Icons.Default.GridOn,
                        title = stringResource(R.string.game_settings_show_selected_area_title),
                        subtitle = stringResource(R.string.game_settings_show_selected_area_description),
                        checked = preferences.highlightSelectedArea,
                        onCheckedChange = { preferencesViewModel.toggleHighlightSelectedArea() }
                    )

                    SettingToggle(
                        icon = Icons.Default.TouchApp,
                        title = stringResource(R.string.game_settings_show_affected_areas_title),
                        subtitle = stringResource(R.string.game_settings_show_affected_areas_description),
                        checked = preferences.showAffectedAreas,
                        onCheckedChange = { preferencesViewModel.toggleShowAffectedAreas() }
                    )

                    SettingToggle(
                        icon = Icons.Default.Edit,
                        title = stringResource(R.string.game_settings_auto_remove_notes_title),
                        subtitle = stringResource(R.string.game_settings_auto_remove_notes_description),
                        checked = preferences.autoRemoveNotes,
                        onCheckedChange = { preferencesViewModel.toggleAutoRemoveNotes() }
                    )

                    SettingToggle(
                        icon = Icons.Default.Star,
                        title = stringResource(R.string.game_settings_show_score_and_streak_title),
                        subtitle = stringResource(R.string.game_settings_show_score_and_streak_description),
                        checked = preferences.showScoreAndStreak,
                        onCheckedChange = { preferencesViewModel.toggleShowScoreAndStreak() }
                    )

                    SettingToggle(
                        icon = Icons.Default.Palette,
                        title = stringResource(R.string.game_settings_colorize_numbers_title),
                        subtitle = stringResource(R.string.game_settings_colorize_numbers_description),
                        checked = preferences.colorizeNumbers,
                        onCheckedChange = { preferencesViewModel.toggleColorizeNumbers() }
                    )

                    SettingToggle(
                        icon = Icons.Default.MusicNote,
                        title = stringResource(R.string.game_settings_sound_effects_title),
                        subtitle = stringResource(R.string.game_settings_sound_effects_description),
                        checked = preferences.soundEnabled,
                        onCheckedChange = { preferencesViewModel.toggleSound() }
                    )
                }
                
                Divider()
                
                // Close Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimensions.spacingMedium),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}
