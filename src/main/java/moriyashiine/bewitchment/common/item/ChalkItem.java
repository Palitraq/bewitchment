/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public class ChalkItem extends Item {
	private final Block glyph;

	public ChalkItem(Settings settings, Block glyph) {
		super(settings);
		this.glyph = glyph;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		boolean client = world.isClient;
		ItemPlacementContext placementContext = new ItemPlacementContext(context);
		if (!world.getBlockState(pos).canReplace(placementContext)) {
			pos = pos.offset(context.getSide());
		}
		if (!world.getBlockState(pos).canReplace(placementContext)) {
			return ActionResult.PASS;
		}
		BlockState state = glyph.getPlacementState(placementContext);
		if (state.canPlaceAt(world, pos)) {
			if (!client) {
				PlayerEntity player = context.getPlayer();
				ItemStack stack = context.getStack();
				world.playSound(null, pos, state.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 1, MathHelper.nextFloat(world.random, 0.8f, 1.2f));
				world.setBlockState(pos, state);
				if (player instanceof ServerPlayerEntity serverPlayer) {
					Criteria.PLACED_BLOCK.trigger(serverPlayer, pos, stack);
					EquipmentSlot slot = context.getHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
					stack.damage(1, serverPlayer.getServerWorld(), serverPlayer, item -> {});
				}
			}
			return ActionResult.success(client);
		}
		return super.useOnBlock(context);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public String getTranslationKey() {
		return glyph.getTranslationKey();
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		var nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
		if (nbt.contains("InnerCircle")) {
			tooltip.add(Text.translatable("bewitchment.tooltip.inner_circle", Text.translatable(nbt.getString("InnerCircle"))).formatted(Formatting.GRAY));
			if (nbt.contains("OuterCircle")) {
				tooltip.add(Text.translatable("bewitchment.tooltip.outer_circle", Text.translatable(nbt.getString("OuterCircle"))).formatted(Formatting.GRAY));
			}
			tooltip.add(Text.translatable("bewitchment.tooltip.cost", nbt.getInt("Cost")).formatted(Formatting.GRAY));
			if (nbt.contains("RunningTime")) {
				tooltip.add(Text.translatable("bewitchment.tooltip.running_time", nbt.getInt("RunningTime") / 20f).formatted(Formatting.GRAY));
			}
		}
	}
}
