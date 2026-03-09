package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class NucleeperEntity extends Creeper {
    public NucleeperEntity(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.2D)
            .add(Attributes.MAX_HEALTH, 40.0D)
            .add(Attributes.ARMOR, 4.0D);
    }

    public static boolean checkNucleeperSpawnRules(EntityType<NucleeperEntity> entityType, ServerLevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource randomSource) {
        return Monster.checkMonsterSpawnRules(entityType, levelAccessor, spawnType, blockPos, randomSource);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.NUCLEEPER_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.NUCLEEPER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.NUCLEEPER_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ACSoundRegistry.NUCLEEPER_STEP.get(), 0.15F, 1.0F);
    }
}
