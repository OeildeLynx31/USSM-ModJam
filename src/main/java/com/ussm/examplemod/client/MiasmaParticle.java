package com.ussm.examplemod.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class MiasmaParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    protected MiasmaParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;

        this.lifetime = 40 + this.random.nextInt(40);
        this.quadSize *= 1.5f + this.random.nextFloat() * 1.5f;
        this.hasPhysics = false;
        this.gravity = -0.002f; // ca monte doucement
        this.friction = 0.98f;

        this.xd = this.random.nextGaussian() * 0.008;
        this.yd = this.random.nextGaussian() * 0.004;
        this.zd = this.random.nextGaussian() * 0.008;

        // violet
        float shade = 0.75f + this.random.nextFloat() * 0.25f;
        this.rCol = 0.72f * shade;
        this.gCol = 0.42f * shade;
        this.bCol = 0.95f * shade;
        this.alpha = 0f;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();
        float life = (float) this.age / this.lifetime;
        this.alpha = 0.7f * Math.min(1f, life * 4f) * Math.min(1f, (1f - life) * 2f);
        this.setSpriteFromAge(this.spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel,
                                       double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new MiasmaParticle(clientLevel, pX, pY, pZ, this.spriteSet);
        }
    }
}
