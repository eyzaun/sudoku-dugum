package com.extremesudoku.di

import android.content.Context
import android.content.SharedPreferences
import com.extremesudoku.data.remote.FirebaseDataSource
import com.extremesudoku.data.remote.PvpFirebaseDataSource
import com.extremesudoku.data.repository.PvpMatchRepositoryImpl
import com.extremesudoku.data.repositories.UserPreferencesRepositoryImpl
import com.extremesudoku.domain.repositories.UserPreferencesRepository
import com.extremesudoku.domain.repository.PvpMatchRepository
import com.extremesudoku.utils.NetworkMonitor
import com.extremesudoku.utils.HapticFeedback
import com.extremesudoku.utils.SoundEffects
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth
    
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore
    
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = Firebase.database
    
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage
    
    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context
    ): UserPreferencesRepository = UserPreferencesRepositoryImpl(context)
    
    @Provides
    @Singleton
    fun providePvpFirebaseDataSource(
        firestore: FirebaseFirestore,
        realtimeDb: FirebaseDatabase,
        auth: FirebaseAuth,
        sudokuDataSource: FirebaseDataSource
    ): PvpFirebaseDataSource = PvpFirebaseDataSource(firestore, realtimeDb, auth, sudokuDataSource)
    
    @Provides
    @Singleton
    fun providePvpMatchRepository(
        pvpDataSource: PvpFirebaseDataSource,
        sudokuDataSource: FirebaseDataSource,
        auth: FirebaseAuth
    ): PvpMatchRepository = PvpMatchRepositoryImpl(pvpDataSource, sudokuDataSource, auth)
    
    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor = NetworkMonitor(context)
    
    @Provides
    @Singleton
    fun provideHapticFeedback(
        @ApplicationContext context: Context,
        preferencesRepository: UserPreferencesRepository
    ): HapticFeedback = HapticFeedback(context, preferencesRepository)
    
    @Provides
    @Singleton
    fun provideSoundEffects(
        @ApplicationContext context: Context,
        preferencesRepository: UserPreferencesRepository
    ): SoundEffects = SoundEffects(context, preferencesRepository)

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences = context.getSharedPreferences(
        "extreme_sudoku_prefs",
        Context.MODE_PRIVATE
    )
}
