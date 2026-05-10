/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.registry;

import moriyashiine.bewitchment.common.Bewitchment;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class BWEntityAttributes {
	public static final RegistryEntry<EntityAttribute> ATTACK_RANGE = register("attack_range",
			new ClampedEntityAttribute("attribute.name.generic." + Bewitchment.MOD_ID + ".attack_range",
					0.0, -1024.0, 1024.0).setTracked(true));

	public static final RegistryEntry<EntityAttribute> REACH = register("reach",
			new ClampedEntityAttribute("attribute.name.generic." + Bewitchment.MOD_ID + ".reach",
					0.0, -1024.0, 1024.0).setTracked(true));

	private static RegistryEntry<EntityAttribute> register(String name, EntityAttribute attribute) {
		return Registry.registerReference(Registries.ATTRIBUTE, Bewitchment.id(name), attribute);
	}

	public static void init() {
	}
}
