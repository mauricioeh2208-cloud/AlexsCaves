package com.github.alexmodguy.alexscaves;

import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.client.config.ACClientConfig;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.CommonProxy;
import com.github.alexmodguy.alexscaves.server.config.ACServerConfig;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACArmorMaterialRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.feature.ACFeatureRegistry;
import com.github.alexmodguy.alexscaves.server.message.ACNetworking;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class AlexsCaves {
    public static final String MODID = "alexscaves";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CommonProxy PROXY = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? new ClientProxy() : new CommonProxy();
    public static final ACServerConfig COMMON_CONFIG = ACServerConfig.INSTANCE;
    public static final ACClientConfig CLIENT_CONFIG = ACClientConfig.INSTANCE;
    public static final List<String> MOD_GENERATION_CONFLICTS = new ArrayList<>();
    private static MinecraftServer currentServer;
    private static boolean initialized;

    private AlexsCaves() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        ServerLifecycleEvents.SERVER_STARTED.register(server -> currentServer = server);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ServerTickRateTracker.clear(server));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> currentServer = null);
        ServerTickEvents.START_SERVER_TICK.register(server -> ServerTickRateTracker.getForServer(server).masterTick());
        ACTagRegistry.init();
        ACDamageTypes.init();
        ACSoundRegistry.init();
        ACParticleRegistry.init();
        ACEffectRegistry.init();
        ACFluidRegistry.init();
        ACArmorMaterialRegistry.init();
        ACEntityRegistry.init();
        ACItemRegistry.init();
        ACBlockRegistry.init();
        ACBlockEntityRegistry.init();
        ACBiomeRegistry.init();
        BiomeGenerationConfig.reloadConfig();
        ACBiomeRarity.init();
        ACFeatureRegistry.init();
        ACNetworking.init();
        PROXY.commonInit();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            PROXY.clientInit();
        }
        readModIncompatibilities();
    }

    public static void sendMSGToServer(CustomPacketPayload message) {
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(message);
    }

    public static void sendMSGToAll(CustomPacketPayload message) {
        if (currentServer == null) {
            return;
        }
        for (ServerPlayer player : PlayerLookup.all(currentServer)) {
            sendNonLocal(message, player);
        }
    }

    public static void sendNonLocal(CustomPacketPayload message, ServerPlayer player) {
        ServerPlayNetworking.send(player, message);
    }

    private static void readModIncompatibilities() {
        try (BufferedReader reader = openModIncompatibilitySource()) {
            if (reader == null) {
                LOGGER.warn("Failed to load mod conflicts");
                return;
            }
            String line;
            while ((line = reader.readLine()) != null) {
                MOD_GENERATION_CONFLICTS.add(line);
            }
        } catch (IOException exception) {
            LOGGER.warn("Failed to load mod conflicts", exception);
        }
    }

    private static BufferedReader openModIncompatibilitySource() {
        try {
            InputStream remoteStream = new URL("https://raw.githubusercontent.com/AlexModGuy/AlexsCaves/main/src/main/resources/assets/alexscaves/warning/mod_generation_conflicts.txt").openStream();
            return new BufferedReader(new InputStreamReader(remoteStream, StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }
        InputStream localStream = AlexsCaves.class.getClassLoader().getResourceAsStream("assets/alexscaves/warning/mod_generation_conflicts.txt");
        if (localStream == null) {
            return null;
        }
        return new BufferedReader(new InputStreamReader(localStream, StandardCharsets.UTF_8));
    }
}
