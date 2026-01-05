package com.github.alexmodguy.alexscaves.server.enchantment;

/**
 * Enchantments are now data-driven in NeoForge 1.21.
 * The Enchantment class is final and cannot be extended.
 * This class is kept for reference but enchantment behavior
 * must be implemented via effects in JSON definitions.
 * 
 * TODO: Implement enchantment behavior via data-driven effects or
 * by checking enchantment levels in item/entity code manually.
 * 
 * @deprecated Enchantment is final in 1.21. Use ResourceKey references from ACEnchantmentRegistry instead.
 */
@Deprecated
public class ACWeaponEnchantment {
    // Enchantment class is final in 1.21 - cannot extend
    // See data/alexscaves/enchantment/ for JSON definitions
}
