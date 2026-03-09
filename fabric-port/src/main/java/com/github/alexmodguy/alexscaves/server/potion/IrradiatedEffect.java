package com.github.alexmodguy.alexscaves.server.potion;

import com.github.alexmodguy.alexscaves.server.item.HazmatArmorUtil;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class IrradiatedEffect extends MobEffect {
    public static final int BLUE_LEVEL = 4;

    protected IrradiatedEffect() {
        super(MobEffectCategory.HARMFUL, 0X77D60E);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int tick) {
        int hazmat = HazmatArmorUtil.getWornAmount(entity);
        float damageScale = 1.0F - hazmat * 0.25F;
        if (entity instanceof Player player && hazmat == 0) {
            player.causeFoodExhaustion(0.4F);
        }
        if (!entity.getType().is(ACTagRegistry.RESISTS_RADIATION) && entity.level().random.nextFloat() < damageScale + 0.1F) {
            entity.hurt(ACDamageTypes.causeRadiationDamage(entity.level().registryAccess()), damageScale);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        if (amplifier <= 0) {
            return false;
        }
        int interval = 200 / amplifier;
        if (interval > 1) {
            return duration % interval == interval / 2;
        }
        return true;
    }
}
