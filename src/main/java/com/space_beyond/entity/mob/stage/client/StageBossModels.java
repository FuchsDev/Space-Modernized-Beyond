package com.space_beyond.entity.mob.stage.client;

import com.space_beyond.entity.mob.stage.client.StageBossModel.Key;
import com.space_beyond.entity.mob.stage.client.StageBossModel.Role;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Modelle der Stufen-Bosse (generiert von {@code scratchpad/bossgen} - Knochen, Wuerfel, Materialien, UV-Zuschnitt,
 * Texturen und Posen kommen aus derselben Spezifikation; nicht von Hand pflegen). Je Boss: {@link LayerDefinition},
 * Rollen fuer die Grundbewegung, Schluesselposen je Angriff (Index wie in {@code Bosses}, 0 = ruhend) und Groesse.
 */
public final class StageBossModels {

    private StageBossModels() {}

    private static final CubeDeformation NONE = CubeDeformation.NONE;

    /** Lunar Sentinel: Sicherheits-Mech mit Ruecklaufknien, Raketenwerfer auf der Schulter und Laserkanone am linken Arm. Textur {@code entity/stage/lunar_sentinel.png}. */
    public static LayerDefinition lunarSentinel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_hips = root.addOrReplaceChild("hips", CubeListBuilder.create()
                .texOffs(56, 41).addBox(-6F, -2F, -4F, 12, 5, 8, NONE)
                .texOffs(0, 54).addBox(-4F, 1F, -4.5F, 8, 2, 9, NONE),
                PartPose.offsetAndRotation(0F, 5.5F, 0F, 0F, 0F, 0F));
        PartDefinition p_torso = p_hips.addOrReplaceChild("torso", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-8F, -15F, -5F, 16, 15, 10, NONE)
                .texOffs(210, 54).addBox(-6F, -13F, -6F, 12, 7, 1, NONE)
                .texOffs(0, 73).addBox(-2F, -11F, -6.5F, 4, 3, 1, NONE)
                .texOffs(0, 41).addBox(-8.5F, -4F, -5.5F, 17, 2, 11, NONE)
                .texOffs(86, 25).addBox(-6F, -14F, 5F, 12, 11, 4, NONE)
                .texOffs(66, 73).addBox(-5F, -12F, 9F, 3, 2, 1, NONE)
                .texOffs(74, 73).addBox(2F, -12F, 9F, 3, 2, 1, NONE)
                .texOffs(178, 54).addBox(-6F, -21F, 6F, 2, 7, 2, NONE)
                .texOffs(186, 54).addBox(4F, -21F, 6F, 2, 7, 2, NONE)
                .texOffs(234, 0).addBox(-9F, -15F, -4F, 1, 8, 8, NONE)
                .texOffs(0, 25).addBox(8F, -15F, -4F, 1, 8, 8, NONE),
                PartPose.offsetAndRotation(0F, -1F, 0F, 0.105F, 0F, 0F));
        PartDefinition p_head = p_torso.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(122, 0).addBox(-4.5F, -7F, -5F, 9, 7, 9, NONE)
                .texOffs(94, 54).addBox(-5F, -8F, -5.5F, 10, 2, 8, NONE)
                .texOffs(50, 73).addBox(-3.5F, -4.5F, -6F, 7, 2, 1, NONE)
                .texOffs(242, 65).addBox(-2.5F, -1F, -6.5F, 5, 2, 2, NONE)
                .texOffs(14, 65).addBox(-5.5F, -5F, -2F, 1, 4, 4, NONE)
                .texOffs(24, 65).addBox(4.5F, -5F, -2F, 1, 4, 4, NONE)
                .texOffs(42, 65).addBox(2.5F, -14F, 2F, 1, 7, 1, NONE)
                .texOffs(82, 73).addBox(2F, -15F, 1.5F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, -15F, -1F, 0F, 0F, 0F));
        PartDefinition p_pod = p_torso.addOrReplaceChild("pod", CubeListBuilder.create()
                .texOffs(52, 0).addBox(-5F, -6F, -5F, 9, 7, 10, NONE)
                .texOffs(96, 41).addBox(-5.5F, -1F, -5.5F, 10, 1, 11, NONE)
                .texOffs(90, 73).addBox(-4F, -5F, -6F, 3, 2, 1, NONE)
                .texOffs(98, 73).addBox(0F, -5F, -6F, 3, 2, 1, NONE)
                .texOffs(124, 73).addBox(-4F, -2.5F, -6F, 3, 1, 1, NONE)
                .texOffs(132, 73).addBox(0F, -2.5F, -6F, 3, 1, 1, NONE),
                PartPose.offsetAndRotation(-8F, -16F, 2F, 0F, 0F, -0.14F));
        PartDefinition p_arm_r = p_torso.addOrReplaceChild("arm_r", CubeListBuilder.create()
                .texOffs(148, 25).addBox(-4F, -2F, -3F, 5, 9, 6, NONE)
                .texOffs(18, 25).addBox(-6F, -5F, -4.5F, 8, 6, 9, NONE)
                .texOffs(152, 41).addBox(-6.5F, -2F, -5F, 9, 1, 10, NONE)
                .texOffs(34, 65).addBox(-2F, 4F, 2.5F, 2, 6, 2, NONE),
                PartPose.offsetAndRotation(-11F, -11F, 0F, 0F, 0F, 0.14F));
        PartDefinition p_fore_r = p_arm_r.addOrReplaceChild("fore_r", CubeListBuilder.create()
                .texOffs(158, 0).addBox(-3.5F, 0F, -3.5F, 7, 9, 7, NONE)
                .texOffs(130, 54).addBox(-4F, 2F, -4F, 8, 2, 8, NONE)
                .texOffs(74, 65).addBox(-2.5F, 8F, -2.5F, 5, 2, 5, NONE),
                PartPose.offsetAndRotation(-1.5F, 8F, 0F, -0.262F, 0F, 0F));
        PartDefinition p_claw0 = p_fore_r.addOrReplaceChild("claw0", CubeListBuilder.create()
                .texOffs(94, 65).addBox(-0.5F, 0F, -1F, 1, 5, 2, NONE)
                .texOffs(112, 73).addBox(-0.5F, 4F, -2F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-1.5F, 9F, -2F, -0.436F, 0F, 0F));
        PartDefinition p_claw1 = p_fore_r.addOrReplaceChild("claw1", CubeListBuilder.create()
                .texOffs(100, 65).addBox(-0.5F, 0F, -1F, 1, 5, 2, NONE)
                .texOffs(116, 73).addBox(-0.5F, 4F, -2F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 9F, -2F, -0.349F, 0F, 0F));
        PartDefinition p_claw2 = p_fore_r.addOrReplaceChild("claw2", CubeListBuilder.create()
                .texOffs(106, 65).addBox(-0.5F, 0F, -1F, 1, 5, 2, NONE)
                .texOffs(120, 73).addBox(-0.5F, 4F, -2F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(1.5F, 9F, -2F, -0.262F, 0F, 0F));
        PartDefinition p_arm_l = p_torso.addOrReplaceChild("arm_l", CubeListBuilder.create()
                .texOffs(170, 25).addBox(-1F, -2F, -3F, 5, 9, 6, NONE)
                .texOffs(52, 25).addBox(-2F, -5F, -4.5F, 8, 6, 9, NONE)
                .texOffs(190, 41).addBox(-2.5F, -2F, -5F, 9, 1, 10, NONE),
                PartPose.offsetAndRotation(11F, -11F, 0F, 0F, 0F, -0.14F));
        PartDefinition p_cannon = p_arm_l.addOrReplaceChild("cannon", CubeListBuilder.create()
                .texOffs(90, 0).addBox(-3F, 0F, -5F, 7, 8, 9, NONE)
                .texOffs(118, 25).addBox(-1F, 2F, -17F, 3, 3, 12, NONE)
                .texOffs(112, 65).addBox(-1.5F, 1.5F, -11F, 4, 4, 2, NONE)
                .texOffs(180, 65).addBox(-1.5F, 1.5F, -15F, 4, 4, 1, NONE)
                .texOffs(106, 73).addBox(-0.5F, 2.5F, -18F, 2, 2, 1, NONE)
                .texOffs(138, 41).addBox(4F, 1F, -3F, 1, 6, 6, NONE)
                .texOffs(46, 65).addBox(-3.5F, 5F, -2F, 8, 1, 6, NONE),
                PartPose.offsetAndRotation(1.5F, 8F, 0F, -0.349F, 0F, 0F));
        PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create()
                .texOffs(186, 0).addBox(-3F, -1F, -3F, 6, 10, 6, NONE)
                .texOffs(124, 65).addBox(-3.5F, 0F, -3.5F, 7, 4, 1, NONE)
                .texOffs(194, 54).addBox(-1F, 2F, 3F, 2, 7, 2, NONE)
                .texOffs(206, 65).addBox(-3.5F, 6F, -1F, 7, 2, 2, NONE),
                PartPose.offsetAndRotation(-6F, 5.5F, 0F, -0.349F, 0F, 0.07F));
        PartDefinition p_shin_r = p_leg_r.addOrReplaceChild("shin_r", CubeListBuilder.create()
                .texOffs(192, 25).addBox(-2F, 0F, -2F, 4, 10, 4, NONE)
                .texOffs(236, 54).addBox(-2.5F, 1F, -3.5F, 5, 6, 2, NONE)
                .texOffs(162, 54).addBox(-1F, 0F, 2F, 2, 8, 2, NONE),
                PartPose.offsetAndRotation(0F, 9F, 0F, 0.873F, 0F, 0F));
        PartDefinition p_foot_r = p_shin_r.addOrReplaceChild("foot_r", CubeListBuilder.create()
                .texOffs(34, 54).addBox(-3F, 0F, -6F, 6, 2, 9, NONE)
                .texOffs(10, 73).addBox(-3.5F, 0.5F, -8F, 2, 1, 3, NONE)
                .texOffs(20, 73).addBox(1.5F, 0.5F, -8F, 2, 1, 3, NONE)
                .texOffs(156, 65).addBox(-1F, 0.5F, -9F, 2, 1, 4, NONE)
                .texOffs(190, 65).addBox(-1F, -1F, 3F, 2, 3, 2, NONE),
                PartPose.offsetAndRotation(0F, 9F, 0F, -0.524F, 0F, 0F));
        PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create()
                .texOffs(210, 0).addBox(-3F, -1F, -3F, 6, 10, 6, NONE)
                .texOffs(140, 65).addBox(-3.5F, 0F, -3.5F, 7, 4, 1, NONE)
                .texOffs(202, 54).addBox(-1F, 2F, 3F, 2, 7, 2, NONE)
                .texOffs(224, 65).addBox(-3.5F, 6F, -1F, 7, 2, 2, NONE),
                PartPose.offsetAndRotation(6F, 5.5F, 0F, -0.349F, 0F, -0.07F));
        PartDefinition p_shin_l = p_leg_l.addOrReplaceChild("shin_l", CubeListBuilder.create()
                .texOffs(208, 25).addBox(-2F, 0F, -2F, 4, 10, 4, NONE)
                .texOffs(0, 65).addBox(-2.5F, 1F, -3.5F, 5, 6, 2, NONE)
                .texOffs(170, 54).addBox(-1F, 0F, 2F, 2, 8, 2, NONE),
                PartPose.offsetAndRotation(0F, 9F, 0F, 0.873F, 0F, 0F));
        PartDefinition p_foot_l = p_shin_l.addOrReplaceChild("foot_l", CubeListBuilder.create()
                .texOffs(64, 54).addBox(-3F, 0F, -6F, 6, 2, 9, NONE)
                .texOffs(30, 73).addBox(-3.5F, 0.5F, -8F, 2, 1, 3, NONE)
                .texOffs(40, 73).addBox(1.5F, 0.5F, -8F, 2, 1, 3, NONE)
                .texOffs(168, 65).addBox(-1F, 0.5F, -9F, 2, 1, 4, NONE)
                .texOffs(198, 65).addBox(-1F, -1F, 3F, 2, 3, 2, NONE),
                PartPose.offsetAndRotation(0F, 9F, 0F, -0.524F, 0F, 0F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    public static final Role[] LUNAR_SENTINEL_ROLES = {new Role("hips/torso", "body", 0.00F), new Role("hips/torso/head", "head", 0.00F), new Role("hips/torso/arm_r", "arm_r", 0.00F), new Role("hips/torso/arm_l", "arm_l", 0.00F), new Role("leg_r", "leg_r", 0.00F), new Role("leg_l", "leg_l", 0.00F)};

    public static final Key[] LUNAR_SENTINEL_POSES = {
            new Key(0, 0F, "hips/torso", 25F, 0F, 0F),
            new Key(0, 0F, "hips/torso/head", 30F, 0F, 0F),
            new Key(0, 0F, "hips/torso/arm_r", 20F, 0F, 10F),
            new Key(0, 0F, "hips/torso/arm_l", 20F, 0F, -10F),
            new Key(1, 0F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(1, 8F, "hips/torso/arm_l", -35F, -10F, 0F),
            new Key(1, 26F, "hips/torso/arm_l", -35F, -10F, 0F),
            new Key(1, 32F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(1, 0F, "hips/torso/arm_l/cannon", 0F, 0F, 0F),
            new Key(1, 8F, "hips/torso/arm_l/cannon", -15F, 0F, 0F),
            new Key(1, 26F, "hips/torso/arm_l/cannon", -15F, 0F, 0F),
            new Key(1, 32F, "hips/torso/arm_l/cannon", 0F, 0F, 0F),
            new Key(1, 0F, "hips/torso", 0F, 0F, 0F),
            new Key(1, 8F, "hips/torso", -5F, -15F, 0F),
            new Key(1, 26F, "hips/torso", -5F, -15F, 0F),
            new Key(1, 32F, "hips/torso", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso/pod", 0F, 0F, 0F),
            new Key(2, 6F, "hips/torso/pod", -35F, 0F, 0F),
            new Key(2, 30F, "hips/torso/pod", -35F, 0F, 0F),
            new Key(2, 34F, "hips/torso/pod", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso", 0F, 0F, 0F),
            new Key(2, 6F, "hips/torso", 8F, 0F, 0F),
            new Key(2, 32F, "hips/torso", 8F, 0F, 0F),
            new Key(2, 36F, "hips/torso", 0F, 0F, 0F),
            new Key(3, 0F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(3, 8F, "hips/torso/arm_r", -150F, 0F, 0F),
            new Key(3, 13F, "hips/torso/arm_r", -20F, 0F, 0F),
            new Key(3, 20F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(3, 0F, "hips/torso", 0F, 0F, 0F),
            new Key(3, 8F, "hips/torso", -15F, 0F, 0F),
            new Key(3, 13F, "hips/torso", 20F, 0F, 0F),
            new Key(3, 20F, "hips/torso", 0F, 0F, 0F),
            new Key(4, 0F, "hips/torso/head", 0F, 0F, 0F),
            new Key(4, 5F, "hips/torso/head", -30F, 0F, 0F),
            new Key(4, 15F, "hips/torso/head", 0F, 0F, 0F),
            new Key(4, 0F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(4, 5F, "hips/torso/arm_l", -40F, 0F, -30F),
            new Key(4, 15F, "hips/torso/arm_l", 0F, 0F, 0F)};

    public static final float LUNAR_SENTINEL_SCALE = 1.35F;

    /** Cryo Behemoth: gorillaartiger Golem aus Minz-Eis und Pressschnee mit Kristallknoecheln, Kristallstacheln auf Ruecken und Schultern und leuchtendem Kern im Brustspalt. Textur {@code entity/stage/cryo_behemoth.png}. */
    public static LayerDefinition cryoBehemoth() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_hips = root.addOrReplaceChild("hips", CubeListBuilder.create()
                .texOffs(72, 57).addBox(-9F, -3F, -6F, 18, 7, 12, NONE)
                .texOffs(44, 94).addBox(-9.5F, 1F, -6.5F, 19, 2, 13, NONE),
                PartPose.offsetAndRotation(0F, 8F, 5F, 0F, 0F, 0F));
        PartDefinition p_torso = p_hips.addOrReplaceChild("torso", CubeListBuilder.create()
                .texOffs(84, 0).addBox(-9F, -10F, -7F, 18, 11, 14, NONE)
                .texOffs(132, 57).addBox(-10F, -4F, -8F, 11, 3, 16, NONE)
                .texOffs(172, 125).addBox(2F, -5F, -8F, 8, 3, 9, NONE)
                .texOffs(0, 0).addBox(-12F, -24F, -9F, 24, 15, 18, NONE)
                .texOffs(0, 57).addBox(-11F, -28F, -5F, 22, 5, 14, NONE)
                .texOffs(116, 76).addBox(-10F, -22F, 8F, 20, 13, 3, NONE)
                .texOffs(36, 76).addBox(-2F, -24F, 10F, 4, 16, 2, NONE)
                .texOffs(88, 125).addBox(-3F, -22F, -9.5F, 6, 12, 1, NONE)
                .texOffs(102, 125).addBox(-13F, -27F, -7F, 26, 3, 9, NONE)
                .texOffs(0, 167).addBox(-7F, -12F, -8.5F, 14, 3, 2, NONE)
                .texOffs(48, 76).addBox(-12.5F, -19F, -3F, 25, 7, 9, NONE),
                PartPose.offsetAndRotation(0F, -1F, 0F, 0.454F, 0F, 0F));
        PartDefinition p_core = p_torso.addOrReplaceChild("core", CubeListBuilder.create()
                .texOffs(202, 138).addBox(-4F, -4F, -1.5F, 8, 8, 2, NONE)
                .texOffs(88, 138).addBox(-5F, -5F, -0.5F, 10, 10, 1, NONE),
                PartPose.offsetAndRotation(0F, -17F, -10.5F, 0F, 0F, 0.785F));
        PartDefinition p_crack_0 = p_torso.addOrReplaceChild("crack_0", CubeListBuilder.create()
                .texOffs(172, 160).addBox(-0.5F, -6F, -0.5F, 1, 6, 1, NONE),
                PartPose.offsetAndRotation(-3.5F, -17F, -11.8F, 0F, 0F, -1.082F));
        PartDefinition p_crack_1 = p_torso.addOrReplaceChild("crack_1", CubeListBuilder.create()
                .texOffs(202, 150).addBox(-0.5F, -7F, -0.5F, 1, 7, 1, NONE),
                PartPose.offsetAndRotation(3.5F, -17F, -11.8F, 0F, 0F, 0.698F));
        PartDefinition p_crack_2 = p_torso.addOrReplaceChild("crack_2", CubeListBuilder.create()
                .texOffs(252, 160).addBox(-0.5F, -5F, -0.5F, 1, 5, 1, NONE),
                PartPose.offsetAndRotation(2.5F, -17F, -11.8F, 0F, 0F, 2.234F));
        PartDefinition p_pec_r = p_torso.addOrReplaceChild("pec_r", CubeListBuilder.create()
                .texOffs(82, 110).addBox(-9F, 0F, -2.5F, 9, 12, 3, NONE),
                PartPose.offsetAndRotation(-3.5F, -24F, -9F, 0F, 0.244F, 0.07F));
        PartDefinition p_pec_l = p_torso.addOrReplaceChild("pec_l", CubeListBuilder.create()
                .texOffs(106, 110).addBox(0F, 0F, -2.5F, 9, 12, 3, NONE),
                PartPose.offsetAndRotation(3.5F, -24F, -9F, 0F, -0.244F, -0.07F));
        PartDefinition p_head = p_torso.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(46, 110).addBox(-5F, -8F, -7F, 10, 7, 8, NONE)
                .texOffs(48, 150).addBox(-6F, -9F, -8.5F, 12, 3, 5, NONE)
                .texOffs(88, 167).addBox(-5F, -6.5F, -7.5F, 10, 3, 1, NONE)
                .texOffs(142, 167).addBox(-4.5F, -6F, -8F, 3, 2, 1, NONE)
                .texOffs(150, 167).addBox(1.5F, -6F, -8F, 3, 2, 1, NONE)
                .texOffs(200, 160).addBox(-3.5F, -4F, -9F, 7, 3, 3, NONE)
                .texOffs(16, 150).addBox(-4.5F, -10F, -5F, 9, 2, 7, NONE),
                PartPose.offsetAndRotation(0F, -22F, -9F, -0.419F, 0F, 0F));
        PartDefinition p_jaw = p_head.addOrReplaceChild("jaw", CubeListBuilder.create()
                .texOffs(170, 138).addBox(-4.5F, 0F, -7F, 9, 3, 7, NONE)
                .texOffs(176, 160).addBox(-3.5F, -0.5F, -6.5F, 7, 1, 5, NONE)
                .texOffs(34, 160).addBox(-5F, 2F, -5F, 10, 2, 5, NONE),
                PartPose.offsetAndRotation(0F, -2F, -2F, 0.105F, 0F, 0F));
        PartDefinition p_crown_a = p_head.addOrReplaceChild("crown_a", CubeListBuilder.create()
                .texOffs(220, 160).addBox(-1F, -3F, -1F, 2, 4, 2, NONE)
                .texOffs(158, 167).addBox(-0.5F, -5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-3F, -10F, -2F, -0.611F, 0F, -0.436F));
        PartDefinition p_crown_b = p_head.addOrReplaceChild("crown_b", CubeListBuilder.create()
                .texOffs(64, 160).addBox(-1.5F, -3F, -1.5F, 3, 4, 3, NONE)
                .texOffs(110, 167).addBox(-0.5F, -6F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(2.5F, -10F, -1F, -0.698F, 0F, 0.384F));
        PartDefinition p_arm_r = p_torso.addOrReplaceChild("arm_r", CubeListBuilder.create()
                .texOffs(180, 33).addBox(-4F, 2F, -4F, 8, 12, 8, NONE)
                .texOffs(16, 138).addBox(-4.5F, 8F, -4.5F, 9, 2, 9, NONE),
                PartPose.offsetAndRotation(-15F, -21F, 0F, -0.524F, 0F, 0.454F));
        PartDefinition p_pauldron_r = p_arm_r.addOrReplaceChild("pauldron_r", CubeListBuilder.create()
                .texOffs(148, 0).addBox(-7F, -6F, -7F, 13, 10, 14, NONE)
                .texOffs(130, 110).addBox(-6.5F, -7.5F, -6F, 12, 2, 12, NONE),
                PartPose.offsetAndRotation(0F, -1F, 0F, 0F, 0F, -0.175F));
        PartDefinition p_sh_r1 = p_pauldron_r.addOrReplaceChild("sh_r1", CubeListBuilder.create()
                .texOffs(110, 138).addBox(-2.5F, -5F, -2.5F, 5, 6, 5, NONE)
                .texOffs(130, 150).addBox(-1.5F, -10F, -1.5F, 3, 5, 3, NONE)
                .texOffs(114, 167).addBox(-0.5F, -13F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(2F, -7F, -1F, -0.262F, 0F, -0.524F));
        PartDefinition p_sh_r2 = p_pauldron_r.addOrReplaceChild("sh_r2", CubeListBuilder.create()
                .texOffs(82, 150).addBox(-2F, -3F, -2F, 4, 4, 4, NONE)
                .texOffs(228, 160).addBox(-1F, -7F, -1F, 2, 4, 2, NONE)
                .texOffs(162, 167).addBox(-0.5F, -9F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-2.5F, -6F, 3F, -0.873F, 0F, -1.047F));
        PartDefinition p_fore_r = p_arm_r.addOrReplaceChild("fore_r", CubeListBuilder.create()
                .texOffs(0, 33).addBox(-6F, 0F, -5.5F, 11, 13, 11, NONE)
                .texOffs(108, 94).addBox(-6.5F, -1F, -6F, 12, 3, 12, NONE)
                .texOffs(0, 94).addBox(-8F, 3F, -4F, 3, 8, 8, NONE),
                PartPose.offsetAndRotation(-0.5F, 13F, 0F, -0.244F, 0F, -0.244F));
        PartDefinition p_fist_r = p_fore_r.addOrReplaceChild("fist_r", CubeListBuilder.create()
                .texOffs(88, 33).addBox(-6F, 0F, -6F, 12, 9, 11, NONE)
                .texOffs(206, 150).addBox(-6.5F, 4F, -7F, 13, 3, 4, NONE)
                .texOffs(76, 160).addBox(2.5F, 3.5F, -9F, 3, 4, 3, NONE)
                .texOffs(88, 160).addBox(-1.5F, 3.5F, -9F, 3, 4, 3, NONE)
                .texOffs(100, 160).addBox(-5.5F, 3.5F, -9F, 3, 4, 3, NONE),
                PartPose.offsetAndRotation(0F, 12F, -1F, 0.14F, 0F, 0F));
        PartDefinition p_elbow_r = p_fore_r.addOrReplaceChild("elbow_r", CubeListBuilder.create()
                .texOffs(142, 150).addBox(-1.5F, -4F, -1.5F, 3, 5, 3, NONE)
                .texOffs(118, 167).addBox(-0.5F, -7F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(-2F, 1F, 5F, -2.007F, 0F, -0.436F));
        PartDefinition p_arm_l = p_torso.addOrReplaceChild("arm_l", CubeListBuilder.create()
                .texOffs(212, 33).addBox(-4F, 2F, -4F, 8, 12, 8, NONE)
                .texOffs(52, 138).addBox(-4.5F, 8F, -4.5F, 9, 2, 9, NONE),
                PartPose.offsetAndRotation(15F, -21F, 0F, -0.524F, 0F, -0.454F));
        PartDefinition p_pauldron_l = p_arm_l.addOrReplaceChild("pauldron_l", CubeListBuilder.create()
                .texOffs(202, 0).addBox(-6F, -6F, -7F, 13, 10, 14, NONE)
                .texOffs(178, 110).addBox(-5.5F, -7.5F, -6F, 12, 2, 12, NONE),
                PartPose.offsetAndRotation(0F, -1F, 0F, 0F, 0F, 0.175F));
        PartDefinition p_sh_l1 = p_pauldron_l.addOrReplaceChild("sh_l1", CubeListBuilder.create()
                .texOffs(130, 138).addBox(-2.5F, -5F, -2.5F, 5, 6, 5, NONE)
                .texOffs(154, 150).addBox(-1.5F, -10F, -1.5F, 3, 5, 3, NONE)
                .texOffs(122, 167).addBox(-0.5F, -13F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(-2F, -7F, -1F, -0.262F, 0F, 0.524F));
        PartDefinition p_sh_l2 = p_pauldron_l.addOrReplaceChild("sh_l2", CubeListBuilder.create()
                .texOffs(98, 150).addBox(-2F, -3F, -2F, 4, 4, 4, NONE)
                .texOffs(236, 160).addBox(-1F, -7F, -1F, 2, 4, 2, NONE)
                .texOffs(166, 167).addBox(-0.5F, -9F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(2.5F, -6F, 3F, -0.873F, 0F, 1.047F));
        PartDefinition p_fore_l = p_arm_l.addOrReplaceChild("fore_l", CubeListBuilder.create()
                .texOffs(44, 33).addBox(-5F, 0F, -5.5F, 11, 13, 11, NONE)
                .texOffs(156, 94).addBox(-5.5F, -1F, -6F, 12, 3, 12, NONE)
                .texOffs(22, 94).addBox(5F, 3F, -4F, 3, 8, 8, NONE),
                PartPose.offsetAndRotation(0.5F, 13F, 0F, -0.244F, 0F, 0.244F));
        PartDefinition p_fist_l = p_fore_l.addOrReplaceChild("fist_l", CubeListBuilder.create()
                .texOffs(134, 33).addBox(-6F, 0F, -6F, 12, 9, 11, NONE)
                .texOffs(0, 160).addBox(-6.5F, 4F, -7F, 13, 3, 4, NONE)
                .texOffs(112, 160).addBox(-5.5F, 3.5F, -9F, 3, 4, 3, NONE)
                .texOffs(124, 160).addBox(-1.5F, 3.5F, -9F, 3, 4, 3, NONE)
                .texOffs(136, 160).addBox(2.5F, 3.5F, -9F, 3, 4, 3, NONE),
                PartPose.offsetAndRotation(0F, 12F, -1F, 0.14F, 0F, 0F));
        PartDefinition p_elbow_l = p_fore_l.addOrReplaceChild("elbow_l", CubeListBuilder.create()
                .texOffs(166, 150).addBox(-1.5F, -4F, -1.5F, 3, 5, 3, NONE)
                .texOffs(126, 167).addBox(-0.5F, -7F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(2F, 1F, 5F, -2.007F, 0F, 0.436F));
        PartDefinition p_sp_1 = p_torso.addOrReplaceChild("sp_1", CubeListBuilder.create()
                .texOffs(226, 110).addBox(-3F, -7F, -3F, 6, 8, 6, NONE)
                .texOffs(0, 150).addBox(-2F, -13F, -2F, 4, 6, 4, NONE)
                .texOffs(244, 160).addBox(-1F, -17F, -1F, 2, 4, 2, NONE),
                PartPose.offsetAndRotation(0F, -28F, 1F, -0.436F, 0F, 0F));
        PartDefinition p_sp_2 = p_torso.addOrReplaceChild("sp_2", CubeListBuilder.create()
                .texOffs(150, 138).addBox(-2.5F, -5F, -2.5F, 5, 6, 5, NONE)
                .texOffs(178, 150).addBox(-1.5F, -10F, -1.5F, 3, 5, 3, NONE)
                .texOffs(170, 167).addBox(-0.5F, -12F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-6.5F, -27F, 3F, -0.698F, 0F, -0.454F));
        PartDefinition p_sp_3 = p_torso.addOrReplaceChild("sp_3", CubeListBuilder.create()
                .texOffs(206, 125).addBox(-2.5F, -6F, -2.5F, 5, 7, 5, NONE)
                .texOffs(190, 150).addBox(-1.5F, -11F, -1.5F, 3, 5, 3, NONE)
                .texOffs(130, 167).addBox(-0.5F, -14F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(6.5F, -27F, 4F, -0.593F, 0F, 0.524F));
        PartDefinition p_sp_4 = p_torso.addOrReplaceChild("sp_4", CubeListBuilder.create()
                .texOffs(222, 138).addBox(-2.5F, -4F, -2.5F, 5, 5, 5, NONE)
                .texOffs(148, 160).addBox(-1.5F, -8F, -1.5F, 3, 4, 3, NONE)
                .texOffs(134, 167).addBox(-0.5F, -11F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(1F, -23F, 9F, -1.082F, 0F, 0.14F));
        PartDefinition p_sp_5 = p_torso.addOrReplaceChild("sp_5", CubeListBuilder.create()
                .texOffs(114, 150).addBox(-2F, -3F, -2F, 4, 4, 4, NONE)
                .texOffs(32, 167).addBox(-1F, -6F, -1F, 2, 3, 2, NONE)
                .texOffs(174, 167).addBox(-0.5F, -8F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-4F, -15F, 11F, -1.466F, 0F, -0.279F));
        PartDefinition p_sp_6 = p_torso.addOrReplaceChild("sp_6", CubeListBuilder.create()
                .texOffs(160, 160).addBox(-1.5F, -3F, -1.5F, 3, 4, 3, NONE)
                .texOffs(138, 167).addBox(-0.5F, -6F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(4F, -10F, 10F, -1.658F, 0F, 0.314F));
        PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create()
                .texOffs(186, 57).addBox(-4.5F, -2F, -4.5F, 9, 9, 9, NONE)
                .texOffs(226, 125).addBox(-5F, 0F, -3F, 2, 6, 6, NONE),
                PartPose.offsetAndRotation(-8F, 8F, 5F, 0F, 0F, 0F));
        PartDefinition p_shin_r = p_leg_r.addOrReplaceChild("shin_r", CubeListBuilder.create()
                .texOffs(162, 76).addBox(-5F, 0F, -5F, 10, 6, 10, NONE)
                .texOffs(0, 125).addBox(-5.5F, -1F, -5.5F, 11, 2, 11, NONE),
                PartPose.offsetAndRotation(0F, 7F, 0F, 0F, 0F, 0F));
        PartDefinition p_foot_r = p_shin_r.addOrReplaceChild("foot_r", CubeListBuilder.create()
                .texOffs(204, 94).addBox(-5.5F, 0F, -7F, 11, 3, 12, NONE)
                .texOffs(40, 167).addBox(-5F, 0.5F, -9F, 10, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
        PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create()
                .texOffs(0, 76).addBox(-4.5F, -2F, -4.5F, 9, 9, 9, NONE)
                .texOffs(0, 138).addBox(3F, 0F, -3F, 2, 6, 6, NONE),
                PartPose.offsetAndRotation(8F, 8F, 5F, 0F, 0F, 0F));
        PartDefinition p_shin_l = p_leg_l.addOrReplaceChild("shin_l", CubeListBuilder.create()
                .texOffs(202, 76).addBox(-5F, 0F, -5F, 10, 6, 10, NONE)
                .texOffs(44, 125).addBox(-5.5F, -1F, -5.5F, 11, 2, 11, NONE),
                PartPose.offsetAndRotation(0F, 7F, 0F, 0F, 0F, 0F));
        PartDefinition p_foot_l = p_shin_l.addOrReplaceChild("foot_l", CubeListBuilder.create()
                .texOffs(0, 110).addBox(-5.5F, 0F, -7F, 11, 3, 12, NONE)
                .texOffs(64, 167).addBox(-5F, 0.5F, -9F, 10, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    public static final Role[] CRYO_BEHEMOTH_ROLES = {new Role("hips/torso", "body", 0.00F), new Role("hips/torso/head", "head", 0.00F), new Role("hips/torso/head/jaw", "jaw", 0.00F), new Role("hips/torso/arm_r", "arm_r", 0.00F), new Role("hips/torso/arm_l", "arm_l", 0.00F), new Role("leg_r", "leg_r", 0.00F), new Role("leg_l", "leg_l", 0.00F)};

    public static final Key[] CRYO_BEHEMOTH_POSES = {
            new Key(0, 0F, "hips/torso", 18F, 0F, 0F),
            new Key(0, 0F, "hips/torso/head", 32F, 0F, 0F),
            new Key(0, 0F, "hips/torso/arm_r", 22F, 0F, 6F),
            new Key(0, 0F, "hips/torso/arm_l", 22F, 0F, -6F),
            new Key(0, 0F, "hips/torso/head/jaw", -6F, 0F, 0F),
            new Key(1, 0F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(1, 10F, "hips/torso/arm_r", -165F, 0F, -12F),
            new Key(1, 20F, "hips/torso/arm_r", -25F, 0F, 0F),
            new Key(1, 24F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(1, 0F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(1, 10F, "hips/torso/arm_l", -165F, 0F, 12F),
            new Key(1, 20F, "hips/torso/arm_l", -25F, 0F, 0F),
            new Key(1, 24F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(1, 0F, "hips/torso", 0F, 0F, 0F),
            new Key(1, 10F, "hips/torso", -22F, 0F, 0F),
            new Key(1, 20F, "hips/torso", 22F, 0F, 0F),
            new Key(1, 24F, "hips/torso", 0F, 0F, 0F),
            new Key(1, 0F, "hips/torso/head", 0F, 0F, 0F),
            new Key(1, 10F, "hips/torso/head", -20F, 0F, 0F),
            new Key(1, 20F, "hips/torso/head", 10F, 0F, 0F),
            new Key(1, 24F, "hips/torso/head", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso/head", 0F, 0F, 0F),
            new Key(2, 6F, "hips/torso/head", -28F, 0F, 0F),
            new Key(2, 10F, "hips/torso/head", 12F, 0F, 0F),
            new Key(2, 26F, "hips/torso/head", 12F, 0F, 0F),
            new Key(2, 30F, "hips/torso/head", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso/head/jaw", 0F, 0F, 0F),
            new Key(2, 6F, "hips/torso/head/jaw", 10F, 0F, 0F),
            new Key(2, 10F, "hips/torso/head/jaw", 38F, 0F, 0F),
            new Key(2, 26F, "hips/torso/head/jaw", 38F, 0F, 0F),
            new Key(2, 30F, "hips/torso/head/jaw", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso", 0F, 0F, 0F),
            new Key(2, 6F, "hips/torso", -12F, 0F, 0F),
            new Key(2, 10F, "hips/torso", 6F, 0F, 0F),
            new Key(2, 26F, "hips/torso", 6F, 0F, 0F),
            new Key(2, 30F, "hips/torso", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(2, 8F, "hips/torso/arm_r", 15F, 0F, 25F),
            new Key(2, 26F, "hips/torso/arm_r", 15F, 0F, 25F),
            new Key(2, 30F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(2, 8F, "hips/torso/arm_l", 15F, 0F, -25F),
            new Key(2, 26F, "hips/torso/arm_l", 15F, 0F, -25F),
            new Key(2, 30F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(3, 0F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(3, 6F, "hips/torso/arm_r", -175F, 15F, 0F),
            new Key(3, 10F, "hips/torso/arm_r", -55F, 0F, 0F),
            new Key(3, 14F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(3, 0F, "hips/torso", 0F, 0F, 0F),
            new Key(3, 6F, "hips/torso", -14F, 22F, 0F),
            new Key(3, 10F, "hips/torso", 16F, -18F, 0F),
            new Key(3, 14F, "hips/torso", 0F, 0F, 0F),
            new Key(3, 0F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(3, 6F, "hips/torso/arm_l", -30F, 0F, -15F),
            new Key(3, 10F, "hips/torso/arm_l", 20F, 0F, 0F),
            new Key(3, 14F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(4, 0F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(4, 7F, "hips/torso/arm_r", -170F, 0F, -28F),
            new Key(4, 12F, "hips/torso/arm_r", -15F, 0F, 0F),
            new Key(4, 18F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(4, 0F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(4, 7F, "hips/torso/arm_l", -170F, 0F, 28F),
            new Key(4, 12F, "hips/torso/arm_l", -15F, 0F, 0F),
            new Key(4, 18F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(4, 0F, "hips/torso", 0F, 0F, 0F),
            new Key(4, 7F, "hips/torso", -24F, 0F, 0F),
            new Key(4, 12F, "hips/torso", 28F, 0F, 0F),
            new Key(4, 18F, "hips/torso", 0F, 0F, 0F),
            new Key(4, 0F, "hips/torso/head", 0F, 0F, 0F),
            new Key(4, 7F, "hips/torso/head", -15F, 0F, 0F),
            new Key(4, 12F, "hips/torso/head", 15F, 0F, 0F),
            new Key(4, 18F, "hips/torso/head", 0F, 0F, 0F),
            new Key(4, 0F, "hips/torso/head/jaw", 0F, 0F, 0F),
            new Key(4, 7F, "hips/torso/head/jaw", 30F, 0F, 0F),
            new Key(4, 12F, "hips/torso/head/jaw", 10F, 0F, 0F),
            new Key(4, 18F, "hips/torso/head/jaw", 0F, 0F, 0F)};

    public static final float CRYO_BEHEMOTH_SCALE = 1.5F;

    /** Rust Titan: verrosteter sechsbeiniger Spinnenpanzer mit Panzerwanne, drehbarem Geschuetzturm mit Langrohr, bernsteinfarbenem Sensorbuendel, Auspuffrohren und Kabeln. Textur {@code entity/stage/rust_titan.png}. */
    public static LayerDefinition rustTitan() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_hull = root.addOrReplaceChild("hull", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-12F, -6F, -18F, 24, 10, 35, NONE)
                .texOffs(0, 79).addBox(-10F, -10F, -14F, 20, 4, 28, NONE)
                .texOffs(96, 79).addBox(-9F, 4F, -13F, 18, 3, 25, NONE)
                .texOffs(0, 45).addBox(-13.5F, -7F, -14F, 2, 7, 27, NONE)
                .texOffs(58, 45).addBox(11.5F, -7F, -14F, 2, 7, 27, NONE)
                .texOffs(76, 111).addBox(-14F, -8F, -10F, 2, 1, 18, NONE)
                .texOffs(116, 111).addBox(12F, -8F, -10F, 2, 1, 18, NONE)
                .texOffs(0, 137).addBox(-9F, -9F, 17F, 18, 11, 6, NONE)
                .texOffs(168, 237).addBox(-7F, -7F, 22.5F, 14, 6, 1, NONE)
                .texOffs(10, 251).addBox(-5F, -6F, 23F, 3, 2, 1, NONE)
                .texOffs(18, 251).addBox(2F, -6F, 23F, 3, 2, 1, NONE)
                .texOffs(120, 237).addBox(-10F, 1F, -21F, 20, 3, 4, NONE)
                .texOffs(142, 228).addBox(-9F, -11F, 7F, 18, 1, 7, NONE)
                .texOffs(0, 245).addBox(-7F, -11F, -13F, 14, 1, 5, NONE),
                PartPose.offsetAndRotation(0F, 2F, 2F, 0F, 0F, 0F));
        PartDefinition p_glacis = p_hull.addOrReplaceChild("glacis", CubeListBuilder.create()
                .texOffs(0, 185).addBox(-11F, 0F, -2F, 22, 11, 2, NONE)
                .texOffs(212, 245).addBox(-6.5F, 2.5F, -3F, 4, 3, 1, NONE)
                .texOffs(222, 245).addBox(2.5F, 2.5F, -3F, 4, 3, 1, NONE)
                .texOffs(0, 251).addBox(-2F, 1.5F, -2.5F, 4, 2, 1, NONE)
                .texOffs(66, 251).addBox(-9F, 6.5F, -2.6F, 2, 2, 1, NONE)
                .texOffs(72, 251).addBox(7F, 6.5F, -2.6F, 2, 2, 1, NONE)
                .texOffs(26, 251).addBox(-1.5F, 6F, -2.6F, 3, 2, 1, NONE)
                .texOffs(84, 251).addBox(-8F, 1F, -3F, 4, 1, 1, NONE)
                .texOffs(94, 251).addBox(4F, 1F, -3F, 4, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -6F, -17F, -0.663F, 0F, 0F));
        PartDefinition p_turret = p_hull.addOrReplaceChild("turret", CubeListBuilder.create()
                .texOffs(180, 111).addBox(-7F, -3F, -7F, 14, 3, 14, NONE)
                .texOffs(0, 111).addBox(-10F, -11F, -9F, 20, 8, 18, NONE)
                .texOffs(0, 154).addBox(-9F, -12F, -7F, 18, 1, 15, NONE)
                .texOffs(30, 170).addBox(-9F, -10F, 9F, 18, 7, 7, NONE)
                .texOffs(38, 245).addBox(-8F, -11F, 14F, 16, 2, 3, NONE)
                .texOffs(192, 228).addBox(-7F, -14F, 1F, 6, 2, 6, NONE)
                .texOffs(88, 198).addBox(-12F, -9F, -6F, 2, 3, 8, NONE)
                .texOffs(108, 198).addBox(10F, -9F, -6F, 2, 3, 8, NONE)
                .texOffs(176, 245).addBox(-10.5F, -5F, -9.5F, 7, 2, 2, NONE)
                .texOffs(194, 245).addBox(3.5F, -5F, -9.5F, 7, 2, 2, NONE)
                .texOffs(34, 251).addBox(5F, -8F, 15.5F, 3, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -10F, 0F, 0F, 0F, 0F));
        PartDefinition p_cannon = p_turret.addOrReplaceChild("cannon", CubeListBuilder.create()
                .texOffs(228, 185).addBox(-4.5F, -3.5F, -4F, 9, 7, 4, NONE)
                .texOffs(116, 45).addBox(-1.5F, -1.5F, -33F, 3, 3, 30, NONE)
                .texOffs(0, 198).addBox(-2.5F, -2.5F, -20F, 5, 5, 6, NONE)
                .texOffs(176, 209).addBox(-3F, -2.5F, -38F, 6, 5, 5, NONE)
                .texOffs(78, 251).addBox(-1F, -1F, -38.5F, 2, 2, 1, NONE)
                .texOffs(230, 237).addBox(-2F, -2F, -9F, 4, 4, 3, NONE)
                .texOffs(198, 209).addBox(3F, 0F, -12F, 1, 1, 9, NONE),
                PartPose.offsetAndRotation(0F, -6F, -10F, 0F, 0F, 0F));
        PartDefinition p_antenna = p_turret.addOrReplaceChild("antenna", CubeListBuilder.create()
                .texOffs(216, 137).addBox(-0.5F, -16F, -0.5F, 1, 16, 1, NONE)
                .texOffs(232, 245).addBox(-1F, -17F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(-7F, -12F, 8F, -0.314F, 0F, -0.14F));
        PartDefinition p_periscope = p_turret.addOrReplaceChild("periscope", CubeListBuilder.create()
                .texOffs(40, 228).addBox(-2F, -4F, -2F, 4, 4, 5, NONE)
                .texOffs(42, 251).addBox(-1.5F, -3.5F, -2.5F, 3, 2, 1, NONE)
                .texOffs(240, 245).addBox(-1F, -1F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(4.5F, -12F, -3F, 0F, -0.262F, 0F));
        PartDefinition p_turret_front = p_turret.addOrReplaceChild("turret_front", CubeListBuilder.create()
                .texOffs(0, 228).addBox(-9F, 0F, -2F, 18, 7, 2, NONE),
                PartPose.offsetAndRotation(0F, -11F, -9F, -0.524F, 0F, 0F));
        PartDefinition p_mandible_r = p_hull.addOrReplaceChild("mandible_r", CubeListBuilder.create()
                .texOffs(22, 198).addBox(-1.5F, -1.5F, -7F, 3, 3, 8, NONE)
                .texOffs(160, 245).addBox(-1F, -1F, -9F, 2, 3, 2, NONE),
                PartPose.offsetAndRotation(-6F, 4F, -19F, 0.349F, 0.314F, 0F));
        PartDefinition p_mandible_l = p_hull.addOrReplaceChild("mandible_l", CubeListBuilder.create()
                .texOffs(44, 198).addBox(-1.5F, -1.5F, -7F, 3, 3, 8, NONE)
                .texOffs(168, 245).addBox(-1F, -1F, -9F, 2, 3, 2, NONE),
                PartPose.offsetAndRotation(6F, 4F, -19F, 0.349F, -0.314F, 0F));
        PartDefinition p_exhaust_r = p_hull.addOrReplaceChild("exhaust_r", CubeListBuilder.create()
                .texOffs(156, 111).addBox(-1.5F, -15F, -1.5F, 3, 15, 3, NONE)
                .texOffs(198, 237).addBox(-2F, -17F, -2F, 4, 3, 4, NONE)
                .texOffs(50, 251).addBox(-1F, -17.5F, -1F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(-6F, -6F, 17F, -0.384F, 0F, -0.209F));
        PartDefinition p_exhaust_l = p_hull.addOrReplaceChild("exhaust_l", CubeListBuilder.create()
                .texOffs(168, 111).addBox(-1.5F, -15F, -1.5F, 3, 15, 3, NONE)
                .texOffs(214, 237).addBox(-2F, -17F, -2F, 4, 3, 4, NONE)
                .texOffs(58, 251).addBox(-1F, -17.5F, -1F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(6F, -6F, 17F, -0.384F, 0F, 0.209F));
        PartDefinition p_cable_0 = p_hull.addOrReplaceChild("cable_0", CubeListBuilder.create()
                .texOffs(0, 170).addBox(-0.5F, -0.5F, 0F, 1, 1, 14, NONE),
                PartPose.offsetAndRotation(-9F, -10F, -6F, -0.14F, 0.524F, 0F));
        PartDefinition p_cable_1 = p_hull.addOrReplaceChild("cable_1", CubeListBuilder.create()
                .texOffs(80, 170).addBox(-0.5F, -0.5F, 0F, 1, 1, 13, NONE),
                PartPose.offsetAndRotation(9.5F, -10F, 4F, 0.209F, -0.349F, 0F));
        PartDefinition p_cable_2 = p_hull.addOrReplaceChild("cable_2", CubeListBuilder.create()
                .texOffs(66, 198).addBox(-0.5F, -0.5F, 0F, 1, 1, 10, NONE),
                PartPose.offsetAndRotation(-10F, -10F, 6F, 0.262F, 0.175F, 0F));
        PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create()
                .texOffs(66, 154).addBox(-2F, -4F, -4F, 6, 8, 8, NONE),
                PartPose.offsetAndRotation(12F, 0F, -11F, 0F, 0.663F, 0F));
        PartDefinition p_thigh_fl = p_leg_fl.addOrReplaceChild("thigh_fl", CubeListBuilder.create()
                .texOffs(128, 198).addBox(0F, -2.5F, -2.5F, 17, 5, 5, NONE)
                .texOffs(0, 219).addBox(1F, -4F, -3.5F, 14, 2, 7, NONE)
                .texOffs(108, 170).addBox(14.5F, -3.5F, -3.5F, 5, 7, 7, NONE),
                PartPose.offsetAndRotation(3F, 0F, 0F, 0F, 0F, -0.698F));
        PartDefinition p_shin_fl = p_thigh_fl.addOrReplaceChild("shin_fl", CubeListBuilder.create()
                .texOffs(118, 0).addBox(-2.5F, 0F, -2.5F, 5, 32, 5, NONE)
                .texOffs(48, 137).addBox(-3.5F, 1F, -3.5F, 7, 10, 7, NONE)
                .texOffs(216, 228).addBox(-3F, 25F, -3F, 6, 2, 6, NONE)
                .texOffs(58, 228).addBox(-2F, -6F, -1.5F, 4, 6, 3, NONE),
                PartPose.offsetAndRotation(16F, 0F, 0F, 0F, 0F, 0.471F));
        PartDefinition p_foot_fl = p_shin_fl.addOrReplaceChild("foot_fl", CubeListBuilder.create()
                .texOffs(48, 185).addBox(-3.5F, 0F, -4F, 7, 3, 8, NONE)
                .texOffs(76, 245).addBox(-2F, 1F, -6.5F, 4, 2, 3, NONE),
                PartPose.offsetAndRotation(0F, 32F, 0F, 0F, 0F, 0.227F));
        PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create()
                .texOffs(94, 154).addBox(-4F, -4F, -4F, 6, 8, 8, NONE),
                PartPose.offsetAndRotation(-12F, 0F, -11F, 0F, -0.663F, 0F));
        PartDefinition p_thigh_fr = p_leg_fr.addOrReplaceChild("thigh_fr", CubeListBuilder.create()
                .texOffs(172, 198).addBox(-17F, -2.5F, -2.5F, 17, 5, 5, NONE)
                .texOffs(42, 219).addBox(-15F, -4F, -3.5F, 14, 2, 7, NONE)
                .texOffs(132, 170).addBox(-19.5F, -3.5F, -3.5F, 5, 7, 7, NONE),
                PartPose.offsetAndRotation(-3F, 0F, 0F, 0F, 0F, 0.698F));
        PartDefinition p_shin_fr = p_thigh_fr.addOrReplaceChild("shin_fr", CubeListBuilder.create()
                .texOffs(138, 0).addBox(-2.5F, 0F, -2.5F, 5, 32, 5, NONE)
                .texOffs(76, 137).addBox(-3.5F, 1F, -3.5F, 7, 10, 7, NONE)
                .texOffs(0, 237).addBox(-3F, 25F, -3F, 6, 2, 6, NONE)
                .texOffs(72, 228).addBox(-2F, -6F, -1.5F, 4, 6, 3, NONE),
                PartPose.offsetAndRotation(-16F, 0F, 0F, 0F, 0F, -0.471F));
        PartDefinition p_foot_fr = p_shin_fr.addOrReplaceChild("foot_fr", CubeListBuilder.create()
                .texOffs(78, 185).addBox(-3.5F, 0F, -4F, 7, 3, 8, NONE)
                .texOffs(90, 245).addBox(-2F, 1F, -6.5F, 4, 2, 3, NONE),
                PartPose.offsetAndRotation(0F, 32F, 0F, 0F, 0F, -0.227F));
        PartDefinition p_leg_ml = root.addOrReplaceChild("leg_ml", CubeListBuilder.create()
                .texOffs(122, 154).addBox(-2F, -4F, -4F, 6, 8, 8, NONE),
                PartPose.offsetAndRotation(12F, 0F, 1F, 0F, 0.07F, 0F));
        PartDefinition p_thigh_ml = p_leg_ml.addOrReplaceChild("thigh_ml", CubeListBuilder.create()
                .texOffs(0, 209).addBox(0F, -2.5F, -2.5F, 17, 5, 5, NONE)
                .texOffs(84, 219).addBox(1F, -4F, -3.5F, 14, 2, 7, NONE)
                .texOffs(156, 170).addBox(14.5F, -3.5F, -3.5F, 5, 7, 7, NONE),
                PartPose.offsetAndRotation(3F, 0F, 0F, 0F, 0F, -0.698F));
        PartDefinition p_shin_ml = p_thigh_ml.addOrReplaceChild("shin_ml", CubeListBuilder.create()
                .texOffs(158, 0).addBox(-2.5F, 0F, -2.5F, 5, 32, 5, NONE)
                .texOffs(104, 137).addBox(-3.5F, 1F, -3.5F, 7, 10, 7, NONE)
                .texOffs(24, 237).addBox(-3F, 25F, -3F, 6, 2, 6, NONE)
                .texOffs(86, 228).addBox(-2F, -6F, -1.5F, 4, 6, 3, NONE),
                PartPose.offsetAndRotation(16F, 0F, 0F, 0F, 0F, 0.471F));
        PartDefinition p_foot_ml = p_shin_ml.addOrReplaceChild("foot_ml", CubeListBuilder.create()
                .texOffs(108, 185).addBox(-3.5F, 0F, -4F, 7, 3, 8, NONE)
                .texOffs(104, 245).addBox(-2F, 1F, -6.5F, 4, 2, 3, NONE),
                PartPose.offsetAndRotation(0F, 32F, 0F, 0F, 0F, 0.227F));
        PartDefinition p_leg_mr = root.addOrReplaceChild("leg_mr", CubeListBuilder.create()
                .texOffs(150, 154).addBox(-4F, -4F, -4F, 6, 8, 8, NONE),
                PartPose.offsetAndRotation(-12F, 0F, 1F, 0F, -0.07F, 0F));
        PartDefinition p_thigh_mr = p_leg_mr.addOrReplaceChild("thigh_mr", CubeListBuilder.create()
                .texOffs(44, 209).addBox(-17F, -2.5F, -2.5F, 17, 5, 5, NONE)
                .texOffs(126, 219).addBox(-15F, -4F, -3.5F, 14, 2, 7, NONE)
                .texOffs(180, 170).addBox(-19.5F, -3.5F, -3.5F, 5, 7, 7, NONE),
                PartPose.offsetAndRotation(-3F, 0F, 0F, 0F, 0F, 0.698F));
        PartDefinition p_shin_mr = p_thigh_mr.addOrReplaceChild("shin_mr", CubeListBuilder.create()
                .texOffs(178, 0).addBox(-2.5F, 0F, -2.5F, 5, 32, 5, NONE)
                .texOffs(132, 137).addBox(-3.5F, 1F, -3.5F, 7, 10, 7, NONE)
                .texOffs(48, 237).addBox(-3F, 25F, -3F, 6, 2, 6, NONE)
                .texOffs(100, 228).addBox(-2F, -6F, -1.5F, 4, 6, 3, NONE),
                PartPose.offsetAndRotation(-16F, 0F, 0F, 0F, 0F, -0.471F));
        PartDefinition p_foot_mr = p_shin_mr.addOrReplaceChild("foot_mr", CubeListBuilder.create()
                .texOffs(138, 185).addBox(-3.5F, 0F, -4F, 7, 3, 8, NONE)
                .texOffs(118, 245).addBox(-2F, 1F, -6.5F, 4, 2, 3, NONE),
                PartPose.offsetAndRotation(0F, 32F, 0F, 0F, 0F, -0.227F));
        PartDefinition p_leg_bl = root.addOrReplaceChild("leg_bl", CubeListBuilder.create()
                .texOffs(178, 154).addBox(-2F, -4F, -4F, 6, 8, 8, NONE),
                PartPose.offsetAndRotation(12F, 0F, 13F, 0F, -0.628F, 0F));
        PartDefinition p_thigh_bl = p_leg_bl.addOrReplaceChild("thigh_bl", CubeListBuilder.create()
                .texOffs(88, 209).addBox(0F, -2.5F, -2.5F, 17, 5, 5, NONE)
                .texOffs(168, 219).addBox(1F, -4F, -3.5F, 14, 2, 7, NONE)
                .texOffs(204, 170).addBox(14.5F, -3.5F, -3.5F, 5, 7, 7, NONE),
                PartPose.offsetAndRotation(3F, 0F, 0F, 0F, 0F, -0.698F));
        PartDefinition p_shin_bl = p_thigh_bl.addOrReplaceChild("shin_bl", CubeListBuilder.create()
                .texOffs(198, 0).addBox(-2.5F, 0F, -2.5F, 5, 32, 5, NONE)
                .texOffs(160, 137).addBox(-3.5F, 1F, -3.5F, 7, 10, 7, NONE)
                .texOffs(72, 237).addBox(-3F, 25F, -3F, 6, 2, 6, NONE)
                .texOffs(114, 228).addBox(-2F, -6F, -1.5F, 4, 6, 3, NONE),
                PartPose.offsetAndRotation(16F, 0F, 0F, 0F, 0F, 0.471F));
        PartDefinition p_foot_bl = p_shin_bl.addOrReplaceChild("foot_bl", CubeListBuilder.create()
                .texOffs(168, 185).addBox(-3.5F, 0F, -4F, 7, 3, 8, NONE)
                .texOffs(132, 245).addBox(-2F, 1F, -6.5F, 4, 2, 3, NONE),
                PartPose.offsetAndRotation(0F, 32F, 0F, 0F, 0F, 0.227F));
        PartDefinition p_leg_br = root.addOrReplaceChild("leg_br", CubeListBuilder.create()
                .texOffs(206, 154).addBox(-4F, -4F, -4F, 6, 8, 8, NONE),
                PartPose.offsetAndRotation(-12F, 0F, 13F, 0F, 0.628F, 0F));
        PartDefinition p_thigh_br = p_leg_br.addOrReplaceChild("thigh_br", CubeListBuilder.create()
                .texOffs(132, 209).addBox(-17F, -2.5F, -2.5F, 17, 5, 5, NONE)
                .texOffs(210, 219).addBox(-15F, -4F, -3.5F, 14, 2, 7, NONE)
                .texOffs(228, 170).addBox(-19.5F, -3.5F, -3.5F, 5, 7, 7, NONE),
                PartPose.offsetAndRotation(-3F, 0F, 0F, 0F, 0F, 0.698F));
        PartDefinition p_shin_br = p_thigh_br.addOrReplaceChild("shin_br", CubeListBuilder.create()
                .texOffs(218, 0).addBox(-2.5F, 0F, -2.5F, 5, 32, 5, NONE)
                .texOffs(188, 137).addBox(-3.5F, 1F, -3.5F, 7, 10, 7, NONE)
                .texOffs(96, 237).addBox(-3F, 25F, -3F, 6, 2, 6, NONE)
                .texOffs(128, 228).addBox(-2F, -6F, -1.5F, 4, 6, 3, NONE),
                PartPose.offsetAndRotation(-16F, 0F, 0F, 0F, 0F, -0.471F));
        PartDefinition p_foot_br = p_shin_br.addOrReplaceChild("foot_br", CubeListBuilder.create()
                .texOffs(198, 185).addBox(-3.5F, 0F, -4F, 7, 3, 8, NONE)
                .texOffs(146, 245).addBox(-2F, 1F, -6.5F, 4, 2, 3, NONE),
                PartPose.offsetAndRotation(0F, 32F, 0F, 0F, 0F, -0.227F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    public static final Role[] RUST_TITAN_ROLES = {new Role("hull", "body", 0.00F), new Role("hull/turret", "head", 0.00F), new Role("leg_fl", "leg_a", 0.00F), new Role("leg_fr", "leg_b", 0.00F), new Role("leg_ml", "leg_b", 0.00F), new Role("leg_mr", "leg_a", 0.00F), new Role("leg_bl", "leg_a", 0.00F), new Role("leg_br", "leg_b", 0.00F)};

    public static final Key[] RUST_TITAN_POSES = {
            new Key(0, 0F, "hull/turret", 0F, 28F, 0F),
            new Key(0, 0F, "hull/turret/cannon", 24F, 0F, 0F),
            new Key(0, 0F, "hull", 5F, 0F, 0F),
            new Key(0, 0F, "hull/turret/antenna", 40F, 0F, 0F),
            new Key(0, 0F, "hull/turret/periscope", 20F, 0F, 0F),
            new Key(1, 0F, "hull/turret/cannon", 0F, 0F, 0F),
            new Key(1, 8F, "hull/turret/cannon", -14F, 0F, 0F),
            new Key(1, 15F, "hull/turret/cannon", -14F, 0F, 0F),
            new Key(1, 16F, "hull/turret/cannon", -24F, 0F, 0F),
            new Key(1, 20F, "hull/turret/cannon", 0F, 0F, 0F),
            new Key(1, 0F, "hull", 0F, 0F, 0F),
            new Key(1, 8F, "hull", 4F, 0F, 0F),
            new Key(1, 16F, "hull", -6F, 0F, 0F),
            new Key(1, 20F, "hull", 0F, 0F, 0F),
            new Key(1, 0F, "hull/turret", 0F, 0F, 0F),
            new Key(1, 8F, "hull/turret", 0F, 0F, 0F),
            new Key(1, 16F, "hull/turret", -6F, 0F, 0F),
            new Key(1, 20F, "hull/turret", 0F, 0F, 0F),
            new Key(2, 0F, "hull/turret", 0F, 0F, 0F),
            new Key(2, 15F, "hull/turret", 0F, 90F, 0F),
            new Key(2, 30F, "hull/turret", 0F, 180F, 0F),
            new Key(2, 45F, "hull/turret", 0F, 270F, 0F),
            new Key(2, 60F, "hull/turret", 0F, 360F, 0F),
            new Key(2, 0F, "hull", 0F, 0F, 0F),
            new Key(2, 8F, "hull", 0F, 0F, 0F),
            new Key(2, 52F, "hull", 0F, 0F, 0F),
            new Key(2, 60F, "hull", 0F, 0F, 0F),
            new Key(2, 0F, "hull/turret/cannon", 0F, 0F, 0F),
            new Key(2, 8F, "hull/turret/cannon", -20F, 0F, 0F),
            new Key(2, 52F, "hull/turret/cannon", -20F, 0F, 0F),
            new Key(2, 60F, "hull/turret/cannon", 0F, 0F, 0F),
            new Key(2, 0F, "hull/exhaust_r", 0F, 0F, 0F),
            new Key(2, 6F, "hull/exhaust_r", -15F, 0F, -10F),
            new Key(2, 54F, "hull/exhaust_r", -15F, 0F, -10F),
            new Key(2, 60F, "hull/exhaust_r", 0F, 0F, 0F),
            new Key(2, 0F, "hull/exhaust_l", 0F, 0F, 0F),
            new Key(2, 6F, "hull/exhaust_l", -15F, 0F, 10F),
            new Key(2, 54F, "hull/exhaust_l", -15F, 0F, 10F),
            new Key(2, 60F, "hull/exhaust_l", 0F, 0F, 0F),
            new Key(2, 0F, "leg_fl/thigh_fl", 0F, 0F, 0F),
            new Key(2, 8F, "leg_fl/thigh_fl", 0F, 0F, 12F),
            new Key(2, 52F, "leg_fl/thigh_fl", 0F, 0F, 12F),
            new Key(2, 60F, "leg_fl/thigh_fl", 0F, 0F, 0F),
            new Key(2, 0F, "leg_fr/thigh_fr", 0F, 0F, 0F),
            new Key(2, 8F, "leg_fr/thigh_fr", 0F, 0F, -12F),
            new Key(2, 52F, "leg_fr/thigh_fr", 0F, 0F, -12F),
            new Key(2, 60F, "leg_fr/thigh_fr", 0F, 0F, 0F),
            new Key(2, 0F, "leg_ml/thigh_ml", 0F, 0F, 0F),
            new Key(2, 8F, "leg_ml/thigh_ml", 0F, 0F, 12F),
            new Key(2, 52F, "leg_ml/thigh_ml", 0F, 0F, 12F),
            new Key(2, 60F, "leg_ml/thigh_ml", 0F, 0F, 0F),
            new Key(2, 0F, "leg_mr/thigh_mr", 0F, 0F, 0F),
            new Key(2, 8F, "leg_mr/thigh_mr", 0F, 0F, -12F),
            new Key(2, 52F, "leg_mr/thigh_mr", 0F, 0F, -12F),
            new Key(2, 60F, "leg_mr/thigh_mr", 0F, 0F, 0F),
            new Key(2, 0F, "leg_bl/thigh_bl", 0F, 0F, 0F),
            new Key(2, 8F, "leg_bl/thigh_bl", 0F, 0F, 12F),
            new Key(2, 52F, "leg_bl/thigh_bl", 0F, 0F, 12F),
            new Key(2, 60F, "leg_bl/thigh_bl", 0F, 0F, 0F),
            new Key(2, 0F, "leg_br/thigh_br", 0F, 0F, 0F),
            new Key(2, 8F, "leg_br/thigh_br", 0F, 0F, -12F),
            new Key(2, 52F, "leg_br/thigh_br", 0F, 0F, -12F),
            new Key(2, 60F, "leg_br/thigh_br", 0F, 0F, 0F),
            new Key(3, 0F, "hull", 0F, 0F, 0F),
            new Key(3, 8F, "hull", -8F, 0F, 0F),
            new Key(3, 12F, "hull", 9F, 0F, 0F),
            new Key(3, 22F, "hull", 9F, 0F, 0F),
            new Key(3, 24F, "hull", 0F, 0F, 0F),
            new Key(3, 0F, "hull/turret/cannon", 0F, 0F, 0F),
            new Key(3, 8F, "hull/turret/cannon", -10F, 0F, 0F),
            new Key(3, 12F, "hull/turret/cannon", 8F, 0F, 0F),
            new Key(3, 22F, "hull/turret/cannon", 8F, 0F, 0F),
            new Key(3, 24F, "hull/turret/cannon", 0F, 0F, 0F),
            new Key(3, 0F, "hull/mandible_r", 0F, 0F, 0F),
            new Key(3, 8F, "hull/mandible_r", 0F, -25F, 0F),
            new Key(3, 12F, "hull/mandible_r", 0F, 10F, 0F),
            new Key(3, 22F, "hull/mandible_r", 0F, 10F, 0F),
            new Key(3, 24F, "hull/mandible_r", 0F, 0F, 0F),
            new Key(3, 0F, "hull/mandible_l", 0F, 0F, 0F),
            new Key(3, 8F, "hull/mandible_l", 0F, 25F, 0F),
            new Key(3, 12F, "hull/mandible_l", 0F, -10F, 0F),
            new Key(3, 22F, "hull/mandible_l", 0F, -10F, 0F),
            new Key(3, 24F, "hull/mandible_l", 0F, 0F, 0F),
            new Key(3, 0F, "leg_fl/thigh_fl", 0F, 0F, 0F),
            new Key(3, 8F, "leg_fl/thigh_fl", 0F, 0F, -25F),
            new Key(3, 12F, "leg_fl/thigh_fl", 0F, 0F, 0F),
            new Key(3, 24F, "leg_fl/thigh_fl", 0F, 0F, 0F),
            new Key(3, 0F, "leg_fr/thigh_fr", 0F, 0F, 0F),
            new Key(3, 8F, "leg_fr/thigh_fr", 0F, 0F, 25F),
            new Key(3, 12F, "leg_fr/thigh_fr", 0F, 0F, 0F),
            new Key(3, 24F, "leg_fr/thigh_fr", 0F, 0F, 0F),
            new Key(4, 0F, "hull", 0F, 0F, 0F),
            new Key(4, 6F, "hull", -7F, 0F, 0F),
            new Key(4, 12F, "hull", 5F, 0F, 0F),
            new Key(4, 15F, "hull", -4F, 0F, 0F),
            new Key(4, 18F, "hull", 5F, 0F, 0F),
            new Key(4, 21F, "hull", -4F, 0F, 0F),
            new Key(4, 24F, "hull", 5F, 0F, 0F),
            new Key(4, 26F, "hull", 0F, 0F, 0F),
            new Key(4, 0F, "leg_fl/thigh_fl", 0F, 0F, 0F),
            new Key(4, 6F, "leg_fl/thigh_fl", 0F, 0F, -30F),
            new Key(4, 12F, "leg_fl/thigh_fl", 0F, 0F, 4F),
            new Key(4, 15F, "leg_fl/thigh_fl", 0F, 0F, -20F),
            new Key(4, 18F, "leg_fl/thigh_fl", 0F, 0F, 4F),
            new Key(4, 21F, "leg_fl/thigh_fl", 0F, 0F, -20F),
            new Key(4, 24F, "leg_fl/thigh_fl", 0F, 0F, 4F),
            new Key(4, 26F, "leg_fl/thigh_fl", 0F, 0F, 0F),
            new Key(4, 0F, "leg_fr/thigh_fr", 0F, 0F, 0F),
            new Key(4, 6F, "leg_fr/thigh_fr", 0F, 0F, 30F),
            new Key(4, 12F, "leg_fr/thigh_fr", 0F, 0F, -4F),
            new Key(4, 15F, "leg_fr/thigh_fr", 0F, 0F, 20F),
            new Key(4, 18F, "leg_fr/thigh_fr", 0F, 0F, -4F),
            new Key(4, 21F, "leg_fr/thigh_fr", 0F, 0F, 20F),
            new Key(4, 24F, "leg_fr/thigh_fr", 0F, 0F, -4F),
            new Key(4, 26F, "leg_fr/thigh_fr", 0F, 0F, 0F),
            new Key(4, 0F, "leg_ml/thigh_ml", 0F, 0F, 0F),
            new Key(4, 6F, "leg_ml/thigh_ml", 0F, 0F, -30F),
            new Key(4, 12F, "leg_ml/thigh_ml", 0F, 0F, 4F),
            new Key(4, 15F, "leg_ml/thigh_ml", 0F, 0F, -20F),
            new Key(4, 18F, "leg_ml/thigh_ml", 0F, 0F, 4F),
            new Key(4, 21F, "leg_ml/thigh_ml", 0F, 0F, -20F),
            new Key(4, 24F, "leg_ml/thigh_ml", 0F, 0F, 4F),
            new Key(4, 26F, "leg_ml/thigh_ml", 0F, 0F, 0F),
            new Key(4, 0F, "leg_mr/thigh_mr", 0F, 0F, 0F),
            new Key(4, 6F, "leg_mr/thigh_mr", 0F, 0F, 30F),
            new Key(4, 12F, "leg_mr/thigh_mr", 0F, 0F, -4F),
            new Key(4, 15F, "leg_mr/thigh_mr", 0F, 0F, 20F),
            new Key(4, 18F, "leg_mr/thigh_mr", 0F, 0F, -4F),
            new Key(4, 21F, "leg_mr/thigh_mr", 0F, 0F, 20F),
            new Key(4, 24F, "leg_mr/thigh_mr", 0F, 0F, -4F),
            new Key(4, 26F, "leg_mr/thigh_mr", 0F, 0F, 0F)};

    public static final float RUST_TITAN_SCALE = 1.4F;

    /** Monolith Keeper: schwebender schwarzer Obelisk mit violett leuchtenden Runen, Schlitzauge, zwei schwebenden Steinhandschuhen, kreisenden Splittern und gebrochenem Steinhalo. Textur {@code entity/stage/monolith_keeper.png}. */
    public static LayerDefinition monolithKeeper() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_monolith = root.addOrReplaceChild("monolith", CubeListBuilder.create()
                .texOffs(148, 0).addBox(-7.5F, -2F, -6.5F, 15, 9, 13, NONE)
                .texOffs(34, 29).addBox(-8F, -3F, -7F, 16, 2, 14, NONE)
                .texOffs(78, 46).addBox(-4.5F, -6F, -4F, 9, 4, 8, NONE)
                .texOffs(112, 46).addBox(-6F, 7F, -5F, 8, 3, 9, NONE)
                .texOffs(234, 59).addBox(1F, 7F, -3F, 5, 4, 6, NONE)
                .texOffs(184, 71).addBox(-3F, 6F, 1F, 4, 2, 5, NONE),
                PartPose.offsetAndRotation(0F, 2F, 0F, 0F, 0F, 0F));
        PartDefinition p_head = p_monolith.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-6.5F, -18F, -6F, 13, 17, 12, NONE)
                .texOffs(158, 80).addBox(-5.5F, -12F, -6.5F, 11, 4, 1, NONE)
                .texOffs(116, 87).addBox(-4.5F, -11F, -7F, 9, 2, 1, NONE)
                .texOffs(32, 80).addBox(-7.5F, -15F, -8F, 15, 2, 4, NONE)
                .texOffs(130, 29).addBox(-7F, -19.5F, -6.5F, 14, 2, 13, NONE),
                PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, 0F));
        PartDefinition p_spire = p_head.addOrReplaceChild("spire", CubeListBuilder.create()
                .texOffs(50, 0).addBox(-5F, -18F, -4.5F, 10, 18, 9, NONE)
                .texOffs(36, 46).addBox(-5.5F, -2F, -5F, 11, 2, 10, NONE)
                .texOffs(228, 59).addBox(-1F, -15F, -5F, 2, 10, 1, NONE)
                .texOffs(54, 59).addBox(-5.5F, -19F, -5F, 11, 1, 10, NONE),
                PartPose.offsetAndRotation(0F, -20F, 0F, 0F, 0.157F, 0F));
        PartDefinition p_cap = p_spire.addOrReplaceChild("cap", CubeListBuilder.create()
                .texOffs(0, 46).addBox(-4.5F, -4F, -4.5F, 9, 4, 9, NONE)
                .texOffs(0, 71).addBox(-3F, -7F, -3F, 6, 3, 6, NONE)
                .texOffs(182, 80).addBox(-1.5F, -9F, -1.5F, 3, 2, 3, NONE)
                .texOffs(52, 87).addBox(-1F, -12F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, -19F, 0F, 0F, -0.209F, 0F));
        PartDefinition p_halo = p_head.addOrReplaceChild("halo", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F, -32F, 10F, 0.105F, 0F, 0F));
        PartDefinition p_halo_0 = p_halo.addOrReplaceChild("halo_0", CubeListBuilder.create()
                .texOffs(146, 46).addBox(-2.5F, -4F, -2F, 5, 8, 4, NONE),
                PartPose.offsetAndRotation(0F, -19F, 0F, 0F, 0F, -1.571F));
        PartDefinition p_halo_1 = p_halo.addOrReplaceChild("halo_1", CubeListBuilder.create()
                .texOffs(164, 46).addBox(-2.5F, -4F, -2F, 5, 8, 4, NONE)
                .texOffs(194, 80).addBox(-3F, -1F, -1.5F, 1, 2, 3, NONE),
                PartPose.offsetAndRotation(9.5F, -16.454F, 0F, 0F, 0F, -1.047F));
        PartDefinition p_halo_4 = p_halo.addOrReplaceChild("halo_4", CubeListBuilder.create()
                .texOffs(182, 46).addBox(-2.5F, -4F, -2F, 5, 8, 4, NONE)
                .texOffs(202, 80).addBox(-3F, -1F, -1.5F, 1, 2, 3, NONE),
                PartPose.offsetAndRotation(18.187F, 10.5F, 0F, 0F, 0F, 0.768F));
        PartDefinition p_halo_5 = p_halo.addOrReplaceChild("halo_5", CubeListBuilder.create()
                .texOffs(200, 46).addBox(-2.5F, -4F, -2F, 5, 8, 4, NONE),
                PartPose.offsetAndRotation(9.5F, 16.454F, 0F, 0F, 0F, 1.047F));
        PartDefinition p_halo_6 = p_halo.addOrReplaceChild("halo_6", CubeListBuilder.create()
                .texOffs(218, 46).addBox(-2.5F, -4F, -2F, 5, 8, 4, NONE),
                PartPose.offsetAndRotation(0F, 19F, 0F, 0F, 0F, 1.571F));
        PartDefinition p_halo_7 = p_halo.addOrReplaceChild("halo_7", CubeListBuilder.create()
                .texOffs(236, 46).addBox(-2.5F, -4F, -2F, 5, 8, 4, NONE)
                .texOffs(210, 80).addBox(-3F, -1F, -1.5F, 1, 2, 3, NONE),
                PartPose.offsetAndRotation(-9.5F, 16.454F, 0F, 0F, 0F, 2.094F));
        PartDefinition p_halo_9 = p_halo.addOrReplaceChild("halo_9", CubeListBuilder.create()
                .texOffs(0, 59).addBox(-2.5F, -4F, -2F, 5, 8, 4, NONE),
                PartPose.offsetAndRotation(-19F, 0F, 0F, 0F, 0F, 2.967F));
        PartDefinition p_halo_10 = p_halo.addOrReplaceChild("halo_10", CubeListBuilder.create()
                .texOffs(18, 59).addBox(-2.5F, -4F, -2F, 5, 8, 4, NONE)
                .texOffs(218, 80).addBox(-3F, -1F, -1.5F, 1, 2, 3, NONE),
                PartPose.offsetAndRotation(-16.454F, -9.5F, 0F, 0F, 0F, 3.665F));
        PartDefinition p_halo_11 = p_halo.addOrReplaceChild("halo_11", CubeListBuilder.create()
                .texOffs(36, 59).addBox(-2.5F, -4F, -2F, 5, 8, 4, NONE),
                PartPose.offsetAndRotation(-9.5F, -16.454F, 0F, 0F, 0F, 4.189F));
        PartDefinition p_slab_r = p_head.addOrReplaceChild("slab_r", CubeListBuilder.create()
                .texOffs(88, 0).addBox(-2F, -8F, -6F, 3, 14, 12, NONE)
                .texOffs(94, 29).addBox(-2.5F, -5F, -4F, 1, 8, 8, NONE)
                .texOffs(184, 29).addBox(-2.5F, -9F, -6.5F, 4, 2, 13, NONE),
                PartPose.offsetAndRotation(-10F, -12F, 0F, 0F, 0.14F, 0.244F));
        PartDefinition p_slab_l = p_head.addOrReplaceChild("slab_l", CubeListBuilder.create()
                .texOffs(118, 0).addBox(-1F, -8F, -6F, 3, 14, 12, NONE)
                .texOffs(112, 29).addBox(1.5F, -5F, -4F, 1, 8, 8, NONE)
                .texOffs(218, 29).addBox(-1.5F, -9F, -6.5F, 4, 2, 13, NONE),
                PartPose.offsetAndRotation(10F, -12F, 0F, 0F, -0.14F, -0.244F));
        PartDefinition p_hand_r = p_monolith.addOrReplaceChild("hand_r", CubeListBuilder.create()
                .texOffs(96, 59).addBox(-4F, -9F, -3.5F, 8, 4, 7, NONE)
                .texOffs(140, 71).addBox(-3F, -11F, -2.5F, 6, 2, 5, NONE)
                .texOffs(204, 0).addBox(-5F, -5F, -4F, 10, 10, 7, NONE)
                .texOffs(70, 80).addBox(-5.5F, -4F, -5F, 11, 4, 2, NONE)
                .texOffs(0, 87).addBox(-5.5F, 3F, -4.5F, 11, 2, 2, NONE)
                .texOffs(24, 71).addBox(-4F, -3F, 3F, 8, 7, 1, NONE)
                .texOffs(136, 87).addBox(-1.5F, -3F, -5.5F, 3, 2, 1, NONE),
                PartPose.offsetAndRotation(-25F, -12F, -5F, -0.209F, 0F, 0.14F));
        PartDefinition p_finger_r0 = p_hand_r.addOrReplaceChild("finger_r0", CubeListBuilder.create()
                .texOffs(76, 71).addBox(-1F, 0F, -1F, 2, 6, 2, NONE)
                .texOffs(202, 71).addBox(-1F, 5.5F, -2.5F, 2, 5, 2, NONE)
                .texOffs(152, 87).addBox(-0.5F, 9.5F, -3.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(3.75F, 5F, -1.5F, -0.384F, 0F, -0.105F));
        PartDefinition p_finger_r1 = p_hand_r.addOrReplaceChild("finger_r1", CubeListBuilder.create()
                .texOffs(84, 71).addBox(-1F, 0F, -1F, 2, 6, 2, NONE)
                .texOffs(210, 71).addBox(-1F, 5.5F, -2.5F, 2, 5, 2, NONE)
                .texOffs(156, 87).addBox(-0.5F, 9.5F, -3.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(1.25F, 5F, -1.5F, -0.436F, 0F, -0.035F));
        PartDefinition p_finger_r2 = p_hand_r.addOrReplaceChild("finger_r2", CubeListBuilder.create()
                .texOffs(92, 71).addBox(-1F, 0F, -1F, 2, 6, 2, NONE)
                .texOffs(218, 71).addBox(-1F, 5.5F, -2.5F, 2, 5, 2, NONE)
                .texOffs(160, 87).addBox(-0.5F, 9.5F, -3.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-1.25F, 5F, -1.5F, -0.489F, 0F, 0.035F));
        PartDefinition p_finger_r3 = p_hand_r.addOrReplaceChild("finger_r3", CubeListBuilder.create()
                .texOffs(100, 71).addBox(-1F, 0F, -1F, 2, 6, 2, NONE)
                .texOffs(226, 71).addBox(-1F, 5.5F, -2.5F, 2, 5, 2, NONE)
                .texOffs(164, 87).addBox(-0.5F, 9.5F, -3.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-3.75F, 5F, -1.5F, -0.541F, 0F, 0.105F));
        PartDefinition p_thumb_r = p_hand_r.addOrReplaceChild("thumb_r", CubeListBuilder.create()
                .texOffs(234, 71).addBox(-1F, 0F, -1F, 2, 5, 2, NONE)
                .texOffs(226, 80).addBox(-1F, 4.5F, -2F, 2, 3, 2, NONE),
                PartPose.offsetAndRotation(4.5F, 2F, -2F, -0.524F, 0F, -0.611F));
        PartDefinition p_hand_l = p_monolith.addOrReplaceChild("hand_l", CubeListBuilder.create()
                .texOffs(126, 59).addBox(-4F, -9F, -3.5F, 8, 4, 7, NONE)
                .texOffs(162, 71).addBox(-3F, -11F, -2.5F, 6, 2, 5, NONE)
                .texOffs(0, 29).addBox(-5F, -5F, -4F, 10, 10, 7, NONE)
                .texOffs(96, 80).addBox(-5.5F, -4F, -5F, 11, 4, 2, NONE)
                .texOffs(26, 87).addBox(-5.5F, 3F, -4.5F, 11, 2, 2, NONE)
                .texOffs(42, 71).addBox(-4F, -3F, 3F, 8, 7, 1, NONE)
                .texOffs(144, 87).addBox(-1.5F, -3F, -5.5F, 3, 2, 1, NONE),
                PartPose.offsetAndRotation(25F, -12F, -5F, -0.209F, 0F, -0.14F));
        PartDefinition p_finger_l0 = p_hand_l.addOrReplaceChild("finger_l0", CubeListBuilder.create()
                .texOffs(108, 71).addBox(-1F, 0F, -1F, 2, 6, 2, NONE)
                .texOffs(242, 71).addBox(-1F, 5.5F, -2.5F, 2, 5, 2, NONE)
                .texOffs(168, 87).addBox(-0.5F, 9.5F, -3.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-3.75F, 5F, -1.5F, -0.384F, 0F, 0.105F));
        PartDefinition p_finger_l1 = p_hand_l.addOrReplaceChild("finger_l1", CubeListBuilder.create()
                .texOffs(116, 71).addBox(-1F, 0F, -1F, 2, 6, 2, NONE)
                .texOffs(0, 80).addBox(-1F, 5.5F, -2.5F, 2, 5, 2, NONE)
                .texOffs(172, 87).addBox(-0.5F, 9.5F, -3.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-1.25F, 5F, -1.5F, -0.436F, 0F, 0.035F));
        PartDefinition p_finger_l2 = p_hand_l.addOrReplaceChild("finger_l2", CubeListBuilder.create()
                .texOffs(124, 71).addBox(-1F, 0F, -1F, 2, 6, 2, NONE)
                .texOffs(8, 80).addBox(-1F, 5.5F, -2.5F, 2, 5, 2, NONE)
                .texOffs(176, 87).addBox(-0.5F, 9.5F, -3.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(1.25F, 5F, -1.5F, -0.489F, 0F, -0.035F));
        PartDefinition p_finger_l3 = p_hand_l.addOrReplaceChild("finger_l3", CubeListBuilder.create()
                .texOffs(132, 71).addBox(-1F, 0F, -1F, 2, 6, 2, NONE)
                .texOffs(16, 80).addBox(-1F, 5.5F, -2.5F, 2, 5, 2, NONE)
                .texOffs(180, 87).addBox(-0.5F, 9.5F, -3.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(3.75F, 5F, -1.5F, -0.541F, 0F, -0.105F));
        PartDefinition p_thumb_l = p_hand_l.addOrReplaceChild("thumb_l", CubeListBuilder.create()
                .texOffs(24, 80).addBox(-1F, 0F, -1F, 2, 5, 2, NONE)
                .texOffs(234, 80).addBox(-1F, 4.5F, -2F, 2, 3, 2, NONE),
                PartPose.offsetAndRotation(-4.5F, 2F, -2F, -0.524F, 0F, 0.611F));
        PartDefinition p_shard_0 = p_monolith.addOrReplaceChild("shard_0", CubeListBuilder.create()
                .texOffs(156, 59).addBox(-1.5F, -4F, -1.5F, 3, 8, 3, NONE)
                .texOffs(60, 87).addBox(-1F, -6F, -1F, 2, 2, 2, NONE)
                .texOffs(146, 80).addBox(-0.5F, -2.5F, -2F, 1, 5, 1, NONE),
                PartPose.offsetAndRotation(18F, 13F, 0F, 0.436F, 0F, 0.262F));
        PartDefinition p_shard_1 = p_monolith.addOrReplaceChild("shard_1", CubeListBuilder.create()
                .texOffs(168, 59).addBox(-1.5F, -4F, -1.5F, 3, 8, 3, NONE)
                .texOffs(68, 87).addBox(-1F, -6F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(9F, 8F, 15.588F, 0.279F, 2.094F, 0.384F));
        PartDefinition p_shard_2 = p_monolith.addOrReplaceChild("shard_2", CubeListBuilder.create()
                .texOffs(180, 59).addBox(-1.5F, -4F, -1.5F, 3, 8, 3, NONE)
                .texOffs(76, 87).addBox(-1F, -6F, -1F, 2, 2, 2, NONE)
                .texOffs(150, 80).addBox(-0.5F, -2.5F, -2F, 1, 5, 1, NONE),
                PartPose.offsetAndRotation(-9F, 13F, 15.588F, 0.122F, 4.189F, 0.506F));
        PartDefinition p_shard_3 = p_monolith.addOrReplaceChild("shard_3", CubeListBuilder.create()
                .texOffs(192, 59).addBox(-1.5F, -4F, -1.5F, 3, 8, 3, NONE)
                .texOffs(84, 87).addBox(-1F, -6F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(-18F, 8F, 0F, -0.035F, 0F, 0.628F));
        PartDefinition p_shard_4 = p_monolith.addOrReplaceChild("shard_4", CubeListBuilder.create()
                .texOffs(204, 59).addBox(-1.5F, -4F, -1.5F, 3, 8, 3, NONE)
                .texOffs(92, 87).addBox(-1F, -6F, -1F, 2, 2, 2, NONE)
                .texOffs(154, 80).addBox(-0.5F, -2.5F, -2F, 1, 5, 1, NONE),
                PartPose.offsetAndRotation(-9F, 13F, -15.588F, -0.192F, 2.094F, 0.75F));
        PartDefinition p_shard_5 = p_monolith.addOrReplaceChild("shard_5", CubeListBuilder.create()
                .texOffs(216, 59).addBox(-1.5F, -4F, -1.5F, 3, 8, 3, NONE)
                .texOffs(100, 87).addBox(-1F, -6F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(9F, 8F, -15.588F, -0.349F, 4.189F, 0.873F));
        PartDefinition p_debris_0 = p_monolith.addOrReplaceChild("debris_0", CubeListBuilder.create()
                .texOffs(60, 71).addBox(-2F, -2F, -2F, 4, 4, 4, NONE),
                PartPose.offsetAndRotation(-3F, 14F, -1F, 0.349F, 0.175F, 0.524F));
        PartDefinition p_debris_1 = p_monolith.addOrReplaceChild("debris_1", CubeListBuilder.create()
                .texOffs(122, 80).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, NONE),
                PartPose.offsetAndRotation(4F, 15.5F, 2F, 0.611F, 0F, 0.262F));
        PartDefinition p_debris_2 = p_monolith.addOrReplaceChild("debris_2", CubeListBuilder.create()
                .texOffs(134, 80).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, NONE),
                PartPose.offsetAndRotation(-1F, 18F, 3F, 0.175F, 0.698F, 0.785F));
        PartDefinition p_debris_3 = p_monolith.addOrReplaceChild("debris_3", CubeListBuilder.create()
                .texOffs(108, 87).addBox(-1F, -1F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(2F, 17F, -4F, 0F, 0.436F, 0.349F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    public static final Role[] MONOLITH_KEEPER_ROLES = {new Role("monolith", "bob", 0.00F), new Role("monolith/head", "head", 0.00F), new Role("monolith/hand_r", "hand", 0.00F), new Role("monolith/hand_l", "hand", 1.00F), new Role("monolith/shard_0", "orbit", 0.00F), new Role("monolith/shard_1", "orbit", 1.00F), new Role("monolith/shard_2", "orbit", 2.00F), new Role("monolith/shard_3", "orbit", 3.00F), new Role("monolith/shard_4", "orbit", 4.00F), new Role("monolith/shard_5", "orbit", 5.00F), new Role("monolith/debris_0", "tentacle", 0.00F), new Role("monolith/debris_1", "tentacle", 1.70F), new Role("monolith/debris_2", "tentacle", 3.40F), new Role("monolith/debris_3", "tentacle", 5.10F)};

    public static final Key[] MONOLITH_KEEPER_POSES = {
            new Key(0, 0F, "monolith/head", 16F, 0F, 0F),
            new Key(0, 0F, "monolith/head/halo", 18F, 0F, 20F),
            new Key(0, 0F, "monolith/hand_r", 30F, 0F, -12F),
            new Key(0, 0F, "monolith/hand_l", 30F, 0F, 12F),
            new Key(0, 0F, "monolith", 0F, 0F, 6F),
            new Key(0, 0F, "monolith/head/spire", 0F, -9F, 4F),
            new Key(0, 0F, "monolith/head/spire/cap", 0F, 12F, -6F),
            new Key(1, 0F, "monolith/head", 0F, 0F, 0F),
            new Key(1, 8F, "monolith/head", -10F, 0F, 0F),
            new Key(1, 20F, "monolith/head", 10F, 0F, 0F),
            new Key(1, 37F, "monolith/head", 10F, 0F, 0F),
            new Key(1, 40F, "monolith/head", 0F, 0F, 0F),
            new Key(1, 0F, "monolith/head/halo", 0F, 0F, 0F),
            new Key(1, 20F, "monolith/head/halo", 0F, 0F, 180F),
            new Key(1, 40F, "monolith/head/halo", 0F, 0F, 360F),
            new Key(1, 0F, "monolith/hand_r", 0F, 0F, 0F),
            new Key(1, 10F, "monolith/hand_r", -25F, 0F, 40F),
            new Key(1, 36F, "monolith/hand_r", -25F, 0F, 40F),
            new Key(1, 40F, "monolith/hand_r", 0F, 0F, 0F),
            new Key(1, 0F, "monolith/hand_l", 0F, 0F, 0F),
            new Key(1, 10F, "monolith/hand_l", -25F, 0F, -40F),
            new Key(1, 36F, "monolith/hand_l", -25F, 0F, -40F),
            new Key(1, 40F, "monolith/hand_l", 0F, 0F, 0F),
            new Key(1, 0F, "monolith", 0F, 0F, 0F),
            new Key(1, 8F, "monolith", -6F, 0F, 0F),
            new Key(1, 20F, "monolith", 6F, 0F, 0F),
            new Key(1, 37F, "monolith", 6F, 0F, 0F),
            new Key(1, 40F, "monolith", 0F, 0F, 0F),
            new Key(2, 0F, "monolith/hand_r", 0F, 0F, 0F),
            new Key(2, 12F, "monolith/hand_r", -165F, 0F, 25F),
            new Key(2, 56F, "monolith/hand_r", -170F, 0F, 15F),
            new Key(2, 60F, "monolith/hand_r", 35F, 0F, 0F),
            new Key(2, 62F, "monolith/hand_r", 0F, 0F, 0F),
            new Key(2, 0F, "monolith/hand_l", 0F, 0F, 0F),
            new Key(2, 12F, "monolith/hand_l", -165F, 0F, -25F),
            new Key(2, 56F, "monolith/hand_l", -170F, 0F, -15F),
            new Key(2, 60F, "monolith/hand_l", 35F, 0F, 0F),
            new Key(2, 62F, "monolith/hand_l", 0F, 0F, 0F),
            new Key(2, 0F, "monolith", 0F, 0F, 0F),
            new Key(2, 12F, "monolith", -10F, 0F, 0F),
            new Key(2, 56F, "monolith", -12F, 0F, 0F),
            new Key(2, 60F, "monolith", 14F, 0F, 0F),
            new Key(2, 62F, "monolith", 0F, 0F, 0F),
            new Key(2, 0F, "monolith/head/halo", 0F, 0F, 0F),
            new Key(2, 56F, "monolith/head/halo", 0F, 0F, -720F),
            new Key(2, 62F, "monolith/head/halo", 0F, 0F, -720F),
            new Key(2, 0F, "monolith/head/spire", 0F, 0F, 0F),
            new Key(2, 12F, "monolith/head/spire", 0F, 45F, 0F),
            new Key(2, 56F, "monolith/head/spire", 0F, 45F, 0F),
            new Key(2, 60F, "monolith/head/spire", 0F, 0F, 0F),
            new Key(3, 0F, "monolith/head", 0F, 0F, 0F),
            new Key(3, 8F, "monolith/head", -22F, 0F, 0F),
            new Key(3, 34F, "monolith/head", -22F, 0F, 0F),
            new Key(3, 40F, "monolith/head", 0F, 0F, 0F),
            new Key(3, 0F, "monolith/hand_r", 0F, 0F, 0F),
            new Key(3, 8F, "monolith/hand_r", -130F, 0F, 30F),
            new Key(3, 34F, "monolith/hand_r", -130F, 0F, 30F),
            new Key(3, 40F, "monolith/hand_r", 0F, 0F, 0F),
            new Key(3, 0F, "monolith/hand_l", 0F, 0F, 0F),
            new Key(3, 8F, "monolith/hand_l", -130F, 0F, -30F),
            new Key(3, 34F, "monolith/hand_l", -130F, 0F, -30F),
            new Key(3, 40F, "monolith/hand_l", 0F, 0F, 0F),
            new Key(3, 0F, "monolith/head/halo", 0F, 0F, 0F),
            new Key(3, 40F, "monolith/head/halo", 0F, 0F, 720F),
            new Key(3, 0F, "monolith/head/spire/cap", 0F, 0F, 0F),
            new Key(3, 40F, "monolith/head/spire/cap", 0F, 360F, 0F),
            new Key(4, 0F, "monolith", 0F, 0F, 0F),
            new Key(4, 1F, "monolith", 0F, 45F, 14F),
            new Key(4, 8F, "monolith", 0F, 0F, 0F),
            new Key(4, 0F, "monolith/hand_r", 0F, 0F, 0F),
            new Key(4, 1F, "monolith/hand_r", -40F, 0F, 55F),
            new Key(4, 8F, "monolith/hand_r", 0F, 0F, 0F),
            new Key(4, 0F, "monolith/hand_l", 0F, 0F, 0F),
            new Key(4, 1F, "monolith/hand_l", -40F, 0F, -55F),
            new Key(4, 8F, "monolith/hand_l", 0F, 0F, 0F),
            new Key(4, 0F, "monolith/head/halo", 0F, 0F, 0F),
            new Key(4, 1F, "monolith/head/halo", 0F, 0F, 40F),
            new Key(4, 8F, "monolith/head/halo", 0F, 0F, 0F)};

    public static final float MONOLITH_KEEPER_SCALE = 1.1F;

    /** Core Abomination: lebender RBMK-Reaktorkern aus Graphitsaeulen mit schraegen Brenn- und Steuerstaeben, tropfendem Corium, gluehendem Schlund, vier Kabel-Tentakeln und brodelndem Deckel mit kreisenden Graphitbrocken. Textur {@code entity/stage/core_abomination.png}. */
    public static LayerDefinition coreAbomination() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_pool = root.addOrReplaceChild("pool", CubeListBuilder.create()
                .texOffs(80, 0).addBox(-17F, -2F, -16F, 34, 2, 32, NONE)
                .texOffs(140, 93).addBox(-21F, -1F, -9F, 5, 1, 16, NONE)
                .texOffs(76, 116).addBox(16F, -1F, -11F, 5, 1, 14, NONE)
                .texOffs(46, 155).addBox(-10F, -1F, -22F, 20, 1, 7, NONE),
                PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
        PartDefinition p_core = root.addOrReplaceChild("core", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-10F, -19F, -10F, 20, 19, 20, NONE),
                PartPose.offsetAndRotation(0F, 21F, 0F, 0F, 0F, 0F));
        PartDefinition p_wall_0 = p_core.addOrReplaceChild("wall_0", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
        PartDefinition p_wall_1 = p_core.addOrReplaceChild("wall_1", CubeListBuilder.create()
                .texOffs(20, 68).addBox(-6.5F, -19F, -16F, 5, 19, 5, NONE)
                .texOffs(76, 39).addBox(-1.5F, -22F, -17F, 3, 22, 6, NONE)
                .texOffs(234, 68).addBox(1.5F, -18F, -16F, 5, 18, 5, NONE)
                .texOffs(44, 179).addBox(-7F, -7F, -17F, 14, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.785F, 0F));
        PartDefinition p_wall_2 = p_core.addOrReplaceChild("wall_2", CubeListBuilder.create()
                .texOffs(208, 39).addBox(-6.5F, -20F, -16F, 5, 20, 5, NONE)
                .texOffs(100, 68).addBox(-1.5F, -18F, -17F, 3, 18, 6, NONE)
                .texOffs(148, 39).addBox(1.5F, -21F, -16F, 5, 21, 5, NONE)
                .texOffs(146, 116).addBox(-5F, -19F, -17.5F, 2, 13, 1, NONE)
                .texOffs(134, 155).addBox(-3.5F, -5F, -19F, 7, 5, 3, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 1.571F, 0F));
        PartDefinition p_wrod_2 = p_wall_2.addOrReplaceChild("wrod_2", CubeListBuilder.create()
                .texOffs(114, 116).addBox(-1F, -12F, -1F, 2, 12, 2, NONE)
                .texOffs(66, 173).addBox(-1.5F, -14F, -1.5F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(2F, -16F, -15F, 0.96F, 0F, 0.175F));
        PartDefinition p_wall_3 = p_core.addOrReplaceChild("wall_3", CubeListBuilder.create()
                .texOffs(40, 68).addBox(-6.5F, -19F, -16F, 5, 19, 5, NONE)
                .texOffs(94, 39).addBox(-1.5F, -22F, -17F, 3, 22, 6, NONE)
                .texOffs(0, 93).addBox(1.5F, -18F, -16F, 5, 18, 5, NONE)
                .texOffs(76, 179).addBox(-7F, -7F, -17F, 14, 2, 2, NONE)
                .texOffs(174, 155).addBox(-4F, -16F, -19F, 6, 5, 3, NONE)
                .texOffs(186, 173).addBox(-2.5F, -17.5F, -20F, 3, 3, 2, new CubeDeformation(0.3F))
                .texOffs(228, 179).addBox(-1.5F, -14F, -20.5F, 1, 1, 1, new CubeDeformation(0.3F)),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 2.356F, 0F));
        PartDefinition p_wall_4 = p_core.addOrReplaceChild("wall_4", CubeListBuilder.create()
                .texOffs(228, 39).addBox(-6.5F, -20F, -16F, 5, 20, 5, NONE)
                .texOffs(118, 68).addBox(-1.5F, -18F, -17F, 3, 18, 6, NONE)
                .texOffs(168, 39).addBox(1.5F, -21F, -16F, 5, 21, 5, NONE)
                .texOffs(182, 93).addBox(-4F, -20F, -18F, 3, 16, 1, NONE)
                .texOffs(40, 155).addBox(2F, -19F, -17.5F, 2, 9, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 3.142F, 0F));
        PartDefinition p_wrod_4 = p_wall_4.addOrReplaceChild("wrod_4", CubeListBuilder.create()
                .texOffs(122, 116).addBox(-1F, -12F, -1F, 2, 12, 2, NONE)
                .texOffs(78, 173).addBox(-1.5F, -14F, -1.5F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(-2F, -16F, -15F, 0.96F, 0F, -0.175F));
        PartDefinition p_wall_5 = p_core.addOrReplaceChild("wall_5", CubeListBuilder.create()
                .texOffs(60, 68).addBox(-6.5F, -19F, -16F, 5, 19, 5, NONE)
                .texOffs(112, 39).addBox(-1.5F, -22F, -17F, 3, 22, 6, NONE)
                .texOffs(20, 93).addBox(1.5F, -18F, -16F, 5, 18, 5, NONE)
                .texOffs(108, 179).addBox(-7F, -7F, -17F, 14, 2, 2, NONE)
                .texOffs(192, 155).addBox(-4F, -16F, -19F, 6, 5, 3, NONE)
                .texOffs(196, 173).addBox(-2.5F, -17.5F, -20F, 3, 3, 2, new CubeDeformation(0.3F))
                .texOffs(232, 179).addBox(-1.5F, -14F, -20.5F, 1, 1, 1, new CubeDeformation(0.3F)),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 3.927F, 0F));
        PartDefinition p_wall_6 = p_core.addOrReplaceChild("wall_6", CubeListBuilder.create()
                .texOffs(0, 68).addBox(-6.5F, -20F, -16F, 5, 20, 5, NONE)
                .texOffs(136, 68).addBox(-1.5F, -18F, -17F, 3, 18, 6, NONE)
                .texOffs(188, 39).addBox(1.5F, -21F, -16F, 5, 21, 5, NONE)
                .texOffs(152, 116).addBox(3F, -19F, -17.5F, 2, 13, 1, NONE)
                .texOffs(154, 155).addBox(-3.5F, -5F, -19F, 7, 5, 3, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 4.712F, 0F));
        PartDefinition p_wrod_6 = p_wall_6.addOrReplaceChild("wrod_6", CubeListBuilder.create()
                .texOffs(130, 116).addBox(-1F, -12F, -1F, 2, 12, 2, NONE)
                .texOffs(90, 173).addBox(-1.5F, -14F, -1.5F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(2F, -16F, -15F, 0.96F, 0F, -0.175F));
        PartDefinition p_wall_7 = p_core.addOrReplaceChild("wall_7", CubeListBuilder.create()
                .texOffs(80, 68).addBox(-6.5F, -19F, -16F, 5, 19, 5, NONE)
                .texOffs(130, 39).addBox(-1.5F, -22F, -17F, 3, 22, 6, NONE)
                .texOffs(40, 93).addBox(1.5F, -18F, -16F, 5, 18, 5, NONE)
                .texOffs(140, 179).addBox(-7F, -7F, -17F, 14, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 5.498F, 0F));
        PartDefinition p_maw = p_core.addOrReplaceChild("maw", CubeListBuilder.create()
                .texOffs(158, 116).addBox(-9F, -17F, -20F, 18, 5, 8, NONE)
                .texOffs(0, 179).addBox(-9.5F, -13F, -20.5F, 19, 1, 3, NONE)
                .texOffs(198, 93).addBox(-10F, -12F, -19F, 3, 10, 6, NONE)
                .texOffs(216, 93).addBox(7F, -12F, -19F, 3, 10, 6, NONE)
                .texOffs(44, 144).addBox(-7F, -12F, -14F, 14, 10, 1, NONE)
                .texOffs(216, 173).addBox(-5F, -12F, -18.5F, 1, 4, 1, NONE)
                .texOffs(20, 173).addBox(-2.5F, -12F, -18.5F, 1, 5, 1, NONE)
                .texOffs(24, 173).addBox(1.5F, -12F, -18.5F, 1, 5, 1, NONE)
                .texOffs(220, 173).addBox(4F, -12F, -18.5F, 1, 4, 1, NONE)
                .texOffs(102, 173).addBox(-6.5F, -21F, -18F, 4, 3, 2, NONE)
                .texOffs(114, 173).addBox(2.5F, -21F, -18F, 4, 3, 2, NONE)
                .texOffs(206, 173).addBox(-1.5F, -23F, -18.5F, 3, 3, 2, NONE)
                .texOffs(100, 155).addBox(-7F, -22F, -17F, 14, 5, 3, NONE)
                .texOffs(74, 144).addBox(-10F, -3F, -20F, 20, 3, 7, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
        PartDefinition p_jaw = p_maw.addOrReplaceChild("jaw", CubeListBuilder.create()
                .texOffs(0, 144).addBox(-7F, 0F, -7F, 14, 3, 8, NONE)
                .texOffs(28, 173).addBox(-7.5F, 1F, -7.5F, 15, 1, 4, NONE)
                .texOffs(136, 165).addBox(-5F, -1F, -6F, 10, 1, 5, NONE)
                .texOffs(220, 179).addBox(-6F, -2F, -6.5F, 1, 2, 1, NONE)
                .texOffs(212, 179).addBox(-3F, -3F, -6.5F, 1, 3, 1, NONE)
                .texOffs(216, 179).addBox(2F, -3F, -6.5F, 1, 3, 1, NONE)
                .texOffs(224, 179).addBox(5F, -2F, -6.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -3F, -13F, 0F, 0F, 0F));
        PartDefinition p_lid = p_core.addOrReplaceChild("lid", CubeListBuilder.create()
                .texOffs(0, 116).addBox(-13F, -2F, -6F, 26, 3, 12, NONE)
                .texOffs(0, 39).addBox(-6F, -2F, -13F, 12, 3, 26, NONE)
                .texOffs(154, 68).addBox(-10F, -2F, -10F, 20, 3, 20, NONE)
                .texOffs(76, 93).addBox(-8F, -3F, -8F, 16, 1, 16, NONE)
                .texOffs(0, 131).addBox(-6F, -4F, -6F, 12, 1, 12, NONE)
                .texOffs(210, 155).addBox(-3F, -7F, -2F, 4, 4, 4, NONE)
                .texOffs(166, 165).addBox(2F, -6F, -5F, 3, 3, 3, NONE)
                .texOffs(172, 179).addBox(1F, -5F, 2F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, -20F, 0F, 0F, 0F, 0F));
        PartDefinition p_rod_0 = p_lid.addOrReplaceChild("rod_0", CubeListBuilder.create()
                .texOffs(190, 93).addBox(-1F, -15F, -1F, 2, 15, 2, NONE)
                .texOffs(126, 173).addBox(-1.5F, -17F, -1.5F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(-9F, -2F, -5F, 0.262F, 0F, -0.524F));
        PartDefinition p_rod_1 = p_lid.addOrReplaceChild("rod_1", CubeListBuilder.create()
                .texOffs(138, 116).addBox(-1F, -12F, -1F, 2, 12, 2, NONE)
                .texOffs(138, 173).addBox(-1.5F, -14F, -1.5F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(8F, -2F, -6F, 0.436F, 0F, 0.436F));
        PartDefinition p_rod_2 = p_lid.addOrReplaceChild("rod_2", CubeListBuilder.create()
                .texOffs(68, 93).addBox(-1F, -16F, -1F, 2, 16, 2, NONE)
                .texOffs(150, 173).addBox(-1.5F, -13F, -1.5F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(-7F, -2F, 8F, -0.524F, 0F, -0.262F));
        PartDefinition p_rod_3 = p_lid.addOrReplaceChild("rod_3", CubeListBuilder.create()
                .texOffs(60, 93).addBox(-1F, -17F, -1F, 2, 17, 2, NONE)
                .texOffs(162, 173).addBox(-1.5F, -19F, -1.5F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(8F, -2F, 7F, -0.349F, 0F, 0.524F));
        PartDefinition p_rod_4 = p_lid.addOrReplaceChild("rod_4", CubeListBuilder.create()
                .texOffs(152, 131).addBox(-1F, -11F, -1F, 2, 11, 2, NONE)
                .texOffs(174, 173).addBox(-1.5F, -8F, -1.5F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(0F, -2F, 10F, -0.611F, 0F, 0F));
        PartDefinition p_chunk_0 = p_lid.addOrReplaceChild("chunk_0", CubeListBuilder.create()
                .texOffs(226, 155).addBox(-2F, -2F, -2F, 4, 4, 4, NONE),
                PartPose.offsetAndRotation(18F, -12F, 0F, 0.524F, 0F, 0.349F));
        PartDefinition p_chunk_2 = p_lid.addOrReplaceChild("chunk_2", CubeListBuilder.create()
                .texOffs(178, 165).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, NONE),
                PartPose.offsetAndRotation(-9F, -12F, 15.588F, 0.873F, 0.873F, 0.349F));
        PartDefinition p_chunk_4 = p_lid.addOrReplaceChild("chunk_4", CubeListBuilder.create()
                .texOffs(0, 165).addBox(-2F, -2F, -2F, 4, 4, 4, NONE),
                PartPose.offsetAndRotation(-9F, -12F, -15.588F, 1.222F, 1.745F, 0.349F));
        PartDefinition p_tent_fr = p_core.addOrReplaceChild("tent_fr", CubeListBuilder.create()
                .texOffs(128, 144).addBox(-3.5F, -3.5F, -1F, 7, 7, 3, NONE)
                .texOffs(48, 131).addBox(-2.5F, -2.5F, -9F, 5, 5, 8, NONE),
                PartPose.offsetAndRotation(-10.607F, -9F, -10.607F, -0.262F, 0.785F, 0F));
        PartDefinition p_tent2_fr = p_tent_fr.addOrReplaceChild("tent2_fr", CubeListBuilder.create()
                .texOffs(160, 131).addBox(-2F, -2F, -8F, 4, 4, 8, NONE)
                .texOffs(80, 165).addBox(-2.5F, -2.5F, -1F, 5, 5, 2, NONE),
                PartPose.offsetAndRotation(0F, 0F, -9F, 0.524F, 0F, 0F));
        PartDefinition p_tent3_fr = p_tent2_fr.addOrReplaceChild("tent3_fr", CubeListBuilder.create()
                .texOffs(148, 144).addBox(-1.5F, -1.5F, -7F, 3, 3, 7, NONE),
                PartPose.offsetAndRotation(0F, 0F, -8F, 0.611F, 0F, 0F));
        PartDefinition p_claw_fr = p_tent3_fr.addOrReplaceChild("claw_fr", CubeListBuilder.create()
                .texOffs(16, 165).addBox(-2.5F, -2.5F, -3F, 5, 5, 3, NONE)
                .texOffs(180, 179).addBox(-1.5F, -1.5F, -4F, 3, 3, 1, NONE)
                .texOffs(190, 165).addBox(-3F, -1F, -7F, 1, 2, 4, NONE)
                .texOffs(200, 165).addBox(2F, -1F, -7F, 1, 2, 4, NONE),
                PartPose.offsetAndRotation(0F, 0F, -7F, 0.785F, 0F, 0F));
        PartDefinition p_tent_fl = p_core.addOrReplaceChild("tent_fl", CubeListBuilder.create()
                .texOffs(168, 144).addBox(-3.5F, -3.5F, -1F, 7, 7, 3, NONE)
                .texOffs(74, 131).addBox(-2.5F, -2.5F, -9F, 5, 5, 8, NONE),
                PartPose.offsetAndRotation(10.607F, -9F, -10.607F, -0.262F, 5.498F, 0F));
        PartDefinition p_tent2_fl = p_tent_fl.addOrReplaceChild("tent2_fl", CubeListBuilder.create()
                .texOffs(184, 131).addBox(-2F, -2F, -8F, 4, 4, 8, NONE)
                .texOffs(94, 165).addBox(-2.5F, -2.5F, -1F, 5, 5, 2, NONE),
                PartPose.offsetAndRotation(0F, 0F, -9F, 0.524F, 0F, 0F));
        PartDefinition p_tent3_fl = p_tent2_fl.addOrReplaceChild("tent3_fl", CubeListBuilder.create()
                .texOffs(188, 144).addBox(-1.5F, -1.5F, -7F, 3, 3, 7, NONE),
                PartPose.offsetAndRotation(0F, 0F, -8F, 0.611F, 0F, 0F));
        PartDefinition p_claw_fl = p_tent3_fl.addOrReplaceChild("claw_fl", CubeListBuilder.create()
                .texOffs(32, 165).addBox(-2.5F, -2.5F, -3F, 5, 5, 3, NONE)
                .texOffs(188, 179).addBox(-1.5F, -1.5F, -4F, 3, 3, 1, NONE)
                .texOffs(210, 165).addBox(-3F, -1F, -7F, 1, 2, 4, NONE)
                .texOffs(220, 165).addBox(2F, -1F, -7F, 1, 2, 4, NONE),
                PartPose.offsetAndRotation(0F, 0F, -7F, 0.785F, 0F, 0F));
        PartDefinition p_tent_br = p_core.addOrReplaceChild("tent_br", CubeListBuilder.create()
                .texOffs(208, 144).addBox(-3.5F, -3.5F, -1F, 7, 7, 3, NONE)
                .texOffs(100, 131).addBox(-2.5F, -2.5F, -9F, 5, 5, 8, NONE),
                PartPose.offsetAndRotation(-10.607F, -9F, 10.607F, -0.262F, 2.356F, 0F));
        PartDefinition p_tent2_br = p_tent_br.addOrReplaceChild("tent2_br", CubeListBuilder.create()
                .texOffs(208, 131).addBox(-2F, -2F, -8F, 4, 4, 8, NONE)
                .texOffs(108, 165).addBox(-2.5F, -2.5F, -1F, 5, 5, 2, NONE),
                PartPose.offsetAndRotation(0F, 0F, -9F, 0.524F, 0F, 0F));
        PartDefinition p_tent3_br = p_tent2_br.addOrReplaceChild("tent3_br", CubeListBuilder.create()
                .texOffs(228, 144).addBox(-1.5F, -1.5F, -7F, 3, 3, 7, NONE),
                PartPose.offsetAndRotation(0F, 0F, -8F, 0.611F, 0F, 0F));
        PartDefinition p_claw_br = p_tent3_br.addOrReplaceChild("claw_br", CubeListBuilder.create()
                .texOffs(48, 165).addBox(-2.5F, -2.5F, -3F, 5, 5, 3, NONE)
                .texOffs(196, 179).addBox(-1.5F, -1.5F, -4F, 3, 3, 1, NONE)
                .texOffs(230, 165).addBox(-3F, -1F, -7F, 1, 2, 4, NONE)
                .texOffs(240, 165).addBox(2F, -1F, -7F, 1, 2, 4, NONE),
                PartPose.offsetAndRotation(0F, 0F, -7F, 0.785F, 0F, 0F));
        PartDefinition p_tent_bl = p_core.addOrReplaceChild("tent_bl", CubeListBuilder.create()
                .texOffs(0, 155).addBox(-3.5F, -3.5F, -1F, 7, 7, 3, NONE)
                .texOffs(126, 131).addBox(-2.5F, -2.5F, -9F, 5, 5, 8, NONE),
                PartPose.offsetAndRotation(10.607F, -9F, 10.607F, -0.262F, 3.927F, 0F));
        PartDefinition p_tent2_bl = p_tent_bl.addOrReplaceChild("tent2_bl", CubeListBuilder.create()
                .texOffs(232, 131).addBox(-2F, -2F, -8F, 4, 4, 8, NONE)
                .texOffs(122, 165).addBox(-2.5F, -2.5F, -1F, 5, 5, 2, NONE),
                PartPose.offsetAndRotation(0F, 0F, -9F, 0.524F, 0F, 0F));
        PartDefinition p_tent3_bl = p_tent2_bl.addOrReplaceChild("tent3_bl", CubeListBuilder.create()
                .texOffs(20, 155).addBox(-1.5F, -1.5F, -7F, 3, 3, 7, NONE),
                PartPose.offsetAndRotation(0F, 0F, -8F, 0.611F, 0F, 0F));
        PartDefinition p_claw_bl = p_tent3_bl.addOrReplaceChild("claw_bl", CubeListBuilder.create()
                .texOffs(64, 165).addBox(-2.5F, -2.5F, -3F, 5, 5, 3, NONE)
                .texOffs(204, 179).addBox(-1.5F, -1.5F, -4F, 3, 3, 1, NONE)
                .texOffs(0, 173).addBox(-3F, -1F, -7F, 1, 2, 4, NONE)
                .texOffs(10, 173).addBox(2F, -1F, -7F, 1, 2, 4, NONE),
                PartPose.offsetAndRotation(0F, 0F, -7F, 0.785F, 0F, 0F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    public static final Role[] CORE_ABOMINATION_ROLES = {new Role("core", "body", 0.00F), new Role("core/maw/jaw", "jaw", 0.00F), new Role("core/lid", "bob", 0.00F), new Role("core/lid/chunk_0", "orbit", 0.00F), new Role("core/lid/chunk_2", "orbit", 2.00F), new Role("core/lid/chunk_4", "orbit", 4.00F), new Role("core/tent_fr/tent2_fr", "tentacle", 0.80F), new Role("core/tent_fr/tent2_fr/tent3_fr", "tentacle", 1.80F), new Role("core/tent_fl/tent2_fl", "tentacle", 5.60F), new Role("core/tent_fl/tent2_fl/tent3_fl", "tentacle", 6.60F), new Role("core/tent_br/tent2_br", "tentacle", 2.40F), new Role("core/tent_br/tent2_br/tent3_br", "tentacle", 3.40F), new Role("core/tent_bl/tent2_bl", "tentacle", 4.00F), new Role("core/tent_bl/tent2_bl/tent3_bl", "tentacle", 5.00F)};

    public static final Key[] CORE_ABOMINATION_POSES = {
            new Key(0, 0F, "core", 6F, 0F, 0F),
            new Key(0, 0F, "core/lid", 4F, 0F, 5F),
            new Key(0, 0F, "core/maw/jaw", -6F, 0F, 0F),
            new Key(0, 0F, "core/tent_fr", 10F, 0F, 0F),
            new Key(0, 0F, "core/tent_fl", 10F, 0F, 0F),
            new Key(0, 0F, "core/tent_br", 4F, 0F, 0F),
            new Key(0, 0F, "core/tent_bl", 4F, 0F, 0F),
            new Key(0, 0F, "core/tent_fr/tent2_fr/tent3_fr", 30F, 0F, 0F),
            new Key(0, 0F, "core/tent_fl/tent2_fl/tent3_fl", 30F, 0F, 0F),
            new Key(0, 0F, "core/tent_br/tent2_br/tent3_br", 30F, 0F, 0F),
            new Key(0, 0F, "core/tent_bl/tent2_bl/tent3_bl", 30F, 0F, 0F),
            new Key(1, 0F, "core", 0F, 0F, 0F),
            new Key(1, 14F, "core", -8F, 0F, 0F),
            new Key(1, 19F, "core", -8F, 0F, 0F),
            new Key(1, 21F, "core", 6F, 0F, 0F),
            new Key(1, 28F, "core", 0F, 0F, 0F),
            new Key(1, 0F, "core/tent_fr", 0F, 0F, 0F),
            new Key(1, 14F, "core/tent_fr", -45F, 0F, 0F),
            new Key(1, 19F, "core/tent_fr", -45F, 0F, 0F),
            new Key(1, 21F, "core/tent_fr", 15F, 0F, 0F),
            new Key(1, 28F, "core/tent_fr", 0F, 0F, 0F),
            new Key(1, 0F, "core/tent_fl", 0F, 0F, 0F),
            new Key(1, 14F, "core/tent_fl", -45F, 0F, 0F),
            new Key(1, 19F, "core/tent_fl", -45F, 0F, 0F),
            new Key(1, 21F, "core/tent_fl", 15F, 0F, 0F),
            new Key(1, 28F, "core/tent_fl", 0F, 0F, 0F),
            new Key(1, 0F, "core/tent_br", 0F, 0F, 0F),
            new Key(1, 14F, "core/tent_br", -45F, 0F, 0F),
            new Key(1, 19F, "core/tent_br", -45F, 0F, 0F),
            new Key(1, 21F, "core/tent_br", 15F, 0F, 0F),
            new Key(1, 28F, "core/tent_br", 0F, 0F, 0F),
            new Key(1, 0F, "core/tent_bl", 0F, 0F, 0F),
            new Key(1, 14F, "core/tent_bl", -45F, 0F, 0F),
            new Key(1, 19F, "core/tent_bl", -45F, 0F, 0F),
            new Key(1, 21F, "core/tent_bl", 15F, 0F, 0F),
            new Key(1, 28F, "core/tent_bl", 0F, 0F, 0F),
            new Key(1, 0F, "core/lid", 0F, 0F, 0F),
            new Key(1, 14F, "core/lid", -6F, 0F, 0F),
            new Key(1, 19F, "core/lid", -6F, 0F, 0F),
            new Key(1, 21F, "core/lid", 4F, 0F, 0F),
            new Key(1, 28F, "core/lid", 0F, 0F, 0F),
            new Key(1, 0F, "core/maw/jaw", 0F, 0F, 0F),
            new Key(1, 14F, "core/maw/jaw", 25F, 0F, 0F),
            new Key(1, 21F, "core/maw/jaw", 35F, 0F, 0F),
            new Key(1, 28F, "core/maw/jaw", 0F, 0F, 0F),
            new Key(2, 0F, "core", 0F, 0F, 0F),
            new Key(2, 4F, "core", -12F, 0F, 0F),
            new Key(2, 6F, "core", 10F, 0F, 0F),
            new Key(2, 12F, "core", 0F, 0F, 0F),
            new Key(2, 0F, "core/maw/jaw", 0F, 0F, 0F),
            new Key(2, 4F, "core/maw/jaw", 15F, 0F, 0F),
            new Key(2, 6F, "core/maw/jaw", 45F, 0F, 0F),
            new Key(2, 12F, "core/maw/jaw", 0F, 0F, 0F),
            new Key(2, 0F, "core/tent_fr", 0F, 0F, 0F),
            new Key(2, 4F, "core/tent_fr", -20F, -15F, 0F),
            new Key(2, 6F, "core/tent_fr", 5F, 0F, 0F),
            new Key(2, 12F, "core/tent_fr", 0F, 0F, 0F),
            new Key(2, 0F, "core/tent_fl", 0F, 0F, 0F),
            new Key(2, 4F, "core/tent_fl", -20F, 15F, 0F),
            new Key(2, 6F, "core/tent_fl", 5F, 0F, 0F),
            new Key(2, 12F, "core/tent_fl", 0F, 0F, 0F),
            new Key(3, 0F, "core", 0F, 0F, 0F),
            new Key(3, 3F, "core", 6F, 0F, 0F),
            new Key(3, 9F, "core", 0F, 0F, 0F),
            new Key(3, 0F, "core/tent_fr", 0F, 0F, 0F),
            new Key(3, 3F, "core/tent_fr", -35F, 0F, 0F),
            new Key(3, 6F, "core/tent_fr", 20F, 0F, 0F),
            new Key(3, 10F, "core/tent_fr", 0F, 0F, 0F),
            new Key(3, 0F, "core/tent_fl", 0F, 0F, 0F),
            new Key(3, 3F, "core/tent_fl", -35F, 0F, 0F),
            new Key(3, 6F, "core/tent_fl", 20F, 0F, 0F),
            new Key(3, 10F, "core/tent_fl", 0F, 0F, 0F),
            new Key(3, 0F, "core/maw/jaw", 0F, 0F, 0F),
            new Key(3, 3F, "core/maw/jaw", 30F, 0F, 0F),
            new Key(3, 10F, "core/maw/jaw", 0F, 0F, 0F),
            new Key(4, 0F, "core", 0F, 0F, 0F),
            new Key(4, 8F, "core", -6F, 0F, 4F),
            new Key(4, 14F, "core", -6F, 0F, -4F),
            new Key(4, 20F, "core", -8F, 0F, 5F),
            new Key(4, 26F, "core", -8F, 0F, -5F),
            new Key(4, 32F, "core", -10F, 0F, 6F),
            new Key(4, 38F, "core", -10F, 0F, -6F),
            new Key(4, 40F, "core", 10F, 0F, 0F),
            new Key(4, 44F, "core", 4F, 0F, 0F),
            new Key(4, 50F, "core", 0F, 0F, 0F),
            new Key(4, 0F, "core/lid", 0F, 0F, 0F),
            new Key(4, 8F, "core/lid", -8F, 0F, 6F),
            new Key(4, 20F, "core/lid", 8F, 0F, -6F),
            new Key(4, 32F, "core/lid", -10F, 0F, 8F),
            new Key(4, 38F, "core/lid", 10F, 0F, -8F),
            new Key(4, 40F, "core/lid", 0F, 0F, 0F),
            new Key(4, 50F, "core/lid", 0F, 0F, 0F),
            new Key(4, 0F, "core/maw/jaw", 0F, 0F, 0F),
            new Key(4, 8F, "core/maw/jaw", 30F, 0F, 0F),
            new Key(4, 38F, "core/maw/jaw", 40F, 0F, 0F),
            new Key(4, 41F, "core/maw/jaw", 50F, 0F, 0F),
            new Key(4, 50F, "core/maw/jaw", 0F, 0F, 0F),
            new Key(4, 0F, "core/tent_fr", 0F, 0F, 0F),
            new Key(4, 10F, "core/tent_fr", -60F, 0F, 0F),
            new Key(4, 38F, "core/tent_fr", -65F, 0F, 0F),
            new Key(4, 41F, "core/tent_fr", 20F, 0F, 0F),
            new Key(4, 50F, "core/tent_fr", 0F, 0F, 0F),
            new Key(4, 0F, "core/tent_fl", 0F, 0F, 0F),
            new Key(4, 10F, "core/tent_fl", -60F, 0F, 0F),
            new Key(4, 38F, "core/tent_fl", -65F, 0F, 0F),
            new Key(4, 41F, "core/tent_fl", 20F, 0F, 0F),
            new Key(4, 50F, "core/tent_fl", 0F, 0F, 0F),
            new Key(4, 0F, "core/tent_br", 0F, 0F, 0F),
            new Key(4, 10F, "core/tent_br", -60F, 0F, 0F),
            new Key(4, 38F, "core/tent_br", -65F, 0F, 0F),
            new Key(4, 41F, "core/tent_br", 20F, 0F, 0F),
            new Key(4, 50F, "core/tent_br", 0F, 0F, 0F),
            new Key(4, 0F, "core/tent_bl", 0F, 0F, 0F),
            new Key(4, 10F, "core/tent_bl", -60F, 0F, 0F),
            new Key(4, 38F, "core/tent_bl", -65F, 0F, 0F),
            new Key(4, 41F, "core/tent_bl", 20F, 0F, 0F),
            new Key(4, 50F, "core/tent_bl", 0F, 0F, 0F),
            new Key(4, 0F, "core/tent_fr/tent2_fr", 0F, 0F, 0F),
            new Key(4, 10F, "core/tent_fr/tent2_fr", -30F, 0F, 0F),
            new Key(4, 41F, "core/tent_fr/tent2_fr", 10F, 0F, 0F),
            new Key(4, 50F, "core/tent_fr/tent2_fr", 0F, 0F, 0F),
            new Key(4, 0F, "core/tent_fl/tent2_fl", 0F, 0F, 0F),
            new Key(4, 10F, "core/tent_fl/tent2_fl", -30F, 0F, 0F),
            new Key(4, 41F, "core/tent_fl/tent2_fl", 10F, 0F, 0F),
            new Key(4, 50F, "core/tent_fl/tent2_fl", 0F, 0F, 0F)};

    public static final float CORE_ABOMINATION_SCALE = 1.45F;

    /** Magma Wyrm: fliegende Lavaschlange mit gehoerntem Kopf, Glutaugen und gluehendem Rachen, kleinen Fledermausfluegeln am Hals und neun sich verjuengenden Krustensegmenten mit Rueckenstacheln bis zur Klingen-Schwanzspitze. Textur {@code entity/stage/magma_wyrm.png}. */
    public static LayerDefinition magmaWyrm() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-7F, -6.5F, -6F, 14, 13, 12, NONE)
                .texOffs(24, 57).addBox(-6F, -7.5F, -5F, 12, 1, 10, NONE)
                .texOffs(68, 57).addBox(-5.5F, 6.5F, -5F, 11, 1, 10, NONE)
                .texOffs(120, 25).addBox(-7.5F, -4F, -4F, 1, 8, 8, NONE)
                .texOffs(138, 25).addBox(6.5F, -4F, -4F, 1, 8, 8, NONE)
                .texOffs(16, 109).addBox(-3F, 3F, -6.5F, 6, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 8F, 4F, 0F, 0F, 0F));
        PartDefinition p_head = p_body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(142, 0).addBox(-6F, -6F, -10F, 12, 8, 11, NONE)
                .texOffs(144, 79).addBox(-6.5F, -7F, -11F, 13, 2, 6, NONE)
                .texOffs(0, 43).addBox(-4.5F, -4.5F, -19F, 9, 5, 9, NONE)
                .texOffs(36, 79).addBox(-2F, -5.5F, -18F, 4, 1, 8, NONE)
                .texOffs(0, 69).addBox(-6.5F, -2F, -8F, 13, 3, 7, NONE)
                .texOffs(148, 109).addBox(-3.5F, -3.5F, -19.5F, 1, 1, 1, NONE)
                .texOffs(152, 109).addBox(2.5F, -3.5F, -19.5F, 1, 1, 1, NONE)
                .texOffs(216, 25).addBox(-3.5F, 0.5F, -18F, 7, 1, 13, NONE)
                .texOffs(72, 109).addBox(-4.5F, 0.5F, -18.5F, 1, 2, 1, NONE)
                .texOffs(76, 109).addBox(3.5F, 0.5F, -18.5F, 1, 2, 1, NONE)
                .texOffs(80, 109).addBox(-4.5F, 0.5F, -14F, 1, 2, 1, NONE)
                .texOffs(84, 109).addBox(3.5F, 0.5F, -14F, 1, 2, 1, NONE)
                .texOffs(88, 103).addBox(-6.6F, -4F, -9F, 1, 2, 3, NONE)
                .texOffs(96, 103).addBox(5.6F, -4F, -9F, 1, 2, 3, NONE)
                .texOffs(78, 79).addBox(-1F, -9F, -8F, 2, 3, 6, NONE),
                PartPose.offsetAndRotation(0F, -2F, -5F, 0F, 0F, 0F));
        PartDefinition p_jaw = p_head.addOrReplaceChild("jaw", CubeListBuilder.create()
                .texOffs(0, 25).addBox(-4.5F, 0F, -15.5F, 9, 3, 15, NONE)
                .texOffs(64, 43).addBox(-4F, 3F, -14F, 8, 1, 12, NONE)
                .texOffs(132, 43).addBox(-3.5F, -0.5F, -14F, 7, 1, 11, NONE)
                .texOffs(88, 109).addBox(-4F, -2F, -15F, 1, 2, 1, NONE)
                .texOffs(92, 109).addBox(3F, -2F, -15F, 1, 2, 1, NONE)
                .texOffs(96, 109).addBox(-4F, -1.5F, -11F, 1, 2, 1, new CubeDeformation(0.1F))
                .texOffs(100, 109).addBox(3F, -1.5F, -11F, 1, 2, 1, new CubeDeformation(0.1F))
                .texOffs(24, 96).addBox(-1.5F, 3F, -6F, 3, 3, 4, NONE),
                PartPose.offsetAndRotation(0F, 1.5F, -2F, 0F, 0F, 0F));
        PartDefinition p_horn_r = p_head.addOrReplaceChild("horn_r", CubeListBuilder.create()
                .texOffs(94, 69).addBox(-1.5F, -7F, -1.5F, 3, 7, 3, NONE)
                .texOffs(0, 103).addBox(-2F, -2F, -2F, 4, 2, 4, NONE),
                PartPose.offsetAndRotation(-4F, -6F, -5F, -0.785F, 0F, -0.524F));
        PartDefinition p_horn2_r = p_horn_r.addOrReplaceChild("horn2_r", CubeListBuilder.create()
                .texOffs(128, 79).addBox(-1F, -7F, -1F, 2, 7, 2, NONE)
                .texOffs(104, 109).addBox(-0.5F, -9F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -7F, 0F, -0.611F, 0F, -0.209F));
        PartDefinition p_horn_l = p_head.addOrReplaceChild("horn_l", CubeListBuilder.create()
                .texOffs(106, 69).addBox(-1.5F, -7F, -1.5F, 3, 7, 3, NONE)
                .texOffs(16, 103).addBox(-2F, -2F, -2F, 4, 2, 4, NONE),
                PartPose.offsetAndRotation(4F, -6F, -5F, -0.785F, 0F, 0.524F));
        PartDefinition p_horn2_l = p_horn_l.addOrReplaceChild("horn2_l", CubeListBuilder.create()
                .texOffs(136, 79).addBox(-1F, -7F, -1F, 2, 7, 2, NONE)
                .texOffs(108, 109).addBox(-0.5F, -9F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -7F, 0F, -0.611F, 0F, 0.209F));
        PartDefinition p_cheek_r = p_head.addOrReplaceChild("cheek_r", CubeListBuilder.create()
                .texOffs(62, 69).addBox(-0.5F, -2F, 0F, 1, 3, 7, NONE)
                .texOffs(38, 96).addBox(-0.5F, -4F, 2F, 1, 2, 5, NONE),
                PartPose.offsetAndRotation(-5.5F, -1F, -4F, 0F, 0.611F, 0F));
        PartDefinition p_cheek_l = p_head.addOrReplaceChild("cheek_l", CubeListBuilder.create()
                .texOffs(78, 69).addBox(-0.5F, -2F, 0F, 1, 3, 7, NONE)
                .texOffs(50, 96).addBox(-0.5F, -4F, 2F, 1, 2, 5, NONE),
                PartPose.offsetAndRotation(5.5F, -1F, -4F, 0F, -0.611F, 0F));
        PartDefinition p_wing_r = p_body.addOrReplaceChild("wing_r", CubeListBuilder.create()
                .texOffs(78, 96).addBox(-13F, -1.5F, -1.5F, 13, 3, 3, NONE)
                .texOffs(142, 57).addBox(-13F, -0.5F, 1F, 13, 1, 9, NONE)
                .texOffs(164, 103).addBox(-8F, -0.5F, 10F, 8, 1, 3, NONE)
                .texOffs(194, 43).addBox(-13F, -1F, 1F, 1, 1, 11, NONE),
                PartPose.offsetAndRotation(-6.5F, -3F, 0F, 0F, -0.262F, 0.262F));
        PartDefinition p_wtip_r = p_wing_r.addOrReplaceChild("wtip_r", CubeListBuilder.create()
                .texOffs(112, 103).addBox(-11F, -1F, -1F, 11, 2, 2, NONE)
                .texOffs(92, 88).addBox(-11F, -0.5F, 1F, 11, 1, 6, NONE)
                .texOffs(226, 103).addBox(-5F, -0.5F, 7F, 5, 1, 3, NONE)
                .texOffs(32, 103).addBox(-13F, -1.5F, -1.5F, 2, 3, 3, NONE),
                PartPose.offsetAndRotation(-13F, 0F, 0F, 0F, -0.436F, 0.349F));
        PartDefinition p_wing_l = p_body.addOrReplaceChild("wing_l", CubeListBuilder.create()
                .texOffs(110, 96).addBox(0F, -1.5F, -1.5F, 13, 3, 3, NONE)
                .texOffs(186, 57).addBox(0F, -0.5F, 1F, 13, 1, 9, NONE)
                .texOffs(186, 103).addBox(0F, -0.5F, 10F, 8, 1, 3, NONE)
                .texOffs(218, 43).addBox(12F, -1F, 1F, 1, 1, 11, NONE),
                PartPose.offsetAndRotation(6.5F, -3F, 0F, 0F, 0.262F, -0.262F));
        PartDefinition p_wtip_l = p_wing_l.addOrReplaceChild("wtip_l", CubeListBuilder.create()
                .texOffs(138, 103).addBox(0F, -1F, -1F, 11, 2, 2, NONE)
                .texOffs(126, 88).addBox(0F, -0.5F, 1F, 11, 1, 6, NONE)
                .texOffs(0, 109).addBox(0F, -0.5F, 7F, 5, 1, 3, NONE)
                .texOffs(42, 103).addBox(11F, -1.5F, -1.5F, 2, 3, 3, NONE),
                PartPose.offsetAndRotation(13F, 0F, 0F, 0F, 0.436F, -0.349F));
        PartDefinition p_collar = p_body.addOrReplaceChild("collar", CubeListBuilder.create()
                .texOffs(242, 43).addBox(-1.5F, -9F, -1.5F, 3, 9, 3, NONE)
                .texOffs(30, 109).addBox(-1F, -11F, -1F, 2, 2, 2, NONE)
                .texOffs(60, 88).addBox(-5.5F, -6F, 0F, 2, 6, 2, NONE)
                .texOffs(68, 88).addBox(3.5F, -6F, 0F, 2, 6, 2, NONE),
                PartPose.offsetAndRotation(0F, -6F, -2F, -0.611F, 0F, 0F));
        PartDefinition p_seg_1 = p_body.addOrReplaceChild("seg_1", CubeListBuilder.create()
                .texOffs(52, 0).addBox(-6.5F, -6F, 0F, 13, 12, 10, NONE)
                .texOffs(126, 69).addBox(-5.5F, -7F, 1F, 11, 1, 8, NONE)
                .texOffs(164, 69).addBox(-5.5F, 6F, 1F, 11, 1, 8, NONE)
                .texOffs(188, 25).addBox(-7.5F, -5F, 2F, 1, 9, 6, NONE)
                .texOffs(202, 25).addBox(6.5F, -5F, 2F, 1, 9, 6, NONE),
                PartPose.offsetAndRotation(0F, 0F, 6F, -0.209F, 0.244F, 0F));
        PartDefinition p_spine_1 = p_seg_1.addOrReplaceChild("spine_1", CubeListBuilder.create()
                .texOffs(134, 57).addBox(-1F, -9F, -1F, 2, 9, 2, NONE)
                .texOffs(112, 109).addBox(-0.5F, -11F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -6.5F, 4F, -0.698F, 0F, 0F));
        PartDefinition p_seg_2 = p_seg_1.addOrReplaceChild("seg_2", CubeListBuilder.create()
                .texOffs(98, 0).addBox(-6F, -5.5F, 0F, 12, 11, 10, NONE)
                .texOffs(202, 69).addBox(-5F, -6.5F, 1F, 10, 1, 8, NONE)
                .texOffs(0, 79).addBox(-5F, 5.5F, 1F, 10, 1, 8, NONE)
                .texOffs(36, 43).addBox(-7F, -4.5F, 2F, 1, 8, 6, NONE)
                .texOffs(50, 43).addBox(6F, -4.5F, 2F, 1, 8, 6, NONE),
                PartPose.offsetAndRotation(0F, 0F, 10F, -0.14F, 0.175F, 0F));
        PartDefinition p_spine_2 = p_seg_2.addOrReplaceChild("spine_2", CubeListBuilder.create()
                .texOffs(76, 88).addBox(-1F, -6F, -1F, 2, 6, 2, NONE)
                .texOffs(116, 109).addBox(-0.5F, -8F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -6F, 4F, -0.698F, 0F, 0F));
        PartDefinition p_seg_3 = p_seg_2.addOrReplaceChild("seg_3", CubeListBuilder.create()
                .texOffs(188, 0).addBox(-5.5F, -5F, 0F, 11, 10, 9, NONE)
                .texOffs(182, 79).addBox(-4.5F, -6F, 1F, 9, 1, 7, NONE)
                .texOffs(214, 79).addBox(-4.5F, 5F, 1F, 9, 1, 7, NONE)
                .texOffs(0, 57).addBox(-6.5F, -4F, 2F, 1, 7, 5, NONE)
                .texOffs(12, 57).addBox(5.5F, -4F, 2F, 1, 7, 5, NONE),
                PartPose.offsetAndRotation(0F, 0F, 10F, 0.035F, -0.209F, 0F));
        PartDefinition p_spine_3 = p_seg_3.addOrReplaceChild("spine_3", CubeListBuilder.create()
                .texOffs(118, 69).addBox(-1F, -8F, -1F, 2, 8, 2, NONE)
                .texOffs(120, 109).addBox(-0.5F, -10F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -5.5F, 3.6F, -0.698F, 0F, 0F));
        PartDefinition p_seg_4 = p_seg_3.addOrReplaceChild("seg_4", CubeListBuilder.create()
                .texOffs(48, 25).addBox(-5F, -4.5F, 0F, 10, 9, 9, NONE)
                .texOffs(0, 88).addBox(-4F, -5.5F, 1F, 8, 1, 7, NONE)
                .texOffs(30, 88).addBox(-4F, 4.5F, 1F, 8, 1, 7, NONE)
                .texOffs(110, 57).addBox(-6F, -3.5F, 2F, 1, 6, 5, NONE)
                .texOffs(122, 57).addBox(5F, -3.5F, 2F, 1, 6, 5, NONE),
                PartPose.offsetAndRotation(0F, 0F, 9F, 0.175F, -0.349F, 0F));
        PartDefinition p_spine_4 = p_seg_4.addOrReplaceChild("spine_4", CubeListBuilder.create()
                .texOffs(62, 96).addBox(-1F, -5F, -1F, 2, 5, 2, NONE)
                .texOffs(124, 109).addBox(-0.5F, -7F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -5F, 3.6F, -0.698F, 0F, 0F));
        PartDefinition p_seg_5 = p_seg_4.addOrReplaceChild("seg_5", CubeListBuilder.create()
                .texOffs(86, 25).addBox(-4.5F, -4F, 0F, 9, 8, 8, NONE)
                .texOffs(160, 88).addBox(-3.5F, -5F, 1F, 7, 1, 6, NONE)
                .texOffs(186, 88).addBox(-3.5F, 4F, 1F, 7, 1, 6, NONE)
                .texOffs(108, 79).addBox(-5.5F, -3F, 2F, 1, 5, 4, NONE)
                .texOffs(118, 79).addBox(4.5F, -3F, 2F, 1, 5, 4, NONE),
                PartPose.offsetAndRotation(0F, 0F, 9F, 0.175F, -0.244F, 0F));
        PartDefinition p_spine_5 = p_seg_5.addOrReplaceChild("spine_5", CubeListBuilder.create()
                .texOffs(84, 88).addBox(-1F, -6F, -1F, 2, 6, 2, NONE)
                .texOffs(128, 109).addBox(-0.5F, -8F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -4.5F, 3.2F, -0.698F, 0F, 0F));
        PartDefinition p_seg_6 = p_seg_5.addOrReplaceChild("seg_6", CubeListBuilder.create()
                .texOffs(156, 25).addBox(-4F, -3.5F, 0F, 8, 7, 8, NONE)
                .texOffs(212, 88).addBox(-3F, -4.5F, 1F, 6, 1, 6, NONE)
                .texOffs(0, 96).addBox(-3F, 3.5F, 1F, 6, 1, 6, NONE),
                PartPose.offsetAndRotation(0F, 0F, 8F, 0.105F, 0.209F, 0F));
        PartDefinition p_spine_6 = p_seg_6.addOrReplaceChild("spine_6", CubeListBuilder.create()
                .texOffs(104, 103).addBox(-1F, -3F, -1F, 2, 3, 2, NONE)
                .texOffs(132, 109).addBox(-0.5F, -5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -4F, 3.2F, -0.698F, 0F, 0F));
        PartDefinition p_seg_7 = p_seg_6.addOrReplaceChild("seg_7", CubeListBuilder.create()
                .texOffs(104, 43).addBox(-3.5F, -3F, 0F, 7, 6, 7, NONE)
                .texOffs(172, 96).addBox(-2.5F, -4F, 1F, 5, 1, 5, NONE)
                .texOffs(192, 96).addBox(-2.5F, 3F, 1F, 5, 1, 5, NONE),
                PartPose.offsetAndRotation(0F, 0F, 8F, 0F, 0.384F, 0F));
        PartDefinition p_spine_7 = p_seg_7.addOrReplaceChild("spine_7", CubeListBuilder.create()
                .texOffs(70, 96).addBox(-1F, -5F, -1F, 2, 5, 2, NONE)
                .texOffs(136, 109).addBox(-0.5F, -7F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -3.5F, 2.8F, -0.698F, 0F, 0F));
        PartDefinition p_seg_8 = p_seg_7.addOrReplaceChild("seg_8", CubeListBuilder.create()
                .texOffs(168, 43).addBox(-3F, -2.5F, 0F, 6, 5, 7, NONE)
                .texOffs(212, 96).addBox(-2F, -3.5F, 1F, 4, 1, 5, NONE)
                .texOffs(230, 96).addBox(-2F, 2.5F, 1F, 4, 1, 5, NONE),
                PartPose.offsetAndRotation(0F, 0F, 7F, -0.105F, 0.279F, 0F));
        PartDefinition p_spine_8 = p_seg_8.addOrReplaceChild("spine_8", CubeListBuilder.create()
                .texOffs(38, 109).addBox(-1F, -2F, -1F, 2, 2, 2, NONE)
                .texOffs(140, 109).addBox(-0.5F, -4F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -3F, 2.8F, -0.698F, 0F, 0F));
        PartDefinition p_seg_9 = p_seg_8.addOrReplaceChild("seg_9", CubeListBuilder.create()
                .texOffs(40, 69).addBox(-2.5F, -2F, 0F, 5, 4, 6, NONE)
                .texOffs(60, 103).addBox(-1.5F, -3F, 1F, 3, 1, 4, NONE)
                .texOffs(74, 103).addBox(-1.5F, 2F, 1F, 3, 1, 4, NONE),
                PartPose.offsetAndRotation(0F, 0F, 7F, -0.14F, -0.14F, 0F));
        PartDefinition p_spine_9 = p_seg_9.addOrReplaceChild("spine_9", CubeListBuilder.create()
                .texOffs(52, 103).addBox(-1F, -4F, -1F, 2, 4, 2, NONE)
                .texOffs(144, 109).addBox(-0.5F, -6F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -2.5F, 2.4F, -0.698F, 0F, 0F));
        PartDefinition p_tail = p_seg_9.addOrReplaceChild("tail", CubeListBuilder.create()
                .texOffs(60, 79).addBox(-1.5F, -1.5F, 0F, 3, 3, 6, NONE)
                .texOffs(142, 96).addBox(-5F, -0.5F, 4F, 10, 1, 5, NONE)
                .texOffs(208, 103).addBox(-3F, -0.5F, 9F, 6, 1, 3, NONE)
                .texOffs(64, 109).addBox(-1F, -0.5F, 12F, 2, 1, 2, NONE)
                .texOffs(94, 79).addBox(-0.5F, -4F, 3F, 1, 3, 6, NONE)
                .texOffs(46, 109).addBox(-4F, -1F, 3.5F, 8, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 6F, -0.105F, -0.175F, 0F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    public static final Role[] MAGMA_WYRM_ROLES = {new Role("body", "body", 0.00F), new Role("body/head", "head", 0.00F), new Role("body/head/jaw", "jaw", 0.00F), new Role("body/wing_r", "wing_r", 0.00F), new Role("body/wing_l", "wing_l", 0.00F), new Role("body/seg_1", "chain", 1.00F), new Role("body/seg_1/seg_2", "chain", 2.00F), new Role("body/seg_1/seg_2/seg_3", "chain", 3.00F), new Role("body/seg_1/seg_2/seg_3/seg_4", "chain", 4.00F), new Role("body/seg_1/seg_2/seg_3/seg_4/seg_5", "chain", 5.00F), new Role("body/seg_1/seg_2/seg_3/seg_4/seg_5/seg_6", "chain", 6.00F), new Role("body/seg_1/seg_2/seg_3/seg_4/seg_5/seg_6/seg_7", "chain", 7.00F), new Role("body/seg_1/seg_2/seg_3/seg_4/seg_5/seg_6/seg_7/seg_8", "chain", 8.00F), new Role("body/seg_1/seg_2/seg_3/seg_4/seg_5/seg_6/seg_7/seg_8/seg_9", "chain", 9.00F), new Role("body/seg_1/seg_2/seg_3/seg_4/seg_5/seg_6/seg_7/seg_8/seg_9/tail", "chain", 10.00F)};

    public static final Key[] MAGMA_WYRM_POSES = {
            new Key(0, 0F, "body", 12F, 0F, 0F),
            new Key(0, 0F, "body/head", 30F, 0F, 0F),
            new Key(0, 0F, "body/head/jaw", -5F, 0F, 0F),
            new Key(0, 0F, "body/wing_r", 0F, 30F, -45F),
            new Key(0, 0F, "body/wing_l", 0F, -30F, 45F),
            new Key(0, 0F, "body/wing_r/wtip_r", 0F, 40F, 0F),
            new Key(0, 0F, "body/wing_l/wtip_l", 0F, -40F, 0F),
            new Key(0, 0F, "body/seg_1", 0F, 28F, 0F),
            new Key(0, 0F, "body/seg_1/seg_2", 0F, 28F, 0F),
            new Key(0, 0F, "body/seg_1/seg_2/seg_3", 0F, 28F, 0F),
            new Key(0, 0F, "body/seg_1/seg_2/seg_3/seg_4", 0F, 28F, 0F),
            new Key(0, 0F, "body/seg_1/seg_2/seg_3/seg_4/seg_5", 0F, 28F, 0F),
            new Key(0, 0F, "body/seg_1/seg_2/seg_3/seg_4/seg_5/seg_6", 0F, 28F, 0F),
            new Key(0, 0F, "body/seg_1/seg_2/seg_3/seg_4/seg_5/seg_6/seg_7", 0F, 28F, 0F),
            new Key(0, 0F, "body/seg_1/seg_2/seg_3/seg_4/seg_5/seg_6/seg_7/seg_8", 0F, 28F, 0F),
            new Key(0, 0F, "body/seg_1/seg_2/seg_3/seg_4/seg_5/seg_6/seg_7/seg_8/seg_9", 0F, 28F, 0F),
            new Key(1, 0F, "body", 0F, 0F, 0F),
            new Key(1, 6F, "body", -12F, 0F, 0F),
            new Key(1, 10F, "body", 6F, 0F, 0F),
            new Key(1, 28F, "body", 6F, 0F, 0F),
            new Key(1, 34F, "body", 0F, 0F, 0F),
            new Key(1, 0F, "body/head", 0F, 0F, 0F),
            new Key(1, 6F, "body/head", -25F, 0F, 0F),
            new Key(1, 10F, "body/head", 12F, 0F, 0F),
            new Key(1, 28F, "body/head", 12F, 0F, 0F),
            new Key(1, 34F, "body/head", 0F, 0F, 0F),
            new Key(1, 0F, "body/head/jaw", 0F, 0F, 0F),
            new Key(1, 6F, "body/head/jaw", 10F, 0F, 0F),
            new Key(1, 10F, "body/head/jaw", 45F, 0F, 0F),
            new Key(1, 28F, "body/head/jaw", 45F, 0F, 0F),
            new Key(1, 34F, "body/head/jaw", 0F, 0F, 0F),
            new Key(1, 0F, "body/wing_r", 0F, 0F, 0F),
            new Key(1, 6F, "body/wing_r", 0F, -10F, 30F),
            new Key(1, 28F, "body/wing_r", 0F, -10F, 30F),
            new Key(1, 34F, "body/wing_r", 0F, 0F, 0F),
            new Key(1, 0F, "body/wing_l", 0F, 0F, 0F),
            new Key(1, 6F, "body/wing_l", 0F, 10F, -30F),
            new Key(1, 28F, "body/wing_l", 0F, 10F, -30F),
            new Key(1, 34F, "body/wing_l", 0F, 0F, 0F),
            new Key(1, 0F, "body/seg_1", 0F, 0F, 0F),
            new Key(1, 6F, "body/seg_1", 12F, 0F, 0F),
            new Key(1, 10F, "body/seg_1", -6F, 0F, 0F),
            new Key(1, 34F, "body/seg_1", 0F, 0F, 0F),
            new Key(2, 0F, "body", 0F, 0F, 0F),
            new Key(2, 6F, "body", -14F, 0F, 0F),
            new Key(2, 8F, "body", 8F, 0F, 0F),
            new Key(2, 14F, "body", 0F, 0F, 0F),
            new Key(2, 0F, "body/head", 0F, 0F, 0F),
            new Key(2, 6F, "body/head", -30F, 0F, 0F),
            new Key(2, 8F, "body/head", 15F, 0F, 0F),
            new Key(2, 14F, "body/head", 0F, 0F, 0F),
            new Key(2, 0F, "body/head/jaw", 0F, 0F, 0F),
            new Key(2, 6F, "body/head/jaw", 20F, 0F, 0F),
            new Key(2, 8F, "body/head/jaw", 50F, 0F, 0F),
            new Key(2, 14F, "body/head/jaw", 0F, 0F, 0F),
            new Key(2, 0F, "body/seg_1", 0F, 0F, 0F),
            new Key(2, 6F, "body/seg_1", 15F, 0F, 0F),
            new Key(2, 8F, "body/seg_1", -8F, 0F, 0F),
            new Key(2, 14F, "body/seg_1", 0F, 0F, 0F),
            new Key(2, 0F, "body/wing_r", 0F, 0F, 0F),
            new Key(2, 6F, "body/wing_r", 0F, 0F, 35F),
            new Key(2, 8F, "body/wing_r", 0F, 0F, -10F),
            new Key(2, 14F, "body/wing_r", 0F, 0F, 0F),
            new Key(2, 0F, "body/wing_l", 0F, 0F, 0F),
            new Key(2, 6F, "body/wing_l", 0F, 0F, -35F),
            new Key(2, 8F, "body/wing_l", 0F, 0F, 10F),
            new Key(2, 14F, "body/wing_l", 0F, 0F, 0F),
            new Key(3, 0F, "body", 0F, 0F, 0F),
            new Key(3, 10F, "body", -35F, 0F, 0F),
            new Key(3, 15F, "body", -35F, 0F, 0F),
            new Key(3, 19F, "body", 45F, 0F, 0F),
            new Key(3, 32F, "body", 45F, 0F, 0F),
            new Key(3, 38F, "body", 0F, 0F, 0F),
            new Key(3, 0F, "body/head", 0F, 0F, 0F),
            new Key(3, 10F, "body/head", -15F, 0F, 0F),
            new Key(3, 19F, "body/head", 15F, 0F, 0F),
            new Key(3, 32F, "body/head", 15F, 0F, 0F),
            new Key(3, 38F, "body/head", 0F, 0F, 0F),
            new Key(3, 0F, "body/head/jaw", 0F, 0F, 0F),
            new Key(3, 10F, "body/head/jaw", 10F, 0F, 0F),
            new Key(3, 19F, "body/head/jaw", 35F, 0F, 0F),
            new Key(3, 32F, "body/head/jaw", 35F, 0F, 0F),
            new Key(3, 38F, "body/head/jaw", 0F, 0F, 0F),
            new Key(3, 0F, "body/wing_r", 0F, 0F, 0F),
            new Key(3, 10F, "body/wing_r", 0F, 0F, 35F),
            new Key(3, 19F, "body/wing_r", 0F, 45F, -40F),
            new Key(3, 32F, "body/wing_r", 0F, 45F, -40F),
            new Key(3, 38F, "body/wing_r", 0F, 0F, 0F),
            new Key(3, 0F, "body/wing_l", 0F, 0F, 0F),
            new Key(3, 10F, "body/wing_l", 0F, 0F, -35F),
            new Key(3, 19F, "body/wing_l", 0F, -45F, 40F),
            new Key(3, 32F, "body/wing_l", 0F, -45F, 40F),
            new Key(3, 38F, "body/wing_l", 0F, 0F, 0F),
            new Key(3, 0F, "body/seg_1", 0F, 0F, 0F),
            new Key(3, 0F, "body/seg_1/seg_2", 0F, 0F, 0F),
            new Key(3, 0F, "body/seg_1/seg_2/seg_3", 0F, 0F, 0F),
            new Key(3, 0F, "body/seg_1/seg_2/seg_3/seg_4", 0F, 0F, 0F),
            new Key(3, 10F, "body/seg_1", 10F, 0F, 0F),
            new Key(3, 10F, "body/seg_1/seg_2", 10F, 0F, 0F),
            new Key(3, 10F, "body/seg_1/seg_2/seg_3", 10F, 0F, 0F),
            new Key(3, 10F, "body/seg_1/seg_2/seg_3/seg_4", 10F, 0F, 0F),
            new Key(3, 19F, "body/seg_1", -12F, 0F, 0F),
            new Key(3, 19F, "body/seg_1/seg_2", -12F, 0F, 0F),
            new Key(3, 19F, "body/seg_1/seg_2/seg_3", -12F, 0F, 0F),
            new Key(3, 19F, "body/seg_1/seg_2/seg_3/seg_4", -12F, 0F, 0F),
            new Key(3, 32F, "body/seg_1", -12F, 0F, 0F),
            new Key(3, 32F, "body/seg_1/seg_2", -12F, 0F, 0F),
            new Key(3, 32F, "body/seg_1/seg_2/seg_3", -12F, 0F, 0F),
            new Key(3, 32F, "body/seg_1/seg_2/seg_3/seg_4", -12F, 0F, 0F),
            new Key(3, 38F, "body/seg_1", 0F, 0F, 0F),
            new Key(3, 38F, "body/seg_1/seg_2", 0F, 0F, 0F),
            new Key(3, 38F, "body/seg_1/seg_2/seg_3", 0F, 0F, 0F),
            new Key(3, 38F, "body/seg_1/seg_2/seg_3/seg_4", 0F, 0F, 0F),
            new Key(4, 0F, "body", 0F, 0F, 0F),
            new Key(4, 8F, "body", -22F, 0F, 0F),
            new Key(4, 18F, "body", -22F, 0F, 0F),
            new Key(4, 21F, "body", 14F, 0F, 0F),
            new Key(4, 26F, "body", 0F, 0F, 0F),
            new Key(4, 0F, "body/head", 0F, 0F, 0F),
            new Key(4, 8F, "body/head", -35F, 0F, 0F),
            new Key(4, 18F, "body/head", -35F, 0F, 0F),
            new Key(4, 21F, "body/head", 20F, 0F, 0F),
            new Key(4, 26F, "body/head", 0F, 0F, 0F),
            new Key(4, 0F, "body/head/jaw", 0F, 0F, 0F),
            new Key(4, 8F, "body/head/jaw", 50F, 0F, 0F),
            new Key(4, 18F, "body/head/jaw", 50F, 0F, 0F),
            new Key(4, 21F, "body/head/jaw", 30F, 0F, 0F),
            new Key(4, 26F, "body/head/jaw", 0F, 0F, 0F),
            new Key(4, 0F, "body/wing_r", 0F, 0F, 0F),
            new Key(4, 8F, "body/wing_r", 0F, -15F, 45F),
            new Key(4, 18F, "body/wing_r", 0F, -15F, 45F),
            new Key(4, 21F, "body/wing_r", 0F, 0F, -20F),
            new Key(4, 26F, "body/wing_r", 0F, 0F, 0F),
            new Key(4, 0F, "body/wing_l", 0F, 0F, 0F),
            new Key(4, 8F, "body/wing_l", 0F, 15F, -45F),
            new Key(4, 18F, "body/wing_l", 0F, 15F, -45F),
            new Key(4, 21F, "body/wing_l", 0F, 0F, 20F),
            new Key(4, 26F, "body/wing_l", 0F, 0F, 0F),
            new Key(4, 0F, "body/seg_1", 0F, 0F, 0F),
            new Key(4, 0F, "body/seg_1/seg_2", 0F, 0F, 0F),
            new Key(4, 0F, "body/seg_1/seg_2/seg_3", 0F, 0F, 0F),
            new Key(4, 8F, "body/seg_1", 10F, 0F, 0F),
            new Key(4, 8F, "body/seg_1/seg_2", 10F, 0F, 0F),
            new Key(4, 8F, "body/seg_1/seg_2/seg_3", 10F, 0F, 0F),
            new Key(4, 21F, "body/seg_1", -6F, 0F, 0F),
            new Key(4, 21F, "body/seg_1/seg_2", -6F, 0F, 0F),
            new Key(4, 21F, "body/seg_1/seg_2/seg_3", -6F, 0F, 0F),
            new Key(4, 26F, "body/seg_1", 0F, 0F, 0F),
            new Key(4, 26F, "body/seg_1/seg_2", 0F, 0F, 0F),
            new Key(4, 26F, "body/seg_1/seg_2/seg_3", 0F, 0F, 0F)};

    public static final float MAGMA_WYRM_SCALE = 1.25F;

    /** Schrab Hydra: vierbeinige Hydra mit tuerkisem Schuppenleib, violettem Bauch, drei langen Haelsen mit Kammkoepfen und cyan leuchtenden Augen, Schrabidium-Kristallgruppen auf dem Ruecken und dickem Schwanz mit Kristallkeule. Textur {@code entity/stage/schrab_hydra.png}. */
    public static LayerDefinition schrabHydra() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-12F, -10F, -16F, 24, 18, 33, NONE)
                .texOffs(164, 51).addBox(-13F, -13F, -18F, 26, 10, 14, NONE)
                .texOffs(16, 81).addBox(-12.5F, -12F, 5F, 25, 9, 12, NONE)
                .texOffs(0, 51).addBox(-10F, 8F, -14F, 20, 2, 28, NONE)
                .texOffs(124, 142).addBox(-7F, -6F, -19F, 14, 12, 3, NONE)
                .texOffs(114, 0).addBox(-13F, -5F, -12F, 1, 10, 26, NONE)
                .texOffs(168, 0).addBox(12F, -5F, -12F, 1, 10, 26, NONE)
                .texOffs(96, 51).addBox(-3F, -14F, -14F, 6, 2, 28, NONE),
                PartPose.offsetAndRotation(0F, 2F, 2F, 0F, 0F, 0F));
        PartDefinition p_neck_c1 = p_body.addOrReplaceChild("neck_c1", CubeListBuilder.create()
                .texOffs(50, 105).addBox(-3.5F, -10F, -3.5F, 7, 11, 7, NONE)
                .texOffs(90, 196).addBox(-2.5F, -9F, -4.5F, 5, 8, 1, NONE)
                .texOffs(210, 206).addBox(-0.5F, -8F, 3.5F, 1, 6, 2, NONE),
                PartPose.offsetAndRotation(0F, -10F, -15F, 0.838F, 0F, 0F));
        PartDefinition p_neck_c2 = p_neck_c1.addOrReplaceChild("neck_c2", CubeListBuilder.create()
                .texOffs(172, 105).addBox(-3F, -10F, -3F, 6, 11, 6, NONE)
                .texOffs(198, 196).addBox(-2F, -9F, -4F, 4, 8, 1, NONE)
                .texOffs(216, 206).addBox(-0.5F, -8F, 3F, 1, 6, 2, NONE),
                PartPose.offsetAndRotation(0F, -10F, 0F, -0.384F, 0F, 0F));
        PartDefinition p_neck_c3 = p_neck_c2.addOrReplaceChild("neck_c3", CubeListBuilder.create()
                .texOffs(36, 142).addBox(-3F, -10F, -3F, 6, 10, 6, NONE)
                .texOffs(208, 196).addBox(-2F, -9F, -4F, 4, 8, 1, NONE),
                PartPose.offsetAndRotation(0F, -10F, 0F, -0.524F, 0F, 0F));
        PartDefinition p_head_c = p_neck_c3.addOrReplaceChild("head_c", CubeListBuilder.create()
                .texOffs(184, 125).addBox(-4.5F, -6F, -6F, 9, 7, 9, NONE)
                .texOffs(0, 215).addBox(-5F, -7F, -7F, 10, 2, 5, NONE)
                .texOffs(226, 173).addBox(-3.5F, -4F, -13F, 7, 4, 7, NONE)
                .texOffs(186, 158).addBox(-3F, 0F, -13F, 6, 1, 11, NONE)
                .texOffs(150, 215).addBox(-4.8F, -4.5F, -6F, 1, 2, 3, NONE)
                .texOffs(158, 215).addBox(3.8F, -4.5F, -6F, 1, 2, 3, NONE)
                .texOffs(102, 196).addBox(-5.5F, -3F, -2F, 1, 4, 5, NONE)
                .texOffs(114, 196).addBox(4.5F, -3F, -2F, 1, 4, 5, NONE),
                PartPose.offsetAndRotation(0F, -10F, 0F, 0.209F, 0F, 0F));
        PartDefinition p_jaw_c = p_head_c.addOrReplaceChild("jaw_c", CubeListBuilder.create()
                .texOffs(60, 158).addBox(-3.5F, 0F, -12F, 7, 2, 12, NONE)
                .texOffs(96, 185).addBox(-3F, 2.5F, -11F, 6, 1, 9, NONE)
                .texOffs(186, 185).addBox(-2.5F, -0.5F, -11F, 5, 1, 9, NONE)
                .texOffs(40, 222).addBox(-3F, -1.5F, -11.5F, 1, 2, 1, new CubeDeformation(0.1F))
                .texOffs(44, 222).addBox(2F, -1.5F, -11.5F, 1, 2, 1, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(0F, 1F, -1F, 0F, 0F, 0F));
        PartDefinition p_crest_c = p_head_c.addOrReplaceChild("crest_c", CubeListBuilder.create()
                .texOffs(60, 173).addBox(-0.5F, -8F, -1F, 1, 8, 4, NONE)
                .texOffs(48, 196).addBox(-1F, -4F, 2F, 2, 4, 5, NONE),
                PartPose.offsetAndRotation(0F, -6F, -2F, -0.611F, 0F, 0F));
        PartDefinition p_neck_r1 = p_body.addOrReplaceChild("neck_r1", CubeListBuilder.create()
                .texOffs(78, 105).addBox(-3.5F, -10F, -3.5F, 7, 11, 7, NONE)
                .texOffs(126, 196).addBox(-2.5F, -9F, -4.5F, 5, 8, 1, NONE)
                .texOffs(222, 206).addBox(-0.5F, -8F, 3.5F, 1, 6, 2, NONE),
                PartPose.offsetAndRotation(-8.5F, -8F, -14F, 0.838F, 0.237F, -0.593F));
        PartDefinition p_neck_r2 = p_neck_r1.addOrReplaceChild("neck_r2", CubeListBuilder.create()
                .texOffs(196, 105).addBox(-3F, -10F, -3F, 6, 11, 6, NONE)
                .texOffs(218, 196).addBox(-2F, -9F, -4F, 4, 8, 1, NONE)
                .texOffs(228, 206).addBox(-0.5F, -8F, 3F, 1, 6, 2, NONE),
                PartPose.offsetAndRotation(0F, -10F, 0F, -0.384F, 0F, 0.208F));
        PartDefinition p_neck_r3 = p_neck_r2.addOrReplaceChild("neck_r3", CubeListBuilder.create()
                .texOffs(60, 142).addBox(-3F, -10F, -3F, 6, 10, 6, NONE)
                .texOffs(228, 196).addBox(-2F, -9F, -4F, 4, 8, 1, NONE),
                PartPose.offsetAndRotation(0F, -10F, 0F, -0.524F, 0F, 0.208F));
        PartDefinition p_head_r = p_neck_r3.addOrReplaceChild("head_r", CubeListBuilder.create()
                .texOffs(220, 125).addBox(-4.5F, -6F, -6F, 9, 7, 9, NONE)
                .texOffs(30, 215).addBox(-5F, -7F, -7F, 10, 2, 5, NONE)
                .texOffs(0, 185).addBox(-3.5F, -4F, -13F, 7, 4, 7, NONE)
                .texOffs(220, 158).addBox(-3F, 0F, -13F, 6, 1, 11, NONE)
                .texOffs(166, 215).addBox(-4.8F, -4.5F, -6F, 1, 2, 3, NONE)
                .texOffs(174, 215).addBox(3.8F, -4.5F, -6F, 1, 2, 3, NONE)
                .texOffs(138, 196).addBox(-5.5F, -3F, -2F, 1, 4, 5, NONE)
                .texOffs(150, 196).addBox(4.5F, -3F, -2F, 1, 4, 5, NONE),
                PartPose.offsetAndRotation(0F, -10F, 0F, 0.209F, 0F, 0.178F));
        PartDefinition p_jaw_r = p_head_r.addOrReplaceChild("jaw_r", CubeListBuilder.create()
                .texOffs(98, 158).addBox(-3.5F, 0F, -12F, 7, 2, 12, NONE)
                .texOffs(126, 185).addBox(-3F, 2.5F, -11F, 6, 1, 9, NONE)
                .texOffs(214, 185).addBox(-2.5F, -0.5F, -11F, 5, 1, 9, NONE)
                .texOffs(48, 222).addBox(-3F, -1.5F, -11.5F, 1, 2, 1, new CubeDeformation(0.1F))
                .texOffs(52, 222).addBox(2F, -1.5F, -11.5F, 1, 2, 1, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(0F, 1F, -1F, 0F, 0F, 0F));
        PartDefinition p_crest_r = p_head_r.addOrReplaceChild("crest_r", CubeListBuilder.create()
                .texOffs(70, 173).addBox(-0.5F, -8F, -1F, 1, 8, 4, NONE)
                .texOffs(62, 196).addBox(-1F, -4F, 2F, 2, 4, 5, NONE),
                PartPose.offsetAndRotation(0F, -6F, -2F, -0.611F, 0F, 0F));
        PartDefinition p_neck_l1 = p_body.addOrReplaceChild("neck_l1", CubeListBuilder.create()
                .texOffs(106, 105).addBox(-3.5F, -10F, -3.5F, 7, 11, 7, NONE)
                .texOffs(162, 196).addBox(-2.5F, -9F, -4.5F, 5, 8, 1, NONE)
                .texOffs(234, 206).addBox(-0.5F, -8F, 3.5F, 1, 6, 2, NONE),
                PartPose.offsetAndRotation(8.5F, -8F, -14F, 0.838F, -0.237F, 0.593F));
        PartDefinition p_neck_l2 = p_neck_l1.addOrReplaceChild("neck_l2", CubeListBuilder.create()
                .texOffs(220, 105).addBox(-3F, -10F, -3F, 6, 11, 6, NONE)
                .texOffs(238, 196).addBox(-2F, -9F, -4F, 4, 8, 1, NONE)
                .texOffs(240, 206).addBox(-0.5F, -8F, 3F, 1, 6, 2, NONE),
                PartPose.offsetAndRotation(0F, -10F, 0F, -0.384F, 0F, -0.208F));
        PartDefinition p_neck_l3 = p_neck_l2.addOrReplaceChild("neck_l3", CubeListBuilder.create()
                .texOffs(84, 142).addBox(-3F, -10F, -3F, 6, 10, 6, NONE)
                .texOffs(0, 206).addBox(-2F, -9F, -4F, 4, 8, 1, NONE),
                PartPose.offsetAndRotation(0F, -10F, 0F, -0.524F, 0F, -0.208F));
        PartDefinition p_head_l = p_neck_l3.addOrReplaceChild("head_l", CubeListBuilder.create()
                .texOffs(0, 142).addBox(-4.5F, -6F, -6F, 9, 7, 9, NONE)
                .texOffs(60, 215).addBox(-5F, -7F, -7F, 10, 2, 5, NONE)
                .texOffs(28, 185).addBox(-3.5F, -4F, -13F, 7, 4, 7, NONE)
                .texOffs(0, 173).addBox(-3F, 0F, -13F, 6, 1, 11, NONE)
                .texOffs(182, 215).addBox(-4.8F, -4.5F, -6F, 1, 2, 3, NONE)
                .texOffs(190, 215).addBox(3.8F, -4.5F, -6F, 1, 2, 3, NONE)
                .texOffs(174, 196).addBox(-5.5F, -3F, -2F, 1, 4, 5, NONE)
                .texOffs(186, 196).addBox(4.5F, -3F, -2F, 1, 4, 5, NONE),
                PartPose.offsetAndRotation(0F, -10F, 0F, 0.209F, 0F, -0.178F));
        PartDefinition p_jaw_l = p_head_l.addOrReplaceChild("jaw_l", CubeListBuilder.create()
                .texOffs(136, 158).addBox(-3.5F, 0F, -12F, 7, 2, 12, NONE)
                .texOffs(156, 185).addBox(-3F, 2.5F, -11F, 6, 1, 9, NONE)
                .texOffs(0, 196).addBox(-2.5F, -0.5F, -11F, 5, 1, 9, NONE)
                .texOffs(56, 222).addBox(-3F, -1.5F, -11.5F, 1, 2, 1, new CubeDeformation(0.1F))
                .texOffs(60, 222).addBox(2F, -1.5F, -11.5F, 1, 2, 1, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(0F, 1F, -1F, 0F, 0F, 0F));
        PartDefinition p_crest_l = p_head_l.addOrReplaceChild("crest_l", CubeListBuilder.create()
                .texOffs(80, 173).addBox(-0.5F, -8F, -1F, 1, 8, 4, NONE)
                .texOffs(76, 196).addBox(-1F, -4F, 2F, 2, 4, 5, NONE),
                PartPose.offsetAndRotation(0F, -6F, -2F, -0.611F, 0F, 0F));
        PartDefinition p_crys_1 = p_body.addOrReplaceChild("crys_1", CubeListBuilder.create()
                .texOffs(0, 125).addBox(-2F, -13F, -2F, 4, 13, 4, NONE)
                .texOffs(10, 206).addBox(-3F, -1F, -3F, 6, 2, 6, NONE),
                PartPose.offsetAndRotation(-6F, -13F, -7F, -0.175F, 0.349F, -0.384F));
        PartDefinition p_crys_1_a = p_crys_1.addOrReplaceChild("crys_1_a", CubeListBuilder.create()
                .texOffs(72, 185).addBox(-1.5F, -7.8F, -1.5F, 3, 8, 3, NONE),
                PartPose.offsetAndRotation(-2F, 0F, 0.5F, 0.175F, 0F, -0.593F));
        PartDefinition p_crys_1_b = p_crys_1.addOrReplaceChild("crys_1_b", CubeListBuilder.create()
                .texOffs(186, 206).addBox(-1F, -6.5F, -1F, 2, 6, 2, NONE),
                PartPose.offsetAndRotation(2F, 0F, -0.5F, -0.262F, 0.349F, 0.524F));
        PartDefinition p_crys_2 = p_body.addOrReplaceChild("crys_2", CubeListBuilder.create()
                .texOffs(34, 105).addBox(-2F, -16F, -2F, 4, 16, 4, NONE)
                .texOffs(34, 206).addBox(-3F, -1F, -3F, 6, 2, 6, NONE),
                PartPose.offsetAndRotation(6F, -13F, -3F, 0.14F, -0.524F, 0.349F));
        PartDefinition p_crys_2_a = p_crys_2.addOrReplaceChild("crys_2_a", CubeListBuilder.create()
                .texOffs(174, 158).addBox(-1.5F, -9.6F, -1.5F, 3, 10, 3, NONE),
                PartPose.offsetAndRotation(-2F, 0F, 0.5F, 0.175F, 0F, -0.593F));
        PartDefinition p_crys_2_b = p_crys_2.addOrReplaceChild("crys_2_b", CubeListBuilder.create()
                .texOffs(40, 196).addBox(-1F, -8F, -1F, 2, 8, 2, NONE),
                PartPose.offsetAndRotation(2F, 0F, -0.5F, -0.262F, 0.349F, 0.524F));
        PartDefinition p_crys_3 = p_body.addOrReplaceChild("crys_3", CubeListBuilder.create()
                .texOffs(108, 142).addBox(-2F, -12F, -2F, 4, 12, 4, NONE)
                .texOffs(58, 206).addBox(-3F, -1F, -3F, 6, 2, 6, NONE),
                PartPose.offsetAndRotation(-6F, -12F, 8F, 0.244F, 0.785F, -0.314F));
        PartDefinition p_crys_3_a = p_crys_3.addOrReplaceChild("crys_3_a", CubeListBuilder.create()
                .texOffs(28, 196).addBox(-1.5F, -7.2F, -1.5F, 3, 7, 3, NONE),
                PartPose.offsetAndRotation(-2F, 0F, 0.5F, 0.175F, 0F, -0.593F));
        PartDefinition p_crys_3_b = p_crys_3.addOrReplaceChild("crys_3_b", CubeListBuilder.create()
                .texOffs(194, 206).addBox(-1F, -6F, -1F, 2, 6, 2, NONE),
                PartPose.offsetAndRotation(2F, 0F, -0.5F, -0.262F, 0.349F, 0.524F));
        PartDefinition p_crys_4 = p_body.addOrReplaceChild("crys_4", CubeListBuilder.create()
                .texOffs(16, 125).addBox(-2F, -13F, -2F, 4, 13, 4, NONE)
                .texOffs(82, 206).addBox(-3F, -1F, -3F, 6, 2, 6, NONE),
                PartPose.offsetAndRotation(6F, -12F, 12F, 0.314F, 0.175F, 0.419F));
        PartDefinition p_crys_4_a = p_crys_4.addOrReplaceChild("crys_4_a", CubeListBuilder.create()
                .texOffs(84, 185).addBox(-1.5F, -7.8F, -1.5F, 3, 8, 3, NONE),
                PartPose.offsetAndRotation(-2F, 0F, 0.5F, 0.175F, 0F, -0.593F));
        PartDefinition p_crys_4_b = p_crys_4.addOrReplaceChild("crys_4_b", CubeListBuilder.create()
                .texOffs(202, 206).addBox(-1F, -6.5F, -1F, 2, 6, 2, NONE),
                PartPose.offsetAndRotation(2F, 0F, -0.5F, -0.262F, 0.349F, 0.524F));
        PartDefinition p_crys_5 = p_body.addOrReplaceChild("crys_5", CubeListBuilder.create()
                .texOffs(0, 81).addBox(-2F, -20F, -2F, 4, 20, 4, NONE)
                .texOffs(106, 206).addBox(-3F, -1F, -3F, 6, 2, 6, NONE),
                PartPose.offsetAndRotation(0F, -15F, 2F, -0.105F, 0F, 0F));
        PartDefinition p_crys_5_a = p_crys_5.addOrReplaceChild("crys_5_a", CubeListBuilder.create()
                .texOffs(48, 158).addBox(-1.5F, -12F, -1.5F, 3, 12, 3, NONE),
                PartPose.offsetAndRotation(-2F, 0F, 0.5F, 0.175F, 0F, -0.593F));
        PartDefinition p_crys_5_b = p_crys_5.addOrReplaceChild("crys_5_b", CubeListBuilder.create()
                .texOffs(90, 173).addBox(-1F, -10F, -1F, 2, 10, 2, NONE),
                PartPose.offsetAndRotation(2F, 0F, -0.5F, -0.262F, 0.349F, 0.524F));
        PartDefinition p_tail_1 = p_body.addOrReplaceChild("tail_1", CubeListBuilder.create()
                .texOffs(90, 81).addBox(-6F, -5F, 0F, 12, 10, 10, NONE)
                .texOffs(154, 206).addBox(-1F, -7F, 2F, 2, 2, 6, NONE),
                PartPose.offsetAndRotation(0F, -2F, 17F, -0.314F, 0.175F, 0F));
        PartDefinition p_tail_2 = p_tail_1.addOrReplaceChild("tail_2", CubeListBuilder.create()
                .texOffs(134, 105).addBox(-5F, -4F, 0F, 10, 8, 9, NONE)
                .texOffs(90, 215).addBox(-1F, -6F, 2F, 2, 2, 5, NONE),
                PartPose.offsetAndRotation(0F, 0F, 10F, -0.14F, 0.244F, 0F));
        PartDefinition p_tail_3 = p_tail_2.addOrReplaceChild("tail_3", CubeListBuilder.create()
                .texOffs(158, 142).addBox(-4F, -3F, 0F, 8, 6, 9, NONE)
                .texOffs(104, 215).addBox(-1F, -5F, 2F, 2, 2, 5, NONE),
                PartPose.offsetAndRotation(0F, 0F, 9F, 0.105F, 0.279F, 0F));
        PartDefinition p_tail_4 = p_tail_3.addOrReplaceChild("tail_4", CubeListBuilder.create()
                .texOffs(34, 173).addBox(-2.5F, -2F, 0F, 5, 4, 8, NONE)
                .texOffs(130, 215).addBox(-1F, -4F, 2F, 2, 2, 4, NONE),
                PartPose.offsetAndRotation(0F, 0F, 9F, 0.175F, 0.209F, 0F));
        PartDefinition p_tail_tip = p_tail_4.addOrReplaceChild("tail_tip", CubeListBuilder.create()
                .texOffs(170, 206).addBox(-2F, -2F, 0F, 4, 4, 4, NONE),
                PartPose.offsetAndRotation(0F, 0F, 8F, 0.175F, 0.175F, 0F));
        PartDefinition p_tail_spike = p_tail_tip.addOrReplaceChild("tail_spike", CubeListBuilder.create()
                .texOffs(56, 185).addBox(-2F, -7F, -2F, 4, 7, 4, NONE)
                .texOffs(130, 206).addBox(-3F, -1F, -3F, 6, 2, 6, NONE),
                PartPose.offsetAndRotation(0F, 0F, 3F, -1.396F, 0F, 0F));
        PartDefinition p_tail_spike_a = p_tail_spike.addOrReplaceChild("tail_spike_a", CubeListBuilder.create()
                .texOffs(118, 215).addBox(-1.5F, -4.2F, -1.5F, 3, 4, 3, NONE),
                PartPose.offsetAndRotation(-2F, 0F, 0.5F, 0.175F, 0F, -0.593F));
        PartDefinition p_tail_spike_b = p_tail_spike.addOrReplaceChild("tail_spike_b", CubeListBuilder.create()
                .texOffs(142, 215).addBox(-1F, -3.5F, -1F, 2, 4, 2, NONE),
                PartPose.offsetAndRotation(2F, 0F, -0.5F, -0.262F, 0.349F, 0.524F));
        PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create()
                .texOffs(134, 81).addBox(-4F, -3F, -4.5F, 8, 11, 9, NONE)
                .texOffs(32, 125).addBox(-5F, -4F, -5F, 9, 6, 10, NONE),
                PartPose.offsetAndRotation(-12F, 4F, -9F, -0.175F, 0F, 0.07F));
        PartDefinition p_shin_fr = p_leg_fr.addOrReplaceChild("shin_fr", CubeListBuilder.create()
                .texOffs(192, 142).addBox(-3F, 0F, -3F, 6, 9, 6, NONE),
                PartPose.offsetAndRotation(0F, 8F, 0F, 0.279F, 0F, 0F));
        PartDefinition p_foot_fr = p_shin_fr.addOrReplaceChild("foot_fr", CubeListBuilder.create()
                .texOffs(98, 173).addBox(-4F, 0F, -5F, 8, 3, 8, NONE)
                .texOffs(198, 215).addBox(-3.5F, 1F, -7F, 2, 2, 2, NONE)
                .texOffs(206, 215).addBox(-1F, 1F, -7F, 2, 2, 2, NONE)
                .texOffs(214, 215).addBox(1.5F, 1F, -7F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 9F, -1F, -0.105F, 0F, 0F));
        PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create()
                .texOffs(168, 81).addBox(-4F, -3F, -4.5F, 8, 11, 9, NONE)
                .texOffs(70, 125).addBox(-4F, -4F, -5F, 9, 6, 10, NONE),
                PartPose.offsetAndRotation(12F, 4F, -9F, -0.175F, 0F, -0.07F));
        PartDefinition p_shin_fl = p_leg_fl.addOrReplaceChild("shin_fl", CubeListBuilder.create()
                .texOffs(216, 142).addBox(-3F, 0F, -3F, 6, 9, 6, NONE),
                PartPose.offsetAndRotation(0F, 8F, 0F, 0.279F, 0F, 0F));
        PartDefinition p_foot_fl = p_shin_fl.addOrReplaceChild("foot_fl", CubeListBuilder.create()
                .texOffs(130, 173).addBox(-4F, 0F, -5F, 8, 3, 8, NONE)
                .texOffs(222, 215).addBox(-3.5F, 1F, -7F, 2, 2, 2, NONE)
                .texOffs(230, 215).addBox(-1F, 1F, -7F, 2, 2, 2, NONE)
                .texOffs(238, 215).addBox(1.5F, 1F, -7F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 9F, -1F, -0.105F, 0F, 0F));
        PartDefinition p_leg_br = root.addOrReplaceChild("leg_br", CubeListBuilder.create()
                .texOffs(202, 81).addBox(-4F, -3F, -4.5F, 8, 11, 9, NONE)
                .texOffs(108, 125).addBox(-5F, -4F, -5F, 9, 6, 10, NONE),
                PartPose.offsetAndRotation(-12F, 4F, 13F, -0.175F, 0F, 0.07F));
        PartDefinition p_shin_br = p_leg_br.addOrReplaceChild("shin_br", CubeListBuilder.create()
                .texOffs(0, 158).addBox(-3F, 0F, -3F, 6, 9, 6, NONE),
                PartPose.offsetAndRotation(0F, 8F, 0F, 0.279F, 0F, 0F));
        PartDefinition p_foot_br = p_shin_br.addOrReplaceChild("foot_br", CubeListBuilder.create()
                .texOffs(162, 173).addBox(-4F, 0F, -5F, 8, 3, 8, NONE)
                .texOffs(246, 215).addBox(-3.5F, 1F, -7F, 2, 2, 2, NONE)
                .texOffs(0, 222).addBox(-1F, 1F, -7F, 2, 2, 2, NONE)
                .texOffs(8, 222).addBox(1.5F, 1F, -7F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 9F, -1F, -0.105F, 0F, 0F));
        PartDefinition p_leg_bl = root.addOrReplaceChild("leg_bl", CubeListBuilder.create()
                .texOffs(0, 105).addBox(-4F, -3F, -4.5F, 8, 11, 9, NONE)
                .texOffs(146, 125).addBox(-4F, -4F, -5F, 9, 6, 10, NONE),
                PartPose.offsetAndRotation(12F, 4F, 13F, -0.175F, 0F, -0.07F));
        PartDefinition p_shin_bl = p_leg_bl.addOrReplaceChild("shin_bl", CubeListBuilder.create()
                .texOffs(24, 158).addBox(-3F, 0F, -3F, 6, 9, 6, NONE),
                PartPose.offsetAndRotation(0F, 8F, 0F, 0.279F, 0F, 0F));
        PartDefinition p_foot_bl = p_shin_bl.addOrReplaceChild("foot_bl", CubeListBuilder.create()
                .texOffs(194, 173).addBox(-4F, 0F, -5F, 8, 3, 8, NONE)
                .texOffs(16, 222).addBox(-3.5F, 1F, -7F, 2, 2, 2, NONE)
                .texOffs(24, 222).addBox(-1F, 1F, -7F, 2, 2, 2, NONE)
                .texOffs(32, 222).addBox(1.5F, 1F, -7F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 9F, -1F, -0.105F, 0F, 0F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    public static final Role[] SCHRAB_HYDRA_ROLES = {new Role("body", "body", 0.00F), new Role("body/neck_c1", "neck", 0.00F), new Role("body/neck_c1/neck_c2", "neck", 0.60F), new Role("body/neck_c1/neck_c2/neck_c3", "neck", 1.20F), new Role("body/neck_c1/neck_c2/neck_c3/head_c", "head", 0.00F), new Role("body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", "jaw", 0.00F), new Role("body/neck_r1", "neck", 2.10F), new Role("body/neck_r1/neck_r2", "neck", 2.70F), new Role("body/neck_r1/neck_r2/neck_r3", "neck", 3.30F), new Role("body/neck_r1/neck_r2/neck_r3/head_r", "head", 0.00F), new Role("body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", "jaw", 0.00F), new Role("body/neck_l1", "neck", 4.20F), new Role("body/neck_l1/neck_l2", "neck", 4.80F), new Role("body/neck_l1/neck_l2/neck_l3", "neck", 5.40F), new Role("body/neck_l1/neck_l2/neck_l3/head_l", "head", 0.00F), new Role("body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", "jaw", 0.00F), new Role("body/tail_1", "chain", 1.00F), new Role("body/tail_1/tail_2", "chain", 2.00F), new Role("body/tail_1/tail_2/tail_3", "chain", 3.00F), new Role("body/tail_1/tail_2/tail_3/tail_4", "chain", 4.00F), new Role("body/tail_1/tail_2/tail_3/tail_4/tail_tip", "chain", 5.00F), new Role("leg_fr", "leg_a", 0.00F), new Role("leg_fl", "leg_b", 0.00F), new Role("leg_br", "leg_b", 0.00F), new Role("leg_bl", "leg_a", 0.00F)};

    public static final Key[] SCHRAB_HYDRA_POSES = {
            new Key(0, 0F, "body/neck_c1", 40F, 0F, 0F),
            new Key(0, 0F, "body/neck_r1", 40F, 0F, 0F),
            new Key(0, 0F, "body/neck_l1", 40F, 0F, 0F),
            new Key(0, 0F, "body/neck_c1/neck_c2", 25F, 0F, 0F),
            new Key(0, 0F, "body/neck_r1/neck_r2", 25F, 0F, 0F),
            new Key(0, 0F, "body/neck_l1/neck_l2", 25F, 0F, 0F),
            new Key(0, 0F, "body/neck_c1/neck_c2/neck_c3/head_c", 15F, 0F, 0F),
            new Key(0, 0F, "body/neck_r1/neck_r2/neck_r3/head_r", 15F, 0F, 0F),
            new Key(0, 0F, "body/neck_l1/neck_l2/neck_l3/head_l", 15F, 0F, 0F),
            new Key(0, 0F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", -5F, 0F, 0F),
            new Key(0, 0F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", -5F, 0F, 0F),
            new Key(0, 0F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", -5F, 0F, 0F),
            new Key(0, 0F, "body", 5F, 0F, 0F),
            new Key(0, 0F, "body/tail_1", 0F, 22F, 0F),
            new Key(0, 0F, "body/tail_1/tail_2", 0F, 22F, 0F),
            new Key(0, 0F, "body/tail_1/tail_2/tail_3", 0F, 22F, 0F),
            new Key(0, 0F, "body/tail_1/tail_2/tail_3/tail_4", 0F, 22F, 0F),
            new Key(1, 0F, "body/neck_c1", 0F, 0F, 0F),
            new Key(1, 0F, "body/neck_r1", 0F, 0F, 0F),
            new Key(1, 0F, "body/neck_l1", 0F, 0F, 0F),
            new Key(1, 8F, "body/neck_c1", -25F, 0F, 0F),
            new Key(1, 8F, "body/neck_r1", -25F, 0F, 0F),
            new Key(1, 8F, "body/neck_l1", -25F, 0F, 0F),
            new Key(1, 16F, "body/neck_c1", -25F, 0F, 0F),
            new Key(1, 16F, "body/neck_r1", -25F, 0F, 0F),
            new Key(1, 16F, "body/neck_l1", -25F, 0F, 0F),
            new Key(1, 19F, "body/neck_c1", 12F, 0F, 0F),
            new Key(1, 19F, "body/neck_r1", 12F, 0F, 0F),
            new Key(1, 19F, "body/neck_l1", 12F, 0F, 0F),
            new Key(1, 24F, "body/neck_c1", 0F, 0F, 0F),
            new Key(1, 24F, "body/neck_r1", 0F, 0F, 0F),
            new Key(1, 24F, "body/neck_l1", 0F, 0F, 0F),
            new Key(1, 0F, "body/neck_c1/neck_c2/neck_c3/head_c", 0F, 0F, 0F),
            new Key(1, 0F, "body/neck_r1/neck_r2/neck_r3/head_r", 0F, 0F, 0F),
            new Key(1, 0F, "body/neck_l1/neck_l2/neck_l3/head_l", 0F, 0F, 0F),
            new Key(1, 8F, "body/neck_c1/neck_c2/neck_c3/head_c", -20F, 0F, 0F),
            new Key(1, 8F, "body/neck_r1/neck_r2/neck_r3/head_r", -20F, 0F, 0F),
            new Key(1, 8F, "body/neck_l1/neck_l2/neck_l3/head_l", -20F, 0F, 0F),
            new Key(1, 16F, "body/neck_c1/neck_c2/neck_c3/head_c", -15F, 0F, 0F),
            new Key(1, 16F, "body/neck_r1/neck_r2/neck_r3/head_r", -15F, 0F, 0F),
            new Key(1, 16F, "body/neck_l1/neck_l2/neck_l3/head_l", -15F, 0F, 0F),
            new Key(1, 19F, "body/neck_c1/neck_c2/neck_c3/head_c", 5F, 0F, 0F),
            new Key(1, 19F, "body/neck_r1/neck_r2/neck_r3/head_r", 5F, 0F, 0F),
            new Key(1, 19F, "body/neck_l1/neck_l2/neck_l3/head_l", 5F, 0F, 0F),
            new Key(1, 24F, "body/neck_c1/neck_c2/neck_c3/head_c", 0F, 0F, 0F),
            new Key(1, 24F, "body/neck_r1/neck_r2/neck_r3/head_r", 0F, 0F, 0F),
            new Key(1, 24F, "body/neck_l1/neck_l2/neck_l3/head_l", 0F, 0F, 0F),
            new Key(1, 0F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 0F, 0F, 0F),
            new Key(1, 0F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 0F, 0F, 0F),
            new Key(1, 0F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 0F, 0F, 0F),
            new Key(1, 8F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 20F, 0F, 0F),
            new Key(1, 8F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 20F, 0F, 0F),
            new Key(1, 8F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 20F, 0F, 0F),
            new Key(1, 16F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 35F, 0F, 0F),
            new Key(1, 16F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 35F, 0F, 0F),
            new Key(1, 16F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 35F, 0F, 0F),
            new Key(1, 19F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 45F, 0F, 0F),
            new Key(1, 19F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 45F, 0F, 0F),
            new Key(1, 19F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 45F, 0F, 0F),
            new Key(1, 24F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 0F, 0F, 0F),
            new Key(1, 24F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 0F, 0F, 0F),
            new Key(1, 24F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 0F, 0F, 0F),
            new Key(1, 0F, "body", 0F, 0F, 0F),
            new Key(1, 8F, "body", -5F, 0F, 0F),
            new Key(1, 19F, "body", 3F, 0F, 0F),
            new Key(1, 24F, "body", 0F, 0F, 0F),
            new Key(2, 0F, "body", 0F, 0F, 0F),
            new Key(2, 4F, "body", -9F, 0F, 0F),
            new Key(2, 6F, "body", 5F, 0F, 0F),
            new Key(2, 12F, "body", 0F, 0F, 0F),
            new Key(2, 0F, "body/neck_c1", 0F, 0F, 0F),
            new Key(2, 0F, "body/neck_r1", 0F, 0F, 0F),
            new Key(2, 0F, "body/neck_l1", 0F, 0F, 0F),
            new Key(2, 4F, "body/neck_c1", -20F, 0F, 0F),
            new Key(2, 4F, "body/neck_r1", -20F, 0F, 0F),
            new Key(2, 4F, "body/neck_l1", -20F, 0F, 0F),
            new Key(2, 6F, "body/neck_c1", 15F, 0F, 0F),
            new Key(2, 6F, "body/neck_r1", 15F, 0F, 0F),
            new Key(2, 6F, "body/neck_l1", 15F, 0F, 0F),
            new Key(2, 12F, "body/neck_c1", 0F, 0F, 0F),
            new Key(2, 12F, "body/neck_r1", 0F, 0F, 0F),
            new Key(2, 12F, "body/neck_l1", 0F, 0F, 0F),
            new Key(2, 0F, "body/neck_c1/neck_c2/neck_c3/head_c", 0F, 0F, 0F),
            new Key(2, 0F, "body/neck_r1/neck_r2/neck_r3/head_r", 0F, 0F, 0F),
            new Key(2, 0F, "body/neck_l1/neck_l2/neck_l3/head_l", 0F, 0F, 0F),
            new Key(2, 4F, "body/neck_c1/neck_c2/neck_c3/head_c", -30F, 0F, 0F),
            new Key(2, 4F, "body/neck_r1/neck_r2/neck_r3/head_r", -30F, 0F, 0F),
            new Key(2, 4F, "body/neck_l1/neck_l2/neck_l3/head_l", -30F, 0F, 0F),
            new Key(2, 6F, "body/neck_c1/neck_c2/neck_c3/head_c", 10F, 0F, 0F),
            new Key(2, 6F, "body/neck_r1/neck_r2/neck_r3/head_r", 10F, 0F, 0F),
            new Key(2, 6F, "body/neck_l1/neck_l2/neck_l3/head_l", 10F, 0F, 0F),
            new Key(2, 12F, "body/neck_c1/neck_c2/neck_c3/head_c", 0F, 0F, 0F),
            new Key(2, 12F, "body/neck_r1/neck_r2/neck_r3/head_r", 0F, 0F, 0F),
            new Key(2, 12F, "body/neck_l1/neck_l2/neck_l3/head_l", 0F, 0F, 0F),
            new Key(2, 0F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 0F, 0F, 0F),
            new Key(2, 0F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 0F, 0F, 0F),
            new Key(2, 0F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 0F, 0F, 0F),
            new Key(2, 4F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 30F, 0F, 0F),
            new Key(2, 4F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 30F, 0F, 0F),
            new Key(2, 4F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 30F, 0F, 0F),
            new Key(2, 6F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 40F, 0F, 0F),
            new Key(2, 6F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 40F, 0F, 0F),
            new Key(2, 6F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 40F, 0F, 0F),
            new Key(2, 12F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 0F, 0F, 0F),
            new Key(2, 12F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 0F, 0F, 0F),
            new Key(2, 12F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 0F, 0F, 0F),
            new Key(3, 0F, "body/neck_c1/neck_c2/neck_c3/head_c", 0F, 0F, 0F),
            new Key(3, 0F, "body/neck_r1/neck_r2/neck_r3/head_r", 0F, 0F, 0F),
            new Key(3, 0F, "body/neck_l1/neck_l2/neck_l3/head_l", 0F, 0F, 0F),
            new Key(3, 3F, "body/neck_c1/neck_c2/neck_c3/head_c", 25F, 0F, 0F),
            new Key(3, 3F, "body/neck_r1/neck_r2/neck_r3/head_r", 25F, 0F, 0F),
            new Key(3, 3F, "body/neck_l1/neck_l2/neck_l3/head_l", 25F, 0F, 0F),
            new Key(3, 8F, "body/neck_c1/neck_c2/neck_c3/head_c", 0F, 0F, 0F),
            new Key(3, 8F, "body/neck_r1/neck_r2/neck_r3/head_r", 0F, 0F, 0F),
            new Key(3, 8F, "body/neck_l1/neck_l2/neck_l3/head_l", 0F, 0F, 0F),
            new Key(3, 0F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 0F, 0F, 0F),
            new Key(3, 0F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 0F, 0F, 0F),
            new Key(3, 0F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 0F, 0F, 0F),
            new Key(3, 3F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 40F, 0F, 0F),
            new Key(3, 3F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 40F, 0F, 0F),
            new Key(3, 3F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 40F, 0F, 0F),
            new Key(3, 8F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 0F, 0F, 0F),
            new Key(3, 8F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 0F, 0F, 0F),
            new Key(3, 8F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 0F, 0F, 0F),
            new Key(3, 0F, "body/neck_c1", 0F, 0F, 0F),
            new Key(3, 0F, "body/neck_r1", 0F, 0F, 0F),
            new Key(3, 0F, "body/neck_l1", 0F, 0F, 0F),
            new Key(3, 3F, "body/neck_c1", 10F, 0F, 0F),
            new Key(3, 3F, "body/neck_r1", 10F, 0F, 0F),
            new Key(3, 3F, "body/neck_l1", 10F, 0F, 0F),
            new Key(3, 8F, "body/neck_c1", 0F, 0F, 0F),
            new Key(3, 8F, "body/neck_r1", 0F, 0F, 0F),
            new Key(3, 8F, "body/neck_l1", 0F, 0F, 0F),
            new Key(4, 0F, "body/neck_c1", 0F, 0F, 0F),
            new Key(4, 5F, "body/neck_c1", -30F, 0F, 0F),
            new Key(4, 8F, "body/neck_c1", 40F, 0F, 0F),
            new Key(4, 13F, "body/neck_c1", 0F, 0F, 0F),
            new Key(4, 0F, "body/neck_c1/neck_c2", 0F, 0F, 0F),
            new Key(4, 5F, "body/neck_c1/neck_c2", -10F, 0F, 0F),
            new Key(4, 8F, "body/neck_c1/neck_c2", 20F, 0F, 0F),
            new Key(4, 13F, "body/neck_c1/neck_c2", 0F, 0F, 0F),
            new Key(4, 0F, "body/neck_c1/neck_c2/neck_c3/head_c", 0F, 0F, 0F),
            new Key(4, 5F, "body/neck_c1/neck_c2/neck_c3/head_c", -25F, 0F, 0F),
            new Key(4, 8F, "body/neck_c1/neck_c2/neck_c3/head_c", 15F, 0F, 0F),
            new Key(4, 13F, "body/neck_c1/neck_c2/neck_c3/head_c", 0F, 0F, 0F),
            new Key(4, 0F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 0F, 0F, 0F),
            new Key(4, 5F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 50F, 0F, 0F),
            new Key(4, 8F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 5F, 0F, 0F),
            new Key(4, 13F, "body/neck_c1/neck_c2/neck_c3/head_c/jaw_c", 0F, 0F, 0F),
            new Key(4, 0F, "body/neck_r1", 0F, 0F, 0F),
            new Key(4, 5F, "body/neck_r1", -15F, 0F, -10F),
            new Key(4, 8F, "body/neck_r1", 20F, 0F, 5F),
            new Key(4, 13F, "body/neck_r1", 0F, 0F, 0F),
            new Key(4, 0F, "body/neck_l1", 0F, 0F, 0F),
            new Key(4, 5F, "body/neck_l1", -15F, 0F, 10F),
            new Key(4, 8F, "body/neck_l1", 20F, 0F, -5F),
            new Key(4, 13F, "body/neck_l1", 0F, 0F, 0F),
            new Key(4, 0F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 0F, 0F, 0F),
            new Key(4, 5F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 30F, 0F, 0F),
            new Key(4, 8F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 10F, 0F, 0F),
            new Key(4, 13F, "body/neck_r1/neck_r2/neck_r3/head_r/jaw_r", 0F, 0F, 0F),
            new Key(4, 0F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 0F, 0F, 0F),
            new Key(4, 5F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 30F, 0F, 0F),
            new Key(4, 8F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 10F, 0F, 0F),
            new Key(4, 13F, "body/neck_l1/neck_l2/neck_l3/head_l/jaw_l", 0F, 0F, 0F),
            new Key(4, 0F, "body", 0F, 0F, 0F),
            new Key(4, 5F, "body", -6F, 0F, 0F),
            new Key(4, 8F, "body", 6F, 0F, 0F),
            new Key(4, 13F, "body", 0F, 0F, 0F)};

    public static final float SCHRAB_HYDRA_SCALE = 1.1F;

    /** Abyssal Leviathan: Tiefsee-Anglerfisch mit riesigem Unterbiss voller Knochenzaehne, leuchtendem Koeder am Stirnstiel, biolumineszenten Punkten, Brustflossen, Rueckenstacheln und schlaengelndem Schwanz. Textur {@code entity/stage/abyssal_leviathan.png}. */
    public static LayerDefinition abyssalLeviathan() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(132, 0).addBox(-11F, -9F, -5F, 22, 16, 13, NONE)
                .texOffs(70, 0).addBox(-9F, -11F, -5F, 18, 20, 13, NONE)
                .texOffs(0, 0).addBox(-9F, -9F, -7F, 18, 16, 17, NONE)
                .texOffs(0, 128).addBox(-9F, 8F, -5F, 18, 3, 12, NONE)
                .texOffs(194, 94).addBox(-8F, 7F, -5F, 16, 5, 12, NONE)
                .texOffs(74, 94).addBox(-8F, 8F, -6F, 16, 3, 14, NONE)
                .texOffs(172, 128).addBox(-7F, -13F, -4F, 14, 3, 12, NONE)
                .texOffs(112, 111).addBox(-8F, -8F, 9F, 16, 14, 2, NONE)
                .texOffs(58, 157).addBox(-11.5F, -6F, -6F, 1, 8, 4, NONE)
                .texOffs(68, 157).addBox(10.5F, -6F, -6F, 1, 8, 4, NONE)
                .texOffs(228, 202).addBox(-11.6F, -2F, 0F, 1, 1, 1, NONE)
                .texOffs(232, 202).addBox(10.6F, -2F, 0F, 1, 1, 1, NONE)
                .texOffs(236, 202).addBox(-11.6F, 1F, 3F, 1, 1, 1, NONE)
                .texOffs(240, 202).addBox(10.6F, 1F, 3F, 1, 1, 1, NONE)
                .texOffs(244, 202).addBox(-11.6F, 4F, 6F, 1, 1, 1, NONE)
                .texOffs(248, 202).addBox(10.6F, 4F, 6F, 1, 1, 1, NONE)
                .texOffs(252, 202).addBox(-11.1F, -6F, 4F, 1, 1, 1, NONE)
                .texOffs(0, 205).addBox(10.1F, -6F, 4F, 1, 1, 1, NONE)
                .texOffs(4, 205).addBox(-9.6F, 9F, -2F, 1, 1, 1, NONE)
                .texOffs(8, 205).addBox(8.6F, 9F, -2F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 4F, 0F, 0F, 0F, 0F));
        PartDefinition p_head = p_body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(134, 94).addBox(-11F, -9F, -9F, 22, 9, 8, NONE)
                .texOffs(60, 57).addBox(-10F, -10F, -9F, 20, 11, 8, NONE)
                .texOffs(0, 57).addBox(-10F, -9F, -10F, 20, 9, 10, NONE)
                .texOffs(30, 143).addBox(-11F, -6F, -16F, 22, 6, 7, NONE)
                .texOffs(118, 128).addBox(-10F, -7F, -16F, 20, 8, 7, NONE)
                .texOffs(60, 128).addBox(-10F, -6F, -17F, 20, 6, 9, NONE)
                .texOffs(166, 169).addBox(-8.5F, -12F, -9F, 17, 2, 7, NONE)
                .texOffs(134, 157).addBox(-7.5F, -13F, -9F, 15, 4, 7, NONE)
                .texOffs(86, 157).addBox(-7.5F, -12F, -10F, 15, 2, 9, NONE)
                .texOffs(0, 76).addBox(-11.5F, 0F, -18F, 23, 2, 16, NONE)
                .texOffs(148, 111).addBox(-10F, 2F, -17F, 20, 1, 14, NONE)
                .texOffs(174, 196).addBox(-11.6F, -5F, -14F, 1, 2, 2, NONE)
                .texOffs(180, 196).addBox(10.6F, -5F, -14F, 1, 2, 2, NONE)
                .texOffs(208, 179).addBox(-11.3F, -6F, -15F, 1, 4, 4, NONE)
                .texOffs(218, 179).addBox(10.3F, -6F, -15F, 1, 4, 4, NONE)
                .texOffs(12, 205).addBox(-12.1F, -2F, -11F, 1, 1, 1, NONE)
                .texOffs(16, 205).addBox(11.1F, -2F, -11F, 1, 1, 1, NONE)
                .texOffs(20, 205).addBox(-12.1F, -4F, -6F, 1, 1, 1, NONE)
                .texOffs(24, 205).addBox(11.1F, -4F, -6F, 1, 1, 1, NONE)
                .texOffs(28, 205).addBox(-9.6F, -10F, -5F, 1, 1, 1, NONE)
                .texOffs(32, 205).addBox(8.6F, -10F, -5F, 1, 1, 1, NONE)
                .texOffs(198, 196).addBox(-9F, 2F, -17.5F, 1, 3, 1, NONE)
                .texOffs(62, 196).addBox(-6F, 2F, -17.5F, 1, 4, 1, NONE)
                .texOffs(202, 196).addBox(-3F, 2F, -17.5F, 1, 3, 1, NONE)
                .texOffs(206, 196).addBox(2F, 2F, -17.5F, 1, 3, 1, NONE)
                .texOffs(66, 196).addBox(5F, 2F, -17.5F, 1, 4, 1, NONE)
                .texOffs(210, 196).addBox(8F, 2F, -17.5F, 1, 3, 1, NONE)
                .texOffs(214, 196).addBox(-10.5F, 2F, -13F, 1, 3, 1, NONE)
                .texOffs(218, 196).addBox(9.5F, 2F, -13F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, -5F, -0.105F, 0F, 0F));
        PartDefinition p_jaw = p_head.addOrReplaceChild("jaw", CubeListBuilder.create()
                .texOffs(164, 33).addBox(-12.5F, 1F, -19F, 25, 5, 17, NONE)
                .texOffs(84, 33).addBox(-11.5F, 0F, -19F, 23, 7, 17, NONE)
                .texOffs(0, 33).addBox(-11.5F, 1F, -20F, 23, 5, 19, NONE)
                .texOffs(40, 111).addBox(-11F, 7F, -17F, 22, 2, 14, NONE)
                .texOffs(150, 76).addBox(-10F, 6F, -17F, 20, 4, 14, NONE)
                .texOffs(78, 76).addBox(-10F, 7F, -18F, 20, 2, 16, NONE)
                .texOffs(0, 94).addBox(-10.5F, -1F, -18.5F, 21, 1, 16, NONE)
                .texOffs(40, 169).addBox(-3F, -1.5F, -15F, 6, 1, 9, new CubeDeformation(0.25F))
                .texOffs(4, 188).addBox(-8F, 1F, -21.5F, 16, 5, 2, NONE)
                .texOffs(144, 188).addBox(-4F, 6F, -20F, 8, 3, 3, NONE)
                .texOffs(36, 205).addBox(-13.1F, 3F, -16F, 1, 1, 1, NONE)
                .texOffs(40, 205).addBox(12.1F, 3F, -16F, 1, 1, 1, NONE)
                .texOffs(44, 205).addBox(-13.1F, 4F, -11F, 1, 1, 1, NONE)
                .texOffs(48, 205).addBox(12.1F, 4F, -11F, 1, 1, 1, NONE)
                .texOffs(52, 205).addBox(-13.1F, 3F, -6F, 1, 1, 1, NONE)
                .texOffs(56, 205).addBox(12.1F, 3F, -6F, 1, 1, 1, NONE)
                .texOffs(60, 205).addBox(-0.5F, 2.5F, -22F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 1F, -3F, 0.209F, 0F, 0F));
        PartDefinition p_teeth_lf = p_jaw.addOrReplaceChild("teeth_lf", CubeListBuilder.create()
                .texOffs(238, 188).addBox(-11F, -5F, 0F, 1, 5, 1, NONE)
                .texOffs(142, 179).addBox(-8.5F, -7F, 0F, 2, 7, 2, NONE)
                .texOffs(70, 196).addBox(-4.5F, -4F, 0F, 1, 4, 1, NONE)
                .texOffs(228, 179).addBox(-2.5F, -6F, 0F, 2, 6, 2, NONE)
                .texOffs(236, 179).addBox(0.5F, -6F, 0F, 2, 6, 2, NONE)
                .texOffs(74, 196).addBox(3.5F, -4F, 0F, 1, 4, 1, NONE)
                .texOffs(150, 179).addBox(6.5F, -7F, 0F, 2, 7, 2, NONE)
                .texOffs(242, 188).addBox(10F, -5F, 0F, 1, 5, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, -19F, 0.105F, 0F, 0F));
        PartDefinition p_teeth_lr = p_jaw.addOrReplaceChild("teeth_lr", CubeListBuilder.create()
                .texOffs(246, 188).addBox(-0.5F, -5F, -7F, 1, 5, 1, NONE)
                .texOffs(222, 196).addBox(-0.5F, -3F, -3.5F, 1, 3, 1, NONE)
                .texOffs(78, 196).addBox(-0.5F, -4F, 0F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(-11.5F, 0F, -10F, 0F, 0F, -0.244F));
        PartDefinition p_teeth_ll = p_jaw.addOrReplaceChild("teeth_ll", CubeListBuilder.create()
                .texOffs(250, 188).addBox(-0.5F, -5F, -7F, 1, 5, 1, NONE)
                .texOffs(226, 196).addBox(-0.5F, -3F, -3.5F, 1, 3, 1, NONE)
                .texOffs(82, 196).addBox(-0.5F, -4F, 0F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(11.5F, 0F, -10F, 0F, 0F, 0.244F));
        PartDefinition p_barbel_r = p_jaw.addOrReplaceChild("barbel_r", CubeListBuilder.create()
                .texOffs(132, 188).addBox(-0.5F, 0F, -0.5F, 1, 6, 1, NONE)
                .texOffs(118, 196).addBox(-1F, 6F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(-5F, 9F, -17F, 0.262F, 0F, 0.209F));
        PartDefinition p_barbel_l = p_jaw.addOrReplaceChild("barbel_l", CubeListBuilder.create()
                .texOffs(136, 188).addBox(-0.5F, 0F, -0.5F, 1, 6, 1, NONE)
                .texOffs(126, 196).addBox(-1F, 6F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(5F, 9F, -17F, 0.262F, 0F, -0.209F));
        PartDefinition p_forehead = p_head.addOrReplaceChild("forehead", CubeListBuilder.create()
                .texOffs(0, 169).addBox(-9F, -9F, 0F, 18, 8, 2, NONE)
                .texOffs(202, 143).addBox(-8F, -10F, 0F, 16, 10, 2, NONE)
                .texOffs(162, 143).addBox(-8F, -9F, -1F, 16, 8, 4, NONE),
                PartPose.offsetAndRotation(0F, -6F, -17F, -0.908F, 0F, 0F));
        PartDefinition p_lure_base = p_head.addOrReplaceChild("lure_base", CubeListBuilder.create()
                .texOffs(46, 157).addBox(-1.5F, -9F, -1.5F, 3, 9, 3, NONE)
                .texOffs(64, 188).addBox(-2.5F, -1F, -2.5F, 5, 2, 5, NONE)
                .texOffs(232, 188).addBox(-0.5F, -6F, 1.5F, 1, 4, 2, NONE),
                PartPose.offsetAndRotation(0F, -14F, -10F, 0.244F, 0F, 0F));
        PartDefinition p_lure_mid = p_lure_base.addOrReplaceChild("lure_mid", CubeListBuilder.create()
                .texOffs(78, 157).addBox(-1F, -10F, -1F, 2, 10, 2, NONE)
                .texOffs(94, 196).addBox(-1.5F, -1F, -1.5F, 3, 1, 3, NONE)
                .texOffs(230, 196).addBox(-0.5F, -7F, 1F, 1, 3, 1, NONE)
                .texOffs(48, 202).addBox(-0.5F, -4F, 1F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -9F, 0F, 0.663F, 0F, 0F));
        PartDefinition p_lure_tip = p_lure_mid.addOrReplaceChild("lure_tip", CubeListBuilder.create()
                .texOffs(140, 188).addBox(-0.5F, -6F, -0.5F, 1, 6, 1, new CubeDeformation(0.15F))
                .texOffs(10, 202).addBox(-1F, -1F, -1F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, -10F, 0F, 0.873F, 0F, 0F));
        PartDefinition p_lure_bulb = p_lure_tip.addOrReplaceChild("lure_bulb", CubeListBuilder.create()
                .texOffs(178, 179).addBox(-2F, -2F, -2F, 4, 4, 4, new CubeDeformation(0.6F))
                .texOffs(106, 196).addBox(-1.5F, -4F, -1.5F, 3, 1, 3, NONE)
                .texOffs(186, 196).addBox(-3F, -1F, -1F, 1, 2, 2, NONE)
                .texOffs(192, 196).addBox(2F, -1F, -1F, 1, 2, 2, NONE)
                .texOffs(42, 202).addBox(-1F, -1F, 2.2F, 2, 2, 1, NONE)
                .texOffs(86, 196).addBox(-0.5F, 2.5F, -0.5F, 1, 4, 1, NONE)
                .texOffs(134, 196).addBox(-1F, 6.5F, -1F, 2, 2, 2, NONE)
                .texOffs(234, 196).addBox(-2F, 2.5F, 0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, -6F, 0F, -1.309F, 0F, 0F));
        PartDefinition p_brow_r = p_head.addOrReplaceChild("brow_r", CubeListBuilder.create()
                .texOffs(70, 169).addBox(-2F, -2F, -4F, 4, 2, 8, NONE)
                .texOffs(8, 196).addBox(-1.5F, -3F, 2F, 3, 1, 4, NONE),
                PartPose.offsetAndRotation(-9F, -7F, -13F, 0F, 0.314F, -0.349F));
        PartDefinition p_brow_l = p_head.addOrReplaceChild("brow_l", CubeListBuilder.create()
                .texOffs(94, 169).addBox(-2F, -2F, -4F, 4, 2, 8, NONE)
                .texOffs(22, 196).addBox(-1.5F, -3F, 2F, 3, 1, 4, NONE),
                PartPose.offsetAndRotation(9F, -7F, -13F, 0F, -0.314F, 0.349F));
        PartDefinition p_cheek_r = p_head.addOrReplaceChild("cheek_r", CubeListBuilder.create()
                .texOffs(106, 179).addBox(-1F, -1F, 0F, 2, 2, 7, NONE)
                .texOffs(36, 196).addBox(-0.5F, -0.5F, 7F, 1, 1, 4, NONE)
                .texOffs(84, 188).addBox(-0.5F, 2F, 1F, 1, 1, 6, NONE),
                PartPose.offsetAndRotation(-11F, -2F, -7F, 0F, 0.698F, 0.209F));
        PartDefinition p_cheek_l = p_head.addOrReplaceChild("cheek_l", CubeListBuilder.create()
                .texOffs(124, 179).addBox(-1F, -1F, 0F, 2, 2, 7, NONE)
                .texOffs(46, 196).addBox(-0.5F, -0.5F, 7F, 1, 1, 4, NONE)
                .texOffs(98, 188).addBox(-0.5F, 2F, 1F, 1, 1, 6, NONE),
                PartPose.offsetAndRotation(11F, -2F, -7F, 0F, -0.698F, -0.209F));
        PartDefinition p_dorsal = p_body.addOrReplaceChild("dorsal", CubeListBuilder.create()
                .texOffs(226, 157).addBox(-0.5F, -10F, 0F, 1, 10, 1, NONE)
                .texOffs(154, 169).addBox(-0.5F, -8F, 1F, 1, 8, 2, NONE)
                .texOffs(18, 202).addBox(-1F, -11F, -0.5F, 2, 1, 2, NONE)
                .texOffs(158, 143).addBox(-0.5F, -12F, 3F, 1, 12, 1, NONE)
                .texOffs(160, 169).addBox(-0.5F, -8F, 4F, 1, 8, 2, NONE)
                .texOffs(230, 157).addBox(-0.5F, -10F, 6F, 1, 10, 1, NONE)
                .texOffs(244, 179).addBox(-0.5F, -6F, 7F, 1, 6, 2, NONE)
                .texOffs(26, 202).addBox(-1F, -11F, 5.5F, 2, 1, 2, NONE)
                .texOffs(174, 179).addBox(-0.5F, -8F, 9F, 1, 8, 1, NONE)
                .texOffs(56, 196).addBox(-0.5F, -3F, 10F, 1, 3, 2, NONE)
                .texOffs(0, 196).addBox(-0.5F, -5F, 12F, 1, 5, 1, NONE)
                .texOffs(34, 202).addBox(-1F, -6F, 11.5F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, -12F, -3F, -0.663F, 0F, 0F));
        PartDefinition p_fin_r = p_body.addOrReplaceChild("fin_r", CubeListBuilder.create()
                .texOffs(118, 169).addBox(-3F, -2F, -2F, 3, 4, 6, NONE)
                .texOffs(214, 169).addBox(-14F, -0.5F, -1F, 11, 1, 8, NONE)
                .texOffs(166, 188).addBox(-19F, -0.5F, 0F, 5, 1, 5, NONE)
                .texOffs(238, 196).addBox(-22F, -0.5F, 1F, 3, 1, 2, NONE)
                .texOffs(52, 202).addBox(-20F, -1F, 0F, 17, 1, 1, NONE)
                .texOffs(124, 202).addBox(-18F, -1F, 3F, 15, 1, 1, NONE)
                .texOffs(188, 202).addBox(-12F, -1F, 6F, 9, 1, 1, NONE)
                .texOffs(142, 196).addBox(-23F, -1F, 0.5F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(-10.5F, 4F, -1F, 0F, 0.489F, -0.384F));
        PartDefinition p_fin_l = p_body.addOrReplaceChild("fin_l", CubeListBuilder.create()
                .texOffs(136, 169).addBox(0F, -2F, -2F, 3, 4, 6, NONE)
                .texOffs(0, 179).addBox(3F, -0.5F, -1F, 11, 1, 8, NONE)
                .texOffs(186, 188).addBox(14F, -0.5F, 0F, 5, 1, 5, NONE)
                .texOffs(0, 202).addBox(19F, -0.5F, 1F, 3, 1, 2, NONE)
                .texOffs(88, 202).addBox(3F, -1F, 0F, 17, 1, 1, NONE)
                .texOffs(156, 202).addBox(3F, -1F, 3F, 15, 1, 1, NONE)
                .texOffs(208, 202).addBox(3F, -1F, 6F, 9, 1, 1, NONE)
                .texOffs(150, 196).addBox(21F, -1F, 0.5F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(10.5F, 4F, -1F, 0F, -0.489F, 0.384F));
        PartDefinition p_pelvic_r = p_body.addOrReplaceChild("pelvic_r", CubeListBuilder.create()
                .texOffs(158, 179).addBox(-1F, 0F, -1F, 1, 6, 3, NONE)
                .texOffs(250, 179).addBox(-1.5F, 0F, -1.5F, 1, 7, 1, NONE),
                PartPose.offsetAndRotation(-6F, 8F, 2F, 0.436F, 0.349F, -0.611F));
        PartDefinition p_pelvic_l = p_body.addOrReplaceChild("pelvic_l", CubeListBuilder.create()
                .texOffs(166, 179).addBox(0F, 0F, -1F, 1, 6, 3, NONE)
                .texOffs(0, 188).addBox(0.5F, 0F, -1.5F, 1, 7, 1, NONE),
                PartPose.offsetAndRotation(6F, 8F, 2F, 0.436F, -0.349F, 0.611F));
        PartDefinition p_tail1 = p_body.addOrReplaceChild("tail1", CubeListBuilder.create()
                .texOffs(0, 111).addBox(-6.5F, -5.5F, 0F, 13, 10, 7, NONE)
                .texOffs(156, 57).addBox(-5.5F, -6.5F, 0F, 11, 12, 7, NONE)
                .texOffs(116, 57).addBox(-5.5F, -5.5F, -1F, 11, 10, 9, NONE)
                .texOffs(38, 179).addBox(-5F, -8F, 0F, 10, 2, 7, NONE)
                .texOffs(72, 179).addBox(-5F, 5F, 0F, 10, 2, 7, NONE)
                .texOffs(4, 196).addBox(-0.5F, -12F, 1F, 1, 5, 1, NONE)
                .texOffs(112, 188).addBox(-0.5F, -11F, 2F, 1, 3, 4, NONE)
                .texOffs(64, 205).addBox(-7.1F, -2F, 1F, 1, 1, 1, NONE)
                .texOffs(68, 205).addBox(6.1F, -2F, 1F, 1, 1, 1, NONE)
                .texOffs(72, 205).addBox(-7.1F, 1F, 4F, 1, 1, 1, NONE)
                .texOffs(76, 205).addBox(6.1F, 1F, 4F, 1, 1, 1, NONE)
                .texOffs(80, 205).addBox(-7.1F, -4F, 6F, 1, 1, 1, NONE)
                .texOffs(84, 205).addBox(6.1F, -4F, 6F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 1F, 10F, 0.07F, 0F, 0F));
        PartDefinition p_tail2 = p_tail1.addOrReplaceChild("tail2", CubeListBuilder.create()
                .texOffs(178, 157).addBox(-4F, -3F, 1F, 8, 6, 5, NONE)
                .texOffs(114, 143).addBox(-3F, -4F, 1F, 6, 8, 5, NONE)
                .texOffs(88, 143).addBox(-3F, -3F, 0F, 6, 6, 7, NONE)
                .texOffs(40, 188).addBox(-3F, -5F, 0F, 6, 1, 6, NONE)
                .texOffs(90, 196).addBox(-0.5F, -8F, 1F, 1, 4, 1, NONE)
                .texOffs(222, 188).addBox(-0.5F, -7F, 2F, 1, 2, 4, NONE)
                .texOffs(88, 205).addBox(-4.6F, -1F, 2F, 1, 1, 1, NONE)
                .texOffs(92, 205).addBox(3.6F, -1F, 2F, 1, 1, 1, NONE)
                .texOffs(96, 205).addBox(-4.6F, 1F, 5F, 1, 1, 1, NONE)
                .texOffs(100, 205).addBox(3.6F, 1F, 5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0.5F, 7F, 0.07F, 0F, 0F));
        PartDefinition p_tail3 = p_tail2.addOrReplaceChild("tail3", CubeListBuilder.create()
                .texOffs(204, 157).addBox(-2.5F, -2.5F, 0F, 5, 5, 6, NONE)
                .texOffs(206, 188).addBox(-1.5F, -3.5F, 0F, 3, 1, 5, NONE)
                .texOffs(122, 188).addBox(-0.5F, -6F, 1F, 1, 3, 4, NONE),
                PartPose.offsetAndRotation(0F, 0.5F, 6F, 0.052F, 0F, 0F));
        PartDefinition p_caudal = p_tail3.addOrReplaceChild("caudal", CubeListBuilder.create()
                .texOffs(194, 179).addBox(-1.5F, -2F, -1F, 3, 4, 4, NONE)
                .texOffs(26, 157).addBox(-0.5F, -1.5F, 2F, 1, 3, 9, NONE),
                PartPose.offsetAndRotation(0F, 0F, 5F, 0F, 0F, 0F));
        PartDefinition p_caudal_up = p_caudal.addOrReplaceChild("caudal_up", CubeListBuilder.create()
                .texOffs(224, 128).addBox(-0.5F, -1F, 0F, 1, 3, 12, NONE)
                .texOffs(0, 143).addBox(-1F, -1.5F, 0F, 2, 1, 13, NONE)
                .texOffs(158, 196).addBox(-1F, -2F, 12.5F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, -1F, 1F, -0.663F, 0F, 0F));
        PartDefinition p_caudal_dn = p_caudal.addOrReplaceChild("caudal_dn", CubeListBuilder.create()
                .texOffs(136, 143).addBox(-0.5F, -2F, 0F, 1, 3, 10, NONE)
                .texOffs(0, 157).addBox(-1F, 0.5F, 0F, 2, 1, 11, NONE)
                .texOffs(166, 196).addBox(-1F, 0F, 10.5F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 1F, 1F, 0.593F, 0F, 0F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    public static final Role[] ABYSSAL_LEVIATHAN_ROLES = {new Role("body", "body", 0.00F), new Role("body/head", "head", 0.00F), new Role("body/head/jaw", "jaw", 0.00F), new Role("body/head/jaw/barbel_r", "tentacle", 0.30F), new Role("body/head/jaw/barbel_l", "tentacle", 0.30F), new Role("body/head/lure_base", "neck", 0.50F), new Role("body/head/lure_base/lure_mid", "tentacle", 1.00F), new Role("body/head/lure_base/lure_mid/lure_tip", "tentacle", 2.00F), new Role("body/fin_r", "wing_r", 0.00F), new Role("body/fin_l", "wing_l", 0.00F), new Role("body/pelvic_r", "tentacle", 1.50F), new Role("body/pelvic_l", "tentacle", 1.50F), new Role("body/tail1", "chain", 1.00F), new Role("body/tail1/tail2", "chain", 2.00F), new Role("body/tail1/tail2/tail3", "chain", 3.00F), new Role("body/tail1/tail2/tail3/caudal", "chain", 4.00F)};

    public static final Key[] ABYSSAL_LEVIATHAN_POSES = {
            new Key(0, 0F, "body", 8F, 0F, 0F),
            new Key(0, 0F, "body/head/lure_base", 40F, 0F, 0F),
            new Key(0, 0F, "body/head/lure_base/lure_mid", 25F, 0F, 0F),
            new Key(0, 0F, "body/head/jaw", -10F, 0F, 0F),
            new Key(0, 0F, "body/fin_r", 0F, -10F, -25F),
            new Key(0, 0F, "body/fin_l", 0F, 10F, 25F),
            new Key(0, 0F, "body/tail1", 0F, 25F, 0F),
            new Key(0, 0F, "body/tail1/tail2", 0F, 25F, 0F),
            new Key(0, 0F, "body/tail1/tail2/tail3", 0F, 20F, 0F),
            new Key(1, 0F, "body/head/lure_base", 0F, 0F, 0F),
            new Key(1, 8F, "body/head/lure_base", -30F, 0F, 0F),
            new Key(1, 18F, "body/head/lure_base", -35F, 0F, 0F),
            new Key(1, 20F, "body/head/lure_base", 25F, 0F, 0F),
            new Key(1, 22F, "body/head/lure_base", 0F, 0F, 0F),
            new Key(1, 0F, "body/head/lure_base/lure_mid", 0F, 0F, 0F),
            new Key(1, 8F, "body/head/lure_base/lure_mid", -25F, 0F, 0F),
            new Key(1, 18F, "body/head/lure_base/lure_mid", -28F, 0F, 0F),
            new Key(1, 20F, "body/head/lure_base/lure_mid", 15F, 0F, 0F),
            new Key(1, 22F, "body/head/lure_base/lure_mid", 0F, 0F, 0F),
            new Key(1, 0F, "body/head", 0F, 0F, 0F),
            new Key(1, 8F, "body/head", -12F, 0F, 0F),
            new Key(1, 18F, "body/head", -14F, 0F, 0F),
            new Key(1, 20F, "body/head", 6F, 0F, 0F),
            new Key(1, 22F, "body/head", 0F, 0F, 0F),
            new Key(1, 0F, "body/fin_r", 0F, 0F, 0F),
            new Key(1, 8F, "body/fin_r", 0F, 0F, 30F),
            new Key(1, 20F, "body/fin_r", 0F, 0F, 30F),
            new Key(1, 22F, "body/fin_r", 0F, 0F, 0F),
            new Key(1, 0F, "body/fin_l", 0F, 0F, 0F),
            new Key(1, 8F, "body/fin_l", 0F, 0F, -30F),
            new Key(1, 20F, "body/fin_l", 0F, 0F, -30F),
            new Key(1, 22F, "body/fin_l", 0F, 0F, 0F),
            new Key(2, 0F, "body", 0F, 0F, 0F),
            new Key(2, 6F, "body", 0F, 0F, 25F),
            new Key(2, 16F, "body", 0F, 0F, -25F),
            new Key(2, 26F, "body", 0F, 0F, 25F),
            new Key(2, 36F, "body", 0F, 0F, -25F),
            new Key(2, 46F, "body", 0F, 0F, 0F),
            new Key(2, 52F, "body", 0F, 0F, 0F),
            new Key(2, 0F, "body/tail1", 0F, 0F, 0F),
            new Key(2, 6F, "body/tail1", 0F, 35F, 0F),
            new Key(2, 16F, "body/tail1", 0F, -35F, 0F),
            new Key(2, 26F, "body/tail1", 0F, 35F, 0F),
            new Key(2, 36F, "body/tail1", 0F, -35F, 0F),
            new Key(2, 46F, "body/tail1", 0F, 0F, 0F),
            new Key(2, 0F, "body/tail1/tail2", 0F, 0F, 0F),
            new Key(2, 6F, "body/tail1/tail2", 0F, 30F, 0F),
            new Key(2, 16F, "body/tail1/tail2", 0F, -30F, 0F),
            new Key(2, 26F, "body/tail1/tail2", 0F, 30F, 0F),
            new Key(2, 36F, "body/tail1/tail2", 0F, -30F, 0F),
            new Key(2, 46F, "body/tail1/tail2", 0F, 0F, 0F),
            new Key(2, 0F, "body/fin_r", 0F, 0F, 0F),
            new Key(2, 6F, "body/fin_r", 0F, -20F, 40F),
            new Key(2, 46F, "body/fin_r", 0F, -20F, 40F),
            new Key(2, 52F, "body/fin_r", 0F, 0F, 0F),
            new Key(2, 0F, "body/fin_l", 0F, 0F, 0F),
            new Key(2, 6F, "body/fin_l", 0F, 20F, -40F),
            new Key(2, 46F, "body/fin_l", 0F, 20F, -40F),
            new Key(2, 52F, "body/fin_l", 0F, 0F, 0F),
            new Key(2, 0F, "body/head/jaw", 0F, 0F, 0F),
            new Key(2, 6F, "body/head/jaw", 15F, 0F, 0F),
            new Key(2, 44F, "body/head/jaw", 0F, 0F, 0F),
            new Key(2, 50F, "body/head/jaw", 40F, 0F, 0F),
            new Key(2, 52F, "body/head/jaw", 0F, 0F, 0F),
            new Key(3, 0F, "body/head", 0F, 0F, 0F),
            new Key(3, 8F, "body/head", -18F, 0F, 0F),
            new Key(3, 12F, "body/head", 8F, 0F, 0F),
            new Key(3, 14F, "body/head", 0F, 0F, 0F),
            new Key(3, 0F, "body/head/jaw", 0F, 0F, 0F),
            new Key(3, 8F, "body/head/jaw", 30F, 0F, 0F),
            new Key(3, 12F, "body/head/jaw", 45F, 0F, 0F),
            new Key(3, 14F, "body/head/jaw", 0F, 0F, 0F),
            new Key(3, 0F, "body", 0F, 0F, 0F),
            new Key(3, 8F, "body", -6F, 0F, 0F),
            new Key(3, 12F, "body", 6F, 0F, 0F),
            new Key(3, 14F, "body", 0F, 0F, 0F),
            new Key(3, 0F, "body/fin_r", 0F, 0F, 0F),
            new Key(3, 8F, "body/fin_r", 0F, 30F, 20F),
            new Key(3, 14F, "body/fin_r", 0F, 0F, 0F),
            new Key(3, 0F, "body/fin_l", 0F, 0F, 0F),
            new Key(3, 8F, "body/fin_l", 0F, -30F, -20F),
            new Key(3, 14F, "body/fin_l", 0F, 0F, 0F),
            new Key(4, 0F, "body/head/jaw", 0F, 0F, 0F),
            new Key(4, 8F, "body/head/jaw", 40F, 0F, 0F),
            new Key(4, 12F, "body/head/jaw", 40F, 0F, 0F),
            new Key(4, 14F, "body/head/jaw", -10F, 0F, 0F),
            new Key(4, 22F, "body/head/jaw", 0F, 0F, 0F),
            new Key(4, 0F, "body/head", 0F, 0F, 0F),
            new Key(4, 8F, "body/head", -15F, 0F, 0F),
            new Key(4, 14F, "body/head", 10F, 0F, 0F),
            new Key(4, 22F, "body/head", 0F, 0F, 0F),
            new Key(4, 0F, "body", 0F, 0F, 0F),
            new Key(4, 8F, "body", -8F, 0F, 0F),
            new Key(4, 14F, "body", 10F, 0F, 0F),
            new Key(4, 22F, "body", 0F, 0F, 0F),
            new Key(4, 0F, "body/head/lure_base", 0F, 0F, 0F),
            new Key(4, 8F, "body/head/lure_base", -25F, 0F, 0F),
            new Key(4, 14F, "body/head/lure_base", 20F, 0F, 0F),
            new Key(4, 22F, "body/head/lure_base", 0F, 0F, 0F),
            new Key(4, 0F, "body/tail1", 0F, 0F, 0F),
            new Key(4, 8F, "body/tail1", 0F, 30F, 0F),
            new Key(4, 14F, "body/tail1", 0F, -30F, 0F),
            new Key(4, 22F, "body/tail1", 0F, 0F, 0F),
            new Key(4, 0F, "body/fin_r", 0F, 0F, 0F),
            new Key(4, 8F, "body/fin_r", 0F, 35F, 15F),
            new Key(4, 22F, "body/fin_r", 0F, 0F, 0F),
            new Key(4, 0F, "body/fin_l", 0F, 0F, 0F),
            new Key(4, 8F, "body/fin_l", 0F, -35F, -15F),
            new Key(4, 22F, "body/fin_l", 0F, 0F, 0F)};

    public static final float ABYSSAL_LEVIATHAN_SCALE = 1.4F;

    /** Polymer Colossus: Chemie-Riese aus weissen PVC-Platten und gruenen Rohren mit Gasmaske, Linsenaugen und Filter, leuchtenden Chlortanks auf dem Ruecken, Rohrzange am rechten und Schlauchspritze am linken Arm. Textur {@code entity/stage/polymer_colossus.png}. */
    public static LayerDefinition polymerColossus() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_hips = root.addOrReplaceChild("hips", CubeListBuilder.create()
                .texOffs(206, 91).addBox(-8F, -1F, -4F, 16, 5, 8, NONE)
                .texOffs(152, 75).addBox(-7F, -2F, -4F, 14, 7, 8, NONE)
                .texOffs(104, 75).addBox(-7F, -1F, -5F, 14, 5, 10, NONE)
                .texOffs(150, 91).addBox(-8.5F, -1F, -5.5F, 17, 2, 11, NONE)
                .texOffs(196, 164).addBox(-4F, 1F, -6.5F, 8, 5, 2, NONE)
                .texOffs(48, 185).addBox(-2F, 2F, -7F, 4, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 2F, 0F, 0F, 0F, 0F));
        PartDefinition p_torso = p_hips.addOrReplaceChild("torso", CubeListBuilder.create()
                .texOffs(156, 0).addBox(-11F, -18F, -4.5F, 22, 10, 9, NONE)
                .texOffs(62, 0).addBox(-9F, -20F, -4.5F, 18, 14, 9, NONE)
                .texOffs(0, 0).addBox(-9F, -18F, -6.5F, 18, 10, 13, NONE)
                .texOffs(42, 59).addBox(-6F, -8F, -4F, 12, 8, 8, NONE)
                .texOffs(72, 131).addBox(-6.5F, -6F, -4.5F, 13, 2, 9, NONE)
                .texOffs(116, 131).addBox(-6.5F, -3F, -4.5F, 13, 2, 9, NONE)
                .texOffs(70, 143).addBox(-8F, -18F, -7.5F, 16, 9, 1, NONE)
                .texOffs(232, 164).addBox(-3F, -16.5F, -8.2F, 6, 6, 1, NONE)
                .texOffs(0, 180).addBox(-2F, -15.5F, -8.8F, 4, 4, 1, NONE)
                .texOffs(28, 42).addBox(-11.5F, -8F, -7F, 23, 2, 14, NONE)
                .texOffs(158, 106).addBox(-6.5F, -23F, -4.5F, 13, 3, 9, NONE)
                .texOffs(24, 185).addBox(-7.5F, -12F, -8F, 2, 3, 1, NONE)
                .texOffs(30, 185).addBox(5.5F, -12F, -8F, 2, 3, 1, NONE)
                .texOffs(130, 106).addBox(-11.8F, -17F, -3F, 1, 7, 6, NONE)
                .texOffs(144, 106).addBox(10.8F, -17F, -3F, 1, 7, 6, NONE),
                PartPose.offsetAndRotation(0F, -1F, 0F, 0.122F, 0F, 0F));
        PartDefinition p_head = p_torso.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(174, 23).addBox(-5.5F, -11F, -4F, 11, 9, 8, NONE)
                .texOffs(0, 23).addBox(-4.5F, -12F, -4F, 9, 11, 8, NONE)
                .texOffs(218, 0).addBox(-4.5F, -11F, -5F, 9, 9, 10, NONE)
                .texOffs(184, 154).addBox(-3F, -1F, -3F, 6, 3, 6, NONE)
                .texOffs(102, 106).addBox(-1.5F, -13F, -5.5F, 3, 2, 11, NONE)
                .texOffs(232, 131).addBox(-5F, -9.5F, -6F, 10, 9, 2, NONE)
                .texOffs(10, 180).addBox(-5.2F, -9F, -6.6F, 4, 4, 1, NONE)
                .texOffs(20, 180).addBox(1.2F, -9F, -6.6F, 4, 4, 1, NONE)
                .texOffs(216, 180).addBox(-4.7F, -8.5F, -7.2F, 3, 3, 1, NONE)
                .texOffs(224, 180).addBox(1.7F, -8.5F, -7.2F, 3, 3, 1, NONE)
                .texOffs(96, 173).addBox(-6.5F, -5F, -4F, 1, 3, 3, NONE)
                .texOffs(104, 173).addBox(5.5F, -5F, -4F, 1, 3, 3, NONE)
                .texOffs(236, 143).addBox(-6F, -10F, -2F, 1, 5, 5, NONE)
                .texOffs(0, 154).addBox(5F, -10F, -2F, 1, 5, 5, NONE)
                .texOffs(48, 173).addBox(2.5F, -16F, 2F, 2, 5, 2, NONE)
                .texOffs(180, 180).addBox(2F, -17F, 1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, -21F, -1.5F, -0.157F, 0F, 0F));
        PartDefinition p_filter = p_head.addOrReplaceChild("filter", CubeListBuilder.create()
                .texOffs(216, 164).addBox(-2.5F, -2F, -2F, 5, 4, 3, NONE)
                .texOffs(208, 154).addBox(-3F, -2F, -7F, 6, 4, 5, NONE)
                .texOffs(24, 143).addBox(-2F, -3F, -7F, 4, 6, 5, NONE)
                .texOffs(30, 180).addBox(-2F, -2F, -7.6F, 4, 4, 1, NONE)
                .texOffs(36, 185).addBox(-3.2F, -1F, -4F, 1, 2, 2, NONE)
                .texOffs(42, 185).addBox(2.2F, -1F, -4F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, -3.5F, -6F, 0.489F, 0F, 0F));
        PartDefinition p_mhose_r = p_head.addOrReplaceChild("mhose_r", CubeListBuilder.create()
                .texOffs(18, 164).addBox(-1F, 0F, -1F, 2, 7, 2, NONE)
                .texOffs(192, 180).addBox(-1.5F, 0F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(-5F, -3F, -3F, 0.611F, 0F, 0.349F));
        PartDefinition p_mhose_l = p_head.addOrReplaceChild("mhose_l", CubeListBuilder.create()
                .texOffs(26, 164).addBox(-1F, 0F, -1F, 2, 7, 2, NONE)
                .texOffs(204, 180).addBox(-1.5F, 0F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(5F, -3F, -3F, 0.611F, 0F, -0.349F));
        PartDefinition p_arm_r = p_torso.addOrReplaceChild("arm_r", CubeListBuilder.create()
                .texOffs(202, 106).addBox(-7F, -4F, -4.5F, 12, 3, 9, NONE)
                .texOffs(82, 59).addBox(-5F, -6F, -4.5F, 8, 7, 9, NONE)
                .texOffs(210, 42).addBox(-5F, -4F, -6.5F, 8, 3, 13, NONE)
                .texOffs(102, 42).addBox(-7.5F, 0F, -7F, 13, 2, 14, NONE)
                .texOffs(14, 173).addBox(-6F, -8F, -2F, 2, 3, 4, NONE)
                .texOffs(182, 59).addBox(-3.5F, 1F, -3.5F, 7, 9, 7, NONE)
                .texOffs(122, 119).addBox(-4F, 3F, -4F, 8, 4, 8, NONE),
                PartPose.offsetAndRotation(-14F, -16F, 0F, 0F, 0F, 0.105F));
        PartDefinition p_fore_r = p_arm_r.addOrReplaceChild("fore_r", CubeListBuilder.create()
                .texOffs(0, 91).addBox(-4.5F, 1F, -3.5F, 9, 8, 7, NONE)
                .texOffs(0, 42).addBox(-3.5F, 0F, -3.5F, 7, 10, 7, NONE)
                .texOffs(212, 23).addBox(-3.5F, 1F, -4.5F, 7, 8, 9, NONE)
                .texOffs(42, 119).addBox(-5F, 6F, -5F, 10, 2, 10, NONE)
                .texOffs(138, 164).addBox(-5.2F, 1F, -2F, 1, 4, 4, NONE),
                PartPose.offsetAndRotation(-0.5F, 9F, 0F, -0.209F, 0F, 0F));
        PartDefinition p_wrench = p_fore_r.addOrReplaceChild("wrench", CubeListBuilder.create()
                .texOffs(168, 143).addBox(-3.5F, -1F, -3.5F, 7, 3, 7, NONE)
                .texOffs(72, 173).addBox(-2F, 1F, -1F, 4, 4, 2, NONE)
                .texOffs(126, 164).addBox(-1F, 1F, -2F, 2, 4, 4, NONE)
                .texOffs(150, 59).addBox(-4.5F, 5F, -3F, 9, 9, 7, NONE)
                .texOffs(106, 23).addBox(-3.5F, 4F, -3F, 7, 11, 7, NONE)
                .texOffs(74, 23).addBox(-3.5F, 5F, -4F, 7, 9, 9, NONE)
                .texOffs(82, 119).addBox(-5F, 6F, -4.5F, 10, 2, 10, NONE)
                .texOffs(196, 75).addBox(-4F, 4F, -14F, 8, 4, 11, NONE)
                .texOffs(168, 173).addBox(-4F, 8F, -14F, 8, 2, 3, NONE)
                .texOffs(50, 180).addBox(-3.5F, 9.5F, -14F, 7, 1, 3, NONE)
                .texOffs(154, 119).addBox(-3.5F, 12F, -12F, 7, 3, 9, NONE)
                .texOffs(70, 180).addBox(-3F, 11F, -12F, 6, 1, 3, NONE)
                .texOffs(222, 173).addBox(-2F, 9F, -6F, 4, 2, 3, NONE)
                .texOffs(38, 173).addBox(-1F, 8F, -6F, 2, 4, 3, NONE)
                .texOffs(42, 143).addBox(-4.8F, 9F, -3F, 1, 5, 6, NONE)
                .texOffs(56, 143).addBox(3.8F, 9F, -3F, 1, 5, 6, NONE)
                .texOffs(56, 173).addBox(-2F, 15F, -2F, 4, 2, 4, NONE),
                PartPose.offsetAndRotation(0F, 9F, 0F, 0F, 0F, 0F));
        PartDefinition p_arm_l = p_torso.addOrReplaceChild("arm_l", CubeListBuilder.create()
                .texOffs(0, 119).addBox(-5F, -4F, -4.5F, 12, 3, 9, NONE)
                .texOffs(116, 59).addBox(-3F, -6F, -4.5F, 8, 7, 9, NONE)
                .texOffs(0, 59).addBox(-3F, -4F, -6.5F, 8, 3, 13, NONE)
                .texOffs(156, 42).addBox(-5.5F, 0F, -7F, 13, 2, 14, NONE)
                .texOffs(26, 173).addBox(4F, -8F, -2F, 2, 3, 4, NONE)
                .texOffs(210, 59).addBox(-3.5F, 1F, -3.5F, 7, 9, 7, NONE)
                .texOffs(186, 119).addBox(-4F, 3F, -4F, 8, 4, 8, NONE),
                PartPose.offsetAndRotation(14F, -16F, 0F, 0F, 0F, -0.105F));
        PartDefinition p_fore_l = p_arm_l.addOrReplaceChild("fore_l", CubeListBuilder.create()
                .texOffs(114, 91).addBox(-4F, 1F, -3F, 8, 8, 6, NONE)
                .texOffs(28, 75).addBox(-3F, 0F, -3F, 6, 10, 6, NONE)
                .texOffs(0, 75).addBox(-3F, 1F, -4F, 6, 8, 8, NONE)
                .texOffs(160, 131).addBox(-4.5F, 1F, -4.5F, 9, 2, 9, NONE)
                .texOffs(196, 131).addBox(-4.5F, 5F, -4.5F, 9, 2, 9, NONE)
                .texOffs(142, 91).addBox(3.5F, -2F, 1F, 2, 12, 2, NONE),
                PartPose.offsetAndRotation(0.5F, 9F, 0F, -0.209F, 0F, 0F));
        PartDefinition p_nozzle = p_fore_l.addOrReplaceChild("nozzle", CubeListBuilder.create()
                .texOffs(0, 143).addBox(-3F, -1F, -3F, 6, 5, 6, NONE)
                .texOffs(72, 106).addBox(-2F, 1F, -13F, 4, 2, 11, NONE)
                .texOffs(88, 91).addBox(-1F, 0F, -13F, 2, 4, 11, NONE)
                .texOffs(0, 173).addBox(-2.5F, -0.5F, -9F, 5, 5, 2, NONE)
                .texOffs(84, 173).addBox(-2.5F, -0.5F, -12F, 5, 5, 1, NONE)
                .texOffs(230, 154).addBox(-3F, -1F, -16F, 6, 6, 3, NONE)
                .texOffs(40, 180).addBox(-2F, 0F, -16.6F, 4, 4, 1, NONE)
                .texOffs(112, 173).addBox(-1F, 4F, -6F, 2, 4, 2, NONE)
                .texOffs(236, 173).addBox(-1F, 7F, -6F, 2, 1, 4, NONE),
                PartPose.offsetAndRotation(0.5F, 9F, 0F, 0F, 0F, 0F));
        PartDefinition p_tanks = p_torso.addOrReplaceChild("tanks", CubeListBuilder.create()
                .texOffs(34, 23).addBox(-9F, -8F, -1F, 18, 16, 2, NONE)
                .texOffs(134, 23).addBox(-7.5F, -7F, 1.5F, 6, 14, 4, NONE)
                .texOffs(116, 0).addBox(-6.5F, -7F, 0.5F, 4, 14, 6, NONE)
                .texOffs(72, 154).addBox(-8F, -9F, 0F, 7, 2, 7, NONE)
                .texOffs(100, 154).addBox(-8F, 7F, 0F, 7, 2, 7, NONE)
                .texOffs(34, 164).addBox(-8.1F, -1F, 0F, 7, 1, 7, NONE)
                .texOffs(232, 180).addBox(-5.5F, -11F, 2.5F, 2, 2, 2, NONE)
                .texOffs(124, 180).addBox(-6.5F, -12F, 2F, 4, 1, 3, NONE)
                .texOffs(96, 75).addBox(-5F, -8F, 6.8F, 1, 15, 1, NONE)
                .texOffs(58, 185).addBox(-6.5F, 3F, 6.4F, 1, 1, 1, NONE)
                .texOffs(62, 185).addBox(-3.5F, -3F, 6.4F, 1, 1, 1, NONE)
                .texOffs(66, 185).addBox(-6.5F, -5F, 6.4F, 1, 1, 1, NONE)
                .texOffs(154, 23).addBox(1.5F, -7F, 1.5F, 6, 14, 4, NONE)
                .texOffs(136, 0).addBox(2.5F, -7F, 0.5F, 4, 14, 6, NONE)
                .texOffs(128, 154).addBox(1F, -9F, 0F, 7, 2, 7, NONE)
                .texOffs(156, 154).addBox(1F, 7F, 0F, 7, 2, 7, NONE)
                .texOffs(62, 164).addBox(0.9F, -1F, 0F, 7, 1, 7, NONE)
                .texOffs(240, 180).addBox(3.5F, -11F, 2.5F, 2, 2, 2, NONE)
                .texOffs(138, 180).addBox(2.5F, -12F, 2F, 4, 1, 3, NONE)
                .texOffs(100, 75).addBox(4F, -8F, 6.8F, 1, 15, 1, NONE)
                .texOffs(70, 185).addBox(2.5F, 3F, 6.4F, 1, 1, 1, NONE)
                .texOffs(74, 185).addBox(5.5F, -3F, 6.4F, 1, 1, 1, NONE)
                .texOffs(78, 185).addBox(2.5F, -5F, 6.4F, 1, 1, 1, NONE)
                .texOffs(0, 164).addBox(-1F, -10F, 1F, 2, 2, 7, NONE)
                .texOffs(82, 185).addBox(-0.5F, 0F, 6.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -12F, 6F, -0.07F, 0F, 0F));
        PartDefinition p_hose_r = p_torso.addOrReplaceChild("hose_r", CubeListBuilder.create()
                .texOffs(196, 143).addBox(-1F, -1F, -7F, 2, 2, 8, NONE)
                .texOffs(248, 180).addBox(-1.5F, -1.5F, -5F, 3, 3, 1, NONE)
                .texOffs(0, 185).addBox(-1.5F, -1.5F, 0F, 3, 3, 1, NONE),
                PartPose.offsetAndRotation(-4.5F, -23F, 8.5F, 0.209F, 0F, 0F));
        PartDefinition p_hose_l = p_torso.addOrReplaceChild("hose_l", CubeListBuilder.create()
                .texOffs(216, 143).addBox(-1F, -1F, -7F, 2, 2, 8, NONE)
                .texOffs(8, 185).addBox(-1.5F, -1.5F, -5F, 3, 3, 1, NONE)
                .texOffs(16, 185).addBox(-1.5F, -1.5F, 0F, 3, 3, 1, NONE),
                PartPose.offsetAndRotation(4.5F, -23F, 8.5F, 0.209F, 0F, 0F));
        PartDefinition p_leg_r = p_hips.addOrReplaceChild("leg_r", CubeListBuilder.create()
                .texOffs(32, 91).addBox(-3.5F, -1F, -3.5F, 7, 8, 7, NONE)
                .texOffs(104, 143).addBox(-4F, 3F, -4F, 8, 2, 8, NONE)
                .texOffs(152, 180).addBox(-3F, 0F, -4.2F, 6, 3, 1, NONE)
                .texOffs(36, 154).addBox(-4.2F, -1F, -2F, 1, 6, 4, NONE),
                PartPose.offsetAndRotation(-5F, 6F, 0F, 0F, 0F, 0F));
        PartDefinition p_shin_r = p_leg_r.addOrReplaceChild("shin_r", CubeListBuilder.create()
                .texOffs(218, 119).addBox(-3F, 0F, -3F, 6, 6, 6, NONE)
                .texOffs(90, 164).addBox(-3.5F, -0.5F, -4.2F, 7, 6, 2, NONE)
                .texOffs(12, 154).addBox(-3.8F, 0F, -2.5F, 1, 5, 5, NONE)
                .texOffs(190, 173).addBox(-3F, -2F, -4.8F, 6, 3, 2, NONE)
                .texOffs(56, 154).addBox(-1F, -2F, 2.5F, 2, 8, 2, NONE),
                PartPose.offsetAndRotation(0F, 7F, 0F, 0F, 0F, 0F));
        PartDefinition p_foot_r = p_shin_r.addOrReplaceChild("foot_r", CubeListBuilder.create()
                .texOffs(0, 106).addBox(-4F, 0F, -6F, 8, 3, 10, NONE)
                .texOffs(120, 173).addBox(-4.5F, 0.5F, -7.5F, 9, 2, 3, NONE)
                .texOffs(148, 164).addBox(-3F, -1F, -4F, 6, 1, 6, NONE)
                .texOffs(88, 180).addBox(-3.5F, 1F, 4F, 7, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
        PartDefinition p_leg_l = p_hips.addOrReplaceChild("leg_l", CubeListBuilder.create()
                .texOffs(60, 91).addBox(-3.5F, -1F, -3.5F, 7, 8, 7, NONE)
                .texOffs(136, 143).addBox(-4F, 3F, -4F, 8, 2, 8, NONE)
                .texOffs(166, 180).addBox(-3F, 0F, -4.2F, 6, 3, 1, NONE)
                .texOffs(46, 154).addBox(3.2F, -1F, -2F, 1, 6, 4, NONE),
                PartPose.offsetAndRotation(5F, 6F, 0F, 0F, 0F, 0F));
        PartDefinition p_shin_l = p_leg_l.addOrReplaceChild("shin_l", CubeListBuilder.create()
                .texOffs(0, 131).addBox(-3F, 0F, -3F, 6, 6, 6, NONE)
                .texOffs(108, 164).addBox(-3.5F, -0.5F, -4.2F, 7, 6, 2, NONE)
                .texOffs(24, 154).addBox(2.8F, 0F, -2.5F, 1, 5, 5, NONE)
                .texOffs(206, 173).addBox(-3F, -2F, -4.8F, 6, 3, 2, NONE)
                .texOffs(64, 154).addBox(-1F, -2F, 2.5F, 2, 8, 2, NONE),
                PartPose.offsetAndRotation(0F, 7F, 0F, 0F, 0F, 0F));
        PartDefinition p_foot_l = p_shin_l.addOrReplaceChild("foot_l", CubeListBuilder.create()
                .texOffs(36, 106).addBox(-4F, 0F, -6F, 8, 3, 10, NONE)
                .texOffs(144, 173).addBox(-4.5F, 0.5F, -7.5F, 9, 2, 3, NONE)
                .texOffs(172, 164).addBox(-3F, -1F, -4F, 6, 1, 6, NONE)
                .texOffs(106, 180).addBox(-3.5F, 1F, 4F, 7, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
        PartDefinition p_tasset_r = p_hips.addOrReplaceChild("tasset_r", CubeListBuilder.create()
                .texOffs(52, 75).addBox(-1.5F, 0F, -4.5F, 2, 7, 9, NONE)
                .texOffs(24, 131).addBox(-2F, 5F, -5F, 2, 2, 10, NONE),
                PartPose.offsetAndRotation(-8F, -1F, 0F, 0F, 0F, 0.175F));
        PartDefinition p_tasset_l = p_hips.addOrReplaceChild("tasset_l", CubeListBuilder.create()
                .texOffs(74, 75).addBox(-0.5F, 0F, -4.5F, 2, 7, 9, NONE)
                .texOffs(48, 131).addBox(0F, 5F, -5F, 2, 2, 10, NONE),
                PartPose.offsetAndRotation(8F, -1F, 0F, 0F, 0F, -0.175F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    public static final Role[] POLYMER_COLOSSUS_ROLES = {new Role("hips/torso", "body", 0.00F), new Role("hips/torso/head", "head", 0.00F), new Role("hips/torso/arm_r", "arm_r", 0.00F), new Role("hips/torso/arm_l", "arm_l", 0.00F), new Role("hips/leg_r", "leg_r", 0.00F), new Role("hips/leg_l", "leg_l", 0.00F)};

    public static final Key[] POLYMER_COLOSSUS_POSES = {
            new Key(0, 0F, "hips/torso", 18F, 0F, 0F),
            new Key(0, 0F, "hips/torso/head", 15F, 0F, 0F),
            new Key(0, 0F, "hips/torso/arm_r", -12F, 0F, 6F),
            new Key(0, 0F, "hips/torso/arm_l", -14F, 0F, -6F),
            new Key(0, 0F, "hips/torso/arm_r/fore_r", 10F, 0F, 0F),
            new Key(0, 0F, "hips/torso/arm_l/fore_l/nozzle", 30F, 0F, 0F),
            new Key(1, 0F, "hips/torso", 0F, 0F, 0F),
            new Key(1, 6F, "hips/torso", -12F, 0F, 0F),
            new Key(1, 14F, "hips/torso", -12F, 0F, 0F),
            new Key(1, 20F, "hips/torso", 0F, 0F, 0F),
            new Key(1, 0F, "hips/torso/head", 0F, 0F, 0F),
            new Key(1, 6F, "hips/torso/head", -22F, 0F, 0F),
            new Key(1, 14F, "hips/torso/head", -22F, 0F, 0F),
            new Key(1, 20F, "hips/torso/head", 0F, 0F, 0F),
            new Key(1, 0F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(1, 6F, "hips/torso/arm_l", -140F, 0F, -20F),
            new Key(1, 14F, "hips/torso/arm_l", -140F, 0F, -20F),
            new Key(1, 20F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(1, 0F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(1, 6F, "hips/torso/arm_r", 0F, 0F, 25F),
            new Key(1, 14F, "hips/torso/arm_r", 0F, 0F, 25F),
            new Key(1, 20F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(2, 8F, "hips/torso/arm_r", -170F, 0F, 10F),
            new Key(2, 13F, "hips/torso/arm_r", -40F, 0F, 0F),
            new Key(2, 15F, "hips/torso/arm_r", -40F, 0F, 0F),
            new Key(2, 18F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso/arm_r/fore_r", 0F, 0F, 0F),
            new Key(2, 8F, "hips/torso/arm_r/fore_r", -35F, 0F, 0F),
            new Key(2, 13F, "hips/torso/arm_r/fore_r", 0F, 0F, 0F),
            new Key(2, 18F, "hips/torso/arm_r/fore_r", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso", 0F, 0F, 0F),
            new Key(2, 8F, "hips/torso", -14F, 10F, 0F),
            new Key(2, 13F, "hips/torso", 24F, 0F, 0F),
            new Key(2, 15F, "hips/torso", 24F, 0F, 0F),
            new Key(2, 18F, "hips/torso", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(2, 8F, "hips/torso/arm_l", -20F, 0F, -30F),
            new Key(2, 13F, "hips/torso/arm_l", 10F, 0F, -15F),
            new Key(2, 18F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(3, 0F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(3, 4F, "hips/torso/arm_l", -85F, -10F, 0F),
            new Key(3, 7F, "hips/torso/arm_l", -74F, -10F, 0F),
            new Key(3, 10F, "hips/torso/arm_l", -85F, -10F, 0F),
            new Key(3, 12F, "hips/torso/arm_l", -74F, -10F, 0F),
            new Key(3, 15F, "hips/torso/arm_l", -85F, -10F, 0F),
            new Key(3, 17F, "hips/torso/arm_l", -74F, -10F, 0F),
            new Key(3, 20F, "hips/torso/arm_l", -85F, -10F, 0F),
            new Key(3, 22F, "hips/torso/arm_l", -74F, -10F, 0F),
            new Key(3, 24F, "hips/torso/arm_l", -80F, -10F, 0F),
            new Key(3, 26F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(3, 0F, "hips/torso/arm_l/fore_l", 0F, 0F, 0F),
            new Key(3, 4F, "hips/torso/arm_l/fore_l", 10F, 0F, 0F),
            new Key(3, 24F, "hips/torso/arm_l/fore_l", 10F, 0F, 0F),
            new Key(3, 26F, "hips/torso/arm_l/fore_l", 0F, 0F, 0F),
            new Key(3, 0F, "hips/torso", 0F, 0F, 0F),
            new Key(3, 4F, "hips/torso", 0F, -18F, 0F),
            new Key(3, 24F, "hips/torso", 0F, -18F, 0F),
            new Key(3, 26F, "hips/torso", 0F, 0F, 0F),
            new Key(3, 0F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(3, 4F, "hips/torso/arm_r", 15F, 0F, 12F),
            new Key(3, 24F, "hips/torso/arm_r", 15F, 0F, 12F),
            new Key(3, 26F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(4, 0F, "hips/torso/head", 0F, 0F, 0F),
            new Key(4, 8F, "hips/torso/head", -18F, 0F, 0F),
            new Key(4, 14F, "hips/torso/head", -18F, 0F, 0F),
            new Key(4, 16F, "hips/torso/head", 10F, 0F, 0F),
            new Key(4, 18F, "hips/torso/head", 0F, 0F, 0F),
            new Key(4, 0F, "hips/torso", 0F, 0F, 0F),
            new Key(4, 8F, "hips/torso", -12F, 0F, 0F),
            new Key(4, 14F, "hips/torso", -12F, 0F, 0F),
            new Key(4, 16F, "hips/torso", 10F, 0F, 0F),
            new Key(4, 18F, "hips/torso", 0F, 0F, 0F),
            new Key(4, 0F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(4, 8F, "hips/torso/arm_r", -25F, 0F, 40F),
            new Key(4, 16F, "hips/torso/arm_r", -25F, 0F, 40F),
            new Key(4, 18F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(4, 0F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(4, 8F, "hips/torso/arm_l", -25F, 0F, -40F),
            new Key(4, 16F, "hips/torso/arm_l", -25F, 0F, -40F),
            new Key(4, 18F, "hips/torso/arm_l", 0F, 0F, 0F)};

    public static final float POLYMER_COLOSSUS_SCALE = 1.3F;

    /** Mycelial Queen: schlanke Pilz-Matriarchin in einer Robe aus Hyphen mit breitem rotbraunem Pilzhut als Krone, bernsteinfarben leuchtenden Lamellen, blassem Gesicht, langen Rankenfingern, zwei Rueckententakeln und Sporensaecken. Textur {@code entity/stage/mycelial_queen.png}. */
    public static LayerDefinition mycelialQueen() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_robe = root.addOrReplaceChild("robe", CubeListBuilder.create()
                .texOffs(72, 78).addBox(-4.5F, 0F, -3.5F, 9, 12, 7, NONE)
                .texOffs(176, 56).addBox(-6F, 12F, -5F, 12, 11, 10, NONE)
                .texOffs(102, 97).addBox(-5F, -1F, -4F, 10, 3, 8, NONE)
                .texOffs(12, 138).addBox(-2F, 1F, -4.6F, 4, 3, 1, NONE)
                .texOffs(50, 127).addBox(-0.5F, 23F, -13F, 1, 1, 5, NONE)
                .texOffs(144, 138).addBox(-1F, 23F, -15F, 2, 1, 2, NONE)
                .texOffs(82, 142).addBox(-11F, 23F, -6F, 4, 1, 1, NONE)
                .texOffs(92, 142).addBox(7F, 23F, -6F, 4, 1, 1, NONE)
                .texOffs(46, 142).addBox(-12F, 23F, 3F, 5, 1, 1, NONE)
                .texOffs(58, 142).addBox(7F, 23F, 4F, 5, 1, 1, NONE)
                .texOffs(216, 138).addBox(-13F, 23F, 2.5F, 1, 1, 2, NONE)
                .texOffs(18, 120).addBox(-3F, 23F, 7F, 1, 1, 6, NONE)
                .texOffs(36, 133).addBox(2F, 23F, 8F, 1, 1, 4, NONE)
                .texOffs(102, 142).addBox(1.5F, 23F, 12F, 2, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
        PartDefinition p_torso = p_robe.addOrReplaceChild("torso", CubeListBuilder.create()
                .texOffs(138, 97).addBox(-3F, -6F, -2.5F, 6, 6, 5, NONE)
                .texOffs(190, 111).addBox(-3.5F, -5F, -3F, 7, 1, 6, NONE)
                .texOffs(216, 111).addBox(-3.5F, -3F, -3F, 7, 1, 6, NONE)
                .texOffs(220, 78).addBox(-4.5F, -14F, -3F, 9, 8, 6, NONE)
                .texOffs(84, 111).addBox(-3.5F, -14F, -3.6F, 1, 8, 1, NONE)
                .texOffs(88, 111).addBox(2.5F, -14F, -3.6F, 1, 8, 1, NONE)
                .texOffs(70, 142).addBox(-2.5F, -9F, -3.6F, 5, 1, 1, NONE)
                .texOffs(22, 138).addBox(-1.5F, -13F, -4F, 3, 3, 1, NONE)
                .texOffs(46, 133).addBox(-2F, -13.5F, -3.7F, 4, 4, 1, NONE)
                .texOffs(164, 97).addBox(-7F, -16F, -3.5F, 14, 3, 7, NONE)
                .texOffs(100, 111).addBox(-5.5F, -17F, -3F, 11, 2, 6, NONE)
                .texOffs(0, 97).addBox(-5F, -15F, 3F, 10, 12, 2, NONE)
                .texOffs(134, 111).addBox(-9.5F, -19F, -2F, 4, 4, 4, NONE)
                .texOffs(62, 127).addBox(-7.5F, -20.5F, 0.5F, 3, 3, 3, NONE)
                .texOffs(30, 138).addBox(-9.5F, -16.5F, 1.5F, 2, 2, 2, NONE)
                .texOffs(172, 120).addBox(-10F, -16F, -2.5F, 5, 1, 5, NONE)
                .texOffs(152, 138).addBox(-7F, -21F, 1F, 2, 1, 2, NONE)
                .texOffs(150, 111).addBox(5.5F, -19F, -2F, 4, 4, 4, NONE)
                .texOffs(74, 127).addBox(4.5F, -20.5F, 0.5F, 3, 3, 3, NONE)
                .texOffs(38, 138).addBox(7.5F, -16.5F, 1.5F, 2, 2, 2, NONE)
                .texOffs(192, 120).addBox(5F, -16F, -2.5F, 5, 1, 5, NONE)
                .texOffs(160, 138).addBox(5F, -21F, 1F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
        PartDefinition p_head = p_torso.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(86, 127).addBox(-1.5F, -3F, -1.5F, 3, 3, 3, NONE)
                .texOffs(62, 97).addBox(-3F, -10F, -3.5F, 6, 6, 6, NONE)
                .texOffs(0, 120).addBox(-2F, -4.5F, -3.2F, 4, 2, 5, NONE)
                .texOffs(32, 142).addBox(-3.2F, -8.5F, -3.8F, 6, 1, 1, NONE)
                .texOffs(108, 142).addBox(-2.5F, -7.5F, -3.9F, 2, 1, 1, NONE)
                .texOffs(114, 142).addBox(0.5F, -7.5F, -3.9F, 2, 1, 1, NONE)
                .texOffs(222, 138).addBox(-2.6F, -6.5F, -3.7F, 1, 2, 1, NONE)
                .texOffs(226, 138).addBox(1.6F, -6.5F, -3.7F, 1, 2, 1, NONE)
                .texOffs(120, 142).addBox(-0.5F, -5F, -3.5F, 1, 1, 1, NONE)
                .texOffs(24, 97).addBox(-3.5F, -10F, -0.5F, 7, 10, 4, NONE)
                .texOffs(68, 111).addBox(-3.6F, -9F, -2.5F, 1, 6, 3, NONE)
                .texOffs(76, 111).addBox(2.6F, -9F, -2.5F, 1, 6, 3, NONE),
                PartPose.offsetAndRotation(0F, -15F, -0.5F, 0F, 0F, 0F));
        PartDefinition p_cap = p_head.addOrReplaceChild("cap", CubeListBuilder.create()
                .texOffs(140, 0).addBox(-12F, -1F, -12F, 24, 1, 24, NONE)
                .texOffs(44, 0).addBox(-12F, -3F, -12F, 24, 2, 24, NONE)
                .texOffs(0, 31).addBox(-11.5F, -5F, -11.5F, 23, 2, 23, NONE)
                .texOffs(0, 56).addBox(-10.5F, -6F, -10.5F, 21, 1, 21, NONE)
                .texOffs(0, 78).addBox(-9F, -7F, -9F, 18, 1, 18, NONE)
                .texOffs(112, 78).addBox(-7F, -8F, -7F, 14, 1, 14, NONE)
                .texOffs(206, 97).addBox(-4.5F, -9F, -4.5F, 9, 1, 9, NONE)
                .texOffs(0, 127).addBox(-2.5F, 0F, 0F, 5, 2, 4, NONE),
                PartPose.offsetAndRotation(0F, -11F, 1F, -0.052F, 0F, 0F));
        PartDefinition p_crown0 = p_cap.addOrReplaceChild("crown0", CubeListBuilder.create()
                .texOffs(122, 127).addBox(-0.5F, -5F, -0.5F, 1, 5, 1, NONE)
                .texOffs(188, 127).addBox(-1.5F, -6.5F, -1.5F, 3, 2, 3, NONE)
                .texOffs(168, 138).addBox(-1F, -7.5F, -1F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, -8.5F, -5.5F, -0.244F, 0F, 0F));
        PartDefinition p_crown1 = p_cap.addOrReplaceChild("crown1", CubeListBuilder.create()
                .texOffs(90, 138).addBox(-0.5F, -3F, -0.5F, 1, 3, 1, NONE)
                .texOffs(200, 127).addBox(-1.5F, -4.5F, -1.5F, 3, 2, 3, NONE)
                .texOffs(176, 138).addBox(-1F, -5.5F, -1F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(4.763F, -8.5F, -2.75F, -0.122F, 0F, 0.212F));
        PartDefinition p_crown2 = p_cap.addOrReplaceChild("crown2", CubeListBuilder.create()
                .texOffs(72, 133).addBox(-0.5F, -4F, -0.5F, 1, 4, 1, NONE)
                .texOffs(212, 127).addBox(-1.5F, -5.5F, -1.5F, 3, 2, 3, NONE)
                .texOffs(184, 138).addBox(-1F, -6.5F, -1F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(4.763F, -8.5F, 2.75F, 0.122F, 0F, 0.212F));
        PartDefinition p_crown3 = p_cap.addOrReplaceChild("crown3", CubeListBuilder.create()
                .texOffs(94, 138).addBox(-0.5F, -3F, -0.5F, 1, 3, 1, NONE)
                .texOffs(224, 127).addBox(-1.5F, -4.5F, -1.5F, 3, 2, 3, NONE)
                .texOffs(192, 138).addBox(-1F, -5.5F, -1F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, -8.5F, 5.5F, 0.244F, 0F, 0F));
        PartDefinition p_crown4 = p_cap.addOrReplaceChild("crown4", CubeListBuilder.create()
                .texOffs(76, 133).addBox(-0.5F, -4F, -0.5F, 1, 4, 1, NONE)
                .texOffs(236, 127).addBox(-1.5F, -5.5F, -1.5F, 3, 2, 3, NONE)
                .texOffs(200, 138).addBox(-1F, -6.5F, -1F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(-4.763F, -8.5F, 2.75F, 0.122F, 0F, -0.212F));
        PartDefinition p_crown5 = p_cap.addOrReplaceChild("crown5", CubeListBuilder.create()
                .texOffs(98, 138).addBox(-0.5F, -3F, -0.5F, 1, 3, 1, NONE)
                .texOffs(0, 133).addBox(-1.5F, -4.5F, -1.5F, 3, 2, 3, NONE)
                .texOffs(208, 138).addBox(-1F, -5.5F, -1F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(-4.763F, -8.5F, -2.75F, -0.122F, 0F, -0.212F));
        PartDefinition p_rim_f = p_cap.addOrReplaceChild("rim_f", CubeListBuilder.create()
                .texOffs(68, 120).addBox(-11F, -1F, -4F, 22, 2, 4, NONE)
                .texOffs(112, 133).addBox(-10F, 1F, -3F, 20, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, -2F, -12F, 0.314F, 0F, 0F));
        PartDefinition p_rim_b = p_cap.addOrReplaceChild("rim_b", CubeListBuilder.create()
                .texOffs(120, 120).addBox(-11F, -1F, 0F, 22, 2, 4, NONE)
                .texOffs(158, 133).addBox(-10F, 1F, 0F, 20, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, -2F, 12F, -0.559F, 0F, 0F));
        PartDefinition p_rim_r = p_cap.addOrReplaceChild("rim_r", CubeListBuilder.create()
                .texOffs(92, 31).addBox(-4F, -1F, -11F, 4, 2, 22, NONE)
                .texOffs(84, 56).addBox(-3F, 1F, -10F, 3, 1, 20, NONE),
                PartPose.offsetAndRotation(-12F, -2F, 0F, 0F, 0F, -0.559F));
        PartDefinition p_rim_l = p_cap.addOrReplaceChild("rim_l", CubeListBuilder.create()
                .texOffs(144, 31).addBox(0F, -1F, -11F, 4, 2, 22, NONE)
                .texOffs(130, 56).addBox(0F, 1F, -10F, 3, 1, 20, NONE),
                PartPose.offsetAndRotation(12F, -2F, 0F, 0F, 0F, 0.559F));
        PartDefinition p_veil0 = p_cap.addOrReplaceChild("veil0", CubeListBuilder.create()
                .texOffs(182, 111).addBox(-0.5F, 0F, -0.5F, 1, 7, 1, NONE)
                .texOffs(124, 142).addBox(-0.5F, 7F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-8F, 0F, -8F, 0F, 0F, 0F));
        PartDefinition p_veil1 = p_cap.addOrReplaceChild("veil1", CubeListBuilder.create()
                .texOffs(186, 111).addBox(-0.5F, 0F, -0.5F, 1, 7, 1, NONE)
                .texOffs(128, 142).addBox(-0.5F, 7F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(8F, 0F, -8F, 0F, 0F, 0F));
        PartDefinition p_veil2 = p_cap.addOrReplaceChild("veil2", CubeListBuilder.create()
                .texOffs(242, 97).addBox(-0.5F, 0F, -0.5F, 1, 9, 1, NONE)
                .texOffs(132, 142).addBox(-0.5F, 9F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-11F, 0F, 0F, 0F, 0F, 0F));
        PartDefinition p_veil3 = p_cap.addOrReplaceChild("veil3", CubeListBuilder.create()
                .texOffs(246, 97).addBox(-0.5F, 0F, -0.5F, 1, 9, 1, NONE)
                .texOffs(136, 142).addBox(-0.5F, 9F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(11F, 0F, 0F, 0F, 0F, 0F));
        PartDefinition p_veil4 = p_cap.addOrReplaceChild("veil4", CubeListBuilder.create()
                .texOffs(92, 111).addBox(-0.5F, 0F, -0.5F, 1, 8, 1, NONE)
                .texOffs(140, 142).addBox(-0.5F, 8F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-7F, 0F, 9F, 0F, 0F, 0F));
        PartDefinition p_veil5 = p_cap.addOrReplaceChild("veil5", CubeListBuilder.create()
                .texOffs(96, 111).addBox(-0.5F, 0F, -0.5F, 1, 8, 1, NONE)
                .texOffs(144, 142).addBox(-0.5F, 8F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(7F, 0F, 9F, 0F, 0F, 0F));
        PartDefinition p_veil6 = p_cap.addOrReplaceChild("veil6", CubeListBuilder.create()
                .texOffs(160, 97).addBox(-0.5F, 0F, -0.5F, 1, 10, 1, NONE)
                .texOffs(148, 142).addBox(-0.5F, 10F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 11F, 0F, 0F, 0F));
        PartDefinition p_arm_r = p_torso.addOrReplaceChild("arm_r", CubeListBuilder.create()
                .texOffs(46, 97).addBox(-1F, 0F, -1F, 2, 11, 2, NONE)
                .texOffs(0, 111).addBox(-3F, -2F, -3F, 5, 3, 6, NONE),
                PartPose.offsetAndRotation(-6.5F, -13F, 0F, 0F, 0F, 0.14F));
        PartDefinition p_sleeve_r = p_arm_r.addOrReplaceChild("sleeve_r", CubeListBuilder.create()
                .texOffs(168, 78).addBox(-3F, 0F, -3F, 6, 9, 6, NONE)
                .texOffs(212, 120).addBox(-2.5F, -1F, -2.5F, 5, 1, 5, NONE)
                .texOffs(46, 138).addBox(-3F, 9F, 1F, 2, 2, 2, NONE)
                .texOffs(78, 138).addBox(1F, 9F, -3F, 2, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 1F, 0F, 0.209F, 0F, 0.175F));
        PartDefinition p_fore_r = p_arm_r.addOrReplaceChild("fore_r", CubeListBuilder.create()
                .texOffs(86, 97).addBox(-1F, 0F, -1F, 2, 10, 2, NONE)
                .texOffs(12, 133).addBox(-1.5F, 7F, -1.5F, 3, 2, 3, NONE)
                .texOffs(218, 133).addBox(-1.5F, 1F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, 11F, 0F, -0.384F, 0F, 0F));
        PartDefinition p_hand_r = p_fore_r.addOrReplaceChild("hand_r", CubeListBuilder.create()
                .texOffs(98, 127).addBox(-1.5F, 0F, -1.5F, 3, 3, 3, NONE)
                .texOffs(156, 127).addBox(-2F, 0F, -2F, 4, 1, 4, NONE),
                PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
        PartDefinition p_finger_r0 = p_hand_r.addOrReplaceChild("finger_r0", CubeListBuilder.create()
                .texOffs(80, 133).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE)
                .texOffs(102, 138).addBox(-0.5F, 3.5F, -1.2F, 1, 3, 1, NONE)
                .texOffs(230, 138).addBox(-0.5F, 6F, -2F, 1, 2, 1, NONE)
                .texOffs(152, 142).addBox(-0.5F, 7.5F, -2.6F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-1F, 2F, -1F, -0.175F, 0F, 0.314F));
        PartDefinition p_finger_r1 = p_hand_r.addOrReplaceChild("finger_r1", CubeListBuilder.create()
                .texOffs(84, 133).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE)
                .texOffs(106, 138).addBox(-0.5F, 3.5F, -1.2F, 1, 3, 1, NONE)
                .texOffs(234, 138).addBox(-0.5F, 6F, -2F, 1, 2, 1, NONE)
                .texOffs(156, 142).addBox(-0.5F, 7.5F, -2.6F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0.5F, 2F, -1F, -0.314F, 0F, -0.14F));
        PartDefinition p_finger_r2 = p_hand_r.addOrReplaceChild("finger_r2", CubeListBuilder.create()
                .texOffs(88, 133).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE)
                .texOffs(110, 138).addBox(-0.5F, 3.5F, -1.2F, 1, 3, 1, NONE)
                .texOffs(238, 138).addBox(-0.5F, 6F, -2F, 1, 2, 1, NONE)
                .texOffs(160, 142).addBox(-0.5F, 7.5F, -2.6F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-1F, 2F, 1F, 0.209F, 0F, 0.244F));
        PartDefinition p_finger_r3 = p_hand_r.addOrReplaceChild("finger_r3", CubeListBuilder.create()
                .texOffs(92, 133).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE)
                .texOffs(114, 138).addBox(-0.5F, 3.5F, -1.2F, 1, 3, 1, NONE)
                .texOffs(242, 138).addBox(-0.5F, 6F, -2F, 1, 2, 1, NONE)
                .texOffs(164, 142).addBox(-0.5F, 7.5F, -2.6F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(1F, 2F, 1F, 0.14F, 0F, -0.279F));
        PartDefinition p_arm_l = p_torso.addOrReplaceChild("arm_l", CubeListBuilder.create()
                .texOffs(54, 97).addBox(-1F, 0F, -1F, 2, 11, 2, NONE)
                .texOffs(22, 111).addBox(-2F, -2F, -3F, 5, 3, 6, NONE),
                PartPose.offsetAndRotation(6.5F, -13F, 0F, 0F, 0F, -0.14F));
        PartDefinition p_sleeve_l = p_arm_l.addOrReplaceChild("sleeve_l", CubeListBuilder.create()
                .texOffs(192, 78).addBox(-3F, 0F, -3F, 6, 9, 6, NONE)
                .texOffs(232, 120).addBox(-2.5F, -1F, -2.5F, 5, 1, 5, NONE)
                .texOffs(54, 138).addBox(1F, 9F, 1F, 2, 2, 2, NONE)
                .texOffs(84, 138).addBox(-3F, 9F, -3F, 2, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 1F, 0F, 0.209F, 0F, -0.175F));
        PartDefinition p_fore_l = p_arm_l.addOrReplaceChild("fore_l", CubeListBuilder.create()
                .texOffs(94, 97).addBox(-1F, 0F, -1F, 2, 10, 2, NONE)
                .texOffs(24, 133).addBox(-1.5F, 7F, -1.5F, 3, 2, 3, NONE)
                .texOffs(230, 133).addBox(-1.5F, 1F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, 11F, 0F, -0.384F, 0F, 0F));
        PartDefinition p_hand_l = p_fore_l.addOrReplaceChild("hand_l", CubeListBuilder.create()
                .texOffs(110, 127).addBox(-1.5F, 0F, -1.5F, 3, 3, 3, NONE)
                .texOffs(172, 127).addBox(-2F, 0F, -2F, 4, 1, 4, NONE),
                PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
        PartDefinition p_finger_l0 = p_hand_l.addOrReplaceChild("finger_l0", CubeListBuilder.create()
                .texOffs(96, 133).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE)
                .texOffs(118, 138).addBox(-0.5F, 3.5F, -1.2F, 1, 3, 1, NONE)
                .texOffs(246, 138).addBox(-0.5F, 6F, -2F, 1, 2, 1, NONE)
                .texOffs(168, 142).addBox(-0.5F, 7.5F, -2.6F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(1F, 2F, -1F, -0.175F, 0F, -0.314F));
        PartDefinition p_finger_l1 = p_hand_l.addOrReplaceChild("finger_l1", CubeListBuilder.create()
                .texOffs(100, 133).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE)
                .texOffs(122, 138).addBox(-0.5F, 3.5F, -1.2F, 1, 3, 1, NONE)
                .texOffs(250, 138).addBox(-0.5F, 6F, -2F, 1, 2, 1, NONE)
                .texOffs(172, 142).addBox(-0.5F, 7.5F, -2.6F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-0.5F, 2F, -1F, -0.314F, 0F, 0.14F));
        PartDefinition p_finger_l2 = p_hand_l.addOrReplaceChild("finger_l2", CubeListBuilder.create()
                .texOffs(104, 133).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE)
                .texOffs(126, 138).addBox(-0.5F, 3.5F, -1.2F, 1, 3, 1, NONE)
                .texOffs(0, 142).addBox(-0.5F, 6F, -2F, 1, 2, 1, NONE)
                .texOffs(176, 142).addBox(-0.5F, 7.5F, -2.6F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(1F, 2F, 1F, 0.209F, 0F, -0.244F));
        PartDefinition p_finger_l3 = p_hand_l.addOrReplaceChild("finger_l3", CubeListBuilder.create()
                .texOffs(108, 133).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE)
                .texOffs(130, 138).addBox(-0.5F, 3.5F, -1.2F, 1, 3, 1, NONE)
                .texOffs(4, 142).addBox(-0.5F, 6F, -2F, 1, 2, 1, NONE)
                .texOffs(180, 142).addBox(-0.5F, 7.5F, -2.6F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-1F, 2F, 1F, 0.14F, 0F, 0.279F));
        PartDefinition p_tendril_r = p_torso.addOrReplaceChild("tendril_r", CubeListBuilder.create()
                .texOffs(44, 111).addBox(-1.5F, -6F, -1.5F, 3, 6, 3, NONE)
                .texOffs(18, 127).addBox(-2F, -1F, -2F, 4, 2, 4, NONE)
                .texOffs(184, 142).addBox(-1F, -4F, 1.2F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-3F, -11F, 3.5F, -0.489F, 0F, -1.082F));
        PartDefinition p_tendril_r2 = p_tendril_r.addOrReplaceChild("tendril_r2", CubeListBuilder.create()
                .texOffs(166, 111).addBox(-1F, -6F, -1F, 2, 6, 2, NONE)
                .texOffs(242, 133).addBox(-1.5F, -2F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, 0.384F));
        PartDefinition p_tendril_r3 = p_tendril_r2.addOrReplaceChild("tendril_r3", CubeListBuilder.create()
                .texOffs(52, 120).addBox(-1F, -5F, -1F, 2, 5, 2, NONE)
                .texOffs(188, 142).addBox(-1.5F, -3F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, 0.349F));
        PartDefinition p_tendril_r4 = p_tendril_r3.addOrReplaceChild("tendril_r4", CubeListBuilder.create()
                .texOffs(126, 127).addBox(-0.5F, -5F, -0.5F, 1, 5, 1, NONE)
                .texOffs(62, 138).addBox(-1F, -7F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, -5F, 0F, 0F, 0F, 0.314F));
        PartDefinition p_tendril_l = p_torso.addOrReplaceChild("tendril_l", CubeListBuilder.create()
                .texOffs(56, 111).addBox(-1.5F, -6F, -1.5F, 3, 6, 3, NONE)
                .texOffs(34, 127).addBox(-2F, -1F, -2F, 4, 2, 4, NONE)
                .texOffs(192, 142).addBox(-1F, -4F, 1.2F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(3F, -11F, 3.5F, -0.489F, 0F, 1.082F));
        PartDefinition p_tendril_l2 = p_tendril_l.addOrReplaceChild("tendril_l2", CubeListBuilder.create()
                .texOffs(174, 111).addBox(-1F, -6F, -1F, 2, 6, 2, NONE)
                .texOffs(0, 138).addBox(-1.5F, -2F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, -0.384F));
        PartDefinition p_tendril_l3 = p_tendril_l2.addOrReplaceChild("tendril_l3", CubeListBuilder.create()
                .texOffs(60, 120).addBox(-1F, -5F, -1F, 2, 5, 2, NONE)
                .texOffs(196, 142).addBox(-1.5F, -3F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, -0.349F));
        PartDefinition p_tendril_l4 = p_tendril_l3.addOrReplaceChild("tendril_l4", CubeListBuilder.create()
                .texOffs(130, 127).addBox(-0.5F, -5F, -0.5F, 1, 5, 1, NONE)
                .texOffs(70, 138).addBox(-1F, -7F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, -5F, 0F, 0F, 0F, -0.314F));
        PartDefinition p_robe_r = p_robe.addOrReplaceChild("robe_r", CubeListBuilder.create()
                .texOffs(224, 31).addBox(-3.5F, 0F, -1F, 6, 22, 2, NONE)
                .texOffs(104, 78).addBox(-3F, 4F, -1.5F, 1, 16, 1, NONE)
                .texOffs(200, 142).addBox(-3F, 22F, -0.5F, 1, 1, 1, NONE)
                .texOffs(8, 142).addBox(0F, 22F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-3F, 0F, -3.5F, -0.157F, 0F, 0.122F));
        PartDefinition p_robe_l = p_robe.addOrReplaceChild("robe_l", CubeListBuilder.create()
                .texOffs(240, 31).addBox(-2.5F, 0F, -1F, 6, 22, 2, NONE)
                .texOffs(108, 78).addBox(2F, 4F, -1.5F, 1, 16, 1, NONE)
                .texOffs(204, 142).addBox(2F, 22F, -0.5F, 1, 1, 1, NONE)
                .texOffs(12, 142).addBox(-1F, 22F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(3F, 0F, -3.5F, -0.157F, 0F, -0.122F));
        PartDefinition p_robe_b = p_robe.addOrReplaceChild("robe_b", CubeListBuilder.create()
                .texOffs(196, 31).addBox(-6F, 0F, -1F, 12, 22, 2, NONE)
                .texOffs(134, 127).addBox(-5F, 0F, -1.5F, 10, 4, 1, NONE)
                .texOffs(204, 133).addBox(-4F, 10F, 1F, 4, 1, 3, NONE)
                .texOffs(134, 138).addBox(1F, 14F, 1F, 3, 1, 2, NONE)
                .texOffs(216, 78).addBox(-1F, 6F, 0.6F, 1, 14, 1, NONE)
                .texOffs(208, 142).addBox(-6F, 22F, -0.5F, 1, 1, 1, NONE)
                .texOffs(16, 142).addBox(-3F, 22F, -0.5F, 1, 2, 1, NONE)
                .texOffs(212, 142).addBox(0F, 22F, -0.5F, 1, 1, 1, NONE)
                .texOffs(20, 142).addBox(3F, 22F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 3.5F, 0.175F, 0F, 0F));
        PartDefinition p_robe_sr = p_robe.addOrReplaceChild("robe_sr", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1F, 0F, -4.5F, 2, 22, 9, NONE)
                .texOffs(32, 120).addBox(-2F, 8F, -2F, 1, 3, 4, NONE)
                .texOffs(56, 133).addBox(-1.6F, 13F, 0F, 1, 2, 3, NONE)
                .texOffs(216, 142).addBox(-0.5F, 22F, -4F, 1, 1, 1, NONE)
                .texOffs(24, 142).addBox(-0.5F, 22F, -1F, 1, 2, 1, NONE)
                .texOffs(220, 142).addBox(-0.5F, 22F, 2F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-5F, 0F, 0F, 0F, 0F, 0.175F));
        PartDefinition p_robe_sl = p_robe.addOrReplaceChild("robe_sl", CubeListBuilder.create()
                .texOffs(22, 0).addBox(-1F, 0F, -4.5F, 2, 22, 9, NONE)
                .texOffs(42, 120).addBox(1F, 8F, -2F, 1, 3, 4, NONE)
                .texOffs(64, 133).addBox(0.6F, 13F, 0F, 1, 2, 3, NONE)
                .texOffs(224, 142).addBox(-0.5F, 22F, -4F, 1, 1, 1, NONE)
                .texOffs(28, 142).addBox(-0.5F, 22F, -1F, 1, 2, 1, NONE)
                .texOffs(228, 142).addBox(-0.5F, 22F, 2F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(5F, 0F, 0F, 0F, 0F, -0.175F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    public static final Role[] MYCELIAL_QUEEN_ROLES = {new Role("robe/torso", "body", 0.00F), new Role("robe/torso/head", "head", 0.00F), new Role("robe/torso/head/cap/veil0", "tentacle", 0.00F), new Role("robe/torso/head/cap/veil1", "tentacle", 1.10F), new Role("robe/torso/head/cap/veil2", "tentacle", 2.20F), new Role("robe/torso/head/cap/veil3", "tentacle", 3.30F), new Role("robe/torso/head/cap/veil4", "tentacle", 4.40F), new Role("robe/torso/head/cap/veil5", "tentacle", 5.50F), new Role("robe/torso/head/cap/veil6", "tentacle", 0.70F), new Role("robe/torso/arm_r", "arm_r", 0.00F), new Role("robe/torso/arm_r/fore_r/hand_r/finger_r0", "tentacle", 0.00F), new Role("robe/torso/arm_r/fore_r/hand_r/finger_r1", "tentacle", 0.80F), new Role("robe/torso/arm_r/fore_r/hand_r/finger_r2", "tentacle", 1.60F), new Role("robe/torso/arm_r/fore_r/hand_r/finger_r3", "tentacle", 2.40F), new Role("robe/torso/arm_l", "arm_l", 0.00F), new Role("robe/torso/arm_l/fore_l/hand_l/finger_l0", "tentacle", 0.00F), new Role("robe/torso/arm_l/fore_l/hand_l/finger_l1", "tentacle", 0.80F), new Role("robe/torso/arm_l/fore_l/hand_l/finger_l2", "tentacle", 1.60F), new Role("robe/torso/arm_l/fore_l/hand_l/finger_l3", "tentacle", 2.40F), new Role("robe/torso/tendril_r", "tentacle", 0.00F), new Role("robe/torso/tendril_r/tendril_r2", "tentacle", 1.00F), new Role("robe/torso/tendril_r/tendril_r2/tendril_r3", "tentacle", 2.00F), new Role("robe/torso/tendril_r/tendril_r2/tendril_r3/tendril_r4", "tentacle", 3.00F), new Role("robe/torso/tendril_l", "tentacle", 0.00F), new Role("robe/torso/tendril_l/tendril_l2", "tentacle", 1.00F), new Role("robe/torso/tendril_l/tendril_l2/tendril_l3", "tentacle", 2.00F), new Role("robe/torso/tendril_l/tendril_l2/tendril_l3/tendril_l4", "tentacle", 3.00F), new Role("robe/robe_r", "leg_r", 0.00F), new Role("robe/robe_l", "leg_l", 0.00F)};

    public static final Key[] MYCELIAL_QUEEN_POSES = {
            new Key(0, 0F, "robe/torso", 8F, 0F, 0F),
            new Key(0, 0F, "robe/torso/arm_r", -35F, 0F, -28F),
            new Key(0, 0F, "robe/torso/arm_l", -35F, 0F, 28F),
            new Key(0, 0F, "robe/torso/arm_r/fore_r", -45F, 0F, 0F),
            new Key(0, 0F, "robe/torso/arm_l/fore_l", -45F, 0F, 0F),
            new Key(0, 0F, "robe/torso/tendril_r", 25F, 0F, 35F),
            new Key(0, 0F, "robe/torso/tendril_l", 25F, 0F, -35F),
            new Key(1, 0F, "robe/torso/arm_r", 0F, 0F, 0F),
            new Key(1, 8F, "robe/torso/arm_r", -30F, 0F, 80F),
            new Key(1, 32F, "robe/torso/arm_r", -30F, 0F, 80F),
            new Key(1, 40F, "robe/torso/arm_r", 0F, 0F, 0F),
            new Key(1, 0F, "robe/torso/arm_l", 0F, 0F, 0F),
            new Key(1, 8F, "robe/torso/arm_l", -30F, 0F, -80F),
            new Key(1, 32F, "robe/torso/arm_l", -30F, 0F, -80F),
            new Key(1, 40F, "robe/torso/arm_l", 0F, 0F, 0F),
            new Key(1, 0F, "robe/torso/arm_r/fore_r", 0F, 0F, 0F),
            new Key(1, 8F, "robe/torso/arm_r/fore_r", 15F, 0F, 0F),
            new Key(1, 32F, "robe/torso/arm_r/fore_r", 15F, 0F, 0F),
            new Key(1, 40F, "robe/torso/arm_r/fore_r", 0F, 0F, 0F),
            new Key(1, 0F, "robe/torso/arm_l/fore_l", 0F, 0F, 0F),
            new Key(1, 8F, "robe/torso/arm_l/fore_l", 15F, 0F, 0F),
            new Key(1, 32F, "robe/torso/arm_l/fore_l", 15F, 0F, 0F),
            new Key(1, 40F, "robe/torso/arm_l/fore_l", 0F, 0F, 0F),
            new Key(1, 0F, "robe/torso/head", 0F, 0F, 0F),
            new Key(1, 8F, "robe/torso/head", -22F, 0F, 0F),
            new Key(1, 32F, "robe/torso/head", -22F, 0F, 0F),
            new Key(1, 40F, "robe/torso/head", 0F, 0F, 0F),
            new Key(1, 0F, "robe/torso", 0F, 0F, 0F),
            new Key(1, 8F, "robe/torso", -8F, 0F, 0F),
            new Key(1, 32F, "robe/torso", -8F, 0F, 0F),
            new Key(1, 40F, "robe/torso", 0F, 0F, 0F),
            new Key(1, 0F, "robe/torso/tendril_r", 0F, 0F, 0F),
            new Key(1, 8F, "robe/torso/tendril_r", -20F, 0F, 25F),
            new Key(1, 32F, "robe/torso/tendril_r", -20F, 0F, 25F),
            new Key(1, 40F, "robe/torso/tendril_r", 0F, 0F, 0F),
            new Key(1, 0F, "robe/torso/tendril_l", 0F, 0F, 0F),
            new Key(1, 8F, "robe/torso/tendril_l", -20F, 0F, -25F),
            new Key(1, 32F, "robe/torso/tendril_l", -20F, 0F, -25F),
            new Key(1, 40F, "robe/torso/tendril_l", 0F, 0F, 0F),
            new Key(2, 0F, "robe/torso/arm_r", 0F, 0F, 0F),
            new Key(2, 12F, "robe/torso/arm_r", -35F, 0F, 115F),
            new Key(2, 17F, "robe/torso/arm_r", -35F, 0F, 120F),
            new Key(2, 18F, "robe/torso/arm_r", -40F, 0F, 25F),
            new Key(2, 20F, "robe/torso/arm_r", 0F, 0F, 0F),
            new Key(2, 0F, "robe/torso/arm_l", 0F, 0F, 0F),
            new Key(2, 12F, "robe/torso/arm_l", -35F, 0F, -115F),
            new Key(2, 17F, "robe/torso/arm_l", -35F, 0F, -120F),
            new Key(2, 18F, "robe/torso/arm_l", -40F, 0F, -25F),
            new Key(2, 20F, "robe/torso/arm_l", 0F, 0F, 0F),
            new Key(2, 0F, "robe/torso", 0F, 0F, 0F),
            new Key(2, 12F, "robe/torso", -10F, 0F, 0F),
            new Key(2, 17F, "robe/torso", -10F, 0F, 0F),
            new Key(2, 18F, "robe/torso", 18F, 0F, 0F),
            new Key(2, 20F, "robe/torso", 0F, 0F, 0F),
            new Key(2, 0F, "robe/torso/head", 0F, 0F, 0F),
            new Key(2, 12F, "robe/torso/head", -15F, 0F, 0F),
            new Key(2, 18F, "robe/torso/head", 15F, 0F, 0F),
            new Key(2, 20F, "robe/torso/head", 0F, 0F, 0F),
            new Key(2, 0F, "robe/torso/tendril_r", 0F, 0F, 0F),
            new Key(2, 12F, "robe/torso/tendril_r", -35F, 0F, 30F),
            new Key(2, 18F, "robe/torso/tendril_r", 30F, 0F, -10F),
            new Key(2, 20F, "robe/torso/tendril_r", 0F, 0F, 0F),
            new Key(2, 0F, "robe/torso/tendril_l", 0F, 0F, 0F),
            new Key(2, 12F, "robe/torso/tendril_l", -35F, 0F, -30F),
            new Key(2, 18F, "robe/torso/tendril_l", 30F, 0F, 10F),
            new Key(2, 20F, "robe/torso/tendril_l", 0F, 0F, 0F),
            new Key(3, 0F, "robe/torso/tendril_r", 0F, 0F, 0F),
            new Key(3, 6F, "robe/torso/tendril_r", -50F, 0F, 20F),
            new Key(3, 10F, "robe/torso/tendril_r", 85F, 0F, -15F),
            new Key(3, 14F, "robe/torso/tendril_r", 0F, 0F, 0F),
            new Key(3, 0F, "robe/torso/tendril_l", 0F, 0F, 0F),
            new Key(3, 6F, "robe/torso/tendril_l", -50F, 0F, -20F),
            new Key(3, 10F, "robe/torso/tendril_l", 85F, 0F, 15F),
            new Key(3, 14F, "robe/torso/tendril_l", 0F, 0F, 0F),
            new Key(3, 0F, "robe/torso/tendril_r/tendril_r2", 0F, 0F, 0F),
            new Key(3, 6F, "robe/torso/tendril_r/tendril_r2", -20F, 0F, 0F),
            new Key(3, 10F, "robe/torso/tendril_r/tendril_r2", 40F, 0F, 0F),
            new Key(3, 14F, "robe/torso/tendril_r/tendril_r2", 0F, 0F, 0F),
            new Key(3, 0F, "robe/torso/tendril_l/tendril_l2", 0F, 0F, 0F),
            new Key(3, 6F, "robe/torso/tendril_l/tendril_l2", -20F, 0F, 0F),
            new Key(3, 10F, "robe/torso/tendril_l/tendril_l2", 40F, 0F, 0F),
            new Key(3, 14F, "robe/torso/tendril_l/tendril_l2", 0F, 0F, 0F),
            new Key(3, 0F, "robe/torso", 0F, 0F, 0F),
            new Key(3, 6F, "robe/torso", -5F, 30F, 0F),
            new Key(3, 10F, "robe/torso", 10F, -35F, 0F),
            new Key(3, 14F, "robe/torso", 0F, 0F, 0F),
            new Key(3, 0F, "robe/torso/arm_r", 0F, 0F, 0F),
            new Key(3, 6F, "robe/torso/arm_r", 10F, 0F, 45F),
            new Key(3, 10F, "robe/torso/arm_r", -70F, 30F, 10F),
            new Key(3, 14F, "robe/torso/arm_r", 0F, 0F, 0F),
            new Key(3, 0F, "robe/torso/arm_l", 0F, 0F, 0F),
            new Key(3, 6F, "robe/torso/arm_l", -40F, 0F, -20F),
            new Key(3, 10F, "robe/torso/arm_l", 20F, 0F, -40F),
            new Key(3, 14F, "robe/torso/arm_l", 0F, 0F, 0F),
            new Key(4, 0F, "robe/torso/head", 0F, 0F, 0F),
            new Key(4, 5F, "robe/torso/head", -22F, 0F, 0F),
            new Key(4, 8F, "robe/torso/head", 18F, 0F, 0F),
            new Key(4, 12F, "robe/torso/head", 0F, 0F, 0F),
            new Key(4, 0F, "robe/torso", 0F, 0F, 0F),
            new Key(4, 5F, "robe/torso", -10F, 0F, 0F),
            new Key(4, 8F, "robe/torso", 10F, 0F, 0F),
            new Key(4, 12F, "robe/torso", 0F, 0F, 0F),
            new Key(4, 0F, "robe/torso/arm_r", 0F, 0F, 0F),
            new Key(4, 5F, "robe/torso/arm_r", -50F, 0F, 30F),
            new Key(4, 8F, "robe/torso/arm_r", -95F, 0F, 5F),
            new Key(4, 12F, "robe/torso/arm_r", 0F, 0F, 0F),
            new Key(4, 0F, "robe/torso/arm_l", 0F, 0F, 0F),
            new Key(4, 5F, "robe/torso/arm_l", -50F, 0F, -30F),
            new Key(4, 8F, "robe/torso/arm_l", -95F, 0F, -5F),
            new Key(4, 12F, "robe/torso/arm_l", 0F, 0F, 0F),
            new Key(5, 0F, "robe/torso", 0F, 0F, 0F),
            new Key(5, 8F, "robe/torso", 20F, 0F, 0F),
            new Key(5, 18F, "robe/torso", 20F, 0F, 0F),
            new Key(5, 24F, "robe/torso", 0F, 0F, 0F),
            new Key(5, 0F, "robe/torso/arm_r", 0F, 0F, 0F),
            new Key(5, 8F, "robe/torso/arm_r", -45F, 0F, 35F),
            new Key(5, 18F, "robe/torso/arm_r", -45F, 0F, 35F),
            new Key(5, 24F, "robe/torso/arm_r", 0F, 0F, 0F),
            new Key(5, 0F, "robe/torso/arm_l", 0F, 0F, 0F),
            new Key(5, 8F, "robe/torso/arm_l", -45F, 0F, -35F),
            new Key(5, 18F, "robe/torso/arm_l", -45F, 0F, -35F),
            new Key(5, 24F, "robe/torso/arm_l", 0F, 0F, 0F),
            new Key(5, 0F, "robe/torso/head", 0F, 0F, 0F),
            new Key(5, 8F, "robe/torso/head", 5F, 0F, 0F),
            new Key(5, 18F, "robe/torso/head", 5F, 0F, 0F),
            new Key(5, 24F, "robe/torso/head", 0F, 0F, 0F)};

    public static final float MYCELIAL_QUEEN_SCALE = 1.4F;
}
