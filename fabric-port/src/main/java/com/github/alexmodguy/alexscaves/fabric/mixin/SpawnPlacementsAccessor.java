package com.github.alexmodguy.alexscaves.fabric.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.util.RandomSource;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpawnPlacements.class)
public interface SpawnPlacementsAccessor {
    @Invoker("register")
    static <T extends Mob> void alexscaves$register(EntityType<T> entityType, SpawnPlacementType placementType, Heightmap.Types heightmapType, SpawnPlacements.SpawnPredicate<T> predicate) {
        throw new AssertionError();
    }
}
