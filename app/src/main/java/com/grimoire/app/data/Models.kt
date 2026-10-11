package com.grimoire.app.data

// --- VTT Models ---

enum class SpellShape {
    CIRCLE, CONE, SQUARE, LINE
}

data class MapScene(
    val id: String,
    val name: String,
    val assetPath: String,
    val gridWidth: Int = 25,
    val gridHeight: Int = 20,
    val cellSizeDp: Float = 48f
)

data class VttToken(
    val id: String,
    val name: String,
    val x: Float, // grid coordinate
    val y: Float,
    val hp: Int = 10,
    val maxHp: Int = 10,
    val ac: Int = 12,
    val speed: Int = 30,
    val isEnemy: Boolean = false,
    val colorHex: Long = 0xFF3B82F6,
    val avatarEmoji: String = "🧙",
    val conditions: List<String> = emptyList(),
    val notes: String = ""
)

data class FowCell(
    val x: Int,
    val y: Int,
    val revealed: Boolean = false
)

data class MapPin(
    val id: String,
    val x: Float,
    val y: Float,
    val title: String,
    val secretDescription: String,
    val isRevealedToPlayers: Boolean = false,
    val iconEmoji: String = "📍"
)

data class SpellAoe(
    val id: String,
    val x: Float,
    val y: Float,
    val radiusTiles: Float,
    val shape: SpellShape = SpellShape.CIRCLE,
    val colorHex: Long = 0x88EF4444,
    val name: String = "Boule de Feu"
)

// --- Combat Tracker Models ---

data class Combatant(
    val id: String,
    val name: String,
    val initiative: Int,
    val hp: Int,
    val maxHp: Int,
    val isEnemy: Boolean,
    val conditions: List<String> = emptyList(),
    val ac: Int = 10,
    val avatarEmoji: String = if (isEnemy) "👹" else "⚔️"
)

data class CombatLogEntry(
    val id: String,
    val timestamp: String,
    val message: String,
    val type: LogType = LogType.INFO
)

enum class LogType {
    ATTACK, HEAL, DAMAGE, INITIATIVE, CONDITION, INFO
}

// --- Grimoire Lore & Campaign Models ---

enum class NoteCategory(val label: String, val emoji: String) {
    QUESTS("Quêtes", "📜"),
    NPCS("PNJs", "👤"),
    LOCATIONS("Lieux", "🏰"),
    FACTIONS("Factions", "🛡️"),
    LORE("Lore & Secrets", "🔮"),
    RULES("Règles & Tables", "⚖️")
}

data class CampaignNote(
    val id: String,
    val title: String,
    val category: NoteCategory,
    val content: String,
    val tags: List<String> = emptyList(),
    val isPinned: Boolean = false,
    val lastUpdated: String = "Aujourd'hui"
)

// --- Compendium Models ---

data class HerbItem(
    val id: String,
    val name: String,
    val latinName: String = "",
    val rarity: String, // Commune, Rare, Très rare, Légendaire
    val biome: String, // Forêt, Montagne, Marais, Souterrain, Plaine
    val effect: String,
    val preparation: String,
    val description: String,
    val emoji: String = "🌿",
    val priceGold: Int = 10
)

data class BestiaryMonster(
    val id: String,
    val name: String,
    val cr: String,
    val type: String,
    val hp: Int,
    val ac: Int,
    val speed: String,
    val attacks: String,
    val specialTraits: String,
    val emoji: String = "🐉"
)

// --- Toolkit Models ---

data class DiceRollResult(
    val formula: String,
    val rolls: List<Int>,
    val modifier: Int,
    val total: Int,
    val timestamp: String,
    val isCriticalSuccess: Boolean = false,
    val isCriticalFailure: Boolean = false
)

data class RollTable(
    val id: String,
    val name: String,
    val emoji: String,
    val items: List<String>
)

data class RandomNpc(
    val name: String,
    val raceAndClass: String,
    val personalityTrait: String,
    val secretOrMotivation: String,
    val quirk: String,
    val appearance: String
)

data class RandomEncounter(
    val biome: String,
    val title: String,
    val description: String,
    val dangerLevel: String,
    val enemySuggestions: List<String>
)

// --- Soundscape Models ---

data class AmbientTrack(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val active: Boolean = false,
    val volume: Float = 0.5f,
    val synthType: SynthType = SynthType.RAIN
)

enum class SynthType {
    RAIN, WIND, FIRE, DRONE, TAVERN_MURMUR, WATER, BELLS
}

data class SoundPreset(
    val id: String,
    val name: String,
    val emoji: String,
    val activeTrackIds: List<String>
)

// --- Player Hub Models ---

data class PlayerCharacter(
    val id: String,
    val name: String,
    val playerRealName: String,
    val charClass: String,
    val level: Int,
    val hp: Int,
    val maxHp: Int,
    val ac: Int,
    val passivePerception: Int,
    val stats: Map<String, Int>, // STR, DEX, CON, INT, WIS, CHA
    val keySpellsOrFeats: List<String>,
    val avatarEmoji: String
)

data class SecretHandout(
    val id: String,
    val title: String,
    val summary: String,
    val fullText: String,
    val isSharedWithPlayers: Boolean = false,
    val targetPlayerName: String = "Tous"
)
