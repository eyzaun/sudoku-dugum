package com.extremesudoku.presentation.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.dp
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.LocalThemeColors

@Composable
fun NumberPad(
    onNumberClick: (Int) -> Unit,
    remainingNumbers: Map<Int, Int> = emptyMap(),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (number in 1..9) {
            NumberButton(
                number = number,
                remaining = remainingNumbers[number] ?: 9,
                onClick = { onNumberClick(number) }
            )
        }
    }
}

@Composable
fun NumberButton(
    number: Int,
    remaining: Int,
    onClick: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    
    Button(
        onClick = onClick,
        modifier = Modifier.size(AppDimensions.numberPadButtonSizeSmall),
        contentPadding = PaddingValues(AppDimensions.numberPadContentPadding),
        colors = ButtonDefaults.buttonColors(
            containerColor = androidx.compose.ui.graphics.Color(0xFF333333),
            contentColor = androidx.compose.ui.graphics.Color.White
        ),
        enabled = remaining > 0
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White
            )
            if (remaining < 9) {
                Text(
                    text = remaining.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
