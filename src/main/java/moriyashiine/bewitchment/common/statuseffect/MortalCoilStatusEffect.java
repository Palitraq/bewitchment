/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.statuseffect;

import moriyashiine.bewitchment.common.registry.BWDamageSources;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

@SuppressWarnings("ConstantConditions")
public class MortalCoilStatusEffect extends StatusEffect {
	public MortalCoilStatusEffect(StatusEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
		StatusEffectInstance instance = entity.getStatusEffect(RegistryEntry.of(this));
		if (instance != null && instance.getDuration() == 1) {
			entity.damage(BWDamageSources.create(entity.getWorld(), BWDamageSources.DEATH), Float.MAX_VALUE);
		}
		return true;
	}
}
