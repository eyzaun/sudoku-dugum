package com.extremesudoku.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ============================================================================
 * MERKEZI TASARIM KONTROL PANELİ
 * ============================================================================
 * 
 * Bu dosyadan tüm uygulamanın tasarımını kontrol edebilirsiniz:
 * - Boyutlar (width, height, padding, margin)
 * - Yuvarlaklıklar (corner radius)
 * - Yazı boyutları (font size)
 * - Boşluklar (spacing)
 * - Gölgeler (elevation)
 * 
 * Bir değeri değiştirin, tüm uygulama otomatik güncellenir!
 * ============================================================================
 */

// ============================================================================
// BOYUTLAR (Dimensions)
// ============================================================================
object AppDimensions {
    
    // KART BOYUTLARI (Card Dimensions)
    val cardElevation: Dp = 2.dp                    // Kart gölge yüksekliği
    val cardPadding: Dp = 16.dp                     // Kart içi boşluk
    val cardMinHeight: Dp = 100.dp                  // Kart minimum yükseklik
    val cardCornerRadius: Dp = 12.dp                // Kart köşe yuvarlaklığı
    val cardBorderWidth: Dp = 1.dp                  // Kart border kalınlığı
    
    // BUTON BOYUTLARI (Button Dimensions)
    val buttonHeight: Dp = 52.dp                    // Standart buton yüksekliği
    val buttonHeightSmall: Dp = 44.dp               // Küçük buton yüksekliği
    val buttonHeightLarge: Dp = 60.dp               // Büyük buton yüksekliği
    val buttonPadding: Dp = 16.dp                   // Buton içi boşluk
    val buttonCornerRadius: Dp = 8.dp               // Buton köşe yuvarlaklığı
    val buttonBorderWidth: Dp = 2.dp                // Outline buton border kalınlığı
    val buttonElevation: Dp = 2.dp                  // Elevated buton gölge
    val buttonPressedElevation: Dp = 6.dp           // Basılı buton gölge
    val buttonRippleRadius: Dp = 24.dp              // Buton ripple efekt yarıçapı
    
    // ZORLUK BUTONLARI (Difficulty Buttons)
    val difficultyButtonHeight: Dp = 72.dp          // Zorluk buton yüksekliği
    val difficultyButtonWidth: Dp = 150.dp          // Zorluk buton genişliği
    val difficultyButtonBorderWidth: Dp = 2.dp      // Zorluk buton border kalınlığı
    val difficultyButtonIconSize: Dp = 28.dp        // Zorluk buton icon boyutu
    
    // ICON BOYUTLARI (Icon Sizes)
    val iconSizeSmall: Dp = 18.dp                   // Küçük icon (stat icons)
    val iconSizeMedium: Dp = 24.dp                  // Orta icon (navigation icons)
    val iconSizeLarge: Dp = 32.dp                   // Büyük icon (feature icons)
    val iconSizeExtraLarge: Dp = 48.dp              // Çok büyük icon (modal icons)
    
    // BOŞLUKLAR (Spacing)
    val spacingExtraSmall: Dp = 4.dp                // Çok küçük boşluk
    val spacingSmall: Dp = 8.dp                     // Küçük boşluk
    val spacingMedium: Dp = 16.dp                   // Orta boşluk (varsayılan)
    val spacingLarge: Dp = 24.dp                    // Büyük boşluk
    val spacingExtraLarge: Dp = 32.dp               // Çok büyük boşluk
    
    // EKRAN PADDING (Screen Padding)
    val screenPaddingHorizontal: Dp = 16.dp         // Yatay ekran kenar boşluğu
    val screenPaddingVertical: Dp = 16.dp           // Dikey ekran kenar boşluğu
    val screenPaddingTop: Dp = 24.dp                // Üst ekran boşluğu (status bar için)
    val screenPaddingBottom: Dp = 16.dp             // Alt ekran boşluğu
    
    // SUDOKU GRID (Sudoku Grid)
    val gridPadding: Dp = 12.dp                     // Grid etrafındaki boşluk
    val gridLineWidth: Dp = 1.dp                    // İnce çizgi kalınlığı
    val gridThickLineWidth: Dp = 3.dp               // Kalın çizgi kalınlığı (3x3 kutular)
    val gridConflictLineWidth: Dp = 2.dp            // Conflict çizgi kalınlığı
    val gridCellSize: Dp = 36.dp                    // Grid hücre boyutu
    val gridCellSizeSmall: Dp = 32.dp               // Küçük grid hücre boyutu
    val gridCellSizeLarge: Dp = 40.dp               // Büyük grid hücre boyutu
    val gridHighlightWidth: Dp = 2.dp               // Hücre vurgu çizgi kalınlığı
    val gridBorderRadius: Dp = 4.dp                 // Grid köşe yuvarlaklığı
    val gridElevation: Dp = 2.dp                    // Grid gölge
    val gridOverlayOpacity: Float = 0.15f           // Grid overlay saydamlığı
    val gridAnimationDuration: Int = 200            // Grid animasyon süresi (ms)
    val gridSelectionAnimDuration: Int = 150        // Hücre seçim animasyon süresi
    val gridNumberAnimDuration: Int = 300           // Sayı yerleştirme animasyon süresi
    
    // NUMBER PAD (Number Pad - Sayı Klavyesi)
    val numberPadButtonSize: Dp = 44.dp             // Sayı butonu boyutu
    val numberPadButtonSizeSmall: Dp = 38.dp        // Küçük sayı butonu
    val numberPadSpacing: Dp = 8.dp                 // Sayı butonları arası boşluk
    val numberPadHeight: Dp = 56.dp                 // Number pad yüksekliği
    val numberPadContentPadding: Dp = 0.dp          // Number pad button içi padding
    val numberPadBorderWidth: Dp = 1.dp             // Number pad border kalınlığı
    val numberPadCornerRadius: Dp = 8.dp            // Number pad köşe yuvarlaklığı
    val numberPadElevation: Dp = 1.dp               // Number pad gölge
    val numberPadRippleRadius: Dp = 20.dp           // Number pad ripple efekt
    
    // GAME CONTROLS (Oyun Kontrolleri)
    val gameControlIconSize: Dp = 26.dp             // Game control icon boyutu
    val gameControlPadding: Dp = 8.dp               // Game control padding
    val gameControlSpacing: Dp = 4.dp               // Game control spacing
    val gameControlButtonSize: Dp = 48.dp           // Game control button boyutu
    val gameControlCornerRadius: Dp = 10.dp         // Game control köşe yuvarlaklığı
    
    // DIALOG BOYUTLARI (Dialog Dimensions)
    val dialogPadding: Dp = 24.dp                   // Dialog içi boşluk
    val dialogPaddingMedium: Dp = 16.dp             // Orta dialog padding
    val dialogMinWidth: Dp = 280.dp                 // Dialog minimum genişlik
    val dialogMaxWidth: Dp = 560.dp                 // Dialog maksimum genişlik
    val dialogMaxHeight: Dp = 400.dp                // Dialog maksimum yükseklik
    val dialogIconSize: Dp = 56.dp                  // Dialog icon boyutu
    val dialogIconSizeMedium: Dp = 44.dp            // Orta dialog icon
    val dialogCornerRadius: Dp = 16.dp              // Dialog köşe yuvarlaklığı
    val dialogElevation: Dp = 8.dp                  // Dialog gölge
    val dialogScrimOpacity: Float = 0.5f            // Dialog arka plan karartma
    val dialogButtonSpacing: Dp = 12.dp             // Dialog button arası boşluk
    val dialogTitleBottomPadding: Dp = 16.dp        // Dialog başlık alt boşluk
    
    // DIVIDER (Ayırıcı Çizgi)
    val dividerThickness: Dp = 1.dp                 // Ayırıcı çizgi kalınlığı
    val dividerPadding: Dp = 0.dp                   // Divider yatay padding
    val dividerVerticalPadding: Dp = 8.dp           // Divider dikey padding
    
    // APP BAR (Üst Bar)
    val appBarHeight: Dp = 56.dp                    // App bar yüksekliği
    val appBarElevation: Dp = 2.dp                  // App bar gölge
    val appBarIconSize: Dp = 24.dp                  // App bar icon boyutu
    val appBarHorizontalPadding: Dp = 4.dp          // App bar yatay padding
    val appBarTitleStartPadding: Dp = 16.dp         // App bar başlık sol padding
    
    // BOTTOM BAR (Alt Bar)
    val bottomBarHeight: Dp = 56.dp                 // Bottom bar yüksekliği
    val bottomBarElevation: Dp = 8.dp               // Bottom bar gölge
    val bottomBarIconSize: Dp = 24.dp               // Bottom bar icon boyutu
    val bottomBarSelectedIndicatorHeight: Dp = 3.dp  // Seçili gösterge yüksekliği
    
    // STATS CARD (İstatistik Kartı)
    val statsCardHeight: Dp = 110.dp                // Stats card yüksekliği
    val statsCardPadding: Dp = 16.dp                // Stats card içi boşluk
    val statsCardIconSize: Dp = 32.dp               // Stats card icon boyutu
    val statsCardCornerRadius: Dp = 12.dp           // Stats card köşe yuvarlaklığı
    val statsCardElevation: Dp = 2.dp               // Stats card gölge
    val statsCardSpacing: Dp = 12.dp                // Stats card içerik arası boşluk
    
    // SAVED GAME ITEM (Kayıtlı Oyun Öğesi)
    val savedGameItemHeight: Dp = 76.dp             // Kayıtlı oyun item yüksekliği
    val savedGameItemPadding: Dp = 12.dp            // Kayıtlı oyun item içi boşluk
    val savedGameItemCornerRadius: Dp = 10.dp       // Kayıtlı oyun köşe yuvarlaklığı
    val savedGameItemElevation: Dp = 1.dp           // Kayıtlı oyun gölge
    val savedGameItemIconSize: Dp = 20.dp           // Kayıtlı oyun icon boyutu
    val savedGameItemSpacing: Dp = 8.dp             // Kayıtlı oyun içerik spacing
    
    // STREAK INDICATOR (Seri Göstergesi)
    val streakIndicatorPadding: Dp = 10.dp          // Streak indicator içi boşluk
    val streakIconSize: Dp = 28.dp                  // Streak icon boyutu
    val streakBadgePadding: Dp = 6.dp               // Streak badge içi boşluk
    val streakBadgeCornerRadius: Dp = 8.dp          // Streak badge köşe yuvarlaklığı
    val streakFlameAnimDuration: Int = 500          // Streak flame animasyon süresi
    val streakCounterAnimDuration: Int = 300        // Streak sayaç animasyon süresi
    
    // SCORE DISPLAY (Skor Göstergesi)
    val scoreIconSize: Dp = 24.dp                   // Score icon boyutu
    val scoreIconSizeSmall: Dp = 20.dp              // Küçük score icon
    val scoreCardPadding: Dp = 12.dp                // Score card içi boşluk
    val scoreAnimationDuration: Int = 400           // Score animasyon süresi
    val scoreIncrementAnimDuration: Int = 600       // Score artış animasyon süresi
    
    // BONUS POPUP (Bonus Popup)
    val bonusPopupPadding: Dp = 12.dp               // Bonus popup içi boşluk
    val bonusPopupTopOffset: Dp = 80.dp             // Bonus popup üstten mesafe
    val bonusPointsTopOffset: Dp = 100.dp           // Bonus points üstten mesafe
    val bonusPopupCornerRadius: Dp = 12.dp          // Bonus popup köşe yuvarlaklığı
    val bonusPopupAnimDuration: Int = 1000          // Bonus popup animasyon süresi
    val bonusPopupFadeInDuration: Int = 200         // Bonus popup fade in süresi
    val bonusPopupFadeOutDuration: Int = 300        // Bonus popup fade out süresi
    
    // GAME COMPLETE DIALOG (Oyun Tamamlama Dialogu)
    val gameCompleteMaxHeight: Dp = 600.dp          // Game complete dialog max yüksekliği
    val gameCompleteIconSize: Dp = 48.dp            // Game complete icon boyutu
    val gameCompleteSpacing: Dp = 20.dp             // Game complete item spacing
    val gameCompleteSpacingMedium: Dp = 12.dp       // Game complete medium spacing
    val badgeWidth: Dp = 76.dp                      // Badge genişliği
    val badgePadding: Dp = 8.dp                     // Badge içi boşluk
    val gameCompleteStarSize: Dp = 32.dp            // Yıldız boyutu
    val gameCompleteConfettiDuration: Int = 3000    // Konfeti animasyon süresi
    
    // HOME SCREEN (Ana Ekran)
    val homeCardHeight: Dp = 180.dp                 // Home card yüksekliği
    val homeItemSpacing: Dp = 12.dp                 // Home item arası boşluk
    val homeLogoSize: Dp = 120.dp                   // Home logo boyutu
    val homeButtonWidth: Dp = 200.dp                // Home button genişliği
    val homeCardCornerRadius: Dp = 16.dp            // Home card köşe yuvarlaklığı
    val homeCardElevation: Dp = 4.dp                // Home card gölge
    
    // PROFILE & LEADERBOARD
    val profileAvatarSize: Dp = 72.dp               // Profile avatar boyutu
    val profileAvatarBorderWidth: Dp = 3.dp         // Avatar border kalınlığı
    val profileStatItemHeight: Dp = 56.dp           // Profile stat item yüksekliği
    val profileBadgeSize: Dp = 40.dp                // Profile badge boyutu
    val leaderboardItemHeight: Dp = 68.dp           // Leaderboard item yüksekliği
    val leaderboardRankSize: Dp = 36.dp             // Leaderboard rank boyutu
    val leaderboardIconSize: Dp = 28.dp             // Leaderboard icon boyutu
    val leaderboardItemCornerRadius: Dp = 10.dp     // Leaderboard item köşe yuvarlaklığı
    val leaderboardItemElevation: Dp = 1.dp         // Leaderboard item gölge
    
    // PVP COMPONENTS
    val pvpPlayerCardWidth: Dp = 160.dp             // PvP player card genişliği
    val pvpPlayerCardPadding: Dp = 12.dp            // PvP player card padding
    val pvpIconSize: Dp = 24.dp                     // PvP icon boyutu
    val pvpBorderWidth: Dp = 2.dp                   // PvP border kalınlığı
    val pvpTimerHeight: Dp = 4.dp                   // PvP timer bar yüksekliği
    val pvpVersusIconSize: Dp = 48.dp               // PvP versus icon boyutu
    val pvpProgressBarHeight: Dp = 6.dp             // PvP progress bar yüksekliği
    val pvpAnimationDuration: Int = 500             // PvP animasyon süresi
    
    // TEXT FIELD (Metin Alanları)
    val textFieldHeight: Dp = 56.dp                 // TextField yüksekliği
    val textFieldCornerRadius: Dp = 8.dp            // TextField köşe yuvarlaklığı
    val textFieldBorderWidth: Dp = 1.dp             // TextField border kalınlığı
    val textFieldPadding: Dp = 16.dp                // TextField içi padding
    val textFieldFocusedBorderWidth: Dp = 2.dp      // Focused border kalınlığı
    
    // SWITCH & CHECKBOX
    val switchWidth: Dp = 52.dp                     // Switch genişliği
    val switchHeight: Dp = 32.dp                    // Switch yüksekliği
    val switchThumbSize: Dp = 24.dp                 // Switch thumb boyutu
    val checkboxSize: Dp = 24.dp                    // Checkbox boyutu
    val checkboxCornerRadius: Dp = 4.dp             // Checkbox köşe yuvarlaklığı
    val checkboxBorderWidth: Dp = 2.dp              // Checkbox border kalınlığı
    
    // SLIDER
    val sliderHeight: Dp = 4.dp                     // Slider track yüksekliği
    val sliderThumbSize: Dp = 20.dp                 // Slider thumb boyutu
    val sliderActiveTrackHeight: Dp = 6.dp          // Active track yüksekliği
    
    // CHIP & TAG
    val chipHeight: Dp = 32.dp                      // Chip yüksekliği
    val chipPadding: Dp = 12.dp                     // Chip içi padding
    val chipCornerRadius: Dp = 16.dp                // Chip köşe yuvarlaklığı
    val chipIconSize: Dp = 18.dp                    // Chip icon boyutu
    val chipSpacing: Dp = 8.dp                      // Chip'ler arası boşluk
    
    // FAB (Floating Action Button)
    val fabSize: Dp = 56.dp                         // FAB boyutu
    val fabExtendedPadding: Dp = 16.dp              // Extended FAB padding
    val fabIconSize: Dp = 24.dp                     // FAB icon boyutu
    val fabElevation: Dp = 6.dp                     // FAB gölge
    val fabPressedElevation: Dp = 12.dp             // Basılı FAB gölge
    
    // SNACKBAR
    val snackbarPadding: Dp = 16.dp                 // Snackbar içi padding
    val snackbarCornerRadius: Dp = 8.dp             // Snackbar köşe yuvarlaklığı
    val snackbarElevation: Dp = 6.dp                // Snackbar gölge
    val snackbarMaxWidth: Dp = 600.dp               // Snackbar max genişlik
    val snackbarMinHeight: Dp = 48.dp               // Snackbar min yükseklik
    val snackbarActionPadding: Dp = 8.dp            // Snackbar action padding
    
    // TOOLTIP
    val tooltipPadding: Dp = 8.dp                   // Tooltip içi padding
    val tooltipCornerRadius: Dp = 4.dp              // Tooltip köşe yuvarlaklığı
    val tooltipElevation: Dp = 4.dp                 // Tooltip gölge
    val tooltipArrowSize: Dp = 8.dp                 // Tooltip ok boyutu
    
    // PROGRESS INDICATORS
    val progressBarHeight: Dp = 4.dp                // Linear progress yüksekliği
    val progressBarCornerRadius: Dp = 2.dp          // Progress köşe yuvarlaklığı
    val circularProgressSize: Dp = 48.dp            // Circular progress boyutu
    val circularProgressStrokeWidth: Dp = 4.dp      // Circular progress kalınlık
    
    // BADGE
    val badgeSize: Dp = 20.dp                       // Badge boyutu
    val badgeMinWidth: Dp = 20.dp                   // Badge minimum genişlik
    val badgePaddingHorizontal: Dp = 6.dp           // Badge yatay padding
    val badgeCornerRadius: Dp = 10.dp               // Badge köşe yuvarlaklığı
    val badgeWithTextMinWidth: Dp = 32.dp           // Yazılı badge min genişlik
    
    // TAB
    val tabHeight: Dp = 48.dp                       // Tab yüksekliği
    val tabMinWidth: Dp = 90.dp                     // Tab minimum genişlik
    val tabPadding: Dp = 12.dp                      // Tab içi padding
    val tabIndicatorHeight: Dp = 3.dp               // Tab indicator yüksekliği
    val tabIconSize: Dp = 24.dp                     // Tab icon boyutu
    val tabScrollableMinWidth: Dp = 72.dp           // Scrollable tab min genişlik
}

// ============================================================================
// YUVARLAKLIKLAR (Corner Radius / Shapes)
// ============================================================================
object AppShapes {
    
    // TEMEL SHAPES (Basic Shapes)
    val none = RoundedCornerShape(0.dp)                        // Köşesiz (keskin köşeler)
    val extraSmall = RoundedCornerShape(4.dp)                  // Çok az yuvarlaklık
    val small = RoundedCornerShape(8.dp)                       // Küçük yuvarlaklık
    val medium = RoundedCornerShape(12.dp)                     // Orta yuvarlaklık
    val large = RoundedCornerShape(16.dp)                      // Büyük yuvarlaklık
    val extraLarge = RoundedCornerShape(20.dp)                 // Çok büyük yuvarlaklık
    val full = RoundedCornerShape(50)                          // Tam yuvarlak (pill shape)
    
    // COMPONENT SHAPES (Bileşen Yuvarlaklıkları)
    val button = RoundedCornerShape(8.dp)                      // Buton yuvarlaklığı
    val card = RoundedCornerShape(12.dp)                       // Kart yuvarlaklığı
    val dialog = RoundedCornerShape(16.dp)                     // Dialog yuvarlaklığı
    val numberPadButton = RoundedCornerShape(8.dp)             // Number pad buton yuvarlaklığı
    val difficultyButton = RoundedCornerShape(12.dp)           // Zorluk butonu yuvarlaklığı
    val gridContainer = RoundedCornerShape(4.dp)               // Grid container yuvarlaklığı
    val chip = RoundedCornerShape(16.dp)                       // Chip/Tag yuvarlaklığı
    val statsCard = RoundedCornerShape(12.dp)                  // Stats card yuvarlaklığı
    val savedGameItem = RoundedCornerShape(10.dp)              // Kayıtlı oyun item yuvarlaklığı
    val streakIndicator = RoundedCornerShape(16.dp)            // Streak indicator yuvarlaklığı
    val streakBadge = RoundedCornerShape(8.dp)                 // Streak badge yuvarlaklığı
    val bonusPopup = RoundedCornerShape(12.dp)                 // Bonus popup yuvarlaklığı
    val comboMultiplier = RoundedCornerShape(16.dp)            // Combo multiplier yuvarlaklığı
    val gameComplete = RoundedCornerShape(20.dp)               // Game complete dialog yuvarlaklığı
    val badge = RoundedCornerShape(10.dp)                      // Badge yuvarlaklığı
}

// ============================================================================
// YAZI TİPLERİ VE BOYUTLARI (Typography)
// ============================================================================
object AppTypography {
    
    // YAZI BOYUTLARI (Font Sizes)
    val fontSizeExtraSmall: TextUnit = 10.sp        // Çok küçük yazı (caption, timestamps)
    val fontSizeSmall: TextUnit = 12.sp             // Küçük yazı (labels, hints)
    val fontSizeMedium: TextUnit = 14.sp            // Orta yazı (body text)
    val fontSizeLarge: TextUnit = 16.sp             // Büyük yazı (titles, headings)
    val fontSizeExtraLarge: TextUnit = 20.sp        // Çok büyük yazı (main titles)
    val fontSizeHuge: TextUnit = 24.sp              // Kocaman yazı (hero text)
    val fontSizeDisplay: TextUnit = 32.sp           // Display yazı (splash, logos)
    
    // SUDOKU SAYILARI (Sudoku Numbers)
    val sudokuNumberSize: TextUnit = 22.sp          // Normal sudoku sayı boyutu
    val sudokuNotesSize: TextUnit = 10.sp           // Not sayıları boyutu
    
    // TIMER (Zamanlayıcı)
    val timerSize: TextUnit = 16.sp                 // Timer yazı boyutu
    
    // STAT NUMBERS (İstatistik Sayıları)
    val statValueSize: TextUnit = 24.sp             // Stat değer boyutu (büyük sayılar)
    val statLabelSize: TextUnit = 12.sp             // Stat etiket boyutu
    
    // YAZI STİLLERİ (Text Styles) - Hazır kombinasyonlar
    val displayLarge = TextStyle(
        fontSize = fontSizeDisplay,
        fontWeight = FontWeight.Bold
    )
    
    val headlineLarge = TextStyle(
        fontSize = fontSizeHuge,
        fontWeight = FontWeight.Bold
    )
    
    val headlineMedium = TextStyle(
        fontSize = fontSizeExtraLarge,
        fontWeight = FontWeight.Bold
    )
    
    val titleLarge = TextStyle(
        fontSize = fontSizeLarge,
        fontWeight = FontWeight.Bold
    )
    
    val titleMedium = TextStyle(
        fontSize = fontSizeMedium,
        fontWeight = FontWeight.SemiBold
    )
    
    val bodyLarge = TextStyle(
        fontSize = fontSizeLarge,
        fontWeight = FontWeight.Normal
    )
    
    val bodyMedium = TextStyle(
        fontSize = fontSizeMedium,
        fontWeight = FontWeight.Normal
    )
    
    val bodySmall = TextStyle(
        fontSize = fontSizeSmall,
        fontWeight = FontWeight.Normal
    )
    
    val labelLarge = TextStyle(
        fontSize = fontSizeMedium,
        fontWeight = FontWeight.Medium
    )
    
    val labelMedium = TextStyle(
        fontSize = fontSizeSmall,
        fontWeight = FontWeight.Medium
    )
    
    val labelSmall = TextStyle(
        fontSize = fontSizeExtraSmall,
        fontWeight = FontWeight.Medium
    )
    
    // BUTTON TEXT (Buton Yazıları)
    val buttonTextLarge = TextStyle(
        fontSize = fontSizeLarge,
        fontWeight = FontWeight.Bold
    )
    
    val buttonTextMedium = TextStyle(
        fontSize = fontSizeMedium,
        fontWeight = FontWeight.Bold
    )
}

// ============================================================================
// ANIMASYON SÜRELERİ (Animation Durations)
// ============================================================================
object AppAnimations {
    const val durationFast: Int = 150               // Hızlı animasyon (ms)
    const val durationMedium: Int = 300             // Orta animasyon (ms)
    const val durationSlow: Int = 500               // Yavaş animasyon (ms)
    
    const val buttonClickDuration: Int = 100        // Buton tıklama animasyonu
    const val dialogEnterDuration: Int = 300        // Dialog açılma animasyonu
    const val dialogExitDuration: Int = 200         // Dialog kapanma animasyonu
    const val fadeInDuration: Int = 300             // Fade in animasyonu
    const val fadeOutDuration: Int = 200            // Fade out animasyonu
}

// ============================================================================
// GÖLGELER (Elevation / Shadows)
// ============================================================================
object AppElevation {
    val none: Dp = 0.dp                             // Gölgesiz
    val extraSmall: Dp = 1.dp                       // Çok hafif gölge
    val small: Dp = 2.dp                            // Hafif gölge
    val medium: Dp = 3.dp                           // Orta gölge
    val large: Dp = 6.dp                            // Büyük gölge
    val extraLarge: Dp = 12.dp                      // Çok büyük gölge
    
    // COMPONENT ELEVATIONS (Bileşen Gölgeleri)
    val card: Dp = small                            // Kart gölgesi
    val button: Dp = extraSmall                     // Buton gölgesi (elevated button)
    val dialog: Dp = large                          // Dialog gölgesi
    val appBar: Dp = small                          // App bar gölgesi
    val bottomSheet: Dp = medium                    // Bottom sheet gölgesi
}

// ============================================================================
// OPACİTY (Saydamlık)
// ============================================================================
object AppOpacity {
    const val disabled: Float = 0.38f               // Devre dışı öğeler
    const val mediumEmphasis: Float = 0.6f          // Orta vurgu
    const val highEmphasis: Float = 0.87f           // Yüksek vurgu
    const val full: Float = 1.0f                    // Tam opak
    
    const val overlay: Float = 0.5f                 // Overlay arka planı
    const val scrim: Float = 0.32f                  // Modal scrim
}

// ============================================================================
// HIZLI ERISIM - HAZIR TASARIM SETLERİ
// ============================================================================

/**
 * Kompakt Tasarım - Daha küçük boyutlar, daha az boşluk
 * Daha fazla içerik sığdırmak için
 */
object CompactDesign {
    val buttonHeight = 44.dp
    val cardPadding = 12.dp
    val spacing = 8.dp
    val fontSize = 13.sp
    val cornerRadius = 6.dp
}

/**
 * Rahat Tasarım - Daha büyük boyutlar, daha fazla boşluk
 * Daha kolay kullanım için
 */
object ComfortableDesign {
    val buttonHeight = 60.dp
    val cardPadding = 20.dp
    val spacing = 20.dp
    val fontSize = 16.sp
    val cornerRadius = 14.dp
}

/**
 * Erişilebilir Tasarım - Büyük yazılar ve butonlar
 * Görme zorluğu çekenler için
 */
object AccessibleDesign {
    val buttonHeight = 68.dp
    val cardPadding = 24.dp
    val spacing = 24.dp
    val fontSize = 18.sp
    val cornerRadius = 12.dp
    val sudokuNumberSize = 26.sp
}

// ============================================================================
// YARDIMCI FONKSİYONLAR
// ============================================================================

/**
 * Responsive boyut - Ekran boyutuna göre ayarlanır
 * Kullanım: responsiveSize(baseSize = 16.dp, multiplier = 1.2f)
 */
fun responsiveSize(baseSize: Dp, multiplier: Float = 1.0f): Dp {
    return baseSize * multiplier
}

/**
 * Responsive yazı boyutu - Ekran boyutuna göre ayarlanır
 * Kullanım: responsiveFontSize(baseSize = 14.sp, multiplier = 1.2f)
 */
fun responsiveFontSize(baseSize: TextUnit, multiplier: Float = 1.0f): TextUnit {
    return baseSize * multiplier
}