package com.extremesudoku.domain.usecase.scoring

import com.extremesudoku.data.models.UserStats
import com.extremesudoku.data.models.DifficultyStats
import com.extremesudoku.data.models.scoring.GameScore
import javax.inject.Inject

/**
 * Oyun bittiğinde kullanıcı istatistiklerini güncelle
 */
class UpdatePlayerStatsUseCase @Inject constructor() {
    
    /**
     * Oyun sonucu ile kullanıcı istatistiklerini güncelle
     * 
     * @param currentStats Mevcut kullanıcı istatistikleri
     * @param gameScore Biten oyunun puanı
     * @param difficulty Oyunun zorluk seviyesi
     * @param isOnline Online mod mu?
     * @return Güncellenmiş UserStats
     */
    operator fun invoke(
        currentStats: UserStats,
        gameScore: GameScore,
        difficulty: String,
        isOnline: Boolean
    ): UserStats {
        
        // 1. GENEL İSTATİSTİKLER GÜNCELLEME
        val newGamesPlayed = currentStats.gamesPlayed + 1
        val newGamesCompleted = currentStats.gamesCompleted + 1
        val newTotalScore = currentStats.totalScore + gameScore.finalScore
        val newHighestScore = maxOf(currentStats.highestScore, gameScore.finalScore)
        val newAverageScore = (newTotalScore / newGamesCompleted).toInt()
        
        // 2. HAREKET İSTATİSTİKLERİ
        val newTotalMoves = currentStats.totalMoves + gameScore.totalMoves
        val newCorrectMoves = currentStats.correctMoves + gameScore.correctMoves
        val newWrongMoves = currentStats.wrongMoves + gameScore.wrongMoves
        val newAccuracy = if (newTotalMoves > 0) {
            (newCorrectMoves.toFloat() / newTotalMoves.toFloat()) * 100f
        } else 100f
        
        // 3. STREAK İSTATİSTİKLERİ
        val newMaxStreakInGame = maxOf(currentStats.maxStreakInGame, gameScore.maxStreak)
        
        // 4. YARDIM KULLANIMI
        val newHintsUsed = currentStats.hintsUsed + gameScore.hintsUsed
        val newErrorChecksUsed = currentStats.errorChecksUsed + gameScore.errorChecksUsed
        val newGamesWithoutHints = if (gameScore.hintsUsed == 0) {
            currentStats.gamesWithoutHints + 1
        } else currentStats.gamesWithoutHints
        
        // 5. BAŞARILAR
        val newPerfectGames = if (gameScore.perfectGame) {
            currentStats.perfectGames + 1
        } else currentStats.perfectGames
        
        val newBoxCompletions = currentStats.boxCompletions + gameScore.boxesCompleted
        val newRowCompletions = currentStats.rowCompletions + gameScore.rowsCompleted
        val newColumnCompletions = currentStats.columnCompletions + gameScore.columnsCompleted
        
        val newGamesWithoutNotes = if (gameScore.playedWithoutNotes) {
            currentStats.gamesWithoutNotes + 1
        } else currentStats.gamesWithoutNotes
        
        val newSpeedBonusEarned = if (gameScore.speedBonus) {
            currentStats.speedBonusEarned + 1
        } else currentStats.speedBonusEarned
        
        // 6. OFFLINE/ONLINE PUAN AYIRIMI
        val newOfflineScore = if (!isOnline) {
            currentStats.offlineScore + gameScore.finalScore
        } else currentStats.offlineScore
        
        val newOnlineScore = if (isOnline) {
            currentStats.onlineScore + gameScore.finalScore
        } else currentStats.onlineScore
        
        // 7. ZORLUK SEVİYESİNE GÖRE İSTATİSTİK GÜNCELLEME
        val updatedDifficultyStats = updateDifficultyStats(
            currentStats = currentStats,
            difficulty = difficulty,
            gameScore = gameScore
        )
        
        // 8. GÜNCELLENMIŞ STATS DÖNDÜR
        return currentStats.copy(
            gamesPlayed = newGamesPlayed,
            gamesCompleted = newGamesCompleted,
            totalScore = newTotalScore,
            highestScore = newHighestScore,
            averageScore = newAverageScore,
            offlineScore = newOfflineScore,
            onlineScore = newOnlineScore,
            totalMoves = newTotalMoves,
            correctMoves = newCorrectMoves,
            wrongMoves = newWrongMoves,
            accuracy = newAccuracy,
            maxStreakInGame = newMaxStreakInGame,
            hintsUsed = newHintsUsed,
            errorChecksUsed = newErrorChecksUsed,
            gamesWithoutHints = newGamesWithoutHints,
            perfectGames = newPerfectGames,
            boxCompletions = newBoxCompletions,
            rowCompletions = newRowCompletions,
            columnCompletions = newColumnCompletions,
            gamesWithoutNotes = newGamesWithoutNotes,
            speedBonusEarned = newSpeedBonusEarned,
            easyStats = updatedDifficultyStats.easy,
            mediumStats = updatedDifficultyStats.medium,
            hardStats = updatedDifficultyStats.hard,
            expertStats = updatedDifficultyStats.expert,
            lastPlayedDate = System.currentTimeMillis()
        )
    }
    
    /**
     * Zorluk seviyesine özel istatistikleri güncelle
     */
    private fun updateDifficultyStats(
        currentStats: UserStats,
        difficulty: String,
        gameScore: GameScore
    ): DifficultyStatsUpdate {
        
        val currentDiffStats = when (difficulty.lowercase()) {
            "easy" -> currentStats.easyStats
            "hard" -> currentStats.hardStats
            "expert" -> currentStats.expertStats
            else -> currentStats.mediumStats
        }
        
        val newGamesPlayed = currentDiffStats.gamesPlayed + 1
        val newGamesCompleted = currentDiffStats.gamesCompleted + 1
        val newBestScore = maxOf(currentDiffStats.bestScore, gameScore.finalScore)
        val newAverageScore = ((currentDiffStats.averageScore * currentDiffStats.gamesPlayed) + gameScore.finalScore) / newGamesPlayed
        
        val totalMoves = (currentDiffStats.accuracy * currentDiffStats.gamesPlayed / 100f * newGamesPlayed).toInt() + gameScore.totalMoves
        val correctMoves = (currentDiffStats.accuracy * currentDiffStats.gamesPlayed / 100f).toInt() + gameScore.correctMoves
        val newAccuracy = if (totalMoves > 0) {
            (correctMoves.toFloat() / totalMoves.toFloat()) * 100f
        } else 100f
        
        val newPerfectGames = if (gameScore.perfectGame) {
            currentDiffStats.perfectGames + 1
        } else currentDiffStats.perfectGames
        
        val newMaxStreak = maxOf(currentDiffStats.maxStreak, gameScore.maxStreak)
        
        val updatedStats = currentDiffStats.copy(
            gamesPlayed = newGamesPlayed,
            gamesCompleted = newGamesCompleted,
            bestScore = newBestScore,
            averageScore = newAverageScore,
            accuracy = newAccuracy,
            perfectGames = newPerfectGames,
            maxStreak = newMaxStreak
        )
        
        return when (difficulty.lowercase()) {
            "easy" -> DifficultyStatsUpdate(
                easy = updatedStats,
                medium = currentStats.mediumStats,
                hard = currentStats.hardStats,
                expert = currentStats.expertStats
            )
            "hard" -> DifficultyStatsUpdate(
                easy = currentStats.easyStats,
                medium = currentStats.mediumStats,
                hard = updatedStats,
                expert = currentStats.expertStats
            )
            "expert" -> DifficultyStatsUpdate(
                easy = currentStats.easyStats,
                medium = currentStats.mediumStats,
                hard = currentStats.hardStats,
                expert = updatedStats
            )
            else -> DifficultyStatsUpdate(
                easy = currentStats.easyStats,
                medium = updatedStats,
                hard = currentStats.hardStats,
                expert = currentStats.expertStats
            )
        }
    }
    
    private data class DifficultyStatsUpdate(
        val easy: DifficultyStats,
        val medium: DifficultyStats,
        val hard: DifficultyStats,
        val expert: DifficultyStats
    )
}
