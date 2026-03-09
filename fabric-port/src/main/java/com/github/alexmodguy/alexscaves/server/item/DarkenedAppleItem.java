package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DarkenedAppleItem extends Item {

    public DarkenedAppleItem() {
        super(new Properties().food(ACFoods.DARKENED_APPLE).rarity(ACItemRegistry.RARITY_DEMONIC));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        MobEffectInstance mobEffectInstance = livingEntity.getEffect(ACEffectRegistry.DARKNESS_INCARNATE.holder());
        if (mobEffectInstance != null) {
            livingEntity.forceAddEffect(
                new MobEffectInstance(ACEffectRegistry.DARKNESS_INCARNATE.holder(), mobEffectInstance.getDuration() + 600, mobEffectInstance.getAmplifier()),
                null
            );
        }
        return super.finishUsingItem(stack, level, livingEntity);
    }
}
