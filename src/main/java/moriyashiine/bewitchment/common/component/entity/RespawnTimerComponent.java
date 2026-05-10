/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.component.entity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

public class RespawnTimerComponent implements Component {
	private int respawnTimer = 400;

	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		setRespawnTimer(tag.getInt("RespawnTimer"));
	}

	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		tag.putInt("RespawnTimer", getRespawnTimer());
	}

	public void serverTick() {
		if (getRespawnTimer() > 0) {
			setRespawnTimer(getRespawnTimer() - 1);
		}
	}

	public int getRespawnTimer() {
		return respawnTimer;
	}

	public void setRespawnTimer(int respawnTimer) {
		this.respawnTimer = respawnTimer;
	}
}
