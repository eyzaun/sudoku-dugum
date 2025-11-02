package com.extremesudoku.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.extremesudoku.R
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
                title = { Text(stringResource(R.string.profile)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
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
                                text = stringResource(R.string.profile_guest_mode_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))
                            Text(
                                text = stringResource(R.string.profile_guest_mode_message),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
                            Button(
                                onClick = onNavigateToAuth,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.profile_guest_mode_action))
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
                            text = uiState.user?.displayName ?: stringResource(R.string.profile_default_display_name),
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
                        text = stringResource(R.string.profile_statistics_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))

                    val completionRate = if (stats.gamesPlayed > 0) {
                        (stats.gamesCompleted * 100 / stats.gamesPlayed)
                    } else {
                        0
                    }
                    
                    DetailedStatCard(
                        title = stringResource(R.string.profile_statistics_games_title),
                        items = listOf(
                            stringResource(R.string.games_played) to stats.gamesPlayed.toString(),
                            stringResource(R.string.games_completed) to stats.gamesCompleted.toString(),
                            stringResource(R.string.profile_completion_rate_label) to stringResource(
                                R.string.profile_completion_rate_value,
                                completionRate
                            )
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
                    
                    DetailedStatCard(
                        title = stringResource(R.string.profile_statistics_time_title),
                        items = listOf(
                            stringResource(R.string.best_time) to formatTime(stats.bestTime),
                            stringResource(R.string.average_time) to formatTime(stats.averageTime),
                            stringResource(R.string.total_time) to formatTime(stats.totalTime)
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
                Text(stringResource(R.string.sign_out))
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
