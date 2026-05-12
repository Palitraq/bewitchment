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
import moriyashiine.bewitchment.common.recipe.CauldronBrewingRecipe;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;


import java.util.Collections;
import java.util.List;

public class EmiCauldronBrewingRecipe extends BasicEmiRecipe {
	public EmiCauldronBrewingRecipe(Identifier id, CauldronBrewingRecipe recipe) {
		super(BWEmiIntegration.CAULDRON_BREWING_CATEGORY, id, 76, 18);
		inputs.add(EmiIngredient.of(recipe.input));
		ItemStack potion = new ItemStack(Items.POTION);
		NbtCompound nbt = new NbtCompound();
		nbt.putBoolean("BewitchmentBrew", true);
		potion.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
		outputs.add(EmiStack.of(potion));
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 26, 1);
		widgets.addSlot(inputs.get(0), 0, 0);
		widgets.addSlot(outputs.get(0), 58, 0).recipeContext(this);
	}
}
