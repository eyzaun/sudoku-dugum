package com.extremesudoku.presentation.pvp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.extremesudoku.presentation.theme.LocalThemeColors
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.AppShapes

/**
 * PvP Mode Seçim Ekranı
 * Kullanıcı Blind Race veya Live Battle modunu seçer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PvpModeSelectionScreen(
    onModeSelected: (String) -> Unit, // "blind_race" veya "live_battle"
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PvP Modu Seç") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(AppDimensions.dialogPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimensions.dialogPadding, Alignment.CenterVertically)
        ) {
            // Başlık
            Text(
                text = "PvP Modu",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Rakibinle kapışacağın modu seç!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
            
            // Blind Race Mode Card
            ModeCard(
                title = "Kör Yarış",
                description = "Rakibi görmeden yarış!\nAynı sudoku'yu kim daha hızlı çözer?",
                icon = Icons.Default.Timer,
                color = MaterialTheme.colorScheme.primary,
                features = listOf(
                    "Aynı puzzle",
                    "Rakibin ilerlemesini görürsün (%)",
                    "İlk tamamlayan kazanır"
                ),
                onClick = { onModeSelected("BLIND_RACE") }  // Enum name
            )
            
            // Live Battle Mode Card
            ModeCard(
                title = "⚔️ Canlı Savaş",
                description = "Rakibin hamlelerini görürsün!\nDoğru +1, Yanlış -1 puan!",
                icon = Icons.Default.Visibility,
                color = MaterialTheme.colorScheme.secondary,
                features = listOf(
                    "Aynı puzzle",
                    "Rakibin DOĞRU hamlelerini anlık görürsün",
                    "En yüksek skor kazanır (10 dk)"
                ),
                onClick = { onModeSelected("LIVE_BATTLE") }  // Enum name
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    features: List<String>,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(AppDimensions.homeCardHeight + AppDimensions.iconSizeSmall),
        shape = AppShapes.card,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppDimensions.spacingLarge),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.homeItemSpacing)
        ) {
            // Icon ve Başlık
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.homeItemSpacing)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(AppDimensions.iconSizeLarge)
                )
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            
            // Açıklama
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Divider(modifier = Modifier.padding(vertical = AppDimensions.spacingExtraSmall))
            
            // Özellikler
            features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(AppDimensions.iconSizeSmall)
                    )
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
