package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class ProtonParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final float orbitOffset;

    protected ProtonParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sprites = sprites;
        this.quadSize = 0.18F + level.random.nextFloat() * 0.08F;
        this.lifetime = 24 + level.random.nextInt(12);
        this.hasPhysics = false;
        this.orbitOffset = level.random.nextFloat() * (float) (Math.PI * 2.0D);
        this.setColor(0.4F, 1.0F, 0.25F);
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        float progress = this.age / (float) this.lifetime;
        double angle = orbitOffset + this.age * 0.35D;
        this.x += Math.cos(angle) * 0.01D;
        this.z += Math.sin(angle) * 0.01D;
        this.y += 0.01D + progress * 0.01D;
        this.alpha = 1.0F - progress;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public float getQuadSize(float partialTick) {
        return this.quadSize * Mth.clamp((this.age + partialTick) / this.lifetime * 16.0F, 0.0F, 1.0F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 240;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ProtonParticle(level, x, y, z, spriteSet);
        }
    }
}
