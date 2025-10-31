package com.extremesudoku.utils

object Constants {
    const val MAX_HINTS = 3
    const val CACHE_SUDOKU_COUNT = 50
    const val LEADERBOARD_LIMIT = 100
    
    // Firestore Collections
    const val COLLECTION_SUDOKUS = "sudokus"
    const val COLLECTION_USERS = "users"
    const val COLLECTION_LEADERBOARD = "leaderboard"
    
    // SharedPreferences Keys
    const val PREF_NAME = "sudoku_prefs"
    const val KEY_THEME_MODE = "theme_mode"
    const val KEY_SOUND_ENABLED = "sound_enabled"
    const val KEY_VIBRATION_ENABLED = "vibration_enabled"
    
    // Game States
    const val GAME_STATE_PLAYING = "playing"
    const val GAME_STATE_PAUSED = "paused"
    const val GAME_STATE_COMPLETED = "completed"
}
