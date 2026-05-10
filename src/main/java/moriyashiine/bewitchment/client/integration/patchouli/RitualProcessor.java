/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.client.integration.patchouli;

import moriyashiine.bewitchment.common.recipe.RitualRecipe;
import moriyashiine.bewitchment.common.registry.BWRecipeTypes;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

@SuppressWarnings("ConstantConditions")
public class RitualProcessor implements IComponentProcessor {
	protected RitualRecipe recipe;

	@Override
	public void setup(World level, IVariableProvider variables) {
		recipe = level.getRecipeManager().get(Identifier.tryParse(variables.get("recipe", level.getRegistryManager()).asString())).map(RecipeEntry::value).filter(recipe -> recipe.getType().equals(BWRecipeTypes.RITUAL_RECIPE_TYPE)).map(recipe -> (RitualRecipe) recipe).orElseThrow(IllegalArgumentException::new);
	}

	@Override
	public IVariable process(World level, String key) {
		switch (key) {
			case "header" -> {
				return IVariable.from(Text.translatable("ritual." + recipe.getId().toString().replaceAll(":", ".").replaceAll("/", ".")), level.getRegistryManager());
			}
			case "inner" -> {
				return IVariable.wrap(recipe.inner, level.getRegistryManager());
			}
			case "outer" -> {
				return IVariable.wrap(recipe.outer, level.getRegistryManager());
			}
			case "cost" -> {
				return IVariable.wrap("$(o)" + I18n.translate("bewitchment.tooltip.cost", recipe.cost), level.getRegistryManager());
			}
		}
		for (int i = 0; i < recipe.input.size(); i++) {
			if (key.equals("ingredient" + i)) {
				ItemStack[] stack = recipe.input.get(i).getMatchingStacks();
				return stack.length > 0 ? IVariable.from(stack[0], level.getRegistryManager()) : null;
			}

		}
		return null;
	}
}
