package com.extremesudoku.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.extremesudoku.data.local.dao.GameStateDao
import com.extremesudoku.data.local.dao.SudokuDao
import com.extremesudoku.data.local.dao.UserStatsDao
import com.extremesudoku.data.local.database.DatabaseMigrations
import com.extremesudoku.data.local.database.SudokuDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // userId kolonunu ekle - default olarak "unknown" kullan (eski kayıtlar için)
            db.execSQL(
                "ALTER TABLE game_states ADD COLUMN userId TEXT NOT NULL DEFAULT 'unknown'"
            )
            // Index ekle
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_game_states_userId ON game_states(userId)"
            )
        }
    }
    
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // difficulty ve isAbandoned kolonlarını ekle
            db.execSQL(
                "ALTER TABLE game_states ADD COLUMN difficulty TEXT NOT NULL DEFAULT 'medium'"
            )
            db.execSQL(
                "ALTER TABLE game_states ADD COLUMN isAbandoned INTEGER NOT NULL DEFAULT 0"
            )
            // Index ekle
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_game_states_isAbandoned ON game_states(isAbandoned)"
            )
        }
    }
    
    @Provides
    @Singleton
    fun provideSudokuDatabase(
        @ApplicationContext context: Context
    ): SudokuDatabase {
        return Room.databaseBuilder(
            context,
            SudokuDatabase::class.java,
            "sudoku_database"
        )
        .addMigrations(
            MIGRATION_1_2, 
            MIGRATION_2_3,
            DatabaseMigrations.MIGRATION_3_4 // Added scoring fields migration
        )
        .build()
    }
    
    @Provides
    @Singleton
    fun provideSudokuDao(database: SudokuDatabase): SudokuDao {
        return database.sudokuDao()
    }
    
    @Provides
    @Singleton
    fun provideGameStateDao(database: SudokuDatabase): GameStateDao {
        return database.gameStateDao()
    }
    
    @Provides
    @Singleton
    fun provideUserStatsDao(database: SudokuDatabase): UserStatsDao {
        return database.userStatsDao()
    }
}
