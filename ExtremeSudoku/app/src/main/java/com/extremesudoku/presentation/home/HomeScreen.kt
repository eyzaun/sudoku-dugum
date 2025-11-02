package com.extremesudoku.presentation.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.extremesudoku.R
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.AppShapes
import com.extremesudoku.presentation.theme.LocalThemeColors
import com.extremesudoku.utils.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToNewGame: (String) -> Unit,
    onNavigateToContinueGame: (String) -> Unit,
    onNavigateToPvp: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val themeColors = LocalThemeColors.current
    
    // Yeni oyun için navigation
    LaunchedEffect(uiState.navigateToNewGame) {
        uiState.navigateToNewGame?.let { sudokuId ->
            onNavigateToNewGame(sudokuId)
            viewModel.onNavigationComplete()
        }
    }
    
    // Devam eden oyun için navigation
    LaunchedEffect(uiState.navigateToContinueGame) {
        uiState.navigateToContinueGame?.let { gameId ->
            onNavigateToContinueGame(gameId)
            viewModel.onNavigationComplete()
        }
    }
    
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
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = stringResource(R.string.profile))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(AppDimensions.spacingMedium)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.homeItemSpacing)
            ) {
                // User Stats Card
                uiState.userStats?.let { stats ->
                    UserStatsCard(stats)
                }
                
                // Active/Saved Games Section
                if (uiState.activeGames.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.continue_playing),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = themeColors.cardBackground
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(AppDimensions.spacingMedium),
                            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
                        ) {
                            val gamesToShow = if (uiState.showAllGames) uiState.activeGames else uiState.activeGames.take(3)
                            
                            gamesToShow.forEach { gameState ->
                                SavedGameItem(
                                    gameState = gameState,
                                    onClick = { viewModel.onContinueGameClicked(gameState.gameId) },
                                    onDelete = { viewModel.deleteGame(gameState.gameId) }
                                )
                                if (gameState != gamesToShow.last()) {
                                    Divider()
                                }
                            }
                            
                            if (uiState.activeGames.size > 3) {
                                TextButton(
                                    onClick = { viewModel.toggleShowAllGames() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (uiState.showAllGames) {
                                        Text(stringResource(R.string.show_less))
                                        Spacer(modifier = Modifier.width(AppDimensions.spacingExtraSmall))
                                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, modifier = Modifier.size(AppDimensions.iconSizeSmall))
                                    } else {
                                        Text(stringResource(R.string.show_all_saved_games, uiState.activeGames.size))
                                        Spacer(modifier = Modifier.width(AppDimensions.spacingExtraSmall))
                                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(AppDimensions.iconSizeSmall))
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))
                }
                
                // Difficulty Selection
                Text(
                    text = stringResource(R.string.new_game),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
                ) {
                    DifficultyButton(
                        textRes = R.string.difficulty_easy,
                        modifier = Modifier.weight(1f),
                        color = themeColors.difficultyEasy,
                        onClick = { viewModel.onNewGameClicked("easy") }
                    )
                    DifficultyButton(
                        textRes = R.string.difficulty_medium,
                        modifier = Modifier.weight(1f),
                        color = themeColors.difficultyMedium,
                        onClick = { viewModel.onNewGameClicked("medium") }
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
                ) {
                    DifficultyButton(
                        textRes = R.string.difficulty_hard,
                        modifier = Modifier.weight(1f),
                        color = themeColors.difficultyHard,
                        onClick = { viewModel.onNewGameClicked("hard") }
                    )
                    DifficultyButton(
                        textRes = R.string.difficulty_expert,
                        modifier = Modifier.weight(1f),
                        color = themeColors.difficultyExpert,
                        onClick = { viewModel.onNewGameClicked("expert") }
                    )
                }
                
                // Daily Challenge
                if (uiState.isDailyChallengeAvailable) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.onDailyChallengeClicked() },
                        colors = CardDefaults.cardColors(
                            containerColor = themeColors.cardBackground
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppDimensions.spacingMedium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                modifier = Modifier.size(AppDimensions.iconSizeExtraLarge),
                                tint = themeColors.secondary
                            )
                            Spacer(modifier = Modifier.width(AppDimensions.spacingMedium))
                            Column {
                                Text(
                                    text = stringResource(R.string.daily_challenge),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(R.string.daily_challenge_description),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                
                // PvP Mode Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToPvp,
                    colors = CardDefaults.cardColors(
                        containerColor = themeColors.cardBackground
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimensions.spacingMedium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            modifier = Modifier.size(AppDimensions.iconSizeExtraLarge),
                            tint = themeColors.primary
                        )
                        Spacer(modifier = Modifier.width(AppDimensions.spacingMedium))
                        Column {
                            Text(
                                text = stringResource(R.string.pvp_mode),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.pvp_mode_home_description),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            
                // Leaderboard Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToLeaderboard
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimensions.spacingLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Leaderboard,
                            contentDescription = null,
                            modifier = Modifier.size(AppDimensions.iconSizeExtraLarge)
                        )
                        Spacer(modifier = Modifier.width(AppDimensions.spacingMedium))
                        Text(
                            text = stringResource(R.string.leaderboard),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultyButton(
    @StringRes textRes: Int,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(AppDimensions.difficultyButtonHeight),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(textRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedGameItem(
    gameState: com.extremesudoku.data.models.GameState,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val themeColors = LocalThemeColors.current
    val difficultyLabel = when (gameState.difficulty.lowercase()) {
        "easy" -> stringResource(R.string.difficulty_easy)
        "medium" -> stringResource(R.string.difficulty_medium)
        "hard" -> stringResource(R.string.difficulty_hard)
        "expert" -> stringResource(R.string.difficulty_expert)
        else -> stringResource(R.string.difficulty_medium)
    }
    
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = themeColors.cardBackground
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.savedGameItemPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.saved_game_in_progress),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = themeColors.text
                    )
                    // Difficulty badge
                    val difficultyColor = when (gameState.difficulty.lowercase()) {
                        "easy" -> themeColors.difficultyEasy
                        "medium" -> themeColors.difficultyMedium
                        "hard" -> themeColors.difficultyHard
                        "expert" -> themeColors.difficultyExpert
                        else -> themeColors.difficultyMedium
                    }
                    Surface(
                        color = difficultyColor.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = difficultyLabel.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = difficultyColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = AppDimensions.spacingSmall, vertical = AppDimensions.spacingExtraSmall)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(AppDimensions.spacingExtraSmall))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingExtraSmall)
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(AppDimensions.iconSizeSmall),
                            tint = themeColors.iconTint
                        )
                        Text(
                            text = formatTime(gameState.elapsedTime),
                            style = MaterialTheme.typography.bodySmall,
                            color = themeColors.textSecondary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingExtraSmall)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(AppDimensions.iconSizeSmall),
                            tint = themeColors.iconTint
                        )
                        Text(
                            text = stringResource(R.string.saved_game_moves, gameState.moves),
                            style = MaterialTheme.typography.bodySmall,
                            color = themeColors.textSecondary
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                onDelete?.let { deleteAction ->
                    IconButton(onClick = deleteAction) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = themeColors.wrongCell
                        )
                    }
                }
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = stringResource(R.string.continue_label),
                    modifier = Modifier.size(AppDimensions.leaderboardIconSize),
                    tint = themeColors.primary
                )
            }
        }
    }
}

@Composable
fun UserStatsCard(stats: com.extremesudoku.data.models.UserStats) {
    val themeColors = LocalThemeColors.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = themeColors.cardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(AppDimensions.spacingMedium)
        ) {
            Text(
                text = stringResource(R.string.your_stats),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = themeColors.text
            )
            Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn(stringResource(R.string.stats_played), stats.gamesPlayed.toString())
                StatColumn(stringResource(R.string.stats_completed), stats.gamesCompleted.toString())
                StatColumn(stringResource(R.string.best_time), formatTime(stats.bestTime))
                StatColumn(stringResource(R.string.stats_streak), "${stats.currentStreak}")
            }
        }
    }
}

@Composable
fun StatColumn(label: String, value: String) {
    val themeColors = LocalThemeColors.current
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
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
