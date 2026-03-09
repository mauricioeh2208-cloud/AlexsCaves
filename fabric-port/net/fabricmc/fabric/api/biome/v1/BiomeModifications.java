/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.biome.v1;

import java.util.function.Predicate;
import net.minecraft.class_1299;
import net.minecraft.class_1311;
import net.minecraft.class_2378;
import net.minecraft.class_2893;
import net.minecraft.class_2922;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_5483;
import net.minecraft.class_6796;
import com.google.common.base.Preconditions;

/**
 * Provides an API to modify Biomes after they have been loaded and before they are used in the World.
 *
 * <p>Any modifications made to biomes will not be available for use in server.properties (as of 1.16.1),
 * or the demo level.
 *
 * <p><b>Experimental feature</b>, may be removed or changed without further notice.
 */
public final class BiomeModifications {
	/**
	 * Convenience method to add a feature to one or more biomes.
	 *
	 * @see BiomeSelectors
	 */
	public static void addFeature(Predicate<BiomeSelectionContext> biomeSelector, class_2893.class_2895 step, class_5321<class_6796> placedFeatureRegistryKey) {
		create(placedFeatureRegistryKey.method_29177()).add(ModificationPhase.ADDITIONS, biomeSelector, context -> {
			context.getGenerationSettings().addFeature(step, placedFeatureRegistryKey);
		});
	}

	/**
	 * Convenience method to add a carver to one or more biomes.
	 *
	 * @see BiomeSelectors
	 */
	public static void addCarver(Predicate<BiomeSelectionContext> biomeSelector, class_2893.class_2894 step, class_5321<class_2922<?>> configuredCarverKey) {
		create(configuredCarverKey.method_29177()).add(ModificationPhase.ADDITIONS, biomeSelector, context -> {
			context.getGenerationSettings().addCarver(step, configuredCarverKey);
		});
	}

	/**
	 * Convenience method to add an entity spawn to one or more biomes.
	 *
	 * @see BiomeSelectors
	 * @see net.minecraft.class_5483.class_5496#method_31011(class_1311, class_5483.class_1964)
	 */
	public static void addSpawn(Predicate<BiomeSelectionContext> biomeSelector,
								class_1311 spawnGroup, class_1299<?> entityType,
								int weight, int minGroupSize, int maxGroupSize) {
		// See constructor of SpawnSettings.SpawnEntry for context
		Preconditions.checkArgument(entityType.method_5891() != class_1311.field_17715,
				"Cannot add spawns for entities with spawnGroup=MISC since they'd be replaced by pigs.");

		// We need the entity type to be registered or we cannot deduce an ID otherwisea
		class_2960 id = class_2378.field_11145.method_10221(entityType);
		Preconditions.checkState(id != class_2378.field_11145.method_10137(), "Unregistered entity type: %s", entityType);

		create(id).add(ModificationPhase.ADDITIONS, biomeSelector, context -> {
			context.getSpawnSettings().addSpawn(spawnGroup, new class_5483.class_1964(entityType, weight, minGroupSize, maxGroupSize));
		});
	}

	/**
	 * Create a new biome modification which will be applied whenever biomes are loaded from datapacks.
	 *
	 * @param id An identifier for the new set of biome modifications that is returned. Is used for
	 *           guaranteeing consistent ordering between the biome modifications added by different mods
	 *           (assuming they otherwise have the same phase).
	 */
	public static BiomeModification create(class_2960 id) {
		return new BiomeModification(id);
	}
}

