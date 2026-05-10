/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.item;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ZephyrHarnessItem extends TrinketItem {
	public ZephyrHarnessItem(Settings settings) {
		super(settings);
	}

	@Override
	public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier identifier) {
		Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiers = super.getModifiers(stack, slot, entity, identifier);
		modifiers.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(identifier, 0.1, EntityAttributeModifier.Operation.ADD_VALUE));
		return modifiers;
	}
}
