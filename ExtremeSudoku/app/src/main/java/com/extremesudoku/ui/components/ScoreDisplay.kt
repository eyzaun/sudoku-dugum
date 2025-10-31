package com.extremesudoku.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.extremesudoku.R
import com.extremesudoku.data.models.scoring.GameScore
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.AppShapes
import com.extremesudoku.presentation.theme.LocalThemeColors

/**
 * Real-time skor gösterimi için animasyonlu component
 * Tetris-inspired visual feedback ile skor değişimlerini gösterir
 */
@Composable
fun ScoreDisplay(
    score: GameScore,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val themeColors = LocalThemeColors.current
    
    // Skor değişim animasyonu için trigger
    var lastScore by remember { mutableStateOf(score.finalScore) }
    val scoreChanged = score.finalScore != lastScore
    
    LaunchedEffect(score.finalScore) {
        lastScore = score.finalScore
    }
    
    // Puls efekti için animasyon
    val pulseScale by animateFloatAsState(
        targetValue = if (scoreChanged) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "score_pulse"
    )
    
    // Renk geçişi streak seviyesine göre
    val scoreColor by animateColorAsState(
        targetValue = when {
            score.currentStreak >= 10 -> themeColors.streakHotOrange
            score.currentStreak >= 5 -> themeColors.streakGold
            score.currentStreak >= 3 -> themeColors.streakTurquoise
            else -> themeColors.primary
        },
        animationSpec = tween(300),
        label = "score_color"
    )
    
    if (compact) {
        CompactScoreView(
            score = score,
            scoreColor = scoreColor,
            pulseScale = pulseScale,
            modifier = modifier
        )
    } else {
        ExpandedScoreView(
            score = score,
            scoreColor = scoreColor,
            pulseScale = pulseScale,
            modifier = modifier
        )
    }
}

@Composable
private fun CompactScoreView(
    score: GameScore,
    scoreColor: Color,
    pulseScale: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    )
                ),
                shape = AppShapes.card
            )
            .padding(horizontal = AppDimensions.spacingMedium, vertical = AppDimensions.spacingSmall),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Score icon
        Icon(
            painter = painterResource(id = R.drawable.ic_trophy),
            contentDescription = "Score",
            tint = scoreColor,
            modifier = Modifier.size(AppDimensions.scoreIconSizeSmall)
        )
        
        // Animated score number
        AnimatedContent(
            targetState = score.finalScore,
            transitionSpec = {
                slideInVertically { -it } togetherWith slideOutVertically { it }
            },
            label = "score_animation"
        ) { targetScore ->
            Text(
                text = formatScore(targetScore),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = scoreColor,
                modifier = Modifier.scale(pulseScale)
            )
        }
    }
}

@Composable
private fun ExpandedScoreView(
    score: GameScore,
    scoreColor: Color,
    pulseScale: Float,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalThemeColors.current
    
    Column(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                ),
                shape = AppShapes.card
            )
            .padding(AppDimensions.spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
    ) {
        // Ana skor
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_trophy),
                contentDescription = "Score",
                tint = scoreColor,
                modifier = Modifier.size(AppDimensions.scoreIconSize)
            )
            
            AnimatedContent(
                targetState = score.finalScore,
                transitionSpec = {
                    slideInVertically { -it } togetherWith slideOutVertically { it }
                },
                label = "score_animation"
            ) { targetScore ->
                Text(
                    text = formatScore(targetScore),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = scoreColor,
                    modifier = Modifier.scale(pulseScale)
                )
            }
        }
        
        // Skor breakdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScoreBreakdownItem(
                label = "Base",
                value = score.basePoints,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            if (score.streakBonus > 0) {
                ScoreBreakdownItem(
                    label = "Streak",
                    value = score.streakBonus,
                    color = themeColors.streakGold
                )
            }
            
            if (score.timeBonus > 0) {
                ScoreBreakdownItem(
                    label = "Time",
                    value = score.timeBonus,
                    color = themeColors.streakTurquoise
                )
            }
        }
        
        // Accuracy ve statistics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = "Accuracy",
                value = "${(score.accuracy * 100).toInt()}%",
                color = if (score.accuracy >= 0.9f) themeColors.accuracyHigh else MaterialTheme.colorScheme.onSurface
            )
            
            StatItem(
                label = "Moves",
                value = "${score.totalMoves}",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            StatItem(
                label = "Best Streak",
                value = "${score.maxStreak}",
                color = themeColors.streakHotOrange
            )
        }
    }
}

@Composable
private fun ScoreBreakdownItem(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = "+$value",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/**
 * Skoru formatla: 1000 -> 1,000 | 1000000 -> 1.0M
 */
private fun formatScore(score: Int): String {
    return when {
        score >= 1_000_000 -> String.format("%.1fM", score / 1_000_000.0)
        score >= 1_000 -> String.format("%,d", score)
        else -> score.toString()
    }
}
