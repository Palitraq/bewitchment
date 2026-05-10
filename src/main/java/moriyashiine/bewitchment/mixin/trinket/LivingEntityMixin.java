/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.mixin.trinket;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.common.registry.BWComponents;
import moriyashiine.bewitchment.common.registry.BWObjects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings({"ConstantConditions", "OptionalGetWithoutIsPresent"})
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow
	public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

	@Shadow
	public abstract boolean addStatusEffect(StatusEffectInstance effect);

	@Shadow
	public int hurtTime;

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "damage", at = @At("HEAD"))
	private void damageHead(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (!getWorld().isClient) {
			if (amount > 0 && hurtTime == 0) {
				if ((Object) this instanceof PlayerEntity player && source.getSource() instanceof LivingEntity livingSource) {
					List<Pair<SlotReference, ItemStack>> component = TrinketsApi.getTrinketComponent(player).get().getEquipped(BWObjects.PRICKLY_BELT);
					if (!component.isEmpty()) {
						ItemStack belt = component.get(0).getRight();
						var beltNbtComponent = belt.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
						var beltNbt = beltNbtComponent.copyNbt();
						if (!beltNbtComponent.equals(NbtComponent.DEFAULT) && beltNbt.getInt("PotionUses") > 0) {
							boolean used = false;
							PotionContentsComponent potionContents = belt.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
							List<StatusEffectInstance> effects = (List<StatusEffectInstance>) potionContents.getEffects();
							for (StatusEffectInstance effect : effects) {
								if (effect.getEffectType().value().getCategory() == StatusEffectCategory.HARMFUL) {
									if (!livingSource.hasStatusEffect(effect.getEffectType()) && BewitchmentAPI.drainMagic(player, 2, true) && livingSource.addStatusEffect(effect)) {
										used = true;
									}
								} else if (!hasStatusEffect(effect.getEffectType()) && BewitchmentAPI.drainMagic(player, 2, true) && addStatusEffect(effect)) {
									if (beltNbt.contains("PolymorphUUID")) {
										BWComponents.POLYMORPH_COMPONENT.maybeGet(this).ifPresent(polymorphComponent -> {
											polymorphComponent.setUuid(beltNbt.getUuid("PolymorphUUID"));
											polymorphComponent.setName(beltNbt.getString("PolymorphName"));
										});
									}
									used = true;
								}
							}
							if (used) {
								beltNbt.putInt("PotionUses", beltNbt.getInt("PotionUses") - 1);
								if (beltNbt.getInt("PotionUses") <= 0) {
									beltNbt.put("CustomPotionEffects", new NbtList());
									if (beltNbt.contains("PolymorphUUID")) {
										beltNbt.remove("PolymorphUUID");
										beltNbt.remove("PolymorphName");
									}
								}
								belt.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(beltNbt));
							}
						}
					}
				}
			}
		}
	}

	@Inject(method = "damage", at = @At("RETURN"))
	private void damageReturn(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (callbackInfo.getReturnValueZ() && !getWorld().isClient && source.getSource() instanceof PlayerEntity player && player.getMainHandStack().isEmpty() && TrinketsApi.getTrinketComponent(player).get().isEquipped(BWObjects.ZEPHYR_HARNESS) && BewitchmentAPI.drainMagic(player, 1, false)) {
			addVelocity(0, 2 / 3f, 0);
		}
	}
}
