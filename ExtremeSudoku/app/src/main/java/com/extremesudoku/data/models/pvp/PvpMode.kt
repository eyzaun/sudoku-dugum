package com.extremesudoku.data.models.pvp

/**
 * PvP oyun modları
 */
enum class PvpMode(val displayName: String, val description: String) {
    BLIND_RACE(
        displayName = "Kör Yarış",
        description = "Rakibi görmeden yarış! Kim daha hızlı tamamlar?"
    ),
    LIVE_BATTLE(
        displayName = "Canlı Savaş",
        description = "Rakibi izle! Kim daha fazla doğru sayı koyar?"
    );
    
    companion object {
        fun fromString(value: String): PvpMode {
            return values().find { it.name == value } ?: BLIND_RACE
        }
    }
}
