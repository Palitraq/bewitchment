/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.registry;

import moriyashiine.bewitchment.common.Bewitchment;

import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvents;

import java.util.List;
import java.util.Map;

public class BWMaterials {
	public static final ArmorMaterial HEDGEWITCH_ARMOR = new ArmorMaterial(
			Map.of(
					ArmorItem.Type.HELMET, 3,
					ArmorItem.Type.CHESTPLATE, 6,
					ArmorItem.Type.LEGGINGS, 4,
					ArmorItem.Type.BOOTS, 2
			),
			15,
			SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
			() -> Ingredient.ofItems(BWObjects.HEDGEWITCH_WOOL),
			List.of(new ArmorMaterial.Layer(Bewitchment.id("hedgewitch"))),
			0.0F,
			0.0F
	);

	public static final ArmorMaterial ALCHEMIST_ARMOR = new ArmorMaterial(
			Map.of(
					ArmorItem.Type.HELMET, 3,
					ArmorItem.Type.CHESTPLATE, 6,
					ArmorItem.Type.LEGGINGS, 4,
					ArmorItem.Type.BOOTS, 2
			),
			15,
			SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
			() -> Ingredient.ofItems(BWObjects.ALCHEMIST_WOOL),
			List.of(new ArmorMaterial.Layer(Bewitchment.id("alchemist"))),
			0.0F,
			0.0F
	);

	public static final ArmorMaterial BESMIRCHED_ARMOR = new ArmorMaterial(
			Map.of(
					ArmorItem.Type.HELMET, 3,
					ArmorItem.Type.CHESTPLATE, 6,
					ArmorItem.Type.LEGGINGS, 4,
					ArmorItem.Type.BOOTS, 2
			),
			15,
			SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
			() -> Ingredient.ofItems(BWObjects.BESMIRCHED_WOOL),
			List.of(new ArmorMaterial.Layer(Bewitchment.id("besmirched"))),
			0.0F,
			0.0F
	);

	public static final ArmorMaterial HARBINGER_ARMOR = new ArmorMaterial(
			Map.of(
					ArmorItem.Type.HELMET, 3,
					ArmorItem.Type.CHESTPLATE, 6,
					ArmorItem.Type.LEGGINGS, 4,
					ArmorItem.Type.BOOTS, 2
			),
			15,
			SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
			() -> ArmorMaterials.NETHERITE.value().repairIngredient().get(),
			List.of(new ArmorMaterial.Layer(Bewitchment.id("harbinger"))),
			4.0F,
			0.1F
	);

	public static final ToolMaterial SILVER_TOOL = new ToolMaterial() {
		@Override
		public int getDurability() {
			return ToolMaterials.IRON.getDurability();
		}

		@Override
		public float getMiningSpeedMultiplier() {
			return ToolMaterials.GOLD.getMiningSpeedMultiplier();
		}

		@Override
		public float getAttackDamage() {
			return ToolMaterials.IRON.getAttackDamage();
		}

		@Override
		public int getEnchantability() {
			return ToolMaterials.GOLD.getEnchantability();
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.ofItems(BWObjects.SILVER_INGOT);
		}

		@Override
		public TagKey<Block> getInverseTag() {
			return ToolMaterials.IRON.getInverseTag();
		}
	};
}
