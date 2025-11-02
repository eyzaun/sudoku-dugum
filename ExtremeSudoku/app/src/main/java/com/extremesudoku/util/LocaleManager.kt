package com.extremesudoku.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleManager {
    private const val PREF_LANGUAGE = "app_language"
    
    enum class Language(val code: String, val displayName: String) {
        ENGLISH("en", "English"),
        TURKISH("tr", "Türkçe");
        
        companion object {
            fun fromCode(code: String): Language {
                return values().find { it.code == code } ?: ENGLISH
            }
        }
    }
    
    /**
     * Kaydedilmiş dil tercihini al
     */
    fun getSavedLanguage(context: Context): Language {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val code = prefs.getString(PREF_LANGUAGE, Language.ENGLISH.code) ?: Language.ENGLISH.code
        return Language.fromCode(code)
    }
    
    /**
     * Dil tercihini kaydet
     */
    fun saveLanguage(context: Context, language: Language) {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit().putString(PREF_LANGUAGE, language.code).apply()
    }
    
    /**
     * Context'e locale uygula
     */
    fun setLocale(context: Context, language: Language): Context {
        saveLanguage(context, language)
        return updateResources(context, language.code)
    }
    
    /**
     * Kaydedilmiş locale'i uygula
     */
    fun applyStoredLocale(context: Context): Context {
        val language = getSavedLanguage(context)
        return updateResources(context, language.code)
    }
    
    private fun updateResources(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }
    
    /**
     * Sistem dilini al
     */
    fun getSystemLanguage(): Language {
        val systemLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Locale.getDefault().language
        } else {
            @Suppress("DEPRECATION")
            Locale.getDefault().language
        }
        
        return Language.values().find { it.code == systemLanguage } ?: Language.ENGLISH
    }
}
