package com.grimoire.app.data

object SampleCampaignData {

    val defaultScenes = listOf(
        MapScene(
            id = "sanctuary",
            name = "Le Sanctuaire Oublié",
            assetPath = "maps/sanctuaire.png",
            gridWidth = 24,
            gridHeight = 18,
            cellSizeDp = 44f
        ),
        MapScene(
            id = "abandoned_fortress",
            name = "Forteresse de Roc-Noir",
            assetPath = "maps/abandoned_fortress.png",
            gridWidth = 26,
            gridHeight = 20,
            cellSizeDp = 44f
        ),
        MapScene(
            id = "crypt",
            name = "Crypte des Ombres",
            assetPath = "",
            gridWidth = 20,
            gridHeight = 16,
            cellSizeDp = 48f
        ),
        MapScene(
            id = "forest_ambush",
            name = "Clairière du Dolmen",
            assetPath = "",
            gridWidth = 22,
            gridHeight = 18,
            cellSizeDp = 46f
        )
    )

    val defaultTokens = listOf(
        VttToken(
            id = "token_thorvald",
            name = "Thorvald (Guerrier)",
            x = 4f,
            y = 8f,
            hp = 28,
            maxHp = 28,
            ac = 17,
            speed = 30,
            isEnemy = false,
            colorHex = 0xFF2563EB,
            avatarEmoji = "🛡️",
            notes = "Spécialité arme d'hast, Sentinelle"
        ),
        VttToken(
            id = "token_lyria",
            name = "Lyria (Magicienne)",
            x = 2f,
            y = 7f,
            hp = 18,
            maxHp = 18,
            ac = 13,
            speed = 30,
            isEnemy = false,
            colorHex = 0xFF8B5CF6,
            avatarEmoji = "🔮",
            notes = "Emplacements niv 2 restants: 2"
        ),
        VttToken(
            id = "token_fenwick",
            name = "Fenwick (Roublard)",
            x = 3f,
            y = 10f,
            hp = 22,
            maxHp = 22,
            ac = 15,
            speed = 35,
            isEnemy = false,
            colorHex = 0xFF10B981,
            avatarEmoji = "🗡️",
            notes = "Discrétion +7, Crochetage d'outils"
        ),
        VttToken(
            id = "token_elara",
            name = "Elara (Clerc de la Lumière)",
            x = 3f,
            y = 8f,
            hp = 24,
            maxHp = 24,
            ac = 16,
            speed = 30,
            isEnemy = false,
            colorHex = 0xFFF59E0B,
            avatarEmoji = "✨",
            notes = "Canalisation divine disponible"
        ),
        VttToken(
            id = "token_goblin_1",
            name = "Gobelin Éclaireur 1",
            x = 12f,
            y = 6f,
            hp = 7,
            maxHp = 7,
            ac = 13,
            speed = 30,
            isEnemy = true,
            colorHex = 0xFFDC2626,
            avatarEmoji = "👺",
            notes = "Arc court +4 (1d6+2)"
        ),
        VttToken(
            id = "token_goblin_2",
            name = "Gobelin Éclaireur 2",
            x = 13f,
            y = 9f,
            hp = 7,
            maxHp = 7,
            ac = 13,
            speed = 30,
            isEnemy = true,
            colorHex = 0xFFDC2626,
            avatarEmoji = "👺",
            notes = "Embusqué derrière le pilier"
        ),
        VttToken(
            id = "token_boss_orc",
            name = "Krag le Borgne (Chef)",
            x = 16f,
            y = 8f,
            hp = 35,
            maxHp = 35,
            ac = 15,
            speed = 30,
            isEnemy = true,
            colorHex = 0xFF991B1B,
            avatarEmoji = "👹",
            notes = "Hache à deux mains +6 (1d12+4), Cri de ralliement"
        )
    )

    val defaultPins = listOf(
        MapPin(
            id = "pin_altar",
            x = 10f,
            y = 8f,
            title = "Autel profané",
            secretDescription = "L'autel porte une rune de sang cachée. Jet d'Investigation DC 14 pour découvrir le compartiment secret contenant une gemme de l'âme et 40 PO.",
            isRevealedToPlayers = false,
            iconEmoji = "🔮"
        ),
        MapPin(
            id = "pin_trap",
            x = 7f,
            y = 8f,
            title = "Dalle piégée (Fosse)",
            secretDescription = "Déclenche des pieux empoisonnés si 40+ kg marchent dessus. Jet de DEX DC 13 ou subir [[2d6]] dégâts perforants + jet de Constitution DC 12 contre le poison.",
            isRevealedToPlayers = false,
            iconEmoji = "⚠️"
        ),
        MapPin(
            id = "pin_chest",
            x = 18f,
            y = 12f,
            title = "Coffre de fer sculpté",
            secretDescription = "Verrouillé (DC 15). Contient: Parchemin de Boule de Feu, 120 PO, et le Journal de l'Inquisiteur mentionnant le Comte de Val-Noir.",
            isRevealedToPlayers = true,
            iconEmoji = "📦"
        )
    )

    val defaultNotes = listOf(
        CampaignNote(
            id = "note_intro",
            title = "Chapitre 1 : Le Réveil du Sanctuaire",
            category = NoteCategory.QUESTS,
            isPinned = true,
            tags = listOf("Quête principale", "Val-d'Or", "Acte I"),
            content = """
# Chapitre 1 : Le Réveil du Sanctuaire

> [!NOTE]
> Cette session débute alors que le groupe arrive aux abords des contreforts brumeux de Roc-Noir. Une pluie fine bat les dalles effritées.

### Objectifs de la session :
- [x] Explorer les abords du temple en ruine
- [ ] Désactiver le piège de la dalle centrale
- [ ] Vaincre Krag le Borgne et récupérer l'Amulette Astrale
- [ ] Décrypter le journal secret pour localiser la Nécropole

> [!WARNING]
> Si les joueurs font trop de bruit à l'entrée (jet de Discrétion collectif DC 12 échoué), Krag déclenche son piège et bénéficie d'une attaque d'embuscade gratuite : [[1d20+5]] d'attaque pour [[1d10+3]] dégâts !

### Indices pour le MJ :
- Le boss a peur du feu. Toute attaque de feu lui inflige un désavantage à son prochain jet d'attaque.
- Le compartiment secret sous l'autel contient la clé du niveau inférieur.
            """.trimIndent()
        ),
        CampaignNote(
            id = "note_npc_krag",
            title = "Krag le Borgne - Chef des Pillards",
            category = NoteCategory.NPCS,
            tags = listOf("Antagoniste", "Orque", "Boss"),
            content = """
# Krag le Borgne

**Race :** Orque du Clan de la Hache Fendue  
**Alignement :** Chaotique Mauvais  
**PV :** 35 | **CA :** 15 | **Initiative :** +1

### Traits et Personnalité :
- Ne supporte pas qu'on mentionne son œil gauche perdu.
- Parle un commun haché mais possède un sens tactique acéré.
- Secret : Il a été payé par le Baron d'Aubépine pour subtiliser la relique de l'autel.

> [!TIP]
> Si Krag tombe sous les 10 PV, il tentera de négocier son pardon en révélant la trahison du Baron.
            """.trimIndent()
        ),
        CampaignNote(
            id = "note_location_valnoir",
            title = "La Région du Val-Noir",
            category = NoteCategory.LOCATIONS,
            tags = listOf("Géographie", "Atmosphère"),
            content = """
# La Région du Val-Noir

Une vallée encaissée perpétuellement voilée de brumes violacées. La végétation y pousse de manière tortueuse en raison des effluves d'énergie nécrotique émanant du Sanctuaire Oublié.

- **Climat :** Humide, crachin persistant, froid mordant la nuit.
- **Dangers :** Loups sanguinaires, spectres errants à minuit.
- **Herboristerie :** Zone propice à la cueillette de *Racine de Sombreroche* et d'*Amanite Pourpre*.
            """.trimIndent()
        ),
        CampaignNote(
            id = "note_lore_amulet",
            title = "L'Amulette Astrale de Séléné",
            category = NoteCategory.LORE,
            tags = listOf("Relique", "Magie", "Objet Merveilleux"),
            content = """
# L'Amulette Astrale de Séléné

Forgée au cours du Premier Âge par les archimages de l'Ordre Céleste.

- **Effet passif :** Confère à son porteur une vision dans le noir de 18 mètres.
- **Pouvoir actif (1x/jour) :** Libère une onde de clairvoyance révélant les passages secrets et pièges dans un rayon de 9 mètres pendant 10 minutes.
- **Malédiction latente :** Lorsque portée la nuit de nouvelle lune, murmure des prophéties funestes dans l'esprit du porteur (jet de Sagesse DC 13 ou 1 point de folie temporaire).
            """.trimIndent()
        )
    )

    val defaultPlayers = listOf(
        PlayerCharacter(
            id = "pc_thorvald",
            name = "Thorvald",
            playerRealName = "Thomas",
            charClass = "Guerrier Champion",
            level = 4,
            hp = 28,
            maxHp = 28,
            ac = 17,
            passivePerception = 11,
            stats = mapOf("FOR" to 18, "DEX" to 12, "CON" to 16, "INT" to 9, "SAG" to 12, "CHA" to 10),
            keySpellsOrFeats = listOf("Sursaut d'activité", "Second souffle (1d10+4)", "Style arme à 2 mains"),
            avatarEmoji = "🛡️"
        ),
        PlayerCharacter(
            id = "pc_lyria",
            name = "Lyria",
            playerRealName = "Sophie",
            charClass = "Magicienne Évocatrice",
            level = 4,
            hp = 18,
            maxHp = 18,
            ac = 13,
            passivePerception = 14,
            stats = mapOf("FOR" to 8, "DEX" to 14, "CON" to 12, "INT" to 18, "SAG" to 13, "CHA" to 11),
            keySpellsOrFeats = listOf("Boule de Feu (8d6)", "Armure de Mage", "Projectiles Magiques", "Image Miroir"),
            avatarEmoji = "🔮"
        ),
        PlayerCharacter(
            id = "pc_fenwick",
            name = "Fenwick",
            playerRealName = "Alex",
            charClass = "Roublard Assassin",
            level = 4,
            hp = 22,
            maxHp = 22,
            ac = 15,
            passivePerception = 16,
            stats = mapOf("FOR" to 10, "DEX" to 18, "CON" to 12, "INT" to 14, "SAG" to 12, "CHA" to 14),
            keySpellsOrFeats = listOf("Attaque Sournoise (2d6)", "Ruse", "Esquive Instinctive"),
            avatarEmoji = "🗡️"
        ),
        PlayerCharacter(
            id = "pc_elara",
            name = "Elara",
            playerRealName = "Camille",
            charClass = "Clerc de la Lumière",
            level = 4,
            hp = 24,
            maxHp = 24,
            ac = 16,
            passivePerception = 15,
            stats = mapOf("FOR" to 14, "DEX" to 10, "CON" to 14, "INT" to 10, "SAG" to 17, "CHA" to 13),
            keySpellsOrFeats = listOf("Flamme sacrée", "Lueur de l'aube", "Mot de guérison (1d4+3)", "Bénédiction"),
            avatarEmoji = "✨"
        )
    )

    val defaultHandouts = listOf(
        SecretHandout(
            id = "handout_1",
            title = "Lettre cachetée de cire pourpre",
            summary = "Trouvée sur le cadavre du messager goblin.",
            fullText = "Mon loyal Krag, l'incursion doit avoir lieu dès la tombée de la nuit. Récupère la relique avant l'arrivée de la milice de Val-d'Or. Le pacte sera honoré. Signé : Le B.",
            isSharedWithPlayers = false
        ),
        SecretHandout(
            id = "handout_2",
            title = "Parchemin d'esquisse du temple",
            summary = "Carte sommaire indiquant une issue dérobée.",
            fullText = "Derrière la statue brisée de la déesse, pousser la troisième dalle à gauche pour révéler les marches menant au laboratoire d'alchimie oublié.",
            isSharedWithPlayers = true
        )
    )
}
