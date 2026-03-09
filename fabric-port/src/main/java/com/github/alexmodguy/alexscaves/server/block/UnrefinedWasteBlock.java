package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class UnrefinedWasteBlock extends FallingBlockWithColor {
    public static final MapCodec<UnrefinedWasteBlock> CODEC = simpleCodec(UnrefinedWasteBlock::new);
    private static final VoxelShape SHAPE = Block.box(0.1D, 0.1D, 0.1D, 15.9D, 14.0D, 15.9D);

    public UnrefinedWasteBlock() {
        this(BlockBehaviour.Properties.of().mapColor(DyeColor.LIME).strength(0.5F).sound(ACSoundTypes.UNREFINED_WASTE).lightLevel(state -> 3).emissiveRendering((state, level, pos) -> true));
    }

    private UnrefinedWasteBlock(BlockBehaviour.Properties properties) {
        super(properties, 0x00EE00);
    }

    @Override
    protected MapCodec<? extends FallingBlockWithColor> codec() {
        return CODEC;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos blockPos, Entity entity) {
        if (entity instanceof LivingEntity living && !entity.getType().is(ACTagRegistry.RESISTS_RADIATION)) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.9D, 1.0D, 0.9D));
            living.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.holder(), 4000));
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos blockPos) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (randomSource.nextInt(2) != 0) {
            return;
        }
        Direction direction = Direction.getRandom(randomSource);
        BlockPos offsetPos = blockPos.relative(direction);
        BlockState offsetState = level.getBlockState(offsetPos);
        if (!blockState.canOcclude() || !offsetState.isFaceSturdy(level, offsetPos, direction.getOpposite())) {
            double x = direction.getStepX() == 0 ? randomSource.nextDouble() : 0.5D + direction.getStepX() * 0.6D;
            double y = direction.getStepY() == 0 ? randomSource.nextDouble() : 0.5D + direction.getStepY() * 0.6D;
            double z = direction.getStepZ() == 0 ? randomSource.nextDouble() : 0.5D + direction.getStepZ() * 0.6D;
            level.addParticle(randomSource.nextBoolean() ? ACParticleRegistry.GAMMAROACH.get() : ACParticleRegistry.HAZMAT_BREATHE.get(), blockPos.getX() + x, blockPos.getY() + y, blockPos.getZ() + z, 0.0D, 0.1D + level.random.nextFloat() * 0.1F, 0.0D);
        }
    }
}
