package com.extremesudoku.data.remote

import com.extremesudoku.data.models.GameState
import com.extremesudoku.data.models.LeaderboardEntry
import com.extremesudoku.data.models.Sudoku
import com.extremesudoku.data.models.User
import com.extremesudoku.data.models.UserStats
import com.google.firebase.auth.FirebaseAuth
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

@Singleton
class FirebaseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    // Sudoku operasyonları
    suspend fun getSudokuById(id: String): Result<Sudoku> = withContext(Dispatchers.IO) {
        try {
            val doc = firestore.collection("sudokus")
                .document(id)
                .get()
                .await()
            
            val sudoku = doc.toObject(Sudoku::class.java)
            if (sudoku != null) {
                Result.success(sudoku)
            } else {
                Result.failure(Exception("Sudoku not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRandomSudoku(): Result<Sudoku> = withContext(Dispatchers.IO) {
        try {
            val query = firestore.collection("sudokus")
                .limit(100)
                .get()
                .await()
            
            if (query.documents.isNotEmpty()) {
                val randomDoc = query.documents.random()
                val sudoku = randomDoc.toObject(Sudoku::class.java)
                if (sudoku != null) {
                    Result.success(sudoku.copy(id = randomDoc.id))
                } else {
                    Result.failure(Exception("Sudoku parsing failed"))
                }
            } else {
                Result.failure(Exception("No sudokus found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getSudokuBatch(limit: Int = 50): Result<List<Sudoku>> = withContext(Dispatchers.IO) {
        try {
            val query = firestore.collection("sudokus")
                .limit(limit.toLong())
                .get()
                .await()
            
            val sudokus = query.documents.mapNotNull { doc ->
                doc.toObject(Sudoku::class.java)?.copy(id = doc.id)
            }
            Result.success(sudokus)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Belirli bir zorluk seviyesinden sudoku'ları çek
     * Firebase'de difficulty field'ı hem lowercase hem capitalize olabilir
     * Önce lowercase dene, bulamazsa capitalize dene
     */
    suspend fun getSudokusByDifficulty(difficulty: String, limit: Int = 10): Result<List<Sudoku>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("FirebaseDataSource", "🔍 getSudokusByDifficulty çağrıldı - Difficulty: $difficulty, Limit: $limit")
            
            // Önce lowercase ile dene (örn: "easy", "medium")
            android.util.Log.d("FirebaseDataSource", "🔍 Deneme 1: lowercase '$difficulty'")
            var query = firestore.collection("sudokus")
                .whereEqualTo("difficulty", difficulty.lowercase())
                .limit(limit.toLong())
                .get()
                .await()
            
            var sudokus = query.documents.mapNotNull { doc ->
                val sudoku = doc.toObject(Sudoku::class.java)?.copy(id = doc.id)
                if (sudoku != null) {
                    android.util.Log.v("FirebaseDataSource", "✅ Puzzle bulundu: ID=${doc.id}, Diff=${sudoku.difficulty}")
                }
                sudoku
            }
            android.util.Log.d("FirebaseDataSource", "📊 lowercase '$difficulty' sonuç: ${sudokus.size} puzzle")
            
            // Bulunamazsa capitalize ile dene (örn: "Easy", "Medium")
            if (sudokus.isEmpty()) {
                val capitalizedDifficulty = difficulty.lowercase().replaceFirstChar { it.uppercase() }
                android.util.Log.d("FirebaseDataSource", "🔍 Deneme 2: capitalize '$capitalizedDifficulty'")
                query = firestore.collection("sudokus")
                    .whereEqualTo("difficulty", capitalizedDifficulty)
                    .limit(limit.toLong())
                    .get()
                    .await()
                
                sudokus = query.documents.mapNotNull { doc ->
                    val sudoku = doc.toObject(Sudoku::class.java)?.copy(id = doc.id)
                    if (sudoku != null) {
                        android.util.Log.v("FirebaseDataSource", "✅ Puzzle bulundu: ID=${doc.id}, Diff=${sudoku.difficulty}")
                    }
                    sudoku
                }
                android.util.Log.d("FirebaseDataSource", "📊 capitalize '$capitalizedDifficulty' sonuç: ${sudokus.size} puzzle")
            }
            
            // Yine bulunamazsa uppercase ile dene (örn: "EASY", "MEDIUM")
            if (sudokus.isEmpty()) {
                android.util.Log.d("FirebaseDataSource", "🔍 Deneme 3: uppercase '${difficulty.uppercase()}'")
                query = firestore.collection("sudokus")
                    .whereEqualTo("difficulty", difficulty.uppercase())
                    .limit(limit.toLong())
                    .get()
                    .await()
                
                sudokus = query.documents.mapNotNull { doc ->
                    val sudoku = doc.toObject(Sudoku::class.java)?.copy(id = doc.id)
                    if (sudoku != null) {
                        android.util.Log.v("FirebaseDataSource", "✅ Puzzle bulundu: ID=${doc.id}, Diff=${sudoku.difficulty}")
                    }
                    sudoku
                }
                android.util.Log.d("FirebaseDataSource", "📊 uppercase '${difficulty.uppercase()}' sonuç: ${sudokus.size} puzzle")
            }
            
            if (sudokus.isNotEmpty()) {
                android.util.Log.d("FirebaseDataSource", "✅ Toplam ${sudokus.size} adet puzzle bulundu")
                Result.success(sudokus)
            } else {
                val errorMsg = "No sudokus found for difficulty: $difficulty (tried lowercase, capitalized, uppercase)"
                android.util.Log.e("FirebaseDataSource", "❌ $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            android.util.Log.e("FirebaseDataSource", "💥 Firebase exception: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // ═══════════════════════════════════════════════════════════
    // USER PROFILE OPERATIONS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Yeni kullanıcı profili oluştur
     * users/{userId} document'ına user bilgilerini kaydet
     */
    suspend fun createUserProfile(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("users")
                .document(user.userId)
                .set(user)
                .await()
            android.util.Log.d("FirebaseDataSource", "✅ User profile created: ${user.userId}")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseDataSource", "❌ Failed to create user profile", e)
            Result.failure(e)
        }
    }
    
    /**
     * Kullanıcı profilini oku
     */
    suspend fun getUserProfile(userId: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val doc = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            val user = doc.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User profile not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ═══════════════════════════════════════════════════════════
    // USER STATS OPERATIONS
    // ═══════════════════════════════════════════════════════════
    
    // User stats operasyonları
    suspend fun getUserStats(userId: String): Result<UserStats> = withContext(Dispatchers.IO) {
        try {
            val doc = firestore.collection("users")
                .document(userId)
                .collection("stats")
                .document("current")
                .get()
                .await()
            
            val stats = doc.toObject(UserStats::class.java) ?: UserStats()
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUserStats(userId: String, stats: UserStats): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("stats")
                .document("current")
                .set(stats)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Leaderboard operasyonları
    fun getLeaderboard(limit: Int = 100): Flow<List<LeaderboardEntry>> = callbackFlow {
        val listener = firestore.collection("leaderboard")
            .orderBy("bestTime", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val entries = snapshot?.toObjects(LeaderboardEntry::class.java) ?: emptyList()
                trySend(entries)
            }
        
        awaitClose { listener.remove() }
    }
    
    suspend fun updateLeaderboard(entry: LeaderboardEntry): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("leaderboard")
                .document(entry.userId)
                .set(entry)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Game state sync
    suspend fun syncGameState(gameState: GameState): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid 
            ?: return@withContext Result.failure(Exception("User not logged in"))
        
        try {
            firestore.collection("users")
                .document(userId)
                .collection("games")
                .document(gameState.gameId)
                .set(gameState)
                .await()
            android.util.Log.d("FirebaseDataSource", "✅ Game state synced: ${gameState.gameId}")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseDataSource", "❌ Failed to sync game state", e)
            Result.failure(e)
        }
    }
    
    suspend fun getGameState(gameId: String): Result<GameState> = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid 
            ?: return@withContext Result.failure(Exception("User not logged in"))
        
        try {
            val doc = firestore.collection("users")
                .document(userId)
                .collection("games")
                .document(gameId)
                .get()
                .await()
            
            val gameState = doc.toObject(GameState::class.java)
            if (gameState != null) {
                Result.success(gameState)
            } else {
                Result.failure(Exception("Game state not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Kullanıcının tüm game state'lerini Firebase'den çek
     */
    suspend fun getAllUserGames(): Result<List<GameState>> = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid 
            ?: return@withContext Result.failure(Exception("User not logged in"))
        
        try {
            val querySnapshot = firestore.collection("users")
                .document(userId)
                .collection("games")
                .get()
                .await()
            
            val games = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(GameState::class.java)
            }
            android.util.Log.d("FirebaseDataSource", "✅ Fetched ${games.size} games from Firebase")
            Result.success(games)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseDataSource", "❌ Failed to fetch user games", e)
            Result.failure(e)
        }
    }
    
    /**
     * Belirli bir oyunu Firebase'den sil
     */
    suspend fun deleteGameState(gameId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid 
            ?: return@withContext Result.failure(Exception("User not logged in"))
        
        try {
            firestore.collection("users")
                .document(userId)
                .collection("games")
                .document(gameId)
                .delete()
                .await()
            android.util.Log.d("FirebaseDataSource", "✅ Game state deleted: $gameId")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseDataSource", "❌ Failed to delete game state", e)
            Result.failure(e)
        }
    }
}
