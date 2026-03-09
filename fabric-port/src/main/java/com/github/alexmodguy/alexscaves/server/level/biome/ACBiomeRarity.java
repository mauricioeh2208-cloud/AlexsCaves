package com.github.alexmodguy.alexscaves.server.level.biome;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationNoiseCondition;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;

public final class ACBiomeRarity {
    private static final List<Integer> BIOME_OCTAVES = List.of(0);
    private static final PerlinSimplexNoise NOISE_X = new PerlinSimplexNoise(new XoroshiroRandomSource(1234L), BIOME_OCTAVES);
    private static final PerlinSimplexNoise NOISE_Z = new PerlinSimplexNoise(new XoroshiroRandomSource(4321L), BIOME_OCTAVES);
    private static final VoronoiGenerator VORONOI_GENERATOR = new VoronoiGenerator(42L);
    private static final double BIOME_BOUNDARY_EXTENSION = 1.0D;

    private static double biomeSize;
    private static double separationDistance;
    private static volatile boolean initialized;

    private ACBiomeRarity() {
    }

    public static void init() {
        VORONOI_GENERATOR.setOffsetAmount(AlexsCaves.COMMON_CONFIG.caveBiomeSpacingRandomness.get());
        biomeSize = AlexsCaves.COMMON_CONFIG.caveBiomeMeanWidth.get() * 0.25D;
        separationDistance = biomeSize + AlexsCaves.COMMON_CONFIG.caveBiomeMeanSeparation.get() * 0.25D;
        initialized = true;
    }

    private static void ensureInitialized() {
        if (!initialized) {
            synchronized (VORONOI_GENERATOR) {
                if (!initialized) {
                    init();
                }
            }
        }
    }

    public static VoronoiGenerator.VoronoiInfo getRareBiomeInfoForQuad(long worldSeed, int x, int z) {
        ensureInitialized();
        if (separationDistance <= 0.0D) {
            return null;
        }
        synchronized (VORONOI_GENERATOR) {
            VORONOI_GENERATOR.setSeed(worldSeed);
            double sampleX = x / separationDistance;
            double sampleZ = z / separationDistance;
            double positionOffsetX = AlexsCaves.COMMON_CONFIG.caveBiomeWidthRandomness.get() * NOISE_X.getValue(sampleX, sampleZ, false);
            double positionOffsetZ = AlexsCaves.COMMON_CONFIG.caveBiomeWidthRandomness.get() * NOISE_Z.getValue(sampleX, sampleZ, false);
            VoronoiGenerator.VoronoiInfo info = VORONOI_GENERATOR.get2(sampleX + positionOffsetX, sampleZ + positionOffsetZ);
            return info.distance() < (biomeSize / separationDistance) * BIOME_BOUNDARY_EXTENSION ? info : null;
        }
    }

    public static Vec3 getRareBiomeCenter(VoronoiGenerator.VoronoiInfo info) {
        return info == null ? null : info.cellPos().scale(separationDistance);
    }

    public static int getRareBiomeOffsetId(VoronoiGenerator.VoronoiInfo info) {
        double normalized = (info.hash() + 1.0D) * 0.5D;
        int biomeCount = BiomeGenerationConfig.getBiomeCount();
        int offset = (int) (normalized * biomeCount);
        return Math.min(offset, Math.max(0, biomeCount - 1));
    }

    public static ResourceKey<Biome> getACBiomeForOffset(int rarityOffset) {
        synchronized (BiomeGenerationConfig.BIOMES_LOCK) {
            for (Map.Entry<ResourceKey<Biome>, BiomeGenerationNoiseCondition> entry : BiomeGenerationConfig.BIOMES.entrySet()) {
                if (entry.getValue().getRarityOffset() == rarityOffset) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
}
