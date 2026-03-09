package com.github.alexmodguy.alexscaves.server.misc.registry;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

public final class RegistryHandle<T> {
    private final ResourceLocation id;
    private final T value;
    private final Holder.Reference<T> holder;

    public RegistryHandle(ResourceLocation id, T value) {
        this(id, value, null);
    }

    public RegistryHandle(ResourceLocation id, Holder.Reference<T> holder) {
        this(id, holder.value(), holder);
    }

    private RegistryHandle(ResourceLocation id, T value, Holder.Reference<T> holder) {
        this.id = id;
        this.value = value;
        this.holder = holder;
    }

    public ResourceLocation id() {
        return id;
    }

    public T get() {
        return value;
    }

    public Holder.Reference<T> holder() {
        if (holder == null) {
            throw new IllegalStateException("No holder available for registry entry " + id);
        }
        return holder;
    }
}
