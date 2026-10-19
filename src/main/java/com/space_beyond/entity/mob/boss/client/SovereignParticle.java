package com.space_beyond.entity.mob.boss.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

/**
 * Eigene Leuchtpartikel der Orbitalwaffen und des Bosses: vollhell (unabhaengig vom Licht), animierte Sprites,
 * schrumpfen und verblassen. Drei Arten ueber den Provider:
 * <ul>
 *   <li>{@code sovereign_spark}: violetter Kristallfunke, folgt der uebergebenen Geschwindigkeit, Reibung 0,9.</li>
 *   <li>{@code gold_ember}: Goldglut, steigt erst und faellt dann langsam, flackert.</li>
 *   <li>{@code void_mote}: dunkler Leerepartikel mit lila Rand, wird zur Mitte gezogen (Geschwindigkeit = Ziel-Offset).</li>
 * </ul>
 */
public class SovereignParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final int kind;
    private final float baseSize;

    protected SovereignParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites, int kind) {
        super(level, x, y, z, 0, 0, 0);
        this.sprites = sprites;
        this.kind = kind;
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.hasPhysics = false;
        switch (kind) {
            case 1 -> { // Goldglut
                this.lifetime = 30 + random.nextInt(30);
                this.gravity = 0.02F;
                this.friction = 0.94F;
                this.baseSize = 0.12F + random.nextFloat() * 0.1F;
                setColor(1.0F, 0.78F + random.nextFloat() * 0.15F, 0.25F);
            }
            case 2 -> { // Leere
                this.lifetime = 20 + random.nextInt(15);
                this.gravity = 0F;
                this.friction = 0.8F;
                this.baseSize = 0.25F + random.nextFloat() * 0.2F;
                setColor(0.55F, 0.2F, 0.85F);
            }
            default -> { // Kristallfunke
                this.lifetime = 14 + random.nextInt(18);
                this.gravity = 0F;
                this.friction = 0.9F;
                this.baseSize = 0.18F + random.nextFloat() * 0.18F;
                float w = random.nextFloat();
                setColor(0.75F + w * 0.25F, 0.35F + w * 0.4F, 1.0F);
            }
        }
        this.quadSize = baseSize;
        setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteFromAge(sprites);
        float life = (float) age / lifetime;
        quadSize = baseSize * (1.0F - life * life);
        alpha = Mth.clamp(1.2F - life, 0F, 1F);
        if (kind == 1) {
            if (age < 6) yd += 0.01D;
            alpha *= 0.75F + 0.25F * Mth.sin(age * 1.7F);
        }
        if (kind == 2) {
            xd *= 1.08D;
            yd *= 1.08D;
            zd *= 1.08D;
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites, int kind) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new SovereignParticle(level, x, y, z, dx, dy, dz, sprites, kind);
        }
    }
}
