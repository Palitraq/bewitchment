/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;

// 1.21: Enchantments are now data-driven. This class is kept for compatibility.
// Enchantment behavior should be defined in data/bewitchment/enchantment/ files.
public class MagicProtectionEnchantment {
	// TODO: Migrate to data-driven enchantments in 1.21
	// Behavior: returns level * 2 protection for WITCH_RESISTANT_TO damage types, 0 otherwise
}
