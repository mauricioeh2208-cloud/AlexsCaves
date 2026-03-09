package com.github.alexmodguy.alexscaves.fabric.mixin;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeMapHolder;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACWorldSeedHolder;
import com.github.alexmodguy.alexscaves.server.level.biome.BiomeSourceAccessor;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerBiomeInitMixin {
    @Shadow
    public abstract WorldData getWorldData();

    @Inject(method = "getWorldData", at = @At("RETURN"))
    private void alexscaves$getWorldData(CallbackInfoReturnable<WorldData> cir) {
        if (cir.getReturnValue() == null || ACWorldSeedHolder.isInitialized()) {
            return;
        }
        try {
            MinecraftServer server = (MinecraftServer) (Object) this;
            ACWorldSeedHolder.setSeed(cir.getReturnValue().worldGenOptions().seed());
            Registry<Biome> biomeRegistry = server.registryAccess().registryOrThrow(Registries.BIOME);
            ACBiomeMapHolder.initializeFromRegistry(biomeRegistry);
            Registry<LevelStem> levelStems = server.registryAccess().registryOrThrow(Registries.LEVEL_STEM);
            ImmutableSet.Builder<Holder<Biome>> builder = ImmutableSet.builder();
            for (ResourceKey<Biome> biomeKey : ACBiomeRegistry.ALEXS_CAVES_BIOMES) {
                Optional<Holder.Reference<Biome>> holder = biomeRegistry.getHolder(biomeKey);
                holder.ifPresent(builder::add);
            }
            ImmutableSet<Holder<Biome>> acBiomes = builder.build();
            if (acBiomes.isEmpty()) {
                return;
            }
            for (ResourceKey<LevelStem> levelStemKey : levelStems.registryKeySet()) {
                Optional<Holder.Reference<LevelStem>> stemHolder = levelStems.getHolder(levelStemKey);
                if (stemHolder.isPresent() && stemHolder.get().value().generator().getBiomeSource() instanceof BiomeSourceAccessor accessor) {
                    accessor.expandBiomesWith(acBiomes);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Inject(method = "stopServer", at = @At("HEAD"))
    private void alexscaves$stopServer(CallbackInfo ci) {
        ACWorldSeedHolder.reset();
        ACBiomeMapHolder.reset();
    }
}
