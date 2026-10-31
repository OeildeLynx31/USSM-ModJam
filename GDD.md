# GDD — Mod Jam USSM

Version 0.3 — 26/10/2026 — Minecraft 1.21.1, NeoForge 21.1.262, GeckoLib 4.8.4
Nom du mod : à définir (mod_id actuel : `examplemod`)

Légende des règles :
- **[Discord]** : demandé dans le salon d'idées (citations en 1.3).
- **[Demande]** : demandé le 26/10 (D14 à D26, citations en 1.3).
- **[Existant]** : déjà dans le code.
- **[Proposition]** : idée à valider par l'équipe.

Tous les noms d'API cités ont été vérifiés dans les sources de NeoForge 21.1.262, de GeckoLib 4.8.4 et dans les mappings officiels de Minecraft 1.21.1. Les points marqués « à vérifier » sont les seuls que je n'ai pas pu confirmer.

Depuis la version 0.2, les sections 4 et 5 décrivent ce qui est implémenté, avec les valeurs mesurées sur une génération réelle (§18). La version 0.3 ajoute les demandes du 26/10 : un katana plus fort (§10), les failles du Bloom (§5.3) et les zones de miasme (§4.9).

## Sommaire

1. Résumé
2. État actuel du projet
3. Boucle de jeu et progression
4. L'Entre-monde
5. Accès et sortie
6. Santé mentale
7. Potions de santé mentale
8. Effet Hallucinations
   - 8 bis. Imitateurs
9. Combinaison Hazmat et masque à gaz
10. Katana
11. Hérisson
12. Succès
13. Contenu à créer
14. Architecture technique et performance
15. Planning
16. Risques
17. Questions ouvertes
18. État d'implémentation

---

## 1. Résumé

### 1.1 Pitch

Sous le vide, il y a l'Entre-monde. En surface, le Gloom : une plaine corrompue, noyée dans un brouillard étouffant. En dessous, le Bloom : des cavernes couvertes de fleurs. Pour y entrer, il faut survivre 10 secondes au vide. Une fois sur place, la santé mentale s'effrite. Le joueur alterne entre la surface hostile, où il farme ses katanas, et les cavernes, où il récupère, cueille de quoi brasser ses potions et croise des hérissons.

### 1.2 Piliers

| Pilier | Ce que le joueur ressent | Systèmes |
|---|---|---|
| Oppression | « Je ne vois rien, et ma tête me lâche. » | Brouillard du Gloom, santé mentale, hallucinations |
| Répit | « Enfin un endroit où souffler. » | Bloom, fleurs, potions, hérisson |
| Mouvement | « Je traverse tout. » | Katana et son dash |

### 1.3 Demandes de référence (Discord, Skyle999)

| Réf. | Date | Citation |
|---|---|---|
| D1 | 20/10 16:55 | « Une dimension assez plate, 2 biomes : à la surface la corruption (jcrois c'est le Gloom) et en dessous dans les caves le Bloom (les fleurs) » |
| D2 | 20/10 16:55 | « Il faudra également un brouillard très opressant juste à la surface » |
| D3 | 20/10 16:55 | « On peut accéder à la dimension en survivant pendant 10s aux dégâts du void (ou alors en atteignant des coordonnées bien en dessous de la bedrock » |
| D4 | 21/10 14:58 | « Combinaison Hazmat tah escape the backrooms qui ralentit les variations de santé mentale, ne peut pas être combiné avec l'effet des potions qui altère la perte/gain de santé mentale » |
| D5 | 21/10 14:58 | « ET COMME CHAQUE COMBI HAZMAT C'EST AVEC UN MASQUE A GAZ » |
| D6 | 20/10 16:58 | « Ajouter un katana au jeu » |
| D7 | 20/10 16:58 | « Le principe du katana est de dash en attaquant tout sur son passage » |
| D8 | 20/10 16:58 | « lorsqu'on a un katana dans notre main le slot secondaire (main gauche) devient inutilisable SAUF si on y met un autre katana » |
| D9 | 20/10 16:58 | « 1 katana = pas beaucoup de dégâts mais moins de cooldown » ; « 2 katanas = + de dégâts mais plus de cooldown » ; « il faut quand même que 2 katanas soient plus avantageux qu'un car il faut les farms » |
| D10 | 20/10 16:54 | Succès : « Welcome to the in-between world », « Ouch, my head », « Fast and sharp », « What is this ? » |
| D11 | 25/10 | « Il faut aussi ajt un hérisson » |
| D12 | 20/10 17:03 | « Dans le biome Gloom (corruption si je me trompe pas), il est possible de croiser des monstres qui peuvent copier des joueurs (comportement, stuff) qu'ils ont croisé récemment. Ils attaquent les autres joueurs au bout d'une durée aléatoire. » |
| D13 | 20/10 17:03 | « Moins on a de santé mentale + on a de chance d'en croiser » |
| C1 | 25/10 | « Il faudra que le code soit écrit de manière optimisée » |
| D14 | 26/10 | « Le katana est trop faible ». Options retenues : meilleures stats, enchantements appliqués au dash, recharge du dash quand il tue, invulnérabilité pendant le dash, parade |
| D15 | 26/10 | Des « failles du Bloom » pour sortir de la dimension, en gardant la sortie par le haut (au-dessus de Y 256) |
| D16 | 26/10 | « Des zones de brouillard violet assez opaque qui mettent 0,5 coeur de dégât toutes les 3 s et qui font baisser super vite la santé mentale » |
| D17 | 26/10 | « Les dégâts du brouillard sont évités avec la combi hazmat » |
| D18 | 26/10 | « Il faut que si on fasse /blue ça ouvre un GUI avec toutes les options » |
| D19 | 26/10 | « En vrai on reste trop longtemps en l'air avec le katana, faut aussi qu'on puisse dash peu importe la direction dans laquelle on regarde (haut bas gauche droite etc) » |
| D20 | 26/10 | « Un seul katana c'est trop nul » |
| D21 | 26/10 | « Pas assez de particules pour le brouillard empoisonné » |
| D22 | 26/10 | « Pour le katana il faut que quand on tue un joueur ça reset le cooldown, mais que ça le réduise juste si on tue un mob » |
| D23 | 26/10 | « Il ne doit pas y avoir de mobs dans les caves ». Précisé : aucun monstre sous terre dans l'Entre-monde (Bloom et tunnels du Gloom) ; hérissons et chauves-souris gardés |
| D24 | 26/10 | « C'est pas rare du tout les failles, j'en ai 3 à côté » |
| D25 | 26/10 | « Il faut que l'overworld reset la santé mentale » |
| D26 | 26/10 | « Pas de dégâts de chute après le dash » |

La santé mentale, les potions qui modifient sa perte ou son gain, et l'effet hallucinations sont cités (D4, D10, D13) mais jamais décrits. Leur conception ci-dessous est donc une proposition.

### 1.4 Périmètre

| Fonctionnalité | Source | Priorité |
|---|---|---|
| L'Entre-monde : terrain plat, Gloom et Bloom, brouillard en surface | D1, D2 | Must |
| Accès en survivant 10 s au vide | D3 | Must |
| Sortie de la dimension | Proposition (sinon on reste bloqué) | Must |
| Santé mentale et HUD | implicite (D4, D10) | Must |
| Potions de santé mentale | implicite (D4) | Must |
| Effet Hallucinations | implicite (D10) | Must |
| Combinaison Hazmat et masque à gaz | D4, D5 | Must |
| Katana : dash, main gauche, double katana | D6 à D9 | Must |
| Hérisson | D11 | Must |
| Imitateurs du Gloom, plus fréquents quand la santé mentale baisse | D12, D13 | Must |
| Les 4 succès | D10 | Must |
| Katana renforcé : stats, enchantements, recharge au kill, invulnérabilité, parade | D14 | Must |
| Failles du Bloom (sortie vers l'overworld) | D15 | Must |
| Zones de miasme | D16, D17 | Must |
| Commande `/blue` : écran de toutes les options | D18 | Must |
| Dash dans la direction du regard, suspension raccourcie | D19 | Must |
| Katana seul renforcé, les deux katanas restant meilleurs | D20 | Must |
| Plus de particules dans le miasme | D21 | Must |
| Kill d'un joueur : cooldown remis à zéro ; kill d'un mob : cooldown réduit | D22 | Must |
| Aucun monstre sous terre dans l'Entre-monde | D23 | Must |
| Failles du Bloom rares | D24 | Must |
| L'overworld remet la santé mentale à 100 | D25 | Must |
| Pas de dégâts de chute après un dash | D26 | Must |

---

## 2. État actuel du projet

### 2.1 Ce qui existe (branche `main`, commit `4c0dab0`, travail d'OeildeLynx31)

| Élément | Détail |
|---|---|
| Dimension `undervoid` | DimensionType `undervoid_type` (minY 0, hauteur 256, lits désactivés, `ambientLight` 1.0, effets du Nether), un seul biome `test_biome`, noise settings `undervoid.json` (copie de l'overworld) |
| Blocs | `dark_dirt`, `void_log`, `void_wood`, `void_planks`, `void_leaves`, `void_sapling` |
| Item | `spawner_receptacle` (rôle non documenté) |
| Effets | registre `ModEffects` vide |
| Téléportation | `ModEvents` : dans l'End, Y < 0 → Entre-monde en Y 250 ; dans l'Entre-monde, Y > 256 → End en Y 1 |
| Datagen | providers complets (blockstates, modèles, loot tables, tags, worldgen). OeildeLynx31 a écrit « Juste ne TOUCHES PAS à la datagen, sinon ban » : chaque section liste donc les entrées à ajouter, sans réorganiser les providers. |

### 2.2 Écarts à corriger

| # | Problème | Conséquence | Correction | Priorité |
|---|---|---|---|---|
| 1 | `src/generated/resources` n'est pas versionné | Un clone neuf ou la CI (`./gradlew build`) produit un jar sans dimension, sans biome, sans modèles ni loot tables | Lancer `runData` et versionner le dossier généré | Bloquant pour la remise |
| 2 | `EntityTickEvent.Post` traité pour toutes les entités | Coût à chaque tick pour chaque entité chargée ; objets et mobs téléportés aussi | `PlayerTickEvent.Post`, joueurs seulement (§5) | Haute |
| 3 | Accès : End, Y < 0, immédiat | Ne respecte pas D3, et sauve tout joueur qui tombe dans le vide de l'End | Règle des 10 s (§5) | Haute |
| 4 | Sortie : End en Y 1, au-dessus du vide | Le joueur retombe dans le vide, donc dans l'Entre-monde : boucle | Retour au point de réapparition (§5) | Haute |
| 5 | `ambientLight` 1.0 et `NETHER_EFFECTS` | Tout est éclairé et le brouillard est partout, alors que D2 le veut « juste à la surface » | §4.5 et §4.6 | Haute |
| 6 | Noise settings en −64/384, dimension en 0/256 ; surface rules de l'overworld ; un seul biome | Relief d'overworld coupé à 0 et 256, pas de Gloom ni de Bloom | §4.3 | Haute |
| 7 | `void_sapling` utilise `TreeGrower.OAK` | Le pousse du vide donne un chêne | `TreeGrower` dédié (§4.4) | Moyenne |
| 8 | Restes du template : mod_id et package `examplemod`, `Config` d'exemple, `DeferredRegister` BLOCKS et ITEMS inutilisés dans `ExampleMod`, logs « HELLO », lang du template, `spawner_receptacle` ajouté à l'onglet Construction | Confusion, noms visibles en jeu (« Example Mod Tab ») | Nettoyage ; renommage du mod_id à décider tôt (§17) | Moyenne |
| 9 | Code de tutoriel commenté (sapphire, pine) | Bruit | À supprimer | Basse |
| 10 | Clés de lang manquantes (blocs du vide, tooltip du `spawner_receptacle`) | Noms bruts en jeu | Compléter `en_us` et ajouter `fr_fr` | Moyenne |

Au 25/10/2026, les écarts 2 à 7 sont corrigés, et le n° 10 l'est sauf pour l'infobulle du `spawner_receptacle` : sa clé `tooltip.examplemod.spawner_receptacle_tooltip` n'a toujours pas de texte, faute de connaître le rôle de l'objet (§17, question 5). Restent aussi le n° 1 (versionner le dossier généré), le n° 8 (mod_id et nom du mod) et une partie du n° 9 (code de tutoriel commenté dans `ModBiomeModifiers` et `ModBlocks`, laissé à OeildeLynx31). Voir §18.

---

## 3. Boucle de jeu et progression

### 3.1 Parcours type

1. **Préparation** (overworld) : potions de soin, pommes dorées, éventuellement la combinaison Hazmat.
2. **La chute** : le joueur saute dans le vide et tient 10 s (§5). Succès « Welcome to the in-between world ».
3. **Le Gloom** : brouillard, obscurité, monstres. La santé mentale baisse dès la première seconde (« Ouch, my head »). Les squelettes lâchent des fragments de lame (§10). Des nappes de miasme violet sont à contourner, ou à traverser en combinaison (§4.9).
4. **Descente vers le Bloom** : environ 30 à 40 blocs de creusage. La santé remonte, on trouve des fleurs du Bloom (potions), des baies lumineuses et des hérissons.
5. **Allers-retours** : farm en surface, récupération en dessous. Le katana arrive (« Fast and sharp »). Rester trop longtemps en surface déclenche des hallucinations (« What is this ? »).
6. **Sortie** : passer par une faille du Bloom, dans les cavernes, ou remonter au sommet de la dimension (§5.3).

### 3.2 Rythme

| Scénario | De 100 à 40 (hallucinations) | De 100 à 0 |
|---|---|---|
| Surface du Gloom, sans protection | 2 min 30 | 4 min 10 |
| Surface, combinaison ou Sérénité I | 5 min | 8 min 20 |
| Surface, Sérénité II | 10 min | 16 min 40 |
| Sous-sol du Gloom, sans protection | 5 min | 8 min 20 |
| Miasme, sans protection | 20 s | 33 s |
| Miasme, combinaison ou Sérénité I | 40 s | 67 s |

| Récupération de 40 à 100 | Durée |
|---|---|
| Bloom | 4 min |
| Bloom avec Clarté I | 2 min |
| Bloom en combinaison | 8 min |
| Overworld | immédiat (D25) |
| Nether ou End | 10 min |

Le détail des calculs est en §6.

### 3.3 Point d'attention : l'accès est de fin de partie

En survie, le seul vide atteignable est celui de l'End : la bedrock de l'overworld et du Nether est incassable. L'Entre-monde demande donc d'avoir trouvé un portail de l'End. C'est cohérent avec le code actuel, mais un juré de la jam risque de ne jamais y arriver. Il faut un plan pour les jurés (§17, question 3). Par exemple : un monde de démo, une page « comment tester » avec la règle du créatif (§5.1), ou un accès plus précoce.

---

## 4. L'Entre-monde

**Sources** : D1, D2, D16, D17. **[Existant]** dimension `undervoid`, blocs du vide, `dark_dirt`.

### 4.1 Intention

Une plaine plate et sombre où l'on ne voit qu'à quelques blocs, avec la sensation d'être observé. Sous les pieds, des cavernes lumineuses et vivantes, où l'on vient reprendre son souffle.

### 4.2 Règles

1. [Discord] Dimension assez plate, avec deux biomes : `gloom` en surface (la corruption) et `bloom` dans les cavernes (les fleurs).
2. [Discord] Brouillard très oppressant, uniquement à la surface du Gloom.
3. [Existant] Les lits explosent (`bedWorks` false) et les ancres de réapparition ne marchent pas.
4. [Proposition] Id technique conservé (`undervoid`), nom affiché « The In-Between » / « L'Entre-monde ».
5. [Proposition] Pas d'eau, pas de minerais : la ressource de la dimension, ce sont ses blocs, ses fleurs et ses mobs.
6. [Demande] Des zones de miasme violet parsèment la surface du Gloom (D16, D17, §4.9), et des failles du Bloom mènent à l'overworld (D15, §5.3).

### 4.3 Génération

**Profil vertical** (mesuré sur 144 chunks générés ; la dimension garde minY 0 et 256 de hauteur)

| Y | Couche | Biome |
|---|---|---|
| 0 à 4 | Bedrock (dégradé) | Bloom |
| 5 à environ 70 | Grandes cavernes reliées entre elles, surtout entre Y 20 et 60 (35 à 47 % de vide) | Bloom |
| environ 75 à 108 | Sous-sol de deepslate, tunnels fins entre Y 84 et 100 (3 à 6 % de vide) | Gloom |
| 109 à 115 | Surface du Gloom, relief de ±3 blocs autour de Y 112 | Gloom |
| 116 à 256 | Air et brouillard | Gloom |

Sous une zone de miasme, le biome `miasma` remplace le Gloom au-dessus de Y 93 environ (§4.9).

De la surface au plafond des cavernes, il faut creuser 45 à 60 blocs : un peu plus que les 30 à 40 prévus en §3.1, car la couche du Gloom fait 32 blocs à elle seule.

**Noise settings** (`data/examplemod/worldgen/noise_settings/undervoid.json`, écrit à la main comme avant)

| Champ | Avant | Maintenant |
|---|---|---|
| `noise.min_y` / `noise.height` | −64 / 384 | 0 / 256 (aligné sur la DimensionType) |
| `sea_level` | 63 | 76 : les chauves-souris n'apparaissent que sous le niveau de la mer |
| `default_fluid` | eau | air : aucune eau, quel que soit le niveau de la mer |
| `aquifers_enabled` / `ore_veins_enabled` | true / true | false / false |
| `default_block` | stone | deepslate |
| `noise_router.temperature`, `continents`, `erosion`, `ridges` | overworld | 0 (constantes) |
| `noise_router.vegetation` | overworld | bruit 2D `examplemod:miasma` (`xz_scale` 1,5, `y_scale` 0), qui place les zones de miasme (§4.9) |
| `noise_router.depth` | `overworld/depth` | gradient de Y 16 (1,0) à Y 95 (0,0), pour placer les biomes |
| `noise_router.final_density` | overworld | `min(interpolated(min(terrain, caverns)), tunnels)` |
| `surface_rule` | règles de l'overworld | règles Gloom et Bloom (voir plus bas) |

**Fonctions de densité** (`data/examplemod/worldgen/density_function/undervoid/`, écrites à la main)
- `terrain` : `y_clamped_gradient` de Y 100 (1,0) à Y 124 (−1,0), plus 0,25 × le bruit vanilla `minecraft:surface` borné à ±1 et mis en `cache_2d`. La surface tombe vers Y 112, avec un relief de ±3 blocs.
- `caverns` : bruit maison `examplemod:bloom_caverns` (`worldgen/noise`, octaves de 64, 32 et 16 blocs) décalé de +0,1, avec deux rampes qui referment les cavernes, de Y 10 à 26 en bas et de Y 62 à 78 en haut. Hors de Y 0 à 80, la fonction renvoie 1 sans calculer le bruit. Le bruit vanilla `cave_cheese` a été écarté : son octave de 256 blocs donnait soit une salle géante, soit 100 blocs de roche pleine.
- `tunnels` : les bruits vanilla des « noodle caves » (`noodle`, `noodle_ridge_a`, `noodle_ridge_b`), un peu plus larges et plus fréquents que dans l'overworld, limités à Y 76 à 108 et refermés par des rampes. Ils ne percent ni la surface ni le plafond du Bloom.

Les réglages ont été choisis en comparant des coupes de plusieurs variantes, rendues en image depuis un serveur de test.

**Placement des biomes** : dans `ModDimensions.bootstrapStem`, la `MultiNoiseBiomeSource` reçoit des plages (`Climate.Parameter.span`) sur deux paramètres, `humidity` (le bruit `vegetation`) et `depth` (5ᵉ argument de `Climate.parameters`, ordre vérifié). Tous les autres sont à 0 (`Parameter.point(0)`).

| Biome | `humidity` | `depth` | Où |
|---|---|---|---|
| `gloom` | −1 à 0,35 | −1 à 0,25 | surface et sous-sol, hors des zones |
| `gloom` | 0,35 à 1 | 0,02 à 0,25 | sous-sol, sous une zone de miasme |
| `miasma` | 0,35 à 1 | −1 à 0,02 | zones de miasme, au-dessus de Y 93 environ |
| `bloom` | −1 à 1 | 0,25 à 1 | cavernes |

La bascule du Bloom (`depth` 0,25) tombe vers Y 75 : le Bloom couvre les cellules de biome jusqu'à Y 72 et le Gloom commence à Y 76. Celle du miasme (`depth` 0,02) tombe vers Y 93 : le gradient va de 1 en Y 16 à 0 en Y 95. Le flou de `BiomeManager` déplace les frontières d'environ 2 blocs.

**Surface rules** (dans cet ordre)
1. Bedrock en bas : `vertical_gradient` de `above_bottom` 0 à 5.
2. Biome `gloom` ou `miasma`, au-dessus de Y 96 : sol → `dark_dirt` sur 1 à 7 blocs (`stone_depth` avec `add_surface_depth`). Les tunnels gardent leur deepslate.
3. Biome `bloom` : sol → `moss_block`, avec 1 à 2 blocs de `dirt` dessous.
4. Partout ailleurs : `deepslate` (le `default_block`).

### 4.4 Contenu et features

| Élément | Biome | Mise en œuvre |
|---|---|---|
| Arbres du vide `void_tree` | Gloom | Configured feature `tree` : `void_log` et `void_leaves`, tronc droit de 5 à 7. Placement `treePlacement` à 0,5 par chunk (mesuré : 0,49) |
| `void_sapling` | — | `ModTreeGrowers.VOID` = `new TreeGrower("examplemod:void", Optional.empty(), Optional.of(VOID_TREE_KEY), Optional.empty())`. Sur la terre sombre, il donne un arbre du vide et la terre reste sombre |
| Buissons morts | Gloom, miasme | Placed feature vanilla `patch_dead_bush_2` (environ 2 par chunk). Le miasme n'a pas d'arbres du vide |
| `bloom_flower` | Bloom | Fleur lumineuse (lumière 7), posée sur la mousse, et sa version en pot. Patch de 64 essais (écart horizontal 6, vertical 2), 8 tentatives par chunk entre Y 16 et 68. Il passe avant la végétation vanilla, sinon l'herbe prend toute la place : environ 16 fleurs par chunk. Ingrédient de la Potion de sérénité (§7) |
| Végétation de grotte luxuriante | Bloom | Placed features vanilla réutilisées sans nouveau JSON : `glow_lichen`, `lush_caves_ceiling_vegetation`, `cave_vines` (baies lumineuses), `lush_caves_vegetation`, `spore_blossom`, `classic_vines_cave_feature` |
| Failles du Bloom `bloom_rift` | Bloom | Colonne de 3 blocs `bloom_rift`, portail vers l'overworld (§5.3). Placed feature `bloom_rift_placed`, à l'étape `UNDERGROUND_DECORATION` |

Avec ces lumières, 35 % de l'air des cavernes reste à lumière 0 : des squelettes peuvent y apparaître (§4.7).

Tags et recettes ajoutés pour que les blocs du vide se comportent comme leurs équivalents vanilla :
- `dark_dirt` dans `minecraft:dirt` (pousses et buissons) et `mineable/shovel` ;
- `void_log` et `void_wood` dans `examplemod:void_logs`, inclus dans `minecraft:logs_that_burn`. Sans le tag des bûches, les feuilles du vide se croient sans tronc et pourrissent ;
- `void_planks` dans `minecraft:planks`, `void_leaves` dans `minecraft:leaves` et `mineable/hoe`, `void_sapling` dans `minecraft:saplings` ;
- recettes : bûche du vide → 4 planches, 4 bûches → 3 bois. Les planches du vide n'avaient pas de recette, alors que le katana en demande une (§10.7).

### 4.5 Brouillard

[Discord] Brouillard très oppressant à la surface seulement.

| | Surface du Gloom (Y des yeux ≥ 104) | Idem, avec le masque à gaz | Sous-sol du Gloom et Bloom |
|---|---|---|---|
| Début du brouillard (`near`) | 0 bloc | 0 bloc | 8 blocs |
| Fin du brouillard (`far`) | 14 blocs | 24 blocs | 56 blocs |
| Couleur (`fogColor` du biome) | `0x3B3547` (gris violacé) | idem | Bloom : `0x16302A` (vert sombre) |

La couleur `0x1B1820` prévue au départ se confondait avec l'obscurité (lumière ambiante 0,1) : la surface paraissait noire. `0x3B3547` se lit comme une brume, et les silhouettes des arbres s'y découpent (vérifié sur captures en jeu).

Mise en œuvre, côté client uniquement (`client/UndervoidEffects`, `client/UndervoidFog`) :
- Les effets `NETHER_EFFECTS` (brouillard partout) sont remplacés par `examplemod:undervoid`, enregistrés via `RegisterDimensionSpecialEffectsEvent#register`. La sous-classe de `DimensionSpecialEffects(Float.NaN, false, SkyType.NONE, false, false)` renvoie false dans `isFoggyAt` et null dans `getSunriseColor` : la dimension est figée à 12000 (coucher du soleil) et le brouillard virait à l'orange vers l'ouest.
- Distances : `ViewportEvent.RenderFog`, avec `setNearPlaneDistance`, `setFarPlaneDistance` et une forme `SPHERE`. **Il faut appeler `setCanceled(true)`**, sinon les changements sont ignorés. On garde le minimum avec les valeurs vanilla : la cécité, l'obscurité, l'eau et la lave gardent leur propre brouillard.
- Couleurs : celles des biomes. `FogRenderer` fait déjà la transition douce entre deux biomes.
- Transition : un facteur entre 0 et 1, mis à jour dans `ClientTickEvent.Post` (biome aux yeux lu une fois par tick, jamais à chaque frame), qui varie de 0,05 par tick et est interpolé entre deux ticks. Passer de la surface au sous-sol prend donc 1 s. À l'entrée dans la dimension, le facteur prend directement sa valeur.
- Le miasme compte comme le Gloom pour ce facteur, et il a son propre brouillard, plus serré (§4.9).

### 4.6 Ambiance

| Réglage | Avant | Maintenant |
|---|---|---|
| `ambient_light` | 1.0 | 0.1, comme le Nether |
| `effects` | `minecraft:the_nether` | `examplemod:undervoid` |

| Biome | Ciel / eau / herbe / feuillage | Particules d'ambiance | Son |
|---|---|---|---|
| Gloom | `0x000000` / `0x2A2438` / `0x3A3340` / `0x2E2836` | `WHITE_ASH`, probabilité 0,03 | Mood sound de grotte, pas de musique |
| Bloom | `0x000000` / `0x3FA88A` / `0x5FBF7F` / `0x4FAF6F` | `SPORE_BLOSSOM_AIR`, 0,01 | Mood sound, musique des grottes luxuriantes vanilla |
| Miasme | `0x000000` / `0x5A2A7A` / `0x4A2E5C` / `0x3E2650` | `examplemod:miasma` (violet clair, lumineux), 0,04, plus l'émetteur du client (§4.9) | Mood sound de grotte, pas de musique |

Sans musique de biome, le jeu retombe sur `Musics.GAME`. `UndervoidFog` coupe donc la musique via `SelectMusicEvent` tant que le joueur est dans le Gloom ou le miasme.

### 4.7 Spawns

[Existant] `MonsterSettings` : les monstres n'apparaissent qu'à lumière 0. La surface du Gloom, sans lumière du ciel, est donc infestée.

[Demande, D23] Aucun monstre sous terre dans l'Entre-monde : ni dans le Bloom, ni dans les tunnels sous le Gloom et le miasme. `ModEntityEvents` écoute `LevelEvent.PotentialSpawns` et annule la liste de la catégorie `MONSTER` quand la position est sous la surface de sa colonne (heightmap `MOTION_BLOCKING_NO_LEAVES`, déjà calculée par le jeu : une lecture par essai de spawn, sans scan). Les créatures (hérissons) et l'ambiance (chauves-souris) ne sont pas touchées. Les imitateurs ne naissent qu'en surface (§8 bis.4). Les spawners de monstres et les mobs qui descendent depuis la surface restent possibles. Vérifié par GameTest le 26/10 : surface acceptée, tunnel 8 blocs sous la surface et Bloom refusés, hérisson accepté dans le Bloom, aucun imitateur pour un joueur à Y 40.

| Biome | Monstres (poids, taille des groupes) | Créatures et ambiance |
|---|---|---|
| Gloom | zombie 100 (4), squelette 100 (4, fragments de lame, §10), araignée 60 (4), enderman 20 (1 à 4) | aucune |
| Miasme | comme le Gloom | aucune |
| Gloom et miasme, santé mentale sous 70 | imitateur : hors de la liste de spawns du biome, tirage une fois par seconde et par joueur, un seul à moins de 48 blocs (§8 bis) | — |
| Bloom | aucun (squelette 20 retiré par D23) | hérisson 8 (1 à 3, §11), chauve-souris 10 (2 à 4) |

### 4.8 Ressources et datagen

| Type | Entrées |
|---|---|
| JSON à la main | `noise_settings/undervoid.json` (refonte), `density_function/undervoid/terrain`, `caverns`, `tunnels`, `noise/bloom_caverns`, tags et recettes du §4.4 |
| Datagen worldgen | biomes `gloom` et `bloom` (à la place de `test_biome`), level stem à 3 biomes (4 entrées, §4.3), configured features `void_tree` et `bloom_flower_patch`, placed features `void_tree_placed` et `bloom_flower_patch_placed` |
| Datagen DimensionType | `ambient_light` 0.1, `effects` `examplemod:undervoid` |
| Datagen blocs | `potted_bloom_flower` : blockstate, modèle `flower_pot_cross` en `cutout`, loot `createPotFlowerItemTable` |
| Textures | `bloom_flower.png` (déjà là ; le pot la réutilise) |
| Lang | `block.examplemod.potted_bloom_flower` (les biomes et la fleur avaient déjà leurs clés) |
| Miasme et failles | voir §4.9 et §5.3 |

### 4.9 Zones de miasme

**Sources** : D16, D17.

**Intention.** Des nappes de brouillard violet, assez denses pour qu'on les voie arriver de loin, qui forcent un choix : contourner, traverser vite, ou s'équiper.

**Génération**
- Biome `miasma`, sous-biome de la surface du Gloom, placé par le bruit `vegetation` (paramètre `humidity`) au-dessus de 0,35 (§4.3).
- Bruit `data/examplemod/worldgen/noise/miasma.json` : `firstOctave` −6 (échelle de 64 blocs), amplitudes `[1.0, 0.5]`, lu avec `xz_scale` 1,5 dans le `noise_router` (soit environ 43 blocs). Il est en 2D (`y_scale` 0) : une zone garde la même forme de la surface jusqu'à Y 93 environ. En dessous, c'est de nouveau le sous-sol du Gloom.
- Objectif : 10 à 15 % de la surface, en taches de 20 à 60 blocs de large. Réglé le 26/10 sur le vrai bruit (les valeurs de départ, octave −7 et seuil 0,4, donnaient 8 % en taches de 60 à 100 blocs). Mesuré avec les valeurs finales : 13 % de la surface sur 16 km² ; sur 24 × 24 chunks générés, 11 % en taches de 27 blocs de diamètre médian, 86 % du miasme dans des taches de 20 à 60 blocs, la plus grande vers 60 (rarement 100 sur de grandes surfaces).
- Même sol que le Gloom (`dark_dirt`), mêmes monstres et mêmes buissons morts, pas d'arbres du vide, pas de précipitations.

**Rendu (client, `UndervoidFog`)**

| | Miasme | Miasme, avec le masque à gaz |
|---|---|---|
| Début du brouillard (`near`) | 0 bloc | idem |
| Fin du brouillard (`far`) | 6 blocs | 9 blocs |
| Couleur (`fogColor`) | `0x6B2FA0` (violet) | idem |

- Un second facteur, lissé de 0,05 par tick comme celui de la surface, mélange ces valeurs avec le brouillard courant : entrer ou sortir d'une zone prend 1 s.
- Le biome aux yeux est celui qui est déjà lu une fois par tick pour la surface : aucune lecture en plus.
- Particules [Demande, D21] : un type maison, `examplemod:miasma` (`particle/ModParticles`, rendu par `client/MiasmaParticle`). C'est une tache violet clair, translucide et toujours éclairée (`LightTexture.FULL_BRIGHT`), car la surface n'a aucune lumière et la poussière vanilla y paraissait noire. Elle vit 2 à 4 s, dérive lentement vers le haut, apparaît et s'efface en fondu. Les textures sont les `generic_0` à `generic_7` vanilla (`assets/examplemod/particles/miasma.json`).
  - Particule d'ambiance du biome (probabilité 0,04) : le jeu la place autour du joueur selon le biome de chaque point, donc on voit la nappe depuis l'extérieur.
  - Émetteur du client, dans `UndervoidFog` : tant que le joueur est dans le miasme, 10 particules par tick (au prorata du facteur de transition) dans une boîte autour de lui, aussi large que la fin du brouillard (6 ou 9 blocs) et moitié moins haute, hors des blocs pleins. Elles ne naissent que là où on les voit, et pas pendant la pause. Environ 1 700 particules dans la zone, contre 850 juste à l'extérieur (capture du 26/10).

**Effets (serveur, `Sanity`, à 1 Hz)**

| Effet | Valeur par défaut | Réglage |
|---|---|---|
| Santé mentale | −3,0 par seconde, à la place de la perte de surface du Gloom ; multiplicateurs habituels (§6.4) | `miasmaSanityLoss` |
| Dégâts | 1 (un demi-cœur) toutes les 3 s, type `examplemod:miasma`, sauf combinaison Hazmat complète | `miasmaDamage`, `miasmaDamageSeconds` |

- Le biome est lu aux yeux, une seule fois par seconde, pour la santé mentale comme pour les dégâts.
- Type de dégât `data/examplemod/damage_type/miasma.json` : `message_id` `examplemod.miasma`, épuisement 0, `scaling` `never`, `effects` `drowning` (son de noyade). Il est ajouté aux tags `minecraft:bypasses_armor` et `minecraft:no_knockback`, comme `insanity`. Messages de mort : « %1$s s'est étouffé dans le miasme » (« … en combattant %2$s »).
- Créatif et spectateur : rien, comme pour toute la santé mentale.

**Chiffres**
- De 100 à 40 (hallucinations) : environ 20 s sans protection, 40 s avec la combinaison complète ou Sérénité I, 80 s avec Sérénité II. De 100 à 0 : 33 s sans protection.
- Dégâts : 0,33 PV par seconde, soit 20 PV par minute avant la régénération naturelle. Les points d'armure ne comptent pas (comme pour `insanity`) ; seule la combinaison complète (D17) bloque les dégâts. Le masque seul ne protège pas, mais il repousse la fin du brouillard de 6 à 9 blocs.
- Les imitateurs peuvent apparaître dans le miasme comme dans le Gloom (§8 bis).

**Fichiers**

| Type | Entrées |
|---|---|
| Datagen worldgen | biome `miasma` dans `ModBiomes`, entrées de `ModDimensions.bootstrapStem` (§4.3) |
| JSON à la main | `noise/miasma.json`, `noise_router.vegetation` et `biome_is` des surface rules dans `noise_settings/undervoid.json`, `damage_type/miasma.json`, tags `bypasses_armor` et `no_knockback` |
| Config serveur | section `miasma`, après `mimic` : `miasmaSanityLoss` (3,0, de 0 à 100), `miasmaDamage` (1,0, de 0 à 20), `miasmaDamageSeconds` (3, de 1 à 60) |
| Lang | `biome.examplemod.miasma`, `death.attack.examplemod.miasma` et `.player`, clés de config, en en_us et fr_fr |

### 4.10 Critères d'acceptation

- [x] Depuis la surface, le terrain ne varie pas de plus de ±4 blocs sur 200 blocs (mesuré : Y 110 à 115 sur 192 × 192 blocs).
- [x] Biome `gloom` en surface, `bloom` sous Y 75 environ.
- [x] En surface, on ne voit pas à plus de 14 blocs ; dans le Bloom, à environ 56 (captures en jeu).
- [ ] Le passage de la surface au sous-sol fait varier le brouillard en douceur, en 1 s environ (code en place, non vu en mouvement).
- [x] Pas d'eau, pas de minerai, pas de relief d'overworld.
- [x] Un pousse du vide sur de la terre sombre donne un arbre du vide.
- [x] Les zones de miasme couvrent 10 à 15 % de la surface, en taches de 20 à 60 blocs, et se voient de l'extérieur (particules violettes). Mesure et capture du 26/10 ; depuis D21, les particules sont lumineuses et bien plus denses dans la zone (capture du 26/10).
- [ ] Dans le miasme, on ne voit pas à plus de 6 blocs (9 avec le masque), et la transition dure 1 s environ. Les 6 blocs sont vus en capture le 26/10 ; le masque et la transition restent à voir.
- [x] Sans protection, la jauge passe de 100 à 40 en 20 s environ et le joueur prend un demi-cœur toutes les 3 s, armure ou non ; message de mort « s'est étouffé dans le miasme ». GameTest du 26/10 : −30 en 10 s, un dégât toutes les 60 ticks ; le message de mort n'a pas été vu.
- [x] Avec la combinaison complète, aucun dégât et une perte deux fois plus lente (GameTest du 26/10).
- [ ] Sous une zone, en dessous de Y 93 environ, on retrouve le sous-sol normal du Gloom.

**Estimation** : 10 à 14 h, surtout pour régler le bruit des cavernes.

---

## 5. Accès et sortie

**Sources** : D3, D15. **[Existant]** `ModEvents` (téléportation End ↔ Entre-monde).

### 5.1 Entrée

**Lecture de D3.** D3 donne deux formulations : « survivre 10 s aux dégâts du void » et « atteindre des coordonnées bien en dessous de la bedrock ». En chute libre (vitesse limite d'environ 3,9 blocs par tick), tomber 10 s représente environ 780 blocs sous la limite du vide. Les deux lectures reviennent donc au même. Je recommande le chronomètre : il colle au « 10s » et reste juste pour un joueur sous Chute lente.

**Règles**
1. [Discord] Dans n'importe quelle dimension sauf l'Entre-monde, un joueur sous `minBuildHeight − 64` (la zone où le vide fait des dégâts) accumule un tick de chrono par tick passé dans la zone.
2. [Discord] À 200 ticks (10 s), s'il est vivant, il est envoyé dans l'Entre-monde.
3. [Proposition] Le chrono revient à 0 quand le joueur sort de la zone, meurt ou se reconnecte.
4. [Proposition] En créatif et en spectateur, le transfert est immédiat à l'entrée dans la zone. Ces joueurs prennent aussi les dégâts du vide, car `out_of_world` passe outre l'invulnérabilité ; sans cette exception, ils mourraient. C'est aussi le moyen le plus simple de tester.
5. [Proposition] Les entités non joueurs (objets, mobs) ne sont pas transférées et disparaissent normalement dans le vide.

**Difficulté** (mécaniques vanilla 1.21.1 vérifiées)
- Le vide inflige 4 dégâts par coup. Le tag `bypasses_cooldown` est vide, donc les frames d'invulnérabilité s'appliquent : environ 1 coup toutes les 10 ticks, soit **8 PV/s**.
- `out_of_world` ignore l'armure, la Résistance et l'invulnérabilité. Le totem ne sauve pas.
- Sur 10 s, le joueur prend environ 21 coups, soit **84 PV**, dont 20 couverts par sa vie de départ. Il faut donc environ 64 PV de soin ou d'absorption. Exemples de préparation :
  - 8 à 9 potions jetables de Soin instantané II (8 PV chacune) ;
  - ou une pomme de Notch (Absorption IV : 16 PV ; Régénération II : environ 8 PV sur 10 s) et 5 potions jetables de Soin II.

**Arrivée**
1. Mêmes X et Z (`coordinate_scale` 1).
2. On force la génération du chunk (`level.getChunk(x >> 4, z >> 4)`), puis Y = `getHeight(Heightmap.Types.MOTION_BLOCKING, x, z)`. Cette méthode renvoie déjà la case au-dessus du bloc le plus haut ; les feuilles comptent, pour ne pas atterrir dans un feuillage. Valeur de secours : Y 120.
3. Le joueur descend d'abord de sa monture et reprend sa propre caméra s'il est spectateur, comme dans `ServerPlayer.teleportTo` (sinon il reste lié à une monture restée dans l'End). Puis `changeDimension(new DimensionTransition(level, pos, Vec3.ZERO, yRot, xRot, DimensionTransition.PLAY_PORTAL_SOUND))` et remise à zéro de la distance de chute. La sortie fait de même.
4. Santé mentale −10 (§6).
5. [Proposition] Obscurité 3 s pour l'effet dramatique, et Régénération I 5 s pour ne pas arriver à 1 PV au milieu des monstres.

### 5.2 Mise en œuvre

| Élément | Choix |
|---|---|
| Événement | `PlayerTickEvent.Post`, côté serveur seulement, dans `network/ModEvents` (l'`EntityTickEvent` est supprimé) |
| Coût hors du vide | une comparaison de Y par joueur et par tick : on sort tout de suite si le joueur n'est pas sous la limite |
| État | attachment `void_ticks` (int, ni sérialisé ni synchronisé), retiré dès que le joueur sort de la zone |
| Réglage | `voidSurvivalTicks` (200) dans la config serveur |
| Réseau | aucun paquet custom |

### 5.3 Sortie

D3 ne dit rien sur la sortie. D15 ajoute les failles du Bloom et garde la sortie par le haut. Il y a donc trois sorties : les failles, le haut de la dimension et la mort.

**Failles du Bloom** [Demande, D15]

1. Une faille est une déchirure verticale et lumineuse de 3 blocs de haut, posée sur le sol des cavernes du Bloom. Elle ne se casse pas en survie, ne se pousse pas au piston, résiste aux explosions, au wither et au dragon (tags `wither_immune` et `dragon_immune`), ne se laisse pas remplacer par l'eau ni la lave (`forceSolidOn`), ne lâche rien et ne s'obtient pas : pas d'item, et le clic molette ne donne rien.
2. Rester dedans 1,5 s (30 ticks ; immédiat en créatif) envoie le joueur dans l'overworld **avec son inventaire**, aux mêmes X et Z, sur le bloc le plus haut (`MOTION_BLOCKING`, feuilles comprises). Si la colonne est vide, il arrive au spawn du monde. Il garde son orientation.
3. Pendant l'attente, l'écran ondule comme dans un portail du Nether (transition `CONFUSION`, son `PORTAL_TRIGGER`) ; à l'arrivée, on entend `PORTAL_TRAVEL`. Sortir de la faille fait redescendre le compteur de 4 ticks par tick (`PortalProcessor` vanilla).
4. Seuls les joueurs passent : pas les mobs ni les objets. Un joueur sur une monture ne passe pas (`canUsePortal(false)`).
5. Contrairement à la sortie par le haut, on n'arrive pas à son point de réapparition mais aux coordonnées de la faille, souvent loin de sa base.

| Élément | Choix |
|---|---|
| `block/custom/BloomRiftBlock` | étend `Block` et implémente `Portal`, l'interface vanilla du portail du Nether et de la passerelle de l'End |
| Propriétés | sans collision, incassable (`strength(-1, 3600000)`), lumière 12, sans loot table, `noOcclusion`, `PushReaction.BLOCK`, aucun spawn dessus. Contour `Block.box(4, 0, 4, 12, 16, 12)`. `getCloneItemStack` renvoie `ItemStack.EMPTY` |
| `entityInside` | si `entity.canUsePortal(false)`, `entity.setAsInsidePortal(this, pos)` : l'attente, les sons et le cooldown de portail sont ceux du jeu |
| `getPortalTransitionTime` | 1 tick pour un joueur invulnérable (créatif), 30 pour les autres joueurs |
| `getPortalDestination` | null si ce n'est pas un `ServerPlayer`. Sinon l'overworld : chunk forcé par `getChunk`, Y = `getHeight(Heightmap.Types.MOTION_BLOCKING, x, z)`, puis `new DimensionTransition(overworld, pos, Vec3.ZERO, yRot, xRot, PLAY_PORTAL_SOUND.then(PLACE_PORTAL_TICKET))` |
| `animateTick` (client) | quelques `REVERSE_PORTAL` par tick, parfois un `END_ROD`, et `PORTAL_AMBIENT` (volume 0,4, hauteur 1,6) avec 1 chance sur 80 par appel. Le client n'appelle `animateTick` qu'au hasard, ce qui donne un son toutes les 8 à 20 s environ par faille |
| `ModBlocks.BLOOM_RIFT` | `BLOCKS.register("bloom_rift", …)`, sans item |

Génération :
- Configured feature `bloom_rift` : `Feature.BLOCK_COLUMN`, `BlockColumnConfiguration.simple(ConstantInt.of(3), BlockStateProvider.simple(BLOOM_RIFT))`.
- Placed feature `bloom_rift_placed`, ajoutée au biome `bloom` à l'étape `UNDERGROUND_DECORATION` : un essai tous les 20 chunks en moyenne (`RarityFilter.onAverageOnceEvery(20)`, 2 avant D24), position au hasard dans le chunk entre Y 20 et 60, descente dans l'air jusqu'à un bloc à face supérieure solide (32 blocs au plus), décalage d'un bloc vers le haut, 4 cases d'air exigées, biome Bloom.
- Résultat : la faille est posée sur le sol, avec au moins un bloc d'air au-dessus. Un essai qui tombe dans la roche ne donne rien : environ un essai sur cinq aboutit. Mesuré le 26/10 sur 2 304 chunks avec un essai tous les 2 chunks : 233 failles, soit une tous les 10 chunks environ, toutes de 3 blocs et posées sur le sol. Jugé bien trop fréquent en test (D24, « j'en ai 3 à côté ») : avec un essai tous les 20 chunks, 9 failles sur 1 024 chunks, soit une tous les 110 chunks environ (GameTest du 26/10), une faille tous les 170 blocs en moyenne. Les valeurs de départ (un essai tous les 6 chunks, décalage de 2) donnaient une faille tous les 23 chunks, qui flottait au-dessus d'un trou rempli ensuite d'herbe ou de fleurs.

Ressources :
- Texture animée `textures/block/bloom_rift.png` : bande verticale de 16 images de 16×16 (16×256), déchirure en zigzag au cœur blanc-turquoise et aux bords violets, sur fond transparent, dessinée en Python (PIL). `bloom_rift.png.mcmeta` : `frametime` 2, `interpolate` true.
- Datagen : une seule entrée, dans `ModBlockStateProvider` : `simpleBlock` avec un modèle `cross` en rendu `translucent`. Pas de loot table (`noLootTable`).
- Lang : `block.examplemod.bloom_rift` (« Faille du Bloom »).

**Sortie par le haut** [Proposition, gardée par D15]

1. Au-dessus de Y 256 (en sortant du brouillard par le haut : piliers, échafaudages, élytres), le joueur est envoyé à son point de réapparition (lit, ancre, ou spawn du monde) **avec son inventaire**.
2. Mise en œuvre : `serverPlayer.changeDimension(serverPlayer.findRespawnPositionAndUseSpawnBlock(false, DimensionTransition.DO_NOTHING))`, comme le portail de sortie de l'End. Vérifié dans les sources : avec `false`, une ancre de réapparition perd une charge, comme à la sortie de l'End ; avec `true`, non. Si le lit a disparu, le joueur reçoit le message vanilla « pas de lit ni d'ancre » et arrive au spawn du monde.
3. Les lits explosent dans l'Entre-monde : le point de réapparition est donc toujours hors de la dimension, et la boucle de l'End disparaît.

**Mort** : elle reste une sortie, avec la perte d'inventaire habituelle.

### 5.4 Critères d'acceptation

- [ ] En survie, tomber dans le vide de l'End sans soins tue le joueur ; aucun transfert (mécanique vanilla, non testé).
- [x] Avec assez de soins, après 10 s (200 ticks exactement) dans le vide, le joueur arrive debout sur la surface du Gloom, sans dégâts de chute, avec Obscurité, Régénération, la santé mentale à 90 et le succès racine.
- [x] Remonter au-dessus de la zone de vide avant 10 s remet le chrono à 0.
- [x] En créatif, le transfert est immédiat.
- [x] Les objets jetés dans le vide ne réapparaissent pas dans l'Entre-monde.
- [x] Monter au-dessus de Y 256 renvoie au point de réapparition (testé avec le spawn du monde), inventaire intact, sans retomber dans le vide.
- [x] On trouve des failles dans les cavernes du Bloom, qui éclairent, émettent des particules et ne se cassent pas en survie (sondage et capture du 26/10 ; la casse en survie n'a pas été essayée).
- [x] Rester 1,5 s dans une faille envoie le joueur dans l'overworld, aux mêmes X et Z, debout sur le bloc le plus haut, inventaire intact ; en créatif, le passage est immédiat (GameTest du 26/10 : 32 ticks en survie, 3 en créatif).
- [x] Un mob ou un objet qui touche une faille reste dans l'Entre-monde (GameTest du 26/10 avec un objet ; les mobs passent par le même code).

**Estimation** : 3 à 4 h.

---

## 6. Santé mentale

**Sources** : système implicite, cité par D4 et D10. Toute la conception est donc une **[Proposition]**.

### 6.1 Intention

Une jauge qui transforme le temps passé en surface en pression. Elle ne tue presque jamais directement, mais elle force à redescendre dans le Bloom ou à s'équiper.

### 6.2 Modèle

| Propriété | Valeur |
|---|---|
| Échelle | de 0 à 100 (float), départ à 100 |
| Calcul | côté serveur, une fois par seconde et par joueur, avec un décalage entre joueurs : `(player.tickCount + player.getId()) % 20 == 0` |
| Stockage | attachment `sanity`, sérialisé par `Codec.FLOAT`. Les mises à jour ne partent qu'au joueur concerné ; NeoForge 21.1 envoie aussi la valeur une fois aux joueurs qui commencent à le voir, sans effet en jeu |
| Mort | remise à 100 (pas de `copyOnDeath`) |
| Créatif, spectateur | jauge figée, quelle que soit la source (`Sanity.add` ne fait rien) |

### 6.3 Sources de variation (points par seconde, avant multiplicateur)

| Situation | Détection | Variation |
|---|---|---|
| Surface du Gloom | biome `gloom` aux yeux et Y des yeux ≥ 104 (même seuil que le brouillard) | −0,40 |
| Sous-sol du Gloom | biome `gloom`, Y < 104 | −0,20 |
| Zone de miasme [Demande, D16] | biome `miasma` aux yeux, quel que soit Y (il descend jusqu'à Y 93 environ) | −3,0 (`miasmaSanityLoss`), et 1 dégât toutes les 3 s sans la combinaison complète (§4.9) |
| Bloom | biome `bloom` | +0,25 |
| Overworld [Demande, D25] | dimension `minecraft:overworld` | remise à 100 à la seconde suivante, et l'effet Hallucinations est retiré |
| Nether, End | autre dimension | +0,10 |
| Arrivée dans l'Entre-monde | événement ponctuel | −10 |
| Caresser un hérisson | événement ponctuel (§11) | +8 |

Le biome est lu aux yeux du joueur une fois par seconde. Le terrain étant plat, un seuil fixe suffit : pas de lecture de heightmap.

### 6.4 Formule unique

```
brut = somme des sources de la seconde (ou montant ponctuel)
si brut < 0 : variation = brut × M_perte
si brut > 0 : variation = brut × M_gain

combinaison Hazmat complète : M_perte = M_gain = M_hazmat = 0,5, effets de potion ignorés
sinon : M_perte = 1 / 0,5 / 0,25 (sans Sérénité / Sérénité I / Sérénité II)
        M_gain  = 1 / 2 / 3     (sans Clarté / Clarté I / Clarté II)
```

**Non-cumul [Discord, D4]** : quand la combinaison complète est portée, seul `M_hazmat` compte et les effets de Sérénité et de Clarté sont ignorés. La règle est dans la formule elle-même : aucun cas particulier à gérer ailleurs.

| Cas limite | Résultat |
|---|---|
| Combinaison incomplète | aucun effet de la combinaison ; les potions s'appliquent |
| Sérénité et Clarté en même temps | chacune agit sur son sens (perte ou gain) : elles se combinent |
| Lait | retire les effets de potion (vanilla) ; la combinaison reste |
| On enfile la combinaison avec une potion active | la potion continue de tourner mais ne compte plus |

Toutes les variations passent par une seule méthode, `Sanity.add(ServerPlayer, float)`. Elle applique les multiplicateurs, borne le résultat entre 0 et 100, et n'appelle `setData` que si la valeur change.

### 6.5 Paliers

| Palier | Seuil | Effets |
|---|---|---|
| Stable | de 100 à 71 | aucun |
| Troublé | ≤ 70 | battement de cœur discret côté client toutes les 10 à 20 s (`WARDEN_HEARTBEAT`, volume 0,3) ; les icônes du HUD tremblent ; sous 70, des imitateurs peuvent apparaître dans le Gloom (§8 bis) |
| Hallucinations | ≤ 40 | effet `hallucinations` I, 30 s, réappliqué toutes les 10 s tant qu'on reste sous le seuil (§8) |
| Panique | ≤ 15 | `hallucinations` II et Faiblesse I, mêmes durées |
| Effondrement | 0 | 1 dégât toutes les 2 s, type de dégât `insanity` (ignore l'armure ; message de mort « %1$s a perdu la raison ») |

En remontant au-dessus d'un seuil, on ne retire rien : l'effet expire de lui-même en 30 s au plus. Cela évite les clignotements autour du seuil.

**Succès « Ouch, my head »** : attribué la première fois que la jauge passe de 100 à moins de 100. Le code ne le vérifie qu'à ce passage (§12).

Calcul du rythme de §3.2, en surface sans protection : arrivée à 90 (−10), puis 0,40 par seconde. On atteint 40 en 125 s et 0 en 225 s. Le tableau de §3.2 part de 100 pour comparer les situations entre elles.

### 6.6 HUD

| Élément | Choix |
|---|---|
| Forme | 10 icônes de 9×9 (une icône = 10 points, une demi-icône = 5), comme la faim |
| Position | au-dessus de la barre de faim. On utilise `Gui#rightHeight` (champ ajouté par NeoForge) pour se placer au-dessus de l'air et de la vie de la monture, puis on l'incrémente |
| Visibilité | si la jauge est sous 100, ou si le joueur est dans l'Entre-monde |
| Enregistrement | `RegisterGuiLayersEvent#registerAbove(VanillaGuiLayers.FOOD_LEVEL, id, layer)` |
| Dessin | `GuiGraphics#blitSprite(ResourceLocation, x, y, 9, 9)` avec les sprites `hud/sanity_full`, `hud/sanity_half`, `hud/sanity_empty` |
| Données | copie client de l'attachment (synchronisée par NeoForge). Pas d'interpolation : l'affichage est en demi-icônes |

[Proposition] Commande de test `/sanity get` et `/sanity set <0-100>` (niveau op 2), enregistrée via `RegisterCommandsEvent`.

### 6.7 Mise en œuvre

```java
public static final Supplier<AttachmentType<Float>> SANITY = ATTACHMENT_TYPES.register("sanity",
        () -> AttachmentType.builder(() -> 100f)
                .serialize(Codec.FLOAT)
                .sync((holder, player) -> holder == player, ByteBufCodecs.FLOAT)
                .build());
```

| Classe | Rôle |
|---|---|
| `attachment/ModAttachments` | `DeferredRegister` sur `NeoForgeRegistries.Keys.ATTACHMENT_TYPES` : `sanity` et `void_ticks` |
| `sanity/Sanity` | `add`, multiplicateurs, paliers, attribution du succès |
| `sanity/SanityEvents` | `PlayerTickEvent.Post` (une fois par seconde), `RegisterCommandsEvent` |
| `client/SanityHudLayer` | couche de HUD |
| `data/examplemod/damage_type/insanity.json` | type de dégât, ajouté aux tags `minecraft:bypasses_armor` et `minecraft:no_knockback` |
| `data/examplemod/damage_type/miasma.json` | type de dégât du miasme (§4.9), dans les mêmes tags |

`setData` déclenche la synchro automatiquement. Une jauge stable n'envoie donc rien, et une jauge qui bouge envoie un paquet de quelques octets par seconde, au seul joueur concerné. Pendant le générique de fin de l'End, le joueur n'est pas « vivant » pour le serveur : la jauge ne bouge pas.

### 6.8 Critères d'acceptation

- [ ] En surface du Gloom, la jauge descend d'une demi-icône toutes les 12,5 s environ.
- [ ] La valeur survit à la déconnexion et revient à 100 après une mort.
- [ ] Combinaison complète et Sérénité I actives : la descente est divisée par 2, pas par 4.
- [ ] À 40, l'effet hallucinations apparaît et le succès « What is this ? » tombe.
- [ ] À 0, le joueur prend des dégâts et le message de mort est « a perdu la raison ».

**Estimation** : 6 à 8 h, HUD compris.

---

## 7. Potions de santé mentale

**Sources** : D4 évoque « l'effet des potions qui altère la perte/gain de santé mentale ». Le contenu est une **[Proposition]**.

| Effet | Type | Couleur | Rôle |
|---|---|---|---|
| `serenity` (Serenity / Sérénité) | bénéfique | `0x7FD1C7` | pertes × 0,5 (I) ou × 0,25 (II) |
| `clarity` (Clarity / Clarté) | bénéfique | `0xF2E28C` | gains × 2 (I) ou × 3 (II) |

| Potion | Effet | Durée | Recette d'alchimie |
|---|---|---|---|
| `serenity` | Sérénité I | 3:00 | Potion étrange + `bloom_flower` |
| `long_serenity` | Sérénité I | 8:00 | `serenity` + poudre de redstone |
| `strong_serenity` | Sérénité II | 1:30 | `serenity` + poudre de glowstone |
| `clarity` | Clarté I | 3:00 | Potion étrange + baies lumineuses |
| `long_clarity` | Clarté I | 8:00 | `clarity` + poudre de redstone |
| `strong_clarity` | Clarté II | 1:30 | `clarity` + poudre de glowstone |

Les versions jetables et persistantes viennent gratuitement des recettes vanilla (poudre à canon, souffle du dragon).

**Mise en œuvre**
- Les effets vont dans `ModEffects`. Le constructeur de `MobEffect` est protégé : on écrit `new MobEffect(MobEffectCategory.BENEFICIAL, couleur) {}`, comme dans les tests de NeoForge.
- Les potions vont dans `potion/ModPotions`, un `DeferredRegister` sur `Registries.POTION`, avec `new Potion(new MobEffectInstance(holder, durée, amplificateur))`.
- Les recettes passent par `RegisterBrewingRecipesEvent#getBuilder().addMix(Holder<Potion>, Item, Holder<Potion>)`. Cet événement est sur le bus du jeu, pas celui du mod.
- [Proposition] Le masque à gaz empêche de boire une potion de santé mentale : annulation de `LivingEntityUseItemEvent.Start`, avec le message « Impossible de boire avec le masque à gaz » dans la barre d'action. C'est la traduction en jeu du non-cumul de D4 ; la formule (§6.4) garantit déjà la règle pour les potions jetables.
- [Proposition, optionnel] Une potion négative « Angoisse » (pertes × 1,5) à base d'un ingrédient du Gloom, pour le PvP.

**Critères d'acceptation**
- [ ] Les 6 potions apparaissent dans l'onglet créatif « Potions » avec leurs versions jetables et persistantes.
- [ ] Sérénité I en surface : la descente est divisée par 2.

**Estimation** : 2 à 3 h.

---

## 8. Effet Hallucinations

**Sources** : D10 (« What is this ? : obtenir l'effet hallucinations »). Le contenu est une **[Proposition]**.

### 8.1 Intention

Faire douter le joueur de ce qu'il voit et entend, sans jamais tricher contre lui : pas de faux dégâts, pas de faux monstres qui attaquent.

### 8.2 Règles

1. Effet `hallucinations`, nocif, couleur `0x6A3D9A`. Il est appliqué par la santé mentale : I à 40 ou moins, II à 15 ou moins (§6.5).
2. Tout est joué côté client, pour le seul joueur concerné. Pas de trafic réseau en plus de la synchro vanilla des effets.
3. Une seule ombre à la fois ; pas de flash ni de clignotement.
4. Une option de config client, `hallucinationIntensity` (de 0 à 1, 1 par défaut), règle les distorsions visuelles. À 0, il ne reste que les sons : l'ombre disparaît aussi.

### 8.3 Contenu par niveau

| Illusion | Niveau I | Niveau II | Mise en œuvre |
|---|---|---|---|
| Pas derrière soi | toutes les 25 à 45 s | toutes les 10 à 20 s | 3 sons `STONE_STEP` ou `GRASS_STEP` à 2 ou 3 blocs dans le dos, espacés de 6 ticks |
| Regard d'enderman | toutes les 60 à 120 s | toutes les 30 à 60 s | `ENDERMAN_STARE`, faible |
| Creeper qui s'amorce | — | toutes les 40 à 80 s | `CREEPER_PRIMED` à 2 blocs dans le dos |
| Balancement de caméra | ±1,5° | ±3° | `ViewportEvent.ComputeCameraAngles#setRoll` (sinus lent) |
| Pulsation du champ de vision | ±2 % | ±4 % | `ViewportEvent.ComputeFov#setFOV` |
| Vignette violette | opacité 25 % | 45 % | couche de HUD au-dessus de `VanillaGuiLayers.CAMERA_OVERLAYS` |
| Ombre | toutes les 60 à 120 s | toutes les 25 à 50 s | voir 8.4 |

Les sons passent par `ClientLevel#playLocalSound(x, y, z, son, SoundSource.HOSTILE, volume, pitch, false)` : seul le joueur local les entend. Les distorsions montent et descendent progressivement (facteur lissé, comme le brouillard).

### 8.4 L'ombre

- Une silhouette d'enderman apparaît entre 12 et 20 blocs, à 50 à 80° de l'axe du regard, posée au sol. Dans l'Entre-monde, la distance est ramenée à 70 % de la fin du brouillard, sinon l'ombre serait invisible : 8 à 10 blocs en surface du Gloom, 12 à 17 avec le masque.
- Elle disparaît dès que le joueur la regarde (moins de 20° d'écart), qu'il s'en approche à moins de 6 blocs, ou après 8 s.
- **Méthode** : une seule instance d'Enderman, créée une fois côté client et jamais ajoutée au monde. Elle ne tick pas, n'a pas de collision et n'envoie rien au réseau. On la dessine dans `RenderLevelStageEvent`, à l'étape `AFTER_ENTITIES`, via `EntityRenderDispatcher#render(entité, x, y, z, rotation, partialTick, poseStack, buffers, lumière)`. L'instance est réutilisée : aucune allocation par frame.
- On a écarté les particules (illisibles) et une entité client ajoutée au monde (fragile à nettoyer).

### 8.5 Mise en œuvre

| Classe | Rôle |
|---|---|
| `effect/ModEffects` | déclaration de l'effet |
| `client/Hallucinations` | `ClientTickEvent.Post` : lit `player.getEffect(ModEffects.HALLUCINATIONS)` une fois par tick, gère les minuteurs (aléatoire client) et l'ombre |
| `client/HallucinationRender` | `ComputeCameraAngles`, `ComputeFov`, `RenderLevelStageEvent`, couche de vignette |

**Ressources** : `textures/gui/hallucination_vignette.png` (dégradé violet, centre transparent), `textures/mob_effect/hallucinations.png` (icône 18×18), lang. Tous les sons sont vanilla.

**Critères d'acceptation**
- [ ] Avec l'effet I, on entend des pas dans son dos en moins d'une minute.
- [ ] L'ombre disparaît quand on la regarde.
- [ ] Un second joueur à côté n'entend et ne voit rien.
- [ ] Avec `hallucinationIntensity` à 0, la caméra ne bouge plus.

**Estimation** : 6 à 8 h, dont la moitié pour l'ombre.

---

## 8 bis. Imitateurs

**Sources** : D12, D13. Les valeurs, la révélation et le rendu sont une **[Proposition]**.

### 8 bis.1 Intention

Un monstre qui a l'air d'un coéquipier. Il prend le skin, le pseudo et l'équipement d'un joueur, marche avec le groupe et refait ses gestes, puis se retourne contre lui. Plus la tête lâche, plus on en croise : à faible santé mentale, on finit par se méfier de ses propres amis.

### 8 bis.2 Règles

1. [Discord] Les imitateurs n'apparaissent que dans le biome `gloom`, et dans son sous-biome `miasma` (§4.9).
2. [Discord] Un imitateur copie un joueur proche : skin, pseudo, équipement (tête, torse, jambes, pieds, deux mains) et main principale. Son nom est celui du joueur : l'étiquette de nom et les messages de mort affichent son pseudo.
3. [Discord] Tant que le joueur copié est en ligne, dans la même dimension et à moins de 32 blocs, l'imitateur refait ses gestes : il s'accroupit et se relève avec lui, et frappe dans le vide quand il frappe.
4. [Discord] Il attaque au bout d'une durée aléatoire, entre 15 et 45 s après la copie, et seulement si une victime est à moins de 20 blocs ; sinon il attend. Il se révèle aussi au premier dégât reçu, quelle qu'en soit la source (coup, flèche, chute).
5. [Discord] Moins on a de santé mentale, plus on en croise : aucun à 70 ou plus, puis une chance par seconde qui suit le carré de la « peur » (§8 bis.3).
6. [Proposition] Avant la révélation, il ne cible personne. Il suit sa victime à 3 à 6 blocs en la regardant, comme un coéquipier. La victime est le joueur vivant le plus proche à moins de 32 blocs, hors créatif et spectateur, autre que le joueur copié. Le joueur copié n'est choisi que si personne d'autre n'est proche, donc en solo.
7. [Proposition] À la révélation : regard d'enderman (audible par tous les joueurs proches), pseudo brouillé en rouge foncé, vitesse +40 %, et attaque de la victime, ou de l'attaquant si c'est un joueur.
8. [Proposition] L'équipement copié ne tombe jamais : pas de duplication possible. L'imitateur ne lâche que de l'XP.
9. [Proposition] Un seul imitateur à moins de 48 blocs. Aucun en Paisible, ni pour un joueur en créatif ou en spectateur. Il disparaît comme un monstre vanilla : en Paisible, ou au-delà de 128 blocs de tout joueur.

### 8 bis.3 Valeurs

| Propriété | Valeur |
|---|---|
| Taille | 0,6 × 1,8, yeux à 1,62 (comme un joueur) |
| PV | 20 |
| Vitesse | 0,25, puis +40 % après la révélation |
| Dégâts à mains nues | 2 ; une arme ou une armure copiée apporte ses propres attributs |
| Portée de suivi (`FOLLOW_RANGE`) | 32 blocs |
| Délai avant l'attaque | 300 à 900 ticks (15 à 45 s), puis une victime à moins de 20 blocs |
| Distance de suivi | 3 à 6 blocs, chemin recalculé toutes les 10 ticks |
| Distance d'apparition | 16 à 28 blocs du joueur |
| Seuil de santé mentale | `mimicSanityThreshold`, 70 |
| Chance par seconde à 0 de santé mentale | `mimicChancePerSecond`, 0,02 |

**Probabilité.** Une fois par seconde et par joueur : `peur = (seuil − santé) / seuil`, et un imitateur apparaît si `aléatoire < chance × peur²`. Avec les valeurs par défaut :

| Santé mentale | Peur | Chance par seconde | En moyenne, une rencontre toutes les |
|---|---|---|---|
| 70 ou plus | 0 | 0 | jamais |
| 60 | 0,14 | 0,04 % | 41 min |
| 40 | 0,43 | 0,37 % | 4 min 30 |
| 15 | 0,79 | 1,23 % | 80 s |
| 0 | 1 | 2 % | 50 s |

Ces durées valent dans le Gloom, sans autre imitateur à moins de 48 blocs. En surface sans protection, la jauge passe de 70 à 0 en 175 s (§6.3) : on croise en moyenne un imitateur pendant cette descente, puis un toutes les 50 s à 0. Le carré garde la zone de 70 à 40 presque calme, pour que les imitateurs arrivent avec les hallucinations.

### 8 bis.4 Apparition (`entity/MimicSpawner`)

1. `MimicSpawner.tick(player)` est appelé depuis un `PlayerTickEvent.Post` de `ModEntityEvents`, une fois par seconde par joueur : `(player.tickCount + player.getId()) % 20 == 10`. Le décalage de 10 ticks évite de tomber sur la même tick que la santé mentale (`== 0`).
2. Sorties immédiates, des moins chères aux plus chères : créatif, spectateur, mort, Paisible, hors de l'Entre-monde (`ModDimensions.UNDERVOID_DIM_LEVEL_KEY`), santé mentale (`Sanity.get`) au seuil ou au-dessus.
3. Tirage (§8 bis.3).
4. Seulement après un tirage réussi : biome `gloom` ou `miasma` à la position du joueur, puis aucun autre imitateur à moins de 48 blocs (une requête d'AABB), puis `spawnNear(player)`.
5. `spawnNear` fait 8 essais au plus, chacun à un angle aléatoire et à 16 à 28 blocs, au-delà de la fin du brouillard de surface (14 blocs). Pour chaque essai, on descend de Y + 6 à Y − 10 (Y du joueur), sans passer sous la surface de la colonne (heightmap `MOTION_BLOCKING_NO_LEAVES`, D23), jusqu'à une case dont le chunk est chargé, dont le bloc du dessous a une face supérieure solide, dont les cases des pieds et de la tête sont vides, et dont le biome est `gloom` ou `miasma`.
6. Joueur copié : le joueur le plus proche du joueur visé, hors spectateur, à moins de 64 blocs ; sinon le joueur visé lui-même. À plusieurs, l'imitateur prend donc l'apparence d'un coéquipier et suit le joueur visé.
7. L'imitateur est placé tourné vers le joueur, puis `copy(source)`, `EventHooks.finalizeMobSpawn(mimic, level, level.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null)` (qui laisse les autres mods modifier ou annuler l'apparition) et `addFreshEntity`. `spawnNear` renvoie l'imitateur, ou null si aucune case ne convient.

Avec le masque à gaz, le brouillard s'arrête à 24 blocs : un imitateur qui apparaît entre 16 et 24 blocs peut être vu au moment où il arrive.

### 8 bis.5 Comportement

| | Déguisé | Révélé |
|---|---|---|
| Cible | aucune | la victime, ou le joueur qui l'a frappé |
| Déplacement | suit la victime à 3 à 6 blocs et la regarde ; sinon promenade | attaque au corps à corps, vitesse +40 % |
| Nom affiché | pseudo du joueur copié | même pseudo, brouillé (`OBFUSCATED`) et rouge foncé (`DARK_RED`) |
| Sons | aucun son d'ambiance ; blessure et mort de joueur (`PLAYER_HURT`, `PLAYER_DEATH`) | idem, et `ENDERMAN_STARE` au moment de la révélation |

Goals :

| Priorité | Goal |
|---|---|
| 0 | `FloatGoal` |
| 2 | `MeleeAttackGoal(this, 1.0, true)` |
| 3 | suivi de la victime (goal du mod, actif seulement déguisé et sans cible) |
| 5 | `WaterAvoidingRandomStrollGoal(this, 0.8)` |
| 6 | `LookAtPlayerGoal(this, Player.class, 8f)` |
| 7 | `RandomLookAroundGoal` |
| cible, 1 | `HurtByTargetGoal` |
| cible, 2 | `NearestAttackableTargetGoal<Player>`, seulement une fois révélé, et jamais vers le joueur copié s'il y a quelqu'un d'autre à portée |

Être blessé révèle l'imitateur : `HurtByTargetGoal` ne lui donne donc jamais de cible avant la révélation. Le second goal lui fait reprendre une cible après la mort de sa victime ou un rechargement. Accroupi, il prend la taille d'un joueur accroupi (1,5 bloc, yeux à 1,27), pour que son pseudo reste à la même hauteur.

### 8 bis.6 Mise en œuvre

| Élément | Choix |
|---|---|
| `entity/custom/Mimic` | étend `Monster`. API : `copy(Player)`, `getCopiedId()` (`@Nullable UUID`), `getCopiedName()`, `isRevealed()`, `createAttributes()` |
| Données synchronisées | UUID copié (`EntityDataSerializers.OPTIONAL_UUID`), pseudo (`STRING`), drapeau de révélation (booléen) |
| Copie | `copy(source)` : les 6 emplacements (`HEAD`, `CHEST`, `LEGS`, `FEET`, `MAINHAND`, `OFFHAND`) en `stack.copy()`, `setDropChance(slot, 0f)` sur chacun, `setLeftHanded(source.getMainArm() == HumanoidArm.LEFT)`, compte à rebours de 300 à 900 ticks |
| Nom | `getName()` renvoie le pseudo en `Component.literal` ; révélé, avec `ChatFormatting.OBFUSCATED` et `DARK_RED`. Sans pseudo : `super.getName()` |
| Imitation | référence au joueur copié gardée en cache et résolue de nouveau au plus une fois par seconde ; `setShiftKeyDown`, `setPose(Pose.CROUCHING ou Pose.STANDING)`, `swing(InteractionHand.MAIN_HAND)` quand le joueur frappe et que l'imitateur ne frappe pas déjà |
| Révélation | drapeau synchronisé, `setTarget`, `level.playSound(null, …, SoundEvents.ENDERMAN_STARE, SoundSource.HOSTILE, 1f, 0.8f)`, modificateur transitoire de `MOVEMENT_SPEED` (`examplemod:mimic_reveal`, +0,4, `ADD_MULTIPLIED_BASE`), remis au chargement d'un imitateur déjà révélé |
| Sauvegarde | `CopiedId` (`putUUID` / `hasUUID`), `CopiedName`, `Revealed`, `RevealIn` |
| Œuf et `/summon` | `finalizeSpawn` copie `level.getNearestPlayer(this, 64)` si rien n'a encore été copié |
| Attributs | `Monster.createMonsterAttributes()`, `MAX_HEALTH` 20, `MOVEMENT_SPEED` 0,25, `ATTACK_DAMAGE` 2, `FOLLOW_RANGE` 32, via `EntityAttributeCreationEvent` |
| `entity/ModEntities` | `MIMIC` : `EntityType.Builder.of(Mimic::new, MobCategory.MONSTER).sized(0.6f, 1.8f).eyeHeight(1.62f).clientTrackingRange(10)` |
| `entity/MimicSpawner` | `tick(ServerPlayer)` et `spawnNear(ServerPlayer)` (§8 bis.4) |
| `item/ModItems` | `MIMIC_SPAWN_EGG` : `DeferredSpawnEggItem(ModEntities.MIMIC, 0x2E2836, 0x8E8A99, props)`, couleurs du feuillage du Gloom et d'un gris pâle |
| `Config` | section `mimic` de la config serveur, après `katana` : `mimicSanityThreshold` (70, de 0 à 100) et `mimicChancePerSecond` (0,02, de 0 à 1) |

### 8 bis.7 Rendu (`client/MimicRenderer`)

- `MimicRenderer` étend `EntityRenderer<Mimic>` (ombre de 0,5) et délègue `render` et `getTextureLocation` à l'un de deux corps, des `LivingEntityRenderer<Mimic, PlayerModel<Mimic>>` : un large et un fin, choisis selon le modèle du skin.
- Skin : `Minecraft.getInstance().getConnection().getPlayerInfo(uuid).getSkin()` si le client connaît le joueur copié ; sinon `DefaultPlayerSkin.get(uuid)` ; sans UUID, le skin par défaut (`DefaultPlayerSkin.getDefaultTexture()`, modèle fin). Si le joueur copié se déconnecte, l'imitateur prend donc un skin par défaut.
- Chaque corps : `PlayerModel` cuit depuis `ModelLayers.PLAYER` ou `PLAYER_SLIM`, couches `HumanoidArmorLayer` (`PLAYER_INNER_ARMOR` et `PLAYER_OUTER_ARMOR`, ou leurs variantes `PLAYER_SLIM_`, avec `context.getModelManager()`), `ItemInHandLayer`, `CustomHeadLayer` et `ElytraLayer`. Échelle de 0,9375, comme `PlayerRenderer.scale`.
- Avant chaque rendu : `setAllVisible(true)`, `crouching = mimic.isCrouching()`, et les poses des bras `ITEM` ou `EMPTY` selon les mains, inversées si la main principale est la gauche.
- L'étiquette de nom s'affiche comme celle d'un joueur, parce que le corps est un `LivingEntityRenderer` et non un `MobRenderer`. Sans étiquette, on reconnaîtrait l'imitateur au premier coup d'œil.

### 8 bis.8 Ressources

| Type | Entrées |
|---|---|
| Modèle d'item | `models/item/mimic_spawn_egg.json`, parent `minecraft:item/template_spawn_egg`, écrit à la main comme celui du hérisson |
| Lang | `entity.examplemod.mimic`, `item.examplemod.mimic_spawn_egg`, `examplemod.configuration.mimic`, `.mimicSanityThreshold`, `.mimicChancePerSecond`, en en_us et fr_fr |
| Textures | aucune : le skin vient du joueur copié |
| Sons | vanilla : `PLAYER_HURT`, `PLAYER_DEATH`, `ENDERMAN_STARE` |
| Datagen | rien ; pas de loot table, l'XP suffit |

**Critères d'acceptation**
- [ ] Avec `/sanity set 0` à la surface du Gloom, un imitateur apparaît en général en moins de 2 min, entre 16 et 28 blocs.
- [ ] Aucun imitateur au-dessus de 70 de santé mentale, hors du Gloom, en créatif, en spectateur ou en Paisible.
- [ ] Il a le skin (large ou fin), le pseudo et l'équipement du joueur copié, et tient ses objets dans la bonne main.
- [ ] Avant la révélation, il suit sa victime à 3 à 6 blocs sans l'attaquer, et s'accroupit et frappe en même temps que le joueur copié.
- [ ] Il se révèle entre 15 et 45 s après la copie si une victime est à moins de 20 blocs, ou dès le premier dégât : son, pseudo brouillé en rouge, accélération, attaque.
- [ ] À sa mort, il ne lâche que de l'XP, jamais l'équipement copié.
- [ ] Un imitateur révélé reste révélé et rapide après un rechargement du monde.
- [ ] À deux joueurs, il prend l'apparence de l'un et suit l'autre.
- [ ] Posé avec l'œuf ou `/summon`, il copie le joueur le plus proche.

**Estimation** : 6 à 8 h, dont la moitié pour le rendu.

---

## 9. Combinaison Hazmat et masque à gaz

**Sources** : D4, D5.

### 9.1 Intention

L'équipement de l'explorateur, à la Escape the Backrooms : combinaison jaune, masque noir à filtres. Il ne protège pas au combat ; il stabilise l'esprit.

### 9.2 Règles

1. [Discord] La combinaison ralentit les variations de santé mentale. [Proposition] Les pertes **et** les gains sont ralentis : `M_hazmat` = 0,5 dans les deux sens. La combinaison devient un stabilisateur, pas un simple bonus : elle double le temps en surface mais aussi le temps de récupération dans le Bloom.
2. [Proposition] Il faut les 4 pièces. Une combinaison incomplète n'a aucun effet, ce qui garde la règle lisible.
3. [Discord] La combinaison ne se cumule pas avec les potions de santé mentale. Mécanique : §6.4 ; en jeu : §7 (impossible de boire avec le masque).
4. [Discord] La combinaison comprend un masque à gaz : c'est le casque. [Proposition] Porté seul, il repousse la fin du brouillard du Gloom de 14 à 24 blocs, et celle du miasme de 6 à 9 blocs.
5. [Demande, D17] La combinaison complète bloque les dégâts du miasme (§4.9). Comme les autres effets, il faut les 4 pièces : le masque seul ne protège pas. La perte de santé mentale du miasme reste, divisée par 2 comme toute variation.
6. Le tooltip de chaque pièce affiche : « Combinaison complète : variations de santé mentale ×0,5. Non cumulable avec les potions de santé mentale. » Il ne parle pas encore du miasme : texte à compléter si l'équipe le souhaite.

### 9.3 Valeurs

Matériau `hazmat` (`ArmorMaterial`, enregistré sur `Registries.ARMOR_MATERIAL`) :

| Propriété | Hazmat | Repères vanilla |
|---|---|---|
| Défense (casque / plastron / jambières / bottes) | 1 / 3 / 2 / 1 (total 7) | cuir 7, maille 12, fer 15 |
| Robustesse et résistance au recul | 0 et 0 | comme le cuir |
| Multiplicateur de durabilité | 10, soit 110 / 160 / 150 / 130 | cuir 5, fer 15 |
| Enchantabilité | 12 | maille 12 |
| Réparation | `hazmat_fabric` | |
| Son d'équipement | `ARMOR_EQUIP_LEATHER` | |

Le constructeur d'`ArmorMaterial` en 1.21.1 prend sept arguments :
- `Map<ArmorItem.Type, Integer>` des défenses : les 5 types, `BODY` compris, comme les matériaux vanilla ;
- l'enchantabilité ;
- le `Holder<SoundEvent>` du son d'équipement ;
- un `Supplier<Ingredient>` pour la réparation ;
- une `List<ArmorMaterial.Layer>` des couches de texture ;
- la robustesse ;
- la résistance au recul.

Les pièces sont des `new ArmorItem(holder, ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(10)))`.

### 9.4 Contenu et recettes

| ID | en_us | fr_fr | Recette |
|---|---|---|---|
| `hazmat_fabric` | Coated Fabric | Tissu enduit | sans forme : laine jaune + cuir + boule de slime → 3 |
| `hazmat_mask` | Gas Mask | Masque à gaz | `FGF` / `ICI` (F tissu, G vitre, I lingot de fer, C charbon de bois) |
| `hazmat_suit` | Hazmat Suit | Combinaison Hazmat | forme de plastron, 8 tissus |
| `hazmat_leggings` | Hazmat Pants | Pantalon Hazmat | forme de jambières, 7 tissus |
| `hazmat_boots` | Hazmat Boots | Bottes Hazmat | forme de bottes, 4 tissus |

Coût total : 21 tissus, soit 7 fabrications (7 laines jaunes, 7 cuirs, 7 boules de slime), plus 2 lingots de fer, une vitre et un charbon de bois. C'est un objectif de milieu de partie, à préparer avant la chute.

### 9.5 Rendu

| Option | Coût | Résultat | Choix |
|---|---|---|---|
| A. Couches d'armure vanilla : 2 textures 64×32, `textures/models/armor/hazmat_layer_1.png` et `_layer_2.png` | environ 2 h de dessin | combinaison et masque plats, lisibles | **MVP** |
| B. `HumanoidModel` custom via `IClientItemExtensions#getHumanoidArmorModel` (enregistré par `RegisterClientExtensionsEvent#registerItem`) | moyen | filtres en relief | écartée (autant passer à C) |
| C. Armure GeckoLib : `GeoItem`, `createGeoRenderer(Consumer<GeoRenderProvider>)`, `GeoRenderProvider#getGeoArmorRenderer`, `GeoArmorRenderer` | 4 à 6 h (modèle Blockbench et code) | masque 3D avec filtres | **Polish**, uniquement pour le masque si le temps le permet |

[Could, fait] Un overlay en vue à la première personne (`client/GasMaskOverlay`, texture `textures/gui/gas_mask_overlay.png`) : deux verres ronds et un caoutchouc sombre autour, dessinés par une couche de HUD au-dessus de `CAMERA_OVERLAYS` quand le masque est porté, comme la citrouille sculptée. Il disparaît en vue à la troisième personne et avec F1.

### 9.6 Datagen et ressources

- Modèles d'item `basicItem` pour les 5 items.
- Recettes : 1 sans forme et 4 avec forme.
- Tags d'items : `minecraft:head_armor`, `chest_armor`, `leg_armor`, `foot_armor`. Ce sont eux qui donnent l'enchantabilité en 1.21.1, via `enchantable/*`.
- Textures : 5 items et 2 couches d'armure.
- Lang : en_us et fr_fr, plus le texte du tooltip.

**Critères d'acceptation**
- [ ] Combinaison complète portée en surface : la descente est divisée par 2.
- [ ] Une pièce retirée : la descente redevient normale.
- [ ] Enchantable avec Protection et Solidité.
- [x] Combinaison complète portée dans le miasme : aucun dégât, perte de santé mentale divisée par 2. Sans le plastron : dégâts toutes les 3 s (GameTest du 26/10, sans aucune pièce pour le second cas).

**Estimation** : 4 à 5 h, plus 4 à 6 h pour l'option GeckoLib.

---

## 10. Katana

**Sources** : D6 à D9, D14.

### 10.1 Intention

Une arme de mouvement : on traverse les groupes de monstres au lieu de les attendre. Un katana rend mobile ; deux katanas rendent dévastateur.

### 10.2 Règles

1. [Discord] Clic droit : le joueur fait un dash vers l'avant et frappe tout ce qu'il traverse.
2. [Discord] Quand un katana est en main principale, la main gauche est inutilisable, sauf si elle tient un autre katana.
3. [Discord] Un katana : peu de dégâts, cooldown court. Deux katanas : plus de dégâts, cooldown plus long, et l'ensemble reste plus avantageux, puisqu'il faut les farmer.
4. [Demande, D19] Le dash suit le regard dans toutes les directions : haut, bas, côtés. Au sol, un regard vers le bas devient horizontal, sinon on dasherait dans le sol. Il marche au sol, en l'air et dans l'eau, mais pas sur une monture.
5. [Proposition] Un katana seul en main gauche ne fait rien.
6. [Demande, D14] Les enchantements du katana en main principale comptent aussi pour le dash : Tranchant, Châtiment et Fléau des arthropodes augmentent ses dégâts, Aura de feu enflamme les cibles.
7. [Demande, D14, D22] Un dash qui tue recharge le katana, une fois par dash, avec un son d'orbe d'XP. Tuer un joueur retire tout le cooldown ; tuer un mob n'en garde que la moitié (`mobKillCooldown`, 0,5). Un kill de joueur après un kill de mob dans le même dash retire quand même le reste.
8. [Demande, D14] Le joueur est invulnérable pendant le mouvement du dash.
9. [Demande, D14] Accroupi, le clic droit pare au lieu de dasher (§10.5).

### 10.3 Valeurs

| | 1 katana | 2 katanas |
|---|---|---|
| Distance du dash | 7 blocs | 8 blocs |
| Dégâts par cible, sans enchantement | 12 | 30 |
| Cooldown | 20 ticks (1 s) ; retiré si le dash tue un joueur, réduit de moitié s'il tue un mob | 40 ticks (2 s), même règle |
| DPS du dash, par cible | 12 | 15 (+25 %) |
| Un dash sur 3 cibles | 36 | 90 |
| Invulnérabilité | les 4 ticks du mouvement (0,2 s) | identique |
| Parade | fenêtre de 6 ticks (0,3 s), cooldown de 20 ticks (1 s) | identique |
| Mêlée (clic gauche) | 8 dégâts, vitesse 1,8, soit 14,4 DPS | identique |
| Durabilité | 1500 | 1500 par katana |

Avant D14 : dash à 5 et 14 dégâts, mêlée à 6 dégâts (10,8 DPS), 750 utilisations, sans enchantement sur le dash, ni recharge, ni invulnérabilité, ni parade. Entre D14 et D20 : dash à 6 et 8 blocs, 8 et 20 dégâts, cooldowns de 1,5 et 3 s ; le katana seul a été jugé « trop nul » en test (D20).

Repères vanilla en mêlée : épée en fer 6 × 1,6 = 9,6 DPS ; diamant 7 × 1,6 = 11,2 ; netherite 8 × 1,6 = 12,8. Durabilité : diamant 1561.

Le katana dépasse maintenant la netherite en mêlée et tient presque aussi longtemps que le diamant. Les deux katanas respectent toujours D9 :
- plus de dégâts (30 contre 12) et plus de cooldown (2 s contre 1 s) ;
- 25 % de DPS en plus, 2,5 fois plus de burst et 14 % de portée en plus.

Les enchantements s'ajoutent à chaque coup, de la même façon dans les deux modes : avec Tranchant V (+3), 15 et 33 dégâts par cible, soit 15 et 16,5 DPS. L'écart de DPS tombe alors à 10 % ; le burst reste 2,2 fois plus fort. À surveiller à l'équilibrage.

Un seul katana garde un intérêt : il dashe deux fois plus souvent, ce qui le rend meilleur pour se déplacer. À 12 dégâts, il tue un zombie (20 PV) en deux dashs, soit 1 s.

Stats en 1.21.1 :
- `SwordItem.createAttributes(ModToolTiers.KATANA, 5, -2.2f)` donne 1 + 5 + 2 (bonus du tier) = 8 dégâts, et une vitesse de 4 − 2,2 = 1,8.
- Le tier est un `SimpleTier` NeoForge : `BlockTags.INCORRECT_FOR_IRON_TOOL`, 1500 utilisations, vitesse 6, bonus de dégâts 2, enchantabilité 14, réparation avec `blade_fragment`.
- Les tags d'items `minecraft:swords` et `enchantable/sword` donnent les enchantements d'épée (Tranchant, Châtiment, Fléau des arthropodes, Aura de feu, Butin…).
- Config serveur : `dashDistance` 7,0, `dashDamage` 12,0, `dashCooldown` 20, `dualDashDistance` 8,0, `dualDashDamage` 30,0, `dualDashCooldown` 40 par défaut. Une config existante garde ses anciennes valeurs : les corriger avec `/blue`.

### 10.4 Le dash (`KatanaItem#use` et `KatanaDash`)

La première version téléportait le joueur d'un coup : c'était saccadé. Le dash est maintenant un vrai mouvement de 4 ticks (0,2 s), joué par le client et vérifié par le serveur.

1. Main gauche, monture ou cooldown actif : on renvoie `pass`. Accroupi, c'est une parade (§10.5). Le mode double est actif si la main gauche tient aussi un katana.
2. Trajet, calculé à l'identique des deux côtés : direction du regard (`getLookAngle()`), remplacée au sol par l'horizontale du regard (`Vec3.directionFromRotation(0, yRot)`) si elle pointe vers le bas, puis `level.clip(…, ClipContext.Block.COLLIDER, …)` depuis la taille du joueur. Sans mur, le trajet fait toute la distance (7 ou 8 blocs) ; avec un mur, il s'arrête 0,5 bloc avant.
3. Mouvement (client) : à chaque tick, `PlayerTickEvent.Pre` impose la vitesse du joueur local : 30 %, 30 %, 25 % puis 15 % du trajet, soit un départ franc et un arrêt en douceur. La vitesse verticale suit le trajet, sans gravité : on monte ou on descend avec le regard, et un dash horizontal ne tombe pas, même au-dessus d'un trou. Contre un sol ou un plafond, les collisions du jeu font glisser le joueur le long de la surface. Comme la vitesse est imposée à chaque tick, la friction ne joue plus : la distance est la même au sol et en l'air (mesuré le 25/10 avec les distances de l'époque : 6,0 et 8,0 blocs). Le rendu interpole entre les ticks, donc c'est fluide à tout framerate. Le mouvement passe par les collisions normales du jeu : le joueur ne traverse pas les blocs.
4. Fin du dash : la vitesse retombe à 0, verticale comprise (un dash vers le haut ne projette pas plus loin). Si le joueur n'a pas de sol sous les pieds, il reste suspendu 4 ticks (0,2 s, réglage `dashHoverTicks`), puis retombe. La suspension durait 10 ticks jusqu'à D19, jugée trop longue en test. La suspension s'arrête s'il retrouve du sol, entre dans un liquide, vole ou plane en élytres. Au sol, rien ne change : on peut sauter dès le tick suivant.
5. Coups (serveur) : `PlayerTickEvent.Post` balaie, à chaque tick, la portion du trajet que le joueur a réellement parcourue (le centre du joueur projeté sur le trajet, en 3D), pendant 7 ticks au plus pour absorber la latence. Les cibles sont donc touchées au passage du joueur, chacune une seule fois par dash, et un joueur arrêté en route ne touche pas ce qui est plus loin. On garde les cibles à moins de 1,5 bloc du segment ; on exclut le joueur, les joueurs que `player.canHarmPlayer` protège et ses propres animaux.
6. Dégâts et enchantements : source `player.damageSources().playerAttack(player)`. Le montant de base (12 ou 30) passe par `EnchantmentHelper.modifyDamage(serverLevel, katana, cible, source, dégâts)` : Tranchant, Châtiment, Fléau des arthropodes. `katana` est une copie du katana tenu au moment du clic : changer d'objet pendant le dash ne change rien. Après un coup réussi, `EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, cible, source, katana)` applique les effets d'après-coup (Aura de feu…), puis un léger recul dans l'axe du dash. Seul le katana en main principale compte, comme en mêlée. Butin marche aussi sur les kills du dash, puisque la source est un coup de joueur.
7. Recharge au kill (`KatanaDash#onKill`) : si une cible touchée est morte après le coup, le serveur agit sur le cooldown du katana, puis joue `EXPERIENCE_ORB_PICKUP` au seul joueur (`ServerPlayer.playNotifySound`, `SoundSource.PLAYERS`, volume 0,6).
   - Joueur tué : `removeCooldown(item)`, hauteur 1,6. Une fois par dash.
   - Mob tué : le reste du cooldown (`getCooldownPercent` × durée du dash) est multiplié par `mobKillCooldown` (0,5) et reposé avec `addCooldown`, hauteur 1,1. Une fois par dash. Le kill arrive dans les premiers ticks du dash, donc la barre de l'objet repart presque pleine, mais deux fois plus courte. Mesuré : 8 ticks restants au lieu de 18 (GameTest du 26/10).
   - `ServerItemCooldowns` prévient le client. Le cooldown est posé sur le type d'item : les deux katanas sont concernés.
8. Invulnérabilité : tant que l'état serveur du dash est en mouvement (`isDashing()`, ses 4 premiers ticks), `EntityInvulnerabilityCheckEvent` rend ce joueur invulnérable, sauf pour les types du tag `minecraft:bypasses_invulnerability` (le vide et `/kill`). Cet événement passe avant que NeoForge empile son contexte de dégâts ; annuler `LivingIncomingDamageEvent` laisserait ce contexte sur la pile à chaque coup bloqué. Les ticks de marge du balayage et la suspension en l'air ne protègent pas.
9. Effets : `SWEEP_ATTACK` le long de chaque segment (un par bloc), `PLAYER_ATTACK_SWEEP` au départ, et un champ de vision élargi de 15 % pendant le dash (`client/DashFov`, réduit par le réglage d'accessibilité « Effets de champ de vision »).
10. Coûts : cooldown posé des deux côtés, pour que le client ne puisse pas relancer un dash avant la réponse du serveur, sur le type d'item donc sur les deux katanas. Ensuite `hurtAndBreak(1, …)` sur chaque katana utilisé, côté serveur.
11. État : attachment `katana_dash` (ni sérialisé ni synchronisé), retiré à la fin du dash.
12. Chute [Demande, D26] : aucune chute ne blesse entre un dash et le retour au sol. Côté serveur, l'attachment `katana_dash` n'est retiré qu'une fois le balayage fini et le joueur au sol (`onGround`) ; tant qu'il existe, `LivingFallEvent` est annulé. Un dash tout droit vers le haut, ou lancé en pleine chute, ne fait donc aucun dégât à l'atterrissage. Le balayage, les coups et l'invulnérabilité (`isDashing()`) gardent leur durée. Avant D26, la chute après le dash comptait : 4 dégâts après un dash vertical.

Il n'y a ni paquet réseau custom ni entité custom : le paquet vanilla d'utilisation d'objet suffit, puisque les deux côtés calculent le même trajet.

### 10.5 La parade

[Demande, D14] Accroupi + clic droit avec un katana en main principale : le joueur pare au lieu de dasher.

1. La parade dure 6 ticks (0,3 s, `parryTicks`) et met le katana en cooldown 20 ticks (1 s, `parryCooldown`), des deux côtés. Le bras frappe (`sidedSuccess`) et on entend `PLAYER_ATTACK_SWEEP` (volume 0,8, hauteur 1,8).
2. Pendant la fenêtre, le premier coup direct reçu est annulé. Coup direct : l'entité responsable est vivante et c'est aussi l'entité directe (`source.getEntity() instanceof LivingEntity` et `source.getDirectEntity() == source.getEntity()`), hors tags `bypasses_invulnerability`, `is_explosion` et `bypasses_shield`. Les projectiles (flèches, tridents, boules de feu), les explosions de creeper, le rayon du gardien et le cri sonique du warden ne sont pas parés.
3. Parade réussie : l'attaquant est repoussé (`attacker.knockback(1.0, player.getX() - attacker.getX(), player.getZ() - attacker.getZ())`, avec `hurtMarked` pour un joueur, sinon son client ne voit pas le recul), son `SHIELD_BLOCK` (volume 1, hauteur 1,3), particules `CRIT` autour du joueur. La parade prend fin et le cooldown du katana disparaît : on peut enchaîner sur un dash.
4. Le cooldown est celui du katana, partagé avec le dash : une parade ratée bloque le dash pendant 1 s, et un dash bloque la parade jusqu'à la fin de son cooldown.
5. À savoir : les épines restent parables, car leur entité directe est le mob lui-même. Les exclusions `is_explosion` et `bypasses_shield` écartent les autres cas de ce genre (explosion de creeper, cri sonique du warden ; le rayon du gardien est de la magie indirecte).

| Élément | Choix |
|---|---|
| `KatanaItem#use` | si `player.isShiftKeyDown()` : parade au lieu du dash |
| État | attachment `katana_parry` (`AttachmentType<Long>`, 0 par défaut, ni sérialisé ni synchronisé) : date de jeu jusqu'à laquelle la parade est active, posée côté serveur. Aucun travail par tick |
| Événement | `EntityInvulnerabilityCheckEvent` pour l'invulnérabilité du dash, `LivingIncomingDamageEvent` pour la parade (côté serveur, `ServerPlayer`) |
| Config | section `katana` : `parryTicks` (6, de 1 à 40) et `parryCooldown` (20, de 0 à 200) |

### 10.6 Main gauche verrouillée

Condition : katana en main principale, et main gauche non vide qui ne tient pas un katana. Ces événements sont communs aux deux côtés, pour éviter les désynchronisations (animation de nourriture lancée côté client, par exemple).

| Usage de la main gauche | Événement | Action |
|---|---|---|
| Manger, boire, lever le bouclier, lancer (perle, potion), feu d'artifice, arc | `PlayerInteractEvent.RightClickItem` avec `getHand() == OFF_HAND` | `setCanceled(true)` et `setCancellationResult(InteractionResult.FAIL)` |
| Poser un bloc, utiliser un seau, de la poudre d'os | `PlayerInteractEvent.RightClickBlock` (main gauche) | annuler |
| Laisse, étiquette, nourrir un animal | `PlayerInteractEvent.EntityInteract` et `EntityInteractSpecific` (main gauche) | annuler |
| Totem d'immortalité | `LivingUseTotemEvent#getHandHolding() == OFF_HAND` | annuler : le totem ne sauve pas |

Retour au joueur : « Votre main gauche est occupée par le katana », dans la barre d'action, une fois par seconde au plus.

On a écarté l'idée de vider automatiquement la main gauche : l'objet serait perdu si l'inventaire est plein, et c'est surprenant pour le joueur.

### 10.7 Obtention (« il faut les farms »)

[Proposition] Les squelettes tués par un joueur dans l'Entre-monde lâchent des fragments de lame (`blade_fragment`) :

| Butin | Chance |
|---|---|
| sans | 5 % |
| I | 7 % |
| II | 9 % |
| III | 11 % |

- Tout se fait en données, sans Java. Un global loot modifier `neoforge:add_table` cible la loot table `minecraft:entities/skeleton` (condition `neoforge:loot_table_id`) et ajoute une table du mod. Cette table porte les conditions `minecraft:killed_by_player`, `minecraft:location_check` (dimension `examplemod:undervoid`) et `minecraft:random_chance_with_enchanted_bonus`. Le format de cette dernière a été vérifié sur la loot table vanilla du wither squelette.
- Recette du katana : 2 fragments de lame et 1 planche du vide (la poignée), en diagonale.
- Coût : environ 40 squelettes par katana (environ 18 avec Butin III), donc le double pour en avoir deux.

### 10.8 Ressources et datagen

- Modèle d'item en `handheld`, texture du katana et du fragment.
- Recette.
- Tags `minecraft:swords` (qui inclut les enchantements d'épée).
- JSON du loot modifier, `data/neoforge/loot_modifiers/global_loot_modifiers.json` (on ajoute notre entrée) et loot table du fragment.
- Lang, dont le message de main gauche verrouillée et les clés de config `parryTicks` et `parryCooldown`.
- Rien de nouveau pour D14 en datagen ni en textures : sons et particules vanilla.

**Critères d'acceptation**
- [x] Le dash traverse un groupe de 3 monstres et les touche tous, chacun une fois, au moment où le joueur passe ; il s'arrête devant un mur, sans jamais finir dedans.
- [x] Avec un bouclier en main gauche, impossible de le lever ; avec un second katana, le dash passe à 8 blocs et aux dégâts du mode double (vérifié avec 14, la valeur d'avant D14).
- [x] Le mouvement est continu (4 ticks), et un dash qui finit en l'air laisse le joueur suspendu 0,2 s (0,5 s avant D19) ; au sol, on peut sauter tout de suite.
- [x] Le dash suit le regard (haut, bas, côtés) ; au sol, regarder vers le bas dashe à l'horizontale. Mesuré dans le client le 26/10 : 7,00 blocs à l'horizontale, au sol en regardant à 30° vers le bas aussi ; 4,95 en avant et 4,95 en haut à 45° ; 7,00 tout droit vers le haut ; suspension de 4 ticks à chaque fois ; positions identiques côté client et côté serveur. Dégâts de chute ensuite : 2 après le dash à 45°, 4 après le dash vertical, supprimés depuis D26 (GameTest du 26/10 : chute de 12 blocs après un dash, aucun dégât ; la même chute sans dash, 6 dégâts ; protection retirée au sol).
- [ ] Le cooldown s'affiche sur les deux katanas.
- [ ] Un totem en main gauche ne sauve pas.
- [ ] Les fragments ne tombent que dans l'Entre-monde, et seulement quand c'est un joueur qui tue.
- [x] Mêlée à 8 dégâts et vitesse 1,8 ; durabilité de 1500 (GameTest du 26/10).
- [ ] Le dash fait 8 dégâts (20 à deux katanas), 11 (23) avec Tranchant V ; Châtiment compte contre les morts-vivants ; Aura de feu enflamme les cibles. GameTest du 26/10 : 7,9 puis 10,8 avec Tranchant V sur un husk (2 d'armure), et Aura de feu enflamme ; restent le double katana et Châtiment.
- [x] Un dash qui tue un joueur retire le cooldown des deux katanas ; un dash qui tue un mob le réduit de moitié ; une seule fois par dash, avec un son d'orbe d'XP que seul ce joueur entend (GameTests du 26/10 pour le cooldown ; le son n'a pas été écouté).
- [x] Pendant les 4 ticks du mouvement, aucun dégât (coup, flèche, chute), sauf le vide et `/kill` (GameTest du 26/10 : 4 coups de husk annulés, le 5ᵉ, après le mouvement, porte).
- [x] Accroupi + clic droit : un zombie qui frappe dans les 0,3 s ne fait aucun dégât et il est repoussé ; son de bouclier, particules, et le dash est disponible tout de suite (GameTest du 26/10 avec un husk ; son et particules non vus).
- [x] Une flèche reçue pendant la parade fait ses dégâts ; une parade ratée bloque le katana 1 s (GameTest du 26/10).

**Estimation** : 6 à 8 h.

---

## 11. Hérisson

**Sources** : D11 (« Il faut aussi ajt un hérisson »). Tout le reste est une **[Proposition]**.

### 11.1 Intention

Le compagnon réconfortant du Bloom. Le caresser fait du bien, mais pas quand il est en boule.

### 11.2 Règles

1. Animal passif, `MobCategory.CREATURE`. Taille de 0,5 × 0,4 bloc ; les bébés sont deux fois plus petits.
2. Il se roule en boule quand il est blessé (5 s) ou quand un joueur court à moins de 3 blocs de lui (3 s).
3. En boule : il ne bouge plus (`isImmobile()` renvoie true), il subit des dégâts × 0,25, et un attaquant au corps à corps prend 2 dégâts d'épines (`damageSources().thorns(this)`).
4. Le caresser (clic droit à main vide) : s'il n'est pas en boule, des cœurs apparaissent et la santé mentale gagne +8 (§6). Chaque hérisson a un cooldown de 60 s, stocké sous forme de date de jeu : aucun travail par tick. S'il est en boule, le joueur prend 1 dégât.
5. Reproduction avec des baies sucrées ou des baies lumineuses (tag `hedgehog_food`). Il se laisse aussi attirer par ces baies.
6. Pas de butin (seulement de l'XP), et pas d'apprivoisement, pour rester simple.

### 11.3 Valeurs

| Propriété | Valeur |
|---|---|
| PV | 8 |
| Vitesse | 0,2 |
| Contrôle « joueur qui court » | toutes les 10 ticks, via `level.getNearestPlayer(this, 3.0)`, qui parcourt la liste des joueurs et non les entités |
| Spawn dans le Bloom | poids 8, groupes de 1 à 3 |
| Spawn dans l'overworld | `#minecraft:is_forest` et prairie, poids 4, groupes de 1 à 2, via un biome modifier `neoforge:add_spawns` |

**Règle de spawn** : `RegisterSpawnPlacementsEvent#register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, prédicat, Operation.REPLACE)`.
- Le bloc dessous doit être dans le tag `hedgehog_spawnable_on` (herbe, mousse, podzol, terre racineuse).
- Il n'y a pas de condition de lumière : la règle des animaux vanilla demande de l'herbe et de la lumière, ce qui exclut les cavernes.

À savoir : les créatures sont surtout générées à la création des chunks, en surface (heightmap). Dans l'Entre-monde, la surface est le Gloom, sans créatures. Les hérissons du Bloom apparaissent donc au fil de la partie, par les tentatives périodiques de spawn des créatures persistantes (toutes les 400 ticks, soit 20 s : vérifié dans `ServerChunkCache`).

### 11.4 Mise en œuvre

| Élément | Choix |
|---|---|
| `entity/ModEntities` | `EntityType.Builder.of(Hedgehog::new, MobCategory.CREATURE).sized(0.5f, 0.4f)`, et l'œuf `DeferredSpawnEggItem(ModEntities.HEDGEHOG, 0x6B4F3A, 0xD9C7A6, props)` |
| `entity/custom/Hedgehog` | étend `Animal` et implémente `GeoEntity` ; état `DATA_CURLED` synchronisé par `EntityDataAccessor<Boolean>` |
| Goals vanilla | `FloatGoal`, `BreedGoal`, `TemptGoal`, `FollowParentGoal`, `WaterAvoidingRandomStrollGoal`, `LookAtPlayerGoal`, `RandomLookAroundGoal` |
| Attributs | `EntityAttributeCreationEvent#put` (bus du mod) |
| Animations GeckoLib 4.8.4 | `GeckoLibUtil.createInstanceCache(this)`. Un contrôleur `new AnimationController<>(this, "main", 5, état -> …)` : en boule `thenPlayAndHold("curl")`, en mouvement `thenLoop("walk")`, sinon `thenLoop("idle")` |
| Rendu | `HedgehogRenderer extends GeoEntityRenderer<Hedgehog>` avec `new DefaultedEntityGeoModel<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "hedgehog"))`, enregistré par `EntityRenderersEvent.RegisterRenderers#registerEntityRenderer` |
| Sons | sons vanilla du tatou : `ARMADILLO_AMBIENT` (aigu), `ARMADILLO_HURT`, `ARMADILLO_ROLL` (boule), `ARMADILLO_UNROLL_FINISH` |

### 11.5 Ressources

| Fichier (chemins imposés par `DefaultedEntityGeoModel`) | Contenu |
|---|---|
| `assets/examplemod/geo/entity/hedgehog.geo.json` | Modèle Blockbench (« GeckoLib Animated Model ») : corps de 8×5×10 px, dos de piquants, museau, 4 pattes |
| `assets/examplemod/animations/entity/hedgehog.animation.json` | `idle` (avec reniflement), `walk`, `curl` |
| `assets/examplemod/textures/entity/hedgehog.png` | texture 32×32 |

S'y ajoutent : le modèle de l'œuf (`withExistingParent(…, "item/template_spawn_egg")`), le biome modifier, les tags et la lang.

**Critères d'acceptation**
- [ ] Taper un hérisson le met en boule et blesse l'attaquant.
- [ ] Le caresser donne +8 de santé mentale, au plus une fois par minute par hérisson.
- [ ] Des hérissons apparaissent dans le Bloom après quelques minutes et dans les forêts de l'overworld.
- [ ] Les bébés suivent leurs parents.

**Estimation** : 8 à 12 h, dont 4 à 6 h de modèle et d'animations.

---

## 12. Succès

**Sources** : D10. Les titres restent en anglais dans les deux langues.

### 12.1 Arbre

| ID | Titre | Description en_us / fr_fr | Icône | Cadre | Parent | Déclencheur |
|---|---|---|---|---|---|---|
| `undervoid/root` | Welcome to the in-between world | Survive the void long enough to fall through it / Survivre au vide assez longtemps pour passer à travers | `dark_dirt` | onglet (racine) | — | `minecraft:changed_dimension` vers `examplemod:undervoid` |
| `undervoid/ouch_my_head` | Ouch, my head | Start losing your sanity / Commencer à perdre la raison | rose de wither | task | root | `minecraft:impossible`, attribué en code (§6.5) |
| `undervoid/what_is_this` | What is this ? | Experience hallucinations / Subir des hallucinations | œil de l'Ender | goal, caché | ouch_my_head | `minecraft:effects_changed` avec `examplemod:hallucinations` |
| `undervoid/fast_and_sharp` | Fast and sharp | Obtain a katana / Obtenir un katana | `katana` | goal | root | `minecraft:inventory_changed` avec `examplemod:katana` |

« Welcome to the in-between world » sert de racine à l'onglet, comme « Nether » chez vanilla. Les trois autres succès ne peuvent arriver qu'après l'entrée dans la dimension.

### 12.2 « Ouch, my head » en code

Il n'y a pas de déclencheur vanilla pour « commencer à perdre de la santé mentale ». On a deux options :
- un `CriterionTrigger` custom : propre, mais il faut une classe, un codec et un registre ;
- `minecraft:impossible` et un appel en code : `player.getAdvancements().award(server.getAdvancements().get(id), "lost_sanity")`.

Je recommande la seconde. L'`id` est une constante, et l'appel n'a lieu qu'au passage de 100 à moins de 100. `award` ne fait rien si le succès est déjà obtenu.

### 12.3 Fichiers

Les JSON sont écrits à la main dans `src/main/resources/data/examplemod/advancement/undervoid/` (le dossier est au singulier en 1.21.1). C'est la même logique que `noise_settings`, et cela évite de toucher à la datagen. Le format est calqué sur `advancement/nether/root.json` et `story/form_obsidian.json` de vanilla.

`root.json` :

```json
{
  "criteria": {
    "entered_undervoid": {
      "trigger": "minecraft:changed_dimension",
      "conditions": {
        "to": "examplemod:undervoid"
      }
    }
  },
  "display": {
    "icon": {
      "id": "examplemod:dark_dirt"
    },
    "title": {
      "translate": "advancements.examplemod.undervoid.root.title"
    },
    "description": {
      "translate": "advancements.examplemod.undervoid.root.description"
    },
    "background": "examplemod:textures/block/dark_dirt.png",
    "show_toast": true,
    "announce_to_chat": true
  }
}
```

Critères des trois autres (display sur le même modèle, avec `"parent"`, `"frame"` et, pour `what_is_this`, `"hidden": true`) :

```json
{ "lost_sanity": { "trigger": "minecraft:impossible" } }
```

```json
{ "hallucinating": { "trigger": "minecraft:effects_changed", "conditions": { "effects": { "examplemod:hallucinations": {} } } } }
```

```json
{ "has_katana": { "trigger": "minecraft:inventory_changed", "conditions": { "items": [ { "items": "examplemod:katana" } ] } } }
```

Clés de lang : `advancements.examplemod.undervoid.<id>.title` et `.description`, en en_us et fr_fr.

### 12.4 Succès optionnels [Could, fait]

| Titre | Condition | Déclencheur | Parent |
|---|---|---|---|
| Twice as sharp | Faire un dash avec deux katanas | `minecraft:impossible`, attribué dans `KatanaItem` (cadre défi) | fast_and_sharp |
| Breathe easy | Avoir les 4 pièces Hazmat | `minecraft:inventory_changed` avec 4 objets | root |
| Prickly friend | Caresser un hérisson | `minecraft:impossible`, attribué dans `Hedgehog` lors d'une caresse réussie | root |

« Twice as sharp » ne peut pas passer par `inventory_changed` : un seul katana suffit à valider deux conditions identiques. Il faut donc un appel en code. « Prickly friend » passe aussi par le code : `player_interacted_with_entity` se déclencherait en nourrissant le hérisson ou en se piquant sur lui en boule.

Les succès attribués en code passent par `util/ModAdvancements.award`, qui valide les critères restants et ne fait rien si le succès est déjà obtenu. « Ouch, my head » l'utilise aussi.

**Estimation** : 1 à 2 h.

---

## 13. Contenu à créer

| ID | Type | en_us | fr_fr |
|---|---|---|---|
| `undervoid` | dimension (existe) | The In-Between | L'Entre-monde |
| `gloom` | biome | Gloom | Gloom |
| `bloom` | biome | Bloom | Bloom |
| `bloom_flower` | bloc | Bloom Flower | Fleur du Bloom |
| `potted_bloom_flower` | bloc (pot) | Potted Bloom Flower | Fleur du Bloom en pot |
| `miasma` | biome | Miasma | Miasme |
| `bloom_rift` | bloc (portail, sans item) | Bloom Rift | Faille du Bloom |
| `void_tree`, `bloom_flower_patch`, `bloom_rift` | features | — | — |
| `bloom_caverns`, `miasma` | bruits de génération | — | — |
| `hazmat_fabric` | item | Coated Fabric | Tissu enduit |
| `hazmat_mask` | armure (tête) | Gas Mask | Masque à gaz |
| `hazmat_suit` | armure (torse) | Hazmat Suit | Combinaison Hazmat |
| `hazmat_leggings` | armure (jambes) | Hazmat Pants | Pantalon Hazmat |
| `hazmat_boots` | armure (pieds) | Hazmat Boots | Bottes Hazmat |
| `katana` | arme | Katana | Katana |
| `blade_fragment` | item | Blade Fragment | Fragment de lame |
| `hedgehog` | entité | Hedgehog | Hérisson |
| `hedgehog_spawn_egg` | item | Hedgehog Spawn Egg | Œuf d'apparition de hérisson |
| `mimic` | entité | Mimic | Imitateur |
| `mimic_spawn_egg` | item | Mimic Spawn Egg | Œuf d'apparition d'imitateur |
| `hallucinations` | effet | Hallucinations | Hallucinations |
| `serenity`, `clarity` | effets et potions (+ `long_`, `strong_`) | Serenity, Clarity | Sérénité, Clarté |
| `insanity` | type de dégât | %1$s lost their mind | %1$s a perdu la raison |
| `miasma` | type de dégât | %1$s choked in the miasma | %1$s s'est étouffé dans le miasme |
| `sanity`, `void_ticks`, `katana_dash`, `katana_parry` | attachments | — | — |

Textures à dessiner :
- blocs : `bloom_flower`, `bloom_rift` (animée, 16 images) ;
- items : 5 pièces Hazmat (avec le tissu), katana, fragment ;
- couches d'armure : 2 ;
- entité : 1 ;
- HUD : 3 sprites ;
- vignette d'hallucination ;
- icônes d'effets : 3.

L'imitateur n'a pas de texture : il reprend le skin du joueur copié.

Aucun son custom n'est obligatoire.

---

## 14. Architecture technique et performance

### 14.1 Arborescence

Ce qui existait est gardé.

```
com/ussm/examplemod/
  ExampleMod, ExampleModClient, Config
  attachment/ModAttachments           sanity, void_ticks, katana_dash, katana_parry
  particle/ModParticles               MIASMA
  block/ModBlocks                     + BLOOM_FLOWER, POTTED_BLOOM_FLOWER, BLOOM_RIFT
  block/custom/BloomRiftBlock         faille du Bloom (Portal vanilla)
  effect/ModEffects                   + HALLUCINATIONS, SERENITY, CLARITY
  entity/ModEntities, ModEntityEvents, MimicSpawner
  entity/custom/Hedgehog, Mimic
  item/ModItems, ModArmorMaterials, ModToolTiers, KatanaEvents
  item/custom/KatanaItem, KatanaDash, HazmatArmorItem
  network/ModEvents                   accès et sortie de la dimension (réécrit)
  potion/ModPotions, ModBrewing
  sanity/Sanity, SanityEvents         + effets du miasme
  util/ModTags, ModAdvancements
  worldgen/...                        + tree/ModTreeGrowers, biome miasma, feature bloom_rift
  client/ModClientEvents, SanityHudLayer, Hallucinations, HallucinationVignette,
         GasMaskOverlay, HedgehogRenderer, MimicRenderer, MiasmaParticle, UndervoidEffects, UndervoidFog
  datagen/...                         entrées ajoutées au fil des lots
```

- Le constructeur d'`ExampleMod` appelle `register(modEventBus)` pour `ModBlocks`, `ModItems`, `ModEffects`, `ModPotions`, `ModEntities`, `ModArmorMaterials`, `ModAttachments` et `ModParticles`. Son `commonSetup` ajoute la fleur du Bloom au pot de fleurs vanilla.
- Les événements du bus du mod (`EntityAttributeCreationEvent`, `RegisterSpawnPlacementsEvent`, `RegisterGuiLayersEvent`, `RegisterDimensionSpecialEffectsEvent`, `EntityRenderersEvent`, `RegisterClientExtensionsEvent`) et ceux du bus du jeu (ticks, interactions, `RegisterBrewingRecipesEvent`, `RegisterCommandsEvent`) passent par des classes `@EventBusSubscriber`, comme le font déjà `ExampleModClient` et `DataGenerators`.
- Tout ce qui est client est dans `client/`, avec `value = Dist.CLIENT`.
- Pas de paquet réseau custom : la synchro des attachments, les cooldowns d'items, les effets et les succès sont déjà synchronisés par le jeu. Si un paquet devenait nécessaire : `RegisterPayloadHandlersEvent`.

### 14.2 Configuration

Le `Config` d'exemple est remplacé.

| Config | Valeurs |
|---|---|
| SERVER (synchronisée aux clients) | durée de survie au vide (200 ticks), vitesses de santé mentale par situation, `M_hazmat`, seuils des paliers, distances, dégâts et cooldowns du katana, fenêtre et cooldown de la parade, seuil de santé mentale et chance d'apparition des imitateurs, perte de santé mentale et dégâts du miasme, chances de fragment (si on passe la loot table en config) |
| CLIENT | `hallucinationIntensity` |

Les valeurs se lisent avec `.get()`. NeoForge les met en cache : aucun coût à les lire chaque seconde.

[Demande, D18] La commande client `/blue` ouvre l'écran de configuration NeoForge du mod (`ConfigurationScreen`) : boutons Client et Serveur, puis toutes les options par section, traduites en anglais et en français. Elle est enregistrée dans `client/ModClientEvents` et ne passe pas par le serveur. Sur un serveur distant, l'accès à la section Serveur suit les règles de NeoForge (non testé).

### 14.3 Règles de performance (C1)

1. Pas d'`EntityTickEvent` global. Seulement `PlayerTickEvent.Post`, côté serveur, avec une sortie immédiate dans le cas courant.
2. Travail par joueur à 1 Hz, décalé entre joueurs (`(tickCount + id) % 20`).
3. Aucune allocation dans les chemins chauds. Les `ResourceKey`, `ResourceLocation`, `TagKey` et `Component` fixes sont des constantes `static final`. Pas de stream ni de lambda créés à chaque tick.
4. Synchro réseau : `setData` seulement si la valeur change, et au seul joueur concerné (hors l'envoi initial de NeoForge, §6.2).
5. Client : le biome à la caméra est lu une fois par tick, jamais à chaque frame. Une seule instance d'ombre, réutilisée. Le brouillard passe par l'événement prévu, sans calcul lourd.
6. Entités : une requête d'AABB par dash. Le hérisson utilise `getNearestPlayer`, qui parcourt la liste des joueurs, toutes les 10 ticks. Son cooldown de caresse est une date de jeu, pas un compteur. L'imitateur résout le joueur copié au plus une fois par seconde ; son apparition est tirée à 1 Hz par joueur, et le biome et la recherche d'un autre imitateur ne sont lus qu'après un tirage réussi.
7. Worldgen : aquifers et filons désactivés, terrain à gradient simple, features vanilla réutilisées.
8. GeckoLib seulement pour le hérisson (et le masque si on choisit l'option 3D). Un contrôleur, des transitions courtes.
9. Le drop des fragments est en données (loot modifier), sans événement Java.

### 14.4 Conventions de code

Les conventions viennent du code existant :
- registres dans des classes `ModXxx` avec une méthode `register(IEventBus)` ;
- constantes en `UPPER_SNAKE_CASE` ;
- ids en `snake_case` ;
- classes spécifiques dans des sous-packages `custom` ;
- helpers sur le modèle de `registerBlock` ;
- identifiants en anglais, 4 espaces d'indentation.

On écrit du code auto-explicatif : des noms clairs plutôt que des commentaires, et pas de code commenté ni de restes de tutoriel. Tous les textes visibles passent par la lang, en `en_us` et en `fr_fr`.

### 14.5 Build et tests

- `runData`, puis versionner `src/generated/resources` (écart n° 1). Sinon la CI et les autres membres de l'équipe n'ont pas la dimension.
- `runClient` pour tester. Raccourcis : vide de l'End en créatif (transfert immédiat) et `/sanity set`.
- Avant la remise : faire passer les critères d'acceptation de chaque section sur un monde neuf, en solo puis à deux joueurs (pour vérifier que les hallucinations restent locales et que la synchro de la santé mentale ne concerne que son joueur).

---

## 15. Planning

### 15.1 Lots

| Lot | Contenu | Dépend de | Estimation |
|---|---|---|---|
| A. Socle | versionner `src/generated`, nettoyer le template, décider du mod_id | — | 2 à 3 h |
| B. Dimension | noise settings, 2 biomes, surface rules, features, arbre du vide, `bloom_flower`, effets de dimension, brouillard | A | 10 à 14 h |
| C. Accès et sortie | chrono du vide, arrivée sûre, sortie par le haut | B | 3 à 4 h |
| D. Santé mentale | attachments, calcul, paliers, HUD, commande, type de dégât | B | 6 à 8 h |
| E. Potions | 2 effets, 6 potions, recettes, masque qui empêche de boire | D, `bloom_flower` | 2 à 3 h |
| F. Hallucinations | effet, sons, caméra, vignette, ombre | D | 6 à 8 h |
| G. Hazmat | matériau, 5 items, textures, `M_hazmat`, tooltip | D | 4 à 5 h (+4 à 6 h GeckoLib) |
| H. Katana | item, dash, main gauche, fragments, recette | B pour les fragments | 6 à 8 h |
| I. Hérisson | entité, IA, boule, caresse, spawns, modèle et animations | D pour la caresse | 8 à 12 h |
| J. Succès | 4 JSON, lang, attribution en code | D, F, H | 1 à 2 h |
| K. Finitions | lang en/fr, textures, équilibrage, monde de démo, tests | tout | 5 à 8 h |
| L. Imitateurs | entité, apparition, déguisement et révélation, rendu en joueur, œuf, config | B, D | 6 à 8 h |
| **Total** | | | **environ 59 à 83 h** |

### 15.2 Jalons

1. Socle et dimension jouable : A, B, C.
2. Santé mentale visible : D et les succès 1 et 2.
3. Combat et équipement : H, G, E.
4. Ambiance : F, I, et les succès 3 et 4.
5. Finitions et tests : K.

### 15.3 Répartition suggérée

| Personne | Lots |
|---|---|
| OeildeLynx31 (déjà sur la dimension et la datagen) | B, C, et les entrées de datagen de tous les lots |
| Personne 2 | D, E, F, J |
| Personne 3, ou la personne 2 ensuite | H, G, I |

### 15.4 Priorités en cas de manque de temps

À couper en premier, dans l'ordre :
1. option GeckoLib du masque ;
2. overlay du masque ;
3. succès optionnels ;
4. potion d'Angoisse ;
5. ombre des hallucinations (garder les sons et la caméra) ;
6. spawns du hérisson dans l'overworld.

---

## 16. Risques

| Risque | Impact | Parade |
|---|---|---|
| Le réglage du bruit des cavernes prend plus de temps que prévu | dimension en retard, tout le reste attend | Commencer par un terrain plat sans cavernes, puis ajouter les cavernes ; les autres lots peuvent avancer en parallèle |
| L'accès est de fin de partie (End obligatoire) | les jurés ne voient pas la dimension | Monde de démo, règle du créatif, page « comment tester » (§17) |
| Production du modèle et des animations du hérisson | hérisson sans animation | Prévoir une version statique en `idle` seul |
| Renommage tardif du mod_id | IDs cassés dans les mondes de test, beaucoup de fichiers à déplacer | Décider du nom avant de créer les nouveaux assets |
| Goulot sur la datagen (une seule personne) | blocage des autres lots | Lister les entrées dès le début (sections « Ressources et datagen ») ; JSON à la main quand c'est possible (succès, loot modifier, noise settings) |
| Dash qui traverse un mur ou finit dans un bloc | joueur coincé | Tests de collision (§10.4) et critère d'acceptation dédié |
| Hallucinations jugées pénibles | mauvaise impression | `hallucinationIntensity`, fréquences en config |

---

## 17. Questions ouvertes

1. **Nom du mod et mod_id définitif** : à décider avant de créer les nouveaux assets. Pistes : « The In-Between », « Gloom & Bloom ».
2. **Date limite et règlement de la jam** : y a-t-il un thème imposé (les noms Gloom et Bloom semblent en venir) ? Des règles sur les assets ?
3. **Comment les jurés testent-ils la dimension** si l'accès demande l'End ? Monde de démo, accès plus précoce, ou les deux ?
4. **Sortie de la dimension** : réglée le 26/10 par D15. Failles du Bloom, et la sortie au-dessus de Y 256 est gardée (§5.3).
5. **Rôle du `spawner_receptacle`** : il existe dans le code, mais sa fonction n'est pas décrite.
6. **Lecture de D3** : le chronomètre de 10 s suffit-il, ou veut-on aussi un seuil de profondeur ?
7. **Combinaison incomplète** : aucun effet (proposé), ou un bonus par pièce ?
8. **Faut-il de nouveaux monstres** dans le Gloom, ou les monstres vanilla suffisent-ils pour la jam ? D12 en demande un, l'imitateur (§8 bis) ; la question reste ouverte pour les autres.
9. **Hérisson** : simple animal, ou compagnon apprivoisable ?

---

## 18. État d'implémentation

Mis à jour le 26/10/2026. Les textures sont des placeholders, à redessiner. Rien n'est encore commité.

| Lot | État | Notes |
|---|---|---|
| A. Socle | Partiel | Template nettoyé (`ExampleMod`, `ExampleModClient`, `Config`), code de tutoriel commenté retiré des features, lang en_us et fr_fr complètes. Reste : mod_id et nom affiché (« Example Mod » dans la liste des mods, §17 question 1), versionner `src/generated/resources` (regénéré et à jour) |
| B. Dimension | Fait | Noise settings, 3 fonctions de densité et un bruit (§4.3), biomes `gloom` et `bloom` à la place de `test_biome`, surface rules, arbre du vide et son `TreeGrower`, buissons morts, fleurs et végétation du Bloom, pot de fleur, tags et recettes du bois du vide, effets de dimension, brouillard, musique coupée dans le Gloom |
| C. Accès et sortie | Fait | `ModEvents` réécrit : chrono de 200 ticks (config `voidSurvivalTicks`), transfert immédiat en créatif et en spectateur, arrivée sûre, sortie au-dessus de Y 256 vers le point de réapparition |
| D. Santé mentale | Fait | Attachment synchronisé, calcul à 1 Hz, paliers, HUD, commande `/sanity`, type de dégât `insanity`. Depuis D25, l'overworld remet la jauge à 100 et retire les hallucinations (GameTest du 26/10 : 30 → 100) |
| E. Potions | Fait | Sérénité et Clarté (6 potions), alchimie, masque qui empêche de boire |
| F. Hallucinations | Fait | Sons, caméra, champ de vision, vignette, ombre |
| G. Hazmat | Fait (option A) | 4 pièces, tissu enduit, recettes, tags d'enchantabilité, tooltip, overlay du masque (Could) |
| H. Katana | Fait | Dash fluide sur 4 ticks avec coups au passage, dans la direction du regard depuis D19, et suspension de 0,2 s en l'air (0,5 s demandée le 25/10, raccourcie le 26/10), double katana, main gauche verrouillée, fragments par loot modifier, recette |
| I. Hérisson | Fait | Entité GeckoLib (modèle et 3 animations), boule, piquants, caresse (cooldown sauvegardé avec l'entité), reproduction, spawn en forêt et dans le Bloom. La règle vanilla des animaux (lumière forte ou herbe) l'empêchait d'apparaître sur la mousse sombre du Bloom : il préfère maintenant les blocs de `hedgehog_spawnable_on` |
| J. Succès | Fait | 4 succès du Discord et les 3 optionnels du §12.4 |
| K. Finitions | Partiel | Lang faite, sauf l'infobulle du `spawner_receptacle`. Relecture complète du code faite (voir plus bas). Restent les textures définitives, l'équilibrage en jeu, le monde de démo pour les jurés (§17 question 3) et les tests à plusieurs |
| L. Imitateurs | Fait | Entité `mimic` (copie du skin, du pseudo et de l'équipement sans drop, gestes du joueur copié, suivi de la victime, révélation après 15 à 45 s ou au premier dégât), apparition à 1 Hz dans le Gloom sous 70 de santé mentale, en surface seulement depuis D23, rendu en joueur large ou fin, œuf, section de config `mimic`, lang. Respecte `doMobSpawning` et `spawn-monsters`. Vérifié par 11 GameTests et des captures en jeu (skin, armure, épée, pseudo, accroupi, révélation). Limite connue : la cape du joueur copié n'est pas dessinée |
| H bis. Katana renforcé (D14, D19, D20, D22) | Fait | Build et GameTests du 26/10 : mêlée à 8, dash à 8 et 20 (12 et 30 depuis D20, cooldowns de 1 et 2 s, 7 blocs pour un katana), dash dans la direction du regard (D19), sans dégâts de chute jusqu'au retour au sol (D26), durabilité 1500, enchantements sur le dash, recharge au kill (complète pour un joueur, de moitié pour un mob depuis D22, config `mobKillCooldown`), invulnérabilité pendant les 4 ticks du mouvement, parade (accroupi + clic droit), config `parryTicks` et `parryCooldown`, lang. Restent les tests en jeu et l'équilibrage (écart de DPS réduit avec Tranchant, §10.3 ; dégâts parés par la règle du coup direct, §10.5) |
| C bis. Failles du Bloom (D15) | Fait | Build, GameTests et capture du 26/10 : bloc `bloom_rift` (portail vanilla vers l'overworld, mêmes X et Z), particules et son, texture animée, entrée de blockstate, features `bloom_rift` et `bloom_rift_placed` dans le Bloom, lang. La sortie au-dessus de Y 256 est gardée. Une faille tous les 110 chunks environ depuis D24 (10 avant, §5.3) |
| B bis. Miasme (D16, D17) | Fait | Build, GameTests et captures du 26/10 : biome `miasma`, bruit `miasma` dans `noise_router.vegetation`, placement par plages de `humidity` et `depth`, surface rules, brouillard violet à 6 blocs (9 avec le masque), perte de 3 par seconde, demi-cœur toutes les 3 s sauf combinaison complète, type de dégât `miasma`, imitateurs, config `miasma`, lang. Seuil 0,35, octave −6 et `xz_scale` 1,5 réglés sur une génération réelle : 11 à 13 % de la surface, taches de 20 à 60 blocs (§4.9). Depuis D21, particule maison lumineuse et émetteur autour du joueur |
| Spawns sous terre (D23) | Fait | Plus aucun monstre sous la surface de l'Entre-monde (`LevelEvent.PotentialSpawns`), squelette retiré du Bloom, imitateurs en surface seulement (§4.7) |
| A bis. Commande `/blue` (D18) | Fait | Commande client qui ouvre l'écran de configuration du mod (§14.2). Vérifiée dans le client le 26/10 : l'écran s'ouvre, les 28 clés d'options et de sections ont leur texte en_us et fr_fr |

Non faits, parmi les options du §15.4 : l'armure GeckoLib du masque et la potion d'Angoisse.

Entrées ajoutées à la datagen, sans réorganiser les providers :
- worldgen : `ModBiomes` (3 biomes depuis le 26/10, avec `miasma`), `ModConfiguredFeatures` et `ModPlacedFeatures` (arbre du vide, fleurs, failles du Bloom), `ModDimensions` (`ambient_light`, `effects`, source de biomes à 4 entrées depuis le 26/10) ;
- blocs : `dropSelf` et blockstate de `bloom_flower`, loot et blockstate de `potted_bloom_flower`, blockstate et modèle `cross` translucide de `bloom_rift` (pas de loot).

Tout le reste est en JSON écrit à la main dans `src/main/resources` : noise settings, fonctions de densité, bruits (`bloom_caverns`, `miasma`), modèles d'items, recettes, tags, succès, loot modifier, biome modifier, types de dégât (`insanity`, `miasma`). La texture animée de la faille est dessinée par un script Python (PIL), avec son `.mcmeta`.

Les vérifications ci-dessous datent d'avant les demandes du 26/10 (D14 à D26), qui sont testées à part (voir les lignes « bis » du tableau).

Vérifications faites :
- build complet sans erreur ni warning ; `runData` sans erreur ;
- génération réelle de 144 chunks de l'Entre-monde sur un serveur de test, avec coupes rendues en image et comptages (relief, vide par couche, biomes, arbres, fleurs, absence d'eau et de minerai) : environ 4 s pour les 144 chunks ;
- GameTests temporaires (supprimés ensuite) : 6 sur les lots D à J (données, dash, main gauche et totem, non-cumul et paliers, hérisson) et 6 sur les lots B et C (chrono de 200 ticks, remise à zéro du chrono, créatif, sortie par le haut, objets non transférés, pousse du vide) ;
- client lancé sur un monde de test, captures d'écran en surface du Gloom (avec et sans masque), dans le Bloom et sous Hallucinations, sans erreur dans les logs ;
- dash mesuré tick par tick dans le client (au sol, en l'air, depuis un pilier, double) et coups vérifiés par GameTests ;
- relecture complète du code (accès, génération et données, client, gameplay, serveur dédié). Corrigés : joueur transféré à cheval (resté monté, puis bloqué), spectateur ramené dans le vide, message « pas de lit » en double, santé mentale pendant le générique, recul aléatoire des dégâts de folie, GeckoLib non déclaré dans `neoforge.mods.toml`, modèles d'objet du pousse et de la fleur, ombre des hallucinations invisible dans le brouillard et non coupée à intensité 0, brouillard levé dans l'eau, transparence du HUD, masque dessiné sur la longue-vue, brouillard du Bloom noirci sous Y 32, référence au niveau précédent gardée par l'ombre, ombre portée des bébés hérissons, hérissons absents du Bloom, cooldown de caresse perdu au rechargement, main gauche non bloquée pour un usage déjà commencé, caresse qui change la jauge en créatif, coups du dash au-delà du point d'arrêt réel. 7 GameTests vérifient les correctifs côté serveur, une capture vérifie le brouillard du Bloom. Non corrigée : l'infobulle du `spawner_receptacle` (texte à fournir).

Non testé : la transition du brouillard en mouvement, le jeu à plusieurs (hallucinations locales, synchro de la santé mentale), l'apparition des hérissons et des chauves-souris dans le Bloom au fil du temps, l'ombre des hallucinations, et la sortie vers un lit (seul le spawn du monde a été testé).
