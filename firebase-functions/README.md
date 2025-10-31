# Firebase Cloud Functions - PvP Matchmaking

Bu klasör, Extreme Sudoku uygulamasının PvP matchmaking sistemini yöneten Firebase Cloud Functions kodlarını içerir.

## 📦 Kurulum

```bash
cd firebase-functions
npm install
```

## 🔧 Geliştirme

### Local Test (Emulator)
```bash
npm run serve
```

### Build
```bash
npm run build
```

### Deploy
```bash
npm run deploy
```

## 🎮 Functions

### 1. matchmakingScheduler
- **Trigger:** Scheduled (her 5 saniyede bir)
- **Görev:** Matchmaking queue'sunu kontrol eder ve oyuncuları eşleştirir
- **Algoritma:**
  - `matchmaking_queue` koleksiyonundan waiting durumundaki oyuncuları al
  - Moda göre grupla (BLIND_RACE / LIVE_BATTLE)
  - Rating farkı en düşük 2 oyuncuyu eşleştir
  - `pvp_matches` koleksiyonuna yeni match kaydet
  - Queue kayıtlarını "matched" olarak güncelle

### 2. triggerMatchmaking
- **Trigger:** HTTP (Manuel test için)
- **Endpoint:** `https://your-project.cloudfunctions.net/triggerMatchmaking`
- **Görev:** Matchmaking'i manuel olarak tetikler (test amaçlı)

### 3. cleanupOldQueue
- **Trigger:** Scheduled (her 10 dakikada bir)
- **Görev:** 30 dakikadan eski queue kayıtlarını siler

## 🗄️ Firebase Collections

### matchmaking_queue/{userId}
```typescript
{
  userId: string
  mode: "BLIND_RACE" | "LIVE_BATTLE"
  rating: number
  createdAt: Timestamp
  status: "waiting" | "matched" | "cancelled"
  matchId?: string  // matched durumunda
  matchedAt?: Timestamp
}
```

### pvp_matches/{matchId}
```typescript
{
  matchId: string
  mode: "BLIND_RACE" | "LIVE_BATTLE"
  status: "WAITING" | "IN_PROGRESS" | "COMPLETED"
  player1Id: string
  player2Id: string
  player1Name: string
  player2Name: string
  player1Status: "READY" | "PLAYING" | "FINISHED"
  player2Status: "READY" | "PLAYING" | "FINISHED"
  puzzleId: string
  puzzleDifficulty: string
  createdAt: Timestamp
  startedAt?: Timestamp
  completedAt?: Timestamp
  winnerId?: string
  player1Result?: PlayerResult
  player2Result?: PlayerResult
}
```

## 🔐 Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Matchmaking Queue: Kullanıcı sadece kendi kaydını görebilir/değiştirebilir
    match /matchmaking_queue/{userId} {
      allow read: if request.auth != null && request.auth.uid == userId;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    // PvP Matches: Oyuncular sadece kendi maçlarını görebilir
    match /pvp_matches/{matchId} {
      allow read: if request.auth != null && 
        (resource.data.player1Id == request.auth.uid || 
         resource.data.player2Id == request.auth.uid);
      allow update: if request.auth != null && 
        (resource.data.player1Id == request.auth.uid || 
         resource.data.player2Id == request.auth.uid);
    }
  }
}
```

## 📊 Monitoring

Firebase Console'dan Cloud Functions loglarını izleyebilirsiniz:
```bash
npm run logs
```

## 🚀 Deploy Checklist

- [ ] `npm install` çalıştırıldı
- [ ] `npm run build` başarılı
- [ ] Local emulator'de test edildi
- [ ] Firestore Security Rules güncellendi
- [ ] Firebase billing aktif (Cloud Scheduler için gerekli)
- [ ] `npm run deploy` çalıştırıldı

## 🔗 Firebase Project

**Project ID:** sudoku-lonca  
**Region:** us-central1
