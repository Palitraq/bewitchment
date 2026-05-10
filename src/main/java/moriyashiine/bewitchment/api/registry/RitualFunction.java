/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.api.registry;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class RitualFunction {
	public final ParticleType<?> startParticle;
	public final Predicate<LivingEntity> sacrifice;

	public RitualFunction(ParticleType<?> startParticle, Predicate<LivingEntity> sacrifice) {
		this.startParticle = startParticle;
		this.sacrifice = sacrifice;
	}

	public String getInvalidMessage() {
		return "ritual.precondition.sacrifice";
	}

	public boolean isValid(ServerWorld world, BlockPos pos, Inventory inventory) {
		return sacrifice == null;
	}

	public void start(ServerWorld world, BlockPos glyphPos, BlockPos effectivePos, Inventory inventory, boolean catFamiliar) {
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.isDamageable()) {
				stack.damage(1, world.getPlayers().isEmpty() ? null : world.getPlayers().get(0), EquipmentSlot.MAINHAND);
				if (stack.getDamage() >= stack.getMaxDamage() - 1) {
					stack.decrement(1);
				}
			} else {
				Item item = stack.getItem();
				inventory.setStack(i, item.hasRecipeRemainder() ? new ItemStack(item.getRecipeRemainder()) : ItemStack.EMPTY);
			}
		}
	}

	public void tick(World world, BlockPos glyphPos, BlockPos effectivePos, boolean catFamiliar) {
	}
}
