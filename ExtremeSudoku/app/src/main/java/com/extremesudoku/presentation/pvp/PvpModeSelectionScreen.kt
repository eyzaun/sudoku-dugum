package com.extremesudoku.presentation.pvp

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.extremesudoku.R
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
                title = { Text(stringResource(R.string.pvp_mode_select_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(AppDimensions.dialogPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingLarge)
        ) {
            // Başlık
            Text(
                text = stringResource(R.string.pvp_mode_heading),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = stringResource(R.string.pvp_mode_select_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(AppDimensions.spacingMedium))
            
            // Blind Race Mode Card
            ModeCard(
                titleRes = R.string.pvp_mode_blind_race_title,
                descriptionRes = R.string.pvp_mode_blind_race_description,
                icon = Icons.Default.Timer,
                color = MaterialTheme.colorScheme.primary,
                featureResIds = listOf(
                    R.string.pvp_mode_blind_feature_same_puzzle,
                    R.string.pvp_mode_blind_feature_progress,
                    R.string.pvp_mode_blind_feature_first_finish
                ),
                onClick = { onModeSelected("BLIND_RACE") }  // Enum name
            )
            
            // Live Battle Mode Card
            ModeCard(
                titleRes = R.string.pvp_mode_live_battle_title,
                descriptionRes = R.string.pvp_mode_live_battle_description,
                icon = Icons.Default.Visibility,
                color = MaterialTheme.colorScheme.secondary,
                featureResIds = listOf(
                    R.string.pvp_mode_live_feature_same_puzzle,
                    R.string.pvp_mode_live_feature_opponent_moves,
                    R.string.pvp_mode_live_feature_high_score
                ),
                onClick = { onModeSelected("LIVE_BATTLE") }  // Enum name
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeCard(
    @StringRes titleRes: Int,
    @StringRes descriptionRes: Int,
    icon: ImageVector,
    color: Color,
    featureResIds: List<Int>,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
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
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            
            // Açıklama
            Text(
                text = stringResource(descriptionRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Divider(modifier = Modifier.padding(vertical = AppDimensions.spacingExtraSmall))
            
            // Özellikler
            featureResIds.forEach { featureRes ->
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
                        text = stringResource(featureRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
