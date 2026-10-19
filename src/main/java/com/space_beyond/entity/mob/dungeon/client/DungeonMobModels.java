package com.space_beyond.entity.mob.dungeon.client;

import com.space_beyond.entity.mob.stage.client.StageBossModel.Key;
import com.space_beyond.entity.mob.stage.client.StageBossModel.Role;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Modelle der Dungeon-Mobs (generiert von {@code tools/bossgen/build_mobs.py} aus {@code mob_<name>.py}; nicht von Hand
 * pflegen). Je Mob: {@link LayerDefinition}, Rollen fuer die Grundbewegung, Schluesselposen (1 Schlag, 2 Schuss,
 * 3 Spezial) und Groesse.
 */
public final class DungeonMobModels {

    private DungeonMobModels() {}

    private static final CubeDeformation NONE = CubeDeformation.NONE;

    /** Moon Drone: Sicherheitsdrohne der Mondstation mit rotem Kameraauge, Rotor-Mantelringen und Laserlauf. Textur {@code entity/dungeon/moon_drone.png}. */
    public static LayerDefinition moonDrone() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_hull = root.addOrReplaceChild("hull", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5F, -5F, -5F, 10, 10, 10, NONE)
                .texOffs(0, 20).addBox(-6F, -4F, -4F, 12, 8, 8, NONE)
                .texOffs(40, 0).addBox(-4F, -4F, -6F, 8, 8, 12, NONE)
                .texOffs(80, 0).addBox(-4F, -6F, -4F, 8, 12, 8, NONE)
                .texOffs(84, 20).addBox(-6.5F, 0F, -4.5F, 13, 2, 9, NONE)
                .texOffs(40, 20).addBox(-4.5F, 0F, -6.5F, 9, 2, 13, NONE)
                .texOffs(102, 36).addBox(-3.5F, -3.5F, -7F, 7, 7, 1, NONE)
                .texOffs(0, 55).addBox(-2F, -2F, -7.5F, 4, 4, 1, NONE)
                .texOffs(86, 55).addBox(-1.5F, -1.5F, -8F, 3, 3, 1, NONE)
                .texOffs(56, 60).addBox(-4F, -4.5F, -8F, 8, 1, 2, NONE)
                .texOffs(40, 47).addBox(-3F, -7F, -3F, 6, 1, 6, NONE)
                .texOffs(76, 60).addBox(-1F, -7.5F, -1F, 2, 1, 2, NONE)
                .texOffs(64, 47).addBox(-2.5F, -2.5F, 6F, 5, 5, 2, NONE)
                .texOffs(94, 55).addBox(-1.5F, -1.5F, 7.5F, 3, 3, 1, NONE)
                .texOffs(16, 47).addBox(-6.5F, -3.5F, -2.5F, 1, 3, 5, NONE)
                .texOffs(28, 47).addBox(5.5F, -3.5F, -2.5F, 1, 3, 5, NONE)
                .texOffs(92, 60).addBox(-7F, -3F, 2.5F, 1, 2, 1, NONE)
                .texOffs(96, 60).addBox(6F, -3F, 2.5F, 1, 2, 1, NONE)
                .texOffs(88, 64).addBox(-4.5F, 2.5F, -6.5F, 2, 1, 1, NONE)
                .texOffs(94, 64).addBox(2.5F, 2.5F, -6.5F, 2, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 15F, 0F, 0F, 0F, 0F));
        PartDefinition p_duct_r = p_hull.addOrReplaceChild("duct_r", CubeListBuilder.create()
                .texOffs(102, 55).addBox(-2F, -1F, -1F, 2, 2, 2, NONE)
                .texOffs(14, 55).addBox(-10F, -1.5F, -4F, 8, 3, 1, NONE)
                .texOffs(32, 55).addBox(-10F, -1.5F, 3F, 8, 3, 1, NONE)
                .texOffs(46, 36).addBox(-3F, -1.5F, -3F, 1, 3, 6, NONE)
                .texOffs(60, 36).addBox(-10F, -1.5F, -3F, 1, 3, 6, NONE)
                .texOffs(100, 60).addBox(-10.5F, 0.5F, -4.5F, 9, 1, 1, NONE)
                .texOffs(0, 64).addBox(-10.5F, 0.5F, 3.5F, 9, 1, 1, NONE)
                .texOffs(32, 60).addBox(-10.5F, -1F, -1F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(-5.5F, -1F, 0F, 0.14F, 0F, 0.209F));
        PartDefinition p_rotor_r = p_duct_r.addOrReplaceChild("rotor_r", CubeListBuilder.create()
                .texOffs(60, 64).addBox(-3F, -0.5F, -0.5F, 6, 1, 1, NONE)
                .texOffs(78, 47).addBox(-0.5F, -0.5F, -3F, 1, 1, 6, NONE)
                .texOffs(110, 55).addBox(-1F, -1.5F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(-6F, 0F, 0F, 0F, -0.524F, 0F));
        PartDefinition p_duct_l = p_hull.addOrReplaceChild("duct_l", CubeListBuilder.create()
                .texOffs(118, 55).addBox(0F, -1F, -1F, 2, 2, 2, NONE)
                .texOffs(50, 55).addBox(2F, -1.5F, -4F, 8, 3, 1, NONE)
                .texOffs(68, 55).addBox(2F, -1.5F, 3F, 8, 3, 1, NONE)
                .texOffs(74, 36).addBox(2F, -1.5F, -3F, 1, 3, 6, NONE)
                .texOffs(88, 36).addBox(9F, -1.5F, -3F, 1, 3, 6, NONE)
                .texOffs(20, 64).addBox(1.5F, 0.5F, -4.5F, 9, 1, 1, NONE)
                .texOffs(40, 64).addBox(1.5F, 0.5F, 3.5F, 9, 1, 1, NONE)
                .texOffs(38, 60).addBox(9.5F, -1F, -1F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(5.5F, -1F, 0F, 0.14F, 0F, -0.209F));
        PartDefinition p_rotor_l = p_duct_l.addOrReplaceChild("rotor_l", CubeListBuilder.create()
                .texOffs(74, 64).addBox(-3F, -0.5F, -0.5F, 6, 1, 1, NONE)
                .texOffs(92, 47).addBox(-0.5F, -0.5F, -3F, 1, 1, 6, NONE)
                .texOffs(0, 60).addBox(-1F, -1.5F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(6F, 0F, 0F, 0F, 0.524F, 0F));
        PartDefinition p_gun = p_hull.addOrReplaceChild("gun", CubeListBuilder.create()
                .texOffs(22, 36).addBox(-3F, -0.5F, -3F, 6, 3, 6, NONE)
                .texOffs(110, 47).addBox(-2F, 2F, -2F, 4, 1, 4, NONE)
                .texOffs(0, 36).addBox(-1F, 0.5F, -11F, 2, 2, 9, NONE)
                .texOffs(8, 60).addBox(-1.5F, 0F, -6F, 3, 3, 1, NONE)
                .texOffs(16, 60).addBox(-1.5F, 0F, -8F, 3, 3, 1, NONE)
                .texOffs(24, 60).addBox(-1.5F, 0F, -11.5F, 3, 3, 1, NONE)
                .texOffs(100, 64).addBox(-0.5F, 1F, -12F, 1, 1, 1, NONE)
                .texOffs(0, 47).addBox(-0.5F, 2.5F, -10F, 1, 1, 7, NONE)
                .texOffs(44, 60).addBox(-3.5F, 0F, -1F, 1, 2, 2, NONE)
                .texOffs(50, 60).addBox(2.5F, 0F, -1F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 5F, -1.5F, 0F, 0F, 0F));
        PartDefinition p_antenna_a = p_hull.addOrReplaceChild("antenna_a", CubeListBuilder.create()
                .texOffs(106, 47).addBox(-0.5F, -6F, -0.5F, 1, 6, 1, NONE)
                .texOffs(84, 60).addBox(-1F, -7F, -1F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(-2.5F, -6F, 2F, -0.209F, 0F, -0.244F));
        PartDefinition p_antenna_b = p_hull.addOrReplaceChild("antenna_b", CubeListBuilder.create()
                .texOffs(10, 55).addBox(-0.5F, -4F, -0.5F, 1, 4, 1, NONE)
                .texOffs(104, 64).addBox(-0.5F, -5F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(2.5F, -6F, 3F, -0.349F, 0F, 0.314F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final Role[] MOON_DRONE_ROLES = {new Role("hull", "bob", 0.00F)};

    public static final Key[] MOON_DRONE_POSES = {
            new Key(2, 0F, "hull", 0F, 0F, 0F),
            new Key(2, 4F, "hull", 10F, 0F, 0F),
            new Key(2, 7F, "hull", -8F, 0F, 0F),
            new Key(2, 11F, "hull", -3F, 0F, 0F),
            new Key(2, 14F, "hull", 0F, 0F, 0F),
            new Key(2, 0F, "hull/gun", 0F, 0F, 0F),
            new Key(2, 4F, "hull/gun", 12F, 0F, 0F),
            new Key(2, 7F, "hull/gun", -14F, 0F, 0F),
            new Key(2, 11F, "hull/gun", -4F, 0F, 0F),
            new Key(2, 14F, "hull/gun", 0F, 0F, 0F),
            new Key(2, 0F, "hull/duct_r", 0F, 0F, 0F),
            new Key(2, 4F, "hull/duct_r", -20F, 0F, 0F),
            new Key(2, 9F, "hull/duct_r", 10F, 0F, 0F),
            new Key(2, 14F, "hull/duct_r", 0F, 0F, 0F),
            new Key(2, 0F, "hull/duct_l", 0F, 0F, 0F),
            new Key(2, 4F, "hull/duct_l", -20F, 0F, 0F),
            new Key(2, 9F, "hull/duct_l", 10F, 0F, 0F),
            new Key(2, 14F, "hull/duct_l", 0F, 0F, 0F),
            new Key(2, 0F, "hull/antenna_a", 0F, 0F, 0F),
            new Key(2, 7F, "hull/antenna_a", 25F, 0F, 0F),
            new Key(2, 14F, "hull/antenna_a", 0F, 0F, 0F),
            new Key(2, 0F, "hull/antenna_b", 0F, 0F, 0F),
            new Key(2, 7F, "hull/antenna_b", 25F, 0F, 0F),
            new Key(2, 14F, "hull/antenna_b", 0F, 0F, 0F)};

    public static final float MOON_DRONE_SCALE = 0.7F;

    /** Lost Astronaut: untoter Astronaut im zerschlissenen Raumanzug mit gesprungenem Visier und Lebenserhaltungs-Rucksack. Textur {@code entity/dungeon/lost_astronaut.png}. */
    public static LayerDefinition lostAstronaut() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(64, 0).addBox(-4.5F, -12F, -2.5F, 9, 12, 5, NONE)
                .texOffs(0, 42).addBox(-5F, -5F, -3F, 10, 2, 6, NONE)
                .texOffs(32, 42).addBox(-5F, -2F, -3F, 10, 2, 6, NONE)
                .texOffs(64, 42).addBox(-4F, -13.5F, -3F, 8, 2, 6, NONE)
                .texOffs(70, 64).addBox(-3F, -11F, -3.5F, 6, 4, 1, NONE)
                .texOffs(124, 75).addBox(-2.5F, -10.5F, -4F, 1, 1, 1, NONE)
                .texOffs(0, 78).addBox(-0.5F, -10.5F, -4F, 1, 1, 1, NONE)
                .texOffs(4, 78).addBox(1.5F, -10.5F, -4F, 1, 1, 1, NONE)
                .texOffs(90, 75).addBox(-2.5F, -8.5F, -4F, 5, 1, 1, NONE)
                .texOffs(12, 70).addBox(1.5F, -7F, -3F, 2, 3, 1, NONE)
                .texOffs(106, 70).addBox(-4F, -12F, -3F, 2, 2, 1, NONE)
                .texOffs(102, 75).addBox(-1.5F, -1.5F, -3.5F, 3, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 12F, 0F, 0.279F, 0F, 0.052F));
        PartDefinition p_head = p_body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.5F, -9F, -4.5F, 9, 9, 9, NONE)
                .texOffs(92, 0).addBox(-5F, -8F, -3.5F, 10, 7, 7, NONE)
                .texOffs(36, 0).addBox(-3.5F, -10F, -3.5F, 7, 11, 7, NONE)
                .texOffs(0, 64).addBox(-3F, -7F, -4.2F, 6, 5, 1, NONE)
                .texOffs(112, 70).addBox(0F, -5F, -4.6F, 2, 2, 1, NONE)
                .texOffs(118, 75).addBox(-3F, -5.5F, -4.6F, 2, 1, 1, NONE)
                .texOffs(36, 50).addBox(-3.5F, -7.5F, -5F, 3, 6, 1, NONE)
                .texOffs(6, 75).addBox(-0.5F, -7.5F, -5F, 1, 2, 1, NONE)
                .texOffs(118, 70).addBox(1.5F, -7.5F, -5F, 2, 2, 1, NONE)
                .texOffs(118, 64).addBox(2.5F, -5.5F, -5F, 1, 4, 1, NONE)
                .texOffs(110, 75).addBox(-0.5F, -2.5F, -5F, 3, 1, 1, NONE)
                .texOffs(8, 78).addBox(0.5F, -7.5F, -5.3F, 1, 1, 1, NONE)
                .texOffs(12, 78).addBox(-1.5F, -4.5F, -5.3F, 1, 1, 1, NONE)
                .texOffs(16, 78).addBox(2.5F, -6.5F, -5.3F, 1, 1, 1, NONE)
                .texOffs(26, 75).addBox(-4F, -8.5F, -5.5F, 8, 1, 1, NONE)
                .texOffs(44, 75).addBox(-4F, -1.5F, -5.5F, 8, 1, 1, NONE)
                .texOffs(48, 32).addBox(-5F, -8.5F, -5.5F, 1, 8, 2, NONE)
                .texOffs(54, 32).addBox(4F, -8.5F, -5.5F, 1, 8, 2, NONE)
                .texOffs(26, 64).addBox(-5.5F, -6F, -1.5F, 1, 3, 3, NONE)
                .texOffs(34, 64).addBox(4.5F, -6F, -1.5F, 1, 3, 3, NONE)
                .texOffs(18, 70).addBox(4.5F, -9F, -2F, 1, 2, 2, NONE)
                .texOffs(122, 64).addBox(-4F, -13F, 1F, 1, 4, 1, NONE)
                .texOffs(90, 70).addBox(-4.5F, -13.5F, 0.5F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, -12F, -0.5F, 0.175F, 0.07F, -0.244F));
        PartDefinition p_arm_r = p_body.addOrReplaceChild("arm_r", CubeListBuilder.create()
                .texOffs(90, 18).addBox(-3.5F, -1F, -2F, 4, 7, 4, NONE)
                .texOffs(92, 42).addBox(-4F, -2F, -2.5F, 5, 3, 5, NONE)
                .texOffs(24, 70).addBox(-3.5F, 1F, -1F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(-5.5F, -10F, 0F, -1.257F, 0.14F, 0.07F));
        PartDefinition p_fore_r = p_arm_r.addOrReplaceChild("fore_r", CubeListBuilder.create()
                .texOffs(60, 32).addBox(-2F, 0F, -2F, 4, 5, 4, NONE)
                .texOffs(92, 50).addBox(-2.5F, 0F, -2.5F, 5, 1, 5, NONE)
                .texOffs(0, 58).addBox(-2.5F, 3.5F, -2.5F, 5, 1, 5, NONE)
                .texOffs(20, 50).addBox(-2F, 5F, -2F, 4, 3, 4, NONE)
                .texOffs(10, 75).addBox(-1.5F, 8F, -2F, 1, 2, 1, NONE)
                .texOffs(14, 75).addBox(0.5F, 8F, -2F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-1.5F, 5.5F, 0F, -0.314F, 0F, 0F));
        PartDefinition p_arm_l = p_body.addOrReplaceChild("arm_l", CubeListBuilder.create()
                .texOffs(106, 18).addBox(-0.5F, -1F, -2F, 4, 7, 4, NONE)
                .texOffs(0, 50).addBox(-1F, -2F, -2.5F, 5, 3, 5, NONE)
                .texOffs(30, 70).addBox(2.5F, 1F, -1F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(5.5F, -10F, 0F, -0.663F, -0.105F, -0.14F));
        PartDefinition p_fore_l = p_arm_l.addOrReplaceChild("fore_l", CubeListBuilder.create()
                .texOffs(76, 32).addBox(-2F, 0F, -2F, 4, 5, 4, NONE)
                .texOffs(20, 58).addBox(-2.5F, 0F, -2.5F, 5, 1, 5, NONE)
                .texOffs(40, 58).addBox(-2.5F, 3.5F, -2.5F, 5, 1, 5, NONE)
                .texOffs(14, 64).addBox(-1.5F, 5F, -1.5F, 3, 3, 3, NONE)
                .texOffs(42, 70).addBox(-1.5F, 8F, -1.5F, 1, 3, 1, NONE)
                .texOffs(46, 70).addBox(0.5F, 8F, -1.5F, 1, 3, 1, NONE)
                .texOffs(18, 75).addBox(-1.5F, 8F, 0.5F, 1, 2, 1, NONE)
                .texOffs(60, 58).addBox(-2.5F, 4F, -2.5F, 5, 1, 5, NONE),
                PartPose.offsetAndRotation(1.5F, 5.5F, 0F, -0.524F, 0F, 0F));
        PartDefinition p_pack = p_body.addOrReplaceChild("pack", CubeListBuilder.create()
                .texOffs(0, 18).addBox(-4F, -6F, 0F, 8, 11, 3, NONE)
                .texOffs(62, 18).addBox(-4.5F, -6.5F, 3F, 4, 10, 3, NONE)
                .texOffs(76, 18).addBox(0.5F, -6.5F, 3F, 4, 10, 3, NONE)
                .texOffs(50, 70).addBox(-4F, -7.5F, 3.5F, 3, 1, 2, NONE)
                .texOffs(60, 70).addBox(1F, -7.5F, 3.5F, 3, 1, 2, NONE)
                .texOffs(52, 64).addBox(-3F, 4F, 0.5F, 6, 2, 3, NONE)
                .texOffs(92, 32).addBox(-0.5F, -4F, 3F, 1, 7, 2, NONE)
                .texOffs(20, 78).addBox(-0.5F, -2F, 5.5F, 1, 1, 1, NONE)
                .texOffs(24, 78).addBox(-5F, -3F, 0.5F, 1, 1, 1, NONE)
                .texOffs(28, 78).addBox(4F, -3F, 0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -6F, 2.5F, 0F, 0F, 0F));
        PartDefinition p_hose_loose = p_pack.addOrReplaceChild("hose_loose", CubeListBuilder.create()
                .texOffs(48, 64).addBox(-0.5F, 0F, -0.5F, 1, 5, 1, NONE)
                .texOffs(98, 70).addBox(-1F, 5F, -1F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(2.5F, -1.5F, 6.5F, 0.349F, 0F, 0.175F));
        PartDefinition p_tube_r = p_body.addOrReplaceChild("tube_r", CubeListBuilder.create()
                .texOffs(84, 64).addBox(-0.5F, -0.5F, -4F, 1, 1, 4, NONE)
                .texOffs(0, 70).addBox(-0.5F, -0.5F, -5F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(-4.5F, -9F, 1F, -0.436F, 0F, 0F));
        PartDefinition p_tube_l = p_body.addOrReplaceChild("tube_l", CubeListBuilder.create()
                .texOffs(94, 64).addBox(-0.5F, -0.5F, -4F, 1, 1, 4, NONE)
                .texOffs(4, 70).addBox(-0.5F, -0.5F, -5F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(4.5F, -9F, 1F, -0.436F, 0F, 0F));
        PartDefinition p_rag_0 = p_body.addOrReplaceChild("rag_0", CubeListBuilder.create()
                .texOffs(42, 64).addBox(-1F, 0F, -0.5F, 2, 5, 1, NONE),
                PartPose.offsetAndRotation(-3F, -1F, -3F, 0.14F, 0F, 0.105F));
        PartDefinition p_rag_1 = p_body.addOrReplaceChild("rag_1", CubeListBuilder.create()
                .texOffs(112, 64).addBox(-1F, 0F, -0.5F, 2, 4, 1, NONE),
                PartPose.offsetAndRotation(2.5F, -1F, -3F, 0.07F, 0F, -0.175F));
        PartDefinition p_rag_2 = p_body.addOrReplaceChild("rag_2", CubeListBuilder.create()
                .texOffs(8, 70).addBox(-1F, 0F, -0.5F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(4.5F, -1F, 1F, 0F, 0F, -0.244F));
        PartDefinition p_rag_3 = p_body.addOrReplaceChild("rag_3", CubeListBuilder.create()
                .texOffs(104, 64).addBox(-1F, 0F, -0.5F, 3, 4, 1, NONE),
                PartPose.offsetAndRotation(-1F, -1F, 3F, -0.175F, 0F, 0F));
        PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create()
                .texOffs(22, 18).addBox(-2.5F, -0.5F, -2.5F, 5, 8, 5, NONE)
                .texOffs(70, 70).addBox(-2F, 5.5F, -3F, 4, 2, 1, NONE)
                .texOffs(80, 58).addBox(-2.5F, 3F, -2.6F, 5, 1, 5, NONE)
                .texOffs(0, 32).addBox(-3F, 8F, -3.5F, 6, 4, 6, NONE)
                .texOffs(62, 75).addBox(-3F, 11F, -4.5F, 6, 1, 1, NONE)
                .texOffs(44, 50).addBox(-3.5F, 7.5F, -3F, 7, 1, 5, NONE)
                .texOffs(0, 75).addBox(0.5F, 0.5F, -2.6F, 2, 2, 1, NONE),
                PartPose.offsetAndRotation(-2.4F, 12F, 0.5F, 0F, 0.07F, 0.035F));
        PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create()
                .texOffs(42, 18).addBox(-2.5F, -0.5F, -2.5F, 5, 8, 5, NONE)
                .texOffs(80, 70).addBox(-2F, 5.5F, -3F, 4, 2, 1, NONE)
                .texOffs(100, 58).addBox(-2.5F, 3F, -2.6F, 5, 1, 5, NONE)
                .texOffs(24, 32).addBox(-3F, 8F, -3.5F, 6, 4, 6, NONE)
                .texOffs(76, 75).addBox(-3F, 11F, -4.5F, 6, 1, 1, NONE)
                .texOffs(68, 50).addBox(-3.5F, 7.5F, -3F, 7, 1, 5, NONE)
                .texOffs(36, 70).addBox(-2.6F, 1F, -2.7F, 2, 3, 1, NONE)
                .texOffs(22, 75).addBox(-2.2F, 1.5F, -2.8F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(2.4F, 12F, 0.5F, -0.14F, -0.175F, -0.052F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final Role[] LOST_ASTRONAUT_ROLES = {new Role("body", "body", 0.00F), new Role("body/head", "head", 0.00F), new Role("body/arm_r", "arm_r", 0.00F), new Role("body/arm_l", "arm_l", 0.00F), new Role("body/pack/hose_loose", "tentacle", 1.00F), new Role("body/rag_0", "tentacle", 0.00F), new Role("body/rag_1", "tentacle", 1.70F), new Role("body/rag_2", "tentacle", 3.40F), new Role("body/rag_3", "tentacle", 5.10F), new Role("leg_r", "leg_r", 0.00F), new Role("leg_l", "leg_l", 0.00F)};

    public static final Key[] LOST_ASTRONAUT_POSES = {
            new Key(1, 0F, "body/arm_r", 0F, 0F, 0F),
            new Key(1, 4F, "body/arm_r", -60F, 0F, 10F),
            new Key(1, 7F, "body/arm_r", 40F, 0F, -5F),
            new Key(1, 10F, "body/arm_r", 0F, 0F, 0F),
            new Key(1, 0F, "body/arm_l", 0F, 0F, 0F),
            new Key(1, 4F, "body/arm_l", -90F, 0F, -10F),
            new Key(1, 7F, "body/arm_l", 20F, 0F, 5F),
            new Key(1, 10F, "body/arm_l", 0F, 0F, 0F),
            new Key(1, 0F, "body", 0F, 0F, 0F),
            new Key(1, 4F, "body", -10F, 0F, 0F),
            new Key(1, 7F, "body", 14F, 0F, 0F),
            new Key(1, 10F, "body", 0F, 0F, 0F),
            new Key(1, 0F, "body/head", 0F, 0F, 0F),
            new Key(1, 4F, "body/head", -15F, 0F, 10F),
            new Key(1, 7F, "body/head", 10F, 0F, 0F),
            new Key(1, 10F, "body/head", 0F, 0F, 0F)};

    public static final float LOST_ASTRONAUT_SCALE = 0.92F;

    /** Frost Wisp: schwebender Eisgeist mit leuchtendem Kristallkern, Eiskrone, Minzaugen, kreisenden Splittern und Eiszacken-Schleier. Textur {@code entity/dungeon/frost_wisp.png}. */
    public static LayerDefinition frostWisp() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_wisp = root.addOrReplaceChild("wisp", CubeListBuilder.create()
                .texOffs(26, 0).addBox(-3F, -1F, -2F, 6, 7, 4, NONE)
                .texOffs(16, 13).addBox(-3.5F, -1.5F, -2.5F, 7, 2, 5, NONE)
                .texOffs(40, 13).addBox(-2.5F, 4F, -2F, 5, 3, 4, NONE),
                PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
        PartDefinition p_head = p_wisp.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.5F, -7F, -3F, 7, 7, 6, NONE)
                .texOffs(46, 0).addBox(-4F, -6F, -2.5F, 8, 5, 5, NONE)
                .texOffs(12, 28).addBox(-4F, -7.5F, -3.5F, 8, 2, 3, NONE)
                .texOffs(104, 38).addBox(-3F, -5F, -3.5F, 2, 1, 1, NONE)
                .texOffs(110, 38).addBox(1F, -5F, -3.5F, 2, 1, 1, NONE)
                .texOffs(32, 38).addBox(-3F, -4F, -3.4F, 2, 2, 1, NONE)
                .texOffs(38, 38).addBox(1F, -4F, -3.4F, 2, 2, 1, NONE)
                .texOffs(116, 34).addBox(-1.5F, -2F, -3.5F, 3, 2, 1, NONE)
                .texOffs(44, 38).addBox(-0.5F, -1F, -4F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -1.5F, 0F, 0F, 0F, 0F));
        PartDefinition p_crown_c = p_head.addOrReplaceChild("crown_c", CubeListBuilder.create()
                .texOffs(72, 21).addBox(-1.5F, -3F, -1.5F, 3, 3, 3, NONE)
                .texOffs(50, 28).addBox(-1F, -5F, -1F, 2, 2, 2, NONE)
                .texOffs(116, 38).addBox(-0.5F, -6F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -7F, 0F, -0.175F, 0F, 0F));
        PartDefinition p_crown_r = p_head.addOrReplaceChild("crown_r", CubeListBuilder.create()
                .texOffs(58, 28).addBox(-1F, -2F, -1F, 2, 2, 2, NONE)
                .texOffs(96, 34).addBox(-0.5F, -5F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(-2.5F, -6.5F, 0.5F, -0.262F, 0F, -0.611F));
        PartDefinition p_crown_l = p_head.addOrReplaceChild("crown_l", CubeListBuilder.create()
                .texOffs(66, 28).addBox(-1F, -2F, -1F, 2, 2, 2, NONE)
                .texOffs(100, 34).addBox(-0.5F, -5F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(2.5F, -6.5F, 0.5F, -0.262F, 0F, 0.611F));
        PartDefinition p_crown_b = p_head.addOrReplaceChild("crown_b", CubeListBuilder.create()
                .texOffs(74, 28).addBox(-1F, -2F, -1F, 2, 2, 2, NONE)
                .texOffs(104, 34).addBox(-0.5F, -5F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, -6F, 2.5F, -0.96F, 0F, 0F));
        PartDefinition p_core = p_wisp.addOrReplaceChild("core", CubeListBuilder.create()
                .texOffs(98, 0).addBox(-2F, -2F, -2F, 4, 4, 4, NONE)
                .texOffs(0, 13).addBox(-1.5F, -1.5F, -2.5F, 3, 3, 5, NONE),
                PartPose.offsetAndRotation(0F, 2F, -1F, 0F, 0F, 0.785F));
        PartDefinition p_spine = p_wisp.addOrReplaceChild("spine", CubeListBuilder.create()
                .texOffs(72, 0).addBox(-1.5F, -1F, 0F, 3, 7, 2, NONE)
                .texOffs(34, 28).addBox(-2.5F, 0F, -1F, 5, 2, 3, NONE),
                PartPose.offsetAndRotation(0F, -0.5F, 1.5F, 0F, 0F, 0F));
        PartDefinition p_hand_r = p_wisp.addOrReplaceChild("hand_r", CubeListBuilder.create()
                .texOffs(58, 13).addBox(-1.5F, -2F, -1.5F, 3, 4, 3, NONE)
                .texOffs(40, 21).addBox(-2F, -3F, -2F, 4, 2, 4, NONE),
                PartPose.offsetAndRotation(-8F, 1F, -1F, 0F, 0F, 0.175F));
        PartDefinition p_claw_r0 = p_hand_r.addOrReplaceChild("claw_r0", CubeListBuilder.create()
                .texOffs(82, 28).addBox(-1F, 0F, -1F, 2, 2, 2, NONE)
                .texOffs(48, 38).addBox(-0.5F, 2F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-1.7F, 1.5F, -1.3F, -0.262F, 0F, -0.175F));
        PartDefinition p_claw_r1 = p_hand_r.addOrReplaceChild("claw_r1", CubeListBuilder.create()
                .texOffs(90, 28).addBox(-1F, 0F, -1F, 2, 2, 2, NONE)
                .texOffs(52, 38).addBox(-0.5F, 2F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-0.5F, 1.5F, -0.5F, -0.262F, 0F, 0.035F));
        PartDefinition p_claw_r2 = p_hand_r.addOrReplaceChild("claw_r2", CubeListBuilder.create()
                .texOffs(98, 28).addBox(-1F, 0F, -1F, 2, 2, 2, NONE)
                .texOffs(56, 38).addBox(-0.5F, 2F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0.7F, 1.5F, 0.3F, -0.262F, 0F, 0.244F));
        PartDefinition p_cuff_r = p_hand_r.addOrReplaceChild("cuff_r", CubeListBuilder.create()
                .texOffs(106, 28).addBox(-1F, -2F, -1F, 2, 2, 2, NONE)
                .texOffs(60, 38).addBox(-0.5F, -4F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-1F, -2.5F, 0F, 0F, 0F, -0.698F));
        PartDefinition p_hand_l = p_wisp.addOrReplaceChild("hand_l", CubeListBuilder.create()
                .texOffs(70, 13).addBox(-1.5F, -2F, -1.5F, 3, 4, 3, NONE)
                .texOffs(56, 21).addBox(-2F, -3F, -2F, 4, 2, 4, NONE),
                PartPose.offsetAndRotation(8F, 1F, -1F, 0F, 0F, -0.175F));
        PartDefinition p_claw_l0 = p_hand_l.addOrReplaceChild("claw_l0", CubeListBuilder.create()
                .texOffs(114, 28).addBox(-1F, 0F, -1F, 2, 2, 2, NONE)
                .texOffs(64, 38).addBox(-0.5F, 2F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-0.7F, 1.5F, -1.3F, -0.262F, 0F, 0.175F));
        PartDefinition p_claw_l1 = p_hand_l.addOrReplaceChild("claw_l1", CubeListBuilder.create()
                .texOffs(0, 34).addBox(-1F, 0F, -1F, 2, 2, 2, NONE)
                .texOffs(68, 38).addBox(-0.5F, 2F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0.5F, 1.5F, -0.5F, -0.262F, 0F, -0.035F));
        PartDefinition p_claw_l2 = p_hand_l.addOrReplaceChild("claw_l2", CubeListBuilder.create()
                .texOffs(8, 34).addBox(-1F, 0F, -1F, 2, 2, 2, NONE)
                .texOffs(72, 38).addBox(-0.5F, 2F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(1.7F, 1.5F, 0.3F, -0.262F, 0F, -0.244F));
        PartDefinition p_cuff_l = p_hand_l.addOrReplaceChild("cuff_l", CubeListBuilder.create()
                .texOffs(16, 34).addBox(-1F, -2F, -1F, 2, 2, 2, NONE)
                .texOffs(76, 38).addBox(-0.5F, -4F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(1F, -2.5F, 0F, 0F, 0F, 0.698F));
        PartDefinition p_rib_r = p_wisp.addOrReplaceChild("rib_r", CubeListBuilder.create()
                .texOffs(82, 0).addBox(-2F, -3.5F, -1F, 2, 7, 2, NONE)
                .texOffs(0, 38).addBox(-1F, -4F, -1.5F, 2, 1, 2, NONE)
                .texOffs(8, 38).addBox(-1F, 3F, -1.5F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(-2.5F, 2F, -2F, 0F, -0.349F, 0F));
        PartDefinition p_shoulder_r = p_rib_r.addOrReplaceChild("shoulder_r", CubeListBuilder.create()
                .texOffs(24, 34).addBox(-1F, -2F, -1F, 2, 2, 2, NONE)
                .texOffs(108, 34).addBox(-0.5F, -5F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(-1F, -3.5F, 1F, -0.175F, 0F, -0.611F));
        PartDefinition p_rib_l = p_wisp.addOrReplaceChild("rib_l", CubeListBuilder.create()
                .texOffs(90, 0).addBox(0F, -3.5F, -1F, 2, 7, 2, NONE)
                .texOffs(16, 38).addBox(-1F, -4F, -1.5F, 2, 1, 2, NONE)
                .texOffs(24, 38).addBox(-1F, 3F, -1.5F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(2.5F, 2F, -2F, 0F, 0.349F, 0F));
        PartDefinition p_shoulder_l = p_rib_l.addOrReplaceChild("shoulder_l", CubeListBuilder.create()
                .texOffs(32, 34).addBox(-1F, -2F, -1F, 2, 2, 2, NONE)
                .texOffs(112, 34).addBox(-0.5F, -5F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(1F, -3.5F, 1F, -0.175F, 0F, 0.611F));
        PartDefinition p_veil_0 = p_wisp.addOrReplaceChild("veil_0", CubeListBuilder.create()
                .texOffs(84, 21).addBox(-1.5F, 0F, -1.5F, 3, 3, 3, NONE)
                .texOffs(40, 34).addBox(-1F, 3F, -1F, 2, 2, 2, NONE)
                .texOffs(120, 38).addBox(-0.5F, 5F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 5.5F, 2.2F, 0.349F, 0F, 0F));
        PartDefinition p_veil_1 = p_wisp.addOrReplaceChild("veil_1", CubeListBuilder.create()
                .texOffs(96, 21).addBox(-1.5F, 0F, -1.5F, 3, 3, 3, NONE)
                .texOffs(48, 34).addBox(-1F, 3F, -1F, 2, 2, 2, NONE)
                .texOffs(124, 38).addBox(-0.5F, 5F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(2.252F, 5.5F, 1.1F, 0.175F, 0F, -0.302F));
        PartDefinition p_veil_2 = p_wisp.addOrReplaceChild("veil_2", CubeListBuilder.create()
                .texOffs(82, 13).addBox(-1.5F, 0F, -1.5F, 3, 4, 3, NONE)
                .texOffs(56, 34).addBox(-1F, 4F, -1F, 2, 2, 2, NONE)
                .texOffs(0, 41).addBox(-0.5F, 6F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(2.252F, 5.5F, -1.1F, -0.175F, 0F, -0.302F));
        PartDefinition p_veil_3 = p_wisp.addOrReplaceChild("veil_3", CubeListBuilder.create()
                .texOffs(94, 13).addBox(-1.5F, 0F, -1.5F, 3, 4, 3, NONE)
                .texOffs(64, 34).addBox(-1F, 4F, -1F, 2, 2, 2, NONE)
                .texOffs(4, 41).addBox(-0.5F, 6F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 5.5F, -2.2F, -0.349F, 0F, 0F));
        PartDefinition p_veil_4 = p_wisp.addOrReplaceChild("veil_4", CubeListBuilder.create()
                .texOffs(106, 13).addBox(-1.5F, 0F, -1.5F, 3, 4, 3, NONE)
                .texOffs(72, 34).addBox(-1F, 4F, -1F, 2, 2, 2, NONE)
                .texOffs(8, 41).addBox(-0.5F, 6F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-2.252F, 5.5F, -1.1F, -0.175F, 0F, 0.302F));
        PartDefinition p_veil_5 = p_wisp.addOrReplaceChild("veil_5", CubeListBuilder.create()
                .texOffs(108, 21).addBox(-1.5F, 0F, -1.5F, 3, 3, 3, NONE)
                .texOffs(80, 34).addBox(-1F, 3F, -1F, 2, 2, 2, NONE)
                .texOffs(12, 41).addBox(-0.5F, 5F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-2.252F, 5.5F, 1.1F, 0.175F, 0F, 0.302F));
        PartDefinition p_veil_c = p_wisp.addOrReplaceChild("veil_c", CubeListBuilder.create()
                .texOffs(0, 28).addBox(-1.5F, 0F, -1.5F, 3, 3, 3, NONE)
                .texOffs(88, 34).addBox(-1F, 3F, -1F, 2, 2, 2, NONE)
                .texOffs(16, 41).addBox(-0.5F, 5F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, 0F, 0F, 0F));
        PartDefinition p_shard_0 = p_wisp.addOrReplaceChild("shard_0", CubeListBuilder.create()
                .texOffs(118, 13).addBox(-1F, -2.5F, -1F, 2, 5, 2, NONE)
                .texOffs(20, 41).addBox(-0.5F, -3.5F, -0.5F, 1, 1, 1, NONE)
                .texOffs(80, 38).addBox(-0.5F, 2.5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(14F, 1F, 0F, 0F, 0F, 0.436F));
        PartDefinition p_shard_1 = p_wisp.addOrReplaceChild("shard_1", CubeListBuilder.create()
                .texOffs(0, 21).addBox(-1F, -2.5F, -1F, 2, 5, 2, NONE)
                .texOffs(24, 41).addBox(-0.5F, -3.5F, -0.5F, 1, 1, 1, NONE)
                .texOffs(84, 38).addBox(-0.5F, 2.5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(7F, 3F, 12.124F, 0F, 0F, 0.436F));
        PartDefinition p_shard_2 = p_wisp.addOrReplaceChild("shard_2", CubeListBuilder.create()
                .texOffs(8, 21).addBox(-1F, -2.5F, -1F, 2, 5, 2, NONE)
                .texOffs(28, 41).addBox(-0.5F, -3.5F, -0.5F, 1, 1, 1, NONE)
                .texOffs(88, 38).addBox(-0.5F, 2.5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-7F, 1F, 12.124F, 0F, 0F, 0.436F));
        PartDefinition p_shard_3 = p_wisp.addOrReplaceChild("shard_3", CubeListBuilder.create()
                .texOffs(16, 21).addBox(-1F, -2.5F, -1F, 2, 5, 2, NONE)
                .texOffs(32, 41).addBox(-0.5F, -3.5F, -0.5F, 1, 1, 1, NONE)
                .texOffs(92, 38).addBox(-0.5F, 2.5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-14F, 3F, 0F, 0F, 0F, 0.436F));
        PartDefinition p_shard_4 = p_wisp.addOrReplaceChild("shard_4", CubeListBuilder.create()
                .texOffs(24, 21).addBox(-1F, -2.5F, -1F, 2, 5, 2, NONE)
                .texOffs(36, 41).addBox(-0.5F, -3.5F, -0.5F, 1, 1, 1, NONE)
                .texOffs(96, 38).addBox(-0.5F, 2.5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-7F, 1F, -12.124F, 0F, 0F, 0.436F));
        PartDefinition p_shard_5 = p_wisp.addOrReplaceChild("shard_5", CubeListBuilder.create()
                .texOffs(32, 21).addBox(-1F, -2.5F, -1F, 2, 5, 2, NONE)
                .texOffs(40, 41).addBox(-0.5F, -3.5F, -0.5F, 1, 1, 1, NONE)
                .texOffs(100, 38).addBox(-0.5F, 2.5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(7F, 3F, -12.124F, 0F, 0F, 0.436F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final Role[] FROST_WISP_ROLES = {new Role("wisp", "bob", 0.00F), new Role("wisp/head", "head", 0.00F), new Role("wisp/hand_r", "hand", 0.00F), new Role("wisp/hand_l", "hand", 2.00F), new Role("wisp/veil_0", "tentacle", 0.00F), new Role("wisp/veil_1", "tentacle", 0.90F), new Role("wisp/veil_2", "tentacle", 1.80F), new Role("wisp/veil_3", "tentacle", 2.70F), new Role("wisp/veil_4", "tentacle", 3.60F), new Role("wisp/veil_5", "tentacle", 4.50F), new Role("wisp/veil_c", "tentacle", 2.50F), new Role("wisp/shard_0", "orbit", 0.00F), new Role("wisp/shard_1", "orbit", 1.00F), new Role("wisp/shard_2", "orbit", 2.00F), new Role("wisp/shard_3", "orbit", 3.00F), new Role("wisp/shard_4", "orbit", 4.00F), new Role("wisp/shard_5", "orbit", 5.00F)};

    public static final Key[] FROST_WISP_POSES = {
            new Key(2, 0F, "wisp/head", 0F, 0F, 0F),
            new Key(2, 5F, "wisp/head", -18F, 0F, 0F),
            new Key(2, 8F, "wisp/head", 12F, 0F, 0F),
            new Key(2, 14F, "wisp/head", 0F, 0F, 0F),
            new Key(2, 0F, "wisp/hand_r", 0F, 0F, 0F),
            new Key(2, 5F, "wisp/hand_r", -40F, 0F, 30F),
            new Key(2, 8F, "wisp/hand_r", -85F, -20F, 0F),
            new Key(2, 14F, "wisp/hand_r", 0F, 0F, 0F),
            new Key(2, 0F, "wisp/hand_l", 0F, 0F, 0F),
            new Key(2, 5F, "wisp/hand_l", -40F, 0F, -30F),
            new Key(2, 8F, "wisp/hand_l", -85F, 20F, 0F),
            new Key(2, 14F, "wisp/hand_l", 0F, 0F, 0F),
            new Key(2, 0F, "wisp/core", 0F, 0F, 0F),
            new Key(2, 5F, "wisp/core", 0F, 0F, 45F),
            new Key(2, 8F, "wisp/core", 0F, 0F, 90F),
            new Key(2, 14F, "wisp/core", 0F, 0F, 0F),
            new Key(2, 0F, "wisp", 0F, 0F, 0F),
            new Key(2, 5F, "wisp", -10F, 0F, 0F),
            new Key(2, 8F, "wisp", 8F, 0F, 0F),
            new Key(2, 14F, "wisp", 0F, 0F, 0F),
            new Key(2, 0F, "wisp/rib_r", 0F, 0F, 0F),
            new Key(2, 5F, "wisp/rib_r", 0F, -35F, 0F),
            new Key(2, 11F, "wisp/rib_r", 0F, 0F, 0F),
            new Key(2, 0F, "wisp/rib_l", 0F, 0F, 0F),
            new Key(2, 5F, "wisp/rib_l", 0F, 35F, 0F),
            new Key(2, 11F, "wisp/rib_l", 0F, 0F, 0F)};

    public static final float FROST_WISP_SCALE = 0.72F;

    /** Glacier Brute: gebeugter Eisriese aus Pressschnee und Minzeis mit Eisfaeusten, Kristallgrat und kleinem Kopf. Textur {@code entity/dungeon/glacier_brute.png}. */
    public static LayerDefinition glacierBrute() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_hips = root.addOrReplaceChild("hips", CubeListBuilder.create()
                .texOffs(144, 26).addBox(-7F, -3F, -4F, 14, 6, 9, NONE)
                .texOffs(0, 59).addBox(-7.5F, 1F, -4.5F, 15, 2, 10, NONE),
                PartPose.offsetAndRotation(0F, 10F, 3F, 0F, 0F, 0F));
        PartDefinition p_body = p_hips.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(66, 0).addBox(-7.5F, -9F, -6F, 15, 10, 11, NONE)
                .texOffs(88, 26).addBox(-8F, -5F, -6.5F, 16, 3, 12, NONE)
                .texOffs(0, 0).addBox(-10F, -21F, -7F, 20, 13, 13, NONE)
                .texOffs(188, 44).addBox(-9F, -23F, -4F, 18, 3, 10, NONE)
                .texOffs(122, 71).addBox(-8F, -19F, -8F, 16, 8, 2, NONE)
                .texOffs(220, 83).addBox(-1.5F, -18F, -8.5F, 3, 7, 1, NONE)
                .texOffs(240, 100).addBox(-4F, -16F, -8.75F, 2, 2, 1, NONE)
                .texOffs(34, 44).addBox(-10.5F, -14F, -5F, 21, 4, 10, NONE)
                .texOffs(96, 44).addBox(-8F, -20F, 5.5F, 16, 12, 2, NONE)
                .texOffs(164, 100).addBox(-6F, -8F, -6.5F, 12, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -2F, 0F, 0.489F, 0F, 0F));
        PartDefinition p_head = p_body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(32, 71).addBox(-3.5F, -6F, -5F, 7, 6, 6, NONE)
                .texOffs(128, 93).addBox(-4F, -6.5F, -6F, 8, 2, 4, NONE)
                .texOffs(226, 100).addBox(-3F, -4F, -5.5F, 6, 2, 1, NONE)
                .texOffs(16, 105).addBox(-2.5F, -3.75F, -5.75F, 2, 1, 1, NONE)
                .texOffs(22, 105).addBox(0.5F, -3.75F, -5.75F, 2, 1, 1, NONE)
                .texOffs(76, 100).addBox(-1F, -2.5F, -6.5F, 2, 2, 2, NONE)
                .texOffs(80, 93).addBox(-3.5F, -7.5F, -3F, 7, 2, 5, NONE),
                PartPose.offsetAndRotation(0F, -20F, -7F, -0.349F, 0F, 0F));
        PartDefinition p_jaw = p_head.addOrReplaceChild("jaw", CubeListBuilder.create()
                .texOffs(104, 93).addBox(-3.5F, 0F, -5F, 7, 2, 5, NONE)
                .texOffs(246, 100).addBox(-3F, -1F, -5.5F, 1, 2, 1, NONE)
                .texOffs(250, 100).addBox(2F, -1F, -5.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, -1F, 0.14F, 0F, 0F));
        PartDefinition p_horn_r = p_head.addOrReplaceChild("horn_r", CubeListBuilder.create()
                .texOffs(84, 100).addBox(-1F, -1F, -1F, 2, 2, 2, NONE)
                .texOffs(132, 100).addBox(-0.5F, -4F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(-3F, -6.5F, -2F, -0.349F, 0F, -0.698F));
        PartDefinition p_horn_l = p_head.addOrReplaceChild("horn_l", CubeListBuilder.create()
                .texOffs(92, 100).addBox(-1F, -1F, -1F, 2, 2, 2, NONE)
                .texOffs(136, 100).addBox(-0.5F, -4F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(3F, -6.5F, -2F, -0.349F, 0F, 0.698F));
        PartDefinition p_arm_r = p_body.addOrReplaceChild("arm_r", CubeListBuilder.create()
                .texOffs(32, 26).addBox(-3.5F, 1F, -3.5F, 7, 10, 7, NONE)
                .texOffs(0, 83).addBox(-4F, 5F, -4F, 8, 2, 8, NONE),
                PartPose.offsetAndRotation(-10F, -17F, -1F, -0.593F, 0F, 0.244F));
        PartDefinition p_shoulder_r = p_arm_r.addOrReplaceChild("shoulder_r", CubeListBuilder.create()
                .texOffs(118, 0).addBox(-5F, -5F, -5F, 10, 8, 10, NONE)
                .texOffs(158, 71).addBox(-4.5F, -6F, -4F, 9, 2, 8, NONE)
                .texOffs(90, 71).addBox(-6F, -3F, -3F, 2, 5, 6, NONE),
                PartPose.offsetAndRotation(0F, -1F, 0F, 0F, 0F, -0.209F));
        PartDefinition p_sh_r1 = p_shoulder_r.addOrReplaceChild("sh_r1", CubeListBuilder.create()
                .texOffs(140, 83).addBox(-2F, -3F, -2F, 4, 4, 4, NONE)
                .texOffs(236, 93).addBox(-1F, -6F, -1F, 2, 3, 2, NONE)
                .texOffs(0, 105).addBox(-0.5F, -8F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-1F, -5.5F, -1F, -0.175F, 0F, -0.436F));
        PartDefinition p_sh_r2 = p_shoulder_r.addOrReplaceChild("sh_r2", CubeListBuilder.create()
                .texOffs(152, 93).addBox(-1.5F, -2F, -1.5F, 3, 3, 3, NONE)
                .texOffs(140, 100).addBox(-0.5F, -5F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(-3.5F, -4F, 2.5F, -0.698F, 0F, -0.96F));
        PartDefinition p_fore_r = p_arm_r.addOrReplaceChild("fore_r", CubeListBuilder.create()
                .texOffs(198, 0).addBox(-4F, 0F, -4F, 8, 10, 8, NONE)
                .texOffs(50, 59).addBox(-4.5F, -1F, -4.5F, 9, 3, 9, NONE)
                .texOffs(58, 71).addBox(-5F, 2F, -3F, 2, 6, 6, NONE),
                PartPose.offsetAndRotation(0F, 9.5F, 0F, -0.175F, 0F, -0.14F));
        PartDefinition p_fist_r = p_fore_r.addOrReplaceChild("fist_r", CubeListBuilder.create()
                .texOffs(190, 26).addBox(-4.5F, 0F, -4.5F, 9, 7, 8, NONE)
                .texOffs(228, 83).addBox(-5F, 1F, -5F, 10, 3, 4, NONE)
                .texOffs(28, 93).addBox(-3.5F, 6F, -3.5F, 7, 1, 6, NONE)
                .texOffs(244, 93).addBox(-4F, 2F, -6.5F, 2, 3, 2, NONE)
                .texOffs(0, 100).addBox(-1F, 2F, -6.5F, 2, 3, 2, NONE)
                .texOffs(8, 100).addBox(2F, 2F, -6.5F, 2, 3, 2, NONE),
                PartPose.offsetAndRotation(-0.5F, 9F, -0.5F, 0.175F, 0F, 0F));
        PartDefinition p_elbow_r = p_fore_r.addOrReplaceChild("elbow_r", CubeListBuilder.create()
                .texOffs(164, 93).addBox(-1.5F, -2F, -1.5F, 3, 3, 3, NONE)
                .texOffs(144, 100).addBox(-0.5F, -5F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 2F, 4F, -1.745F, 0F, 0F));
        PartDefinition p_arm_l = p_body.addOrReplaceChild("arm_l", CubeListBuilder.create()
                .texOffs(60, 26).addBox(-3.5F, 1F, -3.5F, 7, 10, 7, NONE)
                .texOffs(32, 83).addBox(-4F, 5F, -4F, 8, 2, 8, NONE),
                PartPose.offsetAndRotation(10F, -17F, -1F, -0.593F, 0F, -0.244F));
        PartDefinition p_shoulder_l = p_arm_l.addOrReplaceChild("shoulder_l", CubeListBuilder.create()
                .texOffs(158, 0).addBox(-5F, -5F, -5F, 10, 8, 10, NONE)
                .texOffs(192, 71).addBox(-4.5F, -6F, -4F, 9, 2, 8, NONE)
                .texOffs(106, 71).addBox(4F, -3F, -3F, 2, 5, 6, NONE),
                PartPose.offsetAndRotation(0F, -1F, 0F, 0F, 0F, 0.209F));
        PartDefinition p_sh_l1 = p_shoulder_l.addOrReplaceChild("sh_l1", CubeListBuilder.create()
                .texOffs(156, 83).addBox(-2F, -3F, -2F, 4, 4, 4, NONE)
                .texOffs(16, 100).addBox(-1F, -6F, -1F, 2, 3, 2, NONE)
                .texOffs(4, 105).addBox(-0.5F, -8F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(1F, -5.5F, -1F, -0.175F, 0F, 0.436F));
        PartDefinition p_sh_l2 = p_shoulder_l.addOrReplaceChild("sh_l2", CubeListBuilder.create()
                .texOffs(176, 93).addBox(-1.5F, -2F, -1.5F, 3, 3, 3, NONE)
                .texOffs(148, 100).addBox(-0.5F, -5F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(3.5F, -4F, 2.5F, -0.698F, 0F, 0.96F));
        PartDefinition p_fore_l = p_arm_l.addOrReplaceChild("fore_l", CubeListBuilder.create()
                .texOffs(0, 26).addBox(-4F, 0F, -4F, 8, 10, 8, NONE)
                .texOffs(86, 59).addBox(-4.5F, -1F, -4.5F, 9, 3, 9, NONE)
                .texOffs(74, 71).addBox(3F, 2F, -3F, 2, 6, 6, NONE),
                PartPose.offsetAndRotation(0F, 9.5F, 0F, -0.175F, 0F, 0.14F));
        PartDefinition p_fist_l = p_fore_l.addOrReplaceChild("fist_l", CubeListBuilder.create()
                .texOffs(0, 44).addBox(-4.5F, 0F, -4.5F, 9, 7, 8, NONE)
                .texOffs(0, 93).addBox(-5F, 1F, -5F, 10, 3, 4, NONE)
                .texOffs(54, 93).addBox(-3.5F, 6F, -3.5F, 7, 1, 6, NONE)
                .texOffs(24, 100).addBox(-4F, 2F, -6.5F, 2, 3, 2, NONE)
                .texOffs(32, 100).addBox(-1F, 2F, -6.5F, 2, 3, 2, NONE)
                .texOffs(40, 100).addBox(2F, 2F, -6.5F, 2, 3, 2, NONE),
                PartPose.offsetAndRotation(0.5F, 9F, -0.5F, 0.175F, 0F, 0F));
        PartDefinition p_elbow_l = p_fore_l.addOrReplaceChild("elbow_l", CubeListBuilder.create()
                .texOffs(188, 93).addBox(-1.5F, -2F, -1.5F, 3, 3, 3, NONE)
                .texOffs(152, 100).addBox(-0.5F, -5F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 2F, 4F, -1.745F, 0F, 0F));
        PartDefinition p_ridge_0 = p_body.addOrReplaceChild("ridge_0", CubeListBuilder.create()
                .texOffs(120, 83).addBox(-2.5F, -4F, -2.5F, 5, 5, 5, NONE)
                .texOffs(200, 93).addBox(-1.5F, -7F, -1.5F, 3, 3, 3, NONE)
                .texOffs(100, 100).addBox(-1F, -9F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, -21F, 3F, -0.349F, 0F, 0F));
        PartDefinition p_ridge_1 = p_body.addOrReplaceChild("ridge_1", CubeListBuilder.create()
                .texOffs(172, 83).addBox(-2F, -3F, -2F, 4, 4, 4, NONE)
                .texOffs(108, 100).addBox(-1F, -5F, -1F, 2, 2, 2, NONE)
                .texOffs(28, 105).addBox(-0.5F, -6F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-4.5F, -19F, 4F, -0.524F, 0F, -0.489F));
        PartDefinition p_ridge_2 = p_body.addOrReplaceChild("ridge_2", CubeListBuilder.create()
                .texOffs(188, 83).addBox(-2F, -3F, -2F, 4, 4, 4, NONE)
                .texOffs(116, 100).addBox(-1F, -5F, -1F, 2, 2, 2, NONE)
                .texOffs(8, 105).addBox(-0.5F, -7F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(4.5F, -18F, 4.5F, -0.593F, 0F, 0.524F));
        PartDefinition p_ridge_3 = p_body.addOrReplaceChild("ridge_3", CubeListBuilder.create()
                .texOffs(204, 83).addBox(-2F, -3F, -2F, 4, 4, 4, NONE)
                .texOffs(124, 100).addBox(-1F, -5F, -1F, 2, 2, 2, NONE)
                .texOffs(12, 105).addBox(-0.5F, -7F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -14F, 6F, -0.96F, 0F, 0F));
        PartDefinition p_ridge_4 = p_body.addOrReplaceChild("ridge_4", CubeListBuilder.create()
                .texOffs(212, 93).addBox(-1.5F, -2F, -1.5F, 3, 3, 3, NONE)
                .texOffs(156, 100).addBox(-0.5F, -5F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(-3F, -9F, 6F, -1.222F, 0F, -0.314F));
        PartDefinition p_ridge_5 = p_body.addOrReplaceChild("ridge_5", CubeListBuilder.create()
                .texOffs(224, 93).addBox(-1.5F, -1F, -1.5F, 3, 2, 3, NONE)
                .texOffs(160, 100).addBox(-0.5F, -4F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(3F, -6F, 5.5F, -1.396F, 0F, 0.314F));
        PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create()
                .texOffs(132, 44).addBox(-3.5F, -1F, -3.5F, 7, 7, 7, NONE)
                .texOffs(194, 59).addBox(-4F, 5F, -4F, 8, 4, 8, NONE)
                .texOffs(48, 100).addBox(-3F, 5.5F, -4.75F, 6, 3, 1, NONE)
                .texOffs(64, 83).addBox(-3.5F, 9F, -3.5F, 7, 3, 7, NONE)
                .texOffs(122, 59).addBox(-4.5F, 11F, -5.5F, 9, 3, 9, NONE)
                .texOffs(190, 100).addBox(-4F, 12F, -6.5F, 8, 2, 1, NONE),
                PartPose.offsetAndRotation(-5F, 10F, 3.5F, 0F, 0F, 0F));
        PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create()
                .texOffs(160, 44).addBox(-3.5F, -1F, -3.5F, 7, 7, 7, NONE)
                .texOffs(0, 71).addBox(-4F, 5F, -4F, 8, 4, 8, NONE)
                .texOffs(62, 100).addBox(-3F, 5.5F, -4.75F, 6, 3, 1, NONE)
                .texOffs(92, 83).addBox(-3.5F, 9F, -3.5F, 7, 3, 7, NONE)
                .texOffs(158, 59).addBox(-4.5F, 11F, -5.5F, 9, 3, 9, NONE)
                .texOffs(208, 100).addBox(-4F, 12F, -6.5F, 8, 2, 1, NONE),
                PartPose.offsetAndRotation(5F, 10F, 3.5F, 0F, 0F, 0F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    public static final Role[] GLACIER_BRUTE_ROLES = {new Role("hips/body", "body", 0.00F), new Role("hips/body/head", "head", 0.00F), new Role("hips/body/head/jaw", "jaw", 0.00F), new Role("hips/body/arm_r", "arm_r", 0.00F), new Role("hips/body/arm_l", "arm_l", 0.00F), new Role("leg_r", "leg_r", 0.00F), new Role("leg_l", "leg_l", 0.00F)};

    public static final Key[] GLACIER_BRUTE_POSES = {
            new Key(1, 0F, "hips/body/arm_r", 0F, 0F, 0F),
            new Key(1, 4F, "hips/body/arm_r", -95F, 0F, -15F),
            new Key(1, 7F, "hips/body/arm_r", 15F, 0F, 5F),
            new Key(1, 10F, "hips/body/arm_r", 0F, 0F, 0F),
            new Key(1, 0F, "hips/body", 0F, 0F, 0F),
            new Key(1, 4F, "hips/body", -10F, -18F, 0F),
            new Key(1, 7F, "hips/body", 10F, 14F, 0F),
            new Key(1, 10F, "hips/body", 0F, 0F, 0F),
            new Key(1, 0F, "hips/body/head", 0F, 0F, 0F),
            new Key(1, 4F, "hips/body/head", -10F, 0F, 0F),
            new Key(1, 7F, "hips/body/head", 8F, 0F, 0F),
            new Key(1, 10F, "hips/body/head", 0F, 0F, 0F),
            new Key(3, 0F, "hips/body/arm_r", 0F, 0F, 0F),
            new Key(3, 12F, "hips/body/arm_r", -150F, 0F, 18F),
            new Key(3, 15F, "hips/body/arm_r", 12F, 0F, 0F),
            new Key(3, 18F, "hips/body/arm_r", 12F, 0F, 0F),
            new Key(3, 22F, "hips/body/arm_r", 0F, 0F, 0F),
            new Key(3, 0F, "hips/body/arm_l", 0F, 0F, 0F),
            new Key(3, 12F, "hips/body/arm_l", -150F, 0F, -18F),
            new Key(3, 15F, "hips/body/arm_l", 12F, 0F, 0F),
            new Key(3, 18F, "hips/body/arm_l", 12F, 0F, 0F),
            new Key(3, 22F, "hips/body/arm_l", 0F, 0F, 0F),
            new Key(3, 0F, "hips/body", 0F, 0F, 0F),
            new Key(3, 12F, "hips/body", -30F, 0F, 0F),
            new Key(3, 15F, "hips/body", 18F, 0F, 0F),
            new Key(3, 18F, "hips/body", 18F, 0F, 0F),
            new Key(3, 22F, "hips/body", 0F, 0F, 0F),
            new Key(3, 0F, "hips/body/head", 0F, 0F, 0F),
            new Key(3, 12F, "hips/body/head", -15F, 0F, 0F),
            new Key(3, 15F, "hips/body/head", 15F, 0F, 0F),
            new Key(3, 22F, "hips/body/head", 0F, 0F, 0F),
            new Key(3, 0F, "hips/body/head/jaw", 0F, 0F, 0F),
            new Key(3, 12F, "hips/body/head/jaw", 30F, 0F, 0F),
            new Key(3, 15F, "hips/body/head/jaw", 10F, 0F, 0F),
            new Key(3, 22F, "hips/body/head/jaw", 0F, 0F, 0F)};

    public static final float GLACIER_BRUTE_SCALE = 1.05F;

    /** Rust Scuttler: kleiner rostiger Spinnenroboter mit sechs Beinen, Sensorauge und Kneifzangen. Textur {@code entity/dungeon/rust_scuttler.png}. */
    public static LayerDefinition rustScuttler() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.5F, -2F, -6F, 9, 4, 12, NONE)
                .texOffs(42, 0).addBox(-5.5F, -1F, -5F, 11, 2, 10, NONE)
                .texOffs(84, 0).addBox(-4F, -3F, -5F, 8, 1, 10, NONE)
                .texOffs(88, 16).addBox(-3F, -4F, -3F, 6, 1, 6, NONE)
                .texOffs(92, 48).addBox(-3.5F, -3.5F, -5.5F, 7, 1, 2, NONE)
                .texOffs(48, 48).addBox(-1F, -4.5F, -1.5F, 2, 1, 3, NONE)
                .texOffs(0, 26).addBox(-3F, -2F, 5.5F, 6, 4, 3, NONE)
                .texOffs(28, 52).addBox(-2F, -1F, 8F, 4, 1, 1, NONE)
                .texOffs(38, 52).addBox(-2F, 0.5F, 8F, 4, 1, 1, NONE)
                .texOffs(40, 39).addBox(2.5F, -6F, 3.5F, 1, 4, 1, NONE)
                .texOffs(110, 48).addBox(2F, -6.5F, 3F, 2, 1, 2, NONE)
                .texOffs(40, 16).addBox(-3F, 2F, -4F, 6, 1, 8, NONE),
                PartPose.offsetAndRotation(0F, 17F, 0F, 0F, 0F, 0F));
        PartDefinition p_head = p_body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(68, 16).addBox(-3F, -2F, -4F, 6, 4, 4, NONE)
                .texOffs(44, 39).addBox(-3.5F, -2.5F, -3F, 7, 1, 3, NONE)
                .texOffs(58, 48).addBox(-2F, -1.5F, -4.5F, 4, 3, 1, NONE)
                .texOffs(118, 48).addBox(-1.5F, -1F, -5F, 3, 2, 1, NONE)
                .texOffs(108, 52).addBox(-3F, 0.5F, -4.5F, 1, 1, 1, NONE)
                .texOffs(112, 52).addBox(2F, 0.5F, -4.5F, 1, 1, 1, NONE)
                .texOffs(54, 44).addBox(-2.5F, 2F, -3.5F, 5, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, -0.5F, -5.5F, 0F, 0F, 0F));
        PartDefinition p_antenna = p_body.addOrReplaceChild("antenna", CubeListBuilder.create()
                .texOffs(120, 33).addBox(-0.5F, -5F, -0.5F, 1, 5, 1, NONE)
                .texOffs(0, 52).addBox(-1F, -6F, -1F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(-2.5F, -3F, 3.5F, -0.436F, 0F, -0.262F));
        PartDefinition p_claw_r = p_body.addOrReplaceChild("claw_r", CubeListBuilder.create()
                .texOffs(18, 26).addBox(-1F, -1F, -4F, 2, 2, 4, NONE)
                .texOffs(0, 39).addBox(-1.5F, -1.5F, -1F, 3, 3, 2, NONE)
                .texOffs(8, 52).addBox(-2F, -0.5F, -4.5F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(-3F, 1.5F, -5.5F, 0.262F, 0.349F, 0F));
        PartDefinition p_pincer_r = p_claw_r.addOrReplaceChild("pincer_r", CubeListBuilder.create()
                .texOffs(10, 39).addBox(-1F, -1F, -3F, 2, 2, 3, NONE)
                .texOffs(20, 52).addBox(0F, -1F, -4F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, -4F, 0F, -0.524F, 0F));
        PartDefinition p_claw_l = p_body.addOrReplaceChild("claw_l", CubeListBuilder.create()
                .texOffs(30, 26).addBox(-1F, -1F, -4F, 2, 2, 4, NONE)
                .texOffs(20, 39).addBox(-1.5F, -1.5F, -1F, 3, 3, 2, NONE)
                .texOffs(14, 52).addBox(1F, -0.5F, -4.5F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(3F, 1.5F, -5.5F, 0.262F, -0.349F, 0F));
        PartDefinition p_pincer_l = p_claw_l.addOrReplaceChild("pincer_l", CubeListBuilder.create()
                .texOffs(30, 39).addBox(-1F, -1F, -3F, 2, 2, 3, NONE)
                .texOffs(24, 52).addBox(-1F, -1F, -4F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, -4F, 0F, 0.524F, 0F));
        PartDefinition p_leg_r1 = p_body.addOrReplaceChild("leg_r1", CubeListBuilder.create()
                .texOffs(60, 33).addBox(-1F, -1.5F, -1.5F, 2, 3, 3, NONE),
                PartPose.offsetAndRotation(-4.5F, 0.5F, -3.5F, 0F, -0.559F, 0F));
        PartDefinition p_femur_r1 = p_leg_r1.addOrReplaceChild("femur_r1", CubeListBuilder.create()
                .texOffs(64, 39).addBox(-7F, -1F, -1F, 7, 2, 2, NONE)
                .texOffs(70, 44).addBox(-6F, -2F, -1.5F, 5, 1, 3, NONE)
                .texOffs(48, 52).addBox(-6F, 1F, -0.5F, 4, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0.663F));
        PartDefinition p_shin_r1 = p_femur_r1.addOrReplaceChild("shin_r1", CubeListBuilder.create()
                .texOffs(120, 0).addBox(-1F, 0F, -1F, 2, 8, 2, NONE)
                .texOffs(42, 26).addBox(-1.5F, 1F, -1.5F, 3, 3, 3, NONE)
                .texOffs(68, 48).addBox(-0.5F, 8F, -0.5F, 1, 3, 1, NONE)
                .texOffs(54, 26).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, NONE),
                PartPose.offsetAndRotation(-7F, 0F, 0F, 0F, 0F, -0.349F));
        PartDefinition p_leg_r2 = p_body.addOrReplaceChild("leg_r2", CubeListBuilder.create()
                .texOffs(70, 33).addBox(-1F, -1.5F, -1.5F, 2, 3, 3, NONE),
                PartPose.offsetAndRotation(-4.5F, 0.5F, 0F, 0F, 0F, 0F));
        PartDefinition p_femur_r2 = p_leg_r2.addOrReplaceChild("femur_r2", CubeListBuilder.create()
                .texOffs(82, 39).addBox(-7F, -1F, -1F, 7, 2, 2, NONE)
                .texOffs(86, 44).addBox(-6F, -2F, -1.5F, 5, 1, 3, NONE)
                .texOffs(58, 52).addBox(-6F, 1F, -0.5F, 4, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0.663F));
        PartDefinition p_shin_r2 = p_femur_r2.addOrReplaceChild("shin_r2", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-1F, 0F, -1F, 2, 8, 2, NONE)
                .texOffs(66, 26).addBox(-1.5F, 1F, -1.5F, 3, 3, 3, NONE)
                .texOffs(72, 48).addBox(-0.5F, 8F, -0.5F, 1, 3, 1, NONE)
                .texOffs(78, 26).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, NONE),
                PartPose.offsetAndRotation(-7F, 0F, 0F, 0F, 0F, -0.349F));
        PartDefinition p_leg_r3 = p_body.addOrReplaceChild("leg_r3", CubeListBuilder.create()
                .texOffs(80, 33).addBox(-1F, -1.5F, -1.5F, 2, 3, 3, NONE),
                PartPose.offsetAndRotation(-4.5F, 0.5F, 3.5F, 0F, 0.559F, 0F));
        PartDefinition p_femur_r3 = p_leg_r3.addOrReplaceChild("femur_r3", CubeListBuilder.create()
                .texOffs(100, 39).addBox(-7F, -1F, -1F, 7, 2, 2, NONE)
                .texOffs(102, 44).addBox(-6F, -2F, -1.5F, 5, 1, 3, NONE)
                .texOffs(68, 52).addBox(-6F, 1F, -0.5F, 4, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0.663F));
        PartDefinition p_shin_r3 = p_femur_r3.addOrReplaceChild("shin_r3", CubeListBuilder.create()
                .texOffs(8, 16).addBox(-1F, 0F, -1F, 2, 8, 2, NONE)
                .texOffs(90, 26).addBox(-1.5F, 1F, -1.5F, 3, 3, 3, NONE)
                .texOffs(76, 48).addBox(-0.5F, 8F, -0.5F, 1, 3, 1, NONE)
                .texOffs(102, 26).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, NONE),
                PartPose.offsetAndRotation(-7F, 0F, 0F, 0F, 0F, -0.349F));
        PartDefinition p_leg_l1 = p_body.addOrReplaceChild("leg_l1", CubeListBuilder.create()
                .texOffs(90, 33).addBox(-1F, -1.5F, -1.5F, 2, 3, 3, NONE),
                PartPose.offsetAndRotation(4.5F, 0.5F, -3.5F, 0F, 0.559F, 0F));
        PartDefinition p_femur_l1 = p_leg_l1.addOrReplaceChild("femur_l1", CubeListBuilder.create()
                .texOffs(0, 44).addBox(0F, -1F, -1F, 7, 2, 2, NONE)
                .texOffs(0, 48).addBox(1F, -2F, -1.5F, 5, 1, 3, NONE)
                .texOffs(78, 52).addBox(2F, 1F, -0.5F, 4, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, -0.663F));
        PartDefinition p_shin_l1 = p_femur_l1.addOrReplaceChild("shin_l1", CubeListBuilder.create()
                .texOffs(16, 16).addBox(-1F, 0F, -1F, 2, 8, 2, NONE)
                .texOffs(114, 26).addBox(-1.5F, 1F, -1.5F, 3, 3, 3, NONE)
                .texOffs(80, 48).addBox(-0.5F, 8F, -0.5F, 1, 3, 1, NONE)
                .texOffs(0, 33).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, NONE),
                PartPose.offsetAndRotation(7F, 0F, 0F, 0F, 0F, 0.349F));
        PartDefinition p_leg_l2 = p_body.addOrReplaceChild("leg_l2", CubeListBuilder.create()
                .texOffs(100, 33).addBox(-1F, -1.5F, -1.5F, 2, 3, 3, NONE),
                PartPose.offsetAndRotation(4.5F, 0.5F, 0F, 0F, 0F, 0F));
        PartDefinition p_femur_l2 = p_leg_l2.addOrReplaceChild("femur_l2", CubeListBuilder.create()
                .texOffs(18, 44).addBox(0F, -1F, -1F, 7, 2, 2, NONE)
                .texOffs(16, 48).addBox(1F, -2F, -1.5F, 5, 1, 3, NONE)
                .texOffs(88, 52).addBox(2F, 1F, -0.5F, 4, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, -0.663F));
        PartDefinition p_shin_l2 = p_femur_l2.addOrReplaceChild("shin_l2", CubeListBuilder.create()
                .texOffs(24, 16).addBox(-1F, 0F, -1F, 2, 8, 2, NONE)
                .texOffs(12, 33).addBox(-1.5F, 1F, -1.5F, 3, 3, 3, NONE)
                .texOffs(84, 48).addBox(-0.5F, 8F, -0.5F, 1, 3, 1, NONE)
                .texOffs(24, 33).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, NONE),
                PartPose.offsetAndRotation(7F, 0F, 0F, 0F, 0F, 0.349F));
        PartDefinition p_leg_l3 = p_body.addOrReplaceChild("leg_l3", CubeListBuilder.create()
                .texOffs(110, 33).addBox(-1F, -1.5F, -1.5F, 2, 3, 3, NONE),
                PartPose.offsetAndRotation(4.5F, 0.5F, 3.5F, 0F, -0.559F, 0F));
        PartDefinition p_femur_l3 = p_leg_l3.addOrReplaceChild("femur_l3", CubeListBuilder.create()
                .texOffs(36, 44).addBox(0F, -1F, -1F, 7, 2, 2, NONE)
                .texOffs(32, 48).addBox(1F, -2F, -1.5F, 5, 1, 3, NONE)
                .texOffs(98, 52).addBox(2F, 1F, -0.5F, 4, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, -0.663F));
        PartDefinition p_shin_l3 = p_femur_l3.addOrReplaceChild("shin_l3", CubeListBuilder.create()
                .texOffs(32, 16).addBox(-1F, 0F, -1F, 2, 8, 2, NONE)
                .texOffs(36, 33).addBox(-1.5F, 1F, -1.5F, 3, 3, 3, NONE)
                .texOffs(88, 48).addBox(-0.5F, 8F, -0.5F, 1, 3, 1, NONE)
                .texOffs(48, 33).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, NONE),
                PartPose.offsetAndRotation(7F, 0F, 0F, 0F, 0F, 0.349F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final Role[] RUST_SCUTTLER_ROLES = {new Role("body", "body", 0.00F), new Role("body/head", "head", 0.00F), new Role("body/leg_r1", "leg_a", 0.00F), new Role("body/leg_r2", "leg_b", 0.00F), new Role("body/leg_r3", "leg_a", 0.00F), new Role("body/leg_l1", "leg_b", 0.00F), new Role("body/leg_l2", "leg_a", 0.00F), new Role("body/leg_l3", "leg_b", 0.00F)};

    public static final Key[] RUST_SCUTTLER_POSES = {
            new Key(1, 0F, "body/claw_r", 0F, 0F, 0F),
            new Key(1, 4F, "body/claw_r", -20F, -30F, 0F),
            new Key(1, 6F, "body/claw_r", 10F, 20F, 0F),
            new Key(1, 10F, "body/claw_r", 0F, 0F, 0F),
            new Key(1, 0F, "body/claw_l", 0F, 0F, 0F),
            new Key(1, 4F, "body/claw_l", -20F, 30F, 0F),
            new Key(1, 6F, "body/claw_l", 10F, -20F, 0F),
            new Key(1, 10F, "body/claw_l", 0F, 0F, 0F),
            new Key(1, 0F, "body/claw_r/pincer_r", 0F, 0F, 0F),
            new Key(1, 4F, "body/claw_r/pincer_r", 0F, -35F, 0F),
            new Key(1, 6F, "body/claw_r/pincer_r", 0F, 10F, 0F),
            new Key(1, 10F, "body/claw_r/pincer_r", 0F, 0F, 0F),
            new Key(1, 0F, "body/claw_l/pincer_l", 0F, 0F, 0F),
            new Key(1, 4F, "body/claw_l/pincer_l", 0F, 35F, 0F),
            new Key(1, 6F, "body/claw_l/pincer_l", 0F, -10F, 0F),
            new Key(1, 10F, "body/claw_l/pincer_l", 0F, 0F, 0F),
            new Key(1, 0F, "body", 0F, 0F, 0F),
            new Key(1, 4F, "body", -8F, 0F, 0F),
            new Key(1, 6F, "body", 10F, 0F, 0F),
            new Key(1, 10F, "body", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_r1", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_r1/femur_r1/shin_r1", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_r2", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_r2/femur_r2/shin_r2", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_r3", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_r3/femur_r3/shin_r3", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_l1", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_l1/femur_l1/shin_l1", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_l2", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_l2/femur_l2/shin_l2", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_l3", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_l3/femur_l3/shin_l3", 0F, 0F, 0F),
            new Key(3, 3F, "body/leg_r1", -10F, 0F, 0F),
            new Key(3, 3F, "body/leg_r1/femur_r1/shin_r1", 0F, 0F, -25F),
            new Key(3, 3F, "body/leg_r2", -10F, 0F, 0F),
            new Key(3, 3F, "body/leg_r2/femur_r2/shin_r2", 0F, 0F, -25F),
            new Key(3, 3F, "body/leg_r3", -10F, 0F, 0F),
            new Key(3, 3F, "body/leg_r3/femur_r3/shin_r3", 0F, 0F, -25F),
            new Key(3, 3F, "body/leg_l1", -10F, 0F, 0F),
            new Key(3, 3F, "body/leg_l1/femur_l1/shin_l1", 0F, 0F, 25F),
            new Key(3, 3F, "body/leg_l2", -10F, 0F, 0F),
            new Key(3, 3F, "body/leg_l2/femur_l2/shin_l2", 0F, 0F, 25F),
            new Key(3, 3F, "body/leg_l3", -10F, 0F, 0F),
            new Key(3, 3F, "body/leg_l3/femur_l3/shin_l3", 0F, 0F, 25F),
            new Key(3, 6F, "body/leg_r1", 40F, 0F, 0F),
            new Key(3, 6F, "body/leg_r1/femur_r1/shin_r1", 0F, 0F, 15F),
            new Key(3, 6F, "body/leg_r2", 40F, 0F, 0F),
            new Key(3, 6F, "body/leg_r2/femur_r2/shin_r2", 0F, 0F, 15F),
            new Key(3, 6F, "body/leg_r3", 40F, 0F, 0F),
            new Key(3, 6F, "body/leg_r3/femur_r3/shin_r3", 0F, 0F, 15F),
            new Key(3, 6F, "body/leg_l1", 40F, 0F, 0F),
            new Key(3, 6F, "body/leg_l1/femur_l1/shin_l1", 0F, 0F, -15F),
            new Key(3, 6F, "body/leg_l2", 40F, 0F, 0F),
            new Key(3, 6F, "body/leg_l2/femur_l2/shin_l2", 0F, 0F, -15F),
            new Key(3, 6F, "body/leg_l3", 40F, 0F, 0F),
            new Key(3, 6F, "body/leg_l3/femur_l3/shin_l3", 0F, 0F, -15F),
            new Key(3, 10F, "body/leg_r1", 20F, 0F, 0F),
            new Key(3, 10F, "body/leg_r1/femur_r1/shin_r1", 0F, 0F, 25F),
            new Key(3, 10F, "body/leg_r2", 20F, 0F, 0F),
            new Key(3, 10F, "body/leg_r2/femur_r2/shin_r2", 0F, 0F, 25F),
            new Key(3, 10F, "body/leg_r3", 20F, 0F, 0F),
            new Key(3, 10F, "body/leg_r3/femur_r3/shin_r3", 0F, 0F, 25F),
            new Key(3, 10F, "body/leg_l1", 20F, 0F, 0F),
            new Key(3, 10F, "body/leg_l1/femur_l1/shin_l1", 0F, 0F, -25F),
            new Key(3, 10F, "body/leg_l2", 20F, 0F, 0F),
            new Key(3, 10F, "body/leg_l2/femur_l2/shin_l2", 0F, 0F, -25F),
            new Key(3, 10F, "body/leg_l3", 20F, 0F, 0F),
            new Key(3, 10F, "body/leg_l3/femur_l3/shin_l3", 0F, 0F, -25F),
            new Key(3, 14F, "body/leg_r1", 0F, 0F, 0F),
            new Key(3, 14F, "body/leg_r1/femur_r1/shin_r1", 0F, 0F, 0F),
            new Key(3, 14F, "body/leg_r2", 0F, 0F, 0F),
            new Key(3, 14F, "body/leg_r2/femur_r2/shin_r2", 0F, 0F, 0F),
            new Key(3, 14F, "body/leg_r3", 0F, 0F, 0F),
            new Key(3, 14F, "body/leg_r3/femur_r3/shin_r3", 0F, 0F, 0F),
            new Key(3, 14F, "body/leg_l1", 0F, 0F, 0F),
            new Key(3, 14F, "body/leg_l1/femur_l1/shin_l1", 0F, 0F, 0F),
            new Key(3, 14F, "body/leg_l2", 0F, 0F, 0F),
            new Key(3, 14F, "body/leg_l2/femur_l2/shin_l2", 0F, 0F, 0F),
            new Key(3, 14F, "body/leg_l3", 0F, 0F, 0F),
            new Key(3, 14F, "body/leg_l3/femur_l3/shin_l3", 0F, 0F, 0F),
            new Key(3, 0F, "body", 0F, 0F, 0F),
            new Key(3, 3F, "body", 10F, 0F, 0F),
            new Key(3, 6F, "body", -22F, 0F, 0F),
            new Key(3, 10F, "body", -8F, 0F, 0F),
            new Key(3, 14F, "body", 0F, 0F, 0F),
            new Key(3, 0F, "body/claw_r", 0F, 0F, 0F),
            new Key(3, 6F, "body/claw_r", -30F, -20F, 0F),
            new Key(3, 14F, "body/claw_r", 0F, 0F, 0F),
            new Key(3, 0F, "body/claw_l", 0F, 0F, 0F),
            new Key(3, 6F, "body/claw_l", -30F, 20F, 0F),
            new Key(3, 14F, "body/claw_l", 0F, 0F, 0F)};

    public static final float RUST_SCUTTLER_SCALE = 0.8F;

    /** Scrap Gunner: aus rostigen Platten zusammengeflickter Roboter-Schuetze mit Ruecklaufbeinen, Gatling-Arm, Greifzange, Rauchrohren und orange gluehendem Auge. Textur {@code entity/dungeon/scrap_gunner.png}. */
    public static LayerDefinition scrapGunner() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_hips = root.addOrReplaceChild("hips", CubeListBuilder.create()
                .texOffs(0, 40).addBox(-4.5F, -2F, -2.5F, 9, 3, 5, NONE)
                .texOffs(56, 73).addBox(-3.5F, -1F, -3.2F, 7, 2, 1, NONE)
                .texOffs(74, 63).addBox(-5.5F, -1.5F, -1.5F, 2, 2, 3, NONE)
                .texOffs(84, 63).addBox(3.5F, -1.5F, -1.5F, 2, 2, 3, NONE),
                PartPose.offsetAndRotation(0F, 11F, 0.5F, 0F, 0F, 0F));
        PartDefinition p_torso = p_hips.addOrReplaceChild("torso", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5F, -12F, -3.5F, 10, 12, 7, NONE)
                .texOffs(100, 48).addBox(-4.5F, -11.5F, -4.5F, 9, 6, 1, NONE)
                .texOffs(36, 63).addBox(-3F, -5F, -4.4F, 6, 4, 1, NONE)
                .texOffs(24, 68).addBox(-2.5F, -4.5F, -4.8F, 5, 3, 1, NONE)
                .texOffs(48, 73).addBox(-1.5F, -4.5F, -5.3F, 1, 3, 1, NONE)
                .texOffs(52, 73).addBox(0.5F, -4.5F, -5.3F, 1, 3, 1, NONE)
                .texOffs(28, 40).addBox(-3.5F, 0F, -2.5F, 7, 3, 5, NONE)
                .texOffs(58, 0).addBox(-4F, -11F, 3.5F, 8, 9, 3, NONE)
                .texOffs(100, 56).addBox(-4.5F, -4F, 3F, 9, 1, 4, NONE)
                .texOffs(70, 30).addBox(-5.5F, -10F, -2F, 1, 5, 4, NONE)
                .texOffs(80, 30).addBox(4.5F, -7F, -1F, 1, 5, 4, NONE)
                .texOffs(44, 48).addBox(-5F, -12.5F, -3F, 10, 1, 6, NONE)
                .texOffs(0, 77).addBox(-3F, -2F, 6.5F, 2, 2, 1, NONE)
                .texOffs(6, 77).addBox(1F, -2F, 6.5F, 2, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0.175F, 0F, 0F));
        PartDefinition p_head = p_torso.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 19).addBox(-3F, -5F, -3.5F, 6, 5, 6, NONE)
                .texOffs(76, 48).addBox(-3.5F, -6F, -4F, 7, 2, 5, NONE)
                .texOffs(94, 63).addBox(-2F, -3.6F, -4.4F, 4, 4, 1, NONE)
                .texOffs(84, 68).addBox(-1.5F, -3.1F, -4.9F, 3, 3, 1, NONE)
                .texOffs(12, 77).addBox(-2.5F, -0.5F, -4.2F, 5, 1, 1, NONE)
                .texOffs(80, 56).addBox(-4F, -4F, -1.5F, 1, 3, 3, NONE)
                .texOffs(24, 73).addBox(3F, -4.5F, -2F, 1, 2, 2, NONE)
                .texOffs(20, 68).addBox(1.5F, -10F, 1F, 1, 4, 1, NONE)
                .texOffs(96, 73).addBox(1F, -10.5F, 0.5F, 2, 1, 2, NONE)
                .texOffs(104, 73).addBox(-2.5F, -6.5F, 0.5F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, -11.5F, -2F, -0.14F, 0F, 0F));
        PartDefinition p_arm_r = p_torso.addOrReplaceChild("arm_r", CubeListBuilder.create()
                .texOffs(0, 30).addBox(-4.5F, -2.5F, -3F, 5, 4, 6, NONE)
                .texOffs(90, 30).addBox(-5F, -3F, -2F, 1, 5, 4, NONE)
                .texOffs(110, 40).addBox(-3F, 1F, -1.5F, 3, 5, 3, NONE)
                .texOffs(30, 73).addBox(-2.5F, 2F, 1.5F, 2, 3, 1, NONE),
                PartPose.offsetAndRotation(-6F, -9F, 0F, -0.873F, 0F, 0.105F));
        PartDefinition p_gun = p_arm_r.addOrReplaceChild("gun", CubeListBuilder.create()
                .texOffs(34, 0).addBox(-2.5F, -1F, -4F, 5, 5, 7, NONE)
                .texOffs(0, 63).addBox(-3F, -1.5F, -2F, 6, 1, 4, NONE)
                .texOffs(0, 48).addBox(-4.8F, 0F, -2.5F, 2, 4, 4, NONE)
                .texOffs(92, 68).addBox(-4.5F, 4F, -1.5F, 2, 2, 2, NONE)
                .texOffs(36, 68).addBox(-2F, 4F, 3F, 4, 2, 2, NONE)
                .texOffs(0, 68).addBox(1.5F, -2.5F, -1F, 1, 2, 3, NONE),
                PartPose.offsetAndRotation(-1.5F, 5.5F, 0F, 0.698F, 0F, 0F));
        PartDefinition p_barrels = p_gun.addOrReplaceChild("barrels", CubeListBuilder.create()
                .texOffs(24, 19).addBox(-1.5F, -1.5F, -10F, 1, 1, 10, NONE)
                .texOffs(46, 19).addBox(0.5F, -1.5F, -10F, 1, 1, 10, NONE)
                .texOffs(68, 19).addBox(-1.5F, 0.5F, -10F, 1, 1, 10, NONE)
                .texOffs(90, 19).addBox(0.5F, 0.5F, -10F, 1, 1, 10, NONE)
                .texOffs(22, 30).addBox(-0.5F, -0.5F, -9F, 1, 1, 9, NONE)
                .texOffs(104, 63).addBox(-2F, -2F, -3F, 4, 4, 1, NONE)
                .texOffs(114, 63).addBox(-2F, -2F, -8F, 4, 4, 1, NONE)
                .texOffs(24, 77).addBox(-1.5F, -1.5F, -10.6F, 1, 1, 1, NONE)
                .texOffs(28, 77).addBox(0.5F, 0.5F, -10.6F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 2F, -4F, 0F, 0F, 0.209F));
        PartDefinition p_arm_l = p_torso.addOrReplaceChild("arm_l", CubeListBuilder.create()
                .texOffs(92, 40).addBox(-0.5F, -2F, -2.5F, 4, 3, 5, NONE)
                .texOffs(12, 48).addBox(0.5F, 0F, -1F, 2, 6, 2, NONE)
                .texOffs(48, 68).addBox(0F, 0.5F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(5.5F, -9F, 0F, 0.14F, 0F, -0.175F));
        PartDefinition p_fore_l = p_arm_l.addOrReplaceChild("fore_l", CubeListBuilder.create()
                .texOffs(120, 48).addBox(-1F, 0F, -1F, 2, 5, 2, NONE)
                .texOffs(50, 63).addBox(-1.5F, 4.5F, -1.5F, 3, 2, 3, NONE)
                .texOffs(112, 73).addBox(-1.5F, 1F, -1.6F, 3, 2, 1, NONE),
                PartPose.offsetAndRotation(1.5F, 5F, 0F, -0.611F, 0F, 0F));
        PartDefinition p_pincer_a = p_fore_l.addOrReplaceChild("pincer_a", CubeListBuilder.create()
                .texOffs(8, 68).addBox(-1F, 0F, -1F, 1, 3, 2, NONE)
                .texOffs(36, 73).addBox(-0.4F, 2.5F, -1F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, 0F, 0F, -0.384F));
        PartDefinition p_pincer_b = p_fore_l.addOrReplaceChild("pincer_b", CubeListBuilder.create()
                .texOffs(14, 68).addBox(0F, 0F, -1F, 1, 3, 2, NONE)
                .texOffs(42, 73).addBox(-0.6F, 2.5F, -1F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, 0F, 0F, 0.384F));
        PartDefinition p_stack = p_torso.addOrReplaceChild("stack", CubeListBuilder.create()
                .texOffs(112, 0).addBox(-1.5F, -9F, -1.5F, 3, 9, 3, NONE)
                .texOffs(12, 56).addBox(-2F, -10F, -2F, 4, 2, 4, NONE)
                .texOffs(120, 73).addBox(-1F, -10.5F, -1F, 2, 1, 2, NONE)
                .texOffs(20, 63).addBox(-2F, -4F, -2F, 4, 1, 4, NONE),
                PartPose.offsetAndRotation(2F, -9.5F, 5F, -0.244F, 0F, 0.175F));
        PartDefinition p_stack2 = p_torso.addOrReplaceChild("stack2", CubeListBuilder.create()
                .texOffs(20, 48).addBox(-1F, -6F, -1F, 2, 6, 2, NONE)
                .texOffs(62, 63).addBox(-1.5F, -7F, -1.5F, 3, 2, 3, NONE)
                .texOffs(32, 77).addBox(-0.5F, -7.5F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-2.5F, -8F, 5F, -0.349F, 0F, -0.244F));
        PartDefinition p_cable_0 = p_torso.addOrReplaceChild("cable_0", CubeListBuilder.create()
                .texOffs(66, 30).addBox(-0.5F, 0F, -0.5F, 1, 9, 1, NONE),
                PartPose.offsetAndRotation(-4.6F, -11F, 4F, 0.524F, 0F, 0.244F));
        PartDefinition p_cable_1 = p_torso.addOrReplaceChild("cable_1", CubeListBuilder.create()
                .texOffs(100, 30).addBox(-0.5F, 0F, -0.5F, 1, 8, 1, NONE),
                PartPose.offsetAndRotation(4F, -10F, 5.5F, 0.698F, 0F, -0.175F));
        PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create()
                .texOffs(80, 0).addBox(-2F, -1F, -2F, 4, 8, 4, NONE)
                .texOffs(28, 48).addBox(-2.6F, 0F, -1.5F, 1, 5, 3, NONE)
                .texOffs(72, 73).addBox(-2.5F, 1F, -2.6F, 5, 2, 1, NONE)
                .texOffs(88, 56).addBox(-1F, 1F, 2F, 2, 5, 1, NONE),
                PartPose.offsetAndRotation(-3.2F, 11F, 0.5F, 0.454F, 0F, 0.052F));
        PartDefinition p_shin_r = p_leg_r.addOrReplaceChild("shin_r", CubeListBuilder.create()
                .texOffs(42, 30).addBox(-1.5F, 0F, -1.5F, 3, 7, 3, NONE)
                .texOffs(60, 56).addBox(-2F, 1F, -2.5F, 4, 5, 1, NONE)
                .texOffs(0, 56).addBox(-1F, 0F, 1.5F, 2, 6, 1, NONE)
                .texOffs(28, 56).addBox(-2F, -1F, -2F, 4, 2, 4, NONE),
                PartPose.offsetAndRotation(0F, 5.5F, 0.5F, -0.873F, 0F, 0F));
        PartDefinition p_foot_r = p_shin_r.addOrReplaceChild("foot_r", CubeListBuilder.create()
                .texOffs(52, 40).addBox(-2F, 0F, -4F, 4, 2, 6, NONE)
                .texOffs(100, 68).addBox(-2.5F, 0.5F, -5.5F, 2, 2, 2, NONE)
                .texOffs(108, 68).addBox(0.5F, 0.5F, -5.5F, 2, 2, 2, NONE)
                .texOffs(116, 68).addBox(-1F, 0.5F, 1.5F, 2, 2, 2, NONE)
                .texOffs(60, 68).addBox(-1.5F, -1F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.419F, 0F, 0F));
        PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create()
                .texOffs(96, 0).addBox(-2F, -1F, -2F, 4, 8, 4, NONE)
                .texOffs(36, 48).addBox(1.6F, 0F, -1.5F, 1, 5, 3, NONE)
                .texOffs(84, 73).addBox(-2.5F, 1F, -2.6F, 5, 2, 1, NONE)
                .texOffs(94, 56).addBox(-1F, 1F, 2F, 2, 5, 1, NONE),
                PartPose.offsetAndRotation(3.2F, 11F, 0.5F, 0.454F, 0F, -0.052F));
        PartDefinition p_shin_l = p_leg_l.addOrReplaceChild("shin_l", CubeListBuilder.create()
                .texOffs(54, 30).addBox(-1.5F, 0F, -1.5F, 3, 7, 3, NONE)
                .texOffs(70, 56).addBox(-2F, 1F, -2.5F, 4, 5, 1, NONE)
                .texOffs(6, 56).addBox(-1F, 0F, 1.5F, 2, 6, 1, NONE)
                .texOffs(44, 56).addBox(-2F, -1F, -2F, 4, 2, 4, NONE),
                PartPose.offsetAndRotation(0F, 5.5F, 0.5F, -0.873F, 0F, 0F));
        PartDefinition p_foot_l = p_shin_l.addOrReplaceChild("foot_l", CubeListBuilder.create()
                .texOffs(72, 40).addBox(-2F, 0F, -4F, 4, 2, 6, NONE)
                .texOffs(0, 73).addBox(-2.5F, 0.5F, -5.5F, 2, 2, 2, NONE)
                .texOffs(8, 73).addBox(0.5F, 0.5F, -5.5F, 2, 2, 2, NONE)
                .texOffs(16, 73).addBox(-1F, 0.5F, 1.5F, 2, 2, 2, NONE)
                .texOffs(72, 68).addBox(-1.5F, -1F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.419F, 0F, 0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final Role[] SCRAP_GUNNER_ROLES = {new Role("hips/torso", "body", 0.00F), new Role("hips/torso/head", "head", 0.00F), new Role("hips/torso/arm_r", "arm_r", 0.00F), new Role("hips/torso/arm_l", "arm_l", 0.00F), new Role("leg_r", "leg_r", 0.00F), new Role("leg_l", "leg_l", 0.00F)};

    public static final Key[] SCRAP_GUNNER_POSES = {
            new Key(2, 0F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(2, 2F, "hips/torso/arm_r", -14F, 10F, 0F),
            new Key(2, 4F, "hips/torso/arm_r", -32F, 10F, 0F),
            new Key(2, 5F, "hips/torso/arm_r", -14F, 10F, 0F),
            new Key(2, 7F, "hips/torso/arm_r", -30F, 10F, 0F),
            new Key(2, 8F, "hips/torso/arm_r", -14F, 10F, 0F),
            new Key(2, 10F, "hips/torso/arm_r", -28F, 10F, 0F),
            new Key(2, 12F, "hips/torso/arm_r", -14F, 10F, 0F),
            new Key(2, 14F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso/arm_r/gun", 0F, 0F, 0F),
            new Key(2, 2F, "hips/torso/arm_r/gun", -6F, 0F, 0F),
            new Key(2, 4F, "hips/torso/arm_r/gun", -14F, 0F, 0F),
            new Key(2, 5F, "hips/torso/arm_r/gun", -6F, 0F, 0F),
            new Key(2, 7F, "hips/torso/arm_r/gun", -14F, 0F, 0F),
            new Key(2, 8F, "hips/torso/arm_r/gun", -6F, 0F, 0F),
            new Key(2, 10F, "hips/torso/arm_r/gun", -14F, 0F, 0F),
            new Key(2, 12F, "hips/torso/arm_r/gun", -6F, 0F, 0F),
            new Key(2, 14F, "hips/torso/arm_r/gun", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso/arm_r/gun/barrels", 0F, 0F, 0F),
            new Key(2, 14F, "hips/torso/arm_r/gun/barrels", 0F, 0F, 720F),
            new Key(2, 0F, "hips/torso", 0F, 0F, 0F),
            new Key(2, 2F, "hips/torso", -4F, 16F, 0F),
            new Key(2, 4F, "hips/torso", -12F, 16F, 0F),
            new Key(2, 5F, "hips/torso", -6F, 16F, 0F),
            new Key(2, 7F, "hips/torso", -12F, 16F, 0F),
            new Key(2, 8F, "hips/torso", -6F, 16F, 0F),
            new Key(2, 10F, "hips/torso", -11F, 16F, 0F),
            new Key(2, 12F, "hips/torso", -4F, 16F, 0F),
            new Key(2, 14F, "hips/torso", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso/head", 0F, 0F, 0F),
            new Key(2, 2F, "hips/torso/head", 6F, -10F, 0F),
            new Key(2, 12F, "hips/torso/head", 6F, -10F, 0F),
            new Key(2, 14F, "hips/torso/head", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso/stack", 0F, 0F, 0F),
            new Key(2, 4F, "hips/torso/stack", -10F, 0F, 6F),
            new Key(2, 7F, "hips/torso/stack", 4F, 0F, -4F),
            new Key(2, 10F, "hips/torso/stack", -8F, 0F, 6F),
            new Key(2, 14F, "hips/torso/stack", 0F, 0F, 0F),
            new Key(2, 0F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(2, 2F, "hips/torso/arm_l", -30F, 0F, -20F),
            new Key(2, 12F, "hips/torso/arm_l", -30F, 0F, -20F),
            new Key(2, 14F, "hips/torso/arm_l", 0F, 0F, 0F)};

    public static final float SCRAP_GUNNER_SCALE = 1.0F;

    /** Void Shade: schlanker Schatten aus Leere mit Kapuze, violett leuchtenden Augen und Rissen, langen Klauenarmen und zerfasertem Schleier. Textur {@code entity/dungeon/void_shade.png}. */
    public static LayerDefinition voidShade() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_shade = root.addOrReplaceChild("shade", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F, 8.5F, 0F, 0F, 0F, 0F));
        PartDefinition p_torso = p_shade.addOrReplaceChild("torso", CubeListBuilder.create()
                .texOffs(24, 0).addBox(-4F, -11F, -2.5F, 8, 6, 5, NONE)
                .texOffs(78, 22).addBox(-3F, -6F, -2F, 6, 3, 4, NONE)
                .texOffs(50, 12).addBox(-2.5F, -4F, -2F, 5, 4, 4, NONE)
                .texOffs(50, 43).addBox(-2.5F, -10.5F, -2.8F, 1, 2, 1, NONE)
                .texOffs(54, 43).addBox(-1.5F, -9F, -2.8F, 1, 2, 1, NONE)
                .texOffs(58, 43).addBox(-0.5F, -7.5F, -2.8F, 1, 2, 1, NONE)
                .texOffs(0, 48).addBox(0.5F, -6.5F, -2.8F, 2, 1, 1, NONE)
                .texOffs(62, 43).addBox(1.5F, -10F, -2.8F, 1, 2, 1, NONE)
                .texOffs(18, 43).addBox(-2F, -5.5F, -2.3F, 1, 3, 1, NONE)
                .texOffs(110, 43).addBox(-3.5F, -10F, -2.7F, 7, 1, 1, NONE)
                .texOffs(80, 30).addBox(-4.5F, -11.5F, -2F, 9, 2, 4, NONE)
                .texOffs(100, 12).addBox(-2F, -10F, 2.2F, 4, 7, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0.14F, 0F, 0F));
        PartDefinition p_head = p_torso.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3F, -6F, -3F, 6, 6, 6, NONE)
                .texOffs(68, 12).addBox(-3.5F, -5.5F, -3.6F, 7, 6, 2, NONE)
                .texOffs(12, 37).addBox(-2F, -5F, -3.9F, 4, 5, 1, NONE)
                .texOffs(6, 48).addBox(-1.9F, -3.5F, -4.2F, 2, 1, 1, NONE)
                .texOffs(12, 48).addBox(0.1F, -3.5F, -4.2F, 2, 1, 1, NONE)
                .texOffs(36, 48).addBox(-1.2F, -2.5F, -4.1F, 1, 1, 1, NONE)
                .texOffs(40, 48).addBox(0.6F, -1.8F, -4.1F, 1, 1, 1, NONE)
                .texOffs(16, 30).addBox(-2F, -6.5F, 2.5F, 4, 4, 3, NONE)
                .texOffs(22, 37).addBox(-1F, -6F, 5F, 2, 3, 3, NONE)
                .texOffs(12, 43).addBox(-0.5F, -5.5F, 7.5F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, -11F, -0.5F, 0.105F, 0F, 0F));
        PartDefinition p_crown_0 = p_head.addOrReplaceChild("crown_0", CubeListBuilder.create()
                .texOffs(22, 43).addBox(-0.5F, -3F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, -6F, 0.5F, -0.419F, 0F, 0F));
        PartDefinition p_crown_1 = p_head.addOrReplaceChild("crown_1", CubeListBuilder.create()
                .texOffs(66, 43).addBox(-0.5F, -2F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-2F, -6F, 1F, -0.314F, 0F, -0.454F));
        PartDefinition p_crown_2 = p_head.addOrReplaceChild("crown_2", CubeListBuilder.create()
                .texOffs(70, 43).addBox(-0.5F, -2F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(2F, -6F, 1F, -0.314F, 0F, 0.454F));
        PartDefinition p_arm_r = p_torso.addOrReplaceChild("arm_r", CubeListBuilder.create()
                .texOffs(72, 0).addBox(-1F, -1F, -1F, 2, 9, 2, NONE)
                .texOffs(98, 22).addBox(-3F, -2F, -2F, 4, 3, 4, NONE)
                .texOffs(116, 37).addBox(-1.7F, 2F, -0.5F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(-4.5F, -10F, 0F, 0.105F, 0F, 0.157F));
        PartDefinition p_fore_r = p_arm_r.addOrReplaceChild("fore_r", CubeListBuilder.create()
                .texOffs(80, 0).addBox(-1F, 0F, -1F, 2, 9, 2, NONE)
                .texOffs(106, 30).addBox(-1.5F, 2F, -1.5F, 3, 3, 3, NONE)
                .texOffs(120, 37).addBox(-1.8F, 5F, -0.5F, 1, 4, 1, NONE)
                .texOffs(104, 37).addBox(-1.5F, -1F, 0F, 1, 3, 2, NONE),
                PartPose.offsetAndRotation(0F, 7.5F, 0F, -0.384F, 0F, 0F));
        PartDefinition p_hand_r = p_fore_r.addOrReplaceChild("hand_r", CubeListBuilder.create()
                .texOffs(64, 37).addBox(-1.5F, 0F, -1.5F, 3, 2, 3, NONE)
                .texOffs(18, 48).addBox(-1F, -0.5F, -1.8F, 2, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 8.5F, 0F, -0.175F, 0F, 0F));
        PartDefinition p_claw_r0 = p_hand_r.addOrReplaceChild("claw_r0", CubeListBuilder.create()
                .texOffs(64, 30).addBox(-0.5F, 0F, -0.5F, 1, 6, 1, NONE),
                PartPose.offsetAndRotation(-1.3F, 1.5F, 0.1F, -0.244F, -0.175F, -0.035F));
        PartDefinition p_tip_r0 = p_claw_r0.addOrReplaceChild("tip_r0", CubeListBuilder.create()
                .texOffs(26, 43).addBox(-0.5F, 0F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 5.5F, 0F, -0.524F, 0F, 0F));
        PartDefinition p_claw_r1 = p_hand_r.addOrReplaceChild("claw_r1", CubeListBuilder.create()
                .texOffs(70, 22).addBox(-0.5F, 0F, -0.5F, 1, 7, 1, NONE),
                PartPose.offsetAndRotation(-0.2F, 1.5F, -0.5F, -0.244F, 0F, 0.07F));
        PartDefinition p_tip_r1 = p_claw_r1.addOrReplaceChild("tip_r1", CubeListBuilder.create()
                .texOffs(30, 43).addBox(-0.5F, 0F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, -0.524F, 0F, 0F));
        PartDefinition p_claw_r2 = p_hand_r.addOrReplaceChild("claw_r2", CubeListBuilder.create()
                .texOffs(68, 30).addBox(-0.5F, 0F, -0.5F, 1, 6, 1, NONE),
                PartPose.offsetAndRotation(0.9F, 1.5F, 0.1F, -0.244F, 0.175F, 0.175F));
        PartDefinition p_tip_r2 = p_claw_r2.addOrReplaceChild("tip_r2", CubeListBuilder.create()
                .texOffs(34, 43).addBox(-0.5F, 0F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 5.5F, 0F, -0.524F, 0F, 0F));
        PartDefinition p_thumb_r = p_hand_r.addOrReplaceChild("thumb_r", CubeListBuilder.create()
                .texOffs(124, 37).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(1.4F, 1F, -1F, -0.698F, 0F, -0.524F));
        PartDefinition p_arm_l = p_torso.addOrReplaceChild("arm_l", CubeListBuilder.create()
                .texOffs(88, 0).addBox(-1F, -1F, -1F, 2, 9, 2, NONE)
                .texOffs(0, 30).addBox(-1F, -2F, -2F, 4, 3, 4, NONE)
                .texOffs(0, 43).addBox(0.7F, 2F, -0.5F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(4.5F, -10F, 0F, 0.105F, 0F, -0.157F));
        PartDefinition p_fore_l = p_arm_l.addOrReplaceChild("fore_l", CubeListBuilder.create()
                .texOffs(96, 0).addBox(-1F, 0F, -1F, 2, 9, 2, NONE)
                .texOffs(0, 37).addBox(-1.5F, 2F, -1.5F, 3, 3, 3, NONE)
                .texOffs(4, 43).addBox(0.8F, 5F, -0.5F, 1, 4, 1, NONE)
                .texOffs(110, 37).addBox(0.5F, -1F, 0F, 1, 3, 2, NONE),
                PartPose.offsetAndRotation(0F, 7.5F, 0F, -0.384F, 0F, 0F));
        PartDefinition p_hand_l = p_fore_l.addOrReplaceChild("hand_l", CubeListBuilder.create()
                .texOffs(76, 37).addBox(-1.5F, 0F, -1.5F, 3, 2, 3, NONE)
                .texOffs(24, 48).addBox(-1F, -0.5F, -1.8F, 2, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 8.5F, 0F, -0.175F, 0F, 0F));
        PartDefinition p_claw_l0 = p_hand_l.addOrReplaceChild("claw_l0", CubeListBuilder.create()
                .texOffs(72, 30).addBox(-0.5F, 0F, -0.5F, 1, 6, 1, NONE),
                PartPose.offsetAndRotation(-0.9F, 1.5F, 0.1F, -0.244F, -0.175F, -0.175F));
        PartDefinition p_tip_l0 = p_claw_l0.addOrReplaceChild("tip_l0", CubeListBuilder.create()
                .texOffs(38, 43).addBox(-0.5F, 0F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 5.5F, 0F, -0.524F, 0F, 0F));
        PartDefinition p_claw_l1 = p_hand_l.addOrReplaceChild("claw_l1", CubeListBuilder.create()
                .texOffs(74, 22).addBox(-0.5F, 0F, -0.5F, 1, 7, 1, NONE),
                PartPose.offsetAndRotation(0.2F, 1.5F, -0.5F, -0.244F, 0F, -0.07F));
        PartDefinition p_tip_l1 = p_claw_l1.addOrReplaceChild("tip_l1", CubeListBuilder.create()
                .texOffs(42, 43).addBox(-0.5F, 0F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, -0.524F, 0F, 0F));
        PartDefinition p_claw_l2 = p_hand_l.addOrReplaceChild("claw_l2", CubeListBuilder.create()
                .texOffs(76, 30).addBox(-0.5F, 0F, -0.5F, 1, 6, 1, NONE),
                PartPose.offsetAndRotation(1.3F, 1.5F, 0.1F, -0.244F, 0.175F, 0.035F));
        PartDefinition p_tip_l2 = p_claw_l2.addOrReplaceChild("tip_l2", CubeListBuilder.create()
                .texOffs(46, 43).addBox(-0.5F, 0F, -0.5F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 5.5F, 0F, -0.524F, 0F, 0F));
        PartDefinition p_thumb_l = p_hand_l.addOrReplaceChild("thumb_l", CubeListBuilder.create()
                .texOffs(8, 43).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(-1.4F, 1F, -1F, -0.698F, 0F, 0.524F));
        PartDefinition p_spike_r0 = p_torso.addOrReplaceChild("spike_r0", CubeListBuilder.create()
                .texOffs(30, 30).addBox(-1F, -5F, -1F, 2, 5, 2, NONE),
                PartPose.offsetAndRotation(-3.6F, -11.5F, 0.5F, -0.262F, 0F, -0.436F));
        PartDefinition p_spike_r1 = p_torso.addOrReplaceChild("spike_r1", CubeListBuilder.create()
                .texOffs(88, 37).addBox(-1F, -3F, -1F, 2, 3, 2, NONE),
                PartPose.offsetAndRotation(-5.2F, -10.5F, 1.5F, 0.175F, 0F, -0.698F));
        PartDefinition p_spike_l0 = p_torso.addOrReplaceChild("spike_l0", CubeListBuilder.create()
                .texOffs(38, 30).addBox(-1F, -5F, -1F, 2, 5, 2, NONE),
                PartPose.offsetAndRotation(3.6F, -11.5F, 0.5F, -0.262F, 0F, 0.436F));
        PartDefinition p_spike_l1 = p_torso.addOrReplaceChild("spike_l1", CubeListBuilder.create()
                .texOffs(96, 37).addBox(-1F, -3F, -1F, 2, 3, 2, NONE),
                PartPose.offsetAndRotation(5.2F, -10.5F, 1.5F, 0.175F, 0F, 0.698F));
        PartDefinition p_cape_0 = p_torso.addOrReplaceChild("cape_0", CubeListBuilder.create()
                .texOffs(116, 0).addBox(-1F, 0F, 0F, 2, 9, 1, NONE),
                PartPose.offsetAndRotation(-3F, -11F, 2.6F, 0.401F, 0F, 0.157F));
        PartDefinition p_cape_0b = p_cape_0.addOrReplaceChild("cape_0b", CubeListBuilder.create()
                .texOffs(40, 22).addBox(-1F, 0F, 0F, 2, 7, 1, NONE),
                PartPose.offsetAndRotation(0F, 8.5F, 0F, 0.14F, 0F, 0F));
        PartDefinition p_cape_1 = p_torso.addOrReplaceChild("cape_1", CubeListBuilder.create()
                .texOffs(104, 0).addBox(-1F, 0F, 0F, 2, 10, 1, NONE),
                PartPose.offsetAndRotation(-1F, -11F, 2.6F, 0.297F, 0F, 0.052F));
        PartDefinition p_cape_1b = p_cape_1.addOrReplaceChild("cape_1b", CubeListBuilder.create()
                .texOffs(122, 0).addBox(-1F, 0F, 0F, 2, 9, 1, NONE),
                PartPose.offsetAndRotation(0F, 9.5F, 0F, 0.14F, 0F, 0F));
        PartDefinition p_cape_2 = p_torso.addOrReplaceChild("cape_2", CubeListBuilder.create()
                .texOffs(110, 0).addBox(-1F, 0F, 0F, 2, 10, 1, NONE),
                PartPose.offsetAndRotation(1F, -11F, 2.6F, 0.297F, 0F, -0.052F));
        PartDefinition p_cape_2b = p_cape_2.addOrReplaceChild("cape_2b", CubeListBuilder.create()
                .texOffs(6, 12).addBox(-1F, 0F, 0F, 2, 8, 1, NONE),
                PartPose.offsetAndRotation(0F, 9.5F, 0F, 0.14F, 0F, 0F));
        PartDefinition p_cape_3 = p_torso.addOrReplaceChild("cape_3", CubeListBuilder.create()
                .texOffs(0, 12).addBox(-1F, 0F, 0F, 2, 9, 1, NONE),
                PartPose.offsetAndRotation(3F, -11F, 2.6F, 0.401F, 0F, -0.157F));
        PartDefinition p_cape_3b = p_cape_3.addOrReplaceChild("cape_3b", CubeListBuilder.create()
                .texOffs(46, 30).addBox(-1F, 0F, 0F, 2, 6, 1, NONE),
                PartPose.offsetAndRotation(0F, 8.5F, 0F, 0.14F, 0F, 0F));
        PartDefinition p_core = p_shade.addOrReplaceChild("core", CubeListBuilder.create()
                .texOffs(50, 0).addBox(-3F, -1F, -2.5F, 6, 6, 5, NONE)
                .texOffs(86, 12).addBox(-2F, 4F, -1.5F, 4, 5, 3, NONE)
                .texOffs(32, 37).addBox(-1F, 8F, -1F, 2, 4, 2, NONE)
                .texOffs(24, 12).addBox(-3.5F, -1.5F, -3F, 7, 2, 6, NONE)
                .texOffs(30, 48).addBox(-1F, -1F, -3.3F, 2, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
        PartDefinition p_veil_0 = p_core.addOrReplaceChild("veil_0", CubeListBuilder.create()
                .texOffs(110, 12).addBox(-1.5F, 0F, -0.5F, 3, 7, 1, NONE),
                PartPose.offsetAndRotation(1.026F, 0F, -2.255F, -0.197F, 0F, -0.072F));
        PartDefinition p_veil_0b = p_veil_0.addOrReplaceChild("veil_0b", CubeListBuilder.create()
                .texOffs(40, 37).addBox(-1.5F, 0F, -0.5F, 3, 5, 1, NONE)
                .texOffs(74, 43).addBox(-0.5F, 4F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, -0.066F, 0F, -0.024F));
        PartDefinition p_veil_1 = p_core.addOrReplaceChild("veil_1", CubeListBuilder.create()
                .texOffs(46, 22).addBox(-1F, 0F, -0.5F, 2, 7, 1, NONE),
                PartPose.offsetAndRotation(2.598F, 0F, -1.2F, -0.105F, 0F, -0.181F));
        PartDefinition p_veil_1b = p_veil_1.addOrReplaceChild("veil_1b", CubeListBuilder.create()
                .texOffs(52, 30).addBox(-1F, 0F, -0.5F, 2, 6, 1, NONE)
                .texOffs(78, 43).addBox(-0.5F, 5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, -0.035F, 0F, -0.06F));
        PartDefinition p_veil_2 = p_core.addOrReplaceChild("veil_2", CubeListBuilder.create()
                .texOffs(118, 12).addBox(-1.5F, 0F, -0.5F, 3, 7, 1, NONE),
                PartPose.offsetAndRotation(2.954F, 0F, 0.417F, 0.036F, 0F, -0.206F));
        PartDefinition p_veil_2b = p_veil_2.addOrReplaceChild("veil_2b", CubeListBuilder.create()
                .texOffs(0, 22).addBox(-1.5F, 0F, -0.5F, 3, 7, 1, NONE)
                .texOffs(82, 43).addBox(-0.5F, 6F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.012F, 0F, -0.069F));
        PartDefinition p_veil_3 = p_core.addOrReplaceChild("veil_3", CubeListBuilder.create()
                .texOffs(52, 22).addBox(-1F, 0F, -0.5F, 2, 7, 1, NONE),
                PartPose.offsetAndRotation(1.928F, 0F, 1.839F, 0.16F, 0F, -0.135F));
        PartDefinition p_veil_3b = p_veil_3.addOrReplaceChild("veil_3b", CubeListBuilder.create()
                .texOffs(12, 12).addBox(-1F, 0F, -0.5F, 2, 8, 1, NONE)
                .texOffs(86, 43).addBox(-0.5F, 7F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.053F, 0F, -0.045F));
        PartDefinition p_veil_4 = p_core.addOrReplaceChild("veil_4", CubeListBuilder.create()
                .texOffs(8, 22).addBox(-1.5F, 0F, -0.5F, 3, 7, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 2.4F, 0.209F, 0F, 0F));
        PartDefinition p_veil_4b = p_veil_4.addOrReplaceChild("veil_4b", CubeListBuilder.create()
                .texOffs(48, 37).addBox(-1.5F, 0F, -0.5F, 3, 5, 1, NONE)
                .texOffs(90, 43).addBox(-0.5F, 4F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.07F, 0F, 0F));
        PartDefinition p_veil_5 = p_core.addOrReplaceChild("veil_5", CubeListBuilder.create()
                .texOffs(58, 22).addBox(-1F, 0F, -0.5F, 2, 7, 1, NONE),
                PartPose.offsetAndRotation(-1.928F, 0F, 1.839F, 0.16F, 0F, 0.135F));
        PartDefinition p_veil_5b = p_veil_5.addOrReplaceChild("veil_5b", CubeListBuilder.create()
                .texOffs(58, 30).addBox(-1F, 0F, -0.5F, 2, 6, 1, NONE)
                .texOffs(94, 43).addBox(-0.5F, 5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.053F, 0F, 0.045F));
        PartDefinition p_veil_6 = p_core.addOrReplaceChild("veil_6", CubeListBuilder.create()
                .texOffs(16, 22).addBox(-1.5F, 0F, -0.5F, 3, 7, 1, NONE),
                PartPose.offsetAndRotation(-2.954F, 0F, 0.417F, 0.036F, 0F, 0.206F));
        PartDefinition p_veil_6b = p_veil_6.addOrReplaceChild("veil_6b", CubeListBuilder.create()
                .texOffs(24, 22).addBox(-1.5F, 0F, -0.5F, 3, 7, 1, NONE)
                .texOffs(98, 43).addBox(-0.5F, 6F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.012F, 0F, 0.069F));
        PartDefinition p_veil_7 = p_core.addOrReplaceChild("veil_7", CubeListBuilder.create()
                .texOffs(64, 22).addBox(-1F, 0F, -0.5F, 2, 7, 1, NONE),
                PartPose.offsetAndRotation(-2.598F, 0F, -1.2F, -0.105F, 0F, 0.181F));
        PartDefinition p_veil_7b = p_veil_7.addOrReplaceChild("veil_7b", CubeListBuilder.create()
                .texOffs(18, 12).addBox(-1F, 0F, -0.5F, 2, 8, 1, NONE)
                .texOffs(102, 43).addBox(-0.5F, 7F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, -0.035F, 0F, 0.06F));
        PartDefinition p_veil_8 = p_core.addOrReplaceChild("veil_8", CubeListBuilder.create()
                .texOffs(32, 22).addBox(-1.5F, 0F, -0.5F, 3, 7, 1, NONE),
                PartPose.offsetAndRotation(-1.026F, 0F, -2.255F, -0.197F, 0F, 0.072F));
        PartDefinition p_veil_8b = p_veil_8.addOrReplaceChild("veil_8b", CubeListBuilder.create()
                .texOffs(56, 37).addBox(-1.5F, 0F, -0.5F, 3, 5, 1, NONE)
                .texOffs(106, 43).addBox(-0.5F, 4F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, -0.066F, 0F, 0.024F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final Role[] VOID_SHADE_ROLES = {new Role("shade", "bob", 0.00F), new Role("shade/torso", "body", 0.00F), new Role("shade/torso/head", "head", 0.00F), new Role("shade/torso/arm_r", "arm_r", 0.00F), new Role("shade/torso/arm_l", "arm_l", 0.00F), new Role("shade/torso/cape_0", "tentacle", 1.00F), new Role("shade/torso/cape_0/cape_0b", "tentacle", 2.00F), new Role("shade/torso/cape_1", "tentacle", 2.30F), new Role("shade/torso/cape_1/cape_1b", "tentacle", 3.30F), new Role("shade/torso/cape_2", "tentacle", 3.60F), new Role("shade/torso/cape_2/cape_2b", "tentacle", 4.60F), new Role("shade/torso/cape_3", "tentacle", 4.90F), new Role("shade/torso/cape_3/cape_3b", "tentacle", 5.90F), new Role("shade/core/veil_0", "tentacle", 0.00F), new Role("shade/core/veil_0/veil_0b", "tentacle", 0.50F), new Role("shade/core/veil_1", "tentacle", 0.90F), new Role("shade/core/veil_1/veil_1b", "tentacle", 1.40F), new Role("shade/core/veil_2", "tentacle", 1.80F), new Role("shade/core/veil_2/veil_2b", "tentacle", 2.30F), new Role("shade/core/veil_3", "tentacle", 2.70F), new Role("shade/core/veil_3/veil_3b", "tentacle", 3.20F), new Role("shade/core/veil_4", "tentacle", 3.60F), new Role("shade/core/veil_4/veil_4b", "tentacle", 4.10F), new Role("shade/core/veil_5", "tentacle", 4.50F), new Role("shade/core/veil_5/veil_5b", "tentacle", 5.00F), new Role("shade/core/veil_6", "tentacle", 5.40F), new Role("shade/core/veil_6/veil_6b", "tentacle", 5.90F), new Role("shade/core/veil_7", "tentacle", 6.30F), new Role("shade/core/veil_7/veil_7b", "tentacle", 6.80F), new Role("shade/core/veil_8", "tentacle", 7.20F), new Role("shade/core/veil_8/veil_8b", "tentacle", 7.70F)};

    public static final Key[] VOID_SHADE_POSES = {
            new Key(1, 0F, "shade/torso/arm_r", 0F, 0F, 0F),
            new Key(1, 3F, "shade/torso/arm_r", -150F, 0F, -25F),
            new Key(1, 6F, "shade/torso/arm_r", -20F, 0F, 35F),
            new Key(1, 8F, "shade/torso/arm_r", -10F, 0F, 20F),
            new Key(1, 10F, "shade/torso/arm_r", 0F, 0F, 0F),
            new Key(1, 0F, "shade/torso/arm_r/fore_r", 0F, 0F, 0F),
            new Key(1, 3F, "shade/torso/arm_r/fore_r", -30F, 0F, 0F),
            new Key(1, 6F, "shade/torso/arm_r/fore_r", 10F, 0F, 0F),
            new Key(1, 10F, "shade/torso/arm_r/fore_r", 0F, 0F, 0F),
            new Key(1, 0F, "shade/torso/arm_l", 0F, 0F, 0F),
            new Key(1, 3F, "shade/torso/arm_l", -40F, 0F, -10F),
            new Key(1, 6F, "shade/torso/arm_l", -70F, 0F, -20F),
            new Key(1, 10F, "shade/torso/arm_l", 0F, 0F, 0F),
            new Key(1, 0F, "shade/torso", 0F, 0F, 0F),
            new Key(1, 3F, "shade/torso", -10F, 25F, 0F),
            new Key(1, 6F, "shade/torso", 18F, -20F, 0F),
            new Key(1, 10F, "shade/torso", 0F, 0F, 0F),
            new Key(1, 0F, "shade/torso/head", 0F, 0F, 0F),
            new Key(1, 3F, "shade/torso/head", 5F, -15F, 0F),
            new Key(1, 6F, "shade/torso/head", -5F, 15F, 0F),
            new Key(1, 10F, "shade/torso/head", 0F, 0F, 0F),
            new Key(3, 0F, "shade/torso", 0F, 0F, 0F),
            new Key(3, 4F, "shade/torso", 42F, 0F, 0F),
            new Key(3, 7F, "shade/torso", 42F, 0F, 0F),
            new Key(3, 10F, "shade/torso", 0F, 0F, 0F),
            new Key(3, 0F, "shade/torso/head", 0F, 0F, 0F),
            new Key(3, 4F, "shade/torso/head", -25F, 0F, 0F),
            new Key(3, 7F, "shade/torso/head", -25F, 0F, 0F),
            new Key(3, 10F, "shade/torso/head", 0F, 0F, 0F),
            new Key(3, 0F, "shade/torso/arm_r", 0F, 0F, 0F),
            new Key(3, 4F, "shade/torso/arm_r", -70F, 0F, 50F),
            new Key(3, 7F, "shade/torso/arm_r", -70F, 0F, 50F),
            new Key(3, 10F, "shade/torso/arm_r", 0F, 0F, 0F),
            new Key(3, 0F, "shade/torso/arm_l", 0F, 0F, 0F),
            new Key(3, 4F, "shade/torso/arm_l", -70F, 0F, -50F),
            new Key(3, 7F, "shade/torso/arm_l", -70F, 0F, -50F),
            new Key(3, 10F, "shade/torso/arm_l", 0F, 0F, 0F),
            new Key(3, 0F, "shade/torso/arm_r/fore_r", 0F, 0F, 0F),
            new Key(3, 4F, "shade/torso/arm_r/fore_r", -60F, 0F, 0F),
            new Key(3, 7F, "shade/torso/arm_r/fore_r", -60F, 0F, 0F),
            new Key(3, 10F, "shade/torso/arm_r/fore_r", 0F, 0F, 0F),
            new Key(3, 0F, "shade/torso/arm_l/fore_l", 0F, 0F, 0F),
            new Key(3, 4F, "shade/torso/arm_l/fore_l", -60F, 0F, 0F),
            new Key(3, 7F, "shade/torso/arm_l/fore_l", -60F, 0F, 0F),
            new Key(3, 10F, "shade/torso/arm_l/fore_l", 0F, 0F, 0F),
            new Key(3, 0F, "shade/core", 0F, 0F, 0F),
            new Key(3, 4F, "shade/core", 20F, 0F, 0F),
            new Key(3, 7F, "shade/core", 20F, 0F, 0F),
            new Key(3, 10F, "shade/core", 0F, 0F, 0F)};

    public static final float VOID_SHADE_SCALE = 1.0F;

    /** Tomb Warden: Statuen-Golem aus dunklem Stein mit violetten Runen, Obelisk-Helm, gluehendem Runenkern, Steinhammer und Grabstein-Schild. Textur {@code entity/dungeon/tomb_warden.png}. */
    public static LayerDefinition tombWarden() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_hips = root.addOrReplaceChild("hips", CubeListBuilder.create()
                .texOffs(46, 38).addBox(-6.5F, -3F, -4F, 13, 4, 8, NONE)
                .texOffs(0, 51).addBox(-7F, -1.5F, -4.5F, 14, 2, 9, NONE)
                .texOffs(172, 72).addBox(-2F, 0.5F, 4F, 4, 5, 1, NONE),
                PartPose.offsetAndRotation(0F, 9F, 0F, 0F, 0F, 0F));
        PartDefinition p_torso = p_hips.addOrReplaceChild("torso", CubeListBuilder.create()
                .texOffs(0, 38).addBox(-5.5F, -4F, -4F, 11, 5, 8, NONE)
                .texOffs(48, 0).addBox(-9F, -15F, -5.5F, 18, 11, 11, NONE)
                .texOffs(30, 72).addBox(-3.5F, -13F, -6F, 7, 7, 1, NONE)
                .texOffs(160, 72).addBox(-2.5F, -12F, -6.4F, 5, 5, 1, NONE)
                .texOffs(78, 72).addBox(-8.5F, -14F, -6F, 4, 7, 1, NONE)
                .texOffs(88, 72).addBox(4.5F, -14F, -6F, 4, 7, 1, NONE)
                .texOffs(156, 38).addBox(-7F, -15F, 5F, 14, 10, 2, NONE)
                .texOffs(166, 51).addBox(-6F, -16F, -4F, 12, 2, 8, NONE)
                .texOffs(0, 24).addBox(-9.5F, -6F, -6F, 19, 2, 12, NONE)
                .texOffs(98, 81).addBox(-1F, -5F, -4.6F, 2, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -3F, 0F, 0.14F, 0F, 0F));
        PartDefinition p_head = p_torso.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(126, 24).addBox(-3.5F, -7F, -3.5F, 7, 7, 7, NONE)
                .texOffs(0, 81).addBox(-3F, -5F, -4F, 6, 4, 1, NONE)
                .texOffs(126, 81).addBox(-2.5F, -4F, -4.5F, 5, 1, 1, NONE)
                .texOffs(206, 51).addBox(-4F, -1.5F, -4F, 8, 2, 8, NONE)
                .texOffs(10, 72).addBox(-2.5F, -10F, -2.5F, 5, 3, 5, NONE)
                .texOffs(14, 81).addBox(-1.5F, -12F, -1.5F, 3, 2, 3, NONE)
                .texOffs(148, 81).addBox(-0.5F, -13F, -0.5F, 1, 1, 1, NONE)
                .texOffs(98, 72).addBox(-4F, -6F, -2F, 1, 4, 4, NONE)
                .texOffs(108, 72).addBox(3F, -6F, -2F, 1, 4, 4, NONE)
                .texOffs(126, 72).addBox(-2.6F, -10.5F, -2.6F, 5, 1, 5, NONE),
                PartPose.offsetAndRotation(0F, -16F, -1.5F, -0.14F, 0F, 0F));
        PartDefinition p_arm_r = p_torso.addOrReplaceChild("arm_r", CubeListBuilder.create()
                .texOffs(62, 24).addBox(-5F, -3F, -4.5F, 7, 5, 9, NONE)
                .texOffs(46, 51).addBox(-5.5F, 1.5F, -5F, 8, 1, 10, NONE)
                .texOffs(200, 62).addBox(-5.5F, -2F, -3F, 1, 3, 6, NONE)
                .texOffs(202, 24).addBox(-4F, 1F, -3F, 5, 8, 6, NONE),
                PartPose.offsetAndRotation(-10.5F, -12F, 0F, 0F, 0F, 0.052F));
        PartDefinition p_fore_r = p_arm_r.addOrReplaceChild("fore_r", CubeListBuilder.create()
                .texOffs(160, 0).addBox(-3.5F, 0F, -3.5F, 7, 8, 7, NONE)
                .texOffs(0, 62).addBox(-4F, 0F, -4F, 8, 2, 8, NONE)
                .texOffs(64, 62).addBox(-3F, 7.5F, -3F, 6, 4, 6, NONE)
                .texOffs(26, 81).addBox(-3.5F, 8F, -3.5F, 1, 2, 3, NONE),
                PartPose.offsetAndRotation(-1.5F, 8F, 0F, -0.873F, 0F, 0.105F));
        PartDefinition p_hammer = p_fore_r.addOrReplaceChild("hammer", CubeListBuilder.create()
                .texOffs(38, 38).addBox(-1F, -3F, -1F, 2, 11, 2, NONE)
                .texOffs(34, 81).addBox(-1.5F, -4F, -1.5F, 3, 1, 3, NONE)
                .texOffs(46, 81).addBox(-1.5F, 3F, -1.5F, 3, 1, 3, NONE)
                .texOffs(106, 0).addBox(-3.5F, 7.5F, -6F, 7, 6, 12, NONE)
                .texOffs(160, 62).addBox(-4F, 7F, -6.5F, 8, 7, 2, NONE)
                .texOffs(180, 62).addBox(-4F, 7F, 4.5F, 8, 7, 2, NONE)
                .texOffs(132, 62).addBox(-4F, 8.5F, -3F, 1, 4, 6, NONE)
                .texOffs(146, 62).addBox(3F, 8.5F, -3F, 1, 4, 6, NONE)
                .texOffs(58, 81).addBox(-1.5F, 9F, -7F, 3, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 10F, -0.5F, 0.838F, 0F, 0F));
        PartDefinition p_arm_l = p_torso.addOrReplaceChild("arm_l", CubeListBuilder.create()
                .texOffs(94, 24).addBox(-2F, -3F, -4.5F, 7, 5, 9, NONE)
                .texOffs(82, 51).addBox(-2.5F, 1.5F, -5F, 8, 1, 10, NONE)
                .texOffs(214, 62).addBox(4.5F, -2F, -3F, 1, 3, 6, NONE)
                .texOffs(224, 24).addBox(-1F, 1F, -3F, 5, 8, 6, NONE),
                PartPose.offsetAndRotation(10.5F, -12F, 0F, 0F, 0F, -0.052F));
        PartDefinition p_fore_l = p_arm_l.addOrReplaceChild("fore_l", CubeListBuilder.create()
                .texOffs(188, 0).addBox(-3.5F, 0F, -3.5F, 7, 8, 7, NONE)
                .texOffs(32, 62).addBox(-4F, 0F, -4F, 8, 2, 8, NONE)
                .texOffs(88, 62).addBox(-3F, 7.5F, -3F, 6, 4, 6, NONE),
                PartPose.offsetAndRotation(1.5F, 8F, 0F, -0.419F, 0F, -0.07F));
        PartDefinition p_shield = p_fore_l.addOrReplaceChild("shield", CubeListBuilder.create()
                .texOffs(24, 0).addBox(0F, -2F, -5F, 2, 13, 10, NONE)
                .texOffs(112, 62).addBox(0F, -4F, -4F, 2, 2, 8, NONE)
                .texOffs(146, 72).addBox(0F, -5F, -2.5F, 2, 1, 5, NONE)
                .texOffs(0, 0).addBox(-0.5F, -1.5F, -5.5F, 1, 13, 11, NONE)
                .texOffs(144, 0).addBox(2F, 0F, -3.5F, 1, 9, 7, NONE)
                .texOffs(118, 72).addBox(2.5F, 2F, -1.5F, 1, 4, 3, NONE)
                .texOffs(104, 81).addBox(2.5F, 3F, -2.5F, 1, 2, 1, NONE)
                .texOffs(108, 81).addBox(2.5F, 3F, 1.5F, 1, 2, 1, NONE)
                .texOffs(188, 38).addBox(1.5F, 10.5F, -5.5F, 2, 1, 11, NONE),
                PartPose.offsetAndRotation(3F, 3F, -0.5F, 0F, 0F, 0.035F));
        PartDefinition p_tabard = p_hips.addOrReplaceChild("tabard", CubeListBuilder.create()
                .texOffs(228, 62).addBox(-2.5F, 0F, -1F, 5, 8, 1, NONE)
                .texOffs(112, 81).addBox(-3F, 0F, -1.3F, 6, 1, 1, NONE)
                .texOffs(138, 81).addBox(-2F, 7F, -1.2F, 4, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0.5F, -4.5F, -0.07F, 0F, 0F));
        PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create()
                .texOffs(154, 24).addBox(-3F, -1F, -3F, 6, 8, 6, NONE)
                .texOffs(182, 72).addBox(-3.5F, 5.5F, -4.2F, 7, 3, 2, NONE)
                .texOffs(240, 62).addBox(-3.5F, 0F, -2F, 1, 5, 4, NONE),
                PartPose.offsetAndRotation(-4.2F, 9F, 0F, 0F, 0F, 0.035F));
        PartDefinition p_shin_r = p_leg_r.addOrReplaceChild("shin_r", CubeListBuilder.create()
                .texOffs(118, 51).addBox(-3F, 0F, -3F, 6, 5, 6, NONE)
                .texOffs(218, 72).addBox(-3.5F, 0F, -3.8F, 7, 4, 1, NONE)
                .texOffs(88, 38).addBox(-4F, 5F, -5F, 8, 3, 9, NONE)
                .texOffs(66, 81).addBox(-3.5F, 6F, -6F, 7, 2, 1, NONE)
                .texOffs(46, 72).addBox(-4F, 5.5F, -4F, 2, 2, 6, NONE),
                PartPose.offsetAndRotation(0F, 7F, 0F, 0F, 0F, 0F));
        PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create()
                .texOffs(178, 24).addBox(-3F, -1F, -3F, 6, 8, 6, NONE)
                .texOffs(200, 72).addBox(-3.5F, 5.5F, -4.2F, 7, 3, 2, NONE)
                .texOffs(0, 72).addBox(2.5F, 0F, -2F, 1, 5, 4, NONE),
                PartPose.offsetAndRotation(4.2F, 9F, 0F, 0F, 0F, -0.035F));
        PartDefinition p_shin_l = p_leg_l.addOrReplaceChild("shin_l", CubeListBuilder.create()
                .texOffs(142, 51).addBox(-3F, 0F, -3F, 6, 5, 6, NONE)
                .texOffs(234, 72).addBox(-3.5F, 0F, -3.8F, 7, 4, 1, NONE)
                .texOffs(122, 38).addBox(-4F, 5F, -5F, 8, 3, 9, NONE)
                .texOffs(82, 81).addBox(-3.5F, 6F, -6F, 7, 2, 1, NONE)
                .texOffs(62, 72).addBox(2.5F, 5.5F, -4F, 2, 2, 6, NONE),
                PartPose.offsetAndRotation(0F, 7F, 0F, 0F, 0F, 0F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    public static final Role[] TOMB_WARDEN_ROLES = {new Role("hips/torso", "body", 0.00F), new Role("hips/torso/head", "head", 0.00F), new Role("hips/torso/arm_r", "arm_r", 0.00F), new Role("hips/torso/arm_l", "arm_l", 0.00F), new Role("leg_r", "leg_r", 0.00F), new Role("leg_l", "leg_l", 0.00F)};

    public static final Key[] TOMB_WARDEN_POSES = {
            new Key(1, 0F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(1, 4F, "hips/torso/arm_r", -125F, 0F, 15F),
            new Key(1, 7F, "hips/torso/arm_r", -30F, 0F, -10F),
            new Key(1, 10F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(1, 0F, "hips/torso/arm_r/fore_r", 0F, 0F, 0F),
            new Key(1, 4F, "hips/torso/arm_r/fore_r", -15F, 0F, 0F),
            new Key(1, 7F, "hips/torso/arm_r/fore_r", 25F, 0F, 0F),
            new Key(1, 10F, "hips/torso/arm_r/fore_r", 0F, 0F, 0F),
            new Key(1, 0F, "hips/torso", 0F, 0F, 0F),
            new Key(1, 4F, "hips/torso", -8F, 20F, 0F),
            new Key(1, 7F, "hips/torso", 14F, -18F, 0F),
            new Key(1, 10F, "hips/torso", 0F, 0F, 0F),
            new Key(1, 0F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(1, 4F, "hips/torso/arm_l", -35F, 0F, -10F),
            new Key(1, 7F, "hips/torso/arm_l", -15F, 0F, 0F),
            new Key(1, 10F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(3, 0F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(3, 12F, "hips/torso/arm_r", -170F, 0F, 12F),
            new Key(3, 14F, "hips/torso/arm_r", -40F, 0F, 0F),
            new Key(3, 19F, "hips/torso/arm_r", -40F, 0F, 0F),
            new Key(3, 22F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(3, 0F, "hips/torso/arm_r/fore_r", 0F, 0F, 0F),
            new Key(3, 12F, "hips/torso/arm_r/fore_r", 10F, 0F, 0F),
            new Key(3, 14F, "hips/torso/arm_r/fore_r", 30F, 0F, 0F),
            new Key(3, 19F, "hips/torso/arm_r/fore_r", 30F, 0F, 0F),
            new Key(3, 22F, "hips/torso/arm_r/fore_r", 0F, 0F, 0F),
            new Key(3, 0F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(3, 12F, "hips/torso/arm_l", -150F, 0F, -12F),
            new Key(3, 14F, "hips/torso/arm_l", -40F, 0F, 0F),
            new Key(3, 19F, "hips/torso/arm_l", -40F, 0F, 0F),
            new Key(3, 22F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(3, 0F, "hips/torso", 0F, 0F, 0F),
            new Key(3, 12F, "hips/torso", -18F, 0F, 0F),
            new Key(3, 14F, "hips/torso", 26F, 0F, 0F),
            new Key(3, 19F, "hips/torso", 26F, 0F, 0F),
            new Key(3, 22F, "hips/torso", 0F, 0F, 0F),
            new Key(3, 0F, "hips/torso/head", 0F, 0F, 0F),
            new Key(3, 12F, "hips/torso/head", -12F, 0F, 0F),
            new Key(3, 14F, "hips/torso/head", 10F, 0F, 0F),
            new Key(3, 19F, "hips/torso/head", 10F, 0F, 0F),
            new Key(3, 22F, "hips/torso/head", 0F, 0F, 0F),
            new Key(3, 0F, "leg_l", 0F, 0F, 0F),
            new Key(3, 12F, "leg_l", -35F, 0F, 0F),
            new Key(3, 14F, "leg_l", 0F, 0F, 0F),
            new Key(3, 22F, "leg_l", 0F, 0F, 0F),
            new Key(3, 0F, "hips/tabard", 0F, 0F, 0F),
            new Key(3, 12F, "hips/tabard", -20F, 0F, 0F),
            new Key(3, 14F, "hips/tabard", 15F, 0F, 0F),
            new Key(3, 22F, "hips/tabard", 0F, 0F, 0F)};

    public static final float TOMB_WARDEN_SCALE = 1.0F;

    /** Irradiated Ghoul: verstrahlter Kraftwerksarbeiter im zerfetzten Schutzanzug mit gruen leuchtenden Geschwueren und Augen, halb abgerissener Gasmaske und Atemluftflasche. Textur {@code entity/dungeon/irradiated_ghoul.png}. */
    public static LayerDefinition irradiatedGhoul() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_hips = root.addOrReplaceChild("hips", CubeListBuilder.create()
                .texOffs(78, 15).addBox(-3.5F, -2F, -2F, 7, 3, 4, NONE)
                .texOffs(12, 24).addBox(-4F, -1.5F, -2.5F, 8, 1, 5, NONE)
                .texOffs(10, 36).addBox(2.5F, -1F, -2.8F, 2, 2, 2, NONE)
                .texOffs(118, 31).addBox(-4.5F, -1F, -1F, 1, 3, 2, NONE),
                PartPose.offsetAndRotation(0F, 10.5F, 0.5F, 0F, 0F, 0F));
        PartDefinition p_torso = p_hips.addOrReplaceChild("torso", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4F, -10F, -2.5F, 8, 10, 5, NONE)
                .texOffs(94, 31).addBox(-3F, -8F, -2.8F, 3, 4, 1, NONE)
                .texOffs(10, 40).addBox(-3.2F, -7.5F, -3F, 3, 1, 1, NONE)
                .texOffs(18, 40).addBox(-3.2F, -5.8F, -3F, 3, 1, 1, NONE)
                .texOffs(42, 40).addBox(-2F, -6.8F, -3.2F, 1, 1, 1, NONE)
                .texOffs(46, 40).addBox(-1.5F, -5.5F, -3.1F, 1, 1, 1, NONE)
                .texOffs(106, 36).addBox(1.5F, -9F, -2.8F, 2, 2, 1, NONE)
                .texOffs(48, 15).addBox(-4.5F, -1.5F, -3F, 9, 1, 6, NONE)
                .texOffs(74, 0).addBox(-2.5F, -9F, 2.5F, 5, 7, 2, NONE)
                .texOffs(26, 40).addBox(-1.5F, -10F, 3F, 3, 1, 1, NONE)
                .texOffs(26, 36).addBox(2.8F, -6F, 1F, 1, 2, 2, NONE)
                .texOffs(50, 40).addBox(3.3F, -5.5F, 1.5F, 1, 1, 1, NONE)
                .texOffs(32, 36).addBox(-4.4F, -8F, -1F, 1, 2, 2, NONE)
                .texOffs(90, 36).addBox(-2F, -3F, 2.6F, 3, 2, 1, NONE)
                .texOffs(106, 24).addBox(-3F, -10.5F, -2F, 6, 1, 4, NONE)
                .texOffs(0, 36).addBox(-4.2F, -4F, -2.6F, 4, 3, 1, NONE)
                .texOffs(102, 31).addBox(1F, -4.5F, -2.7F, 3, 4, 1, NONE),
                PartPose.offsetAndRotation(0F, -1F, 0F, 0.559F, 0F, 0F));
        PartDefinition p_head = p_torso.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(26, 0).addBox(-3F, -6F, -3F, 6, 6, 6, NONE)
                .texOffs(12, 15).addBox(-3.5F, -6.5F, -2F, 7, 3, 5, NONE)
                .texOffs(70, 24).addBox(-2.5F, -5F, -3.3F, 5, 5, 1, NONE)
                .texOffs(54, 40).addBox(-2F, -3.8F, -3.6F, 1, 1, 1, NONE)
                .texOffs(58, 40).addBox(1F, -3.8F, -3.6F, 1, 1, 1, NONE)
                .texOffs(34, 40).addBox(-1.5F, -1.5F, -3.6F, 3, 1, 1, NONE)
                .texOffs(0, 40).addBox(-2F, -0.8F, -3.4F, 4, 1, 1, NONE)
                .texOffs(62, 40).addBox(-3.3F, -4F, -1F, 1, 1, 1, NONE)
                .texOffs(98, 36).addBox(-0.5F, -6.6F, 0F, 2, 1, 2, NONE)
                .texOffs(80, 36).addBox(-2F, -2F, 3F, 4, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -10F, -1.5F, -0.628F, 0F, 0F));
        PartDefinition p_mask = p_head.addOrReplaceChild("mask", CubeListBuilder.create()
                .texOffs(84, 31).addBox(-4F, -1.5F, -1F, 4, 4, 1, NONE)
                .texOffs(66, 40).addBox(-3.5F, -1F, -1.4F, 1, 1, 1, NONE)
                .texOffs(70, 40).addBox(-1.5F, -1F, -1.4F, 1, 1, 1, NONE)
                .texOffs(18, 36).addBox(-3F, 2F, -2.5F, 2, 2, 2, NONE)
                .texOffs(74, 40).addBox(-2.6F, 3.5F, -2.1F, 1, 1, 1, NONE)
                .texOffs(82, 24).addBox(0F, -1F, -1F, 1, 1, 5, NONE),
                PartPose.offsetAndRotation(3.1F, -1.2F, -3F, 1.082F, 0.593F, 0.384F));
        PartDefinition p_arm_r = p_torso.addOrReplaceChild("arm_r", CubeListBuilder.create()
                .texOffs(88, 0).addBox(-1.5F, -1F, -1.5F, 3, 6, 3, NONE)
                .texOffs(100, 15).addBox(-2F, -1.5F, -2F, 3, 3, 4, NONE)
                .texOffs(38, 36).addBox(-1.8F, 3F, -1.8F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(-4.6F, -9F, 0F, -0.314F, 0F, 0.14F));
        PartDefinition p_fore_r = p_arm_r.addOrReplaceChild("fore_r", CubeListBuilder.create()
                .texOffs(36, 15).addBox(-1F, 0F, -1F, 2, 6, 2, NONE)
                .texOffs(48, 31).addBox(-1.5F, 0F, -1.5F, 3, 2, 3, NONE)
                .texOffs(124, 36).addBox(-1.3F, 2.5F, -1.3F, 1, 2, 1, NONE)
                .texOffs(78, 40).addBox(0.3F, 4F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 5.5F, 0F, -0.489F, 0F, 0F));
        PartDefinition p_hand_r = p_fore_r.addOrReplaceChild("hand_r", CubeListBuilder.create()
                .texOffs(60, 31).addBox(-1.5F, 0F, -1.5F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(0F, 6F, 0F, -0.175F, 0F, 0F));
        PartDefinition p_finger_r0 = p_hand_r.addOrReplaceChild("finger_r0", CubeListBuilder.create()
                .texOffs(56, 36).addBox(-0.5F, 0F, -0.5F, 1, 3, 1, NONE)
                .texOffs(82, 40).addBox(-0.5F, 2.5F, -0.9F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-1F, 1.5F, -1F, -0.349F, 0F, 0F));
        PartDefinition p_finger_r1 = p_hand_r.addOrReplaceChild("finger_r1", CubeListBuilder.create()
                .texOffs(60, 36).addBox(-0.5F, 0F, -0.5F, 1, 3, 1, NONE)
                .texOffs(86, 40).addBox(-0.5F, 2.5F, -0.9F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 1.5F, -1F, -0.436F, 0F, 0F));
        PartDefinition p_finger_r2 = p_hand_r.addOrReplaceChild("finger_r2", CubeListBuilder.create()
                .texOffs(64, 36).addBox(-0.5F, 0F, -0.5F, 1, 3, 1, NONE)
                .texOffs(90, 40).addBox(-0.5F, 2.5F, -0.9F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(1F, 1.5F, -1F, -0.524F, 0F, 0F));
        PartDefinition p_rag_r = p_fore_r.addOrReplaceChild("rag_r", CubeListBuilder.create()
                .texOffs(124, 31).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(-1.2F, 1.5F, 0.5F, 0.175F, 0F, 0.262F));
        PartDefinition p_arm_l = p_torso.addOrReplaceChild("arm_l", CubeListBuilder.create()
                .texOffs(100, 0).addBox(-1.5F, -1F, -1.5F, 3, 6, 3, NONE)
                .texOffs(114, 15).addBox(-1F, -1.5F, -2F, 3, 3, 4, NONE)
                .texOffs(94, 40).addBox(1.2F, 1F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(4.6F, -9F, 0F, -0.14F, 0F, -0.105F));
        PartDefinition p_fore_l = p_arm_l.addOrReplaceChild("fore_l", CubeListBuilder.create()
                .texOffs(0, 24).addBox(-1.5F, 0F, -1.5F, 3, 4, 3, NONE)
                .texOffs(94, 24).addBox(-1.5F, 3.5F, -1.5F, 3, 3, 3, NONE)
                .texOffs(0, 31).addBox(-1.8F, 3F, -1.8F, 4, 1, 4, NONE),
                PartPose.offsetAndRotation(0F, 5.5F, 0F, -0.314F, 0F, 0F));
        PartDefinition p_hand_l = p_fore_l.addOrReplaceChild("hand_l", CubeListBuilder.create()
                .texOffs(72, 31).addBox(-1.5F, 0F, -1.5F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(0F, 6F, 0F, -0.175F, 0F, 0F));
        PartDefinition p_finger_l0 = p_hand_l.addOrReplaceChild("finger_l0", CubeListBuilder.create()
                .texOffs(68, 36).addBox(-0.5F, 0F, -0.5F, 1, 3, 1, NONE)
                .texOffs(98, 40).addBox(-0.5F, 2.5F, -0.9F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-1F, 1.5F, -1F, -0.349F, 0F, 0F));
        PartDefinition p_finger_l1 = p_hand_l.addOrReplaceChild("finger_l1", CubeListBuilder.create()
                .texOffs(72, 36).addBox(-0.5F, 0F, -0.5F, 1, 3, 1, NONE)
                .texOffs(102, 40).addBox(-0.5F, 2.5F, -0.9F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 1.5F, -1F, -0.436F, 0F, 0F));
        PartDefinition p_finger_l2 = p_hand_l.addOrReplaceChild("finger_l2", CubeListBuilder.create()
                .texOffs(76, 36).addBox(-0.5F, 0F, -0.5F, 1, 3, 1, NONE)
                .texOffs(106, 40).addBox(-0.5F, 2.5F, -0.9F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(1F, 1.5F, -1F, -0.524F, 0F, 0F));
        PartDefinition p_hose = p_torso.addOrReplaceChild("hose", CubeListBuilder.create()
                .texOffs(44, 15).addBox(-0.5F, 0F, -0.5F, 1, 7, 1, NONE),
                PartPose.offsetAndRotation(2F, -9F, 3.5F, -1.047F, 0F, 0.349F));
        PartDefinition p_flap = p_torso.addOrReplaceChild("flap", CubeListBuilder.create()
                .texOffs(110, 31).addBox(-2F, 0F, -0.5F, 3, 4, 1, NONE),
                PartPose.offsetAndRotation(-1F, 0F, 2.6F, 0.209F, 0F, 0F));
        PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create()
                .texOffs(50, 0).addBox(-1.5F, -0.5F, -1.5F, 3, 7, 3, NONE)
                .texOffs(112, 36).addBox(-1.4F, 4.5F, -1.8F, 2, 2, 1, NONE)
                .texOffs(110, 40).addBox(-1F, 5F, -2F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-2F, 10.5F, 0.5F, -0.244F, 0F, 0.052F));
        PartDefinition p_shin_r = p_leg_r.addOrReplaceChild("shin_r", CubeListBuilder.create()
                .texOffs(112, 0).addBox(-1.5F, 0F, -1.5F, 3, 6, 3, NONE)
                .texOffs(38, 24).addBox(-2F, 5F, -2.5F, 4, 2, 4, NONE)
                .texOffs(16, 31).addBox(-2F, 4.5F, -2F, 4, 1, 4, NONE)
                .texOffs(44, 36).addBox(-2.2F, 1F, -1F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.279F, 0F, 0F));
        PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create()
                .texOffs(62, 0).addBox(-1.5F, -0.5F, -1.5F, 3, 7, 3, NONE)
                .texOffs(118, 36).addBox(-1.4F, 4.5F, -1.8F, 2, 2, 1, NONE)
                .texOffs(114, 40).addBox(-1F, 5F, -2F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(2F, 10.5F, 0.5F, -0.244F, 0F, -0.052F));
        PartDefinition p_shin_l = p_leg_l.addOrReplaceChild("shin_l", CubeListBuilder.create()
                .texOffs(0, 15).addBox(-1.5F, 0F, -1.5F, 3, 6, 3, NONE)
                .texOffs(54, 24).addBox(-2F, 5F, -2.5F, 4, 2, 4, NONE)
                .texOffs(32, 31).addBox(-2F, 4.5F, -2F, 4, 1, 4, NONE)
                .texOffs(50, 36).addBox(1.2F, 1F, -1F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 6.5F, 0F, 0.279F, 0F, 0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final Role[] IRRADIATED_GHOUL_ROLES = {new Role("hips/torso", "body", 0.00F), new Role("hips/torso/head", "head", 0.00F), new Role("hips/torso/arm_r", "arm_r", 0.00F), new Role("hips/torso/arm_r/fore_r/rag_r", "tentacle", 0.70F), new Role("hips/torso/arm_l", "arm_l", 0.00F), new Role("hips/torso/flap", "tentacle", 1.50F), new Role("leg_r", "leg_r", 0.00F), new Role("leg_l", "leg_l", 0.00F)};

    public static final Key[] IRRADIATED_GHOUL_POSES = {
            new Key(1, 0F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(1, 3F, "hips/torso/arm_r", -120F, 0F, -18F),
            new Key(1, 6F, "hips/torso/arm_r", -45F, 0F, 18F),
            new Key(1, 10F, "hips/torso/arm_r", 0F, 0F, 0F),
            new Key(1, 0F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(1, 3F, "hips/torso/arm_l", -110F, 0F, 18F),
            new Key(1, 6F, "hips/torso/arm_l", -40F, 0F, -18F),
            new Key(1, 10F, "hips/torso/arm_l", 0F, 0F, 0F),
            new Key(1, 0F, "hips/torso/arm_r/fore_r", 0F, 0F, 0F),
            new Key(1, 3F, "hips/torso/arm_r/fore_r", -20F, 0F, 0F),
            new Key(1, 6F, "hips/torso/arm_r/fore_r", 15F, 0F, 0F),
            new Key(1, 10F, "hips/torso/arm_r/fore_r", 0F, 0F, 0F),
            new Key(1, 0F, "hips/torso", 0F, 0F, 0F),
            new Key(1, 3F, "hips/torso", -12F, 0F, 0F),
            new Key(1, 6F, "hips/torso", 14F, 0F, 0F),
            new Key(1, 10F, "hips/torso", 0F, 0F, 0F),
            new Key(1, 0F, "hips/torso/head", 0F, 0F, 0F),
            new Key(1, 3F, "hips/torso/head", -10F, 0F, 0F),
            new Key(1, 6F, "hips/torso/head", 12F, 0F, 0F),
            new Key(1, 10F, "hips/torso/head", 0F, 0F, 0F)};

    public static final float IRRADIATED_GHOUL_SCALE = 1.0F;

    /** Toxic Sludge: Klumpen aus gruenem Giftschleim mit leuchtendem Kern, eingeschlossenen Giftfaessern und Schrott, Blasen und kriechenden Pseudopodien. Textur {@code entity/dungeon/toxic_sludge.png}. */
    public static LayerDefinition toxicSludge() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_blob = root.addOrReplaceChild("blob", CubeListBuilder.create()
                .texOffs(50, 20).addBox(-11F, -3F, -8F, 22, 2, 16, NONE)
                .texOffs(76, 0).addBox(-10F, -4F, -8F, 20, 4, 16, NONE)
                .texOffs(0, 0).addBox(-10F, -3F, -9F, 20, 2, 18, NONE)
                .texOffs(46, 39).addBox(-10F, -7F, -5.5F, 20, 2, 11, NONE)
                .texOffs(0, 20).addBox(-7F, -10F, -5.5F, 14, 8, 11, NONE)
                .texOffs(148, 0).addBox(-7F, -7F, -8.5F, 14, 2, 17, NONE)
                .texOffs(212, 39).addBox(-7F, -12F, -4F, 14, 2, 8, NONE)
                .texOffs(170, 20).addBox(-5F, -14F, -4F, 10, 6, 8, NONE)
                .texOffs(126, 20).addBox(-5F, -12F, -6F, 10, 2, 12, NONE)
                .texOffs(42, 53).addBox(-4F, -15F, -3.5F, 8, 2, 7, NONE)
                .texOffs(0, 53).addBox(2.5F, -12F, -7.5F, 6, 5, 5, NONE)
                .texOffs(108, 39).addBox(-10F, -9F, 0F, 4, 6, 7, NONE)
                .texOffs(72, 53).addBox(5F, -11.5F, 2F, 5, 4, 5, NONE)
                .texOffs(206, 20).addBox(-11.5F, -2F, -6F, 1, 2, 12, NONE)
                .texOffs(0, 39).addBox(10.5F, -2F, -6F, 1, 2, 12, NONE)
                .texOffs(214, 63).addBox(-6.5F, -10F, -5F, 2, 2, 2, NONE)
                .texOffs(14, 70).addBox(4F, -13.5F, -2F, 1, 1, 1, NONE)
                .texOffs(222, 63).addBox(-2F, -15F, 1F, 2, 2, 2, NONE)
                .texOffs(18, 70).addBox(8.5F, -7.5F, 3F, 1, 1, 1, NONE)
                .texOffs(22, 70).addBox(-9F, -6.5F, -4F, 1, 1, 1, NONE)
                .texOffs(26, 70).addBox(1F, -11.5F, -6.5F, 1, 1, 1, NONE)
                .texOffs(30, 70).addBox(-4F, -13F, -3F, 1, 1, 1, NONE)
                .texOffs(34, 70).addBox(10F, -4F, -6F, 1, 1, 1, NONE)
                .texOffs(38, 70).addBox(5F, -9.5F, -7.6F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
        PartDefinition p_core = p_blob.addOrReplaceChild("core", CubeListBuilder.create()
                .texOffs(22, 53).addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, NONE)
                .texOffs(42, 63).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, new CubeDeformation(0.8F)),
                PartPose.offsetAndRotation(0F, -7F, -6.8F, 0.628F, 0.785F, 0F));
        PartDefinition p_reach = p_blob.addOrReplaceChild("reach", CubeListBuilder.create()
                .texOffs(146, 53).addBox(-2F, -5F, -2F, 4, 5, 4, NONE)
                .texOffs(8, 70).addBox(-1F, -5.5F, -2.5F, 2, 1, 1, NONE),
                PartPose.offsetAndRotation(2F, -13F, -1F, -0.209F, 0F, 0.314F));
        PartDefinition p_reach_tip = p_reach.addOrReplaceChild("reach_tip", CubeListBuilder.create()
                .texOffs(54, 63).addBox(-1.5F, -3F, -1.5F, 3, 3, 3, NONE)
                .texOffs(42, 70).addBox(-0.5F, -4F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -4.5F, 0F, -0.489F, 0F, -0.175F));
        PartDefinition p_drum_a = p_blob.addOrReplaceChild("drum_a", CubeListBuilder.create()
                .texOffs(150, 39).addBox(-3F, -4F, -2F, 6, 8, 4, NONE)
                .texOffs(26, 39).addBox(-2F, -4F, -3F, 4, 8, 6, NONE)
                .texOffs(72, 63).addBox(-3F, -2.5F, -2F, 6, 1, 4, new CubeDeformation(0.25F))
                .texOffs(162, 53).addBox(-2F, -2.5F, -3F, 4, 1, 6, new CubeDeformation(0.25F))
                .texOffs(92, 63).addBox(-3F, 1.5F, -2F, 6, 1, 4, new CubeDeformation(0.25F))
                .texOffs(152, 63).addBox(-2F, -4.5F, -2F, 4, 1, 4, NONE)
                .texOffs(46, 70).addBox(-1F, -5F, -1F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(6.5F, -6.5F, -4.5F, 0.349F, -0.349F, 1.257F));
        PartDefinition p_drum_b = p_blob.addOrReplaceChild("drum_b", CubeListBuilder.create()
                .texOffs(192, 39).addBox(-3F, -3.5F, -2F, 6, 7, 4, NONE)
                .texOffs(130, 39).addBox(-2F, -3.5F, -3F, 4, 7, 6, NONE)
                .texOffs(112, 63).addBox(-3F, -2F, -2F, 6, 1, 4, new CubeDeformation(0.25F))
                .texOffs(182, 53).addBox(-2F, -2F, -3F, 4, 1, 6, new CubeDeformation(0.25F))
                .texOffs(132, 63).addBox(-3F, 1F, -2F, 6, 1, 4, new CubeDeformation(0.25F))
                .texOffs(168, 63).addBox(-2F, -4F, -2F, 4, 1, 4, NONE)
                .texOffs(50, 70).addBox(-1F, -4.5F, -1F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-4.5F, -10.5F, 3.5F, 0F, 0.611F, 1.466F));
        PartDefinition p_pod_0 = p_blob.addOrReplaceChild("pod_0", CubeListBuilder.create()
                .texOffs(92, 53).addBox(-2F, -2F, -5F, 4, 4, 5, NONE)
                .texOffs(54, 70).addBox(-1.5F, -2.5F, -4F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-7F, -2.5F, -8F, 0F, 0.436F, 0F));
        PartDefinition p_pod_0_tip = p_pod_0.addOrReplaceChild("pod_0_tip", CubeListBuilder.create()
                .texOffs(202, 53).addBox(-1.5F, -1.5F, -4F, 3, 3, 4, NONE)
                .texOffs(58, 70).addBox(-0.5F, -0.5F, -5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0.5F, -4.5F, -0.14F, 0F, 0F));
        PartDefinition p_pod_1 = p_blob.addOrReplaceChild("pod_1", CubeListBuilder.create()
                .texOffs(110, 53).addBox(-2F, -2F, -5F, 4, 4, 5, NONE)
                .texOffs(62, 70).addBox(-1.5F, -2.5F, -4F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(7F, -2.5F, -8F, 0F, -0.524F, 0F));
        PartDefinition p_pod_1_tip = p_pod_1.addOrReplaceChild("pod_1_tip", CubeListBuilder.create()
                .texOffs(216, 53).addBox(-1.5F, -1.5F, -4F, 3, 3, 4, NONE)
                .texOffs(66, 70).addBox(-0.5F, -0.5F, -5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0.5F, -4.5F, -0.14F, 0F, 0F));
        PartDefinition p_pod_2 = p_blob.addOrReplaceChild("pod_2", CubeListBuilder.create()
                .texOffs(230, 53).addBox(-1.5F, -1.5F, -4F, 3, 3, 4, NONE)
                .texOffs(70, 70).addBox(-1F, -2F, -3F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-10F, -2.5F, 3F, 0F, 1.658F, 0F));
        PartDefinition p_pod_2_tip = p_pod_2.addOrReplaceChild("pod_2_tip", CubeListBuilder.create()
                .texOffs(184, 63).addBox(-1F, -1F, -3F, 2, 2, 3, NONE)
                .texOffs(74, 70).addBox(-0.5F, -0.5F, -4F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0.5F, -3.5F, -0.14F, 0F, 0F));
        PartDefinition p_pod_3 = p_blob.addOrReplaceChild("pod_3", CubeListBuilder.create()
                .texOffs(0, 63).addBox(-1.5F, -1.5F, -4F, 3, 3, 4, NONE)
                .texOffs(78, 70).addBox(-1F, -2F, -3F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(10F, -2.5F, 4F, 0F, -1.745F, 0F));
        PartDefinition p_pod_3_tip = p_pod_3.addOrReplaceChild("pod_3_tip", CubeListBuilder.create()
                .texOffs(194, 63).addBox(-1F, -1F, -3F, 2, 2, 3, NONE)
                .texOffs(82, 70).addBox(-0.5F, -0.5F, -4F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0.5F, -3.5F, -0.14F, 0F, 0F));
        PartDefinition p_pod_4 = p_blob.addOrReplaceChild("pod_4", CubeListBuilder.create()
                .texOffs(128, 53).addBox(-2F, -2F, -5F, 4, 4, 5, NONE)
                .texOffs(86, 70).addBox(-1.5F, -2.5F, -4F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(2F, -2.5F, 9F, 0F, 3.316F, 0F));
        PartDefinition p_pod_4_tip = p_pod_4.addOrReplaceChild("pod_4_tip", CubeListBuilder.create()
                .texOffs(14, 63).addBox(-1.5F, -1.5F, -4F, 3, 3, 4, NONE)
                .texOffs(90, 70).addBox(-0.5F, -0.5F, -5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0.5F, -4.5F, -0.14F, 0F, 0F));
        PartDefinition p_pipe_stub = p_blob.addOrReplaceChild("pipe_stub", CubeListBuilder.create()
                .texOffs(170, 39).addBox(-1F, -1F, -5F, 2, 2, 9, NONE)
                .texOffs(230, 63).addBox(-1.5F, -1.5F, -6F, 3, 3, 1, NONE),
                PartPose.offsetAndRotation(-8F, -8F, 2F, 0.524F, 0.349F, 0.698F));
        PartDefinition p_plate_shard = p_blob.addOrReplaceChild("plate_shard", CubeListBuilder.create()
                .texOffs(28, 63).addBox(-3F, -4F, -0.5F, 6, 5, 1, NONE)
                .texOffs(0, 70).addBox(-2F, -5F, -0.5F, 3, 1, 1, NONE),
                PartPose.offsetAndRotation(3F, -11F, 4.5F, -0.436F, 0.262F, 0.349F));
        PartDefinition p_gear = p_blob.addOrReplaceChild("gear", CubeListBuilder.create()
                .texOffs(204, 63).addBox(-2F, -2F, -0.5F, 4, 4, 1, NONE)
                .texOffs(238, 63).addBox(-2.5F, -1F, -0.5F, 5, 2, 1, NONE)
                .texOffs(66, 63).addBox(-1F, -2.5F, -0.5F, 2, 5, 1, NONE),
                PartPose.offsetAndRotation(-5.5F, -4.5F, -8.2F, 0F, 0F, 0.349F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    public static final Role[] TOXIC_SLUDGE_ROLES = {new Role("blob", "body", 0.00F), new Role("blob/reach", "tentacle", 2.20F), new Role("blob/reach/reach_tip", "tentacle", 3.10F), new Role("blob/pod_0", "tentacle", 0.00F), new Role("blob/pod_0/pod_0_tip", "tentacle", 0.60F), new Role("blob/pod_1", "tentacle", 1.10F), new Role("blob/pod_1/pod_1_tip", "tentacle", 1.70F), new Role("blob/pod_2", "tentacle", 2.20F), new Role("blob/pod_2/pod_2_tip", "tentacle", 2.80F), new Role("blob/pod_3", "tentacle", 3.30F), new Role("blob/pod_3/pod_3_tip", "tentacle", 3.90F), new Role("blob/pod_4", "tentacle", 4.40F), new Role("blob/pod_4/pod_4_tip", "tentacle", 5.00F)};

    public static final Key[] TOXIC_SLUDGE_POSES = {
            new Key(1, 0F, "blob", 0F, 0F, 0F),
            new Key(1, 4F, "blob", -10F, 0F, 0F),
            new Key(1, 7F, "blob", 12F, 0F, 0F),
            new Key(1, 10F, "blob", 0F, 0F, 0F),
            new Key(1, 0F, "blob/reach", 0F, 0F, 0F),
            new Key(1, 4F, "blob/reach", 25F, 0F, -15F),
            new Key(1, 7F, "blob/reach", -95F, 0F, -10F),
            new Key(1, 10F, "blob/reach", 0F, 0F, 0F),
            new Key(1, 0F, "blob/reach/reach_tip", 0F, 0F, 0F),
            new Key(1, 4F, "blob/reach/reach_tip", 20F, 0F, 0F),
            new Key(1, 7F, "blob/reach/reach_tip", -40F, 0F, 0F),
            new Key(1, 10F, "blob/reach/reach_tip", 0F, 0F, 0F),
            new Key(1, 0F, "blob/pod_0", 0F, 0F, 0F),
            new Key(1, 4F, "blob/pod_0", 45F, 0F, 0F),
            new Key(1, 7F, "blob/pod_0", -10F, 0F, 0F),
            new Key(1, 10F, "blob/pod_0", 0F, 0F, 0F),
            new Key(1, 0F, "blob/pod_1", 0F, 0F, 0F),
            new Key(1, 4F, "blob/pod_1", 45F, 0F, 0F),
            new Key(1, 7F, "blob/pod_1", -10F, 0F, 0F),
            new Key(1, 10F, "blob/pod_1", 0F, 0F, 0F),
            new Key(1, 0F, "blob/pod_0/pod_0_tip", 0F, 0F, 0F),
            new Key(1, 4F, "blob/pod_0/pod_0_tip", 30F, 0F, 0F),
            new Key(1, 7F, "blob/pod_0/pod_0_tip", -15F, 0F, 0F),
            new Key(1, 10F, "blob/pod_0/pod_0_tip", 0F, 0F, 0F),
            new Key(1, 0F, "blob/pod_1/pod_1_tip", 0F, 0F, 0F),
            new Key(1, 4F, "blob/pod_1/pod_1_tip", 30F, 0F, 0F),
            new Key(1, 7F, "blob/pod_1/pod_1_tip", -15F, 0F, 0F),
            new Key(1, 10F, "blob/pod_1/pod_1_tip", 0F, 0F, 0F)};

    public static final float TOXIC_SLUDGE_SCALE = 1.0F;

    /** Cinder Imp: schwebender Feuerkobold aus Lavakruste mit gluehenden Rissen, Hoernern, Glutaugen, Fledermausfluegeln, Feuerball in der rechten Klaue und Schwanz mit Glutspitze. Textur {@code entity/dungeon/cinder_imp.png}. */
    public static LayerDefinition cinderImp() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3F, -5F, -2F, 6, 5, 4, NONE)
                .texOffs(4, 9).addBox(-2.5F, 0F, -1.5F, 5, 3, 3, NONE)
                .texOffs(22, 40).addBox(-2F, -4F, -2.4F, 4, 2, 1, NONE)
                .texOffs(56, 44).addBox(-1F, -1.8F, -2.4F, 2, 2, 1, NONE)
                .texOffs(14, 50).addBox(-2F, 0.6F, -1.8F, 4, 1, 1, NONE)
                .texOffs(8, 16).addBox(-0.5F, -5F, 2F, 1, 5, 1, NONE)
                .texOffs(28, 32).addBox(-2.5F, -4.5F, 1.6F, 5, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 14F, 0F, 0.209F, 0F, 0F));
        PartDefinition p_head = p_body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(20, 0).addBox(-2.5F, -5F, -3F, 5, 4, 5, NONE)
                .texOffs(6, 40).addBox(-3F, -4.2F, -3.4F, 6, 1, 2, NONE)
                .texOffs(24, 50).addBox(-2F, -3F, -3.3F, 1, 1, 1, NONE)
                .texOffs(28, 50).addBox(1F, -3F, -3.3F, 1, 1, 1, NONE)
                .texOffs(0, 36).addBox(-1.5F, -2.5F, -4.6F, 3, 2, 2, NONE)
                .texOffs(32, 50).addBox(-1F, -2.2F, -4.9F, 1, 1, 1, NONE)
                .texOffs(36, 50).addBox(0F, -2.2F, -4.9F, 1, 1, 1, NONE)
                .texOffs(40, 50).addBox(-1.5F, -1.6F, -3.6F, 1, 1, 1, NONE)
                .texOffs(44, 50).addBox(0.5F, -1.6F, -3.6F, 1, 1, 1, NONE)
                .texOffs(0, 22).addBox(-0.5F, -5.8F, -2F, 1, 1, 4, NONE)
                .texOffs(0, 32).addBox(-2F, -1.2F, -1.5F, 4, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, -4.5F, -0.5F, 0F, 0F, 0F));
        PartDefinition p_jaw = p_head.addOrReplaceChild("jaw", CubeListBuilder.create()
                .texOffs(14, 32).addBox(-2F, 0F, -2.6F, 4, 1, 3, NONE)
                .texOffs(32, 40).addBox(-1.5F, -0.6F, -2.4F, 3, 1, 2, NONE)
                .texOffs(48, 50).addBox(-2F, -0.9F, -2.8F, 1, 1, 1, NONE)
                .texOffs(52, 50).addBox(1F, -0.9F, -2.8F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -1.2F, -2F, 0F, 0F, 0F));
        PartDefinition p_horn_l = p_head.addOrReplaceChild("horn_l", CubeListBuilder.create()
                .texOffs(20, 22).addBox(-1F, -2.5F, -1F, 2, 3, 2, NONE)
                .texOffs(0, 44).addBox(-1.2F, -0.6F, -1.2F, 2, 1, 2, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(1.8F, -4.6F, -1F, -0.611F, 0F, 0.663F));
        PartDefinition p_horntip_l = p_horn_l.addOrReplaceChild("horntip_l", CubeListBuilder.create()
                .texOffs(6, 47).addBox(-0.5F, -2F, -0.5F, 1, 2, 1, NONE)
                .texOffs(56, 50).addBox(-0.5F, -3F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -2F, 0F, -0.698F, 0F, -0.262F));
        PartDefinition p_horn_r = p_head.addOrReplaceChild("horn_r", CubeListBuilder.create()
                .texOffs(28, 22).addBox(-1F, -2.5F, -1F, 2, 3, 2, NONE)
                .texOffs(8, 44).addBox(-0.8F, -0.6F, -1.2F, 2, 1, 2, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(-1.8F, -4.6F, -1F, -0.611F, 0F, -0.663F));
        PartDefinition p_horntip_r = p_horn_r.addOrReplaceChild("horntip_r", CubeListBuilder.create()
                .texOffs(10, 47).addBox(-0.5F, -2F, -0.5F, 1, 2, 1, NONE)
                .texOffs(60, 50).addBox(-0.5F, -3F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -2F, 0F, -0.698F, 0F, 0.262F));
        PartDefinition p_ear_l = p_head.addOrReplaceChild("ear_l", CubeListBuilder.create()
                .texOffs(16, 44).addBox(0F, -1F, -0.5F, 3, 2, 1, NONE)
                .texOffs(0, 52).addBox(2F, -2F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(2.5F, -3F, -0.5F, 0F, -0.349F, -0.209F));
        PartDefinition p_ear_r = p_head.addOrReplaceChild("ear_r", CubeListBuilder.create()
                .texOffs(24, 44).addBox(-3F, -1F, -0.5F, 3, 2, 1, NONE)
                .texOffs(4, 52).addBox(-3F, -2F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-2.5F, -3F, -0.5F, 0F, 0.349F, 0.209F));
        PartDefinition p_arm_r = p_body.addOrReplaceChild("arm_r", CubeListBuilder.create()
                .texOffs(44, 9).addBox(-1F, -0.5F, -1F, 2, 4, 2, NONE)
                .texOffs(40, 16).addBox(-1.6F, -1.2F, -1.4F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(-3.6F, -3.2F, -0.3F, 0.175F, 0F, 0.209F));
        PartDefinition p_fore_r = p_arm_r.addOrReplaceChild("fore_r", CubeListBuilder.create()
                .texOffs(36, 22).addBox(-1F, 0F, -1F, 2, 3, 2, NONE)
                .texOffs(14, 47).addBox(0.2F, 0.5F, -0.6F, 1, 2, 1, new CubeDeformation(0.15F))
                .texOffs(18, 47).addBox(-0.5F, 2.6F, -1.6F, 1, 2, 1, NONE)
                .texOffs(22, 47).addBox(-1.5F, 2.6F, -1.6F, 1, 2, 1, NONE)
                .texOffs(8, 52).addBox(-0.5F, 2.6F, 0.4F, 1, 1, 1, NONE)
                .texOffs(20, 9).addBox(-1.5F, 3.6F, -2.5F, 3, 3, 3, NONE)
                .texOffs(52, 9).addBox(-1F, 3.1F, -2F, 2, 4, 2, new CubeDeformation(0.15F)),
                PartPose.offsetAndRotation(0F, 3.2F, 0F, -0.611F, 0F, 0F));
        PartDefinition p_arm_l = p_body.addOrReplaceChild("arm_l", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-1F, -0.5F, -1F, 2, 4, 2, NONE)
                .texOffs(52, 16).addBox(-1.4F, -1.2F, -1.4F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(3.6F, -3.2F, -0.3F, 0.175F, 0F, -0.209F));
        PartDefinition p_fore_l = p_arm_l.addOrReplaceChild("fore_l", CubeListBuilder.create()
                .texOffs(44, 22).addBox(-1F, 0F, -1F, 2, 3, 2, NONE)
                .texOffs(26, 47).addBox(-1.2F, 0.5F, -0.6F, 1, 2, 1, new CubeDeformation(0.15F))
                .texOffs(30, 47).addBox(-0.5F, 2.6F, -1.6F, 1, 2, 1, NONE)
                .texOffs(34, 47).addBox(0.5F, 2.6F, -1.6F, 1, 2, 1, NONE)
                .texOffs(12, 52).addBox(-0.5F, 2.6F, 0.4F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 3.2F, 0F, -0.611F, 0F, 0F));
        PartDefinition p_leg_r = p_body.addOrReplaceChild("leg_r", CubeListBuilder.create()
                .texOffs(40, 0).addBox(-1F, -0.5F, -1.2F, 2, 4, 3, NONE)
                .texOffs(56, 36).addBox(0.3F, 0F, -1F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(-1.6F, 3.6F, 0F, -0.873F, 0F, 0.14F));
        PartDefinition p_shin_r = p_leg_r.addOrReplaceChild("shin_r", CubeListBuilder.create()
                .texOffs(52, 22).addBox(-1.2F, 0F, -0.8F, 2, 3, 2, NONE),
                PartPose.offsetAndRotation(0F, 2.6F, 0.2F, 1.484F, 0F, 0F));
        PartDefinition p_foot_r = p_shin_r.addOrReplaceChild("foot_r", CubeListBuilder.create()
                .texOffs(10, 36).addBox(-1F, 0F, -2F, 2, 1, 3, NONE)
                .texOffs(16, 52).addBox(0F, 0.3F, -2.8F, 1, 1, 1, NONE)
                .texOffs(20, 52).addBox(-1F, 0.3F, -2.8F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 2.6F, 0F, -0.436F, 0F, 0F));
        PartDefinition p_leg_l = p_body.addOrReplaceChild("leg_l", CubeListBuilder.create()
                .texOffs(50, 0).addBox(-1F, -0.5F, -1.2F, 2, 4, 3, NONE)
                .texOffs(0, 40).addBox(-1.3F, 0F, -1F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(1.6F, 3.6F, 0F, -0.873F, 0F, -0.14F));
        PartDefinition p_shin_l = p_leg_l.addOrReplaceChild("shin_l", CubeListBuilder.create()
                .texOffs(0, 27).addBox(-0.8F, 0F, -0.8F, 2, 3, 2, NONE),
                PartPose.offsetAndRotation(0F, 2.6F, 0.2F, 1.484F, 0F, 0F));
        PartDefinition p_foot_l = p_shin_l.addOrReplaceChild("foot_l", CubeListBuilder.create()
                .texOffs(20, 36).addBox(-1F, 0F, -2F, 2, 1, 3, NONE)
                .texOffs(24, 52).addBox(-1F, 0.3F, -2.8F, 1, 1, 1, NONE)
                .texOffs(28, 52).addBox(0F, 0.3F, -2.8F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 2.6F, 0F, -0.436F, 0F, 0F));
        PartDefinition p_wing_l = p_body.addOrReplaceChild("wing_l", CubeListBuilder.create()
                .texOffs(22, 27).addBox(0F, -0.8F, -0.8F, 6, 2, 2, NONE)
                .texOffs(12, 16).addBox(0F, 1F, 0F, 6, 4, 1, NONE)
                .texOffs(42, 40).addBox(1F, 5F, 0F, 4, 2, 1, NONE)
                .texOffs(60, 0).addBox(3.5F, 1F, -0.4F, 1, 6, 1, new CubeDeformation(-0.1F))
                .texOffs(40, 36).addBox(-0.5F, -1F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(1.5F, -2.6F, 2.2F, 0F, -0.663F, -0.524F));
        PartDefinition p_wtip_l = p_wing_l.addOrReplaceChild("wtip_l", CubeListBuilder.create()
                .texOffs(38, 47).addBox(0F, -0.5F, -0.5F, 6, 1, 1, NONE)
                .texOffs(32, 52).addBox(5.5F, -1.5F, -0.5F, 1, 1, 1, NONE)
                .texOffs(40, 32).addBox(0F, 0.5F, 0F, 5, 3, 1, NONE)
                .texOffs(32, 44).addBox(0F, 3.5F, 0F, 3, 2, 1, NONE)
                .texOffs(36, 52).addBox(0F, 5.5F, 0F, 1, 1, 1, NONE)
                .texOffs(14, 27).addBox(2.5F, 0.5F, -0.4F, 1, 4, 1, new CubeDeformation(-0.1F)),
                PartPose.offsetAndRotation(5.5F, 0F, 0F, 0F, 0F, -0.489F));
        PartDefinition p_wing_r = p_body.addOrReplaceChild("wing_r", CubeListBuilder.create()
                .texOffs(38, 27).addBox(-6F, -0.8F, -0.8F, 6, 2, 2, NONE)
                .texOffs(26, 16).addBox(-6F, 1F, 0F, 6, 4, 1, NONE)
                .texOffs(52, 40).addBox(-5F, 5F, 0F, 4, 2, 1, NONE)
                .texOffs(0, 9).addBox(-4.5F, 1F, -0.4F, 1, 6, 1, new CubeDeformation(-0.1F))
                .texOffs(48, 36).addBox(-1.5F, -1F, -1F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(-1.5F, -2.6F, 2.2F, 0F, 0.663F, 0.524F));
        PartDefinition p_wtip_r = p_wing_r.addOrReplaceChild("wtip_r", CubeListBuilder.create()
                .texOffs(0, 50).addBox(-6F, -0.5F, -0.5F, 6, 1, 1, NONE)
                .texOffs(40, 52).addBox(-6.5F, -1.5F, -0.5F, 1, 1, 1, NONE)
                .texOffs(52, 32).addBox(-5F, 0.5F, 0F, 5, 3, 1, NONE)
                .texOffs(40, 44).addBox(-3F, 3.5F, 0F, 3, 2, 1, NONE)
                .texOffs(44, 52).addBox(-1F, 5.5F, 0F, 1, 1, 1, NONE)
                .texOffs(18, 27).addBox(-3.5F, 0.5F, -0.4F, 1, 4, 1, new CubeDeformation(-0.1F)),
                PartPose.offsetAndRotation(-5.5F, 0F, 0F, 0F, 0F, 0.489F));
        PartDefinition p_tail_1 = p_body.addOrReplaceChild("tail_1", CubeListBuilder.create()
                .texOffs(32, 9).addBox(-1F, -1F, 0F, 2, 2, 4, NONE)
                .texOffs(0, 47).addBox(-0.5F, -1.6F, 0.5F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, 2.5F, 1.5F, -0.785F, 0F, 0F));
        PartDefinition p_tail_2 = p_tail_1.addOrReplaceChild("tail_2", CubeListBuilder.create()
                .texOffs(10, 22).addBox(-0.5F, -0.5F, 0F, 1, 1, 4, NONE),
                PartPose.offsetAndRotation(0F, 0F, 3.5F, 0.524F, 0F, 0F));
        PartDefinition p_tail_tip = p_tail_2.addOrReplaceChild("tail_tip", CubeListBuilder.create()
                .texOffs(30, 36).addBox(-1.5F, -1F, 1.5F, 3, 2, 2, NONE)
                .texOffs(48, 44).addBox(-1F, -0.5F, 3.5F, 2, 1, 2, NONE)
                .texOffs(8, 27).addBox(-0.5F, -1.5F, 2F, 1, 3, 2, NONE),
                PartPose.offsetAndRotation(0F, 0F, 3F, 0.698F, 0F, 0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static final Role[] CINDER_IMP_ROLES = {new Role("body", "bob", 0.00F), new Role("body/head", "head", 0.00F), new Role("body/head/jaw", "jaw", 0.00F), new Role("body/arm_r", "arm_r", 0.00F), new Role("body/arm_l", "arm_l", 0.00F), new Role("body/leg_r", "leg_r", 0.00F), new Role("body/leg_l", "leg_l", 0.00F), new Role("body/wing_l", "wing_l", 0.00F), new Role("body/wing_r", "wing_r", 0.00F), new Role("body/tail_1", "chain", 1.00F), new Role("body/tail_1/tail_2", "chain", 2.00F), new Role("body/tail_1/tail_2/tail_tip", "chain", 3.00F)};

    public static final Key[] CINDER_IMP_POSES = {
            new Key(2, 0F, "body/arm_r", 0F, 0F, 0F),
            new Key(2, 6F, "body/arm_r", -165F, 15F, 10F),
            new Key(2, 9F, "body/arm_r", -45F, 0F, 0F),
            new Key(2, 14F, "body/arm_r", 0F, 0F, 0F),
            new Key(2, 0F, "body/arm_r/fore_r", 0F, 0F, 0F),
            new Key(2, 6F, "body/arm_r/fore_r", -40F, 0F, 0F),
            new Key(2, 9F, "body/arm_r/fore_r", 10F, 0F, 0F),
            new Key(2, 14F, "body/arm_r/fore_r", 0F, 0F, 0F),
            new Key(2, 0F, "body/arm_l", 0F, 0F, 0F),
            new Key(2, 6F, "body/arm_l", -40F, 0F, -20F),
            new Key(2, 9F, "body/arm_l", 20F, 0F, 0F),
            new Key(2, 14F, "body/arm_l", 0F, 0F, 0F),
            new Key(2, 0F, "body", 0F, 0F, 0F),
            new Key(2, 6F, "body", -12F, 18F, 0F),
            new Key(2, 9F, "body", 18F, -12F, 0F),
            new Key(2, 14F, "body", 0F, 0F, 0F),
            new Key(2, 0F, "body/head", 0F, 0F, 0F),
            new Key(2, 6F, "body/head", -10F, -10F, 0F),
            new Key(2, 9F, "body/head", 5F, 8F, 0F),
            new Key(2, 14F, "body/head", 0F, 0F, 0F),
            new Key(2, 0F, "body/head/jaw", 0F, 0F, 0F),
            new Key(2, 6F, "body/head/jaw", 15F, 0F, 0F),
            new Key(2, 9F, "body/head/jaw", 35F, 0F, 0F),
            new Key(2, 14F, "body/head/jaw", 0F, 0F, 0F),
            new Key(2, 0F, "body/wing_r", 0F, 0F, 0F),
            new Key(2, 6F, "body/wing_r", 0F, 10F, -25F),
            new Key(2, 9F, "body/wing_r", 0F, 0F, 20F),
            new Key(2, 14F, "body/wing_r", 0F, 0F, 0F),
            new Key(2, 0F, "body/wing_l", 0F, 0F, 0F),
            new Key(2, 6F, "body/wing_l", 0F, -10F, 25F),
            new Key(2, 9F, "body/wing_l", 0F, 0F, -20F),
            new Key(2, 14F, "body/wing_l", 0F, 0F, 0F),
            new Key(2, 0F, "body/tail_1", 0F, 0F, 0F),
            new Key(2, 6F, "body/tail_1", -15F, 0F, 0F),
            new Key(2, 9F, "body/tail_1", 15F, 0F, 0F),
            new Key(2, 14F, "body/tail_1", 0F, 0F, 0F)};

    public static final float CINDER_IMP_SCALE = 1.0F;

    /** Magma Hound: Lavahund aus Basalt und Lavakruste mit gluehendem Maul, Glutaugen, Rueckenkamm aus Obsidianzacken und Schwanz mit Glutspitze. Textur {@code entity/dungeon/magma_hound.png}. */
    public static LayerDefinition magmaHound() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(68, 0).addBox(-5F, -3F, -9.5F, 10, 7, 8, NONE)
                .texOffs(36, 0).addBox(-4F, -4F, -9.5F, 8, 9, 8, NONE)
                .texOffs(0, 0).addBox(-4F, -3F, -10.5F, 8, 7, 10, NONE)
                .texOffs(60, 17).addBox(-4F, -2.5F, 0.5F, 8, 4, 8, NONE)
                .texOffs(32, 17).addBox(-3F, -3.5F, 0.5F, 6, 6, 8, NONE)
                .texOffs(0, 17).addBox(-3F, -2.5F, -0.5F, 6, 4, 10, NONE)
                .texOffs(54, 31).addBox(-4F, -5F, -9F, 8, 2, 8, NONE)
                .texOffs(86, 31).addBox(-3F, -4.5F, 0.5F, 6, 2, 8, NONE)
                .texOffs(46, 42).addBox(-3.5F, 4.4F, -9F, 7, 1, 7, NONE)
                .texOffs(0, 51).addBox(-2.5F, 2F, 1F, 5, 1, 6, NONE)
                .texOffs(118, 58).addBox(-5.4F, -2.5F, -8F, 1, 5, 1, NONE)
                .texOffs(122, 58).addBox(-5.4F, -2.5F, -5.5F, 1, 5, 1, NONE)
                .texOffs(0, 64).addBox(-5.4F, -2.5F, -3F, 1, 5, 1, NONE)
                .texOffs(4, 64).addBox(4.4F, -2.5F, -8F, 1, 5, 1, NONE)
                .texOffs(8, 64).addBox(4.4F, -2.5F, -5.5F, 1, 5, 1, NONE)
                .texOffs(12, 64).addBox(4.4F, -2.5F, -3F, 1, 5, 1, NONE)
                .texOffs(74, 42).addBox(-3F, -3.5F, -12F, 6, 6, 2, NONE),
                PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
        PartDefinition p_head = p_body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 31).addBox(-3.5F, -4F, -5F, 7, 5, 6, NONE)
                .texOffs(0, 58).addBox(-3F, -4.4F, -4F, 6, 1, 5, NONE)
                .texOffs(78, 58).addBox(-4F, -3F, -3F, 1, 3, 3, NONE)
                .texOffs(86, 58).addBox(3F, -3F, -3F, 1, 3, 3, NONE)
                .texOffs(24, 42).addBox(-2.5F, -2.5F, -11F, 5, 3, 6, NONE)
                .texOffs(62, 51).addBox(-1.5F, -3.4F, -10.5F, 3, 1, 6, NONE)
                .texOffs(122, 70).addBox(-1F, -2.7F, -11.4F, 2, 1, 1, NONE)
                .texOffs(22, 51).addBox(-2F, 0.4F, -10.5F, 4, 1, 6, NONE)
                .texOffs(78, 70).addBox(-2.5F, 0.4F, -11F, 1, 2, 1, NONE)
                .texOffs(82, 70).addBox(1.5F, 0.4F, -11F, 1, 2, 1, NONE)
                .texOffs(12, 74).addBox(-2.5F, 0.4F, -8F, 1, 1, 1, NONE)
                .texOffs(16, 74).addBox(1.5F, 0.4F, -8F, 1, 1, 1, NONE)
                .texOffs(0, 74).addBox(-3.6F, -3F, -5.3F, 2, 1, 1, NONE)
                .texOffs(6, 74).addBox(1.6F, -3F, -5.3F, 2, 1, 1, NONE)
                .texOffs(32, 70).addBox(-3.5F, -4.6F, -5.5F, 7, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, -3F, -9F, 0.175F, 0F, 0F));
        PartDefinition p_jaw = p_head.addOrReplaceChild("jaw", CubeListBuilder.create()
                .texOffs(0, 42).addBox(-2.5F, 0F, -6.5F, 5, 2, 7, NONE)
                .texOffs(42, 51).addBox(-2F, -0.6F, -6F, 4, 1, 6, NONE)
                .texOffs(20, 74).addBox(-2.5F, -1F, -6.5F, 1, 1, 1, NONE)
                .texOffs(24, 74).addBox(1.5F, -1F, -6.5F, 1, 1, 1, NONE)
                .texOffs(28, 74).addBox(-2.5F, -1F, -3.5F, 1, 1, 1, NONE)
                .texOffs(32, 74).addBox(1.5F, -1F, -3.5F, 1, 1, 1, NONE)
                .texOffs(16, 64).addBox(-1.5F, 2F, -4F, 3, 1, 4, NONE),
                PartPose.offsetAndRotation(0F, 0.5F, -4.5F, 0.14F, 0F, 0F));
        PartDefinition p_ear_r = p_head.addOrReplaceChild("ear_r", CubeListBuilder.create()
                .texOffs(0, 70).addBox(-1F, -2F, -1F, 2, 2, 2, NONE)
                .texOffs(86, 70).addBox(-0.5F, -3.5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-2.5F, -3.5F, -1.5F, -0.785F, 0F, -0.349F));
        PartDefinition p_ear_l = p_head.addOrReplaceChild("ear_l", CubeListBuilder.create()
                .texOffs(8, 70).addBox(-1F, -2F, -1F, 2, 2, 2, NONE)
                .texOffs(90, 70).addBox(-0.5F, -3.5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(2.5F, -3.5F, -1.5F, -0.785F, 0F, 0.349F));
        PartDefinition p_leg_fr = p_body.addOrReplaceChild("leg_fr", CubeListBuilder.create()
                .texOffs(26, 31).addBox(-1.5F, -2F, -2F, 3, 7, 4, NONE)
                .texOffs(94, 51).addBox(-2F, -2.5F, -1.5F, 1, 4, 3, NONE),
                PartPose.offsetAndRotation(-3.2F, 2F, -6F, 0.14F, 0F, 0F));
        PartDefinition p_shin_fr = p_leg_fr.addOrReplaceChild("shin_fr", CubeListBuilder.create()
                .texOffs(94, 58).addBox(-1F, 0F, -1F, 2, 4, 2, NONE)
                .texOffs(50, 70).addBox(-1.5F, -0.5F, -1.6F, 3, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 5F, 0F, -0.105F, 0F, 0F));
        PartDefinition p_paw_fr = p_shin_fr.addOrReplaceChild("paw_fr", CubeListBuilder.create()
                .texOffs(22, 58).addBox(-1.5F, 0F, -2.5F, 3, 2, 4, NONE)
                .texOffs(36, 74).addBox(-1.5F, 0.6F, -3.3F, 1, 1, 1, NONE)
                .texOffs(40, 74).addBox(0.5F, 0.6F, -3.3F, 1, 1, 1, NONE)
                .texOffs(44, 74).addBox(-0.5F, 0.6F, -3.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 3.5F, 0F, -0.175F, 0F, 0F));
        PartDefinition p_leg_fl = p_body.addOrReplaceChild("leg_fl", CubeListBuilder.create()
                .texOffs(40, 31).addBox(-1.5F, -2F, -2F, 3, 7, 4, NONE)
                .texOffs(102, 51).addBox(1F, -2.5F, -1.5F, 1, 4, 3, NONE),
                PartPose.offsetAndRotation(3.2F, 2F, -6F, 0.14F, 0F, 0F));
        PartDefinition p_shin_fl = p_leg_fl.addOrReplaceChild("shin_fl", CubeListBuilder.create()
                .texOffs(102, 58).addBox(-1F, 0F, -1F, 2, 4, 2, NONE)
                .texOffs(58, 70).addBox(-1.5F, -0.5F, -1.6F, 3, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 5F, 0F, -0.105F, 0F, 0F));
        PartDefinition p_paw_fl = p_shin_fl.addOrReplaceChild("paw_fl", CubeListBuilder.create()
                .texOffs(36, 58).addBox(-1.5F, 0F, -2.5F, 3, 2, 4, NONE)
                .texOffs(48, 74).addBox(-1.5F, 0.6F, -3.3F, 1, 1, 1, NONE)
                .texOffs(52, 74).addBox(0.5F, 0.6F, -3.3F, 1, 1, 1, NONE)
                .texOffs(56, 74).addBox(-0.5F, 0.6F, -3.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 3.5F, 0F, -0.175F, 0F, 0F));
        PartDefinition p_leg_br = p_body.addOrReplaceChild("leg_br", CubeListBuilder.create()
                .texOffs(92, 17).addBox(-2F, -2F, -2.5F, 4, 7, 5, NONE)
                .texOffs(90, 42).addBox(-2.5F, -1.5F, -2F, 1, 4, 4, NONE),
                PartPose.offsetAndRotation(-3.2F, 1F, 6F, -0.349F, 0F, 0F));
        PartDefinition p_hock_br = p_leg_br.addOrReplaceChild("hock_br", CubeListBuilder.create()
                .texOffs(110, 51).addBox(-1F, 0F, -1F, 2, 5, 2, NONE)
                .texOffs(94, 70).addBox(-0.5F, 0F, 1F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 4.5F, 1F, 0.698F, 0F, 0F));
        PartDefinition p_paw_br = p_hock_br.addOrReplaceChild("paw_br", CubeListBuilder.create()
                .texOffs(50, 58).addBox(-1.5F, 0F, -2.5F, 3, 2, 4, NONE)
                .texOffs(60, 74).addBox(-1.5F, 0.6F, -3.3F, 1, 1, 1, NONE)
                .texOffs(64, 74).addBox(0.5F, 0.6F, -3.3F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 4.5F, 0F, -0.524F, 0F, 0F));
        PartDefinition p_leg_bl = p_body.addOrReplaceChild("leg_bl", CubeListBuilder.create()
                .texOffs(110, 17).addBox(-2F, -2F, -2.5F, 4, 7, 5, NONE)
                .texOffs(100, 42).addBox(1.5F, -1.5F, -2F, 1, 4, 4, NONE),
                PartPose.offsetAndRotation(3.2F, 1F, 6F, -0.349F, 0F, 0F));
        PartDefinition p_hock_bl = p_leg_bl.addOrReplaceChild("hock_bl", CubeListBuilder.create()
                .texOffs(118, 51).addBox(-1F, 0F, -1F, 2, 5, 2, NONE)
                .texOffs(98, 70).addBox(-0.5F, 0F, 1F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 4.5F, 1F, 0.698F, 0F, 0F));
        PartDefinition p_paw_bl = p_hock_bl.addOrReplaceChild("paw_bl", CubeListBuilder.create()
                .texOffs(64, 58).addBox(-1.5F, 0F, -2.5F, 3, 2, 4, NONE)
                .texOffs(68, 74).addBox(-1.5F, 0.6F, -3.3F, 1, 1, 1, NONE)
                .texOffs(72, 74).addBox(0.5F, 0.6F, -3.3F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 4.5F, 0F, -0.524F, 0F, 0F));
        PartDefinition p_tail = p_body.addOrReplaceChild("tail", CubeListBuilder.create()
                .texOffs(80, 51).addBox(-1.5F, -1.5F, 0F, 3, 3, 4, NONE)
                .texOffs(16, 70).addBox(-0.5F, -2.5F, 0.5F, 1, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, -2F, 8.5F, 0.349F, 0F, 0F));
        PartDefinition p_tail_tip = p_tail.addOrReplaceChild("tail_tip", CubeListBuilder.create()
                .texOffs(30, 64).addBox(-1F, -1F, 0F, 2, 2, 3, NONE)
                .texOffs(66, 70).addBox(-0.5F, -0.5F, 3F, 1, 1, 2, NONE)
                .texOffs(72, 70).addBox(-0.5F, -2F, 0.5F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, 0F, 4F, -0.262F, 0F, 0F));
        PartDefinition p_crest_1 = p_body.addOrReplaceChild("crest_1", CubeListBuilder.create()
                .texOffs(40, 64).addBox(-1F, -3F, -1F, 2, 3, 2, NONE)
                .texOffs(102, 70).addBox(-0.5F, -4.5F, -0.5F, 1, 2, 1, NONE)
                .texOffs(64, 64).addBox(-1.5F, -1F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, -5F, -7.5F, -0.611F, 0F, 0F));
        PartDefinition p_crest_2 = p_body.addOrReplaceChild("crest_2", CubeListBuilder.create()
                .texOffs(110, 58).addBox(-1F, -4F, -1F, 2, 4, 2, NONE)
                .texOffs(106, 70).addBox(-0.5F, -5.5F, -0.5F, 1, 2, 1, NONE)
                .texOffs(76, 64).addBox(-1.5F, -1F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, -5F, -4F, -0.698F, 0F, 0F));
        PartDefinition p_crest_3 = p_body.addOrReplaceChild("crest_3", CubeListBuilder.create()
                .texOffs(48, 64).addBox(-1F, -3F, -1F, 2, 3, 2, NONE)
                .texOffs(110, 70).addBox(-0.5F, -4.5F, -0.5F, 1, 2, 1, NONE)
                .texOffs(88, 64).addBox(-1.5F, -1F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, -4.5F, -0.5F, -0.785F, 0F, 0F));
        PartDefinition p_crest_4 = p_body.addOrReplaceChild("crest_4", CubeListBuilder.create()
                .texOffs(56, 64).addBox(-1F, -3F, -1F, 2, 3, 2, NONE)
                .texOffs(114, 70).addBox(-0.5F, -4.5F, -0.5F, 1, 2, 1, NONE)
                .texOffs(100, 64).addBox(-1.5F, -1F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, -4.5F, 3F, -0.873F, 0F, 0F));
        PartDefinition p_crest_5 = p_body.addOrReplaceChild("crest_5", CubeListBuilder.create()
                .texOffs(24, 70).addBox(-1F, -2F, -1F, 2, 2, 2, NONE)
                .texOffs(118, 70).addBox(-0.5F, -3.5F, -0.5F, 1, 2, 1, NONE)
                .texOffs(112, 64).addBox(-1.5F, -1F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, -4.5F, 6.5F, -0.96F, 0F, 0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final Role[] MAGMA_HOUND_ROLES = {new Role("body", "body", 0.00F), new Role("body/head", "head", 0.00F), new Role("body/head/jaw", "jaw", 0.00F), new Role("body/leg_fr", "leg_a", 0.00F), new Role("body/leg_fl", "leg_b", 0.00F), new Role("body/leg_br", "leg_b", 0.00F), new Role("body/leg_bl", "leg_a", 0.00F), new Role("body/tail", "chain", 1.00F), new Role("body/tail/tail_tip", "chain", 2.00F)};

    public static final Key[] MAGMA_HOUND_POSES = {
            new Key(1, 0F, "body/head", 0F, 0F, 0F),
            new Key(1, 3F, "body/head", -25F, 0F, 0F),
            new Key(1, 6F, "body/head", 18F, 0F, 0F),
            new Key(1, 10F, "body/head", 0F, 0F, 0F),
            new Key(1, 0F, "body/head/jaw", 0F, 0F, 0F),
            new Key(1, 3F, "body/head/jaw", 40F, 0F, 0F),
            new Key(1, 6F, "body/head/jaw", -5F, 0F, 0F),
            new Key(1, 10F, "body/head/jaw", 0F, 0F, 0F),
            new Key(1, 0F, "body", 0F, 0F, 0F),
            new Key(1, 3F, "body", -6F, 0F, 0F),
            new Key(1, 6F, "body", 8F, 0F, 0F),
            new Key(1, 10F, "body", 0F, 0F, 0F),
            new Key(1, 0F, "body/leg_fr", 0F, 0F, 0F),
            new Key(1, 3F, "body/leg_fr", -10F, 0F, 0F),
            new Key(1, 6F, "body/leg_fr", -25F, 0F, 0F),
            new Key(1, 10F, "body/leg_fr", 0F, 0F, 0F),
            new Key(1, 0F, "body/leg_fl", 0F, 0F, 0F),
            new Key(1, 3F, "body/leg_fl", -10F, 0F, 0F),
            new Key(1, 6F, "body/leg_fl", -25F, 0F, 0F),
            new Key(1, 10F, "body/leg_fl", 0F, 0F, 0F),
            new Key(3, 0F, "body", 0F, 0F, 0F),
            new Key(3, 4F, "body", 10F, 0F, 0F),
            new Key(3, 8F, "body", -22F, 0F, 0F),
            new Key(3, 11F, "body", 10F, 0F, 0F),
            new Key(3, 14F, "body", 0F, 0F, 0F),
            new Key(3, 0F, "body/head", 0F, 0F, 0F),
            new Key(3, 4F, "body/head", 10F, 0F, 0F),
            new Key(3, 8F, "body/head", 15F, 0F, 0F),
            new Key(3, 14F, "body/head", 0F, 0F, 0F),
            new Key(3, 0F, "body/head/jaw", 0F, 0F, 0F),
            new Key(3, 4F, "body/head/jaw", 0F, 0F, 0F),
            new Key(3, 8F, "body/head/jaw", 35F, 0F, 0F),
            new Key(3, 14F, "body/head/jaw", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_fr", 0F, 0F, 0F),
            new Key(3, 4F, "body/leg_fr", 25F, 0F, 0F),
            new Key(3, 8F, "body/leg_fr", -70F, 0F, 0F),
            new Key(3, 11F, "body/leg_fr", -20F, 0F, 0F),
            new Key(3, 14F, "body/leg_fr", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_fl", 0F, 0F, 0F),
            new Key(3, 4F, "body/leg_fl", 25F, 0F, 0F),
            new Key(3, 8F, "body/leg_fl", -70F, 0F, 0F),
            new Key(3, 11F, "body/leg_fl", -20F, 0F, 0F),
            new Key(3, 14F, "body/leg_fl", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_br", 0F, 0F, 0F),
            new Key(3, 4F, "body/leg_br", -25F, 0F, 0F),
            new Key(3, 8F, "body/leg_br", 55F, 0F, 0F),
            new Key(3, 11F, "body/leg_br", 10F, 0F, 0F),
            new Key(3, 14F, "body/leg_br", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_bl", 0F, 0F, 0F),
            new Key(3, 4F, "body/leg_bl", -25F, 0F, 0F),
            new Key(3, 8F, "body/leg_bl", 55F, 0F, 0F),
            new Key(3, 11F, "body/leg_bl", 10F, 0F, 0F),
            new Key(3, 14F, "body/leg_bl", 0F, 0F, 0F),
            new Key(3, 0F, "body/tail", 0F, 0F, 0F),
            new Key(3, 4F, "body/tail", 15F, 0F, 0F),
            new Key(3, 8F, "body/tail", -30F, 0F, 0F),
            new Key(3, 14F, "body/tail", 0F, 0F, 0F)};

    public static final float MAGMA_HOUND_SCALE = 1.0F;

    /** Crystal Mantis: aufgerichtete Gottesanbeterin aus violettem Chitin mit Fangarmen und tuerkisen Schrabidium-Kristallklingen, schmalem Kopf mit Facettenaugen und Fuehlern, vier Laufbeinen und Kristallfluegeln. Textur {@code entity/dungeon/crystal_mantis.png}. */
    public static LayerDefinition crystalMantis() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 19).addBox(-2.5F, -2F, -3F, 5, 4, 7, NONE)
                .texOffs(34, 38).addBox(-3F, -2.5F, -2F, 6, 1, 5, NONE)
                .texOffs(76, 30).addBox(-2F, 1.5F, -2.5F, 4, 1, 6, NONE),
                PartPose.offsetAndRotation(0F, 12F, 1F, 0F, 0F, 0F));
        PartDefinition p_thorax = p_body.addOrReplaceChild("thorax", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2F, -14F, -2F, 4, 15, 4, NONE)
                .texOffs(0, 30).addBox(-2.5F, -13.5F, -2.3F, 5, 3, 5, NONE)
                .texOffs(20, 30).addBox(-2.5F, -8F, -2.3F, 5, 3, 5, NONE)
                .texOffs(96, 30).addBox(-2.5F, -3F, -2.3F, 5, 2, 5, NONE)
                .texOffs(16, 0).addBox(-1.5F, -14F, -2.5F, 3, 14, 1, NONE)
                .texOffs(108, 38).addBox(-3F, -11F, -2F, 1, 3, 3, NONE)
                .texOffs(116, 38).addBox(2F, -11F, -2F, 1, 3, 3, NONE),
                PartPose.offsetAndRotation(0F, -1.5F, -2F, 0.384F, 0F, 0F));
        PartDefinition p_head = p_thorax.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 38).addBox(-3F, -3.5F, -2F, 6, 3, 4, NONE)
                .texOffs(84, 38).addBox(-5.5F, -4.5F, -2.2F, 3, 3, 3, NONE)
                .texOffs(96, 38).addBox(2.5F, -4.5F, -2.2F, 3, 3, 3, NONE)
                .texOffs(52, 51).addBox(-2F, -0.5F, -2F, 4, 2, 2, NONE)
                .texOffs(64, 51).addBox(-1F, 1.5F, -1.8F, 2, 2, 2, NONE)
                .texOffs(112, 59).addBox(-1.5F, 3F, -2.2F, 1, 1, 1, NONE)
                .texOffs(116, 59).addBox(0.5F, 3F, -2.2F, 1, 1, 1, NONE)
                .texOffs(120, 59).addBox(-1.5F, -3.7F, -2.3F, 1, 1, 1, NONE)
                .texOffs(124, 59).addBox(0.5F, -3.7F, -2.3F, 1, 1, 1, NONE)
                .texOffs(20, 55).addBox(-2.5F, -4.5F, -1F, 5, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, -14.5F, -0.5F, 0.175F, 0F, 0F));
        PartDefinition p_antenna_l = p_head.addOrReplaceChild("antenna_l", CubeListBuilder.create()
                .texOffs(8, 45).addBox(-0.5F, -5F, -0.5F, 1, 5, 1, NONE),
                PartPose.offsetAndRotation(1F, -3.2F, -1.5F, -0.524F, 0F, 0.314F));
        PartDefinition p_antip_l = p_antenna_l.addOrReplaceChild("antip_l", CubeListBuilder.create()
                .texOffs(12, 45).addBox(-0.5F, -5F, -0.5F, 1, 5, 1, NONE)
                .texOffs(0, 61).addBox(-0.5F, -6F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -5F, 0F, -0.611F, 0F, 0F));
        PartDefinition p_antenna_r = p_head.addOrReplaceChild("antenna_r", CubeListBuilder.create()
                .texOffs(16, 45).addBox(-0.5F, -5F, -0.5F, 1, 5, 1, NONE),
                PartPose.offsetAndRotation(-1F, -3.2F, -1.5F, -0.524F, 0F, -0.314F));
        PartDefinition p_antip_r = p_antenna_r.addOrReplaceChild("antip_r", CubeListBuilder.create()
                .texOffs(20, 45).addBox(-0.5F, -5F, -0.5F, 1, 5, 1, NONE)
                .texOffs(4, 61).addBox(-0.5F, -6F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -5F, 0F, -0.611F, 0F, 0F));
        PartDefinition p_arm_l = p_thorax.addOrReplaceChild("arm_l", CubeListBuilder.create()
                .texOffs(40, 30).addBox(-1F, -0.5F, -1F, 2, 6, 2, NONE)
                .texOffs(76, 45).addBox(-1.4F, 0F, -1.4F, 1, 4, 1, new CubeDeformation(0.2F))
                .texOffs(72, 51).addBox(-1.2F, -1F, -1.2F, 2, 2, 2, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(2F, -10F, -1.5F, -0.524F, -0.175F, -0.262F));
        PartDefinition p_femur_l = p_arm_l.addOrReplaceChild("femur_l", CubeListBuilder.create()
                .texOffs(88, 0).addBox(-1F, 0F, -1.5F, 2, 9, 3, NONE)
                .texOffs(106, 19).addBox(-1.2F, 0.5F, -1.7F, 1, 7, 2, new CubeDeformation(0.15F))
                .texOffs(68, 30).addBox(-0.5F, 1F, 1.5F, 1, 7, 1, NONE)
                .texOffs(80, 45).addBox(-0.5F, 2.5F, 2.5F, 1, 4, 1, NONE)
                .texOffs(8, 55).addBox(-0.5F, 7F, 1.5F, 1, 2, 2, NONE)
                .texOffs(56, 38).addBox(-1.5F, -0.5F, -2F, 3, 2, 4, NONE),
                PartPose.offsetAndRotation(0F, 5.5F, 0F, -2.443F, 0F, 0F));
        PartDefinition p_tibia_l = p_femur_l.addOrReplaceChild("tibia_l", CubeListBuilder.create()
                .texOffs(90, 19).addBox(-1F, 0F, -1F, 2, 7, 2, NONE)
                .texOffs(56, 30).addBox(-0.5F, 0.5F, -2.5F, 1, 6, 2, NONE)
                .texOffs(34, 55).addBox(-0.5F, 6.5F, -2F, 1, 2, 1, NONE)
                .texOffs(8, 61).addBox(-0.5F, 8F, -2F, 1, 1, 1, NONE)
                .texOffs(24, 45).addBox(-0.5F, 1F, 1F, 1, 5, 1, NONE),
                PartPose.offsetAndRotation(0F, 8.5F, -0.5F, 2.618F, 0F, 0F));
        PartDefinition p_arm_r = p_thorax.addOrReplaceChild("arm_r", CubeListBuilder.create()
                .texOffs(48, 30).addBox(-1F, -0.5F, -1F, 2, 6, 2, NONE)
                .texOffs(84, 45).addBox(0.4F, 0F, -1.4F, 1, 4, 1, new CubeDeformation(0.2F))
                .texOffs(80, 51).addBox(-0.8F, -1F, -1.2F, 2, 2, 2, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(-2F, -10F, -1.5F, -0.524F, 0.175F, 0.262F));
        PartDefinition p_femur_r = p_arm_r.addOrReplaceChild("femur_r", CubeListBuilder.create()
                .texOffs(98, 0).addBox(-1F, 0F, -1.5F, 2, 9, 3, NONE)
                .texOffs(112, 19).addBox(0.2F, 0.5F, -1.7F, 1, 7, 2, new CubeDeformation(0.15F))
                .texOffs(72, 30).addBox(-0.5F, 1F, 1.5F, 1, 7, 1, NONE)
                .texOffs(88, 45).addBox(-0.5F, 2.5F, 2.5F, 1, 4, 1, NONE)
                .texOffs(14, 55).addBox(-0.5F, 7F, 1.5F, 1, 2, 2, NONE)
                .texOffs(70, 38).addBox(-1.5F, -0.5F, -2F, 3, 2, 4, NONE),
                PartPose.offsetAndRotation(0F, 5.5F, 0F, -2.443F, 0F, 0F));
        PartDefinition p_tibia_r = p_femur_r.addOrReplaceChild("tibia_r", CubeListBuilder.create()
                .texOffs(98, 19).addBox(-1F, 0F, -1F, 2, 7, 2, NONE)
                .texOffs(62, 30).addBox(-0.5F, 0.5F, -2.5F, 1, 6, 2, NONE)
                .texOffs(38, 55).addBox(-0.5F, 6.5F, -2F, 1, 2, 1, NONE)
                .texOffs(12, 61).addBox(-0.5F, 8F, -2F, 1, 1, 1, NONE)
                .texOffs(28, 45).addBox(-0.5F, 1F, 1F, 1, 5, 1, NONE),
                PartPose.offsetAndRotation(0F, 8.5F, -0.5F, 2.618F, 0F, 0F));
        PartDefinition p_tspike_1 = p_thorax.addOrReplaceChild("tspike_1", CubeListBuilder.create()
                .texOffs(88, 51).addBox(-1F, -2F, -1F, 2, 2, 2, NONE)
                .texOffs(42, 55).addBox(-0.5F, -3.5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -12F, 2F, 0.785F, 0F, 0F));
        PartDefinition p_tspike_2 = p_thorax.addOrReplaceChild("tspike_2", CubeListBuilder.create()
                .texOffs(52, 45).addBox(-1F, -3F, -1F, 2, 3, 2, NONE)
                .texOffs(46, 55).addBox(-0.5F, -4.5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -6.5F, 2F, 0.873F, 0F, 0F));
        PartDefinition p_abdomen = p_body.addOrReplaceChild("abdomen", CubeListBuilder.create()
                .texOffs(24, 19).addBox(-3.5F, -2.5F, 0F, 7, 6, 5, NONE)
                .texOffs(70, 19).addBox(-3F, -2F, 5F, 6, 5, 4, NONE)
                .texOffs(20, 38).addBox(-2F, -1.5F, 9F, 4, 4, 3, NONE)
                .texOffs(96, 51).addBox(-1F, -0.5F, 12F, 2, 2, 2, NONE)
                .texOffs(32, 45).addBox(-3F, 3.5F, 0.5F, 6, 1, 4, NONE)
                .texOffs(36, 51).addBox(-2.5F, 3F, 5.5F, 5, 1, 3, NONE)
                .texOffs(24, 0).addBox(-3.8F, -3.2F, -2F, 4, 1, 12, NONE)
                .texOffs(56, 0).addBox(-0.2F, -3.4F, -2F, 4, 1, 12, NONE)
                .texOffs(48, 19).addBox(-0.5F, -3.8F, -1F, 1, 1, 10, NONE),
                PartPose.offsetAndRotation(0F, 0F, 3.5F, -0.209F, 0F, 0F));
        PartDefinition p_aspike_1 = p_abdomen.addOrReplaceChild("aspike_1", CubeListBuilder.create()
                .texOffs(60, 45).addBox(-1F, -3F, -1F, 2, 3, 2, NONE)
                .texOffs(50, 55).addBox(-0.5F, -4.5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-2F, -3.5F, 3F, -0.524F, 0F, -0.349F));
        PartDefinition p_aspike_2 = p_abdomen.addOrReplaceChild("aspike_2", CubeListBuilder.create()
                .texOffs(0, 45).addBox(-1F, -4F, -1F, 2, 4, 2, NONE)
                .texOffs(54, 55).addBox(-0.5F, -5.5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(2F, -3.5F, 6F, -0.436F, 0F, 0.436F));
        PartDefinition p_aspike_3 = p_abdomen.addOrReplaceChild("aspike_3", CubeListBuilder.create()
                .texOffs(68, 45).addBox(-1F, -3F, -1F, 2, 3, 2, NONE)
                .texOffs(58, 55).addBox(-0.5F, -4.5F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -3F, 9F, -0.785F, 0F, 0F));
        PartDefinition p_leg_m_l = p_body.addOrReplaceChild("leg_m_l", CubeListBuilder.create()
                .texOffs(92, 45).addBox(-0.5F, -1F, -1F, 7, 2, 2, NONE)
                .texOffs(104, 51).addBox(5F, -1.2F, -1.2F, 2, 2, 2, new CubeDeformation(0.15F)),
                PartPose.offsetAndRotation(2.5F, 1F, -1.5F, 0F, 0.611F, -0.663F));
        PartDefinition p_tib_m_l = p_leg_m_l.addOrReplaceChild("tib_m_l", CubeListBuilder.create()
                .texOffs(62, 55).addBox(0F, -0.5F, -0.5F, 13, 1, 1, new CubeDeformation(0.15F))
                .texOffs(88, 59).addBox(5F, -0.6F, -0.6F, 2, 1, 1, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(6F, 0F, 0F, 0F, 0F, 1.885F));
        PartDefinition p_tars_m_l = p_tib_m_l.addOrReplaceChild("tars_m_l", CubeListBuilder.create()
                .texOffs(56, 59).addBox(0F, -0.5F, -0.5F, 3, 1, 1, NONE),
                PartPose.offsetAndRotation(13F, 0F, 0F, 0F, 0F, -0.611F));
        PartDefinition p_leg_m_r = p_body.addOrReplaceChild("leg_m_r", CubeListBuilder.create()
                .texOffs(110, 45).addBox(-6.5F, -1F, -1F, 7, 2, 2, NONE)
                .texOffs(112, 51).addBox(-7F, -1.2F, -1.2F, 2, 2, 2, new CubeDeformation(0.15F)),
                PartPose.offsetAndRotation(-2.5F, 1F, -1.5F, 0F, -0.611F, 0.663F));
        PartDefinition p_tib_m_r = p_leg_m_r.addOrReplaceChild("tib_m_r", CubeListBuilder.create()
                .texOffs(90, 55).addBox(-13F, -0.5F, -0.5F, 13, 1, 1, new CubeDeformation(0.15F))
                .texOffs(94, 59).addBox(-7F, -0.6F, -0.6F, 2, 1, 1, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(-6F, 0F, 0F, 0F, 0F, -1.885F));
        PartDefinition p_tars_m_r = p_tib_m_r.addOrReplaceChild("tars_m_r", CubeListBuilder.create()
                .texOffs(64, 59).addBox(-3F, -0.5F, -0.5F, 3, 1, 1, NONE),
                PartPose.offsetAndRotation(-13F, 0F, 0F, 0F, 0F, 0.611F));
        PartDefinition p_leg_h_l = p_body.addOrReplaceChild("leg_h_l", CubeListBuilder.create()
                .texOffs(0, 51).addBox(-0.5F, -1F, -1F, 7, 2, 2, NONE)
                .texOffs(120, 51).addBox(5F, -1.2F, -1.2F, 2, 2, 2, new CubeDeformation(0.15F)),
                PartPose.offsetAndRotation(2.5F, 1F, 2.5F, 0F, -0.698F, -0.663F));
        PartDefinition p_tib_h_l = p_leg_h_l.addOrReplaceChild("tib_h_l", CubeListBuilder.create()
                .texOffs(0, 59).addBox(0F, -0.5F, -0.5F, 13, 1, 1, new CubeDeformation(0.15F))
                .texOffs(100, 59).addBox(5F, -0.6F, -0.6F, 2, 1, 1, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(6F, 0F, 0F, 0F, 0F, 1.885F));
        PartDefinition p_tars_h_l = p_tib_h_l.addOrReplaceChild("tars_h_l", CubeListBuilder.create()
                .texOffs(72, 59).addBox(0F, -0.5F, -0.5F, 3, 1, 1, NONE),
                PartPose.offsetAndRotation(13F, 0F, 0F, 0F, 0F, -0.611F));
        PartDefinition p_leg_h_r = p_body.addOrReplaceChild("leg_h_r", CubeListBuilder.create()
                .texOffs(18, 51).addBox(-6.5F, -1F, -1F, 7, 2, 2, NONE)
                .texOffs(0, 55).addBox(-7F, -1.2F, -1.2F, 2, 2, 2, new CubeDeformation(0.15F)),
                PartPose.offsetAndRotation(-2.5F, 1F, 2.5F, 0F, 0.698F, 0.663F));
        PartDefinition p_tib_h_r = p_leg_h_r.addOrReplaceChild("tib_h_r", CubeListBuilder.create()
                .texOffs(28, 59).addBox(-13F, -0.5F, -0.5F, 13, 1, 1, new CubeDeformation(0.15F))
                .texOffs(106, 59).addBox(-7F, -0.6F, -0.6F, 2, 1, 1, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(-6F, 0F, 0F, 0F, 0F, -1.885F));
        PartDefinition p_tars_h_r = p_tib_h_r.addOrReplaceChild("tars_h_r", CubeListBuilder.create()
                .texOffs(80, 59).addBox(-3F, -0.5F, -0.5F, 3, 1, 1, NONE),
                PartPose.offsetAndRotation(-13F, 0F, 0F, 0F, 0F, 0.611F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final Role[] CRYSTAL_MANTIS_ROLES = {new Role("body", "body", 0.00F), new Role("body/thorax/head", "head", 0.00F), new Role("body/thorax/arm_l", "arm_l", 0.00F), new Role("body/thorax/arm_r", "arm_r", 0.00F), new Role("body/leg_m_l", "leg_b", 0.00F), new Role("body/leg_m_r", "leg_a", 0.00F), new Role("body/leg_h_l", "leg_a", 0.00F), new Role("body/leg_h_r", "leg_b", 0.00F)};

    public static final Key[] CRYSTAL_MANTIS_POSES = {
            new Key(1, 0F, "body/thorax", 0F, 0F, 0F),
            new Key(1, 3F, "body/thorax", -14F, 0F, 0F),
            new Key(1, 5F, "body/thorax", 22F, 0F, 0F),
            new Key(1, 10F, "body/thorax", 0F, 0F, 0F),
            new Key(1, 0F, "body/thorax/head", 0F, 0F, 0F),
            new Key(1, 3F, "body/thorax/head", 10F, 0F, 0F),
            new Key(1, 5F, "body/thorax/head", -15F, 0F, 0F),
            new Key(1, 10F, "body/thorax/head", 0F, 0F, 0F),
            new Key(1, 0F, "body/thorax/arm_r", 0F, 0F, 0F),
            new Key(1, 3F, "body/thorax/arm_r", -45F, 0F, 0F),
            new Key(1, 5F, "body/thorax/arm_r", 10F, 0F, 0F),
            new Key(1, 10F, "body/thorax/arm_r", 0F, 0F, 0F),
            new Key(1, 0F, "body/thorax/arm_l", 0F, 0F, 0F),
            new Key(1, 3F, "body/thorax/arm_l", -45F, 0F, 0F),
            new Key(1, 5F, "body/thorax/arm_l", 10F, 0F, 0F),
            new Key(1, 10F, "body/thorax/arm_l", 0F, 0F, 0F),
            new Key(1, 0F, "body/thorax/arm_r/femur_r", 0F, 0F, 0F),
            new Key(1, 3F, "body/thorax/arm_r/femur_r", -25F, 0F, 0F),
            new Key(1, 5F, "body/thorax/arm_r/femur_r", 70F, 0F, 0F),
            new Key(1, 10F, "body/thorax/arm_r/femur_r", 0F, 0F, 0F),
            new Key(1, 0F, "body/thorax/arm_l/femur_l", 0F, 0F, 0F),
            new Key(1, 3F, "body/thorax/arm_l/femur_l", -25F, 0F, 0F),
            new Key(1, 5F, "body/thorax/arm_l/femur_l", 70F, 0F, 0F),
            new Key(1, 10F, "body/thorax/arm_l/femur_l", 0F, 0F, 0F),
            new Key(1, 0F, "body/thorax/arm_r/femur_r/tibia_r", 0F, 0F, 0F),
            new Key(1, 3F, "body/thorax/arm_r/femur_r/tibia_r", -70F, 0F, 0F),
            new Key(1, 5F, "body/thorax/arm_r/femur_r/tibia_r", 5F, 0F, 0F),
            new Key(1, 10F, "body/thorax/arm_r/femur_r/tibia_r", 0F, 0F, 0F),
            new Key(1, 0F, "body/thorax/arm_l/femur_l/tibia_l", 0F, 0F, 0F),
            new Key(1, 3F, "body/thorax/arm_l/femur_l/tibia_l", -70F, 0F, 0F),
            new Key(1, 5F, "body/thorax/arm_l/femur_l/tibia_l", 5F, 0F, 0F),
            new Key(1, 10F, "body/thorax/arm_l/femur_l/tibia_l", 0F, 0F, 0F),
            new Key(3, 0F, "body", 0F, 0F, 0F),
            new Key(3, 4F, "body", 8F, 0F, 0F),
            new Key(3, 8F, "body", -15F, 0F, 0F),
            new Key(3, 14F, "body", 0F, 0F, 0F),
            new Key(3, 0F, "body/thorax", 0F, 0F, 0F),
            new Key(3, 4F, "body/thorax", 12F, 0F, 0F),
            new Key(3, 8F, "body/thorax", -10F, 0F, 0F),
            new Key(3, 14F, "body/thorax", 0F, 0F, 0F),
            new Key(3, 0F, "body/abdomen", 0F, 0F, 0F),
            new Key(3, 4F, "body/abdomen", 6F, 0F, 0F),
            new Key(3, 8F, "body/abdomen", 12F, 0F, 0F),
            new Key(3, 14F, "body/abdomen", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_m_l", 0F, 0F, 0F),
            new Key(3, 4F, "body/leg_m_l", 0F, 0F, -18F),
            new Key(3, 8F, "body/leg_m_l", -25F, 0F, 25F),
            new Key(3, 14F, "body/leg_m_l", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_m_r", 0F, 0F, 0F),
            new Key(3, 4F, "body/leg_m_r", 0F, 0F, 18F),
            new Key(3, 8F, "body/leg_m_r", -25F, 0F, -25F),
            new Key(3, 14F, "body/leg_m_r", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_h_l", 0F, 0F, 0F),
            new Key(3, 4F, "body/leg_h_l", 0F, 0F, -18F),
            new Key(3, 8F, "body/leg_h_l", 25F, 0F, 30F),
            new Key(3, 14F, "body/leg_h_l", 0F, 0F, 0F),
            new Key(3, 0F, "body/leg_h_r", 0F, 0F, 0F),
            new Key(3, 4F, "body/leg_h_r", 0F, 0F, 18F),
            new Key(3, 8F, "body/leg_h_r", 25F, 0F, -30F),
            new Key(3, 14F, "body/leg_h_r", 0F, 0F, 0F),
            new Key(3, 0F, "body/thorax/arm_r", 0F, 0F, 0F),
            new Key(3, 4F, "body/thorax/arm_r", 15F, 0F, 0F),
            new Key(3, 8F, "body/thorax/arm_r", -40F, 0F, 0F),
            new Key(3, 14F, "body/thorax/arm_r", 0F, 0F, 0F),
            new Key(3, 0F, "body/thorax/arm_l", 0F, 0F, 0F),
            new Key(3, 4F, "body/thorax/arm_l", 15F, 0F, 0F),
            new Key(3, 8F, "body/thorax/arm_l", -40F, 0F, 0F),
            new Key(3, 14F, "body/thorax/arm_l", 0F, 0F, 0F)};

    public static final float CRYSTAL_MANTIS_SCALE = 1.0F;

    /** Schrab Spitter: gedrungene Kristallkroete mit violetter Warzenhaut, tuerkis leuchtendem Kehlsack, breitem Maul, Glubschaugen und Schrabidium-Kristallauswuechsen auf dem Ruecken. Textur {@code entity/dungeon/schrab_spitter.png}. */
    public static LayerDefinition schrabSpitter() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 22).addBox(-7.5F, -3.5F, -6F, 15, 7, 13, NONE)
                .texOffs(56, 0).addBox(-6.5F, -4.5F, -6F, 13, 9, 13, NONE)
                .texOffs(0, 0).addBox(-6.5F, -3.5F, -7F, 13, 7, 15, NONE)
                .texOffs(56, 22).addBox(-6.5F, 3.5F, -6F, 13, 2, 12, NONE)
                .texOffs(66, 56).addBox(-5.5F, -5.1F, -5F, 11, 1, 11, NONE)
                .texOffs(100, 112).addBox(-4.5F, -5.3F, 2F, 2, 1, 2, NONE)
                .texOffs(108, 112).addBox(3F, -5.3F, -2F, 2, 1, 2, NONE)
                .texOffs(76, 112).addBox(-8.1F, -2F, 3F, 1, 2, 2, NONE)
                .texOffs(82, 112).addBox(7.1F, -1F, -3F, 1, 2, 2, NONE)
                .texOffs(28, 119).addBox(-8.1F, 0F, -4F, 1, 1, 1, NONE)
                .texOffs(32, 119).addBox(7.1F, -3F, 4F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 16.2F, 1.5F, -0.244F, 0F, 0F));
        PartDefinition p_head = p_body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 42).addBox(-6.5F, -4F, -10F, 13, 4, 10, NONE)
                .texOffs(46, 80).addBox(-5.5F, -4.8F, -9F, 11, 1, 8, NONE)
                .texOffs(0, 80).addBox(-7F, -1F, -9.5F, 14, 1, 9, NONE)
                .texOffs(36, 119).addBox(-2F, -3.5F, -10.5F, 1, 1, 1, NONE)
                .texOffs(40, 119).addBox(1F, -3.5F, -10.5F, 1, 1, 1, NONE)
                .texOffs(0, 119).addBox(-6.5F, -0.2F, -10.2F, 13, 1, 1, NONE)
                .texOffs(88, 112).addBox(-7.2F, -3F, -3F, 1, 2, 2, NONE)
                .texOffs(94, 112).addBox(6.2F, -3F, -3F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, -1F, -6F, 0.175F, 0F, 0F));
        PartDefinition p_jaw = p_head.addOrReplaceChild("jaw", CubeListBuilder.create()
                .texOffs(20, 56).addBox(-6.5F, 0F, -9.5F, 13, 2, 10, NONE)
                .texOffs(84, 80).addBox(-5.5F, -0.6F, -9F, 11, 1, 8, NONE)
                .texOffs(50, 99).addBox(-2F, -1F, -7.5F, 4, 1, 5, NONE)
                .texOffs(0, 69).addBox(-5F, 2F, -8.5F, 10, 4, 7, NONE)
                .texOffs(24, 99).addBox(-4F, 5.5F, -7.5F, 8, 1, 5, NONE)
                .texOffs(0, 112).addBox(-6F, 1.5F, -2F, 12, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, 0F, 1F, 0F, 0F, 0F));
        PartDefinition p_eye_l = p_head.addOrReplaceChild("eye_l", CubeListBuilder.create()
                .texOffs(88, 90).addBox(-2F, -2.5F, -2F, 4, 3, 4, NONE)
                .texOffs(56, 112).addBox(-1.5F, -2.5F, -2.4F, 3, 2, 2, NONE)
                .texOffs(28, 112).addBox(-2.2F, -3.2F, -1.5F, 4, 1, 3, NONE),
                PartPose.offsetAndRotation(3.8F, -4F, -5F, 0F, 0F, 0F));
        PartDefinition p_eye_r = p_head.addOrReplaceChild("eye_r", CubeListBuilder.create()
                .texOffs(104, 90).addBox(-2F, -2.5F, -2F, 4, 3, 4, NONE)
                .texOffs(66, 112).addBox(-1.5F, -2.5F, -2.4F, 3, 2, 2, NONE)
                .texOffs(42, 112).addBox(-1.8F, -3.2F, -1.5F, 4, 1, 3, NONE),
                PartPose.offsetAndRotation(-3.8F, -4F, -5F, 0F, 0F, 0F));
        PartDefinition p_fleg_l = p_body.addOrReplaceChild("fleg_l", CubeListBuilder.create()
                .texOffs(0, 90).addBox(-1.5F, -1F, -1.5F, 3, 6, 3, NONE)
                .texOffs(100, 99).addBox(-1.8F, -1.5F, -1.8F, 3, 3, 3, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(6F, 2.5F, -4.5F, 0.419F, 0F, -0.244F));
        PartDefinition p_ffoot_l = p_fleg_l.addOrReplaceChild("ffoot_l", CubeListBuilder.create()
                .texOffs(16, 106).addBox(-2F, 0F, -3F, 4, 1, 4, NONE)
                .texOffs(8, 116).addBox(-2.5F, 0.2F, -4F, 1, 1, 2, NONE)
                .texOffs(14, 116).addBox(-0.5F, 0.2F, -4.5F, 1, 1, 2, NONE)
                .texOffs(20, 116).addBox(1.5F, 0.2F, -4F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, 5.2F, 0F, -0.175F, 0F, 0.244F));
        PartDefinition p_fleg_r = p_body.addOrReplaceChild("fleg_r", CubeListBuilder.create()
                .texOffs(12, 90).addBox(-1.5F, -1F, -1.5F, 3, 6, 3, NONE)
                .texOffs(112, 99).addBox(-1.2F, -1.5F, -1.8F, 3, 3, 3, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(-6F, 2.5F, -4.5F, 0.419F, 0F, 0.244F));
        PartDefinition p_ffoot_r = p_fleg_r.addOrReplaceChild("ffoot_r", CubeListBuilder.create()
                .texOffs(32, 106).addBox(-2F, 0F, -3F, 4, 1, 4, NONE)
                .texOffs(26, 116).addBox(1.5F, 0.2F, -4F, 1, 1, 2, NONE)
                .texOffs(32, 116).addBox(-0.5F, 0.2F, -4.5F, 1, 1, 2, NONE)
                .texOffs(38, 116).addBox(-2.5F, 0.2F, -4F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, 5.2F, 0F, -0.175F, 0F, -0.244F));
        PartDefinition p_hleg_l = p_body.addOrReplaceChild("hleg_l", CubeListBuilder.create()
                .texOffs(34, 69).addBox(-2.5F, -2F, -4F, 5, 4, 7, NONE)
                .texOffs(94, 42).addBox(-1.5F, -3F, -4F, 3, 6, 7, NONE)
                .texOffs(46, 42).addBox(-1.5F, -2F, -5F, 3, 4, 9, NONE)
                .texOffs(116, 112).addBox(-1F, -3.4F, -3F, 2, 1, 2, NONE)
                .texOffs(44, 119).addBox(0F, -3.4F, 1F, 1, 1, 1, NONE)
                .texOffs(44, 116).addBox(2.1F, -1.5F, -2.5F, 1, 1, 2, NONE)
                .texOffs(48, 119).addBox(2.1F, 0.5F, 1F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(7F, 1.5F, 3F, 0.244F, -0.209F, 0F));
        PartDefinition p_hshin_l = p_hleg_l.addOrReplaceChild("hshin_l", CubeListBuilder.create()
                .texOffs(0, 99).addBox(-1.5F, 0F, -1.5F, 3, 4, 3, NONE),
                PartPose.offsetAndRotation(0F, 2F, -4F, -0.611F, 0F, 0F));
        PartDefinition p_hfoot_l = p_hshin_l.addOrReplaceChild("hfoot_l", CubeListBuilder.create()
                .texOffs(36, 90).addBox(-2F, 0F, -6F, 4, 1, 7, NONE)
                .texOffs(50, 116).addBox(-2.5F, 0.2F, -7F, 1, 1, 2, NONE)
                .texOffs(56, 116).addBox(-0.5F, 0.2F, -7.5F, 1, 1, 2, NONE)
                .texOffs(62, 116).addBox(1.5F, 0.2F, -7F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, 3.5F, 0F, 0.611F, 0F, 0F));
        PartDefinition p_hleg_r = p_body.addOrReplaceChild("hleg_r", CubeListBuilder.create()
                .texOffs(58, 69).addBox(-2.5F, -2F, -4F, 5, 4, 7, NONE)
                .texOffs(0, 56).addBox(-1.5F, -3F, -4F, 3, 6, 7, NONE)
                .texOffs(70, 42).addBox(-1.5F, -2F, -5F, 3, 4, 9, NONE)
                .texOffs(0, 116).addBox(-1F, -3.4F, -3F, 2, 1, 2, NONE)
                .texOffs(52, 119).addBox(-1F, -3.4F, 1F, 1, 1, 1, NONE)
                .texOffs(68, 116).addBox(-3.1F, -1.5F, -2.5F, 1, 1, 2, NONE)
                .texOffs(56, 119).addBox(-3.1F, 0.5F, 1F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-7F, 1.5F, 3F, 0.244F, 0.209F, 0F));
        PartDefinition p_hshin_r = p_hleg_r.addOrReplaceChild("hshin_r", CubeListBuilder.create()
                .texOffs(12, 99).addBox(-1.5F, 0F, -1.5F, 3, 4, 3, NONE),
                PartPose.offsetAndRotation(0F, 2F, -4F, -0.611F, 0F, 0F));
        PartDefinition p_hfoot_r = p_hshin_r.addOrReplaceChild("hfoot_r", CubeListBuilder.create()
                .texOffs(58, 90).addBox(-2F, 0F, -6F, 4, 1, 7, NONE)
                .texOffs(74, 116).addBox(1.5F, 0.2F, -7F, 1, 1, 2, NONE)
                .texOffs(80, 116).addBox(-0.5F, 0.2F, -7.5F, 1, 1, 2, NONE)
                .texOffs(86, 116).addBox(-2.5F, 0.2F, -7F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, 3.5F, 0F, 0.611F, 0F, 0F));
        PartDefinition p_crys_1 = p_body.addOrReplaceChild("crys_1", CubeListBuilder.create()
                .texOffs(82, 69).addBox(-1.5F, -8F, -1.5F, 3, 8, 3, NONE)
                .texOffs(92, 116).addBox(-0.5F, -9.5F, -0.5F, 1, 2, 1, NONE)
                .texOffs(68, 99).addBox(-2F, -1F, -2F, 4, 2, 4, NONE),
                PartPose.offsetAndRotation(-2F, -4.5F, -1F, -0.209F, 0F, -0.279F));
        PartDefinition p_crys_2 = p_body.addOrReplaceChild("crys_2", CubeListBuilder.create()
                .texOffs(80, 90).addBox(-1F, -6F, -1F, 2, 6, 2, NONE)
                .texOffs(96, 116).addBox(-0.5F, -7.5F, -0.5F, 1, 2, 1, NONE)
                .texOffs(48, 106).addBox(-1.5F, -1F, -1.5F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(2.5F, -4.5F, 0.5F, 0.209F, 0F, 0.419F));
        PartDefinition p_crys_3 = p_body.addOrReplaceChild("crys_3", CubeListBuilder.create()
                .texOffs(24, 90).addBox(-1.5F, -6F, -1.5F, 3, 6, 3, NONE)
                .texOffs(100, 116).addBox(-0.5F, -7.5F, -0.5F, 1, 2, 1, NONE)
                .texOffs(84, 99).addBox(-2F, -1F, -2F, 4, 2, 4, NONE),
                PartPose.offsetAndRotation(-0.5F, -4.5F, 4F, 0.663F, 0F, -0.105F));
        PartDefinition p_crys_4 = p_body.addOrReplaceChild("crys_4", CubeListBuilder.create()
                .texOffs(0, 106).addBox(-1F, -4F, -1F, 2, 4, 2, NONE)
                .texOffs(104, 116).addBox(-0.5F, -5.5F, -0.5F, 1, 2, 1, NONE)
                .texOffs(60, 106).addBox(-1.5F, -1F, -1.5F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(4.5F, -4.5F, 4.5F, 0.524F, 0F, 0.698F));
        PartDefinition p_crys_5 = p_body.addOrReplaceChild("crys_5", CubeListBuilder.create()
                .texOffs(8, 106).addBox(-1F, -4F, -1F, 2, 4, 2, NONE)
                .texOffs(108, 116).addBox(-0.5F, -5.5F, -0.5F, 1, 2, 1, NONE)
                .texOffs(72, 106).addBox(-1.5F, -1F, -1.5F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(-5F, -4F, 1.5F, 0.087F, 0F, -0.838F));
        PartDefinition p_crys_6 = p_body.addOrReplaceChild("crys_6", CubeListBuilder.create()
                .texOffs(108, 106).addBox(-1F, -3F, -1F, 2, 3, 2, NONE)
                .texOffs(112, 116).addBox(-0.5F, -4.5F, -0.5F, 1, 2, 1, NONE)
                .texOffs(84, 106).addBox(-1.5F, -1F, -1.5F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(0.5F, -4.5F, -4.5F, -0.524F, 0F, 0.14F));
        PartDefinition p_crys_7 = p_body.addOrReplaceChild("crys_7", CubeListBuilder.create()
                .texOffs(116, 106).addBox(-1F, -3F, -1F, 2, 3, 2, NONE)
                .texOffs(116, 116).addBox(-0.5F, -4.5F, -0.5F, 1, 2, 1, NONE)
                .texOffs(96, 106).addBox(-1.5F, -1F, -1.5F, 3, 2, 3, NONE),
                PartPose.offsetAndRotation(-3.5F, -3.5F, 6.5F, 0.96F, 0F, -0.524F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final Role[] SCHRAB_SPITTER_ROLES = {new Role("body", "body", 0.00F), new Role("body/head", "head", 0.00F), new Role("body/head/jaw", "jaw", 0.00F), new Role("body/fleg_l", "leg_a", 0.00F), new Role("body/fleg_r", "leg_b", 0.00F), new Role("body/hleg_l", "leg_b", 0.00F), new Role("body/hleg_r", "leg_a", 0.00F)};

    public static final Key[] SCHRAB_SPITTER_POSES = {
            new Key(2, 0F, "body/head", 0F, 0F, 0F),
            new Key(2, 6F, "body/head", -28F, 0F, 0F),
            new Key(2, 8F, "body/head", 18F, 0F, 0F),
            new Key(2, 11F, "body/head", 10F, 0F, 0F),
            new Key(2, 14F, "body/head", 0F, 0F, 0F),
            new Key(2, 0F, "body/head/jaw", 0F, 0F, 0F),
            new Key(2, 6F, "body/head/jaw", 8F, 0F, 0F),
            new Key(2, 8F, "body/head/jaw", 50F, 0F, 0F),
            new Key(2, 11F, "body/head/jaw", 40F, 0F, 0F),
            new Key(2, 14F, "body/head/jaw", 0F, 0F, 0F),
            new Key(2, 0F, "body", 0F, 0F, 0F),
            new Key(2, 6F, "body", -10F, 0F, 0F),
            new Key(2, 8F, "body", 8F, 0F, 0F),
            new Key(2, 14F, "body", 0F, 0F, 0F),
            new Key(2, 0F, "body/fleg_r", 0F, 0F, 0F),
            new Key(2, 6F, "body/fleg_r", 10F, 0F, 0F),
            new Key(2, 8F, "body/fleg_r", -8F, 0F, 0F),
            new Key(2, 14F, "body/fleg_r", 0F, 0F, 0F),
            new Key(2, 0F, "body/fleg_l", 0F, 0F, 0F),
            new Key(2, 6F, "body/fleg_l", 10F, 0F, 0F),
            new Key(2, 8F, "body/fleg_l", -8F, 0F, 0F),
            new Key(2, 14F, "body/fleg_l", 0F, 0F, 0F)};

    public static final float SCHRAB_SPITTER_SCALE = 1.15F;

    /** Deep Angler: Tiefsee-Anglerfisch mit Unterbiss-Maul voller Nadelzaehne, Leuchtangel ueber dem Kopf, Brustflossen, Biolumineszenzpunkten und Schwanzflosse. Textur {@code entity/dungeon/deep_angler.png}. */
    public static LayerDefinition deepAngler() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 19).addBox(-6F, -5F, -8F, 12, 6, 11, NONE)
                .texOffs(46, 0).addBox(-5F, -6F, -8F, 10, 8, 11, NONE)
                .texOffs(0, 0).addBox(-5F, -5F, -9F, 10, 6, 13, NONE)
                .texOffs(42, 50).addBox(-5F, -6.6F, -7F, 10, 1, 10, NONE)
                .texOffs(58, 61).addBox(-6.5F, -4F, -5F, 13, 3, 6, NONE)
                .texOffs(24, 77).addBox(-6.6F, -2.5F, -3.5F, 1, 2, 3, NONE)
                .texOffs(32, 77).addBox(5.6F, -2.5F, -3.5F, 1, 2, 3, NONE)
                .texOffs(84, 77).addBox(-6.5F, -4.5F, -7.8F, 2, 2, 2, NONE)
                .texOffs(92, 77).addBox(4.5F, -4.5F, -7.8F, 2, 2, 2, NONE)
                .texOffs(0, 50).addBox(-5.5F, 1.5F, -8F, 11, 1, 10, NONE)
                .texOffs(40, 82).addBox(-4F, -2F, -9.5F, 8, 2, 1, NONE)
                .texOffs(38, 86).addBox(-6.6F, 0F, 0F, 1, 1, 1, NONE)
                .texOffs(42, 86).addBox(5.6F, 0F, 0F, 1, 1, 1, NONE)
                .texOffs(46, 86).addBox(-6.6F, -3F, 2F, 1, 1, 1, NONE)
                .texOffs(50, 86).addBox(5.6F, -3F, 2F, 1, 1, 1, NONE)
                .texOffs(0, 82).addBox(-5.5F, 2F, -9F, 1, 3, 1, NONE)
                .texOffs(74, 82).addBox(-3.5F, 2F, -9F, 1, 2, 1, NONE)
                .texOffs(4, 82).addBox(-1.5F, 2F, -9F, 1, 3, 1, NONE)
                .texOffs(8, 82).addBox(0.5F, 2F, -9F, 1, 3, 1, NONE)
                .texOffs(78, 82).addBox(2.5F, 2F, -9F, 1, 2, 1, NONE)
                .texOffs(12, 82).addBox(4.5F, 2F, -9F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 16.5F, 0F, 0F, 0F, 0F));
        PartDefinition p_jaw = p_body.addOrReplaceChild("jaw", CubeListBuilder.create()
                .texOffs(66, 36).addBox(-6.5F, 1F, -10F, 13, 3, 9, NONE)
                .texOffs(0, 36).addBox(-5.5F, 0F, -10F, 11, 5, 9, NONE)
                .texOffs(54, 19).addBox(-5.5F, 1F, -11F, 11, 3, 11, NONE)
                .texOffs(0, 61).addBox(-5.5F, -0.5F, -10F, 11, 1, 9, NONE)
                .texOffs(82, 50).addBox(-5.5F, 4F, -10F, 11, 2, 9, NONE)
                .texOffs(58, 82).addBox(-1F, 5.5F, -11F, 2, 1, 2, NONE)
                .texOffs(16, 82).addBox(-5.5F, -3F, -11.2F, 1, 3, 1, NONE)
                .texOffs(46, 77).addBox(-3.5F, -4F, -11.2F, 1, 4, 1, NONE)
                .texOffs(20, 82).addBox(-1.5F, -3F, -11.2F, 1, 3, 1, NONE)
                .texOffs(24, 82).addBox(0.5F, -3F, -11.2F, 1, 3, 1, NONE)
                .texOffs(50, 77).addBox(2.5F, -4F, -11.2F, 1, 4, 1, NONE)
                .texOffs(28, 82).addBox(4.5F, -3F, -11.2F, 1, 3, 1, NONE)
                .texOffs(82, 82).addBox(-6.2F, -2F, -8F, 1, 2, 1, NONE)
                .texOffs(86, 82).addBox(5.2F, -2F, -8F, 1, 2, 1, NONE)
                .texOffs(90, 82).addBox(-6.2F, -2F, -5F, 1, 2, 1, NONE)
                .texOffs(94, 82).addBox(5.2F, -2F, -5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 1.5F, 0F, 0.105F, 0F, 0F));
        PartDefinition p_lure = p_body.addOrReplaceChild("lure", CubeListBuilder.create()
                .texOffs(96, 61).addBox(-1F, -5F, -1F, 2, 5, 2, NONE)
                .texOffs(72, 77).addBox(-1.5F, -1F, -1.5F, 3, 1, 3, NONE)
                .texOffs(98, 82).addBox(-0.5F, -3.5F, 1F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -6F, -4F, -0.349F, 0F, 0F));
        PartDefinition p_lure_tip = p_lure.addOrReplaceChild("lure_tip", CubeListBuilder.create()
                .texOffs(54, 77).addBox(-0.5F, -4F, -0.5F, 1, 4, 1, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(0F, -5F, 0F, 0.96F, 0F, 0F));
        PartDefinition p_lure_bulb = p_lure_tip.addOrReplaceChild("lure_bulb", CubeListBuilder.create()
                .texOffs(22, 71).addBox(-1.5F, -3F, -1.5F, 3, 3, 3, new CubeDeformation(0.3F))
                .texOffs(66, 82).addBox(-1F, -3.8F, -1F, 2, 1, 2, NONE)
                .texOffs(102, 82).addBox(-0.5F, 0F, -0.5F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, -4F, 0F, -1.047F, 0F, 0F));
        PartDefinition p_fin_l = p_body.addOrReplaceChild("fin_l", CubeListBuilder.create()
                .texOffs(104, 61).addBox(0F, -0.5F, 0F, 6, 1, 5, NONE)
                .texOffs(106, 82).addBox(0F, -0.8F, 0F, 6, 1, 1, new CubeDeformation(0.1F))
                .texOffs(14, 86).addBox(0F, -0.8F, 2.5F, 5, 1, 1, NONE)
                .texOffs(92, 71).addBox(5F, -0.5F, 1F, 2, 1, 4, NONE)
                .texOffs(100, 77).addBox(-0.5F, -1F, -0.5F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(5.5F, 1F, -1F, 0F, 0.524F, 0.349F));
        PartDefinition p_fin_r = p_body.addOrReplaceChild("fin_r", CubeListBuilder.create()
                .texOffs(0, 71).addBox(-6F, -0.5F, 0F, 6, 1, 5, NONE)
                .texOffs(0, 86).addBox(-6F, -0.8F, 0F, 6, 1, 1, new CubeDeformation(0.1F))
                .texOffs(26, 86).addBox(-5F, -0.8F, 2.5F, 5, 1, 1, NONE)
                .texOffs(104, 71).addBox(-7F, -0.5F, 1F, 2, 1, 4, NONE)
                .texOffs(108, 77).addBox(-1.5F, -1F, -0.5F, 2, 2, 2, NONE),
                PartPose.offsetAndRotation(-5.5F, 1F, -1F, 0F, -0.524F, -0.349F));
        PartDefinition p_tail_1 = p_body.addOrReplaceChild("tail_1", CubeListBuilder.create()
                .texOffs(40, 36).addBox(-4F, -4.5F, 0F, 8, 9, 5, NONE)
                .texOffs(70, 71).addBox(-3.5F, 3.5F, 0.5F, 7, 1, 4, NONE)
                .texOffs(34, 71).addBox(-0.5F, -6.5F, 0.5F, 1, 2, 4, NONE)
                .texOffs(54, 86).addBox(-4.3F, -1F, 1F, 1, 1, 1, NONE)
                .texOffs(58, 86).addBox(3.3F, -1F, 1F, 1, 1, 1, NONE)
                .texOffs(62, 86).addBox(-4.3F, 1F, 3F, 1, 1, 1, NONE)
                .texOffs(66, 86).addBox(3.3F, 1F, 3F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -0.5F, 3.5F, 0F, 0F, 0F));
        PartDefinition p_tail_2 = p_tail_1.addOrReplaceChild("tail_2", CubeListBuilder.create()
                .texOffs(40, 61).addBox(-2.5F, -3F, 0F, 5, 6, 4, NONE)
                .texOffs(58, 77).addBox(-2F, 2.5F, 0.5F, 4, 1, 3, NONE)
                .texOffs(44, 71).addBox(-0.5F, -5F, 0F, 1, 2, 4, NONE)
                .texOffs(70, 86).addBox(-2.7F, -1F, 1F, 1, 1, 1, NONE)
                .texOffs(74, 86).addBox(1.7F, -1F, 1F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 5F, 0F, 0F, 0F));
        PartDefinition p_tail_fin = p_tail_2.addOrReplaceChild("tail_fin", CubeListBuilder.create()
                .texOffs(46, 19).addBox(-0.5F, -6F, 0F, 1, 12, 3, NONE)
                .texOffs(54, 71).addBox(-0.5F, -7F, 2F, 1, 3, 3, NONE)
                .texOffs(62, 71).addBox(-0.5F, 4F, 2F, 1, 3, 3, NONE)
                .texOffs(116, 71).addBox(-1F, -5.5F, -0.5F, 2, 1, 4, NONE)
                .texOffs(0, 77).addBox(-1F, 4.5F, -0.5F, 2, 1, 4, NONE)
                .texOffs(12, 77).addBox(-1F, -0.5F, 0F, 2, 1, 4, NONE)
                .texOffs(40, 77).addBox(-1F, -2F, -0.5F, 2, 4, 1, NONE),
                PartPose.offsetAndRotation(0F, 0F, 4F, 0F, 0F, 0F));
        PartDefinition p_spine_1 = p_body.addOrReplaceChild("spine_1", CubeListBuilder.create()
                .texOffs(32, 82).addBox(-0.5F, -3F, -0.5F, 1, 3, 1, NONE)
                .texOffs(116, 77).addBox(-0.5F, -3F, 0.5F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, -6F, -1F, -0.524F, 0F, 0F));
        PartDefinition p_spine_2 = p_body.addOrReplaceChild("spine_2", CubeListBuilder.create()
                .texOffs(36, 82).addBox(-0.5F, -3F, -0.5F, 1, 3, 1, NONE)
                .texOffs(122, 77).addBox(-0.5F, -3F, 0.5F, 1, 2, 2, NONE),
                PartPose.offsetAndRotation(0F, -6F, 1.5F, -0.698F, 0F, 0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final Role[] DEEP_ANGLER_ROLES = {new Role("body", "bob", 0.00F), new Role("body/jaw", "jaw", 0.00F), new Role("body/lure", "tentacle", 0.50F), new Role("body/lure/lure_tip", "tentacle", 1.50F), new Role("body/fin_l", "wing_l", 0.00F), new Role("body/fin_r", "wing_r", 0.00F), new Role("body/tail_1", "chain", 1.00F), new Role("body/tail_1/tail_2", "chain", 2.00F), new Role("body/tail_1/tail_2/tail_fin", "chain", 3.00F)};

    public static final Key[] DEEP_ANGLER_POSES = {
            new Key(1, 0F, "body/jaw", 0F, 0F, 0F),
            new Key(1, 3F, "body/jaw", 50F, 0F, 0F),
            new Key(1, 6F, "body/jaw", -4F, 0F, 0F),
            new Key(1, 10F, "body/jaw", 0F, 0F, 0F),
            new Key(1, 0F, "body", 0F, 0F, 0F),
            new Key(1, 3F, "body", -12F, 0F, 0F),
            new Key(1, 6F, "body", 10F, 0F, 0F),
            new Key(1, 10F, "body", 0F, 0F, 0F),
            new Key(1, 0F, "body/lure", 0F, 0F, 0F),
            new Key(1, 3F, "body/lure", -20F, 0F, 0F),
            new Key(1, 6F, "body/lure", 20F, 0F, 0F),
            new Key(1, 10F, "body/lure", 0F, 0F, 0F),
            new Key(1, 0F, "body/tail_1", 0F, 0F, 0F),
            new Key(1, 3F, "body/tail_1", 0F, 20F, 0F),
            new Key(1, 6F, "body/tail_1", 0F, -25F, 0F),
            new Key(1, 10F, "body/tail_1", 0F, 0F, 0F),
            new Key(1, 0F, "body/fin_r", 0F, 0F, 0F),
            new Key(1, 3F, "body/fin_r", 0F, 0F, 30F),
            new Key(1, 6F, "body/fin_r", 0F, 0F, -20F),
            new Key(1, 10F, "body/fin_r", 0F, 0F, 0F),
            new Key(1, 0F, "body/fin_l", 0F, 0F, 0F),
            new Key(1, 3F, "body/fin_l", 0F, 0F, -30F),
            new Key(1, 6F, "body/fin_l", 0F, 0F, 20F),
            new Key(1, 10F, "body/fin_l", 0F, 0F, 0F)};

    public static final float DEEP_ANGLER_SCALE = 1.0F;

    /** Abyss Diver: untoter Tiefseetaucher im Messing-Taucheranzug mit Bullaugenhelm (schwaches Leuchten innen), Bleischuhen, abgerissenem Luftschlauch und Druckluft-Harpune im linken Arm. Textur {@code entity/dungeon/abyss_diver.png}. */
    public static LayerDefinition abyssDiver() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.5F, -11F, -3F, 9, 11, 6, NONE)
                .texOffs(64, 33).addBox(-5.5F, -12F, -3.5F, 11, 3, 7, NONE)
                .texOffs(0, 44).addBox(-4.5F, -10F, -4F, 9, 2, 8, NONE)
                .texOffs(8, 95).addBox(-5.2F, -12.6F, -2.5F, 1, 1, 1, NONE)
                .texOffs(12, 95).addBox(4.2F, -12.6F, -2.5F, 1, 1, 1, NONE)
                .texOffs(16, 95).addBox(-5.2F, -12.6F, 1.5F, 1, 1, 1, NONE)
                .texOffs(20, 95).addBox(4.2F, -12.6F, 1.5F, 1, 1, 1, NONE)
                .texOffs(46, 81).addBox(-3F, -8F, -4F, 6, 4, 1, NONE)
                .texOffs(112, 92).addBox(-2F, -7.5F, -4.4F, 4, 1, 1, NONE)
                .texOffs(0, 81).addBox(-3F, -8F, 3F, 6, 5, 1, NONE)
                .texOffs(42, 54).addBox(-5F, -3F, -3.5F, 10, 2, 7, NONE)
                .texOffs(48, 92).addBox(-1F, -3F, -3.9F, 2, 2, 1, NONE)
                .texOffs(112, 81).addBox(3.6F, -2F, -2F, 2, 4, 1, NONE)
                .texOffs(60, 92).addBox(4F, 1.5F, -1.9F, 1, 2, 1, NONE)
                .texOffs(68, 64).addBox(-4.8F, -1F, -3.2F, 9, 2, 6, new CubeDeformation(0.1F))
                .texOffs(64, 92).addBox(-4.7F, -6F, -2F, 1, 2, 1, NONE)
                .texOffs(68, 92).addBox(1F, -1F, -3.4F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 13F, 0F, 0F, 0F, 0F));
        PartDefinition p_head = p_body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(24, 17).addBox(-5F, -9F, -3F, 10, 6, 6, NONE)
                .texOffs(0, 17).addBox(-3F, -11F, -3F, 6, 10, 6, NONE)
                .texOffs(30, 0).addBox(-3F, -9F, -5F, 6, 6, 10, NONE)
                .texOffs(62, 0).addBox(-4F, -10F, -4F, 8, 8, 8, NONE)
                .texOffs(80, 17).addBox(-4.5F, -2F, -4.5F, 9, 2, 9, NONE)
                .texOffs(84, 92).addBox(-3F, -9F, -6F, 6, 1, 1, NONE)
                .texOffs(98, 92).addBox(-3F, -4F, -6F, 6, 1, 1, NONE)
                .texOffs(118, 81).addBox(-3F, -8F, -6F, 1, 4, 1, NONE)
                .texOffs(122, 81).addBox(2F, -8F, -6F, 1, 4, 1, NONE)
                .texOffs(84, 81).addBox(-2F, -8F, -5.5F, 4, 4, 1, NONE)
                .texOffs(0, 87).addBox(-0.5F, -8F, -5.75F, 1, 4, 1, NONE)
                .texOffs(24, 95).addBox(-1.8F, -6.6F, -5.65F, 1, 1, 1, NONE)
                .texOffs(28, 95).addBox(0.8F, -6.6F, -5.65F, 1, 1, 1, NONE)
                .texOffs(114, 64).addBox(-5.7F, -8F, -2F, 1, 4, 4, NONE)
                .texOffs(104, 87).addBox(-6F, -7F, -1F, 1, 2, 2, NONE)
                .texOffs(0, 73).addBox(4.7F, -8F, -2F, 1, 4, 4, NONE)
                .texOffs(110, 87).addBox(5F, -7F, -1F, 1, 2, 2, NONE)
                .texOffs(4, 87).addBox(-1.5F, -12F, -2F, 3, 1, 3, NONE)
                .texOffs(24, 92).addBox(-1F, -12.3F, -1.5F, 2, 1, 2, NONE)
                .texOffs(72, 87).addBox(3F, -4F, -4.2F, 2, 2, 2, NONE)
                .texOffs(32, 95).addBox(3.5F, -3.6F, -4.8F, 1, 1, 1, NONE)
                .texOffs(94, 81).addBox(-1.5F, -7.5F, 4.6F, 3, 3, 2, NONE)
                .texOffs(32, 92).addBox(-4F, -10.6F, 1F, 2, 1, 2, NONE)
                .texOffs(40, 92).addBox(1.5F, -10.4F, -4F, 2, 1, 2, NONE)
                .texOffs(54, 92).addBox(-5.3F, -3F, 2F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, -11F, 0F, 0F, 0F, 0F));
        PartDefinition p_hose1 = p_head.addOrReplaceChild("hose1", CubeListBuilder.create()
                .texOffs(60, 73).addBox(-1F, 0F, -1F, 2, 5, 2, NONE)
                .texOffs(16, 87).addBox(-1.4F, 2F, -1.4F, 3, 1, 3, new CubeDeformation(-0.1F)),
                PartPose.offsetAndRotation(0F, -6F, 6F, 0.698F, 0F, 0F));
        PartDefinition p_hose2 = p_hose1.addOrReplaceChild("hose2", CubeListBuilder.create()
                .texOffs(68, 73).addBox(-1F, 0F, -1F, 2, 5, 2, NONE)
                .texOffs(28, 87).addBox(-1.3F, 1F, -1.3F, 3, 1, 3, new CubeDeformation(-0.1F))
                .texOffs(122, 87).addBox(0.8F, 2F, 0F, 1, 3, 1, NONE),
                PartPose.offsetAndRotation(0F, 5F, 0F, -0.314F, 0F, 0F));
        PartDefinition p_hose3 = p_hose2.addOrReplaceChild("hose3", CubeListBuilder.create()
                .texOffs(76, 73).addBox(-1F, 0F, -1F, 2, 5, 2, NONE)
                .texOffs(40, 87).addBox(-1.4F, 4F, -1.4F, 3, 1, 3, NONE)
                .texOffs(72, 92).addBox(-1F, 5F, -1F, 1, 2, 1, NONE)
                .texOffs(36, 95).addBox(0F, 5F, 0F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 5F, 0F, -0.209F, 0F, 0F));
        PartDefinition p_arm_r = p_body.addOrReplaceChild("arm_r", CubeListBuilder.create()
                .texOffs(28, 64).addBox(-2.5F, -2F, -2.5F, 5, 4, 5, NONE)
                .texOffs(0, 33).addBox(-2F, 1F, -2F, 4, 7, 4, NONE)
                .texOffs(84, 73).addBox(-2.3F, 3.5F, -2.3F, 4, 2, 4, new CubeDeformation(0.25F))
                .texOffs(14, 81).addBox(-2.2F, 7F, -2.2F, 4, 1, 4, new CubeDeformation(0.3F))
                .texOffs(60, 81).addBox(-1.5F, 8F, -1.5F, 3, 2, 3, NONE)
                .texOffs(76, 92).addBox(-1.5F, 10F, -1.5F, 1, 2, 1, NONE)
                .texOffs(80, 92).addBox(0.5F, 10F, -1.5F, 1, 2, 1, NONE)
                .texOffs(40, 95).addBox(-0.5F, 10F, 0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-6F, -9F, 0F, -0.314F, 0F, 0.105F));
        PartDefinition p_arm_l = p_body.addOrReplaceChild("arm_l", CubeListBuilder.create()
                .texOffs(48, 64).addBox(-2.5F, -2F, -2.5F, 5, 4, 5, NONE)
                .texOffs(16, 33).addBox(-2F, 1F, -2F, 4, 7, 4, NONE)
                .texOffs(100, 73).addBox(-2.3F, 3.5F, -2.3F, 4, 2, 4, new CubeDeformation(0.25F))
                .texOffs(30, 81).addBox(-2.2F, 7F, -2.2F, 4, 1, 4, new CubeDeformation(0.3F))
                .texOffs(72, 81).addBox(-1.5F, 8F, -1.5F, 3, 2, 3, NONE)
                .texOffs(10, 73).addBox(2.2F, 0F, -1F, 1, 6, 2, NONE),
                PartPose.offsetAndRotation(6F, -9F, 0F, -1.309F, 0F, -0.07F));
        PartDefinition p_harpoon = p_arm_l.addOrReplaceChild("harpoon", CubeListBuilder.create()
                .texOffs(22, 54).addBox(-1.5F, -1F, -2F, 3, 3, 7, NONE)
                .texOffs(104, 81).addBox(-1F, 2F, -1F, 2, 3, 2, NONE)
                .texOffs(56, 17).addBox(-1F, -1.5F, -12F, 2, 2, 10, NONE)
                .texOffs(80, 87).addBox(-1.5F, -2F, -9F, 3, 3, 1, NONE)
                .texOffs(88, 87).addBox(-1.5F, -2F, -12.5F, 3, 3, 1, NONE)
                .texOffs(98, 64).addBox(-1F, 0.5F, -9F, 2, 2, 6, NONE)
                .texOffs(116, 87).addBox(1F, -1.5F, -6F, 1, 2, 2, NONE)
                .texOffs(94, 0).addBox(-0.5F, -2.8F, -15F, 1, 1, 15, NONE)
                .texOffs(96, 87).addBox(-1F, -3.3F, -17F, 2, 2, 2, NONE)
                .texOffs(0, 95).addBox(-1.5F, -2.8F, -15.5F, 3, 1, 1, NONE)
                .texOffs(44, 95).addBox(-0.5F, -3.5F, -2F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 9F, -0.5F, 1.309F, 0F, 0F));
        PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create()
                .texOffs(32, 33).addBox(-2F, 0F, -2F, 4, 7, 4, NONE)
                .texOffs(52, 87).addBox(-2.3F, 3F, -2.4F, 4, 3, 1, NONE)
                .texOffs(90, 44).addBox(-2.6F, 6F, -3F, 5, 4, 6, NONE)
                .texOffs(16, 73).addBox(-2.8F, 6F, -3.2F, 5, 1, 6, new CubeDeformation(0.1F))
                .texOffs(34, 44).addBox(-3F, 9F, -4.5F, 6, 2, 8, NONE)
                .texOffs(0, 92).addBox(-2.5F, 9.2F, -5F, 5, 2, 1, NONE)
                .texOffs(76, 54).addBox(-3.2F, 10.6F, -4.8F, 6, 1, 8, new CubeDeformation(0.15F)),
                PartPose.offsetAndRotation(-2.2F, 13F, 0F, 0F, 0F, 0F));
        PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create()
                .texOffs(48, 33).addBox(-2F, 0F, -2F, 4, 7, 4, NONE)
                .texOffs(62, 87).addBox(-1.7F, 3F, -2.4F, 4, 3, 1, NONE)
                .texOffs(0, 54).addBox(-2.4F, 6F, -3F, 5, 4, 6, NONE)
                .texOffs(38, 73).addBox(-2.2F, 6F, -3.2F, 5, 1, 6, new CubeDeformation(0.1F))
                .texOffs(62, 44).addBox(-3F, 9F, -4.5F, 6, 2, 8, NONE)
                .texOffs(12, 92).addBox(-2.5F, 9.2F, -5F, 5, 2, 1, NONE)
                .texOffs(0, 64).addBox(-2.8F, 10.6F, -4.8F, 6, 1, 8, new CubeDeformation(0.15F)),
                PartPose.offsetAndRotation(2.2F, 13F, 0F, 0F, 0F, 0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final Role[] ABYSS_DIVER_ROLES = {new Role("body", "body", 0.00F), new Role("body/head", "head", 0.00F), new Role("body/head/hose1", "chain", 0.00F), new Role("body/head/hose1/hose2", "chain", 1.00F), new Role("body/head/hose1/hose2/hose3", "chain", 2.00F), new Role("body/arm_r", "arm_r", 0.00F), new Role("body/arm_l", "arm_l", 0.00F)};

    public static final Key[] ABYSS_DIVER_POSES = {
            new Key(2, 0F, "body/arm_l", 0F, 0F, 0F),
            new Key(2, 5F, "body/arm_l", -12F, -6F, 0F),
            new Key(2, 8F, "body/arm_l", -12F, -6F, 0F),
            new Key(2, 9F, "body/arm_l", -38F, -6F, 0F),
            new Key(2, 14F, "body/arm_l", 0F, 0F, 0F),
            new Key(2, 0F, "body/arm_l/harpoon", 0F, 0F, 0F),
            new Key(2, 5F, "body/arm_l/harpoon", 0F, 0F, 0F),
            new Key(2, 9F, "body/arm_l/harpoon", -10F, 0F, 0F),
            new Key(2, 14F, "body/arm_l/harpoon", 0F, 0F, 0F),
            new Key(2, 0F, "body", 0F, 0F, 0F),
            new Key(2, 5F, "body", 0F, -18F, 0F),
            new Key(2, 8F, "body", 0F, -18F, 0F),
            new Key(2, 9F, "body", -8F, -18F, 0F),
            new Key(2, 14F, "body", 0F, 0F, 0F),
            new Key(2, 0F, "body/head", 0F, 0F, 0F),
            new Key(2, 5F, "body/head", 6F, 12F, 0F),
            new Key(2, 8F, "body/head", 6F, 12F, 0F),
            new Key(2, 14F, "body/head", 0F, 0F, 0F),
            new Key(2, 0F, "body/arm_r", 0F, 0F, 0F),
            new Key(2, 5F, "body/arm_r", -50F, 0F, -20F),
            new Key(2, 8F, "body/arm_r", -50F, 0F, -20F),
            new Key(2, 14F, "body/arm_r", 0F, 0F, 0F)};

    public static final float ABYSS_DIVER_SCALE = 1.0F;

    /** Polymer Ooze: zaeher milchweiss-limettengruener Polymerschleim mit leuchtendem Kern, eingeschlossenen Rohrstuecken und Plastikteilen, Laufnasen und tastenden Pseudopodien; teilt sich bei Treffern. Textur {@code entity/dungeon/polymer_ooze.png}. */
    public static LayerDefinition polymerOoze() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(44, 22).addBox(-11F, -2F, -7F, 21, 2, 15, NONE)
                .texOffs(0, 0).addBox(-8F, -2F, -10F, 17, 2, 20, NONE)
                .texOffs(0, 98).addBox(-13F, -1F, 2F, 4, 1, 5, NONE)
                .texOffs(82, 98).addBox(9F, -1F, -9F, 4, 1, 4, NONE)
                .texOffs(0, 42).addBox(-8F, -9F, -5F, 16, 6, 10, NONE)
                .texOffs(0, 22).addBox(-6F, -11F, -5F, 12, 10, 10, NONE)
                .texOffs(74, 0).addBox(-6F, -9F, -7F, 12, 6, 14, NONE)
                .texOffs(64, 81).addBox(-10.5F, -6F, -7F, 8, 3, 5, NONE)
                .texOffs(110, 58).addBox(-8.5F, -8F, -7F, 4, 7, 5, NONE)
                .texOffs(34, 58).addBox(-8.5F, -6F, -9F, 4, 3, 9, NONE)
                .texOffs(52, 70).addBox(2F, -7F, 2F, 9, 4, 5, NONE)
                .texOffs(80, 42).addBox(4F, -9F, 2F, 5, 8, 5, NONE)
                .texOffs(52, 42).addBox(4F, -7F, 0F, 5, 4, 9, NONE)
                .texOffs(62, 98).addBox(2F, -11F, -6F, 7, 2, 3, NONE)
                .texOffs(18, 81).addBox(4F, -13F, -6F, 3, 6, 3, NONE)
                .texOffs(80, 70).addBox(4F, -11F, -8F, 3, 2, 7, NONE)
                .texOffs(30, 81).addBox(-7F, -13F, -2F, 11, 2, 6, NONE)
                .texOffs(60, 58).addBox(-5F, -15F, -2F, 7, 6, 6, NONE)
                .texOffs(0, 58).addBox(-5F, -13F, -4F, 7, 2, 10, NONE)
                .texOffs(98, 90).addBox(-4F, -17F, 0F, 6, 2, 4, NONE)
                .texOffs(110, 81).addBox(-3F, -18F, 0F, 4, 4, 4, NONE)
                .texOffs(90, 81).addBox(-3F, -17F, -1F, 4, 2, 6, NONE)
                .texOffs(102, 109).addBox(-2.5F, -19F, 0.5F, 2, 1, 2, NONE)
                .texOffs(100, 70).addBox(-2.5F, -9F, -8.2F, 5, 5, 4, NONE)
                .texOffs(72, 104).addBox(-1.5F, -10F, -7.6F, 3, 1, 3, NONE)
                .texOffs(58, 109).addBox(-4.5F, -7F, -7.4F, 2, 3, 1, NONE)
                .texOffs(0, 113).addBox(2.5F, -10F, -7.4F, 2, 2, 1, NONE)
                .texOffs(58, 113).addBox(-1F, -4F, -7.4F, 3, 1, 1, NONE)
                .texOffs(48, 113).addBox(-3F, -6.5F, -8.5F, 4, 1, 1, NONE)
                .texOffs(66, 113).addBox(1F, -9.5F, -8.4F, 2, 1, 1, NONE)
                .texOffs(46, 98).addBox(-8.4F, -9F, -3F, 1, 3, 3, NONE)
                .texOffs(42, 104).addBox(10.4F, -6F, 3F, 1, 2, 3, NONE)
                .texOffs(110, 109).addBox(-2F, -14.5F, 5.4F, 3, 2, 1, NONE)
                .texOffs(78, 113).addBox(4F, -6F, -7.4F, 1, 1, 1, NONE)
                .texOffs(82, 113).addBox(-7F, -5F, -9.4F, 1, 1, 1, NONE)
                .texOffs(86, 113).addBox(6F, -3F, -7.4F, 1, 1, 1, NONE)
                .texOffs(90, 113).addBox(-5F, -14F, -4.4F, 1, 1, 1, NONE)
                .texOffs(94, 113).addBox(10.4F, -4F, 5F, 1, 1, 1, NONE)
                .texOffs(98, 113).addBox(-8.4F, -5F, 2F, 1, 1, 1, NONE)
                .texOffs(118, 109).addBox(5F, -4F, -7.6F, 3, 2, 1, NONE)
                .texOffs(34, 98).addBox(-8.6F, -10F, 2F, 2, 2, 4, NONE)
                .texOffs(0, 109).addBox(-12F, -3F, -5F, 3, 2, 2, NONE)
                .texOffs(54, 98).addBox(-13F, -3.5F, -5.5F, 1, 3, 3, NONE)
                .texOffs(24, 113).addBox(-7F, -5F, -9.4F, 1, 2, 1, NONE)
                .texOffs(10, 109).addBox(-7.5F, -3F, -9.9F, 2, 2, 2, NONE)
                .texOffs(28, 113).addBox(4F, -7F, -8.3F, 1, 2, 1, NONE)
                .texOffs(18, 109).addBox(3.5F, -5F, -8.8F, 2, 2, 2, NONE)
                .texOffs(64, 109).addBox(10.4F, -8F, 6F, 1, 3, 1, NONE)
                .texOffs(26, 109).addBox(9.9F, -5F, 5.5F, 2, 2, 2, NONE)
                .texOffs(50, 104).addBox(-8.4F, -9F, 4F, 1, 4, 1, NONE)
                .texOffs(34, 109).addBox(-8.9F, -5F, 3.5F, 2, 2, 2, NONE)
                .texOffs(54, 104).addBox(3F, -11F, 7.4F, 1, 4, 1, NONE)
                .texOffs(42, 109).addBox(2.5F, -7F, 6.9F, 2, 2, 2, NONE)
                .texOffs(68, 109).addBox(-5F, -14F, 6.4F, 1, 3, 1, NONE)
                .texOffs(50, 109).addBox(-5.5F, -11F, 5.9F, 2, 2, 2, NONE)
                .texOffs(102, 113).addBox(-13.5F, -2F, 3.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
        PartDefinition p_pod_r = p_body.addOrReplaceChild("pod_r", CubeListBuilder.create()
                .texOffs(86, 58).addBox(-2F, -2F, -6F, 4, 4, 8, NONE)
                .texOffs(18, 98).addBox(-1.5F, -2.6F, -5F, 3, 1, 5, NONE)
                .texOffs(0, 90).addBox(-2.5F, -2.5F, -1F, 5, 5, 3, NONE)
                .texOffs(72, 109).addBox(-1.5F, 1.6F, -4F, 3, 1, 2, NONE),
                PartPose.offsetAndRotation(-7F, -5F, -6F, 0.384F, 0.611F, 0F));
        PartDefinition p_pod_r_tip = p_pod_r.addOrReplaceChild("pod_r_tip", CubeListBuilder.create()
                .texOffs(0, 81).addBox(-1F, -1F, -6F, 2, 2, 7, NONE)
                .texOffs(12, 104).addBox(-1.5F, -1.5F, -8F, 3, 3, 2, NONE)
                .texOffs(32, 113).addBox(-0.5F, 1F, -7.5F, 1, 2, 1, NONE)
                .texOffs(6, 113).addBox(-0.5F, -1.4F, -5F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, 0F, -6F, -0.314F, 0F, 0F));
        PartDefinition p_pod_l = p_body.addOrReplaceChild("pod_l", CubeListBuilder.create()
                .texOffs(0, 70).addBox(-2F, -2F, -5F, 4, 4, 7, NONE)
                .texOffs(98, 98).addBox(-1.5F, -2.6F, -4F, 3, 1, 4, NONE)
                .texOffs(16, 90).addBox(-2.5F, -2.5F, -1F, 5, 5, 3, NONE)
                .texOffs(82, 109).addBox(-1.5F, 1.6F, -3F, 3, 1, 2, NONE),
                PartPose.offsetAndRotation(7F, -6F, -5F, 0.419F, -0.698F, 0F));
        PartDefinition p_pod_l_tip = p_pod_l.addOrReplaceChild("pod_l_tip", CubeListBuilder.create()
                .texOffs(32, 90).addBox(-1F, -1F, -5F, 2, 2, 6, NONE)
                .texOffs(22, 104).addBox(-1.5F, -1.5F, -7F, 3, 3, 2, NONE)
                .texOffs(36, 113).addBox(-0.5F, 1F, -6.5F, 1, 2, 1, NONE)
                .texOffs(12, 113).addBox(-0.5F, -1.4F, -4F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, 0F, -5F, -0.314F, 0F, 0F));
        PartDefinition p_pod_b = p_body.addOrReplaceChild("pod_b", CubeListBuilder.create()
                .texOffs(22, 70).addBox(-2F, -2F, -5F, 4, 4, 7, NONE)
                .texOffs(112, 98).addBox(-1.5F, -2.6F, -4F, 3, 1, 4, NONE)
                .texOffs(48, 90).addBox(-2.5F, -2.5F, -1F, 5, 5, 3, NONE)
                .texOffs(92, 109).addBox(-1.5F, 1.6F, -3F, 3, 1, 2, NONE),
                PartPose.offsetAndRotation(7F, -4F, 6F, 0.279F, -2.443F, 0F));
        PartDefinition p_pod_b_tip = p_pod_b.addOrReplaceChild("pod_b_tip", CubeListBuilder.create()
                .texOffs(64, 90).addBox(-1F, -1F, -5F, 2, 2, 6, NONE)
                .texOffs(32, 104).addBox(-1.5F, -1.5F, -7F, 3, 3, 2, NONE)
                .texOffs(40, 113).addBox(-0.5F, 1F, -6.5F, 1, 2, 1, NONE)
                .texOffs(18, 113).addBox(-0.5F, -1.4F, -4F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, 0F, -5F, -0.314F, 0F, 0F));
        PartDefinition p_junk_pipe = p_body.addOrReplaceChild("junk_pipe", CubeListBuilder.create()
                .texOffs(44, 70).addBox(-1F, -7F, -1F, 2, 8, 2, NONE)
                .texOffs(84, 104).addBox(-1.5F, -8F, -1.5F, 3, 1, 3, NONE)
                .texOffs(96, 104).addBox(-1.5F, -3F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(-4F, -12F, -1F, 0.175F, 0F, 0.524F));
        PartDefinition p_junk_elbow = p_body.addOrReplaceChild("junk_elbow", CubeListBuilder.create()
                .texOffs(58, 104).addBox(0F, -1F, -1F, 5, 2, 2, NONE)
                .texOffs(90, 90).addBox(4F, -4F, -1F, 2, 5, 2, NONE)
                .texOffs(108, 104).addBox(3.5F, -5F, -1.5F, 3, 1, 3, NONE),
                PartPose.offsetAndRotation(8F, -7F, 2F, 0F, 0F, -0.175F));
        PartDefinition p_junk_shard = p_body.addOrReplaceChild("junk_shard", CubeListBuilder.create()
                .texOffs(80, 90).addBox(-2F, -5F, -0.5F, 4, 6, 1, NONE)
                .texOffs(72, 113).addBox(-1F, -6F, -0.5F, 2, 1, 1, NONE),
                PartPose.offsetAndRotation(4F, -14F, 2F, -0.349F, 0.436F, -0.436F));
        PartDefinition p_junk_sign = p_body.addOrReplaceChild("junk_sign", CubeListBuilder.create()
                .texOffs(0, 104).addBox(-3F, -3F, 0F, 5, 4, 1, NONE)
                .texOffs(44, 113).addBox(-0.5F, 1F, 0F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(-6F, -6F, 6F, 0.262F, -0.349F, 0.349F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final Role[] POLYMER_OOZE_ROLES = {new Role("body", "body", 0.00F), new Role("body/pod_r", "tentacle", 0.00F), new Role("body/pod_r/pod_r_tip", "tentacle", 0.50F), new Role("body/pod_l", "tentacle", 2.00F), new Role("body/pod_l/pod_l_tip", "tentacle", 2.50F), new Role("body/pod_b", "tentacle", 4.00F), new Role("body/pod_b/pod_b_tip", "tentacle", 4.50F)};

    public static final Key[] POLYMER_OOZE_POSES = {
            new Key(1, 0F, "body", 0F, 0F, 0F),
            new Key(1, 4F, "body", -10F, 0F, 0F),
            new Key(1, 7F, "body", 14F, 0F, 0F),
            new Key(1, 10F, "body", 0F, 0F, 0F),
            new Key(1, 0F, "body/pod_r", 0F, 0F, 0F),
            new Key(1, 4F, "body/pod_r", -45F, 15F, 0F),
            new Key(1, 7F, "body/pod_r", 30F, -10F, 0F),
            new Key(1, 10F, "body/pod_r", 0F, 0F, 0F),
            new Key(1, 0F, "body/pod_l", 0F, 0F, 0F),
            new Key(1, 4F, "body/pod_l", -45F, -15F, 0F),
            new Key(1, 7F, "body/pod_l", 30F, 10F, 0F),
            new Key(1, 10F, "body/pod_l", 0F, 0F, 0F),
            new Key(1, 0F, "body/pod_r/pod_r_tip", 0F, 0F, 0F),
            new Key(1, 4F, "body/pod_r/pod_r_tip", -20F, 0F, 0F),
            new Key(1, 7F, "body/pod_r/pod_r_tip", 25F, 0F, 0F),
            new Key(1, 10F, "body/pod_r/pod_r_tip", 0F, 0F, 0F),
            new Key(1, 0F, "body/pod_l/pod_l_tip", 0F, 0F, 0F),
            new Key(1, 4F, "body/pod_l/pod_l_tip", -20F, 0F, 0F),
            new Key(1, 7F, "body/pod_l/pod_l_tip", 25F, 0F, 0F),
            new Key(1, 10F, "body/pod_l/pod_l_tip", 0F, 0F, 0F)};

    public static final float POLYMER_OOZE_SCALE = 1.0F;

    /** Refinery Drone: Quadrokopter der Raffinerie mit gelb-schwarzen Warnstreifen, rotem Brennstofftank, Sensorkopf und Flammenwerfer-Duese mit Glut unter der Nase. Textur {@code entity/dungeon/refinery_drone.png}. */
    public static LayerDefinition refineryDrone() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_drone = root.addOrReplaceChild("drone", CubeListBuilder.create()
                .texOffs(76, 0).addBox(-5F, -2F, -5F, 10, 4, 10, NONE)
                .texOffs(40, 0).addBox(-4F, -3F, -5F, 8, 6, 10, NONE)
                .texOffs(0, 0).addBox(-4F, -2F, -6F, 8, 4, 12, NONE)
                .texOffs(0, 28).addBox(-4F, -4F, -5F, 8, 1, 9, NONE)
                .texOffs(0, 16).addBox(-5.3F, 0F, -5.3F, 11, 1, 11, NONE)
                .texOffs(74, 60).addBox(-3F, -2F, -6.5F, 6, 2, 1, NONE)
                .texOffs(110, 64).addBox(-3F, 0.5F, -6.6F, 6, 1, 1, NONE)
                .texOffs(100, 38).addBox(-5.4F, -2F, -3F, 1, 2, 6, NONE)
                .texOffs(114, 38).addBox(4.4F, -2F, -3F, 1, 2, 6, NONE)
                .texOffs(0, 47).addBox(-2F, 3F, -3F, 4, 1, 6, NONE)
                .texOffs(34, 28).addBox(-4.5F, -9F, -1F, 9, 4, 6, NONE)
                .texOffs(64, 28).addBox(-4.5F, -10F, 0F, 9, 6, 4, NONE)
                .texOffs(36, 60).addBox(-5F, -8F, 1F, 1, 2, 2, NONE)
                .texOffs(42, 60).addBox(4F, -8F, 1F, 1, 2, 2, NONE)
                .texOffs(44, 16).addBox(-2.5F, -10F, -1F, 1, 6, 6, new CubeDeformation(0.2F))
                .texOffs(58, 16).addBox(1.5F, -10F, -1F, 1, 6, 6, new CubeDeformation(0.2F))
                .texOffs(116, 54).addBox(-1.5F, -10.4F, 0.5F, 3, 1, 3, NONE)
                .texOffs(104, 67).addBox(-0.5F, -11.4F, 1.5F, 1, 1, 1, NONE)
                .texOffs(98, 60).addBox(-1F, -12F, 1F, 2, 1, 2, NONE)
                .texOffs(48, 60).addBox(4.6F, -7.5F, 1F, 1, 2, 2, NONE)
                .texOffs(86, 47).addBox(-5.5F, -4F, 3F, 1, 6, 1, NONE)
                .texOffs(0, 38).addBox(-5.5F, 1F, -4F, 1, 1, 8, NONE)
                .texOffs(96, 67).addBox(-5F, 2F, -5F, 3, 1, 1, NONE)
                .texOffs(20, 60).addBox(-2.5F, -1.5F, 6F, 2, 2, 2, NONE)
                .texOffs(28, 60).addBox(0.5F, -1.5F, 6F, 2, 2, 2, NONE)
                .texOffs(108, 67).addBox(-2F, -1F, 7.6F, 1, 1, 1, NONE)
                .texOffs(112, 67).addBox(1F, -1F, 7.6F, 1, 1, 1, NONE)
                .texOffs(54, 60).addBox(-4F, 3F, -3F, 1, 3, 1, NONE)
                .texOffs(58, 60).addBox(3F, 3F, -3F, 1, 3, 1, NONE)
                .texOffs(62, 60).addBox(-4F, 3F, 3F, 1, 3, 1, NONE)
                .texOffs(66, 60).addBox(3F, 3F, 3F, 1, 3, 1, NONE)
                .texOffs(72, 16).addBox(-4.5F, 6F, -5F, 2, 1, 10, NONE)
                .texOffs(96, 16).addBox(2.5F, 6F, -5F, 2, 1, 10, NONE),
                PartPose.offsetAndRotation(0F, 17F, 0F, 0F, 0F, 0F));
        PartDefinition p_head = p_drone.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(20, 47).addBox(-2.5F, -3F, -3F, 5, 3, 4, NONE)
                .texOffs(90, 47).addBox(-3F, -3.5F, -3.4F, 6, 1, 5, NONE)
                .texOffs(88, 60).addBox(-2F, -2.4F, -3.5F, 4, 2, 1, NONE)
                .texOffs(70, 60).addBox(2F, -6F, 0F, 1, 3, 1, NONE)
                .texOffs(106, 60).addBox(1.5F, -7F, -0.5F, 2, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, -4F, -3.5F, 0F, 0F, 0F));
        PartDefinition p_nozzle = p_drone.addOrReplaceChild("nozzle", CubeListBuilder.create()
                .texOffs(18, 38).addBox(-2.5F, -1.5F, -2F, 5, 4, 4, NONE)
                .texOffs(108, 28).addBox(-1.5F, -0.5F, -9F, 3, 2, 7, NONE)
                .texOffs(90, 28).addBox(-1F, -1F, -9F, 2, 3, 7, NONE)
                .texOffs(0, 60).addBox(-2F, -1F, -5F, 4, 3, 1, NONE)
                .texOffs(10, 60).addBox(-2F, -1F, -7F, 4, 3, 1, NONE)
                .texOffs(112, 47).addBox(-2F, -1.5F, -11F, 4, 4, 2, NONE)
                .texOffs(56, 64).addBox(-1F, -0.5F, -11.4F, 2, 2, 1, NONE)
                .texOffs(116, 67).addBox(-0.5F, 2.5F, -10.5F, 1, 1, 1, NONE)
                .texOffs(0, 54).addBox(-0.5F, 2F, -6F, 1, 1, 5, NONE),
                PartPose.offsetAndRotation(0F, 2.5F, -4F, 0.209F, 0F, 0F));
        PartDefinition p_arm_fr = p_drone.addOrReplaceChild("arm_fr", CubeListBuilder.create()
                .texOffs(36, 38).addBox(-1F, -1F, -10F, 2, 2, 6, NONE)
                .texOffs(76, 54).addBox(-0.5F, 1F, -9F, 1, 1, 4, NONE)
                .texOffs(38, 47).addBox(-1.5F, -3F, -12.5F, 3, 4, 3, NONE)
                .texOffs(12, 54).addBox(-2F, -1.5F, -13F, 4, 1, 4, NONE)
                .texOffs(114, 60).addBox(-1F, -4F, -12F, 2, 1, 2, NONE)
                .texOffs(120, 67).addBox(-0.5F, 0.5F, -13.4F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -2F, 0F, 0F, 0.785F, 0F));
        PartDefinition p_arm_fr_rotor = p_arm_fr.addOrReplaceChild("arm_fr_rotor", CubeListBuilder.create()
                .texOffs(0, 64).addBox(-1F, -1F, -1F, 2, 1, 2, NONE)
                .texOffs(0, 67).addBox(-6F, -0.5F, -0.5F, 5, 1, 1, NONE)
                .texOffs(12, 67).addBox(1F, -0.5F, -0.5F, 5, 1, 1, NONE)
                .texOffs(62, 64).addBox(-6.5F, -0.5F, -0.8F, 1, 1, 2, NONE)
                .texOffs(68, 64).addBox(5.5F, -0.5F, -0.8F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, -4.5F, -11F, 0F, 0.524F, 0.07F));
        PartDefinition p_arm_fl = p_drone.addOrReplaceChild("arm_fl", CubeListBuilder.create()
                .texOffs(52, 38).addBox(-1F, -1F, -10F, 2, 2, 6, NONE)
                .texOffs(86, 54).addBox(-0.5F, 1F, -9F, 1, 1, 4, NONE)
                .texOffs(50, 47).addBox(-1.5F, -3F, -12.5F, 3, 4, 3, NONE)
                .texOffs(28, 54).addBox(-2F, -1.5F, -13F, 4, 1, 4, NONE)
                .texOffs(8, 64).addBox(-1F, -4F, -12F, 2, 1, 2, NONE)
                .texOffs(124, 67).addBox(-0.5F, 0.5F, -13.4F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -2F, 0F, 0F, -0.785F, 0F));
        PartDefinition p_arm_fl_rotor = p_arm_fl.addOrReplaceChild("arm_fl_rotor", CubeListBuilder.create()
                .texOffs(16, 64).addBox(-1F, -1F, -1F, 2, 1, 2, NONE)
                .texOffs(24, 67).addBox(-6F, -0.5F, -0.5F, 5, 1, 1, NONE)
                .texOffs(36, 67).addBox(1F, -0.5F, -0.5F, 5, 1, 1, NONE)
                .texOffs(74, 64).addBox(-6.5F, -0.5F, -0.8F, 1, 1, 2, NONE)
                .texOffs(80, 64).addBox(5.5F, -0.5F, -0.8F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, -4.5F, -11F, 0F, 1.169F, 0.07F));
        PartDefinition p_arm_br = p_drone.addOrReplaceChild("arm_br", CubeListBuilder.create()
                .texOffs(68, 38).addBox(-1F, -1F, -10F, 2, 2, 6, NONE)
                .texOffs(96, 54).addBox(-0.5F, 1F, -9F, 1, 1, 4, NONE)
                .texOffs(62, 47).addBox(-1.5F, -3F, -12.5F, 3, 4, 3, NONE)
                .texOffs(44, 54).addBox(-2F, -1.5F, -13F, 4, 1, 4, NONE)
                .texOffs(24, 64).addBox(-1F, -4F, -12F, 2, 1, 2, NONE)
                .texOffs(0, 69).addBox(-0.5F, 0.5F, -13.4F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -2F, 0F, 0F, 2.356F, 0F));
        PartDefinition p_arm_br_rotor = p_arm_br.addOrReplaceChild("arm_br_rotor", CubeListBuilder.create()
                .texOffs(32, 64).addBox(-1F, -1F, -1F, 2, 1, 2, NONE)
                .texOffs(48, 67).addBox(-6F, -0.5F, -0.5F, 5, 1, 1, NONE)
                .texOffs(60, 67).addBox(1F, -0.5F, -0.5F, 5, 1, 1, NONE)
                .texOffs(86, 64).addBox(-6.5F, -0.5F, -0.8F, 1, 1, 2, NONE)
                .texOffs(92, 64).addBox(5.5F, -0.5F, -0.8F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, -4.5F, -11F, 0F, 1.815F, 0.07F));
        PartDefinition p_arm_bl = p_drone.addOrReplaceChild("arm_bl", CubeListBuilder.create()
                .texOffs(84, 38).addBox(-1F, -1F, -10F, 2, 2, 6, NONE)
                .texOffs(106, 54).addBox(-0.5F, 1F, -9F, 1, 1, 4, NONE)
                .texOffs(74, 47).addBox(-1.5F, -3F, -12.5F, 3, 4, 3, NONE)
                .texOffs(60, 54).addBox(-2F, -1.5F, -13F, 4, 1, 4, NONE)
                .texOffs(40, 64).addBox(-1F, -4F, -12F, 2, 1, 2, NONE)
                .texOffs(4, 69).addBox(-0.5F, 0.5F, -13.4F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -2F, 0F, 0F, -2.356F, 0F));
        PartDefinition p_arm_bl_rotor = p_arm_bl.addOrReplaceChild("arm_bl_rotor", CubeListBuilder.create()
                .texOffs(48, 64).addBox(-1F, -1F, -1F, 2, 1, 2, NONE)
                .texOffs(72, 67).addBox(-6F, -0.5F, -0.5F, 5, 1, 1, NONE)
                .texOffs(84, 67).addBox(1F, -0.5F, -0.5F, 5, 1, 1, NONE)
                .texOffs(98, 64).addBox(-6.5F, -0.5F, -0.8F, 1, 1, 2, NONE)
                .texOffs(104, 64).addBox(5.5F, -0.5F, -0.8F, 1, 1, 2, NONE),
                PartPose.offsetAndRotation(0F, -4.5F, -11F, 0F, 2.461F, 0.07F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final Role[] REFINERY_DRONE_ROLES = {new Role("drone", "bob", 0.00F), new Role("drone/head", "head", 0.00F)};

    public static final Key[] REFINERY_DRONE_POSES = {
            new Key(2, 0F, "drone", 0F, 0F, 0F),
            new Key(2, 4F, "drone", -12F, 0F, 0F),
            new Key(2, 7F, "drone", 18F, 0F, 0F),
            new Key(2, 11F, "drone", 18F, 0F, 0F),
            new Key(2, 14F, "drone", 0F, 0F, 0F),
            new Key(2, 0F, "drone/nozzle", 0F, 0F, 0F),
            new Key(2, 4F, "drone/nozzle", -8F, 0F, 0F),
            new Key(2, 7F, "drone/nozzle", 14F, 0F, 0F),
            new Key(2, 11F, "drone/nozzle", 14F, 0F, 0F),
            new Key(2, 14F, "drone/nozzle", 0F, 0F, 0F),
            new Key(2, 0F, "drone/head", 0F, 0F, 0F),
            new Key(2, 4F, "drone/head", -10F, 0F, 0F),
            new Key(2, 7F, "drone/head", 6F, 0F, 0F),
            new Key(2, 14F, "drone/head", 0F, 0F, 0F)};

    public static final float REFINERY_DRONE_SCALE = 0.8F;

    /** Hive Wasp: pilzbefallene Riesenwespe mit dunkelrotem Chitin, Facettenaugen, vier Fluegeln, Giftstachel und Pilzhueten, Myzel und leuchtendem Cordyceps-Stiel auf Ruecken und Hinterleib. Textur {@code entity/dungeon/hive_wasp.png}. */
    public static LayerDefinition hiveWasp() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(44, 0).addBox(-3F, -2F, -2F, 6, 4, 6, NONE)
                .texOffs(24, 0).addBox(-2F, -3F, -2F, 4, 6, 6, NONE)
                .texOffs(0, 0).addBox(-2F, -2F, -3F, 4, 4, 8, NONE)
                .texOffs(56, 21).addBox(-2.5F, -3.6F, -2F, 5, 1, 5, NONE)
                .texOffs(106, 21).addBox(-3.4F, -1F, -1.5F, 1, 2, 4, NONE)
                .texOffs(116, 21).addBox(2.4F, -1F, -1.5F, 1, 2, 4, NONE)
                .texOffs(76, 21).addBox(-1.5F, 2.5F, -2F, 3, 1, 5, NONE)
                .texOffs(30, 39).addBox(-1F, -1F, 4.5F, 2, 2, 2, NONE)
                .texOffs(16, 43).addBox(-2F, -4.2F, 0F, 2, 1, 2, NONE)
                .texOffs(24, 43).addBox(0.5F, -4F, 2.5F, 2, 1, 2, NONE)
                .texOffs(86, 47).addBox(-2.5F, -4.6F, 1F, 1, 1, 1, NONE)
                .texOffs(90, 47).addBox(1.5F, -4.5F, 3F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 14F, 0F, 0F, 0F, 0F));
        PartDefinition p_head = p_body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(40, 12).addBox(-3.5F, -2.5F, -4F, 7, 5, 3, NONE)
                .texOffs(88, 0).addBox(-2.5F, -3.5F, -4F, 5, 7, 3, NONE)
                .texOffs(68, 0).addBox(-2.5F, -2.5F, -5F, 5, 5, 5, NONE)
                .texOffs(16, 12).addBox(-4.4F, -3F, -4.5F, 2, 5, 4, NONE)
                .texOffs(28, 12).addBox(2.4F, -3F, -4.5F, 2, 5, 4, NONE)
                .texOffs(74, 47).addBox(-1F, -4.1F, -3.5F, 2, 1, 1, NONE)
                .texOffs(66, 47).addBox(-1.5F, -3.8F, -2.5F, 3, 1, 1, NONE)
                .texOffs(0, 39).addBox(-2F, -1.5F, -5.6F, 4, 3, 1, NONE)
                .texOffs(80, 47).addBox(-1F, -2.5F, -5.4F, 2, 1, 1, NONE)
                .texOffs(110, 34).addBox(-2F, 2.5F, -4.5F, 4, 1, 3, NONE)
                .texOffs(32, 43).addBox(1.5F, -3.9F, -1.5F, 2, 1, 2, NONE)
                .texOffs(94, 47).addBox(1.8F, -4.7F, -1.1F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, -1F, -3F, 0.07F, 0F, 0F));
        PartDefinition p_antenna_r = p_head.addOrReplaceChild("antenna_r", CubeListBuilder.create()
                .texOffs(0, 28).addBox(-0.5F, -5F, -0.5F, 1, 5, 1, NONE)
                .texOffs(38, 39).addBox(-0.5F, -6F, -2.5F, 1, 1, 3, NONE),
                PartPose.offsetAndRotation(-1.2F, -3F, -4.5F, 0.698F, 0.436F, 0F));
        PartDefinition p_antenna_l = p_head.addOrReplaceChild("antenna_l", CubeListBuilder.create()
                .texOffs(4, 28).addBox(-0.5F, -5F, -0.5F, 1, 5, 1, NONE)
                .texOffs(46, 39).addBox(-0.5F, -6F, -2.5F, 1, 1, 3, NONE),
                PartPose.offsetAndRotation(1.2F, -3F, -4.5F, 0.698F, -0.436F, 0F));
        PartDefinition p_mandible_r = p_head.addOrReplaceChild("mandible_r", CubeListBuilder.create()
                .texOffs(110, 39).addBox(-1F, 0F, -2F, 1, 2, 2, NONE)
                .texOffs(98, 47).addBox(-0.5F, 1F, -3F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-1.5F, 1.5F, -5F, 0F, 0.262F, 0F));
        PartDefinition p_mandible_l = p_head.addOrReplaceChild("mandible_l", CubeListBuilder.create()
                .texOffs(116, 39).addBox(0F, 0F, -2F, 1, 2, 2, NONE)
                .texOffs(102, 47).addBox(-0.5F, 1F, -3F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(1.5F, 1.5F, -5F, 0F, -0.262F, 0F));
        PartDefinition p_abdomen = p_body.addOrReplaceChild("abdomen", CubeListBuilder.create()
                .texOffs(88, 28).addBox(-3F, -2F, 1F, 6, 4, 1, NONE)
                .texOffs(42, 21).addBox(-2F, -3F, 1F, 4, 6, 1, NONE)
                .texOffs(16, 21).addBox(-2F, -2F, 0F, 4, 4, 3, NONE)
                .texOffs(78, 12).addBox(-4F, -2.5F, 4F, 8, 5, 2, NONE)
                .texOffs(0, 12).addBox(-3F, -3.5F, 4F, 6, 7, 2, NONE)
                .texOffs(104, 0).addBox(-3F, -2.5F, 3F, 6, 5, 4, NONE)
                .texOffs(72, 28).addBox(-3.5F, -2F, 8F, 7, 4, 1, NONE)
                .texOffs(30, 21).addBox(-2.5F, -3F, 8F, 5, 6, 1, NONE)
                .texOffs(98, 12).addBox(-2.5F, -2F, 7F, 5, 4, 3, NONE)
                .texOffs(0, 21).addBox(-3.5F, -3F, 2.5F, 7, 6, 1, new CubeDeformation(0.25F))
                .texOffs(60, 12).addBox(-4F, -3.5F, 6.5F, 8, 7, 1, new CubeDeformation(0.15F))
                .texOffs(92, 21).addBox(-2.5F, -2F, 10F, 5, 4, 2, NONE)
                .texOffs(54, 39).addBox(-1.5F, -1F, 11.5F, 3, 3, 1, NONE)
                .texOffs(8, 34).addBox(-4.3F, -1F, 4F, 1, 3, 2, NONE)
                .texOffs(106, 47).addBox(-4.4F, 0F, 4.6F, 1, 1, 1, NONE)
                .texOffs(122, 39).addBox(2.8F, -2F, 8F, 1, 2, 2, NONE)
                .texOffs(6, 43).addBox(-1F, 3.2F, 5F, 3, 1, 2, NONE)
                .texOffs(10, 39).addBox(-4.6F, -2.5F, 5F, 2, 1, 3, NONE)
                .texOffs(0, 43).addBox(-5F, -2F, 5.5F, 1, 2, 2, NONE)
                .texOffs(20, 39).addBox(3.4F, 0F, 3.5F, 2, 1, 3, NONE),
                PartPose.offsetAndRotation(0F, 0.5F, 5F, -0.489F, 0F, 0F));
        PartDefinition p_stinger = p_abdomen.addOrReplaceChild("stinger", CubeListBuilder.create()
                .texOffs(62, 39).addBox(-1F, -1F, -0.5F, 2, 2, 2, NONE)
                .texOffs(70, 39).addBox(-0.5F, -0.5F, 1.5F, 1, 1, 3, NONE)
                .texOffs(110, 47).addBox(-0.5F, -0.3F, 4.4F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0F, 0.5F, 12.5F, 0.262F, 0F, 0F));
        PartDefinition p_shroom_a = p_abdomen.addOrReplaceChild("shroom_a", CubeListBuilder.create()
                .texOffs(56, 43).addBox(-0.5F, -2F, -0.5F, 1, 2, 1, NONE)
                .texOffs(40, 43).addBox(-1F, -3F, -1F, 2, 1, 2, NONE)
                .texOffs(102, 28).addBox(-1.5F, -5F, -1.5F, 3, 2, 3, NONE)
                .texOffs(114, 47).addBox(-0.5F, -5.6F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(1F, -3.5F, 4.5F, -0.175F, 0F, 0.349F));
        PartDefinition p_shroom_b = p_abdomen.addOrReplaceChild("shroom_b", CubeListBuilder.create()
                .texOffs(118, 47).addBox(-0.5F, -1F, -0.5F, 1, 1, 1, NONE)
                .texOffs(122, 47).addBox(-0.5F, -2F, -0.5F, 1, 1, 1, NONE)
                .texOffs(78, 39).addBox(-1F, -4F, -1F, 2, 2, 2, NONE)
                .texOffs(0, 49).addBox(-0.5F, -4.6F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-1.5F, -3.5F, 8F, 0.262F, 0F, -0.262F));
        PartDefinition p_wingf_r = p_body.addOrReplaceChild("wingf_r", CubeListBuilder.create()
                .texOffs(8, 28).addBox(-12F, -0.5F, -1F, 12, 1, 4, NONE)
                .texOffs(88, 43).addBox(-12F, -0.6F, -1.4F, 12, 1, 1, NONE)
                .texOffs(86, 39).addBox(-6F, -0.55F, 0F, 1, 1, 3, NONE),
                PartPose.offsetAndRotation(-2F, -3F, -1F, 0F, 0.384F, 0.384F));
        PartDefinition p_wingb_r = p_body.addOrReplaceChild("wingb_r", CubeListBuilder.create()
                .texOffs(62, 34).addBox(-9F, -0.5F, 0F, 9, 1, 3, NONE)
                .texOffs(26, 47).addBox(-9F, -0.6F, -0.3F, 9, 1, 1, NONE),
                PartPose.offsetAndRotation(-2F, -3F, 1.5F, 0F, 0.698F, 0.209F));
        PartDefinition p_wingf_l = p_body.addOrReplaceChild("wingf_l", CubeListBuilder.create()
                .texOffs(40, 28).addBox(0F, -0.5F, -1F, 12, 1, 4, NONE)
                .texOffs(0, 47).addBox(0F, -0.6F, -1.4F, 12, 1, 1, NONE)
                .texOffs(94, 39).addBox(5F, -0.55F, 0F, 1, 1, 3, NONE),
                PartPose.offsetAndRotation(2F, -3F, -1F, 0F, -0.384F, -0.384F));
        PartDefinition p_wingb_l = p_body.addOrReplaceChild("wingb_l", CubeListBuilder.create()
                .texOffs(86, 34).addBox(0F, -0.5F, 0F, 9, 1, 3, NONE)
                .texOffs(46, 47).addBox(0F, -0.6F, -0.3F, 9, 1, 1, NONE),
                PartPose.offsetAndRotation(2F, -3F, 1.5F, 0F, -0.698F, -0.209F));
        PartDefinition p_cordyceps = p_body.addOrReplaceChild("cordyceps", CubeListBuilder.create()
                .texOffs(52, 21).addBox(-0.5F, -6F, -0.5F, 1, 6, 1, NONE)
                .texOffs(0, 34).addBox(-1F, -9F, -1F, 2, 3, 2, NONE)
                .texOffs(4, 49).addBox(-0.5F, -10F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(0.5F, -3F, 1F, -0.262F, 0F, 0.175F));
        PartDefinition p_shroom_c = p_body.addOrReplaceChild("shroom_c", CubeListBuilder.create()
                .texOffs(60, 43).addBox(-0.5F, -2F, -0.5F, 1, 2, 1, NONE)
                .texOffs(48, 43).addBox(-1F, -3F, -1F, 2, 1, 2, NONE)
                .texOffs(114, 28).addBox(-1.5F, -5F, -1.5F, 3, 2, 3, NONE)
                .texOffs(8, 49).addBox(-0.5F, -5.6F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(-1.5F, -3.5F, -1F, -0.262F, 0F, -0.436F));
        PartDefinition p_shroom_d = p_body.addOrReplaceChild("shroom_d", CubeListBuilder.create()
                .texOffs(12, 49).addBox(-0.5F, -1F, -0.5F, 1, 1, 1, NONE)
                .texOffs(16, 49).addBox(-0.5F, -2F, -0.5F, 1, 1, 1, NONE)
                .texOffs(102, 39).addBox(-1F, -4F, -1F, 2, 2, 2, NONE)
                .texOffs(20, 49).addBox(-0.5F, -4.6F, -0.5F, 1, 1, 1, NONE),
                PartPose.offsetAndRotation(2F, -3.5F, 1.5F, 0.175F, 0F, 0.524F));
        PartDefinition p_leg0_r = p_body.addOrReplaceChild("leg0_r", CubeListBuilder.create()
                .texOffs(14, 34).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(-2.5F, 2.5F, -2F, 0.175F, 0F, 0.524F));
        PartDefinition p_leg0_r_tibia = p_leg0_r.addOrReplaceChild("leg0_r_tibia", CubeListBuilder.create()
                .texOffs(18, 34).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE)
                .texOffs(24, 49).addBox(-0.7F, 1F, -0.7F, 1, 1, 1, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(0F, 3F, 0F, -0.785F, 0F, 0F));
        PartDefinition p_leg0_r_tarsus = p_leg0_r_tibia.addOrReplaceChild("leg0_r_tarsus", CubeListBuilder.create()
                .texOffs(64, 43).addBox(-0.5F, 0F, -1F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 4F, 0F, -0.436F, 0F, 0F));
        PartDefinition p_leg0_l = p_body.addOrReplaceChild("leg0_l", CubeListBuilder.create()
                .texOffs(22, 34).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(2.5F, 2.5F, -2F, 0.175F, 0F, -0.524F));
        PartDefinition p_leg0_l_tibia = p_leg0_l.addOrReplaceChild("leg0_l_tibia", CubeListBuilder.create()
                .texOffs(26, 34).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE)
                .texOffs(28, 49).addBox(-0.3F, 1F, -0.7F, 1, 1, 1, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(0F, 3F, 0F, -0.785F, 0F, 0F));
        PartDefinition p_leg0_l_tarsus = p_leg0_l_tibia.addOrReplaceChild("leg0_l_tarsus", CubeListBuilder.create()
                .texOffs(68, 43).addBox(-0.5F, 0F, -1F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 4F, 0F, -0.436F, 0F, 0F));
        PartDefinition p_leg1_r = p_body.addOrReplaceChild("leg1_r", CubeListBuilder.create()
                .texOffs(30, 34).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(-2.5F, 2.5F, 0.5F, 0.436F, 0F, 0.524F));
        PartDefinition p_leg1_r_tibia = p_leg1_r.addOrReplaceChild("leg1_r_tibia", CubeListBuilder.create()
                .texOffs(34, 34).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE)
                .texOffs(32, 49).addBox(-0.7F, 1F, -0.7F, 1, 1, 1, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(0F, 3F, 0F, -0.785F, 0F, 0F));
        PartDefinition p_leg1_r_tarsus = p_leg1_r_tibia.addOrReplaceChild("leg1_r_tarsus", CubeListBuilder.create()
                .texOffs(72, 43).addBox(-0.5F, 0F, -1F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 4F, 0F, -0.436F, 0F, 0F));
        PartDefinition p_leg1_l = p_body.addOrReplaceChild("leg1_l", CubeListBuilder.create()
                .texOffs(38, 34).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(2.5F, 2.5F, 0.5F, 0.436F, 0F, -0.524F));
        PartDefinition p_leg1_l_tibia = p_leg1_l.addOrReplaceChild("leg1_l_tibia", CubeListBuilder.create()
                .texOffs(42, 34).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE)
                .texOffs(36, 49).addBox(-0.3F, 1F, -0.7F, 1, 1, 1, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(0F, 3F, 0F, -0.785F, 0F, 0F));
        PartDefinition p_leg1_l_tarsus = p_leg1_l_tibia.addOrReplaceChild("leg1_l_tarsus", CubeListBuilder.create()
                .texOffs(76, 43).addBox(-0.5F, 0F, -1F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 4F, 0F, -0.436F, 0F, 0F));
        PartDefinition p_leg2_r = p_body.addOrReplaceChild("leg2_r", CubeListBuilder.create()
                .texOffs(46, 34).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(-2.5F, 2.5F, 3F, 0.698F, 0F, 0.524F));
        PartDefinition p_leg2_r_tibia = p_leg2_r.addOrReplaceChild("leg2_r_tibia", CubeListBuilder.create()
                .texOffs(50, 34).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE)
                .texOffs(40, 49).addBox(-0.7F, 1F, -0.7F, 1, 1, 1, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(0F, 3F, 0F, -0.785F, 0F, 0F));
        PartDefinition p_leg2_r_tarsus = p_leg2_r_tibia.addOrReplaceChild("leg2_r_tarsus", CubeListBuilder.create()
                .texOffs(80, 43).addBox(-0.5F, 0F, -1F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 4F, 0F, -0.436F, 0F, 0F));
        PartDefinition p_leg2_l = p_body.addOrReplaceChild("leg2_l", CubeListBuilder.create()
                .texOffs(54, 34).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, NONE),
                PartPose.offsetAndRotation(2.5F, 2.5F, 3F, 0.698F, 0F, -0.524F));
        PartDefinition p_leg2_l_tibia = p_leg2_l.addOrReplaceChild("leg2_l_tibia", CubeListBuilder.create()
                .texOffs(58, 34).addBox(-0.5F, 0F, -0.5F, 1, 4, 1, NONE)
                .texOffs(44, 49).addBox(-0.3F, 1F, -0.7F, 1, 1, 1, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(0F, 3F, 0F, -0.785F, 0F, 0F));
        PartDefinition p_leg2_l_tarsus = p_leg2_l_tibia.addOrReplaceChild("leg2_l_tarsus", CubeListBuilder.create()
                .texOffs(84, 43).addBox(-0.5F, 0F, -1F, 1, 2, 1, NONE),
                PartPose.offsetAndRotation(0F, 4F, 0F, -0.436F, 0F, 0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static final Role[] HIVE_WASP_ROLES = {new Role("body", "bob", 0.00F), new Role("body/head", "head", 0.00F), new Role("body/head/antenna_r", "tentacle", 0.00F), new Role("body/head/antenna_l", "tentacle", 0.00F), new Role("body/wingf_r", "wing_r", 0.00F), new Role("body/wingb_r", "wing_r", 0.00F), new Role("body/wingf_l", "wing_l", 0.00F), new Role("body/wingb_l", "wing_l", 0.00F), new Role("body/cordyceps", "tentacle", 3.00F)};

    public static final Key[] HIVE_WASP_POSES = {
            new Key(1, 0F, "body/abdomen", 0F, 0F, 0F),
            new Key(1, 4F, "body/abdomen", -60F, 0F, 0F),
            new Key(1, 6F, "body/abdomen", -85F, 0F, 0F),
            new Key(1, 10F, "body/abdomen", 0F, 0F, 0F),
            new Key(1, 0F, "body/abdomen/stinger", 0F, 0F, 0F),
            new Key(1, 4F, "body/abdomen/stinger", -25F, 0F, 0F),
            new Key(1, 6F, "body/abdomen/stinger", -40F, 0F, 0F),
            new Key(1, 10F, "body/abdomen/stinger", 0F, 0F, 0F),
            new Key(1, 0F, "body", 0F, 0F, 0F),
            new Key(1, 4F, "body", -15F, 0F, 0F),
            new Key(1, 6F, "body", 5F, 0F, 0F),
            new Key(1, 10F, "body", 0F, 0F, 0F),
            new Key(1, 0F, "body/head", 0F, 0F, 0F),
            new Key(1, 4F, "body/head", 15F, 0F, 0F),
            new Key(1, 10F, "body/head", 0F, 0F, 0F)};

    public static final float HIVE_WASP_SCALE = 0.75F;
}
