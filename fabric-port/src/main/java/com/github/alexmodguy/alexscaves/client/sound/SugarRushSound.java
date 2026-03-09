package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class SugarRushSound extends AbstractTickableSoundInstance {
    private final LivingEntity user;

    public SugarRushSound(LivingEntity user) {
        super(ACSoundRegistry.SUGAR_RUSH_LOOP.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.user = user;
        this.attenuation = Attenuation.NONE;
        this.looping = true;
        this.x = user.getX();
        this.y = user.getY();
        this.z = user.getZ();
        this.delay = 0;
        this.volume = 0.0F;
    }

    @Override
    public boolean canPlaySound() {
        return !user.isSilent() && user.hasEffect(ACEffectRegistry.SUGAR_RUSH.holder());
    }

    @Override
    public void tick() {
        if (user.isAlive() && user.hasEffect(ACEffectRegistry.SUGAR_RUSH.holder())) {
            this.x = user.getX();
            this.y = user.getY();
            this.z = user.getZ();
            this.volume = Math.min(1.0F, this.volume + 0.1F);
            this.pitch = 1.0F;
        } else {
            this.volume = Math.max(0.0F, this.volume - 0.1F);
            if (this.volume <= 0.0F) {
                this.stop();
            }
        }
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    public boolean isSameEntity(LivingEntity livingEntity) {
        return user.isAlive() && user.getId() == livingEntity.getId();
    }
}
