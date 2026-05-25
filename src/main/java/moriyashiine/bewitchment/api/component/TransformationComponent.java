/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.api.component;

import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.api.event.AllowVampireBurn;
import moriyashiine.bewitchment.api.event.AllowVampireHeal;
import moriyashiine.bewitchment.api.event.OnTransformationSet;
import moriyashiine.bewitchment.api.registry.Transformation;
import moriyashiine.bewitchment.common.component.entity.AdditionalWerewolfDataComponent;
import moriyashiine.bewitchment.common.misc.BWUtil;
import moriyashiine.bewitchment.common.packet.TransformationAbilityPacket;
import moriyashiine.bewitchment.common.registry.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;
import org.ladysnake.cca.api.v3.component.Component;

public class TransformationComponent implements Component {
	private static final EntityAttributeModifier VAMPIRE_ATTACK_DAMAGE_MODIFIER_0 = new EntityAttributeModifier(Identifier.of("066862f6-989c-4f35-ac6d-2696b91a1a5b"), 2, EntityAttributeModifier.Operation.ADD_VALUE);
	private static final EntityAttributeModifier VAMPIRE_ATTACK_DAMAGE_MODIFIER_1 = new EntityAttributeModifier(Identifier.of("d2be3564-97e7-42c9-88c5-6b753472e37f"), 4, EntityAttributeModifier.Operation.ADD_VALUE);
	private static final EntityAttributeModifier VAMPIRE_MOVEMENT_SPEED_MODIFIER_0 = new EntityAttributeModifier(Identifier.of("a782c03d-af7b-4eb7-b997-dd396bfdc7a0"), 0.04, EntityAttributeModifier.Operation.ADD_VALUE);
	private static final EntityAttributeModifier VAMPIRE_MOVEMENT_SPEED_MODIFIER_1 = new EntityAttributeModifier(Identifier.of("7c7a61eb-83e8-4e85-94d6-a410a4153d6d"), 0.08, EntityAttributeModifier.Operation.ADD_VALUE);

	private static final EntityAttributeModifier WEREWOLF_ATTACK_SPEED_MODIFIER = new EntityAttributeModifier(Identifier.of("db2512a4-655d-4843-8b06-619748a33954"), -2, EntityAttributeModifier.Operation.ADD_VALUE);
	private static final EntityAttributeModifier WEREWOLF_ARMOR_MODIFIER = new EntityAttributeModifier(Identifier.of("f00b0b0f-8ad6-4a2f-bdf5-6c337ffee56c"), 16, EntityAttributeModifier.Operation.ADD_VALUE);
	private static final EntityAttributeModifier WEREWOLF_ATTACK_RANGE_MODIFIER = new EntityAttributeModifier(Identifier.of("ae0e4c0a-971f-4629-99ad-60c115112c1d"), 1, EntityAttributeModifier.Operation.ADD_VALUE);
	private static final EntityAttributeModifier WEREWOLF_REACH_MODIFIER = new EntityAttributeModifier(Identifier.of("4c6d90ab-41ad-4d8a-b77a-7329361d3a7b"), 1, EntityAttributeModifier.Operation.ADD_VALUE);
	private static final EntityAttributeModifier WEREWOLF_STEP_HEIGHT_MODIFIER = new EntityAttributeModifier(Identifier.of("af386c1c-b4fc-429d-97b6-b2559826fa9d"), 0.4, EntityAttributeModifier.Operation.ADD_VALUE);

	private static final EntityAttributeModifier WEREWOLF_ATTACK_DAMAGE_MODIFIER_0 = new EntityAttributeModifier(Identifier.of("06861902-ebc4-4e6e-956c-59c1ae3085c7"), 15, EntityAttributeModifier.Operation.ADD_VALUE);
	private static final EntityAttributeModifier WEREWOLF_ATTACK_DAMAGE_MODIFIER_1 = new EntityAttributeModifier(Identifier.of("10c0bedf-bde5-4cae-8acc-90b1204731dd"), 30, EntityAttributeModifier.Operation.ADD_VALUE);
	private static final EntityAttributeModifier WEREWOLF_ARMOR_TOUGHNESS_MODIFIER_0 = new EntityAttributeModifier(Identifier.of("44f17821-1e30-426f-81d7-cd1da88fa584"), 8, EntityAttributeModifier.Operation.ADD_VALUE);
	private static final EntityAttributeModifier WEREWOLF_ARMOR_TOUGHNESS_MODIFIER_1 = new EntityAttributeModifier(Identifier.of("edfd078d-e25c-4e27-ad91-c2b32037c8be"), 16, EntityAttributeModifier.Operation.ADD_VALUE);
	private static final EntityAttributeModifier WEREWOLF_MOVEMENT_SPEED_MODIFIER_0 = new EntityAttributeModifier(Identifier.of("e26a276a-86cd-44db-9091-acd42fc00d95"), 0.08, EntityAttributeModifier.Operation.ADD_VALUE);
	private static final EntityAttributeModifier WEREWOLF_MOVEMENT_SPEED_MODIFIER_1 = new EntityAttributeModifier(Identifier.of("718104a6-aa19-4b53-bad9-1f9edd46d38a"), 0.16, EntityAttributeModifier.Operation.ADD_VALUE);

	private final PlayerEntity obj;
	private Transformation transformation = BWTransformations.HUMAN;
	private boolean alternateForm = false;

	public TransformationComponent(PlayerEntity obj) {
		this.obj = obj;
	}

	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		if (tag.contains("Transformation")) {
			setTransformation(BWRegistries.TRANSFORMATION.get(Identifier.tryParse(tag.getString("Transformation"))));
		}
		setAlternateForm(tag.getBoolean("AlternateForm"));
	}

	@SuppressWarnings({"ConstantConditions"})
	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		tag.putString("Transformation", BWRegistries.TRANSFORMATION.getId(getTransformation()).toString());
		tag.putBoolean("AlternateForm", isAlternateForm());
	}

	public void serverTick() {
		boolean vampire = BewitchmentAPI.isVampire(obj, true);
		if (vampire) {
			boolean pledgedToLilith = BewitchmentAPI.isPledged(obj, BWPledges.LILITH);
			if (obj.getEquippedStack(EquipmentSlot.HEAD).isIn(BWTags.SUN_GLASSES)) {
				obj.removeStatusEffect(StatusEffects.NIGHT_VISION);
			} else {
				obj.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, -1, 0, true, false));
			}
			if (BWComponents.RESPAWN_TIMER_COMPONENT.get(obj).getRespawnTimer() <= 0 && obj.getWorld().isDay() && !obj.getWorld().isRaining() && obj.getWorld().isSkyVisible(obj.getBlockPos()) && AllowVampireBurn.EVENT.invoker().allowBurn(obj)) {
				obj.setOnFireFor(8);
			}
			HungerManager hungerManager = obj.getHungerManager();
			if (BWComponents.BLOOD_COMPONENT.get(obj).getBlood() > 0) {
				if (AllowVampireHeal.EVENT.invoker().allowHeal(obj, pledgedToLilith)) {
					if (obj.age % (pledgedToLilith ? 30 : 40) == 0) {
						if (obj.getHealth() < obj.getMaxHealth()) {
							obj.heal(1);
							hungerManager.addExhaustion(3);
						}
						if ((hungerManager.isNotFull() || hungerManager.getSaturationLevel() < 10) && BWComponents.BLOOD_COMPONENT.get(obj).drainBlood(1, false)) {
							hungerManager.add(1, 20);
						}
					}
				}
			} else {
				if (isAlternateForm()) {
					TransformationAbilityPacket.useAbility(obj, true);
				}
				hungerManager.addExhaustion(Float.MAX_VALUE);
			}
			if (isAlternateForm()) {
				hungerManager.addExhaustion(0.5f);
				if (!pledgedToLilith) {
					TransformationAbilityPacket.useAbility(obj, true);
				}
			}
		}
		if (BewitchmentAPI.isWerewolf(obj, true)) {
			AdditionalWerewolfDataComponent additionalWerewolfDataComponent = BWComponents.ADDITIONAL_WEREWOLF_DATA_COMPONENT.get(obj);
			boolean forced = additionalWerewolfDataComponent.isForcedTransformation();
			if (!obj.isCreative() && !obj.isSpectator() && !isAlternateForm() && obj.getWorld().isNight() && BewitchmentAPI.getMoonPhase(obj.getWorld()) == 0 && obj.getWorld().isSkyVisible(obj.getBlockPos())) {
				TransformationAbilityPacket.useAbility(obj, true);
				additionalWerewolfDataComponent.setForcedTransformation(true);
			} else if (isAlternateForm() && forced && (obj.getWorld().isDay() || BewitchmentAPI.getMoonPhase(obj.getWorld()) != 0)) {
				TransformationAbilityPacket.useAbility(obj, true);
				additionalWerewolfDataComponent.setForcedTransformation(false);
			}
			if (isAlternateForm()) {
				obj.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, -1, 0, true, false));
				Set<ItemStack> toDrop = new HashSet<>();
				obj.getArmorItems().forEach(stack -> {
					if (!stack.isEmpty()) {
						toDrop.add(stack);
					}
				});
				if (BWUtil.isTool(obj.getMainHandStack())) {
					toDrop.add(obj.getMainHandStack());
				}
				if (BWUtil.isTool(obj.getOffHandStack())) {
					toDrop.add(obj.getOffHandStack());
				}
				toDrop.forEach(stack -> {
					int firstEmptyInventorySlot = getFirstEmptyInventorySlot();
					if (firstEmptyInventorySlot != -1) {
						obj.getInventory().setStack(firstEmptyInventorySlot, stack.split(stack.getCount()));
					} else {
						obj.dropStack(stack.split(stack.getCount()));
					}
				});
				if (!forced && !BewitchmentAPI.isPledged(obj, BWPledges.HERNE)) {
					TransformationAbilityPacket.useAbility(obj, true);
				}
			}
		}
	}

	public Transformation getTransformation() {
		return transformation;
	}

	public void setTransformation(Transformation transformation) {
		OnTransformationSet.EVENT.invoker().onTransformationSet(obj, transformation);
		this.transformation = transformation;
		BWComponents.TRANSFORMATION_COMPONENT.sync(obj);
		updateAttributes();
	}

	public boolean isAlternateForm() {
		return alternateForm;
	}

	public void setAlternateForm(boolean alternateForm) {
		this.alternateForm = alternateForm;
		BWComponents.TRANSFORMATION_COMPONENT.sync(obj);
		updateAttributes();
	}

	@SuppressWarnings("ConstantConditions")
	public void updateAttributes() {
		boolean vampire = BewitchmentAPI.isVampire(obj, true);
		boolean werewolfBeast = BewitchmentAPI.isWerewolf(obj, false);
		EntityAttributeInstance attackDamageAttribute = obj.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		EntityAttributeInstance attackSpeedAttribute = obj.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED);
		EntityAttributeInstance armorAttribute = obj.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
		EntityAttributeInstance armorToughnessAttribute = obj.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
		EntityAttributeInstance movementSpeedAttribute = obj.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		EntityAttributeInstance attackRange = obj.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);
		EntityAttributeInstance reach = obj.getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
		EntityAttributeInstance stepHeight = obj.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT);
		boolean shouldHave = vampire && !BewitchmentAPI.isPledged(obj, BWPledges.LILITH);
		if (shouldHave && !attackDamageAttribute.hasModifier(VAMPIRE_ATTACK_DAMAGE_MODIFIER_0.id())) {
			attackDamageAttribute.addPersistentModifier(VAMPIRE_ATTACK_DAMAGE_MODIFIER_0);
			movementSpeedAttribute.addPersistentModifier(VAMPIRE_MOVEMENT_SPEED_MODIFIER_0);
		} else if (!shouldHave && attackDamageAttribute.hasModifier(VAMPIRE_ATTACK_DAMAGE_MODIFIER_0.id())) {
			attackDamageAttribute.removeModifier(VAMPIRE_ATTACK_DAMAGE_MODIFIER_0.id());
			movementSpeedAttribute.removeModifier(VAMPIRE_MOVEMENT_SPEED_MODIFIER_0.id());
		}
		shouldHave = vampire && BewitchmentAPI.isPledged(obj, BWPledges.LILITH);
		if (shouldHave && !attackDamageAttribute.hasModifier(VAMPIRE_ATTACK_DAMAGE_MODIFIER_1.id())) {
			attackDamageAttribute.addPersistentModifier(VAMPIRE_ATTACK_DAMAGE_MODIFIER_1);
			movementSpeedAttribute.addPersistentModifier(VAMPIRE_MOVEMENT_SPEED_MODIFIER_1);
		} else if (!shouldHave && attackDamageAttribute.hasModifier(VAMPIRE_ATTACK_DAMAGE_MODIFIER_1.id())) {
			attackDamageAttribute.removeModifier(VAMPIRE_ATTACK_DAMAGE_MODIFIER_1.id());
			movementSpeedAttribute.removeModifier(VAMPIRE_MOVEMENT_SPEED_MODIFIER_1.id());
		}
		shouldHave = werewolfBeast;
		if (shouldHave && !attackSpeedAttribute.hasModifier(WEREWOLF_ATTACK_SPEED_MODIFIER.id())) {
			attackSpeedAttribute.addPersistentModifier(WEREWOLF_ATTACK_SPEED_MODIFIER);
			armorAttribute.addPersistentModifier(WEREWOLF_ARMOR_MODIFIER);
			attackRange.addPersistentModifier(WEREWOLF_ATTACK_RANGE_MODIFIER);
			reach.addPersistentModifier(WEREWOLF_REACH_MODIFIER);
			stepHeight.addPersistentModifier(WEREWOLF_STEP_HEIGHT_MODIFIER);
		} else if (!shouldHave && attackSpeedAttribute.hasModifier(WEREWOLF_ATTACK_SPEED_MODIFIER.id())) {
			attackSpeedAttribute.removeModifier(WEREWOLF_ATTACK_SPEED_MODIFIER.id());
			armorAttribute.removeModifier(WEREWOLF_ARMOR_MODIFIER.id());
			attackRange.removeModifier(WEREWOLF_ATTACK_RANGE_MODIFIER.id());
			reach.removeModifier(WEREWOLF_REACH_MODIFIER.id());
			stepHeight.removeModifier(WEREWOLF_STEP_HEIGHT_MODIFIER.id());
		}
		shouldHave = werewolfBeast && !BewitchmentAPI.isPledged(obj, BWPledges.HERNE);
		if (shouldHave && !attackDamageAttribute.hasModifier(WEREWOLF_ATTACK_DAMAGE_MODIFIER_0.id())) {
			attackDamageAttribute.addPersistentModifier(WEREWOLF_ATTACK_DAMAGE_MODIFIER_0);
			armorToughnessAttribute.addPersistentModifier(WEREWOLF_ARMOR_TOUGHNESS_MODIFIER_0);
			movementSpeedAttribute.addPersistentModifier(WEREWOLF_MOVEMENT_SPEED_MODIFIER_0);
		} else if (!shouldHave && attackDamageAttribute.hasModifier(WEREWOLF_ATTACK_DAMAGE_MODIFIER_0.id())) {
			attackDamageAttribute.removeModifier(WEREWOLF_ATTACK_DAMAGE_MODIFIER_0.id());
			armorToughnessAttribute.removeModifier(WEREWOLF_ARMOR_TOUGHNESS_MODIFIER_0.id());
			movementSpeedAttribute.removeModifier(WEREWOLF_MOVEMENT_SPEED_MODIFIER_0.id());
		}
		shouldHave = werewolfBeast && BewitchmentAPI.isPledged(obj, BWPledges.HERNE);
		if (shouldHave && !attackDamageAttribute.hasModifier(WEREWOLF_ATTACK_DAMAGE_MODIFIER_1.id())) {
			attackDamageAttribute.addPersistentModifier(WEREWOLF_ATTACK_DAMAGE_MODIFIER_1);
			armorToughnessAttribute.addPersistentModifier(WEREWOLF_ARMOR_TOUGHNESS_MODIFIER_1);
			movementSpeedAttribute.addPersistentModifier(WEREWOLF_MOVEMENT_SPEED_MODIFIER_1);
		} else if (!shouldHave && attackDamageAttribute.hasModifier(WEREWOLF_ATTACK_DAMAGE_MODIFIER_1.id())) {
			attackDamageAttribute.removeModifier(WEREWOLF_ATTACK_DAMAGE_MODIFIER_1.id());
			armorToughnessAttribute.removeModifier(WEREWOLF_ARMOR_TOUGHNESS_MODIFIER_1.id());
			movementSpeedAttribute.removeModifier(WEREWOLF_MOVEMENT_SPEED_MODIFIER_1.id());
		}
	}

	private int getFirstEmptyInventorySlot() {
		for (int i = 0; i < obj.getInventory().main.size(); i++) {
			if (!PlayerInventory.isValidHotbarIndex(i) && obj.getInventory().main.get(i).isEmpty()) {
				return i;
			}
		}
		return -1;
	}
}
