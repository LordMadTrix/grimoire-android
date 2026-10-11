package com.grimoire.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoire.app.data.RollTable
import com.grimoire.app.ui.theme.*
import com.grimoire.app.viewmodel.GrimoireViewModel

@Composable
fun ToolkitScreen(
    viewModel: GrimoireViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedToolSection by remember { mutableStateOf("Tables") } // "Tables", "Générateur PNJ", "Rencontres"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GrimoireBg)
            .padding(16.dp)
    ) {
        // Section Selector Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Tables", "Générateur PNJ", "Rencontres").forEach { sec ->
                val isSelected = selectedToolSection == sec
                Button(
                    onClick = { selectedToolSection = sec },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) GrimoireGold else GrimoireCardElevated,
                        contentColor = if (isSelected) GrimoireBg else TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = when (sec) {
                            "Tables" -> "📜 Tables"
                            "Générateur PNJ" -> "👤 PNJ"
                            else -> "⚔️ Rencontres"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        when (selectedToolSection) {
            "Tables" -> {
                // Roll Tables List and Trigger
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.rollTables) { table ->
                        val isSelected = uiState.selectedRollTable?.id == table.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.rollRollTable(table) },
                            label = { Text("${table.emoji} ${table.name}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GrimoireGold.copy(alpha = 0.25f),
                                selectedLabelColor = GrimoireGoldLight
                            )
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Result Box
                if (uiState.lastTableResult != null && uiState.selectedRollTable != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GrimoireCard),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GrimoireGold)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${uiState.selectedRollTable!!.emoji} Résultat : ${uiState.selectedRollTable!!.name}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = GrimoireGold
                                )
                                Button(
                                    onClick = { viewModel.rollRollTable(uiState.selectedRollTable!!) },
                                    colors = ButtonDefaults.buttonColors(containerColor = GrimoireGold, contentColor = GrimoireBg),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("🎲 Relancer", fontSize = 12.sp)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = uiState.lastTableResult!!,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Items in current table preview
                if (uiState.selectedRollTable != null) {
                    Text(
                        text = "Éléments de la table (${uiState.selectedRollTable!!.items.size}) :",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(uiState.selectedRollTable!!.items) { item ->
                            Surface(
                                color = GrimoireCardElevated,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "• $item",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            "Générateur PNJ" -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Button(
                        onClick = { viewModel.generateNewNpc() },
                        colors = ButtonDefaults.buttonColors(containerColor = GrimoireGold, contentColor = GrimoireBg),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("✨ Générer un Nouveau PNJ Aléatoire", fontWeight = FontWeight.Bold)
                    }

                    val npc = uiState.generatedNpc
                    if (npc != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GrimoireCard),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GrimoirePurple)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("👤", fontSize = 24.sp)
                                    Column {
                                        Text(npc.name, style = MaterialTheme.typography.titleLarge, color = GrimoireGold)
                                        Text(npc.raceAndClass, style = MaterialTheme.typography.labelSmall, color = GrimoireCyan)
                                    }
                                }

                                Divider(color = GrimoireBorder)

                                Text("🎭 Personnalité :", style = MaterialTheme.typography.labelSmall, color = GrimoireGoldLight)
                                Text(npc.personalityTrait, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)

                                Text("🔍 Secret & Motivation :", style = MaterialTheme.typography.labelSmall, color = GrimoirePurple)
                                Text(npc.secretOrMotivation, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)

                                Text("⚡ Tics & Quirks :", style = MaterialTheme.typography.labelSmall, color = GrimoireGreen)
                                Text(npc.quirk, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)

                                Text("👀 Apparence :", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(npc.appearance, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)

                                Spacer(Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        viewModel.addToken(npc.name, false, 15, "👤")
                                        viewModel.showToast("PNJ ajouté sur la carte VTT")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GrimoireCardElevated, contentColor = GrimoireGold),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("📍 Placer ce PNJ sur la carte VTT")
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Touchez 'Générer' pour créer un PNJ complet instantanément.", color = TextMuted)
                        }
                    }
                }
            }

            "Rencontres" -> {
                var selectedBiome by remember { mutableStateOf("Forêt") }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listOf("Forêt", "Souterrain", "Montagne", "Plaines")) { b ->
                            FilterChip(
                                selected = selectedBiome == b,
                                onClick = { selectedBiome = b },
                                label = { Text(b) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GrimoirePurple.copy(alpha = 0.25f),
                                    selectedLabelColor = GrimoireGoldLight
                                )
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.generateNewEncounter(selectedBiome) },
                        colors = ButtonDefaults.buttonColors(containerColor = GrimoireGold, contentColor = GrimoireBg),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("⚔️ Générer une Rencontre ($selectedBiome)", fontWeight = FontWeight.Bold)
                    }

                    val enc = uiState.generatedEncounter
                    if (enc != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GrimoireCard),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GrimoireRed)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(enc.title, style = MaterialTheme.typography.titleMedium, color = GrimoireGold)
                                    Surface(color = GrimoireRed.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                        Text(enc.dangerLevel, color = GrimoireRed, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }

                                Text(enc.description, color = TextPrimary, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)

                                Text("Composition des Ennemis suggérés :", style = MaterialTheme.typography.labelSmall, color = GrimoirePurple)
                                enc.enemySuggestions.forEach { enemy ->
                                    Text("• $enemy", color = GrimoireGoldLight, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
