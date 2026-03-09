package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.misc.registry.RegistryHandle;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ACBlockEntityRegistry {
    public static final RegistryHandle<BlockEntityType<GeothermalVentBlockEntity>> GEOTHERMAL_VENT = register(
        "geothermal_vent",
        BlockEntityType.Builder.of(
            GeothermalVentBlockEntity::new,
            ACBlockRegistry.GEOTHERMAL_VENT.get(),
            ACBlockRegistry.GEOTHERMAL_VENT_MEDIUM.get(),
            ACBlockRegistry.GEOTHERMAL_VENT_THIN.get()
        ).build(null)
    );

    private ACBlockEntityRegistry() {
    }

    public static void init() {
    }

    private static <T extends BlockEntityType<?>> RegistryHandle<T> register(String path, T blockEntityType) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, path);
        return new RegistryHandle<>(id, Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, blockEntityType));
    }
}
