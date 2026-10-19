package com.space_beyond.client.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.hbm_m.client.render.SimpleObjModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.space_m.main.SpaceRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

/**
 * Bruecke vom immediate {@link SimpleObjModel} (Original {@code AdvancedModelLoader}) zu Raptors VBO-Renderpfad
 * ({@code com.hbm_m.client.render.machine.MachineRenderers}): Die gewaehlten OBJ-Gruppen werden genau so gezeichnet
 * wie bisher ({@code SimpleObjModel.renderPart}, gleiche Dreiecke, gleiche UV-Spiegelung), aber in einen
 * Erfassungspuffer statt auf den Bildschirm. Daraus entstehen {@link BakedQuad}s im Blockatlas (Raptors Pfad
 * bindet nur den Blockatlas), die das Fabrik-Spec ueber {@code dynamicPart(...)} als Teilgeometrie bekommt.
 *
 * <p>Die Modelltextur muss deshalb im Blockatlas stehen: {@code textures/block/**} ist es von Haus aus, alles
 * andere ({@code textures/models/**}) ueber {@code assets/minecraft/atlases/blocks.json} ("single").
 *
 * <p>UVs ausserhalb 0..1 (Original: GL_REPEAT) werden an den Kachelgrenzen zerschnitten und zurueckverschoben,
 * damit im Atlas kein Nachbarsprite durchscheint. Eingebackene Transformationen ({@link #baked}) duerfen auch
 * skalieren; die Normalen werden danach neu normiert.
 *
 * <p>Neuladen (F3+T, Ressourcenpaket): Der Atlas vergibt neue Sprite-Objekte. Aendert sich das Sprite, wird die
 * Geometrie neu gebacken und {@link #cacheKey()} bekommt eine neue Generation, damit Raptors VBO-Cache die
 * Teilgeometrie neu baut (er kennt nur den Schluessel). Solange das OBJ noch nicht lesbar ist (Dev-Client kopiert
 * Ressourcen), liefert der Schluessel einen sekundenweise wechselnden "pending"-Wert, damit kein leerer VBO
 * dauerhaft haengen bleibt (sonst unsichtbare Maschine bis zum Neustart).
 *
 * <p>Nur Render-Thread.
 */
public final class ObjAtlasMesh {

    /** Zeichnet die gewuenschten Gruppen eines Modells (beliebige {@code renderPart}-Aufrufe mit Posen). */
    @FunctionalInterface
    public interface Drawer {
        void draw(SimpleObjModel model, PoseStack pose, VertexConsumer vc);
    }

    private final SimpleObjModel model;
    private final ResourceLocation spriteId;
    private final Drawer drawer;
    private final String key;

    @Nullable private TextureAtlasSprite sprite;
    @Nullable private List<BakedQuad> quads;
    private int generation;
    private String readyKey = "";
    private boolean warnedMissing;

    private ObjAtlasMesh(SimpleObjModel model, ResourceLocation spriteId, Drawer drawer, String key) {
        this.model = model;
        this.spriteId = spriteId;
        this.drawer = drawer;
        this.key = key;
    }

    private static Drawer filtered(Predicate<String> partFilter) {
        return (m, pose, vc) -> {
            for (String part : List.copyOf(m.getPartNames())) {
                if (partFilter.test(part)) m.renderPart(part, pose, vc, 0);
            }
        };
    }

    /** Alle Gruppen ({@code renderAll}). */
    public static ObjAtlasMesh all(SimpleObjModel model, ResourceLocation texture, String key) {
        return new ObjAtlasMesh(model, spriteIdOf(texture), filtered(p -> true), key);
    }

    /** Genau diese Gruppen ({@code renderPart}, Gross-/Kleinschreibung exakt wie {@code SimpleObjModel}). */
    public static ObjAtlasMesh parts(SimpleObjModel model, ResourceLocation texture, String key, String... names) {
        List<String> list = List.of(names);
        return new ObjAtlasMesh(model, spriteIdOf(texture), filtered(list::contains), key);
    }

    /** Gruppen ohne Gross-/Kleinschreibung ({@code HFRWavefrontObject.renderPart}). */
    public static ObjAtlasMesh partsIgnoreCase(SimpleObjModel model, ResourceLocation texture, String key, String... names) {
        return new ObjAtlasMesh(model, spriteIdOf(texture), filtered(p -> matchesIgnoreCase(p, names)), key);
    }

    /** Alle Gruppen ausser diesen, ohne Gross-/Kleinschreibung ({@code HFRWavefrontObject.renderAllExcept}). */
    public static ObjAtlasMesh allExceptIgnoreCase(SimpleObjModel model, ResourceLocation texture, String key, String... excluded) {
        return new ObjAtlasMesh(model, spriteIdOf(texture), filtered(p -> !matchesIgnoreCase(p, excluded)), key);
    }

    /**
     * Frei gezeichnete Geometrie: der {@link Drawer} macht dieselben {@code renderPart}-Aufrufe mit denselben Posen
     * wie der alte BER (z.B. Turmsegmente je Hoehe, Anschluesse je Nachbar). Der Schluessel muss den Zustand
     * eindeutig beschreiben, aus dem die Geometrie entsteht.
     */
    public static ObjAtlasMesh drawn(SimpleObjModel model, ResourceLocation texture, String key, Drawer drawer) {
        return new ObjAtlasMesh(model, spriteIdOf(texture), drawer, key);
    }

    /**
     * Gleiche Gruppen, aber mit fest eingebackener Transformation (z.B. eine Facing-Drehung, die im Original nur
     * fuer dieses Teil gilt). Die Transformation landet in den Eckpunkten, deshalb ist hier auch eine Skalierung
     * verlustfrei (Raptors Instancing selbst kennt keine Skalierung).
     */
    public ObjAtlasMesh baked(Consumer<PoseStack> transform, String variantKey) {
        Drawer inner = drawer;
        return new ObjAtlasMesh(model, spriteId, (m, pose, vc) -> {
            transform.accept(pose);
            inner.draw(m, pose, vc);
        }, key + "/" + variantKey);
    }

    /** Teilgeometrie fuer {@code QuadResolver} (leer, solange Modell/Atlas nicht bereit sind). */
    public List<BakedQuad> quads() {
        refresh();
        return quads == null ? List.of() : quads;
    }

    /** Schluessel fuer {@code dynamicPart(..., cacheKeyFn)}. */
    public String cacheKey() {
        refresh();
        if (quads == null) return key + "#pending" + (System.currentTimeMillis() / 1000L);
        return readyKey;
    }

    private void refresh() {
        TextureAtlasSprite current = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteId);
        if (current != sprite) {
            sprite = current;
            quads = null;
            generation++;
            readyKey = key + "#" + generation;
            if (!warnedMissing && MissingTextureAtlasSprite.getLocation().equals(current.contents().name())) {
                warnedMissing = true;
                SpaceRegistry.LOGGER.warn("[space_m] ObjAtlasMesh {}: Sprite {} fehlt im Blockatlas (atlases/blocks.json?)", key, spriteId);
            }
        }
        if (quads == null) {
            List<BakedQuad> baked = bake(current);
            quads = baked.isEmpty() ? null : baked;
        }
    }

    private List<BakedQuad> bake(TextureAtlasSprite atlasSprite) {
        Capture capture = new Capture();
        drawer.draw(model, new PoseStack(), capture);
        List<float[]> v = capture.vertices;
        int count = v.size() / 4;
        List<BakedQuad> out = new ArrayList<>(count);
        float u0 = atlasSprite.getU0(), du = atlasSprite.getU1() - u0;
        float v0 = atlasSprite.getV0(), dv = atlasSprite.getV1() - v0;
        List<float[]> poly = new ArrayList<>(4);
        for (int q = 0; q < count; q++) {
            poly.clear();
            for (int i = 0; i < 4; i++) {
                float[] p = v.get(q * 4 + i);
                if (poly.isEmpty() || !Arrays.equals(poly.get(poly.size() - 1), p)) poly.add(p);
            }
            if (poly.size() > 1 && Arrays.equals(poly.get(0), poly.get(poly.size() - 1))) poly.remove(poly.size() - 1);
            if (poly.size() < 3) continue;
            emitWrapped(poly, out, atlasSprite, u0, du, v0, dv);
        }
        return List.copyOf(out);
    }

    private static final float EPS = 1.0E-4F;

    /**
     * Die Modelltexturen wurden im Original wiederholend gesampelt (GL_REPEAT); einige Modelle nutzen UVs knapp
     * ausserhalb von 0..1 ({@code dish_controller} v bis 1,05, {@code dyson_spinlaunch} u ab -1,01,
     * {@code air_scrubber} v bis 1,19). Im Atlas wuerden diese Flaechen das Nachbarsprite zeigen. Deshalb: liegt
     * das Polygon in einer UV-Kachel, wird es ganzzahlig zurueckverschoben; ueberspannt es eine Kachelgrenze, wird
     * es an den Grenzen zerschnitten und jedes Stueck einzeln verschoben (= exakt das Wiederholungsbild).
     */
    private static void emitWrapped(List<float[]> poly, List<BakedQuad> out, TextureAtlasSprite sprite,
                                    float u0, float du, float v0, float dv) {
        float umin = Float.MAX_VALUE, umax = -Float.MAX_VALUE, vmin = Float.MAX_VALUE, vmax = -Float.MAX_VALUE;
        for (float[] p : poly) {
            umin = Math.min(umin, p[3]);
            umax = Math.max(umax, p[3]);
            vmin = Math.min(vmin, p[4]);
            vmax = Math.max(vmax, p[4]);
        }
        int cu0 = (int) Math.floor(umin + EPS), cu1 = Math.max(cu0, (int) Math.floor(umax - EPS));
        int cv0 = (int) Math.floor(vmin + EPS), cv1 = Math.max(cv0, (int) Math.floor(vmax - EPS));
        if (cu0 == cu1 && cv0 == cv1) {
            emit(poly, cu0, cv0, out, sprite, u0, du, v0, dv);
            return;
        }
        for (int cu = cu0; cu <= cu1; cu++) {
            for (int cv = cv0; cv <= cv1; cv++) {
                List<float[]> piece = clip(poly, 3, cu, true);
                piece = clip(piece, 3, cu + 1, false);
                piece = clip(piece, 4, cv, true);
                piece = clip(piece, 4, cv + 1, false);
                if (piece.size() >= 3) emit(piece, cu, cv, out, sprite, u0, du, v0, dv);
            }
        }
    }

    /** Sutherland-Hodgman an einer UV-Achse ({@code idx} 3 = u, 4 = v): behaelt {@code >= limit} bzw. {@code <= limit}. */
    private static List<float[]> clip(List<float[]> in, int idx, float limit, boolean keepAbove) {
        List<float[]> res = new ArrayList<>(in.size() + 2);
        int n = in.size();
        for (int i = 0; i < n; i++) {
            float[] a = in.get(i), b = in.get((i + 1) % n);
            boolean ain = keepAbove ? a[idx] >= limit : a[idx] <= limit;
            boolean bin = keepAbove ? b[idx] >= limit : b[idx] <= limit;
            if (ain) res.add(a);
            if (ain != bin) {
                float t = (limit - a[idx]) / (b[idx] - a[idx]);
                float[] c = new float[8];
                for (int k = 0; k < 8; k++) c[k] = a[k] + (b[k] - a[k]) * t;
                c[idx] = limit;
                res.add(c);
            }
        }
        return res;
    }

    /**
     * Polygon als Vierecke (ein echtes Viereck bleibt eines, sonst Faecher entarteter Vierecke wie
     * {@code SimpleObjModel}), UV um die Kachel {@code (cu, cv)} verschoben.
     */
    private static void emit(List<float[]> poly, int cu, int cv, List<BakedQuad> out, TextureAtlasSprite sprite,
                             float u0, float du, float v0, float dv) {
        if (poly.size() == 4) {
            out.add(quad(poly.get(0), poly.get(1), poly.get(2), poly.get(3), cu, cv, sprite, u0, du, v0, dv));
            return;
        }
        for (int t = 1; t < poly.size() - 1; t++) {
            out.add(quad(poly.get(0), poly.get(t), poly.get(t + 1), poly.get(t + 1), cu, cv, sprite, u0, du, v0, dv));
        }
    }

    private static BakedQuad quad(float[] a, float[] b, float[] c, float[] d, int cu, int cv, TextureAtlasSprite sprite,
                                  float u0, float du, float v0, float dv) {
        float[][] corners = { a, b, c, d };
        int[] data = new int[32];
        float nxSum = 0, nySum = 0, nzSum = 0;
        for (int i = 0; i < 4; i++) {
            float[] p = corners[i];
            int o = i * 8;
            float nx = p[5], ny = p[6], nz = p[7];
            float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
            if (len > 1.0E-6F) {
                nx /= len;
                ny /= len;
                nz /= len;
            }
            float u = Math.min(1F, Math.max(0F, p[3] - cu));
            float v = Math.min(1F, Math.max(0F, p[4] - cv));
            data[o] = Float.floatToRawIntBits(p[0]);
            data[o + 1] = Float.floatToRawIntBits(p[1]);
            data[o + 2] = Float.floatToRawIntBits(p[2]);
            data[o + 3] = -1;
            data[o + 4] = Float.floatToRawIntBits(u0 + u * du);
            data[o + 5] = Float.floatToRawIntBits(v0 + v * dv);
            data[o + 6] = 0;
            data[o + 7] = packNormal(nx, ny, nz);
            nxSum += nx;
            nySum += ny;
            nzSum += nz;
        }
        Direction dir = Direction.getNearest(nxSum, nySum, nzSum);
        return new BakedQuad(data, -1, dir, sprite, true);
    }

    private static int packNormal(float x, float y, float z) {
        return ((byte) Math.round(x * 127F) & 0xFF)
                | (((byte) Math.round(y * 127F) & 0xFF) << 8)
                | (((byte) Math.round(z * 127F) & 0xFF) << 16);
    }

    private static boolean matchesIgnoreCase(String part, String[] names) {
        for (String n : names) if (n.equalsIgnoreCase(part)) return true;
        return false;
    }

    /** {@code ns:textures/x/y.png} -> Atlas-Sprite {@code ns:x/y}; ohne Praefix/Endung unveraendert. */
    static ResourceLocation spriteIdOf(ResourceLocation texture) {
        String path = texture.getPath();
        if (path.startsWith("textures/")) path = path.substring("textures/".length());
        if (path.endsWith(".png")) path = path.substring(0, path.length() - 4);
        return ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), path);
    }

    /** Erfasst x, y, z, u, v, nx, ny, nz je Eckpunkt (SimpleObjModel schreibt Dreiecke als entartete Vierecke). */
    private static final class Capture implements VertexConsumer {
        final List<float[]> vertices = new ArrayList<>();
        private float[] cur = new float[8];

        private VertexConsumer begin(float x, float y, float z) {
            cur = new float[8];
            cur[0] = x;
            cur[1] = y;
            cur[2] = z;
            vertices.add(cur);
            return this;
        }

        //? if < 1.21.1 {
        @Override public VertexConsumer vertex(double x, double y, double z) { return begin((float) x, (float) y, (float) z); }
        @Override public VertexConsumer color(int r, int g, int b, int a) { return this; }
        @Override public VertexConsumer uv(float u, float v) { cur[3] = u; cur[4] = v; return this; }
        @Override public VertexConsumer overlayCoords(int u, int v) { return this; }
        @Override public VertexConsumer uv2(int u, int v) { return this; }
        @Override public VertexConsumer normal(float x, float y, float z) { cur[5] = x; cur[6] = y; cur[7] = z; return this; }
        @Override public void endVertex() { }
        @Override public void defaultColor(int r, int g, int b, int a) { }
        @Override public void unsetDefaultColor() { }
        //?} else {
        /*@Override public VertexConsumer addVertex(float x, float y, float z) { return begin(x, y, z); }
        @Override public VertexConsumer setColor(int r, int g, int b, int a) { return this; }
        @Override public VertexConsumer setUv(float u, float v) { cur[3] = u; cur[4] = v; return this; }
        @Override public VertexConsumer setUv1(int u, int v) { return this; }
        @Override public VertexConsumer setUv2(int u, int v) { return this; }
        @Override public VertexConsumer setNormal(float x, float y, float z) { cur[5] = x; cur[6] = y; cur[7] = z; return this; }
        *///?}
    }
}
