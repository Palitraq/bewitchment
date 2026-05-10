/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.mixin.brew;

import moriyashiine.bewitchment.common.registry.BWComponents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin extends ThrownItemEntity {
	public PotionEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
		super(entityType, world);
	}

	@ModifyArg(method = "applyLingeringPotion", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/AreaEffectCloudEntity;setPotionContents(Lnet/minecraft/component/type/PotionContentsComponent;)V"))
	private PotionContentsComponent modifyPotionContents(PotionContentsComponent potionContents) {
		var nbtComponent = getStack().getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
		if (nbtComponent.copyNbt().getBoolean("BewitchmentBrew")) {
			List<StatusEffectInstance> modifiedEffects = new ArrayList<>();
			for (StatusEffectInstance effect : potionContents.getEffects()) {
				modifiedEffects.add(new StatusEffectInstance(effect.getEffectType(), effect.getDuration() / 4, effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()));
			}
			return new PotionContentsComponent(Optional.empty(), potionContents.customColor(), modifiedEffects);
		}
		return potionContents;
	}

	@Inject(method = "applySplashPotion", at = @At("HEAD"))
	private void applySplashPotion(Iterable<StatusEffectInstance> statusEffects, @Nullable Entity entity, CallbackInfo callbackInfo) {
		ItemStack stack = getStack();
		if (stack.contains(DataComponentTypes.CUSTOM_DATA)) {
			var nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
			var nbt = nbtComponent.copyNbt();
			if (nbt.contains("PolymorphUUID")) {
				UUID uuid = nbt.getUuid("PolymorphUUID");
				String name = nbt.getString("PolymorphName");
				for (LivingEntity livingEntity : getWorld().getNonSpectatingEntities(LivingEntity.class, getBoundingBox().expand(4, 2, 4))) {
					BWComponents.POLYMORPH_COMPONENT.maybeGet(livingEntity).ifPresent(polymorphComponent -> {
						polymorphComponent.setUuid(uuid);
						polymorphComponent.setName(name);
					});
				}
			}
		}
	}

	@Inject(method = "applyLingeringPotion", at = @At("TAIL"))
	private void applyLingeringPotion(PotionContentsComponent potionContents, CallbackInfo callbackInfo) {
		ItemStack stack = getStack();
		if (stack.contains(DataComponentTypes.CUSTOM_DATA)) {
			var nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
			var nbt = nbtComponent.copyNbt();
			if (nbt.contains("PolymorphUUID")) {
				UUID uuid = nbt.getUuid("PolymorphUUID");
				String name = nbt.getString("PolymorphName");
				for (AreaEffectCloudEntity cloud : getWorld().getNonSpectatingEntities(AreaEffectCloudEntity.class, getBoundingBox().expand(4, 2, 4))) {
					BWComponents.POLYMORPH_COMPONENT.maybeGet(cloud).ifPresent(polymorphComponent -> {
						polymorphComponent.setUuid(uuid);
						polymorphComponent.setName(name);
					});
				}
			}
		}
	}
}
