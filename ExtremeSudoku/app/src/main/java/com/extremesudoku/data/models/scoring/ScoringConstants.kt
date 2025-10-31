package com.extremesudoku.data.models.scoring

/**
 * Puanlama sistemi sabitleri
 * Tetris-tabanlı rekabetçi puan modeli
 */
object ScoringConstants {
    
    // ═══════════════════════════════════════════════════════════
    // TEMEL PUANLAR (Base Points)
    // ═══════════════════════════════════════════════════════════
    
    /** Doğru sayı yerleştirme - Base ödül */
    const val CORRECT_PLACEMENT = 100
    
    /** Yanlış sayı yerleştirme - Ağır ceza */
    const val WRONG_PLACEMENT = -300
    
    /** Boş hücreyi silme - Kararsızlık cezası */
    const val EMPTY_CELL_DELETE = -10
    
    // ═══════════════════════════════════════════════════════════
    // SERİ BONUSU (Streak Bonus) - TETRİS COMBO MEKANİĞİ
    // ═══════════════════════════════════════════════════════════
    
    /** Her seri adımı için bonus çarpanı */
    const val STREAK_MULTIPLIER = 10
    
    /**
     * Seri bonus hesaplama:
     * 1. doğru: 100 + (1 * 10) = 110
     * 2. doğru: 100 + (2 * 10) = 120
     * 3. doğru: 100 + (3 * 10) = 130
     * ...
     * 10. doğru: 100 + (10 * 10) = 200
     * 
     * Yanlış yapınca seri sıfırlanır!
     */
    fun calculateStreakBonus(streakCount: Int): Int {
        return streakCount * STREAK_MULTIPLIER
    }
    
    // ═══════════════════════════════════════════════════════════
    // ZAMAN BONUSU (Time Bonus) - HIZLI OYUN ÖDÜLÜ
    // ═══════════════════════════════════════════════════════════
    
    /** Kolay seviye - Başlangıç zaman havuzu */
    const val TIME_POOL_EASY = 10_000
    
    /** Orta seviye - Başlangıç zaman havuzu */
    const val TIME_POOL_MEDIUM = 20_000
    
    /** Zor seviye - Başlangıç zaman havuzu */
    const val TIME_POOL_HARD = 40_000
    
    /** Uzman seviye - Başlangıç zaman havuzu */
    const val TIME_POOL_EXPERT = 60_000
    
    /** Her saniye için düşen puan */
    const val TIME_PENALTY_PER_SECOND = 20
    
    /**
     * Zaman bonusu hesaplama
     * Örnek: 5 dakika (300 saniye) bitirme
     * 30.000 - (300 * 20) = 30.000 - 6.000 = 24.000 bonus
     */
    fun calculateTimeBonus(difficulty: String, elapsedSeconds: Long): Int {
        val pool = when (difficulty.lowercase()) {
            "easy" -> TIME_POOL_EASY
            "medium" -> TIME_POOL_MEDIUM
            "hard" -> TIME_POOL_HARD
            "expert" -> TIME_POOL_EXPERT
            else -> TIME_POOL_MEDIUM
        }
        
        val loss = (elapsedSeconds * TIME_PENALTY_PER_SECOND).toInt()
        return maxOf(0, pool - loss) // Negatif olamaz
    }
    
    // ═══════════════════════════════════════════════════════════
    // TAMAMLAMA BONUSLARI (Completion Bonuses)
    // ═══════════════════════════════════════════════════════════
    
    /** 3x3 kutu tamamlama bonusu */
    const val BOX_COMPLETE_BONUS = 250
    
    /** Sıra (row) tamamlama bonusu */
    const val ROW_COMPLETE_BONUS = 250
    
    /** Sütun (column) tamamlama bonusu */
    const val COLUMN_COMPLETE_BONUS = 250
    
    // ═══════════════════════════════════════════════════════════
    // ÖZEL BONUSLAR (Special Bonuses)
    // ═══════════════════════════════════════════════════════════
    
    /** Not kullanmadan bitirme bonusu */
    const val NO_NOTES_BONUS = 5_000
    
    /** Hiç hata yapmadan bitirme bonusu (Perfect Game) */
    const val PERFECT_GAME_BONUS = 10_000
    
    /** Süper hızlı bitirme bonusu (zorluk seviyesine göre süre) */
    const val SPEED_BONUS = 3_000
    
    // Hız bonusu için sınırlar (saniye)
    const val SPEED_LIMIT_EASY = 180      // 3 dakika
    const val SPEED_LIMIT_MEDIUM = 300    // 5 dakika
    const val SPEED_LIMIT_HARD = 480      // 8 dakika
    const val SPEED_LIMIT_EXPERT = 600    // 10 dakika
    
    /**
     * Hız bonusu kontrolü
     */
    fun isEligibleForSpeedBonus(difficulty: String, elapsedSeconds: Long): Boolean {
        val limit = when (difficulty.lowercase()) {
            "easy" -> SPEED_LIMIT_EASY
            "medium" -> SPEED_LIMIT_MEDIUM
            "hard" -> SPEED_LIMIT_HARD
            "expert" -> SPEED_LIMIT_EXPERT
            else -> SPEED_LIMIT_MEDIUM
        }
        return elapsedSeconds <= limit
    }
    
    // ═══════════════════════════════════════════════════════════
    // CEZALAR (Penalties)
    // ═══════════════════════════════════════════════════════════
    
    /** İpucu (hint) kullanma cezası - Seriyi de sıfırlar */
    const val HINT_PENALTY = -1_000
    
    /** Hata kontrolü (error check) kullanma cezası - Seriyi de sıfırlar */
    const val ERROR_CHECK_PENALTY = -500
    
    // ═══════════════════════════════════════════════════════════
    // ZORLUK ÇARPANLARI (Difficulty Multipliers)
    // ═══════════════════════════════════════════════════════════
    
    /** Kolay seviye çarpanı */
    const val MULTIPLIER_EASY = 1.0f
    
    /** Orta seviye çarpanı */
    const val MULTIPLIER_MEDIUM = 1.5f
    
    /** Zor seviye çarpanı */
    const val MULTIPLIER_HARD = 2.5f
    
    /** Uzman seviye çarpanı */
    const val MULTIPLIER_EXPERT = 4.0f
    
    /**
     * Zorluk çarpanını al
     */
    fun getDifficultyMultiplier(difficulty: String): Float {
        return when (difficulty.lowercase()) {
            "easy" -> MULTIPLIER_EASY
            "medium" -> MULTIPLIER_MEDIUM
            "hard" -> MULTIPLIER_HARD
            "expert" -> MULTIPLIER_EXPERT
            else -> MULTIPLIER_MEDIUM
        }
    }
    
    // ═══════════════════════════════════════════════════════════
    // PVP ÖZEL BONUSLAR
    // ═══════════════════════════════════════════════════════════
    
    /** Rakip disconnect olduğunda kazanma bonusu */
    const val OPPONENT_DISCONNECT_BONUS = 2_000
    
    /** İlk bitiren bonusu (Blind Race) */
    const val FIRST_FINISH_BONUS = 5_000
    
    /** Rakipten çok önde bitirme bonusu (2x puan farkı) */
    const val DOMINATION_BONUS = 3_000
    
    // ═══════════════════════════════════════════════════════════
    // HESAPLAMA FONKSİYONLARI
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Final puanı hesapla
     * 
     * Formula:
     * (Base + Streak + Time + Completions + Specials - Penalties) * Difficulty
     */
    fun calculateFinalScore(
        basePoints: Int,
        streakBonus: Int,
        timeBonus: Int,
        completionBonuses: Int,
        specialBonuses: Int,
        penalties: Int,
        difficulty: String
    ): Int {
        val subtotal = basePoints + streakBonus + timeBonus + 
                      completionBonuses + specialBonuses - penalties
        
        val multiplier = getDifficultyMultiplier(difficulty)
        val finalScore = (subtotal * multiplier).toInt()
        
        return maxOf(0, finalScore) // Negatif puan olmaz
    }
    
    /**
     * Doğruluk yüzdesini hesapla
     */
    fun calculateAccuracy(correctMoves: Int, totalMoves: Int): Float {
        return if (totalMoves > 0) {
            (correctMoves.toFloat() / totalMoves.toFloat()) * 100f
        } else 100f
    }
    
    /**
     * Puan formatla (binlik ayraçlı)
     * Örnek: 15420 -> "15,420"
     */
    fun formatScore(score: Int): String {
        return String.format("%,d", score)
    }
}

/**
 * Zorluk seviyeleri
 */
enum class Difficulty(val value: String, val multiplier: Float) {
    EASY("easy", ScoringConstants.MULTIPLIER_EASY),
    MEDIUM("medium", ScoringConstants.MULTIPLIER_MEDIUM),
    HARD("hard", ScoringConstants.MULTIPLIER_HARD),
    EXPERT("expert", ScoringConstants.MULTIPLIER_EXPERT);
    
    companion object {
        fun fromString(value: String): Difficulty {
            return values().find { it.value.equals(value, ignoreCase = true) } ?: MEDIUM
        }
    }
}
