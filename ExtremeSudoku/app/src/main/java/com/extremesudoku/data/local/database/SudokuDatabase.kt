package com.extremesudoku.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.extremesudoku.data.local.dao.GameStateDao
import com.extremesudoku.data.local.dao.SudokuDao
import com.extremesudoku.data.local.dao.UserStatsDao
import com.extremesudoku.data.local.entities.GameStateEntity
import com.extremesudoku.data.local.entities.SudokuEntity
import com.extremesudoku.data.local.entities.UserStatsEntity

@Database(
    entities = [
        SudokuEntity::class,
        GameStateEntity::class,
        UserStatsEntity::class
    ],
    version = 4, // Incremented for scoring fields addition
    exportSchema = false
)
abstract class SudokuDatabase : RoomDatabase() {
    abstract fun sudokuDao(): SudokuDao
    abstract fun gameStateDao(): GameStateDao
    abstract fun userStatsDao(): UserStatsDao
}
