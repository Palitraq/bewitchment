/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.mixin.familiar;

import moriyashiine.bewitchment.api.BewitchmentAPI;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.Identifier;

@SuppressWarnings("ConstantConditions")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	@Unique
	private static final EntityAttributeModifier WOLF_FAMILIAR_ARMOR_MODIFIER = new EntityAttributeModifier(Identifier.of("bewitchment", "wolf_familiar_armor"), 6, EntityAttributeModifier.Operation.ADD_VALUE);
	@Unique
	private static final EntityAttributeModifier WOLF_FAMILIAR_ARMOR_TOUGHNESS_MODIFIER = new EntityAttributeModifier(Identifier.of("bewitchment", "wolf_familiar_armor_toughness"), 6, EntityAttributeModifier.Operation.ADD_VALUE);

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void tick(CallbackInfo callbackInfo) {
		if (!getWorld().isClient) {
			boolean shouldHave = BewitchmentAPI.getFamiliar((PlayerEntity) (Object) this) == EntityType.WOLF;
			EntityAttributeInstance armorAttribute = getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
			EntityAttributeInstance armorToughnessAttribute = getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
			if (shouldHave && !armorAttribute.hasModifier(WOLF_FAMILIAR_ARMOR_MODIFIER.id())) {
				armorAttribute.addPersistentModifier(WOLF_FAMILIAR_ARMOR_MODIFIER);
				armorToughnessAttribute.addPersistentModifier(WOLF_FAMILIAR_ARMOR_TOUGHNESS_MODIFIER);
			} else if (!shouldHave && armorAttribute.hasModifier(WOLF_FAMILIAR_ARMOR_MODIFIER.id())) {
				armorAttribute.removeModifier(WOLF_FAMILIAR_ARMOR_MODIFIER.id());
				armorToughnessAttribute.removeModifier(WOLF_FAMILIAR_ARMOR_TOUGHNESS_MODIFIER.id());
			}
		}
	}
}
