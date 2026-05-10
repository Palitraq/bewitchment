/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.mixin.brew;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ConstantConditions")
@Mixin(BrewingRecipeRegistry.class)
public abstract class BrewingRecipeRegistryMixin {
	@Inject(method = "craft", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 0, target = "Lnet/minecraft/potion/PotionUtil;setPotion(Lnet/minecraft/item/ItemStack;Lnet/minecraft/potion/Potion;)Lnet/minecraft/item/ItemStack;"), cancellable = true)
	private static void craft(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<ItemStack> callbackInfo) {
		if (ingredient.contains(DataComponentTypes.CUSTOM_DATA)) {
			var ingredientNbtComponent = ingredient.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
			NbtCompound nbt = ingredientNbtComponent.copyNbt();
			if (nbt.getBoolean("BewitchmentBrew")) {
				ItemStack potionStack = new ItemStack(input.getItem() == Items.GUNPOWDER ? Items.SPLASH_POTION : input.getItem() == Items.DRAGON_BREATH ? Items.LINGERING_POTION : Items.POTION);
				var potionNbtComponent = potionStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
				var potionNbt = potionNbtComponent.copyNbt();
				potionNbt.copyFrom(nbt);
				potionStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(potionNbt));
				callbackInfo.setReturnValue(potionStack);
			}
		}
	}
}
