/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.recipe;

import moriyashiine.bewitchment.common.item.ScepterItem;
import moriyashiine.bewitchment.common.registry.BWRecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class ScepterCraftingRecipe extends SpecialCraftingRecipe {
	public ScepterCraftingRecipe(CraftingRecipeCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingRecipeInput inventory, World world) {
		boolean foundScepter = false, foundPotion = false;
		int foundItems = 0;
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.getItem() instanceof ScepterItem) {
				if (!foundScepter) {
					foundScepter = true;
				}
				foundItems++;
			} else if (stack.getItem() instanceof SplashPotionItem && (!stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).customEffects().isEmpty())) {
				if (!foundPotion) {
					foundPotion = true;
				}
				foundItems++;
			}
		}
		return foundScepter && foundPotion && foundItems == 2;
	}

	@Override
	public ItemStack craft(CraftingRecipeInput inventory, RegistryWrapper.WrapperLookup lookup) {
		ItemStack scepter = null, potion = null;
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.getItem() instanceof ScepterItem) {
				scepter = stack.copy();
			} else if (stack.getItem() instanceof SplashPotionItem) {
				potion = stack.copy();
			}
		}
		scepter.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(nbt -> nbt.putInt("PotionUses", 4)));
		PotionContentsComponent potionContents = potion.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
		scepter.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(), Optional.empty(), potionContents.customEffects()));
		var potionNbt = potion.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
		var potionNbtCopy = potionNbt.copyNbt();
		if (potionNbtCopy.contains("PolymorphUUID")) {
			var polyUuid = potionNbtCopy.getUuid("PolymorphUUID");
			var polyName = potionNbtCopy.getString("PolymorphName");
			scepter.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(nbt -> {
				nbt.putUuid("PolymorphUUID", polyUuid);
				nbt.putString("PolymorphName", polyName);
			}));
		}
		return scepter;
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return BWRecipeTypes.SCEPTER_CRAFTING_SERIALIZER;
	}
}
