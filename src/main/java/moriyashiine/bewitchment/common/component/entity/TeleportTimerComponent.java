/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.component.entity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

public class TeleportTimerComponent implements Component {
	private int teleportTimer = 0;

	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		setTeleportTimer(tag.getInt("TeleportTimer"));
	}

	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		tag.putInt("TeleportTimer", getTeleportTimer());
	}

	public void serverTick() {
		if (getTeleportTimer() > 0) {
			setTeleportTimer(getTeleportTimer() - 1);
		}
	}

	public int getTeleportTimer() {
		return teleportTimer;
	}

	public void setTeleportTimer(int teleportTimer) {
		this.teleportTimer = teleportTimer;
	}
}
