package com.extremesudoku.presentation.theme

/**
 * ============================================================================
 * TEMA SİSTEMİ
 * ============================================================================
 * 
 * Renkleri değiştirmek için ColorPalette.kt dosyasını açın.
 * Orada tüm renkler tek bir yerden kontrol edilebilir.
 * ============================================================================
 */

/**
 * Get theme colors by theme type
 * ColorPalette.kt dosyasından renkleri alır
 */
fun getThemeColors(themeType: ThemeType): ThemeColors {
    return getColorPalette(themeType)
}
