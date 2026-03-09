package com.github.alexmodguy.alexscaves.server.block.fluid;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.misc.registry.RegistryHandle;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;

public final class ACFluidRegistry {
    public static final RegistryHandle<FlowingFluid> ACID = register("acid", new AcidFluid.Source());
    public static final RegistryHandle<FlowingFluid> FLOWING_ACID = register("acid_flowing", new AcidFluid.Flowing());

    private ACFluidRegistry() {
    }

    public static void init() {
    }

    private static RegistryHandle<FlowingFluid> register(String path, FlowingFluid fluid) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, path);
        return new RegistryHandle<>(id, Registry.register(BuiltInRegistries.FLUID, id, fluid));
    }
}
