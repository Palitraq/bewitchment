/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.client.packet;

import moriyashiine.bewitchment.common.Bewitchment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleTypes;

public class SpawnSpecterBangleParticlesPacket implements CustomPayload {
	public static final Id<SpawnSpecterBangleParticlesPacket> ID = new Id<>(Bewitchment.id("spawn_specter_bangle_particles"));
	public static final PacketCodec<PacketByteBuf, SpawnSpecterBangleParticlesPacket> CODEC = CustomPayload.codecOf(SpawnSpecterBangleParticlesPacket::write, SpawnSpecterBangleParticlesPacket::new);

	private final int entityId;

	public SpawnSpecterBangleParticlesPacket(Entity entity) {
		this.entityId = entity.getId();
	}

	public SpawnSpecterBangleParticlesPacket(PacketByteBuf buf) {
		this.entityId = buf.readInt();
	}

	public static void send(net.minecraft.server.network.ServerPlayerEntity player, Entity entity) {
		player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket(new SpawnSpecterBangleParticlesPacket(entity)));
	}

	public static void register() {
		PayloadTypeRegistry.playS2C().register(ID, CODEC);
		ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
			int id = payload.entityId;
			context.client().execute(() -> {
				var world = context.client().world;
				if (world != null) {
					Entity entity = world.getEntityById(id);
					if (entity != null) {
						world.addParticle(ParticleTypes.SMOKE, entity.getParticleX(1), entity.getY(), entity.getParticleZ(1), 0, 0, 0);
					}
				}
			});
		});
	}

	private void write(PacketByteBuf buf) {
		buf.writeInt(entityId);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
