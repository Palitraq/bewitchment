/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.mixin.brew;

import moriyashiine.bewitchment.common.registry.BWComponents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ConstantConditions")
@Mixin(ArrowEntity.class)
public abstract class ArrowEntityMixin extends Entity {
	public ArrowEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "setStack", at = @At("TAIL"))
	private void onSetStack(ItemStack stack, CallbackInfo callbackInfo) {
		var nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
		var nbt = nbtComponent.copyNbt();
		if (nbt.getBoolean("BewitchmentBrew")) {
			PotionContentsComponent potionContents = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
			List<StatusEffectInstance> halved = new ArrayList<>();
			for (StatusEffectInstance effect : potionContents.getEffects()) {
				halved.add(new StatusEffectInstance(effect.getEffectType(), effect.getDuration() / 8, effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()));
			}
			stack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(), Optional.empty(), halved));
		}
		BWComponents.POLYMORPH_COMPONENT.maybeGet(this).ifPresent(polymorphComponent -> {
			if (stack.contains(DataComponentTypes.CUSTOM_DATA)) {
				var nbtComponent2 = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
				var nbt2 = nbtComponent2.copyNbt();
				if (nbt2.contains("PolymorphUUID")) {
					polymorphComponent.setUuid(nbt2.getUuid("PolymorphUUID"));
					polymorphComponent.setName(nbt2.getString("PolymorphName"));
				}
			}
		});
	}

	@Inject(method = "getItemStack", at = @At("RETURN"))
	private void asItemStack(CallbackInfoReturnable<ItemStack> callbackInfo) {
		BWComponents.POLYMORPH_COMPONENT.maybeGet(this).ifPresent(polymorphComponent -> {
			if (polymorphComponent.getUuid() != null) {
				ItemStack stack = callbackInfo.getReturnValue();
				var nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
				var nbt = nbtComponent.copyNbt();
				nbt.putUuid("PolymorphUUID", polymorphComponent.getUuid());
				nbt.putString("PolymorphName", polymorphComponent.getName());
				stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
			}
		});
	}

	@Inject(method = "onHit", at = @At("HEAD"))
	private void onHit(LivingEntity target, CallbackInfo callbackInfo) {
		BWComponents.POLYMORPH_COMPONENT.maybeGet(this).ifPresent(thisPolymorphComponent -> {
			if (thisPolymorphComponent.getUuid() != null) {
				BWComponents.POLYMORPH_COMPONENT.maybeGet(target).ifPresent(targetPolymorphComponent -> {
					targetPolymorphComponent.setUuid(thisPolymorphComponent.getUuid());
					targetPolymorphComponent.setName(thisPolymorphComponent.getName());
				});
			}
		});
	}
}
