package com.extremesudoku.presentation.pvp.result

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.extremesudoku.data.models.pvp.PvpMode
import com.extremesudoku.presentation.theme.LocalThemeColors
import com.extremesudoku.utils.formatTime
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.AppTypography

/**
 * PvP Match Result Screen
 * Oyun bittiğinde gösterilen sonuç ekranı
 */
@Composable
fun PvpResultScreen(
    matchId: String,
    viewModel: PvpResultViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onPlayAgain: () -> Unit
) {
    @Suppress("UNUSED_VARIABLE")
    val themeColors = LocalThemeColors.current
    val resultState by viewModel.resultState.collectAsState()
    
    LaunchedEffect(matchId) {
        viewModel.loadResult(matchId)
    }
    
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = resultState) {
                is PvpResultState.Loading -> {
                    CircularProgressIndicator()
                }
                
                is PvpResultState.Success -> {
                    ResultContent(
                        result = state,
                        onNavigateToHome = onNavigateToHome,
                        onPlayAgain = onPlayAgain
                    )
                }
                
                is PvpResultState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onNavigateToHome = onNavigateToHome
                    )
                }
            }
        }
    }
}

@Composable
fun ResultContent(
    result: PvpResultState.Success,
    onNavigateToHome: () -> Unit,
    onPlayAgain: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppDimensions.dialogPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Winner/Loser Icon & Title
        when {
            // İPTAL EDİLDİYSE: Skordan bağımsız sadece kazandınız/kaybettiniz
            result.isCancelled -> {
                if (result.isWinner) {
                    Text(
                        text = "🏆",
                        fontSize = AppTypography.fontSizeDisplay * 2.5f
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
                    Text(
                        text = "KAZANDIN!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.achievementGold
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))
                    Text(
                        text = "Rakip oyundan ayrıldı",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                } else {
                    Text(
                        text = "😢",
                        fontSize = AppTypography.fontSizeDisplay * 2.5f
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
                    Text(
                        text = "Kaybettin",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))
                    Text(
                        text = "Oyundan ayrıldınız",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            // NORMAL BİTİŞ: Skordan bak
            result.isDraw -> {
                Text(
                    text = "🤝",
                    fontSize = AppTypography.fontSizeDisplay * 2.5f
                )
                Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
                Text(
                    text = "BERABERE!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            result.isWinner -> {
                Text(
                    text = "🏆",
                    fontSize = AppTypography.fontSizeDisplay * 2.5f
                )
                Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
                Text(
                    text = "KAZANDIN!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.achievementGold
                )
            }
            else -> {
                Text(
                    text = "😢",
                    fontSize = AppTypography.fontSizeDisplay * 2.5f
                )
                Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
                Text(
                    text = "Kaybettin",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        
        Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))
        
        // Mode bilgisi
        Text(
            text = result.mode.displayName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(AppDimensions.spacingExtraLarge))
        
        // Stats Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.homeItemSpacing)
        ) {
            // Player 1 Card
            PlayerStatsCard(
                name = result.myName,
                score = result.myScore,
                time = result.myTime,
                accuracy = result.myAccuracy,
                isWinner = result.isWinner,
                modifier = Modifier.weight(1f)
            )
            
            // VS
            Text(
                text = "VS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            
            // Player 2 Card
            PlayerStatsCard(
                name = result.opponentName,
                score = result.opponentScore,
                time = result.opponentTime,
                accuracy = result.opponentAccuracy,
                isWinner = !result.isWinner && !result.isDraw,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(AppDimensions.spacingExtraLarge))
        
        // Detailed Stats
        DetailedStatsSection(result)
        
        Spacer(modifier = Modifier.height(AppDimensions.spacingExtraLarge))
        
        // Action Buttons
        Button(
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(AppDimensions.spacingSmall))
            Text("Tekrar Oyna")
        }
        
        Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))
        
        OutlinedButton(
            onClick = onNavigateToHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Home, contentDescription = null)
            Spacer(modifier = Modifier.width(AppDimensions.spacingSmall))
            Text("Ana Menü")
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
fun PlayerStatsCard(
    name: String,
    score: Int,
    time: Long,
    accuracy: Float,
    isWinner: Boolean,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalThemeColors.current
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isWinner) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isWinner) {
            androidx.compose.foundation.BorderStroke(AppDimensions.pvpBorderWidth, themeColors.achievementGold)
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier.padding(AppDimensions.homeItemSpacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isWinner) {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = themeColors.achievementGold,
                    modifier = Modifier.size(AppDimensions.iconSizeMedium)
                )
                Spacer(modifier = Modifier.height(AppDimensions.spacingExtraSmall))
            }
            
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))
            
            Text(
                text = "$score",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "puan",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DetailedStatsSection(result: PvpResultState.Success) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(AppDimensions.spacingMedium)
        ) {
            Text(
                text = "Detaylı İstatistikler",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(AppDimensions.homeItemSpacing))
            
            // Time comparison
            StatRow(
                icon = Icons.Default.Timer,
                label = "Süre",
                myValue = formatTime(result.myTime / 1000),
                opponentValue = formatTime(result.opponentTime / 1000),
                myIsBetter = result.myTime < result.opponentTime
            )
            
            Divider(modifier = Modifier.padding(vertical = AppDimensions.spacingSmall))
            
            // Score comparison
            StatRow(
                icon = Icons.Default.Star,
                label = "Skor",
                myValue = "${result.myScore}",
                opponentValue = "${result.opponentScore}",
                myIsBetter = result.myScore > result.opponentScore
            )
            
            Divider(modifier = Modifier.padding(vertical = AppDimensions.spacingSmall))
            
            // Accuracy comparison
            StatRow(
                icon = Icons.Default.CheckCircle,
                label = "Doğruluk",
                myValue = "${result.myAccuracy.toInt()}%",
                opponentValue = "${result.opponentAccuracy.toInt()}%",
                myIsBetter = result.myAccuracy > result.opponentAccuracy
            )
        }
    }
}

@Composable
fun StatRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    myValue: String,
    opponentValue: String,
    myIsBetter: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Icon & Label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(AppDimensions.iconSizeSmall),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(AppDimensions.spacingSmall))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // My Value
        Text(
            text = myValue,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (myIsBetter) FontWeight.Bold else FontWeight.Normal,
            color = if (myIsBetter) MaterialTheme.colorScheme.primary 
                   else MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.width(AppDimensions.spacingMedium))
        
        // Opponent Value
        Text(
            text = opponentValue,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (!myIsBetter) FontWeight.Bold else FontWeight.Normal,
            color = if (!myIsBetter) MaterialTheme.colorScheme.secondary 
                   else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ErrorContent(
    message: String,
    onNavigateToHome: () -> Unit
) {
    Column(
        modifier = Modifier.padding(AppDimensions.spacingExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(AppDimensions.dialogIconSize),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
        
        Text(
            text = "Hata",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(AppDimensions.dialogPadding))
        
        Button(onClick = onNavigateToHome) {
            Text("Ana Menüye Dön")
        }
    }
}
