/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.statuseffect;

import moriyashiine.bewitchment.common.registry.BWStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class CorruptionStatusEffect extends StatusEffect {
	public static final Map<RegistryEntry<StatusEffect>, RegistryEntry<StatusEffect>> INVERSE_EFFECTS = new HashMap<>();

	static {
		INVERSE_EFFECTS.put(StatusEffects.STRENGTH, StatusEffects.WEAKNESS);
		INVERSE_EFFECTS.put(StatusEffects.REGENERATION, StatusEffects.POISON);
		INVERSE_EFFECTS.put(StatusEffects.NIGHT_VISION, StatusEffects.BLINDNESS);
		INVERSE_EFFECTS.put(StatusEffects.HASTE, StatusEffects.MINING_FATIGUE);
		INVERSE_EFFECTS.put(StatusEffects.SPEED, StatusEffects.SLOWNESS);
		INVERSE_EFFECTS.put(StatusEffects.JUMP_BOOST, RegistryEntry.of(BWStatusEffects.SINKING));
		INVERSE_EFFECTS.put(StatusEffects.FIRE_RESISTANCE, RegistryEntry.of(BWStatusEffects.IGNITION));
		INVERSE_EFFECTS.put(StatusEffects.WATER_BREATHING, RegistryEntry.of(BWStatusEffects.GILLS));
		INVERSE_EFFECTS.put(StatusEffects.SLOW_FALLING, StatusEffects.LEVITATION);
		INVERSE_EFFECTS.put(RegistryEntry.of(BWStatusEffects.HARDENING), RegistryEntry.of(BWStatusEffects.CORROSION));
		INVERSE_EFFECTS.put(RegistryEntry.of(BWStatusEffects.ENCHANTED), RegistryEntry.of(BWStatusEffects.INHIBITED));
		INVERSE_EFFECTS.put(RegistryEntry.of(BWStatusEffects.NOURISHING), StatusEffects.HUNGER);
	}

	public CorruptionStatusEffect(StatusEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public boolean isInstant() {
		return true;
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
		if (!entity.getWorld().isClient) {
			Registries.STATUS_EFFECT.streamEntries().forEach(entry -> {
				if (entry.value().getCategory() == StatusEffectCategory.BENEFICIAL && entity.hasStatusEffect(entry) && !entity.getStatusEffect(entry).isAmbient()) {
					RegistryEntry<StatusEffect> inverse = INVERSE_EFFECTS.get(entry);
					StatusEffectInstance inverseEffect = null;
					if (inverse != null) {
						StatusEffectInstance goodEffect = entity.getStatusEffect(entry);
						inverseEffect = new StatusEffectInstance(inverse, goodEffect.getDuration(), goodEffect.getAmplifier(), goodEffect.isAmbient(), goodEffect.shouldShowParticles(), goodEffect.shouldShowIcon());
					}
					entity.removeStatusEffect(entry);
					if (inverseEffect != null) {
						entity.addStatusEffect(inverseEffect);
					}
				}
			});
		}
		return true;
	}
}
