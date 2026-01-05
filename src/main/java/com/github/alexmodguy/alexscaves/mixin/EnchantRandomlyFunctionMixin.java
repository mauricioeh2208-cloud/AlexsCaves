package com.github.alexmodguy.alexscaves.mixin;

// TODO: This entire mixin needs to be rewritten for 1.21
// In 1.21, enchantments are data-driven and no longer classes:
// - ACWeaponEnchantment class no longer exists - enchantments are ResourceLocations
// - BuiltInRegistries.ENCHANTMENT doesn't exist - enchantments are in a dynamic registry accessed via LootContext
// - Enchantment::isDiscoverable method doesn't exist
// - EnchantedBookItem.addEnchantment and itemStack.enchant take Holder<Enchantment>, not Enchantment
//
// To fix this properly, you need to:
// 1. Check enchantments by their ResourceLocation (e.g., alexscaves:extinction, alexscaves:toxic_rush, etc.)
// 2. Get enchantments from the LootContext's RegistryAccess
// 3. Use Holder<Enchantment> for all enchantment operations
//
// For now, this mixin is disabled to allow compilation. The feature of preventing
// AC enchantments in loot will not work until this is reimplemented.

import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {
    // Mixin disabled - see TODO above
}
