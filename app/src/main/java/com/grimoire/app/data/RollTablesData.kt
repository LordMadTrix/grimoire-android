package com.grimoire.app.data

import kotlin.random.Random

object RollTablesData {

    val tables = listOf(
        RollTable(
            id = "pnj_traits",
            name = "Traits de PNJ",
            emoji = "👤",
            items = listOf(
                "Avide et cupide", "Loyal jusqu'à la mort", "Craintif et sursaute au moindre bruit",
                "Arrogant et condescendant", "Généreux et accueillant", "Paranoïaque envers la magie",
                "Mélancolique et nostalgique", "Enthousiaste et plein d'énergie", "Calculateur et froid",
                "Naïf et crédule", "Cruel et sans pitié", "Compatissant envers les faibles",
                "Discret et parle à voix basse", "Bruyant et rieur", "Mystérieux et évasif",
                "Franc et direct", "Indécis et hésitant", "Déterminé et obstiné"
            )
        ),
        RollTable(
            id = "weather",
            name = "Météo & Ambiance",
            emoji = "🌤️",
            items = listOf(
                "Soleil éclatant et brise légère", "Nuages lourds annonçant la tourmente",
                "Pluie battante réduisant la visibilité à 10 mètres", "Brouillard épais et froid mordant",
                "Vent violent rendant les tirs à distance désavantagés", "Orage électrique avec grondements sourds",
                "Neige silencieuse recouvrant les pistes", "Crachin froid persistant",
                "Chaleur étouffante provoquant la soif", "Nuit claire étoilée avec lueur argentée",
                "Grêle soudaine cinglant les visages", "Ciel pourpre surnaturel avec odeur d'ozone"
            )
        ),
        RollTable(
            id = "urban_encounters",
            name = "Rencontres Urbaines",
            emoji = "🏙️",
            items = listOf(
                "Un pickpocket tente discrètement de couper la bourse d'un PJ (DC 14 Perception)",
                "Un marchand accuse un noble local d'avoir payé avec des fausses pièces",
                "Une patrouille de la milice arrête le groupe pour contrôle des armes",
                "Un enfant des rues propose de mener les PJ à un passage secret contre 2 pièces d'argent",
                "Une rixe éclate dans la taverne voisine et passe par la fenêtre",
                "Un prédicateur exalté annonce la venue du dévoreur de mondes",
                "Un mystérieux colporteur sous cape propose des philtres alchimiques interdits",
                "Une calèche lancée à toute allure risque de renverser un piéton sans défense",
                "Alerte incendie : des flammes s'échappent d'un atelier d'alchimie"
            )
        ),
        RollTable(
            id = "wild_encounters",
            name = "Rencontres Sauvages",
            emoji = "🌲",
            items = listOf(
                "Une meute de loups affamés encercle le bivouac à la lisière des bois",
                "Un ermite druide propose un abri dans un arbre creux contre une offrande",
                "Une caravane de marchands renversée avec des traces de griffes géantes",
                "Traces fraîches d'un dragon vert dans la boue séchée",
                "Un dolmen de pierre gravé de runes luisantes émettant un bourdonnement",
                "Un pont de corde au-dessus d'un gouffre profond paraît sur le point de rompre",
                "Un chasseur blessé par un piège à ours demande de l'aide immédiate",
                "Une brume épaisse murmure des souvenirs d'enfance aux voyageurs"
            )
        ),
        RollTable(
            id = "dungeon_rooms",
            name = "Salles de Donjon",
            emoji = "🏚️",
            items = listOf(
                "Chambre de torture abandonnée avec chaînes rouillées et cage suspendue",
                "Bibliothèque effondrée envahie de champignons luminescents",
                "Autel sacré profané portant encore des traces de cire noire",
                "Geôle aux barreaux tordus avec ossements d'un géant",
                "Cuisine délabrée infestée d'araignées géantes tissant dans les coins",
                "Salle du trône fissurée ornée de bas-reliefs nains écornés",
                "Atelier d'orfèvrerie avec lingots de cuivre ternis sous un éboulement",
                "Puits circulaire sans fond produisant un courant d'air glacial ascendant",
                "Crypte funéraire avec sarcophages dont les couvercles sont descellés"
            )
        ),
        RollTable(
            id = "complications",
            name = "Complications Dramatiques",
            emoji = "💥",
            items = listOf(
                "La torche s'éteint brusquement au moment où un bruit se rapproche",
                "L'objet convoité s'avère être une réplique enchantée piégée",
                "Le plafond commence à grincer et menacer de s'effondrer dans 3 rounds",
                "Un ennemi défait déclenche une corne de brume sonnant l'alerte générale",
                "Un PJ trébuche sur un mécanisme secret révélant un passage insoupçonné",
                "La porte arrière se referme et se verrouille par un lourd contrepoids",
                "Un gaz soporifique commence à envahir la salle par les grilles au sol",
                "L'allié promis au rendez-vous est introuvable et a laissé une trace de sang"
            )
        ),
        RollTable(
            id = "loot",
            name = "Trésors & Butin",
            emoji = "💰",
            items = listOf(
                "Bourse en velours contenant 45 pièces d'or et 30 d'argent",
                "Dague d'apparat sertie d'une topaze gravée (valeur 75 PO)",
                "Parchemin de sort d'Armure de Mage scellé par de la cire bleue",
                "Fiole de Potion de Soin Supérieure (restaure 4d4+4 PV)",
                "Clé en argent sculptée en forme de tête de corbeau",
                "Amulette en os de vouivre conférant +1 à la sauvegarde de Réflexes",
                "Carnet relié en cuir contenant les noms de 5 comploteurs du Val-d'Or",
                "Bague d'or signée portant un blason noble oublié (valeur 120 PO)"
            )
        )
    )

    fun rollOnTable(table: RollTable): String {
        return table.items.random()
    }

    private val npcFirstNames = listOf("Aldric", "Mira", "Thorvald", "Seraphine", "Barak", "Lyria", "Dorn", "Isadora", "Fenwick", "Zara", "Korvus", "Elara", "Ragnar", "Tessaly", "Grimm", "Nadia", "Loric", "Vessa", "Edvard", "Sylvaine")
    private val npcLastNames = listOf("Corbebec", "de Val-Noir", "Hache-Froide", "Ombrelame", "Chantefleur", "Forgacier", "Cœur-de-Chêne", "Larme-d'Argent", "Poing-de-Foudre", "Grand-Pas")
    private val races = listOf("Humain", "Elfe sylvain", "Haut-elfe", "Nain des collines", "Nain des montagnes", "Halfelin pied-léger", "Gnome des forêts", "Demi-orque", "Tieffelin", "Drakéide")
    private val classesAndRoles = listOf("Aubergiste chaleureux", "Garde de la milice", "Forgeron bourru", "Marchand d'épices", "Erudit d'arcanes", "Mercenaire désabusé", "Herboriste discret", "Voleur repentant", "Prêtre de campagne", "Chasseur de primes")
    private val traits = listOf("Parle constamment de sa famille", "Regarde nerveusement par-dessus son épaule", "Ne fait confiance à personne portant une cape", "Collectionne les insectes séchés", "Toujours poli même menacé", "Parle avec des métaphores culinaires", "A une dette de jeu dangereuse", "Bégaye quand il ment")
    private val secrets = listOf("Possède la clé d'un coffre de la guilde des voleurs", "A vu le maire rencontrer un nécromancien", "Est secrètement un espion d'une baronnie voisine", "Garde une relique volée dans sa cave", "Recherche vengeance pour un frère emprisonné", "Possède une fausse identité")

    fun generateRandomNpc(): RandomNpc {
        val name = "${npcFirstNames.random()} ${npcLastNames.random()}"
        val raceRole = "${races.random()} — ${classesAndRoles.random()}"
        val trait = traits.random()
        val secret = secrets.random()
        val quirk = "Tics : ${listOf("Se frotte la barbe", "Fait tourner une pièce d'or", "Évite le regard direct", "Sourit mystérieusement").random()}"
        val app = listOf("Grand et musclé, cicatrice sur la joue", "Chétif, lunettes à monture de cuivre", "Élégant, vêtements brodés usés", "Robuste, mains calleuses noircies").random()
        return RandomNpc(
            name = name,
            raceAndClass = raceRole,
            personalityTrait = trait,
            secretOrMotivation = secret,
            quirk = quirk,
            appearance = app
        )
    }

    fun generateRandomEncounter(biome: String): RandomEncounter {
        val encounter = when (biome.lowercase()) {
            "forêt" -> RandomEncounter(
                biome = "Forêt",
                title = "Embuscade des Ronces Sanglantes",
                description = "Des gobelins cachés dans la canopée tirent des flèches empoisonnées pendant que deux loups rabattent les PJ vers un bourbier.",
                dangerLevel = "Moyen (CR 2)",
                enemySuggestions = listOf("3x Gobelins tireurs", "2x Loups sauvages")
            )
            "souterrain" -> RandomEncounter(
                biome = "Souterrain",
                title = "Le Nid des Araignées de Phase",
                description = "Les plafonds sont recouverts de toiles épaisses. Deux araignées de phase se téléportent derrière les lanceurs de sorts.",
                dangerLevel = "Difficile (CR 3)",
                enemySuggestions = listOf("2x Araignées de phase", "1x Enchevêtrement de toiles piégé")
            )
            "montagne" -> RandomEncounter(
                biome = "Montagne",
                title = "Le Nid de la Vouivre des Pics",
                description = "Un souffle de vent glacial annonce l'attaque en piqué d'une jeune vouivre défendant son nid rocheux.",
                dangerLevel = "Élevé (CR 4)",
                enemySuggestions = listOf("1x Jeune Vouivre", "Terrain escarpé (sauvegarde d'escalade DC 12)")
            )
            else -> RandomEncounter(
                biome = "Plaines / Route",
                title = "Barrage de Brigands du Val",
                description = "Un arbre abattu bloque la route. Six brigands armés d'arbalètes et de boucliers exigent un droit de passage de 20 PO par personne.",
                dangerLevel = "Facile à Moyen (CR 1)",
                enemySuggestions = listOf("1x Chef brigand", "4x Bandits avec arbalètes légères")
            )
        }
        return encounter
    }
}
