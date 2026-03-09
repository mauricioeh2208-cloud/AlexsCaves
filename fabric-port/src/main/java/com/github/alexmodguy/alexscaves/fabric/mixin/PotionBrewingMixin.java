package com.github.alexmodguy.alexscaves.fabric.mixin;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionBrewing.class)
public abstract class PotionBrewingMixin {

    @Inject(method = "addVanillaMixes", at = @At("TAIL"))
    private static void alexscaves$addBrewingRecipes(PotionBrewing.Builder builder, CallbackInfo ci) {
        builder.addMix(Potions.AWKWARD, ACItemRegistry.FERROUSLIME_BALL.get(), ACEffectRegistry.MAGNETIZING_POTION.holder());
        builder.addMix(ACEffectRegistry.MAGNETIZING_POTION.holder(), Items.REDSTONE, ACEffectRegistry.LONG_MAGNETIZING_POTION.holder());
        builder.addMix(Potions.AWKWARD, ACItemRegistry.LANTERNFISH.get(), ACEffectRegistry.DEEPSIGHT_POTION.holder());
        builder.addMix(ACEffectRegistry.DEEPSIGHT_POTION.holder(), Items.REDSTONE, ACEffectRegistry.LONG_DEEPSIGHT_POTION.holder());
        builder.addMix(Potions.AWKWARD, ACItemRegistry.BIOLUMINESSCENCE.get(), ACEffectRegistry.GLOWING_POTION.holder());
        builder.addMix(ACEffectRegistry.GLOWING_POTION.holder(), Items.REDSTONE, ACEffectRegistry.LONG_GLOWING_POTION.holder());
        builder.addMix(Potions.AWKWARD, ACItemRegistry.CORRODENT_TEETH.get(), ACEffectRegistry.HASTE_POTION.holder());
        builder.addMix(ACEffectRegistry.HASTE_POTION.holder(), Items.REDSTONE, ACEffectRegistry.LONG_HASTE_POTION.holder());
        builder.addMix(ACEffectRegistry.HASTE_POTION.holder(), Items.GLOWSTONE_DUST, ACEffectRegistry.STRONG_HASTE_POTION.holder());
        builder.addMix(Potions.STRONG_SWIFTNESS, ACItemRegistry.SWEET_TOOTH.get(), ACEffectRegistry.SUGAR_RUSH_POTION.holder());
        builder.addMix(ACEffectRegistry.SUGAR_RUSH_POTION.holder(), Items.REDSTONE, ACEffectRegistry.LONG_SUGAR_RUSH_POTION.holder());
    }
}
