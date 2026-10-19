package com.space_beyond.dim.sporos.genlayer;

import org.jetbrains.annotations.Nullable;

import com.space_m.dim.legacy.GenLayer;
import com.space_beyond.dim.sporos.biome.BiomeGenBaseSporos;
import com.space_beyond.dim.sporos.city.SporosCities;
import com.space_beyond.dim.sporos.city.SporosCities.City;

/**
 * Stadtschicht (Runde 3): Innerhalb einer geplanten Stadt ({@link SporosCities#cityAt}, Kreis aus Mittelpunkt und
 * Radius) gibt es nur die Stadtbiome ({@link SporosCities#isCityBiome}: verwilderte Ebene, Moosdickicht) und als
 * ueberflutete Randbereiche Sumpf und Fluss:
 * <ul>
 *   <li>Hochland, Pilzhain, Sporenfeld -> verwilderte Ebene (flaches Gelaende, Bewuchs der Ebene),</li>
 *   <li>See, Kueste -> Sumpf (geflutete Zellen statt zugeschuetteter Seen),</li>
 *   <li>Ebene, Moosdickicht, Sumpf, Fluss bleiben.</li>
 * </ul>
 * Wird auf der 1/4-Aufloesung (eine Zelle = 4 x 4 Bloecke) nach der Glaettung und vor der Flussmischung eingesetzt;
 * der Fluss kommt also auch durch Staedte (als geflutete Zellen). Der Stadtplan haengt nur vom Weltseed ab
 * ({@code SporosCities.cityAt(seed, chunk)}), die Schicht braucht denselben Seed wie der Generator.
 */
public class GenLayerSporosCity extends GenLayer {

    /** Bloecke je Zelle dieser Schicht. */
    public static final int SCALE = 4;

    private final long worldSeed;

    public GenLayerSporosCity(long worldSeed, long seed, GenLayer parent) {
        super(seed);
        this.worldSeed = worldSeed;
        this.parent = parent;
    }

    @Override
    public int[] getInts(int x, int z, int w, int h) {
        int[] in = this.parent.getInts(x, z, w, h);
        int[] out = new int[w * h];
        int lake = BiomeGenBaseSporos.lake.biomeID, coast = BiomeGenBaseSporos.coast.biomeID;
        int highland = BiomeGenBaseSporos.highland.biomeID, grove = BiomeGenBaseSporos.fungalGrove.biomeID, field = BiomeGenBaseSporos.sporeField.biomeID;
        int plains = BiomeGenBaseSporos.overgrownPlains.biomeID, swamp = BiomeGenBaseSporos.swamp.biomeID;

        int lastDx = Integer.MIN_VALUE, lastDz = Integer.MIN_VALUE;
        City district = null;
        for (int dz = 0; dz < h; dz++) {
            for (int dx = 0; dx < w; dx++) {
                int c = in[dx + dz * w];
                if (c == lake || c == coast || c == highland || c == grove || c == field) {
                    int bx = (x + dx) * SCALE + SCALE / 2, bz = (z + dz) * SCALE + SCALE / 2;
                    int ddx = Math.floorDiv(bx >> 4, SporosCities.DISTRICT_CHUNKS), ddz = Math.floorDiv(bz >> 4, SporosCities.DISTRICT_CHUNKS);
                    if (ddx != lastDx || ddz != lastDz) {
                        district = SporosCities.cityOfDistrict(worldSeed, ddx, ddz);
                        lastDx = ddx;
                        lastDz = ddz;
                    }
                    if (inCity(district, bx, bz)) c = (c == lake || c == coast) ? swamp : plains;
                }
                out[dx + dz * w] = c;
            }
        }
        return out;
    }

    /** Liegt der Block im Stadtkreis (Rand-Faktor unter 1, gleiche Rechnung wie die Gelaendeangleichung)? */
    public static boolean inCity(@Nullable City city, int blockX, int blockZ) {
        return city != null && city.edgeFactor(blockX, blockZ) < 1.0D;
    }
}
