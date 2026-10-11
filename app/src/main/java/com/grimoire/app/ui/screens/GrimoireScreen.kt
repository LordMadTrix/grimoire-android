package com.grimoire.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.grimoire.app.data.CampaignNote
import com.grimoire.app.data.NoteCategory
import com.grimoire.app.ui.theme.*
import com.grimoire.app.viewmodel.GrimoireViewModel

@Composable
fun GrimoireScreen(
    viewModel: GrimoireViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var isEditingNote by remember { mutableStateOf(false) }
    var showNewNoteDialog by remember { mutableStateOf(false) }

    // Filter notes
    val filteredNotes = uiState.notes.filter { note ->
        val matchesCategory = uiState.selectedCategory == null || note.category == uiState.selectedCategory
        val matchesSearch = uiState.noteSearchQuery.isBlank() ||
                note.title.contains(uiState.noteSearchQuery, ignoreCase = true) ||
                note.content.contains(uiState.noteSearchQuery, ignoreCase = true) ||
                note.tags.any { it.contains(uiState.noteSearchQuery, ignoreCase = true) }
        matchesCategory && matchesSearch
    }

    val selectedNote = uiState.notes.find { it.id == uiState.selectedNoteId } ?: filteredNotes.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GrimoireBg)
            .padding(16.dp)
    ) {
        // Search & Category Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = uiState.noteSearchQuery,
                onValueChange = { viewModel.setNoteSearch(it) },
                placeholder = { Text("Rechercher dans le Grimoire...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Recherche", tint = TextMuted) },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GrimoireGold,
                    unfocusedBorderColor = GrimoireBorder
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            FloatingActionButton(
                onClick = { showNewNoteDialog = true },
                containerColor = GrimoireGold,
                contentColor = GrimoireBg,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nouvelle note")
            }
        }

        Spacer(Modifier.height(10.dp))

        // Category Filter Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedCategory == null,
                    onClick = { viewModel.setNoteCategory(null) },
                    label = { Text("Tous") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GrimoireGold.copy(alpha = 0.25f),
                        selectedLabelColor = GrimoireGoldLight
                    )
                )
            }
            items(NoteCategory.entries.toTypedArray()) { cat ->
                val isSelected = uiState.selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setNoteCategory(if (isSelected) null else cat) },
                    label = { Text("${cat.emoji} ${cat.label}") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GrimoireGold.copy(alpha = 0.25f),
                        selectedLabelColor = GrimoireGoldLight
                    )
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Content Area: Note Browser & Selected Note Detail View
        if (selectedNote != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = GrimoireCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GrimoireBorder)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                    // Note selector drawer / title header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(selectedNote.category.emoji, fontSize = 22.sp)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = selectedNote.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = GrimoireGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${selectedNote.category.label} • ${selectedNote.lastUpdated}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                        }

                        Row {
                            IconButton(onClick = { isEditingNote = !isEditingNote }) {
                                Icon(
                                    imageVector = if (isEditingNote) Icons.Default.Visibility else Icons.Default.Edit,
                                    contentDescription = "Éditer",
                                    tint = GrimoireGold
                                )
                            }
                            IconButton(onClick = { viewModel.deleteCurrentNote() }) {
                                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = GrimoireRed)
                            }
                        }
                    }

                    // Tags
                    if (selectedNote.tags.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            selectedNote.tags.forEach { tag ->
                                Surface(
                                    color = GrimoireCardElevated,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "#$tag",
                                        color = GrimoirePurple,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Divider(color = GrimoireBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // Content View or Edit View
                    if (isEditingNote) {
                        var editContent by remember(selectedNote.id) { mutableStateOf(selectedNote.content) }
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = editContent,
                                onValueChange = { editContent = it },
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GrimoireGold,
                                    unfocusedBorderColor = GrimoireBorder
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.saveNote(selectedNote.title, selectedNote.category, editContent, selectedNote.tags)
                                    isEditingNote = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GrimoireGold, contentColor = GrimoireBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Enregistrer les modifications")
                            }
                        }
                    } else {
                        // Render rich markdown
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                RenderMarkdownContent(
                                    content = selectedNote.content,
                                    onInlineDiceRoll = { formula ->
                                        // Parse and roll inline dice tag like "1d20+4"
                                        val parts = formula.lowercase().replace(" ", "").split("+", "-")
                                        val dicePart = parts.firstOrNull() ?: "1d20"
                                        val dIdx = dicePart.indexOf('d')
                                        val count = if (dIdx > 0) dicePart.substring(0, dIdx).toIntOrNull() ?: 1 else 1
                                        val sides = if (dIdx >= 0) dicePart.substring(dIdx + 1).toIntOrNull() ?: 20 else 20
                                        val mod = if (formula.contains("+")) formula.substringAfter("+").trim().toIntOrNull() ?: 0 else 0
                                        viewModel.rollDice(count, sides, mod)
                                        viewModel.showToast("Dé en ligne lancé : $formula")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucune note trouvée. Créez-en une nouvelle !", color = TextMuted)
            }
        }
    }

    // New Note Modal
    if (showNewNoteDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newCat by remember { mutableStateOf(NoteCategory.QUESTS) }
        var newContent by remember { mutableStateOf("# Titre\n\n> [!NOTE]\n> Note d'introduction...\n\n- [ ] Objectif 1\n- [ ] Objectif 2") }

        AlertDialog(
            onDismissRequest = { showNewNoteDialog = false },
            containerColor = GrimoireCard,
            title = { Text("Créer une Nouvelle Note", color = GrimoireGold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Titre de la note") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Catégorie :", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(NoteCategory.entries.toTypedArray()) { cat ->
                            FilterChip(
                                selected = newCat == cat,
                                onClick = { newCat = cat },
                                label = { Text("${cat.emoji} ${cat.label}") }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = newContent,
                        onValueChange = { newContent = it },
                        label = { Text("Contenu Markdown") },
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.saveNote(newTitle, newCat, newContent, listOf("Campagne"))
                            showNewNoteDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GrimoireGold, contentColor = GrimoireBg)
                ) {
                    Text("Créer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewNoteDialog = false }) { Text("Annuler", color = TextMuted) }
            }
        )
    }
}

@Composable
fun RenderMarkdownContent(
    content: String,
    onInlineDiceRoll: (String) -> Unit
) {
    val lines = content.lines()
    var inCallout = false
    var calloutType = "NOTE"
    val calloutLines = mutableListOf<String>()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        lines.forEach { line ->
            when {
                line.startsWith("> [!") -> {
                    inCallout = true
                    calloutType = line.substringAfter("> [!").substringBefore("]")
                    calloutLines.clear()
                }
                inCallout && line.startsWith(">") -> {
                    calloutLines.add(line.removePrefix(">").trim())
                }
                inCallout && !line.startsWith(">") -> {
                    inCallout = false
                    CalloutBox(type = calloutType, lines = calloutLines, onDiceRoll = onInlineDiceRoll)
                    calloutLines.clear()
                    RenderSingleLine(line, onInlineDiceRoll)
                }
                else -> {
                    RenderSingleLine(line, onInlineDiceRoll)
                }
            }
        }
        if (inCallout) {
            CalloutBox(type = calloutType, lines = calloutLines, onDiceRoll = onInlineDiceRoll)
        }
    }
}

@Composable
fun CalloutBox(
    type: String,
    lines: List<String>,
    onDiceRoll: (String) -> Unit
) {
    val (color, icon) = when (type.uppercase()) {
        "WARNING" -> Pair(GrimoireRed, "⚠️ ATTENTION")
        "TIP" -> Pair(GrimoireGreen, "💡 ASTUCE")
        else -> Pair(GrimoirePurple, "ℹ️ NOTE MJ")
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(icon, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            lines.forEach { l ->
                RenderSingleLine(l, onDiceRoll)
            }
        }
    }
}

@Composable
fun RenderSingleLine(
    line: String,
    onDiceRoll: (String) -> Unit
) {
    when {
        line.startsWith("# ") -> {
            Text(
                text = line.removePrefix("# "),
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GrimoireGold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        line.startsWith("## ") -> {
            Text(
                text = line.removePrefix("## "),
                fontFamily = FontFamily.Serif,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = GrimoireGoldLight,
                modifier = Modifier.padding(vertical = 3.dp)
            )
        }
        line.startsWith("### ") -> {
            Text(
                text = line.removePrefix("### "),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = GrimoireCyan,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
        line.startsWith("- [ ] ") -> {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                Text("☐", color = GrimoireGold, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                RenderLineWithInlineDice(line.removePrefix("- [ ] "), onDiceRoll)
            }
        }
        line.startsWith("- [x] ") -> {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                Text("☑", color = GrimoireGreen, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                RenderLineWithInlineDice(line.removePrefix("- [x] "), onDiceRoll)
            }
        }
        line.startsWith("- ") -> {
            Row(modifier = Modifier.padding(vertical = 1.dp)) {
                Text("• ", color = GrimoireGold, fontWeight = FontWeight.Bold)
                RenderLineWithInlineDice(line.removePrefix("- "), onDiceRoll)
            }
        }
        line.isBlank() -> {
            Spacer(Modifier.height(4.dp))
        }
        else -> {
            RenderLineWithInlineDice(line, onDiceRoll)
        }
    }
}

@Composable
fun RenderLineWithInlineDice(
    text: String,
    onDiceRoll: (String) -> Unit
) {
    // If line has [[1d20+4]] syntax, split and render clickable dice badge
    if (text.contains("[[") && text.contains("]]")) {
        val parts = text.split("[[")
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 1.dp)) {
            parts.forEachIndexed { idx, part ->
                if (idx == 0) {
                    Text(part, color = TextPrimary, fontSize = 13.sp)
                } else {
                    val formula = part.substringBefore("]]")
                    val rest = part.substringAfter("]]")
                    Surface(
                        color = GrimoirePurple.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .clickable { onDiceRoll(formula) }
                            .padding(horizontal = 2.dp)
                    ) {
                        Text(
                            text = "🎲 $formula",
                            color = GrimoireGoldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(rest, color = TextPrimary, fontSize = 13.sp)
                }
            }
        }
    } else {
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )
    }
}
