package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class UraniumRodBlock extends RotatedPillarBlock {
    private static final VoxelShape SHAPE_X = Shapes.or(
        Block.box(2.0D, 6.0D, 6.0D, 14.0D, 10.0D, 10.0D),
        Block.box(14.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D),
        Block.box(0.0D, 5.0D, 5.0D, 2.0D, 11.0D, 11.0D)
    );
    private static final VoxelShape SHAPE_Y = Shapes.or(
        Block.box(6.0D, 2.0D, 6.0D, 10.0D, 14.0D, 10.0D),
        Block.box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D),
        Block.box(5.0D, 14.0D, 5.0D, 11.0D, 16.0D, 11.0D)
    );
    private static final VoxelShape SHAPE_Z = Shapes.or(
        Block.box(6.0D, 6.0D, 2.0D, 10.0D, 10.0D, 14.0D),
        Block.box(5.0D, 5.0D, 14.0D, 11.0D, 11.0D, 16.0D),
        Block.box(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 2.0D)
    );

    public UraniumRodBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_LIGHT_GREEN)
            .strength(1.5F)
            .lightLevel(state -> 9)
            .emissiveRendering((state, level, pos) -> true)
            .sound(ACSoundTypes.URANIUM));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(AXIS)) {
            case X -> SHAPE_X;
            case Z -> SHAPE_Z;
            default -> SHAPE_Y;
        };
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if (randomSource.nextInt(80) == 0) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ACSoundRegistry.URANIUM_HUM.get(), SoundSource.BLOCKS, 0.5F, randomSource.nextFloat() * 0.4F + 0.8F, false);
        }
        if (randomSource.nextInt(10) == 0) {
            Vec3 center = Vec3.upFromBottomCenterOf(pos, 0.5F);
            level.addParticle(ACParticleRegistry.PROTON.get(), center.x, center.y, center.z, center.x, center.y, center.z);
        }
    }
}
