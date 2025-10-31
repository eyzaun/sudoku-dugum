package com.extremesudoku

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SudokuApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Firebase initialization
        FirebaseApp.initializeApp(this)
        
        // Crashlytics setup
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }
}
