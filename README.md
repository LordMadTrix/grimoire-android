# 🐉 Grimoire — Compagnon de Maître du Jeu TTRPG (Android)

**Grimoire** est une application Android moderne et performante conçue pour les Maîtres du Jeu (MJ) sur table et en mobilité. Développée en **Kotlin** et **Jetpack Compose**, elle rassemble en une interface élégante et immersive tout ce dont vous avez besoin pour préparer et animer vos campagnes de jeu de rôle sur table (D&D 5e, WFRP, Pathfinder, Chroniques Oubliées, etc.).

---

## ✨ Fonctionnalités Principales

### 1. 🗺️ Table Virtuelle Tactique (VTT)
- **Cartes tactiques haute résolution** : Support de scènes prêtes à l'emploi (*Le Sanctuaire Oublié*, *Forteresse de Roc-Noir*, donjons de cryptes, clairières forestières).
- **Navigation fluide** : Zoom au pincement (pinch-to-zoom) et déplacement tactile libre (pan).
- **Grille tactique & Échelle** : Grille configurable avec activation/désactivation en 1 clic.
- **Pions dynamiques (Tokens)** : Héros et monstres avec barres de points de vie colorées, conditions d'états, scores de CA et déplacement tactile sur la grille.
- **Brouillard de guerre (Fog of War)** : Voile d'ombre dynamique, révélation case par case au doigt, ou bouton *Tout Révéler*.
- **Points d'intérêt secrets (POI Pins)** : Notes MJ secrètes dissimulées sur la carte, révélables aux joueurs d'un clic.
- **Gabarits de sorts AoE** : Rayon de Boule de Feu, Cônes de souffle glacé, Lignes de foudre.

### 2. ⚔️ Gestionnaire de Combat & Initiative
- **Ordre d'initiative dynamique** : Tri automatique et mise en surbrillance du combattant actif.
- **Contrôles de tours et rounds** : Boutons Tour Suivant (▶) et Tour Précédent (◀) avec compteur de rounds en direct.
- **Lancer d'initiatives en 1 clic** : Tirage instantané de l'initiative pour tous les combattants (format classique d20 ou Warhammer Fantasy 1d10+3).
- **Ajustement rapide des PV** : Steppers rapides (+/-1, +/-5) avec barre de santé animée et passage automatique à l'état inconscient/mort.
- **Roue des conditions & altérations** : Aveuglé, Charmé, Empoisonné, À terre, Entravé, Étourdi, Paralysé, etc.
- **Journal de combat en temps réel** : Historique coloré des attaques, soins, dégâts et états.

### 3. 📜 Grimoire de Campagne & Wiki Markdown
- **Notes structurées par catégories** : Quêtes, PNJs, Lieux, Factions, Lore & Secrets, Règles de jeu.
- **Recherche instantanée & Filtrage** : Recherche plein texte et tags `#Campagne`.
- **Rendu Markdown Rôliste** :
  - **Encadrés stylisés (Callouts)** : `> [!NOTE]`, `> [!WARNING]`, `> [!TIP]`.
  - **Dés en ligne cliquables** : Les mentions `[[1d20+4]]` ou `[[2d6]]` deviennent des badges interactifs qui lancent directement les dés au toucher !
  - **Listes de quêtes interactives** : Cases à cocher `- [ ]` et `- [x]`.
- **Édition intégrée** : Création, modification et suppression de notes à la volée.

### 4. 🌿 Compendium Herboriste & Bestiaire
- **Herboristerie & Minéralogie fantastique** : Plantes magiques, minéraux, champignons et poisons portés du moteur *Herboriste* de Grimoire.
- **Fiches illustrées détaillées** : Rareté (Commune, Rare, Très rare, Légendaire), biome d'origine, effets en jeu, mode de préparation alchimique et valeur en pièces d'or.
- **Bestiaire des monstres** : Facteurs de puissance (FP / CR), PV, CA, vitesses, attaques et aptitudes spéciales.
- **Simulateur de cueillette** : Jet de dés d20 + Survie contre la difficulté du biome avec tirage des spécimens récoltés.

### 5. 🎲 Boîte à Outils & Générateurs du MJ
- **Lanceur de dés physique** : Tray complet d4, d6, d8, d10, d12, d20, d100 avec sélecteur de quantité, modificateur (+/-) et détection des réussites/échecs critiques.
- **Tables aléatoires (Roll Tables)** : Tirage instantané d'un tap : Traits de PNJ, Météo & Climat, Rencontres urbaines et sauvages, Salles de donjon, Complications dramatiques, Trésors & Butins.
- **Générateur instantané de PNJ** : Nom, ascendance, classe, trait de caractère, secret inavouable et tics de comportement, avec bouton d'insertion directe sur la carte VTT !
- **Générateur de rencontres** : Scénarios impromptus selon le biome sélectionné.

### 6. 🌧️ Studio d'Ambiance Sonore & Soundboard SFX
- **Mixeur d'ambiances procédurales** : Synthèse audio dynamique (Pluie battante, Feu de camp, Résonance de donjon, Bourrasque des plaines).
- **Préréglages d'ambiance 1-clic** : *Nuit de Tempête*, *Bivouac Sauvage*, *Crypte Maudite*, *Auberge & Pluie*.
- **Soundboard d'effets sonores (SFX)** : Bruitages synthétisés de lancers de dés, chocs d'acier, sorts magiques, rugissements de monstre et cloche de guérison.

### 7. 👥 Hub Joueurs & Fiches Compagnon
- **Fiches synthétiques des Héros** : Caractéristiques (FOR, DEX, CON, INT, SAG, CHA), CA, Perception passive, PV et compétences clés.
- **Gestionnaire d'indices & Handouts** : Documents, lettres et cartes cachées avec bascule de statut secret/partagé aux joueurs.

---

## 📱 Ergonomie & Design

- **Design Arcane Sombre & Luxueux** : Palette soignée aux teintes or chaud (`#E5A853`), pourpre arcanique (`#8B5CF6`) et noir grimoire (`#0A0C10`).
- **Adaptatif Téléphone & Tablette** : Navigation par barre inférieure sur mobile et *Navigation Rail* sur écran large / tablette.
- **Edge-to-Edge** : Prise en charge native des encoches d'écran et des barres système Android.
