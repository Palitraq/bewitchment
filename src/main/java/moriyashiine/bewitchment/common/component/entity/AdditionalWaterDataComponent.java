/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.component.entity;

import moriyashiine.bewitchment.common.registry.BWComponents;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

public class AdditionalWaterDataComponent implements Component {
	private final Entity obj;
	private boolean submerged = false;
	private int wetTimer = 0;

	public AdditionalWaterDataComponent(Entity obj) {
		this.obj = obj;
	}

	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		setSubmerged(tag.getBoolean("Submerged"));
		setWetTimer(tag.getInt("WetTimer"));
	}

	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		tag.putBoolean("Submerged", isSubmerged());
		tag.putInt("WetTimer", getWetTimer());
	}

	public void serverTick() {
		if (isSubmerged()) {
			setSubmerged(false);
		}
		if (getWetTimer() > 0) {
			setWetTimer(getWetTimer() - 1);
		}
	}

	public boolean isSubmerged() {
		return submerged;
	}

	public void setSubmerged(boolean submerged) {
		this.submerged = submerged;
		BWComponents.ADDITIONAL_WATER_DATA_COMPONENT.sync(obj);
	}

	public int getWetTimer() {
		return wetTimer;
	}

	public void setWetTimer(int wetTimer) {
		this.wetTimer = wetTimer;
		BWComponents.ADDITIONAL_WATER_DATA_COMPONENT.sync(obj);
	}
}
