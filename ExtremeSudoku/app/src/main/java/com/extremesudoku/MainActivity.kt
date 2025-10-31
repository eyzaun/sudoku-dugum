package com.extremesudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.extremesudoku.data.preferences.ThemeManager
import com.extremesudoku.presentation.navigation.SudokuNavigation
import com.extremesudoku.presentation.theme.ExtremeSudokuTheme
import com.extremesudoku.presentation.theme.LocalThemeColors
import com.extremesudoku.presentation.theme.getColorPalette
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var themeManager: ThemeManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        setContent {
            val currentThemeType by themeManager.themeType.collectAsState(initial = com.extremesudoku.presentation.theme.ThemeType.LIGHT)
            val themeColors = getColorPalette(currentThemeType)
            
            CompositionLocalProvider(LocalThemeColors provides themeColors) {
                ExtremeSudokuTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = themeColors.background
                    ) {
                        SudokuNavigation()
                    }
                }
            }
        }
    }
}
