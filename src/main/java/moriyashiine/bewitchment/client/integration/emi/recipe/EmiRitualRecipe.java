/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.client.integration.emi.recipe;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import moriyashiine.bewitchment.client.integration.emi.BWEmiIntegration;
import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.recipe.RitualRecipe;
import moriyashiine.bewitchment.common.registry.BWObjects;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EmiRitualRecipe extends BasicEmiRecipe {
	public EmiRitualRecipe(Identifier id, RitualRecipe recipe) {
		super(BWEmiIntegration.RITUALS_CATGORY, id, 0, 18);
		for (Ingredient ingredient : recipe.input) {
			if (!ingredient.isEmpty()) {
				inputs.add(EmiIngredient.of(ingredient));
				width += 18;
			}
		}
		width += 58;
		ItemStack chalk = new ItemStack(BWObjects.GOLDEN_CHALK);
		chalk.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("ritual." + id.toString().replaceAll(":", ".").replaceAll("/", ".")));
		NbtCompound nbt = new NbtCompound();
		nbt.putString("InnerCircle", "chalk." + Bewitchment.MOD_ID + "." + recipe.inner);
		if (!recipe.outer.isEmpty()) {
			nbt.putString("OuterCircle", "chalk." + Bewitchment.MOD_ID + "." + recipe.outer);
		}
		nbt.putInt("Cost", recipe.cost);
		if (recipe.runningTime > 0) {
			nbt.putInt("RunningTime", recipe.runningTime);
		}
		chalk.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
		outputs.add(EmiStack.of(chalk));
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		int x = 0;
		for (EmiIngredient emiIngredient : inputs) {
			widgets.addSlot(emiIngredient, x, 0);
			x += 18;
		}
		widgets.addTexture(EmiTexture.EMPTY_ARROW, x + 8, 1);
		widgets.addSlot(outputs.get(0), x + 40, 0).recipeContext(this);
	}
}
