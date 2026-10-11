package com.grimoire.app.data

object CompendiumData {

    val herbs = listOf(
        HerbItem(
            id = "herb_feuille_dore",
            name = "Feuille d'Or Solaire",
            latinName = "Solaria aurifolia",
            rarity = "Commune",
            biome = "Plaine",
            effect = "Restaure 1d4 PV et dissipe la fatigue mineure",
            preparation = "Infusion bouillie dans de l'eau de source",
            description = "Petite plante herbacée aux feuilles dentelées réfléchissant la lumière du soleil comme de véritables paillettes dorées.",
            emoji = "🌿",
            priceGold = 5
        ),
        HerbItem(
            id = "herb_racine_sombreroche",
            name = "Racine de Sombreroche",
            latinName = "Radix tenebrosa",
            rarity = "Rare",
            biome = "Souterrain",
            effect = "Confère vision dans le noir pendant 1 heure mais rend vulnérable à la lumière vive",
            preparation = "Broyée en pâte et appliquée sur les paupières",
            description = "Pousse exclusivement dans l'obscurité totale des crevasses de roche basaltique, luisante d'une phosphorescence bleuâtre.",
            emoji = "🌱",
            priceGold = 35
        ),
        HerbItem(
            id = "herb_larmes_sylvaines",
            name = "Larmes Sylvaines",
            latinName = "Lacrima dryadis",
            rarity = "Très rare",
            biome = "Forêt",
            effect = "Neutralise tout poison non-magique et restaure 2d8 PV",
            preparation = "Distillation lente à froid dans une fiole d'argent",
            description = "Fleur rare ne s'ouvrant qu'au clair de lune dans les bosquets protégés par des créatures féeriques.",
            emoji = "🌸",
            priceGold = 80
        ),
        HerbItem(
            id = "herb_mousse_sanglante",
            name = "Mousse Sanglante",
            latinName = "Bryum cruentum",
            rarity = "Commune",
            biome = "Marais",
            effect = "Arrête immédiatement les hémorragies et stabilise un mourant",
            preparation = "Cataplasme direct sur plaie ouverte",
            description = "Mousse écarlate poussant sur les souches humides, libérant un fluide épais semblable à du sang coagulé.",
            emoji = "🩸",
            priceGold = 8
        ),
        HerbItem(
            id = "herb_fleur_givre",
            name = "Fleur de Givre Éternel",
            latinName = "Glacies flos",
            rarity = "Légendaire",
            biome = "Montagne",
            effect = "Permet d'absorber jusqu'à 30 points de dégâts de froid ou de créer un cône de glace (4d6 dégâts)",
            preparation = "Cristallisation alchimique dans de l'alcool pur",
            description = "Fleur translucide qui ne fond jamais, cueillie au sommet des pics perpétuellement enneigés.",
            emoji = "❄️",
            priceGold = 250
        ),
        HerbItem(
            id = "herb_amanite_lunaire",
            name = "Amanite Pourpre Lunaire",
            latinName = "Amanita lunaris",
            rarity = "Rare",
            biome = "Forêt",
            effect = "Provoque des visions prophétiques ou une transe mystique (+2 à tous les jets d'Arcane pour 2h)",
            preparation = "Séchage et réduction en poudre d'encens",
            description = "Chapeau d'un violet profond moucheté d'étoiles argentées scintillantes.",
            emoji = "🍄",
            priceGold = 45
        ),
        HerbItem(
            id = "herb_venin_vipere_noire",
            name = "Venin d'Épine-Noire",
            latinName = "Toxicum spinosum",
            rarity = "Rare",
            biome = "Marais",
            effect = "Enduit d'arme : jet de CON DC 13 ou subir 2d8 dégâts de poison et état Empoisonné pendant 1 minute",
            preparation = "Condensation de suc vénéneux et graisse animale",
            description = "Sécrétion huileuse extraite des ronces épineuses des marais fétides.",
            emoji = "🧪",
            priceGold = 50
        ),
        HerbItem(
            id = "herb_pierre_soleil",
            name = "Héliolite Scintillante",
            latinName = "Lapis solaris",
            rarity = "Très rare",
            biome = "Montagne",
            effect = "Catalyseur d'enchantement : augmente les dégâts radiants de +1d4 sur une arme",
            preparation = "Taillée en cabochon et enchâssée sur la garde",
            description = "Minéral incandescent émettant une douce chaleur continue, trouvé dans les filons aurifères.",
            emoji = "💎",
            priceGold = 120
        )
    )

    val bestiary = listOf(
        BestiaryMonster(
            id = "mob_goblin",
            name = "Gobelin Pillard",
            cr = "1/4",
            type = "Humanoïde (Gobelinoïde)",
            hp = 7,
            ac = 15,
            speed = "9m",
            attacks = "Cimeterre (+4 au toucher, 1d6+2 tranchant), Arc court (+4, 1d6+2 perforant, portée 24/96m)",
            specialTraits = "Fuite agile : peut Se désengager ou Se cacher par une action bonus à chaque tour.",
            emoji = "👺"
        ),
        BestiaryMonster(
            id = "mob_orc",
            name = "Guerrier Orque",
            cr = "1/2",
            type = "Humanoïde (Orque)",
            hp = 15,
            ac = 13,
            speed = "9m",
            attacks = "Grande hache (+5 au toucher, 1d12+3 tranchant), Javelot (+5, 1d6+3)",
            specialTraits = "Agressivité : peut se déplacer jusqu'à sa vitesse vers un ennemi par une action bonus.",
            emoji = "👹"
        ),
        BestiaryMonster(
            id = "mob_skeleton",
            name = "Squelette Ranimé",
            cr = "1/4",
            type = "Mort-vivant",
            hp = 13,
            ac = 13,
            speed = "9m",
            attacks = "Épée courte (+4 au toucher, 1d6+2), Arc court (+4, 1d6+2)",
            specialTraits = "Vulnérabilité aux dégâts contondants. Immunisé au poison et à l'état empoisonné.",
            emoji = "💀"
        ),
        BestiaryMonster(
            id = "mob_direwolf",
            name = "Loup Sanguinaire",
            cr = "1",
            type = "Bête",
            hp = 37,
            ac = 14,
            speed = "15m",
            attacks = "Morsure (+5 au toucher, 2d6+3 perforant). Si la cible est une créature, jet de FOR DC 13 ou mise à terre.",
            specialTraits = "Tactique de meute : avantage aux jets d'attaque si un allié est à moins de 1,5m de la cible.",
            emoji = "🐺"
        ),
        BestiaryMonster(
            id = "mob_dragon_young",
            name = "Jeune Dragon Rouge",
            cr = "10",
            type = "Dragon",
            hp = 178,
            ac = 18,
            speed = "12m, vol 24m",
            attacks = "Multi-attaque : 1 morsure (+10, 2d10+6 + 1d6 feu), 2 griffes (+10, 2d6+6). Souffle de feu (16d6 feu, DEX DC 17 demi).",
            specialTraits = "Immunité au feu. Présence terrifiante (DC 14). Vision aveugle 9m, vision dans le noir 36m.",
            emoji = "🐉"
        )
    )

    val biomes = listOf("Tous", "Plaine", "Forêt", "Montagne", "Marais", "Souterrain")
    val rarities = listOf("Toutes", "Commune", "Rare", "Très rare", "Légendaire")
}
