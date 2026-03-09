package com.github.alexmodguy.alexscaves.server.potion;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.registry.RegistryHandle;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.effect.MobEffects;

public final class ACEffectRegistry {
    public static final RegistryHandle<MobEffect> BUBBLED = registerEffect("bubbled", new BubbledEffect());
    public static final RegistryHandle<MobEffect> DARKNESS_INCARNATE = registerEffect("darkness_incarnate", new DarknessIncarnateEffect());
    public static final RegistryHandle<MobEffect> IRRADIATED = registerEffect("irradiated", new IrradiatedEffect());
    public static final RegistryHandle<MobEffect> MAGNETIZING = registerEffect("magnetizing", new MagnetizedEffect());
    public static final RegistryHandle<MobEffect> RAGE = registerEffect("rage", new RageEffect());
    public static final RegistryHandle<MobEffect> STUNNED = registerEffect("stunned", new StunnedEffect());
    public static final RegistryHandle<MobEffect> SUGAR_RUSH = registerEffect("sugar_rush", new SugarRushEffect());
    public static final RegistryHandle<MobEffect> DEEPSIGHT = registerEffect("deepsight", new DeepsightEffect());

    public static final RegistryHandle<Potion> MAGNETIZING_POTION = registerPotion("magnetizing", new Potion(new MobEffectInstance(MAGNETIZING.holder(), 3600)));
    public static final RegistryHandle<Potion> LONG_MAGNETIZING_POTION = registerPotion("long_magnetizing", new Potion(new MobEffectInstance(MAGNETIZING.holder(), 9600)));
    public static final RegistryHandle<Potion> DEEPSIGHT_POTION = registerPotion("deepsight", new Potion(new MobEffectInstance(DEEPSIGHT.holder(), 3600)));
    public static final RegistryHandle<Potion> LONG_DEEPSIGHT_POTION = registerPotion("long_deepsight", new Potion(new MobEffectInstance(DEEPSIGHT.holder(), 9600)));
    public static final RegistryHandle<Potion> GLOWING_POTION = registerPotion("glowing", new Potion(new MobEffectInstance(MobEffects.GLOWING, 3600)));
    public static final RegistryHandle<Potion> LONG_GLOWING_POTION = registerPotion("long_glowing", new Potion(new MobEffectInstance(MobEffects.GLOWING, 9600)));
    public static final RegistryHandle<Potion> HASTE_POTION = registerPotion("haste", new Potion(new MobEffectInstance(MobEffects.DIG_SPEED, 3600)));
    public static final RegistryHandle<Potion> LONG_HASTE_POTION = registerPotion("long_haste", new Potion(new MobEffectInstance(MobEffects.DIG_SPEED, 9600)));
    public static final RegistryHandle<Potion> STRONG_HASTE_POTION = registerPotion("strong_haste", new Potion(new MobEffectInstance(MobEffects.DIG_SPEED, 1800, 1)));
    public static final RegistryHandle<Potion> SUGAR_RUSH_POTION = registerPotion("sugar_rush", new Potion(new MobEffectInstance(SUGAR_RUSH.holder(), 1800)));
    public static final RegistryHandle<Potion> LONG_SUGAR_RUSH_POTION = registerPotion("long_sugar_rush", new Potion(new MobEffectInstance(SUGAR_RUSH.holder(), 3600)));

    private ACEffectRegistry() {
    }

    public static void init() {
    }

    public static ItemStack createPotion(RegistryHandle<Potion> potion) {
        ItemStack stack = new ItemStack(Items.POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion.holder()));
        return stack;
    }

    public static ItemStack createSplashPotion(RegistryHandle<Potion> potion) {
        ItemStack stack = new ItemStack(Items.SPLASH_POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion.holder()));
        return stack;
    }

    public static ItemStack createLingeringPotion(RegistryHandle<Potion> potion) {
        ItemStack stack = new ItemStack(Items.LINGERING_POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion.holder()));
        return stack;
    }

    public static ItemStack createJellybean(RegistryHandle<Potion> potion) {
        ItemStack stack = new ItemStack(ACItemRegistry.JELLY_BEAN.get());
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion.holder()));
        return stack;
    }

    private static RegistryHandle<MobEffect> registerEffect(String path, MobEffect effect) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, path);
        return new RegistryHandle<>(id, Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, id, effect));
    }

    private static RegistryHandle<Potion> registerPotion(String path, Potion potion) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, path);
        return new RegistryHandle<>(id, Registry.registerForHolder(BuiltInRegistries.POTION, id, potion));
    }
}
