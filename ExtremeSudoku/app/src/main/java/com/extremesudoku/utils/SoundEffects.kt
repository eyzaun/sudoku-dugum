package com.extremesudoku.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.extremesudoku.R
import com.extremesudoku.domain.repositories.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundEffects @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: UserPreferencesRepository
) {
    private var soundPool: SoundPool? = null
    private var clickSound: Int = 0
    private var errorSound: Int = 0
    private var successSound: Int = 0
    private var hintSound: Int = 0
    
    init {
        initSoundPool()
    }
    
    private fun isEnabled(): Boolean = runBlocking {
        preferencesRepository.soundEnabled.first()
    }
    
    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()
        
        // Note: Ses dosyaları res/raw/ klasörüne eklenebilir
        // Şimdilik sadece sistem sesleri kullanıyoruz
    }
    
    fun playClick() {
        if (!isEnabled()) return
        // Sistem click sesi için
        // soundPool?.play(clickSound, 0.3f, 0.3f, 1, 0, 1f)
    }
    
    fun playError() {
        if (!isEnabled()) return
        // soundPool?.play(errorSound, 0.5f, 0.5f, 1, 0, 1f)
    }
    
    fun playSuccess() {
        if (!isEnabled()) return
        // soundPool?.play(successSound, 0.7f, 0.7f, 1, 0, 1f)
    }
    
    fun playHint() {
        if (!isEnabled()) return
        // soundPool?.play(hintSound, 0.4f, 0.4f, 1, 0, 1f)
    }
    
    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
