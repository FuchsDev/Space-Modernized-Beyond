package com.space_beyond.machine.misc.client;

import org.joml.Matrix4f;

import com.hbm_m.interfaces.IEnergyConnector;
import com.hbm_m.platform.RenderHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Hilfen fuer die Maschinen-Renderer auf Raptors Renderpfad, die in Space Modernized privat sind
 * ({@code RenderSolarPanel.canConnect}, Lavaspiegel aus {@code RenderMagma.render}); gleiche Rechnung.
 */
public final class BeyondRenderUtil {
    private BeyondRenderUtil() {}

    /** {@code Library.canConnect}: Energie-Verbinder, der von dieser Seite aus verbinden will. */
    public static boolean canConnect(Level level, BlockPos pos, Direction side) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof IEnergyConnector c && c.canConnectEnergy(side);
    }

    /** Lavaspiegel (Original: {@code RenderBlocks.renderFaceYPos} 3x3 mit Lava-Icon). */
    public static void renderLava(PoseStack ps, MultiBufferSource buf, int light, float lavaHeight) {
        TextureAtlasSprite lava = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper()
                .getParticleIcon(Blocks.LAVA.defaultBlockState());
        VertexConsumer lvc = buf.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
        Matrix4f m = ps.last().pose();
        org.joml.Vector3f n = new org.joml.Vector3f(0F, 1F, 0F).mul(ps.last().normal());

        // RenderBlocks.renderFaceYPos mit Grenzen (0,0,0)-(1,lavaHeight,1) bei (ox-0,5, 0, oz-0,5)
        for (int ox = -1; ox <= 1; ox++) {
            for (int oz = -1; oz <= 1; oz++) {
                float minX = ox - 0.5F, maxX = ox + 0.5F, minZ = oz - 0.5F, maxZ = oz + 0.5F;
                vertex(lvc, m, n, maxX, lavaHeight, maxZ, lava.getU1(), lava.getV1(), light);
                vertex(lvc, m, n, maxX, lavaHeight, minZ, lava.getU1(), lava.getV0(), light);
                vertex(lvc, m, n, minX, lavaHeight, minZ, lava.getU0(), lava.getV0(), light);
                vertex(lvc, m, n, minX, lavaHeight, maxZ, lava.getU0(), lava.getV1(), light);
            }
        }
    }

    private static void vertex(VertexConsumer vc, Matrix4f m, org.joml.Vector3f n, float x, float y, float z, float u, float v, int light) {
        RenderHooks.vertexFull(vc, m, x, y, z, 255, 255, 255, 255, u, v, OverlayTexture.NO_OVERLAY, light, n.x, n.y, n.z);
    }
}
