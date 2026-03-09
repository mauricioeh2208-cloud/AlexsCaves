package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.misc.registry.RegistryHandle;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class ACFeatureRegistry {
    public static final RegistryHandle<Feature<NoneFeatureConfiguration>> ACID_VENT = register("acid_vent", new AcidVentFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryHandle<Feature<NoneFeatureConfiguration>> SULFUR_STACK = register("sulfur_stack", new SulfurStackFeature(NoneFeatureConfiguration.CODEC));

    private ACFeatureRegistry() {
    }

    public static void init() {
    }

    private static <T extends Feature<?>> RegistryHandle<T> register(String path, T feature) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, path);
        return new RegistryHandle<>(id, Registry.register(BuiltInRegistries.FEATURE, id, feature));
    }
}
