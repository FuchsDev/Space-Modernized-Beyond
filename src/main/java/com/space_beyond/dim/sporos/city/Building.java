package com.space_beyond.dim.sporos.city;

import java.util.Random;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Eine Gebaeude-Ruine auf einem Grundstueck. {@link #blockAt} ist eine reine Funktion der Weltkoordinaten (Plan +
 * Hash), damit jeder Chunk seinen Ausschnitt unabhaengig von den Nachbarn setzt.
 *
 * <p>Aufbau: Stockwerke zu {@link #FLOOR_H} Bloecken (Boden + 3 Luft), Treppenhaus als 3x3-Wendeltreppe an der
 * Nordwest-Ecke (8 Stufen pro Umlauf, Ausgang auf jedem Stockwerk abwechselnd nach Osten/Sueden), Fassade je Typ,
 * Zwischenwaende mit Tueren, Keller (teils geflutet). Verfall: gekappte Obergeschosse mit zackiger Abbruchkante,
 * eingestuerzte Ecke (nach oben breiter werdender Keil), Loecher in Decken, fehlende Fensterscheiben und Wandsteine,
 * schiefe Obergeschosse (um 1-2 Bloecke versetzt).
 */
public final class Building {

    public enum Kind { TOWER, APARTMENT, SHOP, PARKING, WAREHOUSE }

    public static final int FLOOR_H = 4;

    /** Ring der Wendeltreppe (lokal im 3x3-Schacht), im Uhrzeigersinn ab Nordwest. */
    private static final int[][] RING = {{0, 0}, {1, 0}, {2, 0}, {2, 1}, {2, 2}, {1, 2}, {0, 2}, {0, 1}};

    public final Kind kind;
    public final int x0, z0, x1, z1;
    public final int base;
    public final int floors, floorH;
    /** y der Dachplatte. */
    public final int topY;
    public final long hashSeed;
    public final double decay;

    // Material
    final BlockState wall, wallAlt, trim, floor, floorAlt, partition, roof, roofEdge, foundation;

    // Treppenhaus
    public final boolean hasShaft;
    public final int sx, sz;

    // Keller
    public final boolean basement;
    /** Hoechste Wasserebene im Keller (unter {@link #basementFloor()} = trocken). */
    public final int floodTop;

    // Verfall
    final boolean capped;
    final int keptTop, jagMax;
    final boolean collapse;
    final int ccx, ccz, cy0;
    final double cr;
    final int[][] holes; // {stockwerk, x, z, r}
    final int shearY, sdx, sdz;

    // Eingang
    public final Direction entrance;
    final int entranceAlong;
    final int midX, midZ;

    Building(Kind kind, CityLayout layout, int x0, int z0, int x1, int z1, int floors, double decay, double edge, Random rand) {
        CityPalette p = CityPalette.get();
        this.kind = kind;
        this.x0 = x0;
        this.z0 = z0;
        this.x1 = x1;
        this.z1 = z1;
        this.base = layout.baseY;
        this.floors = floors;
        this.floorH = kind == Kind.WAREHOUSE ? 8 : FLOOR_H;
        this.topY = base + floors * floorH;
        this.hashSeed = rand.nextLong();
        this.decay = decay;
        this.midX = (x0 + x1) / 2;
        this.midZ = (z0 + z1) / 2;

        int style = layout.style;
        switch (kind) {
            case TOWER -> {
                BlockState[] walls = style == 1
                        ? new BlockState[] {p.brickConcrete, p.brickLight, p.concreteWhite, p.terracottaWhite}
                        : new BlockState[] {p.concrete, p.concreteSmooth, p.concreteWhite, p.concreteGray, p.brickConcrete, p.vConcreteLightGray};
                wall = pick(rand, walls);
                wallAlt = wall == p.brickConcrete ? p.brickConcreteCracked : pick(rand, p.concreteCracked, p.concreteMossy, p.brickConcreteCracked);
                trim = pick(rand, p.concreteSmooth, p.concreteGray, p.polishedAndesite, p.concreteSilver);
                floor = pick(rand, p.tileLab, p.vinylTile, p.concreteTile, p.smoothStone);
                floorAlt = floor == p.tileLab ? p.tileLabCracked : p.concreteCracked;
                partition = pick(rand, p.concreteWhite, p.vConcreteWhite, p.smoothStone);
                roof = p.concreteGray;
                roofEdge = trim;
            }
            case APARTMENT -> {
                wall = pick(rand, p.bricks, p.brickLight, p.terracottaWhite, p.terracottaLightGray, p.mudBricks, p.terracottaOrange);
                wallAlt = pick(rand, p.stoneBricksCracked, p.stoneBricksMossy, p.brickConcreteBroken);
                trim = pick(rand, p.stoneBricks, p.smoothStone, p.polishedAndesite);
                floor = pick(rand, p.oakPlanks, p.sprucePlanks, p.darkOakPlanks, p.vinylTile);
                floorAlt = p.coarseDirt;
                partition = pick(rand, p.terracottaWhite, p.vConcreteWhite, p.sprucePlanks);
                roof = pick(rand, p.smoothStone, p.concreteGray);
                roofEdge = trim;
            }
            case SHOP -> {
                wall = pick(rand, p.bricks, p.concreteWhite, p.terracottaRed, p.terracottaCyan, p.terracottaWhite, p.brickLight);
                wallAlt = pick(rand, p.stoneBricksCracked, p.concreteCracked);
                trim = pick(rand, p.smoothStone, p.polishedAndesite, p.concreteSmooth);
                floor = pick(rand, p.tileLab, p.vinylTile, p.concreteTile);
                floorAlt = p.tileLabBroken;
                partition = p.terracottaWhite;
                roof = p.concreteGray;
                roofEdge = trim;
            }
            case PARKING -> {
                wall = pick(rand, p.concrete, p.concreteGray, p.concreteSmooth);
                wallAlt = p.concreteCracked;
                trim = pick(rand, p.concreteSmooth, p.concreteGray);
                floor = p.asphalt;
                floorAlt = p.concreteCracked;
                partition = wall;
                roof = p.asphalt;
                roofEdge = wall;
            }
            default -> { // WAREHOUSE
                wall = pick(rand, p.terracottaLightGray, p.terracottaCyan, p.brickConcrete, p.concreteGray, p.bricks, p.terracottaBrown);
                wallAlt = pick(rand, p.concreteCracked, p.brickConcreteBroken);
                trim = pick(rand, p.concreteSmooth, p.stoneBricks);
                floor = pick(rand, p.concrete, p.smoothStone, p.concreteTile);
                floorAlt = p.concreteCracked;
                partition = wall;
                roof = pick(rand, p.sprucePlanks, p.darkOakPlanks, p.concreteGray);
                roofEdge = trim;
            }
        }
        foundation = p.concrete;

        // Treppenhaus (nur mehrstoeckig): 3x3 an der Nordwest-Ecke innen
        this.hasShaft = floors > 1 && floorH == FLOOR_H && x1 - x0 >= 9 && z1 - z0 >= 9;
        this.sx = x0 + 1;
        this.sz = z0 + 1;

        // Keller
        this.basement = kind != Kind.PARKING && rand.nextFloat() < (kind == Kind.WAREHOUSE ? 0.3F : 0.55F);
        int flood = rand.nextFloat() < 0.35F + decay * 0.4F ? 1 + rand.nextInt(3) : 0;
        this.floodTop = basementFloor() + flood;

        // Verfall: gekappte Obergeschosse
        boolean cap = floors >= 3 && rand.nextFloat() < 0.15F + decay * 0.7F;
        int kept = floors;
        if (cap) kept = Math.max(1, floors - 1 - rand.nextInt(Math.max(1, (int) Math.ceil(floors * (0.15 + 0.45 * decay)))));
        this.capped = cap;
        this.keptTop = base + kept * floorH + 1 + rand.nextInt(2);
        this.jagMax = cap ? 2 + rand.nextInt(Math.min(floorH * 2, 6)) : 0;

        // eingestuerzte Ecke
        this.collapse = rand.nextFloat() < 0.1F + decay * 0.55F;
        int corner = rand.nextInt(4);
        this.ccx = (corner & 1) == 0 ? x0 : x1;
        this.ccz = (corner & 2) == 0 ? z0 : z1;
        int firstFloor = Math.min(floors - 1, (int) (floors * (0.2 + rand.nextDouble() * 0.6)));
        this.cy0 = base + Math.max(kind == Kind.WAREHOUSE ? 3 : FLOOR_H, firstFloor * floorH);
        this.cr = 2 + rand.nextDouble() * Math.min(x1 - x0, z1 - z0) * (0.25 + 0.35 * decay);

        // Deckenloecher
        int nHoles = (int) (decay * floors * (kind == Kind.WAREHOUSE ? 4 : 1.2)) + (rand.nextFloat() < decay ? 1 : 0);
        this.holes = new int[nHoles][];
        for (int i = 0; i < nHoles; i++) {
            int f = kind == Kind.WAREHOUSE ? floors : 1 + rand.nextInt(Math.max(1, floors));
            holes[i] = new int[] {f, x0 + 2 + rand.nextInt(Math.max(1, x1 - x0 - 3)), z0 + 2 + rand.nextInt(Math.max(1, z1 - z0 - 3)),
                    1 + rand.nextInt(kind == Kind.WAREHOUSE ? 5 : 3)};
        }

        // schiefe Obergeschosse (nur Hochhaeuser, Versatz bleibt im Gehweg der eigenen Zelle)
        if (kind == Kind.TOWER && floors >= 8 && rand.nextFloat() < 0.12F + decay * 0.3F) {
            int top = cap ? keptTop : topY;
            this.shearY = Math.max(base + 3 * floorH, top - floorH * (2 + rand.nextInt(3)));
            Direction d = Direction.from2DDataValue(rand.nextInt(4));
            this.sdx = d.getStepX();
            this.sdz = d.getStepZ();
        } else {
            this.shearY = Integer.MAX_VALUE;
            this.sdx = 0;
            this.sdz = 0;
        }

        this.entrance = Direction.from2DDataValue(rand.nextInt(4));
        int len = entrance.getAxis() == Direction.Axis.Z ? x1 - x0 : z1 - z0;
        this.entranceAlong = len / 2 + (len > 8 ? rand.nextInt(5) - 2 : 0);
    }

    @SafeVarargs
    static <T> T pick(Random r, T... options) {
        return options[r.nextInt(options.length)];
    }

    public int basementFloor() {
        return base - 4;
    }

    /** Unterste Ebene, die das Gebaeude setzt (Keller bzw. Fundament). */
    public int bottomY() {
        return basement ? basementFloor() : base - 3;
    }

    /** Hoechste Ebene inkl. Bruestung. */
    public int maxY() {
        return (capped ? keptTop : topY) + 2;
    }

    /** Bereich (inkl. Versatz/Ranken), den der Maler absucht. */
    public int minXPaint() {
        return x0 - 2;
    }

    public int maxXPaint() {
        return x1 + 2;
    }

    public int minZPaint() {
        return z0 - 2;
    }

    public int maxZPaint() {
        return z1 + 2;
    }

    public boolean inFootprint(int x, int z) {
        return x >= x0 && x <= x1 && z >= z0 && z <= z1;
    }

    public boolean inShaft(int x, int z) {
        return hasShaft && x >= sx && x <= sx + 2 && z >= sz && z <= sz + 2;
    }

    /** Stockwerk-Fussboden y (0 = Erdgeschoss). */
    public int floorY(int f) {
        return base + f * floorH;
    }

    // ── Verfall ─────────────────────────────────────────────────────────────

    /** Oberste erhaltene Hoehe der Spalte (zackige Abbruchkante bei gekappten Gebaeuden). */
    public int topLimit(int x, int z) {
        if (!capped) return topY + 1;
        int jag = (int) (CityRandom.noise(hashSeed ^ 0xCA9L, x, z, 3) * (jagMax + 1));
        return keptTop - jag;
    }

    public boolean removed(int x, int y, int z) {
        if (y > topLimit(x, z)) return true;
        if (collapse && y >= cy0) {
            double t = (y - cy0) / (double) Math.max(1, topY - cy0 + 1);
            double r = 1.2D + cr * Math.sqrt(t) * 1.6D + (CityRandom.noise(hashSeed ^ 0xC011L, x + y, z - y, 2) - 0.5D) * 2.5D;
            double dx = x - ccx, dz = z - ccz;
            if (dx * dx + dz * dz < r * r) return true;
        }
        return false;
    }

    private boolean hole(int f, int x, int z) {
        for (int[] h : holes) {
            if (h[0] != f) continue;
            int dx = x - h[1], dz = z - h[2];
            double r = h[3] + (CityRandom.unit(hashSeed ^ 0x401EL, x, f, z) - 0.5D) * 1.2D;
            if (dx * dx + dz * dz <= r * r) return true;
        }
        return false;
    }

    // ── Bloecke ─────────────────────────────────────────────────────────────

    /** Versatz der schiefen Obergeschosse auf Hoehe y. */
    public int shear(int y) {
        return y < shearY ? 0 : Math.min(2, 1 + (y - shearY) / 6);
    }

    /**
     * Block des Gebaeudes an (x,y,z): {@code null} = nicht zum Gebaeude gehoerig (nichts setzen), Luft = innen/zerstoert.
     */
    @Nullable
    public BlockState blockAt(int x, int y, int z) {
        int off = shear(y);
        if (off != 0) {
            x -= off * sdx;
            z -= off * sdz;
        }
        if (!inFootprint(x, z) || y < bottomY() || y > maxY()) return null;
        CityPalette p = CityPalette.get();
        BlockState air = Blocks.AIR.defaultBlockState();
        if (removed(x, y, z)) {
            // eingestuerzt: im Keller Wasser stehen lassen
            return y < base && y <= floodTop && y > basementFloor() ? p.water : air;
        }
        boolean perim = x == x0 || x == x1 || z == z0 || z == z1;

        if (y < base) {
            if (!basement) return foundation;
            if (y == basementFloor()) return p.concrete;
            if (perim) return CityRandom.unit(hashSeed, x, y, z) < 0.2 ? p.stoneBricksMossy : p.stoneBricks;
            BlockState s = inShaft(x, z) ? shaftBlock(x, y, z) : air;
            return s.isAir() && y <= floodTop ? p.water : s;
        }

        int rel = y - base;
        int f = rel / floorH;
        int yIn = rel % floorH;

        if (y > topY) {
            if (y == topY + 1 && perim && !capped && kind != Kind.PARKING) return roofEdge;
            if (y == topY + 1 && perim && kind == Kind.PARKING && (x == x0 || x == x1) && (z == z0 || z == z1)) return roofEdge;
            return null;
        }
        if (inShaft(x, z) && !perim) return shaftBlock(x, y, z);
        if (y == topY) {
            if (perim) return trim;
            if (kind == Kind.WAREHOUSE && hole(floors, x, z)) return air;
            if (kind == Kind.WAREHOUSE && CityRandom.noise(hashSeed ^ 0x700FL, x, z, 4) < decay * 0.55) return air;
            return roof;
        }
        if (yIn == 0) {
            if (perim) return trim;
            if (f > 0 && hole(f, x, z)) return air;
            if (CityRandom.unit(hashSeed ^ 0xF100L, x, y, z) < decay * 0.18) return floorAlt;
            return floor;
        }
        if (perim) return facade(x, y, z, f, yIn);
        if (kind == Kind.WAREHOUSE) return air;
        if ((kind == Kind.TOWER || kind == Kind.APARTMENT) && partitionAt(x, z, yIn)) {
            return CityRandom.unit(hashSeed ^ 0x9A27L, x, y, z) < decay * 0.3 ? air : partition;
        }
        if (kind == Kind.PARKING && (x - x0) % 6 == 0 && (z - z0) % 6 == 0) return trim; // Stuetzen
        return air;
    }

    private boolean partitionAt(int x, int z, int yIn) {
        if (x1 - x0 < 11 || z1 - z0 < 11) return false;
        if (x == midX && z > z0 && z < z1) return !(yIn <= 2 && (z == midZ - 3 || z == midZ + 3));
        if (z == midZ && x > x0 && x < x1) return !(yIn <= 2 && (x == midX - 3 || x == midX + 3));
        return false;
    }

    private BlockState facade(int x, int y, int z, int f, int yIn) {
        CityPalette p = CityPalette.get();
        BlockState air = Blocks.AIR.defaultBlockState();
        boolean corner = (x == x0 || x == x1) && (z == z0 || z == z1);
        if (corner) return trim;
        Direction side = x == x0 ? Direction.WEST : x == x1 ? Direction.EAST : z == z0 ? Direction.NORTH : Direction.SOUTH;
        int along = side.getAxis() == Direction.Axis.X ? z - z0 : x - x0;

        double h = CityRandom.unit(hashSeed ^ 0xFACEL, x, y, z);
        if (h < decay * 0.05) return air;
        BlockState w = h < decay * 0.3 ? wallAlt : wall;
        boolean entranceSide = side == entrance && f == 0;

        switch (kind) {
            case TOWER -> {
                if (f == 0) {
                    if (entranceSide && Math.abs(along - entranceAlong) <= 1) return air;
                    if (along % 4 != 0 && yIn <= 3) return window(x, y, z);
                    return trim;
                }
                if (along % 3 != 0 && yIn <= 2) return window(x, y, z);
                return w;
            }
            case APARTMENT -> {
                if (entranceSide && Math.abs(along - entranceAlong) <= 0 && yIn <= 2) return air;
                if (along % 3 == 1 && yIn <= 2 && yIn >= 1) return window(x, y, z);
                if (along % 3 == 2 && yIn <= 2 && yIn >= 1 && (along / 3) % 2 == 0) return window(x, y, z);
                return w;
            }
            case SHOP -> {
                if (f == 0 && (side == entrance || side == entrance.getClockWise())) {
                    if (side == entrance && (along == entranceAlong || along == entranceAlong + 1) && yIn <= 2) return air;
                    if (along % 6 != 0 && yIn <= 3 && yIn >= 1) return yIn == 3 ? trim : window(x, y, z);
                    return trim;
                }
                if (along % 3 == 1 && yIn <= 2 && yIn >= 1) return window(x, y, z);
                return w;
            }
            case PARKING -> {
                if (along % 5 == 0) return trim;
                if (entranceSide && Math.abs(along - entranceAlong) <= 2) return air;
                if (yIn == 1) return CityRandom.unit(hashSeed, x, y, z) < decay * 0.2 ? air : w;
                return air;
            }
            default -> { // WAREHOUSE
                if (along % 6 == 0) return trim;
                if (entranceSide && Math.abs(along - entranceAlong) <= 2 && yIn <= 5) return air;
                if ((yIn == 5 || yIn == 6) && along % 2 == 1) return window(x, y, z);
                return w;
            }
        }
    }

    private BlockState window(int x, int y, int z) {
        double h = CityRandom.unit(hashSeed ^ 0x61A55L, x, y, z);
        if (h < 0.22 + decay * 0.55) return Blocks.AIR.defaultBlockState();
        return CityPalette.get().glassPane;
    }

    /** Wendeltreppe: Mittelsaeule, je Hoehe eine Stufe auf dem Ring (Ausgaenge auf Stockwerkshoehe Ost/Sued). */
    private BlockState shaftBlock(int x, int y, int z) {
        CityPalette p = CityPalette.get();
        int lx = x - sx, lz = z - sz;
        if (lx == 1 && lz == 1) return trim;
        int bottom = basement ? basementFloor() : base;
        if (y < bottom) return foundation;
        if (y == basementFloor() && basement) return p.concrete;
        int step = Math.floorMod(y - base + 2, RING.length);
        int ring = ringIndex(lx, lz);
        if (ring != step) return !basement && y == base ? trim : Blocks.AIR.defaultBlockState();
        int[] next = RING[(ring + 1) % RING.length];
        int[] cur = RING[ring];
        Direction up = Direction.fromDelta(next[0] - cur[0], 0, next[1] - cur[1]);
        return p.concreteStairs(up == null ? Direction.NORTH : up);
    }

    private static int ringIndex(int lx, int lz) {
        for (int i = 0; i < RING.length; i++) if (RING[i][0] == lx && RING[i][1] == lz) return i;
        return -1;
    }

    /** Ist (x,z) eine Stelle direkt vor dem Treppenhaus (frei halten)? */
    public boolean nearShaft(int x, int z) {
        return hasShaft && x >= sx - 1 && x <= sx + 3 && z >= sz - 1 && z <= sz + 3;
    }
}
