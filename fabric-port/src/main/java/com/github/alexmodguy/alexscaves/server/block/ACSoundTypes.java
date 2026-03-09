package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.world.level.block.SoundType;

public final class ACSoundTypes {
    public static final SoundType RADROCK = new SoundType(
        1.0F,
        1.0F,
        ACSoundRegistry.RADROCK_BREAK.get(),
        ACSoundRegistry.RADROCK_STEP.get(),
        ACSoundRegistry.RADROCK_PLACE.get(),
        ACSoundRegistry.RADROCK_BREAKING.get(),
        ACSoundRegistry.RADROCK_STEP.get()
    );
    public static final SoundType SULFUR = new SoundType(
        1.0F,
        1.0F,
        ACSoundRegistry.SULFUR_BREAK.get(),
        ACSoundRegistry.SULFUR_STEP.get(),
        ACSoundRegistry.SULFUR_PLACE.get(),
        ACSoundRegistry.SULFUR_BREAKING.get(),
        ACSoundRegistry.SULFUR_STEP.get()
    );
    public static final SoundType HAZMAT_BLOCK = new SoundType(
        1.0F,
        1.0F,
        ACSoundRegistry.HAZMAT_BLOCK_BREAK.get(),
        ACSoundRegistry.HAZMAT_BLOCK_STEP.get(),
        ACSoundRegistry.HAZMAT_BLOCK_PLACE.get(),
        ACSoundRegistry.HAZMAT_BLOCK_BREAKING.get(),
        ACSoundRegistry.HAZMAT_BLOCK_STEP.get()
    );
    public static final SoundType UNREFINED_WASTE = new SoundType(
        1.0F,
        1.0F,
        ACSoundRegistry.UNREFINED_WASTE_BREAK.get(),
        ACSoundRegistry.UNREFINED_WASTE_STEP.get(),
        ACSoundRegistry.UNREFINED_WASTE_PLACE.get(),
        ACSoundRegistry.UNREFINED_WASTE_BREAKING.get(),
        ACSoundRegistry.UNREFINED_WASTE_STEP.get()
    );
    public static final SoundType URANIUM = new SoundType(
        1.0F,
        1.0F,
        ACSoundRegistry.URANIUM_BREAK.get(),
        ACSoundRegistry.URANIUM_STEP.get(),
        ACSoundRegistry.URANIUM_PLACE.get(),
        ACSoundRegistry.URANIUM_BREAKING.get(),
        ACSoundRegistry.URANIUM_STEP.get()
    );
    public static final SoundType SCRAP_METAL = new SoundType(
        1.0F,
        1.0F,
        ACSoundRegistry.SCRAP_METAL_BREAK.get(),
        ACSoundRegistry.SCRAP_METAL_STEP.get(),
        ACSoundRegistry.SCRAP_METAL_PLACE.get(),
        ACSoundRegistry.SCRAP_METAL_BREAKING.get(),
        ACSoundRegistry.SCRAP_METAL_STEP.get()
    );

    private ACSoundTypes() {
    }
}
