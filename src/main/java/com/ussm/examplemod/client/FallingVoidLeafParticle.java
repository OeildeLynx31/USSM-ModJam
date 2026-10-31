package com.ussm.examplemod.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CherryParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class FallingVoidLeafParticle extends CherryParticle {
    protected FallingVoidLeafParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z, spriteSet);

        this.quadSize *= 1.3F;
        float shade = 0.75F + this.random.nextFloat() * 0.25F;
        this.rCol = 0.7f * shade;
        this.gCol = 0.88f * shade;
        this.bCol = 0.52f * shade;
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
            return new FallingVoidLeafParticle(clientLevel, pX, pY, pZ, this.spriteSet);
        }
    }
}
