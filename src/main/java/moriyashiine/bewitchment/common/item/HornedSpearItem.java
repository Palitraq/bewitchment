/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.item;

import com.google.common.collect.Multimap;
import moriyashiine.bewitchment.client.packet.SyncHornedSpearPacket;
import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.entity.projectile.HornedSpearEntity;
import moriyashiine.bewitchment.common.registry.BWEntityTypes;
import moriyashiine.bewitchment.common.registry.BWSoundEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class HornedSpearItem extends SwordItem {
	private static final EntityAttributeModifier REACH_MODIFIER = new EntityAttributeModifier(Bewitchment.id("spear_reach"), 2, EntityAttributeModifier.Operation.ADD_VALUE);

	public HornedSpearItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
		super(toolMaterial, settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (stack.getDamage() >= stack.getMaxDamage() - 1) {
			return TypedActionResult.fail(stack);
		}
		return ItemUsage.consumeHeldItem(world, user, hand);
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		int timer = getMaxUseTime(stack, user) - remainingUseTicks;
		if (timer >= 10) {
			if (!world.isClient) {
				spawnEntity(world, user, stack);
			}
			if (user instanceof PlayerEntity player) {
				player.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.SPEAR;
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		return 72000;
	}

	public static void spawnEntity(World world, LivingEntity owner, ItemStack stack) {
		if (owner instanceof ServerPlayerEntity serverPlayer) {
			EquipmentSlot slot = serverPlayer.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
			stack.damage(1, serverPlayer.getServerWorld(), serverPlayer, item -> {});
		}
		HornedSpearEntity spear = new HornedSpearEntity(BWEntityTypes.HORNED_SPEAR, owner, world, stack.copy());
		spear.setVelocity(owner, owner.getPitch(), owner.getYaw(), 0, 3, 1);
		if (owner instanceof PlayerEntity player) {
			if (player.isCreative()) {
				spear.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
			}
		} else {
			spear.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
		}
		world.spawnEntity(spear);
		PlayerLookup.tracking(spear).forEach(trackingPlayer -> SyncHornedSpearPacket.send(trackingPlayer, spear));
		world.playSoundFromEntity(null, spear, BWSoundEvents.ITEM_HORNED_SPEAR_USE, SoundCategory.PLAYERS, 1, 1);
		if (owner instanceof PlayerEntity player && !player.isCreative()) {
			stack.decrement(1);
		}
	}
}
