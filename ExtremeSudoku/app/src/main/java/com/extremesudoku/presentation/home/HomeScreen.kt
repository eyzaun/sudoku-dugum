package com.extremesudoku.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
                title = { Text("Extreme Sudoku") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
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
                        text = "Continue Playing",
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
                                        Text("Show less")
                                        Spacer(modifier = Modifier.width(AppDimensions.spacingExtraSmall))
                                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, modifier = Modifier.size(AppDimensions.iconSizeSmall))
                                    } else {
                                        Text("See all ${uiState.activeGames.size} saved games")
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
                    text = "New Game",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
                ) {
                    DifficultyButton(
                        text = "Easy",
                        modifier = Modifier.weight(1f),
                        color = themeColors.difficultyEasy,
                        onClick = { viewModel.onNewGameClicked("easy") }
                    )
                    DifficultyButton(
                        text = "Medium",
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
                        text = "Hard",
                        modifier = Modifier.weight(1f),
                        color = themeColors.difficultyHard,
                        onClick = { viewModel.onNewGameClicked("hard") }
                    )
                    DifficultyButton(
                        text = "Expert",
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
                                    text = "Daily Challenge",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Complete today's special puzzle",
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
                                text = "🔥 PvP Mode",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Challenge players online!",
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
                            text = "Leaderboard",
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
    text: String,
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
                text = text,
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
                        text = "Game in progress",
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
                            text = gameState.difficulty.uppercase(),
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
                            text = "${gameState.moves} moves",
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
                            contentDescription = "Delete",
                            tint = themeColors.wrongCell
                        )
                    }
                }
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Continue",
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
                text = "Your Stats",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = themeColors.text
            )
            Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn("Played", stats.gamesPlayed.toString())
                StatColumn("Completed", stats.gamesCompleted.toString())
                StatColumn("Best Time", formatTime(stats.bestTime))
                StatColumn("Streak", "${stats.currentStreak}🔥")
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
