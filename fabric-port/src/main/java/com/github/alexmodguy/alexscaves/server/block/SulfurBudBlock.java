package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class SulfurBudBlock extends Block implements SimpleWaterloggedBlock {
    public static final IntegerProperty LIQUID_LOGGED = IntegerProperty.create("liquid_logged", 0, 2);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private final Map<Direction, VoxelShape> shapeMap;

    public SulfurBudBlock(int pixelWidth, int pixelHeight) {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_YELLOW)
            .strength(1.0F, 2.0F)
            .sound(ACSoundTypes.SULFUR)
            .randomTicks());
        this.registerDefaultState(this.defaultBlockState().setValue(LIQUID_LOGGED, 0).setValue(FACING, Direction.UP));
        this.shapeMap = buildShapeMap(pixelWidth, pixelHeight);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIQUID_LOGGED, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
            .setValue(LIQUID_LOGGED, getLiquidType(context.getLevel().getFluidState(context.getClickedPos())))
            .setValue(FACING, context.getClickedFace());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos supportPos = pos.relative(direction.getOpposite());
        return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, direction);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        int liquidType = state.getValue(LIQUID_LOGGED);
        if (liquidType == 1) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        } else if (liquidType == 2) {
            level.scheduleTick(pos, ACFluidRegistry.ACID.get(), ACFluidRegistry.ACID.get().getTickDelay(level));
        }
        return direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeMap.get(state.getValue(FACING));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if (randomSource.nextInt(3) != 0 || state.is(ACBlockRegistry.SULFUR_CLUSTER.get())) {
            return;
        }

        BlockPos cursor = pos.above();
        while (level.getBlockState(cursor).isAir() && cursor.getY() < level.getMaxBuildHeight()) {
            cursor = cursor.above();
        }

        if (!level.getBlockState(cursor).is(ACBlockRegistry.ACIDIC_RADROCK.get()) && state.getValue(LIQUID_LOGGED) != 2) {
            return;
        }

        Block nextBlock = null;
        if (state.is(ACBlockRegistry.SULFUR_BUD_SMALL.get())) {
            nextBlock = ACBlockRegistry.SULFUR_BUD_MEDIUM.get();
        } else if (state.is(ACBlockRegistry.SULFUR_BUD_MEDIUM.get())) {
            nextBlock = ACBlockRegistry.SULFUR_BUD_LARGE.get();
        } else if (state.is(ACBlockRegistry.SULFUR_BUD_LARGE.get())) {
            nextBlock = ACBlockRegistry.SULFUR_CLUSTER.get();
        }

        if (nextBlock != null) {
            level.setBlockAndUpdate(pos, nextBlock.defaultBlockState().setValue(FACING, state.getValue(FACING)).setValue(LIQUID_LOGGED, state.getValue(LIQUID_LOGGED)));
        }
    }

    @Override
    public boolean canPlaceLiquid(net.minecraft.world.entity.player.Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return fluid == Fluids.WATER || fluid == ACFluidRegistry.ACID.get();
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.getValue(LIQUID_LOGGED) != 0) {
            return false;
        }
        if (!level.isClientSide()) {
            if (fluidState.getType() == Fluids.WATER) {
                level.setBlock(pos, state.setValue(LIQUID_LOGGED, 1), Block.UPDATE_ALL);
            } else if (fluidState.getType() == ACFluidRegistry.ACID.get()) {
                level.setBlock(pos, state.setValue(LIQUID_LOGGED, 2), Block.UPDATE_ALL);
            } else {
                return false;
            }
            level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
        }
        return true;
    }

    @Override
    public ItemStack pickupBlock(net.minecraft.world.entity.player.Player player, LevelAccessor level, BlockPos pos, BlockState state) {
        int liquidType = state.getValue(LIQUID_LOGGED);
        if (liquidType == 0) {
            return ItemStack.EMPTY;
        }
        level.setBlock(pos, state.setValue(LIQUID_LOGGED, 0), Block.UPDATE_ALL);
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
        return new ItemStack(liquidType == 1 ? Items.WATER_BUCKET : ACItemRegistry.ACID_BUCKET.get());
    }

    @Override
    public Optional<net.minecraft.sounds.SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        int liquidType = state.getValue(LIQUID_LOGGED);
        if (liquidType == 1) {
            return Fluids.WATER.getSource(false);
        }
        if (liquidType == 2) {
            return ACFluidRegistry.ACID.get().getSource(false);
        }
        return super.getFluidState(state);
    }

    public static int getLiquidType(FluidState fluidState) {
        if (fluidState.getType() == Fluids.WATER) {
            return 1;
        }
        if (fluidState.getType() == ACFluidRegistry.ACID.get() && fluidState.isSource()) {
            return 2;
        }
        return 0;
    }

    private static Map<Direction, VoxelShape> buildShapeMap(int pixelWidth, int pixelHeight) {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);
        map.put(Direction.UP, Block.box(8 - pixelWidth / 2.0D, 0.0D, 8 - pixelWidth / 2.0D, 8 + pixelWidth / 2.0D, pixelHeight, 8 + pixelWidth / 2.0D));
        map.put(Direction.DOWN, Block.box(8 - pixelWidth / 2.0D, 16 - pixelHeight, 8 - pixelWidth / 2.0D, 8 + pixelWidth / 2.0D, 16.0D, 8 + pixelWidth / 2.0D));
        map.put(Direction.NORTH, Block.box(8 - pixelWidth / 2.0D, 8 - pixelWidth / 2.0D, 0.0D, 8 + pixelWidth / 2.0D, 8 + pixelWidth / 2.0D, pixelHeight));
        map.put(Direction.SOUTH, Block.box(8 - pixelWidth / 2.0D, 8 - pixelWidth / 2.0D, 16 - pixelHeight, 8 + pixelWidth / 2.0D, 8 + pixelWidth / 2.0D, 16.0D));
        map.put(Direction.EAST, Block.box(0.0D, 8 - pixelWidth / 2.0D, 8 - pixelWidth / 2.0D, pixelHeight, 8 + pixelWidth / 2.0D, 8 + pixelWidth / 2.0D));
        map.put(Direction.WEST, Block.box(16 - pixelHeight, 8 - pixelWidth / 2.0D, 8 - pixelWidth / 2.0D, 16.0D, 8 + pixelWidth / 2.0D, 8 + pixelWidth / 2.0D));
        return map;
    }
}
