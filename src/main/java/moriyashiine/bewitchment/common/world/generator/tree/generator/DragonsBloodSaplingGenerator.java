/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.world.generator.tree.generator;

import moriyashiine.bewitchment.common.registry.BWWorldGenerators;
import net.minecraft.block.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import java.util.Optional;

public class DragonsBloodSaplingGenerator {
	public static SaplingGenerator create() {
		return new SaplingGenerator("dragons_blood", Optional.empty(), Optional.of(BWWorldGenerators.DRAGONS_BLOOD), Optional.empty());
	}
}
