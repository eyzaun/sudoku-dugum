package com.extremesudoku.data.models.pvp

/**
 * Oyuncu oyun sonu sonuçları (Enhanced Scoring)
 */
data class PlayerResult(
    val completedAt: Long = System.currentTimeMillis(),
    val score: Int = 0,           // Doğru yerleştirilen sayı adedi (eski - backward compatibility)
    val timeElapsed: Long = 0,    // Milisaniye
    val accuracy: Float = 0f,     // 0-100 arası doğruluk yüzdesi
    
    // DETAYLI PUANLAMA (YENİ)
    val finalScore: Int = 0,              // Hesaplanmış final puan
    val basePoints: Int = 0,              // Temel puanlar
    val streakBonus: Int = 0,             // Seri bonusu
    val timeBonus: Int = 0,               // Zaman bonusu
    val completionBonuses: Int = 0,       // Tamamlama bonusları (eski field - deprecated)
    val totalCompletionBonus: Int = 0,    // Toplam tamamlama bonusu (yeni)
    val maxStreak: Int = 0,               // En uzun seri
    val totalMoves: Int = 0,              // Toplam hamle sayısı
    val correctMoves: Int = 0,            // Doğru hamle sayısı
    val wrongMoves: Int = 0,              // Yanlış hamle sayısı
    val hintsUsed: Int = 0,               // Kullanılan hint
    val isPerfectGame: Boolean = false,   // Hiç hata yapmadan bitirdi mi?
    val isFirstFinish: Boolean = false    // İlk bitiren mi? (Blind Race için)
)
