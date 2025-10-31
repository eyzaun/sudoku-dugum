package com.extremesudoku.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * ============================================================================
 * MERKEZI RENK KONTROL PANELİ
 * ============================================================================
 * 
 * Bu dosyadan tüm uygulamanın renklerini kontrol edebilirsiniz.
 * Her renk için hex kod değiştirin, tüm uygulama otomatik güncellenir.
 * 
 * RENK KODLARI: Color(0xFFRRGGBB)
 * - FF: Opaklık (her zaman FF)
 * - RR: Kırmızı (00-FF)
 * - GG: Yeşil (00-FF)
 * - BB: Mavi (00-FF)
 * 
 * ÖRNEKLER:
 * - Siyah: Color(0xFF000000)
 * - Beyaz: Color(0xFFFFFFFF)
 * - Kırmızı: Color(0xFFFF0000)
 * - Yeşil: Color(0xFF00FF00)
 * - Mavi: Color(0xFF0000FF)
 * ============================================================================
 */

// ============================================================================
// LIGHT TEMA (Aydınlık Tema)
// ============================================================================
object LightColors {
    // TEMEL RENKLER - Ana arka plan ve yüzey renkleri
    val background = Color(0xFFFAFAFA)          // Ana arka plan rengi (çok açık gri)
    val surface = Color(0xFFFFFFFF)              // Yüzey rengi (beyaz)
    val cardBackground = Color(0xFFFFFFFF)       // Kart arka plan rengi (beyaz)
    val surfaceVariant = Color(0xFFF0F0F0)       // İkincil yüzey tonu (hafif gri)
    val outline = Color(0xFFB0BEC5)              // Sınırlar ve çizgiler için kontur rengi
    val primaryContainer = Color(0xFFBBDEFB)     // Birincil renk container (açık mavi)
    val onPrimaryContainer = Color(0xFF0D47A1)   // Birincil container üzerindeki metin
    val secondaryContainer = Color(0xFFB2DFDB)   // İkincil renk container (açık teal)
    val onSecondaryContainer = Color(0xFF004D40) // İkincil container üzerindeki metin
    val onTertiary = Color(0xFFFFFFFF)           // Üçüncül renk üzerindeki metin
    val onSurfaceVariant = Color(0xFF424242)     // İkincil yüzey üzerindeki metin
    val error = Color(0xFFEF5350)                // Hata rengi (kırmızı)
    val onError = Color(0xFFFFFFFF)              // Hata üzerinde metin
    val errorContainer = Color(0xFFFFDAD6)       // Hata container'ı (açık kırmızı)
    val onErrorContainer = Color(0xFF410002)     // Hata container üzerindeki metin
    val modalScrim = Color(0x80000000)           // Modallar için karartma
    val highlightText = Color(0xFFFFFFFF)        // Vurgulu bileşenler için metin
    
    // ANA RENKLER - Butonlar ve vurgular için
    val primary = Color(0xFF1E88E5)              // Birincil renk (parlak mavi)
    val secondary = Color(0xFF26A69A)            // İkincil renk (teal)
    val tertiary = Color(0xFF7E57C2)             // Üçüncül renk (mor)
    
    // METİN RENKLERİ - Yazı renkleri
    val text = Color(0xFF212121)                 // Ana metin rengi (koyu gri)
    val textSecondary = Color(0xFF757575)        // İkincil metin (orta gri)
    val textOnPrimary = Color(0xFFFFFFFF)        // Renkli buton üzerindeki yazı (beyaz)
    
    // GRID RENKLERİ - Sudoku grid için
    val gridBackground = Color(0xFFFFFFFF)       // Grid arka plan (beyaz)
    val gridLine = Color(0xFFE0E0E0)             // İnce çizgiler (açık gri)
    val gridThickLine = Color(0xFF424242)        // Kalın çizgiler (koyu gri)
    
    // HÜCRE RENKLERİ - Sudoku hücreleri
    val cellBackground = Color(0xFFFFFFFF)       // Hücre arka plan (beyaz)
    val selectedCell = Color(0xFFBBDEFB)         // Seçili hücre (açık mavi)
    val selectedCellRow = Color(0xFFE3F2FD)      // Seçili satır/sütun (çok açık mavi)
    val selectedCellBox = Color(0xFFE3F2FD)      // Seçili 3x3 kutu (çok açık mavi)
    val sameNumberCell = Color(0xFFFFF9C4)       // Aynı sayı vurgusu (açık sarı)
    val conflictCell = Color(0xFFFFCDD2)         // Çakışma/hata (açık kırmızı)
    
    // SAYI RENKLERİ - Sudoku sayıları
    val initialNumberText = Color(0xFF212121)    // Başlangıç sayıları (koyu gri)
    val userNumberText = Color(0xFF1E88E5)       // Kullanıcı sayıları (parlak mavi)
    val notesText = Color(0xFF9E9E9E)            // Not sayıları (gri)
    
    // BUTON RENKLERİ
    val buttonBackground = Color(0xFF1E88E5)     // Ana buton arka plan (parlak mavi)
    val buttonText = Color(0xFFFFFFFF)           // Ana buton yazı (beyaz)
    val buttonBackgroundSecondary = Color(0xFFEEEEEE) // İkincil buton arka plan (açık gri)
    val buttonTextSecondary = Color(0xFF212121)  // İkincil buton yazı (koyu gri)
    
    // OYUN GERİ BİLDİRİM RENKLERİ
    val correctCell = Color(0xFFC8E6C9)          // Doğru hücre (açık yeşil)
    val wrongCell = Color(0xFFFFCDD2)            // Yanlış hücre (açık kırmızı)
    val hintCell = Color(0xFFFFF9C4)             // İpucu hücre (açık sarı)
    
    // UI ELEMENTLER
    val divider = Color(0xFFE0E0E0)              // Ayırıcı çizgi (açık gri)
    val iconTint = Color(0xFF757575)             // İkon rengi (orta gri)
    
    // ZORLUK SEVİYESİ RENKLERİ
    val difficultyEasy = Color(0xFF66BB6A)       // Kolay (yeşil)
    val difficultyMedium = Color(0xFFFFA726)     // Orta (turuncu)
    val difficultyHard = Color(0xFFEF5350)       // Zor (kırmızı)
    val difficultyExpert = Color(0xFF7E57C2)     // Uzman (mor)
    
    // PVP RENKLERİ
    val playerOneColor = Color(0xFF1E88E5)       // Oyuncu 1 (mavi)
    val playerTwoColor = Color(0xFFEC407A)       // Oyuncu 2 (pembe)
    val winColor = Color(0xFF66BB6A)             // Kazanma (yeşil)
    val loseColor = Color(0xFFEF5350)            // Kaybetme (kırmızı)
    
    // STREAK & SCORE RENKLERİ
    val streakGray = Color(0xFF9E9E9E)
    val streakGreen = Color(0xFF66BB6A)
    val streakCyan = Color(0xFF26C6DA)
    val streakGold = Color(0xFFFFB300)
    val streakOrange = Color(0xFFFF9800)
    val streakDeepOrange = Color(0xFFFF7043)
    val streakPink = Color(0xFFEC407A)
    val streakPurple = Color(0xFF7E57C2)
    val streakTurquoise = Color(0xFF26A69A)
    val streakHotOrange = Color(0xFFFF6E40)
    
    // BONUS RENKLERİ
    val bonusGold = Color(0xFFFFB300)
    val bonusBlue = Color(0xFF1E88E5)
    val bonusCyan = Color(0xFF26C6DA)
    val bonusPink = Color(0xFFEC407A)
    val bonusLightGreen = Color(0xFF9CCC65)
    
    // BAŞARI RENKLERİ
    val achievementGold = Color(0xFFFFB300)
    val achievementSilver = Color(0xFFBDBDBD)
    val achievementBronze = Color(0xFFD4A574)
    
    // DOĞRULUK RENKLERİ
    val accuracyHigh = Color(0xFF66BB6A)         // 90%+ (yeşil)
    val accuracyMedium = Color(0xFFFFA726)       // 70-89% (turuncu)
    val accuracyLow = Color(0xFFEF5350)          // <70% (kırmızı)
}

// ============================================================================
// DARK TEMA (Karanlık Tema)
// ============================================================================
object DarkColors {
    // TEMEL RENKLER
    val background = Color(0xFF121212)           // Ana arka plan (çok koyu)
    val surface = Color(0xFF1E1E1E)              // Yüzey rengi (koyu gri)
    val cardBackground = Color(0xFF2C2C2C)       // Kart arka plan (orta koyu gri)
    val surfaceVariant = Color(0xFF2C2C2C)       // İkincil yüzey tonu
    val outline = Color(0xFF616161)              // Kontur rengi
    val primaryContainer = Color(0xFF0D47A1)     // Birincil container (koyu mavi)
    val onPrimaryContainer = Color(0xFFE3F2FD)   // Birincil container üzerindeki metin
    val secondaryContainer = Color(0xFF1E3A34)   // İkincil container (koyu teal)
    val onSecondaryContainer = Color(0xFFB2DFDB) // İkincil container üzerindeki metin
    val onTertiary = Color(0xFF1B0036)           // Üçüncül renk üzerindeki metin
    val onSurfaceVariant = Color(0xFFB0B0B0)     // İkincil yüzey üzerindeki metin
    val error = Color(0xFFCF6679)                // Hata rengi (pembe kırmızı)
    val onError = Color(0xFF000000)              // Hata üzerindeki metin
    val errorContainer = Color(0xFF8C1D1D)       // Hata container'ı (koyu kırmızı)
    val onErrorContainer = Color(0xFFFFDAD6)     // Hata container üzerindeki metin
    val modalScrim = Color(0xB3000000)           // Modallar için karartma
    val highlightText = Color(0xFFFFFFFF)        // Vurgulu metin
    
    // ANA RENKLER
    val primary = Color(0xFF42A5F5)              // Birincil renk (açık mavi)
    val secondary = Color(0xFF26A69A)            // İkincil renk (teal)
    val tertiary = Color(0xFF9575CD)             // Üçüncül renk (açık mor)
    
    // METİN RENKLERİ
    val text = Color(0xFFFFFFFF)                 // Ana metin (beyaz)
    val textSecondary = Color(0xFFB0B0B0)        // İkincil metin (açık gri)
    val textOnPrimary = Color(0xFF000000)        // Renkli buton üzerindeki yazı (siyah)
    
    // GRID RENKLERİ - DAHA AÇIK VE GÖRÜNÜR
    val gridBackground = Color(0xFF2C2C2C)       // Grid arka plan (orta koyu gri)
    val gridLine = Color(0xFF424242)             // İnce çizgiler (orta gri)
    val gridThickLine = Color(0xFF9E9E9E)        // Kalın çizgiler (açık gri)
    
    // HÜCRE RENKLERİ
    val cellBackground = Color(0xFF2C2C2C)       // Hücre arka plan (orta koyu gri)
    val selectedCell = Color(0xFF1565C0)         // Seçili hücre (koyu mavi)
    val selectedCellRow = Color(0xFF263238)      // Seçili satır/sütun (koyu gri-mavi)
    val selectedCellBox = Color(0xFF263238)      // Seçili 3x3 kutu (koyu gri-mavi)
    val sameNumberCell = Color(0xFF4A4A2C)       // Aynı sayı vurgusu (koyu sarı-gri)
    val conflictCell = Color(0xFF4A2C2C)         // Çakışma/hata (koyu kırmızı)
    
    // SAYI RENKLERİ - YÜKSEK KONTRAST
    val initialNumberText = Color(0xFFFFFFFF)    // Başlangıç sayıları (beyaz)
    val userNumberText = Color(0xFF42A5F5)       // Kullanıcı sayıları (açık mavi)
    val notesText = Color(0xFF757575)            // Not sayıları (orta gri)
    
    // BUTON RENKLERİ
    val buttonBackground = Color(0xFF42A5F5)     // Ana buton arka plan (açık mavi)
    val buttonText = Color(0xFF000000)           // Ana buton yazı (siyah)
    val buttonBackgroundSecondary = Color(0xFF424242) // İkincil buton arka plan (orta gri)
    val buttonTextSecondary = Color(0xFFFFFFFF)  // İkincil buton yazı (beyaz)
    
    // OYUN GERİ BİLDİRİM RENKLERİ
    val correctCell = Color(0xFF2C4A2C)          // Doğru hücre (koyu yeşil)
    val wrongCell = Color(0xFF4A2C2C)            // Yanlış hücre (koyu kırmızı)
    val hintCell = Color(0xFF4A4A2C)             // İpucu hücre (koyu sarı)
    
    // UI ELEMENTLER
    val divider = Color(0xFF424242)              // Ayırıcı çizgi (orta gri)
    val iconTint = Color(0xFFB0B0B0)             // İkon rengi (açık gri)
    
    // ZORLUK SEVİYESİ RENKLERİ
    val difficultyEasy = Color(0xFF66BB6A)       // Kolay (yeşil)
    val difficultyMedium = Color(0xFFFFA726)     // Orta (turuncu)
    val difficultyHard = Color(0xFFEF5350)       // Zor (kırmızı)
    val difficultyExpert = Color(0xFF9575CD)     // Uzman (açık mor)
    
    // PVP RENKLERİ
    val playerOneColor = Color(0xFF42A5F5)       // Oyuncu 1 (açık mavi)
    val playerTwoColor = Color(0xFFEC407A)       // Oyuncu 2 (pembe)
    val winColor = Color(0xFF66BB6A)             // Kazanma (yeşil)
    val loseColor = Color(0xFFEF5350)            // Kaybetme (kırmızı)
    
    // STREAK & SCORE RENKLERİ
    val streakGray = Color(0xFF9E9E9E)
    val streakGreen = Color(0xFF81C784)
    val streakCyan = Color(0xFF4DD0E1)
    val streakGold = Color(0xFFFFD740)
    val streakOrange = Color(0xFFFFB74D)
    val streakDeepOrange = Color(0xFFFF8A65)
    val streakPink = Color(0xFFF06292)
    val streakPurple = Color(0xFF9575CD)
    val streakTurquoise = Color(0xFF4DB6AC)
    val streakHotOrange = Color(0xFFFF7043)
    
    // BONUS RENKLERİ
    val bonusGold = Color(0xFFFFD740)
    val bonusBlue = Color(0xFF42A5F5)
    val bonusCyan = Color(0xFF4DD0E1)
    val bonusPink = Color(0xFFF06292)
    val bonusLightGreen = Color(0xFF9CCC65)
    
    // BAŞARI RENKLERİ
    val achievementGold = Color(0xFFFFD740)
    val achievementSilver = Color(0xFFCFD8DC)
    val achievementBronze = Color(0xFFFFAB91)
    
    // DOĞRULUK RENKLERİ
    val accuracyHigh = Color(0xFF81C784)         // 90%+ (açık yeşil)
    val accuracyMedium = Color(0xFFFFB74D)       // 70-89% (açık turuncu)
    val accuracyLow = Color(0xFFEF5350)          // <70% (açık kırmızı)
}

// ============================================================================
// NEWSPAPER TEMA (Gazete - Klasik Siyah-Beyaz)
// ============================================================================
object BlueOceanColors {
    // TEMEL RENKLER - Klasik gazete görünümü
    val background = Color(0xFFFAF8F3)           // Ana arka plan (kağıt rengi - krem beyaz)
    val surface = Color(0xFFFFFFFF)              // Yüzey rengi (beyaz)
    val cardBackground = Color(0xFFFFFFFF)       // Kart arka plan (beyaz)
    val surfaceVariant = Color(0xFFE0E0E0)       // İkincil yüzey tonu (açık gri)
    val outline = Color(0xFF9E9E9E)              // Kontur rengi (orta gri)
    val primaryContainer = Color(0xFF424242)     // Birincil container (koyu gri)
    val onPrimaryContainer = Color(0xFFFFFFFF)   // Birincil container üzerindeki metin
    val secondaryContainer = Color(0xFFBDBDBD)   // İkincil container (açık gri)
    val onSecondaryContainer = Color(0xFF212121) // İkincil container üzerindeki metin
    val onTertiary = Color(0xFFFFFFFF)           // Üçüncül renk üzerindeki metin
    val onSurfaceVariant = Color(0xFF424242)     // İkincil yüzey üzerindeki metin
    val error = Color(0xFFB71C1C)                // Hata rengi (koyu kırmızı)
    val onError = Color(0xFFFFFFFF)              // Hata üzerindeki metin
    val errorContainer = Color(0xFFFAD0D0)       // Hata container'ı (açık kırmızı)
    val onErrorContainer = Color(0xFF410002)     // Hata container üzerindeki metin
    val modalScrim = Color(0x8A000000)           // Modallar için karartma
    val highlightText = Color(0xFFFFFFFF)        // Vurgulu bileşenler için metin
    
    // ANA RENKLER - Minimal siyah-gri palet
    val primary = Color(0xFF212121)              // Birincil renk (siyah)
    val secondary = Color(0xFF616161)            // İkincil renk (koyu gri)
    val tertiary = Color(0xFF424242)             // Üçüncül renk (orta koyu gri)
    
    // METİN RENKLERİ
    val text = Color(0xFF000000)                 // Ana metin (tam siyah)
    val textSecondary = Color(0xFF616161)        // İkincil metin (koyu gri)
    val textOnPrimary = Color(0xFFFFFFFF)        // Renkli buton üzerindeki yazı (beyaz)
    
    // GRID RENKLERİ - Klasik gazete çizgileri
    val gridBackground = Color(0xFFFFFFFF)       // Grid arka plan (beyaz)
    val gridLine = Color(0xFFD0D0D0)             // İnce çizgiler (açık gri)
    val gridThickLine = Color(0xFF000000)        // Kalın çizgiler (siyah)
    
    // HÜCRE RENKLERİ - Minimal vurgular
    val cellBackground = Color(0xFFFFFFFF)       // Hücre arka plan (beyaz)
    val selectedCell = Color(0xFFE0E0E0)         // Seçili hücre (açık gri)
    val selectedCellRow = Color(0xFFF5F5F5)      // Seçili satır/sütun (çok açık gri)
    val selectedCellBox = Color(0xFFF5F5F5)      // Seçili 3x3 kutu (çok açık gri)
    val sameNumberCell = Color(0xFFEEEEEE)       // Aynı sayı vurgusu (açık gri)
    val conflictCell = Color(0xFFFFE0E0)         // Çakışma/hata (çok açık kırmızı)
    
    // SAYI RENKLERİ - Siyah-gri palet
    val initialNumberText = Color(0xFF000000)    // Başlangıç sayıları (siyah)
    val userNumberText = Color(0xFF424242)       // Kullanıcı sayıları (koyu gri)
    val notesText = Color(0xFF9E9E9E)            // Not sayıları (gri)
    
    // BUTON RENKLERİ
    val buttonBackground = Color(0xFF212121)     // Ana buton arka plan (siyah)
    val buttonText = Color(0xFFFFFFFF)           // Ana buton yazı (beyaz)
    val buttonBackgroundSecondary = Color(0xFFEEEEEE) // İkincil buton arka plan (açık gri)
    val buttonTextSecondary = Color(0xFF212121)  // İkincil buton yazı (siyah)
    
    // OYUN GERİ BİLDİRİM RENKLERİ - Minimal
    val correctCell = Color(0xFFE8F5E9)          // Doğru hücre (çok açık yeşil)
    val wrongCell = Color(0xFFFFE0E0)            // Yanlış hücre (çok açık kırmızı)
    val hintCell = Color(0xFFFFF9C4)             // İpucu hücre (açık sarı)
    
    // UI ELEMENTLER
    val divider = Color(0xFFD0D0D0)              // Ayırıcı çizgi (açık gri)
    val iconTint = Color(0xFF616161)             // İkon rengi (koyu gri)
    
    // ZORLUK SEVİYESİ RENKLERİ - Gri tonları
    val difficultyEasy = Color(0xFF757575)       // Kolay (orta gri)
    val difficultyMedium = Color(0xFF616161)     // Orta (koyu gri)
    val difficultyHard = Color(0xFF424242)       // Zor (çok koyu gri)
    val difficultyExpert = Color(0xFF212121)     // Uzman (siyah)
    
    // PVP RENKLERİ
    val playerOneColor = Color(0xFF212121)       // Oyuncu 1 (siyah)
    val playerTwoColor = Color(0xFF616161)       // Oyuncu 2 (koyu gri)
    val winColor = Color(0xFF424242)             // Kazanma (orta koyu gri)
    val loseColor = Color(0xFF757575)            // Kaybetme (orta gri)
    
    // STREAK & SCORE RENKLERİ - Gri skalası
    val streakGray = Color(0xFF9E9E9E)
    val streakGreen = Color(0xFF757575)
    val streakCyan = Color(0xFF616161)
    val streakGold = Color(0xFF424242)
    val streakOrange = Color(0xFF616161)
    val streakDeepOrange = Color(0xFF424242)
    val streakPink = Color(0xFF757575)
    val streakPurple = Color(0xFF616161)
    val streakTurquoise = Color(0xFF757575)
    val streakHotOrange = Color(0xFF424242)
    
    // BONUS RENKLERİ - Gri tonları
    val bonusGold = Color(0xFF424242)
    val bonusBlue = Color(0xFF616161)
    val bonusCyan = Color(0xFF757575)
    val bonusPink = Color(0xFF757575)
    val bonusLightGreen = Color(0xFF757575)
    
    // BAŞARI RENKLERİ - Gri tonları
    val achievementGold = Color(0xFF424242)
    val achievementSilver = Color(0xFF9E9E9E)
    val achievementBronze = Color(0xFFBDBDBD)
    
    // DOĞRULUK RENKLERİ
    val accuracyHigh = Color(0xFF616161)         // 90%+ (koyu gri)
    val accuracyMedium = Color(0xFF757575)       // 70-89% (orta gri)
    val accuracyLow = Color(0xFF9E9E9E)          // <70% (açık gri)
}

// ============================================================================
// YARDIMCI FONKSİYON - Tema tipine göre renkleri döndürür
// ============================================================================
fun getColorPalette(themeType: ThemeType): ThemeColors {
    return when (themeType) {
        ThemeType.LIGHT -> ThemeColors(
            background = LightColors.background,
            surface = LightColors.surface,
            surfaceVariant = LightColors.surfaceVariant,
            primary = LightColors.primary,
            primaryContainer = LightColors.primaryContainer,
            onPrimaryContainer = LightColors.onPrimaryContainer,
            secondary = LightColors.secondary,
            secondaryContainer = LightColors.secondaryContainer,
            onSecondaryContainer = LightColors.onSecondaryContainer,
            tertiary = LightColors.tertiary,
            onTertiary = LightColors.onTertiary,
            text = LightColors.text,
            textSecondary = LightColors.textSecondary,
            textOnPrimary = LightColors.textOnPrimary,
            onSurfaceVariant = LightColors.onSurfaceVariant,
            outline = LightColors.outline,
            gridBackground = LightColors.gridBackground,
            gridLine = LightColors.gridLine,
            gridThickLine = LightColors.gridThickLine,
            cellBackground = LightColors.cellBackground,
            selectedCell = LightColors.selectedCell,
            selectedCellRow = LightColors.selectedCellRow,
            selectedCellBox = LightColors.selectedCellBox,
            sameNumberCell = LightColors.sameNumberCell,
            conflictCell = LightColors.conflictCell,
            initialNumberText = LightColors.initialNumberText,
            userNumberText = LightColors.userNumberText,
            notesText = LightColors.notesText,
            buttonBackground = LightColors.buttonBackground,
            buttonText = LightColors.buttonText,
            buttonBackgroundSecondary = LightColors.buttonBackgroundSecondary,
            buttonTextSecondary = LightColors.buttonTextSecondary,
            correctCell = LightColors.correctCell,
            wrongCell = LightColors.wrongCell,
            hintCell = LightColors.hintCell,
            divider = LightColors.divider,
            cardBackground = LightColors.cardBackground,
            iconTint = LightColors.iconTint,
            modalScrim = LightColors.modalScrim,
            highlightText = LightColors.highlightText,
            difficultyEasy = LightColors.difficultyEasy,
            difficultyMedium = LightColors.difficultyMedium,
            difficultyHard = LightColors.difficultyHard,
            difficultyExpert = LightColors.difficultyExpert,
            playerOneColor = LightColors.playerOneColor,
            playerTwoColor = LightColors.playerTwoColor,
            winColor = LightColors.winColor,
            loseColor = LightColors.loseColor,
            error = LightColors.error,
            onError = LightColors.onError,
            errorContainer = LightColors.errorContainer,
            onErrorContainer = LightColors.onErrorContainer,
            streakGray = LightColors.streakGray,
            streakGreen = LightColors.streakGreen,
            streakCyan = LightColors.streakCyan,
            streakGold = LightColors.streakGold,
            streakOrange = LightColors.streakOrange,
            streakDeepOrange = LightColors.streakDeepOrange,
            streakPink = LightColors.streakPink,
            streakPurple = LightColors.streakPurple,
            streakTurquoise = LightColors.streakTurquoise,
            streakHotOrange = LightColors.streakHotOrange,
            bonusGold = LightColors.bonusGold,
            bonusBlue = LightColors.bonusBlue,
            bonusCyan = LightColors.bonusCyan,
            bonusPink = LightColors.bonusPink,
            bonusLightGreen = LightColors.bonusLightGreen,
            achievementGold = LightColors.achievementGold,
            achievementSilver = LightColors.achievementSilver,
            achievementBronze = LightColors.achievementBronze,
            accuracyHigh = LightColors.accuracyHigh,
            accuracyMedium = LightColors.accuracyMedium,
            accuracyLow = LightColors.accuracyLow
        )
        ThemeType.DARK -> ThemeColors(
            background = DarkColors.background,
            surface = DarkColors.surface,
            surfaceVariant = DarkColors.surfaceVariant,
            primary = DarkColors.primary,
            primaryContainer = DarkColors.primaryContainer,
            onPrimaryContainer = DarkColors.onPrimaryContainer,
            secondary = DarkColors.secondary,
            secondaryContainer = DarkColors.secondaryContainer,
            onSecondaryContainer = DarkColors.onSecondaryContainer,
            tertiary = DarkColors.tertiary,
            onTertiary = DarkColors.onTertiary,
            text = DarkColors.text,
            textSecondary = DarkColors.textSecondary,
            textOnPrimary = DarkColors.textOnPrimary,
            onSurfaceVariant = DarkColors.onSurfaceVariant,
            outline = DarkColors.outline,
            gridBackground = DarkColors.gridBackground,
            gridLine = DarkColors.gridLine,
            gridThickLine = DarkColors.gridThickLine,
            cellBackground = DarkColors.cellBackground,
            selectedCell = DarkColors.selectedCell,
            selectedCellRow = DarkColors.selectedCellRow,
            selectedCellBox = DarkColors.selectedCellBox,
            sameNumberCell = DarkColors.sameNumberCell,
            conflictCell = DarkColors.conflictCell,
            initialNumberText = DarkColors.initialNumberText,
            userNumberText = DarkColors.userNumberText,
            notesText = DarkColors.notesText,
            buttonBackground = DarkColors.buttonBackground,
            buttonText = DarkColors.buttonText,
            buttonBackgroundSecondary = DarkColors.buttonBackgroundSecondary,
            buttonTextSecondary = DarkColors.buttonTextSecondary,
            correctCell = DarkColors.correctCell,
            wrongCell = DarkColors.wrongCell,
            hintCell = DarkColors.hintCell,
            divider = DarkColors.divider,
            cardBackground = DarkColors.cardBackground,
            iconTint = DarkColors.iconTint,
            modalScrim = DarkColors.modalScrim,
            highlightText = DarkColors.highlightText,
            difficultyEasy = DarkColors.difficultyEasy,
            difficultyMedium = DarkColors.difficultyMedium,
            difficultyHard = DarkColors.difficultyHard,
            difficultyExpert = DarkColors.difficultyExpert,
            playerOneColor = DarkColors.playerOneColor,
            playerTwoColor = DarkColors.playerTwoColor,
            winColor = DarkColors.winColor,
            loseColor = DarkColors.loseColor,
            error = DarkColors.error,
            onError = DarkColors.onError,
            errorContainer = DarkColors.errorContainer,
            onErrorContainer = DarkColors.onErrorContainer,
            streakGray = DarkColors.streakGray,
            streakGreen = DarkColors.streakGreen,
            streakCyan = DarkColors.streakCyan,
            streakGold = DarkColors.streakGold,
            streakOrange = DarkColors.streakOrange,
            streakDeepOrange = DarkColors.streakDeepOrange,
            streakPink = DarkColors.streakPink,
            streakPurple = DarkColors.streakPurple,
            streakTurquoise = DarkColors.streakTurquoise,
            streakHotOrange = DarkColors.streakHotOrange,
            bonusGold = DarkColors.bonusGold,
            bonusBlue = DarkColors.bonusBlue,
            bonusCyan = DarkColors.bonusCyan,
            bonusPink = DarkColors.bonusPink,
            bonusLightGreen = DarkColors.bonusLightGreen,
            achievementGold = DarkColors.achievementGold,
            achievementSilver = DarkColors.achievementSilver,
            achievementBronze = DarkColors.achievementBronze,
            accuracyHigh = DarkColors.accuracyHigh,
            accuracyMedium = DarkColors.accuracyMedium,
            accuracyLow = DarkColors.accuracyLow
        )
        ThemeType.BLUE_OCEAN -> ThemeColors(
            background = BlueOceanColors.background,
            surface = BlueOceanColors.surface,
            surfaceVariant = BlueOceanColors.surfaceVariant,
            primary = BlueOceanColors.primary,
            primaryContainer = BlueOceanColors.primaryContainer,
            onPrimaryContainer = BlueOceanColors.onPrimaryContainer,
            secondary = BlueOceanColors.secondary,
            secondaryContainer = BlueOceanColors.secondaryContainer,
            onSecondaryContainer = BlueOceanColors.onSecondaryContainer,
            tertiary = BlueOceanColors.tertiary,
            onTertiary = BlueOceanColors.onTertiary,
            text = BlueOceanColors.text,
            textSecondary = BlueOceanColors.textSecondary,
            textOnPrimary = BlueOceanColors.textOnPrimary,
            onSurfaceVariant = BlueOceanColors.onSurfaceVariant,
            outline = BlueOceanColors.outline,
            gridBackground = BlueOceanColors.gridBackground,
            gridLine = BlueOceanColors.gridLine,
            gridThickLine = BlueOceanColors.gridThickLine,
            cellBackground = BlueOceanColors.cellBackground,
            selectedCell = BlueOceanColors.selectedCell,
            selectedCellRow = BlueOceanColors.selectedCellRow,
            selectedCellBox = BlueOceanColors.selectedCellBox,
            sameNumberCell = BlueOceanColors.sameNumberCell,
            conflictCell = BlueOceanColors.conflictCell,
            initialNumberText = BlueOceanColors.initialNumberText,
            userNumberText = BlueOceanColors.userNumberText,
            notesText = BlueOceanColors.notesText,
            buttonBackground = BlueOceanColors.buttonBackground,
            buttonText = BlueOceanColors.buttonText,
            buttonBackgroundSecondary = BlueOceanColors.buttonBackgroundSecondary,
            buttonTextSecondary = BlueOceanColors.buttonTextSecondary,
            correctCell = BlueOceanColors.correctCell,
            wrongCell = BlueOceanColors.wrongCell,
            hintCell = BlueOceanColors.hintCell,
            divider = BlueOceanColors.divider,
            cardBackground = BlueOceanColors.cardBackground,
            iconTint = BlueOceanColors.iconTint,
            modalScrim = BlueOceanColors.modalScrim,
            highlightText = BlueOceanColors.highlightText,
            difficultyEasy = BlueOceanColors.difficultyEasy,
            difficultyMedium = BlueOceanColors.difficultyMedium,
            difficultyHard = BlueOceanColors.difficultyHard,
            difficultyExpert = BlueOceanColors.difficultyExpert,
            playerOneColor = BlueOceanColors.playerOneColor,
            playerTwoColor = BlueOceanColors.playerTwoColor,
            winColor = BlueOceanColors.winColor,
            loseColor = BlueOceanColors.loseColor,
            error = BlueOceanColors.error,
            onError = BlueOceanColors.onError,
            errorContainer = BlueOceanColors.errorContainer,
            onErrorContainer = BlueOceanColors.onErrorContainer,
            streakGray = BlueOceanColors.streakGray,
            streakGreen = BlueOceanColors.streakGreen,
            streakCyan = BlueOceanColors.streakCyan,
            streakGold = BlueOceanColors.streakGold,
            streakOrange = BlueOceanColors.streakOrange,
            streakDeepOrange = BlueOceanColors.streakDeepOrange,
            streakPink = BlueOceanColors.streakPink,
            streakPurple = BlueOceanColors.streakPurple,
            streakTurquoise = BlueOceanColors.streakTurquoise,
            streakHotOrange = BlueOceanColors.streakHotOrange,
            bonusGold = BlueOceanColors.bonusGold,
            bonusBlue = BlueOceanColors.bonusBlue,
            bonusCyan = BlueOceanColors.bonusCyan,
            bonusPink = BlueOceanColors.bonusPink,
            bonusLightGreen = BlueOceanColors.bonusLightGreen,
            achievementGold = BlueOceanColors.achievementGold,
            achievementSilver = BlueOceanColors.achievementSilver,
            achievementBronze = BlueOceanColors.achievementBronze,
            accuracyHigh = BlueOceanColors.accuracyHigh,
            accuracyMedium = BlueOceanColors.accuracyMedium,
            accuracyLow = BlueOceanColors.accuracyLow
        )
    }
}