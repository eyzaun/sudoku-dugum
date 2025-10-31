package com.extremesudoku.data.repository

import com.extremesudoku.data.models.LeaderboardEntry
import com.extremesudoku.data.remote.FirebaseDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    fun getLeaderboard(limit: Int = 100): Flow<List<LeaderboardEntry>> {
        return firebaseDataSource.getLeaderboard(limit)
    }
    
    suspend fun updateLeaderboard(entry: LeaderboardEntry): Result<Unit> {
        return firebaseDataSource.updateLeaderboard(entry)
    }
}
