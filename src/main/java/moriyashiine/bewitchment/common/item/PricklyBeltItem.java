/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.item;

import dev.emi.trinkets.api.TrinketItem;
import moriyashiine.bewitchment.common.Bewitchment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public class PricklyBeltItem extends TrinketItem {
	public PricklyBeltItem(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		int uses = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt().getInt("PotionUses");
		tooltip.add(Text.translatable(Bewitchment.MOD_ID + ".tooltip.uses_left", uses).formatted(Formatting.GRAY));

	}
}
