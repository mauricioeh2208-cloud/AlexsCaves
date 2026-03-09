package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.item.RadioactiveBlockItem;
import com.github.alexmodguy.alexscaves.server.misc.registry.RegistryHandle;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public final class ACBlockRegistry {
    private static final BlockBehaviour.Properties RADROCK_PROPERTIES = BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_LIGHT_GREEN)
        .requiresCorrectToolForDrops()
        .strength(4.0F, 11.0F)
        .sound(ACSoundTypes.RADROCK);
    private static final BlockBehaviour.Properties RADON_LAMP_PROPERTIES = BlockBehaviour.Properties.of()
        .mapColor(MapColor.TERRACOTTA_WHITE)
        .requiresCorrectToolForDrops()
        .lightLevel(state -> 15)
        .strength(2.0F, 11.0F)
        .sound(SoundType.GLASS);

    public static final RegistryHandle<Block> RADROCK = register("radrock", new Block(RADROCK_PROPERTIES));
    public static final RegistryHandle<Block> RADROCK_STAIRS = register("radrock_stairs", new StairBlock(RADROCK.get().defaultBlockState(), RADROCK_PROPERTIES));
    public static final RegistryHandle<Block> RADROCK_SLAB = register("radrock_slab", new SlabBlock(RADROCK_PROPERTIES));
    public static final RegistryHandle<Block> RADROCK_WALL = register("radrock_wall", new WallBlock(RADROCK_PROPERTIES));
    public static final RegistryHandle<Block> RADROCK_BRICKS = register("radrock_bricks", new Block(RADROCK_PROPERTIES));
    public static final RegistryHandle<Block> RADROCK_BRICK_STAIRS = register("radrock_brick_stairs", new StairBlock(RADROCK_BRICKS.get().defaultBlockState(), RADROCK_PROPERTIES));
    public static final RegistryHandle<Block> RADROCK_BRICK_SLAB = register("radrock_brick_slab", new SlabBlock(RADROCK_PROPERTIES));
    public static final RegistryHandle<Block> RADROCK_BRICK_WALL = register("radrock_brick_wall", new WallBlock(RADROCK_PROPERTIES));
    public static final RegistryHandle<Block> RADROCK_CHISELED = register("radrock_chiseled", new Block(RADROCK_PROPERTIES));
    public static final RegistryHandle<Block> RADROCK_URANIUM_ORE = register("radrock_uranium_ore", new RadrockUraniumOreBlock(), block -> new RadioactiveBlockItem(block, new Item.Properties(), 0.001F));
    public static final RegistryHandle<Block> WASTE_DRUM = register("waste_drum", new WasteDrumBlock());
    public static final RegistryHandle<Block> ACID = registerWithoutItem("acid", new AcidBlock());
    public static final RegistryHandle<Block> ACIDIC_RADROCK = register("acidic_radrock", new AcidicRadrockBlock());
    public static final RegistryHandle<Block> GEOTHERMAL_VENT = register("geothermal_vent", new GeothermalVentBlock());
    public static final RegistryHandle<Block> GEOTHERMAL_VENT_MEDIUM = register("geothermal_vent_medium", new ThinGeothermalVentBlock(12));
    public static final RegistryHandle<Block> GEOTHERMAL_VENT_THIN = register("geothermal_vent_thin", new ThinGeothermalVentBlock(8));
    public static final RegistryHandle<Block> URANIUM_ROD = register("uranium_rod", new UraniumRodBlock());
    public static final RegistryHandle<Block> BLOCK_OF_URANIUM = register("block_of_uranium", new UraniumFullBlock(), block -> new RadioactiveBlockItem(block, new Item.Properties(), 0.001F));
    public static final RegistryHandle<Block> UNREFINED_WASTE = register("unrefined_waste", new UnrefinedWasteBlock(), block -> new RadioactiveBlockItem(block, new Item.Properties(), 0.001F));
    public static final RegistryHandle<Block> SULFUR = register("sulfur", new SulfurBlock());
    public static final RegistryHandle<Block> SULFUR_BUD_SMALL = register("sulfur_bud_small", new SulfurBudBlock(6, 4));
    public static final RegistryHandle<Block> SULFUR_BUD_MEDIUM = register("sulfur_bud_medium", new SulfurBudBlock(6, 8));
    public static final RegistryHandle<Block> SULFUR_BUD_LARGE = register("sulfur_bud_large", new SulfurBudBlock(6, 12));
    public static final RegistryHandle<Block> SULFUR_CLUSTER = register("sulfur_cluster", new SulfurBudBlock(6, 14));
    public static final RegistryHandle<Block> HAZMAT_BLOCK = register("hazmat_block", new HazmatBlock());
    public static final RegistryHandle<Block> HAZMAT_WARNING_BLOCK = register("hazmat_warning_block", new HazmatBlock());
    public static final RegistryHandle<Block> HAZMAT_SKULL_BLOCK = register("hazmat_skull_block", new HazmatBlock());
    public static final RegistryHandle<Block> WHITE_RADON_LAMP = register("radon_lamp_white", new Block(RADON_LAMP_PROPERTIES));
    public static final RegistryHandle<Block> ORANGE_RADON_LAMP = register("radon_lamp_orange", new Block(RADON_LAMP_PROPERTIES));
    public static final RegistryHandle<Block> MAGENTA_RADON_LAMP = register("radon_lamp_magenta", new Block(RADON_LAMP_PROPERTIES));
    public static final RegistryHandle<Block> LIGHT_BLUE_RADON_LAMP = register("radon_lamp_light_blue", new Block(RADON_LAMP_PROPERTIES));
    public static final RegistryHandle<Block> YELLOW_RADON_LAMP = register("radon_lamp_yellow", new Block(RADON_LAMP_PROPERTIES));
    public static final RegistryHandle<Block> LIME_RADON_LAMP = register("radon_lamp_lime", new Block(RADON_LAMP_PROPERTIES));
    public static final RegistryHandle<Block> PINK_RADON_LAMP = register("radon_lamp_pink", new Block(RADON_LAMP_PROPERTIES));
    public static final RegistryHandle<Block> GRAY_RADON_LAMP = register("radon_lamp_gray", new Block(RADON_LAMP_PROPERTIES));
    public static final RegistryHandle<Block> LIGHT_GRAY_RADON_LAMP = register("radon_lamp_light_gray", new Block(RADON_LAMP_PROPERTIES));
    public static final RegistryHandle<Block> CYAN_RADON_LAMP = register("radon_lamp_cyan", new Block(RADON_LAMP_PROPERTIES));
    public static final RegistryHandle<Block> PURPLE_RADON_LAMP = register("radon_lamp_purple", new Block(RADON_LAMP_PROPERTIES));
    public static final RegistryHandle<Block> BLUE_RADON_LAMP = register("radon_lamp_blue", new Block(RADON_LAMP_PROPERTIES));
    public static final RegistryHandle<Block> BROWN_RADON_LAMP = register("radon_lamp_brown", new Block(RADON_LAMP_PROPERTIES));
    public static final RegistryHandle<Block> GREEN_RADON_LAMP = register("radon_lamp_green", new Block(RADON_LAMP_PROPERTIES));
    public static final RegistryHandle<Block> RED_RADON_LAMP = register("radon_lamp_red", new Block(RADON_LAMP_PROPERTIES));
    public static final RegistryHandle<Block> BLACK_RADON_LAMP = register("radon_lamp_black", new Block(RADON_LAMP_PROPERTIES));

    private ACBlockRegistry() {
    }

    public static void init() {
    }

    private static RegistryHandle<Block> registerWithoutItem(String path, Block block) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, path);
        return new RegistryHandle<>(id, Registry.register(BuiltInRegistries.BLOCK, id, block));
    }

    private static RegistryHandle<Block> register(String path, Block block) {
        return register(path, block, registeredBlock -> new BlockItem(registeredBlock, new Item.Properties()));
    }

    private static RegistryHandle<Block> register(String path, Block block, Function<Block, Item> itemFactory) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, path);
        Block registeredBlock = Registry.register(BuiltInRegistries.BLOCK, id, block);
        Registry.register(BuiltInRegistries.ITEM, id, itemFactory.apply(registeredBlock));
        return new RegistryHandle<>(id, registeredBlock);
    }
}
