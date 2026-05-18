package moriyashiine.bewitchment.client.packet;

import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.registry.BWComponents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class MagicSyncPacket implements CustomPayload {
	public static final Id<MagicSyncPacket> ID = new Id<>(Bewitchment.id("magic_sync"));
	public static final PacketCodec<PacketByteBuf, MagicSyncPacket> CODEC = CustomPayload.codecOf(MagicSyncPacket::write, MagicSyncPacket::new);

	private final int magic;
	private final int magicTimer;

	public MagicSyncPacket(int magic, int magicTimer) {
		this.magic = magic;
		this.magicTimer = magicTimer;
	}

	public MagicSyncPacket(PacketByteBuf buf) {
		this.magic = buf.readInt();
		this.magicTimer = buf.readInt();
	}

	public static void send(ServerPlayerEntity player, int magic, int magicTimer) {
		player.networkHandler.sendPacket(new CustomPayloadS2CPacket(new MagicSyncPacket(magic, magicTimer)));
	}

	public static void register() {
		PayloadTypeRegistry.playS2C().register(ID, CODEC);
		ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
			context.client().execute(() -> {
				if (context.client().player != null) {
					BWComponents.MAGIC_COMPONENT.maybeGet(context.client().player).ifPresent(component -> {
						component.onClientSync(payload.magic, payload.magicTimer);
					});
				}
			});
		});
	}

	private void write(PacketByteBuf buf) {
		buf.writeInt(magic);
		buf.writeInt(magicTimer);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
