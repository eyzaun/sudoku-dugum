package com.extremesudoku.data.models.pvp

/**
 * Kullanıcının PvP istatistikleri
 */
data class PvpStats(
    val userId: String = "",
    val blindRaceStats: ModeStats = ModeStats(),
    val liveBattleStats: ModeStats = ModeStats()
) {
    /**
     * Toplam kazanma oranı
     */
    fun getTotalWinRate(): Float {
        val totalWins = blindRaceStats.wins + liveBattleStats.wins
        val totalGames = blindRaceStats.gamesPlayed + liveBattleStats.gamesPlayed
        return if (totalGames > 0) (totalWins.toFloat() / totalGames.toFloat()) * 100f else 0f
    }
}

/**
 * Bir mod için istatistikler
 */
data class ModeStats(
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val averageTime: Long = 0,    // Ortalama süre (ms)
    val averageScore: Float = 0f,  // Ortalama skor
    val rating: Int = 1000         // ELO rating
) {
    /**
     * Kazanma oranı
     */
    fun getWinRate(): Float {
        return if (gamesPlayed > 0) (wins.toFloat() / gamesPlayed.toFloat()) * 100f else 0f
    }
    
    /**
     * Ortalama süreyi saniye cinsinden al
     */
    fun getAverageTimeInSeconds(): Long {
        return averageTime / 1000
    }
}
