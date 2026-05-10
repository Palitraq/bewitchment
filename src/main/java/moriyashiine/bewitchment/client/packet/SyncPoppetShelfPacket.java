/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.client.packet;

import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.block.entity.PoppetShelfBlockEntity;
import moriyashiine.bewitchment.common.world.BWWorldState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class SyncPoppetShelfPacket implements CustomPayload {
	public static final Id<SyncPoppetShelfPacket> ID = new Id<>(Bewitchment.id("sync_poppet_shelf"));
	public static final PacketCodec<RegistryByteBuf, SyncPoppetShelfPacket> CODEC = CustomPayload.codecOf(SyncPoppetShelfPacket::write, SyncPoppetShelfPacket::new);

	private final BlockPos pos;
	private final DefaultedList<ItemStack> inventory;

	public SyncPoppetShelfPacket(BlockPos pos, DefaultedList<ItemStack> inventory) {
		this.pos = pos;
		this.inventory = inventory;
	}

	public SyncPoppetShelfPacket(RegistryByteBuf buf) {
		this.pos = BlockPos.fromLong(buf.readLong());
		this.inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
		for (int i = 0; i < 9; i++) {
			inventory.set(i, ItemStack.OPTIONAL_PACKET_CODEC.decode(buf));
		}
	}

	public static void send(net.minecraft.server.network.ServerPlayerEntity player, BlockPos pos) {
		DefaultedList<ItemStack> inventory = BWWorldState.get(player.getWorld()).poppetShelves.get(pos.asLong());
		DefaultedList<ItemStack> invToSend = inventory != null ? inventory : DefaultedList.ofSize(9, ItemStack.EMPTY);
		player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket(new SyncPoppetShelfPacket(pos, invToSend)));
	}

	public static void register() {
		PayloadTypeRegistry.playS2C().register(ID, CODEC);
		ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
			BlockPos pos = payload.pos;
			DefaultedList<ItemStack> inventory = payload.inventory;
			context.client().execute(() -> {
				if (context.client().world.getBlockEntity(pos) instanceof PoppetShelfBlockEntity poppetShelfBlockEntity) {
					poppetShelfBlockEntity.clientInventory = inventory;
				}
			});
		});
	}

	private void write(RegistryByteBuf buf) {
		buf.writeLong(pos.asLong());
		for (ItemStack stack : inventory) {
			ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, stack);
		}
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
