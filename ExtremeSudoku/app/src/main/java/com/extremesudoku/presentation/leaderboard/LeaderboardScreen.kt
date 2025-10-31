package com.extremesudoku.presentation.leaderboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.extremesudoku.data.models.LeaderboardEntry
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.LocalThemeColors
import com.extremesudoku.utils.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        containerColor = themeColors.background,
        topBar = {
            TopAppBar(
                title = { Text("Leaderboard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onRefresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
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
        } else if (uiState.entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No leaderboard data yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                itemsIndexed(uiState.entries) { index, entry ->
                    LeaderboardItem(
                        rank = index + 1,
                        entry = entry,
                        isTopThree = index < 3
                    )
                    if (index < uiState.entries.lastIndex) {
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardItem(
    rank: Int,
    entry: LeaderboardEntry,
    isTopThree: Boolean
) {
    val themeColors = LocalThemeColors.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppDimensions.spacingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank
        Box(
            modifier = Modifier.size(AppDimensions.leaderboardRankSize),
            contentAlignment = Alignment.Center
        ) {
            if (isTopThree) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = when (rank) {
                        1 -> themeColors.achievementGold
                        2 -> themeColors.achievementSilver
                        3 -> themeColors.achievementBronze
                        else -> themeColors.textSecondary
                    },
                    modifier = Modifier.size(AppDimensions.leaderboardIconSize)
                )
            } else {
                Text(
                    text = rank.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.width(AppDimensions.spacingMedium))
        
        // User info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.username.ifEmpty { "Player $rank" },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${entry.gamesCompleted} games completed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Best time
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatTime(entry.bestTime),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "best time",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
