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
// LIGHT TEMA (Aydınlık Tema - Modern ve Uyumlu)
// ============================================================================
object LightColors {
    // TEMEL RENKLER - Ana arka plan ve yüzey renkleri
    val background = Color(0xFFFBFCFD)          // Ana arka plan rengi (çok açık gri-mavi)
    val surface = Color(0xFFFFFFFF)              // Yüzey rengi (beyaz)
    val cardBackground = Color(0xFFFFFFFF)       // Kart arka plan rengi (beyaz)
    val surfaceVariant = Color(0xFFF5F6F8)       // İkincil yüzey tonu (açık gri)
    val outline = Color(0xFFB3BAC2)              // Sınırlar ve çizgiler için kontur rengi
    val primaryContainer = Color(0xFFCFE0F8)     // Birincil renk container (çok açık mavi)
    val onPrimaryContainer = Color(0xFF0F3875)   // Birincil container üzerindeki metin
    val secondaryContainer = Color(0xFFC9E8E0)   // İkincil renk container (çok açık teal)
    val onSecondaryContainer = Color(0xFF0B3D37) // İkincil container üzerindeki metin
    val onTertiary = Color(0xFFFFFFFF)           // Üçüncül renk üzerindeki metin
    val onSurfaceVariant = Color(0xFF45464F)     // İkincil yüzey üzerindeki metin
    val error = Color(0xFFD32F2F)                // Hata rengi (kırmızı)
    val onError = Color(0xFFFFFFFF)              // Hata üzerinde metin
    val errorContainer = Color(0xFFFFCDD2)       // Hata container'ı (açık kırmızı)
    val onErrorContainer = Color(0xFF4B0000)     // Hata container üzerindeki metin
    val modalScrim = Color(0x80000000)           // Modallar için karartma
    val highlightText = Color(0xFFFFFFFF)        // Vurgulu bileşenler için metin

    // ANA RENKLER - Butonlar ve vurgular için (Modern Material3 uyumlu)
    val primary = Color(0xFF2196F3)              // Birincil renk (Google Mavi)
    val secondary = Color(0xFF009688)            // İkincil renk (Teal/Turkuaz)
    val tertiary = Color(0xFF9C27B0)             // Üçüncül renk (Mor)

    // METİN RENKLERİ - Yazı renkleri
    val text = Color(0xFF1A1A1A)                 // Ana metin rengi (koyu gri)
    val textSecondary = Color(0xFF6C757D)        // İkincil metin (orta gri)
    val textOnPrimary = Color(0xFFFFFFFF)        // Renkli buton üzerindeki yazı (beyaz)

    // GRID RENKLERİ - Sudoku grid için
    val gridBackground = Color(0xFFFFFFFF)       // Grid arka plan (beyaz)
    val gridLine = Color(0xFFE8EAED)             // İnce çizgiler (açık gri)
    val gridThickLine = Color(0xFF424242)        // Kalın çizgiler (koyu gri)

    // HÜCRE RENKLERİ - Sudoku hücreleri
    val cellBackground = Color(0xFFFFFFFF)       // Hücre arka plan (beyaz)
    val selectedCell = Color(0xFFB3D9FF)         // Seçili hücre (açık mavi)
    val selectedCellRow = Color(0xFFE0EFF8)      // Seçili satır/sütun (çok açık mavi)
    val selectedCellBox = Color(0xFFE0EFF8)      // Seçili 3x3 kutu (çok açık mavi)
    val sameNumberCell = Color(0xFFFFF4E5)       // Aynı sayı vurgusu (açık portakal)
    val conflictCell = Color(0xFFFFCED2)         // Çakışma/hata (açık kırmızı)

    // SAYI RENKLERİ - Sudoku sayıları
    val initialNumberText = Color(0xFF1A1A1A)    // Başlangıç sayıları (koyu gri)
    val userNumberText = Color(0xFF2196F3)       // Kullanıcı sayıları (mavi)
    val notesText = Color(0xFF9E9E9E)            // Not sayıları (gri)

    // BUTON RENKLERİ
    val buttonBackground = Color(0xFF2196F3)     // Ana buton arka plan (mavi)
    val buttonText = Color(0xFFFFFFFF)           // Ana buton yazı (beyaz)
    val buttonBackgroundSecondary = Color(0xFFF0F0F0) // İkincil buton arka plan (açık gri)
    val buttonTextSecondary = Color(0xFF212121)  // İkincil buton yazı (koyu gri)

    // OYUN GERİ BİLDİRİM RENKLERİ
    val correctCell = Color(0xFFC8E6C9)          // Doğru hücre (açık yeşil)
    val wrongCell = Color(0xFFFFCED2)            // Yanlış hücre (açık kırmızı)
    val hintCell = Color(0xFFFFF9C4)             // İpucu hücre (açık sarı)

    // UI ELEMENTLER
    val divider = Color(0xFFE8EAED)              // Ayırıcı çizgi (açık gri)
    val iconTint = Color(0xFF6C757D)             // İkon rengi (orta gri)

    // ZORLUK SEVİYESİ RENKLERİ
    val difficultyEasy = Color(0xFF4CAF50)       // Kolay (yeşil)
    val difficultyMedium = Color(0xFFFF9800)     // Orta (turuncu)
    val difficultyHard = Color(0xFFD32F2F)       // Zor (kırmızı)
    val difficultyExpert = Color(0xFF9C27B0)     // Uzman (mor)

    // PVP RENKLERİ
    val playerOneColor = Color(0xFF2196F3)       // Oyuncu 1 (mavi)
    val playerTwoColor = Color(0xFFEC407A)       // Oyuncu 2 (pembe)
    val winColor = Color(0xFF4CAF50)             // Kazanma (yeşil)
    val loseColor = Color(0xFFD32F2F)            // Kaybetme (kırmızı)

    // STREAK & SCORE RENKLERİ
    val streakGray = Color(0xFF9E9E9E)
    val streakGreen = Color(0xFF4CAF50)
    val streakCyan = Color(0xFF00BCD4)
    val streakGold = Color(0xFFFFC107)
    val streakOrange = Color(0xFFFF9800)
    val streakDeepOrange = Color(0xFFFF5722)
    val streakPink = Color(0xFFEC407A)
    val streakPurple = Color(0xFF9C27B0)
    val streakTurquoise = Color(0xFF009688)
    val streakHotOrange = Color(0xFFFF6D00)

    // BONUS RENKLERİ
    val bonusGold = Color(0xFFFFC107)
    val bonusBlue = Color(0xFF2196F3)
    val bonusCyan = Color(0xFF00BCD4)
    val bonusPink = Color(0xFFEC407A)
    val bonusLightGreen = Color(0xFF9CCC65)

    // BAŞARI RENKLERİ
    val achievementGold = Color(0xFFFFC107)
    val achievementSilver = Color(0xFFBDBDBD)
    val achievementBronze = Color(0xFFCD7F32)

    // DOĞRULUK RENKLERİ
    val accuracyHigh = Color(0xFF4CAF50)         // 90%+ (yeşil)
    val accuracyMedium = Color(0xFFFF9800)       // 70-89% (turuncu)
    val accuracyLow = Color(0xFFD32F2F)          // <70% (kırmızı)
}

// ============================================================================
// DARK TEMA (Karanlık Tema - Modern AMOLED)
// ============================================================================
object DarkColors {
    // TEMEL RENKLER - AMOLED optimized
    val background = Color(0xFF0A0E27)           // Ana arka plan (çok koyu mavi-gri)
    val surface = Color(0xFF141B2F)              // Yüzey rengi (koyu mavi-gri)
    val cardBackground = Color(0xFF1E2847)       // Kart arka plan (koyu mavi)
    val surfaceVariant = Color(0xFF1E2847)       // İkincil yüzey tonu
    val outline = Color(0xFF5A6B7D)              // Kontur rengi
    val primaryContainer = Color(0xFF1F3A70)     // Birincil container (koyu mavi)
    val onPrimaryContainer = Color(0xFFD4E5FF)   // Birincil container üzerindeki metin
    val secondaryContainer = Color(0xFF0F4C44)   // İkincil container (koyu teal)
    val onSecondaryContainer = Color(0xFFB2DFDB) // İkincil container üzerindeki metin
    val onTertiary = Color(0xFFE8D9F6)           // Üçüncül renk üzerindeki metin
    val onSurfaceVariant = Color(0xFFD0D4DD)     // İkincil yüzey üzerindeki metin
    val error = Color(0xFFFF8A8A)                // Hata rengi (açık kırmızı)
    val onError = Color(0xFF4B0000)              // Hata üzerindeki metin
    val errorContainer = Color(0xFF7A2A2A)       // Hata container'ı (koyu kırmızı)
    val onErrorContainer = Color(0xFFFFDAD6)     // Hata container üzerindeki metin
    val modalScrim = Color(0xCC000000)           // Modallar için karartma
    val highlightText = Color(0xFFFFFFFF)        // Vurgulu metin

    // ANA RENKLER - Material3 Uyumlu
    val primary = Color(0xFF64B5F6)              // Birincil renk (açık mavi)
    val secondary = Color(0xFF4DB8A8)            // İkincil renk (açık teal)
    val tertiary = Color(0xFFB89DE1)             // Üçüncül renk (açık mor)

    // METİN RENKLERİ
    val text = Color(0xFFE8EAF6)                 // Ana metin (açık gri)
    val textSecondary = Color(0xFFB0B4C3)        // İkincil metin (orta gri)
    val textOnPrimary = Color(0xFF0D1B2A)        // Renkli buton üzerindeki yazı (çok koyu)

    // GRID RENKLERİ - AMOLED uyumlu
    val gridBackground = Color(0xFF1E2847)       // Grid arka plan (koyu mavi)
    val gridLine = Color(0xFF3A4F63)             // İnce çizgiler (orta gri-mavi)
    val gridThickLine = Color(0xFFA0AEC0)        // Kalın çizgiler (açık gri)

    // HÜCRE RENKLERİ
    val cellBackground = Color(0xFF1E2847)       // Hücre arka plan (koyu mavi)
    val selectedCell = Color(0xFF334A8F)         // Seçili hücre (mavi)
    val selectedCellRow = Color(0xFF253349)      // Seçili satır/sütun (koyu mavi)
    val selectedCellBox = Color(0xFF253349)      // Seçili 3x3 kutu (koyu mavi)
    val sameNumberCell = Color(0xFF3D3A1F)       // Aynı sayı vurgusu (koyu sarı-gri)
    val conflictCell = Color(0xFF5F2E2E)         // Çakışma/hata (koyu kırmızı)

    // SAYI RENKLERİ - Yüksek kontrast
    val initialNumberText = Color(0xFFE8EAF6)    // Başlangıç sayıları (açık gri)
    val userNumberText = Color(0xFF64B5F6)       // Kullanıcı sayıları (açık mavi)
    val notesText = Color(0xFF8FA3B8)            // Not sayıları (orta gri)

    // BUTON RENKLERİ
    val buttonBackground = Color(0xFF64B5F6)     // Ana buton arka plan (açık mavi)
    val buttonText = Color(0xFF0D1B2A)           // Ana buton yazı (çok koyu)
    val buttonBackgroundSecondary = Color(0xFF3A4F63) // İkincil buton arka plan (orta gri)
    val buttonTextSecondary = Color(0xFFE8EAF6)  // İkincil buton yazı (açık gri)

    // OYUN GERİ BİLDİRİM RENKLERİ
    val correctCell = Color(0xFF3D5A3D)          // Doğru hücre (koyu yeşil)
    val wrongCell = Color(0xFF5F2E2E)            // Yanlış hücre (koyu kırmızı)
    val hintCell = Color(0xFF5F5A3D)             // İpucu hücre (koyu sarı)

    // UI ELEMENTLER
    val divider = Color(0xFF3A4F63)              // Ayırıcı çizgi (orta gri)
    val iconTint = Color(0xFFB0B4C3)             // İkon rengi (açık gri)

    // ZORLUK SEVİYESİ RENKLERİ
    val difficultyEasy = Color(0xFF81C784)       // Kolay (açık yeşil)
    val difficultyMedium = Color(0xFFFFB74D)     // Orta (açık turuncu)
    val difficultyHard = Color(0xFFFF8A8A)       // Zor (açık kırmızı)
    val difficultyExpert = Color(0xFFB89DE1)     // Uzman (açık mor)

    // PVP RENKLERİ
    val playerOneColor = Color(0xFF64B5F6)       // Oyuncu 1 (açık mavi)
    val playerTwoColor = Color(0xFFF48FB1)       // Oyuncu 2 (açık pembe)
    val winColor = Color(0xFF81C784)             // Kazanma (açık yeşil)
    val loseColor = Color(0xFFFF8A8A)            // Kaybetme (açık kırmızı)

    // STREAK & SCORE RENKLERİ
    val streakGray = Color(0xFFB0B4C3)
    val streakGreen = Color(0xFF81C784)
    val streakCyan = Color(0xFF4DD0E1)
    val streakGold = Color(0xFFFFD740)
    val streakOrange = Color(0xFFFFB74D)
    val streakDeepOrange = Color(0xFFFF8A65)
    val streakPink = Color(0xFFF48FB1)
    val streakPurple = Color(0xFFB89DE1)
    val streakTurquoise = Color(0xFF4DB8A8)
    val streakHotOrange = Color(0xFFFF8A65)

    // BONUS RENKLERİ
    val bonusGold = Color(0xFFFFD740)
    val bonusBlue = Color(0xFF64B5F6)
    val bonusCyan = Color(0xFF4DD0E1)
    val bonusPink = Color(0xFFF48FB1)
    val bonusLightGreen = Color(0xFFB5E7A0)

    // BAŞARI RENKLERİ
    val achievementGold = Color(0xFFFFD740)
    val achievementSilver = Color(0xFFBBDEFB)
    val achievementBronze = Color(0xFFFFAB91)

    // DOĞRULUK RENKLERİ
    val accuracyHigh = Color(0xFF81C784)         // 90%+ (açık yeşil)
    val accuracyMedium = Color(0xFFFFB74D)       // 70-89% (açık turuncu)
    val accuracyLow = Color(0xFFFF8A8A)          // <70% (açık kırmızı)
}

// ============================================================================
// NEWSPAPER TEMA (Gazete - Klasik Siyah-Beyaz-Şampanya)
// ============================================================================
object BlueOceanColors {
    // TEMEL RENKLER - Gerçek gazete kağıdı görünümü
    val background = Color(0xFFE8E6E1)           // Ana arka plan (gazete kağıdı - kahverengi-krem)
    val surface = Color(0xFFF5F3EF)              // Yüzey rengi (açık gazete kağıdı)
    val cardBackground = Color(0xFFF5F3EF)       // Kart arka plan (gazete kağıdı)
    val surfaceVariant = Color(0xFFD9D7D2)       // İkincil yüzey tonu (orta gazete tonu)
    val outline = Color(0xFFB0ADA6)              // Kontur rengi (koyu gazete tonu)
    val primaryContainer = Color(0xFF3E3B37)     // Birincil container (koyu şampanya)
    val onPrimaryContainer = Color(0xFFF5F3EF)   // Birincil container üzerindeki metin
    val secondaryContainer = Color(0xFFC9C5BB)   // İkincil container (gazete gri)
    val onSecondaryContainer = Color(0xFF1A1815) // İkincil container üzerindeki metin
    val onTertiary = Color(0xFFF5F3EF)           // Üçüncül renk üzerindeki metin
    val onSurfaceVariant = Color(0xFF3E3B37)     // İkincil yüzey üzerindeki metin
    val error = Color(0xFF8B3A3A)                // Hata rengi (koyu kırmızı kahverengi)
    val onError = Color(0xFFF5F3EF)              // Hata üzerindeki metin
    val errorContainer = Color(0xFFD9CFC8)       // Hata container'ı (açık hata)
    val onErrorContainer = Color(0xFF1A0000)     // Hata container üzerindeki metin
    val modalScrim = Color(0x99000000)           // Modallar için karartma
    val highlightText = Color(0xFFF5F3EF)        // Vurgulu bileşenler için metin

    // ANA RENKLER - Gazete klasiği siyah-gri-şampanya
    val primary = Color(0xFF1A1815)              // Birincil renk (gazetede siyah gibi çok koyu)
    val secondary = Color(0xFF514D47)            // İkincil renk (gazete yazı gri)
    val tertiary = Color(0xFF3E3B37)             // Üçüncül renk (gazete başlık gri)

    // METİN RENKLERİ - Gazete karakteri
    val text = Color(0xFF0D0B08)                 // Ana metin (tam siyah gazete yazı)
    val textSecondary = Color(0xFF514D47)        // İkincil metin (gazete alt başlık)
    val textOnPrimary = Color(0xFFF5F3EF)        // Renkli buton üzerindeki yazı (kağıt rengi)

    // GRID RENKLERİ - Gazete kareleri/bölümleri
    val gridBackground = Color(0xFFF5F3EF)       // Grid arka plan (gazete kağıdı)
    val gridLine = Color(0xFFB0ADA6)             // İnce çizgiler (hafif gazete çizgileri)
    val gridThickLine = Color(0xFF1A1815)        // Kalın çizgiler (gazetede siyah çerçeveler)

    // HÜCRE RENKLERİ - Gazete vurguları
    val cellBackground = Color(0xFFF5F3EF)       // Hücre arka plan (gazete kağıdı)
    val selectedCell = Color(0xFFD9D7D2)         // Seçili hücre (gazete gri)
    val selectedCellRow = Color(0xFFE8E6E1)      // Seçili satır/sütun (gazete vurgusu)
    val selectedCellBox = Color(0xFFE8E6E1)      // Seçili 3x3 kutu (gazete kutusu)
    val sameNumberCell = Color(0xFFE0D9CF)       // Aynı sayı vurgusu (hafif gazete)
    val conflictCell = Color(0xFFD9CFCA)         // Çakışma/hata (gazete hata vurgusu)

    // SAYI RENKLERİ - Gazete karakteri
    val initialNumberText = Color(0xFF0D0B08)    // Başlangıç sayıları (gazete siyahı)
    val userNumberText = Color(0xFF3E3B37)       // Kullanıcı sayıları (gazete koyu gri)
    val notesText = Color(0xFF9E9A8F)            // Not sayıları (açık gazete gri)

    // BUTON RENKLERİ
    val buttonBackground = Color(0xFF1A1815)     // Ana buton arka plan (gazete siyahı)
    val buttonText = Color(0xFFF5F3EF)           // Ana buton yazı (kağıt rengi)
    val buttonBackgroundSecondary = Color(0xFFD9D7D2) // İkincil buton arka plan (gazete gri)
    val buttonTextSecondary = Color(0xFF1A1815)  // İkincil buton yazı (gazete siyahı)

    // OYUN GERİ BİLDİRİM RENKLERİ - Gazete tonları
    val correctCell = Color(0xFFD4E5D0)          // Doğru hücre (gazete yeşili)
    val wrongCell = Color(0xFFE0CFCA)            // Yanlış hücre (gazete kırmızısı)
    val hintCell = Color(0xFFE8DFC8)             // İpucu hücre (gazete sarısı)

    // UI ELEMENTLER
    val divider = Color(0xFFB0ADA6)              // Ayırıcı çizgi (gazete bölümleri)
    val iconTint = Color(0xFF514D47)             // İkon rengi (gazete gri)

    // ZORLUK SEVİYESİ RENKLERİ - Gazete gri tonları
    val difficultyEasy = Color(0xFF7A7268)       // Kolay (açık gazete gri)
    val difficultyMedium = Color(0xFF634F47)     // Orta (gazete gri)
    val difficultyHard = Color(0xFF3E3B37)       // Zor (koyu gazete gri)
    val difficultyExpert = Color(0xFF1A1815)     // Uzman (gazete siyahı)

    // PVP RENKLERİ
    val playerOneColor = Color(0xFF1A1815)       // Oyuncu 1 (gazete siyahı)
    val playerTwoColor = Color(0xFF7A7268)       // Oyuncu 2 (gazete gri)
    val winColor = Color(0xFF3E3B37)             // Kazanma (gazete koyu gri)
    val loseColor = Color(0xFF9E9A8F)            // Kaybetme (gazete açık gri)

    // STREAK & SCORE RENKLERİ - Gazete gri skalası
    val streakGray = Color(0xFF9E9A8F)
    val streakGreen = Color(0xFF7A7268)
    val streakCyan = Color(0xFF6F6A5F)
    val streakGold = Color(0xFF3E3B37)
    val streakOrange = Color(0xFF6F6A5F)
    val streakDeepOrange = Color(0xFF3E3B37)
    val streakPink = Color(0xFF7A7268)
    val streakPurple = Color(0xFF6F6A5F)
    val streakTurquoise = Color(0xFF7A7268)
    val streakHotOrange = Color(0xFF3E3B37)

    // BONUS RENKLERİ - Gazete tonları
    val bonusGold = Color(0xFF3E3B37)
    val bonusBlue = Color(0xFF514D47)
    val bonusCyan = Color(0xFF7A7268)
    val bonusPink = Color(0xFF7A7268)
    val bonusLightGreen = Color(0xFF7A7268)

    // BAŞARI RENKLERİ - Gazete tonları
    val achievementGold = Color(0xFF3E3B37)
    val achievementSilver = Color(0xFF9E9A8F)
    val achievementBronze = Color(0xFFB0ADA6)

    // DOĞRULUK RENKLERİ
    val accuracyHigh = Color(0xFF514D47)         // 90%+ (gazete koyu gri)
    val accuracyMedium = Color(0xFF7A7268)       // 70-89% (gazete orta gri)
    val accuracyLow = Color(0xFF9E9A8F)          // <70% (gazete açık gri)
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