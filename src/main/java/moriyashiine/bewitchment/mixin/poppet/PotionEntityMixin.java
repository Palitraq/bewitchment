/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.mixin.poppet;

import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.common.BWConfig;
import moriyashiine.bewitchment.common.registry.BWObjects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin extends ThrownItemEntity {
	public PotionEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "applySplashPotion", at = @At("TAIL"))
	private void applySplashPotion(Iterable<StatusEffectInstance> statusEffects, @Nullable Entity entity, CallbackInfo callbackInfo) {
		Box box = getBoundingBox().expand(4, 2, 4);
		if (BWConfig.disabledPoppets.contains("bewitchment:voodoo_poppet")) {
			return;
		}
		List<ItemEntity> itemEntities = getWorld().getNonSpectatingEntities(ItemEntity.class, box);
		for (ItemEntity itemEntity : itemEntities) {
			if (itemEntity.getStack().getItem() == BWObjects.VOODOO_POPPET) {
				LivingEntity owner = BewitchmentAPI.getTaglockOwner(getWorld(), itemEntity.getStack());
				if (owner != null && owner.isAffectedBySplashPotions()) {
					for (StatusEffectInstance effect : statusEffects) {
						if (getWorld() instanceof ServerWorld serverWorld) {
							itemEntity.getStack().damage(8, serverWorld, null, item -> {});
							if (itemEntity.getStack().getDamage() >= itemEntity.getStack().getMaxDamage()) {
								itemEntity.getStack().decrement(1);
							}
						}
						if (!BewitchmentAPI.hasVoodooProtection(owner, 8)) {
							if (effect.getEffectType().value().isInstant()) {
								effect.getEffectType().value().applyInstantEffect(null, null, owner, effect.getAmplifier(), 0.5);
							} else {
								owner.addStatusEffect(new StatusEffectInstance(effect.getEffectType(), effect.getDuration() / 2, effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles()));
							}
						}
					}
				}
			}
		}
	}
}
