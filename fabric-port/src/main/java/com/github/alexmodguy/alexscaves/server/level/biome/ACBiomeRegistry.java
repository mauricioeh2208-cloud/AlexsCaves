package com.github.alexmodguy.alexscaves.server.level.biome;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

public final class ACBiomeRegistry {
    public static final ResourceKey<Biome> TOXIC_CAVES = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "toxic_caves"));
    public static final List<ResourceKey<Biome>> ALEXS_CAVES_BIOMES = List.of(TOXIC_CAVES);

    private ACBiomeRegistry() {
    }

    public static void init() {
    }
}
