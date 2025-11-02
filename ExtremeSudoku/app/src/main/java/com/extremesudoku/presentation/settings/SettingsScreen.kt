package com.extremesudoku.presentation.settings

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.extremesudoku.R
import com.extremesudoku.util.LocaleManager
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
    val context = LocalContext.current
    val activity = context as? Activity
    
    var currentLanguage by remember { 
        mutableStateOf(LocaleManager.getSavedLanguage(context))
    }
    var showLanguageDialog by remember { mutableStateOf(false) }
    
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { language ->
                currentLanguage = language
                LocaleManager.setLocale(context, language)
                showLanguageDialog = false
                // Restart activity to apply language change
                activity?.recreate()
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), color = themeColors.text) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
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
            // Language Settings
            Text(
                text = stringResource(R.string.language),
                style = MaterialTheme.typography.titleMedium,
                color = themeColors.primary
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLanguageDialog = true },
                colors = CardDefaults.cardColors(
                    containerColor = themeColors.cardBackground
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimensions.spacingMedium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = stringResource(R.string.language),
                            tint = themeColors.iconTint
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.language),
                                style = MaterialTheme.typography.bodyLarge,
                                color = themeColors.text
                            )
                            Text(
                                text = currentLanguage.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = themeColors.textSecondary
                            )
                        }
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = themeColors.iconTint
                    )
                }
            }
            
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = themeColors.divider
            )
            
            // Theme Settings
            Text(
                text = stringResource(R.string.settings_section_appearance),
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
                text = stringResource(R.string.settings_section_sound),
                style = MaterialTheme.typography.titleMedium,
                color = themeColors.primary
            )
            
            SettingToggle(
                icon = Icons.Default.MusicNote,
                title = stringResource(R.string.game_settings_sound_effects_title),
                subtitle = stringResource(R.string.game_settings_sound_effects_description),
                checked = uiState.soundEnabled,
                onCheckedChange = { viewModel.toggleSound() }
            )

            SettingToggle(
                icon = Icons.Default.VolumeUp,
                title = stringResource(R.string.game_settings_vibration_title),
                subtitle = stringResource(R.string.game_settings_vibration_description),
                checked = uiState.vibrationEnabled,
                onCheckedChange = { viewModel.toggleVibration() }
            )
            
            Divider(
                modifier = Modifier.padding(vertical = AppDimensions.spacingSmall),
                color = themeColors.divider
            )
            
            // Game Settings
            Text(
                text = stringResource(R.string.settings_section_visual_helpers),
                style = MaterialTheme.typography.titleMedium,
                color = themeColors.primary
            )
            
            SettingToggle(
                icon = Icons.Default.Warning,
                title = stringResource(R.string.game_settings_highlight_conflicts_title),
                subtitle = stringResource(R.string.game_settings_highlight_conflicts_description),
                checked = uiState.highlightConflicts,
                onCheckedChange = { viewModel.toggleHighlightConflicts() }
            )

            SettingToggle(
                icon = Icons.Default.Visibility,
                title = stringResource(R.string.game_settings_highlight_same_numbers_title),
                subtitle = stringResource(R.string.game_settings_highlight_same_numbers_description),
                checked = uiState.highlightSameNumbers,
                onCheckedChange = { viewModel.toggleHighlightSameNumbers() }
            )

            SettingToggle(
                icon = Icons.Default.FormatListNumbered,
                title = stringResource(R.string.game_settings_show_remaining_numbers_title),
                subtitle = stringResource(R.string.game_settings_show_remaining_numbers_description),
                checked = uiState.showRemainingNumbers,
                onCheckedChange = { viewModel.toggleShowRemainingNumbers() }
            )

            SettingToggle(
                icon = Icons.Default.Timer,
                title = stringResource(R.string.game_settings_show_timer_title),
                subtitle = stringResource(R.string.game_settings_show_timer_description),
                checked = uiState.showTimer,
                onCheckedChange = { viewModel.toggleShowTimer() }
            )

            SettingToggle(
                icon = Icons.Default.GridOn,
                title = stringResource(R.string.game_settings_show_selected_area_title),
                subtitle = stringResource(R.string.game_settings_show_selected_area_description),
                checked = uiState.highlightSelectedArea,
                onCheckedChange = { viewModel.toggleHighlightSelectedArea() }
            )

            SettingToggle(
                icon = Icons.Default.TouchApp,
                title = stringResource(R.string.game_settings_show_affected_areas_title),
                subtitle = stringResource(R.string.game_settings_show_affected_areas_description),
                checked = uiState.showAffectedAreas,
                onCheckedChange = { viewModel.toggleShowAffectedAreas() }
            )

            SettingToggle(
                icon = Icons.Default.Edit,
                title = stringResource(R.string.game_settings_auto_remove_notes_title),
                subtitle = stringResource(R.string.game_settings_auto_remove_notes_description),
                checked = uiState.autoRemoveNotes,
                onCheckedChange = { viewModel.toggleAutoRemoveNotes() }
            )

            SettingToggle(
                icon = Icons.Default.Star,
                title = stringResource(R.string.game_settings_show_score_and_streak_title),
                subtitle = stringResource(R.string.game_settings_show_score_and_streak_description),
                checked = uiState.showScoreAndStreak,
                onCheckedChange = { viewModel.toggleShowScoreAndStreak() }
            )

            SettingToggle(
                icon = Icons.Default.Palette,
                title = stringResource(R.string.game_settings_colorize_numbers_title),
                subtitle = stringResource(R.string.game_settings_colorize_numbers_description),
                checked = uiState.colorizeNumbers,
                onCheckedChange = { viewModel.toggleColorizeNumbers() }
            )
            
            Divider(
                modifier = Modifier.padding(vertical = AppDimensions.spacingSmall),
                color = themeColors.divider
            )
            
            // Advanced Settings
            Text(
                text = stringResource(R.string.settings_section_advanced),
                style = MaterialTheme.typography.titleMedium,
                color = themeColors.primary
            )
            
            SettingToggle(
                icon = Icons.Default.Lightbulb,
                title = stringResource(R.string.game_settings_auto_check_mistakes_title),
                subtitle = stringResource(R.string.game_settings_auto_check_mistakes_description),
                checked = uiState.autoCheckMistakes,
                onCheckedChange = { viewModel.toggleAutoCheckMistakes() }
            )
            
            Divider(
                modifier = Modifier.padding(vertical = AppDimensions.spacingSmall),
                color = themeColors.divider
            )
            
            // About
            Text(
                text = stringResource(R.string.settings_section_about),
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
                            stringResource(R.string.settings_version_label),
                            style = MaterialTheme.typography.bodyMedium,
                            color = themeColors.text
                        )
                        Text(
                            stringResource(R.string.settings_version_value),
                            style = MaterialTheme.typography.bodyMedium,
                            color = themeColors.textSecondary
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(R.string.settings_puzzles_available_label),
                            style = MaterialTheme.typography.bodyMedium,
                            color = themeColors.text
                        )
                        Text(
                            stringResource(R.string.settings_puzzles_available_value),
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
            themeType = ThemeType.GAZETE,
            themeName = stringResource(R.string.settings_theme_gazete_name),
            themeDescription = stringResource(R.string.settings_theme_gazete_description),
            isSelected = currentTheme == ThemeType.GAZETE,
            onSelected = { onThemeSelected(ThemeType.GAZETE) }
        )

        ThemeOption(
            themeType = ThemeType.LIGHT,
            themeName = stringResource(R.string.settings_theme_light_name),
            themeDescription = stringResource(R.string.settings_theme_light_description),
            isSelected = currentTheme == ThemeType.LIGHT,
            onSelected = { onThemeSelected(ThemeType.LIGHT) }
        )

        ThemeOption(
            themeType = ThemeType.DARK,
            themeName = stringResource(R.string.settings_theme_dark_name),
            themeDescription = stringResource(R.string.settings_theme_dark_description),
            isSelected = currentTheme == ThemeType.DARK,
            onSelected = { onThemeSelected(ThemeType.DARK) }
        )

        ThemeOption(
            themeType = ThemeType.MONOCHROME,
            themeName = stringResource(R.string.settings_theme_monochrome_name),
            themeDescription = stringResource(R.string.settings_theme_monochrome_description),
            isSelected = currentTheme == ThemeType.MONOCHROME,
            onSelected = { onThemeSelected(ThemeType.MONOCHROME) }
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

@Composable
private fun LanguageSelectionDialog(
    currentLanguage: LocaleManager.Language,
    onLanguageSelected: (LocaleManager.Language) -> Unit,
    onDismiss: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.language_selection_title),
                color = themeColors.text
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
            ) {
                LocaleManager.Language.values().forEach { language ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(language) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentLanguage == language) 
                                themeColors.primary.copy(alpha = 0.1f) 
                            else 
                                themeColors.cardBackground
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppDimensions.spacingMedium),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = language.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = themeColors.text
                            )
                            
                            if (currentLanguage == language) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = themeColors.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = themeColors.primary
                )
            }
        },
        containerColor = themeColors.surface
    )
}

