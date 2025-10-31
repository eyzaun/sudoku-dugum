package com.extremesudoku.data.models

/**
 * User profile data
 */
data class User(
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    
    // ═══════════════════════════════════════════════════════════
    // PUANLAMA SİSTEMİ
    // ═══════════════════════════════════════════════════════════
    
    /** Toplam puan (offline + online) */
    val totalScore: Long = 0,
    
    /** Offline modda kazanılan toplam puan */
    val offlineScore: Long = 0,
    
    /** Online modda kazanılan toplam puan */
    val onlineScore: Long = 0,
    
    /** En yüksek tek oyun puanı */
    val highestGameScore: Int = 0,
    
    // ═══════════════════════════════════════════════════════════
    // SIRALAMA (LEADERBOARD)
    // ═══════════════════════════════════════════════════════════
    
    /** Global sıralama */
    val globalRank: Int = 0,
    
    /** Ülke sıralaması */
    val countryRank: Int = 0,
    
    /** Ülke kodu (TR, US, UK vb.) */
    val country: String = "",
    
    // ═══════════════════════════════════════════════════════════
    // ELO RATING (PVP)
    // ═══════════════════════════════════════════════════════════
    
    /** Genel ELO rating */
    val eloRating: Int = 1000,
    
    /** Live Battle modu ELO rating */
    val liveBattleElo: Int = 1000,
    
    /** Blind Race modu ELO rating */
    val blindRaceElo: Int = 1000,
    
    // ═══════════════════════════════════════════════════════════
    // SEVİYE & DENEYIM
    // ═══════════════════════════════════════════════════════════
    
    /** Oyuncu seviyesi */
    val level: Int = 1,
    
    /** Deneyim puanı (XP) */
    val experience: Long = 0,
    
    /** Bir sonraki seviye için gereken XP */
    val nextLevelXP: Long = 1000,
    
    // ═══════════════════════════════════════════════════════════
    // BADGE & ACHIEVEMENTS
    // ═══════════════════════════════════════════════════════════
    
    /** Açılan badge'ler (ID listesi) */
    val unlockedBadges: List<String> = emptyList(),
    
    /** Favorilere eklenen badge (profilde gösterilen) */
    val featuredBadge: String? = null
)
