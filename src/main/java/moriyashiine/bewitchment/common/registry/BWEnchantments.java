/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.registry;

import moriyashiine.bewitchment.common.Bewitchment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class BWEnchantments {
	public static final RegistryKey<Enchantment> MAGIC_PROTECTION = RegistryKey.of(RegistryKeys.ENCHANTMENT, Bewitchment.id("magic_protection"));

	public static void init() {
		// Enchantments are data-driven in 1.21
		// Define magic_protection in data/bewitchment/enchantment/magic_protection.json
	}
}
