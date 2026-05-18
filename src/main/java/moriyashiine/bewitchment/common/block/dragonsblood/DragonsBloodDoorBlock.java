/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.block.dragonsblood;

import moriyashiine.bewitchment.common.block.entity.SigilBlockEntity;
import moriyashiine.bewitchment.common.block.entity.interfaces.SigilHolder;
import moriyashiine.bewitchment.common.registry.BWBlockEntityTypes;
import moriyashiine.bewitchment.common.block.util.interfaces.SpecialDoor;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DragonsBloodDoorBlock extends DoorBlock implements BlockEntityProvider, SpecialDoor {
	public DragonsBloodDoorBlock(Settings settings) {
		super(BlockSetType.OAK, settings);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SigilBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return type == BWBlockEntityTypes.SIGIL ? (tickerWorld, pos, tickerState, blockEntity) -> SigilBlockEntity.tick(tickerWorld, pos, tickerState, (SigilBlockEntity) blockEntity) : null;
	}

	@Override
	public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		SigilHolder.onUse(world, state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos, player, hand);
		return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		SigilHolder.onUse(world, state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos, player, Hand.MAIN_HAND);
		return super.onUse(state, world, pos, player, hit);
	}

	@Override
	public ActionResult onSpecialUse(BlockState state, World world, BlockPos pos, LivingEntity user, Hand hand) {
		SigilHolder.onUse(world, state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos, user, hand);
		return ActionResult.PASS;
	}
}
