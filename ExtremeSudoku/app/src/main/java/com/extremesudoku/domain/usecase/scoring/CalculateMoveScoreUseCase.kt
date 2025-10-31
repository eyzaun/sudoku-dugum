package com.extremesudoku.domain.usecase.scoring

import com.extremesudoku.data.models.scoring.GameScore
import com.extremesudoku.data.models.scoring.ScoringConstants
import com.extremesudoku.data.models.scoring.StreakEvent
import javax.inject.Inject

/**
 * Hamle bazında puan hesaplama
 * Her sayı yerleştirildiğinde kullanılır
 */
class CalculateMoveScoreUseCase @Inject constructor() {
    
    /**
     * Bir hamlenin puanını hesapla
     * 
     * @param currentScore Mevcut oyun puanı
     * @param isCorrect Hamle doğru mu?
     * @param row Hamlenin satırı
     * @param col Hamlenin sütunu
     * @param number Yerleştirilen sayı
     * @return Güncellenmiş GameScore ve kazanılan puan
     */
    @Suppress("UNUSED_PARAMETER")
    operator fun invoke(
        currentScore: GameScore,
        isCorrect: Boolean,
        row: Int,
        col: Int,
        number: Int
    ): Pair<GameScore, Int> {
        
        if (isCorrect) {
            // DOĞRU HAMLE
            val newStreak = currentScore.currentStreak + 1
            val streakBonus = ScoringConstants.calculateStreakBonus(newStreak)
            val totalPointsForMove = ScoringConstants.CORRECT_PLACEMENT + streakBonus
            
            val newBasePoints = currentScore.basePoints + ScoringConstants.CORRECT_PLACEMENT
            val newStreakBonus = currentScore.streakBonus + streakBonus
            
            // FINAL SCORE HESAPLA
            val newFinalScore = ((newBasePoints + newStreakBonus + currentScore.timeBonus + 
                                 currentScore.completionBonuses + currentScore.specialBonuses - 
                                 currentScore.penalties) * currentScore.difficultyMultiplier).toInt()
            
            val updatedScore = currentScore.copy(
                finalScore = newFinalScore,  // FIX: finalScore'u hesapla!
                basePoints = newBasePoints,
                streakBonus = newStreakBonus,
                currentStreak = newStreak,
                maxStreak = maxOf(currentScore.maxStreak, newStreak),
                correctMoves = currentScore.correctMoves + 1,
                totalMoves = currentScore.totalMoves + 1
            )
            
            return updatedScore to totalPointsForMove
            
        } else {
            // YANLIŞ HAMLE - Seri sıfırlanır
            val newBasePoints = currentScore.basePoints + ScoringConstants.WRONG_PLACEMENT
            
            // FINAL SCORE HESAPLA
            val newFinalScore = ((newBasePoints + currentScore.streakBonus + currentScore.timeBonus + 
                                 currentScore.completionBonuses + currentScore.specialBonuses - 
                                 currentScore.penalties) * currentScore.difficultyMultiplier).toInt()
            
            val updatedScore = currentScore.copy(
                finalScore = newFinalScore,  // FIX: finalScore'u hesapla!
                basePoints = newBasePoints,
                currentStreak = 0, // SERİ KIRILDI!
                streakBroken = currentScore.streakBroken + 1,
                wrongMoves = currentScore.wrongMoves + 1,
                totalMoves = currentScore.totalMoves + 1
            )
            
            return updatedScore to ScoringConstants.WRONG_PLACEMENT
        }
    }
    
    /**
     * Boş hücre silme cezası
     */
    fun calculateEmptyDeletePenalty(currentScore: GameScore): GameScore {
        return currentScore.copy(
            basePoints = currentScore.basePoints + ScoringConstants.EMPTY_CELL_DELETE
        )
    }
}
