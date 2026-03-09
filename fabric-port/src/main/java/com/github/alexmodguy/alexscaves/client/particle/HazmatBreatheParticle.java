package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class HazmatBreatheParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected HazmatBreatheParticle(ClientLevel level, double x, double y, double z, double xMotion, double yMotion, double zMotion, SpriteSet sprites, boolean blue) {
        super(level, x, y, z, xMotion, yMotion, zMotion);
        this.friction = 0.96F;
        this.gravity = -0.02F - 0.03F * level.random.nextFloat();
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.xd *= 0.125F;
        this.yd *= 0.125F;
        this.zd *= 0.125F;
        float baseColor = level.random.nextFloat() * 0.25F;
        float greenAmount = level.random.nextFloat() * 0.5F + 0.5F;
        this.rCol = baseColor;
        this.gCol = greenAmount;
        this.bCol = blue ? greenAmount : baseColor;
        this.quadSize *= 0.75F + random.nextFloat() * 0.5F;
        this.lifetime = (int) (30 / (level.random.nextFloat() * 0.8D + 0.2D));
        this.setSpriteFromAge(sprites);
        this.hasPhysics = false;
    }

    @Override
    public float getQuadSize(float partialTick) {
        return this.quadSize * Mth.clamp((this.age + partialTick) / this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
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
            return new HazmatBreatheParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, false);
        }
    }

    public static class BlueFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public BlueFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new HazmatBreatheParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, true);
        }
    }
}
