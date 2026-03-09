package com.github.alexmodguy.alexscaves.server.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.material.MapColor;

public class HazmatBlock extends RotatedPillarBlock {
    public static final MapCodec<HazmatBlock> CODEC = simpleCodec(HazmatBlock::new);

    public HazmatBlock() {
        this(Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(3.5F, 12.0F).sound(ACSoundTypes.HAZMAT_BLOCK));
    }

    private HazmatBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends RotatedPillarBlock> codec() {
        return CODEC;
    }
}
