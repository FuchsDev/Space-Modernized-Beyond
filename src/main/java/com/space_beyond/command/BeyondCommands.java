package com.space_beyond.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.space_m.celestial.CelestialBody;
import com.space_m.celestial.SolarSystem;
import com.space_beyond.celestial.trait.CBT_Spores;
import com.space_m.dim.SpaceDimensions;
import com.space_beyond.dim.sporos.city.CityLocator;
import com.space_beyond.dim.sporos.city.SporosCities;
import com.space_beyond.dim.sporos.spores.SporeInfection;
import com.space_m.travel.CelestialTeleporter;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Unterbefehle von Beyond unter {@code /space} (Berechtigung 2). Brigadier fuehrt sie mit dem {@code /space}-Knoten von
 * Space Modernized zusammen ({@code destroystar} bleibt dort).
 * <ul>
 *   <li>{@code /space tp <koerper>}: Teleporter wie {@code wand_d} (Ziele: {@code orbit}, alle Koerper des
 *       {@code SolarSystem.Body}-Enums inkl. Sporos/Wemburos und Thatmo), Spieler bleibt bei x/z, landet auf dem Boden
 *       ({@link CelestialTeleporter}, Orbit: y 128).</li>
 *   <li>{@code /space locate_city [tp|inside]}: Testhilfe der Sporos-Staedte;
 *       {@code /space locate_landmark <checkpoint|camp|triage|nest|convoy> [tp]}: naechste Landmarke der naechsten Stadt;
 *       {@code /space locate_structure <radio_tower|lodge|hospital|power_plant|settlement|any> [tp]}: naechste Wildnis-Struktur;
 *       {@code /space locate_wemburos <throne|vault|portal|any> [tp]}: naechste Struktur auf Wemburos.</li>
 *   <li>{@code /space locate_dungeon <planet> [tp]}: naechster Boss-Dungeon; {@code /space progression <0-11>}: Zahl
 *       besiegter Stufen-Bosse setzen (Planeten-Fortschritt).</li>
 *   <li>{@code /space spores <dichte> [virulenz]}: Sporen-Trait der aktuellen Welt setzen (0 = sporenfrei),
 *       {@code /space infection <0..1>}: eigenen Infektionsfortschritt setzen.</li>
 * </ul>
 */
public final class BeyondCommands {
    private BeyondCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("space")
                .requires(s -> s.hasPermission(2))
                .then(Commands.literal("tp")
                        .then(Commands.argument("body", StringArgumentType.word())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(targets(), b))
                                .executes(c -> teleport(c.getSource(), StringArgumentType.getString(c, "body")))))
                .then(Commands.literal("locate_city")
                        .executes(c -> locateCity(c.getSource(), 0))
                        .then(Commands.literal("tp").executes(c -> locateCity(c.getSource(), 1)))
                        .then(Commands.literal("inside").executes(c -> locateCity(c.getSource(), 2))))
                .then(Commands.literal("locate_landmark")
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(LANDMARKS, b))
                                .executes(c -> locateLandmark(c.getSource(), StringArgumentType.getString(c, "type"), false))
                                .then(Commands.literal("tp").executes(c -> locateLandmark(c.getSource(), StringArgumentType.getString(c, "type"), true)))))
                .then(Commands.literal("locate_structure")
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(STRUCTURES, b))
                                .executes(c -> locateStructure(c.getSource(), StringArgumentType.getString(c, "type"), false))
                                .then(Commands.literal("tp").executes(c -> locateStructure(c.getSource(), StringArgumentType.getString(c, "type"), true)))))
                .then(Commands.literal("locate_wemburos")
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(WEMBUROS_STRUCTURES, b))
                                .executes(c -> locateWemburos(c.getSource(), StringArgumentType.getString(c, "type"), false))
                                .then(Commands.literal("tp").executes(c -> locateWemburos(c.getSource(), StringArgumentType.getString(c, "type"), true)))))
                .then(Commands.literal("locate_dungeon")
                        .then(Commands.argument("planet", StringArgumentType.word())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(com.space_beyond.dim.dungeon.PlanetDungeons.designs().keySet(), b))
                                .executes(c -> locateDungeon(c.getSource(), StringArgumentType.getString(c, "planet"), false))
                                .then(Commands.literal("tp").executes(c -> locateDungeon(c.getSource(), StringArgumentType.getString(c, "planet"), true)))))
                .then(Commands.literal("progression")
                        .then(Commands.argument("defeated", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0, 11))
                                .executes(c -> setProgression(c.getSource(), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(c, "defeated")))))
                .then(Commands.literal("spores")
                        .then(Commands.argument("density", FloatArgumentType.floatArg(0F, 1F))
                                .executes(c -> setSpores(c.getSource(), FloatArgumentType.getFloat(c, "density"), -1F))
                                .then(Commands.argument("virulence", FloatArgumentType.floatArg(0F, 1F))
                                        .executes(c -> setSpores(c.getSource(), FloatArgumentType.getFloat(c, "density"),
                                                FloatArgumentType.getFloat(c, "virulence"))))))
                .then(Commands.literal("infection")
                        .then(Commands.argument("progress", FloatArgumentType.floatArg(0F, 1F))
                                .executes(c -> setInfection(c.getSource(), FloatArgumentType.getFloat(c, "progress"))))));
    }

    /** Sporen-Trait der Welt des Aufrufers setzen (Runde 4); Virulenz &lt; 0 = bisherige/Standard behalten. */
    private static int setSpores(CommandSourceStack source, float density, float virulence) {
        ServerLevel level = source.getLevel();
        if (CelestialBody.inOrbit(level)) {
            source.sendFailure(Component.literal("Not available in orbit"));
            return 0;
        }
        CBT_Spores current = CelestialBody.getTrait(level, CBT_Spores.class);
        float v = virulence >= 0F ? virulence : current != null ? current.virulence : CBT_Spores.DEFAULT_VIRULENCE;
        int strain = current != null ? current.strain : CBT_Spores.DEFAULT_STRAIN;
        CelestialBody.modifyTraits(level, new CBT_Spores(density, v, strain));
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "Spores on %s: density %.2f, virulence %.2f",
                CelestialBody.getBody(level).name, density, v)), true);
        return 1;
    }

    /** Eigenen Infektionsfortschritt setzen (Runde 4, Testhilfe). */
    private static int setInfection(CommandSourceStack source, float progress) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        SporeInfection.apply(player, progress);
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "Spore infection %.2f (stage %d)", progress, SporeInfection.stage(progress))), true);
        return 1;
    }

    /**
     * Testhilfe Sporos-Staedte: naechste Stadt (von der Spielerposition, ausserhalb Sporos ab x/z) melden;
     * {@code tp} = auf eine freie Strassenstelle nahe der Stadtmitte, {@code inside} = in ein Stockwerk des naechsten
     * Hochhauses/Wohnblocks. Teleportiert bei Bedarf nach Sporos.
     */
    private static int locateCity(CommandSourceStack source, int mode) throws CommandSyntaxException {
        Vec3 at = source.getPosition();
        long seed = com.space_m.dim.CelestialSeed.get();
        SporosCities.City city = CityLocator.nearest(seed, (int) at.x, (int) at.z, 4);
        if (city == null) {
            source.sendFailure(Component.literal("No city within 4 districts"));
            return 0;
        }
        int base = CityLocator.baseY(city);
        double dist = Math.hypot(city.originX() - at.x, city.originZ() - at.z);
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "Sporos city at %d %d %d (radius %d chunks), %.0f blocks away",
                city.originX(), base + 1, city.originZ(), city.radiusChunks(), dist)), false);
        if (mode == 0) return 1;

        BlockPos target = mode == 2 ? CityLocator.inside(city) : CityLocator.street(city);
        if (target == null) {
            source.sendFailure(Component.literal("No walkable building floor found, using the street"));
            target = CityLocator.street(city);
        }
        ServerPlayer player = source.getPlayerOrException();
        double x = target.getX() + 0.5, y = target.getY(), z = target.getZ() + 0.5;
        if (player.level().dimension() == com.space_beyond.dim.BeyondDimensions.SPOROS) {
            player.teleportTo(x, y, z);
        } else {
            CelestialTeleporter.teleport(player, com.space_beyond.dim.BeyondDimensions.SPOROS, x, y, z, false);
        }
        BlockPos t = target;
        source.sendSuccess(() -> Component.literal("Teleported to " + t.toShortString()), true);
        return 1;
    }

    private static final List<String> LANDMARKS = List.of("checkpoint", "camp", "triage", "nest", "convoy");

    /** {@code /space locate_landmark <art> [tp]}: naechste Landmarke der naechsten Stadt (Stelle davor), optional hinteleportieren. */
    private static int locateLandmark(CommandSourceStack source, String type, boolean tp) throws CommandSyntaxException {
        Vec3 at = source.getPosition();
        long seed = com.space_m.dim.CelestialSeed.get();
        SporosCities.City city = CityLocator.nearest(seed, (int) at.x, (int) at.z, 4);
        if (city == null) {
            source.sendFailure(Component.literal("No city within 4 districts"));
            return 0;
        }
        com.space_beyond.dim.sporos.city.CellPlan.LotType lot = switch (type) {
            case "checkpoint" -> com.space_beyond.dim.sporos.city.CellPlan.LotType.CHECKPOINT;
            case "camp" -> com.space_beyond.dim.sporos.city.CellPlan.LotType.CAMP;
            case "triage" -> com.space_beyond.dim.sporos.city.CellPlan.LotType.TRIAGE;
            case "nest" -> com.space_beyond.dim.sporos.city.CellPlan.LotType.NEST;
            case "convoy" -> null;
            default -> throw new SimpleCommandExceptionType(Component.literal("Unknown landmark, use: " + String.join(", ", LANDMARKS))).create();
        };
        BlockPos target = CityLocator.landmark(city, lot, (int) at.x, (int) at.z);
        if (target == null) {
            source.sendFailure(Component.literal("The nearest city (" + city.originX() + " " + city.originZ() + ") has no " + type));
            return 0;
        }
        double dist = Math.hypot(target.getX() - at.x, target.getZ() - at.z);
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "Nearest %s at %s, %.0f blocks away", type, target.toShortString(), dist)), false);
        if (!tp) return 1;
        ServerPlayer player = source.getPlayerOrException();
        double x = target.getX() + 0.5, y = target.getY(), z = target.getZ() + 0.5;
        if (player.level().dimension() == com.space_beyond.dim.BeyondDimensions.SPOROS) {
            player.teleportTo(x, y, z);
        } else {
            CelestialTeleporter.teleport(player, com.space_beyond.dim.BeyondDimensions.SPOROS, x, y, z, false);
        }
        source.sendSuccess(() -> Component.literal("Teleported to " + target.toShortString()), true);
        return 1;
    }

    private static final List<String> STRUCTURES = List.of("any", "radio_tower", "lodge", "hospital", "power_plant", "settlement");

    /** {@code /space locate_structure <art|any> [tp]}: naechste Wildnis-Struktur (Stelle davor), optional hinteleportieren. */
    private static int locateStructure(CommandSourceStack source, String type, boolean tp) throws CommandSyntaxException {
        Vec3 at = source.getPosition();
        com.space_beyond.dim.sporos.structure.SporosStructures.Type st = com.space_beyond.dim.sporos.structure.SporosStructures.Type.byName(type);
        if (st == null && !type.equalsIgnoreCase("any")) {
            throw new SimpleCommandExceptionType(Component.literal("Unknown structure, use: " + String.join(", ", STRUCTURES))).create();
        }
        com.space_m.dim.CelestialWorld world = CityLocator.world();
        com.space_beyond.dim.sporos.structure.SporosStructures.Structure s = com.space_beyond.dim.sporos.structure.SporosStructures.nearest(world, st, (int) at.x, (int) at.z, 6);
        if (s == null) {
            source.sendFailure(Component.literal("No " + type + " structure within 6 districts"));
            return 0;
        }
        com.space_beyond.dim.sporos.structure.StructurePlan plan = com.space_beyond.dim.sporos.structure.SporosStructures.plan(world, s);
        int lv = s.type().radius + 2;
        BlockPos target = new BlockPos(com.space_beyond.dim.sporos.structure.Frame.worldX(s, 0, lv), plan.base + 1,
                com.space_beyond.dim.sporos.structure.Frame.worldZ(s, 0, lv));
        double dist = Math.hypot(s.x() - at.x, s.z() - at.z);
        String name = s.type().name().toLowerCase(Locale.ROOT);
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "Nearest %s at %d %d %d (rotation %d), %.0f blocks away", name,
                s.x(), plan.base + 1, s.z(), s.rotation() * 90, dist)), false);
        if (!tp) return 1;
        ServerPlayer player = source.getPlayerOrException();
        double x = target.getX() + 0.5, y = target.getY(), z = target.getZ() + 0.5;
        if (player.level().dimension() == com.space_beyond.dim.BeyondDimensions.SPOROS) {
            player.teleportTo(x, y, z);
        } else {
            CelestialTeleporter.teleport(player, com.space_beyond.dim.BeyondDimensions.SPOROS, x, y, z, false);
        }
        source.sendSuccess(() -> Component.literal("Teleported to " + target.toShortString()), true);
        return 1;
    }

    /** {@code /space locate_dungeon <planet> [tp]}: naechster Boss-Dungeon des Planeten (Stelle vor dem Eingang). */
    private static int locateDungeon(CommandSourceStack source, String planet, boolean tp) throws CommandSyntaxException {
        Vec3 at = source.getPosition();
        if (com.space_beyond.dim.dungeon.PlanetDungeons.design(planet) == null) {
            throw new SimpleCommandExceptionType(Component.literal("No dungeon on " + planet + ", use: "
                    + String.join(", ", com.space_beyond.dim.dungeon.PlanetDungeons.designs().keySet()))).create();
        }
        com.space_m.dim.CelestialWorld world = com.space_m.dim.CelestialGenerators.state(planet).chunkManager;
        var d = com.space_beyond.dim.dungeon.PlanetDungeons.nearest(planet, world, (int) at.x, (int) at.z, 6);
        if (d == null) {
            source.sendFailure(Component.literal("No dungeon within 6 districts"));
            return 0;
        }
        var plan = com.space_beyond.dim.dungeon.PlanetDungeons.plan(world, d);
        BlockPos target = com.space_beyond.dim.dungeon.PlanetDungeons.worldPos(plan, 0, 1, 36);
        double dist = Math.hypot(d.x() - at.x, d.z() - at.z);
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "Nearest %s dungeon at %d %d %d, %.0f blocks away", planet, d.x(), plan.base + 1, d.z(), dist)), false);
        if (!tp) return 1;
        ServerPlayer player = source.getPlayerOrException();
        CelestialBody planetBody = CelestialBody.getBodyOrNull(planet);
        ResourceKey<Level> dim = planetBody != null && planetBody.dimension != null ? planetBody.dimension : SpaceDimensions.level(planet);
        double x = target.getX() + 0.5, y = target.getY(), z = target.getZ() + 0.5;
        if (player.level().dimension() == dim) {
            player.teleportTo(x, y, z);
        } else {
            CelestialTeleporter.teleport(player, dim, x, y, z, false);
        }
        source.sendSuccess(() -> Component.literal("Teleported to " + target.toShortString()), true);
        return 1;
    }

    /** {@code /space progression <0-11>}: Anzahl besiegter Stufen-Bosse setzen (Stationsfreigabe, Tests). */
    private static int setProgression(CommandSourceStack source, int defeated) {
        com.space_beyond.progression.SpaceProgression.Data data = com.space_beyond.progression.SpaceProgression.Data.get(source.getServer());
        data.defeated = defeated;
        data.setDirty();
        com.space_beyond.progression.SpaceProgression.setUnlockedForTests(defeated + 1);
        source.sendSuccess(() -> Component.literal("Defeated guardians: " + defeated + ", stations may travel up to tier " + (defeated + 1)), true);
        return 1;
    }

    private static final List<String> WEMBUROS_STRUCTURES = List.of("any", "throne", "vault", "portal");

    /** {@code /space locate_wemburos <throne|vault|portal|any> [tp]}: naechste Struktur auf Wemburos (Stelle vor dem Eingang). */
    private static int locateWemburos(CommandSourceStack source, String type, boolean tp) throws CommandSyntaxException {
        Vec3 at = source.getPosition();
        com.space_beyond.dim.wemburos.structure.WemburosStructures.Type wt = com.space_beyond.dim.wemburos.structure.WemburosStructures.Type.byName(type);
        if (wt == null && !type.equalsIgnoreCase("any")) {
            throw new SimpleCommandExceptionType(Component.literal("Unknown structure, use: " + String.join(", ", WEMBUROS_STRUCTURES))).create();
        }
        com.space_m.dim.CelestialWorld world = com.space_m.dim.CelestialGenerators.state(com.space_beyond.dim.wemburos.WemburosBlocks.BODY).chunkManager;
        var s = com.space_beyond.dim.wemburos.structure.WemburosStructures.nearest(world, wt, (int) at.x, (int) at.z, 8);
        if (s == null) {
            source.sendFailure(Component.literal("No " + type + " structure within 8 districts"));
            return 0;
        }
        var plan = com.space_beyond.dim.wemburos.structure.WemburosStructures.plan(world, s);
        BlockPos target = com.space_beyond.dim.wemburos.structure.WemburosStructures.worldPos(plan, 0, 1, s.type().radius + 2);
        double dist = Math.hypot(s.x() - at.x, s.z() - at.z);
        String name = s.type().table;
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "Nearest %s at %d %d %d, %.0f blocks away", name, s.x(), plan.base + 1, s.z(), dist)), false);
        if (!tp) return 1;
        ServerPlayer player = source.getPlayerOrException();
        double x = target.getX() + 0.5, y = target.getY(), z = target.getZ() + 0.5;
        if (player.level().dimension() == com.space_beyond.dim.BeyondDimensions.WEMBUROS) {
            player.teleportTo(x, y, z);
        } else {
            CelestialTeleporter.teleport(player, com.space_beyond.dim.BeyondDimensions.WEMBUROS, x, y, z, false);
        }
        source.sendSuccess(() -> Component.literal("Teleported to " + target.toShortString()), true);
        return 1;
    }

    /** Teleportziele: {@code orbit}, Laufwerk-/wand_d-Koerper und Thatmo. */
    public static List<String> targets() {
        List<String> list = new ArrayList<>();
        list.add("orbit");
        for (SolarSystem.Body body : SolarSystem.Body.values()) {
            if (body != SolarSystem.Body.ORBIT) list.add(body.name);
        }
        list.add("thatmo");
        return list;
    }

    /** Ziel zu einem Namen (null = Orbit), wirft fuer unbekannte Namen. */
    @Nullable
    public static SolarSystem.Body parse(String name) throws IllegalArgumentException {
        String n = name.toLowerCase(Locale.ROOT);
        if (n.equals("orbit")) return SolarSystem.Body.ORBIT;
        for (SolarSystem.Body body : SolarSystem.Body.values()) {
            if (body != SolarSystem.Body.ORBIT && body.name.equals(n)) return body;
        }
        throw new IllegalArgumentException(name);
    }

    private static int teleport(CommandSourceStack source, String name) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (name.equalsIgnoreCase("thatmo")) {
            CelestialTeleporter.teleport(player, SpaceDimensions.THATMO, player.getX(), 300, player.getZ(), true);
            source.sendSuccess(() -> Component.literal("Teleported to: THATMO"), true);
            return 1;
        }

        SolarSystem.Body target;
        try {
            target = parse(name);
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.literal("Unknown body: " + name + " (" + String.join(", ", targets()) + ")"));
            return 0;
        }

        if (target == SolarSystem.Body.ORBIT) {
            CelestialTeleporter.teleport(player, SpaceDimensions.ORBIT, player.getX(), 128, player.getZ(), false);
            source.sendSuccess(() -> Component.literal("Teleported to: ORBIT"), true);
        } else {
            CelestialBody body = target.getBody();
            CelestialTeleporter.teleport(player, body.dimension, player.getX(), 300, player.getZ(), true);
            source.sendSuccess(() -> Component.literal("Teleported to: " + body.getUnlocalizedName()), true);
        }
        return 1;
    }
}
