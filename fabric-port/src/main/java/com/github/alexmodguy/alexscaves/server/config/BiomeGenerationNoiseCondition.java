package com.github.alexmodguy.alexscaves.server.config;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BiomeGenerationNoiseCondition {
    private final boolean disabledCompletely;
    private final int distanceFromSpawn;
    private final int rarityOffset;
    private final float[] continentalness;
    private final float[] depth;
    private final List<String> dimensions;

    private BiomeGenerationNoiseCondition(boolean disabledCompletely, int distanceFromSpawn, int rarityOffset, float[] continentalness, float[] depth, String[] dimensions) {
        this.disabledCompletely = disabledCompletely;
        this.distanceFromSpawn = distanceFromSpawn;
        this.rarityOffset = rarityOffset;
        this.continentalness = continentalness;
        this.depth = depth;
        this.dimensions = dimensions == null || dimensions.length == 0 ? List.of() : List.of(dimensions);
    }

    public boolean test(int x, int y, int z, float unquantizedDepth, Climate.Sampler climateSampler, ResourceKey<Level> dimension, VoronoiGenerator.VoronoiInfo info) {
        if (disabledCompletely) {
            return false;
        }
        Vec3 biomeCenter = ACBiomeRarity.getRareBiomeCenter(info);
        if (biomeCenter == null) {
            return false;
        }
        int centerBlockX = (int) biomeCenter.x * 4;
        int centerBlockZ = (int) biomeCenter.z * 4;
        if (centerBlockX * centerBlockX + centerBlockZ * centerBlockZ < distanceFromSpawn * distanceFromSpawn) {
            return false;
        }
        Climate.TargetPoint centerTargetPoint = climateSampler.sample((int) Math.floor(biomeCenter.x), y, (int) Math.floor(biomeCenter.z));
        float localContinentalness = Climate.unquantizeCoord(centerTargetPoint.continentalness());
        if (continentalness != null && continentalness.length >= 2 && (localContinentalness < continentalness[0] || localContinentalness > continentalness[1])) {
            return false;
        }
        if (depth != null && depth.length >= 2 && (unquantizedDepth < depth[0] || unquantizedDepth > depth[1])) {
            return false;
        }
        return dimension == null || dimensions.isEmpty() || dimensions.contains(dimension.location().toString());
    }

    public int getRarityOffset() {
        return rarityOffset;
    }

    public static final class Builder {
        private boolean disabledCompletely;
        private int distanceFromSpawn;
        private int rarityOffset;
        private float[] continentalness;
        private float[] depth;
        private String[] dimensions;

        public Builder disabledCompletely(boolean disabledCompletely) {
            this.disabledCompletely = disabledCompletely;
            return this;
        }

        public Builder distanceFromSpawn(int distanceFromSpawn) {
            this.distanceFromSpawn = distanceFromSpawn;
            return this;
        }

        public Builder alexscavesRarityOffset(int rarityOffset) {
            this.rarityOffset = rarityOffset;
            return this;
        }

        public Builder continentalness(float... continentalness) {
            this.continentalness = continentalness;
            return this;
        }

        public Builder depth(float... depth) {
            this.depth = depth;
            return this;
        }

        public Builder dimensions(String... dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public BiomeGenerationNoiseCondition build() {
            return new BiomeGenerationNoiseCondition(disabledCompletely, distanceFromSpawn, rarityOffset, continentalness, depth, dimensions);
        }
    }
}
