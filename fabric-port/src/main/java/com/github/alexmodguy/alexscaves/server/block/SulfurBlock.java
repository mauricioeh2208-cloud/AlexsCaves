package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class SulfurBlock extends Block {

    public SulfurBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_YELLOW)
            .strength(2.0F, 4.0F)
            .sound(ACSoundTypes.SULFUR)
            .randomTicks());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if (randomSource.nextInt(10) != 0) {
            return;
        }

        Direction direction = Util.getRandom(Direction.values(), randomSource);
        BlockPos offsetPos = pos.relative(direction);
        if (level.getBlockState(offsetPos).isAir() && isDrippingAcidAbove(level, offsetPos)) {
            BlockState budState = ACBlockRegistry.SULFUR_BUD_SMALL.get().defaultBlockState().setValue(SulfurBudBlock.FACING, direction);
            level.setBlockAndUpdate(offsetPos, budState);
        }
    }

    private boolean isDrippingAcidAbove(Level level, BlockPos pos) {
        BlockPos cursor = pos;
        while (level.getBlockState(cursor).isAir() && cursor.getY() < level.getMaxBuildHeight()) {
            cursor = cursor.above();
        }
        return level.getBlockState(cursor).is(ACBlockRegistry.ACIDIC_RADROCK.get());
    }
}
