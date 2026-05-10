/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.packet;

import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.registry.BWComponents;
import moriyashiine.bewitchment.common.registry.BWEntityTypes;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class TogglePressingForwardPacket implements CustomPayload {
	public static final Id<TogglePressingForwardPacket> ID = new Id<>(Bewitchment.id("toggle_pressing_forward"));
	public static final PacketCodec<PacketByteBuf, TogglePressingForwardPacket> CODEC = CustomPayload.codecOf(TogglePressingForwardPacket::write, TogglePressingForwardPacket::new);

	private final boolean pressingForward;

	public TogglePressingForwardPacket(boolean pressingForward) {
		this.pressingForward = pressingForward;
	}

	public TogglePressingForwardPacket(PacketByteBuf buf) {
		boolean value;
		try {
			value = buf.readBoolean();
		} catch (IndexOutOfBoundsException e) {
			value = false;
		}
		this.pressingForward = value;
	}

	public static void send(boolean pressingForward) {
		ClientPlayNetworking.send(new TogglePressingForwardPacket(pressingForward));
	}

	public static void register() {
		PayloadTypeRegistry.playC2S().register(ID, CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
			boolean pressingForward = payload.pressingForward;
			context.server().execute(() -> {
				var player = context.player();
				try {
					if (pressingForward && BewitchmentAPI.getFamiliar(player) != BWEntityTypes.OWL) {
						if (!BewitchmentAPI.drainMagic(player, 1, true)) {
							return;
						}
						if (player.age % 60 == 0) {
							BewitchmentAPI.drainMagic(player, 1, false);
						}
					}
					BWComponents.BROOM_USER_COMPONENT.get(player).setPressingForward(pressingForward);
				} catch (IndexOutOfBoundsException ignored) {
				}
			});
		});
	}

	private void write(PacketByteBuf buf) {
		buf.writeBoolean(pressingForward);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
