package com.grimoire.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoire.app.data.Combatant
import com.grimoire.app.data.LogType
import com.grimoire.app.ui.components.COMMON_CONDITIONS
import com.grimoire.app.ui.theme.*
import com.grimoire.app.viewmodel.GrimoireViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CombatScreen(
    viewModel: GrimoireViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCombatantForConditions by remember { mutableStateOf<Combatant?>(null) }

    val currentTurnCombatant = uiState.combatants.getOrNull(uiState.currentTurnIndex)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GrimoireBg)
            .padding(16.dp)
    ) {
        // Combat Header & Controls (Round & Turn Management)
        Card(
            colors = CardDefaults.cardColors(containerColor = GrimoireCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GrimoireBorder)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "⚔️ GESTION DU COMBAT",
                            style = MaterialTheme.typography.titleMedium,
                            color = GrimoireGold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Round ${uiState.currentRound} • Combattant : ${currentTurnCombatant?.name ?: "Aucun"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    // Turn Steppers
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledIconButton(
                            onClick = { viewModel.prevTurn() },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = GrimoireCardElevated)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Tour précédent", tint = TextPrimary)
                        }

                        Button(
                            onClick = { viewModel.nextTurn() },
                            colors = ButtonDefaults.buttonColors(containerColor = GrimoireGold, contentColor = GrimoireBg),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Tour Suivant ▶", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Action Bar for rolling initiative and adding combatants
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.rollAllInitiatives("d20") },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("🎲 Init d20", fontSize = 12.sp, color = GrimoireGold)
                        }
                        OutlinedButton(
                            onClick = { viewModel.rollAllInitiatives("wfrp") },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("🎲 WFRP (1d10+3)", fontSize = 12.sp, color = GrimoirePurple)
                        }
                    }

                    FilledTonalButton(
                        onClick = { showAddDialog = true },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Ajouter", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Combatants List
        Text(
            text = "Ordre d'Initiative (${uiState.combatants.size} combattants)",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(uiState.combatants) { index, combatant ->
                val isCurrentTurn = index == uiState.currentTurnIndex
                val hpPct = if (combatant.maxHp > 0) (combatant.hp.toFloat() / combatant.maxHp).coerceIn(0f, 1f) else 0f
                val isDead = combatant.hp <= 0

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrentTurn) GrimoireCardElevated else GrimoireCard
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isCurrentTurn) GrimoireGold else if (isDead) GrimoireRed.copy(alpha = 0.4f) else GrimoireBorder
                        )
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Initiative Badge & Name
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (combatant.isEnemy) GrimoireRed.copy(alpha = 0.2f) else GrimoireCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${combatant.initiative}",
                                        fontWeight = FontWeight.Black,
                                        color = if (combatant.isEnemy) GrimoireRed else GrimoireCyan,
                                        fontSize = 15.sp
                                    )
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(combatant.avatarEmoji, fontSize = 16.sp)
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = combatant.name,
                                            fontWeight = if (isCurrentTurn) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isDead) TextMuted else TextPrimary,
                                            fontSize = 15.sp
                                        )
                                    }
                                    if (combatant.conditions.isNotEmpty()) {
                                        Text(
                                            text = combatant.conditions.joinToString(", "),
                                            color = GrimoirePurple,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            // HP Controls
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.modifyCombatantHp(combatant.id, -1) },
                                    modifier = Modifier.size(32.dp).background(GrimoireCardElevated, CircleShape)
                                ) {
                                    Text("-", color = GrimoireRed, fontWeight = FontWeight.Bold)
                                }

                                Text(
                                    text = "${combatant.hp}/${combatant.maxHp}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDead) GrimoireRed else GrimoireGold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )

                                IconButton(
                                    onClick = { viewModel.modifyCombatantHp(combatant.id, 1) },
                                    modifier = Modifier.size(32.dp).background(GrimoireCardElevated, CircleShape)
                                ) {
                                    Text("+", color = GrimoireGreen, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = { selectedCombatantForConditions = combatant },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = TextMuted)
                                }
                            }
                        }

                        // Health Progress Bar
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { hpPct },
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                            color = if (hpPct > 0.5f) GrimoireGreen else if (hpPct > 0.2f) GrimoireYellow else GrimoireRed,
                            trackColor = Color(0xFF222632)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Live Combat Log snippet
        Card(
            colors = CardDefaults.cardColors(containerColor = GrimoireCardElevated),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(100.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "📜 JOURNAL DU COMBAT",
                    style = MaterialTheme.typography.labelSmall,
                    color = GrimoireGold
                )
                Spacer(Modifier.height(4.dp))
                LazyColumn {
                    itemsIndexed(uiState.combatLogs.take(5)) { _, log ->
                        Text(
                            text = "[${log.timestamp}] ${log.message}",
                            fontSize = 11.sp,
                            color = when (log.type) {
                                LogType.DAMAGE -> GrimoireRed
                                LogType.HEAL -> GrimoireGreen
                                LogType.INITIATIVE -> GrimoirePurple
                                else -> TextSecondary
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Combatant Dialog
    if (showAddDialog) {
        var newName by remember { mutableStateOf("") }
        var newInit by remember { mutableStateOf("12") }
        var newHp by remember { mutableStateOf("20") }
        var isEnemy by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = GrimoireCard,
            title = { Text("Nouveau Combattant", color = GrimoireGold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Nom") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newInit,
                            onValueChange = { newInit = it },
                            label = { Text("Initiative") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newHp,
                            onValueChange = { newHp = it },
                            label = { Text("PV Max") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Camp :", color = TextSecondary)
                        FilterChip(
                            selected = isEnemy,
                            onClick = { isEnemy = !isEnemy },
                            label = { Text(if (isEnemy) "Ennemi" else "Allié") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.addCombatant(
                                name = newName,
                                init = newInit.toIntOrNull() ?: 10,
                                hp = newHp.toIntOrNull() ?: 10,
                                isEnemy = isEnemy
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GrimoireGold, contentColor = GrimoireBg)
                ) {
                    Text("Ajouter")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Annuler", color = TextMuted) }
            }
        )
    }

    // Conditions and options sheet for a combatant
    if (selectedCombatantForConditions != null) {
        val c = selectedCombatantForConditions!!
        AlertDialog(
            onDismissRequest = { selectedCombatantForConditions = null },
            containerColor = GrimoireCard,
            title = { Text("États de ${c.name}", color = GrimoireGold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Activer / Désactiver une condition :", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        COMMON_CONDITIONS.forEach { cond ->
                            val isApplied = c.conditions.contains(cond)
                            FilterChip(
                                selected = isApplied,
                                onClick = {
                                    viewModel.toggleCombatantCondition(c.id, cond)
                                    selectedCombatantForConditions = uiState.combatants.find { it.id == c.id }
                                },
                                label = { Text(cond, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedCombatantForConditions = null }) { Text("OK", color = GrimoireGold) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.removeCombatant(c.id)
                        selectedCombatantForConditions = null
                    }
                ) {
                    Text("Retirer du combat", color = GrimoireRed)
                }
            }
        )
    }
}
