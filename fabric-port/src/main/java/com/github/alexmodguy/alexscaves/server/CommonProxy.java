package com.github.alexmodguy.alexscaves.server;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CommonProxy {

    public void commonInit() {
    }

    public void clientInit() {
    }

    public void initPathfinding() {
    }

    public void setModEventBus(Object ignored) {
    }

    public void playWorldSound(Object soundEmitter, byte type) {
    }

    public Player getClientSidePlayer() {
        return null;
    }

    public boolean isTickRateModificationActive(Level level) {
        return level.tickRateManager().tickrate() != 20.0F;
    }
}
