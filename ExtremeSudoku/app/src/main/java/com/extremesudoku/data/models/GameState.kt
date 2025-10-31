package com.extremesudoku.data.models

import com.extremesudoku.data.models.scoring.GameScore

data class GameState(
    val gameId: String = "",
    val userId: String = "",
    val sudokuId: String = "",
    val difficulty: String = "medium",
    val currentState: String = "", // 81 karakter (mevcut durum)
    val notes: String = "", // JSON format (her hücre için notlar)
    val elapsedTime: Long = 0, // saniye cinsinden
    val moves: Int = 0,
    val hintsUsed: Int = 0,
    val isCompleted: Boolean = false,
    val isAbandoned: Boolean = false,
    val lastPlayedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    
    // ═══════════════════════════════════════════════════════════
    // PUANLAMA SİSTEMİ
    // ═══════════════════════════════════════════════════════════
    
    /** Oyunun puanlama verileri */
    val score: Int = 0,  // Final puan (backward compatibility)
    
    /** Detaylı puan bilgisi (JSON string olarak saklanabilir) */
    val scoreDetails: String = ""  // GameScore JSON'u
)
