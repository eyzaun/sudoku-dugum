package com.extremesudoku.data.models.pvp

/**
 * Match durumu
 */
enum class MatchStatus {
    WAITING,      // Oyuncu bekleniyor
    IN_PROGRESS,  // Oyun devam ediyor
    COMPLETED,    // Oyun bitti
    CANCELLED;    // İptal edildi
    
    companion object {
        fun fromString(value: String): MatchStatus {
            return values().find { it.name == value } ?: WAITING
        }
    }
}
