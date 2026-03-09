package com.github.alexmodguy.alexscaves.server.level.biome;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ACBiomeMapHolder {
    private static final Map<ResourceKey<Biome>, Holder<Biome>> BIOME_MAP = new ConcurrentHashMap<>();

    private ACBiomeMapHolder() {
    }

    public static Map<ResourceKey<Biome>, Holder<Biome>> getBiomeMap() {
        return BIOME_MAP;
    }

    public static boolean isInitialized() {
        return !BIOME_MAP.isEmpty();
    }

    public static synchronized void initializeFromRegistry(Registry<Biome> biomeRegistry) {
        BIOME_MAP.clear();
        for (ResourceKey<Biome> biomeKey : biomeRegistry.registryKeySet()) {
            Optional<Holder.Reference<Biome>> holder = biomeRegistry.getHolder(biomeKey);
            holder.ifPresent(value -> BIOME_MAP.put(biomeKey, value));
        }
    }

    public static synchronized void reset() {
        BIOME_MAP.clear();
    }
}
