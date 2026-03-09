package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.GeothermalVentBlockEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class GeothermalVentBlock extends BaseEntityBlock {
    public static final MapCodec<GeothermalVentBlock> CODEC = simpleCodec(properties -> new GeothermalVentBlock());

    public static final IntegerProperty SMOKE_TYPE = IntegerProperty.create("smoke_type", 0, 3);
    public static final BooleanProperty SPAWNING_PARTICLES = BooleanProperty.create("spawning_particles");

    public GeothermalVentBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .requiresCorrectToolForDrops()
            .strength(2.0F, 5.0F)
            .sound(SoundType.TUFF));
        this.registerDefaultState(this.stateDefinition.any().setValue(SMOKE_TYPE, 0).setValue(SPAWNING_PARTICLES, true));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SMOKE_TYPE, SPAWNING_PARTICLES);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState()
            .setValue(SMOKE_TYPE, getSmokeType(level, pos))
            .setValue(SPAWNING_PARTICLES, isSpawningParticles(level, pos));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state
            .setValue(SMOKE_TYPE, getSmokeType(level, pos))
            .setValue(SPAWNING_PARTICLES, isSpawningParticles(level, pos));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (heldItem.is(Items.GLASS_BOTTLE) && state.getValue(SMOKE_TYPE) == 3 && state.getValue(SPAWNING_PARTICLES)) {
            ItemStack radonBottle = new ItemStack(ACItemRegistry.RADON_BOTTLE.get());
            if (!level.isClientSide) {
                if (!player.addItem(radonBottle)) {
                    player.drop(radonBottle, false);
                }
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
            }
            player.playSound(SoundEvents.BOTTLE_FILL);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
    }

    protected int getSmokeType(LevelAccessor level, BlockPos pos) {
        BlockState belowState = level.getBlockState(pos.below());
        var belowFluid = belowState.getFluidState();
        if (belowState.getBlock() instanceof GeothermalVentBlock) {
            return belowState.getValue(SMOKE_TYPE);
        }
        if (belowState.is(ACBlockRegistry.ACIDIC_RADROCK.get()) || belowState.is(ACBlockRegistry.UNREFINED_WASTE.get()) || belowFluid.is(ACTagRegistry.ACID)) {
            return 3;
        }
        if (belowFluid.getType() == net.minecraft.world.level.material.Fluids.WATER) {
            return 1;
        }
        if (belowFluid.getType() == net.minecraft.world.level.material.Fluids.LAVA) {
            return 2;
        }
        return 0;
    }

    protected boolean isSpawningParticles(LevelAccessor level, BlockPos pos) {
        BlockState aboveState = level.getBlockState(pos.above());
        return aboveState.isAir() || !aboveState.blocksMotion();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GeothermalVentBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (!level.isClientSide || state.getValue(SMOKE_TYPE) <= 0 || !state.getValue(SPAWNING_PARTICLES)) {
            return null;
        }
        return createTickerHelper(blockEntityType, ACBlockEntityRegistry.GEOTHERMAL_VENT.get(), GeothermalVentBlockEntity::particleTick);
    }
}
