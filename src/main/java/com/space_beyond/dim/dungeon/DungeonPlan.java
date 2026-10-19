package com.space_beyond.dim.dungeon;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Direction;

/**
 * Plan eines Boss-Dungeons: Grundhoehe, Beutestellen, Spawner, Bewohner, Boss. Lokale Koordinaten (u, y, v) relativ
 * zum Mittelpunkt und zur Grundhoehe, gedreht ueber {@link PlanetDungeons.Dungeon}.
 */
public final class DungeonPlan {

    /** Behaelter: Truhe (Blickrichtung) oder Fass (nach oben). */
    public record Loot(int u, int y, int v, Direction facing, boolean barrel, String table) { }

    /** Mob/Spawner nach Registry-ID ({@code minecraft:husk}, {@code hbm_m:glyphid}, {@code space_m:...}). */
    public record Mob(int u, int y, int v, String type) { }

    public final PlanetDungeons.Dungeon d;
    public final int base;
    public final List<Loot> loot = new ArrayList<>();
    public final List<Mob> spawners = new ArrayList<>();
    public final List<Mob> mobs = new ArrayList<>();
    /** Siegelwaechter: verstaerkte Mobs, die einen Siegelschluessel tragen. */
    public final List<Mob> elites = new ArrayList<>();
    /** Pfeilfallen: Werfer, die beim Erzeugen mit Pfeilen gefuellt werden. */
    public final List<Mob> dispensers = new ArrayList<>();

    DungeonPlan(PlanetDungeons.Dungeon d, int base) {
        this.d = d;
        this.base = base;
    }

    private DungeonSurface surface;

    /** Gelaende-Huelle (beim ersten Zugriff berechnet; Plaene sind je Thread zwischengespeichert). */
    public DungeonSurface surface() {
        if (surface == null) surface = new DungeonSurface(d.design(), this);
        return surface;
    }

    public void chest(int u, int y, int v, Direction facing, String table) {
        loot.add(new Loot(u, y, v, facing, false, table));
    }

    public void barrel(int u, int y, int v, String table) {
        loot.add(new Loot(u, y, v, Direction.UP, true, table));
    }

    public void spawner(int u, int y, int v, String type) {
        spawners.add(new Mob(u, y, v, type));
    }

    public void mob(int u, int y, int v, String type) {
        mobs.add(new Mob(u, y, v, type));
    }

    public void elite(int u, int y, int v, String type) {
        elites.add(new Mob(u, y, v, type));
    }

    public void dispenser(int u, int y, int v) {
        dispensers.add(new Mob(u, y, v, "minecraft:arrow"));
    }

    /** Schatzkiste mit Siegelschluessel am Ende eines Fluegels. */
    public void keyChest(int u, int y, int v, Direction facing) {
        loot.add(new Loot(u, y, v, facing, false, "seal_key"));
    }
}
