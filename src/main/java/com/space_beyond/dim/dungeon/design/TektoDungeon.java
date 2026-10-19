package com.space_beyond.dim.dungeon.design;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import com.hbm_m.block.ModBlocks;
import com.space_m.dim.BiomeGenBaseCelestial;
import com.space_beyond.dim.dungeon.CustomDungeon;
import com.space_beyond.progression.SpaceProgression.Stage;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Tekto - "Polymer-Raffinerie": eine Petrochemie-Anlage auf dem Gummi-/PVC-Boden von Tekto.
 * <pre>
 *  Oberirdisch (y 0..86):
 *   NW  Destillationskolonnen C1..C7 (bis y 72) mit Ringbuehnen, Rohrbruecken und Wendeltreppe   -> Schluessel 1 (C1 oben)
 *   NO  Tanklager: Kugeltank S1 (Ø 31, CCl-Bruehe, Innenstreben-Parkour), S2, S3, Flachtanks      -> Schluessel 2 (in S1)
 *   O   Rohrbruecken-Labyrinth auf y 20 (8 x 8 Zellen), Siegelwaechter-Buehne im Nordosten          -> Schluessel 3 (Waechter)
 *   SW  zwei Kuehltuerme, Fackel (Flare-Stack, y 86) mit Abscheider
 *   S   Kontrollgebaeude mit Siegeltor, Zufahrt von Sueden
 *  Unterirdisch: Treppenstollen (y 0..-16) zur Crackanlagen-Halle (69 x 50, Boden y -34, Decke y -10) mit Boss.
 * </pre>
 */
public final class TektoDungeon extends CustomDungeon {

    static final int R = 92;

    @Override public Stage stage() { return Stage.TEKTO; }
    @Override public int radius() { return R; }
    @Override public BlockState surface() { return Kit.b(com.space_m.dim.tekto.TektoBlocks.RUBBER_SILT.get()); }
    @Override public BlockState filler() { return Kit.b(Blocks.STONE); }
    @Override public boolean fits(BiomeGenBaseCelestial biome) {
        return biome != com.space_m.dim.tekto.biome.BiomeGenBaseTekto.tetrachloricRiver;
    }
    @Override public int minY() { return -40; }
    @Override public int maxY() { return 86; }
    @Override public int maxBase() { return 160; }
    @Override public int[] entry() { return new int[] {0, 1, 91}; }
    @Override public String boss() { return "space_beyond:polymer_colossus"; }

    static final String OOZE = "space_beyond:polymer_ooze", DRONE = "space_beyond:refinery_drone";

    // ── Lage der Bauteile ──
    // Kolonnen {u, v, r, top}
    static final int[][] COLS = {
            {-60, -56, 6, 71},   // C1 Schluesselkolonne
            {-28, -56, 4, 56},   // C2
            {-60, -24, 4, 50},   // C3
            {-28, -24, 5, 40},   // C4
            {-82, -42, 3, 36},   // C5
            {-82, -78, 3, 30},   // C6
            {-44, -82, 3, 44}};  // C7
    // Kugeltank S1 (Schluessel 2)
    static final int S1U = 50, S1Y = 26, S1V = -52, S1R = 15;
    // Labyrinth
    static final int MU = 30, MV = -10, MS = 7, NC = 8, NR = 8, MY = 20;
    // Arena
    static final int AF = -34, AC = -10;            // Boden/Decke
    static final int AU0 = -34, AU1 = 34, AV0 = -30, AV1 = 19;

    // ── Materialien (je Aufruf, damit Registry-Zugriffe erst beim Bauen passieren) ──
    static final class M {
        final BlockState steel = b(ModBlocks.DECO_STEEL.get()), lead = b(ModBlocks.DECO_LEAD.get()), rust = b(ModBlocks.DECO_RUSTY_STEEL.get());
        final BlockState alu = b(ModBlocks.DECO_ALUMINUM.get()), conc = b(ModBlocks.CONCRETE.get()), smooth = b(ModBlocks.CONCRETE_SMOOTH.get());
        final BlockState concCr = b(ModBlocks.CONCRETE_CRACKED.get()), haz = b(ModBlocks.CONCRETE_HAZARD.get()), brick = b(ModBlocks.BRICK_CONCRETE.get());
        final BlockState brickCr = b(ModBlocks.BRICK_CONCRETE_CRACKED.get()), pillar = b(ModBlocks.CONCRETE_PILLAR.get());
        final BlockState pvc = b(com.space_m.dim.tekto.TektoBlocks.PVC_PLANKS.get()), vinyl = b(com.space_m.dim.tekto.TektoBlocks.VINYL_PLANKS.get());
        final BlockState silt = b(com.space_m.dim.tekto.TektoBlocks.RUBBER_SILT.get()), rgrass = b(com.space_m.dim.tekto.TektoBlocks.RUBBER_GRASS.get());
        final BlockState ccl = b(com.space_m.dim.tekto.TektoBlocks.CCL_BLOCK.get());
        final BlockState pvcLog = Kit.axis(com.space_m.dim.tekto.TektoBlocks.PVC_LOG.get(), Direction.Axis.Y);
        final BlockState limeC = b(Blocks.LIME_CONCRETE), yelC = b(Blocks.YELLOW_CONCRETE), grayC = b(Blocks.GRAY_CONCRETE), whiteC = b(Blocks.WHITE_CONCRETE);
        final BlockState redC = b(Blocks.RED_CONCRETE), blackC = b(Blocks.BLACK_CONCRETE), lgrayC = b(Blocks.LIGHT_GRAY_CONCRETE);
        final BlockState plateLime = b(ModBlocks.PLATEMETAL_LIME.get()), plateYel = b(ModBlocks.PLATEMETAL_YELLOW.get()), plateWhite = b(ModBlocks.PLATEMETAL_WHITE.get());
        final BlockState poly = b(ModBlocks.BLOCK_POLYMER.get()), rubber = b(ModBlocks.BLOCK_RUBBER.get()), bakelite = b(ModBlocks.BLOCK_BAKELITE.get());
        final BlockState asph = b(ModBlocks.ASPHALT.get()), vtile = b(ModBlocks.VINYL_TILE.get()), vtileS = b(ModBlocks.VINYL_TILE_SMALL.get());
        final BlockState oily = b(ModBlocks.DIRT_OILY.get());
        final BlockState glassLime = b(Blocks.LIME_STAINED_GLASS), glassLead = b(ModBlocks.GLASS_LEAD.get()), glassGreen = b(Blocks.GREEN_STAINED_GLASS);
        final BlockState glassOrange = b(Blocks.ORANGE_STAINED_GLASS), glassYellow = b(Blocks.YELLOW_STAINED_GLASS), glassRed = b(Blocks.RED_STAINED_GLASS);
        final BlockState light = b(Blocks.VERDANT_FROGLIGHT), light2 = b(Blocks.OCHRE_FROGLIGHT), sea = b(Blocks.SEA_LANTERN), shroom = b(Blocks.SHROOMLIGHT);
        final BlockState rlight = b(ModBlocks.REINFORCED_LIGHT.get());
        final BlockState bars = b(Blocks.IRON_BARS), chain = b(Blocks.CHAIN), wall = b(Blocks.POLISHED_DEEPSLATE_WALL);
        final BlockState lanternH = Kit.lantern(Blocks.LANTERN, true), lantern = Kit.lantern(Blocks.LANTERN, false);
        final BlockState pipeX = Kit.axis(ModBlocks.DECO_PIPE.get(), Direction.Axis.X), pipeZ = Kit.axis(ModBlocks.DECO_PIPE.get(), Direction.Axis.Z);
        final BlockState pipeY = Kit.axis(ModBlocks.DECO_PIPE.get(), Direction.Axis.Y);
        final BlockState gpipeX = Kit.axis(ModBlocks.DECO_PIPE_GREEN.get(), Direction.Axis.X), gpipeZ = Kit.axis(ModBlocks.DECO_PIPE_GREEN.get(), Direction.Axis.Z);
        final BlockState gpipeY = Kit.axis(ModBlocks.DECO_PIPE_GREEN.get(), Direction.Axis.Y);
        final BlockState rpipeX = Kit.axis(ModBlocks.DECO_PIPE_RUSTED.get(), Direction.Axis.X), rpipeZ = Kit.axis(ModBlocks.DECO_PIPE_RUSTED.get(), Direction.Axis.Z);
        final BlockState rimY = Kit.axis(ModBlocks.DECO_PIPE_RIM.get(), Direction.Axis.Y), quadY = Kit.axis(ModBlocks.DECO_PIPE_QUAD_GREEN.get(), Direction.Axis.Y);
        final BlockState quadX = Kit.axis(ModBlocks.DECO_PIPE_QUAD.get(), Direction.Axis.X), quadZ = Kit.axis(ModBlocks.DECO_PIPE_QUAD.get(), Direction.Axis.Z);
        final BlockState crate = b(ModBlocks.CRATE_STEEL.get()), crateW = b(ModBlocks.CRATE.get());
        final BlockState vent = b(ModBlocks.CONCRETE_VENT.get()), fan = b(ModBlocks.CONCRETE_FAN.get());
        final BlockState rod = b(Blocks.LIGHTNING_ROD), endRod = b(Blocks.END_ROD), campfire = b(Blocks.CAMPFIRE);
        final BlockState netherrack = b(Blocks.NETHERRACK), fire = b(Blocks.FIRE);

        static BlockState b(Block x) {
            return x.defaultBlockState();
        }
    }

    // ── Labyrinth (fester Zufall je Dungeon) ──
    static final class Maze {
        final boolean[][] east = new boolean[NC][NR], south = new boolean[NC][NR];
        final List<int[]> dead = new ArrayList<>();

        Maze(long seed) {
            Random r = new Random(seed);
            boolean[][] seen = new boolean[NC][NR];
            Deque<int[]> st = new ArrayDeque<>();
            st.push(new int[] {0, NR - 1});
            seen[0][NR - 1] = true;
            int[][] d4 = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            while (!st.isEmpty()) {
                int[] c = st.peek();
                List<int[]> nb = new ArrayList<>();
                for (int[] d : d4) {
                    int i = c[0] + d[0], j = c[1] + d[1];
                    if (i >= 0 && i < NC && j >= 0 && j < NR && !seen[i][j]) nb.add(new int[] {i, j});
                }
                if (nb.isEmpty()) { st.pop(); continue; }
                int[] n = nb.get(r.nextInt(nb.size()));
                if (n[0] != c[0]) east[Math.min(n[0], c[0])][c[1]] = true;
                else south[c[0]][Math.min(n[1], c[1])] = true;
                seen[n[0]][n[1]] = true;
                st.push(n);
            }
            for (int j = 0; j < NR; j++) for (int i = 0; i < NC; i++) {
                if (i == 0 && j == NR - 1 || i == NC - 1 && j == 0) continue;
                int deg = (i < NC - 1 && east[i][j] ? 1 : 0) + (i > 0 && east[i - 1][j] ? 1 : 0) + (j < NR - 1 && south[i][j] ? 1 : 0) + (j > 0 && south[i][j - 1] ? 1 : 0);
                if (deg == 1) dead.add(new int[] {MU + i * MS, MV + j * MS});
            }
        }

        boolean deck(int u, int v) {
            if (u >= MU - 1 && u <= MU + 1 && v > MV + (NR - 1) * MS && v <= MV + (NR - 1) * MS + 3) return true;   // zum Treppenturm
            int gu = MU + (NC - 1) * MS;
            if (u >= gu - 1 && u <= gu + 1 && v < MV && v >= MV - 4) return true;                                   // zur Waechterbuehne
            int du = u - MU, dv = v - MV;
            int i = Math.floorDiv(du + 3, MS), j = Math.floorDiv(dv + 3, MS);
            if (i < 0 || i >= NC || j < 0 || j >= NR) return false;
            int ou = du - i * MS, ov = dv - j * MS;
            if (Math.abs(ou) <= 1 && Math.abs(ov) <= 1) return true;
            if (Math.abs(ov) <= 1) {
                if (ou >= 2) return i < NC - 1 && east[i][j];
                if (ou <= -2) return i > 0 && east[i - 1][j];
            }
            if (Math.abs(ou) <= 1) {
                if (ov >= 2) return j < NR - 1 && south[i][j];
                if (ov <= -2) return j > 0 && south[i][j - 1];
            }
            return false;
        }
    }

    @Override
    protected void build(Kit k) {
        M m = new M();
        Maze mz = new Maze((long) (k.rnd(0x5EEDL, 1, 2, 3) * 4.0e15));
        ground(k, m);
        perimeter(k, m);
        arena(k, m);
        controlBuilding(k, m);
        tunnel(k, m);
        k.sealGate(0, 0, 36, Direction.SOUTH, 2, 5, m.steel, m.plateLime);
        racks(k, m);
        columns(k, m);
        tankFarm(k, m);
        maze(k, m, mz);
        coolingTower(k, m, -62, 36, 14, 9, 30, 42);
        coolingTower(k, m, -66, 78, 11, 7, 24, 34);
        flare(k, m);
        exchangers(k, m);
        furnace(k, m);
        warehouse(k, m);
        props(k, m);
        plan(k, mz);
    }

    // ═════════════════════════════════════════ Gelaende ═════════════════════════════════════════

    private static boolean tankBund(int u, int v) {
        return u >= 12 && u <= 90 && v >= -90 && v <= -20;
    }

    private void ground(Kit k, M m) {
        int ua = Math.max(-R, k.u0()), ub = Math.min(R, k.u1()), va = Math.max(-R, k.v0()), vb = Math.min(R, k.v1());
        for (int u = ua; u <= ub; u++) for (int v = va; v <= vb; v++) {
            BlockState s;
            if (Math.abs(u) <= 3 && v >= 58) s = u == 0 && Math.floorMod(v, 4) < 2 ? m.yelC : m.asph;
            else if (v >= 60 && v <= 64 && u >= -86 && u <= 86) s = v == 62 && Math.floorMod(u, 4) < 2 ? m.yelC : m.asph;
            else if (Math.abs(u) <= 3 && v >= -86 && v < 60 || Math.abs(v + 16) <= 1 && u >= -86 && u <= 86) s = m.asph;
            else if (u >= -21 && u <= 21 && v >= 19 && v <= 59) s = m.smooth;
            else if (tankBund(u, v)) {
                boolean edge = u == 12 || u == 90 || v == -90 || v == -20;
                s = edge ? m.haz : k.rnd(3, u, 0, v) < 0.12 ? m.concCr : k.noise(7, u, v, 6) < 0.18 ? m.oily : m.conc;
            } else if (u >= 26 && u <= 86 && v >= -14 && v <= 56) s = k.rnd(4, u, 0, v) < 0.1 ? m.concCr : m.conc;
            else {
                double n = k.noise(5, u, v, 9);
                s = n < 0.32 ? m.rgrass : n > 0.8 ? m.oily : m.silt;
            }
            k.set(u, 0, v, s);
            // Auffangwanne um das Tanklager (eine Stufe hoch)
            if (tankBund(u, v) && (u == 12 || u == 90 || v == -90 || v == -20) && !(Math.abs(u - 50) <= 2 && v == -20) && !(u == 12 && Math.abs(v + 40) <= 2)) {
                k.set(u, 1, v, m.conc);
            }
        }
    }

    private void perimeter(Kit k, M m) {
        int ua = Math.max(-R, k.u0()), ub = Math.min(R, k.u1()), va = Math.max(-R, k.v0()), vb = Math.min(R, k.v1());
        for (int u = ua; u <= ub; u++) for (int v = va; v <= vb; v++) {
            if (Math.max(Math.abs(u), Math.abs(v)) != 89) continue;
            if (v == 89 && Math.abs(u) <= 5) continue;                         // Werktor
            if (v >= 60 && v <= 64 && Math.abs(u) == 89) continue;              // Strasse
            boolean post = Math.floorMod(u + v, 8) == 0;
            if (post) {
                k.fill(u, 1, v, u, 4, v, m.pillar);
                k.set(u, 5, v, Math.floorMod(u + v, 24) == 0 ? m.light2 : m.haz);
            } else {
                k.fill(u, 1, v, u, 3, v, m.bars);
                k.set(u, 4, v, m.chain);
            }
        }
        // Torpfosten mit Lampen
        for (int s = -1; s <= 1; s += 2) {
            int u = s * 6;
            k.fill(u, 1, 88, u, 7, 90, m.haz);
            k.fill(u, 1, 89, u, 7, 89, m.steel);
            k.set(u, 8, 89, m.light2);
        }
        k.fill(-6, 8, 89, 6, 8, 89, m.steel);
        for (int u = -5; u <= 5; u++) if (Math.floorMod(u, 2) == 0) k.set(u, 9, 89, m.plateLime); else k.set(u, 9, 89, m.plateYel);
    }

    // ═════════════════════════════════════════ Rohrbruecken am Boden ═════════════════════════════════════════

    private void racks(Kit k, M m) {
        rackU(k, m, -18, 22, -16);
        rackV(k, m, 4, -86, -20);
        rackU(k, m, -86, -40, -16);
    }

    /** Rohrbruecke entlang u bei v. */
    private void rackU(Kit k, M m, int u0, int u1, int v) {
        if (!k.touches(u0, v - 4, u1, v + 4)) return;
        for (int u = u0; u <= u1; u += 6) {
            for (int s = -3; s <= 3; s += 6) {
                k.set(u, 1, v + s, m.conc);
                k.fill(u, 2, v + s, u, 9, v + s, m.steel);
            }
            k.fill(u, 6, v - 3, u, 6, v + 3, m.steel);
            k.fill(u, 9, v - 3, u, 9, v + 3, m.steel);
            k.set(u, 5, v - 3, m.light);
        }
        k.fill(u0, 7, v - 2, u1, 7, v - 2, m.gpipeX);
        k.fill(u0, 7, v, u1, 7, v, m.pipeX);
        k.fill(u0, 7, v + 2, u1, 7, v + 2, m.rpipeX);
        k.line(u0, 11, v - 1, u1, 11, v - 1, 1.0, m.plateLime);
        k.line(u0, 11, v + 2, u1, 11, v + 2, 1.0, m.alu);
        for (int u = u0 + 3; u <= u1; u += 12) {                // Flansche
            k.fill(u, 10, v - 2, u, 12, v, m.lead);
            k.fill(u, 10, v + 1, u, 12, v + 3, m.steel);
        }
    }

    /** Rohrbruecke entlang v bei u. */
    private void rackV(Kit k, M m, int u, int v0, int v1) {
        if (!k.touches(u - 4, v0, u + 4, v1)) return;
        for (int v = v0; v <= v1; v += 6) {
            for (int s = -3; s <= 3; s += 6) {
                k.set(u + s, 1, v, m.conc);
                k.fill(u + s, 2, v, u + s, 9, v, m.steel);
            }
            k.fill(u - 3, 6, v, u + 3, 6, v, m.steel);
            k.fill(u - 3, 9, v, u + 3, 9, v, m.steel);
            k.set(u + 3, 5, v, m.light);
        }
        k.fill(u - 2, 7, v0, u - 2, 7, v1, m.gpipeZ);
        k.fill(u, 7, v0, u, 7, v1, m.pipeZ);
        k.fill(u + 2, 7, v0, u + 2, 7, v1, m.rpipeZ);
        k.line(u - 1, 11, v0, u - 1, 11, v1, 1.0, m.plateLime);
        k.line(u + 2, 11, v0, u + 2, 11, v1, 1.0, m.alu);
        for (int v = v0 + 3; v <= v1; v += 12) {
            k.fill(u - 2, 10, v, u, 12, v, m.lead);
            k.fill(u + 1, 10, v, u + 3, 12, v, m.steel);
        }
    }

    // ═════════════════════════════════════════ Kolonnen (Schluessel 1) ═════════════════════════════════════════

    private void columns(Kit k, M m) {
        for (int i = 0; i < COLS.length; i++) {
            int[] c = COLS[i];
            column(k, m, c[0], c[1], c[2], c[3], i == 0);
        }
        // C4: Ringe 12 und 24, Leitern Sued (Boden -> 12) und Nord (12 -> 24)
        ring(k, m, -28, -24, 5, 12);
        ring(k, m, -28, -24, 5, 24);
        k.ladder(-28, 1, 12, -18, Direction.NORTH);
        k.ladder(-28, 13, 24, -30, Direction.SOUTH);
        // C3: Ringe 24 und 36, Leiter West (24 -> 36)
        ring(k, m, -60, -24, 4, 24);
        ring(k, m, -60, -24, 4, 36);
        k.ladder(-65, 25, 36, -24, Direction.EAST);
        // C2: Ring 24 (Abstecher mit Beute), Ring 44 (Zierde)
        ring(k, m, -28, -56, 4, 24);
        ring(k, m, -28, -56, 4, 44);
        // Zierringe der kleinen Kolonnen
        ring(k, m, -82, -42, 3, 28);
        ring(k, m, -82, -78, 3, 22);
        ring(k, m, -44, -82, 3, 34);
        // Bruecke C4 -> C3 (y 24) mit zwei Bruchstellen
        bridge(k, m, true, -24, -53, -36, 24, new int[][] {{-42, -40}, {-49, -47}});
        // Abstecher C4 -> C2 (y 24)
        bridge(k, m, false, -28, -49, -32, 24, new int[0][]);
        // Bruecke C3 -> C1 (y 36) mit zwei Bruchstellen
        bridge(k, m, false, -60, -47, -31, 36, new int[][] {{-42, -40}, {-36, -34}});
        // C1: Wendeltreppe 36 -> 71, Gipfelbuehne y 72
        spiralStair(k, m.steel, m.haz, m.bars, -60, -56, 36, 71, 7, 10, 2.5, 90, true, 0.0, 99.0);
        topPlatform(k, m);
    }

    private void column(Kit k, M m, int cu, int cv, int r, int top, boolean key) {
        int ext = r + 2;
        if (!k.touches(cu - ext, cv - ext, cu + ext, cv + ext)) return;
        for (int u = Math.max(cu - ext, k.u0()); u <= Math.min(cu + ext, k.u1()); u++) {
            for (int v = Math.max(cv - ext, k.v0()); v <= Math.min(cv + ext, k.v1()); v++) {
                int du = u - cu, dv = v - cv;
                double d2 = du * du + dv * dv;
                if (d2 <= (r + 1.5) * (r + 1.5)) {                     // Sockel
                    k.set(u, 1, v, m.conc);
                    k.set(u, 2, v, d2 > (r + 0.5) * (r + 0.5) ? m.haz : m.conc);
                }
                if (d2 > r * r + 0.25) continue;
                boolean edge = d2 > (r - 1) * (r - 1) + 0.25;
                for (int y = 3; y <= top; y++) {
                    BlockState s;
                    if (!edge) s = m.steel;
                    else if (y % 9 == 0) s = m.lead;
                    else if (key && y > top - 8) s = (y / 2) % 2 == 0 ? m.plateLime : m.plateYel;
                    else if (y % 9 == 1 || y % 9 == 8) s = m.alu;
                    else if (k.rnd(11, u, y, v) < 0.06) s = m.rust;
                    else s = k.noise(12, u * 3 + y, v * 3 - y, 7) < 0.25 ? m.lgrayC : m.steel;
                    k.set(u, y, v, s);
                }
                // Kopf: flache Haube
                if (!key) {
                    double rr = Math.sqrt(d2);
                    int cap = top + (rr < r - 1.5 ? 2 : rr < r - 0.5 ? 1 : 0);
                    for (int y = top + 1; y <= cap; y++) k.set(u, y, v, m.lead);
                }
            }
        }
        // Stutzen (Mannloecher) und Steigleitung
        for (int i = 0; i < 4; i++) {
            Direction d = Direction.from2DDataValue(i);
            int y = 8 + ((i * 7 + cu) & 7) + i * 3;
            if (y > top - 4) continue;
            int su = d.getStepX(), sv = d.getStepZ();
            int pu = cu + su * (r + 1), pv = cv + sv * (r + 1);
            // um 45 Grad versetzt, damit Leitern frei bleiben
            pu += sv * 2;
            pv -= su * 2;
            k.set(pu, y, pv, d.getAxis() == Direction.Axis.X ? m.pipeX : m.pipeZ);
            k.set(pu + su, y, pv + sv, m.lead);
        }
        if (!key) {
            int du = (int) Math.round(r * 0.72) + 1;
            k.fill(cu + du, 3, cv + du, cu + du, top - 2, cv + du, m.gpipeY);
            k.set(cu + du, top - 1, cv + du, m.rimY);
        }
        // Flugwarnlicht
        k.set(cu, top + (key ? 1 : 3), cv, key ? m.light : m.light2);
    }

    /** Ringbuehne: Boden zwischen r+1 und r+4, aussen Kante mit Gelaender. */
    private void ring(Kit k, M m, int cu, int cv, int r, int y) {
        int ext = r + 5;
        if (!k.touches(cu - ext, cv - ext, cu + ext, cv + ext)) return;
        for (int u = Math.max(cu - ext, k.u0()); u <= Math.min(cu + ext, k.u1()); u++) {
            for (int v = Math.max(cv - ext, k.v0()); v <= Math.min(cv + ext, k.v1()); v++) {
                double d = Math.sqrt((u - cu) * (u - cu) + (v - cv) * (v - cv));
                if (d <= r + 0.5 || d > r + 4.5) continue;
                boolean rim = d > r + 3.5;
                double a = Math.atan2(v - cv, u - cu);
                boolean lamp = !rim && d > r + 2.5 && Math.abs(Math.IEEEremainder(a, Math.PI / 3)) * d < 0.5;
                k.set(u, y, v, rim ? m.haz : lamp ? m.light : (int) Math.floor(d) % 2 == 0 ? m.steel : m.alu);
                if (rim) {
                    k.set(u, y + 1, v, m.bars);
                    k.set(u, y - 1, v, m.lead);
                }
                // Konsolen unter der Buehne
                if (!rim && Math.abs(Math.IEEEremainder(a + Math.PI / 6, Math.PI / 3)) * d < 0.5) {
                    k.set(u, y - 1, v, m.steel);
                    if (d < r + 1.6) k.set(u, y - 2, v, m.steel);
                }
            }
        }
    }

    /** Laufsteg entlang u (alongU) oder v; Bruchstellen [a,b] ohne Boden. */
    private void bridge(Kit k, M m, boolean alongU, int fixed, int a, int b, int y, int[][] gaps) {
        int lo = Math.min(a, b), hi = Math.max(a, b);
        if (alongU ? !k.touches(lo, fixed - 3, hi, fixed + 3) : !k.touches(fixed - 3, lo, fixed + 3, hi)) return;
        for (int t = lo; t <= hi; t++) {
            boolean gap = false, nearGap = false;
            for (int[] g : gaps) {
                if (t >= g[0] && t <= g[1]) gap = true;
                if (t == g[0] - 1 || t == g[1] + 1) nearGap = true;
            }
            for (int o = -2; o <= 2; o++) {
                int u = alongU ? t : fixed + o, v = alongU ? fixed + o : t;
                for (int yy = y + 1; yy <= y + 3; yy++) k.air(u, yy, v);
                if (gap) { k.air(u, y, v); continue; }
                if (Math.abs(o) == 2) {
                    k.set(u, y, v, m.haz);
                    k.set(u, y + 1, v, m.bars);
                } else {
                    k.set(u, y, v, nearGap ? m.rust : o == 0 && Math.floorMod(t, 6) == 0 ? m.light : m.steel);
                }
            }
            // Rohr unter dem Steg (gebrochen an den Luecken)
            if (!gap) {
                int u = alongU ? t : fixed, v = alongU ? fixed : t;
                BlockState pm = Math.floorMod(t, 8) == 0 ? m.lead : m.plateLime;
                for (int o = -1; o <= 1; o++) {
                    k.set(alongU ? u : u + o, y - 2, alongU ? v + o : v, pm);
                    if (o == 0) {
                        k.set(u, y - 1, v, pm);
                        k.set(u, y - 3, v, pm);
                    }
                }
                if (Math.floorMod(t, 5) == 0) {                     // Haenger
                    k.set(alongU ? u : u - 2, y - 1, alongU ? v - 2 : v, m.chain);
                    k.set(alongU ? u : u + 2, y - 1, alongU ? v + 2 : v, m.chain);
                }
            } else {
                int u = alongU ? t : fixed, v = alongU ? fixed : t;
                k.air(u, y - 1, v);
            }
        }
    }

    /**
     * Wendeltreppe wie {@link Kit#spiral}, zusaetzlich mit Gelaender (Kante + Gitter) aussen. Gelaender fehlt am Anfang
     * (Bogen &lt; railFrom) und oben (y &gt;= railTo), damit man ein- und aussteigen kann.
     */
    private void spiralStair(Kit k, BlockState step, BlockState kerb, BlockState rail, double cu, double cv, int y0, int y1, double rIn, double rOut,
                             double bps, double a0, boolean railing, double railFrom, double railTo) {
        int ir = (int) Math.ceil(rOut + 2);
        if (!k.touches((int) cu - ir, (int) cv - ir, (int) cu + ir, (int) cv + ir)) return;
        double perRad = rOut / bps;
        for (int u = Math.max((int) Math.floor(cu - ir), k.u0()); u <= Math.min((int) Math.ceil(cu + ir), k.u1()); u++) {
            for (int v = Math.max((int) Math.floor(cv - ir), k.v0()); v <= Math.min((int) Math.ceil(cv + ir), k.v1()); v++) {
                double d = Math.sqrt((u - cu) * (u - cu) + (v - cv) * (v - cv));
                boolean railCell = railing && d > rOut + 0.5 && d <= rOut + 1.5;
                if (d < rIn - 0.5 || d > rOut + 0.5 && !railCell) continue;
                double ang = Math.atan2(v - cv, u - cu) - Math.toRadians(a0);
                ang = ((ang % (2 * Math.PI)) + 2 * Math.PI) % (2 * Math.PI);
                for (double a = ang; ; a += 2 * Math.PI) {
                    int y = y0 + (int) Math.floor(a * perRad);
                    if (y > y1) break;
                    if (railCell) {
                        if (a < railFrom || y >= railTo) continue;
                        k.set(u, y, v, kerb);
                        k.set(u, y + 1, v, rail);
                    } else {
                        k.set(u, y, v, step);
                        for (int h = 1; h <= 3; h++) k.air(u, y + h, v);
                    }
                }
            }
        }
    }

    private void topPlatform(Kit k, M m) {
        int cu = -60, cv = -56;
        if (k.touches(cu - 9, cv - 9, cu + 9, cv + 9)) {
            for (int u = Math.max(cu - 8, k.u0()); u <= Math.min(cu + 8, k.u1()); u++) for (int v = Math.max(cv - 8, k.v0()); v <= Math.min(cv + 8, k.v1()); v++) {
                double d = Math.sqrt((u - cu) * (u - cu) + (v - cv) * (v - cv));
                if (d > 7.5) continue;
                double ang = Math.toDegrees(Math.atan2(v - cv, u - cu));
                ang = ((ang % 360) + 360) % 360;
                boolean rim = d > 6.5;
                k.set(u, 72, v, rim ? m.haz : d < 2.5 ? m.plateLime : (int) d % 2 == 0 ? m.steel : m.plateYel);
                for (int y = 73; y <= 75; y++) k.air(u, y, v);
                if (rim && !(ang > 205 && ang < 290)) k.set(u, 73, v, m.bars);
            }
            // Mast mit Warnlicht
            k.fill(cu + 3, 73, cv + 3, cu + 3, 82, cv + 3, m.steel);
            k.set(cu + 3, 83, cv + 3, m.light2);
            k.set(cu + 3, 84, cv + 3, m.rod);
            k.set(cu + 3, 79, cv + 2, m.bars);
            k.set(cu + 2, 79, cv + 3, m.bars);
            // Laternen auf der Buehne
            k.set(cu - 4, 73, cv + 3, m.lantern);
            k.set(cu + 4, 73, cv - 3, m.lantern);
        }
    }

    // ═════════════════════════════════════════ Tanklager (Schluessel 2) ═════════════════════════════════════════

    private void tankFarm(Kit k, M m) {
        sphere(k, m, S1U, S1Y, S1V, S1R, true);
        sphere(k, m, 76, 16, -76, 9, false);
        sphere(k, m, 22, 15, -78, 8, false);
        // Aequator-Laufsteg um S1 (y 26)
        if (k.touches(S1U - 19, S1V - 19, S1U + 19, S1V + 19)) {
            for (int u = Math.max(S1U - 19, k.u0()); u <= Math.min(S1U + 19, k.u1()); u++) for (int v = Math.max(S1V - 19, k.v0()); v <= Math.min(S1V + 19, k.v1()); v++) {
                double d = Math.sqrt((u - S1U) * (u - S1U) + (v - S1V) * (v - S1V));
                if (d <= 15.2 || d > 18.5) continue;
                boolean rim = d > 17.5;
                k.set(u, S1Y, v, rim ? m.haz : m.steel);
                if (rim) { k.set(u, S1Y + 1, v, m.bars); k.set(u, S1Y - 1, v, m.lead); }
            }
        }
        // Treppenturm zum Kugeltank (Wendel um einen Kern)
        int tu = S1U, tv = -28;
        if (k.touches(tu - 6, tv - 6, tu + 6, tv + 6)) {
            k.cylinder(tu, tv, 1, S1Y + 1, 1.2, m.steel);
            spiralStair(k, m.steel, m.haz, m.bars, tu, tv, 1, S1Y, 1.5, 4.5, 1.5, 152.5, true, 0.9, S1Y - 1);
            for (int s = -1; s <= 1; s += 2) for (int t = -1; t <= 1; t += 2) k.fill(tu + 5 * s, 1, tv + 5 * t, tu + 5 * s, S1Y + 3, tv + 5 * t, m.steel);
            for (int y = 8; y <= S1Y; y += 9) {
                for (int s = -5; s <= 5; s++) {
                    k.set(tu + s, y + 3, tv - 5, m.steel); k.set(tu + s, y + 3, tv + 5, m.steel);
                    k.set(tu - 5, y + 3, tv + s, m.steel); k.set(tu + 5, y + 3, tv + s, m.steel);
                }
            }
            k.set(tu, S1Y + 2, tv, m.light);
            k.set(tu, S1Y + 3, tv, m.rod);
        }
        // Podest vom Turm zum Laufsteg
        k.fill(S1U - 2, S1Y, -36, S1U + 2, S1Y, -33, m.steel);
        k.fill(S1U - 2, S1Y + 1, -36, S1U + 2, S1Y + 3, -33, Kit.AIR);
        k.fill(S1U - 3, S1Y, -36, S1U - 3, S1Y, -33, m.haz);
        k.fill(S1U + 3, S1Y, -36, S1U + 3, S1Y, -33, m.haz);
        k.fill(S1U - 3, S1Y + 1, -35, S1U - 3, S1Y + 1, -33, m.bars);
        k.fill(S1U + 3, S1Y + 1, -35, S1U + 3, S1Y + 1, -33, m.bars);
        // Mannloch in der Huelle (Sueden)
        k.fill(S1U - 1, S1Y + 1, -41, S1U + 1, S1Y + 3, -36, Kit.AIR);
        k.fill(S1U - 2, S1Y + 4, -38, S1U + 2, S1Y + 4, -36, m.haz);
        k.set(S1U - 2, S1Y + 2, -37, m.light2);
        k.set(S1U + 2, S1Y + 2, -37, m.light2);
        // Parkour im Inneren (Innenstreben ueber der CCl-Bruehe)
        tankParkour(k, m);

        // Flachtanks
        flatTank(k, m, 80, -50, 7, 16, true);
        flatTank(k, m, 24, -52, 5, 12, false);
        flatTank(k, m, 40, -84, 4, 9, false);
        flatTank(k, m, 60, -84, 4, 9, false);
        // Rohre Tanklager -> Hauptbruecke
        k.line(S1U, 3, S1V + 13, S1U, 3, -21, 1.0, m.plateLime);
        k.line(22, 3, -70, 22, 3, -21, 0.6, m.lead);
        k.line(30, 2, -52, 7, 2, -52, 0.6, m.gpipeX);
        k.line(65, 3, -52, 73, 3, -52, 1.0, m.alu);
        k.line(76, 3, -67, 76, 3, -57, 1.0, m.alu);
    }

    /** Kugeltank auf Stelzen; {@code key}: hohl mit CCl-Bruehe bis y-1 unter der Mitte. */
    private void sphere(Kit k, M m, int cu, int cy, int cv, int r, boolean key) {
        int ext = r + 2;
        if (!k.touches(cu - ext, cv - ext, cu + ext, cv + ext)) return;
        double inner = r - 1.6;
        for (int u = Math.max(cu - r, k.u0()); u <= Math.min(cu + r, k.u1()); u++) for (int v = Math.max(cv - r, k.v0()); v <= Math.min(cv + r, k.v1()); v++) {
            int du = u - cu, dv = v - cv;
            for (int y = cy - r; y <= cy + r; y++) {
                int dy = y - cy;
                double d = Math.sqrt(du * du + dv * dv + dy * dy);
                if (d > r + 0.3) continue;
                if (d <= inner) {
                    if (key) k.set(u, y, v, y <= cy - 1 ? m.ccl : Kit.AIR);
                    continue;
                }
                BlockState s;
                if (Math.abs(dy) <= 1) s = m.lead;
                else if (dy > r - 3) s = m.alu;
                else if (Math.abs(dy) % 6 == 4) s = m.steel;
                else s = k.rnd(21, u, y, v) < 0.04 ? m.plateYel : m.plateLime;
                if (key && d <= inner + 1 && dy >= 4 && dy <= 8 && Math.floorMod(du * 3 + dv * 5, 11) == 0) s = m.light;
                k.set(u, y, v, s);
            }
        }
        // Stelzen mit Kreuzverband
        int legs = 8;
        double lr = r * 0.78;
        int[][] tops = new int[legs][];
        for (int i = 0; i < legs; i++) {
            double a = Math.PI * 2 * i / legs + Math.PI / 8;
            int lu = cu + (int) Math.round(Math.cos(a) * lr), lv = cv + (int) Math.round(Math.sin(a) * lr);
            double hd = Math.sqrt((lu - cu) * (lu - cu) + (lv - cv) * (lv - cv));
            int top = (int) Math.floor(cy - Math.sqrt(Math.max(0, r * r - hd * hd))) + 1;
            tops[i] = new int[] {lu, lv, top};
            k.fill(lu - 1, 1, lv - 1, lu + 1, 1, lv + 1, m.conc);
            k.fill(lu, 2, lv, lu, top, lv, m.steel);
            k.set(lu, 2, lv, m.haz);
        }
        for (int i = 0; i < legs; i++) {
            int[] a = tops[i], b = tops[(i + 1) % legs];
            int ya = 3, yb = Math.min(a[2], b[2]) - 2;
            if (yb - ya < 4) continue;
            k.line(a[0], ya, a[1], b[0], yb, b[1], 0.5, m.bars);
            k.line(b[0], ya, b[1], a[0], yb, a[1], 0.5, m.bars);
        }
        // Scheitel: Mannloch und Entlueftung
        k.set(cu, cy + r + 1, cv, m.steel);
        k.set(cu, cy + r + 2, cv, m.rimY);
        k.set(cu, cy + r + 3, cv, key ? m.light2 : m.endRod);
        if (key) {
            // Hangelampen innen
            for (int[] p : new int[][] {{0, -4}, {4, -8}, {-1, -12}}) {
                k.fill(cu + p[0], cy + 6, cv + p[1], cu + p[0], cy + r - 3, cv + p[1], m.chain);
                k.set(cu + p[0], cy + 5, cv + p[1], m.lanternH);
            }
        }
    }

    private void tankParkour(Kit k, M m) {
        int bot = S1Y - S1R + 1, top = S1Y;     // y 12 .. 26
        // Einstiegssims (Sueden) y 26
        k.fill(S1U - 2, top, -43, S1U + 2, top, -39, m.steel);
        k.fill(S1U - 2, top, -43, S1U + 2, top, -43, m.haz);
        for (int s = -2; s <= 2; s += 4) k.fill(S1U + s, bot, -42, S1U + s, top - 1, -42, m.steel);
        // Streben: {u, topY, v}
        int[][] st = {{50, 26, -47}, {48, 27, -49}, {56, 27, -56}, {54, 26, -58}, {52, 27, -60}};
        for (int[] p : st) {
            k.fill(p[0], bot, p[2], p[0], p[1] - 1, p[2], m.steel);
            k.set(p[0], p[1], p[2], m.haz);
        }
        // Querstrebe y 27 (v -53, u 46..54) auf zwei Stuetzen
        k.fill(46, 27, -53, 54, 27, -53, m.steel);
        k.set(50, 27, -53, m.light2);
        k.fill(46, bot, -53, 46, 26, -53, m.steel);
        k.fill(54, bot, -53, 54, 26, -53, m.steel);
        // Zierstreben unter der Oberflaeche (Fachwerk)
        k.line(46, 16, -53, 54, 16, -53, 0.5, m.rust);
        k.line(50, 14, -47, 52, 14, -60, 0.5, m.rust);
        // Schluesselbuehne (Norden) y 27
        k.fill(46, 27, -66, 54, 27, -64, m.steel);
        k.fill(46, 27, -64, 54, 27, -64, m.haz);
        for (int s = -3; s <= 3; s += 6) k.fill(S1U + s, bot + 1, -64, S1U + s, 26, -64, m.steel);
        // Lampen oben an der Ruecklehne der Buehne
        k.set(47, 30, -64, m.light);
        k.set(53, 30, -64, m.light);
    }

    private void flatTank(Kit k, M m, int cu, int cv, int r, int h, boolean stair) {
        int ext = r + 4;
        if (!k.touches(cu - ext, cv - ext, cu + ext, cv + ext)) return;
        for (int u = Math.max(cu - r - 1, k.u0()); u <= Math.min(cu + r + 1, k.u1()); u++) for (int v = Math.max(cv - r - 1, k.v0()); v <= Math.min(cv + r + 1, k.v1()); v++) {
            double d = Math.sqrt((u - cu) * (u - cu) + (v - cv) * (v - cv));
            if (d > r + 1.5) continue;
            k.set(u, 1, v, m.conc);
            if (d > r + 0.5) continue;
            for (int y = 2; y <= h; y++) {
                BlockState s = d > r - 0.5 ? (y % 5 == 0 ? m.lead : y == h ? m.haz : (u + y) % 7 == 0 ? m.lgrayC : m.alu) : m.alu;
                k.set(u, y, v, s);
            }
            k.set(u, h + 1, v, d > r - 0.5 ? m.haz : d < 1.5 ? m.rimY : m.steel);
            if (d > r - 0.5) k.set(u, h + 2, v, m.bars);
        }
        if (stair) spiralStair(k, m.steel, m.haz, m.bars, cu, cv, 1, h + 1, r + 1, r + 2.5, 2.0, 200, true, 0.9, h);
    }

    // ═════════════════════════════════════════ Rohrbruecken-Labyrinth (Schluessel 3) ═════════════════════════════════════════

    private void maze(Kit k, M m, Maze mz) {
        int u0 = MU - 3, u1 = MU + (NC - 1) * MS + 3, v0 = MV - 4, v1 = MV + (NR - 1) * MS + 3;
        if (k.touches(u0, v0, u1, v1)) {
            for (int u = Math.max(u0, k.u0()); u <= Math.min(u1, k.u1()); u++) for (int v = Math.max(v0, k.v0()); v <= Math.min(v1, k.v1()); v++) {
                int du = u - MU, dv = v - MV;
                int i = Math.floorDiv(du + 3, MS), j = Math.floorDiv(dv + 3, MS);
                int ou = du - i * MS, ov = dv - j * MS;
                boolean center = Math.abs(ou) <= 1 && Math.abs(ov) <= 1 && i >= 0 && i < NC && j >= 0 && j < NR;
                if (mz.deck(u, v)) {
                    boolean mid = ou == 0 && ov == 0 && center;
                    k.set(u, MY, v, mid ? m.light : center ? m.plateLime : m.steel);
                    // Rohre unter dem Steg
                    if (!center) {
                        boolean horiz = Math.abs(ov) <= 1 && Math.abs(ou) >= 2;
                        int off = horiz ? ov : ou;
                        if (off != 0) k.set(u, MY - 2, v, horiz ? (off < 0 ? m.gpipeX : m.pipeX) : (off < 0 ? m.gpipeZ : m.pipeZ));
                        else k.set(u, MY - 1, v, m.lead);
                    }
                } else {
                    boolean near = false;
                    for (int a = -1; a <= 1 && !near; a++) for (int b = -1; b <= 1 && !near; b++) if (mz.deck(u + a, v + b)) near = true;
                    if (near && v >= v0 && v <= MV + (NR - 1) * MS + 2) {
                        k.set(u, MY, v, m.haz);
                        k.set(u, MY + 1, v, m.wall);
                    }
                }
            }
            // Stuetzen unter jeder Zelle
            for (int i = 0; i < NC; i++) for (int j = 0; j < NR; j++) {
                int cu = MU + i * MS, cv = MV + j * MS;
                if (!k.touches(cu - 2, cv - 2, cu + 2, cv + 2)) continue;
                k.fill(cu - 1, 1, cv - 1, cu + 1, 1, cv + 1, m.conc);
                k.fill(cu, 2, cv, cu, MY - 1, cv, m.steel);
                k.fill(cu - 1, MY - 1, cv, cu + 1, MY - 1, cv, m.steel);
                k.fill(cu, MY - 1, cv - 1, cu, MY - 1, cv + 1, m.steel);
                k.set(cu, 2, cv, m.haz);
                if ((i + j) % 2 == 0) k.set(cu, MY - 2, cv, m.light);
            }
            // grosse Sammelleitungen unter dem Labyrinth (Rohrbruecke)
            for (int j = 0; j < NR; j += 2) {
                int cv = MV + j * MS + 3;
                k.line(u0, 14, cv, u1, 14, cv, 1.0, j % 4 == 0 ? m.plateLime : m.alu);
            }
            for (int i = 1; i < NC; i += 3) {
                int cu = MU + i * MS + 3;
                k.line(cu, 11, v0, cu, 11, v1, 1.0, m.lead);
            }
        }
        // Treppenturm zum Labyrinth (Suedwestecke)
        int tu = MU, tv = MV + (NR - 1) * MS + 8;
        if (k.touches(tu - 7, tv - 7, tu + 7, tv + 7)) {
            k.cylinder(tu, tv, 1, MY + 1, 1.2, m.steel);
            spiralStair(k, m.steel, m.haz, m.bars, tu, tv, 1, MY, 1.5, 4.5, 1.5, 222, true, 0.9, MY - 1);
            for (int s = -1; s <= 1; s += 2) for (int t = -1; t <= 1; t += 2) k.fill(tu + 5 * s, 1, tv + 5 * t, tu + 5 * s, MY + 4, tv + 5 * t, m.steel);
            for (int s = -5; s <= 5; s++) {
                k.set(tu + s, MY + 5, tv - 5, m.haz); k.set(tu + s, MY + 5, tv + 5, m.haz);
                k.set(tu - 5, MY + 5, tv + s, m.haz); k.set(tu + 5, MY + 5, tv + s, m.haz);
            }
            k.set(tu, MY + 2, tv, m.light);
            k.set(tu, MY + 3, tv, m.rod);
            k.set(tu + 5, MY + 6, tv + 5, m.light2);
        }
        // Waechterbuehne (Nordosten) y 20
        int pu0 = 72, pu1 = 86, pv0 = -30, pv1 = -15, gu = MU + (NC - 1) * MS;
        if (k.touches(pu0, pv0, pu1, pv1)) {
            for (int u = Math.max(pu0, k.u0()); u <= Math.min(pu1, k.u1()); u++) for (int v = Math.max(pv0, k.v0()); v <= Math.min(pv1, k.v1()); v++) {
                boolean edge = u == pu0 || u == pu1 || v == pv0 || v == pv1;
                boolean open = v == pv1 && Math.abs(u - gu) <= 1;
                double dc = Math.sqrt((u - 79) * (u - 79) + (v + 23) * (v + 23));
                if (edge && !open) {
                    k.set(u, MY, v, m.haz);
                    k.set(u, MY + 1, v, m.wall);
                } else {
                    k.set(u, MY, v, dc < 2.5 ? m.light : dc < 5.5 && dc > 4.5 ? m.plateLime : (u + v) % 2 == 0 ? m.steel : m.alu);
                }
                for (int y = MY + 2; y <= MY + 6; y++) k.air(u, y, v);
            }
            for (int[] c : new int[][] {{pu0, pv0}, {pu1, pv0}, {pu0, pv1}, {pu1, pv1}, {79, pv0}, {pu0, -23}, {pu1, -23}}) {
                k.fill(c[0], 1, c[1], c[0], MY - 1, c[1], m.steel);
                k.set(c[0], 1, c[1], m.conc);
            }
            // vier kleine Druckbehaelter an den Ecken
            for (int[] c : new int[][] {{74, -28}, {84, -28}, {74, -17}, {84, -17}}) {
                k.cylinder(c[0], c[1], MY + 1, MY + 5, 1.2, m.plateLime);
                k.set(c[0], MY + 6, c[1], m.rimY);
                k.set(c[0], MY + 3, c[1] + (c[1] < -20 ? 2 : -2), m.light2);
            }
            k.line(74, MY + 4, -28, 84, MY + 4, -28, 0.5, m.pipeX);
            k.line(74, MY + 4, -17, 84, MY + 4, -17, 0.5, m.pipeX);
        }
    }

    // ═════════════════════════════════════════ Kuehltuerme, Fackel ═════════════════════════════════════════

    private void coolingTower(Kit k, M m, int cu, int cv, double rBase, double rThroat, int yThroat, int h) {
        int ext = (int) Math.ceil(rBase) + 2;
        if (!k.touches(cu - ext, cv - ext, cu + ext, cv + ext)) return;
        double c = (yThroat - 1) / Math.sqrt((rBase / rThroat) * (rBase / rThroat) - 1);
        for (int u = Math.max(cu - ext, k.u0()); u <= Math.min(cu + ext, k.u1()); u++) for (int v = Math.max(cv - ext, k.v0()); v <= Math.min(cv + ext, k.v1()); v++) {
            double d = Math.sqrt((u - cu) * (u - cu) + (v - cv) * (v - cv));
            if (d < rBase - 0.5) k.set(u, 0, v, d < 2 ? m.light : (u + v) % 2 == 0 ? m.grayC : m.conc);     // Becken
            double ang = Math.toDegrees(Math.atan2(v - cv, u - cu));
            for (int y = 1; y <= h; y++) {
                double t = (y - yThroat) / c;
                double rr = rThroat * Math.sqrt(1 + t * t);
                if (Math.abs(d - rr) > 0.75) continue;
                if (y <= 5) {                                   // Stuetzenkranz
                    double a = ((ang + y * 4) % 24 + 24) % 24;
                    if (a > 6) continue;
                    k.set(u, y, v, m.conc);
                    continue;
                }
                BlockState s;
                if (y == 6 || y == h) s = m.lead;
                else if (y > h - 6 && k.noise(31, (int) (ang * 2), y, 3) < 0.5) s = m.grayC;
                else if (y % 8 == 0) s = m.smooth;
                else s = k.rnd(32, u, y, v) < 0.05 ? m.concCr : m.lgrayC;
                k.set(u, y, v, s);
            }
        }
    }

    private void flare(Kit k, M m) {
        int cu = -34, cv = 72;
        if (k.touches(cu - 18, cv - 18, cu + 18, cv + 18)) {
            k.cylinder(cu, cv, 1, 2, 3.2, m.conc);
            for (int y = 3; y <= 74; y++) k.tube(cu, cv, y, y, 2, 2, (y / 6) % 2 == 0 ? m.redC : m.whiteC, m.steel);
            // Wartungsbuehne y 66 und Steigleiter
            k.ladder(cu, 3, 65, cv + 3, Direction.NORTH);
            for (int u = cu - 5; u <= cu + 5; u++) for (int v = cv - 5; v <= cv + 5; v++) {
                double d = Math.sqrt((u - cu) * (u - cu) + (v - cv) * (v - cv));
                if (d <= 2.5 || d > 5.5) continue;
                k.set(u, 66, v, d > 4.5 ? m.haz : m.steel);
                if (d > 4.5) k.set(u, 67, v, m.bars);
            }
            k.ladder(cu, 66, cv + 3, Direction.NORTH);
            // Fackelkopf und Flamme
            k.cylinder(cu, cv, 75, 76, 2.6, m.steel);
            k.cylinder(cu, cv, 77, 77, 1.5, m.netherrack);
            k.ellipsoid(cu, 81, cv, 2.6, 4.5, 2.6, m.glassOrange);
            k.ellipsoid(cu, 83, cv, 1.6, 3.2, 1.6, m.glassYellow);
            k.ellipsoid(cu, 80, cv, 1.2, 2.5, 1.2, m.shroom);
            k.set(cu, 86, cv, m.glassYellow);
            for (int[] o : new int[][] {{2, 0}, {-2, 0}, {0, 2}, {0, -2}}) k.set(cu + o[0], 78, cv + o[1], m.glassRed);
            // Abspannseile
            for (int[] o : new int[][] {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}) {
                k.line(cu + o[0] * 2, 58, cv + o[1] * 2, cu + o[0] * 15, 2, cv + o[1] * 15, 0.5, m.chain);
                k.fill(cu + o[0] * 15, 1, cv + o[1] * 15, cu + o[0] * 15, 1, cv + o[1] * 15, m.conc);
            }
            // Abscheidetrommel liegend auf Saetteln
            k.line(cu - 14, 4, cv - 9, cu - 3, 4, cv - 9, 2.0, m.plateLime);
            k.line(cu - 15, 4, cv - 9, cu - 15, 4, cv - 9, 1.6, m.lead);
            k.line(cu - 2, 4, cv - 9, cu - 2, 4, cv - 9, 1.6, m.lead);
            for (int u = cu - 12; u <= cu - 4; u += 8) k.fill(u, 1, cv - 11, u, 2, cv - 7, m.conc);
            k.line(cu - 2, 4, cv - 9, cu, 4, cv - 3, 0.6, m.steel);
        }
    }

    // ═════════════════════════════════════════ Kontrollgebaeude, Stollen ═════════════════════════════════════════

    private void controlBuilding(Kit k, M m) {
        int u0 = -18, u1 = 18, v0 = 22, v1 = 58;
        if (!k.touches(u0 - 3, v0 - 1, u1 + 3, v1 + 4)) return;
        for (int u = Math.max(u0, k.u0()); u <= Math.min(u1, k.u1()); u++) for (int v = Math.max(v0, k.v0()); v <= Math.min(v1, k.v1()); v++) {
            boolean edge = u == u0 || u == u1 || v == v0 || v == v1;
            boolean corner = (u == u0 || u == u1) && (v == v0 || v == v1);
            int along = (u == u0 || u == u1) ? v : u;
            boolean pil = edge && Math.floorMod(along, 6) == 0;
            k.set(u, 0, v, v >= 37 ? ((u + v) % 2 == 0 ? m.vtile : m.vtileS) : m.smooth);
            for (int y = 1; y <= 14; y++) {
                BlockState s;
                if (edge) {
                    if (corner || pil) s = y == 14 ? m.haz : m.pillar;
                    else if (y == 1) s = m.haz;
                    else if (y == 7 || y == 14) s = m.steel;
                    else if ((y >= 3 && y <= 5 || y >= 9 && y <= 12) && v > v0) s = m.glassLime;
                    else s = y < 7 ? m.brick : m.smooth;
                } else if (y == 7) s = m.conc;                                 // Zwischendecke
                else if (y == 14) s = m.steel;                                 // Dach
                else if (v <= 35 && y < 7) s = m.conc;                         // Kern ueber dem Stollen
                else s = Kit.AIR;
                k.set(u, y, v, s);
            }
            if (edge) k.set(u, 15, v, Math.floorMod(along, 2) == 0 ? m.haz : m.steel);       // Brustwehr
        }
        // Lobby-Nordwand
        k.fill(u0 + 1, 1, 36, -5, 6, 36, m.brick);
        k.fill(5, 1, 36, u1 - 1, 6, 36, m.brick);
        for (int u = -15; u <= 15; u += 6) if (Math.abs(u) > 5) k.set(u, 4, 37, m.light);
        // Eingang Sued mit Vordach
        k.fill(-2, 1, v1, 2, 4, v1, Kit.AIR);
        k.fill(-4, 5, v1, 4, 5, v1 + 3, m.steel);
        k.fill(-4, 6, v1 + 3, 4, 6, v1 + 3, m.haz);
        k.set(-4, 4, v1 + 3, m.lanternH);
        k.set(4, 4, v1 + 3, m.lanternH);
        // Treppe zum Obergeschoss (Westseite, steigt nach Norden)
        for (int i = 0; i < 6; i++) {
            int v = 50 - i;
            k.fill(-16, 1, v, -14, i + 1, v, m.conc);
            k.fill(-16, i + 1, v, -14, i + 1, v, Kit.stairs(ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.NORTH, false));
        }
        k.fill(-16, 7, 43, -14, 7, 50, Kit.AIR);
        k.fill(-13, 8, 43, -13, 8, 50, m.bars);
        // Lobby-Einrichtung: Leitstand-Pulte
        for (int u = -8; u <= 8; u += 4) {
            k.set(u, 1, 46, Kit.stairs(ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.SOUTH, false));
            k.set(u, 1, 47, m.plateLime);
            k.set(u, 2, 47, m.glassGreen);
        }
        k.fill(-12, 6, 40, 12, 6, 40, m.chain);
        for (int u = -12; u <= 12; u += 6) k.set(u, 6, 50, m.light);
        for (int u = -12; u <= 12; u += 6) k.set(u, 6, 42, m.light);
        // Obergeschoss: Leitwarte
        for (int u = -12; u <= 12; u += 3) {
            k.set(u, 8, 24, m.plateLime);
            k.set(u, 9, 24, m.glassGreen);
        }
        for (int u = -15; u <= 15; u += 6) for (int v = 27; v <= 55; v += 7) k.set(u, 13, v, m.light);
        k.fill(4, 8, 30, 12, 8, 32, m.poly);
        k.fill(-12, 8, 52, -6, 9, 56, m.crate);
        // Dachaufbauten: Luefter, Antenne
        k.fill(-12, 15, 26, -6, 17, 32, m.alu);
        k.fill(-11, 18, 27, -7, 18, 31, m.fan);
        k.fill(6, 15, 44, 12, 16, 52, m.steel);
        k.fill(7, 17, 45, 11, 17, 51, m.vent);
        k.fill(14, 15, 26, 14, 30, 26, m.steel);
        k.set(14, 31, 26, m.light2);
        k.set(14, 32, 26, m.rod);
        k.fill(13, 25, 26, 15, 25, 26, m.bars);
    }

    private void tunnel(Kit k, M m) {
        if (!k.touches(-5, 18, 5, 36)) return;
        // Huelle (ausserhalb der Halle)
        for (int v = 22; v <= 35; v++) {
            int f = v - 35;
            k.fill(-4, f - 1, v, 4, Math.min(f + 6, 6), v, m.conc);
        }
        // Stufen und Lichtraum
        for (int v = 19; v <= 35; v++) {
            int f = v - 35;
            k.fill(-2, f, v, 2, f, v, v == 35 ? m.steel : Kit.stairs(ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.SOUTH, false));
            k.clear(-2, f + 1, v, 2, f + 5, v);
            if (v >= 22) {
                k.set(-3, f + 1, v, m.haz);
                k.set(3, f + 1, v, m.haz);
                if (v % 4 == 0) {
                    k.set(-3, f + 3, v, m.light);
                    k.set(3, f + 3, v, m.light);
                }
                if (v < 34) k.fill(-2, f + 6, v, 2, f + 6, v, v % 3 == 0 ? m.lead : m.steel);
            }
        }
    }

    // ═════════════════════════════════════════ Crackanlagen-Halle (Arena) ═════════════════════════════════════════

    private void arena(Kit k, M m) {
        int u0 = AU0 - 2, u1 = AU1 + 2, v0 = AV0 - 2, v1 = AV1 + 2;
        if (!k.touches(u0, v0 - 10, u1, v1)) return;
        for (int u = Math.max(u0, k.u0()); u <= Math.min(u1, k.u1()); u++) for (int v = Math.max(v0, k.v0()); v <= Math.min(v1, k.v1()); v++) {
            boolean in = u >= AU0 && u <= AU1 && v >= AV0 && v <= AV1;
            boolean side = u < AU0 || u > AU1;
            int along = side ? v : u;
            boolean pil = Math.floorMod(along, 7) == 0;
            double dc = Math.sqrt(u * u + (v + 6) * (v + 6));
            for (int y = AF - 2; y <= AC + 2; y++) {
                BlockState s;
                if (y <= AF) {
                    if (y < AF || !in) s = m.conc;
                    else if (dc < 3.5) s = dc < 1.5 ? m.light : m.plateLime;
                    else if (dc > 14.5 && dc < 16) s = m.haz;
                    else if (Math.floorMod(u, 8) == 0 || Math.floorMod(v, 8) == 0) s = m.steel;
                    else s = (u + v) % 2 == 0 ? m.vtile : m.vtileS;
                } else if (y >= AC) {
                    if (y > AC || !in) s = m.conc;
                    else s = Math.floorMod(u, 7) == 0 ? m.light : Math.floorMod(v, 6) == 0 ? m.steel : m.smooth;
                } else if (in) {
                    s = Kit.AIR;
                } else {
                    boolean inner = u == AU0 - 1 || u == AU1 + 1 || v == AV0 - 1 || v == AV1 + 1;
                    if (!inner) s = m.conc;
                    else if (pil) s = y == -20 ? m.light : m.steel;
                    else if (y == -22) s = m.lead;
                    else if (y <= AF + 6) s = k.rnd(41, u, y, v) < 0.1 ? m.brickCr : m.brick;
                    else if (y == AF + 7) s = m.haz;
                    else s = k.rnd(42, u, y, v) < 0.06 ? m.concCr : m.smooth;
                }
                k.set(u, y, v, s);
            }
        }
        // Deckentraeger (Querriegel) und Rohrbuendel
        for (int v = AV0 + 4; v <= AV1; v += 8) k.fill(AU0, AC - 1, v, AU1, AC - 1, v, m.steel);
        for (int u : new int[] {-14, -10, 10, 14}) k.line(u, AC - 3, AV0, u, AC - 3, AV1, 1.0, Math.abs(u) == 10 ? m.plateLime : m.lead);
        for (int u : new int[] {-12, 12}) k.fill(u, AC - 2, AV0, u, AC - 2, AV1, m.gpipeZ);
        k.line(AU0, AC - 3, -20, AU1, AC - 3, -20, 0.6, m.alu);
        for (int u = -24; u <= 24; u += 12) for (int v = -24; v <= 12; v += 12) {
            if (Math.abs(u) == 12) continue;
            k.fill(u, AC - 5, v, u, AC - 1, v, m.chain);
            k.set(u, AC - 6, v, m.lanternH);
        }
        // Reaktorbehaelter am Rand
        for (int s = -1; s <= 1; s += 2) for (int v : new int[] {-22, -8, 6}) reactor(k, m, s * 27, v, s);
        // halb eingelassener Cracker im Norden
        int cv = AV0 - 4;
        for (int y = AF + 1; y <= AC - 1; y++) {
            BlockState s = y % 5 == 0 ? m.lead : y >= -26 && y <= -22 ? m.glassLime : m.plateLime;
            k.cylinder(0, cv, y, y, 7, s);
        }
        k.cylinder(0, cv, AF + 1, AC - 1, 5.5, m.light);
        k.fill(-8, AF + 1, AV0 - 3, 8, AC - 1, AV0 - 2, m.conc);
        // Podest mit den Beutekisten
        k.fill(-8, AF + 1, -26, 8, AF + 1, -22, m.steel);
        k.fill(-8, AF + 1, -22, 8, AF + 1, -22, Kit.stairs(ModBlocks.CONCRETE_SMOOTH_STAIRS.get(), Direction.NORTH, false));
        k.set(-8, AF + 2, -26, m.lantern);
        k.set(8, AF + 2, -26, m.lantern);
        // Galerie (Sueden) und Freitreppen
        k.fill(-14, AF + 1, 12, 14, -17, AV1, m.brick);
        k.fill(-14, -17, 12, 14, -17, AV1, m.steel);
        k.fill(-14, -17, 12, 14, -17, 12, m.haz);
        k.fill(-14, -16, 12, 14, -16, 12, m.bars);
        for (int u = -12; u <= 12; u += 6) k.set(u, -20, 11, m.light);
        for (int s = -1; s <= 1; s += 2) {
            for (int i = 1; i <= 16; i++) {
                int u = s * (14 + i), top = -17 - i;
                k.fill(u, AF + 1, 12, u, top - 1, AV1, m.brick);
                k.fill(u, top, 13, u, top, AV1, Kit.stairs(ModBlocks.VINYL_TILE_STAIRS.get(), s < 0 ? Direction.EAST : Direction.WEST, false));
                k.set(u, top, 12, m.haz);
                k.set(u, top + 1, 12, m.bars);
                for (int y = top + 1; y <= AC - 1; y++) for (int v = 13; v <= AV1; v++) k.air(u, y, v);
            }
        }
    }

    private void reactor(Kit k, M m, int cu, int cv, int side) {
        if (!k.touches(cu - 6, cv - 6, cu + 6, cv + 6)) return;
        k.cylinder(cu, cv, AF + 1, AF + 2, 5, m.conc);
        for (int y = AF + 3; y <= -20; y++) {
            BlockState s = (y - AF) % 4 == 0 ? m.lead : m.steel;
            k.tube(cu, cv, y, y, 4, 1, s, m.light2);
        }
        k.ellipsoid(cu, -20, cv, 4, 4, 4, m.steel);
        k.ellipsoid(cu, -20, cv, 2.6, 2.6, 2.6, m.light2);
        k.fill(cu, -21, cv, cu, -20, cv, m.light2);
        // Schauglaeser zur Hallenmitte
        for (int y = -29; y <= -25; y++) {
            k.set(cu - side * 4, y, cv, m.glassLime);
            k.set(cu - side * 4, y, cv - 1, m.glassLime);
            k.set(cu - side * 4, y, cv + 1, m.glassLime);
        }
        // Steigrohr zur Decke
        k.fill(cu, -15, cv, cu, AC - 1, cv, m.pipeY);
        k.set(cu, -16, cv, m.rimY);
        k.fill(cu + side * 2, -19, cv, cu + side * 7, -19, cv, m.pipeX);
    }


    // ═════════════════════════════════════════ Waermetauscher, Pumpenhaus, Roehrenofen, Lagerhalle ═════════════════════════════════════════

    private void exchangers(Kit k, M m) {
        if (!k.touches(-88, -18, -22, 18)) return;
        int[] rows = {-3, 4, 11};
        for (int n = 0; n < rows.length; n++) {
            int v = rows[n];
            k.line(-82, 4, v, -52, 4, v, 2.0, n == 1 ? m.alu : m.plateLime);
            k.line(-83, 4, v, -83, 4, v, 1.6, m.lead);
            k.line(-51, 4, v, -51, 4, v, 1.6, m.lead);
            for (int u = -80; u <= -54; u += 9) {
                k.fill(u, 1, v - 2, u, 2, v + 2, m.conc);
                k.set(u, 7, v, m.lead);
            }
            k.fill(-58, 7, v, -58, 10, v, m.gpipeY);
            k.fill(-74, 7, v, -74, 10, v, m.pipeY);
        }
        k.fill(-58, 10, -16, -58, 10, 11, m.gpipeZ);
        k.fill(-74, 10, -16, -74, 10, 11, m.pipeZ);
        // Pumpenhaus
        int u0 = -44, u1 = -26, v0 = -4, v1 = 14;
        for (int u = Math.max(u0, k.u0()); u <= Math.min(u1, k.u1()); u++) for (int v = Math.max(v0, k.v0()); v <= Math.min(v1, k.v1()); v++) {
            boolean edge = u == u0 || u == u1 || v == v0 || v == v1;
            k.set(u, 0, v, m.smooth);
            for (int y = 1; y <= 9; y++) {
                BlockState s;
                if (y == 9) s = edge ? m.haz : m.steel;
                else if (!edge) s = Kit.AIR;
                else if ((u == u0 || u == u1) && (v == v0 || v == v1)) s = m.pillar;
                else if (y >= 4 && y <= 6 && Math.floorMod(u + v, 4) != 0) s = m.glassLime;
                else s = y == 1 ? m.haz : m.pvc;
                k.set(u, y, v, s);
            }
        }
        if (k.touches(u0, v0, u1, v1)) {
            k.clear(-37, 1, v1, -33, 4, v1);
            for (int u = -40; u <= -30; u += 5) {
                k.fill(u, 1, 2, u + 1, 2, 4, m.steel);
                k.set(u, 3, 3, m.lead);
                k.fill(u, 1, 5, u + 1, 1, 6, m.gpipeZ);
                k.fill(u, 3, 4, u, 8, 4, m.pipeY);
            }
            k.fill(-40, 8, 4, -30, 8, 4, m.pipeX);
            for (int u = -40; u <= -30; u += 5) k.set(u, 8, 10, m.light);
        }
    }

    private void furnace(Kit k, M m) {
        int u0 = -18, u1 = -4, v0 = -84, v1 = -60;
        if (!k.touches(u0 - 2, v0 - 2, u1 + 8, v1 + 2)) return;
        BlockState fb = Kit.b(ModBlocks.BRICK_FIRE.get());
        for (int u = Math.max(u0, k.u0()); u <= Math.min(u1, k.u1()); u++) for (int v = Math.max(v0, k.v0()); v <= Math.min(v1, k.v1()); v++) {
            boolean edgeU = u == u0 || u == u1, edgeV = v == v0 || v == v1;
            int top = 14 - Math.max(0, Math.abs(u - (u0 + u1) / 2) - 4);
            for (int y = 1; y <= top; y++) {
                BlockState s;
                if ((edgeU || edgeV) && (Math.floorMod(v, 4) == 0 || edgeU && edgeV)) s = m.steel;
                else if (y == top) s = m.lead;
                else if (y >= 3 && y <= 4 && edgeU && Math.floorMod(v, 4) == 2) s = m.shroom;
                else s = fb;
                k.set(u, y, v, s);
            }
        }
        // Konvektionsteil und Schornstein
        k.fill(-15, 15, -76, -7, 22, -68, m.steel);
        k.fill(-14, 23, -75, -8, 23, -69, m.lead);
        for (int y = 24; y <= 60; y++) k.tube(-11, -72, y, y, 2, 1, (y / 5) % 2 == 0 ? m.redC : m.whiteC, null);
        k.tube(-11, -72, 61, 61, 2.6, 2.6, m.steel, null);
        k.set(-11, 62, -72, m.light2);
        // Ofenleitungen zur Rohrbruecke
        k.line(-4, 6, -64, 1, 6, -64, 1.0, m.lead);
        k.line(-4, 6, -80, 1, 6, -80, 1.0, m.lead);
        // Brennerreihe am Boden
        for (int v = v0 + 2; v <= v1 - 2; v += 4) {
            k.set(u0 - 1, 1, v, m.steel);
            k.set(u0 - 1, 2, v, m.glassOrange);
        }
    }

    private void warehouse(Kit k, M m) {
        int u0 = 24, u1 = 62, v0 = 68, v1 = 86;
        if (!k.touches(u0, v0, u1, v1)) return;
        double cvv = (v0 + v1) / 2.0, half = (v1 - v0) / 2.0;
        for (int u = Math.max(u0, k.u0()); u <= Math.min(u1, k.u1()); u++) for (int v = Math.max(v0, k.v0()); v <= Math.min(v1, k.v1()); v++) {
            double t = (v - cvv) / (half + 0.5);
            int roof = 8 + (int) Math.round(6 * Math.sqrt(Math.max(0, 1 - t * t)));
            boolean endWall = u == u0 || u == u1, side = v == v0 || v == v1;
            boolean rib = Math.floorMod(u - u0, 6) == 0;
            k.set(u, 0, v, (u + v) % 2 == 0 ? m.vtile : m.conc);
            for (int y = 1; y <= roof; y++) {
                BlockState s;
                if (y == roof) s = rib ? m.steel : (Math.floorMod(v, 5) == 0 ? m.glassLime : m.alu);
                else if (side) s = rib ? m.pillar : y == 1 ? m.haz : y >= 4 && y <= 5 ? m.glassLime : m.vinyl;
                else if (endWall) s = rib || Math.floorMod(v, 6) == 2 ? m.steel : y == 1 ? m.haz : m.pvc;
                else s = Kit.AIR;
                k.set(u, y, v, s);
            }
        }
        // Rolltor West
        k.clear(u0, 1, 74, u0, 6, 80);
        k.fill(u0, 7, 73, u0, 7, 81, m.haz);
        // Regale mit Polymerballen
        for (int u = u0 + 4; u <= u1 - 4; u += 6) for (int v : new int[] {71, 83}) {
            k.fill(u - 1, 1, v - 1, u + 1, 1, v + 1, m.steel);
            k.fill(u - 1, 2, v - 1, u + 1, 3, v + 1, (u / 6) % 2 == 0 ? m.poly : m.rubber);
            k.fill(u - 1, 4, v - 1, u + 1, 4, v + 1, m.steel);
            if ((u + v) % 3 == 0) k.fill(u, 5, v, u, 5, v, m.crate);
        }
        for (int u = u0 + 3; u <= u1 - 3; u += 6) k.set(u, 12, 77, m.light);
    }

    // ═════════════════════════════════════════ Deko-Props ═════════════════════════════════════════

    private void props(Kit k, M m) {
        // Containerstapel an der Zufahrt
        int[][] crates = {{8, 70}, {9, 74}, {-9, 72}, {-10, 76}, {12, 80}, {-14, 82}};
        for (int[] c : crates) {
            if (!k.touches(c[0] - 2, c[1] - 2, c[0] + 2, c[1] + 2)) continue;
            k.fill(c[0] - 1, 1, c[1] - 1, c[0] + 1, 2, c[1] + 1, (c[0] + c[1]) % 3 == 0 ? m.crateW : m.crate);
            if ((c[0] * 7 + c[1]) % 2 == 0) k.fill(c[0], 3, c[1] - 1, c[0], 3, c[1], m.crate);
        }
        // Strassenlaternen
        for (int v = 66; v <= 86; v += 10) for (int s = -1; s <= 1; s += 2) {
            int u = s * 5;
            if (!k.touches(u - 1, v - 1, u + 1, v + 1)) continue;
            k.fill(u, 1, v, u, 5, v, m.steel);
            k.set(u - s, 5, v, m.steel);
            k.set(u - s, 4, v, m.light);
        }
        // Fassreihen beim Tanklager (Polymer-/Gummiballen)
        for (int u = 16; u <= 30; u += 2) {
            if (!k.touches(u, -36, u, -32)) continue;
            k.fill(u, 1, -36, u, 1 + (u % 4 == 0 ? 2 : 1), -36, (u % 6 == 0) ? m.rubber : m.poly);
            k.set(u, 1, -33, m.bakelite);
        }
        // Fackel-Laterne am Strassenkreuz
        if (k.touches(-24, 58, -20, 66)) {
            k.fill(-22, 1, 58, -22, 7, 58, m.steel);
            k.set(-22, 8, 58, m.light2);
        }
    }

    // ═════════════════════════════════════════ Plan ═════════════════════════════════════════

    private void plan(Kit k, Maze mz) {
        // Schluessel
        k.keyChest(-60, 73, -56, Direction.SOUTH);                 // 1: Kolonne C1 oben
        k.keyChest(S1U, 28, -65, Direction.SOUTH);                 // 2: im Kugeltank
        k.elite(79, MY + 1, -23, OOZE);                            // 3: Waechter am Ende des Labyrinths
        // Boss
        k.boss(0, AF + 1, -6);
        k.bossChest(-4, AF + 2, -25, Direction.SOUTH);
        k.bossChest(4, AF + 2, -25, Direction.SOUTH);
        // Beute
        k.loot(10, 8, 26, Direction.SOUTH);
        k.barrel(-15, 1, 55);
        k.loot(-34, 25, -56, Direction.EAST);
        k.barrel(66, S1Y + 1, -52);
        k.loot(-62, 1, 36, Direction.SOUTH);
        k.loot(-42, 1, 58, Direction.NORTH);
        k.barrel(83, 18, -50);
        k.loot(56, 1, 77, Direction.WEST);
        List<int[]> de = mz.dead;
        if (de.size() > 0) k.barrel(de.get(0)[0], MY + 1, de.get(0)[1]);
        if (de.size() > 1) k.barrel(de.get(1)[0], MY + 1, de.get(1)[1]);
        // Spawner
        k.spawner(-28, 25, -63, DRONE);
        k.spawner(34, S1Y + 1, -52, OOZE);
        k.spawner(-58, 1, 36, OOZE);
        k.spawner(14, 1, 54, OOZE);
        k.spawner(84, MY + 1, -28, DRONE);
        if (de.size() > 2) k.spawner(de.get(2)[0], MY + 1, de.get(2)[1], DRONE);
        if (de.size() > 3) k.spawner(de.get(3)[0], MY + 1, de.get(3)[1], OOZE);
        // Bewohner
        k.mob(6, 1, 76, OOZE);
        k.mob(-8, 1, 66, OOZE);
        k.mob(10, 1, 63, OOZE);
        k.mob(-10, 1, 46, OOZE);
        k.mob(10, 1, 44, OOZE);
        k.mob(-35, 13, -24, DRONE);
        k.mob(-60, 37, -31, DRONE);
        k.mob(-44, 25, -24, DRONE);
        k.mob(40, 1, -30, OOZE);
        k.mob(64, 1, -34, OOZE);
        k.mob(S1U, S1Y + 1, -69, OOZE);
        k.mob(75, MY + 1, -26, DRONE);
        k.mob(83, MY + 1, -20, DRONE);
        k.mob(-66, 1, 38, OOZE);
        k.mob(-30, 1, 60, OOZE);
        k.mob(40, 1, 77, OOZE);
        k.mob(-35, 1, 9, OOZE);
        if (de.size() > 4) k.mob(de.get(4)[0], MY + 1, de.get(4)[1], DRONE);
        if (de.size() > 5) k.mob(de.get(5)[0], MY + 1, de.get(5)[1], OOZE);
    }
}
