package com.extremesudoku.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.extremesudoku.data.models.scoring.BonusEvent
import com.extremesudoku.data.models.scoring.BonusType
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.AppShapes
import com.extremesudoku.presentation.theme.LocalThemeColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

/**
 * Bonus kazanıldığında animasyonlu popup gösterir
 * Multiple bonusları queue sistemi ile sırayla gösterir
 */
@Composable
fun BonusPopupManager(
    bonusEvents: Flow<BonusEvent>,
    modifier: Modifier = Modifier
) {
    var currentBonus by remember { mutableStateOf<BonusEvent?>(null) }
    var isVisible by remember { mutableStateOf(false) }
    
    // Bonus event'leri dinle
    LaunchedEffect(bonusEvents) {
        bonusEvents.collect { event ->
            // Mevcut bonus varsa bekle
            while (isVisible) {
                delay(100)
            }
            
            // Yeni bonusu göster
            currentBonus = event
            isVisible = true
            
            // 2 saniye sonra gizle
            delay(2000)
            isVisible = false
            
            // Animasyon bitene kadar bekle
            delay(300)
            currentBonus = null
        }
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = isVisible && currentBonus != null,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(300)
            ) + fadeOut()
        ) {
            currentBonus?.let { bonus ->
                BonusPopup(
                    bonus = bonus,
                    modifier = Modifier.padding(top = AppDimensions.bonusPopupTopOffset)
                )
            }
        }
    }
}

@Composable
private fun BonusPopup(
    bonus: BonusEvent,
    modifier: Modifier = Modifier
) {
    // Pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "bonus_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    // Bonus type'a göre renk ve emoji
    val (backgroundColor, emoji) = getBonusStyle(bonus.type)
    val themeColors = LocalThemeColors.current
    
    Row(
        modifier = modifier
            .scale(scale)
            .background(
                color = backgroundColor,
                shape = AppShapes.bonusPopup
            )
            .padding(horizontal = AppDimensions.spacingLarge, vertical = AppDimensions.bonusPopupPadding),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Emoji icon
        Text(
            text = emoji,
            fontSize = AppDimensions.streakIconSize.value.sp
        )
        
        // Bonus info
        Column {
            Text(
                text = bonus.message,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                    color = themeColors.highlightText
            )
            
            if (bonus.points > 0) {
                Text(
                    text = "+${bonus.points} pts",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                        color = themeColors.highlightText.copy(alpha = 0.9f)
                )
            }
        }
    }
}

/**
 * Simple version - tek bir bonus göster (queue yok)
 * KÜÇÜK CARD - Ekranı kaplamaz
 */
@Composable
fun SimpleBonusPopup(
    message: String,
    points: Int,
    type: BonusType = BonusType.STREAK,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(visible) {
        if (visible) {
            delay(2000)
            onDismiss()
        }
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn() + scaleIn(initialScale = 0.8f),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeOut() + scaleOut(targetScale = 0.8f),
        modifier = modifier
            .padding(top = AppDimensions.bonusPointsTopOffset) // Üstten boşluk
            .wrapContentSize() // Sadece içerik kadar yer kapla
    ) {
        BonusPopup(
            bonus = BonusEvent(
                type = type,
                message = message,
                points = points,
                position = null
            )
        )
    }
}

/**
 * Floating points animation - sadece puan sayısı
 */
@Composable
fun FloatingPoints(
    points: Int,
    visible: Boolean,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = LocalThemeColors.current.bonusGold
) {
    var offsetY by remember { mutableStateOf(0.dp) }
    var alpha by remember { mutableStateOf(1f) }
    
    LaunchedEffect(visible) {
        if (visible) {
            // Animate up and fade out
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(1500, easing = FastOutSlowInEasing)
            ) { value, _ ->
                offsetY = (value * 100).dp
                alpha = 1f - value
            }
            onComplete()
        }
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 1.5f),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Text(
            text = "+$points",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color.copy(alpha = alpha),
            modifier = Modifier.offset(y = offsetY)
        )
    }
}

/**
 * Combo multiplier display
 */
@Composable
fun ComboMultiplierPopup(
    multiplier: Int,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalThemeColors.current
    
    AnimatedVisibility(
        visible = visible && multiplier > 1,
        enter = scaleIn(
            initialScale = 0.3f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = scaleOut(targetScale = 1.5f) + fadeOut(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = themeColors.streakHotOrange,
                    shape = AppShapes.comboMultiplier
                )
                .padding(horizontal = AppDimensions.spacingMedium, vertical = AppDimensions.spacingSmall),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔥",
                fontSize = AppDimensions.scoreIconSize.value.sp
            )
            Text(
                text = "${multiplier}x COMBO!",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black
                ),
                color = themeColors.highlightText
            )
        }
    }
}

/**
 * Bonus type'a göre stil belirle
 */
@Composable
private fun getBonusStyle(type: BonusType): Pair<Color, String> {
    val themeColors = LocalThemeColors.current
    return when (type) {
        BonusType.STREAK -> themeColors.bonusGold to "🔥"
        BonusType.COMPLETION -> themeColors.bonusBlue to "📦"
        BonusType.TIME -> themeColors.bonusCyan to "⚡"
        BonusType.PERFECT -> themeColors.bonusPink to "🏆"
        BonusType.SPECIAL -> themeColors.bonusLightGreen to "✨"
    }
}
