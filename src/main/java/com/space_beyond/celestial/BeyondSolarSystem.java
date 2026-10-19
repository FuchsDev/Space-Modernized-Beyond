package com.space_beyond.celestial;

import com.space_beyond.BeyondMod;
import com.space_m.celestial.CelestialBody;
import com.space_m.celestial.SolarSystem;

import net.minecraft.resources.ResourceLocation;

/**
 * Erweiterung des Sonnensystems von Space Modernized, aufgerufen von {@code SolarSystemMixin} am Ende von
 * {@code SolarSystem.init()} (vor {@code runTests}):
 * <ul>
 *   <li>20 ergaenzte Monde und Zwergplaneten im Heimatsystem (nur Karte/Himmel, nicht landbar);</li>
 *   <li>fuenf Monde mit den Werten ihrer echten Vorbilder (Hyperion, Phoebe, Puck, Portia, Sycorax) und neuer Textur;</li>
 *   <li>die Sternsysteme: Heimat (Kerbol) und das Sporos-System rechts daneben.</li>
 * </ul>
 * Die Anzeigenamen (Sonne, Mars, Jupiter ...) kommen aus {@code assets/space_beyond/lang_override}.
 */
public final class BeyondSolarSystem {
    private BeyondSolarSystem() {}

    /** {@code space_beyond:textures/misc/space/<name>.png}. */
    public static ResourceLocation tex(String name) {
        return BeyondMod.id("textures/misc/space/" + name + ".png");
    }

    private static CelestialBody body(String name) {
        return new CelestialBody(name).withTexture(tex(name));
    }

    private static CelestialBody existing(String name) {
        CelestialBody b = CelestialBody.getBodyOrNull(name);
        if (b == null) throw new IllegalStateException("Space Modernized: body " + name + " missing");
        return b;
    }

    public static void extend() {
        existing("duna").withSatellites(
            body("deimos")
                .withMassRadius(4.233e17F, 12)
                .withOrbitalParameters(8_000, 0.0F, 0.0F, 1.8F, 20.0F)
                .withRotationalPeriod(30_000)
                .withColor(0.553F, 0.478F, 0.408F)
                .withTidalLockingTo("duna"));

        existing("jool").withSatellites(
            body("amalthea")
                .withMassRadius(3.969e18F, 30)
                .withOrbitalParameters(15_000, 0.003F, 0.0F, 0.4F, 108.0F)
                .withRotationalPeriod(43_000)
                .withColor(0.62F, 0.333F, 0.251F)
                .withTidalLockingTo("jool"));

        // Sarnus-Monde mit den Werten ihrer Vorbilder
        existing("eeloo")
            .withMassRadius(4.41e18F, 50) // Hyperion
            .withColor(0.718F, 0.627F, 0.51F)
            .withTexture(tex("eeloo"));
        existing("slate")
            .withMassRadius(2.352e18F, 40) // Phoebe
            .withColor(0.329F, 0.31F, 0.29F)
            .withTexture(tex("slate"));

        existing("sarnus").withSatellites(
            body("mimas")
                .withMassRadius(4.321e19F, 70)
                .withOrbitalParameters(8_600, 0.02F, 0.0F, 1.5F, 55.0F)
                .withRotationalPeriod(81_000)
                .withColor(0.722F, 0.722F, 0.702F)
                .withTidalLockingTo("sarnus"),

            body("enceladus")
                .withMassRadius(1.31e20F, 90)
                .withOrbitalParameters(14_800, 0.005F, 0.0F, 0.02F, 55.0F)
                .withRotationalPeriod(118_000)
                .withColor(0.92F, 0.949F, 0.98F)
                .withTidalLockingTo("sarnus"),

            body("tethys")
                .withMassRadius(5.644e20F, 160)
                .withOrbitalParameters(23_500, 0.0F, 0.0F, 1.1F, 55.0F)
                .withRotationalPeriod(163_000)
                .withColor(0.82F, 0.8F, 0.78F)
                .withTidalLockingTo("sarnus"),

            body("dione")
                .withMassRadius(9.77e20F, 170)
                .withOrbitalParameters(30_000, 0.002F, 0.0F, 0.03F, 55.0F)
                .withRotationalPeriod(236_000)
                .withColor(0.702F, 0.702F, 0.722F)
                .withTidalLockingTo("sarnus"),

            body("rhea")
                .withMassRadius(2.022e21F, 230)
                .withOrbitalParameters(52_000, 0.001F, 0.0F, 0.35F, 55.0F)
                .withRotationalPeriod(390_000)
                .withColor(0.749F, 0.729F, 0.702F)
                .withTidalLockingTo("sarnus"),

            body("iapetus")
                .withMassRadius(1.426e21F, 210)
                .withOrbitalParameters(140_000, 0.028F, 0.0F, 15.5F, 80.0F)
                .withRotationalPeriod(1_100_000)
                .withColor(0.553F, 0.451F, 0.349F)
                .withTidalLockingTo("sarnus"));

        // Urlum-Monde mit den Werten ihrer Vorbilder
        existing("polta").withMassRadius(1.058e18F, 30).withTexture(tex("polta")); // Puck
        existing("priax").withMassRadius(5.512e17F, 25).withTexture(tex("priax")); // Portia
        existing("wal").withMassRadius(1.058e19F, 60).withTexture(tex("wal")); // Sycorax (schwerer als Caliban)

        existing("urlum").withSatellites(
            body("miranda")
                .withMassRadius(5.762e19F, 70)
                .withOrbitalParameters(6_000, 0.001F, 0.0F, 4.2F, 40.0F)
                .withRotationalPeriod(61_000)
                .withColor(0.62F, 0.62F, 0.639F)
                .withTidalLockingTo("urlum"),

            body("ariel")
                .withMassRadius(1.147e21F, 170)
                .withOrbitalParameters(9_000, 0.001F, 0.0F, 0.3F, 40.0F)
                .withRotationalPeriod(108_000)
                .withColor(0.678F, 0.659F, 0.659F)
                .withTidalLockingTo("urlum"),

            body("umbriel")
                .withMassRadius(1.035e21F, 175)
                .withOrbitalParameters(16_000, 0.004F, 0.0F, 0.1F, 40.0F)
                .withRotationalPeriod(179_000)
                .withColor(0.38F, 0.369F, 0.38F)
                .withTidalLockingTo("urlum"),

            body("titania")
                .withMassRadius(3.217e21F, 240)
                .withOrbitalParameters(26_000, 0.001F, 0.0F, 0.3F, 40.0F)
                .withRotationalPeriod(376_000)
                .withColor(0.62F, 0.58F, 0.561F)
                .withTidalLockingTo("urlum"),

            body("oberon")
                .withMassRadius(2.721e21F, 230)
                .withOrbitalParameters(36_000, 0.001F, 0.0F, 0.1F, 40.0F)
                .withRotationalPeriod(583_000)
                .withColor(0.549F, 0.502F, 0.482F)
                .withTidalLockingTo("urlum"));

        existing("neidon").withSatellites(
            body("proteus")
                .withMassRadius(3.704e19F, 60)
                .withOrbitalParameters(14_000, 0.0F, 0.0F, 0.5F, 184.0F)
                .withRotationalPeriod(96_000)
                .withColor(0.361F, 0.341F, 0.329F)
                .withTidalLockingTo("neidon"));

        existing("plock").withSatellites(
            body("nix")
                .withMassRadius(7.349e16F, 10)
                .withOrbitalParameters(4_800, 0.002F, 0.0F, 0.1F, 260.0F)
                .withRotationalPeriod(8_600)
                .withColor(0.8F, 0.8F, 0.8F),

            body("hydra")
                .withMassRadius(7.349e16F, 10)
                .withOrbitalParameters(6_400, 0.006F, 0.0F, 0.2F, 260.0F)
                .withRotationalPeriod(3_700)
                .withColor(0.78F, 0.78F, 0.8F));

        // Zwergplaneten jenseits von Pluto (nur Karte, nicht landbar)
        SolarSystem.kerbol.withSatellites(
            body("haumea")
                .withMassRadius(8.467e19F, 120)
                .withOrbitalParameters(585_000_000, 0.19F, 240.0F, 28.2F, 122.0F)
                .withRotationalPeriod(3_900)
                .withColor(0.878F, 0.859F, 0.839F)
                .withRings(28.0F, 2, 0.75F, 0.72F, 0.7F),

            body("makemake")
                .withMassRadius(1.166e20F, 115)
                .withOrbitalParameters(622_000_000, 0.16F, 295.0F, 29.0F, 79.0F)
                .withRotationalPeriod(22_800)
                .withColor(0.722F, 0.522F, 0.42F),

            body("eris")
                .withMassRadius(4.245e20F, 190)
                .withOrbitalParameters(920_000_000, 0.25F, 151.0F, 44.0F, 36.0F)
                .withRotationalPeriod(25_900)
                .withColor(0.902F, 0.902F, 0.882F)
                .withSatellites(
                    body("dysnomia")
                        .withMassRadius(2.646e18F, 30)
                        .withOrbitalParameters(3_700, 0.006F, 0.0F, 0.0F, 36.0F)
                        .withRotationalPeriod(57_000)
                        .withColor(0.4F, 0.4F, 0.42F)
                        .withTidalLockingTo("eris")));

        // Sternsysteme: Heimat zuerst, danach das Sporos-System rechts daneben
        StarSystem.register("kerbol", SolarSystem.kerbol, 0.0D, 0.0D, 1.0D);
        SporosSystem.star = SporosSystem.createStar();
        StarSystem.register(SporosSystem.SYSTEM_ID, SporosSystem.star, SporosSystem.GALAXY_X_LY, SporosSystem.GALAXY_Y_LY, SporosSystem.STAR_LUMINOSITY);
    }
}
