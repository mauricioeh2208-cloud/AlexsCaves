package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.misc.registry.RegistryHandle;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;

public final class ACArmorMaterialRegistry {
    private static final int HAZMAT_DURABILITY_MULTIPLIER = 20;

    public static final RegistryHandle<ArmorMaterial> HAZMAT_SUIT = register("hazmat_suit", new ArmorMaterial(
        Map.of(
            ArmorItem.Type.BOOTS, 2,
            ArmorItem.Type.LEGGINGS, 5,
            ArmorItem.Type.CHESTPLATE, 4,
            ArmorItem.Type.HELMET, 2,
            ArmorItem.Type.BODY, 4
        ),
        25,
        SoundEvents.ARMOR_EQUIP_IRON,
        () -> Ingredient.of(ACItemRegistry.POLYMER_PLATE.get()),
        List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "hazmat_suit"))),
        0.5F,
        0.0F
    ));

    private ACArmorMaterialRegistry() {
    }

    public static void init() {
    }

    public static int hazmatDurabilityFor(ArmorItem.Type type) {
        return type.getDurability(HAZMAT_DURABILITY_MULTIPLIER);
    }

    private static RegistryHandle<ArmorMaterial> register(String path, ArmorMaterial material) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, path);
        return new RegistryHandle<>(id, Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, id, material));
    }
}
