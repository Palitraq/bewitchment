/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.registry;

import moriyashiine.bewitchment.common.BWConfig;
import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.entity.living.GhostEntity;
import moriyashiine.bewitchment.common.entity.living.HellhoundEntity;
import moriyashiine.bewitchment.common.entity.living.VampireEntity;
import moriyashiine.bewitchment.common.entity.living.WerewolfEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.function.Predicate;

public class BWWorldGenerators {
	public static final RegistryKey<ConfiguredFeature<?, ?>> JUNIPER = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Bewitchment.id("juniper"));
	public static final RegistryKey<ConfiguredFeature<?, ?>> CYPRESS = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Bewitchment.id("cypress"));
	public static final RegistryKey<ConfiguredFeature<?, ?>> ELDER = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Bewitchment.id("elder"));
	public static final RegistryKey<ConfiguredFeature<?, ?>> DRAGONS_BLOOD = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Bewitchment.id("dragons_blood"));

	public static final RegistryKey<PlacedFeature> TREE_JUNIPER = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Bewitchment.id("tree_juniper"));
	public static final RegistryKey<PlacedFeature> TREE_CYPRESS = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Bewitchment.id("tree_cypress"));
	public static final RegistryKey<PlacedFeature> TREE_ELDER = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Bewitchment.id("tree_elder"));

	public static final RegistryKey<PlacedFeature> ORE_SALT_UPPER = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Bewitchment.id("ore_salt_upper"));
	public static final RegistryKey<PlacedFeature> ORE_SALT_LOWER = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Bewitchment.id("ore_salt_lower"));
	public static final RegistryKey<PlacedFeature> ORE_SILVER = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Bewitchment.id("ore_silver"));
	public static final RegistryKey<PlacedFeature> ORE_SILVER_LOWER = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Bewitchment.id("ore_silver_lower"));

	public static void init() {
		BiomeModifications.addFeature(BiomeSelectors.tag(ConventionalBiomeTags.SAVANNA), GenerationStep.Feature.VEGETAL_DECORATION, TREE_JUNIPER);
		BiomeModifications.addFeature(BiomeSelectors.tag(ConventionalBiomeTags.TAIGA).or(BiomeSelectors.tag(ConventionalBiomeTags.SWAMP)), GenerationStep.Feature.VEGETAL_DECORATION, TREE_CYPRESS);
		BiomeModifications.addFeature(BiomeSelectors.tag(ConventionalBiomeTags.FOREST), GenerationStep.Feature.VEGETAL_DECORATION, TREE_ELDER);
		if (BWConfig.generateSalt) {
			BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_SALT_UPPER);
			BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_SALT_LOWER);
		}
		if (BWConfig.generateSilver) {
			BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_SILVER);
			BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_SILVER_LOWER);
		}
		if (registerEntitySpawn(BWEntityTypes.OWL, BiomeSelectors.foundInOverworld().and(BiomeSelectors.tag(ConventionalBiomeTags.TAIGA).or(BiomeSelectors.tag(ConventionalBiomeTags.FOREST))), BWConfig.owlWeight, BWConfig.owlMinGroupCount, BWConfig.owlMaxGroupCount)) {
			SpawnRestriction.register(BWEntityTypes.OWL, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		}
		if (registerEntitySpawn(BWEntityTypes.RAVEN, BiomeSelectors.foundInOverworld().and(BiomeSelectors.tag(ConventionalBiomeTags.PLAINS).or(BiomeSelectors.tag(ConventionalBiomeTags.FOREST))), BWConfig.ravenWeight, BWConfig.ravenMinGroupCount, BWConfig.ravenMaxGroupCount)) {
			SpawnRestriction.register(BWEntityTypes.RAVEN, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		}
		if (registerEntitySpawn(BWEntityTypes.SNAKE, BiomeSelectors.foundInOverworld().and(BiomeSelectors.tag(ConventionalBiomeTags.PLAINS).or(BiomeSelectors.tag(ConventionalBiomeTags.SAVANNA).or(BiomeSelectors.tag(ConventionalBiomeTags.DESERT)))), BWConfig.snakeWeight, BWConfig.snakeMinGroupCount, BWConfig.snakeMaxGroupCount)) {
			SpawnRestriction.register(BWEntityTypes.SNAKE, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		}
		if (registerEntitySpawn(BWEntityTypes.TOAD, BiomeSelectors.foundInOverworld().and(BiomeSelectors.tag(ConventionalBiomeTags.JUNGLE).or(BiomeSelectors.tag(ConventionalBiomeTags.SWAMP))), BWConfig.toadWeight, BWConfig.toadMinGroupCount, BWConfig.toadMaxGroupCount)) {
			SpawnRestriction.register(BWEntityTypes.TOAD, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		}
		if (registerEntitySpawn(BWEntityTypes.GHOST, BiomeSelectors.foundInOverworld().and(context -> !context.getBiome().getSpawnSettings().getSpawnEntries(BWEntityTypes.GHOST.getSpawnGroup()).isEmpty()), BWConfig.ghostWeight, BWConfig.ghostMinGroupCount, BWConfig.ghostMaxGroupCount)) {
			SpawnRestriction.register(BWEntityTypes.GHOST, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GhostEntity::canSpawn);
		}
		if (registerEntitySpawn(BWEntityTypes.VAMPIRE, BiomeSelectors.foundInOverworld().and(context -> !context.getBiome().getSpawnSettings().getSpawnEntries(BWEntityTypes.VAMPIRE.getSpawnGroup()).isEmpty()).and(BiomeSelectors.tag(ConventionalBiomeTags.TAIGA).or(BiomeSelectors.tag(ConventionalBiomeTags.PLAINS))), BWConfig.vampireWeight, BWConfig.vampireMinGroupCount, BWConfig.vampireMaxGroupCount)) {
			SpawnRestriction.register(BWEntityTypes.VAMPIRE, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, VampireEntity::canSpawn);
		}
		if (registerEntitySpawn(BWEntityTypes.WEREWOLF, BiomeSelectors.foundInOverworld().and(context -> !context.getBiome().getSpawnSettings().getSpawnEntries(BWEntityTypes.WEREWOLF.getSpawnGroup()).isEmpty()).and(BiomeSelectors.tag(ConventionalBiomeTags.TAIGA).or(BiomeSelectors.tag(ConventionalBiomeTags.FOREST))), BWConfig.werewolfWeight, BWConfig.werewolfMinGroupCount, BWConfig.werewolfMaxGroupCount)) {
			SpawnRestriction.register(BWEntityTypes.WEREWOLF, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WerewolfEntity::canSpawn);
		}
		if (registerEntitySpawn(BWEntityTypes.HELLHOUND, BiomeSelectors.foundInTheNether(), BWConfig.hellhoundWeight, BWConfig.hellhoundMinGroupCount, BWConfig.hellhoundMaxGroupCount)) {
			SpawnRestriction.register(BWEntityTypes.HELLHOUND, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HellhoundEntity::canSpawn);
		}
		LootTableEvents.MODIFY.register((RegistryKey<LootTable> key, LootTable.Builder tableBuilder, LootTableSource source, RegistryWrapper.WrapperLookup wrapperLookup) -> {
			RegistryKey<LootTable> seeds = RegistryKey.of(RegistryKeys.LOOT_TABLE, Bewitchment.id("inject/seeds"));
			RegistryKey<LootTable> nether_fortress = RegistryKey.of(RegistryKeys.LOOT_TABLE, Bewitchment.id("inject/nether_fortress"));
			if (key.getValue().equals(Identifier.ofVanilla("blocks/short_grass")) || key.getValue().equals(Identifier.ofVanilla("blocks/fern")) || key.getValue().equals(Identifier.ofVanilla("blocks/tall_grass")) || key.getValue().equals(Identifier.ofVanilla("blocks/large_fern"))) {
				tableBuilder.pool(LootPool.builder().with(LootTableEntry.builder(seeds).weight(1)).build());
			}
			if (LootTables.NETHER_BRIDGE_CHEST.equals(key)) {
				tableBuilder.pool(LootPool.builder().with(LootTableEntry.builder(nether_fortress).weight(1)).build());
			}
		});
	}

	private static boolean registerEntitySpawn(EntityType<?> type, Predicate<BiomeSelectionContext> predicate, int weight, int minGroupSize, int maxGroupSize) {
		if (weight < 0) {
			throw new UnsupportedOperationException("Could not register entity type " + type.getTranslationKey() + ": weight " + weight + " cannot be negative.");
		} else if (minGroupSize < 0) {
			throw new UnsupportedOperationException("Could not register entity type " + type.getTranslationKey() + ": minGroupSize " + minGroupSize + " cannot be negative.");
		} else if (maxGroupSize < 0) {
			throw new UnsupportedOperationException("Could not register entity type " + type.getTranslationKey() + ": maxGroupSize " + maxGroupSize + " cannot be negative.");
		} else if (minGroupSize > maxGroupSize) {
			throw new UnsupportedOperationException("Could not register entity type " + type.getTranslationKey() + ": minGroupSize " + minGroupSize + " cannot be greater than maxGroupSize " + maxGroupSize + ".");
		} else if (weight == 0 || minGroupSize == 0) {
			return false;
		}
		BiomeModifications.addSpawn(predicate, type.getSpawnGroup(), type, weight, minGroupSize, maxGroupSize);
		return true;
	}
}
