package com.space_beyond.entity.mob.client;

import org.joml.Matrix4f;

import com.hbm_m.platform.RenderHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.space_m.SpaceMod;
import com.space_beyond.entity.mob.boss.EntityUnstableSovereign;
import com.space_beyond.entity.mob.boss.client.OrbitalBeamRenderer;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Renderer des Bosses {@link EntityUnstableSovereign}: eigenes Wuerfelmodell (doppelte Groesse, ~4,6 Bloecke mit
 * Krone) - schwebender Herrscher mit Gewand statt Beinen, Umhang, Goldhandschuhen, Zepter mit Kristallkugel, Krone,
 * Kristallfluegeln aus je drei leuchtenden Splittern, Rueckenkristallen, wehenden Gewandranken und drei kreisenden
 * Splittern. Dazu frei gezeichnet: rotierender Runen-Halo hinter dem Kopf (Phase 2: doppelt, gold), Runenkreis am Boden.
 * <p>Jeder Zustand hat eine eigene Pose: gesenkter Kopf auf dem Thron, ausgebreitete Arme beim Erwachen und
 * Phasenwechsel, nach vorn gelehnt beim Sturmlauf, beide Arme hoch beim Aufstieg, Faeuste nach unten beim Sturz,
 * erhobenes Zepter beim Zaubern; im Tod dreht er sich, schrumpft und zerfaellt.
 * Textur {@code entity/unstable_sovereign.png} (128 x 128), Leuchtschicht {@code entity/unstable_sovereign_glow.png}.
 */
public class RenderUnstableSovereign extends MobRenderer<EntityUnstableSovereign, RenderUnstableSovereign.Model> {

    public static final ResourceLocation TEXTURE = com.space_beyond.BeyondMod.id("textures/entity/unstable_sovereign.png");
    public static final ResourceLocation GLOW = com.space_beyond.BeyondMod.id("textures/entity/unstable_sovereign_glow.png");
    private static final int LIGHT = 0xF000F0;

    public RenderUnstableSovereign(EntityRendererProvider.Context ctx) {
        super(ctx, new Model(Model.createBodyLayer().bakeRoot()), 1.4F);
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(GLOW);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(EntityUnstableSovereign entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(EntityUnstableSovereign e, PoseStack ps, float pt) {
        int state = e.state();
        float st = e.stateTime(pt);
        float d = e.dying(pt);
        // Neigung (im gespiegelten Modellraum: positiv = nach vorn)
        float lean = switch (state) {
            case EntityUnstableSovereign.DASH -> 0.55F;
            case EntityUnstableSovereign.DASH_WIND -> -0.2F * Math.min(1.0F, st / 8.0F);
            case EntityUnstableSovereign.SLAM -> 0.25F;
            case EntityUnstableSovereign.RISE -> -0.15F;
            default -> 0.0F;
        };
        ps.translate(0.0D, -2.2D, 0.0D);
        ps.mulPose(Axis.XP.rotation(lean));
        if (d > 0) ps.mulPose(Axis.YP.rotationDegrees(d * d * 900.0F));
        ps.translate(0.0D, 2.2D, 0.0D);
        float s = 2.0F * (1.0F - d * 0.35F);
        ps.scale(s, s, s);
        ps.translate(0.0D, -e.hover(pt) - (d > 0 ? Mth.sin(e.tickCount * 3.1F) * 0.02F : 0), 0.0D);
    }

    @Override
    public void render(EntityUnstableSovereign e, float yaw, float pt, PoseStack ps, MultiBufferSource buffers, int light) {
        super.render(e, yaw, pt, ps, buffers, light);
        if (com.space_beyond.client.compat.ShaderCompat.shadowPass()) return; // Halo und Runenkreis werfen keinen Schatten
        float age = e.tickCount + pt;
        float d = e.dying(pt);
        boolean p2 = e.phaseTwo();
        int state = e.state();
        float body = Mth.rotLerp(pt, e.yBodyRotO, e.yBodyRot);

        // Halo hinter dem Kopf
        if (state != EntityUnstableSovereign.IDLE || d > 0) {
            ps.pushPose();
            ps.mulPose(Axis.YP.rotationDegrees(180.0F - body));
            float scale = 1.0F - d * 0.35F;
            ps.translate(0.0D, (3.75D + e.hover(pt) * 2.0F) * scale, 0.75D * scale);
            float r = (p2 ? 1.25F : 1.0F) * (1.0F + d * 1.5F) * scale;
            float alpha = 1.0F - d;
            halo(ps, buffers, r, age * 2.0F, 1.0F, 0.78F, 0.3F, alpha);
            if (p2) halo(ps, buffers, r * 0.68F, -age * 3.5F, 0.85F, 0.35F, 1.0F, alpha);
            ps.popPose();
        }

        // Runenkreis am Boden unter ihm
        if (state != EntityUnstableSovereign.IDLE) {
            double floor = floorBelow(e);
            if (floor > -1) {
                ps.pushPose();
                ps.translate(0.0D, -floor + 0.06D, 0.0D);
                float r = 3.0F + Mth.sin(age * 0.1F) * 0.2F + (state == EntityUnstableSovereign.SHIFT ? Math.min(4.0F, e.stateTime(pt) * 0.15F) : 0);
                floorRing(ps, buffers, r, age * 1.2F, p2 ? 1.0F : 0.75F, p2 ? 0.7F : 0.3F, p2 ? 0.35F : 1.0F, 0.6F * (1.0F - d));
                ps.popPose();
            }
        }
    }

    /** Abstand bis zum Boden unter dem Boss (max. 10 Bloecke), -1 wenn keiner. */
    private static double floorBelow(EntityUnstableSovereign e) {
        BlockPos p = e.blockPosition();
        for (int i = 0; i <= 10; i++) {
            BlockPos q = p.below(i);
            if (!e.level().getBlockState(q).getCollisionShape(e.level(), q).isEmpty()) {
                return e.getY() - (q.getY() + 1);
            }
        }
        return -1;
    }

    private static int c(float v) {
        return Mth.clamp((int) (v * 255), 0, 255);
    }

    /** Stehender Runenkreis (zur Kamera hin vorne/hinten sichtbar). */
    private static void halo(PoseStack ps, MultiBufferSource buffers, float r, float rotDeg, float red, float green, float blue, float a) {
        ps.pushPose();
        ps.mulPose(Axis.ZP.rotationDegrees(rotDeg));
        VertexConsumer vc = buffers.getBuffer(RenderType.eyes(OrbitalBeamRenderer.RING));
        Matrix4f m = ps.last().pose();
        int R = c(red * a), G = c(green * a), B = c(blue * a), A = c(a);
        quad(vc, m, -r, -r, 0, r, -r, 0, r, r, 0, -r, r, 0, R, G, B, A);
        ps.popPose();
    }

    private static void floorRing(PoseStack ps, MultiBufferSource buffers, float r, float rotDeg, float red, float green, float blue, float a) {
        ps.pushPose();
        ps.mulPose(Axis.YP.rotationDegrees(rotDeg));
        VertexConsumer vc = buffers.getBuffer(RenderType.eyes(OrbitalBeamRenderer.RING));
        Matrix4f m = ps.last().pose();
        int R = c(red * a), G = c(green * a), B = c(blue * a), A = c(a);
        quad(vc, m, -r, 0, -r, r, 0, -r, r, 0, r, -r, 0, r, R, G, B, A);
        ps.popPose();
    }

    private static void quad(VertexConsumer vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1,
                             float x2, float y2, float z2, float x3, float y3, float z3, int r, int g, int b, int a) {
        int ov = OverlayTexture.NO_OVERLAY;
        RenderHooks.vertexFull(vc, m, x0, y0, z0, r, g, b, a, 0, 0, ov, LIGHT, 0, 1, 0);
        RenderHooks.vertexFull(vc, m, x1, y1, z1, r, g, b, a, 1, 0, ov, LIGHT, 0, 1, 0);
        RenderHooks.vertexFull(vc, m, x2, y2, z2, r, g, b, a, 1, 1, ov, LIGHT, 0, 1, 0);
        RenderHooks.vertexFull(vc, m, x3, y3, z3, r, g, b, a, 0, 1, ov, LIGHT, 0, 1, 0);
        RenderHooks.vertexFull(vc, m, x3, y3, z3, r, g, b, a, 0, 1, ov, LIGHT, 0, 1, 0);
        RenderHooks.vertexFull(vc, m, x2, y2, z2, r, g, b, a, 1, 1, ov, LIGHT, 0, 1, 0);
        RenderHooks.vertexFull(vc, m, x1, y1, z1, r, g, b, a, 1, 0, ov, LIGHT, 0, 1, 0);
        RenderHooks.vertexFull(vc, m, x0, y0, z0, r, g, b, a, 0, 0, ov, LIGHT, 0, 1, 0);
    }

    /** Wuerfelmodell (Koordinaten wie Vanilla-Humanoid: y 24 = Boden, 16 px = 1 Block vor der Skalierung). */
    public static class Model extends HierarchicalModel<EntityUnstableSovereign> {

        private final ModelPart root, head, body, cape, rightArm, leftArm, rightWing, leftWing;
        private final ModelPart[] shards = new ModelPart[3];
        private final ModelPart[] tendrils = new ModelPart[5];
        private final ModelPart[][] feathers = new ModelPart[2][3];

        public Model(ModelPart root) {
            this.root = root;
            this.head = root.getChild("head");
            this.body = root.getChild("body");
            this.cape = body.getChild("cape");
            this.rightArm = root.getChild("right_arm");
            this.leftArm = root.getChild("left_arm");
            this.rightWing = body.getChild("right_wing");
            this.leftWing = body.getChild("left_wing");
            for (int i = 0; i < 3; i++) {
                shards[i] = root.getChild("shard" + i);
                feathers[0][i] = rightWing.getChild("feather" + i);
                feathers[1][i] = leftWing.getChild("feather" + i);
            }
            for (int i = 0; i < tendrils.length; i++) tendrils[i] = body.getChild("tendril" + i);
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition root = mesh.getRoot();
            CubeDeformation none = CubeDeformation.NONE;

            PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, none), PartPose.offset(0.0F, 0.0F, 0.0F));
            head.addOrReplaceChild("crown", CubeListBuilder.create()
                    .texOffs(32, 0).addBox(-4.5F, -11.0F, -4.5F, 9, 3, 9, none)
                    .texOffs(70, 0).addBox(-4.5F, -13.0F, -4.5F, 1, 2, 1, none)
                    .texOffs(70, 0).addBox(3.5F, -13.0F, -4.5F, 1, 2, 1, none)
                    .texOffs(70, 0).addBox(-4.5F, -13.0F, 3.5F, 1, 2, 1, none)
                    .texOffs(70, 0).addBox(3.5F, -13.0F, 3.5F, 1, 2, 1, none)
                    .texOffs(74, 0).addBox(-1.0F, -14.0F, -4.8F, 2, 3, 1, none), PartPose.ZERO);

            PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                    .texOffs(0, 16).addBox(-4.5F, 0.0F, -2.5F, 9, 12, 5, none)
                    .texOffs(0, 34).addBox(-5.0F, 12.0F, -3.5F, 10, 8, 7, none)
                    .texOffs(0, 54).addBox(-6.0F, 20.0F, -4.5F, 12, 4, 9, none), PartPose.ZERO);
            body.addOrReplaceChild("cape", CubeListBuilder.create()
                    .texOffs(80, 16).addBox(-5.0F, 0.0F, 0.0F, 10, 22, 1, none), PartPose.offset(0.0F, 0.5F, 2.6F));
            // Rueckenkristalle zwischen den Fluegeln
            body.addOrReplaceChild("spine", CubeListBuilder.create()
                    .texOffs(96, 72).addBox(-0.5F, -6.0F, 0.0F, 1, 6, 1, none)
                    .texOffs(96, 72).addBox(-2.5F, -4.0F, 0.0F, 1, 6, 1, none)
                    .texOffs(96, 72).addBox(1.5F, -4.0F, 0.0F, 1, 6, 1, none), PartPose.offset(0.0F, 2.0F, 3.6F));
            // Kristallfluegel: je drei gefaecherte Splitter
            for (int side = 0; side < 2; side++) {
                float sx = side == 0 ? -1.0F : 1.0F;
                PartDefinition wing = body.addOrReplaceChild(side == 0 ? "right_wing" : "left_wing", CubeListBuilder.create(),
                        PartPose.offset(sx * 2.5F, 3.0F, 3.4F));
                for (int i = 0; i < 3; i++) {
                    wing.addOrReplaceChild("feather" + i, CubeListBuilder.create()
                            .texOffs(64, 72).addBox(-1.0F, -14.0F, -1.0F, 2, 14, 2, none), PartPose.ZERO);
                }
            }
            // Gewandranken unter dem Saum
            for (int i = 0; i < 5; i++) {
                float x = -5.0F + i * 2.5F;
                body.addOrReplaceChild("tendril" + i, CubeListBuilder.create()
                        .texOffs(80, 72).addBox(-1.0F, 0.0F, -0.5F, 2, 10, 1, none), PartPose.offset(x, 23.5F, (i % 2 == 0) ? -3.5F : 3.5F));
            }

            PartDefinition rightArm = root.addOrReplaceChild("right_arm", CubeListBuilder.create()
                    .texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4, 14, 4, none)
                    .texOffs(40, 36).addBox(-3.5F, 8.0F, -2.5F, 5, 5, 5, none)
                    .texOffs(80, 0).addBox(-3.5F, -3.0F, -2.5F, 5, 3, 5, none), PartPose.offset(-6.0F, 2.0F, 0.0F));
            rightArm.addOrReplaceChild("scepter", CubeListBuilder.create()
                    .texOffs(0, 70).addBox(-1.5F, 10.5F, -9.0F, 1, 1, 14, none)
                    .texOffs(32, 70).addBox(-2.5F, 9.5F, -12.0F, 3, 3, 3, none), PartPose.ZERO);
            root.addOrReplaceChild("left_arm", CubeListBuilder.create()
                    .texOffs(56, 16).addBox(-1.0F, -2.0F, -2.0F, 4, 14, 4, none)
                    .texOffs(60, 36).addBox(-1.5F, 8.0F, -2.5F, 5, 5, 5, none)
                    .texOffs(80, 8).addBox(-1.5F, -3.0F, -2.5F, 5, 3, 5, none), PartPose.offset(6.0F, 2.0F, 0.0F));

            for (int i = 0; i < 3; i++) {
                root.addOrReplaceChild("shard" + i, CubeListBuilder.create()
                        .texOffs(48, 70).addBox(-1.0F, -2.0F, -1.0F, 2, 4, 2, none), PartPose.offset(0.0F, 4.0F, 0.0F));
            }
            return LayerDefinition.create(mesh, 128, 128);
        }

        @Override
        public ModelPart root() {
            return root;
        }

        @Override
        public void setupAnim(EntityUnstableSovereign e, float limbSwing, float limbSwingAmount, float age, float netHeadYaw, float headPitch) {
            float pt = age - e.tickCount;
            int state = e.state();
            float st = e.stateTime(pt);
            float d = e.dying(pt);
            boolean p2 = e.phaseTwo();
            float breathe = Mth.sin(age * 0.06F);

            head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
            head.xRot = headPitch * Mth.DEG_TO_RAD;
            body.yRot = Mth.sin(age * 0.03F) * 0.03F;

            // Grundpose: Zepter halb gesenkt, linke Hand offen
            float rx = -0.35F + breathe * 0.08F, lx = -0.1F - breathe * 0.08F, rz = 0.1F, lz = -0.1F;
            // Fluegel: Spreizung (0 = angelegt, 1 = ganz offen) und Schlag
            float spread = 0.55F + breathe * 0.05F;
            float flap = Mth.sin(age * 0.12F) * 0.06F;
            switch (state) {
                case EntityUnstableSovereign.IDLE -> {
                    head.xRot = 0.45F;
                    head.yRot = 0;
                    rx = 0.0F;
                    lx = 0.0F;
                    rz = 0.25F;
                    lz = -0.25F;
                    spread = 0.1F;
                    flap = 0;
                }
                case EntityUnstableSovereign.AWAKEN -> {
                    float p = Math.min(1.0F, st / 30.0F);
                    head.xRot = 0.45F * (1 - p) - 0.25F * p;
                    rz = 0.25F + p * 1.0F;
                    lz = -0.25F - p * 1.0F;
                    rx = -0.3F * p;
                    lx = -0.3F * p;
                    spread = 0.1F + p * 0.95F;
                }
                case EntityUnstableSovereign.DASH_WIND -> {
                    rx = -1.6F;
                    rz = -0.2F;
                    lx = 0.4F;
                    lz = -0.5F;
                    spread = 0.2F;
                }
                case EntityUnstableSovereign.DASH -> {
                    rx = -1.5F;
                    lx = 0.6F;
                    lz = -0.4F;
                    spread = 0.05F;
                    flap = 0;
                }
                case EntityUnstableSovereign.RISE -> {
                    rx = -2.9F;
                    lx = -2.9F;
                    rz = 0.25F;
                    lz = -0.25F;
                    spread = 1.0F;
                    flap = Mth.sin(age * 0.6F) * 0.25F;
                    head.xRot = -0.4F;
                }
                case EntityUnstableSovereign.SLAM -> {
                    rx = -2.6F;
                    lx = -2.6F;
                    rz = -0.35F;
                    lz = 0.35F;
                    spread = 1.1F;
                    flap = -0.3F;
                    head.xRot = 0.5F;
                }
                case EntityUnstableSovereign.CAST -> {
                    float p = Math.min(1.0F, st / 8.0F);
                    rx = -0.35F - p * 2.6F + Mth.sin(age * 0.5F) * 0.05F;
                    rz = 0.1F - p * 0.15F;
                    lx = -0.1F - p * 1.2F;
                    lz = -0.1F - p * 0.5F;
                    spread = 0.7F + p * 0.3F;
                }
                case EntityUnstableSovereign.SHIFT -> {
                    float p = Math.min(1.0F, st / 15.0F);
                    rz = 0.1F + p * 1.35F;
                    lz = -0.1F - p * 1.35F;
                    rx = 0;
                    lx = 0;
                    head.xRot = -0.6F * p;
                    spread = 1.2F;
                    flap = Mth.sin(age * 0.9F) * 0.15F;
                }
                default -> {
                }
            }
            if (d > 0) {
                rz = 1.4F;
                lz = -1.4F;
                rx = -0.2F;
                lx = -0.2F;
                head.xRot = -0.7F;
                spread = 1.3F;
            }
            if (attackTime > 0.0F && state == EntityUnstableSovereign.HOVER) {
                float a = Mth.sin(attackTime * Mth.PI);
                rx = -0.35F - a * 2.4F;
                lx = -0.1F - a * 0.6F;
            }
            rightArm.xRot = rx;
            leftArm.xRot = lx;
            rightArm.zRot = rz;
            leftArm.zRot = lz;
            rightArm.yRot = 0;
            leftArm.yRot = 0;

            cape.xRot = 0.12F + (state == EntityUnstableSovereign.DASH ? 0.9F : state == EntityUnstableSovereign.SLAM ? -0.4F : 0.0F)
                    + limbSwingAmount * 0.5F + Mth.sin(age * 0.07F) * 0.05F;

            // Fluegel: drei Splitter je Seite gefaechert, aeusserer am weitesten gespreizt
            for (int side = 0; side < 2; side++) {
                float sx = side == 0 ? -1.0F : 1.0F;
                ModelPart wing = side == 0 ? rightWing : leftWing;
                wing.yRot = sx * (-0.35F - spread * 0.35F) + sx * flap;
                wing.xRot = -0.25F + flap * 0.5F;
                for (int i = 0; i < 3; i++) {
                    ModelPart f = feathers[side][i];
                    float fan = (0.35F + i * 0.45F) * (0.25F + spread * 0.75F);
                    f.zRot = sx * fan;
                    f.xRot = -0.1F * i;
                    f.y = i * 1.2F;
                    float len = p2 ? 1.25F : 1.0F;
                    f.yScale = len * (1.0F - i * 0.18F);
                    f.xScale = 1.0F;
                    f.zScale = 1.0F;
                }
            }

            // Gewandranken wehen, beim Sturmlauf nach hinten
            for (int i = 0; i < tendrils.length; i++) {
                ModelPart t = tendrils[i];
                t.xRot = Mth.sin(age * 0.15F + i * 1.3F) * 0.25F + (state == EntityUnstableSovereign.DASH ? 0.9F : 0.0F)
                        + (state == EntityUnstableSovereign.RISE ? 0.3F : 0.0F);
                t.zRot = Mth.cos(age * 0.11F + i) * 0.15F;
                t.yScale = state == EntityUnstableSovereign.IDLE ? 0.4F : 1.0F;
            }

            // Kristallsplitter kreisen um den Oberkoerper (Phase 2 schneller, beim Zaubern enger)
            float orbit = state == EntityUnstableSovereign.CAST ? 9.0F : 13.0F + d * 10.0F;
            float speed = p2 ? 0.09F : 0.05F;
            for (int i = 0; i < 3; i++) {
                float t = age * speed + i * Mth.TWO_PI / 3.0F;
                shards[i].x = Mth.cos(t) * orbit;
                shards[i].z = Mth.sin(t) * orbit;
                shards[i].y = 4.0F + Mth.sin(age * 0.1F + i * 2.0F) * 2.0F;
                shards[i].yRot = t * 2.0F;
                shards[i].xRot = 0.4F;
            }
        }
    }
}
