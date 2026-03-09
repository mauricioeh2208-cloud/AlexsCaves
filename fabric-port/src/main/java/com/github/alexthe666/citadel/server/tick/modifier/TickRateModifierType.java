package com.github.alexthe666.citadel.server.tick.modifier;

public enum TickRateModifierType {
    LOCAL_ENTITY(true);

    private final boolean local;

    TickRateModifierType(boolean local) {
        this.local = local;
    }

    public boolean isLocal() {
        return local;
    }
}
