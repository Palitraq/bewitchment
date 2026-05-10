/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.mixin.poppet;

import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.api.misc.PoppetData;
import moriyashiine.bewitchment.common.registry.BWObjects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow
	public abstract void setDamage(int damage);

	@Shadow
	public abstract int getDamage();

	@Shadow
	public abstract int getMaxDamage();

	@Inject(method = "damage(ILnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"), cancellable = true)
	private void damage(int amount, ServerWorld world, ServerPlayerEntity player, Consumer<Item> breakCallback, CallbackInfo callbackInfo) {
		if (getDamage() == getMaxDamage()) {
			if (player == null) return;
			PoppetData poppetData = BewitchmentAPI.getPoppet(world, BWObjects.MENDING_POPPET, player);
			if (!poppetData.stack().isEmpty()) {
				boolean sync = false;
				poppetData.stack().damage(player instanceof PlayerEntity p && BewitchmentAPI.getFamiliar(p) == EntityType.WOLF && player.getRandom().nextBoolean() ? 0 : 1, world, player, item -> {});
				if (poppetData.stack().getDamage() == poppetData.stack().getMaxDamage()) {
					poppetData.stack().decrement(1);
					sync = true;
				}
				poppetData.update(world, sync);
				setDamage(0);
				callbackInfo.cancel();
			}
		}
	}
}
