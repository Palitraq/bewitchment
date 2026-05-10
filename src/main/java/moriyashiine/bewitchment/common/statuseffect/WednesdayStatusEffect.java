/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.statuseffect;

import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class WednesdayStatusEffect extends StatusEffect {
	public WednesdayStatusEffect(StatusEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public void onRemoved(AttributeContainer attributeContainer) {
		super.onRemoved(attributeContainer);
		// TODO: entity-specific explosion logic needs to be re-implemented
		// In 1.21.3, onRemoved no longer takes LivingEntity
	}
}
