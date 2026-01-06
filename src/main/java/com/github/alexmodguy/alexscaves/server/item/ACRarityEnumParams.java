package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.awt.*;
import java.util.function.UnaryOperator;

/**
 * Parameter providers for custom Rarity enum extensions.
 * This class is used by the enum extension system to create custom rarities.
 * 
 * Original colors from AlexsCaves:
 * - DEMONIC: DARK_RED
 * - NUCLEAR: GREEN  
 * - SWEET: Pink (0xFF8ACD)
 * - RAINBOW: Animated HSB rainbow
 */
public class ACRarityEnumParams {
    
    /**
     * DEMONIC rarity - Dark red color for demonic/fiery items
     */
    public static final EnumProxy<Rarity> RARITY_DEMONIC_PROXY = new EnumProxy<>(
            Rarity.class, -1, "alexscaves:demonic",
            (UnaryOperator<Style>) style -> style.withColor(ChatFormatting.DARK_RED)
    );
    
    /**
     * NUCLEAR rarity - Green color for radioactive/nuclear items
     */
    public static final EnumProxy<Rarity> RARITY_NUCLEAR_PROXY = new EnumProxy<>(
            Rarity.class, -1, "alexscaves:nuclear",
            (UnaryOperator<Style>) style -> style.withColor(ChatFormatting.GREEN)
    );
    
    /**
     * SWEET rarity - Pink color for candy/sweet items
     */
    public static final EnumProxy<Rarity> RARITY_SWEET_PROXY = new EnumProxy<>(
            Rarity.class, -1, "alexscaves:sweet",
            (UnaryOperator<Style>) style -> style.withColor(0xFF8ACD)
    );
    
    /**
     * RAINBOW rarity - Animated rainbow color for special items
     * Note: The rainbow animation is applied via a UnaryOperator that calculates
     * the current color based on system time.
     */
    public static final EnumProxy<Rarity> RARITY_RAINBOW_PROXY = new EnumProxy<>(
            Rarity.class, -1, "alexscaves:rainbow",
            (UnaryOperator<Style>) style -> style.withColor(Color.HSBtoRGB((System.currentTimeMillis() % 5000) / 5000F, 1f, 1F))
    );
}
