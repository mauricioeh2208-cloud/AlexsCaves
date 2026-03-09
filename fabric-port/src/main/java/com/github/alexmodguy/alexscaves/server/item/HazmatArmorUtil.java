package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class HazmatArmorUtil {
    private static final ResourceLocation HAZMAT_MASK = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "hazmat_mask");
    private static final ResourceLocation HAZMAT_CHESTPLATE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "hazmat_chestplate");
    private static final ResourceLocation HAZMAT_LEGGINGS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "hazmat_leggings");
    private static final ResourceLocation HAZMAT_BOOTS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "hazmat_boots");

    private HazmatArmorUtil() {
    }

    public static int getWornAmount(LivingEntity entity) {
        int wornAmount = 0;
        if (isItem(entity.getItemBySlot(EquipmentSlot.HEAD), HAZMAT_MASK)) {
            wornAmount++;
        }
        if (isItem(entity.getItemBySlot(EquipmentSlot.CHEST), HAZMAT_CHESTPLATE)) {
            wornAmount++;
        }
        if (isItem(entity.getItemBySlot(EquipmentSlot.LEGS), HAZMAT_LEGGINGS)) {
            wornAmount++;
        }
        if (isItem(entity.getItemBySlot(EquipmentSlot.FEET), HAZMAT_BOOTS)) {
            wornAmount++;
        }
        return wornAmount;
    }

    private static boolean isItem(ItemStack stack, ResourceLocation id) {
        return id.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }
}
