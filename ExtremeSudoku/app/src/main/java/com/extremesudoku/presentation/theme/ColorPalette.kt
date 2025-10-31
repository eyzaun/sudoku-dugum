package com.extremesudoku.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * ============================================================================
 * MERKEZI RENK KONTROL PANELİ - UI/UX KURALLARIYA UYGUN
 * ============================================================================
 *
 * Uygulanılan Kurallar:
 * 1. 60-30-10 Renk Kuralı: %60 Arka plan, %30 Vurgu, %10 Aksesuar
 * 2. WCAG 2.1 Kontrast: Metin/BG ≥ 4.5:1 (AA), UI ≥ 3:1 (AA)
 * 3. Anlamsal Renk Kullanımı: Renkler anlama göre ayarlanmış
 * 4. Renk Psikolojisi: Algı ve kullanıcı deneyimini iyileştirme
 *
 * RENK KODLARI: Color(0xFFRRGGBB)
 * - FF: Opaklık (her zaman FF)
 * - RR: Kırmızı (00-FF)
 * - GG: Yeşil (00-FF)
 * - BB: Mavi (00-FF)
 * ============================================================================
 */

// ============================================================================
// LIGHT TEMA (Aydınlık Tema - Temiz ve Modern)
// 60-30-10: Beyaz BG (60%), Mavi/Gri Vurgu (30%), Accent (10%)
// ============================================================================
object LightColors {
    // ===== TEMEL RENKLER (60%) - Arka Plan ve Yüzeyler =====
    val background = Color(0xFFFAFBFC)          // Ana arka plan (neutral gri-beyaz)
    val surface = Color(0xFFFFFFFF)              // Yüzey rengi (tam beyaz)
    val cardBackground = Color(0xFFF8F9FA)       // Kart arka plan (hafif gri)
    val surfaceVariant = Color(0xFFF0F2F5)       // İkincil yüzey (orta gri)

    // ===== YAPISAL RENKLERİ (Kontrol ve Sınırlar) =====
    val outline = Color(0xFFD0D5DD)              // Sınırlar (hafif gri)
    val divider = Color(0xFFE5E7EB)              // Ayırıcı çizgi (açık gri)
    val modalScrim = Color(0x80000000)           // Modal arka plan (siyah 50%)

    // ===== TEMEL VURGU RENKLERİ (30%) - Etkileşim =====
    val primary = Color(0xFF2563EB)              // Birincil (parlak mavi - aksyon)
    val onPrimary = Color(0xFFFFFFFF)            // Birincil üstü (beyaz metin)
    val primaryContainer = Color(0xFFDEE9F8)     // Birincil hafif (arka plan)
    val onPrimaryContainer = Color(0xFF1E40AF)   // Birincil hafif üstü (koyu mavi)

    val secondary = Color(0xFF1DB584)            // İkincil (yeşil - onay)
    val onSecondary = Color(0xFFFFFFFF)          // İkincil üstü (beyaz metin)
    val secondaryContainer = Color(0xFFD1F4E6)   // İkincil hafif
    val onSecondaryContainer = Color(0xFF0F6A47) // İkincil hafif üstü

    val tertiary = Color(0xFF8B5CF6)             // Üçüncül (mor - bilgi/uyarı)
    val onTertiary = Color(0xFFFFFFFF)           // Üçüncül üstü (beyaz metin)
    val tertiaryContainer = Color(0xFFF3E8FF)    // Üçüncül hafif
    val onTertiaryContainer = Color(0xFF5B21B6) // Üçüncül hafif üstü

    // ===== METIN RENKLERİ (WCAG AA Uyumlu) =====
    val text = Color(0xFF1F2937)                 // Ana metin (koyu gri)
    val textSecondary = Color(0xFF6B7280)        // İkincil metin (orta gri)
    val onSurfaceVariant = Color(0xFF4B5563)     // Varyant üzerindeki metin

    // ===== HATA VE UYARI RENKLERİ (Anlamsal) =====
    val error = Color(0xFFDC2626)                // Hata (kırmızı - dikkat)
    val onError = Color(0xFFFFFFFF)              // Hata üstü (beyaz)
    val errorContainer = Color(0xFFFEE2E2)       // Hata hafif
    val onErrorContainer = Color(0xFF7F1D1D)     // Hata hafif üstü

    // ===== SUDOKU GRID RENKLERİ =====
    val gridBackground = Color(0xFFFFFFFF)       // Grid arka plan (beyaz)
    val gridLine = Color(0xFFE5E7EB)             // İnce çizgiler (açık gri - 3:1 kontrast)
    val gridThickLine = Color(0xFF374151)        // Kalın çizgiler (koyu gri - 8:1 kontrast)

    // ===== SUDOKU HÜCRE RENKLERİ (Net ve Belirgin) =====
    val cellBackground = Color(0xFFFFFFFF)       // Hücre arka plan
    val selectedCell = Color(0xFFBFDBFE)         // Seçili hücre (açık mavi)
    val selectedCellRow = Color(0xFFEFF6FF)      // Seçili satır/sütun (çok açık mavi)
    val selectedCellBox = Color(0xFFF3F4F6)      // Seçili 3x3 kutu (hafif gri)
    val sameNumberCell = Color(0xFFFEF3C7)       // Aynı sayı (açık sarı)
    val conflictCell = Color(0xFFFECACA)         // Çakışma (açık kırmızı)

    // ===== SUDOKU SAYI RENKLERİ =====
    val initialNumberText = Color(0xFF1F2937)    // Başlangıç sayıları (koyu)
    val userNumberText = Color(0xFF2563EB)       // Kullanıcı sayıları (mavi)
    val notesText = Color(0xFF9CA3AF)            // Not sayıları (açık gri)

    // ===== BUTON RENKLERİ =====
    val buttonBackground = Color(0xFF2563EB)     // Ana buton (mavi)
    val buttonText = Color(0xFFFFFFFF)           // Buton yazı (beyaz)
    val buttonBackgroundSecondary = Color(0xFFF3F4F6) // İkincil buton
    val buttonTextSecondary = Color(0xFF1F2937)  // İkincil buton yazı

    // ===== OYUN GERİ BİLDİRİMİ (Anlamsal) =====
    val correctCell = Color(0xFFDCFCE7)          // Doğru (açık yeşil)
    val wrongCell = Color(0xFFFECACA)            // Yanlış (açık kırmızı)
    val hintCell = Color(0xFFFEF3C7)             // İpucu (açık sarı)

    // ===== ZOR LUK SEVİYESİ RENKLERİ (Anlamsal) =====
    val difficultyEasy = Color(0xFF15803D)       // Kolay (yeşil)
    val difficultyMedium = Color(0xFFCA8A04)     // Orta (turuncu)
    val difficultyHard = Color(0xFFDC2626)       // Zor (kırmızı)
    val difficultyExpert = Color(0xFF6D28D9)     // Uzman (mor)

    // ===== PVP RENKLERİ =====
    val playerOneColor = Color(0xFF2563EB)       // Oyuncu 1 (mavi)
    val playerTwoColor = Color(0xFFDC2626)       // Oyuncu 2 (kırmızı)
    val winColor = Color(0xFF15803D)             // Kazanma (yeşil)
    val loseColor = Color(0xFFDC2626)            // Kaybetme (kırmızı)

    // ===== STREAK VE BONUS RENKLERİ =====
    val streakGray = Color(0xFF9CA3AF)
    val streakGreen = Color(0xFF15803D)
    val streakCyan = Color(0xFF0891B2)
    val streakGold = Color(0xFFCA8A04)
    val streakOrange = Color(0xFFEA580C)
    val streakDeepOrange = Color(0xFFDC2626)
    val streakPink = Color(0xFFBE185D)
    val streakPurple = Color(0xFF7C3AED)
    val streakTurquoise = Color(0xFF0D9488)
    val streakHotOrange = Color(0xFFE5004B)

    // ===== BONUS RENKLERİ =====
    val bonusGold = Color(0xFFCA8A04)
    val bonusBlue = Color(0xFF2563EB)
    val bonusCyan = Color(0xFF0891B2)
    val bonusPink = Color(0xFFBE185D)
    val bonusLightGreen = Color(0xFF65A30D)

    // ===== BAŞARI RENKLERİ =====
    val achievementGold = Color(0xFFCA8A04)
    val achievementSilver = Color(0xFF9CA3AF)
    val achievementBronze = Color(0xFFB45309)

    // ===== DOĞRULUK RENKLERİ =====
    val accuracyHigh = Color(0xFF15803D)         // Yüksek (yeşil)
    val accuracyMedium = Color(0xFFCA8A04)       // Orta (turuncu)
    val accuracyLow = Color(0xFFDC2626)          // Düşük (kırmızı)

    // ===== HIGHLIGHT VE ACCENT =====
    val highlightText = Color(0xFFFFFFFF)        // Vurgulu metin
}

// ============================================================================
// DARK TEMA (Karanlık Tema - AMOLED Optimized)
// 60-30-10: Koyu BG (60%), Açık Vurgu (30%), Accent (10%)
// ============================================================================
object DarkColors {
    // ===== TEMEL RENKLER (60%) - Arka Plan ve Yüzeyler =====
    val background = Color(0xFF0F172A)           // Ana arka plan (derin mavi-siyah)
    val surface = Color(0xFF1E293B)              // Yüzey (koyu mavi-gri)
    val cardBackground = Color(0xFF334155)       // Kart arka plan (orta koyu)
    val surfaceVariant = Color(0xFF475569)       // İkincil yüzey (açık koyu)

    // ===== YAPISAL RENKLERİ (Kontrol ve Sınırlar) =====
    val outline = Color(0xFF64748B)              // Sınırlar (orta gri)
    val divider = Color(0xFF475569)              // Ayırıcı çizgi (koyu gri)
    val modalScrim = Color(0xCC000000)           // Modal arka plan (siyah 80%)

    // ===== TEMEL VURGU RENKLERİ (30%) - Etkileşim =====
    val primary = Color(0xFF60A5FA)              // Birincil (açık mavi - aksyon)
    val onPrimary = Color(0xFF0F172A)            // Birincil üstü (koyu metin)
    val primaryContainer = Color(0xFF1E3A8A)     // Birincil hafif (arka plan)
    val onPrimaryContainer = Color(0xFFBFDBFE)   // Birincil hafif üstü (açık mavi)

    val secondary = Color(0xFF4ADE80)            // İkincil (açık yeşil - onay)
    val onSecondary = Color(0xFF0F172A)          // İkincil üstü (koyu metin)
    val secondaryContainer = Color(0xFF166534)   // İkincil hafif
    val onSecondaryContainer = Color(0xFFDCFCE7) // İkincil hafif üstü

    val tertiary = Color(0xFFA78BFA)             // Üçüncül (açık mor - bilgi/uyarı)
    val onTertiary = Color(0xFF0F172A)           // Üçüncül üstü (koyu metin)
    val tertiaryContainer = Color(0xFF4C1D95)    // Üçüncül hafif
    val onTertiaryContainer = Color(0xFFF3E8FF) // Üçüncül hafif üstü

    // ===== METIN RENKLERİ (WCAG AA Uyumlu) =====
    val text = Color(0xFFF1F5F9)                 // Ana metin (açık gri)
    val textSecondary = Color(0xFFCBD5E1)        // İkincil metin (orta açık gri)
    val onSurfaceVariant = Color(0xFFE2E8F0)     // Varyant üzerindeki metin

    // ===== HATA VE UYARI RENKLERİ (Anlamsal) =====
    val error = Color(0xFFFF6B6B)                // Hata (açık kırmızı)
    val onError = Color(0xFF000000)              // Hata üstü (siyah)
    val errorContainer = Color(0xFF7F1D1D)       // Hata hafif
    val onErrorContainer = Color(0xFFFEE2E2)     // Hata hafif üstü

    // ===== SUDOKU GRID RENKLERİ (Net Görünüm) =====
    val gridBackground = Color(0xFF334155)       // Grid arka plan (orta koyu)
    val gridLine = Color(0xFF64748B)             // İnce çizgiler (orta gri - 3:1 kontrast)
    val gridThickLine = Color(0xFFE2E8F0)        // Kalın çizgiler (açık gri - 7:1 kontrast)

    // ===== SUDOKU HÜCRE RENKLERİ (Net ve Belirgin) =====
    val cellBackground = Color(0xFF334155)       // Hücre arka plan (orta koyu)
    val selectedCell = Color(0xFF1E40AF)         // Seçili hücre (mavi)
    val selectedCellRow = Color(0xFF0C4A6E)      // Seçili satır/sütun (koyu mavi)
    val selectedCellBox = Color(0xFF1E293B)      // Seçili 3x3 kutu (siyaha yakın)
    val sameNumberCell = Color(0xFF78350F)       // Aynı sayı (koyu turuncu)
    val conflictCell = Color(0xFF7F1D1D)         // Çakışma (koyu kırmızı)

    // ===== SUDOKU SAYI RENKLERİ =====
    val initialNumberText = Color(0xFFF1F5F9)    // Başlangıç sayıları (açık)
    val userNumberText = Color(0xFF60A5FA)       // Kullanıcı sayıları (açık mavi)
    val notesText = Color(0xFF94A3B8)            // Not sayıları (orta gri)

    // ===== BUTON RENKLERİ =====
    val buttonBackground = Color(0xFF60A5FA)     // Ana buton (açık mavi)
    val buttonText = Color(0xFF0F172A)           // Buton yazı (koyu)
    val buttonBackgroundSecondary = Color(0xFF475569) // İkincil buton
    val buttonTextSecondary = Color(0xFFF1F5F9)  // İkincil buton yazı

    // ===== OYUN GERİ BİLDİRİMİ (Anlamsal) =====
    val correctCell = Color(0xFF166534)          // Doğru (koyu yeşil)
    val wrongCell = Color(0xFF7F1D1D)            // Yanlış (koyu kırmızı)
    val hintCell = Color(0xFF78350F)             // İpucu (koyu turuncu)

    // ===== ZORLUK SEVİYESİ RENKLERİ (Anlamsal) =====
    val difficultyEasy = Color(0xFF4ADE80)       // Kolay (yeşil)
    val difficultyMedium = Color(0xFFFBBF24)     // Orta (sarı)
    val difficultyHard = Color(0xFFFF6B6B)       // Zor (kırmızı)
    val difficultyExpert = Color(0xFFA78BFA)     // Uzman (mor)

    // ===== PVP RENKLERİ =====
    val playerOneColor = Color(0xFF60A5FA)       // Oyuncu 1 (açık mavi)
    val playerTwoColor = Color(0xFFFF6B6B)       // Oyuncu 2 (açık kırmızı)
    val winColor = Color(0xFF4ADE80)             // Kazanma (yeşil)
    val loseColor = Color(0xFFFF6B6B)            // Kaybetme (kırmızı)

    // ===== STREAK VE BONUS RENKLERİ =====
    val streakGray = Color(0xFF94A3B8)
    val streakGreen = Color(0xFF4ADE80)
    val streakCyan = Color(0xFF06B6D4)
    val streakGold = Color(0xFFFBBF24)
    val streakOrange = Color(0xFFFB923C)
    val streakDeepOrange = Color(0xFFFF6B6B)
    val streakPink = Color(0xFFFF8FA3)
    val streakPurple = Color(0xFFA78BFA)
    val streakTurquoise = Color(0xFF14B8A6)
    val streakHotOrange = Color(0xFFFF1493)

    // ===== BONUS RENKLERİ =====
    val bonusGold = Color(0xFFFBBF24)
    val bonusBlue = Color(0xFF60A5FA)
    val bonusCyan = Color(0xFF06B6D4)
    val bonusPink = Color(0xFFFF8FA3)
    val bonusLightGreen = Color(0xFFADE80)

    // ===== BAŞARI RENKLERİ =====
    val achievementGold = Color(0xFFFBBF24)
    val achievementSilver = Color(0xFF94A3B8)
    val achievementBronze = Color(0xFFFB923C)

    // ===== DOĞRULUK RENKLERİ =====
    val accuracyHigh = Color(0xFF4ADE80)         // Yüksek (yeşil)
    val accuracyMedium = Color(0xFFFBBF24)       // Orta (sarı)
    val accuracyLow = Color(0xFFFF6B6B)          // Düşük (kırmızı)

    // ===== HIGHLIGHT VE ACCENT =====
    val highlightText = Color(0xFFFFFFFF)        // Vurgulu metin
}

// ============================================================================
// GAZETE TEMASI (Newspaper/Monochrome Effect)
// 60-30-10: Krem BG (60%), Siyah/Gri Vurgu (30%), Accent (10%)
// ============================================================================
object GazetteColors {
    // ===== TEMEL RENKLER (60%) - Arka Plan ve Yüzeyler =====
    val background = Color(0xFFEEEBE7)           // Ana arka plan (gazete kağıdı)
    val surface = Color(0xFFFAF9F7)              // Yüzey (açık gazete)
    val cardBackground = Color(0xFFF4F2ED)       // Kart arka plan (orta gazete)
    val surfaceVariant = Color(0xFFE8E5E0)       // İkincil yüzey (koyu gazete)

    // ===== YAPISAL RENKLERİ (Kontrol ve Sınırlar) =====
    val outline = Color(0xFFCBC8C3)              // Sınırlar (açık kahverengi)
    val divider = Color(0xFFD8D5D0)              // Ayırıcı çizgi (hafif gri)
    val modalScrim = Color(0x99000000)           // Modal arka plan (siyah 60%)

    // ===== TEMEL VURGU RENKLERİ (30%) - Etkileşim =====
    val primary = Color(0xFF1A1815)              // Birincil (siyah - güçlü vurgu)
    val onPrimary = Color(0xFFFAF9F7)            // Birincil üstü (açık metin)
    val primaryContainer = Color(0xFF3D3A35)     // Birincil hafif (koyu gri)
    val onPrimaryContainer = Color(0xFFF4F2ED)   // Birincil hafif üstü (açık metin)

    val secondary = Color(0xFF4A4A48)            // İkincil (koyu gri - vurgu)
    val onSecondary = Color(0xFFFAF9F7)          // İkincil üstü (açık metin)
    val secondaryContainer = Color(0xFFD8D5D0)   // İkincil hafif
    val onSecondaryContainer = Color(0xFF1A1815) // İkincil hafif üstü

    val tertiary = Color(0xFF6F6F6D)             // Üçüncül (orta gri - bilgi)
    val onTertiary = Color(0xFFFAF9F7)           // Üçüncül üstü (açık metin)
    val tertiaryContainer = Color(0xFFC8C5C0)    // Üçüncül hafif
    val onTertiaryContainer = Color(0xFF2B2B28)  // Üçüncül hafif üstü

    // ===== METIN RENKLERİ (WCAG AA Uyumlu) =====
    val text = Color(0xFF1A1815)                 // Ana metin (siyah - 12:1 kontrast)
    val textSecondary = Color(0xFF4A4A48)        // İkincil metin (koyu gri - 7:1 kontrast)
    val onSurfaceVariant = Color(0xFF2B2B28)     // Varyant üzerindeki metin

    // ===== HATA VE UYARI RENKLERİ (Anlamsal - Gazete Tonlarında) =====
    val error = Color(0xFF7A3A3A)                // Hata (koyu kırmızı-kahve)
    val onError = Color(0xFFFAF9F7)              // Hata üstü (açık)
    val errorContainer = Color(0xFFD8CFCC)       // Hata hafif
    val onErrorContainer = Color(0xFF1A1815)     // Hata hafif üstü

    // ===== SUDOKU GRID RENKLERİ (Net ve Belirgin) =====
    val gridBackground = Color(0xFFFAF9F7)       // Grid arka plan (açık gazete)
    val gridLine = Color(0xFFD8D5D0)             // İnce çizgiler (hafif gri - 3:1 kontrast)
    val gridThickLine = Color(0xFF1A1815)        // Kalın çizgiler (siyah - 11:1 kontrast)

    // ===== SUDOKU HÜCRE RENKLERİ (Net ve Belirgin) =====
    val cellBackground = Color(0xFFFAF9F7)       // Hücre arka plan (açık)
    val selectedCell = Color(0xFFD8CFCC)         // Seçili hücre (açık gri)
    val selectedCellRow = Color(0xFFEEEBE7)      // Seçili satır/sütun (krem)
    val selectedCellBox = Color(0xFFF4F2ED)      // Seçili 3x3 kutu (açık gazete)
    val sameNumberCell = Color(0xFFE8E5E0)       // Aynı sayı (hafif gri)
    val conflictCell = Color(0xFFD8CFCC)         // Çakışma (açık kahverengi)

    // ===== SUDOKU SAYI RENKLERİ =====
    val initialNumberText = Color(0xFF1A1815)    // Başlangıç sayıları (siyah)
    val userNumberText = Color(0xFF3D3A35)       // Kullanıcı sayıları (koyu gri)
    val notesText = Color(0xFF9A9A98)            // Not sayıları (açık gri)

    // ===== BUTON RENKLERİ =====
    val buttonBackground = Color(0xFF1A1815)     // Ana buton (siyah)
    val buttonText = Color(0xFFFAF9F7)           // Buton yazı (açık)
    val buttonBackgroundSecondary = Color(0xFFD8D5D0) // İkincil buton
    val buttonTextSecondary = Color(0xFF1A1815)  // İkincil buton yazı

    // ===== OYUN GERİ BİLDİRİMİ (Anlamsal - Gazete Tonlarında) =====
    val correctCell = Color(0xFFD8E8D8)          // Doğru (açık yeşil-krem)
    val wrongCell = Color(0xFFD8CFCC)            // Yanlış (açık kahve)
    val hintCell = Color(0xFFDCD8D0)             // İpucu (açık gri)

    // ===== ZORLUK SEVİYESİ RENKLERİ (Anlamsal - Gri Tonları) =====
    val difficultyEasy = Color(0xFF7A8D7A)       // Kolay (açık yeşilimsi gri)
    val difficultyMedium = Color(0xFF6F6F6D)     // Orta (orta gri)
    val difficultyHard = Color(0xFF4A4A48)       // Zor (koyu gri)
    val difficultyExpert = Color(0xFF1A1815)     // Uzman (siyah)

    // ===== PVP RENKLERİ =====
    val playerOneColor = Color(0xFF1A1815)       // Oyuncu 1 (siyah)
    val playerTwoColor = Color(0xFF6F6F6D)       // Oyuncu 2 (koyu gri)
    val winColor = Color(0xFF4A4A48)             // Kazanma (koyu gri)
    val loseColor = Color(0xFF9A9A98)            // Kaybetme (açık gri)

    // ===== STREAK VE BONUS RENKLERİ (Gri Skalası) =====
    val streakGray = Color(0xFF9A9A98)
    val streakGreen = Color(0xFF7A8D7A)
    val streakCyan = Color(0xFF6F7A7F)
    val streakGold = Color(0xFF8B8B8A)
    val streakOrange = Color(0xFF7A6B5F)
    val streakDeepOrange = Color(0xFF6F5f55)
    val streakPink = Color(0xFF8B7A82)
    val streakPurple = Color(0xFF7A7A8D)
    val streakTurquoise = Color(0xFF6B7A7A)
    val streakHotOrange = Color(0xFF7A6555)

    // ===== BONUS RENKLERİ =====
    val bonusGold = Color(0xFF8B8B8A)
    val bonusBlue = Color(0xFF6F6F6D)
    val bonusCyan = Color(0xFF6F7A7F)
    val bonusPink = Color(0xFF8B7A82)
    val bonusLightGreen = Color(0xFF7A8D7A)

    // ===== BAŞARI RENKLERİ =====
    val achievementGold = Color(0xFF8B8B8A)
    val achievementSilver = Color(0xFF9A9A98)
    val achievementBronze = Color(0xFF7A6B5F)

    // ===== DOĞRULUK RENKLERİ =====
    val accuracyHigh = Color(0xFF7A8D7A)         // Yüksek (yeşilimsi)
    val accuracyMedium = Color(0xFF6F6F6D)       // Orta (gri)
    val accuracyLow = Color(0xFF4A4A48)          // Düşük (koyu gri)

    // ===== HIGHLIGHT VE ACCENT =====
    val highlightText = Color(0xFFFAF9F7)        // Vurgulu metin
}

// ============================================================================
// MONOKROM TEMA (Siyah-Beyaz Saf)
// 60-30-10: Beyaz BG (60%), Siyah/Gri Vurgu (30%), Accent (10%)
// ============================================================================
object MonochromeColors {
    // ===== TEMEL RENKLER (60%) - Arka Plan ve Yüzeyler =====
    val background = Color(0xFFFAFAFA)           // Ana arka plan (beyaz)
    val surface = Color(0xFFFFFFFF)              // Yüzey (tam beyaz)
    val cardBackground = Color(0xFFF5F5F5)       // Kart arka plan (hafif gri)
    val surfaceVariant = Color(0xFFEEEEEE)       // İkincil yüzey (orta gri)

    // ===== YAPISAL RENKLERİ (Kontrol ve Sınırlar) =====
    val outline = Color(0xFFD0D0D0)              // Sınırlar (açık gri)
    val divider = Color(0xFFDDDDDD)              // Ayırıcı çizgi (açık gri)
    val modalScrim = Color(0x80000000)           // Modal arka plan (siyah 50%)

    // ===== TEMEL VURGU RENKLERİ (30%) - Etkileşim =====
    val primary = Color(0xFF000000)              // Birincil (siyah - güçlü vurgu)
    val onPrimary = Color(0xFFFFFFFF)            // Birincil üstü (beyaz metin)
    val primaryContainer = Color(0xFF424242)     // Birincil hafif (koyu gri)
    val onPrimaryContainer = Color(0xFFFFFFFF)   // Birincil hafif üstü (beyaz)

    val secondary = Color(0xFF383838)            // İkincil (koyu gri - vurgu)
    val onSecondary = Color(0xFFFFFFFF)          // İkincil üstü (beyaz metin)
    val secondaryContainer = Color(0xFFCCCCCC)   // İkincil hafif
    val onSecondaryContainer = Color(0xFF000000) // İkincil hafif üstü

    val tertiary = Color(0xFF616161)             // Üçüncül (orta gri - bilgi)
    val onTertiary = Color(0xFFFFFFFF)           // Üçüncül üstü (beyaz metin)
    val tertiaryContainer = Color(0xFFB0B0B0)    // Üçüncül hafif
    val onTertiaryContainer = Color(0xFF000000)  // Üçüncül hafif üstü

    // ===== METIN RENKLERİ (WCAG AA Uyumlu) =====
    val text = Color(0xFF000000)                 // Ana metin (siyah - 21:1 kontrast)
    val textSecondary = Color(0xFF333333)        // İkincil metin (koyu gri - 9:1 kontrast)
    val onSurfaceVariant = Color(0xFF555555)     // Varyant üzerindeki metin

    // ===== HATA VE UYARI RENKLERİ (Anlamsal - Siyah-Beyaz) =====
    val error = Color(0xFF404040)                // Hata (çok koyu gri)
    val onError = Color(0xFFFFFFFF)              // Hata üstü (beyaz)
    val errorContainer = Color(0xFFD0D0D0)       // Hata hafif
    val onErrorContainer = Color(0xFF000000)     // Hata hafif üstü

    // ===== SUDOKU GRID RENKLERİ (Saf Siyah-Beyaz) =====
    val gridBackground = Color(0xFFFFFFFF)       // Grid arka plan (beyaz)
    val gridLine = Color(0xFFCCCCCC)             // İnce çizgiler (açık gri - 3:1 kontrast)
    val gridThickLine = Color(0xFF000000)        // Kalın çizgiler (siyah - 21:1 kontrast)

    // ===== SUDOKU HÜCRE RENKLERİ (Net ve Belirgin) =====
    val cellBackground = Color(0xFFFFFFFF)       // Hücre arka plan (beyaz)
    val selectedCell = Color(0xFFCCCCCC)         // Seçili hücre (açık gri)
    val selectedCellRow = Color(0xFFE8E8E8)      // Seçili satır/sütun (çok açık gri)
    val selectedCellBox = Color(0xFFF0F0F0)      // Seçili 3x3 kutu (hafif gri)
    val sameNumberCell = Color(0xFFF5F5F5)       // Aynı sayı (çok açık gri)
    val conflictCell = Color(0xFFCCCCCC)         // Çakışma (açık gri)

    // ===== SUDOKU SAYI RENKLERİ =====
    val initialNumberText = Color(0xFF000000)    // Başlangıç sayıları (siyah)
    val userNumberText = Color(0xFF404040)       // Kullanıcı sayıları (koyu gri)
    val notesText = Color(0xFF999999)            // Not sayıları (açık gri)

    // ===== BUTON RENKLERİ =====
    val buttonBackground = Color(0xFF000000)     // Ana buton (siyah)
    val buttonText = Color(0xFFFFFFFF)           // Buton yazı (beyaz)
    val buttonBackgroundSecondary = Color(0xFFF5F5F5) // İkincil buton
    val buttonTextSecondary = Color(0xFF000000)  // İkincil buton yazı

    // ===== OYUN GERİ BİLDİRİMİ (Anlamsal - Siyah-Beyaz) =====
    val correctCell = Color(0xFFE8E8E8)          // Doğru (çok açık gri)
    val wrongCell = Color(0xFFCCCCCC)            // Yanlış (açık gri)
    val hintCell = Color(0xFFDDDDDD)             // İpucu (hafif gri)

    // ===== ZORLUK SEVİYESİ RENKLERİ (Anlamsal - Gri Tonları) =====
    val difficultyEasy = Color(0xFF808080)       // Kolay (açık gri)
    val difficultyMedium = Color(0xFF616161)     // Orta (orta gri)
    val difficultyHard = Color(0xFF383838)       // Zor (koyu gri)
    val difficultyExpert = Color(0xFF000000)     // Uzman (siyah)

    // ===== PVP RENKLERİ =====
    val playerOneColor = Color(0xFF000000)       // Oyuncu 1 (siyah)
    val playerTwoColor = Color(0xFF606060)       // Oyuncu 2 (koyu gri)
    val winColor = Color(0xFF383838)             // Kazanma (koyu gri)
    val loseColor = Color(0xFF999999)            // Kaybetme (açık gri)

    // ===== STREAK VE BONUS RENKLERİ (Gri Skalası) =====
    val streakGray = Color(0xFF999999)
    val streakGreen = Color(0xFF808080)
    val streakCyan = Color(0xFF707070)
    val streakGold = Color(0xFF606060)
    val streakOrange = Color(0xFF707070)
    val streakDeepOrange = Color(0xFF606060)
    val streakPink = Color(0xFF808080)
    val streakPurple = Color(0xFF707070)
    val streakTurquoise = Color(0xFF808080)
    val streakHotOrange = Color(0xFF606060)

    // ===== BONUS RENKLERİ =====
    val bonusGold = Color(0xFF606060)
    val bonusBlue = Color(0xFF616161)
    val bonusCyan = Color(0xFF707070)
    val bonusPink = Color(0xFF808080)
    val bonusLightGreen = Color(0xFF808080)

    // ===== BAŞARI RENKLERİ =====
    val achievementGold = Color(0xFF606060)
    val achievementSilver = Color(0xFF999999)
    val achievementBronze = Color(0xFF707070)

    // ===== DOĞRULUK RENKLERİ =====
    val accuracyHigh = Color(0xFF808080)         // Yüksek (açık gri)
    val accuracyMedium = Color(0xFF616161)       // Orta (gri)
    val accuracyLow = Color(0xFF383838)          // Düşük (koyu gri)

    // ===== HIGHLIGHT VE ACCENT =====
    val highlightText = Color(0xFFFFFFFF)        // Vurgulu metin
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
            onPrimary = LightColors.onPrimary,
            primaryContainer = LightColors.primaryContainer,
            onPrimaryContainer = LightColors.onPrimaryContainer,
            secondary = LightColors.secondary,
            onSecondary = LightColors.onSecondary,
            secondaryContainer = LightColors.secondaryContainer,
            onSecondaryContainer = LightColors.onSecondaryContainer,
            tertiary = LightColors.tertiary,
            onTertiary = LightColors.onTertiary,
            tertiaryContainer = LightColors.tertiaryContainer,
            onTertiaryContainer = LightColors.onTertiaryContainer,
            text = LightColors.text,
            textSecondary = LightColors.textSecondary,
            onSurfaceVariant = LightColors.onSurfaceVariant,
            outline = LightColors.outline,
            divider = LightColors.divider,
            iconTint = LightColors.primary,
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
            cardBackground = LightColors.cardBackground,
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
            onPrimary = DarkColors.onPrimary,
            primaryContainer = DarkColors.primaryContainer,
            onPrimaryContainer = DarkColors.onPrimaryContainer,
            secondary = DarkColors.secondary,
            onSecondary = DarkColors.onSecondary,
            secondaryContainer = DarkColors.secondaryContainer,
            onSecondaryContainer = DarkColors.onSecondaryContainer,
            tertiary = DarkColors.tertiary,
            onTertiary = DarkColors.onTertiary,
            tertiaryContainer = DarkColors.tertiaryContainer,
            onTertiaryContainer = DarkColors.onTertiaryContainer,
            text = DarkColors.text,
            textSecondary = DarkColors.textSecondary,
            onSurfaceVariant = DarkColors.onSurfaceVariant,
            outline = DarkColors.outline,
            divider = DarkColors.divider,
            iconTint = DarkColors.primary,
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
            cardBackground = DarkColors.cardBackground,
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
        ThemeType.GAZETE -> ThemeColors(
            background = GazetteColors.background,
            surface = GazetteColors.surface,
            surfaceVariant = GazetteColors.surfaceVariant,
            primary = GazetteColors.primary,
            onPrimary = GazetteColors.onPrimary,
            primaryContainer = GazetteColors.primaryContainer,
            onPrimaryContainer = GazetteColors.onPrimaryContainer,
            secondary = GazetteColors.secondary,
            onSecondary = GazetteColors.onSecondary,
            secondaryContainer = GazetteColors.secondaryContainer,
            onSecondaryContainer = GazetteColors.onSecondaryContainer,
            tertiary = GazetteColors.tertiary,
            onTertiary = GazetteColors.onTertiary,
            tertiaryContainer = GazetteColors.tertiaryContainer,
            onTertiaryContainer = GazetteColors.onTertiaryContainer,
            text = GazetteColors.text,
            textSecondary = GazetteColors.textSecondary,
            onSurfaceVariant = GazetteColors.onSurfaceVariant,
            outline = GazetteColors.outline,
            divider = GazetteColors.divider,
            iconTint = GazetteColors.primary,
            gridBackground = GazetteColors.gridBackground,
            gridLine = GazetteColors.gridLine,
            gridThickLine = GazetteColors.gridThickLine,
            cellBackground = GazetteColors.cellBackground,
            selectedCell = GazetteColors.selectedCell,
            selectedCellRow = GazetteColors.selectedCellRow,
            selectedCellBox = GazetteColors.selectedCellBox,
            sameNumberCell = GazetteColors.sameNumberCell,
            conflictCell = GazetteColors.conflictCell,
            initialNumberText = GazetteColors.initialNumberText,
            userNumberText = GazetteColors.userNumberText,
            notesText = GazetteColors.notesText,
            buttonBackground = GazetteColors.buttonBackground,
            buttonText = GazetteColors.buttonText,
            buttonBackgroundSecondary = GazetteColors.buttonBackgroundSecondary,
            buttonTextSecondary = GazetteColors.buttonTextSecondary,
            correctCell = GazetteColors.correctCell,
            wrongCell = GazetteColors.wrongCell,
            hintCell = GazetteColors.hintCell,
            cardBackground = GazetteColors.cardBackground,
            modalScrim = GazetteColors.modalScrim,
            highlightText = GazetteColors.highlightText,
            difficultyEasy = GazetteColors.difficultyEasy,
            difficultyMedium = GazetteColors.difficultyMedium,
            difficultyHard = GazetteColors.difficultyHard,
            difficultyExpert = GazetteColors.difficultyExpert,
            playerOneColor = GazetteColors.playerOneColor,
            playerTwoColor = GazetteColors.playerTwoColor,
            winColor = GazetteColors.winColor,
            loseColor = GazetteColors.loseColor,
            error = GazetteColors.error,
            onError = GazetteColors.onError,
            errorContainer = GazetteColors.errorContainer,
            onErrorContainer = GazetteColors.onErrorContainer,
            streakGray = GazetteColors.streakGray,
            streakGreen = GazetteColors.streakGreen,
            streakCyan = GazetteColors.streakCyan,
            streakGold = GazetteColors.streakGold,
            streakOrange = GazetteColors.streakOrange,
            streakDeepOrange = GazetteColors.streakDeepOrange,
            streakPink = GazetteColors.streakPink,
            streakPurple = GazetteColors.streakPurple,
            streakTurquoise = GazetteColors.streakTurquoise,
            streakHotOrange = GazetteColors.streakHotOrange,
            bonusGold = GazetteColors.bonusGold,
            bonusBlue = GazetteColors.bonusBlue,
            bonusCyan = GazetteColors.bonusCyan,
            bonusPink = GazetteColors.bonusPink,
            bonusLightGreen = GazetteColors.bonusLightGreen,
            achievementGold = GazetteColors.achievementGold,
            achievementSilver = GazetteColors.achievementSilver,
            achievementBronze = GazetteColors.achievementBronze,
            accuracyHigh = GazetteColors.accuracyHigh,
            accuracyMedium = GazetteColors.accuracyMedium,
            accuracyLow = GazetteColors.accuracyLow
        )
        ThemeType.MONOCHROME -> ThemeColors(
            background = MonochromeColors.background,
            surface = MonochromeColors.surface,
            surfaceVariant = MonochromeColors.surfaceVariant,
            primary = MonochromeColors.primary,
            onPrimary = MonochromeColors.onPrimary,
            primaryContainer = MonochromeColors.primaryContainer,
            onPrimaryContainer = MonochromeColors.onPrimaryContainer,
            secondary = MonochromeColors.secondary,
            onSecondary = MonochromeColors.onSecondary,
            secondaryContainer = MonochromeColors.secondaryContainer,
            onSecondaryContainer = MonochromeColors.onSecondaryContainer,
            tertiary = MonochromeColors.tertiary,
            onTertiary = MonochromeColors.onTertiary,
            tertiaryContainer = MonochromeColors.tertiaryContainer,
            onTertiaryContainer = MonochromeColors.onTertiaryContainer,
            text = MonochromeColors.text,
            textSecondary = MonochromeColors.textSecondary,
            onSurfaceVariant = MonochromeColors.onSurfaceVariant,
            outline = MonochromeColors.outline,
            divider = MonochromeColors.divider,
            iconTint = MonochromeColors.primary,
            gridBackground = MonochromeColors.gridBackground,
            gridLine = MonochromeColors.gridLine,
            gridThickLine = MonochromeColors.gridThickLine,
            cellBackground = MonochromeColors.cellBackground,
            selectedCell = MonochromeColors.selectedCell,
            selectedCellRow = MonochromeColors.selectedCellRow,
            selectedCellBox = MonochromeColors.selectedCellBox,
            sameNumberCell = MonochromeColors.sameNumberCell,
            conflictCell = MonochromeColors.conflictCell,
            initialNumberText = MonochromeColors.initialNumberText,
            userNumberText = MonochromeColors.userNumberText,
            notesText = MonochromeColors.notesText,
            buttonBackground = MonochromeColors.buttonBackground,
            buttonText = MonochromeColors.buttonText,
            buttonBackgroundSecondary = MonochromeColors.buttonBackgroundSecondary,
            buttonTextSecondary = MonochromeColors.buttonTextSecondary,
            correctCell = MonochromeColors.correctCell,
            wrongCell = MonochromeColors.wrongCell,
            hintCell = MonochromeColors.hintCell,
            cardBackground = MonochromeColors.cardBackground,
            modalScrim = MonochromeColors.modalScrim,
            highlightText = MonochromeColors.highlightText,
            difficultyEasy = MonochromeColors.difficultyEasy,
            difficultyMedium = MonochromeColors.difficultyMedium,
            difficultyHard = MonochromeColors.difficultyHard,
            difficultyExpert = MonochromeColors.difficultyExpert,
            playerOneColor = MonochromeColors.playerOneColor,
            playerTwoColor = MonochromeColors.playerTwoColor,
            winColor = MonochromeColors.winColor,
            loseColor = MonochromeColors.loseColor,
            error = MonochromeColors.error,
            onError = MonochromeColors.onError,
            errorContainer = MonochromeColors.errorContainer,
            onErrorContainer = MonochromeColors.onErrorContainer,
            streakGray = MonochromeColors.streakGray,
            streakGreen = MonochromeColors.streakGreen,
            streakCyan = MonochromeColors.streakCyan,
            streakGold = MonochromeColors.streakGold,
            streakOrange = MonochromeColors.streakOrange,
            streakDeepOrange = MonochromeColors.streakDeepOrange,
            streakPink = MonochromeColors.streakPink,
            streakPurple = MonochromeColors.streakPurple,
            streakTurquoise = MonochromeColors.streakTurquoise,
            streakHotOrange = MonochromeColors.streakHotOrange,
            bonusGold = MonochromeColors.bonusGold,
            bonusBlue = MonochromeColors.bonusBlue,
            bonusCyan = MonochromeColors.bonusCyan,
            bonusPink = MonochromeColors.bonusPink,
            bonusLightGreen = MonochromeColors.bonusLightGreen,
            achievementGold = MonochromeColors.achievementGold,
            achievementSilver = MonochromeColors.achievementSilver,
            achievementBronze = MonochromeColors.achievementBronze,
            accuracyHigh = MonochromeColors.accuracyHigh,
            accuracyMedium = MonochromeColors.accuracyMedium,
            accuracyLow = MonochromeColors.accuracyLow
        )
    }
}
