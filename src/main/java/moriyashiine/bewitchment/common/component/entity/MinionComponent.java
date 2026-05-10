/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.component.entity;

import moriyashiine.bewitchment.client.packet.SpawnSmokeParticlesPacket;
import moriyashiine.bewitchment.common.registry.BWComponents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;
import org.ladysnake.cca.api.v3.component.Component;

public class MinionComponent implements Component {
	private final MobEntity obj;
	private UUID master = null;

	public MinionComponent(MobEntity obj) {
		this.obj = obj;
	}

	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		setMaster(tag.getString("MasterUUID").isEmpty() ? null : UUID.fromString(tag.getString("MasterUUID")));
	}

	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		tag.putString("MasterUUID", getMaster() == null ? "" : getMaster().toString());
	}

	public void serverTick() {
		if (getMaster() != null) {
			Entity master = ((ServerWorld) obj.getWorld()).getEntity(getMaster());
			if (master instanceof MobEntity mob && !mob.isDead() && mob.getTarget() != null) {
				obj.setTarget(mob.getTarget());
			} else {
				PlayerLookup.tracking(obj).forEach(trackingPlayer -> SpawnSmokeParticlesPacket.send(trackingPlayer, obj));
				obj.remove(Entity.RemovalReason.DISCARDED);
			}
		}
	}

	public UUID getMaster() {
		return master;
	}

	public void setMaster(UUID master) {
		this.master = master;
		BWComponents.MINION_COMPONENT.sync(obj);
	}
}
