package com.github.alexmodguy.alexscaves.server.level.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class ACWorldSeedHolder {
    private static volatile long worldSeed;
    private static volatile ResourceKey<Level> currentDimension = Level.OVERWORLD;
    private static final ThreadLocal<ResourceKey<Level>> THREAD_DIMENSION = new ThreadLocal<>();

    private ACWorldSeedHolder() {
    }

    public static synchronized void setSeed(long seed) {
        worldSeed = seed;
    }

    public static long getSeed() {
        return worldSeed;
    }

    public static synchronized void setDimension(ResourceKey<Level> dimension) {
        currentDimension = dimension;
        THREAD_DIMENSION.set(dimension);
    }

    public static ResourceKey<Level> getDimension() {
        ResourceKey<Level> threadValue = THREAD_DIMENSION.get();
        return threadValue != null ? threadValue : currentDimension;
    }

    public static boolean isInitialized() {
        return worldSeed != 0L;
    }

    public static synchronized void reset() {
        worldSeed = 0L;
        currentDimension = Level.OVERWORLD;
        THREAD_DIMENSION.remove();
    }
}
