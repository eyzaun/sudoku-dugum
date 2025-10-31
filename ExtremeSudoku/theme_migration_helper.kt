// Helper file for quick theme migration
// This file provides quick replacements for theme colors
// 
// THEME MIGRATION GUIDE:
// 1. Add import: import com.extremesudoku.presentation.theme.LocalThemeColors
// 2. Add at top of Composable: val themeColors = LocalThemeColors.current
// 3. Replace hardcoded colors:
//    - Color(0xFFXXXXXX) -> themeColors.appropriateProperty
//    - MaterialTheme.colorScheme.primary -> themeColors.primary
//    - MaterialTheme.colorScheme.background -> themeColors.background
//    - MaterialTheme.colorScheme.surface -> themeColors.surface
//    - MaterialTheme.colorScheme.onBackground -> themeColors.text
//    - MaterialTheme.colorScheme.onSurface -> themeColors.text
// 
// Common replacements:
// - Card/Surface background -> themeColors.cardBackground
// - Icon tint -> themeColors.iconTint
// - Text color -> themeColors.text or themeColors.textSecondary
// - Button background -> themeColors.buttonBackground
// - Button text -> themeColors.buttonText
// - Divider -> themeColors.divider
