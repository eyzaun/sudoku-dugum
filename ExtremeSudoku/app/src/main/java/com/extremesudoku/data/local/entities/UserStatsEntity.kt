package com.extremesudoku.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.extremesudoku.data.models.UserStats

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val userId: String,
    val gamesPlayed: Int,
    val gamesCompleted: Int,
    val totalTime: Long,
    val bestTime: Long,
    val averageTime: Long,
    val hintsUsed: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastPlayedDate: Long
)

fun UserStatsEntity.toDomain(): UserStats {
    return UserStats(
        gamesPlayed = gamesPlayed,
        gamesCompleted = gamesCompleted,
        totalTime = totalTime,
        bestTime = bestTime,
        averageTime = averageTime,
        hintsUsed = hintsUsed,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        lastPlayedDate = lastPlayedDate
    )
}

fun UserStats.toEntity(userId: String): UserStatsEntity {
    return UserStatsEntity(
        userId = userId,
        gamesPlayed = gamesPlayed,
        gamesCompleted = gamesCompleted,
        totalTime = totalTime,
        bestTime = bestTime,
        averageTime = averageTime,
        hintsUsed = hintsUsed,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        lastPlayedDate = lastPlayedDate
    )
}
