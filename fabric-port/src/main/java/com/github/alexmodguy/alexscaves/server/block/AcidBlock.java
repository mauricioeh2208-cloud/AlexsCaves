package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.item.HazmatArmorItem;
import com.github.alexmodguy.alexscaves.server.item.HazmatArmorUtil;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class AcidBlock extends LiquidBlock {
    public static final MapCodec<AcidBlock> CODEC = simpleCodec(AcidBlock::new);
    private static final Map<Block, Block> CORRODES_INTERACTIONS = Util.make(new HashMap<>(), map -> {
        map.put(Blocks.COPPER_BLOCK, Blocks.WEATHERED_COPPER);
        map.put(Blocks.WEATHERED_COPPER, Blocks.EXPOSED_COPPER);
        map.put(Blocks.EXPOSED_COPPER, Blocks.OXIDIZED_COPPER);
        map.put(Blocks.CUT_COPPER, Blocks.WEATHERED_CUT_COPPER);
        map.put(Blocks.WEATHERED_CUT_COPPER, Blocks.EXPOSED_CUT_COPPER);
        map.put(Blocks.EXPOSED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER);
        map.put(Blocks.CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB);
        map.put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB);
        map.put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB);
        map.put(Blocks.CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS);
        map.put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS);
        map.put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS);
    });

    public AcidBlock() {
        this(BlockBehaviour.Properties.ofLegacyCopy(Blocks.WATER)
            .mapColor(MapColor.COLOR_LIGHT_GREEN)
            .lightLevel(state -> 5)
            .pushReaction(PushReaction.DESTROY));
    }

    private AcidBlock(BlockBehaviour.Properties properties) {
        super(ACFluidRegistry.ACID.get(), properties);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MapCodec<LiquidBlock> codec() {
        return (MapCodec<LiquidBlock>) (MapCodec<?>) CODEC;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if (randomSource.nextInt(400) == 0) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ACSoundRegistry.ACID_IDLE.get(), SoundSource.BLOCKS, 0.5F, randomSource.nextFloat() * 0.4F + 0.8F, false);
        }
        if (randomSource.nextInt(10) == 0 && level.getFluidState(pos.above()).isEmpty()) {
            SimpleParticleType particleType = ACParticleRegistry.ACID_DROP.get();
            level.addParticle(
                particleType,
                pos.getX() + randomSource.nextDouble(),
                pos.getY() + 0.9D,
                pos.getZ() + randomSource.nextDouble(),
                (randomSource.nextDouble() - 0.5D) * 0.02D,
                0.03D + randomSource.nextDouble() * 0.02D,
                (randomSource.nextDouble() - 0.5D) * 0.02D
            );
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!entity.getType().is(ACTagRegistry.RESISTS_ACID)) {
            boolean armor = false;
            boolean hurtSound = false;
            float damageMultiplier = 1.0F;
            if (entity instanceof LivingEntity living) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    if (!slot.isArmor()) {
                        continue;
                    }
                    ItemStack item = living.getItemBySlot(slot);
                    if (!item.isEmpty() && item.isDamageableItem() && !(item.getItem() instanceof HazmatArmorItem)) {
                        armor = true;
                        if (!level.isClientSide && level.random.nextFloat() < 0.05F && !(living instanceof Player player && player.isCreative())) {
                            item.hurtAndBreak(1, living, slot);
                        }
                    }
                }
                damageMultiplier = 1.0F - HazmatArmorUtil.getWornAmount(living) / 4.0F;
            }
            if (damageMultiplier > 0.0F) {
                float bonusDamage = entity.getType().is(ACTagRegistry.WEAK_TO_ACID) ? 10.0F : 0.0F;
                float damage = damageMultiplier * (armor ? 0.01F : 1.0F) + bonusDamage;
                hurtSound = entity.hurt(ACDamageTypes.causeAcidDamage(level.registryAccess()), damage);
            }
            if (hurtSound) {
                entity.playSound(ACSoundRegistry.ACID_BURN.get(), 1.0F, 1.0F);
            }
        }
        if (entity instanceof LivingEntity living && living.tickCount % 20 == 0 && living.getDeltaMovement().horizontalDistanceSqr() > 0.003D) {
            float swimVolume = Math.min(1.0F, (float) living.getDeltaMovement().length());
            entity.playSound(ACSoundRegistry.ACID_SWIM.get(), swimVolume, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            tickCorrosion(level, pos);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        if (!level.isClientSide) {
            tickCorrosion(level, pos);
        }
    }

    private void tickCorrosion(Level level, BlockPos pos) {
        if (tickFluidInteractions(level, pos)) {
            return;
        }
        for (Direction direction : Direction.values()) {
            BlockPos offset = pos.relative(direction);
            BlockState neighborState = level.getBlockState(offset);
            Block transformedBlock = CORRODES_INTERACTIONS.get(neighborState.getBlock());
            if (transformedBlock == null) {
                continue;
            }
            level.setBlockAndUpdate(offset, copySharedProperties(neighborState, transformedBlock.defaultBlockState()));
            level.playSound(null, offset, ACSoundRegistry.ACID_CORROSION.get(), SoundSource.BLOCKS, 1.0F, 0.9F + level.random.nextFloat() * 0.2F);
        }
    }

    private boolean tickFluidInteractions(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos offset = pos.relative(direction);
            if (level.getFluidState(offset).is(FluidTags.WATER)) {
                level.setBlockAndUpdate(pos, Blocks.MUD.defaultBlockState());
                level.playSound(null, pos, ACSoundRegistry.ACID_CORROSION.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                return true;
            }
            if (level.getFluidState(offset).is(FluidTags.LAVA)) {
                level.setBlockAndUpdate(pos, ACBlockRegistry.RADROCK.get().defaultBlockState());
                level.playSound(null, pos, ACSoundRegistry.ACID_CORROSION.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static BlockState copySharedProperties(BlockState from, BlockState to) {
        for (Property property : from.getProperties()) {
            if (to.hasProperty(property)) {
                to = to.setValue(property, from.getValue(property));
            }
        }
        return to;
    }
}
