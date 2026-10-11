package com.grimoire.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoire.app.ui.theme.*
import com.grimoire.app.viewmodel.GrimoireViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceRollerBottomSheet(
    viewModel: GrimoireViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedSides by remember { mutableIntStateOf(20) }
    var diceCount by remember { mutableIntStateOf(1) }
    var modifier by remember { mutableIntStateOf(0) }

    val diceTypes = listOf(4, 6, 8, 10, 12, 20, 100)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = GrimoireCard,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎲 Lanceur de Dés Rôliste",
                    style = MaterialTheme.typography.titleLarge,
                    color = GrimoireGold
                )
                IconButton(onClick = onDismiss) {
                    Text("✕", color = TextMuted, fontSize = 18.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Main Dice Display result box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(GrimoireCardElevated, GrimoireBg)
                        )
                    )
                    .border(
                        1.dp,
                        if (uiState.lastDiceRoll?.isCriticalSuccess == true) GrimoireGreen
                        else if (uiState.lastDiceRoll?.isCriticalFailure == true) GrimoireRed
                        else GrimoireGold.copy(alpha = 0.4f),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isRollingDice) {
                    CircularProgressIndicator(color = GrimoireGold, modifier = Modifier.size(36.dp))
                } else if (uiState.lastDiceRoll != null) {
                    val roll = uiState.lastDiceRoll!!
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${roll.total}",
                            fontFamily = FontFamily.Serif,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black,
                            color = when {
                                roll.isCriticalSuccess -> GrimoireGreen
                                roll.isCriticalFailure -> GrimoireRed
                                else -> GrimoireGold
                            }
                        )
                        Text(
                            text = "${roll.formula} [${roll.rolls.joinToString("+")}]" + (if (roll.modifier != 0) " + mod" else ""),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                        if (roll.isCriticalSuccess) {
                            Text(
                                text = "✨ SUCCÈS CRITIQUE ! ✨",
                                color = GrimoireGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        } else if (roll.isCriticalFailure) {
                            Text(
                                text = "💀 ÉCHEC CRITIQUE ! 💀",
                                color = GrimoireRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Sélectionnez un dé et lancez !",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextMuted
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Dice type selector row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(diceTypes) { sides ->
                    val isSelected = selectedSides == sides
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) GrimoireGold else GrimoireCardElevated
                            )
                            .border(
                                1.dp,
                                if (isSelected) GrimoireGold else GrimoireBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedSides = sides },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "d$sides",
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) GrimoireBg else TextPrimary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Count and Modifier Steppers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Count stepper
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Nombre : ", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                    IconButton(
                        onClick = { if (diceCount > 1) diceCount-- },
                        modifier = Modifier.size(32.dp).background(GrimoireCardElevated, CircleShape)
                    ) {
                        Text("-", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "$diceCount",
                        color = GrimoireGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(
                        onClick = { if (diceCount < 10) diceCount++ },
                        modifier = Modifier.size(32.dp).background(GrimoireCardElevated, CircleShape)
                    ) {
                        Text("+", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                // Modifier stepper
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Modificateur : ", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                    IconButton(
                        onClick = { modifier-- },
                        modifier = Modifier.size(32.dp).background(GrimoireCardElevated, CircleShape)
                    ) {
                        Text("-", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = if (modifier >= 0) "+$modifier" else "$modifier",
                        color = GrimoirePurple,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(
                        onClick = { modifier++ },
                        modifier = Modifier.size(32.dp).background(GrimoireCardElevated, CircleShape)
                    ) {
                        Text("+", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Big Roll Button
            Button(
                onClick = {
                    viewModel.rollDice(diceCount, selectedSides, modifier)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GrimoireGold,
                    contentColor = GrimoireBg
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "LANCER ${diceCount}d$selectedSides" + (if (modifier != 0) " ${if (modifier > 0) "+$modifier" else "$modifier"}" else ""),
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                )
            }

            // History Preview
            if (uiState.diceHistory.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Derniers lancers : " + uiState.diceHistory.take(5).joinToString(" • ") { "${it.formula}=${it.total}" },
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
