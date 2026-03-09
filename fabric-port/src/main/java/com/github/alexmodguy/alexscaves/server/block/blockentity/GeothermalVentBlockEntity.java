package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.GeothermalVentBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GeothermalVentBlockEntity extends BlockEntity {
    private static final double PARTICLE_DIST = 120.0D * 120.0D;

    private int soundTime;

    public GeothermalVentBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.GEOTHERMAL_VENT.get(), pos, state);
    }

    public static void particleTick(Level level, BlockPos pos, BlockState state, GeothermalVentBlockEntity blockEntity) {
        Player player = AlexsCaves.PROXY.getClientSidePlayer();
        if (player == null || player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > PARTICLE_DIST || level.random.nextBoolean()) {
            return;
        }

        ParticleOptions particle = switch (state.getValue(GeothermalVentBlock.SMOKE_TYPE)) {
            case 1 -> level.random.nextInt(3) == 0 ? ParticleTypes.POOF : ACParticleRegistry.WHITE_VENT_SMOKE.get();
            case 2 -> level.random.nextInt(3) == 0 ? ParticleTypes.SQUID_INK : ACParticleRegistry.BLACK_VENT_SMOKE.get();
            case 3 -> level.random.nextInt(3) == 0 ? ACParticleRegistry.ACID_BUBBLE.get() : ACParticleRegistry.GREEN_VENT_SMOKE.get();
            default -> ParticleTypes.SMOKE;
        };

        double xOffset = (level.random.nextFloat() - 0.5F) * 0.25F;
        double zOffset = (level.random.nextFloat() - 0.5F) * 0.25F;
        level.addAlwaysVisibleParticle(particle, true, pos.getX() + 0.5D + xOffset, pos.getY() + 1.0D, pos.getZ() + 0.5D + zOffset, xOffset * 0.15D, 0.03D + level.random.nextFloat() * 0.2D, zOffset * 0.15D);

        if (blockEntity.soundTime-- <= 0) {
            blockEntity.soundTime = level.random.nextInt(20) + 30;
            boolean underwater = !state.getFluidState().isEmpty() || !level.getBlockState(pos.above()).getFluidState().isEmpty();
            level.playLocalSound(
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                underwater ? ACSoundRegistry.GEOTHERMAL_VENT_BUBBLE_UNDERWATER.get() : ACSoundRegistry.GEOTHERMAL_VENT_BUBBLE.get(),
                SoundSource.BLOCKS,
                underwater ? 2.5F : 1.5F,
                level.random.nextFloat() * 0.4F + 0.8F,
                false
            );
        }
    }
}
