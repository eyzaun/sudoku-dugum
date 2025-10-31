package com.extremesudoku.domain.usecase.scoring

import com.extremesudoku.data.models.scoring.GameScore
import com.extremesudoku.data.models.scoring.ScoringConstants
import javax.inject.Inject

/**
 * Final puan hesaplama - Oyun bittiğinde kullanılır
 */
class CalculateFinalScoreUseCase @Inject constructor() {
    
    /**
     * Oyun bitişinde final puanı hesapla
     * 
     * @param gameScore Mevcut oyun puanlama durumu
     * @param elapsedTimeMs Geçen süre (milisaniye)
     * @param difficulty Zorluk seviyesi
     * @param usedNotes Not kullanıldı mı?
     * @return Güncellenmiş GameScore (final score ile)
     */
    operator fun invoke(
        gameScore: GameScore,
        elapsedTimeMs: Long,
        difficulty: String,
        usedNotes: Boolean
    ): GameScore {
        
        // 1. ZAMAN BONUSU HESAPLA
        val elapsedSeconds = elapsedTimeMs / 1000
        val timeBonus = ScoringConstants.calculateTimeBonus(difficulty, elapsedSeconds)
        
        // 2. ÖZEL BONUSLAR
        var specialBonuses = 0
        
        // Perfect game bonusu (hiç hata yok)
        if (gameScore.wrongMoves == 0 && gameScore.correctMoves > 0) {
            specialBonuses += ScoringConstants.PERFECT_GAME_BONUS
        }
        
        // Not kullanmama bonusu
        if (!usedNotes) {
            specialBonuses += ScoringConstants.NO_NOTES_BONUS
        }
        
        // Hız bonusu
        val speedBonus = if (ScoringConstants.isEligibleForSpeedBonus(difficulty, elapsedSeconds)) {
            ScoringConstants.SPEED_BONUS
        } else 0
        
        specialBonuses += speedBonus
        
        // 3. DOĞRULUK HESAPLA
        val accuracy = ScoringConstants.calculateAccuracy(
            gameScore.correctMoves,
            gameScore.totalMoves
        )
        
        // 4. FINAL PUAN HESAPLA
        val finalScore = ScoringConstants.calculateFinalScore(
            basePoints = gameScore.basePoints,
            streakBonus = gameScore.streakBonus,
            timeBonus = timeBonus,
            completionBonuses = gameScore.completionBonuses,
            specialBonuses = specialBonuses,
            penalties = gameScore.penalties,
            difficulty = difficulty
        )
        
        // 5. GÜNCELLENMIŞ GAMESCORE DÖNDÜR
        return gameScore.copy(
            timeBonus = timeBonus,
            specialBonuses = specialBonuses,
            accuracy = accuracy,
            finalScore = finalScore,
            elapsedTimeMs = elapsedTimeMs,
            difficulty = difficulty,
            playedWithoutNotes = !usedNotes,
            perfectGame = gameScore.wrongMoves == 0,
            speedBonus = speedBonus > 0
        )
    }
}
