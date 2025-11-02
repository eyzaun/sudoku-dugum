package com.extremesudoku.data.models.scoring

import org.json.JSONException
import org.json.JSONObject

/**
 * Oyun puanlama sistemi - Tetris tabanlı rekabetçi model
 * 
 * Puan Kaynakları:
 * 1. Base Points: Doğru/Yanlış hareketler
 * 2. Streak Bonus: Art arda doğru hamle bonusu (Tetris combo)
 * 3. Time Bonus: Hız bonusu (azalan havuz)
 * 4. Completion Bonuses: Kutu/Sıra/Sütun tamamlama
 * 5. Special Bonuses: Perfect game, no hints
 * 6. Penalties: Hint/Error check kullanımı
 * 7. Difficulty Multiplier: Final çarpan
 */
data class GameScore(
    // TOPLAM PUAN (FINAL)
    val finalScore: Int = 0,
    
    // PUAN BİLEŞENLERİ
    val basePoints: Int = 0,              // Temel puanlar (correct - wrong)
    val streakBonus: Int = 0,             // Art arda doğru hamle bonusu
    val timeBonus: Int = 0,               // Zaman bonusu
    val completionBonuses: Int = 0,       // Kutu/Sıra/Sütun tamamlama
    val specialBonuses: Int = 0,          // Perfect game, no notes vb.
    val penalties: Int = 0,               // Hint/Error check cezaları
    val difficultyMultiplier: Float = 1.0f,
    
    // STREAK DETAYLARI
    val currentStreak: Int = 0,           // Mevcut seri
    val maxStreak: Int = 0,               // Oyundaki en uzun seri
    val streakBroken: Int = 0,            // Kaç kez seri kırıldı
    
    // HAREKET İSTATİSTİKLERİ
    val correctMoves: Int = 0,            // Doğru hamle sayısı
    val wrongMoves: Int = 0,              // Yanlış hamle sayısı
    val totalMoves: Int = 0,              // Toplam hamle
    val accuracy: Float = 0f,             // Doğruluk yüzdesi (0-100)
    
    // YARDIM KULLANIMI
    val hintsUsed: Int = 0,               // Kullanılan hint sayısı
    val errorChecksUsed: Int = 0,         // Hata kontrolü sayısı
    
    // TAMAMLAMA İSTATİSTİKLERİ
    val boxesCompleted: Int = 0,          // Tamamlanan 3x3 kutu sayısı
    val rowsCompleted: Int = 0,           // Tamamlanan sıra sayısı
    val columnsCompleted: Int = 0,        // Tamamlanan sütun sayısı
    
    // ÖZEL BAŞARILAR
    val playedWithoutNotes: Boolean = false,  // Not kullanmadan oynadı
    val perfectGame: Boolean = false,         // Hiç hata yapmadı
    val speedBonus: Boolean = false,          // Çok hızlı bitirdi
    
    // ZAMAN
    val elapsedTimeMs: Long = 0,          // Geçen süre (ms)
    val difficulty: String = "medium"      // Zorluk seviyesi
) {
    /**
     * Alt toplam (multiplier öncesi)
     */
    fun getSubtotal(): Int {
        return basePoints + streakBonus + timeBonus + 
               completionBonuses + specialBonuses - penalties
    }
    
    /**
     * Doğruluk oranı hesapla
     */
    fun calculateAccuracy(): Float {
        return if (totalMoves > 0) {
            (correctMoves.toFloat() / totalMoves.toFloat()) * 100f
        } else 0f
    }
    
    /**
     * Ortalama seri uzunluğu
     */
    fun getAverageStreakLength(): Float {
        return if (streakBroken > 0) {
            correctMoves.toFloat() / (streakBroken + 1).toFloat()
        } else correctMoves.toFloat()
    }

    companion object {
        fun fromJsonString(jsonString: String?): GameScore {
            if (jsonString.isNullOrBlank()) {
                return GameScore()
            }
            return try {
                val json = JSONObject(jsonString)
                GameScore(
                    finalScore = json.optInt("finalScore", 0),
                    basePoints = json.optInt("basePoints", 0),
                    streakBonus = json.optInt("streakBonus", 0),
                    timeBonus = json.optInt("timeBonus", 0),
                    completionBonuses = json.optInt("completionBonuses", 0),
                    specialBonuses = json.optInt("specialBonuses", 0),
                    penalties = json.optInt("penalties", 0),
                    difficultyMultiplier = json.optDouble("difficultyMultiplier", 1.0).toFloat(),
                    currentStreak = json.optInt("currentStreak", 0),
                    maxStreak = json.optInt("maxStreak", 0),
                    streakBroken = json.optInt("streakBroken", 0),
                    correctMoves = json.optInt("correctMoves", 0),
                    wrongMoves = json.optInt("wrongMoves", 0),
                    totalMoves = json.optInt("totalMoves", 0),
                    accuracy = json.optDouble("accuracy", 0.0).toFloat(),
                    hintsUsed = json.optInt("hintsUsed", 0),
                    errorChecksUsed = json.optInt("errorChecksUsed", 0),
                    boxesCompleted = json.optInt("boxesCompleted", 0),
                    rowsCompleted = json.optInt("rowsCompleted", 0),
                    columnsCompleted = json.optInt("columnsCompleted", 0),
                    playedWithoutNotes = json.optBoolean("playedWithoutNotes", false),
                    perfectGame = json.optBoolean("perfectGame", false),
                    speedBonus = json.optBoolean("speedBonus", false),
                    elapsedTimeMs = json.optLong("elapsedTimeMs", 0L),
                    difficulty = json.optString("difficulty", "medium")
                )
            } catch (e: JSONException) {
                GameScore()
            }
        }
    }
}

fun GameScore.toJsonString(): String {
    val json = JSONObject()
    json.put("finalScore", finalScore)
    json.put("basePoints", basePoints)
    json.put("streakBonus", streakBonus)
    json.put("timeBonus", timeBonus)
    json.put("completionBonuses", completionBonuses)
    json.put("specialBonuses", specialBonuses)
    json.put("penalties", penalties)
    json.put("difficultyMultiplier", difficultyMultiplier.toDouble())
    json.put("currentStreak", currentStreak)
    json.put("maxStreak", maxStreak)
    json.put("streakBroken", streakBroken)
    json.put("correctMoves", correctMoves)
    json.put("wrongMoves", wrongMoves)
    json.put("totalMoves", totalMoves)
    json.put("accuracy", accuracy.toDouble())
    json.put("hintsUsed", hintsUsed)
    json.put("errorChecksUsed", errorChecksUsed)
    json.put("boxesCompleted", boxesCompleted)
    json.put("rowsCompleted", rowsCompleted)
    json.put("columnsCompleted", columnsCompleted)
    json.put("playedWithoutNotes", playedWithoutNotes)
    json.put("perfectGame", perfectGame)
    json.put("speedBonus", speedBonus)
    json.put("elapsedTimeMs", elapsedTimeMs)
    json.put("difficulty", difficulty)
    return json.toString()
}

/**
 * Seri (Streak) eventi
 * Her doğru hamlede kaydedilir
 */
data class StreakEvent(
    val timestamp: Long,                  // Ne zaman
    val streakCount: Int,                 // Kaçıncı seri
    val bonusEarned: Int,                 // Kazanılan bonus puan
    val position: Pair<Int, Int>,         // Hangi hücre (row, col)
    val number: Int                       // Hangi sayı
)

/**
 * Tamamlama eventi
 * Kutu/Sıra/Sütun tamamlandığında kaydedilir
 */
data class CompletionEvent(
    val type: CompletionType,             // Tamamlama tipi
    val index: Int,                       // Hangi index (0-8)
    val timestamp: Long,                  // Ne zaman
    val bonusEarned: Int                  // Kazanılan bonus
)

/**
 * Tamamlama tipleri
 */
enum class CompletionType {
    BOX,        // 3x3 kutu
    ROW,        // Yatay sıra
    COLUMN      // Dikey sütun
}

/**
 * Bonus eventi (UI için)
 * Popup göstermek için kullanılır
 */
data class BonusEvent(
    val message: String,                  // "+250 Box Complete!"
    val points: Int,                      // Kazanılan puan
    val position: Pair<Int, Int>?,        // Gösterim pozisyonu (opsiyonel)
    val timestamp: Long = System.currentTimeMillis(),
    val type: BonusType
)

enum class BonusType {
    STREAK,
    COMPLETION,
    TIME,
    PERFECT,
    SPECIAL
}
