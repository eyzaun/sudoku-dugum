package com.extremesudoku.data.repository

import com.extremesudoku.data.local.dao.GameStateDao
import com.extremesudoku.data.local.dao.UserStatsDao
import com.extremesudoku.data.local.entities.toEntity
import com.extremesudoku.data.local.entities.toDomain
import com.extremesudoku.data.models.User
import com.extremesudoku.data.models.UserStats
import com.extremesudoku.data.remote.FirebaseDataSource
import com.google.firebase.auth.FirebaseAuth
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userStatsDao: UserStatsDao,
    private val gameStateDao: GameStateDao,
    private val firebaseDataSource: FirebaseDataSource,
    private val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val GUEST_USER_ID = "guest_user"
        private const val PREFS_IS_GUEST = "is_guest_mode"
        private const val PREFS_ONBOARDING_COMPLETED = "onboarding_completed"
    }

    // Guest mode flag - SharedPreferences'tan okuyacağız
    private var isGuestMode = false
    
    fun isGuestMode(): Boolean = isGuestMode || (auth.currentUser?.isAnonymous == true)
    
    fun getCurrentUserId(): String? {
        // Anonymous auth bile olsa gerçek Firebase UID döner
        return auth.currentUser?.uid
    }
    
    fun getCurrentUserEmail(): String? {
        return if (auth.currentUser?.isAnonymous == true) {
            "Guest Player"
        } else {
            auth.currentUser?.email
        }
    }
    
    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            userId = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName ?: firebaseUser.email?.substringBefore('@') ?: "Player",
            photoUrl = firebaseUser.photoUrl?.toString()
        )
    }
    
    fun isUserLoggedIn(): Boolean = auth.currentUser != null
    
    fun getUserStats(): Flow<UserStats> {
        val userId = getCurrentUserId() ?: return flowOf(UserStats())
        return userStatsDao.getUserStats(userId).map { entity ->
            entity?.toDomain() ?: UserStats()
        }
    }
    
    suspend fun getUserStatsOnce(): UserStats {
        val userId = getCurrentUserId() ?: return UserStats()
        return userStatsDao.getUserStatsOnce(userId)?.toDomain() ?: UserStats()
    }
    
    suspend fun updateStats(stats: UserStats) {
        val userId = getCurrentUserId() ?: return
        userStatsDao.updateUserStats(stats.toEntity(userId))
        firebaseDataSource.updateUserStats(userId, stats)
    }
    
    suspend fun syncStatsFromFirebase() {
        val userId = getCurrentUserId() ?: return
        val result = firebaseDataSource.getUserStats(userId)
        result.getOrNull()?.let { stats ->
            userStatsDao.updateUserStats(stats.toEntity(userId))
            android.util.Log.d("UserRepository", "✅ Stats synced from Firebase")
        }
    }
    
    /**
     * Firebase'den tüm oyunları çekip local DB'ye kaydet
     * Kullanıcı giriş yaptığında veya yeni cihazda oturum açtığında çağrılır
     */
    suspend fun syncGamesFromFirebase() {
        val userId = getCurrentUserId() ?: return
        
        try {
            val result = firebaseDataSource.getAllUserGames()
            result.getOrNull()?.let { games ->
                android.util.Log.d("UserRepository", "📥 Syncing ${games.size} games from Firebase...")
                
                games.forEach { gameState ->
                    // Her oyunu local DB'ye kaydet
                    gameStateDao.saveGameState(gameState.toEntity(userId))
                }
                
                android.util.Log.d("UserRepository", "✅ ${games.size} games synced successfully")
            } ?: run {
                android.util.Log.e("UserRepository", "❌ Failed to fetch games from Firebase")
            }
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "❌ Error syncing games", e)
        }
    }
    
    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            
            // Guest mode'dan çıkıyoruz
            isGuestMode = false
            
            // Her iki veriyi de sync et
            syncStatsFromFirebase()
            syncGamesFromFirebase()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Continue as guest - creates anonymous Firebase user
     * This gives the guest a real Firebase UID that works with Firestore
     */
    suspend fun continueAsGuest(): Result<Unit> {
        return try {
            // Firebase Anonymous Authentication kullan - her cihaz için unique ID
            auth.signInAnonymously().await()
            
            isGuestMode = true
            
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Failed to get user ID"))
            
            // Local DB'de guest user için initial stats oluştur
            val initialStats = UserStats()
            userStatsDao.updateUserStats(initialStats.toEntity(userId))
            
            android.util.Log.d("UserRepository", "✅ Guest mode activated with Firebase Anonymous Auth - UID: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "❌ Guest mode error", e)
            Result.failure(e)
        }
    }
    
    suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            val wasAnonymous = currentUser?.isAnonymous == true
            
            if (wasAnonymous && currentUser != null) {
                // Anonymous user'ı email/password ile upgrade et
                android.util.Log.d("UserRepository", "🔄 Converting anonymous user to email/password account")
                
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, password)
                currentUser.linkWithCredential(credential).await()
                
                // Guest mode'dan çıkıyoruz
                isGuestMode = false
                
                // User Profile Firebase'e kaydet (UID aynı kalır)
                val userProfile = User(
                    userId = currentUser.uid,
                    email = email,
                    displayName = email.substringBefore('@'),
                    photoUrl = null,
                    createdAt = System.currentTimeMillis()
                )
                firebaseDataSource.createUserProfile(userProfile)
                
                android.util.Log.d("UserRepository", "✅ Anonymous user upgraded to full account - UID remains: ${currentUser.uid}")
            } else {
                // Yeni kullanıcı oluştur
                auth.createUserWithEmailAndPassword(email, password).await()
                
                isGuestMode = false
                
                val userId = getCurrentUserId()
                if (userId != null) {
                    // User Profile Firebase'e kaydet
                    val userProfile = User(
                        userId = userId,
                        email = email,
                        displayName = email.substringBefore('@'),
                        photoUrl = null,
                        createdAt = System.currentTimeMillis()
                    )
                    firebaseDataSource.createUserProfile(userProfile)
                    
                    // İlk UserStats'ı oluştur
                    val initialStats = UserStats()
                    userStatsDao.updateUserStats(initialStats.toEntity(userId))
                    firebaseDataSource.updateUserStats(userId, initialStats)
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "❌ Sign up error", e)
            Result.failure(e)
        }
    }
    
    fun signOut() {
        isGuestMode = false
        auth.signOut()
    }

    // ============ Onboarding Methods ============
    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(PREFS_ONBOARDING_COMPLETED, false)
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit().putBoolean(PREFS_ONBOARDING_COMPLETED, completed).apply()
    }
}
