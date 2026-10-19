package com.space_beyond.entity.mob.stage.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.space_beyond.entity.mob.stage.PosedMob;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * Gemeinsames Modell der Stufen-Bosse und Dungeon-Mobs: Teile, Rollen und Posen kommen aus {@link StageBossModels}.
 * <ul>
 *   <li>Grundbewegung nach Rolle eines Teils (Kopf folgt dem Blick, Beine laufen, Ketten schlaengeln, Haende schweben,
 *       Splitter kreisen ...), in der Wut (Phase 3) schneller.</li>
 *   <li>Schluesselposen ({@link Key}): je Angriff (Index wie {@link PosedMob#anim()}) Drehungen einzelner Teile
 *       ueber die Zeit (Ausholen, Schlag, Erholung), linear interpoliert; Index 0 ist die Ruhepose, solange der Boss
 *       schlaeft. Hat ein Angriff keine Posen, hebt das Modell allgemein die Arme und oeffnet den Kiefer.</li>
 * </ul>
 */
public class StageBossModel<T extends LivingEntity & PosedMob> extends HierarchicalModel<T> {

    public record Role(String path, String kind, float param) { }

    /** Schluessel: Angriff {@code anim}, Zeitpunkt (Ticks), Teil (Pfad), zusaetzliche Drehung in Grad. */
    public record Key(int anim, float t, String path, float rx, float ry, float rz) { }

    private record Bound(ModelPart part, String kind, float param) { }

    private record Track(ModelPart part, float[] t, float[][] rot) { }

    private final ModelPart root;
    private final List<Bound> parts = new ArrayList<>();
    private final Map<Integer, List<Track>> tracks = new HashMap<>();

    public StageBossModel(ModelPart root, Role[] roles, Key[] keys) {
        this.root = root;
        for (Role r : roles) parts.add(new Bound(part(r.path()), r.kind(), r.param()));
        Map<Integer, Map<String, List<Key>>> byAnim = new HashMap<>();
        for (Key k : keys) byAnim.computeIfAbsent(k.anim(), a -> new HashMap<>()).computeIfAbsent(k.path(), p -> new ArrayList<>()).add(k);
        for (var e : byAnim.entrySet()) {
            List<Track> list = new ArrayList<>();
            for (var pk : e.getValue().entrySet()) {
                List<Key> ks = pk.getValue();
                ks.sort((a, b) -> Float.compare(a.t(), b.t()));
                float[] t = new float[ks.size()];
                float[][] rot = new float[ks.size()][];
                for (int i = 0; i < ks.size(); i++) {
                    Key k = ks.get(i);
                    t[i] = k.t();
                    rot[i] = new float[] {k.rx() * Mth.DEG_TO_RAD, k.ry() * Mth.DEG_TO_RAD, k.rz() * Mth.DEG_TO_RAD};
                }
                list.add(new Track(part(pk.getKey()), t, rot));
            }
            tracks.put(e.getKey(), list);
        }
    }

    private ModelPart part(String path) {
        ModelPart p = root;
        for (String name : path.split("/")) p = p.getChild(name);
        return p;
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(T e, float limbSwing, float limbAmount, float age, float headYaw, float headPitch) {
        root.getAllParts().forEach(ModelPart::resetPose);
        boolean awake = e.awake();
        int anim = e.anim();
        boolean posed = anim > 0 && tracks.containsKey(anim);
        float pt = age - e.tickCount;
        float at = e.animTime(pt);
        float lift = anim > 0 && !posed ? Mth.clamp(at / 8.0F, 0F, 1F) : 0F;
        float rage = e.phaseThree() ? 2.0F : e.phaseTwo() ? 1.5F : 1.0F;
        float walk = Mth.cos(limbSwing * 0.55F) * 0.7F * limbAmount;
        for (Bound b : parts) {
            ModelPart p = b.part();
            float k = b.param();
            switch (b.kind()) {
                case "head" -> {
                    if (awake) {
                        p.yRot += headYaw * Mth.DEG_TO_RAD * 0.8F;
                        p.xRot += headPitch * Mth.DEG_TO_RAD * 0.6F - lift * 0.25F;
                    }
                }
                case "body" -> p.xRot += Mth.sin(age * 0.05F * rage) * 0.03F;
                case "arm_r" -> {
                    p.xRot += -walk + Mth.sin(age * 0.06F) * 0.05F - lift * 2.1F;
                    p.zRot += lift * 0.3F;
                }
                case "arm_l" -> {
                    p.xRot += walk - Mth.sin(age * 0.06F) * 0.05F - lift * 1.4F;
                    p.zRot += -lift * 0.3F;
                }
                case "leg_r", "leg_a" -> p.xRot += walk;
                case "leg_l", "leg_b" -> p.xRot -= walk;
                case "bob" -> p.y += Mth.sin(age * 0.08F) * 1.5F;
                case "orbit" -> {
                    float a = age * 0.06F * rage * (anim > 0 ? 2.0F : 1.0F) + k * Mth.TWO_PI / 6.0F;
                    float r = anim > 0 ? 14.0F : 18.0F;
                    p.x = Mth.cos(a) * r;
                    p.z = Mth.sin(a) * r;
                    p.y += Mth.sin(age * 0.1F + k) * 3.0F;
                    p.yRot = a * 2.0F;
                }
                case "hand" -> {
                    p.y += Mth.sin(age * 0.09F + k * 2.0F) * 2.0F - lift * 6.0F;
                    p.z += -lift * 4.0F;
                    p.xRot += -lift * 0.8F;
                }
                case "tentacle" -> {
                    p.xRot += Mth.sin(age * 0.12F * rage + k) * 0.3F;
                    p.zRot += Mth.cos(age * 0.1F * rage + k * 1.3F) * 0.25F;
                }
                case "chain" -> {
                    p.yRot += Mth.sin(age * 0.15F * rage - k * 0.7F) * 0.28F;
                    p.xRot += Mth.cos(age * 0.11F - k * 0.5F) * 0.1F;
                }
                case "neck" -> {
                    p.yRot += Mth.sin(age * 0.07F + k) * 0.16F;
                    p.xRot += Mth.cos(age * 0.09F + k) * 0.1F - lift * 0.25F;
                }
                case "jaw" -> p.xRot += 0.06F + Math.max(lift * 0.6F, Math.max(0F, Mth.sin(age * 0.05F)) * 0.12F);
                case "wing_r" -> p.zRot += Mth.sin(age * 0.18F * rage) * 0.45F;
                case "wing_l" -> p.zRot -= Mth.sin(age * 0.18F * rage) * 0.45F;
                default -> { }
            }
        }
        if (!awake) apply(0, 0F, 1F);
        else if (posed) apply(anim, at, 1F);
    }

    /** Posen eines Angriffs zum Zeitpunkt {@code t} addieren. */
    private void apply(int anim, float t, float weight) {
        List<Track> list = tracks.get(anim);
        if (list == null) return;
        for (Track tr : list) {
            float[] ts = tr.t();
            float[] r;
            if (t <= ts[0]) r = tr.rot()[0];
            else if (t >= ts[ts.length - 1]) r = tr.rot()[ts.length - 1];
            else {
                int i = 1;
                while (ts[i] < t) i++;
                float f = (t - ts[i - 1]) / Math.max(1e-3F, ts[i] - ts[i - 1]);
                float[] a = tr.rot()[i - 1], b = tr.rot()[i];
                r = new float[] {Mth.lerp(f, a[0], b[0]), Mth.lerp(f, a[1], b[1]), Mth.lerp(f, a[2], b[2])};
            }
            tr.part().xRot += r[0] * weight;
            tr.part().yRot += r[1] * weight;
            tr.part().zRot += r[2] * weight;
        }
    }
}
