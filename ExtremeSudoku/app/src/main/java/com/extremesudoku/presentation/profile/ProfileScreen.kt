package com.extremesudoku.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.LocalThemeColors
import com.extremesudoku.utils.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val themeColors = LocalThemeColors.current
    
    LaunchedEffect(uiState.navigateToAuth) {
        if (uiState.navigateToAuth) {
            onNavigateToAuth()
            viewModel.onNavigationComplete()
        }
    }
    Scaffold(
        containerColor = themeColors.background,
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppDimensions.spacingMedium),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Guest Mode Warning
                if (uiState.isGuestMode) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = AppDimensions.spacingMedium),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(AppDimensions.spacingMedium)
                        ) {
                            Text(
                                text = "⚠️ Guest Mode",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))
                            Text(
                                text = "Your progress is saved locally. Create an account to sync across devices!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
                            Button(
                                onClick = onNavigateToAuth,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Create Account / Sign In")
                            }
                        }
                    }
                }
                
                // Profile Header
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimensions.dialogPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(AppDimensions.profileAvatarSize),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
                        Text(
                            text = uiState.user?.displayName ?: "Player",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        uiState.user?.email?.takeIf { it.isNotEmpty() }?.let { email ->
                            Spacer(modifier = Modifier.height(AppDimensions.spacingExtraSmall))
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // User Stats
                uiState.userStats?.let { stats ->
                    Text(
                        text = "Statistics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
                    
                    DetailedStatCard(
                        title = "Games",
                        items = listOf(
                            "Played" to stats.gamesPlayed.toString(),
                            "Completed" to stats.gamesCompleted.toString(),
                            "Completion Rate" to "${if (stats.gamesPlayed > 0) (stats.gamesCompleted * 100 / stats.gamesPlayed) else 0}%"
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
                    
                    DetailedStatCard(
                        title = "Time",
                        items = listOf(
                            "Best Time" to formatTime(stats.bestTime),
                            "Average Time" to formatTime(stats.averageTime),
                            "Total Time" to formatTime(stats.totalTime)
                        )
                    )
                }
            }
            
            // Sign Out Button
            Button(
                onClick = { viewModel.onSignOutClicked() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Sign Out")
            }
        }
    }
}

@Composable
fun DetailedStatCard(
    title: String,
    items: List<Pair<String, String>>
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(AppDimensions.spacingMedium)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (items.last() != (label to value)) {
                    Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))
                }
            }
        }
    }
}
