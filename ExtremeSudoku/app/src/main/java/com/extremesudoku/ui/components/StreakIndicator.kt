package com.extremesudoku.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.AppShapes
import com.extremesudoku.presentation.theme.LocalThemeColors

/**
 * Streak seviyesini görselleştiren component
 * Fire emoji ve animasyonlar ile engaging feedback
 */
@Composable
fun StreakIndicator(
    currentStreak: Int,
    maxStreak: Int,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val themeColors = LocalThemeColors.current
    // Streak seviyesi değiştiğinde animasyon trigger
    var previousStreak by remember { mutableStateOf(currentStreak) }
    val streakIncreased = currentStreak > previousStreak
    
    LaunchedEffect(currentStreak) {
        previousStreak = currentStreak
    }
    
    // Pulsing animation for active streak
    val infiniteTransition = rememberInfiniteTransition(label = "streak_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (currentStreak > 0) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    
    // Pop animation when streak increases
    val popScale by animateFloatAsState(
        targetValue = if (streakIncreased) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pop_scale"
    )
    
    // Color gradient based on streak level
    val gradientColors = getStreakColors(currentStreak)
    
    Box(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(gradientColors),
                shape = AppShapes.streakIndicator
            )
            .padding(horizontal = AppDimensions.spacingMedium, vertical = AppDimensions.streakIndicatorPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fire emoji progression
            AnimatedContent(
                targetState = currentStreak,
                transitionSpec = {
                    if (targetState > initialState) {
                        // Streak increased - slide up with scale
                        (slideInVertically { it } + fadeIn() + scaleIn(initialScale = 0.8f))
                            .togetherWith(
                                slideOutVertically { -it } + fadeOut() + scaleOut(targetScale = 1.2f)
                            )
                    } else {
                        // Streak decreased or reset - fade
                        fadeIn() togetherWith fadeOut()
                    }
                },
                label = "fire_animation"
            ) { streak ->
                Text(
                    text = getFireEmoji(streak),
                    fontSize = 24.sp,
                    modifier = Modifier
                        .scale(if (streak > 0) pulseScale else 1f)
                        .scale(popScale)
                )
            }
            
            // Streak count with animation
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                if (showLabel) {
                    Text(
                        text = "Streak",
                        style = MaterialTheme.typography.labelSmall,
                        color = themeColors.highlightText.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingExtraSmall),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Current streak with counter animation
                    AnimatedContent(
                        targetState = currentStreak,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInVertically { it } togetherWith slideOutVertically { -it }
                            } else {
                                fadeIn() togetherWith fadeOut()
                            }
                        },
                        label = "streak_count"
                    ) { streak ->
                        Text(
                            text = streak.toString(),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = themeColors.highlightText,
                            modifier = Modifier.scale(popScale)
                        )
                    }
                    
                    // Best streak indicator
                    if (currentStreak < maxStreak && maxStreak > 0) {
                        Text(
                            text = "/ $maxStreak",
                            style = MaterialTheme.typography.bodySmall,
                            color = themeColors.highlightText.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = AppDimensions.spacingExtraSmall / 2)
                        )
                    }
                }
            }
            
            // Streak level indicator
            if (currentStreak >= 10) {
                StreakLevelBadge(
                    level = getStreakLevel(currentStreak),
                    modifier = Modifier.scale(pulseScale)
                )
            }
        }
    }
}

@Composable
private fun StreakLevelBadge(
    level: String,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalThemeColors.current
    Box(
        modifier = modifier
            .size(AppDimensions.streakIconSize)
            .background(
                color = themeColors.highlightText.copy(alpha = 0.3f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = level,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = themeColors.highlightText
        )
    }
}

/**
 * Fire emoji progression based on streak
 */
private fun getFireEmoji(streak: Int): String {
    return when {
        streak == 0 -> "" // Sleeping
        streak in 1..2 -> "×1" // Single fire
        streak in 3..5 -> "×2" // Double fire
        streak in 6..9 -> "×3" // Triple fire
        streak in 10..14 -> "×4" // Quad fire
        streak in 15..19 -> "★" // Rocket
        streak >= 20 -> "⬆" // Lightning - Master level
        else -> "×1"
    }
}

/**
 * Streak level badge text
 */
private fun getStreakLevel(streak: Int): String {
    return when {
        streak in 10..14 -> "×4"
        streak in 15..19 -> "★"
        streak >= 20 -> "⬆"
        else -> "×1"
    }
}

/**
 * Color gradient based on streak level
 */
@Composable
private fun getStreakColors(streak: Int): List<Color> {
    val themeColors = LocalThemeColors.current
    return when {
        streak == 0 -> listOf(
            themeColors.streakGray,
            themeColors.textSecondary
        )
        streak in 1..2 -> listOf(
            themeColors.streakGreen,
            themeColors.difficultyEasy
        )
        streak in 3..5 -> listOf(
            themeColors.streakCyan,
            themeColors.streakTurquoise
        )
        streak in 6..9 -> listOf(
            themeColors.streakGold,
            themeColors.bonusGold
        )
        streak in 10..14 -> listOf(
            themeColors.streakOrange,
            themeColors.difficultyMedium
        )
        streak in 15..19 -> listOf(
            themeColors.streakDeepOrange,
            themeColors.difficultyHard
        )
        streak >= 20 -> listOf(
            themeColors.streakPink,
            themeColors.playerTwoColor,
            themeColors.streakPurple
        )
        else -> listOf(themeColors.streakGreen, themeColors.difficultyEasy)
    }
}

/**
 * Compact version for smaller spaces
 */
@Composable
fun CompactStreakIndicator(
    currentStreak: Int,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalThemeColors.current
    if (currentStreak > 0) {
        Row(
            modifier = modifier
                .background(
                    brush = Brush.horizontalGradient(getStreakColors(currentStreak)),
                    shape = AppShapes.streakBadge
                )
                .padding(horizontal = AppDimensions.spacingSmall, vertical = AppDimensions.streakBadgePadding),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingExtraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = getFireEmoji(currentStreak),
                fontSize = 16.sp
            )
            Text(
                text = currentStreak.toString(),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = themeColors.highlightText
            )
        }
    }
}
