/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.client.integration.emi.recipe;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import moriyashiine.bewitchment.client.integration.emi.BWEmiIntegration;
import moriyashiine.bewitchment.common.recipe.AthameDropRecipe;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class EmiAthameDropRecipe extends BasicEmiRecipe {
	public EmiAthameDropRecipe(AthameDropRecipe recipe) {
		super(BWEmiIntegration.ATHAME_DROPS_CATEGORY, recipe.getId(), 76, 18);
		ItemStack spawnerStack = new ItemStack(Items.SPAWNER);
		spawnerStack.set(DataComponentTypes.ITEM_NAME, recipe.entity_type.getName());
		inputs.add(EmiStack.of(spawnerStack));
		outputs.add(EmiStack.of(EmiPort.getOutput(recipe)));
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 26, 1);
		widgets.addSlot(inputs.get(0), 0, 0);
		widgets.addSlot(outputs.get(0), 58, 0).recipeContext(this);
	}
}
