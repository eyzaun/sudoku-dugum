package com.extremesudoku.data.models.pvp

/**
 * PvP Match - İki oyuncunun kapıştığı oyun
 */
data class PvpMatch(
    val matchId: String = "",
    val mode: PvpMode = PvpMode.BLIND_RACE,
    val status: MatchStatus = MatchStatus.WAITING,
    val createdAt: Long = System.currentTimeMillis(),
    val startedAt: Long? = null,
    val endedAt: Long? = null,
    
    val puzzle: PvpPuzzle = PvpPuzzle(),
    val players: Map<String, PlayerMatchData> = emptyMap(), // userId -> PlayerMatchData
    val winnerId: String? = null // kazanan userId
) {
    /**
     * Match'e katılan oyuncu sayısı
     */
    fun getPlayerCount(): Int = players.size
    
    /**
     * Match başlatılabilir mi? (2 oyuncu var mı?)
     */
    fun canStart(): Boolean = players.size == 2 && status == MatchStatus.WAITING
    
    /**
     * Match tamamlandı mı?
     */
    fun isCompleted(): Boolean = status == MatchStatus.COMPLETED
    
    /**
     * Belirli bir oyuncunun match verisini al
     */
    fun getPlayerData(userId: String): PlayerMatchData? = players[userId]
    
    /**
     * Rakip oyuncunun verisini al
     */
    fun getOpponentData(myUserId: String): PlayerMatchData? {
        return players.values.firstOrNull { it.userId != myUserId }
    }
}
