/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.item;

import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.api.item.PoppetItem;
import moriyashiine.bewitchment.api.misc.PoppetData;
import moriyashiine.bewitchment.api.registry.Curse;
import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.registry.BWComponents;
import moriyashiine.bewitchment.common.registry.BWObjects;
import moriyashiine.bewitchment.common.registry.BWRegistries;
import moriyashiine.bewitchment.common.registry.BWSoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class CursePoppetItem extends PoppetItem {
	public CursePoppetItem(Settings settings, boolean worksInShelf) {
		super(settings, worksInShelf);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		var nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
		return nbt.getBoolean("Cursed") && nbt.contains("OwnerUUID") ? ItemUsage.consumeHeldItem(world, user, hand) : super.use(world, user, hand);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (!world.isClient && stack.contains(DataComponentTypes.CUSTOM_DATA)) {
			MinecraftServer server = world.getServer();
			var stackNbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
			if (server != null && stackNbt.contains("Cursed")) {
				UUID uuid = TaglockItem.getTaglockUUID(stack);
				for (ServerWorld serverWorld : server.getWorlds()) {
					Entity entity = serverWorld.getEntity(uuid);
					if (entity instanceof LivingEntity livingEntity) {
						boolean failed = false;
						Curse curse = BWRegistries.CURSE.get(Identifier.tryParse(stackNbt.getString("Curse")));
						PoppetData poppetData = BewitchmentAPI.getPoppet(world, BWObjects.CURSE_POPPET, entity);
						if (!poppetData.stack().isEmpty()) {
							var pNbtComponent = poppetData.stack().getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
							var pNbt = pNbtComponent.copyNbt();
							if (!pNbt.getBoolean("Cursed")) {
								pNbt.putString("Curse", BWRegistries.CURSE.getId(curse).toString());
								pNbt.putBoolean("Cursed", true);
								poppetData.stack().set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(pNbt));
								TaglockItem.removeTaglock(poppetData.stack());
								poppetData.update(world, true);
								failed = true;
							}
						}
						if (curse != null) {
							if (!failed) {
								BWComponents.CURSES_COMPONENT.get(livingEntity).addCurse(new Curse.Instance(curse, 168000));
							}
							world.playSound(null, user.getBlockPos(), BWSoundEvents.ENTITY_GENERIC_CURSE, SoundCategory.PLAYERS, 1, 1);
							if (!(user instanceof PlayerEntity && ((PlayerEntity) user).isCreative())) {
								stack.decrement(1);
							}
							return stack;
						}
					}
				}
				if (user instanceof PlayerEntity player) {
					player.sendMessage(Text.translatable(Bewitchment.MOD_ID + ".message.invalid_entity", stackNbt.getString("OwnerName")), true);
				}
			}
		}
		return stack;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		var nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
		return nbt.getBoolean("Cursed") && nbt.contains("OwnerUUID") ? UseAction.BOW : super.getUseAction(stack);
	}

	@Override
	public int getMaxUseTime(ItemStack stack, net.minecraft.entity.LivingEntity user) {
		return 32;
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		super.appendTooltip(stack, context, tooltip, type);
		var nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
		if (nbt.contains("Curse")) {
			tooltip.add(Text.translatable("curse." + nbt.getString("Curse").replace(":", ".")).formatted(Formatting.DARK_RED));
		}
	}
}
