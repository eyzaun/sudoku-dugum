package com.extremesudoku.data.repository

import com.extremesudoku.data.models.pvp.*
import com.extremesudoku.data.remote.FirebaseDataSource
import com.extremesudoku.data.remote.PvpFirebaseDataSource
import com.extremesudoku.domain.repository.PvpMatchRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PvP Match Repository implementasyonu
 */
@Singleton
class PvpMatchRepositoryImpl @Inject constructor(
    private val pvpDataSource: PvpFirebaseDataSource,
    private val sudokuDataSource: FirebaseDataSource, // Puzzle almak için
    private val auth: FirebaseAuth
) : PvpMatchRepository {
    
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""
    
    // ========== Matchmaking ==========
    
    override suspend fun joinMatchmaking(mode: PvpMode): Result<Unit> {
        return pvpDataSource.joinMatchmaking(mode)
    }
    
    override suspend fun leaveMatchmaking(): Result<Unit> {
        return pvpDataSource.leaveMatchmaking()
    }
    
    override fun observeMatchmaking(): Flow<MatchmakingRequest?> {
        return pvpDataSource.observeMatchmaking()
    }
    
    override suspend fun tryMatchmaking(mode: PvpMode): Result<String?> {
        return pvpDataSource.tryMatchmaking(mode)
    }
    
    // ========== Match Management ==========
    
    override suspend fun createMatch(
        player1Id: String,
        player2Id: String,
        mode: PvpMode
    ): Result<String> {
        // ✅ PVP modunda SADECE EASY zorlukta puzzle kullan
        // 🎲 RANDOM SELECTION için daha fazla puzzle çek
        android.util.Log.d("PvpMatchRepository", "🧩 Firebase'den EASY puzzle çekiliyor...")
        val puzzleResult = sudokuDataSource.getSudokusByDifficulty("easy", limit = 50) // 50 puzzle çek, random seç
        
        return puzzleResult.fold(
            onSuccess = { sudokuList ->
                if (sudokuList.isEmpty()) {
                    android.util.Log.e("PvpMatchRepository", "❌ Firebase'de EASY puzzle bulunamadı!")
                    return Result.failure(Exception("No easy puzzle found in Firebase"))
                }
                
                // 🎲 RANDOM SELECTION - Her seferinde farklı puzzle
                val sudoku = sudokuList.random()
                android.util.Log.d("PvpMatchRepository", "✅ EASY puzzle alındı (${sudokuList.size} arasından random): ID=${sudoku.id}, Difficulty=${sudoku.difficulty}")
                
                // ✅ PUZZLE VALIDATION - Format kontrolü
                if (!isValidPuzzle(sudoku.puzzle, sudoku.solution)) {
                    android.util.Log.e("PvpMatchRepository", "❌ Hatalı puzzle formatı! ID=${sudoku.id}")
                    return Result.failure(Exception("Invalid puzzle format"))
                }
                
                val pvpPuzzle = PvpPuzzle(
                    puzzleString = sudoku.puzzle,
                    solution = sudoku.solution,
                    difficulty = "easy" // Her zaman easy
                )
                
                android.util.Log.d("PvpMatchRepository", "🧩 Puzzle validated: ${sudoku.puzzle.length} chars, solution: ${sudoku.solution.length} chars")
                
                // TODO: Get actual player names from Firestore user profiles
                // Şimdilik Firebase Auth'dan alınan isimler kullanılıyor (PvpFirebaseDataSource içinde)
                // Match oluştur
                pvpDataSource.createMatch(
                    player1Id = player1Id,
                    player2Id = player2Id,
                    player1Name = "Player 1", // Firebase Auth'tan alınıyor (currentUserName)
                    player2Name = "Player 2", // Firebase Auth'tan alınıyor (currentUserName)
                    mode = mode,
                    puzzle = pvpPuzzle
                )
            },
            onFailure = { error ->
                android.util.Log.e("PvpMatchRepository", "❌ Firebase puzzle çekme hatası: ${error.message}", error)
                Result.failure(error)
            }
        )
    }
    
    /**
     * Puzzle format validation
     * puzzle: 81 karakter, 0-9 arası
     * solution: 81 karakter, 1-9 arası (0 yok)
     */
    private fun isValidPuzzle(puzzle: String, solution: String): Boolean {
        // Length kontrolü
        if (puzzle.length != 81 || solution.length != 81) {
            android.util.Log.e("PvpMatchRepository", "❌ Invalid length: puzzle=${puzzle.length}, solution=${solution.length}")
            return false
        }
        
        // Puzzle karakterleri: 0-9
        if (!puzzle.all { it in '0'..'9' }) {
            android.util.Log.e("PvpMatchRepository", "❌ Puzzle contains invalid characters")
            return false
        }
        
        // Solution karakterleri: 1-9 (0 olmamalı)
        if (!solution.all { it in '1'..'9' }) {
            android.util.Log.e("PvpMatchRepository", "❌ Solution contains invalid characters or zeros")
            return false
        }
        
        // Puzzle'daki verili sayılar solution'la eşleşmeli
        puzzle.forEachIndexed { index, char ->
            if (char != '0' && char != solution[index]) {
                android.util.Log.e("PvpMatchRepository", "❌ Puzzle mismatch at index $index: puzzle=$char, solution=${solution[index]}")
                return false
            }
        }
        
        return true
    }
    
    override suspend fun getMatch(matchId: String): Result<PvpMatch> {
        return pvpDataSource.getMatch(matchId)
    }
    
    override fun observeMatch(matchId: String): Flow<PvpMatch?> {
        return pvpDataSource.observeMatch(matchId)
    }
    
    override suspend fun startMatch(matchId: String): Result<Unit> {
        return pvpDataSource.startMatch(matchId)
    }
    
    override suspend fun endMatch(matchId: String, winnerId: String?): Result<Unit> {
        return pvpDataSource.endMatch(matchId, winnerId)
    }
    
    override suspend fun cancelMatch(matchId: String): Result<Unit> {
        return pvpDataSource.cancelMatch(matchId)
    }
    
    // ========== Presence Management ==========
    
    override suspend fun startMatchPresence(matchId: String) {
        pvpDataSource.startMatchPresence(matchId)
    }
    
    override suspend fun updateHeartbeat(matchId: String) {
        pvpDataSource.updateHeartbeat(matchId)
    }
    
    override suspend fun stopMatchPresence(matchId: String) {
        pvpDataSource.stopMatchPresence(matchId)
    }
    
    override fun observeOpponentPresence(matchId: String, opponentId: String): Flow<Boolean> {
        return pvpDataSource.observeOpponentPresence(matchId, opponentId)
    }
    
    // ========== Player Actions ==========
    
    override suspend fun updatePlayerStatus(
        matchId: String,
        status: PlayerStatus
    ): Result<Unit> {
        return pvpDataSource.updatePlayerStatus(matchId, currentUserId, status)
    }
    
    override suspend fun submitPlayerResult(
        matchId: String,
        result: PlayerResult
    ): Result<Unit> {
        return pvpDataSource.submitPlayerResult(matchId, currentUserId, result)
    }
    
    // ========== Moves ==========
    
    override suspend fun submitMove(
        matchId: String,
        move: PvpMove
    ): Result<Unit> {
        return pvpDataSource.submitMove(matchId, move)
    }
    
    override fun observeMoves(matchId: String): Flow<List<PvpMove>> {
        return pvpDataSource.observeMoves(matchId)
    }
    
    // ========== Stats ==========
    
    override suspend fun getUserStats(userId: String): Result<PvpStats> {
        return pvpDataSource.getUserStats(userId)
    }
    
    override suspend fun updateUserStats(stats: PvpStats): Result<Unit> {
        return pvpDataSource.updateUserStats(stats)
    }
    
    override suspend fun updateStatsAfterMatch(
        matchId: String,
        userId: String,
        won: Boolean,
        result: PlayerResult
    ): Result<Unit> {
        // Match'i al
        val matchResult = getMatch(matchId)
        if (matchResult.isFailure) {
            return Result.failure(matchResult.exceptionOrNull() ?: Exception("Failed to get match"))
        }
        
        val match = matchResult.getOrNull() ?: return Result.failure(Exception("Match is null"))
        
        // Mevcut istatistikleri al
        val statsResult = getUserStats(userId)
        if (statsResult.isFailure) {
            return Result.failure(statsResult.exceptionOrNull() ?: Exception("Failed to get stats"))
        }
        
        val currentStats = statsResult.getOrNull() ?: PvpStats(userId = userId)
        
        // Mode'a göre istatistiği güncelle
        val updatedStats = when (match.mode) {
            PvpMode.BLIND_RACE -> {
                val blindStats = currentStats.blindRaceStats
                currentStats.copy(
                    blindRaceStats = blindStats.copy(
                        gamesPlayed = blindStats.gamesPlayed + 1,
                        wins = if (won) blindStats.wins + 1 else blindStats.wins,
                        losses = if (!won) blindStats.losses + 1 else blindStats.losses,
                        averageTime = calculateNewAverage(
                            blindStats.averageTime,
                            result.timeElapsed,
                            blindStats.gamesPlayed
                        ),
                        averageScore = calculateNewAverageFloat(
                            blindStats.averageScore,
                            result.score.toFloat(),
                            blindStats.gamesPlayed
                        )
                    )
                )
            }
            PvpMode.LIVE_BATTLE -> {
                val liveStats = currentStats.liveBattleStats
                currentStats.copy(
                    liveBattleStats = liveStats.copy(
                        gamesPlayed = liveStats.gamesPlayed + 1,
                        wins = if (won) liveStats.wins + 1 else liveStats.wins,
                        losses = if (!won) liveStats.losses + 1 else liveStats.losses,
                        averageTime = calculateNewAverage(
                            liveStats.averageTime,
                            result.timeElapsed,
                            liveStats.gamesPlayed
                        ),
                        averageScore = calculateNewAverageFloat(
                            liveStats.averageScore,
                            result.score.toFloat(),
                            liveStats.gamesPlayed
                        )
                    )
                )
            }
        }
        
        return updateUserStats(updatedStats)
    }
    
    // ========== Helpers ==========
    
    private fun calculateNewAverage(
        oldAverage: Long,
        newValue: Long,
        oldCount: Int
    ): Long {
        return ((oldAverage * oldCount) + newValue) / (oldCount + 1)
    }
    
    private fun calculateNewAverageFloat(
        oldAverage: Float,
        newValue: Float,
        oldCount: Int
    ): Float {
        return ((oldAverage * oldCount) + newValue) / (oldCount + 1)
    }
}
