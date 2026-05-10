/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.block.elder;

import moriyashiine.bewitchment.common.block.BWChestBlock;
import moriyashiine.bewitchment.common.block.entity.BWChestBlockEntity;
import moriyashiine.bewitchment.common.block.entity.ElderChestBlockEntity;
import moriyashiine.bewitchment.common.block.entity.interfaces.Lockable;
import moriyashiine.bewitchment.common.registry.BWBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
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

import java.util.function.Supplier;

@SuppressWarnings("ConstantConditions")
public class ElderChestBlock extends BWChestBlock {
	public ElderChestBlock(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, boolean trapped) {
		super(settings, supplier, trapped);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ElderChestBlockEntity(BWBlockEntityTypes.ELDER_CHEST, pos, state, BWChestBlockEntity.Type.ELDER, trapped);
	}

	@Override
	public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ActionResult lockableResult = Lockable.onUse(world, pos, player, hand);
		if (lockableResult == ActionResult.FAIL) {
			return ItemActionResult.FAIL;
		} else if (lockableResult == ActionResult.SUCCESS) {
			return ItemActionResult.SUCCESS;
		}
		return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		ActionResult result = Lockable.onUse(world, pos, player, Hand.MAIN_HAND);
		if (result != ActionResult.PASS) {
			return result;
		}
		return super.onUse(state, world, pos, player, hit);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		if (!world.isClient && placer != null) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			Lockable lockable = (Lockable) blockEntity;
			lockable.setOwner(placer.getUuid());
			lockable.syncLockable(blockEntity);
			blockEntity.markDirty();
		}
	}
}
