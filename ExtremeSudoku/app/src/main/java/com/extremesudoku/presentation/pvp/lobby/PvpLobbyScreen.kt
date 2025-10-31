package com.extremesudoku.presentation.pvp.lobby

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.extremesudoku.data.models.pvp.PvpMode
import com.extremesudoku.presentation.theme.LocalThemeColors
import com.extremesudoku.presentation.theme.AppDimensions

/**
 * PvP Lobby (Matchmaking) Ekranı
 * Kullanıcı rakip bulunana kadar bekler
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PvpLobbyScreen(
    mode: PvpMode,
    viewModel: PvpLobbyViewModel = hiltViewModel(),
    onNavigateToGame: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Otomatik matchmaking başlat
    LaunchedEffect(mode) {
        if (uiState is PvpLobbyState.Idle) {
            viewModel.startMatchmaking(mode)
        }
    }
    
    // Eşleşme bulununca oyuna git
    LaunchedEffect(uiState) {
        if (uiState is PvpLobbyState.MatchFound) {
            val matchId = (uiState as PvpLobbyState.MatchFound).matchId
            onNavigateToGame(matchId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rakip Aranıyor...") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.cancelMatchmaking()
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is PvpLobbyState.Idle -> {
                    // Başlangıç durumu
                    CircularProgressIndicator()
                }
                
                is PvpLobbyState.Searching -> {
                    SearchingContent(
                        mode = state.mode,
                        onCancel = {
                            viewModel.cancelMatchmaking()
                            onNavigateBack()
                        }
                    )
                }
                
                is PvpLobbyState.MatchFound -> {
                    MatchFoundContent(matchId = state.matchId)
                }
                
                is PvpLobbyState.Cancelled -> {
                    LaunchedEffect(Unit) {
                        onNavigateBack()
                    }
                }
                
                is PvpLobbyState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { viewModel.startMatchmaking(mode) },
                        onDismiss = {
                            viewModel.clearError()
                            onNavigateBack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchingContent(
    mode: PvpMode,
    onCancel: () -> Unit
) {
    // Rotating animation
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppDimensions.spacingExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimensions.dialogPadding)
    ) {
        // Animasyonlu progress indicator
        Box(
            modifier = Modifier.size(AppDimensions.profileAvatarSize + AppDimensions.iconSizeExtraLarge),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(rotation),
                strokeWidth = AppDimensions.gridConflictLineWidth * 3
            )
            
            Text(
                text = "🎮",
                style = MaterialTheme.typography.displayMedium
            )
        }
        
        // Başlık
        Text(
            text = "Rakip Aranıyor...",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // Mod bilgisi
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(AppDimensions.spacingMedium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Mod: ${mode.displayName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))
                
                Text(
                    text = mode.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Bekleme mesajı
        Text(
            text = "Senin seviyende bir rakip arıyoruz...",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
        
        // İptal butonu
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Close, contentDescription = null)
            Spacer(modifier = Modifier.width(AppDimensions.spacingSmall))
            Text("Aramayı İptal Et")
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
fun MatchFoundContent(matchId: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppDimensions.spacingExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimensions.dialogPadding)
    ) {
        // Success icon
        Text(
            text = "🎉",
            style = MaterialTheme.typography.displayLarge
        )
        
        Text(
            text = "Rakip Bulundu!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Oyuna yönlendiriliyorsun...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppDimensions.spacingExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimensions.dialogPadding)
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.displayLarge
        )
        
        Text(
            text = "Bir Hata Oluştu",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Text("İptal")
            }
            
            Button(
                onClick = onRetry,
                modifier = Modifier.weight(1f)
            ) {
                Text("Tekrar Dene")
            }
        }
    }
}
