package com.extremesudoku.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
                contentDescription = "Skip",
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
                        Text("Back")
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

// ============ SAYFA 1: Hoş Geldiniz ============
@Composable
private fun OnboardingPage1() {
    val themeColors = LocalThemeColors.current

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
                    text = "9×9",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = themeColors.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(AppDimensions.spacingExtraLarge))

        Text(
            text = "Welcome to\nExtreme Sudoku",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = themeColors.text,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))

        Text(
            text = "The ultimate puzzle game that challenges your mind and improves your logical thinking skills.",
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
                text = "Swipe to explore features",
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
            text = "How to Play",
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
                number = "1",
                title = "Select a Cell",
                description = "Tap any empty cell in the 9×9 grid to select it"
            )

            InstructionItem(
                number = "2",
                title = "Enter Numbers",
                description = "Use the number pad to enter numbers 1-9"
            )

            InstructionItem(
                number = "3",
                title = "Follow Rules",
                description = "Each row, column, and 3×3 box must contain 1-9"
            )

            InstructionItem(
                number = "4",
                title = "Complete Puzzle",
                description = "Fill all cells correctly to solve the puzzle"
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
            text = "Smart Features",
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
                title = "Hints",
                description = "Get hints when you're stuck. Choose to reveal a number or highlight cells",
                color = themeColors.secondary
            )

            FeatureCard(
                icon = Icons.Default.Notes,
                title = "Candidate Numbers",
                description = "Toggle note mode to write small candidate numbers in cells",
                color = themeColors.tertiary
            )

            FeatureCard(
                icon = Icons.Default.Undo,
                title = "Undo & Redo",
                description = "Made a mistake? Undo your moves or redo them anytime",
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
            text = "Game Modes",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = themeColors.text
        )

        Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))

        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ModeCard(
                title = "Classic Sudoku",
                description = "Traditional 9×9 puzzle\nMultiple difficulty levels",
                icon = Icons.Default.GridView
            )

            ModeCard(
                title = "Daily Challenge",
                description = "New puzzle every day\nCompete with others",
                icon = Icons.Default.Today
            )

            ModeCard(
                title = "PvP Modes",
                description = "Battle players online\nRank up and earn badges",
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
                        text = "🎮",
                        fontSize = 48.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))

            Text(
                text = "Ready to Play?",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = themeColors.text
            )

            Spacer(modifier = Modifier.height(AppDimensions.spacingSmall))

            Text(
                text = "You're all set to dive into the world of Extreme Sudoku. Start with an easy puzzle and challenge yourself!",
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
                        text = "💡 Quick Tips",
                        fontWeight = FontWeight.Bold,
                        color = themeColors.text,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "• Swipe cells for faster navigation\n• Use hints strategically\n• Practice different difficulty levels\n• Check your accuracy stats",
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
    number: String,
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
                    text = number,
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
