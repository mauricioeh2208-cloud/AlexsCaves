package com.github.alexmodguy.alexscaves.fabric.mixin;

import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationNoiseCondition;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeMapHolder;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACWorldSeedHolder;
import com.github.alexmodguy.alexscaves.server.level.biome.BiomeSourceAccessor;
import com.github.alexmodguy.alexscaves.server.level.biome.MultiNoiseBiomeSourceAccessor;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = MultiNoiseBiomeSource.class, priority = -69420)
public class MultiNoiseBiomeSourceMixin implements MultiNoiseBiomeSourceAccessor {
    @Unique
    private long alexscaves$lastSampledWorldSeed;

    @Unique
    private ResourceKey<Level> alexscaves$lastSampledDimension;

    @Inject(
        method = "Lnet/minecraft/world/level/biome/MultiNoiseBiomeSource;getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void alexscaves$getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        long seed = alexscaves$lastSampledWorldSeed != 0L ? alexscaves$lastSampledWorldSeed : ACWorldSeedHolder.getSeed();
        ResourceKey<Level> dimension = alexscaves$lastSampledDimension != null ? alexscaves$lastSampledDimension : ACWorldSeedHolder.getDimension();
        if (seed == 0L || dimension == null || !dimension.equals(Level.OVERWORLD)) {
            return;
        }

        Map<ResourceKey<Biome>, Holder<Biome>> biomeMap = ACBiomeMapHolder.getBiomeMap();
        if (biomeMap.isEmpty()) {
            return;
        }
        Holder<Biome> toxicCaves = biomeMap.get(ACBiomeRegistry.TOXIC_CAVES);
        if (toxicCaves == null) {
            return;
        }
        if (this instanceof BiomeSourceAccessor accessor) {
            accessor.expandBiomesWith(ImmutableSet.of(toxicCaves));
        }

        VoronoiGenerator.VoronoiInfo voronoiInfo = ACBiomeRarity.getRareBiomeInfoForQuad(seed, x, z);
        if (voronoiInfo == null) {
            return;
        }
        float unquantizedDepth = Climate.unquantizeCoord(sampler.sample(x, y, z).depth());
        synchronized (BiomeGenerationConfig.BIOMES_LOCK) {
            BiomeGenerationNoiseCondition condition = BiomeGenerationConfig.BIOMES.get(ACBiomeRegistry.TOXIC_CAVES);
            if (condition != null && ACBiomeRarity.getRareBiomeOffsetId(voronoiInfo) == condition.getRarityOffset() && condition.test(x, y, z, unquantizedDepth, sampler, dimension, voronoiInfo)) {
                cir.setReturnValue(toxicCaves);
            }
        }
    }

    @Override
    public void setLastSampledSeed(long seed) {
        alexscaves$lastSampledWorldSeed = seed;
        ACWorldSeedHolder.setSeed(seed);
    }

    @Override
    public void setLastSampledDimension(ResourceKey<Level> dimension) {
        alexscaves$lastSampledDimension = dimension;
        ACWorldSeedHolder.setDimension(dimension);
    }
}
