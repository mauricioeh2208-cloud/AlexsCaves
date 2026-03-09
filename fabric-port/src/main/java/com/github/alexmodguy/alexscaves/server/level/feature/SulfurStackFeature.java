package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.SulfurBudBlock;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

public class SulfurStackFeature extends Feature<NoneFeatureConfiguration> {

    public SulfurStackFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource random = context.random();
        WorldGenLevel level = context.level();
        BlockPos base = context.origin();
        if (!level.getBlockState(base.below()).is(ACBlockRegistry.RADROCK.get())) {
            return false;
        }

        int centerHeight = 3 + random.nextInt(3);
        generatePillar(level, base, random, centerHeight);
        for (int i = 0; i < 2 + random.nextInt(2); i++) {
            BlockPos offset = base.offset(random.nextInt(4) - 2, -random.nextInt(2), random.nextInt(4) - 2);
            int dist = (int) Math.ceil(offset.distManhattan(base));
            generatePillar(level, offset, random, centerHeight - dist + random.nextInt(2));
        }
        for (int i = 0; i < 4 + random.nextInt(6); i++) {
            BlockPos offset = base.offset(random.nextInt(6) - 3, 4, random.nextInt(6) - 3);
            while (level.isEmptyBlock(offset) && offset.getY() > level.getMinBuildHeight()) {
                offset = offset.below();
            }
            if (level.getBlockState(offset).isFaceSturdy(level, offset, Direction.UP) && level.isEmptyBlock(offset.above())) {
                placeRandomCrystal(level, offset.above(), random);
            }
        }
        return true;
    }

    private static boolean canReplace(BlockState state) {
        return (state.isAir() || state.canBeReplaced()) && !state.is(ACTagRegistry.UNMOVEABLE);
    }

    private static void generatePillar(WorldGenLevel level, BlockPos pos, RandomSource random, int height) {
        BlockPos cursor = pos.relative(Direction.DOWN, 3);
        BlockPos stopAt = pos.relative(Direction.UP, height);
        while (!cursor.equals(stopAt)) {
            cursor = cursor.relative(Direction.UP);
            if (canReplace(level.getBlockState(cursor))) {
                level.setBlock(cursor, ACBlockRegistry.SULFUR.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
        if (canReplace(level.getBlockState(stopAt.above())) && !(level.getBlockState(stopAt).getBlock() instanceof SulfurBudBlock)) {
            placeRandomCrystal(level, stopAt.above(), random);
        }
    }

    private static void placeRandomCrystal(WorldGenLevel level, BlockPos placeAt, RandomSource random) {
        BlockState crystal = switch (random.nextInt(3)) {
            case 0 -> ACBlockRegistry.SULFUR_BUD_SMALL.get().defaultBlockState();
            case 1 -> ACBlockRegistry.SULFUR_BUD_MEDIUM.get().defaultBlockState();
            default -> ACBlockRegistry.SULFUR_BUD_LARGE.get().defaultBlockState();
        };

        if (crystal.getBlock() instanceof SulfurBudBlock) {
            if (level.getFluidState(placeAt).getType() == Fluids.WATER) {
                crystal = crystal.setValue(SulfurBudBlock.LIQUID_LOGGED, 1);
            } else if (level.getFluidState(placeAt).getType() == ACFluidRegistry.ACID.get()) {
                crystal = crystal.setValue(SulfurBudBlock.LIQUID_LOGGED, 2);
            }
        }
        level.setBlock(placeAt, crystal, Block.UPDATE_ALL);

        BlockPos drip = placeAt.above();
        while (level.isEmptyBlock(drip) && drip.getY() < level.getMaxBuildHeight()) {
            drip = drip.above();
        }
        if (level.getFluidState(drip).isEmpty()) {
            level.setBlock(drip, ACBlockRegistry.ACIDIC_RADROCK.get().defaultBlockState(), Block.UPDATE_ALL);
        }
    }
}
