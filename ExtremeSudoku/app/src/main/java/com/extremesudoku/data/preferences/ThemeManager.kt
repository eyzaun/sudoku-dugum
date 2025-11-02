package com.extremesudoku.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.extremesudoku.presentation.theme.ThemeType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val THEME_TYPE = stringPreferencesKey("theme_type")
    }
    
    /**
     * Get current theme type as Flow
     */
    val themeType: Flow<ThemeType> = context.dataStore.data
        .map { preferences ->
            val themeName = preferences[PreferencesKeys.THEME_TYPE] ?: ThemeType.GAZETE.name
            try {
                ThemeType.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                ThemeType.GAZETE
            }
        }
    
    /**
     * Save theme type preference
     */
    suspend fun setThemeType(themeType: ThemeType) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_TYPE] = themeType.name
        }
    }
}
