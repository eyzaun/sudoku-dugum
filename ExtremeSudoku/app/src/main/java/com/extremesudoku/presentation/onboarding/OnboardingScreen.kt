package com.extremesudoku.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.LocalThemeColors

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onOnboardingComplete: () -> Unit
) {
    val currentPage by viewModel.currentPage.collectAsState()
    val onboardingComplete by viewModel.onboardingComplete.collectAsState()
    val themeColors = LocalThemeColors.current

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
        // Sayfalar
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
            modifier = Modifier.fillMaxSize()
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

        // Alt buton ve göstergeler
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
                    .padding(bottom = AppDimensions.spacingLarge),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(OnboardingViewModel.TOTAL_PAGES) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentPage) 10.dp else 8.dp)
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
                        Text("Previous")
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
                        Text("Next")
                    }
                } else {
                    Button(
                        onClick = { viewModel.completeOnboarding() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Get Started")
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageBase(
    title: String,
    description: String,
    icon: ImageVector,
    features: List<String>,
    bgColor: Color? = null
) {
    val themeColors = LocalThemeColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor ?: themeColors.background)
            .verticalScroll(rememberScrollState())
            .padding(AppDimensions.spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // İkon
        Surface(
            modifier = Modifier
                .size(120.dp),
            color = themeColors.primary.copy(alpha = 0.1f),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(30.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = themeColors.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(AppDimensions.spacingExtraLarge))

        // Başlık
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = themeColors.text,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))

        // Açıklama
        Text(
            text = description,
            fontSize = 16.sp,
            color = themeColors.textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(AppDimensions.spacingExtraLarge))

        // Özellikler
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimensions.spacingMedium),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
        ) {
            features.forEach { feature ->
                FeatureItem(text = feature)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun FeatureItem(text: String) {
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
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = themeColors.secondary
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = themeColors.text
        )
    }
}

// Sayfa 1: Hoş geldiniz
@Composable
private fun OnboardingPage1() {
    val themeColors = LocalThemeColors.current
    OnboardingPageBase(
        title = "Welcome to\nExtreme Sudoku",
        description = "The ultimate puzzle game that challenges your mind and sharpens your problem-solving skills",
        icon = Icons.Default.Games,
        features = listOf(
            "Multiple difficulty levels (Easy to Expert)",
            "Compete with players worldwide",
            "Track your progress and achievements"
        ),
        bgColor = themeColors.primary.copy(alpha = 0.05f)
    )
}

// Sayfa 2: Oyun Modları
@Composable
private fun OnboardingPage2() {
    OnboardingPageBase(
        title = "Multiple Game Modes",
        description = "Choose your favorite way to play and challenge yourself in different ways",
        icon = Icons.Default.Extension,
        features = listOf(
            "Classic Sudoku - Traditional 9×9 grid",
            "X-Sudoku - Extra diagonal challenge",
            "Daily Challenge - New puzzle every day",
            "Leaderboards - Compete globally"
        )
    )
}

// Sayfa 3: PvP Modu
@Composable
private fun OnboardingPage3() {
    val themeColors = LocalThemeColors.current
    OnboardingPageBase(
        title = "Battle Other Players",
        description = "Challenge friends and players worldwide in real-time competitive modes",
        icon = Icons.Default.People,
        features = listOf(
            "Live Battle - Solve puzzles in real-time",
            "Blind Race - Compete without seeing solutions",
            "Rank System - Climb the leaderboards",
            "Achievements - Unlock special rewards"
        ),
        bgColor = themeColors.secondary.copy(alpha = 0.05f)
    )
}

// Sayfa 4: Özellikler
@Composable
private fun OnboardingPage4() {
    OnboardingPageBase(
        title = "Smart Features",
        description = "Tools designed to help you solve puzzles efficiently and improve your skills",
        icon = Icons.Default.AutoAwesome,
        features = listOf(
            "Hints System - Get help when stuck",
            "Note-taking - Write candidate numbers",
            "Undo/Redo - Never lose a move",
            "Customizable Themes - Light, Dark, and more"
        )
    )
}

// Sayfa 5: Başla
@Composable
private fun OnboardingPage5() {
    val themeColors = LocalThemeColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        themeColors.primary.copy(alpha = 0.1f),
                        themeColors.background
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(AppDimensions.spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Surface(
            modifier = Modifier.size(140.dp),
            color = themeColors.primary.copy(alpha = 0.15f),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(35.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "9×9",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = themeColors.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "You're All Set!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = themeColors.text,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "You're ready to dive into the world of Extreme Sudoku.\nLet's start playing and have fun!",
            fontSize = 16.sp,
            color = themeColors.textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Hızlı başlangıç ipuçları
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = themeColors.cardBackground)
        ) {
            Column(
                modifier = Modifier.padding(AppDimensions.spacingMedium),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
            ) {
                Text(
                    text = "Quick Tips:",
                    fontWeight = FontWeight.Bold,
                    color = themeColors.text
                )
                Text(
                    text = "• Tap a cell to select it\n• Use the number pad to enter values\n• Toggle note mode for candidate numbers\n• Use hints when you're stuck",
                    fontSize = 13.sp,
                    color = themeColors.textSecondary,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
