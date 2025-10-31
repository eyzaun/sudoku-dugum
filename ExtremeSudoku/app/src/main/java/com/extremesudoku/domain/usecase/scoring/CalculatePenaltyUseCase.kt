package com.extremesudoku.domain.usecase.scoring

import com.extremesudoku.data.models.scoring.GameScore
import com.extremesudoku.data.models.scoring.ScoringConstants
import javax.inject.Inject

/**
 * Hint ve Error Check kullanımı için ceza hesaplama
 */
class CalculatePenaltyUseCase @Inject constructor() {
    
    /**
     * Hint kullanımı cezası
     * - Puan düşürür
     * - Seriyi sıfırlar
     */
    fun calculateHintPenalty(currentScore: GameScore): GameScore {
        val newPenalties = currentScore.penalties + (-ScoringConstants.HINT_PENALTY) // Pozitif değer
        
        // FINAL SCORE HESAPLA
        val newFinalScore = ((currentScore.basePoints + currentScore.streakBonus + 
                             currentScore.timeBonus + currentScore.completionBonuses + 
                             currentScore.specialBonuses - newPenalties) * 
                             currentScore.difficultyMultiplier).toInt()
        
        return currentScore.copy(
            finalScore = newFinalScore,  // FIX: finalScore'u hesapla!
            penalties = newPenalties,
            currentStreak = 0, // SERİ KIRILDI
            streakBroken = currentScore.streakBroken + 1,
            hintsUsed = currentScore.hintsUsed + 1
        )
    }
    
    /**
     * Error Check kullanımı cezası
     * - Puan düşürür
     * - Seriyi sıfırlar
     */
    fun calculateErrorCheckPenalty(currentScore: GameScore): GameScore {
        val newPenalties = currentScore.penalties + (-ScoringConstants.ERROR_CHECK_PENALTY) // Pozitif değer
        
        // FINAL SCORE HESAPLA
        val newFinalScore = ((currentScore.basePoints + currentScore.streakBonus + 
                             currentScore.timeBonus + currentScore.completionBonuses + 
                             currentScore.specialBonuses - newPenalties) * 
                             currentScore.difficultyMultiplier).toInt()
        
        return currentScore.copy(
            finalScore = newFinalScore,  // FIX: finalScore'u hesapla!
            penalties = newPenalties,
            currentStreak = 0, // SERİ KIRILDI
            streakBroken = currentScore.streakBroken + 1,
            errorChecksUsed = currentScore.errorChecksUsed + 1
        )
    }
}
