package com.grimoire.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimoire.app.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

enum class AppDestination(val label: String, val icon: String) {
    VTT("Table Virtuelle", "🗺️"),
    COMBAT("Initiative", "⚔️"),
    GRIMOIRE("Grimoire & Lore", "📜"),
    HERBORISTE("Compendium", "🌿"),
    TOOLKIT("Boîte à Outils", "🎲"),
    SOUNDSCAPE("Ambiances", "🌧️"),
    PLAYER_HUB("Hub Joueurs", "👥")
}

data class GrimoireUiState(
    val currentDestination: AppDestination = AppDestination.VTT,
    // VTT
    val maps: List<MapScene> = SampleCampaignData.defaultScenes,
    val activeMap: MapScene = SampleCampaignData.defaultScenes.first(),
    val tokens: List<VttToken> = SampleCampaignData.defaultTokens,
    val selectedTokenId: String? = null,
    val pins: List<MapPin> = SampleCampaignData.defaultPins,
    val spells: List<SpellAoe> = emptyList(),
    val showGrid: Boolean = true,
    val fogOfWarActive: Boolean = true,
    val revealedCells: Set<Pair<Int, Int>> = setOf(
        Pair(2, 6), Pair(2, 7), Pair(3, 7), Pair(3, 8), Pair(4, 8),
        Pair(3, 9), Pair(4, 9), Pair(5, 8), Pair(5, 9), Pair(6, 8)
    ),
    // Combat
    val combatants: List<Combatant> = SampleCampaignData.defaultTokens.mapIndexed { index, t ->
        Combatant(
            id = t.id,
            name = t.name,
            initiative = 15 - index * 2,
            hp = t.hp,
            maxHp = t.maxHp,
            isEnemy = t.isEnemy,
            ac = t.ac,
            avatarEmoji = t.avatarEmoji
        )
    }.sortedByDescending { it.initiative },
    val currentTurnIndex: Int = 0,
    val currentRound: Int = 1,
    val isCombatActive: Boolean = true,
    val combatLogs: List<CombatLogEntry> = listOf(
        CombatLogEntry("log1", "12:00", "Combat engagé au Sanctuaire Oublié !", LogType.INFO)
    ),
    // Grimoire Notes
    val notes: List<CampaignNote> = SampleCampaignData.defaultNotes,
    val selectedNoteId: String? = SampleCampaignData.defaultNotes.firstOrNull()?.id,
    val noteSearchQuery: String = "",
    val selectedCategory: NoteCategory? = null,
    // Compendium
    val herbs: List<HerbItem> = CompendiumData.herbs,
    val bestiary: List<BestiaryMonster> = CompendiumData.bestiary,
    val compendiumTab: String = "Plantes", // "Plantes", "Bestiaire"
    val compendiumBiomeFilter: String = "Tous",
    val compendiumSearchQuery: String = "",
    // Toolkit
    val diceHistory: List<DiceRollResult> = emptyList(),
    val lastDiceRoll: DiceRollResult? = null,
    val isRollingDice: Boolean = false,
    val rollTables: List<RollTable> = RollTablesData.tables,
    val selectedRollTable: RollTable? = RollTablesData.tables.first(),
    val lastTableResult: String? = null,
    val generatedNpc: RandomNpc? = null,
    val generatedEncounter: RandomEncounter? = null,
    // Soundscape
    val ambientTracks: List<AmbientTrack> = listOf(
        AmbientTrack("rain", "Pluie battante", "🌧️", active = true, volume = 0.6f, synthType = SynthType.RAIN),
        AmbientTrack("fire", "Feu crépitant", "🔥", active = true, volume = 0.5f, synthType = SynthType.FIRE),
        AmbientTrack("dungeon", "Résonance du Donjon", "🕯️", active = false, volume = 0.5f, synthType = SynthType.DRONE),
        AmbientTrack("wind", "Bourrasque des plaines", "💨", active = false, volume = 0.4f, synthType = SynthType.WIND)
    ),
    val masterVolume: Float = 0.7f,
    val isMuted: Boolean = false,
    val isSoundscapePlaying: Boolean = false,
    // Player Hub
    val players: List<PlayerCharacter> = SampleCampaignData.defaultPlayers,
    val handouts: List<SecretHandout> = SampleCampaignData.defaultHandouts,
    // Notification Toast
    val toastMessage: String? = null
)

class GrimoireViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GrimoireUiState())
    val uiState: StateFlow<GrimoireUiState> = _uiState.asStateFlow()

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun navigateTo(dest: AppDestination) {
        _uiState.update { it.copy(currentDestination = dest) }
    }

    fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
        viewModelScope.launch {
            delay(3000)
            _uiState.update { if (it.toastMessage == msg) it.copy(toastMessage = null) else it }
        }
    }

    // --- VTT Actions ---

    fun switchMapScene(sceneId: String) {
        val scene = _uiState.value.maps.find { it.id == sceneId } ?: return
        _uiState.update { it.copy(activeMap = scene) }
        showToast("Carte changée : ${scene.name}")
    }

    fun selectToken(id: String?) {
        _uiState.update { it.copy(selectedTokenId = id) }
    }

    fun moveToken(id: String, newX: Float, newY: Float) {
        val clampedX = newX.coerceIn(0f, _uiState.value.activeMap.gridWidth.toFloat() - 1f)
        val clampedY = newY.coerceIn(0f, _uiState.value.activeMap.gridHeight.toFloat() - 1f)
        _uiState.update { state ->
            val updated = state.tokens.map { token ->
                if (token.id == id) token.copy(x = clampedX, y = clampedY) else token
            }
            state.copy(tokens = updated)
        }
    }

    fun addToken(name: String, isEnemy: Boolean, hp: Int = 10, avatar: String = if (isEnemy) "👹" else "⚔️") {
        val newToken = VttToken(
            id = "tok_" + UUID.randomUUID().toString().take(6),
            name = name,
            x = Random.nextInt(2, 10).toFloat(),
            y = Random.nextInt(2, 10).toFloat(),
            hp = hp,
            maxHp = hp,
            isEnemy = isEnemy,
            colorHex = if (isEnemy) 0xFFDC2626 else 0xFF2563EB,
            avatarEmoji = avatar
        )
        _uiState.update { state ->
            state.copy(
                tokens = state.tokens + newToken,
                combatants = (state.combatants + Combatant(
                    id = newToken.id,
                    name = newToken.name,
                    initiative = Random.nextInt(1, 21),
                    hp = newToken.hp,
                    maxHp = newToken.maxHp,
                    isEnemy = newToken.isEnemy,
                    avatarEmoji = newToken.avatarEmoji
                )).sortedByDescending { it.initiative }
            )
        }
        showToast("Pion ajouté : $name")
    }

    fun removeToken(id: String) {
        _uiState.update { state ->
            val updatedTokens = state.tokens.filterNot { it.id == id }
            val updatedCombatants = state.combatants.filterNot { it.id == id }
            val newTurnIndex = if (updatedCombatants.isEmpty()) 0 else state.currentTurnIndex.coerceIn(0, updatedCombatants.size - 1)
            state.copy(
                tokens = updatedTokens,
                combatants = updatedCombatants,
                currentTurnIndex = newTurnIndex,
                selectedTokenId = if (state.selectedTokenId == id) null else state.selectedTokenId
            )
        }
    }

    fun toggleGrid() {
        _uiState.update { it.copy(showGrid = !it.showGrid) }
    }

    fun toggleFog() {
        _uiState.update { it.copy(fogOfWarActive = !it.fogOfWarActive) }
        showToast(if (_uiState.value.fogOfWarActive) "Brouillard de guerre activé" else "Brouillard désactivé")
    }

    fun revealCell(x: Int, y: Int) {
        _uiState.update { state ->
            val newCells = state.revealedCells.toMutableSet()
            for (dx in -1..1) {
                for (dy in -1..1) {
                    newCells.add(Pair(x + dx, y + dy))
                }
            }
            state.copy(revealedCells = newCells)
        }
    }

    fun revealAllFog() {
        val map = _uiState.value.activeMap
        val all = mutableSetOf<Pair<Int, Int>>()
        for (x in 0 until map.gridWidth) {
            for (y in 0 until map.gridHeight) {
                all.add(Pair(x, y))
            }
        }
        _uiState.update { it.copy(revealedCells = all) }
        showToast("Carte entièrement révélée aux joueurs !")
    }

    fun resetFog() {
        _uiState.update { it.copy(revealedCells = emptySet()) }
        showToast("Brouillard réinitialisé")
    }

    fun togglePinSecret(pinId: String) {
        _uiState.update { state ->
            val updated = state.pins.map { pin ->
                if (pin.id == pinId) pin.copy(isRevealedToPlayers = !pin.isRevealedToPlayers) else pin
            }
            state.copy(pins = updated)
        }
    }

    fun addSpellAoe(name: String, x: Float, y: Float, radius: Float, shape: SpellShape) {
        val spell = SpellAoe(
            id = UUID.randomUUID().toString(),
            name = name,
            x = x,
            y = y,
            radiusTiles = radius,
            shape = shape
        )
        _uiState.update { it.copy(spells = it.spells + spell) }
        AudioSynthEngine.playSfx("magic")
        showToast("Gabarit placé : $name")
    }

    fun clearSpells() {
        _uiState.update { it.copy(spells = emptyList()) }
    }

    // --- Combat Actions ---

    fun nextTurn() {
        val count = _uiState.value.combatants.size
        if (count == 0) return
        val nextIdx = (_uiState.value.currentTurnIndex + 1) % count
        val newRound = if (nextIdx == 0) _uiState.value.currentRound + 1 else _uiState.value.currentRound
        val nextName = _uiState.value.combatants[nextIdx].name
        _uiState.update {
            it.copy(
                currentTurnIndex = nextIdx,
                currentRound = newRound,
                combatLogs = listOf(
                    CombatLogEntry(UUID.randomUUID().toString(), timeFormat.format(Date()), "Tour de $nextName (Round $newRound)", LogType.INFO)
                ) + it.combatLogs
            )
        }
    }

    fun prevTurn() {
        val count = _uiState.value.combatants.size
        if (count == 0) return
        val prevIdx = if (_uiState.value.currentTurnIndex - 1 < 0) count - 1 else _uiState.value.currentTurnIndex - 1
        _uiState.update { it.copy(currentTurnIndex = prevIdx) }
    }

    fun rollAllInitiatives(type: String = "d20") {
        AudioSynthEngine.playSfx("dice")
        _uiState.update { state ->
            val updated = state.combatants.map { c ->
                val roll = if (type == "wfrp") Random.nextInt(1, 11) + 3 else Random.nextInt(1, 21)
                c.copy(initiative = roll)
            }.sortedByDescending { it.initiative }

            val log = CombatLogEntry(
                UUID.randomUUID().toString(),
                timeFormat.format(Date()),
                "Initiatives relancées ($type) !",
                LogType.INITIATIVE
            )
            state.copy(
                combatants = updated,
                currentTurnIndex = 0,
                combatLogs = listOf(log) + state.combatLogs
            )
        }
        showToast("Initiatives tirées pour tous les combattants")
    }

    fun modifyCombatantHp(id: String, delta: Int) {
        _uiState.update { state ->
            var targetName = ""
            var newHpVal = 0
            val updated = state.combatants.map { c ->
                if (c.id == id) {
                    val hp = (c.hp + delta).coerceIn(0, c.maxHp + 20)
                    targetName = c.name
                    newHpVal = hp
                    c.copy(hp = hp)
                } else c
            }
            // Also sync token HP
            val updatedTokens = state.tokens.map { t ->
                if (t.id == id) t.copy(hp = newHpVal) else t
            }

            val logType = if (delta < 0) LogType.DAMAGE else LogType.HEAL
            val logMsg = if (delta < 0) "$targetName subit ${-delta} dégâts (PV: $newHpVal)" else "$targetName récupère $delta PV (PV: $newHpVal)"
            if (delta < 0) AudioSynthEngine.playSfx("sword") else AudioSynthEngine.playSfx("bell")

            state.copy(
                combatants = updated,
                tokens = updatedTokens,
                combatLogs = listOf(CombatLogEntry(UUID.randomUUID().toString(), timeFormat.format(Date()), logMsg, logType)) + state.combatLogs
            )
        }
    }

    fun toggleCombatantCondition(id: String, condition: String) {
        _uiState.update { state ->
            val updated = state.combatants.map { c ->
                if (c.id == id) {
                    val newConds = if (c.conditions.contains(condition)) c.conditions - condition else c.conditions + condition
                    c.copy(conditions = newConds)
                } else c
            }
            state.copy(combatants = updated)
        }
    }

    fun addCombatant(name: String, init: Int, hp: Int, isEnemy: Boolean) {
        val c = Combatant(
            id = UUID.randomUUID().toString(),
            name = name,
            initiative = init,
            hp = hp,
            maxHp = hp,
            isEnemy = isEnemy,
            avatarEmoji = if (isEnemy) "👹" else "⚔️"
        )
        _uiState.update {
            it.copy(combatants = (it.combatants + c).sortedByDescending { it.initiative })
        }
        showToast("Combattant ajouté : $name")
    }

    fun removeCombatant(id: String) {
        _uiState.update { state ->
            val updated = state.combatants.filterNot { it.id == id }
            val newTurnIndex = if (updated.isEmpty()) 0 else state.currentTurnIndex.coerceIn(0, updated.size - 1)
            state.copy(
                combatants = updated,
                currentTurnIndex = newTurnIndex
            )
        }
    }

    // --- Grimoire Notes Actions ---

    fun selectNote(id: String?) {
        _uiState.update { it.copy(selectedNoteId = id) }
    }

    fun setNoteSearch(query: String) {
        _uiState.update { it.copy(noteSearchQuery = query) }
    }

    fun setNoteCategory(cat: NoteCategory?) {
        _uiState.update { it.copy(selectedCategory = cat) }
    }

    fun saveNote(title: String, category: NoteCategory, content: String, tags: List<String>) {
        val existingId = _uiState.value.selectedNoteId
        _uiState.update { state ->
            if (existingId != null && state.notes.any { it.id == existingId }) {
                val updated = state.notes.map { n ->
                    if (n.id == existingId) n.copy(title = title, category = category, content = content, tags = tags, lastUpdated = "À l'instant") else n
                }
                state.copy(notes = updated)
            } else {
                val newNote = CampaignNote(
                    id = UUID.randomUUID().toString(),
                    title = title.ifBlank { "Nouvelle Note" },
                    category = category,
                    content = content,
                    tags = tags,
                    lastUpdated = "À l'instant"
                )
                state.copy(notes = listOf(newNote) + state.notes, selectedNoteId = newNote.id)
            }
        }
        showToast("Note enregistrée dans le Grimoire")
    }

    fun deleteCurrentNote() {
        val currentId = _uiState.value.selectedNoteId ?: return
        _uiState.update { state ->
            val filtered = state.notes.filterNot { it.id == currentId }
            state.copy(notes = filtered, selectedNoteId = filtered.firstOrNull()?.id)
        }
        showToast("Note supprimée")
    }

    // --- Compendium Actions ---

    fun setCompendiumTab(tab: String) {
        _uiState.update { it.copy(compendiumTab = tab) }
    }

    fun setCompendiumBiome(biome: String) {
        _uiState.update { it.copy(compendiumBiomeFilter = biome) }
    }

    fun setCompendiumSearch(q: String) {
        _uiState.update { it.copy(compendiumSearchQuery = q) }
    }

    // --- Toolkit & Dice Actions ---

    fun rollDice(count: Int, sides: Int, modifier: Int = 0) {
        if (_uiState.value.isRollingDice) return
        _uiState.update { it.copy(isRollingDice = true) }
        AudioSynthEngine.playSfx("dice")

        viewModelScope.launch {
            delay(350)
            val rolls = List(count) { Random.nextInt(1, sides + 1) }
            val sum = rolls.sum() + modifier
            val isCritSuccess = sides == 20 && count == 1 && rolls.first() == 20
            val isCritFail = sides == 20 && count == 1 && rolls.first() == 1
            val formulaStr = "${count}d$sides" + (if (modifier > 0) "+$modifier" else if (modifier < 0) "$modifier" else "")
            val result = DiceRollResult(
                formula = formulaStr,
                rolls = rolls,
                modifier = modifier,
                total = sum,
                timestamp = timeFormat.format(Date()),
                isCriticalSuccess = isCritSuccess,
                isCriticalFailure = isCritFail
            )

            _uiState.update { state ->
                state.copy(
                    isRollingDice = false,
                    lastDiceRoll = result,
                    diceHistory = (listOf(result) + state.diceHistory).take(25)
                )
            }

            if (isCritSuccess) AudioSynthEngine.playSfx("magic")
            else if (isCritFail) AudioSynthEngine.playSfx("monster")
        }
    }

    fun rollRollTable(table: RollTable) {
        val result = RollTablesData.rollOnTable(table)
        AudioSynthEngine.playSfx("dice")
        _uiState.update { it.copy(selectedRollTable = table, lastTableResult = result) }
    }

    fun generateNewNpc() {
        val npc = RollTablesData.generateRandomNpc()
        _uiState.update { it.copy(generatedNpc = npc) }
        showToast("PNJ généré : ${npc.name}")
    }

    fun generateNewEncounter(biome: String) {
        val enc = RollTablesData.generateRandomEncounter(biome)
        _uiState.update { it.copy(generatedEncounter = enc) }
        showToast("Rencontre générée : ${enc.title}")
    }

    // --- Soundscape Actions ---

    fun toggleTrack(trackId: String) {
        _uiState.update { state ->
            val updated = state.ambientTracks.map {
                if (it.id == trackId) it.copy(active = !it.active) else it
            }
            state.copy(ambientTracks = updated)
        }
        syncAudioEngine()
    }

    fun toggleMute() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
        if (_uiState.value.isMuted) {
            AudioSynthEngine.stopAmbient()
        } else {
            syncAudioEngine()
        }
    }

    fun applySoundPreset(preset: SoundPreset) {
        _uiState.update { state ->
            val updated = state.ambientTracks.map { track ->
                track.copy(active = preset.activeTrackIds.contains(track.id))
            }
            state.copy(ambientTracks = updated, isMuted = false)
        }
        syncAudioEngine()
        showToast("Préréglage appliqué : ${preset.name}")
    }

    fun stopAllAudio() {
        _uiState.update { state ->
            val updated = state.ambientTracks.map { it.copy(active = false) }
            state.copy(ambientTracks = updated, isSoundscapePlaying = false)
        }
        AudioSynthEngine.stopAmbient()
        showToast("Toutes les ambiances arrêtées")
    }

    fun playSfx(type: String) {
        AudioSynthEngine.playSfx(type)
    }

    private fun syncAudioEngine() {
        if (_uiState.value.isMuted) {
            AudioSynthEngine.stopAmbient()
            _uiState.update { it.copy(isSoundscapePlaying = false) }
            return
        }
        val activeNames = _uiState.value.ambientTracks.filter { it.active }.map { it.name }
        if (activeNames.isNotEmpty()) {
            AudioSynthEngine.startAmbientSynthesis(activeNames, _uiState.value.masterVolume)
            _uiState.update { it.copy(isSoundscapePlaying = true) }
        } else {
            AudioSynthEngine.stopAmbient()
            _uiState.update { it.copy(isSoundscapePlaying = false) }
        }
    }

    // --- Player Hub Actions ---

    fun modifyPlayerHp(id: String, delta: Int) {
        _uiState.update { state ->
            val updated = state.players.map { p ->
                if (p.id == id) p.copy(hp = (p.hp + delta).coerceIn(0, p.maxHp)) else p
            }
            state.copy(players = updated)
        }
    }

    fun toggleHandoutShare(id: String) {
        _uiState.update { state ->
            val updated = state.handouts.map { h ->
                if (h.id == id) h.copy(isSharedWithPlayers = !h.isSharedWithPlayers) else h
            }
            state.copy(handouts = updated)
        }
        showToast("Statut de partage mis à jour")
    }
}
