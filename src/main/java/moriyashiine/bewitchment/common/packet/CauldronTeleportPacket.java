/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.packet;

import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.api.block.entity.UsesAltarPower;
import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.block.entity.WitchAltarBlockEntity;
import moriyashiine.bewitchment.common.misc.BWUtil;
import moriyashiine.bewitchment.common.registry.BWPledges;
import moriyashiine.bewitchment.common.world.BWWorldState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

public class CauldronTeleportPacket implements CustomPayload {
	public static final Id<CauldronTeleportPacket> ID = new Id<>(Bewitchment.id("cauldron_teleport"));
	public static final PacketCodec<PacketByteBuf, CauldronTeleportPacket> CODEC = CustomPayload.codecOf(CauldronTeleportPacket::write, CauldronTeleportPacket::new);

	private final BlockPos cauldronPos;
	private final String message;

	public CauldronTeleportPacket(BlockPos cauldronPos, String message) {
		this.cauldronPos = cauldronPos;
		this.message = message;
	}

	public CauldronTeleportPacket(PacketByteBuf buf) {
		this.cauldronPos = buf.readBlockPos();
		this.message = buf.readString(Short.MAX_VALUE);
	}

	public static void send(BlockPos cauldronPos, String message) {
		ClientPlayNetworking.send(new CauldronTeleportPacket(cauldronPos, message));
	}

	public static void register() {
		PayloadTypeRegistry.playC2S().register(ID, CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
			BlockPos cauldronPos = payload.cauldronPos;
			String message = payload.message;
			context.server().execute(() -> {
				var player = context.player();
				World world = player.getWorld();
				BlockPos closest = null;
				BWWorldState worldState = BWWorldState.get(world);
				for (Map.Entry<Long, String> entry : worldState.witchCauldrons.entrySet()) {
					if (message.equals(entry.getValue())) {
						BlockPos foundCauldronPos = BlockPos.fromLong(entry.getKey());
						if (closest == null || cauldronPos.getSquaredDistance(player.getPos()) < closest.getSquaredDistance(player.getPos())) {
							closest = foundCauldronPos;
						}
					}
				}
				if (closest != null) {
					boolean hasPower = BewitchmentAPI.isPledged(player, BWPledges.LEONARD);
					if (!hasPower) {
						BlockPos altarPos = ((UsesAltarPower) world.getBlockEntity(cauldronPos)).getAltarPos();
						if (altarPos != null) {
							BlockEntity altarBE = world.getBlockEntity(altarPos);
							if (altarBE instanceof WitchAltarBlockEntity altar && altar.drain((int) Math.sqrt(closest.getSquaredDistance(player.getPos())) / 2, false)) {
								hasPower = true;
							}
						}
					}
					if (hasPower) {
						BWUtil.teleport(player, closest.getX() + 0.5, closest.getY() - 0.4375, closest.getZ() + 0.5, true);
					} else {
						player.sendMessage(Text.translatable(Bewitchment.MOD_ID + ".message.insufficent_altar_power", message), true);
					}
				} else {
					player.sendMessage(Text.translatable(Bewitchment.MOD_ID + ".message.invalid_cauldron", message), true);
				}
			});
		});
	}

	private void write(PacketByteBuf buf) {
		buf.writeBlockPos(cauldronPos);
		buf.writeString(message);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
