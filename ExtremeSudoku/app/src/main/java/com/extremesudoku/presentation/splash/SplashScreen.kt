package com.extremesudoku.presentation.splash

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.extremesudoku.R
import com.extremesudoku.presentation.theme.LocalThemeColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinish: () -> Unit
) {
    val themeColors = LocalThemeColors.current

    // Animasyon durumu
    val infiniteTransition = rememberInfiniteTransition(label = "splash_transition")

    // Başlık için scale animasyonu
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_animation"
    )

    // Alfa animasyonu
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_animation"
    )

    // Gradient animasyonu
    val gradientColor by animateColorAsState(
        targetValue = themeColors.primary,
        animationSpec = tween(2000),
        label = "gradient_animation"
    )

    // Otomatik geçiş
    LaunchedEffect(Unit) {
        delay(3000)
        onSplashFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        themeColors.background,
                        themeColors.cardBackground
                    )
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppDimensions.spacingExtraLarge)
                .padding(vertical = AppDimensions.spacingLarge)
        ) {
            Spacer(modifier = Modifier.height(AppDimensions.topSpacing))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo animasyon
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(30.dp),
                            color = gradientColor.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.splash_logo_text),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = gradientColor,
                        modifier = Modifier
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                    )
                }

                Spacer(modifier = Modifier.height(AppDimensions.spacingExtraLarge))

                // Başlık
                Text(
                    text = stringResource(R.string.splash_title),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.text,
                    modifier = Modifier.graphicsLayer(alpha = alpha)
                )

                Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))

                // Alt başlık
                Text(
                    text = stringResource(R.string.splash_tagline),
                    fontSize = 14.sp,
                    color = themeColors.textSecondary,
                    modifier = Modifier.graphicsLayer(alpha = alpha)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Yükleme göstergesi
                LoadingIndicator(
                    modifier = Modifier.size(40.dp),
                    color = gradientColor
                )

                Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
            }
        }
    }
}

@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_transition")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .graphicsLayer(rotationZ = rotation)
            .background(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = color.copy(alpha = 0.1f)
            )
            .padding(4.dp)
            .background(
                shape = androidx.compose.foundation.shape.CircleShape,
                brush = Brush.sweepGradient(
                    colors = listOf(
                        color,
                        color.copy(alpha = 0.2f),
                        color
                    )
                )
            )
    )
}

private object AppDimensions {
    val spacingSmall = 8.dp
    val spacingMedium = 16.dp
    val spacingLarge = 24.dp
    val spacingExtraLarge = 32.dp
    val topSpacing = 48.dp
}
