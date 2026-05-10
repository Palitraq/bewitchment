/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.recipe;

import moriyashiine.bewitchment.api.item.PoppetItem;
import moriyashiine.bewitchment.common.item.TaglockItem;
import moriyashiine.bewitchment.common.registry.BWRecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class TaglockCraftingRecipe extends SpecialCraftingRecipe {
	public TaglockCraftingRecipe(CraftingRecipeCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingRecipeInput inventory, World world) {
		boolean foundTaglock = false, foundCraftable = false;
		int foundItems = 0;
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.getItem() instanceof TaglockItem && TaglockItem.hasTaglock(stack)) {
				if (!foundTaglock) {
					foundTaglock = true;
				}
				foundItems++;
			} else if (isTaglockCraftable(stack)) {
				if (!foundCraftable) {
					foundCraftable = true;
				}
				foundItems++;
			}
		}
		return foundTaglock && foundCraftable && foundItems == 2;
	}

	@Override
	public ItemStack craft(CraftingRecipeInput inventory, RegistryWrapper.WrapperLookup lookup) {
		ItemStack taglock = null, craftedStack = null;
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.getItem() instanceof TaglockItem) {
				taglock = stack.copy();
			} else if (isTaglockCraftable(stack)) {
				craftedStack = stack.copy();
			}
		}
		return TaglockItem.copyTo(taglock, craftedStack);
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return BWRecipeTypes.TAGLOCK_CRAFTING_SERIALIZER;
	}

	private static boolean isTaglockCraftable(ItemStack stack) {
		if (stack.getItem() instanceof PoppetItem) {
			return TaglockItem.getTaglockUUID(stack) == null;
		}
		return false;
	}
}
