package com.github.alexmodguy.alexscaves.server.config;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.LinkedHashMap;

public final class BiomeGenerationConfig {
    private static final String OVERWORLD = "minecraft:overworld";
    private static final BiomeGenerationNoiseCondition TOXIC_CAVES_CONDITION = new BiomeGenerationNoiseCondition.Builder()
        .dimensions(OVERWORLD)
        .distanceFromSpawn(650)
        .alexscavesRarityOffset(0)
        .continentalness(0.5F, 1.0F)
        .depth(0.05F, 2.0F)
        .build();

    public static final LinkedHashMap<ResourceKey<Biome>, BiomeGenerationNoiseCondition> BIOMES = new LinkedHashMap<>();
    public static final Object BIOMES_LOCK = new Object();

    private BiomeGenerationConfig() {
    }

    public static void reloadConfig() {
        synchronized (BIOMES_LOCK) {
            BIOMES.clear();
            BIOMES.put(ACBiomeRegistry.TOXIC_CAVES, TOXIC_CAVES_CONDITION);
        }
    }

    public static int getBiomeCount() {
        synchronized (BIOMES_LOCK) {
            return BIOMES.size();
        }
    }
}
