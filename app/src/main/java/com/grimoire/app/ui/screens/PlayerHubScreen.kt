package com.grimoire.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoire.app.data.PlayerCharacter
import com.grimoire.app.data.SecretHandout
import com.grimoire.app.ui.theme.*
import com.grimoire.app.viewmodel.GrimoireViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayerHubScreen(
    viewModel: GrimoireViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedSection by remember { mutableStateOf("Fiches") } // "Fiches" or "Indices"
    var expandedHandoutId by remember { mutableStateOf<String?>(null) }
    val expandedHandout = uiState.handouts.find { it.id == expandedHandoutId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GrimoireBg)
            .padding(16.dp)
    ) {
        // Sub-Tab Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { selectedSection = "Fiches" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedSection == "Fiches") GrimoireGold else GrimoireCardElevated,
                    contentColor = if (selectedSection == "Fiches") GrimoireBg else TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("👥 Fiches des Joueurs", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Button(
                onClick = { selectedSection = "Indices" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedSection == "Indices") GrimoireGold else GrimoireCardElevated,
                    contentColor = if (selectedSection == "Indices") GrimoireBg else TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("📜 Indices & Handouts", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(14.dp))

        if (selectedSection == "Fiches") {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.players) { player ->
                    PlayerCharacterCard(
                        player = player,
                        onHpDelta = { delta -> viewModel.modifyPlayerHp(player.id, delta) }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.handouts) { handout ->
                    HandoutCard(
                        handout = handout,
                        onToggleShare = { viewModel.toggleHandoutShare(handout.id) },
                        onExpand = { expandedHandoutId = handout.id }
                    )
                }
            }
        }
    }

    if (expandedHandout != null) {
        val h = expandedHandout
        AlertDialog(
            onDismissRequest = { expandedHandoutId = null },
            containerColor = GrimoireCard,
            title = { Text(h.title, color = GrimoireGold, style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(h.summary, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Surface(
                        color = GrimoireCardElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, GrimoireBorder, RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = h.fullText,
                            modifier = Modifier.padding(12.dp),
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { expandedHandoutId = null }) { Text("Fermer", color = GrimoireGold) }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayerCharacterCard(
    player: PlayerCharacter,
    onHpDelta: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GrimoireCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GrimoireBorder)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(CircleShape).background(GrimoireCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(player.avatarEmoji, fontSize = 22.sp)
                    }
                    Column {
                        Text(player.name, style = MaterialTheme.typography.titleMedium, color = GrimoireGold)
                        Text("${player.charClass} (Niv. ${player.level}) • Joué par ${player.playerRealName}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }

                // HP Controls
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { onHpDelta(-1) },
                        modifier = Modifier.size(30.dp).background(GrimoireCardElevated, CircleShape)
                    ) {
                        Text("-", color = GrimoireRed, fontWeight = FontWeight.Bold)
                    }
                    Text("${player.hp}/${player.maxHp}", fontWeight = FontWeight.Bold, color = GrimoireGreen, fontSize = 13.sp)
                    IconButton(
                        onClick = { onHpDelta(1) },
                        modifier = Modifier.size(30.dp).background(GrimoireCardElevated, CircleShape)
                    ) {
                        Text("+", color = GrimoireGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Stats grid (CA, Perception passive, Caractéristiques)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Surface(color = GrimoireCardElevated, shape = RoundedCornerShape(8.dp)) {
                    Text("🛡️ CA : ${player.ac}", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Surface(color = GrimoireCardElevated, shape = RoundedCornerShape(8.dp)) {
                    Text("👁️ Perception : ${player.passivePerception}", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Ability Scores mini-row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                player.stats.forEach { (stat, valScore) ->
                    val mod = (valScore - 10) / 2
                    val modStr = if (mod >= 0) "+$mod" else "$mod"
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stat, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("$valScore ($modStr)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrimoireGoldLight)
                    }
                }
            }

            // Key Feats / Spells chips
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                player.keySpellsOrFeats.forEach { feat ->
                    Surface(color = GrimoireCardElevated, shape = RoundedCornerShape(4.dp)) {
                        Text("✨ $feat", color = GrimoirePurple, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HandoutCard(
    handout: SecretHandout,
    onToggleShare: () -> Unit,
    onExpand: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GrimoireCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GrimoireBorder)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().clickable { onExpand() }
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(handout.title, style = MaterialTheme.typography.titleMedium, color = GrimoireGold)
                Surface(
                    color = if (handout.isSharedWithPlayers) GrimoireGreen.copy(alpha = 0.2f) else GrimoirePurple.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (handout.isSharedWithPlayers) "Partagé aux joueurs" else "Secret MJ",
                        color = if (handout.isSharedWithPlayers) GrimoireGreen else GrimoirePurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(handout.summary, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onExpand) { Text("Lire le contenu intégral 📖", color = GrimoireGold, fontSize = 12.sp) }
                OutlinedButton(onClick = onToggleShare, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(if (handout.isSharedWithPlayers) "Retirer le partage" else "Partager aux joueurs", fontSize = 11.sp)
                }
            }
        }
    }
}
