/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.mixin.brew;

import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.recipe.TippedArrowRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@SuppressWarnings("ConstantConditions")
@Mixin(TippedArrowRecipe.class)
public class TippedArrowRecipeMixin {
	@Inject(method = "craft(Lnet/minecraft/recipe/input/CraftingRecipeInput;Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Lnet/minecraft/item/ItemStack;", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
	private void craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup lookup, CallbackInfoReturnable<ItemStack> cir) {
		ItemStack stack = craftingRecipeInput.getStackInSlot(1, 1);
		var inputNbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
		var inputNbt = inputNbtComponent.copyNbt();
		if (!inputNbt.isEmpty() && inputNbt.contains("BewitchmentBrew")) {
			PotionContentsComponent potionContents = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
			int color = PotionContentsComponent.getColor(potionContents.getEffects());
			UUID uuid = null;
			String name = null;
			if (inputNbt.contains("PolymorphUUID")) {
				uuid = inputNbt.getUuid("PolymorphUUID");
				name = inputNbt.getString("PolymorphName");
			}
			stack = cir.getReturnValue();
			var resultNbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
			var resultNbt = resultNbtComponent.copyNbt();
			resultNbt.putBoolean("BewitchmentBrew", true);
			resultNbt.putInt("CustomPotionColor", color);
			if (uuid != null) {
				resultNbt.putUuid("PolymorphUUID", uuid);
				resultNbt.putString("PolymorphName", name);
			}
			stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(resultNbt));
			cir.setReturnValue(stack);
		}
	}
}
