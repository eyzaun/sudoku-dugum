import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

const db = admin.firestore();

interface MatchmakingRequest {
  userId: string;
  mode: "BLIND_RACE" | "LIVE_BATTLE";
  rating: number;
  createdAt: admin.firestore.Timestamp;
  status: "waiting" | "matched" | "cancelled";
}

/**
 * Cloud Function: PvP Matchmaking
 * Her 5 saniyede bir çalışır ve eşleşme bekleyen oyuncuları birleştirir
 * 
 * Algoritma:
 * 1. matchmaking_queue koleksiyonundan "waiting" durumundaki kayıtları al
 * 2. Moda göre grupla (BLIND_RACE, LIVE_BATTLE)
 * 3. Rating farkı en düşük 2 oyuncuyu eşleştir
 * 4. PvpMatch oluştur ve Firebase'e kaydet
 * 5. Queue kayıtlarını "matched" olarak güncelle
 */
export const matchmakingScheduler = functions.pubsub
  .schedule("every 5 seconds")
  .onRun(async (context) => {
    console.log("Matchmaking scheduler started...");

    try {
      // 1. Waiting durumundaki tüm kayıtları al
      const queueSnapshot = await db
        .collection("matchmaking_queue")
        .where("status", "==", "waiting")
        .get();

      if (queueSnapshot.empty) {
        console.log("No players in queue");
        return null;
      }

      // 2. Moda göre grupla
      const blindRacePlayers: MatchmakingRequest[] = [];
      const liveBattlePlayers: MatchmakingRequest[] = [];

      queueSnapshot.forEach((doc) => {
        const data = doc.data() as MatchmakingRequest;
        if (data.mode === "BLIND_RACE") {
          blindRacePlayers.push({...data, userId: doc.id});
        } else if (data.mode === "LIVE_BATTLE") {
          liveBattlePlayers.push({...data, userId: doc.id});
        }
      });

      console.log(
        `Players in queue - Blind Race: ${blindRacePlayers.length}, ` +
        `Live Battle: ${liveBattlePlayers.length}`
      );

      // 3. Her mod için eşleşmeleri yap
      await matchPlayers(blindRacePlayers, "BLIND_RACE");
      await matchPlayers(liveBattlePlayers, "LIVE_BATTLE");

      return null;
    } catch (error) {
      console.error("Matchmaking error:", error);
      return null;
    }
  });

/**
 * Oyuncuları eşleştirir
 */
async function matchPlayers(
  players: MatchmakingRequest[],
  mode: "BLIND_RACE" | "LIVE_BATTLE"
): Promise<void> {
  if (players.length < 2) {
    console.log(`Not enough players for ${mode}`);
    return;
  }

  // Rating'e göre sırala
  players.sort((a, b) => a.rating - b.rating);

  // İkişerli eşleştir
  for (let i = 0; i < players.length - 1; i += 2) {
    const player1 = players[i];
    const player2 = players[i + 1];

    try {
      // Puzzle seç (100,000+ puzzle var)
      const puzzleId = await selectRandomPuzzle();

      // Match ID oluştur
      const matchId = `match_${Date.now()}_${Math.random()
        .toString(36)
        .substring(7)}`;

      // PvpMatch oluştur
      const matchData = {
        matchId: matchId,
        mode: mode,
        status: "WAITING",
        player1Id: player1.userId,
        player2Id: player2.userId,
        player1Name: "Player 1", // TODO: Gerçek isim çek
        player2Name: "Player 2", // TODO: Gerçek isim çek
        player1Status: "READY",
        player2Status: "READY",
        puzzleId: puzzleId,
        puzzleDifficulty: "hard",
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        startedAt: null,
        completedAt: null,
        winnerId: null,
        player1Result: null,
        player2Result: null,
      };

      // Batch write: Match oluştur + Queue'ları güncelle
      const batch = db.batch();

      // Match kaydet
      const matchRef = db.collection("pvp_matches").doc(matchId);
      batch.set(matchRef, matchData);

      // Queue kayıtlarını güncelle
      const queue1Ref = db.collection("matchmaking_queue").doc(player1.userId);
      batch.update(queue1Ref, {
        status: "matched",
        matchId: matchId,
        matchedAt: admin.firestore.FieldValue.serverTimestamp(),
      });

      const queue2Ref = db.collection("matchmaking_queue").doc(player2.userId);
      batch.update(queue2Ref, {
        status: "matched",
        matchId: matchId,
        matchedAt: admin.firestore.FieldValue.serverTimestamp(),
      });

      await batch.commit();

      console.log(
        `Match created: ${matchId} - ${player1.userId} vs ${player2.userId}`
      );
    } catch (error) {
      console.error(
        `Failed to create match for ${player1.userId} vs ${player2.userId}`,
        error
      );
    }
  }
}

/**
 * Rastgele bir puzzle seçer
 */
async function selectRandomPuzzle(): Promise<string> {
  // 100,000 puzzle var, rastgele bir ID oluştur
  // Format: puzzle_000001 to puzzle_100000
  const randomNum = Math.floor(Math.random() * 100000) + 1;
  const puzzleId = `puzzle_${randomNum.toString().padStart(6, "0")}`;

  // TODO: Puzzle'ın gerçekten var olduğunu kontrol et
  // Şimdilik hard difficulty varsayıyoruz
  return puzzleId;
}

/**
 * HTTP Trigger: Manuel matchmaking test için
 */
export const triggerMatchmaking = functions.https.onRequest(
  async (req, res) => {
    console.log("Manual matchmaking triggered");

    try {
      // matchmakingScheduler'ı manuel çağır
      await matchmakingScheduler(null as any);
      res.status(200).send({success: true, message: "Matchmaking completed"});
    } catch (error) {
      console.error("Manual matchmaking error:", error);
      res.status(500).send({success: false, error: String(error)});
    }
  }
);

/**
 * Clean up: Eski queue kayıtlarını sil (30 dakikadan eski)
 */
export const cleanupOldQueue = functions.pubsub
  .schedule("every 10 minutes")
  .onRun(async (context) => {
    console.log("Cleaning up old queue entries...");

    const thirtyMinutesAgo = admin.firestore.Timestamp.fromDate(
      new Date(Date.now() - 30 * 60 * 1000)
    );

    const oldEntriesSnapshot = await db
      .collection("matchmaking_queue")
      .where("createdAt", "<", thirtyMinutesAgo)
      .get();

    if (oldEntriesSnapshot.empty) {
      console.log("No old queue entries to clean");
      return null;
    }

    const batch = db.batch();
    oldEntriesSnapshot.forEach((doc) => {
      batch.delete(doc.ref);
    });

    await batch.commit();
    console.log(`Deleted ${oldEntriesSnapshot.size} old queue entries`);

    return null;
  });
