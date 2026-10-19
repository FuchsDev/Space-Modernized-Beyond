package com.space_beyond.client.sky;

import com.space_m.client.sky.SkyGL;
import com.space_m.client.sky.SkyTess;

import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.platform.NativeImage;
import com.space_beyond.celestial.AsteroidBelts;
import com.space_m.celestial.AstronomyUtil;
import com.space_m.celestial.CelestialBody;
import com.space_m.celestial.SolarSystem.AstroMetric;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Asteroidenguertel am Himmel (Erweiterung), drei Schichten:
 * <ol>
 *   <li>Staubband entlang der Bahnebene, gesehen vom eigenen Standort (breiter/heller, je naeher der Guertel);</li>
 *   <li>die Brocken der {@link AsteroidBelts} als Lichtpunkte (feine Koernung im Band);</li>
 *   <li>Nahfeld, wenn man selbst im Guertel steht (Ceres): ein dichter Strom aus ~1200 winzigen Nachbarn als
 *       Lichtpunkte und ~200 etwas groesseren mit erkennbarer Form und Lichtphase, sehr langsam wandernd.</li>
 * </ol>
 *
 * <p>Erwartet den Himmelsrahmen von {@code CelestialSkyRenderer} (Sonne bei (0, 100, 0); Richtung eines Koerpers =
 * Drehung um X um {@code angle}, dann um Z um {@code inclination}, wie in {@code renderCelestials}) und additive Mischung.
 */
public final class AsteroidBeltSkyRenderer {

    private AsteroidBeltSkyRenderer() {}

    private static final int BAND_SEGMENTS = 192;
    private static final double SKY_RADIUS = 100D;
    /** Querschnitt des Bands: Abstand von der Mitte (Anteil der Halbbreite) und relative Helligkeit. */
    private static final double[] BAND_ROWS = {0D, 0.35D, 0.7D, 1D};
    private static final float[] BAND_FALLOFF = {1F, 0.55F, 0.18F, 0F};

    /**
     * Nahfeld (realitaetsnah): sehr viele winzige Nachbarn als Lichtpunkte (wie langsam wandernde Sterne) und einige
     * etwas groessere, bei denen man gerade Form und Lichtphase erkennt. Keine Riesenbrocken.
     */
    private static final int LOCAL_POINTS = 1200, LOCAL_SPRITES = 220;
    private static final int SPRITE_SHAPES = 4;
    /** Lichtphasen im Atlas (Zeilen): Phasenwinkel Sonne-Brocken-Betrachter in Grad (voll ... Sichel). */
    private static final double[] SPRITE_PHASES = {15D, 60D, 100D, 140D};
    private static final int SPRITE_SIZE = 64;

    /** Nahfeld-Asteroid: Laenge (Grad zur Sonne), Breite, Halbgroesse (Himmelseinheiten), Drift je Tick, Form, Farbe. */
    private record LocalRock(double lon0, double lat, double size, double drift, double wobble0, int shape, float r, float g, float b) { }

    private static LocalRock[] localRocks;
    private static ResourceLocation spriteTex;

    /**
     * @param viewer     Koerper, von dem aus gesehen wird (Oberflaeche: der eigene Koerper; Orbit: der umkreiste)
     * @param brightness Sternhelligkeit (Nacht = hell, Tag = 0) mal Sichtbarkeit der Atmosphaere
     * @param airless    keine Atmosphaere (oder Orbit): Himmel auch tagsueber schwarz, Guertel dann auch tagsueber sichtbar
     */
    public static void render(ClientLevel level, float partialTicks, CelestialBody viewer, List<AstroMetric> metrics, float brightness, boolean airless) {
        if (viewer == null || metrics == null) return;
        float light = airless ? Math.max(brightness, 0.8F) : brightness;
        if (light <= 0.01F) return;
        CelestialBody star = viewer.getStar();
        List<AsteroidBelts.Belt> belts = AsteroidBelts.of(star);
        if (belts.isEmpty()) return;

        Vec3 from = null;
        for (AstroMetric metric : metrics) {
            if (metric.body == viewer) {
                from = metric.getPosition();
                break;
            }
        }
        if (from == null) return;

        double gameTicks = (double) level.getGameTime() + partialTicks;
        double ticks = gameTicks * (double) AstronomyUtil.TIME_MULTIPLIER;
        float time = (float) ((gameTicks % 24000D) / 20D);

        // Band-Haelften und Quads haben je nach Blickrichtung unterschiedliche Windung: ohne Culling zeichnen
        SkyGL.disableCull();

        // Nahfeld: im staerksten Guertel, in dem man steht
        double viewerR = Math.sqrt(from.x * from.x + from.y * from.y);
        float insideFactor = 0F;
        for (AsteroidBelts.Belt belt : belts) insideFactor = Math.max(insideFactor, insideFactor(belt, viewerR));
        float local = insideFactor > 0.01F ? insideFactor * light : 0F;

        SkyGL.disableTexture();
        SkyTess.startDrawingQuads();
        for (AsteroidBelts.Belt belt : belts) {
            addBand(belt, from, light);
            addRocks(belt, star, from, ticks, time, light);
        }
        if (local > 0F) addLocalPoints(local, gameTicks, time);
        SkyTess.draw();
        SkyGL.enableTexture();

        if (local > 0F) renderLocalSprites(local, gameTicks);

        SkyGL.enableCull();
        SkyGL.color(1F, 1F, 1F, 1F);
    }

    /** 1 mitten im Guertel, weich auslaufend an Innen- und Aussenrand. */
    private static float insideFactor(AsteroidBelts.Belt belt, double r) {
        double margin = (belt.outerKm() - belt.innerKm()) * 0.12D;
        double in = smoothstep(belt.innerKm() - margin, belt.innerKm() + margin, r);
        double out = 1D - smoothstep(belt.outerKm() - margin, belt.outerKm() + margin, r);
        return (float) (in * out);
    }

    private static double smoothstep(double e0, double e1, double x) {
        double t = Mth.clamp((x - e0) / (e1 - e0), 0D, 1D);
        return t * t * (3D - 2D * t);
    }

    // ── Staubband ──

    /** Leuchtband: Mitte der Guertelbahn, Breite = scheinbare Dicke des Guertels, weich nach aussen ausblendend. */
    private static void addBand(AsteroidBelts.Belt belt, Vec3 from, float light) {
        double mid = (belt.innerKm() + belt.outerKm()) * 0.5D;
        double halfThickness = mid * Math.sin(Math.toRadians(belt.maxInclinationDeg() * 0.5D)) + (belt.outerKm() - belt.innerKm()) * 0.15D;
        float[] a = belt.colorA();
        float[] c = {a[0] * 0.75F + 0.25F, a[1] * 0.75F + 0.25F, a[2] * 0.75F + 0.25F};
        float base = (belt.innerKm() > 100_000_000F ? 0.08F : 0.17F) * light;

        double[] prev = null;
        for (int i = 0; i <= BAND_SEGMENTS; i++) {
            double theta = 2D * Math.PI * i / BAND_SEGMENTS;
            Vec3 ring = new Vec3(mid * Math.cos(theta), mid * Math.sin(theta), 0D);
            double dist = Math.max(1D, ring.distanceTo(from));
            double angle = apparentAngle(from, ring);
            double incl = apparentInclination(from, ring);
            double width = Mth.clamp(Math.toDegrees(Math.atan2(halfThickness, dist)), 0.8D, 24D);
            // zur Sonne hin ausblenden (Blendung), gegenueber der Sonne heller (Rueckstreuung): wirkt wie Material um die Sonne
            double gap = Math.abs(angle);
            double sunSide = smoothstep(30D, 95D, gap) * (0.85D + 0.6D * smoothstep(140D, 180D, gap));
            float alpha = (float) (base * Mth.clamp(mid / dist * 0.7D, 0.15D, 1.4D) * sunSide);
            double[] cur = {angle, incl, width, alpha};
            if (prev != null) {
                for (int side = -1; side <= 1; side += 2) {
                    for (int r = 0; r < BAND_ROWS.length - 1; r++) {
                        Vec3 p0 = dir(prev[0], prev[1] + side * prev[2] * BAND_ROWS[r]);
                        Vec3 p1 = dir(cur[0], cur[1] + side * cur[2] * BAND_ROWS[r]);
                        Vec3 q1 = dir(cur[0], cur[1] + side * cur[2] * BAND_ROWS[r + 1]);
                        Vec3 q0 = dir(prev[0], prev[1] + side * prev[2] * BAND_ROWS[r + 1]);
                        vertex(p0, c, (float) prev[3] * BAND_FALLOFF[r]);
                        vertex(p1, c, (float) cur[3] * BAND_FALLOFF[r]);
                        vertex(q1, c, (float) cur[3] * BAND_FALLOFF[r + 1]);
                        vertex(q0, c, (float) prev[3] * BAND_FALLOFF[r + 1]);
                    }
                }
            }
            prev = cur;
        }
    }

    private static void vertex(Vec3 p, float[] c, float alpha) {
        SkyTess.setColorRGBA_F(c[0], c[1], c[2], alpha);
        SkyTess.addVertex(p.x, p.y, p.z);
    }

    // ── Brocken als Lichtpunkte ──

    /** Brocken als zur Kamera gedrehte Quads; Groesse/Helligkeit nach Naehe (relativ zum mittleren Brockenabstand). */
    private static void addRocks(AsteroidBelts.Belt belt, CelestialBody star, Vec3 from, double ticks, float time, float light) {
        double spacing = belt.spacingKm();
        for (AsteroidBelts.Rock rock : AsteroidBelts.rocks(belt, star)) {
            Vec3 pos = rock.positionAt(ticks);
            double dist = pos.distanceTo(from);
            if (dist < 1D) continue;
            double p = spacing / dist;
            double size = (0.10D + 0.55D * Math.min(p, 1.6D)) * rock.size();
            float twinkle = 0.85F + 0.15F * Mth.sin(time * (1.5F + rock.brightness()) + (float) rock.meanAnomaly0() * 9F);
            double angle = apparentAngle(from, pos);
            float alpha = (float) ((0.22D + 0.78D * Math.min(p, 1D)) * rock.brightness() * twinkle * light * smoothstep(20D, 50D, Math.abs(angle)));
            if (alpha < 0.01F) continue;

            Vec3 u = dir(angle, apparentInclination(from, pos));
            Vec3[] t = tangents(u);
            Vec3 t1 = t[0].scale(size), t2 = t[1].scale(size);
            SkyTess.setColorRGBA_F(Math.min(1F, rock.r() * 1.2F + 0.1F), Math.min(1F, rock.g() * 1.2F + 0.1F), Math.min(1F, rock.b() * 1.2F + 0.1F), Math.min(1F, alpha));
            SkyTess.addVertex(u.x - t1.x - t2.x, u.y - t1.y - t2.y, u.z - t1.z - t2.z);
            SkyTess.addVertex(u.x + t1.x - t2.x, u.y + t1.y - t2.y, u.z + t1.z - t2.z);
            SkyTess.addVertex(u.x + t1.x + t2.x, u.y + t1.y + t2.y, u.z + t1.z + t2.z);
            SkyTess.addVertex(u.x - t1.x + t2.x, u.y - t1.y + t2.y, u.z - t1.z + t2.z);
        }
    }

    // ── Nahfeld (im Guertel stehend) ──

    /** Helligkeit nach Phasenwinkel (0 = der Sonne gegenueber, voll beleuchtet; 180 = Gegenlicht). */
    private static float phaseLight(double phaseAngle) {
        return (float) (0.12D + 0.88D * (1D + Math.cos(Math.toRadians(phaseAngle))) * 0.5D);
    }

    private static boolean thinnedOut(int i, float fill) {
        // am Guertelrand weniger Brocken (gleichmaessig ausgeduennt)
        return (i * 0.6180339887D) % 1D > fill;
    }

    /** Winzige Nachbarn als Lichtpunkte (additiv wie Sterne), Helligkeit nach Lichtphase. */
    private static void addLocalPoints(float strength, double gameTicks, float time) {
        LocalRock[] rocks = localRocks();
        float fill = Mth.clamp(strength, 0F, 1F);
        for (int i = 0; i < LOCAL_POINTS; i++) {
            if (thinnedOut(i, fill)) continue;
            LocalRock rock = rocks[i];
            double lon = Mth.wrapDegrees(rock.lon0() + rock.drift() * gameTicks);
            double elongation = Math.abs(lon);
            float twinkle = 0.9F + 0.1F * Mth.sin(time * 1.3F + (float) rock.wobble0() * 5F);
            float alpha = Math.min(1F, strength * 1.3F) * (float) smoothstep(18D, 40D, elongation) * phaseLight(180D - elongation) * twinkle;
            if (alpha < 0.02F) continue;
            Vec3 u = dir(lon, rock.lat());
            Vec3[] t = tangents(u);
            Vec3 t1 = t[0].scale(rock.size()), t2 = t[1].scale(rock.size());
            SkyTess.setColorRGBA_F(rock.r(), rock.g(), rock.b(), alpha);
            SkyTess.addVertex(u.x - t1.x - t2.x, u.y - t1.y - t2.y, u.z - t1.z - t2.z);
            SkyTess.addVertex(u.x + t1.x - t2.x, u.y + t1.y - t2.y, u.z + t1.z - t2.z);
            SkyTess.addVertex(u.x + t1.x + t2.x, u.y + t1.y + t2.y, u.z + t1.z + t2.z);
            SkyTess.addVertex(u.x - t1.x + t2.x, u.y - t1.y + t2.y, u.z - t1.z + t2.z);
        }
    }

    /** Etwas groessere Nachbarn als Sprites mit Form und Lichtphase (beleuchtete Seite zur Sonne). */
    private static void renderLocalSprites(float strength, double gameTicks) {
        LocalRock[] rocks = localRocks();
        float fill = Mth.clamp(strength, 0F, 1F);
        float baseAlpha = Math.min(1F, strength * 1.5F);
        Vec3 sun = new Vec3(0D, 1D, 0D);

        // Normale Mischung: die Brocken sind fest und verdecken Sterne und Band dahinter
        SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE_MINUS_SRC_ALPHA, SkyGL.GL_ONE, SkyGL.GL_ZERO);
        SkyGL.bind(spriteTexture());
        SkyTess.startDrawingQuads();
        for (int i = LOCAL_POINTS; i < rocks.length; i++) {
            if (thinnedOut(i, fill)) continue;
            LocalRock rock = rocks[i];
            double lon = Mth.wrapDegrees(rock.lon0() + rock.drift() * gameTicks);
            double elongation = Math.abs(lon);
            float alpha = baseAlpha * (float) smoothstep(22D, 48D, elongation);
            if (alpha < 0.01F) continue;

            // Lichtphase: gegenueber der Sonne voll, seitlich halb, zur Sonne hin Sichel
            double phaseAngle = 180D - elongation;
            int phase = phaseAngle < 40D ? 0 : phaseAngle < 80D ? 1 : phaseAngle < 120D ? 2 : 3;

            Vec3 u = dir(lon, rock.lat());
            Vec3 n = u.scale(1D / SKY_RADIUS);
            // Sprite-x (beleuchtete Seite) zeigt am Himmel zur Sonne; leichtes Taumeln
            Vec3 toSun = sun.subtract(n.scale(sun.dot(n)));
            Vec3 ex = toSun.lengthSqr() > 1.0E-6D ? toSun.normalize() : tangents(u)[0];
            Vec3 ey = n.cross(ex);
            double wob = Math.toRadians(9D * Math.sin(rock.wobble0() + gameTicks * 0.004D));
            Vec3 e1 = ex.scale(Math.cos(wob)).add(ey.scale(Math.sin(wob))).scale(rock.size());
            Vec3 e2 = ey.scale(Math.cos(wob)).subtract(ex.scale(Math.sin(wob))).scale(rock.size());

            SkyTess.setColorRGBA_F(rock.r(), rock.g(), rock.b(), alpha);
            double u0 = (double) rock.shape() / SPRITE_SHAPES, u1 = (double) (rock.shape() + 1) / SPRITE_SHAPES;
            double v0 = (double) phase / SPRITE_PHASES.length, v1 = (double) (phase + 1) / SPRITE_PHASES.length;
            SkyTess.addVertexWithUV(u.x - e1.x - e2.x, u.y - e1.y - e2.y, u.z - e1.z - e2.z, u0, v1);
            SkyTess.addVertexWithUV(u.x + e1.x - e2.x, u.y + e1.y - e2.y, u.z + e1.z - e2.z, u1, v1);
            SkyTess.addVertexWithUV(u.x + e1.x + e2.x, u.y + e1.y + e2.y, u.z + e1.z + e2.z, u1, v0);
            SkyTess.addVertexWithUV(u.x - e1.x + e2.x, u.y - e1.y + e2.y, u.z - e1.z + e2.z, u0, v0);
        }
        SkyTess.draw();
        SkyGL.blendFunc(SkyGL.GL_SRC_ALPHA, SkyGL.GL_ONE, SkyGL.GL_ONE, SkyGL.GL_ZERO);
    }

    /**
     * Feste Nahfeld-Brocken: Laenge gleichverteilt, eng um die Bahnebene; Punkte 0,03-0,12 Grad, Sprites 0,15-0,5 Grad.
     * Sehr langsame Drift (groessere = naeher = etwas schneller). Farben: kohlig dunkel, steinig braun, metallisch hell.
     */
    private static LocalRock[] localRocks() {
        if (localRocks == null) {
            Random r = new Random(0xCE7E5L);
            LocalRock[] out = new LocalRock[LOCAL_POINTS + LOCAL_SPRITES];
            for (int i = 0; i < out.length; i++) {
                double u = r.nextDouble();
                double size, latSpread;
                if (i < LOCAL_POINTS) {
                    size = 0.05D + 0.16D * u * u;
                    latSpread = 4.5D;
                } else {
                    size = 0.25D + 0.65D * u * u;
                    latSpread = 7D;
                }
                double lat = Mth.clamp(r.nextGaussian() * latSpread, -40D, 40D);
                double drift = (r.nextBoolean() ? 1D : -1D) * (0.00015D + 0.0012D * size) * (0.7D + r.nextDouble() * 0.6D);
                double type = r.nextDouble();
                float[] c = type < 0.35D ? new float[] {0.50F, 0.48F, 0.46F} : type < 0.85D ? new float[] {0.90F, 0.80F, 0.66F} : new float[] {0.90F, 0.90F, 0.95F};
                float bright = 0.8F + r.nextFloat() * 0.35F;
                out[i] = new LocalRock(r.nextDouble() * 360D - 180D, lat, size, drift, r.nextDouble() * 6.28D, r.nextInt(SPRITE_SHAPES),
                    Math.min(1F, c[0] * bright), Math.min(1F, c[1] * bright), Math.min(1F, c[2] * bright));
            }
            localRocks = out;
        }
        return localRocks;
    }

    /**
     * Sprite-Atlas: 4 Formen (Spalten) x 4 Lichtphasen (Zeilen), je 64 px. Hoehenfeld = verbeulte Kugel mit Kratern
     * (Mulde + Wall) und Rauschen; Normalen aus dem Hoehenfeld, Licht aus +x (zur Sonne) mit dem Phasenwinkel der Zeile.
     */
    private static ResourceLocation spriteTexture() {
        if (spriteTex == null) {
            int n = SPRITE_SIZE;
            NativeImage img = new NativeImage(NativeImage.Format.RGBA, n * SPRITE_SHAPES, n * SPRITE_PHASES.length, false);
            for (int v = 0; v < SPRITE_SHAPES; v++) {
                double[][] h = new double[n + 2][n + 2];
                double[][] edge = new double[n + 2][n + 2];
                asteroidHeight(v, n, h, edge);
                double step = 2D / n;
                for (int p = 0; p < SPRITE_PHASES.length; p++) {
                    double a = Math.toRadians(SPRITE_PHASES[p]);
                    double lx = Math.sin(a), lz = Math.cos(a);
                    for (int y = 0; y < n; y++) {
                        for (int x = 0; x < n; x++) {
                            int px = v * n + x, py = p * n + y;
                            double cov = edge[y + 1][x + 1];
                            if (cov <= 0D) {
                                img.setPixelRGBA(px, py, 0);
                                continue;
                            }
                            double gx = (h[y + 1][x + 2] - h[y + 1][x]) / (2D * step);
                            double gy = (h[y + 2][x + 1] - h[y][x + 1]) / (2D * step);
                            double nx = -gx, ny = -gy, nz = 1D;
                            double len = Math.sqrt(nx * nx + ny * ny + nz * nz);
                            double diffuse = Math.max(0D, (nx * lx + nz * lz) / len);
                            double albedo = 0.82D + 0.3D * hash(px, y, v);
                            double shade = 0.035D + 1.05D * diffuse * albedo;
                            int g = Mth.clamp((int) (shade * 230D), 0, 255);
                            int alpha = Mth.clamp((int) (cov * 255D), 0, 255);
                            img.setPixelRGBA(px, py, alpha << 24 | g << 16 | g << 8 | g); // ABGR (grau, Farbe per Vertex)
                        }
                    }
                }
            }
            DynamicTexture texture = new DynamicTexture(img);
            texture.setFilter(true, false); // weich gefiltert: kleine Sprites flimmern nicht
            spriteTex = Minecraft.getInstance().getTextureManager().register("space_m_asteroid_sprites", texture);
        }
        return spriteTex;
    }

    /** Hoehenfeld einer Form (mit 1 px Rand) und Deckung des Umrisses (weiche Kante). */
    private static void asteroidHeight(int shape, int n, double[][] h, double[][] edge) {
        Random r = new Random(0xA57E401DL + shape * 7919L);
        double p1 = r.nextDouble() * 6.28D, p2 = r.nextDouble() * 6.28D, p3 = r.nextDouble() * 6.28D;
        double k1 = 0.07D + r.nextDouble() * 0.06D, k2 = 0.04D + r.nextDouble() * 0.03D, k3 = 0.015D + r.nextDouble() * 0.02D;
        double stretch = 0.62D + r.nextDouble() * 0.3D;
        double[][] craters = new double[6 + r.nextInt(5)][];
        for (int c = 0; c < craters.length; c++) {
            craters[c] = new double[] {r.nextDouble() * 1.3D - 0.65D, r.nextDouble() * 1.3D - 0.65D, 0.07D + r.nextDouble() * 0.2D};
        }
        for (int y = -1; y <= n; y++) {
            for (int x = -1; x <= n; x++) {
                double dx = (x + 0.5D) / n * 2D - 1D, dy = ((y + 0.5D) / n * 2D - 1D) / stretch;
                double ang = Math.atan2(dy, dx);
                double rad = 0.84D + k1 * Math.sin(2D * ang + p1) + k2 * Math.sin(5D * ang + p2) + k3 * Math.sin(11D * ang + p3);
                double d = Math.sqrt(dx * dx + dy * dy);
                double q = Math.min(1D, d / rad);
                // Kuppel mit fester Hoehe (nicht rad), sonst laufen von der Mitte sternfoermige Kanten aus
                double height = Math.sqrt(Math.max(0D, 1D - q * q)) * 0.84D;
                for (double[] c : craters) {
                    double cd = Math.sqrt((dx - c[0]) * (dx - c[0]) + (dy - c[1]) * (dy - c[1])) / c[2];
                    if (cd < 1D) height -= 0.22D * c[2] * (1D - cd * cd);
                    else if (cd < 1.35D) height += 0.08D * c[2] * (1D - (cd - 1D) / 0.35D);
                }
                height += 0.012D * (hash(x + 1000, y, shape) - 0.5D);
                h[y + 1][x + 1] = height;
                edge[y + 1][x + 1] = Mth.clamp((rad - d) / 0.05D, 0D, 1D);
            }
        }
    }

    private static double hash(int x, int y, int seed) {
        int h = x * 374761393 + y * 668265263 + seed * 1442695041;
        h = (h ^ (h >>> 13)) * 1274126177;
        return ((h ^ (h >>> 16)) & 0xFFFF) / 65535D;
    }

    // ── Richtungen ──

    /** Zwei Tangenten (Einheitslaenge) senkrecht zur Blickrichtung {@code u}. */
    private static Vec3[] tangents(Vec3 u) {
        Vec3 n = u.scale(1D / SKY_RADIUS);
        Vec3 t1 = Math.abs(n.x) < 0.9D ? n.cross(new Vec3(1D, 0D, 0D)).normalize() : n.cross(new Vec3(0D, 1D, 0D)).normalize();
        return new Vec3[] {t1, n.cross(t1)};
    }

    /** Himmelsrichtung (Radius 100) wie {@code rotate(angle, X); rotate(inclination, Z)} auf (0, 100, 0). */
    private static Vec3 dir(double angleDeg, double inclinationDeg) {
        double a = Math.toRadians(angleDeg), i = Math.toRadians(inclinationDeg);
        double ci = Math.cos(i);
        return new Vec3(-SKY_RADIUS * Math.sin(i), SKY_RADIUS * ci * Math.cos(a), SKY_RADIUS * ci * Math.sin(a));
    }

    /** Wie {@code SolarSystem.getApparentAngleDegrees}: Winkel zur Sonnenrichtung in der Bahnebene. */
    private static double apparentAngle(Vec3 from, Vec3 to) {
        double angleToOrigin = Math.atan2(-from.y, -from.x);
        double angleToTarget = Math.atan2(to.y - from.y, to.x - from.x);
        return Mth.wrapDegrees(Math.toDegrees(angleToOrigin - angleToTarget));
    }

    /** Wie {@code SolarSystem.getApparentInclinationDegrees}. */
    private static double apparentInclination(Vec3 from, Vec3 to) {
        double x = from.x - to.x;
        double y = from.y - to.y;
        double planeDistance = Math.sqrt(x * x + y * y);
        double offsetDistance = from.z - to.z;
        return Mth.wrapDegrees(Math.toDegrees(Math.atan2(offsetDistance, planeDistance)));
    }
}
