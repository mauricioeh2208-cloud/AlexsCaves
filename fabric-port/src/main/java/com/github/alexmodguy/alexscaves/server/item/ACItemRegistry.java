package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.misc.registry.RegistryHandle;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.sounds.SoundEvents;

public final class ACItemRegistry {
    public static final Rarity RARITY_DEMONIC = Rarity.EPIC;
    public static final Rarity RARITY_NUCLEAR = Rarity.UNCOMMON;
    public static final Rarity RARITY_SWEET = Rarity.UNCOMMON;

    public static final RegistryHandle<Item> FERROUSLIME_BALL = register("ferrouslime_ball", new Item(new Item.Properties()));
    public static final RegistryHandle<Item> BIOLUMINESSCENCE = register("bioluminesscence", new Item(new Item.Properties()));
    public static final RegistryHandle<Item> CORRODENT_TEETH = register("corrodent_teeth", new Item(new Item.Properties()));
    public static final RegistryHandle<Item> SWEET_TOOTH = register("sweet_tooth", new Item(new Item.Properties().rarity(RARITY_SWEET)));
    public static final RegistryHandle<Item> POLYMER_PLATE = register("polymer_plate", new Item(new Item.Properties()));
    public static final RegistryHandle<Item> LANTERNFISH_BUCKET = register("lanternfish_bucket", new MobBucketItem(ACEntityRegistry.LANTERNFISH.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1)));
    public static final RegistryHandle<Item> LANTERNFISH = register("lanternfish", new Item(new Item.Properties().food(ACFoods.LANTERNFISH)));
    public static final RegistryHandle<Item> COOKED_LANTERNFISH = register("cooked_lanternfish", new Item(new Item.Properties().food(ACFoods.LANTERNFISH_COOKED)));
    public static final RegistryHandle<Item> SEA_PIG_BUCKET = register("sea_pig_bucket", new MobBucketItem(ACEntityRegistry.SEA_PIG.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1)));
    public static final RegistryHandle<Item> SEA_PIG = register("sea_pig", new Item(new Item.Properties().food(ACFoods.SEA_PIG)));
    public static final RegistryHandle<Item> TRILOCARIS_BUCKET = register("trilocaris_bucket", new MobBucketItem(ACEntityRegistry.TRILOCARIS.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1)));
    public static final RegistryHandle<Item> TRILOCARIS_TAIL = register("trilocaris_tail", new Item(new Item.Properties().food(ACFoods.TRILOCARIS_TAIL)));
    public static final RegistryHandle<Item> COOKED_TRILOCARIS_TAIL = register("cooked_trilocaris_tail", new Item(new Item.Properties().food(ACFoods.TRILOCARIS_TAIL_COOKED)));
    public static final RegistryHandle<Item> TRIPODFISH_BUCKET = register("tripodfish_bucket", new MobBucketItem(ACEntityRegistry.TRIPODFISH.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1)));
    public static final RegistryHandle<Item> TRIPODFISH = register("tripodfish", new Item(new Item.Properties().food(ACFoods.TRIPODFISH)));
    public static final RegistryHandle<Item> COOKED_TRIPODFISH = register("cooked_tripodfish", new Item(new Item.Properties().food(ACFoods.TRIPODFISH_COOKED)));
    public static final RegistryHandle<Item> MARINE_SNOW = register("marine_snow", new MarineSnowItem());
    public static final RegistryHandle<Item> PEARL = register("pearl", new Item(new Item.Properties()));
    public static final RegistryHandle<Item> RADGILL = register("radgill", new Item(new Item.Properties().food(ACFoods.RADGILL)));
    public static final RegistryHandle<Item> COOKED_RADGILL = register("cooked_radgill", new Item(new Item.Properties().food(ACFoods.RADGILL_COOKED)));
    public static final RegistryHandle<Item> RADGILL_BUCKET = register("radgill_bucket", new MobBucketItem(ACEntityRegistry.RADGILL.get(), ACFluidRegistry.ACID.get(), SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1)));
    public static final RegistryHandle<Item> URANIUM = register("uranium", new RadioactiveItem(new Item.Properties(), 0.001F));
    public static final RegistryHandle<Item> URANIUM_SHARD = register("uranium_shard", new RadioactiveItem(new Item.Properties(), 0.001F));
    public static final RegistryHandle<Item> SULFUR_DUST = register("sulfur_dust", new Item(new Item.Properties()));
    public static final RegistryHandle<Item> ACID_BUCKET = register("acid_bucket", new BucketItem(ACFluidRegistry.ACID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryHandle<Item> RADON_BOTTLE = register("radon_bottle", new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16)));
    public static final RegistryHandle<Item> SPELUNKIE = register("spelunkie", new RadiationRemovingFoodItem(new Item.Properties().food(ACFoods.SPELUNKIE)));
    public static final RegistryHandle<Item> SLAM = register("slam", new RadiationRemovingFoodItem(new Item.Properties().food(ACFoods.SLAM)));
    public static final RegistryHandle<Item> GREEN_SOYLENT = register("green_soylent", new RadiationRemovingFoodItem(new Item.Properties().food(ACFoods.SOYLENT_GREEN)));
    public static final RegistryHandle<Item> TOXIC_PASTE = register("toxic_paste", new Item(new Item.Properties()));
    public static final RegistryHandle<Item> HAZMAT_MASK = register("hazmat_mask", new HazmatArmorItem(ACArmorMaterialRegistry.HAZMAT_SUIT.holder(), ArmorItem.Type.HELMET, hazmatProperties(ArmorItem.Type.HELMET)));
    public static final RegistryHandle<Item> HAZMAT_CHESTPLATE = register("hazmat_chestplate", new HazmatArmorItem(ACArmorMaterialRegistry.HAZMAT_SUIT.holder(), ArmorItem.Type.CHESTPLATE, hazmatProperties(ArmorItem.Type.CHESTPLATE)));
    public static final RegistryHandle<Item> HAZMAT_LEGGINGS = register("hazmat_leggings", new HazmatArmorItem(ACArmorMaterialRegistry.HAZMAT_SUIT.holder(), ArmorItem.Type.LEGGINGS, hazmatProperties(ArmorItem.Type.LEGGINGS)));
    public static final RegistryHandle<Item> HAZMAT_BOOTS = register("hazmat_boots", new HazmatArmorItem(ACArmorMaterialRegistry.HAZMAT_SUIT.holder(), ArmorItem.Type.BOOTS, hazmatProperties(ArmorItem.Type.BOOTS)));
    public static final RegistryHandle<Item> FISSILE_CORE = register("fissile_core", new RadioactiveItem(new Item.Properties().rarity(Rarity.UNCOMMON), 0.001F));
    public static final RegistryHandle<Item> CHARRED_REMNANT = register("charred_remnant", new RadioactiveItem(new Item.Properties(), 0.0005F));
    public static final RegistryHandle<Item> DARKENED_APPLE = register("darkened_apple", new DarkenedAppleItem());
    public static final RegistryHandle<Item> JELLY_BEAN = register("jelly_bean", new JellyBeanItem());
    public static final RegistryHandle<Item> SPAWN_EGG_BRAINIAC = register("spawn_egg_brainiac", new SpawnEggItem(ACEntityRegistry.BRAINIAC.get(), 0x3E5136, 0xE87C9E, new Item.Properties()));
    public static final RegistryHandle<Item> SPAWN_EGG_GAMMAROACH = register("spawn_egg_gammaroach", new SpawnEggItem(ACEntityRegistry.GAMMAROACH.get(), 0x224227, 0xB5D80D, new Item.Properties()));
    public static final RegistryHandle<Item> SPAWN_EGG_LANTERNFISH = register("spawn_egg_lanternfish", new SpawnEggItem(ACEntityRegistry.LANTERNFISH.get(), 0x182538, 0xECA500, new Item.Properties()));
    public static final RegistryHandle<Item> SPAWN_EGG_NUCLEEPER = register("spawn_egg_nucleeper", new SpawnEggItem(ACEntityRegistry.NUCLEEPER.get(), 0x95A1A5, 0x00FF00, new Item.Properties()));
    public static final RegistryHandle<Item> SPAWN_EGG_RAYCAT = register("spawn_egg_raycat", new SpawnEggItem(ACEntityRegistry.RAYCAT.get(), 0x48B7B2, 0xF7F8CB, new Item.Properties()));
    public static final RegistryHandle<Item> SPAWN_EGG_RADGILL = register("spawn_egg_radgill", new SpawnEggItem(ACEntityRegistry.RADGILL.get(), 0x43302C, 0xE8E400, new Item.Properties()));
    public static final RegistryHandle<Item> SPAWN_EGG_SEA_PIG = register("spawn_egg_sea_pig", new SpawnEggItem(ACEntityRegistry.SEA_PIG.get(), 0xFFA3B9, 0xF88672, new Item.Properties()));
    public static final RegistryHandle<Item> SPAWN_EGG_TRILOCARIS = register("spawn_egg_trilocaris", new SpawnEggItem(ACEntityRegistry.TRILOCARIS.get(), 0x713E0D, 0x8B2010, new Item.Properties()));
    public static final RegistryHandle<Item> SPAWN_EGG_TRIPODFISH = register("spawn_egg_tripodfish", new SpawnEggItem(ACEntityRegistry.TRIPODFISH.get(), 0x34529D, 0x81A1CF, new Item.Properties()));

    private ACItemRegistry() {
    }

    public static void init() {
    }

    private static Item.Properties hazmatProperties(ArmorItem.Type type) {
        return new Item.Properties().durability(ACArmorMaterialRegistry.hazmatDurabilityFor(type));
    }

    private static RegistryHandle<Item> register(String path, Item item) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, path);
        return new RegistryHandle<>(id, Registry.register(BuiltInRegistries.ITEM, id, item));
    }
}
