package com.extremesudoku.data.repositories

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.extremesudoku.domain.repositories.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {
    
    private object PreferencesKeys {
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val HIGHLIGHT_CONFLICTS = booleanPreferencesKey("highlight_conflicts")
        val HIGHLIGHT_SAME_NUMBERS = booleanPreferencesKey("highlight_same_numbers")
        val SHOW_REMAINING_NUMBERS = booleanPreferencesKey("show_remaining_numbers")
        val SHOW_TIMER = booleanPreferencesKey("show_timer")
        val AUTO_CHECK_MISTAKES = booleanPreferencesKey("auto_check_mistakes")
        val HIGHLIGHT_SELECTED_AREA = booleanPreferencesKey("highlight_selected_area")
        val AUTO_REMOVE_NOTES = booleanPreferencesKey("auto_remove_notes")
        val SHOW_AFFECTED_AREAS = booleanPreferencesKey("show_affected_areas")
        val SHOW_SCORE_AND_STREAK = booleanPreferencesKey("show_score_and_streak")  // NEW
        val COLORIZE_NUMBERS = booleanPreferencesKey("colorize_numbers")  // NEW
    }
    
    override val soundEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.SOUND_ENABLED] ?: true }
    
    override val vibrationEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true }
    
    override val highlightConflicts: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.HIGHLIGHT_CONFLICTS] ?: true }
    
    override val highlightSameNumbers: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.HIGHLIGHT_SAME_NUMBERS] ?: true }
    
    override val showRemainingNumbers: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.SHOW_REMAINING_NUMBERS] ?: true }
    
    override val showTimer: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.SHOW_TIMER] ?: true }
    
    override val autoCheckMistakes: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.AUTO_CHECK_MISTAKES] ?: false }
    
    override val highlightSelectedArea: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.HIGHLIGHT_SELECTED_AREA] ?: true }
    
    override val autoRemoveNotes: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.AUTO_REMOVE_NOTES] ?: true }
    
    override val showAffectedAreas: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.SHOW_AFFECTED_AREAS] ?: true }
    
    override val showScoreAndStreak: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.SHOW_SCORE_AND_STREAK] ?: true }
    
    override val colorizeNumbers: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.COLORIZE_NUMBERS] ?: true }
    
    override suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }
    
    override suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] = enabled
        }
    }
    
    override suspend fun setHighlightConflicts(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HIGHLIGHT_CONFLICTS] = enabled
        }
    }
    
    override suspend fun setAutoCheckMistakes(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_CHECK_MISTAKES] = enabled
        }
    }
    
    override suspend fun setHighlightSameNumbers(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HIGHLIGHT_SAME_NUMBERS] = enabled
        }
    }
    
    override suspend fun setShowRemainingNumbers(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_REMAINING_NUMBERS] = enabled
        }
    }
    
    override suspend fun setShowTimer(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_TIMER] = enabled
        }
    }
    
    override suspend fun setHighlightSelectedArea(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HIGHLIGHT_SELECTED_AREA] = enabled
        }
    }
    
    override suspend fun setAutoRemoveNotes(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_REMOVE_NOTES] = enabled
        }
    }
    
    override suspend fun setShowAffectedAreas(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_AFFECTED_AREAS] = enabled
        }
    }
    
    override suspend fun setShowScoreAndStreak(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_SCORE_AND_STREAK] = enabled
        }
    }
    
    override suspend fun setColorizeNumbers(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.COLORIZE_NUMBERS] = enabled
        }
    }
}
