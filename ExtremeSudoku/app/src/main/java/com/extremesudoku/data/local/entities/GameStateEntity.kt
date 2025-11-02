package com.extremesudoku.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.extremesudoku.data.models.GameState
import com.extremesudoku.data.models.scoring.GameScore
import com.extremesudoku.data.models.scoring.ScoringConstants
import com.extremesudoku.data.models.scoring.toJsonString

@Entity(
    tableName = "game_states",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["isCompleted"]),
        Index(value = ["isAbandoned"]),
        Index(value = ["lastPlayedAt"])
    ]
)
data class GameStateEntity(
    @PrimaryKey val gameId: String,
    val userId: String,
    val sudokuId: String,
    val difficulty: String = "medium",
    val currentState: String,
    val notes: String,
    val elapsedTime: Long,
    val moves: Int,
    val hintsUsed: Int,
    val isCompleted: Boolean,
    val isAbandoned: Boolean = false,
    val lastPlayedAt: Long,
    val createdAt: Long,
    // **SCORING FIELDS** - Added for comprehensive scoring system
    val score: Int = 0,
    val basePoints: Int = 0,
    val streakBonus: Int = 0,
    val timeBonus: Int = 0,
    val completionBonus: Int = 0,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val correctMoves: Int = 0,
    val wrongMoves: Int = 0,
    val accuracy: Float = 0f
)

fun GameStateEntity.toDomain(): GameState {
    val scoreSnapshot = GameScore(
        finalScore = score,
        basePoints = basePoints,
        streakBonus = streakBonus,
        timeBonus = timeBonus,
        completionBonuses = completionBonus,
        difficultyMultiplier = ScoringConstants.getDifficultyMultiplier(difficulty),
        currentStreak = currentStreak,
        maxStreak = maxStreak,
        correctMoves = correctMoves,
        wrongMoves = wrongMoves,
        totalMoves = correctMoves + wrongMoves,
        accuracy = accuracy,
        hintsUsed = hintsUsed,
        elapsedTimeMs = elapsedTime * 1000,
        difficulty = difficulty
    )
    
    return GameState(
        gameId = gameId,
        userId = userId,
        sudokuId = sudokuId,
        difficulty = difficulty,
        currentState = currentState,
        notes = notes,
        elapsedTime = elapsedTime,
        moves = moves,
        hintsUsed = hintsUsed,
        isCompleted = isCompleted,
        isAbandoned = isAbandoned,
        lastPlayedAt = lastPlayedAt,
        createdAt = createdAt,
        score = score,
        scoreDetails = scoreSnapshot.toJsonString()
    )
}

fun GameState.toEntity(userId: String? = null): GameStateEntity {
    val parsedScore = GameScore.fromJsonString(scoreDetails)
    val resolvedFinalScore = if (score != 0) score else parsedScore.finalScore
    val resolvedTotalMoves = if (parsedScore.totalMoves != 0) parsedScore.totalMoves else moves
    val resolvedElapsedTimeMs = if (parsedScore.elapsedTimeMs != 0L) parsedScore.elapsedTimeMs else elapsedTime * 1000
    val resolvedAccuracy = if (parsedScore.totalMoves > 0) parsedScore.calculateAccuracy() else parsedScore.accuracy
    val resolvedScore = parsedScore.copy(
        finalScore = resolvedFinalScore,
        totalMoves = resolvedTotalMoves,
        elapsedTimeMs = resolvedElapsedTimeMs,
        difficulty = difficulty,
        difficultyMultiplier = ScoringConstants.getDifficultyMultiplier(difficulty),
        hintsUsed = hintsUsed,
        accuracy = resolvedAccuracy
    )
    
    return GameStateEntity(
        gameId = gameId,
        userId = userId ?: this.userId, // Parameter varsa onu kullan, yoksa GameState'teki userId'yi al
        sudokuId = sudokuId,
        difficulty = difficulty,
        currentState = currentState,
        notes = notes,
        elapsedTime = elapsedTime,
        moves = moves,
        hintsUsed = hintsUsed,
        isCompleted = isCompleted,
        isAbandoned = isAbandoned,
        lastPlayedAt = lastPlayedAt,
        createdAt = createdAt,
        score = resolvedScore.finalScore,
        basePoints = resolvedScore.basePoints,
        streakBonus = resolvedScore.streakBonus,
        timeBonus = resolvedScore.timeBonus,
        completionBonus = resolvedScore.completionBonuses,
        currentStreak = resolvedScore.currentStreak,
        maxStreak = resolvedScore.maxStreak,
        correctMoves = resolvedScore.correctMoves,
        wrongMoves = resolvedScore.wrongMoves,
        accuracy = resolvedScore.accuracy
    )
}
