package com.extremesudoku.domain.repositories

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val soundEnabled: Flow<Boolean>
    val vibrationEnabled: Flow<Boolean>
    val highlightConflicts: Flow<Boolean>
    val highlightSameNumbers: Flow<Boolean>
    val showRemainingNumbers: Flow<Boolean>
    val showTimer: Flow<Boolean>
    val autoCheckMistakes: Flow<Boolean>
    val highlightSelectedArea: Flow<Boolean>  // Row/Column/Box highlight
    val autoRemoveNotes: Flow<Boolean>  // Auto-remove notes when placing number
    val showAffectedAreas: Flow<Boolean>  // Show affected areas from number pad
    val showScoreAndStreak: Flow<Boolean>  // NEW: Show score and streak display
    val colorizeNumbers: Flow<Boolean>  // NEW: Colorize correct/wrong numbers
    
    suspend fun setSoundEnabled(enabled: Boolean)
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setHighlightConflicts(enabled: Boolean)
    suspend fun setHighlightSameNumbers(enabled: Boolean)
    suspend fun setShowRemainingNumbers(enabled: Boolean)
    suspend fun setShowTimer(enabled: Boolean)
    suspend fun setAutoCheckMistakes(enabled: Boolean)
    suspend fun setHighlightSelectedArea(enabled: Boolean)
    suspend fun setAutoRemoveNotes(enabled: Boolean)
    suspend fun setShowAffectedAreas(enabled: Boolean)
    suspend fun setShowScoreAndStreak(enabled: Boolean)  // NEW
    suspend fun setColorizeNumbers(enabled: Boolean)  // NEW
}
