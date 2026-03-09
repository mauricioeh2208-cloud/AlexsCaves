package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class LanternfishEntity extends AbstractSchoolingFish {
    public LanternfishEntity(EntityType<? extends LanternfishEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractFish.createAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.15D)
            .add(Attributes.MAX_HEALTH, 2.0D);
    }

    public static boolean checkLanternfishSpawnRules(EntityType<LanternfishEntity> entityType, ServerLevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource randomSource) {
        return levelAccessor.getFluidState(blockPos).is(FluidTags.WATER)
            && blockPos.getY() < levelAccessor.getSeaLevel() - 30;
    }

    @Override
    public int getMaxSchoolSize() {
        return 20;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ACItemRegistry.LANTERNFISH_BUCKET.get());
    }

    @Override
    protected SoundEvent getFlopSound() {
        return ACSoundRegistry.LANTERNFISH_FLOP.get();
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource damageSource) {
        return ACSoundRegistry.LANTERNFISH_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.LANTERNFISH_HURT.get();
    }
}
