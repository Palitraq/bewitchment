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

public class SpawnExplosionParticlesPacket implements CustomPayload {
	public static final Id<SpawnExplosionParticlesPacket> ID = new Id<>(Bewitchment.id("spawn_explosion_particles"));
	public static final PacketCodec<PacketByteBuf, SpawnExplosionParticlesPacket> CODEC = CustomPayload.codecOf(SpawnExplosionParticlesPacket::write, SpawnExplosionParticlesPacket::new);

	private final int entityId;

	public SpawnExplosionParticlesPacket(Entity entity) {
		this.entityId = entity.getId();
	}

	public SpawnExplosionParticlesPacket(PacketByteBuf buf) {
		this.entityId = buf.readInt();
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
						for (int i = 0; i < 8; i++) {
							world.addParticle(ParticleTypes.EXPLOSION, entity.getParticleX(1), entity.getRandomBodyY(), entity.getParticleZ(1), 0, 0, 0);
						}
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
