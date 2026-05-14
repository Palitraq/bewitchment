/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.item;

import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.block.dragonsblood.DragonsBloodLogBlock;
import moriyashiine.bewitchment.common.block.entity.interfaces.Lockable;
import moriyashiine.bewitchment.common.block.entity.interfaces.SigilHolder;
import moriyashiine.bewitchment.common.block.entity.interfaces.TaglockHolder;
import moriyashiine.bewitchment.common.recipe.AthameStrippingRecipe;
import moriyashiine.bewitchment.common.registry.BWObjects;
import moriyashiine.bewitchment.common.registry.BWProperties;
import moriyashiine.bewitchment.common.registry.BWRecipeTypes;
import moriyashiine.bewitchment.common.registry.BWSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AthameItem extends SwordItem {
	private static final EntityAttributeModifier REACH_MODIFIER = new EntityAttributeModifier(Bewitchment.id("weapon_reach"), -0.5, EntityAttributeModifier.Operation.ADD_VALUE);

	public AthameItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
		super(toolMaterial, settings.component(DataComponentTypes.ATTRIBUTE_MODIFIERS,
				SwordItem.createAttributeModifiers(toolMaterial, attackDamage, attackSpeed)
						.with(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE, REACH_MODIFIER, AttributeModifierSlot.MAINHAND)
		));
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		BlockState state = world.getBlockState(pos);
		PlayerEntity player = context.getPlayer();
		boolean client = world.isClient;
		AthameStrippingRecipe entry = world.getRecipeManager().listAllOfType(BWRecipeTypes.ATHAME_STRIPPING_RECIPE_TYPE).stream().filter(recipe -> recipe.value().log == state.getBlock()).findFirst().map(RecipeEntry::value).orElse(null);
		if (entry != null) {
			world.playSound(player, pos, BWSoundEvents.ITEM_ATHAME_STRIP, SoundCategory.BLOCKS, 1, 1);
			if (!client) {
				world.setBlockState(pos, entry.strippedLog.getDefaultState().with(PillarBlock.AXIS, state.get(PillarBlock.AXIS)), 11);
				if (player != null) {
					if (player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
						EquipmentSlot slot = context.getHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
						context.getStack().damage(1, serverPlayer.getServerWorld(), serverPlayer, item -> {});
					}
					if (world.random.nextFloat() < 2 / 3f) {
						ItemStack bark = entry.getResult(world.getRegistryManager()).copy();
						if (!player.getInventory().insertStack(bark)) {
							player.dropStack(bark);
						}
					}
				}
			}
			return ActionResult.success(client);
		}
		BlockEntity blockEntity = world.getBlockEntity(state.getBlock() instanceof DoorBlock && state.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos);
		if (blockEntity instanceof SigilHolder sigilHolder) {
			if (player != null && player.getUuid().equals(sigilHolder.getOwner())) {
				if (!client && !sigilHolder.getEntities().isEmpty()) {
					boolean whitelist = sigilHolder.getModeOnWhitelist();
					world.playSound(null, pos, BWSoundEvents.BLOCK_SIGIL_PLING, SoundCategory.BLOCKS, 1, whitelist ? 0.5f : 1);
					player.sendMessage(Text.translatable(Bewitchment.MOD_ID + ".message.toggle_" + (!whitelist ? "whitelist" : "blacklist")), true);
					sigilHolder.setModeOnWhitelist(!whitelist);
					blockEntity.markDirty();
				}
				return ActionResult.success(client);
			}
		} else if (blockEntity instanceof TaglockHolder taglockHolder) {
			if (player != null && player.getUuid().equals(taglockHolder.getOwner()) && taglockHolder.getFirstEmptySlot() != 0) {
				if (!client) {
					ItemScatterer.spawn(world, pos, taglockHolder.getTaglockInventory());
					taglockHolder.syncTaglockHolder(blockEntity);
					blockEntity.markDirty();
				}
				return ActionResult.success(client);
			}
		} else if (blockEntity instanceof Lockable lockable) {
			if (player != null && player.getUuid().equals(lockable.getOwner()) && !lockable.getEntities().isEmpty()) {
				if (!client) {
					boolean whitelist = lockable.getModeOnWhitelist();
					world.playSound(null, pos, BWSoundEvents.BLOCK_SIGIL_PLING, SoundCategory.BLOCKS, 1, whitelist ? 0.5f : 1);
					player.sendMessage(Text.translatable(Bewitchment.MOD_ID + ".message.toggle_" + (!whitelist ? "whitelist" : "blacklist")), true);
					lockable.setModeOnWhitelist(!whitelist);
					blockEntity.markDirty();
				}
				return ActionResult.success(client);
			}
		}
		return super.useOnBlock(context);
	}

	private static boolean cutLog(World world, BlockPos pos, ItemStack stack) {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof DragonsBloodLogBlock && state.get(BWProperties.NATURAL) && !state.get(BWProperties.CUT)) {
			world.playSound(null, pos, BWSoundEvents.ITEM_ATHAME_STRIP, SoundCategory.BLOCKS, 1, 1);
			world.setBlockState(pos, state.with(BWProperties.CUT, true));
			if (!stack.isDamageable() || (stack.getDamage() + 1 >= stack.getMaxDamage())) {
				stack.decrement(1);
			}
			return true;
		}
		return false;
	}
}
