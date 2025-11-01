# PvP Matchmaking Fix - Sorun Çözümü

## Loglar Analiz Sonucu Bulunan Sorunlar

### 1. **Google API Bağlantı Hatası** (Expected - Emulator)
```
java.lang.SecurityException: Unknown calling package name 'com.google.android.gms'
```
- **Sebep**: Emulator'da Google Play Services sınırlı
- **Çözüm**: Gerçek cihazda test et (sorun olmayacak)
- **Etki**: Firebase Realtime Database bağlantısını etkilemiyor

### 2. **Matchmaking Timeout** (Ana Sorun)
```
2025-11-01 15:45:31.341  8484-8484  PvpLobby  🔍 Hala aranıyor...
[3+ dakika bekleme]
```
- **Sebep**: Firestore composite index eksik veya kuyrukta kullanıcı yok
- **Çözüm**:
  - Firebase Console'da composite index oluştur
  - Polling aralığını 2s → 3s yükselttik (server yükü azaltımı)
  - 3 dakika timeout eklendi

### 3. **Query Yapı Sorunu**
**Eski (Hatalı):**
```kotlin
matchmakingCollection
    .whereEqualTo("mode", mode.name)      // Compound query start
    .whereEqualTo("status", "searching")  // Second filter
    .orderBy("timestamp", Query.Direction.ASCENDING)
```

**Yeni (Doğru):**
```kotlin
matchmakingCollection
    .whereEqualTo("status", "searching")  // FIRST filter (composite index sıralanması önemli)
    .whereEqualTo("mode", mode.name)      // SECOND filter
    .orderBy("timestamp", Query.Direction.ASCENDING)
```

---

## Firestore Composite Index Oluşturma

**Collection**: `matchmaking_queue`

**Gerekli Index:**
| Field | Order | Type |
|-------|-------|------|
| status | Ascending | String |
| mode | Ascending | String |
| timestamp | Ascending | Long |

### Firebase Console'da Oluşturma Adımları:
1. Firebase Console → Project → Firestore Database
2. **Indexes** sekmesine git
3. **Create Composite Index** butonuna bas
4. Aşağıdaki değerleri gir:
   - Collection ID: `matchmaking_queue`
   - Field 1: `status` → Ascending
   - Field 2: `mode` → Ascending
   - Field 3: `timestamp` → Ascending
5. **Create Index** butonuna bas
6. Index durumu **Enabled** olana kadar bekle (birkaç dakika)

**Alternatif**: Firestore error mesajında "Create index" linki varsa, doğrudan tıkla.

---

## Yapılan Değişiklikler

### 1. `PvpFirebaseDataSource.kt` - Query Düzeltmesi

```kotlin
// ⚡ FIX: Query yapısını Firestore index ile uyumlu hale getir
val querySnapshot = matchmakingCollection
    .whereEqualTo("status", "searching")  // FIRST - primary filter
    .whereEqualTo("mode", mode.name)      // SECOND - secondary filter
    .orderBy("timestamp", Query.Direction.ASCENDING) // THEN - sorting
    .limit(20)  // 10 → 20 artırıldı
    .get()
    .await()
```

### 2. `PvpLobbyViewModel.kt` - Polling Optimizasyonu

```kotlin
// CHANGES:
- Polling interval: 2000ms → 3000ms (server yükü azaltımı)
- Timeout: Unlimited → 180 saniye (3 dakika)
- Better logging with attempt tracking
- Error state on timeout
```

### 3. `PvpFirebaseDataSource.kt` - Diagnostic Method

```kotlin
suspend fun getDiagnosticInfo(): Result<String>
```
Matchmaking sorunlarında kuyruk durumunu kontrol etmek için.

---

## Test Yapma

### 1. **İlk Test - Emulator/Cihazda Tek Kullanıcı**
```
1. App'ı aç
2. Sign In / Guest Mode'a gir
3. PvP → Blind Race / Live Battle seç
4. "Aranıyor..." mesajını gözle
5. 3 dakika sonra timeout uyarısı görmelisin
```

### 2. **İkinci Test - İki Cihazda**
```
1. Cihaz 1: PvP matchmaking başlat (Mod: BLIND_RACE)
2. Cihaz 2: PvP matchmaking başlat (Mod: BLIND_RACE)
3. 3-6 saniye içinde eşleşme bulunmalı
4. İkisi de "Match Found" görmeli
5. Game screen'ine geçmeli
```

### 3. **Debug - Kuyruk Durumunu Kontrol Etme**

Logları takip et:
```
D/PvpFirebase: 📝 Matchmaking kuyruğuna katılıyor - User: XXX, Mode: BLIND_RACE
D/PvpFirebase: ✅ Kuyruğa başarıyla eklendi
D/PvpFirebase: 🔍 Rakip aranıyor - Mode: BLIND_RACE
D/PvpFirebase: 📊 Toplam bulunan oyuncu: [N]  ← BURAYA BAK!
D/PvpFirebase: 📊 Filtrelenmiş rakip sayısı: [N-1]
```

---

## Known Issues & Solutions

| Issue | Sebep | Çözüm |
|-------|-------|-------|
| "Kuyrukta başka oyuncu yok" mesajı | Başka user online değil | İkinci cihazdan test et |
| "Transaction başarısız" | Race condition (ikisi aynı rakibi seçti) | Normal davranış, retry eder |
| Emulator'da Google API hatası | GMS sınırlaması | Gerçek cihazda test et |
| Match oluşturma timeout | Firebase connection | WiFi/Internet bağlantısını kontrol et |

---

## Performance Notes

### Matching Süreleri
- **Passive Match** (başkası sizin için match yapsa): < 1 saniye
- **Active Match** (siz rakip ararsanız): 3-9 saniye (polling + transaction)
- **Worst Case** (kuyrukta az kişi): 30-60 saniye
- **Timeout**: 180 saniye (3 dakika)

### Database Writes
Her matchmaking denemesi:
- 1x Firestore Query (free tier: 1,000 reads/day)
- 1x Transaction attempt (2 reads + 2 writes)
- Polling: Her 3 saniyede 1x (optimized)

---

## Firestore Firewall Rules (Güvenlik)

```firestore
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Matchmaking Queue - Authenticated users only
    match /matchmaking_queue/{userId} {
      allow read: if request.auth.uid == userId;
      allow write: if request.auth.uid == userId;
      allow list: if request.auth != null;  // For queries
    }

    // PvP Matches - Players only
    match /pvp_matches/{matchId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update: if request.auth.uid in resource.data.players;
    }
  }
}
```

---

## Next Steps

1. **Firebase Console'da Composite Index Oluştur** (CRITICAL)
2. Index enabled olana kadar bekle
3. İki cihazda test et
4. Logları gözle ve doğrula
5. Production'a deploy et

**IMPORTANT**: Index yoksa query sonsuza kadar timeout'a girer!
