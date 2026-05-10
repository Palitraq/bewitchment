/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.client.packet;

import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.entity.projectile.HornedSpearEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class SyncHornedSpearPacket implements CustomPayload {
	public static final Id<SyncHornedSpearPacket> ID = new Id<>(Bewitchment.id("sync_horned_spear"));
	public static final PacketCodec<RegistryByteBuf, SyncHornedSpearPacket> CODEC = CustomPayload.codecOf(SyncHornedSpearPacket::write, SyncHornedSpearPacket::new);

	private final int entityId;
	private final ItemStack spear;

	public SyncHornedSpearPacket(int entityId, ItemStack spear) {
		this.entityId = entityId;
		this.spear = spear;
	}

	public SyncHornedSpearPacket(RegistryByteBuf buf) {
		this.entityId = buf.readInt();
		this.spear = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
	}

	public static void send(net.minecraft.server.network.ServerPlayerEntity player, HornedSpearEntity entity) {
		player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket(new SyncHornedSpearPacket(entity.getId(), entity.spear)));
	}

	public static void register() {
		PayloadTypeRegistry.playS2C().register(ID, CODEC);
		ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
			int entityId = payload.entityId;
			ItemStack spear = payload.spear;
			context.client().execute(() -> ((HornedSpearEntity) context.client().world.getEntityById(entityId)).spear = spear);
		});
	}

	private void write(RegistryByteBuf buf) {
		buf.writeInt(entityId);
		ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, spear);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
