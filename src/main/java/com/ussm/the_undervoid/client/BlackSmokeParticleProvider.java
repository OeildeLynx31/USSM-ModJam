package com.ussm.the_undervoid.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/** Fumée de feu de camp (cosy), noircie. */
public class BlackSmokeParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final CampfireSmokeParticle.CosyProvider inner;

    public BlackSmokeParticleProvider(SpriteSet sprites) {
        this.inner = new CampfireSmokeParticle.CosyProvider(sprites);
    }

    @Nullable
    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                   double dx, double dy, double dz) {
        Particle particle = inner.createParticle(type, level, x, y, z, dx, dy, dz);
        if (particle != null) {
            particle.setColor(0.03f, 0.03f, 0.03f);
        }
        return particle;
    }
}
