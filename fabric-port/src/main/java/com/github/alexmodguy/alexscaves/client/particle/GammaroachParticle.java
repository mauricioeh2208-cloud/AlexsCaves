package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;

public class GammaroachParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected GammaroachParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.setSize(0.5F, 0.5F);
        this.quadSize = 0.2F + level.random.nextFloat() * 0.4F;
        this.lifetime = 8 + level.random.nextInt(5);
        this.friction = 0.96F;
        this.sprites = sprites;
        float base = level.random.nextFloat() * 0.05F;
        this.setColor(base * 0.2F + 0.2F, base * 0.2F + 0.2F, base * 0.2F + 0.2F);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.setSpriteFromAge(this.sprites);
        if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - (this.age - this.lifetime / 4.0F) / this.lifetime);
        }
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        int color = 0x5CF600;
        float randomTint = level.random.nextFloat() * 0.05F;
        Vec3 target = new Vec3(
            Math.min(FastColor.ARGB32.red(color) / 255.0F + randomTint, 1.0F),
            Math.min(FastColor.ARGB32.green(color) / 255.0F + randomTint, 1.0F),
            Math.min(FastColor.ARGB32.blue(color) / 255.0F + randomTint, 1.0F)
        );
        this.rCol += (target.x - rCol) * 0.1F;
        this.gCol += (target.y - gCol) * 0.1F;
        this.bCol += (target.z - bCol) * 0.1F;
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
            GammaroachParticle particle = new GammaroachParticle(level, x, y, z, spriteSet);
            particle.setSpriteFromAge(spriteSet);
            return particle;
        }
    }
}
