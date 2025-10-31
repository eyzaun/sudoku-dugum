package com.extremesudoku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.extremesudoku.data.local.entities.SudokuEntity

@Dao
interface SudokuDao {
    @Query("SELECT * FROM sudokus WHERE id = :id")
    suspend fun getSudokuById(id: String): SudokuEntity?
    
    @Query("SELECT * FROM sudokus ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomSudoku(): SudokuEntity?
    
    @Query("SELECT * FROM sudokus WHERE LOWER(difficulty) = LOWER(:difficulty) ORDER BY RANDOM()")
    suspend fun getSudokuByDifficulty(difficulty: String): List<SudokuEntity>
    
    @Query("""
        SELECT * FROM sudokus 
        WHERE LOWER(difficulty) = LOWER(:difficulty)
        AND id NOT IN (
            SELECT sudokuId FROM game_states WHERE isCompleted = 1
        )
        ORDER BY RANDOM()
    """)
    suspend fun getUnplayedSudokuByDifficulty(difficulty: String): List<SudokuEntity>
    
    @Query("""
        SELECT * FROM sudokus 
        WHERE id NOT IN (
            SELECT sudokuId FROM game_states WHERE isCompleted = 1
        )
        ORDER BY RANDOM() 
        LIMIT 1
    """)
    suspend fun getRandomUnplayedSudoku(): SudokuEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSudokus(sudokus: List<SudokuEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSudoku(sudoku: SudokuEntity)
    
    @Query("SELECT COUNT(*) FROM sudokus")
    suspend fun getSudokuCount(): Int
    
    @Query("SELECT COUNT(*) FROM sudokus WHERE difficulty = :difficulty")
    suspend fun getSudokuCountByDifficulty(difficulty: String): Int
    
    @Query("DELETE FROM sudokus")
    suspend fun deleteAll()
}
