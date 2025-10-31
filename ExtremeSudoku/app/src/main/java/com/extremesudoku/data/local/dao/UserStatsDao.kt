package com.extremesudoku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.extremesudoku.data.local.entities.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    fun getUserStats(userId: String): Flow<UserStatsEntity?>
    
    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    suspend fun getUserStatsOnce(userId: String): UserStatsEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserStats(stats: UserStatsEntity)
    
    @Query("DELETE FROM user_stats WHERE userId = :userId")
    suspend fun deleteUserStats(userId: String)
    
    @Query("DELETE FROM user_stats")
    suspend fun deleteAll()
}
