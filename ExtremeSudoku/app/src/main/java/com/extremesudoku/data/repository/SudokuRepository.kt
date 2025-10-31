package com.extremesudoku.data.repository

import com.extremesudoku.data.TestData
import com.extremesudoku.data.local.dao.GameStateDao
import com.extremesudoku.data.local.dao.SudokuDao
import com.extremesudoku.data.local.entities.toEntity
import com.extremesudoku.data.local.entities.toDomain
import com.extremesudoku.data.models.GameState
import com.extremesudoku.data.models.Sudoku
import com.extremesudoku.data.remote.FirebaseDataSource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SudokuRepository @Inject constructor(
    private val sudokuDao: SudokuDao,
    private val gameStateDao: GameStateDao,
    private val firebaseDataSource: FirebaseDataSource,
    private val auth: FirebaseAuth
) {
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "guest"
    // Sudoku getirme (önce local, sonra remote)
    suspend fun getSudoku(id: String): Result<Sudoku> {
        val localSudoku = sudokuDao.getSudokuById(id)
        if (localSudoku != null) {
            return Result.success(localSudoku.toDomain())
        }
        
        return firebaseDataSource.getSudokuById(id).also { result ->
            result.getOrNull()?.let { sudoku ->
                sudokuDao.insertSudoku(sudoku.toEntity())
            }
        }
    }
    
    suspend fun getRandomSudoku(difficulty: String? = null): Result<Sudoku> {
        android.util.Log.d("SudokuRepository", "🎯 getRandomSudoku çağrıldı - İstenen difficulty: $difficulty")
        
        // Difficulty'yi normalize et (lowercase)
        val normalizedDifficulty = difficulty?.lowercase()
        android.util.Log.d("SudokuRepository", "✅ Normalize edildi: $normalizedDifficulty")
        
        // Önce local database'den oynamadığı sudoku'ları bul
        val localSudoku = if (normalizedDifficulty != null) {
            val puzzles = sudokuDao.getUnplayedSudokuByDifficulty(normalizedDifficulty)
            android.util.Log.d("SudokuRepository", "📊 Local'de oynamadık $normalizedDifficulty puzzle sayısı: ${puzzles.size}")
            puzzles.randomOrNull()
        } else {
            val puzzle = sudokuDao.getRandomUnplayedSudoku()
            android.util.Log.d("SudokuRepository", "📊 Local'de oynamadık rastgele puzzle: ${puzzle != null}")
            puzzle
        }
        
        if (localSudoku != null) {
            android.util.Log.d("SudokuRepository", "✅ Local'den puzzle döndürülüyor - ID: ${localSudoku.id}, Difficulty: ${localSudoku.difficulty}")
            return Result.success(localSudoku.toDomain())
        }
        
        android.util.Log.w("SudokuRepository", "⚠️ Local'de puzzle bulunamadı, Firebase kontrolüne geçiliyor...")
        
        // Local'de oynamadık sudoku kalmadıysa:
        // 1. Database'de hiç sudoku yok mu kontrol et
        val totalCount = sudokuDao.getSudokuCount()
        android.util.Log.d("SudokuRepository", "📊 Database'deki toplam puzzle sayısı: $totalCount")
        
        if (totalCount == 0) {
            android.util.Log.d("SudokuRepository", "🔄 İlk açılış - Firebase'den puzzle yüklenecek")
            // İlk açılış - Firebase'den yükle
            return loadInitialSudokusFromFirebase(normalizedDifficulty)
        }
        
        // 2. Belirli zorlukta oynamadık puzzle kalmadıysa Firebase'den YENİ puzzle'lar çek
        if (normalizedDifficulty != null) {
            android.util.Log.d("SudokuRepository", "🔍 $normalizedDifficulty puzzle'ı tükendi, Firebase'den YENİ puzzle'lar yükleniyor...")
            
            // Firebase'den 50 adet yeni puzzle çek
            val firebaseResult = firebaseDataSource.getSudokusByDifficulty(normalizedDifficulty, limit = 50)
            firebaseResult.getOrNull()?.let { newPuzzles ->
                android.util.Log.d("SudokuRepository", "✅ Firebase'den ${newPuzzles.size} adet YENİ puzzle alındı")
                
                // Yeni puzzle'ları database'e kaydet
                newPuzzles.forEach { sudoku ->
                    sudokuDao.insertSudoku(sudoku.toEntity())
                }
                
                // Yeni puzzle'lardan birini random seç ve döndür
                val selectedPuzzle = newPuzzles.random()
                android.util.Log.d("SudokuRepository", "✅ Seçilen puzzle - ID: ${selectedPuzzle.id}, Difficulty: ${selectedPuzzle.difficulty}")
                return Result.success(selectedPuzzle)
            }
            
            // Firebase'den de yeni puzzle gelemezse, farklı zorluktan dene
            android.util.Log.d("SudokuRepository", "🔍 Firebase'den puzzle gelmedi, alternatif aranıyor...")
            val allUnplayed = sudokuDao.getRandomUnplayedSudoku()
            if (allUnplayed != null) {
                android.util.Log.w("SudokuRepository", "⚠️ FARKLI ZORLUKTA puzzle döndürülüyor! İstenilen: $normalizedDifficulty, Döndürülen: ${allUnplayed.difficulty}")
                return Result.success(allUnplayed.toDomain())
            }
            
            android.util.Log.d("SudokuRepository", "🔍 Oynanan puzzle'lardan aranıyor...")
            // Son çare: Oynanan puzzlelardan döndür
            val anyPuzzle = sudokuDao.getSudokuByDifficulty(normalizedDifficulty).randomOrNull()
            if (anyPuzzle != null) {
                android.util.Log.d("SudokuRepository", "✅ Oynanan puzzle'dan döndürülüyor - ID: ${anyPuzzle.id}, Difficulty: ${anyPuzzle.difficulty}")
                return Result.success(anyPuzzle.toDomain())
            }
            
            android.util.Log.e("SudokuRepository", "❌ $normalizedDifficulty için hiçbir puzzle bulunamadı!")
            return Result.failure(Exception("No puzzles available for difficulty: $normalizedDifficulty"))
        }
        
        // Zorluk belirtilmemişse, Firebase'den random 50 puzzle çek
        android.util.Log.d("SudokuRepository", "🔥 Firebase'den rastgele puzzle'lar isteniyor...")
        val firebaseResult = firebaseDataSource.getSudokuBatch(limit = 50)
        firebaseResult.getOrNull()?.let { newPuzzles ->
            android.util.Log.d("SudokuRepository", "✅ Firebase'den ${newPuzzles.size} adet puzzle alındı")
            
            // Puzzle'ları database'e kaydet
            newPuzzles.forEach { sudoku ->
                sudokuDao.insertSudoku(sudoku.toEntity())
            }
            
            // Random birini seç ve döndür
            val selectedPuzzle = newPuzzles.random()
            android.util.Log.d("SudokuRepository", "✅ Seçilen puzzle - ID: ${selectedPuzzle.id}, Difficulty: ${selectedPuzzle.difficulty}")
            return Result.success(selectedPuzzle)
        }
        
        // Hiçbir şey çalışmazsa hata döndür
        android.util.Log.e("SudokuRepository", "❌ Firebase'den puzzle alınamadı!")
        return Result.failure(Exception("Could not load puzzles from Firebase"))
    }
    
    /**
     * İlk açılışta Firebase'den sudokuları yükle
     * 200K sudoku varsa, her zorluktan random çekip database'e kaydet
     * @param preferredDifficulty İstenilen zorluk varsa önce onu yükle
     */
    private suspend fun loadInitialSudokusFromFirebase(preferredDifficulty: String? = null): Result<Sudoku> {
        android.util.Log.d("SudokuRepository", "🚀 loadInitialSudokusFromFirebase başladı - Preferred: $preferredDifficulty")
        return try {
            val difficulties = listOf("easy", "medium", "hard", "expert")
            
            // Önce istenen difficulty'den yükle (varsa)
            if (preferredDifficulty != null && preferredDifficulty in difficulties) {
                android.util.Log.d("SudokuRepository", "📥 Firebase'den $preferredDifficulty puzzle'lar yükleniyor (limit: 50)...")
                val result = firebaseDataSource.getSudokusByDifficulty(preferredDifficulty, limit = 50)
                result.getOrNull()?.let { puzzles ->
                    android.util.Log.d("SudokuRepository", "✅ Firebase'den ${puzzles.size} adet $preferredDifficulty puzzle alındı")
                    if (puzzles.isNotEmpty()) {
                        puzzles.forEach { sudoku ->
                            android.util.Log.v("SudokuRepository", "💾 Kaydediliyor: ID=${sudoku.id}, Diff=${sudoku.difficulty}, Puzzle=${sudoku.puzzle.take(20)}...")
                            sudokuDao.insertSudoku(sudoku.toEntity())
                        }
                        val randomPuzzle = puzzles.random()
                        android.util.Log.d("SudokuRepository", "✅ Döndürülen puzzle: ID=${randomPuzzle.id}, Difficulty=${randomPuzzle.difficulty}")
                        // İstenen difficulty'den birini döndür
                        return Result.success(randomPuzzle)
                    } else {
                        android.util.Log.w("SudokuRepository", "⚠️ Firebase'den $preferredDifficulty puzzle gelmedi!")
                    }
                }
            }
            
            android.util.Log.d("SudokuRepository", "📥 Diğer zorluklar da yükleniyor...")
            // Diğer zorlukları da background'da yükle (her birinden 50'şer)
            difficulties.filter { it != preferredDifficulty }.forEach { difficulty ->
                try {
                    android.util.Log.d("SudokuRepository", "📥 $difficulty puzzle'lar yükleniyor...")
                    val result = firebaseDataSource.getSudokusByDifficulty(difficulty, limit = 50)
                    result.getOrNull()?.let { puzzles ->
                        android.util.Log.d("SudokuRepository", "✅ ${puzzles.size} adet $difficulty puzzle alındı")
                        puzzles.forEach { sudoku ->
                            sudokuDao.insertSudoku(sudoku.toEntity())
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("SudokuRepository", "❌ $difficulty yüklenemedi: ${e.message}", e)
                    // Tek bir zorluk başarısız olursa devam et
                }
            }
            
            android.util.Log.d("SudokuRepository", "🔥 Rastgele puzzle denenecek...")
            // Eğer hiçbir puzzle yüklenemediyse, random çek
            val randomResult = firebaseDataSource.getRandomSudoku()
            randomResult.getOrNull()?.let { sudoku ->
                android.util.Log.d("SudokuRepository", "✅ Rastgele puzzle alındı - ID: ${sudoku.id}, Difficulty: ${sudoku.difficulty}")
                sudokuDao.insertSudoku(sudoku.toEntity())
                return Result.success(sudoku)
            }
            
            android.util.Log.e("SudokuRepository", "❌ Firebase'den hiçbir puzzle yüklenemedi!")
            // Son çare olarak hata döndür
            Result.failure(Exception("Could not load any puzzles from Firebase"))
        } catch (e: Exception) {
            android.util.Log.e("SudokuRepository", "💥 loadInitialSudokusFromFirebase exception: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Belirli bir zorluktan daha fazla puzzle yükle
     */
    private suspend fun loadMoreSudokusFromFirebase(difficulty: String) {
        try {
            val result = firebaseDataSource.getSudokusByDifficulty(difficulty, limit = 50)
            result.getOrNull()?.forEach { sudoku ->
                sudokuDao.insertSudoku(sudoku.toEntity())
            }
        } catch (e: Exception) {
            // Hata durumunda sessizce devam et - kritik değil
        }
    }
    
    // Game state operasyonları
    fun getActiveGames(): Flow<List<GameState>> {
        return gameStateDao.getActiveGames(currentUserId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getCompletedGames(limit: Int = 10): Flow<List<GameState>> {
        return gameStateDao.getCompletedGames(currentUserId, limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun saveGameState(gameState: GameState) {
        gameStateDao.saveGameState(gameState.toEntity())
        // Background'da Firebase'e sync et
        firebaseDataSource.syncGameState(gameState)
    }
    
    suspend fun getGameState(gameId: String): GameState? {
        return gameStateDao.getGameState(gameId)?.toDomain()
    }
    
    suspend fun deleteGameState(gameId: String) {
        gameStateDao.deleteGameById(gameId)
    }
    
    suspend fun abandonGame(gameId: String) {
        // Oyunu "abandoned" olarak işaretle - bir daha denk gelmesin
        val gameState = gameStateDao.getGameState(gameId)
        gameState?.let {
            val updatedState = it.copy(isAbandoned = true, lastPlayedAt = System.currentTimeMillis())
            gameStateDao.saveGameState(updatedState)
        }
    }
    
    // Sudoku cache'i doldurma - Background'da çalışır
    suspend fun cacheSudokus(count: Int = 200) {
        val currentCount = sudokuDao.getSudokuCount()
        if (currentCount >= count) return
        
        // Her zorluktan eşit sayıda puzzle çek
        val difficulties = listOf("easy", "medium", "hard", "expert")
        val perDifficulty = count / difficulties.size
        
        try {
            difficulties.forEach { difficulty ->
                val existingCount = sudokuDao.getSudokuCountByDifficulty(difficulty)
                if (existingCount < perDifficulty) {
                    val needed = perDifficulty - existingCount
                    val result = firebaseDataSource.getSudokusByDifficulty(difficulty, limit = needed)
                    result.getOrNull()?.forEach { sudoku ->
                        sudokuDao.insertSudoku(sudoku.toEntity())
                    }
                }
            }
        } catch (e: Exception) {
            // Hata durumunda sessizce devam et - kritik değil
        }
    }
    
    suspend fun getSudokuCount(): Int {
        return sudokuDao.getSudokuCount()
    }
}
