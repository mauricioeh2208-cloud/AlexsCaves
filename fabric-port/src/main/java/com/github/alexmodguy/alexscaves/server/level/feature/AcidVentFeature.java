package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.GeothermalVentBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class AcidVentFeature extends Feature<NoneFeatureConfiguration> {

    public AcidVentFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource random = context.random();
        WorldGenLevel level = context.level();
        BlockPos ventBottom = context.origin();
        if (!level.getBlockState(ventBottom.below()).is(Blocks.MUD)) {
            return false;
        }

        boolean placed = drawVent(level, ventBottom, random);
        for (int i = 0; i < 1 + random.nextInt(2); i++) {
            placed |= drawVent(level, ventBottom.offset(random.nextInt(8) - 4, 0, random.nextInt(8) - 4), random);
        }
        return placed;
    }

    private static boolean drawVent(WorldGenLevel level, BlockPos ventBottom, RandomSource random) {
        int height = random.nextInt(4) + 2;
        int acidCount = 0;
        while ((!level.getFluidState(ventBottom).isEmpty() || !level.getBlockState(ventBottom).isAir()) && ventBottom.getY() < level.getMaxBuildHeight() - height) {
            ventBottom = ventBottom.above();
            acidCount++;
            if (acidCount >= 3) {
                return false;
            }
        }
        if (!hasClearance(level, ventBottom, height)) {
            return false;
        }

        BlockPos basin = ventBottom.below();
        drawOrb(level, basin, random, 1 + random.nextInt(1), 2 + random.nextInt(3), 1 + random.nextInt(1));
        level.setBlock(basin.north(), Blocks.TUFF.defaultBlockState(), Block.UPDATE_CLIENTS);
        level.setBlock(basin.south(), Blocks.TUFF.defaultBlockState(), Block.UPDATE_CLIENTS);
        level.setBlock(basin.east(), Blocks.TUFF.defaultBlockState(), Block.UPDATE_CLIENTS);
        level.setBlock(basin.west(), Blocks.TUFF.defaultBlockState(), Block.UPDATE_CLIENTS);
        level.setBlock(basin.below(), Blocks.TUFF.defaultBlockState(), Block.UPDATE_CLIENTS);
        level.setBlock(basin, ACBlockRegistry.ACID.get().defaultBlockState(), Block.UPDATE_CLIENTS);

        int middleStart = Math.max(1, height / 3);
        int middleTop = middleStart * 2;
        for (int i = 1; i <= height; i++) {
            BlockState ventState;
            if (i <= middleStart) {
                ventState = ACBlockRegistry.GEOTHERMAL_VENT.get().defaultBlockState();
            } else if (i > middleTop) {
                ventState = ACBlockRegistry.GEOTHERMAL_VENT_THIN.get().defaultBlockState();
            } else {
                ventState = ACBlockRegistry.GEOTHERMAL_VENT_MEDIUM.get().defaultBlockState();
            }
            level.setBlock(basin.above(i), ventState.setValue(GeothermalVentBlock.SMOKE_TYPE, 3).setValue(GeothermalVentBlock.SPAWNING_PARTICLES, i == height), Block.UPDATE_CLIENTS);
        }
        level.setBlock(basin.above(height + 1), Blocks.CAVE_AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        return true;
    }

    private static boolean hasClearance(WorldGenLevel level, BlockPos ventBottom, int height) {
        for (int i = 1; i <= height; i++) {
            if (!level.isEmptyBlock(ventBottom.above(i))) {
                return false;
            }
        }
        return true;
    }

    private static void drawOrb(WorldGenLevel level, BlockPos center, RandomSource random, int radiusX, int radiusY, int radiusZ) {
        double equalRadius = (radiusX + radiusY + radiusZ) / 3.0D;
        for (int x = -radiusX; x <= radiusX; x++) {
            for (int y = -radiusY; y <= radiusY; y++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    BlockPos fill = center.offset(x, y, z);
                    if (fill.distToLowCornerSqr(center.getX(), center.getY(), center.getZ()) <= equalRadius * equalRadius + random.nextFloat() * 2.0F && canReplace(level.getBlockState(fill)) && fill.getY() <= center.getY()) {
                        level.setBlock(fill, Blocks.TUFF.defaultBlockState(), Block.UPDATE_CLIENTS);
                    }
                }
            }
        }
    }

    private static boolean canReplace(BlockState state) {
        return !state.is(ACTagRegistry.UNMOVEABLE) && !state.isAir();
    }
}
