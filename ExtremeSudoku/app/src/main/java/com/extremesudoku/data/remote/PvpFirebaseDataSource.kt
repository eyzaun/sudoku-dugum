package com.extremesudoku.data.remote

import com.extremesudoku.data.models.pvp.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Firestore ile PvP işlemleri için data source
 */
@Singleton
class PvpFirebaseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val realtimeDb: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val sudokuDataSource: FirebaseDataSource
) {
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""
    
    private val currentUserName: String
        get() {
            val user = auth.currentUser
            return user?.displayName 
                ?: user?.email?.substringBefore('@') 
                ?: "Player${user?.uid?.take(4) ?: ""}"
        }
    
    private val currentUserPhotoUrl: String?
        get() = auth.currentUser?.photoUrl?.toString()
    
    // ========== Collections ==========
    
    private val matchmakingCollection = firestore.collection("matchmaking_queue")
    private val matchesCollection = firestore.collection("pvp_matches")
    private val statsCollection = firestore.collection("pvp_stats")
    
    // Realtime Database references for presence
    private val presenceRef = realtimeDb.getReference("presence")
    private val matchPresenceRef = realtimeDb.getReference("match_presence")
    
    // ========== Presence Management ==========
    
    /**
     * Match'e katıldığında presence kaydı oluştur
     * Her 5 saniyede heartbeat gönder
     */
    suspend fun startMatchPresence(matchId: String) = withContext(Dispatchers.IO) {
        try {
            val presencePath = matchPresenceRef.child(matchId).child(currentUserId)
            
            // Online durumunu yaz
            presencePath.setValue(
                mapOf(
                    "userId" to currentUserId,
                    "status" to "online",
                    "lastSeen" to ServerValue.TIMESTAMP
                )
            ).await()
            
            // Disconnect olunca otomatik "offline" yap
            presencePath.onDisconnect().setValue(
                mapOf(
                    "userId" to currentUserId,
                    "status" to "offline",
                    "lastSeen" to ServerValue.TIMESTAMP
                )
            )
            
            android.util.Log.d("PvpFirebase", "✅ Presence başlatıldı: $matchId")
        } catch (e: Exception) {
            android.util.Log.e("PvpFirebase", "❌ Presence başlatma hatası", e)
        }
    }
    
    /**
     * Heartbeat güncelle
     */
    suspend fun updateHeartbeat(matchId: String) = withContext(Dispatchers.IO) {
        try {
            matchPresenceRef.child(matchId).child(currentUserId)
                .child("lastSeen")
                .setValue(ServerValue.TIMESTAMP)
                .await()
        } catch (e: Exception) {
            // Heartbeat kritik değil ama log tutalım
            android.util.Log.w("PvpFirebase", "⚠️ Heartbeat güncelleme hatası: ${e.message}")
        }
    }
    
    /**
     * Match'ten çıkarken presence'ı temizle
     */
    suspend fun stopMatchPresence(matchId: String) = withContext(Dispatchers.IO) {
        try {
            matchPresenceRef.child(matchId).child(currentUserId)
                .setValue(
                    mapOf(
                        "userId" to currentUserId,
                        "status" to "offline",
                        "lastSeen" to ServerValue.TIMESTAMP
                    )
                ).await()
            
            android.util.Log.d("PvpFirebase", "✅ Presence durduruldu: $matchId")
        } catch (e: Exception) {
            android.util.Log.e("PvpFirebase", "❌ Presence durdurma hatası", e)
        }
    }
    
    /**
     * Rakibin presence'ını dinle
     */
    fun observeOpponentPresence(matchId: String, opponentId: String): Flow<Boolean> = callbackFlow {
        val presencePath = matchPresenceRef.child(matchId).child(opponentId)
        
        val listener = presencePath.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val status = snapshot.child("status").getValue(String::class.java)
                val isOnline = status == "online"
                
                android.util.Log.d("PvpFirebase", "👥 Opponent presence: $opponentId = ${if (isOnline) "ONLINE" else "OFFLINE"}")
                trySend(isOnline)
            }
            
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                android.util.Log.e("PvpFirebase", "❌ Presence listener error", error.toException())
                close(error.toException())
            }
        })
        
        awaitClose { 
            presencePath.removeEventListener(listener)
        }
    }
    
    // ========== DIAGNOSTIC ==========

    /**
     * Debug: Kuyruk durumunu kontrol et
     * Matchmaking sorunlarında kullan
     */
    suspend fun getDiagnosticInfo(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = matchmakingCollection.get().await()
            val docs = snapshot.documents

            val info = buildString {
                appendLine("=== MATCHMAKING QUEUE DIAGNOSTICS ===")
                appendLine("Total docs in queue: ${docs.size}")
                appendLine("Current user: $currentUserId")
                appendLine()

                docs.forEach { doc ->
                    val userId = doc.getString("userId") ?: "N/A"
                    val status = doc.getString("status") ?: "N/A"
                    val mode = doc.getString("mode") ?: "N/A"
                    val timestamp = doc.getLong("timestamp") ?: 0L
                    val age = (System.currentTimeMillis() - timestamp) / 1000

                    appendLine("- $userId (Status: $status, Mode: $mode, Age: ${age}s)")
                }
            }

            android.util.Log.d("PvpFirebase", info)
            Result.success(info)
        } catch (e: Exception) {
            android.util.Log.e("PvpFirebase", "Diagnostic error: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ========== Matchmaking ==========

    suspend fun joinMatchmaking(mode: PvpMode): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("PvpFirebase", "📝 Matchmaking kuyruğuna katılıyor - User: $currentUserId, Mode: $mode")
            
            val request = mapOf(
                "userId" to currentUserId,
                "playerName" to currentUserName,
                "rating" to 1000, // Default rating
                "mode" to mode.name,
                "timestamp" to System.currentTimeMillis(),
                "status" to "searching",
                "matchId" to null
            )
            
            matchmakingCollection
                .document(currentUserId)
                .set(request)
                .await()
            
            android.util.Log.d("PvpFirebase", "✅ Kuyruğa başarıyla eklendi")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("PvpFirebase", "❌ Kuyruğa katılma hatası", e)
            Result.failure(e)
        }
    }
    
    suspend fun leaveMatchmaking(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            matchmakingCollection
                .document(currentUserId)
                .delete()
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Kuyruktaki diğer oyuncuları arar ve eşleşme yapar
     * Client-side matchmaking
     *
     * NOT: Firestore composite index için:
     * Collection: matchmaking_queue
     * Fields: status (Ascending), mode (Ascending), timestamp (Ascending)
     */
    suspend fun tryMatchmaking(mode: PvpMode): Result<String?> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("PvpFirebase", "🔍 Rakip aranıyor - Mode: $mode, CurrentUser: $currentUserId")

            // ⚡ FIX: Query yapısını Firestore index ile uyumlu hale getir
            // Firestore index gerekli: status + mode + timestamp (composite index)
            val querySnapshot = matchmakingCollection
                .whereEqualTo("status", "searching")  // FIRST - primary filter
                .whereEqualTo("mode", mode.name)      // SECOND - secondary filter
                .orderBy("timestamp", Query.Direction.ASCENDING) // THEN - sorting
                .limit(20) // Limit artırıldı - daha fazla aday arama
                .get()
                .await()
            
            android.util.Log.d("PvpFirebase", "📊 Toplam bulunan oyuncu: ${querySnapshot.documents.size}")
            
            // Client-side filtreleme - kendini çıkar
            val opponents = querySnapshot.documents.filter { 
                it.getString("userId") != currentUserId 
            }
            
            android.util.Log.d("PvpFirebase", "📊 Filtrelenmiş rakip sayısı: ${opponents.size}")
            
            if (opponents.isEmpty()) {
                // Kimse yok, beklemeye devam
                android.util.Log.d("PvpFirebase", "⏳ Kuyrukta başka oyuncu yok")
                return@withContext Result.success(null)
            }
            
            // İlk rakibi al
            val opponentDoc = opponents.first()
            val opponentId = opponentDoc.getString("userId") ?: return@withContext Result.success(null)
            val opponentName = opponentDoc.getString("playerName") ?: "Rakip"
            
            android.util.Log.d("PvpFirebase", "🎯 Potansiyel rakip bulundu! OpponentID: $opponentId, Name: $opponentName")
            
            // ⚡ RACE CONDITION FIX: Rakibin zaten eşleşmiş olup olmadığını kontrol et
            val opponentCheckDoc = matchmakingCollection
                .document(opponentId)
                .get()
                .await()
            
            val opponentStatus = opponentCheckDoc.getString("status")
            if (opponentStatus != "searching") {
                // Rakip artık searching değil (başkası ile eşleşmiş olabilir)
                android.util.Log.w("PvpFirebase", "⚠️ Rakip artık uygun değil (status: $opponentStatus), beklemeye devam")
                return@withContext Result.success(null)
            }
            
            android.util.Log.d("PvpFirebase", "✅ Rakip hala uygun, match oluşturuluyor")
            
            // Puzzle oluştur
            val puzzle = generatePuzzleForMode(mode)
            android.util.Log.d("PvpFirebase", "🧩 Puzzle oluşturuldu")
            
            // Match oluştur
            val matchResult = createMatch(
                player1Id = currentUserId,
                player2Id = opponentId,
                player1Name = currentUserName,
                player2Name = opponentName,
                mode = mode,
                puzzle = puzzle
            )
            
            if (matchResult.isFailure) {
                val error = matchResult.exceptionOrNull()
                android.util.Log.e("PvpFirebase", "❌ Match oluşturma hatası: ${error?.message}", error)
                return@withContext Result.failure(error ?: Exception("Match oluşturulamadı"))
            }
            
            val matchId = matchResult.getOrNull() ?: run {
                android.util.Log.e("PvpFirebase", "❌ Match ID null döndü!")
                return@withContext Result.failure(Exception("Match ID alınamadı"))
            }
            
            android.util.Log.d("PvpFirebase", "🎮 Match oluşturuldu! MatchID: $matchId")
            
            // ⚡ TRANSACTION: Her iki oyuncunun matchmaking kaydını ATOMIK olarak güncelle
            // Eğer birisi zaten matched ise, işlem başarısız olur
            try {
                firestore.runTransaction { transaction ->
                    // Önce mevcut durumları oku
                    val myDoc = transaction.get(matchmakingCollection.document(currentUserId))
                    val opponentDocSnapshot = transaction.get(matchmakingCollection.document(opponentId))
                    
                    // İkisi de hala "searching" durumunda mı kontrol et
                    if (myDoc.getString("status") != "searching") {
                        throw Exception("Ben artık searching değilim")
                    }
                    if (opponentDocSnapshot.getString("status") != "searching") {
                        throw Exception("Rakip artık searching değil")
                    }
                    
                    // Her ikisi de uygunsa, güncelle
                    transaction.update(matchmakingCollection.document(currentUserId), mapOf(
                        "status" to "matched",
                        "matchId" to matchId
                    ))
                    transaction.update(matchmakingCollection.document(opponentId), mapOf(
                        "status" to "matched",
                        "matchId" to matchId
                    ))
                }.await()
                
                android.util.Log.d("PvpFirebase", "✅ Her iki oyuncu da eşleştirildi (TRANSACTION SUCCESS)")
                Result.success(matchId)
            } catch (e: Exception) {
                // Transaction başarısız - başka biri rakibi çaldı
                android.util.Log.w("PvpFirebase", "⚠️ Transaction başarısız (başkası rakibi çaldı): ${e.message}")
                Result.success(null) // Başarısızlık değil, sadece rakip kaçtı
            }
        } catch (e: Exception) {
            // ⚡ ERROR DIAGNOSIS: Detailed logging for debugging
            val errorDetails = when {
                e.message?.contains("index", ignoreCase = true) == true ->
                    "🔴 FIRESTORE INDEX MISSING! Firebase Console'da composite index oluştur:\n" +
                    "Collection: matchmaking_queue\n" +
                    "Fields: status (Asc), mode (Asc), timestamp (Asc)"
                e.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true ->
                    "🔴 FIRESTORE PERMISSION ERROR! Security rules eksik veya yanlış.\n" +
                    "matchmaking_queue koleksiyonuna okuma/yazma izni ver"
                e.message?.contains("UNAVAILABLE", ignoreCase = true) == true ->
                    "🔴 FIRESTORE UNAVAILABLE! Bağlantı hatası veya server problemli"
                else ->
                    "🔴 Unknown matchmaking error: ${e.message}"
            }
            android.util.Log.e("PvpFirebase", errorDetails, e)
            Result.failure(e)
        }
    }
    
    /**
     * Mod için rastgele bir puzzle oluşturur
     * Firebase'den GERÇEK puzzle çeker - RANDOM seçim yapar
     */
    @Suppress("UNUSED_PARAMETER")
    private suspend fun generatePuzzleForMode(mode: PvpMode): PvpPuzzle {
        android.util.Log.d("PvpFirebase", "🧩 Firebase'den EASY puzzle çekiliyor...")
        
        return try {
            // Firebase'den 50 tane easy puzzle çek
            val puzzleResult = sudokuDataSource.getSudokusByDifficulty("easy", limit = 50)
            
            puzzleResult.fold(
                onSuccess = { sudokuList ->
                    if (sudokuList.isEmpty()) {
                        android.util.Log.e("PvpFirebase", "❌ Firebase'de EASY puzzle bulunamadı!")
                        // Fallback: TestData kullan
                        val testPuzzle = com.extremesudoku.data.TestData.ALL_TEST_PUZZLES.first()
                        PvpPuzzle(
                            puzzleString = testPuzzle.puzzle,
                            solution = testPuzzle.solution,
                            difficulty = "easy"
                        )
                    } else {
                        // RANDOM puzzle seç
                        val sudoku = sudokuList.random()
                        android.util.Log.d("PvpFirebase", "✅ EASY puzzle alındı (${sudokuList.size} arasından random)")
                        
                        // Validation
                        if (sudoku.puzzle.length != 81 || sudoku.solution.length != 81) {
                            android.util.Log.e("PvpFirebase", "❌ Invalid puzzle format detected!")
                            throw IllegalStateException("Invalid puzzle format")
                        }
                        
                        PvpPuzzle(
                            puzzleString = sudoku.puzzle,
                            solution = sudoku.solution,
                            difficulty = "easy"
                        )
                    }
                },
                onFailure = { error ->
                    android.util.Log.e("PvpFirebase", "❌ Firebase puzzle çekme hatası: ${error.message}")
                    // Fallback: TestData kullan
                    val testPuzzle = com.extremesudoku.data.TestData.ALL_TEST_PUZZLES.first()
                    PvpPuzzle(
                        puzzleString = testPuzzle.puzzle,
                        solution = testPuzzle.solution,
                        difficulty = "easy"
                    )
                }
            )
        } catch (e: Exception) {
            android.util.Log.e("PvpFirebase", "❌ Puzzle oluşturma hatası: ${e.message}")
            // Fallback: TestData kullan
            val testPuzzle = com.extremesudoku.data.TestData.ALL_TEST_PUZZLES.first()
            PvpPuzzle(
                puzzleString = testPuzzle.puzzle,
                solution = testPuzzle.solution,
                difficulty = "easy"
            )
        }
    }
    
    fun observeMatchmaking(): Flow<MatchmakingRequest?> = callbackFlow {
        val listener = matchmakingCollection
            .document(currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val request = snapshot?.let { doc ->
                    if (doc.exists()) {
                        MatchmakingRequest(
                            userId = doc.getString("userId") ?: "",
                            playerName = doc.getString("playerName") ?: "",
                            rating = doc.getLong("rating")?.toInt() ?: 1000,
                            mode = PvpMode.fromString(doc.getString("mode") ?: "BLIND_RACE"),
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                            status = doc.getString("status") ?: "searching",
                            matchId = doc.getString("matchId")
                        )
                    } else {
                        null
                    }
                }
                
                trySend(request)
            }
        
        awaitClose { listener.remove() }
    }
    
    // ========== Match Management ==========
    
    suspend fun createMatch(
        player1Id: String,
        player2Id: String,
        player1Name: String,
        player2Name: String,
        mode: PvpMode,
        puzzle: PvpPuzzle
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("PvpFirebase", "🔨 Match oluşturuluyor - P1: $player1Id, P2: $player2Id")
            
            val matchData = mapOf(
                "mode" to mode.name,
                "status" to MatchStatus.WAITING.name,
                "createdAt" to System.currentTimeMillis(),
                "startedAt" to null,
                "endedAt" to null,
                "puzzle" to mapOf(
                    "puzzleString" to puzzle.puzzleString,
                    "solution" to puzzle.solution,
                    "difficulty" to puzzle.difficulty
                ),
                "players" to mapOf(
                    player1Id to mapOf(
                        "userId" to player1Id,
                        "displayName" to player1Name,
                        "photoUrl" to null,
                        "status" to PlayerStatus.READY.name,
                        "joinedAt" to System.currentTimeMillis(),
                        "result" to null
                    ),
                    player2Id to mapOf(
                        "userId" to player2Id,
                        "displayName" to player2Name,
                        "photoUrl" to null,
                        "status" to PlayerStatus.READY.name,
                        "joinedAt" to System.currentTimeMillis(),
                        "result" to null
                    )
                ),
                "winnerId" to null
            )
            
            android.util.Log.d("PvpFirebase", "📝 Match data hazırlandı, Firestore'a yazılıyor...")
            val docRef = matchesCollection.add(matchData).await()
            android.util.Log.d("PvpFirebase", "✅ Match Firestore'a yazıldı! DocID: ${docRef.id}")
            
            Result.success(docRef.id)
        } catch (e: Exception) {
            android.util.Log.e("PvpFirebase", "💥 Match oluşturma exception: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getMatch(matchId: String): Result<PvpMatch> = withContext(Dispatchers.IO) {
        try {
            val doc = matchesCollection.document(matchId).get().await()
            
            if (!doc.exists()) {
                return@withContext Result.failure(Exception("Match not found"))
            }
            
            val match = documentToPvpMatch(matchId, doc.data ?: emptyMap())
            Result.success(match)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun observeMatch(matchId: String): Flow<PvpMatch?> = callbackFlow {
        val listener = matchesCollection
            .document(matchId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val match = snapshot?.let { doc ->
                    if (doc.exists()) {
                        documentToPvpMatch(matchId, doc.data ?: emptyMap())
                    } else {
                        null
                    }
                }
                
                trySend(match)
            }
        
        awaitClose { listener.remove() }
    }
    
    suspend fun startMatch(matchId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            matchesCollection
                .document(matchId)
                .update(
                    mapOf(
                        "status" to MatchStatus.IN_PROGRESS.name,
                        "startedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun endMatch(matchId: String, winnerId: String?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            matchesCollection
                .document(matchId)
                .update(
                    mapOf(
                        "status" to MatchStatus.COMPLETED.name,
                        "endedAt" to System.currentTimeMillis(),
                        "winnerId" to winnerId
                    )
                )
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun cancelMatch(matchId: String, forfeitedByCurrentUser: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d(
                "PvpFirebase",
                "🚫 cancelMatch çağrıldı - MatchID: $matchId, User: $currentUserId, forfeitedByCurrentUser=$forfeitedByCurrentUser"
            )
            
            // Match'i önce al - ama sadece status ve players field'larını oku
            val matchDoc = matchesCollection.document(matchId).get().await()
            
            if (matchDoc.exists()) {
                val status = matchDoc.getString("status")
                android.util.Log.d("PvpFirebase", "📋 Match bulundu, Status: $status")
                
                // Eğer match zaten bitmişse, tekrar güncelleme
                if (status == MatchStatus.COMPLETED.name || status == MatchStatus.CANCELLED.name) {
                    android.util.Log.w("PvpFirebase", "⚠️ Match zaten bitmiş ($status), yeniden iptal edilmeyecek")
                    return@withContext Result.success(Unit)
                }
                
                // Players map'inden rakip oyuncuyu bul
                val playersMap = matchDoc.get("players") as? Map<*, *>
                val opponentId = playersMap?.keys?.firstOrNull { it != currentUserId }?.toString()
                val winnerId = if (forfeitedByCurrentUser) opponentId else currentUserId
                
                android.util.Log.d("PvpFirebase", "🎯 Rakip oyuncu: $opponentId")
                android.util.Log.d(
                    "PvpFirebase",
                    "📝 Firestore'a yazılacak: status=CANCELLED, winnerId=$winnerId"
                )
                
                // Match'i iptal et ve kazananı belirle
                val updateData = mapOf(
                    "status" to MatchStatus.CANCELLED.name,
                    "winnerId" to winnerId,
                    "endedAt" to System.currentTimeMillis()
                )
                
                matchesCollection
                    .document(matchId)
                    .update(updateData)
                    .await()
                
                android.util.Log.d(
                    "PvpFirebase",
                    "✅ Match başarıyla iptal edildi! WinnerId: $winnerId yazıldı"
                )
            } else {
                android.util.Log.e("PvpFirebase", "❌ Match bulunamadı!")
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("PvpFirebase", "❌ cancelMatch HATASI", e)
            Result.failure(e)
        }
    }
    
    // ========== Player Actions ==========
    
    suspend fun updatePlayerStatus(
        matchId: String,
        userId: String,
        status: PlayerStatus
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            matchesCollection
                .document(matchId)
                .update("players.$userId.status", status.name)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun submitPlayerResult(
        matchId: String,
        userId: String,
        result: PlayerResult
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resultMap = mapOf(
                "completedAt" to result.completedAt,
                "score" to result.score,
                "timeElapsed" to result.timeElapsed,
                "accuracy" to result.accuracy
            )
            
            matchesCollection
                .document(matchId)
                .update(
                    mapOf(
                        "players.$userId.result" to resultMap,
                        "players.$userId.status" to PlayerStatus.FINISHED.name
                    )
                )
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ========== Moves ==========
    
    suspend fun submitMove(matchId: String, move: PvpMove): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val moveData = mapOf(
                "playerId" to move.playerId,
                "timestamp" to move.timestamp,
                "row" to move.row,
                "col" to move.col,
                "value" to move.value,
                "isCorrect" to move.isCorrect,
                "moveNumber" to move.moveNumber
            )
            
            matchesCollection
                .document(matchId)
                .collection("moves")
                .add(moveData)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun observeMoves(matchId: String): Flow<List<PvpMove>> = callbackFlow {
        val listener = matchesCollection
            .document(matchId)
            .collection("moves")
            .orderBy("moveNumber", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val moves = snapshot?.documents?.mapNotNull { doc ->
                    PvpMove(
                        moveId = doc.id,
                        playerId = doc.getString("playerId") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        row = doc.getLong("row")?.toInt() ?: 0,
                        col = doc.getLong("col")?.toInt() ?: 0,
                        value = doc.getLong("value")?.toInt() ?: 0,
                        isCorrect = doc.getBoolean("isCorrect") ?: false,
                        moveNumber = doc.getLong("moveNumber")?.toInt() ?: 0
                    )
                } ?: emptyList()
                
                trySend(moves)
            }
        
        awaitClose { listener.remove() }
    }
    
    // ========== Stats ==========
    
    suspend fun getUserStats(userId: String): Result<PvpStats> = withContext(Dispatchers.IO) {
        try {
            val doc = statsCollection.document(userId).get().await()
            
            if (!doc.exists()) {
                // Kullanıcının istatistiği yoksa default oluştur
                val defaultStats = PvpStats(userId = userId)
                statsCollection.document(userId).set(pvpStatsToMap(defaultStats)).await()
                return@withContext Result.success(defaultStats)
            }
            
            val stats = documentToPvpStats(userId, doc.data ?: emptyMap())
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUserStats(stats: PvpStats): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            statsCollection
                .document(stats.userId)
                .set(pvpStatsToMap(stats))
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ========== Mappers ==========
    
    @Suppress("UNCHECKED_CAST")
    private fun documentToPvpMatch(matchId: String, data: Map<String, Any>): PvpMatch {
        val playersMap = (data["players"] as? Map<String, Map<String, Any>>) ?: emptyMap()
        
        val players = playersMap.mapValues { (userId, playerData) ->
            PlayerMatchData(
                userId = userId,
                displayName = playerData["displayName"] as? String ?: "",
                photoUrl = playerData["photoUrl"] as? String?,
                status = PlayerStatus.fromString(playerData["status"] as? String ?: "READY"),
                joinedAt = playerData["joinedAt"] as? Long ?: 0L,
                result = (playerData["result"] as? Map<String, Any>)?.let { resultData ->
                    PlayerResult(
                        completedAt = resultData["completedAt"] as? Long ?: 0L,
                        score = (resultData["score"] as? Long)?.toInt() ?: 0,
                        timeElapsed = resultData["timeElapsed"] as? Long ?: 0L,
                        accuracy = (resultData["accuracy"] as? Double)?.toFloat() ?: 0f
                    )
                }
            )
        }
        
        val puzzleData = data["puzzle"] as? Map<String, Any> ?: emptyMap()
        val puzzle = PvpPuzzle(
            puzzleString = puzzleData["puzzleString"] as? String ?: "",
            solution = puzzleData["solution"] as? String ?: "",
            difficulty = puzzleData["difficulty"] as? String ?: "medium"
        )
        
        return PvpMatch(
            matchId = matchId,
            mode = PvpMode.fromString(data["mode"] as? String ?: "BLIND_RACE"),
            status = MatchStatus.fromString(data["status"] as? String ?: "WAITING"),
            createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis(),
            startedAt = data["startedAt"] as? Long,
            endedAt = data["endedAt"] as? Long,
            puzzle = puzzle,
            players = players,
            winnerId = data["winnerId"] as? String
        )
    }
    
    @Suppress("UNCHECKED_CAST")
    private fun documentToPvpStats(userId: String, data: Map<String, Any>): PvpStats {
        fun mapToModeStats(statsData: Map<String, Any>?): ModeStats {
            if (statsData == null) return ModeStats()
            
            return ModeStats(
                gamesPlayed = (statsData["gamesPlayed"] as? Long)?.toInt() ?: 0,
                wins = (statsData["wins"] as? Long)?.toInt() ?: 0,
                losses = (statsData["losses"] as? Long)?.toInt() ?: 0,
                draws = (statsData["draws"] as? Long)?.toInt() ?: 0,
                averageTime = statsData["averageTime"] as? Long ?: 0L,
                averageScore = (statsData["averageScore"] as? Double)?.toFloat() ?: 0f,
                rating = (statsData["rating"] as? Long)?.toInt() ?: 1000
            )
        }
        
        return PvpStats(
            userId = userId,
            blindRaceStats = mapToModeStats(data["blindRaceStats"] as? Map<String, Any>),
            liveBattleStats = mapToModeStats(data["liveBattleStats"] as? Map<String, Any>)
        )
    }
    
    private fun pvpStatsToMap(stats: PvpStats): Map<String, Any> {
        fun modeStatsToMap(modeStats: ModeStats): Map<String, Any> {
            return mapOf(
                "gamesPlayed" to modeStats.gamesPlayed,
                "wins" to modeStats.wins,
                "losses" to modeStats.losses,
                "draws" to modeStats.draws,
                "averageTime" to modeStats.averageTime,
                "averageScore" to modeStats.averageScore,
                "rating" to modeStats.rating
            )
        }
        
        return mapOf(
            "userId" to stats.userId,
            "blindRaceStats" to modeStatsToMap(stats.blindRaceStats),
            "liveBattleStats" to modeStatsToMap(stats.liveBattleStats)
        )
    }
}
