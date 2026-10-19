package com.space_beyond.dim.sporos.city;

import com.space_beyond.dim.sporos.city.Building.Kind;
import com.space_beyond.dim.sporos.city.CellPlan.Car;
import com.space_beyond.dim.sporos.city.CellPlan.Lamp;
import com.space_beyond.dim.sporos.city.CellPlan.Log;
import com.space_beyond.dim.sporos.city.CellPlan.LootSpot;
import com.space_beyond.dim.sporos.city.CellPlan.LotType;
import com.space_beyond.dim.sporos.city.CellPlan.Pond;
import com.space_beyond.dim.sporos.city.CellPlan.Rubble;
import com.space_beyond.dim.sporos.city.CellPlan.Ruin;
import com.space_beyond.dim.sporos.city.CellPlan.Tree;
import com.space_beyond.dim.sporos.flora.SporosFlora;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Setzt eine geplante Rasterzelle in den Chunk-Puffer. Reihenfolge: Strasse/Gehweg/Grundstuecksboden je Spalte,
 * Gebaeude ({@link Building#blockAt}), Einrichtung, Behaelter, Bewuchs auf Daechern/Etagen, Ranken an Fassaden,
 * Schutt, Wracks, umgestuerzte Staemme, Laternen/Ampeln, Teiche, Baeume. Jede Entscheidung haengt nur von Plan und
 * Weltkoordinaten ab; Lesezugriffe gibt es nur fuer Stellen im eigenen Chunk.
 *
 * <p>Bewuchs ist die Sporos-Flora (R2b): Hyphe-Baeume (Schirm, Rankenvorhang, Totholz) und Riesenpilz-Gruppen statt
 * Vanilla-Baeumen, Geflecht-Ranken an Fassaden und Masten, Leuchtpilze in dunklen Etagen, auf Totholz und Schutt,
 * Moospolster und Riesenfarne auf Daechern, Sporenblueten im Gras, Sporenkapseln auf Brachen und in Parks. Pilze
 * stehen nur auf Boeden, auf denen sie auch im Spiel ueberleben ({@link SporosFlora#FUNGUS_SOIL}).
 */
final class CityPainter {

    private CityPainter() {}

    static void paintCell(CityCanvas c, CellPlan plan) {
        CityPalette p = CityPalette.get();
        CityLayout layout = plan.layout;
        int x0 = Math.max(c.minX, plan.minX), x1 = Math.min(c.maxX(), plan.minX + CityLayout.CELL - 1);
        int z0 = Math.max(c.minZ, plan.minZ), z1 = Math.min(c.maxZ(), plan.minZ + CityLayout.CELL - 1);
        if (x0 > x1 || z0 > z1) return;

        // 1. Boden je Spalte
        for (int x = x0; x <= x1; x++) {
            for (int z = z0; z <= z1; z++) {
                int lx = x - plan.minX, lz = z - plan.minZ;
                double e = layout.edgeJittered(x, z);
                if (e >= CityLayout.URBAN) continue;
                if (lx < CityLayout.STREET || lz < CityLayout.STREET) street(c, plan, p, x, z, lx, lz, e);
                else if (lx < CityLayout.LOT_MIN || lz < CityLayout.LOT_MIN || lx > CityLayout.LOT_MAX || lz > CityLayout.LOT_MAX) sidewalk(c, plan, p, x, z, lx, lz, e);
                else lotGround(c, plan, p, x, z, lx, lz);
            }
        }

        // 2. Gebaeude
        Building b = plan.building;
        if (b != null) paintBuilding(c, plan, b, p);

        // 3. Grundstuecks-Objekte
        if (plan.ruin != null) ruin(c, plan, plan.ruin, p);
        if (plan.pond != null) pond(c, plan, plan.pond, p);
        if (plan.type == LotType.PARK) parkFurniture(c, plan, p);
        CityLandmarks.paint(c, plan, p); // Kontrollpunkt/Lager/Lazarett/Nest/Konvoi, Zelte, Sandsaecke (vor Wracks und Behaeltern)
        for (Rubble r : plan.rubble) rubble(c, plan, r, p);
        for (Car car : plan.cars) car(c, plan, car, p);
        for (Log log : plan.logs) fallenLog(c, plan, log, p);
        for (Lamp l : plan.lamps) lamp(c, plan, l, p);
        for (Lamp l : plan.trafficLights) trafficLight(c, plan, l, p);
        for (LootSpot s : plan.loot) lootContainer(c, s, p);
        for (Tree t : plan.trees) tree(c, plan, t, p);
    }

    // ── Strasse ─────────────────────────────────────────────────────────────

    private static void street(CityCanvas c, CellPlan plan, CityPalette p, int x, int z, int lx, int lz, double e) {
        int g = plan.groundY();
        long s = plan.layout.seed;
        boolean inter = lx < CityLayout.STREET && lz < CityLayout.STREET;
        boolean westSeg = lx < CityLayout.STREET;
        int across = westSeg ? lx : lz;
        int along = westSeg ? lz : lx;
        double decay = CityRandom.clamp01(plan.decay * 0.75 + CityRandom.smoothstep(0.5, CityLayout.URBAN, e) * 0.7);
        double n = CityRandom.noise(s ^ 0x57EEL, x, z, 5);
        double m = decay + (n - 0.5) * 0.7;
        double h = CityRandom.unit(s ^ 0x5717L, x, g, z);

        BlockState top;
        BlockState above = null;
        if (m > 0.78 && h < 0.75) {
            top = p.grassBlock;
            above = plant(s, x, g + 1, z, 0.55);
        } else if (m > 0.62 && h < 0.5) {
            top = p.moss;
            above = h < 0.2 ? p.mossCarpet : null;
        } else if (h < 0.015 + decay * 0.05) {
            top = plan.wet ? p.mud : (h < 0.01 + decay * 0.02 ? p.water : p.coarseDirt); // Schlagloch
        } else if (h < 0.05 + decay * 0.16) {
            top = switch ((int) (h * 1000) % 5) {
                case 0 -> p.gravel;
                case 1 -> p.cobble;
                case 2 -> p.rootedDirt;
                case 3 -> p.coarseDirt;
                default -> p.asphaltPatch;
            };
            if (top == p.rootedDirt && !plan.wet) above = h * 1000 % 2 < 1 ? p.shortGrass : null;
        } else if (inter) {
            top = lx == 3 && lz == 3 ? p.manhole : p.asphalt;
        } else if (across == 3 && along % 8 < 4 && along >= CityLayout.STREET + 3) {
            top = p.lineYellow;
        } else if (plan.edge < 0.6 && along >= CityLayout.STREET && along <= CityLayout.STREET + 2 && across % 2 == 0) {
            top = p.lineWhite; // Zebrastreifen hinter der Kreuzung
        } else {
            top = p.asphalt;
        }
        c.set(x, g, z, top);
        c.set(x, g - 1, z, top == p.water ? p.coarseDirt : p.silt);
        if (above != null && !plan.wet) {
            c.set(x, g + 1, z, above);
            if (above == p.largeFernLower) c.set(x, g + 2, z, p.largeFernUpper);
            else if (above == p.giantFernLower) c.set(x, g + 2, z, p.giantFernUpper);
        } else if (top == p.grassBlock && !plan.wet) {
            sporePod(c, s, x, g + 1, z, 0.03 + decay * 0.04);
        }
        // verstreuter Schutt
        if (!plan.wet && above == null && CityRandom.unit(s ^ 0xDE8L, x, 0, z) < 0.01 + decay * 0.03) {
            c.set(x, g + 1, z, CityRandom.unit(s, x, 1, z) < 0.5 ? p.cobbleSlab : p.stoneBrickSlab);
        }
    }

    /** Kleine Pflanze oder null. */
    private static BlockState plant(long s, int x, int y, int z, double chance) {
        CityPalette p = CityPalette.get();
        double h = CityRandom.unit(s ^ 0x91A7L, x, y, z);
        if (h >= chance) return null;
        h /= chance;
        if (h < 0.4) return p.shortGrass;
        if (h < 0.6) return p.fern;
        if (h < 0.72) return p.largeFernLower;
        if (h < 0.82) return p.giantFernLower;
        if (h < 0.9) return p.sporeBloom;
        if (h < 0.94) return p.poppy;
        if (h < 0.97) return p.dandelion;
        return p.mossCarpet;
    }

    private static void placePlant(CityCanvas c, int x, int y, int z, BlockState plant) {
        CityPalette p = CityPalette.get();
        if (plant == null || !c.isAir(x, y, z)) return;
        if (plant == p.largeFernLower || plant == p.giantFernLower) {
            if (!c.isAir(x, y + 1, z)) return;
            c.set(x, y + 1, z, plant == p.largeFernLower ? p.largeFernUpper : p.giantFernUpper);
        }
        c.set(x, y, z, plant);
    }

    /** Sporenkapsel auf Boden ({@link SporosFlora#SOIL}) mit Wahrscheinlichkeit {@code chance}, nur in freier Luft. */
    private static void sporePod(CityCanvas c, long s, int x, int y, int z, double chance) {
        CityPalette p = CityPalette.get();
        if (CityRandom.unit(s ^ 0x5B0DL, x, y, z) >= chance || !c.isAir(x, y, z)) return;
        BlockState below = c.get(x, y - 1, z);
        if (below != null && SporosFlora.SOIL.test(below)) c.set(x, y, z, p.sporePod);
    }

    /** Leuchtpilz, wenn der Block darunter ihn traegt ({@link SporosFlora#FUNGUS_SOIL}) und die Stelle frei ist. */
    private static void glowCap(CityCanvas c, int x, int y, int z) {
        CityPalette p = CityPalette.get();
        if (!c.isAir(x, y, z)) return;
        BlockState below = c.get(x, y - 1, z);
        if (below != null && SporosFlora.FUNGUS_SOIL.test(below)) c.set(x, y, z, p.glowCap);
    }

    // ── Gehweg ──────────────────────────────────────────────────────────────

    private static void sidewalk(CityCanvas c, CellPlan plan, CityPalette p, int x, int z, int lx, int lz, double e) {
        int g = plan.groundY();
        long s = plan.layout.seed;
        boolean curb = lx == CityLayout.STREET || lz == CityLayout.STREET || lx == CityLayout.CELL - 1 || lz == CityLayout.CELL - 1;
        double decay = CityRandom.clamp01(plan.decay * 0.8 + CityRandom.smoothstep(0.5, CityLayout.URBAN, e) * 0.6);
        double m = decay + (CityRandom.noise(s ^ 0x51DEL, x, z, 4) - 0.5) * 0.7;
        double h = CityRandom.unit(s ^ 0x51D3L, x, g, z);
        BlockState surface = curb ? p.curb : (plan.layout.style == 1 ? p.sidewalkTile : p.sidewalk);
        BlockState slab = curb ? p.curbSlab : p.sidewalkSlab;

        if (m > 0.75 && h < 0.7) {
            c.set(x, g, z, p.grassBlock);
            if (!plan.wet) placePlant(c, x, g + 1, z, plant(s, x, g + 1, z, 0.6));
            return;
        }
        if (m > 0.6 && h < 0.45) {
            c.set(x, g, z, p.moss);
            if (!plan.wet && h < 0.25) c.set(x, g + 1, z, p.mossCarpet);
            return;
        }
        c.set(x, g, z, h < decay * 0.15 ? p.concreteCracked : surface);
        if (plan.wet) return;
        if (h < 0.04 + decay * 0.25) return; // Platte fehlt
        c.set(x, g + 1, z, slab);
    }

    // ── Grundstuecksboden ───────────────────────────────────────────────────

    private static void lotGround(CityCanvas c, CellPlan plan, CityPalette p, int x, int z, int lx, int lz) {
        int g = plan.groundY();
        long s = plan.layout.seed;
        double h = CityRandom.unit(s ^ 0x6A0DL, x, g, z);
        double n = CityRandom.noise(s ^ 0x6A0EL, x, z, 4);
        boolean wet = plan.wet;
        switch (plan.type) {
            case PARK, TRIAGE -> {
                int mid = (CityLayout.LOT_MIN + CityLayout.LOT_MAX) / 2;
                boolean path = Math.abs(lx - mid) <= 1 || Math.abs(lz - mid) <= 1;
                if (plan.plaza) {
                    BlockState pave = (lx + lz) % 2 == 0 ? p.stoneBricks : p.polishedAndesite;
                    if (h < plan.decay * 0.25) pave = h < 0.05 ? p.grassBlock : p.stoneBricksMossy;
                    c.set(x, g, z, pave);
                    if (pave == p.grassBlock && !wet) placePlant(c, x, g + 1, z, plant(s, x, g + 1, z, 0.7));
                } else if (path && n < 0.75) {
                    c.set(x, g, z, h < 0.3 ? p.coarseDirt : p.gravel);
                } else {
                    c.set(x, g, z, p.grassBlock);
                    if (!wet) {
                        placePlant(c, x, g + 1, z, plant(s, x, g + 1, z, 0.35 + plan.veg * 0.4));
                        sporePod(c, s, x, g + 1, z, 0.015 + plan.veg * 0.03);
                    }
                }
            }
            case NEST -> {
                // Pilzboden ums Nest: Moos, Moospolster, Leuchtpilze, Sporenkapseln
                BlockState top = n < 0.35 ? p.moss : n < 0.5 ? p.mossCushion : h < 0.08 ? p.rootedDirt : p.grassBlock;
                c.set(x, g, z, top);
                if (!wet) {
                    if (top == p.moss && h > 0.85) c.set(x, g + 1, z, p.glowCap);
                    else if (top == p.grassBlock || top == p.mossCushion) placePlant(c, x, g + 1, z, plant(s, x, g + 1, z, 0.3 + plan.veg * 0.3));
                    sporePod(c, s, x, g + 1, z, 0.06 + plan.veg * 0.06);
                }
            }
            case VACANT, WILD, CAMP -> {
                BlockState top = n < 0.3 ? p.coarseDirt : n < 0.4 ? p.gravel : p.grassBlock;
                if (h < 0.05) top = p.cobble;
                if (n > 0.85 && h > 0.6) top = p.mossCushion;
                c.set(x, g, z, top);
                if ((top == p.grassBlock || top == p.mossCushion) && !wet) {
                    placePlant(c, x, g + 1, z, plant(s, x, g + 1, z, 0.4 + plan.veg * 0.4));
                    sporePod(c, s, x, g + 1, z, 0.03 + plan.veg * 0.05);
                }
            }
            default -> {
                // Hof um das Gebaeude: Beton/Kies, zugewachsen
                BlockState top = n > 0.7 - plan.veg * 0.3 ? p.grassBlock : h < 0.3 ? p.gravel : p.concrete;
                c.set(x, g, z, top);
                if (top == p.grassBlock && !wet) placePlant(c, x, g + 1, z, plant(s, x, g + 1, z, 0.5));
            }
        }
        c.set(x, g - 1, z, p.silt);
    }

    // ── Gebaeude ────────────────────────────────────────────────────────────

    private static void paintBuilding(CityCanvas c, CellPlan plan, Building b, CityPalette p) {
        int bx0 = Math.max(c.minX, b.minXPaint()), bx1 = Math.min(c.maxX(), b.maxXPaint());
        int bz0 = Math.max(c.minZ, b.minZPaint()), bz1 = Math.min(c.maxZ(), b.maxZPaint());
        if (bx0 > bx1 || bz0 > bz1) return;
        int yMin = b.bottomY(), yMax = b.maxY();
        for (int x = bx0; x <= bx1; x++) {
            for (int z = bz0; z <= bz1; z++) {
                for (int y = yMin; y <= yMax; y++) {
                    BlockState s = b.blockAt(x, y, z);
                    if (s != null) c.set(x, y, z, s);
                }
            }
        }

        // Ladenmarkisen ueber dem Gehweg, Balkone am Wohnblock
        if (b.kind == Kind.SHOP) awning(c, b, p);
        if (b.kind == Kind.APARTMENT) balconies(c, b, p);

        // Einrichtung und Bewuchs je Etage
        long hs = b.hashSeed;
        for (int x = Math.max(bx0, b.x0 + 1); x <= Math.min(bx1, b.x1 - 1); x++) {
            for (int z = Math.max(bz0, b.z0 + 1); z <= Math.min(bz1, b.z1 - 1); z++) {
                if (b.nearShaft(x, z)) continue;
                int floors = b.kind == Kind.WAREHOUSE ? 1 : b.floors;
                for (int f = 0; f < floors; f++) {
                    int y = b.floorY(f) + 1;
                    if (!c.isAir(x, y, z)) continue;
                    BlockState below = c.get(x, y - 1, z);
                    if (below == null || below.isAir() || !below.getFluidState().isEmpty()) continue;
                    furnish(c, b, plan, x, y, z, f, hs, p);
                }
                if (b.basement && b.floodTop >= b.basementFloor() + 1 && b.floodTop < b.base - 1) {
                    int y = b.floodTop + 1;
                    if (c.isAir(x, y, z) && CityRandom.unit(hs ^ 0x1111L, x, y, z) < 0.12) c.set(x, y, z, p.lilyPad);
                }
            }
        }

        // Dach-/Kantenbewuchs: oberste Flaeche je Spalte
        for (int x = Math.max(bx0, b.x0); x <= Math.min(bx1, b.x1); x++) {
            for (int z = Math.max(bz0, b.z0); z <= Math.min(bz1, b.z1); z++) {
                int top = b.maxY() + 1;
                int y = top;
                while (y > b.base && c.isAir(x, y, z)) y--;
                if (y <= b.base + 1 || y >= top) continue;
                BlockState s = c.get(x, y, z);
                if (s == null || s.isAir() || !s.getFluidState().isEmpty() || s.getBlock() == Blocks.GLASS_PANE
                        || s.getBlock() instanceof net.minecraft.world.level.block.StairBlock) continue;
                roofGreen(c, plan, b, x, y, z, p);
            }
        }

        vines(c, plan, b);
    }

    private static void furnish(CityCanvas c, Building b, CellPlan plan, int x, int y, int z, int f, long hs, CityPalette p) {
        double h = CityRandom.unit(hs ^ 0xF0F0L, x, y, z);
        boolean wallX = x == b.x0 + 1 || x == b.x1 - 1;
        boolean wallZ = z == b.z0 + 1 || z == b.z1 - 1;
        boolean atWall = wallX || wallZ;
        double decay = plan.decay;
        switch (b.kind) {
            case TOWER, APARTMENT -> {
                if (atWall && h < 0.06) {
                    BlockState piece = b.kind == Kind.TOWER
                            ? (h < 0.025 ? p.bookshelf : h < 0.04 ? p.spruceSlabTop : p.barrel)
                            : (h < 0.02 ? p.craftingTable : h < 0.04 ? p.bookshelf : h < 0.05 ? p.flowerPot : p.barrel);
                    c.set(x, y, z, piece);
                    if (piece == p.bookshelf && h < 0.012) c.setIfAir(x, y + 1, z, p.bookshelf);
                    return;
                }
                if (!atWall && b.kind == Kind.TOWER && h < 0.025 && (x - b.x0) % 4 == 2 && (z - b.z0) % 3 == 1) {
                    c.set(x, y, z, p.spruceSlabTop); // Schreibtisch
                    return;
                }
                if (b.kind == Kind.APARTMENT && h > 0.97) {
                    c.set(x, y, z, h > 0.985 ? p.carpetRed : p.carpetBrown);
                    return;
                }
            }
            case SHOP -> {
                int row = (z - b.z0) % 4;
                if (f == 0 && row == 2 && x > b.x0 + 2 && x < b.x1 - 2 && h > 0.15) {
                    c.set(x, y, z, p.spruceSlabTop);
                    c.setIfAir(x, y + 1, z, h > 0.8 ? p.flowerPot : p.spruceSlabTop);
                    return;
                }
                if (atWall && h < 0.05) {
                    c.set(x, y, z, p.barrel);
                    return;
                }
            }
            case WAREHOUSE -> {
                int row = (x - b.x0) % 5;
                if ((row == 2 || row == 3) && z > b.z0 + 2 && z < b.z1 - 2 && (z - b.z0) % 6 != 0) {
                    // Hochregal: Pfosten + Boeden, teils mit Faessern
                    boolean post = (z - b.z0) % 3 == 0;
                    for (int dy = 0; dy < 5; dy++) {
                        if (!c.isAir(x, y + dy, z)) break;
                        if (dy == 2 || dy == 4) c.set(x, y + dy, z, p.spruceSlabTop);
                        else if (post) c.set(x, y + dy, z, p.darkOakFence);
                        else if (CityRandom.unit(hs ^ 0xBA22L, x, y + dy, z) < 0.3) c.set(x, y + dy, z, p.barrel);
                    }
                    return;
                }
            }
            case PARKING -> {
                if (atWall && h < 0.01) {
                    c.set(x, y, z, p.barrel);
                    return;
                }
            }
        }
        // Verfall auf jeder Etage: Moos, Laub, Schutt, Farne
        double g = CityRandom.noise(hs ^ 0x6005L, x + f * 7, z, 4);
        if (g < plan.veg * 0.45 && h < 0.45) {
            if (h < plan.veg * 0.08) {
                c.set(x, y - 1, z, p.moss);
                if (h < plan.veg * 0.02 && c.isAir(x, y + 1, z)) {
                    c.set(x, y, z, p.giantFernLower);
                    c.set(x, y + 1, z, p.giantFernUpper);
                } else if (h < plan.veg * 0.035) {
                    c.set(x, y, z, p.glowCap); // Leuchtpilz in der dunklen Etage (auf Moos)
                } else {
                    c.set(x, y, z, p.fern);
                }
            } else {
                c.set(x, y, z, p.mossCarpet);
            }
            return;
        }
        if (h > 1 - decay * 0.05) c.set(x, y, z, h > 1 - decay * 0.015 ? p.gravel : p.cobbleSlab);
        else if (h > 1 - decay * 0.05 - plan.veg * 0.03) c.set(x, y, z, p.hypheLeaves); // Buschwerk
    }

    private static void roofGreen(CityCanvas c, CellPlan plan, Building b, int x, int y, int z, CityPalette p) {
        long hs = b.hashSeed;
        double n = CityRandom.noise(hs ^ 0x400FL, x, z, 4);
        double h = CityRandom.unit(hs ^ 0x400EL, x, y, z);
        double v = plan.veg;
        if (n < v * 0.55) {
            // Gras-/Moosteppich (Moosblock und Moospolster zaehlen als Erde -> dort keimen die Hyphe-Baeume der Biom-Features)
            c.set(x, y, z, h < 0.25 ? p.mossCushion : h < 0.6 ? p.moss : p.grassBlock);
            BlockState pl = plant(hs, x, y + 1, z, 0.35 + v * 0.3);
            if (pl != null) placePlant(c, x, y + 1, z, pl);
            else if (h < 0.06) c.setIfAir(x, y + 1, z, h < 0.03 ? p.glowCap : p.hypheLeaves);
        } else if (n < v * 0.8 + 0.1 && h < 0.6) {
            c.setIfAir(x, y + 1, z, p.mossCarpet);
        }
    }

    private static void vines(CityCanvas c, CellPlan plan, Building b) {
        CityPalette p = CityPalette.get();
        long hs = b.hashSeed;
        double v = plan.veg;
        for (Direction side : Direction.Plane.HORIZONTAL) {
            int len = side.getAxis() == Direction.Axis.X ? b.z1 - b.z0 : b.x1 - b.x0;
            for (int a = 1; a < len; a++) {
                int wx = side == Direction.WEST ? b.x0 : side == Direction.EAST ? b.x1 : b.x0 + a;
                int wz = side == Direction.NORTH ? b.z0 : side == Direction.SOUTH ? b.z1 : b.z0 + a;
                int ox = wx + side.getStepX(), oz = wz + side.getStepZ();
                if (!c.contains(ox, oz)) continue;
                double curtain = CityRandom.noise(hs ^ 0x7111L, wx * 3 + wz, 0, 3);
                if (curtain > v * 0.9 + 0.08) continue;
                int top = Math.min(b.topLimit(wx, wz), b.capped ? b.keptTop : b.topY + 1);
                int length = (int) ((0.3 + 0.7 * CityRandom.unit(hs ^ 0x7112L, wx, 0, wz)) * (top - b.base) * (0.35 + v));
                BlockState vine = p.vine(side.getOpposite());
                for (int y = top; y > Math.max(b.base, top - length); y--) {
                    if (b.shear(y) != 0) continue;
                    BlockState wall = b.blockAt(wx, y, wz);
                    if (wall == null || wall.isAir() || wall.getBlock() == Blocks.GLASS_PANE) {
                        if (CityRandom.unit(hs ^ 0x7113L, wx, y, wz) < 0.5) break;
                        continue;
                    }
                    if (c.isAir(ox, y, oz)) c.set(ox, y, oz, vine);
                }
            }
        }
    }

    private static void awning(CityCanvas c, Building b, CityPalette p) {
        BlockState color = p.awnings[(int) Math.floorMod(b.hashSeed, (long) p.awnings.length)];
        int y = b.base + 4;
        for (Direction side : new Direction[] {b.entrance, b.entrance.getClockWise()}) {
            int len = side.getAxis() == Direction.Axis.X ? b.z1 - b.z0 : b.x1 - b.x0;
            for (int a = 1; a < len; a++) {
                int wx = side == Direction.WEST ? b.x0 : side == Direction.EAST ? b.x1 : b.x0 + a;
                int wz = side == Direction.NORTH ? b.z0 : side == Direction.SOUTH ? b.z1 : b.z0 + a;
                int ox = wx + side.getStepX(), oz = wz + side.getStepZ();
                if (b.removed(wx, y, wz) || CityRandom.unit(b.hashSeed ^ 0xA77L, ox, y, oz) < b.decay * 0.6) continue;
                c.setIfAir(ox, y, oz, color);
            }
        }
    }

    /** Balkone (Platte + Holzgelaender) auf der Rueckseite des Wohnblocks, je Stockwerk ab dem ersten. */
    private static void balconies(CityCanvas c, Building b, CityPalette p) {
        Direction side = b.entrance.getOpposite();
        BlockState floor = CityPalette.slab(Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), net.minecraft.world.level.block.state.properties.SlabType.TOP);
        int len = side.getAxis() == Direction.Axis.X ? b.z1 - b.z0 : b.x1 - b.x0;
        int top = b.capped ? b.keptTop : b.topY;
        for (int f = 1; f < b.floors; f++) {
            int y = b.floorY(f);
            if (y >= top) break;
            for (int a = 2; a < len - 1; a++) {
                if (a % 6 != 2 && a % 6 != 3) continue;
                int wx = side == Direction.WEST ? b.x0 : side == Direction.EAST ? b.x1 : b.x0 + a;
                int wz = side == Direction.NORTH ? b.z0 : side == Direction.SOUTH ? b.z1 : b.z0 + a;
                int ox = wx + side.getStepX(), oz = wz + side.getStepZ();
                if (b.removed(wx, y, wz) || CityRandom.unit(b.hashSeed ^ 0xBA1CL, a / 6, f, 0) < b.decay * 0.5) continue;
                c.setIfAir(ox, y, oz, floor);
                if (CityRandom.unit(b.hashSeed ^ 0xBA1DL, ox, y, oz) > b.decay * 0.4) c.setIfAir(ox, y + 1, oz, p.oakFence);
            }
        }
    }

    // ── Objekte ─────────────────────────────────────────────────────────────

    private static void lootContainer(CityCanvas c, LootSpot s, CityPalette p) {
        if (!c.contains(s.x(), s.z())) return;
        BlockState below = c.get(s.x(), s.y() - 1, s.z());
        if (below == null || below.isAir() || !below.getFluidState().isEmpty()) return;
        if (!c.isOpen(s.x(), s.y(), s.z())) {
            BlockState here = c.get(s.x(), s.y(), s.z());
            // Einrichtung darf weichen, Waende nicht
            if (here == null || !(here == p.barrel || here == p.bookshelf || here == p.mossCarpet || here == p.cobbleSlab || here == p.gravel
                    || here == p.hypheLeaves || here == p.glowCap || here == p.fern || here == p.spruceSlabTop || here == p.craftingTable
                    || here == p.flowerPot || here == p.spruceSlab || here == p.carpetWhite || here == p.sandbag)) return;
        }
        BlockState container = switch (s.container()) {
            case BARREL -> CityPalette.facingAll(p.barrel, Direction.UP);
            case SUPPLY_CRATE -> p.supplyCrate;
            case MILITARY_CRATE -> p.militaryCrate;
            default -> CityPalette.facing(p.chest, s.facing());
        };
        c.set(s.x(), s.y(), s.z(), container);
    }

    private static void ruin(CityCanvas c, CellPlan plan, Ruin r, CityPalette p) {
        int g = plan.groundY();
        long s = plan.layout.seed ^ 0x2B1AL;
        for (int x = Math.max(c.minX, r.x0()); x <= Math.min(c.maxX(), r.x1()); x++) {
            for (int z = Math.max(c.minZ, r.z0()); z <= Math.min(c.maxZ(), r.z1()); z++) {
                boolean edge = x == r.x0() || x == r.x1() || z == r.z0() || z == r.z1();
                if (!edge) {
                    if (CityRandom.unit(s, x, 0, z) < 0.5) c.set(x, g, z, p.concreteCracked);
                    continue;
                }
                int hgt = (int) (CityRandom.noise(s, x, z, 3) * (r.height() + 1.5));
                for (int dy = 1; dy <= hgt; dy++) {
                    double h = CityRandom.unit(s, x, g + dy, z);
                    c.set(x, g + dy, z, h < 0.3 ? p.brickConcreteBroken : h < 0.55 ? p.cobbleMossy : h < 0.8 ? p.bricks : p.brickConcreteMossy);
                }
            }
        }
    }

    private static void pond(CityCanvas c, CellPlan plan, Pond pd, CityPalette p) {
        if (plan.wet) return;
        int g = plan.groundY();
        long s = plan.layout.seed ^ 0x90D0L;
        for (int x = Math.max(c.minX, pd.x() - pd.rx() - 1); x <= Math.min(c.maxX(), pd.x() + pd.rx() + 1); x++) {
            for (int z = Math.max(c.minZ, pd.z() - pd.rz() - 1); z <= Math.min(c.maxZ(), pd.z() + pd.rz() + 1); z++) {
                double dx = (x - pd.x()) / (pd.rx() + 0.5), dz = (z - pd.z()) / (pd.rz() + 0.5);
                double d = dx * dx + dz * dz + (CityRandom.unit(s, x, 0, z) - 0.5) * 0.3;
                if (d > 1) continue;
                int depth = d < 0.4 ? 2 : 1;
                for (int dy = 0; dy < depth; dy++) c.set(x, g - dy, z, p.water);
                c.set(x, g - depth, z, p.mud);
                c.set(x, g + 1, z, CityRandom.unit(s, x, 1, z) < 0.18 ? p.lilyPad : Blocks.AIR.defaultBlockState());
                c.set(x, g + 2, z, Blocks.AIR.defaultBlockState());
            }
        }
    }

    private static void parkFurniture(CityCanvas c, CellPlan plan, CityPalette p) {
        int g = plan.groundY();
        int mid = (CityLayout.LOT_MIN + CityLayout.LOT_MAX) / 2;
        int cx = plan.minX + mid, cz = plan.minZ + mid;
        if (plan.plaza) {
            // Brunnen: Steinziegelring, Wasser, Moos
            for (int x = cx - 3; x <= cx + 3; x++) {
                for (int z = cz - 3; z <= cz + 3; z++) {
                    int d2 = (x - cx) * (x - cx) + (z - cz) * (z - cz);
                    if (d2 > 10) continue;
                    if (d2 >= 6) {
                        c.set(x, g + 1, z, CityRandom.unit(plan.layout.seed, x, g, z) < plan.decay * 0.5 ? p.stoneBricksMossy : p.stoneBricks);
                    } else {
                        c.set(x, g, z, p.water);
                        c.set(x, g - 1, z, p.stoneBricks);
                        c.set(x, g + 1, z, CityRandom.unit(plan.layout.seed, x, g + 1, z) < 0.2 ? p.lilyPad : Blocks.AIR.defaultBlockState());
                    }
                }
            }
            c.set(cx, g, cz, p.stoneBricks);
            c.set(cx, g + 1, cz, p.postStone);
            c.set(cx, g + 2, cz, p.stoneBricksMossy);
        }
        // Baenke an den Wegen
        for (int i = -8; i <= 8; i += 4) {
            if (i == 0) continue;
            if (CityRandom.unit(plan.layout.seed ^ 0xBE4CL, plan.gx, i, plan.gz) < 0.6) {
                c.setIfAir(cx + i, g + 1, cz - 2, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH, false));
                c.setIfAir(cx - 2, g + 1, cz + i, CityPalette.stairs(Blocks.SPRUCE_STAIRS, Direction.WEST, false));
            }
        }
    }

    private static void rubble(CityCanvas c, CellPlan plan, Rubble r, CityPalette p) {
        long s = plan.layout.seed ^ 0x40BBL ^ ((long) r.x() << 20) ^ r.z();
        for (int x = Math.max(c.minX, r.x() - r.radius()); x <= Math.min(c.maxX(), r.x() + r.radius()); x++) {
            for (int z = Math.max(c.minZ, r.z() - r.radius()); z <= Math.min(c.maxZ(), r.z() + r.radius()); z++) {
                double d = Math.sqrt((x - r.x()) * (x - r.x()) + (z - r.z()) * (z - r.z()));
                int h = (int) Math.round(r.height() * (1 - d / (r.radius() + 0.5)) + CityRandom.unit(s, x, 0, z) * 0.9 - 0.2);
                for (int dy = 1; dy <= h; dy++) {
                    int y = r.y() + dy;
                    if (!c.isOpen(x, y, z)) continue;
                    double u = CityRandom.unit(s, x, y, z);
                    BlockState b = u < 0.22 ? p.cobble : u < 0.38 ? p.gravel : u < 0.52 ? p.concreteCracked : u < 0.66 ? p.brickConcreteBroken
                            : u < 0.78 ? p.andesite : u < 0.88 ? p.cobbleMossy : p.tuff;
                    c.set(x, y, z, dy == h && u > 0.7 ? p.cobbleSlab : b);
                }
                if (h >= 0 && c.isAir(x, r.y() + h + 1, z) && CityRandom.unit(s, x, 9, z) < plan.veg * 0.4) {
                    double v = CityRandom.unit(s, x, 10, z);
                    BlockState top = c.get(x, r.y() + h, z);
                    if (v < 0.2 && top != null && SporosFlora.FUNGUS_SOIL.test(top)) c.set(x, r.y() + h + 1, z, p.glowCap);
                    else if (v < 0.6 || top == null || !isSoil(top)) c.set(x, r.y() + h + 1, z, p.mossCarpet);
                    else c.set(x, r.y() + h + 1, z, p.fern);
                }
            }
        }
    }

    private static boolean isSoil(BlockState s) {
        CityPalette p = CityPalette.get();
        return s == p.grassBlock || s == p.moss || s == p.dirt || s == p.rootedDirt || s == p.coarseDirt || s == p.mud;
    }

    private static void car(CityCanvas c, CellPlan plan, Car car, CityPalette p) {
        BlockState body = car.burnt() ? p.vConcreteGray : p.carColors[car.color()];
        BlockState dark = car.burnt() ? p.vConcreteBlack : p.terracottaBrown;
        BlockState glass = car.burnt() ? Blocks.AIR.defaultBlockState() : p.glassBlack;
        int y = car.y();
        boolean van = car.len() == 5;
        long s = plan.layout.seed ^ 0xCA2L;
        for (int i = 0; i < car.len(); i++) {
            for (int j = 0; j < 2; j++) {
                int x = car.alongX() ? car.x() + i : car.x() + j;
                int z = car.alongX() ? car.z() + j : car.z() + i;
                if (!c.contains(x, z)) continue;
                boolean end = i == 0 || i == car.len() - 1;
                if (c.isOpen(x, y, z)) c.set(x, y, z, end && (CityRandom.unit(s, x, y, z) < 0.5) ? dark : body);
                boolean cabin = van ? i >= 1 : i >= 1 && i <= 2;
                if (cabin) {
                    if (c.isOpen(x, y + 1, z)) c.set(x, y + 1, z, van && i >= 2 ? body : glass);
                    if (van && c.isOpen(x, y + 2, z)) c.set(x, y + 2, z, body);
                    BlockState roof = CityRandom.unit(s, x, y + 3, z) < plan.veg * 0.7 ? p.mossCarpet : null;
                    int ry = van ? y + 3 : y + 2;
                    if (!car.burnt() && !van && c.isOpen(x, ry, z)) c.set(x, ry, z, CityPalette.slab(Blocks.SMOOTH_STONE_SLAB.defaultBlockState(),
                            net.minecraft.world.level.block.state.properties.SlabType.BOTTOM));
                    else if (roof != null && c.isAir(x, ry, z)) c.set(x, ry, z, roof);
                } else if (CityRandom.unit(s, x, y + 1, z) < plan.veg * 0.5 && c.isAir(x, y + 1, z)) {
                    c.set(x, y + 1, z, p.mossCarpet);
                }
            }
        }
    }

    private static void lamp(CityCanvas c, CellPlan plan, Lamp l, CityPalette p) {
        int g = plan.groundY();
        int dx = l.toStreet().getStepX(), dz = l.toStreet().getStepZ();
        if (l.state() == 2) {
            // umgestuerzt: liegt quer auf der Strasse
            for (int i = 0; i < 5; i++) {
                if (i == 0) c.set(l.x(), g + 1, l.z(), p.postBlack); // Stumpf
                else c.setIfOpen(l.x() + dx * i, g + 1, l.z() + dz * i, p.armSlab);
            }
            return;
        }
        for (int y = g + 1; y <= g + 5; y++) c.set(l.x(), y, l.z(), p.postBlack);
        c.set(l.x(), g + 6, l.z(), p.armSlabTop);
        c.set(l.x() + dx, g + 6, l.z() + dz, p.armSlabTop);
        c.set(l.x() + 2 * dx, g + 6, l.z() + 2 * dz, p.armSlabTop);
        if (l.state() == 0) c.set(l.x() + 2 * dx, g + 5, l.z() + 2 * dz, p.glass);
        if (CityRandom.unit(plan.layout.seed ^ 0x1A3L, l.x(), 0, l.z()) < plan.veg * 0.7) {
            Direction face = l.toStreet().getClockWise();
            for (int y = g + 5; y > g + 1 + (int) (CityRandom.unit(plan.layout.seed, l.x(), 1, l.z()) * 3); y--) {
                c.setIfAir(l.x() + face.getStepX(), y, l.z() + face.getStepZ(), p.vine(face.getOpposite()));
            }
        }
    }

    private static void trafficLight(CityCanvas c, CellPlan plan, Lamp l, CityPalette p) {
        int g = plan.groundY();
        int dx = l.toStreet().getStepX(), dz = l.toStreet().getStepZ();
        for (int y = g + 1; y <= g + 5; y++) c.set(l.x(), y, l.z(), p.postBlack);
        for (int i = 0; i <= 3; i++) c.set(l.x() + dx * i, g + 6, l.z() + dz * i, p.armSlabTop);
        int hx = l.x() + dx * 3, hz = l.z() + dz * 3;
        if (l.state() == 0) {
            c.set(hx, g + 5, hz, p.glassRed);
            c.set(hx, g + 4, hz, p.glassYellow);
            c.set(hx, g + 3, hz, p.glassLime);
        } else {
            c.set(hx, g + 5, hz, p.vConcreteBlack);
        }
    }

    /**
     * Baum der Sporos-Flora: 0 Hyphe-Schirmbaum, 1 Hyphe-Totholz (kahler Stamm mit Pilzregalen und Leuchtpilz),
     * 2 Hyphe-Baum mit Rankenvorhang (Geflecht-Ranken bis zum Boden), 3 Riesenpilz-Gruppe (Stiel, flacher Hut, Leuchtpilze
     * am Fuss). Alles bleibt im Umkreis {@code radius + 1} der Pflanzstelle (Zellgrenze).
     */
    private static void tree(CityCanvas c, CellPlan plan, Tree t, CityPalette p) {
        long s = plan.layout.seed ^ 0x7EEL ^ ((long) t.x() << 24) ^ t.z();
        int y0 = t.y();
        int r = t.radius();
        if (t.kind() == 3) {
            giantFungus(c, s, t, p);
            return;
        }
        boolean dead = t.kind() == 1;
        boolean curtain = t.kind() == 2;
        BlockState log = dead ? p.deadHypheLog : p.hypheLog;
        int height = dead ? Math.max(4, t.height() - 2 - (int) (CityRandom.unit(s, 0, 1, 0) * 3)) : t.height();
        int top = y0 + height;
        c.set(t.x(), y0 - 1, t.z(), p.rootedDirt);
        for (int y = y0; y < top; y++) c.set(t.x(), y, t.z(), log);
        // Brettwurzeln, die den Boden aufbrechen
        for (Direction d : Direction.Plane.HORIZONTAL) {
            if (CityRandom.unit(s, d.get2DDataValue(), 0, 0) < 0.5) {
                c.set(t.x() + d.getStepX(), y0 - 1, t.z() + d.getStepZ(), p.rootedDirt);
                c.setIfOpen(t.x() + d.getStepX(), y0, t.z() + d.getStepZ(), CityPalette.log(log, d.getAxis()));
            }
        }
        // Pilzregale am Stamm (Sporos: Konsolenpilze an jedem aelteren Stamm)
        for (Direction d : Direction.Plane.HORIZONTAL) {
            for (int y = y0 + 1; y < top - 1; y++) {
                if (CityRandom.unit(s ^ 0x5E1FL, d.get2DDataValue(), y, 0) < (dead ? 0.18 : 0.06)) {
                    c.setIfAir(t.x() + d.getStepX(), y, t.z() + d.getStepZ(), p.shelfFungus(d.getOpposite()));
                }
            }
        }
        if (dead) {
            // abgebrochene Aeste, Leuchtpilz auf dem Stumpf, Ranken
            for (Direction d : Direction.Plane.HORIZONTAL) {
                if (CityRandom.unit(s ^ 0xB2A7L, d.get2DDataValue(), 0, 0) < 0.4) {
                    int by = top - 2 - (int) (CityRandom.unit(s ^ 0xB2A8L, d.get2DDataValue(), 0, 0) * 2);
                    c.setIfOpen(t.x() + d.getStepX(), by, t.z() + d.getStepZ(), CityPalette.log(log, d.getAxis()));
                    if (r >= 2) c.setIfOpen(t.x() + 2 * d.getStepX(), by, t.z() + 2 * d.getStepZ(), CityPalette.log(log, d.getAxis()));
                }
            }
            glowCap(c, t.x(), top, t.z());
            return;
        }
        // Krone aus Hyphe-Laub
        int cy = top - 1;
        for (int x = t.x() - r - 1; x <= t.x() + r + 1; x++) {
            for (int z = t.z() - r - 1; z <= t.z() + r + 1; z++) {
                for (int y = cy - 2; y <= cy + 2; y++) {
                    double dx = x - t.x(), dz = z - t.z(), dy = (y - cy) * 1.4;
                    double d = dx * dx + dz * dz + dy * dy;
                    if (d > (r + 0.6) * (r + 0.6) + CityRandom.unit(s, x, y, z) * 1.5) continue;
                    c.setIfOpen(x, y, z, p.hypheLeaves);
                }
                // Geflecht-Ranken vom Kronenrand (Rankenvorhang: lang und dicht)
                double chance = curtain ? 0.55 : 0.2;
                if (CityRandom.unit(s, x, 1, z) < chance) {
                    int vy = cy - 3;
                    if (c.get(x, vy + 1, z) == p.hypheLeaves) {
                        int len = curtain ? 3 + (int) (CityRandom.unit(s, x, 2, z) * (vy - y0)) : 1 + (int) (CityRandom.unit(s, x, 2, z) * 4);
                        for (int i = 0; i < len && c.isAir(x, vy - i, z); i++) c.set(x, vy - i, z, p.vine(Direction.UP));
                    }
                }
            }
        }
    }

    /** Riesenpilz-Gruppe: Stiel bis {@code height}, flacher Hut (Radius r) mit Rand eine Ebene tiefer, Leuchtpilze am Fuss. */
    private static void giantFungus(CityCanvas c, long s, Tree t, CityPalette p) {
        int r = t.radius();
        int y0 = t.y();
        int h = Math.max(3, Math.min(6, t.height() - 3));
        c.set(t.x(), y0 - 1, t.z(), p.mossCushion);
        for (int y = y0; y < y0 + h; y++) c.set(t.x(), y, t.z(), p.fungalStem);
        int capY = y0 + h;
        for (int x = t.x() - r; x <= t.x() + r; x++) {
            for (int z = t.z() - r; z <= t.z() + r; z++) {
                int d2 = (x - t.x()) * (x - t.x()) + (z - t.z()) * (z - t.z());
                if (d2 > r * r + 1) continue;
                boolean rim = d2 >= (r - 1) * (r - 1) + 1;
                c.setIfOpen(x, rim ? capY - 1 : capY, z, p.fungalCap);
                if (!rim && d2 <= 1 && r >= 2) c.setIfOpen(x, capY + 1, z, p.fungalCap); // Buckel
            }
        }
        // kleine Pilze und Leuchtpilze am Fuss
        for (Direction d : Direction.Plane.HORIZONTAL) {
            int fx = t.x() + d.getStepX(), fz = t.z() + d.getStepZ();
            double u = CityRandom.unit(s ^ 0xF00DL, fx, 0, fz);
            if (u < 0.3) {
                BlockState below = c.get(fx, y0 - 1, fz);
                if (below != null && SporosFlora.SOIL.test(below)) {
                    c.setIfAir(fx, y0, fz, p.fungalStem);
                    c.setIfAir(fx, y0 + 1, fz, p.fungalCap);
                }
            } else if (u < 0.7) {
                glowCap(c, fx, y0, fz);
            }
        }
    }

    /** Umgestuerzter Hyphe-Stamm (Totholz) quer auf der Strasse: liegender Stamm, Pilzregale, Moos und ein Leuchtpilz. */
    private static void fallenLog(CityCanvas c, CellPlan plan, Log log, CityPalette p) {
        long s = plan.layout.seed ^ 0xFA11L ^ ((long) log.x() << 20) ^ log.z();
        Direction.Axis axis = log.alongX() ? Direction.Axis.X : Direction.Axis.Z;
        BlockState trunk = CityPalette.log(p.deadHypheLog, axis);
        for (int i = 0; i < log.len(); i++) {
            int x = log.alongX() ? log.x() + i : log.x();
            int z = log.alongX() ? log.z() : log.z() + i;
            if (!c.contains(x, z)) continue;
            if (!c.isOpen(x, log.y(), z)) continue;
            c.set(x, log.y(), z, trunk);
            double u = CityRandom.unit(s, x, 0, z);
            if (u < 0.25) c.setIfAir(x, log.y() + 1, z, p.mossCarpet);
            else if (u < 0.33) glowCap(c, x, log.y() + 1, z);
            // Pilzregale an den Flanken
            Direction side = log.alongX() ? Direction.NORTH : Direction.WEST;
            for (Direction d : new Direction[] {side, side.getOpposite()}) {
                if (CityRandom.unit(s ^ 0x5E1FL, x, d.get2DDataValue(), z) < 0.2) {
                    c.setIfAir(x + d.getStepX(), log.y(), z + d.getStepZ(), p.shelfFungus(d.getOpposite()));
                }
            }
        }
    }
}
