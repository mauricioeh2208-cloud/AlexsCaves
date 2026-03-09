package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class JellyBeanItem extends PotionItem {

    public JellyBeanItem() {
        super(new Item.Properties().food(ACFoods.JELLY_BEAN).stacksTo(16));
    }

    public static int getBeanColor(ItemStack stack) {
        return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor();
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
        return 16;
    }

    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return this.getDescriptionId();
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_EAT;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_EAT;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int useDuration) {
        Vec3 motion = new Vec3(((double) level.getRandom().nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
        motion = motion.xRot(-living.getXRot() * ((float) Math.PI / 180F));
        motion = motion.yRot(-living.getYRot() * ((float) Math.PI / 180F));
        double yOffset = (double) (-level.getRandom().nextFloat()) * 0.6D - 0.3D;
        Vec3 position = new Vec3(((double) level.getRandom().nextFloat() - 0.5D) * 0.3D, yOffset, 0.6D);
        position = position.xRot(-living.getXRot() * ((float) Math.PI / 180F));
        position = position.yRot(-living.getYRot() * ((float) Math.PI / 180F));
        position = position.add(living.getX(), living.getEyeY(), living.getZ());
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new ItemParticleOption(ACParticleRegistry.JELLY_BEAN_EAT.get(), stack), position.x, position.y, position.z, 1, motion.x, motion.y + 0.05D, motion.z, 0.0D);
        } else {
            level.addParticle(new ItemParticleOption(ACParticleRegistry.JELLY_BEAN_EAT.get(), stack), position.x, position.y, position.z, motion.x, motion.y + 0.05D, motion.z);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).addPotionTooltip(tooltip::add, 1.0F, context.tickRate());
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        Player player = living instanceof Player playerEntity ? playerEntity : null;
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
        }

        PotionContents potionContents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (!level.isClientSide) {
            for (MobEffectInstance mobEffectInstance : potionContents.getAllEffects()) {
                MobEffect mobEffect = mobEffectInstance.getEffect().value();
                if (mobEffect.isInstantenous()) {
                    mobEffect.applyInstantenousEffect(player, player, living, mobEffectInstance.getAmplifier(), 1.0D);
                } else {
                    living.addEffect(new MobEffectInstance(mobEffectInstance));
                }
            }
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        } else {
            stack.shrink(1);
        }
        living.gameEvent(GameEvent.EAT);
        return stack;
    }
}
