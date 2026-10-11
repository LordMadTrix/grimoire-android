package com.grimoire.app.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoire.app.data.MapPin
import com.grimoire.app.data.MapScene
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
    var selectedPinIdForDetail by remember { mutableStateOf<String?>(null) }
    val selectedPinForDetail = uiState.pins.find { it.id == selectedPinIdForDetail }
    var editingTokenId by remember { mutableStateOf<String?>(null) }
    val editingToken = uiState.tokens.find { it.id == editingTokenId }

    // Map Library & Hover Preview state
    var showLibraryPanel by remember { mutableStateOf(true) }
    var hoveredMapScene by remember { mutableStateOf<MapScene?>(null) }

    // Map zooming and panning
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 3.5f)
        offset += panChange
    }

    // Load active map bitmap
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
        // Map Library Header & Collapsible Panel
        Surface(
            color = GrimoireCard,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                // Header with Library Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.clickable { showLibraryPanel = !showLibraryPanel }
                    ) {
                        Text(
                            text = "🗺️ BIBLIOTHÈQUE DE CARTES",
                            style = MaterialTheme.typography.titleSmall,
                            color = GrimoireGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Surface(
                            color = GrimoireGold.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${uiState.maps.size} cartes",
                                fontSize = 10.sp,
                                color = GrimoireGoldLight,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (showLibraryPanel) "Masquer miniatures" else "Afficher miniatures",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        IconButton(
                            onClick = { showLibraryPanel = !showLibraryPanel },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (showLibraryPanel) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Basculer panneau bibliothèque",
                                tint = GrimoireGold
                            )
                        }
                    }
                }

                // Map Thumbnails Row with Hover Preview
                AnimatedVisibility(
                    visible = showLibraryPanel,
                    enter = expandVertically(animationSpec = tween(200)) + fadeIn(),
                    exit = shrinkVertically(animationSpec = tween(150)) + fadeOut()
                ) {
                    Column(modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)) {
                        Text(
                            text = "💡 Survolez une miniature (ou maintenez l'appui) pour un aperçu agrandi instantané sans cliquer.",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(uiState.maps) { scene ->
                                val isSelected = scene.id == activeMap.id
                                val isHovered = hoveredMapScene?.id == scene.id

                                MapThumbnailCard(
                                    scene = scene,
                                    isSelected = isSelected,
                                    isHovered = isHovered,
                                    onSelect = {
                                        viewModel.switchMapScene(scene.id)
                                        hoveredMapScene = null
                                    },
                                    onHover = { hoverScene ->
                                        hoveredMapScene = hoverScene
                                    }
                                )
                            }
                        }
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

        // Tactical Canvas Area & Hover Preview Overlay
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(0.dp))
                .background(Color(0xFF0F1219))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            // Dismiss hover preview if tapping anywhere on canvas
                            hoveredMapScene = null

                            val cellSize = 44f * density
                            val canvasX = (tapOffset.x - offset.x) / scale
                            val canvasY = (tapOffset.y - offset.y) / scale
                            val gridX = (canvasX / cellSize).toInt()
                            val gridY = (canvasY / cellSize).toInt()

                            val clickedToken = uiState.tokens.find { t ->
                                val px = t.x * cellSize + cellSize / 2
                                val py = t.y * cellSize + cellSize / 2
                                val dist = kotlin.math.hypot(canvasX - px, canvasY - py)
                                dist <= cellSize / 1.5
                            }

                            val clickedPin = uiState.pins.find { p ->
                                val px = p.x * cellSize + cellSize / 2
                                val py = p.y * cellSize + cellSize / 2
                                val dist = kotlin.math.hypot(canvasX - px, canvasY - py)
                                dist <= cellSize / 1.5
                            }

                            if (clickedToken != null) {
                                viewModel.selectToken(clickedToken.id)
                                editingTokenId = clickedToken.id
                            } else if (clickedPin != null) {
                                selectedPinIdForDetail = clickedPin.id
                            } else if (uiState.selectedTokenId != null) {
                                viewModel.moveToken(uiState.selectedTokenId!!, gridX.toFloat(), gridY.toFloat())
                                viewModel.revealCell(gridX, gridY)
                                viewModel.selectToken(null)
                            } else if (uiState.fogOfWarActive) {
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

                // 2. Tactical Grid
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

                // 3. Spell AoEs
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

                // 4. Fog of War
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

                // 5. Map Pins
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

                // 6. Tokens
                uiState.tokens.forEach { token ->
                    val isTokenSelected = token.id == uiState.selectedTokenId
                    val tx = token.x * cellSize + cellSize / 2
                    val ty = token.y * cellSize + cellSize / 2
                    val radius = cellSize * 0.42f

                    if (isTokenSelected) {
                        drawCircle(
                            color = Color(0x66E5A853),
                            radius = radius * 1.4f,
                            center = Offset(tx, ty)
                        )
                    }

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

                    val emojiLayout = textMeasurer.measure(
                        text = token.avatarEmoji,
                        style = TextStyle(fontSize = 16.sp)
                    )
                    drawText(
                        textLayoutResult = emojiLayout,
                        topLeft = Offset(tx - emojiLayout.size.width / 2, ty - emojiLayout.size.height / 2)
                    )

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

            // Floating Hover Preview Overlay (Displays without clicking!)
            androidx.compose.animation.AnimatedVisibility(
                visible = hoveredMapScene != null,
                enter = fadeIn(animationSpec = tween(180)) + scaleIn(initialScale = 0.93f, animationSpec = tween(180)),
                exit = fadeOut(animationSpec = tween(140)) + scaleOut(targetScale = 0.93f, animationSpec = tween(140)),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                if (hoveredMapScene != null) {
                    MapHoverPreviewCard(
                        scene = hoveredMapScene!!,
                        isCurrentlyActive = hoveredMapScene!!.id == activeMap.id,
                        onLoad = {
                            viewModel.switchMapScene(hoveredMapScene!!.id)
                            hoveredMapScene = null
                        },
                        onDismiss = { hoveredMapScene = null }
                    )
                }
            }

            // Quick instruction overlay hint when a token is selected
            if (selectedToken != null && hoveredMapScene == null) {
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
            token = editingToken,
            onHpDelta = { delta ->
                viewModel.modifyCombatantHp(editingToken.id, delta)
            },
            onToggleCondition = { cond ->
                viewModel.toggleCombatantCondition(editingToken.id, cond)
            },
            onRemove = {
                viewModel.removeToken(editingToken.id)
                editingTokenId = null
            },
            onDismiss = { editingTokenId = null }
        )
    }

    if (selectedPinForDetail != null) {
        PinDetailDialog(
            pin = selectedPinForDetail,
            onToggleReveal = {
                viewModel.togglePinSecret(selectedPinForDetail.id)
            },
            onDismiss = { selectedPinIdForDetail = null }
        )
    }
}

/**
 * Thumbnail Card in the Map Library panel.
 * Supports mouse cursor hover (Enter / Exit) as well as touch hold / press
 * and a direct preview eye icon.
 */
@Composable
fun MapThumbnailCard(
    scene: MapScene,
    isSelected: Boolean,
    isHovered: Boolean,
    onSelect: () -> Unit,
    onHover: (MapScene?) -> Unit
) {
    val context = LocalContext.current
    val thumbnailBitmap = remember(scene.assetPath) {
        if (scene.assetPath.isNotBlank()) {
            try {
                context.assets.open(scene.assetPath).use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            } catch (_: Exception) {
                null
            }
        } else null
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GrimoireCardElevated),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                when {
                    isHovered -> GrimoirePurple
                    isSelected -> GrimoireGold
                    else -> GrimoireBorder
                }
            )
        ),
        modifier = Modifier
            .width(165.dp)
            .height(115.dp)
            // Desktop/Mouse Pointer Hover Detection
            .pointerInput(scene.id) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Enter -> onHover(scene)
                            PointerEventType.Exit -> onHover(null)
                            PointerEventType.Move -> onHover(scene)
                        }
                    }
                }
            }
            // Touch Press / Long Press / Tap Detection
            .pointerInput(scene.id) {
                detectTapGestures(
                    onPress = {
                        onHover(scene)
                        tryAwaitRelease()
                        onHover(null)
                    },
                    onTap = {
                        onSelect()
                    }
                )
            }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Map Visual Thumbnail
            if (thumbnailBitmap != null) {
                Image(
                    bitmap = thumbnailBitmap,
                    contentDescription = scene.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Procedural stylized tactical dungeon miniature
                Canvas(modifier = Modifier.fillMaxSize().background(Color(0xFF141822))) {
                    val tile = 16.dp.toPx()
                    for (x in 0..12) {
                        for (y in 0..8) {
                            val c = if ((x + y) % 2 == 0) Color(0xFF1E2330) else Color(0xFF171B26)
                            drawRect(c, topLeft = Offset(x * tile, y * tile), size = Size(tile - 1, tile - 1))
                        }
                    }
                }
            }

            // Gradient shade for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0x990A0C10), Color(0xF00A0C10))
                        )
                    )
            )

            // Top Status Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelected) {
                    Surface(
                        color = GrimoireGold,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "ACTIF",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = GrimoireBg,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                } else {
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${scene.gridWidth}×${scene.gridHeight}",
                            fontSize = 9.sp,
                            color = GrimoireGoldLight,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                // Explicit Preview Eye button for touch devices
                IconButton(
                    onClick = { onHover(if (isHovered) null else scene) },
                    modifier = Modifier.size(24.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Aperçu de la carte",
                        tint = if (isHovered) GrimoirePurple else Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Bottom Label Info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = scene.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    maxLines = 1
                )
                Text(
                    text = scene.environment,
                    color = GrimoireCyan,
                    fontSize = 9.sp,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Floating Hover Preview Card:
 * Shows a larger, high-fidelity preview of the battlemap with metadata,
 * narrative description, and quick load button without requiring clicking.
 */
@Composable
fun MapHoverPreviewCard(
    scene: MapScene,
    isCurrentlyActive: Boolean,
    onLoad: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val previewBitmap = remember(scene.assetPath) {
        if (scene.assetPath.isNotBlank()) {
            try {
                context.assets.open(scene.assetPath).use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            } catch (_: Exception) {
                null
            }
        } else null
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GrimoireCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(GrimoireGold)
        ),
        modifier = Modifier
            .widthIn(max = 380.dp)
            .shadow(24.dp, shape = RoundedCornerShape(16.dp), ambientColor = GrimoireGold, spotColor = GrimoirePurple)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Preview Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("👁️", fontSize = 16.sp)
                    Text(
                        text = "APERÇU DE LA CARTE",
                        style = MaterialTheme.typography.labelSmall,
                        color = GrimoireGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer l'aperçu",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Large Map Graphic Preview Frame (with subtle tactical grid overlay)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF10131B))
                    .border(1.dp, GrimoireBorder, RoundedCornerShape(12.dp))
            ) {
                if (previewBitmap != null) {
                    Image(
                        bitmap = previewBitmap,
                        contentDescription = scene.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val tile = 24.dp.toPx()
                        val cols = (size.width / tile).toInt()
                        val rows = (size.height / tile).toInt()
                        for (x in 0..cols) {
                            for (y in 0..rows) {
                                val c = if ((x + y) % 2 == 0) Color(0xFF1E2330) else Color(0xFF151924)
                                drawRect(c, topLeft = Offset(x * tile, y * tile), size = Size(tile - 1, tile - 1))
                            }
                        }
                    }
                }

                // Subtle grid lines overlay on the preview image
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val step = 28.dp.toPx()
                    val cols = (size.width / step).toInt()
                    val rows = (size.height / step).toInt()
                    for (x in 0..cols) {
                        drawLine(Color(0x33E5A853), Offset(x * step, 0f), Offset(x * step, size.height), strokeWidth = 1f)
                    }
                    for (y in 0..rows) {
                        drawLine(Color(0x33E5A853), Offset(0f, y * step), Offset(size.width, y * step), strokeWidth = 1f)
                    }
                }

                // Status tag
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(bottomStart = 8.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "Échelle : ${scene.cellSizeDp.toInt()}dp / case",
                        fontSize = 10.sp,
                        color = GrimoireGoldLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Title & Badges
            Text(
                text = scene.name,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GrimoireGold
            )

            Spacer(Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    color = GrimoirePurple.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "📍 ${scene.environment}",
                        color = GrimoirePurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = GrimoireCyan.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "📐 ${scene.gridWidth} × ${scene.gridHeight} cases",
                        color = GrimoireCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = GrimoireGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "⚔️ ${scene.recommendedLevel}",
                        color = GrimoireGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Description
            if (scene.description.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = scene.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            // Load Button
            Button(
                onClick = onLoad,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrentlyActive) GrimoireCardElevated else GrimoireGold,
                    contentColor = if (isCurrentlyActive) GrimoireGold else GrimoireBg
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                Icon(
                    imageVector = if (isCurrentlyActive) Icons.Default.Check else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (isCurrentlyActive) "Carte déjà active sur la table" else "Charger sur la table virtuelle",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
