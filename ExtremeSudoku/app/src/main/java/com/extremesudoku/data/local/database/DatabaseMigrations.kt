package com.extremesudoku.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migrations for Room database schema changes
 */
object DatabaseMigrations {
    
    /**
     * Migration 3 -> 4: Add scoring fields to game_states table
     * 
     * Added fields:
     * - score: Total score for the game
     * - basePoints: Points from correct moves
     * - streakBonus: Bonus points from streaks
     * - timeBonus: Bonus points from speed
     * - completionBonus: Bonus from box/row/column completions
     * - currentStreak: Current streak count
     * - maxStreak: Highest streak achieved
     * - correctMoves: Number of correct moves
     * - wrongMoves: Number of wrong moves
     * - accuracy: Accuracy percentage (0.0 - 1.0)
     */
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add scoring columns to game_states table
            db.execSQL("""
                ALTER TABLE game_states ADD COLUMN score INTEGER NOT NULL DEFAULT 0
            """.trimIndent())
            
            db.execSQL("""
                ALTER TABLE game_states ADD COLUMN basePoints INTEGER NOT NULL DEFAULT 0
            """.trimIndent())
            
            db.execSQL("""
                ALTER TABLE game_states ADD COLUMN streakBonus INTEGER NOT NULL DEFAULT 0
            """.trimIndent())
            
            db.execSQL("""
                ALTER TABLE game_states ADD COLUMN timeBonus INTEGER NOT NULL DEFAULT 0
            """.trimIndent())
            
            db.execSQL("""
                ALTER TABLE game_states ADD COLUMN completionBonus INTEGER NOT NULL DEFAULT 0
            """.trimIndent())
            
            db.execSQL("""
                ALTER TABLE game_states ADD COLUMN currentStreak INTEGER NOT NULL DEFAULT 0
            """.trimIndent())
            
            db.execSQL("""
                ALTER TABLE game_states ADD COLUMN maxStreak INTEGER NOT NULL DEFAULT 0
            """.trimIndent())
            
            db.execSQL("""
                ALTER TABLE game_states ADD COLUMN correctMoves INTEGER NOT NULL DEFAULT 0
            """.trimIndent())
            
            db.execSQL("""
                ALTER TABLE game_states ADD COLUMN wrongMoves INTEGER NOT NULL DEFAULT 0
            """.trimIndent())
            
            db.execSQL("""
                ALTER TABLE game_states ADD COLUMN accuracy REAL NOT NULL DEFAULT 0.0
            """.trimIndent())
            
            android.util.Log.i("DatabaseMigration", "✅ Successfully migrated from version 3 to 4 - Added scoring fields")
        }
    }
    
    /**
     * Get all migrations as an array
     */
    fun getAllMigrations(): Array<Migration> {
        return arrayOf(
            MIGRATION_3_4
        )
    }
}
