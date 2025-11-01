# 🔧 Extreme Sudoku - Kod Düzeltme Checklist

**Tarih:** 01 Kasım 2025  
**Durum:** Geliştirme Aşamasında  
**Tahmini Süre:** 2-3 gün (16 saat)

---

## 🚨 GÜN 1: KRİTİK SORUNLAR (P0) - 4 saat

### ✅ ADIM 1: Memory Leak - Flow Collections Düzeltme (2 saat)

**Dosyalar:**
- [ ] `HomeViewModel.kt` - `loadData()` fonksiyonu
- [ ] `ProfileViewModel.kt` - `loadUserData()` fonksiyonu
- [ ] `LeaderboardViewModel.kt` - `loadLeaderboard()` fonksiyonu
- [ ] `PvpLobbyViewModel.kt` - `observeMatchmaking()` fonksiyonu
- [ ] `PvpBlindRaceViewModel.kt` - `startListeners()` fonksiyonu
- [ ] `PvpLiveBattleViewModel.kt` - `startListeners()` fonksiyonu

**Yapılacak:**
```kotlin
// ❌ ÖNCE (Hatalı)
viewModelScope.launch {
    repository.getData().collect { data ->
        _uiState.update { it.copy(data = data) }
    }
}

// ✅ SONRA (Doğru)
viewModelScope.launch {
    repository.getData()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        .collect { data ->
            _uiState.update { it.copy(data = data) }
        }
}
```

---

### ✅ ADIM 2: Infinite Loop - PvpLobbyViewModel (1 saat)

**Dosya:**
- [ ] `PvpLobbyViewModel.kt` - `startActiveMatchmaking()` fonksiyonu

**Yapılacak:**
1. Max attempt limiti ekle: `val maxAttempts = 150`
2. Loop condition'a `attemptCount < maxAttempts` ekle
3. Try-catch ekle ve error durumunda state değiştir
4. Exponential backoff ekle (opsiyonel)

```kotlin
// Eklenecek
val maxAttempts = 150
while (_uiState.value is Searching && attemptCount < maxAttempts) {
    // ... mevcut kod
}
```

---

### ✅ ADIM 3: Timer Memory Leak (1 saat)

**Dosyalar:**
- [ ] `PvpBlindRaceViewModel.kt` - `startTimer()` ve `onCleared()`
- [ ] `PvpLiveBattleViewModel.kt` - `startTimer()` ve `onCleared()`
- [ ] `GameViewModel.kt` - `startTimer()` ve `onCleared()`

**Yapılacak:**
1. `while(true)` → `while(isActive && secondsElapsed < maxDuration)` değiştir
2. `onCleared()` fonksiyonunda job'ları cancel et
3. Max duration ekle: `val maxDuration = 600` (10 dakika)

```kotlin
// Eklenecek
private fun startTimer() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
        var secondsElapsed = 0
        val maxDuration = 600
        while (isActive && secondsElapsed < maxDuration) {
            // ... mevcut kod
        }
    }
}

override fun onCleared() {
    super.onCleared()
    timerJob?.cancel()
    progressSyncJob?.cancel()
}
```

---

## 🔥 GÜN 2: YÜKSEK ÖNCELİK (P1) - 8 saat

### ✅ ADIM 4: Error Handling - Try-Catch Ekle (3 saat)

**Dosyalar:**
- [ ] `SudokuRepository.kt` - `getSudoku()`, `getRandomSudoku()`
- [ ] `UserRepository.kt` - `syncStatsFromFirebase()`, `syncGamesFromFirebase()`
- [ ] `PvpMatchRepositoryImpl.kt` - Tüm fonksiyonlar

**Yapılacak:**
Her repository fonksiyonunu try-catch ile sarmala:

```kotlin
suspend fun getSudoku(id: String): Result<Sudoku> {
    return try {
        // mevcut kod
    } catch (e: Exception) {
        android.util.Log.e(TAG, "Error: ${e.message}", e)
        Result.failure(e)
    }
}
```

---

### ✅ ADIM 5: Performance - Composable Optimization (2 saat)

**Dosyalar:**
- [ ] `GameScreen.kt`
- [ ] `PvpBlindRaceScreen.kt`
- [ ] `PvpLiveBattleScreen.kt`
- [ ] `HomeScreen.kt`

**Yapılacak:**
Hesaplamaları `remember` + `derivedStateOf` ile sarmala:

```kotlin
// Eklenecek
val conflicts = remember(uiState.currentGrid, uiState.selectedCell) {
    derivedStateOf { findConflicts(...) }
}.value
```

---

### ✅ ADIM 6: Firebase Query Optimization (2 saat)

**Dosya:**
- [ ] `SudokuRepository.kt` - `getRandomSudoku()` fonksiyonu

**Yapılacak:**
1. `limit = 50` → `limit = 1` değiştir
2. Background cache loading ekle
3. Local puzzle count kontrolü ekle

```kotlin
// Değiştirilecek
val result = firebaseDataSource.getSudokusByDifficulty(diff, limit = 1)  // 50 → 1
```

---

### ✅ ADIM 7: Input Validation (1 saat)

**Yapılacak:**
1. Yeni dosya oluştur: `ValidateSudokuUseCase.kt`
2. Puzzle validation logic ekle
3. Repository'de kullan

```kotlin
// Yeni use case oluştur
class ValidateSudokuUseCase {
    operator fun invoke(sudoku: Sudoku): Result<Unit> {
        if (sudoku.puzzle.length != 81) {
            return Result.failure(Exception("Invalid puzzle length"))
        }
        // ... diğer kontroller
    }
}
```

---

## 📝 GÜN 3: KOD KALİTESİ (P2-P3) - 4 saat

### ✅ ADIM 8: Logger Implementation (1 saat)

**Yapılacak:**
1. [ ] Yeni dosya: `utils/Logger.kt` oluştur
2. [ ] Tüm `android.util.Log` çağrılarını `Logger` ile değiştir
3. [ ] BuildConfig.DEBUG kontrolü ekle

```kotlin
// Yeni file
object Logger {
    private val isDebug = BuildConfig.DEBUG
    fun d(tag: String, message: String) {
        if (isDebug) android.util.Log.d(tag, message)
    }
}
```

---

### ✅ ADIM 9: Constants Düzenleme (1 saat)

**Dosya:**
- [ ] `utils/Constants.kt`

**Yapılacak:**
Magic number'ları constant'a çevir:

```kotlin
// Eklenecek
const val MATCHMAKING_RETRY_DELAY_MS = 2000L
const val PUZZLE_CACHE_SIZE = 20
const val HEARTBEAT_INTERVAL_SEC = 15
```

---

### ✅ ADIM 10: Null Safety (1 saat)

**Dosyalar:**
- [ ] Tüm dosyalarda `!!` operatörünü bul ve değiştir

**Yapılacak:**
`!!` → `?:` veya `?.let { }` değiştir:

```kotlin
// ❌ Önce
val userId = auth.currentUser!!.uid

// ✅ Sonra
val userId = auth.currentUser?.uid ?: return Result.failure(...)
```

---

### ✅ ADIM 11: Analytics Ekleme (1 saat)

**Yapılacak:**
1. [ ] Yeni dosya: `utils/AnalyticsManager.kt` oluştur
2. [ ] Event tracking fonksiyonları ekle
3. [ ] ViewModellerde event log'la

```kotlin
// Yeni file
class AnalyticsManager {
    fun logGameStarted(difficulty: String) { ... }
    fun logGameCompleted(time: Long, score: Int) { ... }
}
```

---

## ✅ TEST & DOĞRULAMA

### ADIM 12: Build & Test

**Yapılacak:**
```bash
cd ExtremeSudoku
.\gradlew.bat clean
.\gradlew.bat assembleDebug
.\gradlew.bat test
```

**Kontroller:**
- [ ] Build başarılı
- [ ] Compilation error yok
- [ ] App açılıyor
- [ ] Oyun oynanabiliyor
- [ ] PvP matchmaking çalışıyor
- [ ] Memory leak yok (Android Studio Profiler)
- [ ] Crash yok

---

### ADIM 13: Code Review

**Kontrol Listesi:**
- [ ] Tüm `collect()` çağrıları `stateIn()` ile sarmalandı mı?
- [ ] Infinite loop'lar düzeltildi mi?
- [ ] Timer'lar `onCleared()` da cancel ediliyor mu?
- [ ] Try-catch blokları eklendi mi?
- [ ] `remember` ve `derivedStateOf` kullanıldı mı?
- [ ] Magic number'lar constant'a çevrildi mi?
- [ ] `!!` operatörü kaldırıldı mı?
- [ ] Log'lar `Logger` ile değiştirildi mi?

---

## 📊 İLERLEME TAKİBİ

| Gün | Adım | Durum | Süre |
|-----|------|-------|------|
| 1 | Memory Leak Fix | ⬜ Bekliyor | 2h |
| 1 | Infinite Loop Fix | ⬜ Bekliyor | 1h |
| 1 | Timer Leak Fix | ⬜ Bekliyor | 1h |
| 2 | Error Handling | ⬜ Bekliyor | 3h |
| 2 | Performance Opt. | ⬜ Bekliyor | 2h |
| 2 | Firebase Query | ⬜ Bekliyor | 2h |
| 2 | Input Validation | ⬜ Bekliyor | 1h |
| 3 | Logger | ⬜ Bekliyor | 1h |
| 3 | Constants | ⬜ Bekliyor | 1h |
| 3 | Null Safety | ⬜ Bekliyor | 1h |
| 3 | Analytics | ⬜ Bekliyor | 1h |
| - | Test & Review | ⬜ Bekliyor | 2h |

**Toplam:** 16 saat

---

## 🎯 HIZLI BAŞLANGIÇ

Hangi adımdan başlayacaksınız? İşte öncelik sırası:

1. **EN KRİTİK:** ADIM 1 (Memory Leak) → App'in temel sağlığı için
2. **ÇOK ÖNEMLİ:** ADIM 2-3 (Loop & Timer) → Battery drain önleme
3. **ÖNEMLİ:** ADIM 4-7 (Error & Performance) → Kullanıcı deneyimi
4. **İYİLEŞTİRME:** ADIM 8-11 (Code Quality) → Maintainability

---

## 📞 YARDIM

Her adımda **detaylı kod örnekleri** için `CODE_AUDIT_REPORT.md` dosyasına bakın.

Bir adımda takılırsanız veya yardım isterseniz, hangi adımda olduğunuzu belirtin!

---

**Son Güncelleme:** 01 Kasım 2025  
**Durum:** Başlamaya hazır ✅
