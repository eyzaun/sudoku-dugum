package com.extremesudoku.data.models.pvp

/**
 * Oyuncu durumu
 */
enum class PlayerStatus {
    READY,    // Hazır, oyun başlasın
    PLAYING,  // Oynuyor
    FINISHED; // Bitirdi
    
    companion object {
        fun fromString(value: String): PlayerStatus {
            return values().find { it.name == value } ?: READY
        }
    }
}
