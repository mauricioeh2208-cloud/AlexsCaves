package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class StunStarParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final int entityId;
    private final float angleOffset;
    private final boolean reverseOrbit;

    protected StunStarParticle(ClientLevel level, double x, double y, double z, int entityId, float angleOffset, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sprites = sprites;
        this.entityId = entityId;
        this.angleOffset = angleOffset;
        this.reverseOrbit = random.nextBoolean();
        this.hasPhysics = false;
        this.quadSize = 0.28F;
        this.lifetime = 30 + level.random.nextInt(5);
        this.setColor(1.0F, 1.0F, 1.0F);
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

        if (!(level.getEntity(entityId) instanceof LivingEntity living) || !living.hasEffect(ACEffectRegistry.STUNNED.holder())) {
            this.remove();
            return;
        }

        float angleDegrees = age * 12.0F + angleOffset * (reverseOrbit ? -1.0F : 1.0F);
        float angle = angleDegrees * Mth.DEG_TO_RAD;
        float radius = living.getBbWidth() * 0.5F + 0.25F;
        Vec3 center = living.getEyePosition().add(0.0D, 0.35D + Math.sin(age * 0.25D) * 0.05D, 0.0D);
        this.setPos(center.x + Math.cos(angle) * radius, center.y, center.z + Math.sin(angle) * radius);
        this.alpha = 1.0F - age / (float) this.lifetime;
        this.setSpriteFromAge(sprites);
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
            return new StunStarParticle(level, x, y, z, (int) xSpeed, (float) ySpeed, spriteSet);
        }
    }
}
