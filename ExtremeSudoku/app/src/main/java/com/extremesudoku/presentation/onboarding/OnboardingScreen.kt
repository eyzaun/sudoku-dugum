package com.extremesudoku.presentation.onboarding

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.extremesudoku.R
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.LocalThemeColors
import com.extremesudoku.util.LocaleManager

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onOnboardingComplete: () -> Unit
) {
    val currentPage by viewModel.currentPage.collectAsState()
    val onboardingComplete by viewModel.onboardingComplete.collectAsState()
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
                activity?.recreate()
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    LaunchedEffect(onboardingComplete) {
        if (onboardingComplete) {
            onOnboardingComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColors.background)
    ) {
        // Sol üst - Dil değiştirme butonu
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(AppDimensions.spacingMedium)
                .zIndex(10f)
                .clickable { showLanguageDialog = true }
                .background(
                    color = themeColors.surface.copy(alpha = 0.9f),
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = stringResource(R.string.language),
                tint = themeColors.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = currentLanguage.code.uppercase(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = themeColors.text
            )
        }
        
        // Sağ üst - Atlama butonu
        IconButton(
            onClick = { viewModel.completeOnboarding() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(AppDimensions.spacingMedium)
                .zIndex(10f)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.skip),
                tint = themeColors.textSecondary,
                modifier = Modifier.size(24.dp)
            )
        }

        // Sayfalar - pointerInput ile
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp, bottom = 120.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        when {
                            dragAmount > 100 -> viewModel.previousPage()
                            dragAmount < -100 -> viewModel.nextPage()
                        }
                    }
                }
        ) { page ->
            when (page) {
                0 -> OnboardingPage1()
                1 -> OnboardingPage2()
                2 -> OnboardingPage3()
                3 -> OnboardingPage4()
                4 -> OnboardingPage5()
                else -> OnboardingPage1()
            }
        }

        // Alt navigasyon bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(AppDimensions.spacingMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sayfa göstergeleri (dots)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppDimensions.spacingMedium),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(OnboardingViewModel.TOTAL_PAGES) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentPage) 12.dp else 8.dp)
                            .background(
                                color = if (index == currentPage) {
                                    themeColors.primary
                                } else {
                                    themeColors.outline.copy(alpha = 0.5f)
                                },
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                    if (index < OnboardingViewModel.TOTAL_PAGES - 1) {
                        Spacer(modifier = Modifier.width(AppDimensions.spacingSmall))
                    }
                }
            }

            // Butonlar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
            ) {
                if (currentPage > 0) {
                    OutlinedButton(
                        onClick = { viewModel.previousPage() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(stringResource(R.string.back))
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                if (currentPage < OnboardingViewModel.TOTAL_PAGES - 1) {
                    Button(
                        onClick = { viewModel.nextPage() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(stringResource(R.string.next))
                    }
                } else {
                    Button(
                        onClick = { viewModel.completeOnboarding() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(stringResource(R.string.get_started))
                    }
                }
            }
        }
    }
}

// ============ SAYFA 1: Hoş Geldiniz ============
@Composable
private fun OnboardingPage1() {
    val themeColors = LocalThemeColors.current
    val appName = stringResource(R.string.splash_title)
    val welcomeTitle = stringResource(R.string.onboarding_welcome_title, appName)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AppDimensions.spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Surface(
            modifier = Modifier.size(100.dp),
            color = themeColors.primary.copy(alpha = 0.1f),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(25.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.splash_logo_text),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = themeColors.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(AppDimensions.spacingExtraLarge))

        Text(
            text = welcomeTitle,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = themeColors.text,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))

        Text(
            text = stringResource(R.string.onboarding_welcome_message),
            fontSize = 15.sp,
            color = themeColors.textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(AppDimensions.spacingExtraLarge))

        // Başlat göstergesi
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SwipeRight,
                contentDescription = null,
                tint = themeColors.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(R.string.onboarding_swipe_hint),
                fontSize = 13.sp,
                color = themeColors.textSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ============ SAYFA 2: Nasıl Oyna ============
@Composable
private fun OnboardingPage2() {
    val themeColors = LocalThemeColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AppDimensions.spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))

        // Başlık
        Text(
            text = stringResource(R.string.onboarding_how_to_play_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = themeColors.text
        )

        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))

        // Instructions
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InstructionItem(
                number = 1,
                title = stringResource(R.string.onboarding_step_select_title),
                description = stringResource(R.string.onboarding_step_select_description)
            )

            InstructionItem(
                number = 2,
                title = stringResource(R.string.onboarding_step_enter_title),
                description = stringResource(R.string.onboarding_step_enter_description)
            )

            InstructionItem(
                number = 3,
                title = stringResource(R.string.onboarding_step_rules_title),
                description = stringResource(R.string.onboarding_step_rules_description)
            )

            InstructionItem(
                number = 4,
                title = stringResource(R.string.onboarding_step_complete_title),
                description = stringResource(R.string.onboarding_step_complete_description)
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
    }
}

// ============ SAYFA 3: İpuçları ve Notlar ============
@Composable
private fun OnboardingPage3() {
    val themeColors = LocalThemeColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AppDimensions.spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))

        Text(
            text = stringResource(R.string.onboarding_smart_features_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = themeColors.text
        )

        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureCard(
                icon = Icons.Default.LightbulbCircle,
                title = stringResource(R.string.onboarding_feature_hints_title),
                description = stringResource(R.string.onboarding_feature_hints_description),
                color = themeColors.secondary
            )

            FeatureCard(
                icon = Icons.Default.Notes,
                title = stringResource(R.string.onboarding_feature_candidates_title),
                description = stringResource(R.string.onboarding_feature_candidates_description),
                color = themeColors.tertiary
            )

            FeatureCard(
                icon = Icons.Default.Undo,
                title = stringResource(R.string.onboarding_feature_undo_title),
                description = stringResource(R.string.onboarding_feature_undo_description),
                color = themeColors.primary
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
    }
}

// ============ SAYFA 4: Oyun Modları ============
@Composable
private fun OnboardingPage4() {
    val themeColors = LocalThemeColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AppDimensions.spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))

        Text(
            text = stringResource(R.string.onboarding_game_modes_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = themeColors.text
        )

        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))

        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ModeCard(
                title = stringResource(R.string.onboarding_mode_classic_title),
                description = stringResource(R.string.onboarding_mode_classic_description),
                icon = Icons.Default.GridView
            )

            ModeCard(
                title = stringResource(R.string.onboarding_mode_daily_title),
                description = stringResource(R.string.onboarding_mode_daily_description),
                icon = Icons.Default.Today
            )

            ModeCard(
                title = stringResource(R.string.onboarding_mode_pvp_title),
                description = stringResource(R.string.onboarding_mode_pvp_description),
                icon = Icons.Default.People
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
    }
}

// ============ SAYFA 5: Başla ============
@Composable
private fun OnboardingPage5() {
    val themeColors = LocalThemeColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AppDimensions.spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(110.dp),
                color = themeColors.primary.copy(alpha = 0.15f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(27.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.onboarding_ready_icon),
                        fontSize = 48.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))

            Text(
                text = stringResource(R.string.onboarding_ready_title),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = themeColors.text
            )

            Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))

            Text(
                text = stringResource(R.string.onboarding_ready_message, stringResource(R.string.splash_title)),
                fontSize = 15.sp,
                color = themeColors.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Quick tips
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = themeColors.cardBackground
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(AppDimensions.spacingMedium),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_quick_tips_title),
                        fontWeight = FontWeight.Bold,
                        color = themeColors.text,
                        fontSize = 14.sp
                    )
                    Text(
                        text = stringResource(R.string.onboarding_quick_tips_list),
                        fontSize = 13.sp,
                        color = themeColors.textSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
    }
}

// ============ HELPER COMPOSABLES ============

@Composable
private fun InstructionItem(
    number: Int,
    title: String,
    description: String
) {
    val themeColors = LocalThemeColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = themeColors.cardBackground,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .padding(AppDimensions.spacingMedium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
    ) {
        // Numara
        Surface(
            modifier = Modifier.size(40.dp),
            color = themeColors.primary.copy(alpha = 0.2f),
            shape = androidx.compose.foundation.shape.CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number.toString(),
                    fontWeight = FontWeight.Bold,
                    color = themeColors.primary,
                    fontSize = 18.sp
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = themeColors.text,
                fontSize = 14.sp
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = themeColors.textSecondary
            )
        }
    }
}

@Composable
private fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    color: Color
) {
    val themeColors = LocalThemeColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = themeColors.cardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.spacingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                color = color.copy(alpha = 0.2f),
                shape = androidx.compose.foundation.shape.CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.text,
                    fontSize = 14.sp
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = themeColors.textSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val themeColors = LocalThemeColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = themeColors.cardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.spacingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = themeColors.primary,
                modifier = Modifier.size(40.dp)
            )

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.text,
                    fontSize = 14.sp
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = themeColors.textSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
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
