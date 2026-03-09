package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class VentSmokeParticle extends TextureSheetParticle {
    private final boolean fullbright;

    protected VentSmokeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, boolean fullbright) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.setSize(0.5F, 0.5F);
        this.quadSize = 0.8F + level.random.nextFloat() * 0.3F;
        this.lifetime = (int) (Math.random() * 20.0D) + 40;
        this.friction = 0.99F;
        this.fullbright = fullbright;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - (this.age - this.lifetime / 2.0F) / this.lifetime);
        }
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.move(this.xd, this.yd, this.zd);
        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public float getQuadSize(float partialTick) {
        return this.quadSize * Mth.clamp((this.age + partialTick) / this.lifetime * 4.0F, 0.0F, 1.0F);
    }

    @Override
    public int getLightColor(float partialTick) {
        return fullbright ? 240 : super.getLightColor(partialTick);
    }

    public static class BlackFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public BlackFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            VentSmokeParticle particle = new VentSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, false);
            particle.pickSprite(spriteSet);
            float tint = level.random.nextFloat() * 0.05F;
            particle.setColor(tint + 0.1F, tint + 0.1F, tint + 0.1F);
            return particle;
        }
    }

    public static class WhiteFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public WhiteFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            VentSmokeParticle particle = new VentSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, false);
            particle.pickSprite(spriteSet);
            float tint = level.random.nextFloat() * 0.05F;
            particle.setColor(tint + 0.95F, tint + 0.95F, tint + 0.95F);
            return particle;
        }
    }

    public static class GreenFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public GreenFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            VentSmokeParticle particle = new VentSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, true);
            particle.pickSprite(spriteSet);
            float tint = level.random.nextFloat() * 0.05F;
            particle.setColor(tint + 0.05F, tint + 0.95F, 0.0F);
            return particle;
        }
    }

    public static class RedFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public RedFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            VentSmokeParticle particle = new VentSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, true);
            particle.pickSprite(spriteSet);
            float tint = level.random.nextFloat() * 0.15F;
            particle.setColor(tint + 0.85F, tint + 0.55F, tint + 0.35F);
            return particle;
        }
    }
}
