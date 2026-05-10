/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.item;

import moriyashiine.bewitchment.api.item.BroomItem;
import moriyashiine.bewitchment.common.Bewitchment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public class DragonsBloodBroomItem extends BroomItem {
	public DragonsBloodBroomItem(Settings settings, EntityType<?> broom) {
		super(settings, broom);
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		var nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
		if (nbt.contains("Sigil")) {
			tooltip.add(Text.translatable("sigil." + nbt.getString("Sigil").replace(":", ".")).formatted(Formatting.GRAY));
			tooltip.add(Text.translatable(Bewitchment.MOD_ID + ".tooltip.uses_left", nbt.getInt("Uses")).formatted(Formatting.GRAY));
		}
	}
}
