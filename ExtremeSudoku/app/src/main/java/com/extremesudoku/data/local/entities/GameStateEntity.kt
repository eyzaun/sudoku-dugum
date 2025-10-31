package com.extremesudoku.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.extremesudoku.data.models.GameState

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
    // Build scoreDetails as JSON string
    val scoreDetailsJson = """
        {
            "basePoints": $basePoints,
            "streakBonus": $streakBonus,
            "timeBonus": $timeBonus,
            "completionBonus": $completionBonus,
            "currentStreak": $currentStreak,
            "maxStreak": $maxStreak,
            "correctMoves": $correctMoves,
            "wrongMoves": $wrongMoves,
            "accuracy": $accuracy
        }
    """.trimIndent()
    
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
        scoreDetails = scoreDetailsJson
    )
}

fun GameState.toEntity(userId: String? = null): GameStateEntity {
    // Simple parsing for scoreDetails - assumes empty or simple values
    // In production, use proper JSON parsing library
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
        score = score,
        basePoints = 0, // These would be parsed from scoreDetails JSON in production
        streakBonus = 0,
        timeBonus = 0,
        completionBonus = 0,
        currentStreak = 0,
        maxStreak = 0,
        correctMoves = 0,
        wrongMoves = 0,
        accuracy = 0f
    )
}
