/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.mixin;

import com.mojang.authlib.GameProfile;
import moriyashiine.bewitchment.common.item.TaglockItem;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@SuppressWarnings("ConstantConditions")
@Mixin(ItemStackArgument.class)
public class ItemStackArgumentMixin {
	@Inject(method = "createStack", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void createStack(int amount, boolean checkOverstack, CallbackInfoReturnable<ItemStack> callbackInfo, ItemStack stack) {
		if (stack.getItem() instanceof TaglockItem && stack.contains(DataComponentTypes.CUSTOM_DATA)) {
			var nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
			var nbt = nbtComponent.copyNbt();
			if (nbt.contains("OwnerName") && !nbt.contains("OwnerUUID")) {
				GameProfile profile = new GameProfile(null, nbt.getString("OwnerName"));
				nbt.putUuid("OwnerUUID", profile.getId());
				nbt.putBoolean("FromPlayer", true);
				stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
			}
		}
	}
}
