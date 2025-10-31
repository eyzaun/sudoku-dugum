package com.extremesudoku.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.AppShapes
import com.extremesudoku.presentation.theme.LocalThemeColors
import com.extremesudoku.presentation.theme.getColorPalette
import com.extremesudoku.presentation.theme.ThemeType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val themeColors = LocalThemeColors.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = themeColors.text) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = themeColors.iconTint
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeColors.surface
                )
            )
        },
        containerColor = themeColors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(start = AppDimensions.spacingMedium, end = AppDimensions.spacingMedium, top = AppDimensions.spacingMedium, bottom = AppDimensions.spacingExtraLarge),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
        ) {
            // Theme Settings
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                color = themeColors.primary
            )
            
            ThemeSelector(
                currentTheme = uiState.currentTheme,
                onThemeSelected = { viewModel.setTheme(it) }
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = themeColors.divider
            )
            
            // Sound Settings
            Text(
                text = "Sound & Haptics",
                style = MaterialTheme.typography.titleMedium,
                color = themeColors.primary
            )
            
            SettingToggle(
                icon = Icons.Default.MusicNote,
                title = "Sound Effects",
                subtitle = "Play sounds for moves, hints, and completion",
                checked = uiState.soundEnabled,
                onCheckedChange = { viewModel.toggleSound() }
            )

            SettingToggle(
                icon = Icons.Default.VolumeUp,
                title = "Vibration",
                subtitle = "Haptic feedback for interactions",
                checked = uiState.vibrationEnabled,
                onCheckedChange = { viewModel.toggleVibration() }
            )
            
            Divider(
                modifier = Modifier.padding(vertical = AppDimensions.spacingSmall),
                color = themeColors.divider
            )
            
            // Game Settings
            Text(
                text = "Visual Helpers",
                style = MaterialTheme.typography.titleMedium,
                color = themeColors.primary
            )
            
            SettingToggle(
                icon = Icons.Default.Warning,
                title = "Highlight Conflicts",
                subtitle = "Show conflicting numbers in red",
                checked = uiState.highlightConflicts,
                onCheckedChange = { viewModel.toggleHighlightConflicts() }
            )

            SettingToggle(
                icon = Icons.Default.Visibility,
                title = "Highlight Same Numbers",
                subtitle = "Highlight all cells with selected number",
                checked = uiState.highlightSameNumbers,
                onCheckedChange = { viewModel.toggleHighlightSameNumbers() }
            )

            SettingToggle(
                icon = Icons.Default.FormatListNumbered,
                title = "Show Remaining Numbers",
                subtitle = "Display count below each number button",
                checked = uiState.showRemainingNumbers,
                onCheckedChange = { viewModel.toggleShowRemainingNumbers() }
            )

            SettingToggle(
                icon = Icons.Default.Timer,
                title = "Show Timer",
                subtitle = "Display elapsed game time",
                checked = uiState.showTimer,
                onCheckedChange = { viewModel.toggleShowTimer() }
            )

            SettingToggle(
                icon = Icons.Default.GridOn,
                title = "Show Selected Area",
                subtitle = "Highlight row, column and box of selected cell",
                checked = uiState.highlightSelectedArea,
                onCheckedChange = { viewModel.toggleHighlightSelectedArea() }
            )

            SettingToggle(
                icon = Icons.Default.TouchApp,
                title = "Show Affected Areas",
                subtitle = "Show all affected cells when tapping number pad",
                checked = uiState.showAffectedAreas,
                onCheckedChange = { viewModel.toggleShowAffectedAreas() }
            )

            SettingToggle(
                icon = Icons.Default.Edit,
                title = "Auto-Remove Notes",
                subtitle = "Automatically clear notes when placing a number",
                checked = uiState.autoRemoveNotes,
                onCheckedChange = { viewModel.toggleAutoRemoveNotes() }
            )

            SettingToggle(
                icon = Icons.Default.Star,
                title = "Show Score & Streak",
                subtitle = "Display score and streak counter during gameplay",
                checked = uiState.showScoreAndStreak,
                onCheckedChange = { viewModel.toggleShowScoreAndStreak() }
            )

            SettingToggle(
                icon = Icons.Default.Palette,
                title = "Colorize Numbers",
                subtitle = "Show correct numbers in green, wrong in red",
                checked = uiState.colorizeNumbers,
                onCheckedChange = { viewModel.toggleColorizeNumbers() }
            )
            
            Divider(
                modifier = Modifier.padding(vertical = AppDimensions.spacingSmall),
                color = themeColors.divider
            )
            
            // Advanced Settings
            Text(
                text = "Advanced",
                style = MaterialTheme.typography.titleMedium,
                color = themeColors.primary
            )
            
            SettingToggle(
                icon = Icons.Default.Lightbulb,
                title = "Auto-check Mistakes",
                subtitle = "Prevent placing wrong numbers (Coming soon)",
                checked = uiState.autoCheckMistakes,
                onCheckedChange = { viewModel.toggleAutoCheckMistakes() }
            )
            
            Divider(
                modifier = Modifier.padding(vertical = AppDimensions.spacingSmall),
                color = themeColors.divider
            )
            
            // About
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                color = themeColors.primary
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = themeColors.cardBackground
                )
            ) {
                Column(
                    modifier = Modifier.padding(AppDimensions.spacingMedium),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Version",
                            style = MaterialTheme.typography.bodyMedium,
                            color = themeColors.text
                        )
                        Text(
                            "1.0.5",
                            style = MaterialTheme.typography.bodyMedium,
                            color = themeColors.textSecondary
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Puzzles Available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = themeColors.text
                        )
                        Text(
                            "3.8M+",
                            style = MaterialTheme.typography.bodyMedium,
                            color = themeColors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

// SettingToggle is now provided by SettingsComponents.kt to keep UI consistent and DRY

@Composable
private fun ThemeSelector(
    currentTheme: ThemeType,
    onThemeSelected: (ThemeType) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
    ) {
        ThemeOption(
            themeType = ThemeType.LIGHT,
            themeName = "Light Theme",
            themeDescription = "Bright and clean appearance",
            isSelected = currentTheme == ThemeType.LIGHT,
            onSelected = { onThemeSelected(ThemeType.LIGHT) }
        )
        
        ThemeOption(
            themeType = ThemeType.DARK,
            themeName = "Dark Theme",
            themeDescription = "Easy on the eyes in low light",
            isSelected = currentTheme == ThemeType.DARK,
            onSelected = { onThemeSelected(ThemeType.DARK) }
        )
        
        ThemeOption(
            themeType = ThemeType.BLUE_OCEAN,
            themeName = "Blue Ocean",
            themeDescription = "Calming blue tones",
            isSelected = currentTheme == ThemeType.BLUE_OCEAN,
            onSelected = { onThemeSelected(ThemeType.BLUE_OCEAN) }
        )
    }
}

@Composable
private fun ThemeOption(
    themeType: ThemeType,
    themeName: String,
    themeDescription: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) themeColors.primary.copy(alpha = 0.1f) else themeColors.cardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.spacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Theme preview
            Box(
                modifier = Modifier
                    .size(AppDimensions.gameCompleteIconSize)
                    .clip(AppShapes.small)
                    .background(getThemePreviewColor(themeType))
            )
            
            Spacer(modifier = Modifier.width(AppDimensions.spacingMedium))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = themeName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = themeColors.text
                )
                Text(
                    text = themeDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = themeColors.textSecondary
                )
            }
            
            RadioButton(
                selected = isSelected,
                onClick = onSelected
            )
        }
    }
}

private fun getThemePreviewColor(themeType: ThemeType): Color {
    return getColorPalette(themeType).background
}

