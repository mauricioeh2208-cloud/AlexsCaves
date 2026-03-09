package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class AcidicRadrockBlock extends Block {

    public AcidicRadrockBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_LIGHT_GREEN)
            .requiresCorrectToolForDrops()
            .strength(2.5F, 7.0F)
            .sound(ACSoundTypes.RADROCK));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if (randomSource.nextInt(3) != 0) {
            return;
        }

        Direction direction = Direction.getRandom(randomSource);
        if (direction == Direction.UP) {
            return;
        }

        BlockPos offsetPos = pos.relative(direction);
        BlockState offsetState = level.getBlockState(offsetPos);
        if (!state.canOcclude() || !offsetState.isFaceSturdy(level, offsetPos, direction.getOpposite())) {
            double x = direction.getStepX() == 0 ? randomSource.nextDouble() : 0.5D + direction.getStepX() * 0.6D;
            double y = direction.getStepY() == 0 ? randomSource.nextDouble() : 0.5D + direction.getStepY() * 0.6D;
            double z = direction.getStepZ() == 0 ? randomSource.nextDouble() : 0.5D + direction.getStepZ() * 0.6D;
            level.addParticle(ACParticleRegistry.ACID_DROP.get(), pos.getX() + x, pos.getY() + y, pos.getZ() + z, 0.0D, 0.0D, 0.0D);
        }
    }
}
