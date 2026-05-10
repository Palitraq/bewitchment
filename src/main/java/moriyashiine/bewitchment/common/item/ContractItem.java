/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.item;

import moriyashiine.bewitchment.api.registry.Contract;
import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.registry.BWComponents;
import moriyashiine.bewitchment.common.registry.BWRegistries;
import moriyashiine.bewitchment.common.registry.BWSoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public class ContractItem extends Item {
	public ContractItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return ItemUsage.consumeHeldItem(world, user, hand);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (!world.isClient && user instanceof PlayerEntity player) {
			var nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
			Contract contract = BWRegistries.CONTRACT.get(Identifier.tryParse(nbt.getString("Contract")));
			if (contract != null) {
				BWComponents.CONTRACTS_COMPONENT.get(player).addContract(new Contract.Instance(contract, nbt.getInt("Duration"), 0));
				world.playSound(null, player.getBlockPos(), BWSoundEvents.ITEM_CONTRACT_USE, SoundCategory.PLAYERS, 1, 1);
				if (!player.isCreative()) {
					stack.decrement(1);
				}
				return stack;
			}
		}
		return stack;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public int getMaxUseTime(ItemStack stack, net.minecraft.entity.LivingEntity user) {
		return 32;
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		var nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
		if (nbt.contains("Contract")) {
			tooltip.add(Text.translatable("contract." + nbt.getString("Contract").replace(":", ".")).formatted(Formatting.DARK_RED));
			tooltip.add(Text.translatable(Bewitchment.MOD_ID + ".tooltip.days", nbt.getInt("Duration") / 24000).formatted(Formatting.DARK_RED));
		}
	}
}
