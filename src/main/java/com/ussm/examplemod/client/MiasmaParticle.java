package com.ussm.examplemod.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;

public class MiasmaParticle extends TextureSheetParticle {
    private static final float MAX_ALPHA = 0.7f;

    private final SpriteSet sprites;

    private MiasmaParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        lifetime = 40 + random.nextInt(40);
        quadSize *= 1.5f + random.nextFloat() * 1.5f;
        hasPhysics = false;
        gravity = -0.002f;
        friction = 0.98f;
        xd = random.nextGaussian() * 0.008;
        yd = random.nextGaussian() * 0.004;
        zd = random.nextGaussian() * 0.008;
        float shade = 0.75f + random.nextFloat() * 0.25f;
        setColor(0.72f * shade, 0.42f * shade, 0.95f * shade);
        alpha = 0f;
        setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        float life = (float) age / lifetime;
        alpha = MAX_ALPHA * Math.min(1f, life * 4f) * Math.min(1f, (1f - life) * 2f);
        setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new MiasmaParticle(level, x, y, z, sprites);
        }
    }
}
