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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.grimoire.app.data.BestiaryMonster
import com.grimoire.app.data.CompendiumData
import com.grimoire.app.data.HerbItem
import com.grimoire.app.ui.theme.*
import com.grimoire.app.viewmodel.GrimoireViewModel
import kotlin.random.Random

@Composable
fun HerboristeScreen(
    viewModel: GrimoireViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedHerbForDetail by remember { mutableStateOf<HerbItem?>(null) }
    var selectedMonsterForDetail by remember { mutableStateOf<BestiaryMonster?>(null) }
    var foragingResult by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GrimoireBg)
            .padding(16.dp)
    ) {
        // Compendium Tab Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Plantes", "Bestiaire", "Cueillette").forEach { tab ->
                val isSelected = uiState.compendiumTab == tab
                Button(
                    onClick = { viewModel.setCompendiumTab(tab) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) GrimoireGold else GrimoireCardElevated,
                        contentColor = if (isSelected) GrimoireBg else TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = when (tab) {
                            "Plantes" -> "🌿 Plantes"
                            "Bestiaire" -> "🐉 Bestiaire"
                            else -> "🎲 Cueillette"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = uiState.compendiumSearchQuery,
            onValueChange = { viewModel.setCompendiumSearch(it) },
            placeholder = { Text("Rechercher dans le compendium...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Recherche", tint = TextMuted) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrimoireGold,
                unfocusedBorderColor = GrimoireBorder
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        // Biome Filter Chips
        if (uiState.compendiumTab == "Plantes") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CompendiumData.biomes) { biome ->
                    val isSelected = uiState.compendiumBiomeFilter == biome
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCompendiumBiome(biome) },
                        label = { Text(biome) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GrimoirePurple.copy(alpha = 0.25f),
                            selectedLabelColor = GrimoireGoldLight
                        )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // Tab Content
        when (uiState.compendiumTab) {
            "Plantes" -> {
                val filteredHerbs = uiState.herbs.filter { herb ->
                    val matchBiome = uiState.compendiumBiomeFilter == "Tous" || herb.biome == uiState.compendiumBiomeFilter
                    val matchSearch = uiState.compendiumSearchQuery.isBlank() ||
                            herb.name.contains(uiState.compendiumSearchQuery, ignoreCase = true) ||
                            herb.effect.contains(uiState.compendiumSearchQuery, ignoreCase = true) ||
                            herb.description.contains(uiState.compendiumSearchQuery, ignoreCase = true)
                    matchBiome && matchSearch
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredHerbs) { herb ->
                        HerbCard(herb = herb, onClick = { selectedHerbForDetail = herb })
                    }
                }
            }
            "Bestiaire" -> {
                val filteredBestiary = uiState.bestiary.filter { mob ->
                    uiState.compendiumSearchQuery.isBlank() ||
                            mob.name.contains(uiState.compendiumSearchQuery, ignoreCase = true) ||
                            mob.type.contains(uiState.compendiumSearchQuery, ignoreCase = true)
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredBestiary) { mob ->
                        MonsterCard(monster = mob, onClick = { selectedMonsterForDetail = mob })
                    }
                }
            }
            "Cueillette" -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GrimoireCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GrimoireBorder)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🍃 Simulateur de Cueillette & Herboristerie",
                            style = MaterialTheme.typography.titleMedium,
                            color = GrimoireGold,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Testez la récolte des PJs selon l'environnement. Le jet prend en compte un dé d20 + modificateur de Survie / Nature (DC 13).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val roll = Random.nextInt(1, 21) + 4
                                val biome = uiState.compendiumBiomeFilter.ifEmpty { "Forêt" }
                                val candidate = uiState.herbs.filter { it.biome == biome || it.rarity == "Commune" }.randomOrNull()
                                foragingResult = if (roll >= 13 && candidate != null) {
                                    "✨ Réussite (Jet: $roll vs DC 13) !\nTrouvé : 1d3x ${candidate.name} (${candidate.rarity})\nEffet : ${candidate.effect}"
                                } else {
                                    "🍂 Échec (Jet: $roll vs DC 13)\nAucune herbe d'intérêt trouvée dans cette zone, ou les spécimens étaient gâtés."
                                }
                                viewModel.playSfx(if (roll >= 13) "magic" else "dice")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GrimoireGreen, contentColor = GrimoireBg),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Lancer un jet de Cueillette (1d20+4)")
                        }

                        if (foragingResult != null) {
                            Spacer(Modifier.height(16.dp))
                            Surface(
                                color = GrimoireCardElevated,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().border(1.dp, GrimoireGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            ) {
                                Text(
                                    text = foragingResult!!,
                                    modifier = Modifier.padding(14.dp),
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Herb Detail Dialog
    if (selectedHerbForDetail != null) {
        val herb = selectedHerbForDetail!!
        AlertDialog(
            onDismissRequest = { selectedHerbForDetail = null },
            containerColor = GrimoireCard,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(herb.emoji, fontSize = 24.sp)
                    Column {
                        Text(herb.name, style = MaterialTheme.typography.titleMedium, color = GrimoireGold)
                        if (herb.latinName.isNotBlank()) {
                            Text(herb.latinName, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RarityBadge(herb.rarity)
                        Surface(color = GrimoireCardElevated, shape = RoundedCornerShape(4.dp)) {
                            Text("📍 ${herb.biome}", color = GrimoireCyan, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Surface(color = GrimoireCardElevated, shape = RoundedCornerShape(4.dp)) {
                            Text("💰 ${herb.priceGold} PO", color = GrimoireGold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Text("Description :", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(herb.description, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)

                    Text("Effet en Jeu :", style = MaterialTheme.typography.labelSmall, color = GrimoireGreen)
                    Text(herb.effect, style = MaterialTheme.typography.bodyMedium, color = GrimoireGoldLight, fontWeight = FontWeight.SemiBold)

                    Text("Préparation Alchimique :", style = MaterialTheme.typography.labelSmall, color = GrimoirePurple)
                    Text(herb.preparation, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedHerbForDetail = null }) { Text("Fermer", color = GrimoireGold) }
            }
        )
    }

    // Monster Detail Dialog
    if (selectedMonsterForDetail != null) {
        val mob = selectedMonsterForDetail!!
        AlertDialog(
            onDismissRequest = { selectedMonsterForDetail = null },
            containerColor = GrimoireCard,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(mob.emoji, fontSize = 24.sp)
                    Column {
                        Text(mob.name, style = MaterialTheme.typography.titleMedium, color = GrimoireGold)
                        Text("${mob.type} • FP ${mob.cr}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        Text("❤️ PV : ${mob.hp}", color = GrimoireRed, fontWeight = FontWeight.Bold)
                        Text("🛡️ CA : ${mob.ac}", color = GrimoireCyan, fontWeight = FontWeight.Bold)
                        Text("⚡ Vitesse : ${mob.speed}", color = TextPrimary)
                    }
                    Divider(color = GrimoireBorder, modifier = Modifier.padding(vertical = 4.dp))
                    Text("Attaques :", style = MaterialTheme.typography.labelSmall, color = GrimoireGold)
                    Text(mob.attacks, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Text("Traits Spéciaux :", style = MaterialTheme.typography.labelSmall, color = GrimoirePurple)
                    Text(mob.specialTraits, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addToken(mob.name, true, mob.hp, mob.emoji)
                        selectedMonsterForDetail = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GrimoireRed, contentColor = TextPrimary)
                ) {
                    Text("Ajouter au Combat / VTT")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedMonsterForDetail = null }) { Text("Fermer", color = TextMuted) }
            }
        )
    }
}

@Composable
fun HerbCard(herb: HerbItem, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GrimoireCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GrimoireBorder)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GrimoireCardElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(herb.emoji, fontSize = 22.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(herb.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    RarityBadge(herb.rarity)
                }
                Spacer(Modifier.height(2.dp))
                Text(herb.effect, style = MaterialTheme.typography.bodyMedium, color = GrimoireGoldLight, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Text("📍 ${herb.biome} • 💰 ${herb.priceGold} PO", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
        }
    }
}

@Composable
fun MonsterCard(monster: BestiaryMonster, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GrimoireCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GrimoireBorder)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(GrimoireRed.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(monster.emoji, fontSize = 22.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(monster.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Surface(color = GrimoireRed.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text("FP ${monster.cr}", color = GrimoireRed, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text("${monster.type} • ❤️ ${monster.hp} PV • 🛡️ CA ${monster.ac}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}

@Composable
fun RarityBadge(rarity: String) {
    val color = when (rarity.lowercase()) {
        "légendaire" -> GrimoireGold
        "très rare" -> GrimoirePurple
        "rare" -> GrimoireCyan
        else -> GrimoireGreen
    }
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = rarity,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
