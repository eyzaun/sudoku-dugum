package com.extremesudoku.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Calculate luminance of a color (0.0 = black, 1.0 = white)
 */
fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}

@Composable
fun ExtremeSudokuTheme(
    content: @Composable () -> Unit
) {
    // Bizim tema sistemimizden renkleri al
    val themeColors = LocalThemeColors.current
    
    // MaterialTheme'i bizim renklerle senkronize et
    val colorScheme = lightColorScheme(
        primary = themeColors.primary,
        onPrimary = themeColors.onPrimary,
        primaryContainer = themeColors.primaryContainer,
        onPrimaryContainer = themeColors.onPrimaryContainer,
        secondary = themeColors.secondary,
        onSecondary = themeColors.onSecondary,
        secondaryContainer = themeColors.secondaryContainer,
        onSecondaryContainer = themeColors.onSecondaryContainer,
        tertiary = themeColors.tertiary,
        onTertiary = themeColors.onTertiary,
        background = themeColors.background,
        surface = themeColors.surface,
        surfaceVariant = themeColors.surfaceVariant,
        onBackground = themeColors.text,
        onSurface = themeColors.text,
        onSurfaceVariant = themeColors.onSurfaceVariant,
        outline = themeColors.outline,
        error = themeColors.error,
        onError = themeColors.onError,
        errorContainer = themeColors.errorContainer,
        onErrorContainer = themeColors.onErrorContainer
    )
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Status bar rengini arka plan rengine göre ayarla
            @Suppress("DEPRECATION")
            window.statusBarColor = themeColors.background.toArgb()
            // Arka plan açıksa status bar iconları koyu, koyuysa açık olsun
            val isLightBackground = themeColors.background.luminance() > 0.5f
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLightBackground
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
