package com.grimoire.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoire.app.data.SoundPreset
import com.grimoire.app.ui.theme.*
import com.grimoire.app.viewmodel.GrimoireViewModel

@Composable
fun SoundscapeScreen(
    viewModel: GrimoireViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val presets = listOf(
        SoundPreset("storm", "Nuit de Tempête", "⛈️", listOf("rain", "wind")),
        SoundPreset("camp", "Bivouac Sauvage", "🏕️", listOf("fire", "wind")),
        SoundPreset("crypt", "Crypte Maudite", "💀", listOf("dungeon", "wind")),
        SoundPreset("tavern", "Auberge & Pluie", "🍻", listOf("fire", "rain"))
    )

    val sfxButtons = listOf(
        Triple("dice", "Lancer de Dés", "🎲"),
        Triple("sword", "Choc d'Acier", "⚔️"),
        Triple("magic", "Surgissement Magique", "✨"),
        Triple("monster", "Rugissement Monstre", "👹"),
        Triple("bell", "Cloche Sacrée / Soin", "🔔")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GrimoireBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Master Control Card
        Card(
            colors = CardDefaults.cardColors(containerColor = GrimoireCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GrimoireBorder)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🌧️ STUDIO D'AMBIANCE SONORE",
                            style = MaterialTheme.typography.titleMedium,
                            color = GrimoireGold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (uiState.isSoundscapePlaying && !uiState.isMuted) "Diffusion active • Synthèse procédurale" else "Silencieux",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (uiState.isSoundscapePlaying && !uiState.isMuted) GrimoireGreen else TextMuted
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledIconButton(
                            onClick = { viewModel.toggleMute() },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (uiState.isMuted) GrimoireRed.copy(alpha = 0.2f) else GrimoirePurple.copy(alpha = 0.2f)
                            )
                        ) {
                            Icon(
                                imageVector = if (uiState.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = "Mute",
                                tint = if (uiState.isMuted) GrimoireRed else GrimoirePurple
                            )
                        }

                        FilledIconButton(
                            onClick = { viewModel.stopAllAudio() },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = GrimoireCardElevated)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", tint = TextMuted)
                        }
                    }
                }
            }
        }

        // Atmospheric Presets Row
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "⚡ Préréglages d'Ambiance Rapides :",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    Surface(
                        color = GrimoireCardElevated,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.applySoundPreset(preset) }
                            .border(1.dp, GrimoireBorder, RoundedCornerShape(10.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(preset.emoji, fontSize = 20.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = preset.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GrimoireGoldLight,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Ambient Tracks Toggles
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "🎛️ Pistes Superposables :",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.ambientTracks.forEach { track ->
                    Surface(
                        color = if (track.active) GrimoirePurple.copy(alpha = 0.2f) else GrimoireCard,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleTrack(track.id) }
                            .border(
                                1.dp,
                                if (track.active) GrimoirePurple else GrimoireBorder,
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(track.iconEmoji, fontSize = 20.sp)
                                Text(
                                    text = track.name,
                                    fontWeight = if (track.active) FontWeight.Bold else FontWeight.Normal,
                                    color = if (track.active) GrimoireGoldLight else TextPrimary,
                                    fontSize = 14.sp
                                )
                            }
                            Switch(
                                checked = track.active,
                                onCheckedChange = { viewModel.toggleTrack(track.id) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = GrimoireGold,
                                    checkedTrackColor = GrimoirePurple
                                )
                            )
                        }
                    }
                }
            }
        }

        // SFX Soundboard
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "🔊 Soundboard d'Effets Sonores Immédiats :",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sfxButtons) { (type, label, emoji) ->
                    Surface(
                        color = GrimoireCardElevated,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .clickable { viewModel.playSfx(type) }
                            .border(1.dp, GrimoireGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(emoji, fontSize = 24.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
