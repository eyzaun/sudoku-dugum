package com.extremesudoku.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.extremesudoku.R
import com.extremesudoku.data.models.scoring.GameScore
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.AppShapes
import com.extremesudoku.presentation.theme.LocalThemeColors
import kotlinx.coroutines.delay

/**
 * Oyun tamamlandığında detaylı skor breakdown gösteren dialog
 * Tetris-style score presentation with badges and achievements
 */
@Composable
fun GameCompleteDialog(
    score: GameScore,
    isNewHighScore: Boolean = false,
    earnedBadges: List<Badge> = emptyList(),
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation states
    var showContent by remember { mutableStateOf(false) }
    var showScore by remember { mutableStateOf(false) }
    var showBreakdown by remember { mutableStateOf(false) }
    var showBadges by remember { mutableStateOf(false) }
    
    // Trigger animations sequentially
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
        delay(300)
        showScore = true
        delay(500)
        showBreakdown = true
        delay(300)
        showBadges = true
    }
    
    Dialog(onDismissRequest = onMainMenu) {
        AnimatedVisibility(
            visible = showContent,
            enter = scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn()
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(max = AppDimensions.gameCompleteMaxHeight),
                shape = AppShapes.gameComplete,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(AppDimensions.dialogPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.gameCompleteSpacing)
                ) {
                    // Victory header
                    VictoryHeader(
                        isPerfect = score.perfectGame,
                        isNewHighScore = isNewHighScore
                    )
                    
                    // Main score display
                    AnimatedVisibility(
                        visible = showScore,
                        enter = slideInVertically { it } + fadeIn()
                    ) {
                        MainScoreDisplay(
                            score = score,
                            isNewHighScore = isNewHighScore
                        )
                    }
                    
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    
                    // Score breakdown
                    AnimatedVisibility(
                        visible = showBreakdown,
                        enter = fadeIn() + expandVertically()
                    ) {
                        ScoreBreakdown(score = score)
                    }
                    
                    // Statistics
                    GameStatistics(score = score)
                    
                    // Earned badges
                    if (earnedBadges.isNotEmpty()) {
                        AnimatedVisibility(
                            visible = showBadges,
                            enter = fadeIn() + expandVertically()
                        ) {
                            EarnedBadgesSection(badges = earnedBadges)
                        }
                    }
                    
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    
                    // Action buttons
                    ActionButtons(
                        onPlayAgain = onPlayAgain,
                        onMainMenu = onMainMenu,
                        onShare = onShare
                    )
                }
            }
        }
    }
}

@Composable
private fun VictoryHeader(
    isPerfect: Boolean,
    isNewHighScore: Boolean
) {
    // Celebration emoji animation
    val infiniteTransition = rememberInfiniteTransition(label = "trophy_pulse")
    @Suppress("UNUSED_VARIABLE")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val themeColors = LocalThemeColors.current
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
    ) {
        Text(
            text = when {
                isPerfect -> "★"
                isNewHighScore -> "↑"
                else -> "✓"
            },
            fontSize = AppDimensions.dialogIconSize.value.sp
        )
        
        Text(
            text = when {
                isPerfect -> "PERFECT GAME!"
                isNewHighScore -> "NEW HIGH SCORE!"
                else -> "PUZZLE COMPLETE!"
            },
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = when {
                isPerfect -> themeColors.achievementGold
                isNewHighScore -> themeColors.streakHotOrange
                else -> MaterialTheme.colorScheme.primary
            },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MainScoreDisplay(
    score: GameScore,
    isNewHighScore: Boolean
) {
    val themeColors = LocalThemeColors.current
    
    // Counter animation for final score
    var displayedScore by remember { mutableStateOf(0) }
    
    LaunchedEffect(score.finalScore) {
        val increment = score.finalScore / 50
        while (displayedScore < score.finalScore) {
            displayedScore = (displayedScore + increment).coerceAtMost(score.finalScore)
            delay(20)
        }
        displayedScore = score.finalScore
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall),
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                    )
                ),
                shape = AppShapes.card
            )
            .padding(AppDimensions.dialogPadding)
    ) {
        Text(
            text = stringResource(R.string.game_complete_final_score),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Text(
            text = formatScore(displayedScore),
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Black
            ),
            color = if (isNewHighScore) themeColors.streakHotOrange else MaterialTheme.colorScheme.primary
        )
        
        // Difficulty multiplier indicator
        if (score.difficultyMultiplier > 1.0f) {
            Text(
                text = stringResource(R.string.game_complete_difficulty_multiplier, score.difficultyMultiplier),
                style = MaterialTheme.typography.bodyMedium,
                color = themeColors.achievementGold
            )
        }
    }
}

@Composable
private fun ScoreBreakdown(score: GameScore) {
    val themeColors = LocalThemeColors.current
    
    Column(
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
    ) {
        Text(
            text = stringResource(R.string.game_complete_score_breakdown),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        
        // Base points
        BreakdownRow(
            label = stringResource(R.string.game_complete_correct_moves),
            value = score.basePoints,
            icon = "✓",
            color = themeColors.accuracyHigh
        )
        
        // Streak bonus
        if (score.streakBonus > 0) {
            BreakdownRow(
                label = stringResource(R.string.game_complete_streak_bonus, score.maxStreak),
                value = score.streakBonus,
                icon = "S",
                color = themeColors.streakOrange
            )
        }
        
        // Time bonus
        if (score.timeBonus > 0) {
            BreakdownRow(
                label = stringResource(R.string.game_complete_speed_bonus),
                value = score.timeBonus,
                icon = "T",
                color = themeColors.streakCyan
            )
        }
        
        // Completion bonuses
        val totalCompletionBonus = score.completionBonuses
        if (totalCompletionBonus > 0) {
            BreakdownRow(
                label = stringResource(R.string.game_complete_completion_bonuses),
                value = totalCompletionBonus,
                icon = "B",
                color = themeColors.streakPurple
            )
        }
        
        // Special bonuses
        if (score.perfectGame) {
            BreakdownRow(
                label = stringResource(R.string.game_complete_perfect_game),
                value = 10000,
                icon = "★",
                color = themeColors.achievementGold
            )
        }
        
        if (score.playedWithoutNotes) {
            BreakdownRow(
                label = stringResource(R.string.game_complete_no_notes),
                value = 5000,
                icon = "N",
                color = themeColors.accuracyMedium
            )
        }
        
        // Penalties
        if (score.penalties < 0) {
            BreakdownRow(
                label = stringResource(R.string.game_complete_penalties),
                value = score.penalties,
                icon = "X",
                color = themeColors.accuracyLow
            )
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: Int,
    icon: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = icon, fontSize = AppDimensions.scoreIconSize.value.sp)
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
        
        Text(
            text = if (value >= 0) "+$value" else value.toString(),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
    }
}

@Composable
private fun GameStatistics(score: GameScore) {
    val themeColors = LocalThemeColors.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatCard(
            label = stringResource(R.string.game_complete_accuracy_label),
            value = "${(score.accuracy * 100).toInt()}%",
            icon = "A",
            color = if (score.accuracy >= 0.9f) themeColors.accuracyHigh else themeColors.accuracyMedium
        )
        
        StatCard(
            label = stringResource(R.string.game_complete_time_label),
            value = formatTime(score.elapsedTimeMs),
            icon = "T",
            color = MaterialTheme.colorScheme.primary
        )
        
        StatCard(
            label = stringResource(R.string.game_complete_best_streak_label),
            value = "${score.maxStreak}",
            icon = "S",
            color = themeColors.streakHotOrange
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingExtraSmall),
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = AppShapes.card
            )
            .padding(AppDimensions.spacingMedium)
    ) {
        Text(text = icon, fontSize = AppDimensions.scoreIconSize.value.sp)
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
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

@Composable
private fun EarnedBadgesSection(badges: List<Badge>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
    ) {
        Text(
            text = stringResource(R.string.game_complete_badges_earned),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
        ) {
            items(badges) { badge ->
                BadgeCard(badge = badge)
            }
        }
    }
}

@Composable
private fun BadgeCard(badge: Badge) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingExtraSmall),
        modifier = Modifier
            .width(AppDimensions.badgeWidth)
            .background(
                color = badge.color.copy(alpha = 0.2f),
                shape = AppShapes.badge
            )
            .padding(AppDimensions.spacingSmall)
    ) {
        Box(
            modifier = Modifier
                .size(AppDimensions.gameCompleteIconSize)
                .clip(CircleShape)
                .background(badge.color.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = badge.icon, fontSize = AppDimensions.iconSizeLarge.value.sp)
        }
        
        Text(
            text = badge.name,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun ActionButtons(
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit,
    onShare: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
    ) {
        Button(
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(stringResource(R.string.game_complete_play_again), style = MaterialTheme.typography.titleMedium)
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
        ) {
            OutlinedButton(
                onClick = onShare,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.game_complete_share))
            }
            
            OutlinedButton(
                onClick = onMainMenu,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.game_complete_menu))
            }
        }
    }
}

private fun formatScore(score: Int): String {
    return when {
        score >= 1_000_000 -> String.format("%.1fM", score / 1_000_000.0)
        score >= 1_000 -> String.format("%,d", score)
        else -> score.toString()
    }
}

private fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000).toInt()
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", minutes, secs)
}

/**
 * Badge data class
 */
data class Badge(
    val id: String,
    val name: String,
    val icon: String,
    val color: Color
)
