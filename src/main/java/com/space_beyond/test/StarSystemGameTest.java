package com.space_beyond.test;

import com.space_m.SpaceMod;
import com.space_m.celestial.AstronomyUtil;
import com.space_m.celestial.CelestialBody;
import com.space_m.celestial.SolarSystem;
import com.space_beyond.celestial.SporosSystem;
import com.space_beyond.celestial.StarSystem;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.Level;
//? if forge {
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
//?} elif neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}

/**
 * Erweiterung Sternsysteme: Heimatsystem unveraendert, zweites System (Sporos-Stern) vorhanden, interstellare Ziele
 * gesperrt. Delta-v-Sollwerte: Original-Rechnung ({@code calculateHohmannTransfer}) mit Java-float-Semantik
 * nachgerechnet (scratchpad/star2/dv.py), vor der Erweiterung.
 */
@GameTestHolder(com.space_beyond.BeyondMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class StarSystemGameTest {

    private StarSystemGameTest() {}

    private static void check(boolean cond, String msg) {
        if (!cond) throw new GameTestAssertException(msg);
    }

    private static void near(double actual, double expected, double eps, String msg) {
        check(Math.abs(actual - expected) <= eps, msg + ": erwartet " + expected + ", ist " + actual);
    }

    private static CelestialBody body(String name) {
        CelestialBody b = CelestialBody.getBodyOrNull(name);
        check(b != null, "Koerper fehlt: " + name);
        return b;
    }

    /** Heimatsystem: Werte und Delta-v wie vor der Erweiterung. */
    @GameTest(template = "empty3x3x3", batch = "space_starsystem", timeoutTicks = 20)
    public static void homeSystemUnchanged(GameTestHelper helper) {
        StarSystem home = StarSystem.home();
        check(home != null && home.isHome() && home.star == SolarSystem.kerbol, "Heimatsystem = Kerbol");
        check(home.galaxyX == 0 && home.galaxyY == 0 && home.luminosity == 1, "Heimat bei 0/0, Leuchtkraft 1");
        check(StarSystem.all().get(0) == home, "Heimat zuerst");
        check(home.bodies().size() == 52, "52 Koerper im Heimatsystem, ist " + home.bodies().size());
        check(StarSystem.of(body("mun")) == home && StarSystem.of(body("karen")) == home, "Monde gehoeren zur Heimat");
        check(CelestialBody.getStar(helper.getLevel()) == SolarSystem.kerbol, "Stern der Testwelt = Kerbol");

        CelestialBody kerbol = SolarSystem.kerbol;
        check(kerbol.massKg == 1.757e28F && kerbol.radiusKm == 261_600 && kerbol.getRotationalPeriodSeconds() == 432_000, "Kerbol-Werte");
        check(kerbol.color[0] == 1.0F && kerbol.color[1] == 0.8667F && kerbol.color[2] == 0.0F, "Kerbol-Farbe");
        check(kerbol.satellites.size() == 13, "Kerbol mit 10 Planeten + 3 Zwergplaneten");
        check(body("kerbin").semiMajorAxisKm == 13_599_840 && body("duna").semiMajorAxisKm == 20_726_155
                && body("plock").semiMajorAxisKm == 535_833_706, "Bahnradien");
        near(body("kerbin").getOrbitalPeriod(), 426.09, 0.5, "Kerbin-Jahr");
        near(body("duna").getSunPower(), 0.43055, 1e-4, "Sonne Duna");

        near(SolarSystem.getDeltaVBetween(body("kerbin"), body("mun")), 1127.740111244644, 0.01, "dv Kerbin->Mun");
        near(SolarSystem.getDeltaVBetween(body("mun"), body("kerbin")), 1127.740111244644, 0.01, "dv Mun->Kerbin");
        near(SolarSystem.getDeltaVBetween(body("kerbin"), body("minmus")), 1073.4274103144783, 0.01, "dv Kerbin->Minmus");
        near(SolarSystem.getDeltaVBetween(body("kerbin"), body("duna")), 1668.6843246049402, 0.01, "dv Kerbin->Duna");
        near(SolarSystem.getDeltaVBetween(body("kerbin"), body("eve")), 2426.5806736761347, 0.01, "dv Kerbin->Eve");
        near(SolarSystem.getDeltaVBetween(body("kerbin"), body("ike")), 1560.5500860121851, 0.01, "dv Kerbin->Ike");
        near(SolarSystem.getDeltaVBetween(body("minmus"), body("ike")), 1383.255292202235, 0.01, "dv Minmus->Ike");
        check(SolarSystem.getDeltaVBetween(body("duna"), body("duna")) == 0, "dv zu sich selbst = 0");

        int cost = SolarSystem.getCostBetween(body("kerbin"), body("mun"), 50_000, 2_000_000, 300, false, false);
        check(cost > 0 && cost < Integer.MAX_VALUE && cost % 100 == 0, "Treibstoff Kerbin->Mun " + cost);

        for (CelestialBody b : home.bodies()) {
            check(StarSystem.reachability(body("kerbin"), b) == StarSystem.Reachability.SAME_SYSTEM, b.name + " erreichbar");
        }
        double d = SolarSystem.calculateDistanceBetweenTwoBodies(helper.getLevel(), body("kerbin"), body("duna"));
        check(d > 0 && d < 1e9, "Abstand Kerbin-Duna aus der Bahnrechnung: " + d);
        helper.succeed();
    }

    /** Zweites System mit dem Sporos-Stern als eigenem Stern, rechts weit entfernt, noch ohne Planeten. */
    @GameTest(template = "empty3x3x3", batch = "space_starsystem", timeoutTicks = 20)
    public static void secondSystemExists(GameTestHelper helper) {
        check(StarSystem.all().size() == 2, "2 Sternsysteme, ist " + StarSystem.all().size());
        StarSystem sporos = StarSystem.byId(SporosSystem.SYSTEM_ID);
        check(sporos != null && !sporos.isHome(), "Sporos-System vorhanden");

        CelestialBody star = body(SporosSystem.STAR_NAME);
        check(star == SporosSystem.star && sporos.star == star, "Sporos-Stern registriert");
        check(star.parent == null && star.getStar() == star && star.getPlanet() == star, "Stern ohne Elternkoerper");
        check(StarSystem.of(star) == sporos && sporos.bodies().size() == 3 && sporos.bodies().get(1).name.equals("sporos")
                && sporos.bodies().get(2).name.equals("wemburos"), "Stern + Planeten Sporos und Wemburos");
        check(star != SolarSystem.kerbol && StarSystem.of(SolarSystem.kerbol) != sporos, "getrennt von Kerbol");

        check(star.massKg == 1.441e28F && star.radiusKm == 209_280 && star.getRotationalPeriodSeconds() == 604_800, "Sternwerte");
        near(star.getSurfaceGravity(), 21.96, 0.05, "Oberflaechenschwere Sporos-Stern");
        check(star.color[0] == 0.72F && star.color[1] == 0.35F && star.color[2] == 1.0F, "Farbe violett");
        check(sporos.luminosity == 0.45D, "Leuchtkraft");

        check(sporos.galaxyX > 0 && sporos.galaxyY == 0, "rechts vom Heimatsystem");
        near(StarSystem.home().distanceLightYears(sporos), 5.0, 1e-9, "Abstand Lichtjahre");
        check(StarSystem.home().distanceKm(sporos) > 1000D * body("plock").semiMajorAxisKm, "weit ausserhalb der aeussersten Bahn");

        check(star.dimension == null && !star.canLand && star.getEnum() == null, "keine Dimension, keine Landung");
        check(!CelestialBody.getLandableBodies().contains(star), "nicht landbar");
        for (SolarSystem.Body type : SolarSystem.Body.values()) {
            check(type.getBody() != star, "kein Laufwerk/Teleportziel fuer den Stern (" + type + ")");
        }
        check(CelestialBody.getBody(Level.OVERWORLD) == body("kerbin"), "Oberwelt bleibt Kerbin");
        helper.succeed();
    }

    /** Ziele in einem anderen System sind ohne interstellaren Antrieb gesperrt; der Erweiterungspunkt greift. */
    @GameTest(template = "empty3x3x3", batch = "space_starsystem", timeoutTicks = 20)
    public static void interstellarTargetsLocked(GameTestHelper helper) {
        CelestialBody kerbin = body("kerbin");
        CelestialBody star = body(SporosSystem.STAR_NAME);

        // Runde 2: angemeldet ist der Faltraum-Antrieb; ohne Station mit geladenem Antrieb bleibt alles gesperrt
        StarSystem.InterstellarDrive registered = StarSystem.interstellarDrive();
        check(registered == com.space_beyond.orbit.interstellar.FoldDriveNetwork.INSTANCE, "Faltantrieb angemeldet");
        check(!StarSystem.sameSystem(kerbin, star), "verschiedene Systeme");
        check(StarSystem.reachability(kerbin, star) == StarSystem.Reachability.NEEDS_INTERSTELLAR_DRIVE, "braucht interstellaren Antrieb");
        check(!StarSystem.isReachable(kerbin, star) && !StarSystem.isReachable(star, body("mun")), "beide Richtungen gesperrt");
        check(SolarSystem.getDeltaVBetween(kerbin, star) == Double.POSITIVE_INFINITY, "Delta-v unendlich");
        check(SolarSystem.getCostBetween(kerbin, star, 50_000, 2_000_000, 300, false, true) == Integer.MAX_VALUE, "Treibstoff unbezahlbar");
        check(SolarSystem.getFuelCost(Double.POSITIVE_INFINITY, 50_000, 300) == Integer.MAX_VALUE, "getFuelCost(inf)");
        near(SolarSystem.calculateDistanceBetweenTwoBodies(helper.getLevel(), kerbin, star), 5.0 * StarSystem.KM_PER_LIGHT_YEAR, 1, "Reiseabstand = Systemabstand");

        // Erweiterungspunkt: ein (Test-)Antrieb schaltet frei, danach wieder gesperrt
        try {
            StarSystem.setInterstellarDrive(new StarSystem.InterstellarDrive() {
                @Override
                public boolean canTravel(CelestialBody from, CelestialBody to) {
                    return true;
                }

                @Override
                public double deltaV(CelestialBody from, CelestialBody to) {
                    return 12_345;
                }
            });
            check(StarSystem.reachability(kerbin, star) == StarSystem.Reachability.INTERSTELLAR, "Antrieb schaltet frei");
            check(SolarSystem.getDeltaVBetween(kerbin, star) == 12_345, "Delta-v vom Antrieb");
            near(SolarSystem.getDeltaVBetween(kerbin, body("mun")), 1127.740111244644, 0.01, "Heimat-dv unberuehrt");
            StarSystem.setInterstellarDrive(null);
            check(StarSystem.interstellarDrive() == StarSystem.InterstellarDrive.LOCKED, "null -> LOCKED");
        } finally {
            StarSystem.setInterstellarDrive(registered);
        }
        check(StarSystem.reachability(kerbin, star) == StarSystem.Reachability.NEEDS_INTERSTELLAR_DRIVE, "wieder gesperrt");
        check(AstronomyUtil.TIME_MULTIPLIER == 1, "Zeitfaktor unveraendert");
        helper.succeed();
    }
}
