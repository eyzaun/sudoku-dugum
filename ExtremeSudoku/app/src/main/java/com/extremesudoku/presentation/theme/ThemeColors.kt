package com.extremesudoku.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Uygulamada mevcut tema türleri (UI/UX Kurallarına Uygun)
 * Uygulanılan Kurallar:
 * 1. 60-30-10 Renk Kuralı: %60 Arka plan, %30 Vurgu, %10 Aksesuar
 * 2. WCAG 2.1 Kontrast: Metin/BG ≥ 4.5:1 (AA), UI ≥ 3:1 (AA)
 * 3. Anlamsal Renk Kullanımı: Renkler anlama göre ayarlanmış
 * 4. Renk Psikolojisi: Optimal kullanıcı deneyimi
 */
enum class ThemeType {
    LIGHT,          // Aydınlık - Modern Mavi (#2563EB), Yeşil (#1DB584), Mor (#8B5CF6)
    DARK,           // Karanlık - AMOLED Mavi (#60A5FA), Yeşil (#4ADE80), Mor (#A78BFA)
    GAZETE,         // Gazete - Krem (#EEEBE7), Siyah (#1A1815), Gri Tonları
    MONOCHROME      // Monokrom - Saf Siyah-Beyaz, Net Kontrastlar
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
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,

    // Text colors
    val text: Color,
    val textSecondary: Color,
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
    val affectedAreaCell: Color,

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
