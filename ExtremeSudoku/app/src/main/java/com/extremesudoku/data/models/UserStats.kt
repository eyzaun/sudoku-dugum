package com.extremesudoku.data.models

/**
 * User gameplay statistics (enhanced scoring system)
 */
data class UserStats(
    val userId: String = "",
    
    // ═══════════════════════════════════════════════════════════
    // GENEL İSTATİSTİKLER
    // ═══════════════════════════════════════════════════════════
    
    val gamesPlayed: Int = 0,
    val gamesCompleted: Int = 0,
    val gamesAbandoned: Int = 0,
    val totalTime: Long = 0,
    val bestTime: Long = Long.MAX_VALUE,
    val averageTime: Long = 0,
    
    // ═══════════════════════════════════════════════════════════
    // PUAN İSTATİSTİKLERİ
    // ═══════════════════════════════════════════════════════════
    
    /** Toplam kazanılan puan */
    val totalScore: Long = 0,
    
    /** En yüksek tek oyun puanı */
    val highestScore: Int = 0,
    
    /** Ortalama oyun puanı */
    val averageScore: Int = 0,
    
    /** Offline modda kazanılan puan */
    val offlineScore: Long = 0,
    
    /** Online modda kazanılan puan */
    val onlineScore: Long = 0,
    
    // ═══════════════════════════════════════════════════════════
    // DOĞRULUK (ACCURACY) İSTATİSTİKLERİ
    // ═══════════════════════════════════════════════════════════
    
    /** Toplam yapılan hamle */
    val totalMoves: Int = 0,
    
    /** Toplam doğru hamle */
    val correctMoves: Int = 0,
    
    /** Toplam yanlış hamle */
    val wrongMoves: Int = 0,
    
    /** Genel doğruluk yüzdesi (0-100) */
    val accuracy: Float = 0f,
    
    // ═══════════════════════════════════════════════════════════
    // SERİ (STREAK) İSTATİSTİKLERİ
    // ═══════════════════════════════════════════════════════════
    
    /** Şu anki günlük oynama serisi */
    val currentStreak: Int = 0,
    
    /** Tüm zamanların en uzun günlük serisi */
    val longestStreak: Int = 0,
    
    /** Tek oyunda en uzun doğru hamle serisi */
    val maxStreakInGame: Int = 0,
    
    // ═══════════════════════════════════════════════════════════
    // YARDIM KULLANIMI
    // ═══════════════════════════════════════════════════════════
    
    /** Toplam kullanılan hint */
    val hintsUsed: Int = 0,
    
    /** Toplam error check kullanımı */
    val errorChecksUsed: Int = 0,
    
    /** Hint kullanmadan biten oyun sayısı */
    val gamesWithoutHints: Int = 0,
    
    // ═══════════════════════════════════════════════════════════
    // BAŞARILAR (ACHIEVEMENTS)
    // ═══════════════════════════════════════════════════════════
    
    /** Hiç hata yapmadan biten oyun (Perfect Game) */
    val perfectGames: Int = 0,
    
    /** Tamamlanan 3x3 kutu sayısı (toplam) */
    val boxCompletions: Int = 0,
    
    /** Tamamlanan sıra sayısı (toplam) */
    val rowCompletions: Int = 0,
    
    /** Tamamlanan sütun sayısı (toplam) */
    val columnCompletions: Int = 0,
    
    /** Not kullanmadan biten oyun sayısı */
    val gamesWithoutNotes: Int = 0,
    
    /** Hız bonusu kazanılan oyun sayısı */
    val speedBonusEarned: Int = 0,
    
    // ═══════════════════════════════════════════════════════════
    // ZORLUK SEVİYESİNE GÖRE İSTATİSTİKLER
    // ═══════════════════════════════════════════════════════════
    
    val easyStats: DifficultyStats = DifficultyStats(),
    val mediumStats: DifficultyStats = DifficultyStats(),
    val hardStats: DifficultyStats = DifficultyStats(),
    val expertStats: DifficultyStats = DifficultyStats(),
    
    val lastPlayedDate: Long = 0
)

/**
 * Zorluk seviyesine özel istatistikler
 */
data class DifficultyStats(
    val gamesPlayed: Int = 0,
    val gamesCompleted: Int = 0,
    val bestScore: Int = 0,
    val bestTime: Long = Long.MAX_VALUE,
    val averageScore: Int = 0,
    val averageTime: Long = 0,
    val accuracy: Float = 0f,
    val perfectGames: Int = 0,
    val maxStreak: Int = 0
)
