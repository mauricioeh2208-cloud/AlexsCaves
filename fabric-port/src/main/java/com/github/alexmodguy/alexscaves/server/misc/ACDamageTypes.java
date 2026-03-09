package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class ACDamageTypes {
    public static final ResourceKey<DamageType> ACID = key("acid");
    public static final ResourceKey<DamageType> NUKE = key("nuke");
    public static final ResourceKey<DamageType> RADIATION = key("radiation");
    public static final ResourceKey<DamageType> RAYGUN = key("raygun");
    public static final ResourceKey<DamageType> FORSAKEN_SONIC_BOOM = key("forsaken_sonic_boom");
    public static final ResourceKey<DamageType> DESOLATE_DAGGER = key("desolate_dagger");
    public static final ResourceKey<DamageType> DARK_ARROW = key("dark_arrow");
    public static final ResourceKey<DamageType> SPIRIT_DINOSAUR = key("spirit_dinosaur");
    public static final ResourceKey<DamageType> TREMORZILLA_BEAM = key("tremorzilla_beam");
    public static final ResourceKey<DamageType> GUMBALL = key("gumball");
    public static final ResourceKey<DamageType> INTENTIONAL_GAME_DESIGN = key("intentional_game_design");

    private ACDamageTypes() {
    }

    public static void init() {
    }

    public static DamageSource causeAcidDamage(RegistryAccess registryAccess) {
        return new DamageSourceRandomMessages(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ACID), 1);
    }

    public static DamageSource causeRadiationDamage(RegistryAccess registryAccess) {
        return new DamageSourceRandomMessages(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(RADIATION), 2);
    }

    public static DamageSource causeNukeDamage(RegistryAccess registryAccess) {
        return new DamageSourceRandomMessages(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(NUKE), 4);
    }

    public static DamageSource causeRaygunDamage(RegistryAccess registryAccess, Entity source) {
        return new DamageSourceRandomMessages(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(RAYGUN), source, 1);
    }

    public static DamageSource causeForsakenSonicBoomDamage(RegistryAccess registryAccess, Entity source) {
        return new DamageSourceRandomMessages(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(FORSAKEN_SONIC_BOOM), source, 2);
    }

    public static DamageSource causeDesolateDaggerDamage(RegistryAccess registryAccess, Entity source) {
        return new DamageSourceRandomMessages(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DESOLATE_DAGGER), source, 1);
    }

    public static DamageSource causeDarkArrowDamage(RegistryAccess registryAccess, Entity source) {
        return new DamageSourceRandomMessages(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DARK_ARROW), source, 1);
    }

    public static DamageSource causeSpiritDinosaurDamage(RegistryAccess registryAccess, Entity source) {
        return new DamageSourceRandomMessages(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(SPIRIT_DINOSAUR), source, 1);
    }

    public static DamageSource causeTremorzillaBeamDamage(RegistryAccess registryAccess, Entity source) {
        return new DamageSourceRandomMessages(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(TREMORZILLA_BEAM), source, 1);
    }

    public static DamageSource causeGumballDamage(RegistryAccess registryAccess, Entity source) {
        return new DamageSourceRandomMessages(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(GUMBALL), source, 1);
    }

    public static DamageSource causeIntentionalGameDesign(RegistryAccess registryAccess) {
        return new DamageSourceRandomMessages(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(INTENTIONAL_GAME_DESIGN), 1);
    }

    private static ResourceKey<DamageType> key(String path) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, path));
    }

    private static class DamageSourceRandomMessages extends DamageSource {
        private final int messageCount;

        DamageSourceRandomMessages(Holder<DamageType> damageType, int messageCount) {
            super(damageType);
            this.messageCount = messageCount;
        }

        DamageSourceRandomMessages(Holder<DamageType> damageType, Entity source, int messageCount) {
            super(damageType, source);
            this.messageCount = messageCount;
        }

        @Override
        public Component getLocalizedDeathMessage(LivingEntity attacked) {
            int type = attacked.getRandom().nextInt(this.messageCount);
            String key = "death.attack." + this.getMsgId() + "_" + type;
            Entity entity = this.getDirectEntity() == null ? this.getEntity() : this.getDirectEntity();
            if (entity != null) {
                return Component.translatable(key + ".entity", attacked.getDisplayName(), entity.getDisplayName());
            }
            return Component.translatable(key, attacked.getDisplayName());
        }
    }
}
