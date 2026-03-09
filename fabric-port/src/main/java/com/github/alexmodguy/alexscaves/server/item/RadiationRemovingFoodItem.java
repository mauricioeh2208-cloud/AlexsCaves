package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class RadiationRemovingFoodItem extends Item {

    public RadiationRemovingFoodItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        MobEffectInstance mobEffectInstance = livingEntity.getEffect(ACEffectRegistry.IRRADIATED.holder());
        FoodProperties foodProperties = stack.get(DataComponents.FOOD);
        if (mobEffectInstance != null && foodProperties != null) {
            float healedAmount = (float) Math.ceil(foodProperties.nutrition() * 1.5F + 1.0F);
            livingEntity.setHealth(Math.min(livingEntity.getMaxHealth(), livingEntity.getHealth() + healedAmount));
        }
        return super.finishUsingItem(stack, level, livingEntity);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return this == ACItemRegistry.GREEN_SOYLENT.get() ? ItemUtils.startUsingInstantly(level, player, hand) : super.use(level, player, hand);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return this == ACItemRegistry.GREEN_SOYLENT.get() ? UseAnim.DRINK : UseAnim.EAT;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return this == ACItemRegistry.GREEN_SOYLENT.get() ? SoundEvents.HONEY_DRINK : super.getDrinkingSound();
    }

    @Override
    public SoundEvent getEatingSound() {
        return this == ACItemRegistry.GREEN_SOYLENT.get() ? SoundEvents.HONEY_DRINK : super.getEatingSound();
    }
}
