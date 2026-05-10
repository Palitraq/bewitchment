/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.block.elder;

import moriyashiine.bewitchment.common.block.entity.LockableBlockEntity;
import moriyashiine.bewitchment.common.block.entity.interfaces.Lockable;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
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

@SuppressWarnings("ConstantConditions")
public class ElderButtonBlock extends ButtonBlock implements BlockEntityProvider {
	public ElderButtonBlock(Settings settings) {
		super(BlockSetType.OAK, 30, settings);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new LockableBlockEntity(pos, state);
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

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof Lockable lockable && lockable.getLocked()) {
			return;
		}
		super.onEntityCollision(state, world, pos, entity);
	}
}
