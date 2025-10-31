package com.extremesudoku.data.local.dao

import androidx.room.*
import com.extremesudoku.data.local.entities.GameStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameStateDao {
    @Query("SELECT * FROM game_states WHERE gameId = :gameId")
    suspend fun getGameState(gameId: String): GameStateEntity?
    
    @Query("SELECT * FROM game_states WHERE userId = :userId AND isCompleted = 0 AND isAbandoned = 0 ORDER BY lastPlayedAt DESC")
    fun getActiveGames(userId: String): Flow<List<GameStateEntity>>
    
    @Query("SELECT * FROM game_states WHERE userId = :userId AND isCompleted = 0 AND isAbandoned = 0 ORDER BY lastPlayedAt DESC")
    suspend fun getActiveGamesOnce(userId: String): List<GameStateEntity>
    
    @Query("SELECT * FROM game_states WHERE userId = :userId AND isCompleted = 1 ORDER BY lastPlayedAt DESC LIMIT :limit")
    fun getCompletedGames(userId: String, limit: Int = 10): Flow<List<GameStateEntity>>
    
    @Query("SELECT * FROM game_states WHERE userId = :userId AND isCompleted = 1 ORDER BY lastPlayedAt DESC LIMIT :limit")
    suspend fun getCompletedGamesOnce(userId: String, limit: Int = 10): List<GameStateEntity>
    
    @Query("SELECT * FROM game_states WHERE userId = :userId AND isAbandoned = 1 ORDER BY lastPlayedAt DESC")
    fun getAbandonedGames(userId: String): Flow<List<GameStateEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGameState(gameState: GameStateEntity)
    
    @Delete
    suspend fun deleteGameState(gameState: GameStateEntity)
    
    @Query("DELETE FROM game_states WHERE gameId = :gameId")
    suspend fun deleteGameById(gameId: String)
    
    @Query("DELETE FROM game_states")
    suspend fun deleteAll()
}
