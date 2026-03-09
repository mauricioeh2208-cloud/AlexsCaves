package com.github.alexmodguy.alexscaves.fabric;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.fabricmc.api.ModInitializer;

public final class AlexsCavesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        AlexsCaves.init();
    }
}
