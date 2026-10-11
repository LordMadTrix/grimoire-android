package com.grimoire.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoire.app.data.MapPin
import com.grimoire.app.data.SpellShape
import com.grimoire.app.data.VttToken
import com.grimoire.app.ui.theme.*

val COMMON_CONDITIONS = listOf(
    "Aveuglé", "Charmé", "Assourdi", "Effrayé", "Paralysé",
    "Empoisonné", "À terre", "Entravé", "Étourdi", "Inconscient"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TokenEditDialog(
    token: VttToken,
    onHpDelta: (Int) -> Unit,
    onToggleCondition: (String) -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GrimoireCard,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(token.avatarEmoji, fontSize = 24.sp)
                Column {
                    Text(
                        text = token.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = GrimoireGold
                    )
                    Text(
                        text = if (token.isEnemy) "Ennemi / Monstre" else "Allié / Joueur",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (token.isEnemy) GrimoireRed else GrimoireCyan
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // HP Stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Points de Vie (PV) :", color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { onHpDelta(-5) },
                            colors = ButtonDefaults.buttonColors(containerColor = GrimoireRed.copy(alpha = 0.2f), contentColor = GrimoireRed),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) { Text("-5") }
                        Spacer(Modifier.width(4.dp))
                        Button(
                            onClick = { onHpDelta(-1) },
                            colors = ButtonDefaults.buttonColors(containerColor = GrimoireRed.copy(alpha = 0.2f), contentColor = GrimoireRed),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) { Text("-1") }

                        Text(
                            text = " ${token.hp} / ${token.maxHp} ",
                            fontWeight = FontWeight.Bold,
                            color = GrimoireGold,
                            fontSize = 16.sp
                        )

                        Button(
                            onClick = { onHpDelta(1) },
                            colors = ButtonDefaults.buttonColors(containerColor = GrimoireGreen.copy(alpha = 0.2f), contentColor = GrimoireGreen),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) { Text("+1") }
                        Spacer(Modifier.width(4.dp))
                        Button(
                            onClick = { onHpDelta(5) },
                            colors = ButtonDefaults.buttonColors(containerColor = GrimoireGreen.copy(alpha = 0.2f), contentColor = GrimoireGreen),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) { Text("+5") }
                    }
                }

                // Stats overview (CA, Vitesse)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("🛡️ CA : ${token.ac}", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text("⚡ Vitesse : ${token.speed}m", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }

                if (token.notes.isNotBlank()) {
                    Text(
                        text = "Notes : ${token.notes}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }

                // Conditions
                Text("États & Altérations :", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    COMMON_CONDITIONS.forEach { cond ->
                        val isApplied = token.conditions.contains(cond)
                        FilterChip(
                            selected = isApplied,
                            onClick = { onToggleCondition(cond) },
                            label = { Text(cond, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GrimoirePurple.copy(alpha = 0.3f),
                                selectedLabelColor = GrimoireGoldLight
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer", color = GrimoireGold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onRemove()
                    onDismiss()
                }
            ) {
                Text("Supprimer le pion", color = GrimoireRed)
            }
        }
    )
}

@Composable
fun AddTokenDialog(
    onAdd: (name: String, isEnemy: Boolean, hp: Int, avatar: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isEnemy by remember { mutableStateOf(true) }
    var hpStr by remember { mutableStateOf("15") }
    var selectedEmoji by remember { mutableStateOf("👺") }

    val emojis = listOf("👺", "👹", "💀", "🐺", "🐉", "🛡️", "🔮", "🗡️", "✨", "🏹", "🧙")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GrimoireCard,
        title = { Text("Ajouter un Pion sur la Carte", color = GrimoireGold, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de la créature ou du héros") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GrimoireGold,
                        unfocusedBorderColor = GrimoireBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Type :", color = TextSecondary)
                    Row {
                        FilterChip(
                            selected = isEnemy,
                            onClick = { isEnemy = true },
                            label = { Text("Monstre/Ennemi") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GrimoireRed.copy(alpha = 0.25f))
                        )
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = !isEnemy,
                            onClick = { isEnemy = false },
                            label = { Text("Joueur/Allié") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GrimoireCyan.copy(alpha = 0.25f))
                        )
                    }
                }

                OutlinedTextField(
                    value = hpStr,
                    onValueChange = { hpStr = it },
                    label = { Text("Points de Vie Max (PV)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GrimoireGold,
                        unfocusedBorderColor = GrimoireBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Icône :", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(emojis) { emoji ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (selectedEmoji == emoji) GrimoireGold.copy(alpha = 0.3f) else GrimoireCardElevated)
                                .clickable { selectedEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 20.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val hp = hpStr.toIntOrNull() ?: 10
                        onAdd(name, isEnemy, hp, selectedEmoji)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GrimoireGold, contentColor = GrimoireBg)
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler", color = TextMuted) }
        }
    )
}

@Composable
fun PinDetailDialog(
    pin: MapPin,
    onToggleReveal: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GrimoireCard,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(pin.iconEmoji, fontSize = 24.sp)
                Text(pin.title, style = MaterialTheme.typography.titleMedium, color = GrimoireGold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = GrimoireCardElevated,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            text = "🔒 NOTE SECRÈTE MJ :",
                            style = MaterialTheme.typography.labelSmall,
                            color = GrimoirePurple
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = pin.secretDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (pin.isRevealedToPlayers) "Visible par les joueurs" else "Secret (MJ seulement)",
                        color = if (pin.isRevealedToPlayers) GrimoireGreen else GrimoireGold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = onToggleReveal,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (pin.isRevealedToPlayers) GrimoireBorder else GrimoireGreen,
                            contentColor = TextPrimary
                        )
                    ) {
                        Text(if (pin.isRevealedToPlayers) "Masquer" else "Révéler aux joueurs")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fermer", color = GrimoireGold) }
        }
    )
}

@Composable
fun AddSpellDialog(
    onAdd: (name: String, radius: Float, shape: SpellShape) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("Boule de Feu") }
    var radiusTiles by remember { mutableFloatStateOf(4f) }
    var shape by remember { mutableStateOf(SpellShape.CIRCLE) }

    val presetSpells = listOf(
        Triple("Boule de Feu (Rayon 6m)", 4f, SpellShape.CIRCLE),
        Triple("Cône de Froid (Cône 15m)", 6f, SpellShape.CONE),
        Triple("Nuage Nauséabond (Rayon 6m)", 4f, SpellShape.CIRCLE),
        Triple("Ligne de Foudre (Ligne 30m)", 10f, SpellShape.LINE)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GrimoireCard,
        title = { Text("Placer un Gabarit de Sort / Effet AoE", color = GrimoireGold, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Préréglages de Sorts :", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    presetSpells.forEach { (pName, pRad, pShape) ->
                        Surface(
                            color = GrimoireCardElevated,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    name = pName
                                    radiusTiles = pRad
                                    shape = pShape
                                }
                        ) {
                            Text(
                                text = "✨ $pName",
                                modifier = Modifier.padding(10.dp),
                                color = if (name == pName) GrimoireGold else TextPrimary,
                                fontWeight = if (name == pName) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(name, radiusTiles, shape)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = GrimoireGold, contentColor = GrimoireBg)
            ) {
                Text("Placer sur la Carte")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler", color = TextMuted) }
        }
    )
}
