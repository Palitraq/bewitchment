/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.mixin.transformation;

import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.api.component.BloodComponent;
import moriyashiine.bewitchment.common.registry.BWComponents;
import moriyashiine.bewitchment.common.registry.BWEntityTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public class StatusEffectMixin {
	@Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;applyUpdateEffect(Lnet/minecraft/entity/LivingEntity;I)Z"))
	private boolean redirectApplyUpdateEffect(StatusEffect effect, LivingEntity entity, int amplifier) {
		if (entity instanceof PlayerEntity player && BewitchmentAPI.getFamiliar(player) == BWEntityTypes.SNAKE) {
			if (effect == StatusEffects.POISON || effect == StatusEffects.WITHER) {
				BewitchmentAPI.fillMagic(player, 1, false);
				return true;
			}
		}
		if (BewitchmentAPI.isVampire(entity, true) && effect == StatusEffects.HUNGER) {
			BWComponents.BLOOD_COMPONENT.maybeGet(entity).ifPresent(bloodComponent -> {
				if (bloodComponent.getBlood() < BloodComponent.MAX_BLOOD) {
					bloodComponent.fillBlood(amplifier + 1, false);
				}
			});
		}
		return effect.applyUpdateEffect(entity, amplifier);
	}
}
