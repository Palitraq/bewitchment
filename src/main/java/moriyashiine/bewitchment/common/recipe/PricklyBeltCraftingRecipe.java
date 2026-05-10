/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.recipe;

import moriyashiine.bewitchment.common.item.PricklyBeltItem;
import moriyashiine.bewitchment.common.registry.BWRecipeTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class PricklyBeltCraftingRecipe extends SpecialCraftingRecipe {
	public PricklyBeltCraftingRecipe() {
		super(CraftingRecipeCategory.EQUIPMENT);
	}

	@Override
	public boolean matches(CraftingRecipeInput inventory, World world) {
		boolean foundPricklyBelt = false, foundPotion = false;
		int foundItems = 0;
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.getItem() instanceof PricklyBeltItem) {
				if (!foundPricklyBelt) {
					foundPricklyBelt = true;
				}
				foundItems++;
			} else if (stack.getItem() == Items.POTION && !stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).customEffects().isEmpty()) {
				if (!foundPotion) {
					foundPotion = true;
				}
				foundItems++;
			}
		}
		return foundPricklyBelt && foundPotion && foundItems == 2;
	}

	@Override
	public ItemStack craft(CraftingRecipeInput inventory, RegistryWrapper.WrapperLookup lookup) {
		ItemStack pricklyBelt = null, potion = null;
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.getItem() instanceof PricklyBeltItem) {
				pricklyBelt = stack.copy();
			} else if (stack.getItem() == Items.POTION) {
				potion = stack.copy();
			}
		}
		int uses = 1;
		PotionContentsComponent potionContents = potion.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
		List<StatusEffectInstance> effects = potionContents.customEffects().isEmpty() ? pricklyBelt.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).customEffects() : potionContents.customEffects();
		for (int i = 0; i < effects.size(); i++) {
			StatusEffectInstance effect = effects.get(i);
			int duration = effect.getDuration();
			int amplifier = effect.getAmplifier();
			while (duration > 600) {
				uses++;
				duration /= 2;
			}
			while (amplifier > 0) {
				uses += 2;
				amplifier--;
			}
			effects.set(i, new StatusEffectInstance(effect.getEffectType(), duration, amplifier, effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()));
		}
		var potionNbt = potion.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
		var potionNbtCopy = potionNbt.copyNbt();
		int finalUses = uses;
		if (potionNbtCopy.contains("PolymorphUUID")) {
			var polyUuid = potionNbtCopy.getUuid("PolymorphUUID");
			var polyName = potionNbtCopy.getString("PolymorphName");
			pricklyBelt.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(nbt -> {
				nbt.putUuid("PolymorphUUID", polyUuid);
				nbt.putString("PolymorphName", polyName);
			}));
		}
		pricklyBelt.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(), Optional.empty(), effects));
		pricklyBelt.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(nbt -> nbt.putInt("PotionUses", finalUses)));
		return pricklyBelt;
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return BWRecipeTypes.PRICKLY_BELT_CRAFTING_SERIALIZER;
	}
}
