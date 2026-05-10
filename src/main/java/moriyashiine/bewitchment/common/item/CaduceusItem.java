/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.item;

import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.common.registry.BWComponents;
import moriyashiine.bewitchment.common.registry.BWSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CaduceusItem extends MiningToolItem {
	public CaduceusItem(ToolMaterial material, TagKey<Block> effectiveBlocks, Settings settings) {
		super(material, effectiveBlocks, settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return ItemUsage.consumeHeldItem(world, user, hand);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (user instanceof PlayerEntity player) {
			if (!world.isClient && BewitchmentAPI.drainMagic(player, 2, false)) {
				FireballEntity fireball = new FireballEntity(world, user, new Vec3d(user.getRotationVector().x, user.getRotationVector().y, user.getRotationVector().z), 1);
				fireball.setOwner(user);
				fireball.setPos(fireball.getX(), fireball.getY() + 1, fireball.getZ());
				BWComponents.CADUCEUS_FIREBALL_COMPONENT.get(fireball).setFromCaduceus(true);
				world.playSound(null, user.getBlockPos(), BWSoundEvents.ENTITY_GENERIC_SHOOT, SoundCategory.HOSTILE, 1, 1);
				world.spawnEntity(fireball);
				if (user instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
					EquipmentSlot slot = user.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
					stack.damage(1, serverPlayer.getServerWorld(), serverPlayer, item -> {});
				}
			}
		}
		return stack;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		return 24;
	}

	@Override
	public boolean isCorrectForDrops(ItemStack stack, BlockState state) {
		return true;
	}

	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		return getMaterial().getMiningSpeedMultiplier() * 0.75f;
	}
}
