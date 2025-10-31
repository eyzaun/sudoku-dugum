package com.extremesudoku.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Uygulamada mevcut tema türleri
 */
enum class ThemeType {
    LIGHT,          // Aydınlık Tema - Modern Mavi, Teal, Mor renk paletine sahip
    DARK,           // Karanlık Tema - AMOLED Optimized, derin mavi-gri arka planı
    BLUE_OCEAN      // Gazete Teması - Klasik gazete kağıdı renkleri (Şampanya-Siyah)
}

/**
 * Custom color scheme for Sudoku game
 * Contains all colors used across the app for consistent theming
 */
data class ThemeColors(
    // Base colors
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    
    // Text colors
    val text: Color,
    val textSecondary: Color,
    val textOnPrimary: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    
    // Grid colors
    val gridBackground: Color,
    val gridLine: Color,
    val gridThickLine: Color,
    
    // Cell colors
    val cellBackground: Color,
    val selectedCell: Color,
    val selectedCellRow: Color,
    val selectedCellBox: Color,
    val sameNumberCell: Color,
    val conflictCell: Color,
    
    // Number colors
    val initialNumberText: Color,
    val userNumberText: Color,
    val notesText: Color,
    
    // Button colors
    val buttonBackground: Color,
    val buttonText: Color,
    val buttonBackgroundSecondary: Color,
    val buttonTextSecondary: Color,
    
    // Game feedback colors
    val correctCell: Color,
    val wrongCell: Color,
    val hintCell: Color,
    
    // UI elements
    val divider: Color,
    val cardBackground: Color,
    val iconTint: Color,
    val modalScrim: Color,
    val highlightText: Color,
    
    // Difficulty colors
    val difficultyEasy: Color,
    val difficultyMedium: Color,
    val difficultyHard: Color,
    val difficultyExpert: Color,
    
    // PvP colors
    val playerOneColor: Color,
    val playerTwoColor: Color,
    val winColor: Color,
    val loseColor: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    
    // ============ YENİ EKLEMELER - UI Components için ============
    // Streak & Score colors
    val streakGray: Color,
    val streakGreen: Color,
    val streakCyan: Color,
    val streakGold: Color,
    val streakOrange: Color,
    val streakDeepOrange: Color,
    val streakPink: Color,
    val streakPurple: Color,
    val streakTurquoise: Color,
    val streakHotOrange: Color,
    
    // Bonus colors
    val bonusGold: Color,
    val bonusBlue: Color,
    val bonusCyan: Color,
    val bonusPink: Color,
    val bonusLightGreen: Color,
    
    // Achievement colors
    val achievementGold: Color,
    val achievementSilver: Color,
    val achievementBronze: Color,
    
    // Accuracy colors
    val accuracyHigh: Color,      // 90%+
    val accuracyMedium: Color,    // 70-89%
    val accuracyLow: Color        // <70%
)

/**
 * CompositionLocal to provide theme colors throughout the app
 */
val LocalThemeColors = staticCompositionLocalOf<ThemeColors> {
    error("No theme colors provided")
}
