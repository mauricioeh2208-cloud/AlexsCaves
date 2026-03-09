package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class AcidDropParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private int onGroundTime;

    protected AcidDropParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.friction = 0.96F;
        this.gravity = 0.0F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.quadSize *= 1.2F + random.nextFloat();
        this.lifetime = 100 + random.nextInt(40);
        this.setSpriteFromAge(sprites);
        this.hasPhysics = true;
    }

    @Override
    public float getQuadSize(float partialTick) {
        return this.quadSize * Mth.clamp((this.age + partialTick) / this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public void tick() {
        if (this.age < this.lifetime * 0.25F) {
            this.gravity = 0.0F;
        } else {
            this.gravity = 1.0F;
            if (this.onGround) {
                onGroundTime++;
            }
        }

        this.setSprite(this.sprites.get(this.onGround ? 1 : 0, 1));
        if (onGroundTime > 5) {
            this.remove();
            this.level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        }
        super.tick();
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
            return new AcidDropParticle(level, x, y, z, spriteSet);
        }
    }
}
