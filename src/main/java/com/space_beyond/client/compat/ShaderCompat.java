package com.space_beyond.client.compat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.space_m.SpaceMod;
import com.space_m.celestial.CelestialBody;
import com.space_m.celestial.trait.CBT_Atmosphere;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

/**
 * Shader-Unterstuetzung (Iris auf NeoForge, Oculus auf Forge), komplett ueber Reflexion - Space M laeuft ohne beide.
 * <ul>
 *   <li><b>Planeten</b>: Iris waehlt das Programm eines Shaderpacks nach der Dimension; unbekannte Dimensionen bekommen
 *       das Oberwelt-Programm (blauer Himmel, Wolken, Sonne). Ueber {@code IrisDimensionMixin} bekommen Space-Dimensionen
 *       ohne nennenswerte Atmosphaere (Monde, Orbit, unter {@link #AIRLESS_ATM}) stattdessen das End-Programm
 *       ({@code world1}): schwarzer Himmel, keine Wolken, kein Regen. Planeten mit Luft behalten das Oberwelt-Programm.
 *       Ordnet ein Shaderpack die Dimension in seiner {@code dimension.properties} selbst zu, gilt seine Zuordnung.</li>
 *   <li><b>Effekte</b>: {@link #shadowPass()} - leuchtende Effekte (Orbitalstrahlen, Halo, Splitter) werfen keine
 *       Schatten; {@link #shadersActive()} fuer Renderer, die mit Shadern anders zeichnen.</li>
 * </ul>
 */
public final class ShaderCompat {

    private ShaderCompat() {}

    /** Druck (atm), unter dem eine Space-Dimension als luftleer gilt. */
    public static final double AIRLESS_ATM = 0.15D;

    private static boolean apiResolved;
    @Nullable private static Object api;
    @Nullable private static Method inUse, shadow;

    private static void resolveApi() {
        if (apiResolved) return;
        apiResolved = true;
        try {
            Class<?> c = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            api = c.getMethod("getInstance").invoke(null);
            inUse = c.getMethod("isShaderPackInUse");
            shadow = c.getMethod("isRenderingShadowPass");
        } catch (Throwable t) {
            api = null;
        }
    }

    /** Ist gerade ein Shaderpack aktiv? */
    public static boolean shadersActive() {
        resolveApi();
        if (api == null || inUse == null) return false;
        try {
            return (Boolean) inUse.invoke(api);
        } catch (Throwable t) {
            return false;
        }
    }

    /** Laeuft gerade der Schattendurchgang des Shaderpacks? */
    public static boolean shadowPass() {
        resolveApi();
        if (api == null || shadow == null) return false;
        try {
            return (Boolean) shadow.invoke(api);
        } catch (Throwable t) {
            return false;
        }
    }

    // ── Dimensionszuordnung (aus IrisDimensionMixin) ────────────────────────

    @Nullable private static ClientLevel cachedLevel;
    private static long cachedAt = Long.MIN_VALUE;
    private static boolean cachedAirless;
    @Nullable private static Constructor<?> idCtor;
    @Nullable private static Object endId, ownId;
    @Nullable private static ClientLevel ownLevel;

    /**
     * Ersatz fuer {@code Iris.getCurrentDimension()}: liefert die End-Kennung, wenn die aktuelle Space-Dimension
     * luftleer ist und das Shaderpack sie nicht selbst zuordnet; sonst {@code null} (Iris-Ergebnis bleibt).
     */
    @Nullable
    public static Object endDimensionFor(@Nullable Object irisResult) {
        if (irisResult == null) return null;
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || !isSpaceNamespace(level.dimension().location().getNamespace())) return null;
        try {
            if (endId == null || idCtor == null || idCtor.getDeclaringClass() != irisResult.getClass()) {
                idCtor = irisResult.getClass().getConstructor(String.class, String.class);
                endId = idCtor.newInstance("minecraft", "the_end");
                ownId = null;
            }
            // nur ersetzen, wenn Iris die eigene Kennung der Dimension liefert (keine Effekt-Umleitung)
            if (ownLevel != level || ownId == null) {
                ownLevel = level;
                ownId = idCtor.newInstance(level.dimension().location().getNamespace(), level.dimension().location().getPath());
            }
            if (!ownId.equals(irisResult)) return null;
            if (!override(level, irisResult)) return null;
            return endId;
        } catch (Throwable t) {
            return null;
        }
    }

    private static boolean irisResolved;
    @Nullable private static Class<?> iris;

    @Nullable
    private static Class<?> irisClass() {
        if (!irisResolved) {
            irisResolved = true;
            for (String name : new String[] {"net.irisshaders.iris.Iris", "net.coderbot.iris.Iris"}) {
                try {
                    iris = Class.forName(name);
                    break;
                } catch (Throwable ignored) {
                }
            }
        }
        return iris;
    }

    private static boolean packMapsExplicitly(Class<?> irisClass, Object id) {
        try {
            Object pack = ((Optional<?>) irisClass.getMethod("getCurrentPack").invoke(null)).orElse(null);
            if (pack == null) return false;
            Map<?, ?> map = (Map<?, ?>) pack.getClass().getMethod("getDimensionMap").invoke(pack);
            return map != null && map.containsKey(id);
        } catch (Throwable t) {
            return false;
        }
    }

    @Nullable private static ClientLevel overrideLevel;
    private static long overrideAt = Long.MIN_VALUE;
    private static boolean overrideCached;

    /** Luftleer und vom Pack nicht selbst zugeordnet? (alle 5 s neu: Terraforming, Packwechsel). */
    private static boolean override(ClientLevel level, Object irisId) {
        long now = level.getGameTime();
        if (level != overrideLevel || now - overrideAt > 100 || now < overrideAt) {
            overrideLevel = level;
            overrideAt = now;
            Class<?> irisClass = irisClass();
            overrideCached = airless(level) && !(irisClass != null && packMapsExplicitly(irisClass, irisId));
        }
        return overrideCached;
    }

    /** Luftleer? (Druck unter {@link #AIRLESS_ATM}, alle 5 s neu). */
    public static boolean airless(ClientLevel level) {
        long now = level.getGameTime();
        if (level != cachedLevel || now - cachedAt > 100 || now < cachedAt) {
            cachedLevel = level;
            cachedAt = now;
            CBT_Atmosphere atmosphere;
            try {
                atmosphere = CelestialBody.getTrait(level, CBT_Atmosphere.class);
            } catch (Throwable t) {
                atmosphere = null;
            }
            cachedAirless = atmosphere == null || atmosphere.getPressure() < AIRLESS_ATM;
        }
        return cachedAirless;
    }

    /** Dimensionen von Space Modernized und Beyond (Sporos/Wemburos liegen im Namensraum space_beyond). */
    private static boolean isSpaceNamespace(String namespace) {
        return SpaceMod.MOD_ID.equals(namespace) || com.space_beyond.BeyondMod.MOD_ID.equals(namespace);
    }
}
