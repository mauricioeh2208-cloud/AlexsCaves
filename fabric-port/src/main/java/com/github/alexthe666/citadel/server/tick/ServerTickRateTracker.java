package com.github.alexthe666.citadel.server.tick;

import com.github.alexthe666.citadel.server.tick.modifier.TickRateModifier;
import net.minecraft.server.MinecraftServer;

import java.util.IdentityHashMap;
import java.util.Map;

public class ServerTickRateTracker extends TickRateTracker {
    private static final Map<MinecraftServer, ServerTickRateTracker> TRACKERS = new IdentityHashMap<>();
    private final MinecraftServer server;

    private ServerTickRateTracker(MinecraftServer server) {
        this.server = server;
    }

    public void addTickRateModifier(TickRateModifier modifier) {
        tickRateModifierList.add(modifier);
    }

    public int getServerTickLengthMs() {
        return 50;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public static ServerTickRateTracker getForServer(MinecraftServer server) {
        return TRACKERS.computeIfAbsent(server, ServerTickRateTracker::new);
    }

    public static void clear(MinecraftServer server) {
        TRACKERS.remove(server);
    }
}
