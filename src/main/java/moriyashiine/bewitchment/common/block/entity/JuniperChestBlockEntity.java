/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.block.entity;

import moriyashiine.bewitchment.common.block.entity.interfaces.TaglockHolder;
import moriyashiine.bewitchment.common.registry.BWBlockEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class JuniperChestBlockEntity extends BWChestBlockEntity implements TaglockHolder {
	private final DefaultedList<ItemStack> taglockInventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
	private UUID owner = null;

	public JuniperChestBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, Type type, boolean trapped) {
		super(blockEntityType, blockPos, blockState, type, trapped);
	}

	public JuniperChestBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(BWBlockEntityTypes.JUNIPER_CHEST, blockPos, blockState, Type.JUNIPER, false);
	}

	@Override
	public DefaultedList<ItemStack> getTaglockInventory() {
		return taglockInventory;
	}

	@Override
	public UUID getOwner() {
		return owner;
	}

	@Override
	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup lookup) {
		NbtCompound nbt = super.toInitialChunkDataNbt(lookup);
		writeNbt(nbt, lookup);
		return nbt;
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
		super.readNbt(nbt, lookup);
		fromNbtTaglock(nbt, lookup);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
		super.writeNbt(nbt, lookup);
		toNbtTaglock(nbt, lookup);
	}

	public void sync() {
		if (world != null && !world.isClient) {
			world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
		}
	}
}
