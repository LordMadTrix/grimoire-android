package com.grimoire.app.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoire.app.data.MapPin
import com.grimoire.app.data.SpellShape
import com.grimoire.app.data.VttToken
import com.grimoire.app.ui.components.*
import com.grimoire.app.ui.theme.*
import com.grimoire.app.viewmodel.GrimoireViewModel

@Composable
fun VttScreen(
    viewModel: GrimoireViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val textMeasurer = rememberTextMeasurer()

    var showAddTokenDialog by remember { mutableStateOf(false) }
    var showSpellDialog by remember { mutableStateOf(false) }
    var selectedPinForDetail by remember { mutableStateOf<MapPin?>(null) }
    var editingToken by remember { mutableStateOf<VttToken?>(null) }

    // Map zooming and panning
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 3.5f)
        offset += panChange
    }

    // Load bitmap from asset if available
    val activeMap = uiState.activeMap
    val mapBitmap = remember(activeMap.assetPath) {
        if (activeMap.assetPath.isNotBlank()) {
            try {
                context.assets.open(activeMap.assetPath).use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            } catch (_: Exception) {
                null
            }
        } else null
    }

    val selectedToken = uiState.tokens.find { it.id == uiState.selectedTokenId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GrimoireBg)
    ) {
        // Top Scene Switcher bar
        Surface(
            color = GrimoireCard,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(uiState.maps) { scene ->
                        val isSelected = scene.id == activeMap.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.switchMapScene(scene.id) },
                            label = { Text("🗺️ ${scene.name}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GrimoireGold.copy(alpha = 0.25f),
                                selectedLabelColor = GrimoireGoldLight,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
            }
        }

        // Secondary VTT Toolbar (Fog, Grid, Spells, Add Token, Reset Zoom)
        Surface(
            color = GrimoireCardElevated,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { showAddTokenDialog = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Ajouter Pion", tint = GrimoireGold)
                    }
                    IconButton(
                        onClick = { showSpellDialog = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = "Sort AoE", tint = GrimoirePurple)
                    }
                    IconButton(
                        onClick = { viewModel.toggleFog() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.fogOfWarActive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Brouillard",
                            tint = if (uiState.fogOfWarActive) GrimoirePurple else TextMuted
                        )
                    }
                    IconButton(
                        onClick = { viewModel.toggleGrid() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Grille",
                            tint = if (uiState.showGrid) GrimoireCyan else TextMuted
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(
                        onClick = { viewModel.revealAllFog() },
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("Tout Révéler", fontSize = 12.sp, color = GrimoireGreen)
                    }
                    TextButton(
                        onClick = {
                            scale = 1f
                            offset = Offset.Zero
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("Centrer", fontSize = 12.sp, color = GrimoireGold)
                    }
                }
            }
        }

        // Tactical Canvas Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(0.dp))
                .background(Color(0xFF0F1219))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            val cellSize = 44f * density
                            // Convert canvas pixel to grid coord factoring zoom & pan
                            val canvasX = (tapOffset.x - offset.x) / scale
                            val canvasY = (tapOffset.y - offset.y) / scale
                            val gridX = (canvasX / cellSize).toInt()
                            val gridY = (canvasY / cellSize).toInt()

                            // Check if tapped on a token
                            val clickedToken = uiState.tokens.find { t ->
                                val px = t.x * cellSize + cellSize / 2
                                val py = t.y * cellSize + cellSize / 2
                                val dist = kotlin.math.hypot(canvasX - px, canvasY - py)
                                dist <= cellSize / 1.5
                            }

                            // Check if tapped on a pin
                            val clickedPin = uiState.pins.find { p ->
                                val px = p.x * cellSize + cellSize / 2
                                val py = p.y * cellSize + cellSize / 2
                                val dist = kotlin.math.hypot(canvasX - px, canvasY - py)
                                dist <= cellSize / 1.5
                            }

                            if (clickedToken != null) {
                                viewModel.selectToken(clickedToken.id)
                                editingToken = clickedToken
                            } else if (clickedPin != null) {
                                selectedPinForDetail = clickedPin
                            } else if (uiState.selectedTokenId != null) {
                                // Move selected token to this cell
                                viewModel.moveToken(uiState.selectedTokenId!!, gridX.toFloat(), gridY.toFloat())
                                viewModel.revealCell(gridX, gridY)
                                viewModel.selectToken(null)
                            } else if (uiState.fogOfWarActive) {
                                // Reveal clicked cell
                                viewModel.revealCell(gridX, gridY)
                            }
                        }
                    )
                }
                .transformable(state = transformState)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            ) {
                val cellSize = 44.dp.toPx()
                val mapCols = activeMap.gridWidth
                val mapRows = activeMap.gridHeight
                val mapW = mapCols * cellSize
                val mapH = mapRows * cellSize

                // 1. Draw Map Background (Image or procedural stone dungeon)
                if (mapBitmap != null) {
                    drawImage(
                        image = mapBitmap,
                        dstSize = androidx.compose.ui.unit.IntSize(mapW.toInt(), mapH.toInt())
                    )
                } else {
                    // Procedural stone floor tiles
                    drawRect(color = Color(0xFF161A22), size = Size(mapW, mapH))
                    for (x in 0 until mapCols) {
                        for (y in 0 until mapRows) {
                            val tint = if ((x + y) % 2 == 0) Color(0xFF1A1F29) else Color(0xFF131720)
                            drawRect(
                                color = tint,
                                topLeft = Offset(x * cellSize, y * cellSize),
                                size = Size(cellSize - 1, cellSize - 1)
                            )
                        }
                    }
                }

                // 2. Draw Tactical Grid
                if (uiState.showGrid) {
                    for (x in 0..mapCols) {
                        drawLine(
                            color = Color(0x33E5A853),
                            start = Offset(x * cellSize, 0f),
                            end = Offset(x * cellSize, mapH),
                            strokeWidth = 1f
                        )
                    }
                    for (y in 0..mapRows) {
                        drawLine(
                            color = Color(0x33E5A853),
                            start = Offset(0f, y * cellSize),
                            end = Offset(mapW, y * cellSize),
                            strokeWidth = 1f
                        )
                    }
                }

                // 3. Draw Spell AoEs
                uiState.spells.forEach { spell ->
                    val center = Offset(spell.x * cellSize + cellSize / 2, spell.y * cellSize + cellSize / 2)
                    val radius = spell.radiusTiles * cellSize
                    drawCircle(
                        color = Color(spell.colorHex),
                        radius = radius,
                        center = center
                    )
                    drawCircle(
                        color = Color(0xFFEF4444),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // 4. Draw Fog of War (Dark veil over unrevealed cells)
                if (uiState.fogOfWarActive) {
                    for (x in 0 until mapCols) {
                        for (y in 0 until mapRows) {
                            if (!uiState.revealedCells.contains(Pair(x, y))) {
                                drawRect(
                                    color = Color(0xDD07090D),
                                    topLeft = Offset(x * cellSize, y * cellSize),
                                    size = Size(cellSize, cellSize)
                                )
                            }
                        }
                    }
                }

                // 5. Draw Map Pins (POIs)
                uiState.pins.forEach { pin ->
                    val px = pin.x * cellSize + cellSize / 2
                    val py = pin.y * cellSize + cellSize / 2
                    drawCircle(
                        color = if (pin.isRevealedToPlayers) Color(0xFF10B981) else Color(0xFFE5A853),
                        radius = cellSize * 0.35f,
                        center = Offset(px, py)
                    )
                    val textLayout = textMeasurer.measure(
                        text = pin.iconEmoji,
                        style = TextStyle(fontSize = 14.sp)
                    )
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(px - textLayout.size.width / 2, py - textLayout.size.height / 2)
                    )
                }

                // 6. Draw Tokens
                uiState.tokens.forEach { token ->
                    val isTokenSelected = token.id == uiState.selectedTokenId
                    val tx = token.x * cellSize + cellSize / 2
                    val ty = token.y * cellSize + cellSize / 2
                    val radius = cellSize * 0.42f

                    // Selection / Aura glow
                    if (isTokenSelected) {
                        drawCircle(
                            color = Color(0x66E5A853),
                            radius = radius * 1.4f,
                            center = Offset(tx, ty)
                        )
                    }

                    // Token Base Circle
                    drawCircle(
                        color = Color(token.colorHex),
                        radius = radius,
                        center = Offset(tx, ty)
                    )
                    drawCircle(
                        color = if (isTokenSelected) Color(0xFFE5A853) else Color.White,
                        radius = radius,
                        center = Offset(tx, ty),
                        style = Stroke(width = if (isTokenSelected) 3.dp.toPx() else 1.5.dp.toPx())
                    )

                    // Avatar Emoji
                    val emojiLayout = textMeasurer.measure(
                        text = token.avatarEmoji,
                        style = TextStyle(fontSize = 16.sp)
                    )
                    drawText(
                        textLayoutResult = emojiLayout,
                        topLeft = Offset(tx - emojiLayout.size.width / 2, ty - emojiLayout.size.height / 2)
                    )

                    // Health Bar under token
                    val barW = cellSize * 0.8f
                    val barH = 5.dp.toPx()
                    val barX = tx - barW / 2
                    val barY = ty + radius + 3.dp.toPx()
                    val hpRatio = if (token.maxHp > 0) (token.hp.toFloat() / token.maxHp).coerceIn(0f, 1f) else 1f

                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(barX, barY),
                        size = Size(barW, barH)
                    )
                    drawRect(
                        color = if (hpRatio > 0.5f) Color(0xFF22C55E) else if (hpRatio > 0.2f) Color(0xFFEAB308) else Color(0xFFEF4444),
                        topLeft = Offset(barX, barY),
                        size = Size(barW * hpRatio, barH)
                    )

                    // Token Name
                    val nameLayout = textMeasurer.measure(
                        text = token.name.take(12),
                        style = TextStyle(fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    )
                    drawText(
                        textLayoutResult = nameLayout,
                        topLeft = Offset(tx - nameLayout.size.width / 2, barY + barH + 1.dp.toPx())
                    )
                }
            }

            // Quick instruction overlay hint when a token is selected
            if (selectedToken != null) {
                Surface(
                    color = GrimoireCardElevated.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .border(1.dp, GrimoireGold, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "📍 ${selectedToken.name} sélectionné. Touchez une case pour déplacer.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrimoireGoldLight
                        )
                        IconButton(
                            onClick = { editingToken = selectedToken },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Éditer", tint = GrimoireGold)
                        }
                    }
                }
            }
        }
    }

    // Modals
    if (showAddTokenDialog) {
        AddTokenDialog(
            onAdd = { name, isEnemy, hp, avatar ->
                viewModel.addToken(name, isEnemy, hp, avatar)
            },
            onDismiss = { showAddTokenDialog = false }
        )
    }

    if (showSpellDialog) {
        AddSpellDialog(
            onAdd = { name, rad, shape ->
                viewModel.addSpellAoe(name, 6f, 6f, rad, shape)
            },
            onDismiss = { showSpellDialog = false }
        )
    }

    if (editingToken != null) {
        TokenEditDialog(
            token = editingToken!!,
            onHpDelta = { delta ->
                viewModel.modifyCombatantHp(editingToken!!.id, delta)
                editingToken = uiState.tokens.find { it.id == editingToken!!.id }
            },
            onToggleCondition = { cond ->
                viewModel.toggleCombatantCondition(editingToken!!.id, cond)
            },
            onRemove = {
                viewModel.removeToken(editingToken!!.id)
                editingToken = null
            },
            onDismiss = { editingToken = null }
        )
    }

    if (selectedPinForDetail != null) {
        PinDetailDialog(
            pin = selectedPinForDetail!!,
            onToggleReveal = {
                viewModel.togglePinSecret(selectedPinForDetail!!.id)
                selectedPinForDetail = uiState.pins.find { it.id == selectedPinForDetail!!.id }
            },
            onDismiss = { selectedPinForDetail = null }
        )
    }
}
