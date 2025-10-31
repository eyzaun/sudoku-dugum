package com.extremesudoku.domain.repository

import com.extremesudoku.data.models.pvp.*
import kotlinx.coroutines.flow.Flow

/**
 * PvP Match işlemleri için repository interface
 */
interface PvpMatchRepository {
    
    // ========== Matchmaking ==========
    
    /**
     * Matchmaking kuyruğuna katıl
     */
    suspend fun joinMatchmaking(mode: PvpMode): Result<Unit>
    
    /**
     * Matchmaking'den ayrıl
     */
    suspend fun leaveMatchmaking(): Result<Unit>
    
    /**
     * Matchmaking durumunu dinle (eşleşme bulunana kadar)
     */
    fun observeMatchmaking(): Flow<MatchmakingRequest?>
    
    /**
     * Aktif olarak diğer oyuncuları arar ve eşleşme yapmaya çalışır
     * @return matchId eğer eşleşme bulunursa, null eğer henüz kimse yoksa
     */
    suspend fun tryMatchmaking(mode: PvpMode): Result<String?>
    
    // ========== Match Management ==========
    
    /**
     * Yeni match oluştur (matchmaking tarafından çağrılır)
     */
    suspend fun createMatch(
        player1Id: String,
        player2Id: String,
        mode: PvpMode
    ): Result<String> // matchId döner
    
    /**
     * Match'i ID ile getir
     */
    suspend fun getMatch(matchId: String): Result<PvpMatch>
    
    /**
     * Match'i real-time dinle
     */
    fun observeMatch(matchId: String): Flow<PvpMatch?>
    
    /**
     * Match'i başlat (her iki oyuncu hazır olunca)
     */
    suspend fun startMatch(matchId: String): Result<Unit>
    
    /**
     * Match'i bitir ve kazananı belirle
     */
    suspend fun endMatch(matchId: String, winnerId: String?): Result<Unit>
    
    /**
     * Match'i iptal et
     */
    suspend fun cancelMatch(matchId: String): Result<Unit>
    
    // ========== Presence Management ==========
    
    /**
     * Match presence'ı başlat (heartbeat)
     */
    suspend fun startMatchPresence(matchId: String)
    
    /**
     * Heartbeat güncelle
     */
    suspend fun updateHeartbeat(matchId: String)
    
    /**
     * Match presence'ı durdur
     */
    suspend fun stopMatchPresence(matchId: String)
    
    /**
     * Rakibin online/offline durumunu dinle
     */
    fun observeOpponentPresence(matchId: String, opponentId: String): Flow<Boolean>
    
    // ========== Player Actions ==========
    
    /**
     * Oyuncunun durumunu güncelle (READY, PLAYING, FINISHED)
     */
    suspend fun updatePlayerStatus(
        matchId: String,
        status: PlayerStatus
    ): Result<Unit>
    
    /**
     * Oyuncunun sonucunu gönder (oyun bittiğinde)
     */
    suspend fun submitPlayerResult(
        matchId: String,
        result: PlayerResult
    ): Result<Unit>
    
    // ========== Moves (Live Battle için) ==========
    
    /**
     * Bir hamle gönder (Live Battle modunda)
     */
    suspend fun submitMove(
        matchId: String,
        move: PvpMove
    ): Result<Unit>
    
    /**
     * Match'teki tüm hamleleri real-time dinle
     */
    fun observeMoves(matchId: String): Flow<List<PvpMove>>
    
    // ========== Stats ==========
    
    /**
     * Kullanıcının PvP istatistiklerini getir
     */
    suspend fun getUserStats(userId: String): Result<PvpStats>
    
    /**
     * Kullanıcının istatistiklerini güncelle
     */
    suspend fun updateUserStats(stats: PvpStats): Result<Unit>
    
    /**
     * Match sonucuna göre istatistikleri otomatik güncelle
     */
    suspend fun updateStatsAfterMatch(
        matchId: String,
        userId: String,
        won: Boolean,
        result: PlayerResult
    ): Result<Unit>
}
