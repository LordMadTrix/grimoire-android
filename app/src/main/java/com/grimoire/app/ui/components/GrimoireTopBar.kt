package com.grimoire.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoire.app.ui.theme.*
import com.grimoire.app.viewmodel.GrimoireUiState

@Composable
fun GrimoireTopBar(
    uiState: GrimoireUiState,
    onOpenDiceRoller: () -> Unit,
    onToggleSoundMute: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = GrimoireBg,
        tonalElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Logo & Brand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(listOf(GrimoireGold, GrimoirePurple))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.ExtraBold,
                        color = GrimoireBg,
                        fontSize = 20.sp
                    )
                }

                Column {
                    Text(
                        text = "GRIMOIRE",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = GrimoireGold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Maître du Jeu • TTRPG",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }

            // Quick Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sound indicator
                IconButton(
                    onClick = onToggleSoundMute,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (uiState.isSoundscapePlaying && !uiState.isMuted)
                                GrimoirePurple.copy(alpha = 0.25f)
                            else GrimoireCardElevated
                        )
                ) {
                    Icon(
                        imageVector = if (uiState.isMuted || !uiState.isSoundscapePlaying) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Son",
                        tint = if (uiState.isSoundscapePlaying && !uiState.isMuted) GrimoirePurple else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Quick Dice Button
                FilledTonalButton(
                    onClick = onOpenDiceRoller,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = GrimoireGold.copy(alpha = 0.2f),
                        contentColor = GrimoireGold
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Lancer un dé",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "d20",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
