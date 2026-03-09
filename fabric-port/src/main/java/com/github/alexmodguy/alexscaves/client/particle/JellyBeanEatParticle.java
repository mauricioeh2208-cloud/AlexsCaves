package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.world.item.ItemStack;

public class JellyBeanEatParticle extends BreakingItemParticle {

    protected JellyBeanEatParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, ItemStack stack) {
        super(level, x, y, z, stack);
        this.xd *= 0.1F;
        this.yd *= 0.1F;
        this.zd *= 0.1F;
        this.xd += xd;
        this.yd += yd;
        this.zd += zd;
    }

    public static class Factory implements ParticleProvider<ItemParticleOption> {
        @Override
        public Particle createParticle(ItemParticleOption particle, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new JellyBeanEatParticle(level, x, y, z, xd, yd, zd, particle.getItem());
        }
    }
}
