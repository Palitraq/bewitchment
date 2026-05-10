/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.item;

import moriyashiine.bewitchment.common.Bewitchment;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public class WaystoneItem extends Item {
	public WaystoneItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		boolean client = world.isClient;
		if (!client) {
			context.getStack().apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(nbt -> {
				nbt.putLong("LocationPos", context.getBlockPos().offset(context.getSide()).asLong());
				nbt.putString("LocationWorld", world.getRegistryKey().getValue().toString());
			}));
		}
		return ActionResult.success(client);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
		NbtCompound nbt = nbtComponent.copyNbt();
		if (nbt.contains("LocationPos")) {
			BlockPos pos = BlockPos.fromLong(nbt.getLong("LocationPos"));
			tooltip.add(Text.translatable(Bewitchment.MOD_ID + ".tooltip.location", pos.getX(), pos.getY(), pos.getZ(), nbt.getString("LocationWorld")).formatted(Formatting.GRAY));
		}
	}
}
