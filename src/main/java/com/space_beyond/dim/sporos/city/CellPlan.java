package com.space_beyond.dim.sporos.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

import com.space_beyond.dim.sporos.city.Building.Kind;

import net.minecraft.core.Direction;

/**
 * Deterministische Planung einer Rasterzelle (Kreuzung, zwei Strassenstuecke, Grundstueck): Grundstuecksart,
 * Gebaeude, Autowracks, umgestuerzte Hyphe-Staemme, Laternen, Ampeln, Baeume, Schutthaufen, Teich/Brunnen,
 * Behaelter mit Loot.
 * Landmarken (Kontrollpunkt, Lager, Lazarett, Nest, Konvoi) werden hier geplant und von {@link CityLandmarks} gemalt.
 * Haengt nur von Stadtseed und Zellkoordinaten ab ({@code java.util.Random} mit Zellseed); jedes Objekt liegt
 * vollstaendig in der eigenen Zelle.
 */
public final class CellPlan {

    /**
     * Grundstuecksart. Landmarken (eigene Erweiterung, Nutzerwunsch "Strukturen mit Loot-Kisten"): {@code CHECKPOINT}
     * Quarantaene-Kontrollpunkt am Stadtrand auf der Hauptstrasse, {@code CAMP} Ueberlebenden-Lager, {@code TRIAGE}
     * Feldlazarett im Park, {@code NEST} Infizierten-Nest (Pilzhuegel mit Spawner). Gemalt von {@link CityLandmarks}.
     */
    public enum LotType { TOWER, APARTMENT, SHOP, PARKING, WAREHOUSE, PARK, VACANT, WILD, CHECKPOINT, CAMP, TRIAGE, NEST }

    /** Behaelterart einer Beutestelle. */
    public enum Container { CHEST, BARREL, SUPPLY_CRATE, MILITARY_CRATE }

    /**
     * Zelt: {@code x,z} Ecke mit den kleinsten Koordinaten, First entlang x ({@code alongX}) oder z, Breite {@code width}
     * (ungerade: 5 oder 9), Laenge {@code len}; {@code openHigh}: der Eingang liegt am Ende mit der groesseren Koordinate.
     * {@code kind}: Plane 0 gruen, 1 braun, 2 weiss (Lazarett), 3 hellgrau. {@code y} = Bodenniveau.
     */
    public record Tent(int x, int z, boolean alongX, int width, int len, boolean openHigh, int kind, int y) { }

    /** Sandsack-Stellung: Reihe ab {@code x,z} der Laenge {@code len} entlang x oder z, {@code y} = Bodenniveau. */
    public record Sandbags(int x, int z, boolean alongX, int len, int y) { }

    /** Infizierten-Spawner (Nest). */
    public record Spawner(int x, int y, int z) { }

    /** Bewohner einer Landmarke (Fuesse bei x, y, z), Art nach {@code structure.StructureMobs}. */
    public record MobSpot(int x, int y, int z, int kind) { }

    /** Liegengebliebener Armee-Konvoi auf dem Strassenstueck: {@code north} Nordsegment (sonst West), ab {@code along}. */
    public record Convoy(boolean north, int along) { }

    public record Car(int x, int z, boolean alongX, int len, int color, boolean burnt, int y) { }

    /** Umgestuerzter Hyphe-Stamm (Totholz) auf der Strasse, {@code y} = Strassenniveau + 1. */
    public record Log(int x, int z, boolean alongX, int len, int y) { }

    public record Lamp(int x, int z, Direction toStreet, int state) { }

    /** Baum: {@code kind} 0 Hyphe-Schirmbaum, 1 Hyphe-Totholz, 2 Hyphe mit Rankenvorhang, 3 Riesenpilz-Gruppe. */
    public record Tree(int x, int z, int y, int height, int radius, int kind) { }

    public record Rubble(int x, int z, int radius, int height, int y) { }

    public record LootSpot(int x, int y, int z, Direction facing, Container container, String table) {
        public boolean barrel() {
            return container == Container.BARREL;
        }
    }

    public record Pond(int x, int z, int rx, int rz) { }

    public record Ruin(int x0, int z0, int x1, int z1, int height) { }

    public final CityLayout layout;
    public final int gx, gz, minX, minZ;
    public final boolean wet;
    /** Rand-Faktor Grundstuecksmitte und weitester Grundstueckspunkt. */
    public final double edge, edgeMax;
    public final double decay, veg;
    public final LotType type;
    @Nullable public final Building building;
    public final List<Car> cars = new ArrayList<>();
    public final List<Log> logs = new ArrayList<>();
    public final List<Lamp> lamps = new ArrayList<>();
    public final List<Lamp> trafficLights = new ArrayList<>();
    public final List<Tree> trees = new ArrayList<>();
    public final List<Rubble> rubble = new ArrayList<>();
    public final List<LootSpot> loot = new ArrayList<>();
    public final List<Tent> tents = new ArrayList<>();
    public final List<Sandbags> sandbags = new ArrayList<>();
    public final List<MobSpot> mobs = new ArrayList<>();
    @Nullable public final Pond pond;
    @Nullable public final Ruin ruin;
    public final boolean plaza;
    public final boolean pharmacy;
    /** Kontrollpunkt: Richtung des kontrollierten Strassenstuecks (WEST = Nord-Sued-Hauptstrasse, NORTH = Ost-West), sonst null. */
    @Nullable public final Direction checkpoint;
    @Nullable public final Spawner spawner;
    @Nullable public final Convoy convoy;
    /** Radius des Pilzhuegels ({@code NEST}), sonst 0. */
    public final int nestRadius;

    CellPlan(CityLayout layout, int gx, int gz, boolean wet) {
        this.layout = layout;
        this.gx = gx;
        this.gz = gz;
        this.minX = layout.cellMinX(gx);
        this.minZ = layout.cellMinZ(gz);
        this.wet = wet;
        Random rand = new Random(CityRandom.mix(layout.seed ^ 0xCE11L, gx, gz));

        int lotMin = CityLayout.STREET, lotMax = CityLayout.CELL - 1;
        this.edge = layout.edge(minX + (lotMin + lotMax) / 2, minZ + (lotMin + lotMax) / 2);
        double em = 0;
        for (int cx : new int[] {lotMin, lotMax}) for (int cz : new int[] {lotMin, lotMax}) em = Math.max(em, layout.edge(minX + cx, minZ + cz));
        this.edgeMax = em;

        double cluster = CityRandom.noise(layout.seed ^ 0xDECA7L, gx, gz, 3);
        this.decay = CityRandom.clamp01(0.12 + 0.6 * edge + (cluster - 0.5) * 0.7);
        double cluster2 = CityRandom.noise(layout.seed ^ 0x6EE7L, gx, gz, 2);
        this.veg = CityRandom.clamp01(0.2 + 0.75 * edge + (cluster2 - 0.5) * 0.5);

        LotType t = edgeMax >= CityLayout.URBAN ? LotType.WILD : pickType(rand, edge);
        // Landmarken: Kontrollpunkt auf der Hauptstrasse (Westsegment der Spalte gx = 0 bzw. Nordsegment der Zeile gz = 0)
        // im aeusseren Stadtring, sonst Lager/Lazarett/Nest als seltener Ersatz passender Grundstuecke
        Direction cp = null;
        if (!wet && t != LotType.WILD && edge > 0.5 && edge < CityLayout.URBAN - 0.06) {
            if (gx == 0 && gz != 0 && rand.nextFloat() < 0.75F) cp = Direction.WEST;
            else if (gz == 0 && gx != 0 && rand.nextFloat() < 0.75F) cp = Direction.NORTH;
        }
        if (cp != null) t = LotType.CHECKPOINT;
        else if (!wet && t != LotType.WILD) {
            float r = rand.nextFloat();
            if ((t == LotType.VACANT || t == LotType.PARK) && edge > 0.3 && r < 0.14F) t = LotType.CAMP;
            else if (t == LotType.PARK && edge < 0.5 && r < 0.2F) t = LotType.TRIAGE;
            else if ((t == LotType.VACANT || t == LotType.WAREHOUSE || t == LotType.PARKING) && r < 0.08F + decay * 0.1F) t = LotType.NEST;
        }
        this.type = t;
        this.checkpoint = cp;
        this.plaza = type == LotType.PARK && edge < 0.4 && rand.nextFloat() < 0.6F;
        this.pharmacy = type == LotType.SHOP && rand.nextFloat() < 0.2F;

        int base = layout.baseY;
        int ground = groundY();
        int lx0 = minX + CityLayout.LOT_MIN, lz0 = minZ + CityLayout.LOT_MIN;
        int lx1 = minX + CityLayout.LOT_MAX, lz1 = minZ + CityLayout.LOT_MAX;

        // ── Gebaeude ────────────────────────────────────────────────────────
        Building b = null;
        switch (type) {
            case TOWER -> {
                int w = 14 + rand.nextInt(7), d = 14 + rand.nextInt(7);
                double central = Math.pow(1 - Math.min(1, edge / CityLayout.URBAN), 0.8);
                int floors = 8 + (int) Math.round(17 * central * (0.45 + 0.55 * rand.nextDouble()));
                if (edge > 0.5) floors = Math.min(floors, 14);
                b = place(Kind.TOWER, rand, w, d, Math.max(8, Math.min(25, floors)));
            }
            case APARTMENT -> {
                boolean longX = rand.nextBoolean();
                int w = longX ? 18 + rand.nextInt(3) : 11 + rand.nextInt(4);
                int d = longX ? 11 + rand.nextInt(4) : 18 + rand.nextInt(3);
                b = place(Kind.APARTMENT, rand, w, d, 3 + rand.nextInt(4));
            }
            case SHOP -> b = place(Kind.SHOP, rand, 12 + rand.nextInt(9), 12 + rand.nextInt(9), 1 + rand.nextInt(2));
            case PARKING -> b = place(Kind.PARKING, rand, 18 + rand.nextInt(3), 18 + rand.nextInt(3), 3 + rand.nextInt(3));
            case WAREHOUSE -> b = place(Kind.WAREHOUSE, rand, 16 + rand.nextInt(5), 16 + rand.nextInt(5), 1);
            default -> { }
        }
        this.building = b;

        // ── Strassenmoebel (nur im bebauten Bereich) ────────────────────────
        boolean streetWest = layout.edge(minX + 4, minZ + 20) < CityLayout.URBAN - 0.03;
        boolean streetNorth = layout.edge(minX + 20, minZ + 4) < CityLayout.URBAN - 0.03;
        double carChance = 0.15 + 0.4 * (1 - edge);
        Convoy cv = null;
        if (!wet && edge > 0.2 && edge < 0.75 && (streetWest || streetNorth) && rand.nextFloat() < 0.035F) {
            boolean north = streetNorth && (!streetWest || rand.nextBoolean());
            cv = new Convoy(north, CityLayout.STREET + 2 + rand.nextInt(CityLayout.CELL - CityLayout.STREET - 16));
            planConvoy(cv, rand, ground);
        }
        this.convoy = cv;
        if (streetWest && (cv == null || cv.north()) && rand.nextDouble() < carChance) cars.add(car(rand, false, ground));
        if (streetNorth && (cv == null || !cv.north()) && rand.nextDouble() < carChance) cars.add(car(rand, true, ground));
        if (streetWest && streetNorth && edge < 0.5 && rand.nextDouble() < 0.12) {
            // quer auf der Kreuzung stehengebliebener Wagen
            cars.add(new Car(minX + 2, minZ + 1 + rand.nextInt(2), true, 4, rand.nextInt(10), rand.nextFloat() < 0.4F, ground + 1));
        }
        // umgestuerzte Staemme quer ueber die Strasse (Verfall/Bewuchs), nicht in gefluteten Zellen
        if (!wet) {
            double logChance = 0.05 + 0.3 * decay * veg;
            if (streetWest && rand.nextDouble() < logChance) logs.add(fallenLog(rand, false, ground));
            if (streetNorth && rand.nextDouble() < logChance) logs.add(fallenLog(rand, true, ground));
        }

        if (!wet) {
            for (int along : new int[] {13, 25}) {
                if (streetWest && rand.nextFloat() < 0.75F) lamps.add(new Lamp(minX + CityLayout.STREET + 1, minZ + along, Direction.WEST, lampState(rand)));
                if (streetNorth && rand.nextFloat() < 0.75F) lamps.add(new Lamp(minX + along, minZ + CityLayout.STREET + 1, Direction.NORTH, lampState(rand)));
            }
            if (streetWest && streetNorth && edge < 0.55 && rand.nextFloat() < 0.6F) {
                trafficLights.add(new Lamp(minX + CityLayout.STREET + 1, minZ + CityLayout.STREET + 1, rand.nextBoolean() ? Direction.WEST : Direction.NORTH,
                        rand.nextFloat() < decay * 0.5 ? 1 : 0));
            }
        }

        // ── Baeume ──────────────────────────────────────────────────────────
        if (streetWest && rand.nextDouble() < 0.15 + 0.45 * veg) trees.add(tree(rand, minX + CityLayout.STREET + 1, minZ + 19, ground, 2));
        if (streetNorth && rand.nextDouble() < 0.15 + 0.45 * veg) trees.add(tree(rand, minX + 19, minZ + CityLayout.STREET + 1, ground, 2));
        if (streetWest && streetNorth && rand.nextDouble() < decay * decay * 0.6) trees.add(tree(rand, minX + 3 + rand.nextInt(2), minZ + 3 + rand.nextInt(2), ground, 3));
        if (streetWest && rand.nextDouble() < decay * 0.35) trees.add(tree(rand, minX + 3 + rand.nextInt(2), minZ + 12 + rand.nextInt(14), ground, 3));

        Pond pd = null;
        Ruin rn = null;
        switch (type) {
            case PARK -> {
                int n = plaza ? 2 + rand.nextInt(3) : 3 + rand.nextInt(4);
                for (int i = 0; i < n; i++) trees.add(tree(rand, lx0 + 3 + rand.nextInt(14), lz0 + 3 + rand.nextInt(14), ground, 2 + rand.nextInt(2)));
                if (!plaza && rand.nextFloat() < 0.3F) pd = new Pond(lx0 + 6 + rand.nextInt(8), lz0 + 6 + rand.nextInt(8), 2 + rand.nextInt(2), 2 + rand.nextInt(2));
            }
            case VACANT -> {
                if (rand.nextFloat() < 0.6F) {
                    int rx0 = lx0 + 1 + rand.nextInt(4), rz0 = lz0 + 1 + rand.nextInt(4);
                    rn = new Ruin(rx0, rz0, rx0 + 8 + rand.nextInt(6), rz0 + 8 + rand.nextInt(6), 1 + rand.nextInt(3));
                }
                for (int i = rand.nextInt(4); i > 0; i--) rubble.add(new Rubble(lx0 + 3 + rand.nextInt(14), lz0 + 3 + rand.nextInt(14), 2 + rand.nextInt(2), 1 + rand.nextInt(3), ground));
                if (rand.nextFloat() < 0.4F) pd = new Pond(lx0 + 5 + rand.nextInt(10), lz0 + 5 + rand.nextInt(10), 2 + rand.nextInt(3), 2 + rand.nextInt(3));
                for (int i = 1 + rand.nextInt(3); i > 0; i--) trees.add(tree(rand, lx0 + 3 + rand.nextInt(14), lz0 + 3 + rand.nextInt(14), ground, 2 + rand.nextInt(2)));
                if (rand.nextFloat() < 0.25F) loot.add(new LootSpot(lx0 + 4 + rand.nextInt(12), ground + 1, lz0 + 4 + rand.nextInt(12), Direction.from2DDataValue(rand.nextInt(4)),
                        Container.CHEST, rand.nextBoolean() ? "residential" : "industrial"));
            }
            default -> { }
        }
        this.pond = pd;
        this.ruin = rn;

        // Landmarken
        Spawner sp = null;
        int nr = 0;
        switch (type) {
            case CHECKPOINT -> planCheckpoint(rand, ground);
            case CAMP -> planCamp(rand, ground);
            case TRIAGE -> planTriage(rand, ground);
            case NEST -> {
                nr = 5 + rand.nextInt(3);
                int cx = (lx0 + lx1) / 2, cz = (lz0 + lz1) / 2;
                sp = new Spawner(cx, ground + 1, cz);
                loot.add(new LootSpot(cx + nr - 3, ground + 1, cz + 1, Direction.WEST, Container.CHEST, "nest"));
                if (rand.nextBoolean()) loot.add(new LootSpot(cx, ground + 1, cz - (nr - 3), Direction.SOUTH, Container.CHEST, "nest"));
                // Bewohner: Lauerer und Sporlinge um den Huegel, drinnen manchmal ein Pilzkoloss
                for (int i = 0; i < 3; i++) {
                    double a = rand.nextDouble() * Math.PI * 2;
                    mobs.add(new MobSpot(cx + (int) Math.round(Math.cos(a) * (nr + 2)), ground + 1, cz + (int) Math.round(Math.sin(a) * (nr + 2)),
                            rand.nextFloat() < 0.6F ? M_LURKER : M_SPORELING));
                }
                if (rand.nextFloat() < 0.3F) mobs.add(new MobSpot(cx - 2, ground + 1, cz, M_MYCOBRUTE));
                else mobs.add(new MobSpot(cx - 2, ground + 1, cz, M_BLINDCAP));
            }
            default -> { }
        }
        this.spawner = sp;
        this.nestRadius = nr;

        if (b != null) planBuildingExtras(b, rand, base);
    }

    // ── Landmarken (Planung; Malerei in CityLandmarks) ───────────────────────

    /** Mob-Arten wie {@code structure.StructureMobs} (hier als Zahl, damit die Planung ohne Registry auskommt). */
    static final int M_LURKER = 0, M_SPORELING = 1, M_BLINDCAP = 2, M_MYCOBRUTE = 3;

    /** Grundstueckskoordinate: beim Kontrollpunkt am Nordsegment werden lokale x/z vertauscht (Anlage zeigt zur Strasse). */
    private int lotX(int lx, int lz) {
        return minX + (checkpoint == Direction.NORTH ? lz : lx);
    }

    private int lotZ(int lx, int lz) {
        return minZ + (checkpoint == Direction.NORTH ? lx : lz);
    }

    /**
     * Kontrollpunkt (lokale Planung fuer das Westsegment, bei NORTH transponiert): Sperre quer ueber die Strasse mit
     * Sandsack-Stellungen, Wachturm an der Strassenecke, ummauertes Lager mit Zelten, Armee-Lkw, Kisten.
     */
    private void planCheckpoint(Random rand, int ground) {
        boolean t = checkpoint == Direction.NORTH;
        // Sandsaecke an der Strassensperre (Strasse lokal x 0-7, Sperre bei z 18, s. CityLandmarks)
        sandbags.add(new Sandbags(lotX(0, 15), lotZ(0, 15), t, 2, ground));
        sandbags.add(new Sandbags(lotX(6, 15), lotZ(6, 15), t, 2, ground));
        sandbags.add(new Sandbags(lotX(1, 21), lotZ(1, 21), !t, 3, ground));
        // Lkw quer in der Luecke der Sperre (ausgebrannt) oder im Lager
        if (rand.nextFloat() < 0.5F) cars.add(new Car(lotX(2, 17), lotZ(2, 17), t, 5, 10, true, ground + 1));
        cars.add(new Car(lotX(22, 22), lotZ(22, 22), !t, 5, 10, rand.nextFloat() < 0.3F, ground + 1));
        // Zelte (Eingang zur Lagermitte)
        tents.add(new Tent(lotX(15, 12), lotZ(15, 12), t, 5, 5, true, rand.nextBoolean() ? 0 : 3, ground));
        tents.add(new Tent(lotX(23, 12), lotZ(23, 12), t, 5, 5, true, 0, ground));
        if (rand.nextBoolean()) tents.add(new Tent(lotX(24, 25), lotZ(24, 25), !t, 5, 4, false, 1, ground));
        // Kisten: Depot in der Suedwestecke des Lagers, Fass und Kiste neben den Zelten
        for (int i = 0; i < 3 + rand.nextInt(3); i++) {
            int lx = 12 + (i % 3) * 2, lz = 24 + (i / 3) * 2;
            loot.add(new LootSpot(lotX(lx, lz), ground + 1, lotZ(lx, lz), Direction.from2DDataValue(rand.nextInt(4)),
                    rand.nextFloat() < 0.75F ? Container.MILITARY_CRATE : Container.SUPPLY_CRATE, "checkpoint"));
        }
        loot.add(new LootSpot(lotX(21, 13), ground + 1, lotZ(21, 13), Direction.SOUTH, Container.BARREL, rand.nextFloat() < 0.5F ? "military" : "checkpoint"));
        loot.add(new LootSpot(lotX(16, 18), ground + 1, lotZ(16, 18), Direction.EAST, Container.MILITARY_CRATE, "checkpoint"));
        // Kiste auf der Turmplattform (Plattform bei ground + 5, s. CityLandmarks.tower)
        loot.add(new LootSpot(lotX(12, 18), ground + 6, lotZ(12, 18), Direction.NORTH, Container.MILITARY_CRATE, "checkpoint"));
        // Sandsaecke am Tor und am Turm
        sandbags.add(new Sandbags(lotX(12, 19), lotZ(12, 19), !t, 3, ground));
        sandbags.add(new Sandbags(lotX(13, 14), lotZ(13, 14), t, 2, ground));
        // Bewohner: der Kontrollpunkt ist gefallen - Sporlinge und Blindhuete im Lager und an der Sperre
        mobs.add(new MobSpot(lotX(19, 20), ground + 1, lotZ(19, 20), rand.nextBoolean() ? M_SPORELING : M_BLINDCAP));
        mobs.add(new MobSpot(lotX(4, 20), ground + 1, lotZ(4, 20), M_SPORELING));
        if (rand.nextBoolean()) mobs.add(new MobSpot(lotX(25, 18), ground + 1, lotZ(25, 18), M_BLINDCAP));
    }

    /** Ueberlebenden-Lager: Lagerfeuer in der Mitte, Zelte ringsum, Sitzstaemme, Kisten, Barrikaden (CityLandmarks). */
    private void planCamp(Random rand, int ground) {
        int cx = minX + (CityLayout.LOT_MIN + CityLayout.LOT_MAX) / 2, cz = minZ + (CityLayout.LOT_MIN + CityLayout.LOT_MAX) / 2;
        int n = 2 + rand.nextInt(3);
        Direction[] dirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        int start = rand.nextInt(4);
        for (int i = 0; i < n; i++) {
            Direction d = dirs[(start + i) % 4];
            int kind = rand.nextFloat() < 0.6F ? 0 : rand.nextFloat() < 0.6F ? 1 : 3;
            int len = 4 + rand.nextInt(2);
            // Zelt auf der Seite d, Eingang zur Mitte
            Tent tent = switch (d) {
                case NORTH -> new Tent(cx - 2, cz - 4 - len, false, 5, len, true, kind, ground);
                case SOUTH -> new Tent(cx - 2, cz + 5, false, 5, len, false, kind, ground);
                case WEST -> new Tent(cx - 4 - len, cz - 2, true, 5, len, true, kind, ground);
                default -> new Tent(cx + 5, cz - 2, true, 5, len, false, kind, ground);
            };
            tents.add(tent);
            // Kiste neben dem Zelteingang
            if (rand.nextFloat() < 0.7F) {
                int kx = d.getAxis() == Direction.Axis.Z ? cx + 3 : cx + d.getStepX() * 4;
                int kz = d.getAxis() == Direction.Axis.Z ? cz + d.getStepZ() * 4 : cz + 3;
                loot.add(new LootSpot(kx, ground + 1, kz, d.getOpposite(), rand.nextFloat() < 0.7F ? Container.SUPPLY_CRATE : Container.CHEST, "camp"));
            }
        }
        // Sitzstaemme ums Feuer
        logs.add(new Log(cx - 1, cz - 2, true, 3, ground + 1));
        if (rand.nextBoolean()) logs.add(new Log(cx + 2, cz - 1, false, 3, ground + 1));
        loot.add(new LootSpot(cx - 3, ground + 1, cz + 2, Direction.EAST, Container.BARREL, "camp"));
        // Bewohner: das Lager wurde ueberrannt - meist ein bis zwei Lauerer am Feuer
        for (int i = rand.nextFloat() < 0.75F ? 1 + rand.nextInt(2) : 0; i > 0; i--) mobs.add(new MobSpot(cx - 2 + rand.nextInt(5), ground + 1, cz + 2, M_LURKER));
        // Sandsaecke/Barrikade an der Strassenseite
        sandbags.add(new Sandbags(minX + CityLayout.LOT_MIN, minZ + CityLayout.LOT_MIN + 4 + rand.nextInt(8), false, 3 + rand.nextInt(3), ground));
    }

    /** Feldlazarett: grosses weisses Zelt in der Parkmitte, Pritschen, Medizin-Kisten, Krankenwagen davor. */
    private void planTriage(Random rand, int ground) {
        boolean alongX = rand.nextBoolean();
        int lx0 = minX + CityLayout.LOT_MIN, lz0 = minZ + CityLayout.LOT_MIN;
        int len = 11 + rand.nextInt(2);
        Tent tent = alongX ? new Tent(lx0 + 3, lz0 + 5, true, 9, len, rand.nextBoolean(), 2, ground)
                : new Tent(lx0 + 5, lz0 + 3, false, 9, len, rand.nextBoolean(), 2, ground);
        tents.add(tent);
        // Kisten entlang der Innenwaende (im Zelt: Breite 9 -> Innenraum 1..7)
        for (int i = 0; i < 3 + rand.nextInt(3); i++) {
            int along = 2 + rand.nextInt(len - 4);
            int across = rand.nextBoolean() ? 2 : 6;
            int x = alongX ? tent.x() + along : tent.x() + across;
            int z = alongX ? tent.z() + across : tent.z() + along;
            loot.add(new LootSpot(x, ground + 1, z, alongX ? (across == 2 ? Direction.SOUTH : Direction.NORTH) : (across == 2 ? Direction.EAST : Direction.WEST),
                    rand.nextFloat() < 0.6F ? Container.SUPPLY_CRATE : Container.CHEST, "triage"));
        }
        loot.add(new LootSpot(alongX ? tent.x() + len / 2 : tent.x() + 4, ground + 1, alongX ? tent.z() + 4 : tent.z() + len / 2,
                Direction.NORTH, Container.BARREL, "triage"));
        // Krankenwagen (weisser Lieferwagen) neben dem Zelt, Sandsaecke am Weg
        int vx = alongX ? lx0 + 4 + rand.nextInt(8) : lx0 + 16, vz = alongX ? lz0 + 16 : lz0 + 4 + rand.nextInt(8);
        cars.add(new Car(vx, vz, alongX, 5, 2, false, ground + 1));
        sandbags.add(new Sandbags(lx0, lz0 + 8, false, 4, ground));
        // Bewohner: Blindhuete zwischen den Pritschen, ein Sporling davor
        int mid = len / 2;
        mobs.add(new MobSpot(alongX ? tent.x() + mid - 2 : tent.x() + 4, ground + 1, alongX ? tent.z() + 4 : tent.z() + mid - 2, M_BLINDCAP));
        if (rand.nextBoolean()) mobs.add(new MobSpot(alongX ? tent.x() + mid + 2 : tent.x() + 4, ground + 1, alongX ? tent.z() + 4 : tent.z() + mid + 2, M_BLINDCAP));
        mobs.add(new MobSpot(lx0 + 2, ground + 1, lz0 + 2, M_SPORELING));
    }

    /** Konvoi: zwei olivgruene Lkw versetzt auf dem Strassenstueck, Kisten und Sandsaecke dazwischen. */
    private void planConvoy(Convoy cv, Random rand, int ground) {
        boolean north = cv.north();
        int a = cv.along();
        // (across, along) -> Welt
        java.util.function.BiFunction<Integer, Integer, int[]> at = (across, along) ->
                north ? new int[] {minX + along, minZ + across} : new int[] {minX + across, minZ + along};
        int[] v1 = at.apply(1, a), v2 = at.apply(5, a + 8);
        cars.add(new Car(v1[0], v1[1], north, 5, 10, rand.nextFloat() < 0.4F, ground + 1));
        cars.add(new Car(v2[0], v2[1], north, 5, 10, rand.nextFloat() < 0.4F, ground + 1));
        int n = 3 + rand.nextInt(3);
        for (int i = 0; i < n; i++) {
            int across = rand.nextBoolean() ? 3 + rand.nextInt(2) : (i % 2 == 0 ? 6 + rand.nextInt(2) : rand.nextInt(2));
            int along = a + rand.nextInt(13);
            int[] q = at.apply(across, along);
            loot.add(new LootSpot(q[0], ground + 1, q[1], Direction.from2DDataValue(rand.nextInt(4)),
                    rand.nextFloat() < 0.7F ? Container.MILITARY_CRATE : Container.SUPPLY_CRATE, "convoy"));
        }
        int[] s1 = at.apply(6, a + 5);
        sandbags.add(new Sandbags(s1[0], s1[1], !north, 2, ground));
        // Bewohner: ein bis zwei Sporlinge zwischen den Lkw
        for (int i = 1 + rand.nextInt(2); i > 0; i--) {
            int[] m = at.apply(3 + rand.nextInt(2), a + 5 + rand.nextInt(4));
            mobs.add(new MobSpot(m[0], ground + 1, m[1], M_SPORELING));
        }
    }

    /** Strassen-/Grundstuecksniveau dieser Zelle (geflutete Zellen 2 Bloecke tiefer, Wasser bis {@code baseY}). */
    public int groundY() {
        return wet ? layout.baseY - 2 : layout.baseY;
    }

    private static LotType pickType(Random rand, double e) {
        int[] w; // TOWER, APARTMENT, SHOP, PARKING, WAREHOUSE, PARK, VACANT
        if (e < 0.3) w = new int[] {45, 12, 15, 8, 0, 8, 6};
        else if (e < 0.55) w = new int[] {14, 30, 20, 7, 7, 10, 12};
        else w = new int[] {2, 22, 10, 2, 22, 14, 28};
        int sum = 0;
        for (int v : w) sum += v;
        int r = rand.nextInt(sum);
        LotType[] t = {LotType.TOWER, LotType.APARTMENT, LotType.SHOP, LotType.PARKING, LotType.WAREHOUSE, LotType.PARK, LotType.VACANT};
        for (int i = 0; i < w.length; i++) {
            r -= w[i];
            if (r < 0) return t[i];
        }
        return LotType.VACANT;
    }

    private Building place(Kind kind, Random rand, int w, int d, int floors) {
        int span = CityLayout.LOT_MAX - CityLayout.LOT_MIN + 1; // 20
        w = Math.min(w, span);
        d = Math.min(d, span);
        int x0 = minX + CityLayout.LOT_MIN + rand.nextInt(span - w + 1);
        int z0 = minZ + CityLayout.LOT_MIN + rand.nextInt(span - d + 1);
        return new Building(kind, layout, x0, z0, x0 + w - 1, z0 + d - 1, floors, decay, edge, rand);
    }

    private Car car(Random rand, boolean alongX, int ground) {
        int len = rand.nextFloat() < 0.15F ? 5 : 4;
        int lane = rand.nextBoolean() ? 1 : 5;
        int along = CityLayout.STREET + 1 + rand.nextInt(CityLayout.CELL - CityLayout.STREET - len - 1);
        int x = alongX ? minX + along : minX + lane;
        int z = alongX ? minZ + lane : minZ + along;
        return new Car(x, z, alongX, len, rand.nextInt(10), rand.nextFloat() < 0.2F + decay * 0.3F, ground + 1);
    }

    /** Liegender Stamm quer zur Fahrtrichtung (3-6 Bloecke), ganz im Strassenstueck der Zelle. */
    private Log fallenLog(Random rand, boolean northSegment, int ground) {
        int len = 3 + rand.nextInt(4);
        int across = 1 + rand.nextInt(Math.max(1, CityLayout.STREET - len)); // quer ueber die Strasse (Breite 8)
        int along = CityLayout.STREET + 1 + rand.nextInt(CityLayout.CELL - CityLayout.STREET - 2);
        // Westsegment (x 0-7): Stamm liegt in x-Richtung; Nordsegment (z 0-7): in z-Richtung
        int x = northSegment ? minX + along : minX + across;
        int z = northSegment ? minZ + across : minZ + along;
        return new Log(x, z, !northSegment, len, ground + 1);
    }

    private int lampState(Random rand) {
        float r = rand.nextFloat();
        if (r < decay * 0.25F) return 2;
        if (r < decay * 0.5F) return 1;
        return 0;
    }

    private Tree tree(Random rand, int x, int z, int ground, int radius) {
        int kind = rand.nextInt(10);
        kind = kind < 4 ? 0 : kind < 7 ? 2 : kind < 9 ? 1 : 3;
        return new Tree(x, z, ground + 1, 5 + rand.nextInt(4) + radius, radius, kind);
    }

    private void planBuildingExtras(Building b, Random rand, int base) {
        // Behaelter je Stockwerk
        double perFloor = switch (b.kind) {
            case TOWER -> 0.45;
            case APARTMENT -> 0.6;
            case SHOP -> 1.0;
            case PARKING -> 0.35;
            case WAREHOUSE -> 1.0;
        };
        int floorsWithStuff = b.kind == Kind.WAREHOUSE ? 1 : b.floors;
        for (int f = 0; f < floorsWithStuff; f++) {
            int count = b.kind == Kind.WAREHOUSE ? 2 + rand.nextInt(3) : b.kind == Kind.SHOP && f == 0 ? 1 + rand.nextInt(3) : rand.nextDouble() < perFloor ? 1 : 0;
            for (int i = 0; i < count; i++) {
                LootSpot s = wallSpot(b, rand, b.floorY(f) + 1);
                if (s != null) loot.add(s);
            }
        }
        if (b.basement && rand.nextFloat() < 0.5F) {
            int y = b.basementFloor() + 1;
            if (b.floodTop < y) {
                LootSpot s = wallSpot(b, rand, y);
                if (s != null) {
                    String table = rand.nextFloat() < 0.35F ? "military" : "medical";
                    loot.add(new LootSpot(s.x(), s.y(), s.z(), s.facing(), table.equals("military") ? Container.MILITARY_CRATE : s.container(), table));
                }
            }
        }

        // Schutt am eingestuerzten Eck (aussen auf dem Gehweg, innen im Erdgeschoss)
        if (b.collapse) {
            int ox = b.ccx == b.x0 ? -2 : 2, oz = b.ccz == b.z0 ? -2 : 2;
            int r = 2 + rand.nextInt(2);
            rubble.add(new Rubble(clampLocal(minX, b.ccx + ox, r), clampLocal(minZ, b.ccz + oz, r), r, 1 + rand.nextInt(3), groundY()));
            rubble.add(new Rubble(b.ccx - ox, b.ccz - oz, 2, 1 + rand.nextInt(2), base));
            if (decay > 0.45 && rand.nextFloat() < 0.6F) {
                int tx = b.ccx - Integer.signum(ox) * 3, tz = b.ccz - Integer.signum(oz) * 3;
                trees.add(new Tree(tx, tz, base + 1, 7 + rand.nextInt(6), 2 + rand.nextInt(2), rand.nextBoolean() ? 2 : 1));
            }
        }
        if (decay > 0.5 && rand.nextFloat() < 0.4F) {
            rubble.add(new Rubble(b.x0 + 2 + rand.nextInt(Math.max(1, b.x1 - b.x0 - 4)), b.z0 + 2 + rand.nextInt(Math.max(1, b.z1 - b.z0 - 4)), 2, 1, base));
        }

        // Parkhaus: Wracks auf den Decks
        if (b.kind == Kind.PARKING) {
            for (int f = 0; f < b.floors; f++) {
                for (int i = rand.nextInt(4); i > 0; i--) {
                    int cx = b.x0 + 5 + rand.nextInt(Math.max(1, b.x1 - b.x0 - 9));
                    int cz = b.z0 + 2 + rand.nextInt(Math.max(1, b.z1 - b.z0 - 4));
                    cars.add(new Car(cx, cz, true, 4, rand.nextInt(10), rand.nextFloat() < 0.25F, b.floorY(f) + 1));
                }
            }
        }
    }

    private static int clampLocal(int cellMin, int v, int r) {
        return Math.max(cellMin + r, Math.min(cellMin + CityLayout.CELL - 1 - r, v));
    }

    /** Behaelter innen an einer Aussenwand (nicht vor Treppenhaus, Tuer oder Zwischenwand). */
    @Nullable
    private LootSpot wallSpot(Building b, Random rand, int y) {
        for (int tries = 0; tries < 6; tries++) {
            Direction side = Direction.from2DDataValue(rand.nextInt(4));
            int x, z;
            int lenX = b.x1 - b.x0, lenZ = b.z1 - b.z0;
            switch (side) {
                case WEST -> { x = b.x0 + 1; z = b.z0 + 2 + rand.nextInt(Math.max(1, lenZ - 3)); }
                case EAST -> { x = b.x1 - 1; z = b.z0 + 2 + rand.nextInt(Math.max(1, lenZ - 3)); }
                case NORTH -> { z = b.z0 + 1; x = b.x0 + 2 + rand.nextInt(Math.max(1, lenX - 3)); }
                default -> { z = b.z1 - 1; x = b.x0 + 2 + rand.nextInt(Math.max(1, lenX - 3)); }
            }
            if (b.nearShaft(x, z)) continue;
            if (Math.abs(x - b.midX) <= 1 || Math.abs(z - b.midZ) <= 1) continue;
            boolean barrel = b.kind == Kind.WAREHOUSE || b.kind == Kind.PARKING || (b.kind == Kind.SHOP && rand.nextBoolean());
            String table = table(b.kind, rand);
            Container container = table.equals("military") ? Container.MILITARY_CRATE
                    : barrel && b.kind == Kind.WAREHOUSE && rand.nextFloat() < 0.4F ? Container.SUPPLY_CRATE
                    : barrel ? Container.BARREL : Container.CHEST;
            return new LootSpot(x, y, z, side.getOpposite(), container, table);
        }
        return null;
    }

    private String table(Kind kind, Random rand) {
        float r = rand.nextFloat();
        return switch (kind) {
            case TOWER -> r < 0.05F ? "military" : r < 0.2F ? "medical" : r < 0.3F ? "residential" : "office";
            case APARTMENT -> r < 0.04F ? "military" : r < 0.2F ? "medical" : "residential";
            case SHOP -> pharmacy ? (r < 0.8F ? "medical" : "shop") : r < 0.08F ? "medical" : "shop";
            case WAREHOUSE -> r < 0.12F ? "military" : r < 0.2F ? "shop" : "industrial";
            case PARKING -> r < 0.05F ? "military" : r < 0.15F ? "industrial" : "parking";
        };
    }
}
