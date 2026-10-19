# Planeten-Dungeon bauen (CustomDungeon) – Anleitung

Projekt: Minecraft-Mod „Space Modernized“ (`space_m`, Addon zu HBM's Nuclear Tech Modernized), MC 1.20.1 Forge
und 1.21.1 NeoForge (Stonecutter). Repo: `/home/user/Modernized-Space`. **Kein Gradle verfügbar** – geprüft wird
ausschließlich mit der Harness in diesem Ordner.

## Ziel (Wunsch des Nutzers)
> „Ich finde das Prinzip mit den 3 Keys sehr geil, aber mir fehlt die Abwechslung in der Struktur der Dungeons.
> Mach die Dungeons alle unterschiedlich designed, gleiches Prinzip mit den Keys und dem Schild. Du kannst ruhig
> groß machen und abwechslungsreich.“

Zuvor kritisierte er: „Die Dungeons sind grauenvoll, man kann einfach zum Boss durchlaufen“ und wollte einen
„WOW-Effekt, RIESIG, mehr Details, verschiedene Blöcke“. Jeder Planet bekommt deshalb einen **komplett eigenen
Grundriss und eine eigene Architektur**. Das Prinzip bleibt gleich:

1. Spieler kommt von der Oberfläche (`entry()`).
2. Drei Siegelschlüssel, jeder am Ende einer **eigenen, andersartigen Prüfung** (z. B. Kletterturm, Parkour über
   Gefahr, Labyrinth, Spawner-Gauntlet, Siegelwächter-Kampf, Schwimmstrecke, Fallengang, Rätselweg über
   Ebenen …). Schlüssel = `k.keyChest(...)` (Truhe) **oder** `k.elite(...)` (Wächter trägt ihn). Genau 3 insgesamt.
3. Ein Siegeltor (`k.sealGate(...)`) mit drei Schlössern versperrt den **einzigen** Weg zum Boss.
4. Dahinter eine große Boss-Arena mit `k.boss(...)` und zwei `k.bossChest(...)`.

## Datei
`src/main/java/com/space_m/dim/dungeon/design/<Name>.java`, `public final class <Name> extends CustomDungeon`,
öffentlicher parameterloser Konstruktor. **Nur diese eine Datei anlegen/ändern** (keine anderen Repo-Dateien, kein
git). Registrierung macht der Hauptagent. Vorlage für Planetenmaterial/Biom-Regeln: die bisherige Klasse des
Planeten in `design/PlanetDesigns.java` (surface(), filler(), fits()/fitsAt(), minBase() übernehmen!).
Baukasten: `src/main/java/com/space_m/dim/dungeon/CustomDungeon.java` (lesen!). Beispiel:
die fertigen Designs in `design/*Dungeon.java`.

Pflicht-Overrides: `stage()`, `radius()`, `surface()`, `filler()`, `minY()`, `maxY()`, `entry()`, `boss()`,
`build(Kit k)`; je nach Planet `fits(...)`/`fitsAt(...)`, `minBase()`, `maxBase()`.

Erlaubte Imports: `com.hbm_m.block.ModBlocks`, `com.space_m.dim.dungeon.*`, `com.space_m.progression.SpaceProgression`,
`com.space_m.dim.BiomeGenBaseCelestial`, `com.space_m.dim.CelestialWorld`, `java.util.*`, `net.minecraft.core.Direction`,
`net.minecraft.world.level.block.Block/Blocks`, `…block.state.BlockState`, `…block.state.properties.*`,
`com.space_m.dim.sporos.city.CityPalette/CityRandom`. Planetenblöcke **voll qualifiziert** schreiben, z. B.
`com.space_m.dim.minmus.MinmusBlocks.MINMUS_STONE.get()`; Biome voll qualifiziert wie in PlanetDesigns.
Blöcke: `Blocks.X` (muss in 1.20.1 **und** 1.21.1 existieren – die Harness prüft das; z. B. `TUFF_BRICKS`,
`GRASS` gibt es nicht), `ModBlocks.X.get()` (HBM, Liste: `hbm_modblocks_fields.txt`), Planetenblöcke (Felder
werden geprüft). Lies `hbm_modblocks_fields.txt` und die `*Blocks.java` deines Planeten für Ideen.

## Koordinaten
Lokal: `u` = Ost, `v` = Süd, `y` relativ zur Grundhöhe (y 0 = Geländeoberfläche, Füße auf y 1). Der Dungeon wird
in der Welt beliebig gedreht – das Kit dreht Blockzustände (Treppen, Leitern, Truhen …) automatisch mit.
Alles muss in `max(|u|,|v|) <= radius()` und `minY()..maxY()` liegen. `minBase()+minY() >= 3`,
`maxBase()+maxY() <= 250` (Standard maxBase 190 → bei hohen Türmen maxBase senken, z. B. 160).
Unter y 0 ist Planetengestein, darüber Luft. Das Gelände wird nur dort eingeebnet, wo du bei y >= 0 baust (plus
Übergang); Unterirdisches bekommt bei Bedarf mindestens 3 Blöcke Deckung. Oberirdische Bauten brauchen also ein
eigenes Fundament nicht unbedingt, sehen aber besser aus mit Sockel/Treppen/Terrassen.

## Wichtig: Malen je Chunk
`build(k)` läuft **für jeden Chunk einzeln** mit einem 16×16-Ausschnitt (`k.u0()..k.u1()`, `k.v0()..k.v1()`), außerdem
einmal im Planmodus (leerer Ausschnitt) und einmal voll. Daraus folgt:
- Alle Kit-Primitive schneiden selbst zu. Eigene Schleifen über große Flächen **auf den Ausschnitt begrenzen**
  (`Math.max(a, k.u0())` …) oder ganze Bauteile mit `if (k.touches(u0, v0, u1, v1)) { … }` überspringen.
- **Plan-Einträge** (`keyChest, loot, barrel, bossChest, spawner, mob, elite, boss, arrowTrap`) **immer** aufrufen,
  nie innerhalb eines `touches`-Blocks/Returns. Ihre Position muss deterministisch sein (gleiches Ergebnis in allen
  Läufen; `k.rnd(...)` ist deterministisch).
- `k.get()` liefert nur Blöcke im aktuellen Ausschnitt; Logik, die von `get()` abhängt, ist riskant (die Harness
  vergleicht Chunk-Malen mit Voll-Malen). Lieber rechnen statt lesen.
- Reihenfolge zählt: später gesetzte Blöcke überschreiben frühere (erst Räume, dann Türen/Durchbrüche, dann Deko).
- Zeitbudget: Ein Chunk sollte unter ~30 ms bleiben (die Harness misst).

## Kit-API (Auszug, Details in CustomDungeon.java)
- Zuschnitt: `u0() u1() v0() v1() col(u,v) touches(ua,va,ub,vb)`
- Bloecke: `set air get isAir setIfAir fill clear room cylinder tube ellipsoid shell shellCut line spiral`
- Zufall: `rnd(salt,u,y,v)` [0,1), `noise(salt,u,v,scale)`, `pick(salt,u,y,v, s1, s2, …)`
- Zustände (statisch): `Kit.b(Block)`, `Kit.stairs(Block, upDir, top)`, `Kit.slab(Block, top)`, `Kit.facing(Block, dir)`,
  `Kit.facingAll(Block, dir)`, `Kit.axis(Block, axis)`, `Kit.lantern(Block, hanging)`, `k.ladder(u,y,v,wallDir)`,
  `k.ladder(u,y0,y1,v,wallDir)`; für weitere Eigenschaften `CityPalette.with(state, BlockStateProperties.X, wert)`.
- Gameplay: `sealGate(cu,y,cv,front,half,h,frame,accent)`, `keyChest`, `loot`, `barrel`, `bossChest`, `spawner`,
  `mob`, `elite`, `boss`, `arrowTrap(u,y,v,dir)`.
- Beutetabellen: `k.common` / `k.bossLoot` (macht loot()/bossChest() automatisch).

## Regeln (prüft die Harness)
- Genau 3 Schlüssel (Kisten `seal_key` + Wächter), alle bei **geschlossenem** Siegel vom Eingang aus erreichbar.
- Genau 3 Siegelschlösser, über das Siegelfeld zusammenhängend (nimm `sealGate`), alle erreichbar.
- Boss bei geschlossenem Siegel **nicht** erreichbar, bei offenem erreichbar; Hitbox-Raum um den Boss frei.
- Alle Truhen auf festem Block, nicht in Wänden, alle erreichbar; Spawner in Wegnähe.
- Mobs/Spawner/Wächter **nur** aus der Mob-Liste des Planeten (unten).
- Flüssigkeiten dicht (Lava/Wasser/HBM-Fluids dürfen nicht an Luft grenzen außer nach oben), Sand/Kies gestützt.
- Erreichbarkeit nach Spielerbewegung: gehen, 1 Block hoch springen, Weitsprung bis ~4 Blöcke (eben), Fallen bis
  30, Leitern/Ranken/Gerüst klettern, Wasser schwimmen. Zäune/Mauern/Gitter/Ketten sind keine Standfläche.
  Lava/Feuer/Pulverschnee/Säure/Quecksilber sind tödlich. **Spieler können im Dungeon keine Blöcke abbauen oder
  setzen** (Schutz bis zum Bosssieg) – jeder Weg muss ohne Bauen gehen. Türen (außer Eisen) zählen als offen.

## Qualitätsanspruch (nicht automatisch prüfbar – dafür Bilder ansehen!)
- **Groß**: Radius 60–100, mehrere Ebenen, riesige Räume (Hallen 30–60 Blöcke, Höhlen/Kuppeln), ein
  Wahrzeichen an der Oberfläche, das man von weitem sieht.
- **Eigenständig**: Grundriss, Architektur und Prüfungen sollen sich deutlich von anderen Planeten unterscheiden
  (das Briefing nennt das Konzept). Kein Kopieren der alten SealedDungeon-Struktur (Halle + 3 Flügel + Kuppel).
- **Details**: Materialmix (Grundstein + 2–3 Varianten mit Rauschen, Zierleisten, Säulen, Treppen/Stufen als
  Profile, Lichter in Rhythmus, Geländer, Ketten, Laternen, Verfall/Risse, Trümmer, Deko-Props passend zum Thema
  wie Fässer, Kisten, Maschinen, Rohre, Kristalle). Vanilla + HBM + Planetenblöcke mischen.
- **Spielbar**: Wege lesbar (Lichter führen), Prüfungen fordernd aber fair (keine blinden Sprünge, Sprünge ≤ 3
  Blöcke Lücke auf gleicher Höhe), Kämpfe in Räumen mit Platz, genug Licht gegen zufällige Spawns, Boss-Arena
  Radius ≥ 14 und ≥ 12 hoch (fliegende Bosse ≥ 18 hoch), Boss soll nicht weiter als ~28 Blöcke von seinem
  Startpunkt laufen müssen (er wird sonst zurückgeholt).
- Beute: 5–10 Planetenkisten/Fässer verteilt (`loot`/`barrel`), 5–10 Spawner, 10–25 Mobs, Pfeilfallen nur wo es passt.

## Ablauf
1. Konzept skizzieren (Ebenen, Koordinaten), dann Datei schreiben.
2. `bash tools/dungeoncheck/check.sh <Klasse>`
   → Bericht in `work_<Klasse>/report.txt`. Iterieren bis `ERGEBNIS: OK`; Warnungen bewerten.
3. Bilder: `python3 tools/dungeoncheck/render2.py tools/dungeoncheck/work_<Klasse> iso [ymax]`, `top`, `slice <y> <y2>` (Draufsicht-Schnitt
   mit begehbaren Feldern cyan und Markierungen), `section u=<n>` / `section v=<n>`. **Bilder mit dem Read-Tool
   ansehen** und Architektur/Details verbessern, bis es wirklich beeindruckend aussieht.
4. Abschließend mit mehreren Seeds prüfen: `check.sh <Klasse> 1 2 3 777 4242` muss OK sein.
5. Bericht zurück (kurz): Konzept, Ebenen mit y-Werten, Lage der 3 Schlüssel und Prüfungen, Arena, Größe, Zeiten,
   Harness-Ergebnis, offene Bedenken.

## Mob-Listen (Registry-IDs; Modelle/Verhalten baut der Hauptagent)
| Planet | Mobs |
|---|---|
| minmus | `space_m:frost_wisp` (schwebender Eisgeist, Fernkampf Frost), `space_m:glacier_brute` (schwerer Eisriese, Stampfen) |
| duna | `space_m:rust_scuttler` (kleiner schneller Spinnenroboter, Schwarm), `space_m:scrap_gunner` (Schrott-Schütze, Fernkampf) |
| ike | `space_m:void_shade` (teleportierender Schatten), `space_m:tomb_warden` (Steinwächter, Stampfen) |
| dres | `space_m:irradiated_ghoul` (verstrahlter Ghul), `space_m:toxic_sludge` (Schleim, teilt sich) |
| moho | `space_m:cinder_imp` (fliegender Feuerkobold, Feuerbälle), `space_m:magma_hound` (Lavahund, Sprung) |
| eve | `space_m:crystal_mantis` (Kristallgottesanbeterin, schnell), `space_m:schrab_spitter` (spuckt Schrabidium) |
| laythe | `space_m:deep_angler` (Anglerfisch, schwimmt – nur im Wasser sinnvoll), `space_m:abyss_diver` (Taucher, amphibisch, Harpune) |
| tekto | `space_m:polymer_ooze` (Polymerschleim, teilt sich), `space_m:refinery_drone` (fliegende Drohne, Flammen) |
| sporos | `space_m:hive_wasp` (Stockwespe, fliegt), `space_m:sporeling`, `space_m:lurker`, `space_m:blindcap`, `space_m:mycobrute` (vorhandene Infizierte) |

Wächter (`elite`) eher die schweren Typen. Fliegende Mobs gern als Spawner in hohen Räumen.

## Bosse (Hitbox B×H, Bewegung)
cryo_behemoth 3.2×5 laufen · rust_titan 3.5×3.8 laufen · monolith_keeper 2.6×5 **fliegt** · core_abomination 3×3.2 laufen ·
magma_wyrm 2.2×2 **fliegt** · schrab_hydra 2.8×3.4 laufen · abyssal_leviathan 3.5×3 **fliegt/schwimmt** ·
polymer_colossus 2.8×4.4 laufen · mycelial_queen 2.2×5.4 laufen.
