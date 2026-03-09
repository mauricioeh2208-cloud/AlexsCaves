package com.github.alexmodguy.alexscaves.fabric.mixin;

import com.github.alexmodguy.alexscaves.server.level.biome.BiomeSourceAccessor;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(BiomeSource.class)
public class BiomeSourceMixin implements BiomeSourceAccessor {
    @Shadow
    @Final
    @Mutable
    public Supplier<Set<Holder<Biome>>> possibleBiomes;

    @Unique
    private boolean alexscaves$expanded;

    @Unique
    private Map<ResourceKey<Biome>, Holder<Biome>> alexscaves$biomeMap = new HashMap<>();

    @Override
    public void setResourceKeyMap(Map<ResourceKey<Biome>, Holder<Biome>> map) {
        alexscaves$biomeMap = map;
    }

    @Override
    public Map<ResourceKey<Biome>, Holder<Biome>> getResourceKeyMap() {
        return alexscaves$biomeMap;
    }

    @Override
    public synchronized void expandBiomesWith(Set<Holder<Biome>> newBiomes) {
        if (alexscaves$expanded || newBiomes.isEmpty()) {
            return;
        }
        ImmutableSet.Builder<Holder<Biome>> builder = ImmutableSet.builder();
        builder.addAll(this.possibleBiomes.get());
        builder.addAll(newBiomes);
        this.possibleBiomes = Suppliers.memoize(builder::build);
        alexscaves$expanded = true;
    }
}
