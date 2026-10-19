package com.space_beyond.celestial;

import com.space_m.celestial.CelestialBody;
import com.space_m.celestial.CelestialFluids;

import com.space_m.SpaceMod;
import com.space_m.celestial.trait.CBT_Atmosphere;
import com.space_beyond.celestial.trait.CBT_Spores;
import com.space_m.celestial.trait.CBT_Temperature;
import com.space_m.celestial.trait.CBT_Water;
import com.space_m.dim.SpaceDimensions;

/**
 * Zweites Sternsystem "Sporos" (eigene Erweiterung, kein NTM-Original): violetter Stern {@code sporos_star}, der
 * Planet {@code sporos} (Runde 2) und der innere Gesteinsplanet {@code wemburos}. Alle Werte stehen hier als Konstanten.
 *
 * <p><b>Stern</b> (Spektralklasse K1 V, im selben Massstab wie KSP-Kerbol, d.h. Verhaeltnisse zur echten Sonne auf
 * Kerbol uebertragen):
 * <ul>
 *   <li>Masse 0,82 Kerbol = 1,441e28 kg (echter K1-Zwerg ~0,82 Sonnenmassen)</li>
 *   <li>Radius 0,80 Kerbol = 209 280 km (~0,80 Sonnenradien) -> Oberflaechenschwere 21,9 m/s^2 (Kerbol 17,1),
 *       passend zum hoeheren log g kleiner K-Zwerge</li>
 *   <li>Rotation 604 800 s (1,4 x Kerbol; K-Zwerge drehen langsamer als die Sonne)</li>
 *   <li>Leuchtkraft 0,45 Kerbol, Oberflaechentemperatur ~5 100 K (K1 V bleibt fuer Masse/Leuchtkraft/Sonnenleistung)</li>
 *   <li>Farbe <b>violett</b> (0,72 / 0,35 / 1,0, Nutzerwunsch). Ein Schwarzer Strahler ist nie violett (heisse Sterne
 *       wirken blau-weiss); die Farbe ist bewusst fiktiv und wird als dichte Staub-/Sporenhuelle um den Stern
 *       erklaert, die Gruen/Gelb verschluckt (Absorption um 500-600 nm) - Rot und Blau kommen durch. Deshalb
 *       bleiben Masse und Leuchtkraft die eines K-Zwergs.</li>
 *   <li>Galaxie-Position (5,0 / 0,0) Lichtjahre: rechts vom Heimatsystem</li>
 * </ul>
 *
 * <p><b>Planet Sporos</b> (feuchte, ueberwucherte Erdwelt in <b>Erdgroesse</b>, Nutzerwunsch: auf Karte und Himmel
 * deutlich sichtbar - Kerbin hat nur KSP-Massstab 600 km):
 * <ul>
 *   <li>Radius 6 371 km (Erde, ~10,6 x Kerbin, etwas groesser als Jool 6 000 km), Masse 5,97e24 kg (Erde) ->
 *       Oberflaechenschwere 9,82 m/s^2 = 1,00 g (Spieler-Schwerkraft 1,601 wie Kerbin)</li>
 *   <li>Folgen des Erdradius: Orbit-Dimension (Umlauf 7 200 s) liegt 8 060 km vom Mittelpunkt = ~1 690 km ueber
 *       der Oberflaeche (Kerbin 1 667 km vom Mittelpunkt); Orbitalgeschwindigkeit 7,9 km/s -> Start von Sporos in den
 *       Orbit kostet wie auf der Erde viel Treibstoff (Raketen brauchen grosse Stufen), Landen per Luftbremsung ist
 *       billig; Fluchtimpuls der Station am Sporos-Ende einer Faltreise ~3 250 m/s (Kerbin ~930). Himmel/Karte/Orrery
 *       skalieren mit dem Radius (scheinbare Groesse), keine Begrenzung noetig (Jool ist aehnlich gross).</li>
 *   <li>Bahnradius 8 700 000 km = 0,640 AU (Kerbin-AU), Exzentrizitaet 0,021, Inklination 0,8 Grad. Die
 *       habitable Zone skaliert mit der Wurzel der Leuchtkraft: Kerbin-gleiche Einstrahlung laege bei
 *       sqrt(0,45) = 0,671 AU; Sporos liegt etwas innen -> Sonnenleistung 0,45 / 0,640^2 = 1,10 (10 % mehr als
 *       Kerbin, warm und feucht). Umlaufzeit ~241 KSP-Tage.</li>
 *   <li>Rotation 26 400 s (7 h 20 min, Kerbin 5 h 59 min) -> Minecraft-Tag 29 333 Ticks (~24,4 min);
 *       Achsneigung 18 Grad</li>
 *   <li>Atmosphaere 1,0 bar Erdluft (O2/N2, atembar), Wasser ({@link CBT_Water}), 24 Grad C, Sporen
 *       ({@link CBT_Spores}; Wirkung Runde 4: {@code SporeExposure}/{@code SporeInfection})</li>
 *   <li>Verarbeitungsstufe mindestens 3 (Stardar/Laufwerke wie Laythe/Tekto)</li>
 * </ul>
 * Name, Farbe und Position sind hier zentral und leicht aenderbar (Lang-Schluessel {@code body.sporos_star},
 * {@code body.sporos}).
 */
public final class SporosSystem {

    private SporosSystem() {}

    /** Systemkennung. */
    public static final String SYSTEM_ID = "sporos";
    /** Platzhaltername des Sterns (Koerpername = Lang-Schluessel {@code body.<name>} und Textur {@code <name>.png}). */
    public static final String STAR_NAME = "sporos_star";

    public static final float STAR_MASS_KG = 1.441e28F;
    public static final float STAR_RADIUS_KM = 209_280;
    public static final int STAR_ROTATION_S = 604_800;
    public static final float[] STAR_COLOR = {0.72F, 0.35F, 1.0F};
    /** Leuchtkraft relativ zu Kerbol (Sonnenleistung im System, {@link CelestialBody#getSunPower()}). */
    public static final double STAR_LUMINOSITY = 0.45D;

    public static final double GALAXY_X_LY = 5.0D;
    public static final double GALAXY_Y_LY = 0.0D;

    // ── Planet Sporos ───────────────────────────────────────────────────────────────────────────

    /** Koerpername = Dimension {@code space_m:sporos}, Lang {@code body.sporos}, Textur {@code sporos.png}. */
    public static final String PLANET_NAME = "sporos";
    /** Fortlaufend nach Thatmo (413 025); nur fuer das Wetter-Speicherintervall wie die Original-Nummern. */
    public static final int PLANET_LEGACY_ID = 413_026;

    public static final float PLANET_MASS_KG = 5.97e24F;
    public static final float PLANET_RADIUS_KM = 6_371;
    public static final float PLANET_SEMI_MAJOR_AXIS_KM = 8_700_000;
    public static final float PLANET_ECCENTRICITY = 0.021F;
    public static final float PLANET_ARG_PERIAPSIS_DEG = 30.0F;
    public static final float PLANET_INCLINATION_DEG = 0.8F;
    public static final float PLANET_ASCENDING_NODE_DEG = 40.0F;
    public static final int PLANET_ROTATION_S = 26_400;
    public static final float PLANET_AXIAL_TILT_DEG = 18.0F;
    public static final float[] PLANET_COLOR = {0.38F, 0.55F, 0.30F};
    public static final double PLANET_PRESSURE_ATM = 1.0D;
    public static final float PLANET_TEMPERATURE_C = 24.0F;
    public static final int PLANET_MIN_PROCESSING_LEVEL = 3;

    // ── Planet Wemburos (eigene Erweiterung, zweiter Planet) ─────────────────────────────────────

    /**
     * Wemburos: trockener Gesteinsplanet in <b>Venusgroesse</b> (Nutzerwunsch), lila Gestein, schwarzer Onyx, Gold.
     * Radius 6 052 km, Masse 4,87e24 kg -> 8,87 m/s^2 (0,90 g). Innerer Planet: Bahnradius 5 600 000 km = 0,412 AU
     * (Kerbin-AU) -> Sonnenleistung 0,45 / 0,412^2 = 2,65 (heiss, kein fluessiges Wasser). Duenne Stickstoffluft mit
     * Xenon (0,55 + 0,15 bar): Xenon leuchtet im Sternwind violett und erklaert den lila Himmel; nicht atembar.
     * 38 Grad C, Rotation 32 400 s (9 h), Achsneigung 2,6 Grad, Verarbeitungsstufe 3 wie Sporos.
     */
    public static final String WEMBUROS_NAME = "wemburos";
    public static final int WEMBUROS_LEGACY_ID = 413_027;
    public static final float WEMBUROS_MASS_KG = 4.87e24F;
    public static final float WEMBUROS_RADIUS_KM = 6_052;
    public static final float WEMBUROS_SEMI_MAJOR_AXIS_KM = 5_600_000;
    public static final float WEMBUROS_ECCENTRICITY = 0.007F;
    public static final float WEMBUROS_ARG_PERIAPSIS_DEG = 55.0F;
    public static final float WEMBUROS_INCLINATION_DEG = 3.4F;
    public static final float WEMBUROS_ASCENDING_NODE_DEG = 76.0F;
    public static final int WEMBUROS_ROTATION_S = 32_400;
    public static final float WEMBUROS_AXIAL_TILT_DEG = 2.6F;
    public static final float[] WEMBUROS_COLOR = {0.55F, 0.26F, 0.78F};
    public static final double WEMBUROS_NITROGEN_ATM = 0.55D;
    public static final double WEMBUROS_XENON_ATM = 0.15D;
    public static final float WEMBUROS_TEMPERATURE_C = 38.0F;

    /** Der Planet Wemburos, gesetzt von {@link BeyondSolarSystem#extend()}. */
    public static CelestialBody wemburos;

    /** Der Stern, gesetzt von {@link BeyondSolarSystem#extend()}. */
    public static CelestialBody star;
    /** Der Planet Sporos, gesetzt von {@link BeyondSolarSystem#extend()}. */
    public static CelestialBody planet;

    static CelestialBody createStar() {
        CelestialBody s = new CelestialBody(STAR_NAME)
            .withTexture(BeyondSolarSystem.tex(STAR_NAME))
            .withMassRadius(STAR_MASS_KG, STAR_RADIUS_KM)
            .withRotationalPeriod(STAR_ROTATION_S)
            .withColor(STAR_COLOR.clone());
        planet = createPlanet();
        wemburos = createWemburos();
        return s.withSatellites(planet, wemburos);
    }

    private static CelestialBody createWemburos() {
        return new CelestialBody(WEMBUROS_NAME, com.space_beyond.dim.BeyondDimensions.WEMBUROS, WEMBUROS_LEGACY_ID, com.space_beyond.celestial.BeyondBodies.WEMBUROS)
            .withTexture(BeyondSolarSystem.tex(WEMBUROS_NAME))
            .withMassRadius(WEMBUROS_MASS_KG, WEMBUROS_RADIUS_KM)
            .withOrbitalParameters(WEMBUROS_SEMI_MAJOR_AXIS_KM, WEMBUROS_ECCENTRICITY, WEMBUROS_ARG_PERIAPSIS_DEG,
                    WEMBUROS_INCLINATION_DEG, WEMBUROS_ASCENDING_NODE_DEG)
            .withRotationalPeriod(WEMBUROS_ROTATION_S)
            .withAxialTilt(WEMBUROS_AXIAL_TILT_DEG)
            .withColor(WEMBUROS_COLOR.clone())
            .withMinProcessingLevel(PLANET_MIN_PROCESSING_LEVEL)
            .withBlockTextures(com.space_beyond.BeyondMod.id("textures/block/wemburos_stone.png"), com.space_beyond.BeyondMod.id("textures/block/wemburos_regolith.png"))
            .withTraits(new CBT_Atmosphere(CelestialFluids.NITROGEN, WEMBUROS_NITROGEN_ATM).and(CelestialFluids.XENON, WEMBUROS_XENON_ATM),
                    new CBT_Temperature(WEMBUROS_TEMPERATURE_C));
    }

    private static CelestialBody createPlanet() {
        return new CelestialBody(PLANET_NAME, com.space_beyond.dim.BeyondDimensions.SPOROS, PLANET_LEGACY_ID, com.space_beyond.celestial.BeyondBodies.SPOROS)
            .withTexture(BeyondSolarSystem.tex(PLANET_NAME))
            .withMassRadius(PLANET_MASS_KG, PLANET_RADIUS_KM)
            .withOrbitalParameters(PLANET_SEMI_MAJOR_AXIS_KM, PLANET_ECCENTRICITY, PLANET_ARG_PERIAPSIS_DEG,
                    PLANET_INCLINATION_DEG, PLANET_ASCENDING_NODE_DEG)
            .withRotationalPeriod(PLANET_ROTATION_S)
            .withAxialTilt(PLANET_AXIAL_TILT_DEG)
            .withColor(PLANET_COLOR.clone())
            .withMinProcessingLevel(PLANET_MIN_PROCESSING_LEVEL)
            .withBlockTextures(com.space_beyond.BeyondMod.id("textures/block/sporos_stone.png"), com.space_beyond.BeyondMod.id("textures/block/sporos_silt.png"))
            .withTraits(new CBT_Atmosphere(CelestialFluids.EARTHAIR, PLANET_PRESSURE_ATM), new CBT_Water(),
                    new CBT_Temperature(PLANET_TEMPERATURE_C), new CBT_Spores());
    }
}
