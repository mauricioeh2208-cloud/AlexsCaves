package com.github.alexmodguy.alexscaves.server.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.fabric.mixin.SpawnPlacementsAccessor;
import com.github.alexmodguy.alexscaves.server.entity.item.ThrownWasteDrumEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.BrainiacEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GammaroachEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.LanternfishEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.NucleeperEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.RadgillEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.RaycatEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.SeaPigEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TrilocarisEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TripodfishEntity;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.misc.registry.RegistryHandle;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.BlockPos;

public final class ACEntityRegistry {
    private static final SpawnPlacementType IN_ACID = new SpawnPlacementType() {
        @Override
        public boolean isSpawnPositionOk(LevelReader levelReader, BlockPos pos, EntityType<?> entityType) {
            return levelReader.getFluidState(pos).is(ACTagRegistry.ACID);
        }
    };

    public static final RegistryHandle<EntityType<RadgillEntity>> RADGILL = register(
        "radgill",
        EntityType.Builder.of(RadgillEntity::new, MobCategory.WATER_AMBIENT)
            .sized(0.9F, 0.6F)
            .clientTrackingRange(8)
            .updateInterval(3)
            .build("alexscaves:radgill")
    );
    public static final RegistryHandle<EntityType<LanternfishEntity>> LANTERNFISH = register(
        "lanternfish",
        EntityType.Builder.of(LanternfishEntity::new, MobCategory.WATER_AMBIENT)
            .sized(0.5F, 0.4F)
            .clientTrackingRange(8)
            .updateInterval(3)
            .build("alexscaves:lanternfish")
    );
    public static final RegistryHandle<EntityType<SeaPigEntity>> SEA_PIG = register(
        "sea_pig",
        EntityType.Builder.of(SeaPigEntity::new, MobCategory.WATER_AMBIENT)
            .sized(0.5F, 0.65F)
            .clientTrackingRange(8)
            .updateInterval(3)
            .build("alexscaves:sea_pig")
    );
    public static final RegistryHandle<EntityType<TripodfishEntity>> TRIPODFISH = register(
        "tripodfish",
        EntityType.Builder.of(TripodfishEntity::new, MobCategory.WATER_AMBIENT)
            .sized(0.95F, 0.5F)
            .clientTrackingRange(8)
            .updateInterval(3)
            .build("alexscaves:tripodfish")
    );
    public static final RegistryHandle<EntityType<TrilocarisEntity>> TRILOCARIS = register(
        "trilocaris",
        EntityType.Builder.of(TrilocarisEntity::new, MobCategory.WATER_AMBIENT)
            .sized(0.9F, 0.4F)
            .clientTrackingRange(8)
            .updateInterval(3)
            .build("alexscaves:trilocaris")
    );
    public static final RegistryHandle<EntityType<GammaroachEntity>> GAMMAROACH = register(
        "gammaroach",
        EntityType.Builder.of(GammaroachEntity::new, MobCategory.AMBIENT)
            .sized(1.25F, 0.9F)
            .clientTrackingRange(8)
            .updateInterval(3)
            .build("alexscaves:gammaroach")
    );
    public static final RegistryHandle<EntityType<NucleeperEntity>> NUCLEEPER = register(
        "nucleeper",
        EntityType.Builder.of(NucleeperEntity::new, MobCategory.MONSTER)
            .sized(0.98F, 3.95F)
            .clientTrackingRange(8)
            .updateInterval(3)
            .build("alexscaves:nucleeper")
    );
    public static final RegistryHandle<EntityType<BrainiacEntity>> BRAINIAC = register(
        "brainiac",
        EntityType.Builder.of(BrainiacEntity::new, MobCategory.MONSTER)
            .sized(1.3F, 2.5F)
            .clientTrackingRange(8)
            .updateInterval(3)
            .build("alexscaves:brainiac")
    );
    public static final RegistryHandle<EntityType<ThrownWasteDrumEntity>> THROWN_WASTE_DRUM = register(
        "thrown_waste_drum",
        EntityType.Builder.of(ThrownWasteDrumEntity::new, MobCategory.MISC)
            .sized(0.98F, 0.98F)
            .clientTrackingRange(10)
            .updateInterval(1)
            .build("alexscaves:thrown_waste_drum")
    );
    public static final RegistryHandle<EntityType<RaycatEntity>> RAYCAT = register(
        "raycat",
        EntityType.Builder.of(RaycatEntity::new, MobCategory.CREATURE)
            .sized(0.85F, 0.6F)
            .clientTrackingRange(8)
            .updateInterval(3)
            .build("alexscaves:raycat")
    );

    private ACEntityRegistry() {
    }

    public static void init() {
        FabricDefaultAttributeRegistry.register(RADGILL.get(), RadgillEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(LANTERNFISH.get(), LanternfishEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SEA_PIG.get(), SeaPigEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(TRIPODFISH.get(), TripodfishEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(TRILOCARIS.get(), TrilocarisEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GAMMAROACH.get(), GammaroachEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(NUCLEEPER.get(), NucleeperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(BRAINIAC.get(), BrainiacEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(RAYCAT.get(), RaycatEntity.createAttributes());
        SpawnPlacementsAccessor.alexscaves$register(RADGILL.get(), IN_ACID, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, RadgillEntity::checkRadgillSpawnRules);
        SpawnPlacementsAccessor.alexscaves$register(LANTERNFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LanternfishEntity::checkLanternfishSpawnRules);
        SpawnPlacementsAccessor.alexscaves$register(SEA_PIG.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SeaPigEntity::checkSeaPigSpawnRules);
        SpawnPlacementsAccessor.alexscaves$register(TRIPODFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TripodfishEntity::checkTripodfishSpawnRules);
        SpawnPlacementsAccessor.alexscaves$register(TRILOCARIS.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TrilocarisEntity::checkTrilocarisSpawnRules);
        SpawnPlacementsAccessor.alexscaves$register(GAMMAROACH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GammaroachEntity::checkGammaroachSpawnRules);
        SpawnPlacementsAccessor.alexscaves$register(NUCLEEPER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NucleeperEntity::checkNucleeperSpawnRules);
        SpawnPlacementsAccessor.alexscaves$register(BRAINIAC.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BrainiacEntity::checkBrainiacSpawnRules);
        SpawnPlacementsAccessor.alexscaves$register(RAYCAT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, RaycatEntity::checkRaycatSpawnRules);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(ACBiomeRegistry.TOXIC_CAVES), MobCategory.WATER_AMBIENT, RADGILL.get(), 10, 1, 3);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.DEEP_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), MobCategory.WATER_AMBIENT, LANTERNFISH.get(), 12, 4, 10);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.DEEP_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), MobCategory.WATER_AMBIENT, SEA_PIG.get(), 8, 1, 4);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.DEEP_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), MobCategory.WATER_AMBIENT, TRIPODFISH.get(), 7, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.LUSH_CAVES, Biomes.DRIPSTONE_CAVES), MobCategory.WATER_AMBIENT, TRILOCARIS.get(), 10, 1, 3);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(ACBiomeRegistry.TOXIC_CAVES), MobCategory.AMBIENT, GAMMAROACH.get(), 55, 8, 8);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(ACBiomeRegistry.TOXIC_CAVES), MobCategory.MONSTER, NUCLEEPER.get(), 45, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(ACBiomeRegistry.TOXIC_CAVES), MobCategory.MONSTER, BRAINIAC.get(), 20, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(ACBiomeRegistry.TOXIC_CAVES), MobCategory.CREATURE, RAYCAT.get(), 10, 1, 1);
    }

    private static <T extends EntityType<?>> RegistryHandle<T> register(String path, T entityType) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, path);
        return new RegistryHandle<>(id, Registry.register(BuiltInRegistries.ENTITY_TYPE, id, entityType));
    }
}
