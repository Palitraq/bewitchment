/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.registry;

import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.statuseffect.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class BWStatusEffects {
	public static final RegistryEntry<StatusEffect> CLIMBING = create("climbing", new EmptyStatusEffect(StatusEffectCategory.BENEFICIAL, 0x605448));
	public static final RegistryEntry<StatusEffect> CORROSION = create("corrosion", new EmptyStatusEffect(StatusEffectCategory.HARMFUL, 0x6fc536).addAttributeModifier(EntityAttributes.GENERIC_ARMOR, Identifier.of("5b5df91d-001a-464f-a773-ab4ccd62e09f"), -3, EntityAttributeModifier.Operation.ADD_VALUE));
	public static final RegistryEntry<StatusEffect> CORRUPTION = create("corruption", new CorruptionStatusEffect(StatusEffectCategory.HARMFUL, 0x600000));
	public static final RegistryEntry<StatusEffect> DEAFENED = create("deafened", new EmptyStatusEffect(StatusEffectCategory.HARMFUL, 0x676c77));
	public static final RegistryEntry<StatusEffect> DEFLECTION = create("deflection", new EmptyStatusEffect(StatusEffectCategory.BENEFICIAL, 0x969696));
	public static final RegistryEntry<StatusEffect> DISJUNCTION = create("disjunction", new DisjunctionStatusEffect(StatusEffectCategory.NEUTRAL, 0xb154a9));
	public static final RegistryEntry<StatusEffect> ENCHANTED = create("enchanted", new EmptyStatusEffect(StatusEffectCategory.BENEFICIAL, 0xf5ce8c));
	public static final RegistryEntry<StatusEffect> ETHEREAL = create("ethereal", new EmptyStatusEffect(StatusEffectCategory.NEUTRAL, 0x9a9ebf));
	public static final RegistryEntry<StatusEffect> GILLS = create("gills", new GillsStatusEffect(StatusEffectCategory.HARMFUL, 0x2f5e7b));
	public static final RegistryEntry<StatusEffect> HARDENING = create("hardening", new EmptyStatusEffect(StatusEffectCategory.BENEFICIAL, 0x3fa442).addAttributeModifier(EntityAttributes.GENERIC_ARMOR, Identifier.of("7f7f1ce3-6383-49a8-a9f1-af0f5c7ab38f"), 3, EntityAttributeModifier.Operation.ADD_VALUE));
	public static final RegistryEntry<StatusEffect> IGNITION = create("ignition", new IgnitionStatusEffect(StatusEffectCategory.HARMFUL, 0xd68a0c));
	public static final RegistryEntry<StatusEffect> INHIBITED = create("inhibited", new EmptyStatusEffect(StatusEffectCategory.HARMFUL, 0x383a41));
	public static final RegistryEntry<StatusEffect> INVIGORATING = create("invigorating", new InvigoratingStatusEffect(StatusEffectCategory.BENEFICIAL, 0xaa64b3));
	public static final RegistryEntry<StatusEffect> LEECHING = create("leeching", new EmptyStatusEffect(StatusEffectCategory.BENEFICIAL, 0x4a0000));
	public static final RegistryEntry<StatusEffect> MAGIC_SPONGE = create("magic_sponge", new EmptyStatusEffect(StatusEffectCategory.BENEFICIAL, 0xa264aa));
	public static final RegistryEntry<StatusEffect> MORTAL_COIL = create("mortal_coil", new MortalCoilStatusEffect(StatusEffectCategory.HARMFUL, 0x2d2d2d));
	public static final RegistryEntry<StatusEffect> NOURISHING = create("nourishing", new EmptyStatusEffect(StatusEffectCategory.BENEFICIAL, 0x71bc78));
	public static final RegistryEntry<StatusEffect> PACT = create("pact", new EmptyStatusEffect(StatusEffectCategory.HARMFUL, 0x7d0000).addAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH, Identifier.of("be0cf4d3-6e4b-43e6-9831-9e7d14aa14a7"), -1, EntityAttributeModifier.Operation.ADD_VALUE));
	public static final RegistryEntry<StatusEffect> POLYMORPH = create("polymorph", new PolymorphStatusEffect(StatusEffectCategory.NEUTRAL, 0xb154a9));
	public static final RegistryEntry<StatusEffect> PURITY = create("purity", new PurityStatusEffect(StatusEffectCategory.BENEFICIAL, 0xded3d5));
	public static final RegistryEntry<StatusEffect> SINKING = create("sinking", new SinkingStatusEffect(StatusEffectCategory.HARMFUL, 0x6c6c6c));
	public static final RegistryEntry<StatusEffect> SYNCHRONIZED = create("synchronized", new SynchronizedStatusEffect(StatusEffectCategory.NEUTRAL, 0x990404));
	public static final RegistryEntry<StatusEffect> THEFT = create("theft", new TheftStatusEffect(StatusEffectCategory.BENEFICIAL, 0x773b89));
	public static final RegistryEntry<StatusEffect> THORNS = create("thorns", new EmptyStatusEffect(StatusEffectCategory.BENEFICIAL, 0x527d26));
	public static final RegistryEntry<StatusEffect> VOLATILITY = create("volatility", new EmptyStatusEffect(StatusEffectCategory.HARMFUL, 0xb76b00));
	public static final RegistryEntry<StatusEffect> WEBBED = create("webbed", new WebbedStatusEffect(StatusEffectCategory.HARMFUL, 0x2d312f));
	public static final RegistryEntry<StatusEffect> WEDNESDAY = create("wednesday", new WednesdayStatusEffect(StatusEffectCategory.BENEFICIAL, 0x00ff00));

	private static RegistryEntry<StatusEffect> create(String name, StatusEffect effect) {
		return Registry.registerReference(Registries.STATUS_EFFECT, Bewitchment.id(name), effect);
	}
}
