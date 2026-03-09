package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public final class ACFoods {
    public static final FoodProperties DARKENED_APPLE = food(4, 0.35F).alwaysEdible().build();
    public static final FoodProperties JELLY_BEAN = food(1, 0.05F).build();
    public static final FoodProperties LANTERNFISH = food(1, 0.175F).fast().build();
    public static final FoodProperties LANTERNFISH_COOKED = food(2, 0.3F).fast().build();
    public static final FoodProperties RADGILL = food(2, 0.2F).effect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.holder(), 2000), 1.0F).build();
    public static final FoodProperties RADGILL_COOKED = food(5, 0.3F).effect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.holder(), 1000), 0.1F).build();
    public static final FoodProperties SEA_PIG = food(1, 0.2F).effect(new MobEffectInstance(MobEffects.HUNGER, 1200), 0.7F).build();
    public static final FoodProperties TRILOCARIS_TAIL = food(2, 0.3F).build();
    public static final FoodProperties TRILOCARIS_TAIL_COOKED = food(5, 0.5F).build();
    public static final FoodProperties TRIPODFISH = food(2, 0.2F).build();
    public static final FoodProperties TRIPODFISH_COOKED = food(5, 0.34F).build();
    public static final FoodProperties SLAM = food(4, 0.5F).effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400), 1.0F).build();
    public static final FoodProperties SOYLENT_GREEN = food(3, 0.35F).alwaysEdible().build();
    public static final FoodProperties SPELUNKIE = food(2, 0.1F).fast().build();

    private ACFoods() {
    }

    private static FoodProperties.Builder food(int nutrition, float saturationModifier) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturationModifier);
    }
}
