/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.curse;

import moriyashiine.bewitchment.api.registry.Curse;
import moriyashiine.bewitchment.common.registry.BWStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;

public class MisfortuneCurse extends Curse {
	public MisfortuneCurse(Type type) {
		super(type);
	}

	@Override
	public void tick(LivingEntity target) {
		if (target.age % 20 == 0 && target.getRandom().nextFloat() < 1 / 100f) {
			target.addStatusEffect(getEffect(target.getRandom().nextInt(8)));
		}
	}

	private static StatusEffectInstance getEffect(int value) {
		RegistryEntry<StatusEffect> effect;
		if (value == 1) effect = StatusEffects.POISON;
		else if (value == 2) effect = StatusEffects.WEAKNESS;
		else if (value == 3) effect = StatusEffects.SLOWNESS;
		else if (value == 4) effect = StatusEffects.BLINDNESS;
		else if (value == 5) effect = StatusEffects.NAUSEA;
		else if (value == 6) effect = StatusEffects.MINING_FATIGUE;
		else if (value == 7) effect = RegistryEntry.of(BWStatusEffects.CORROSION);
		else effect = RegistryEntry.of(BWStatusEffects.SINKING);
		return new StatusEffectInstance(effect, 400);
	}
}
