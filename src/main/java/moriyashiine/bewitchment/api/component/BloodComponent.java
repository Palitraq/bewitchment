/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.api.component;

import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.api.event.BloodSetEvents;
import moriyashiine.bewitchment.common.registry.BWComponents;
import moriyashiine.bewitchment.common.registry.BWTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

public class BloodComponent implements Component {
	public static final int MAX_BLOOD = 100;

	private final LivingEntity obj;
	private int blood = MAX_BLOOD;

	public BloodComponent(LivingEntity obj) {
		this.obj = obj;
	}

	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		if (tag.contains("Blood")) {
			setBlood(tag.getInt("Blood"));
		}
	}

	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		tag.putInt("Blood", getBlood());
	}

	public int getBlood() {
		return blood;
	}

	public void setBlood(int blood) {
		BloodSetEvents.ON_BLOOD_SET.invoker().onSetBlood(obj, blood);
		this.blood = blood;
		BWComponents.BLOOD_COMPONENT.sync(obj);
	}

	public boolean fillBlood(int amount, boolean simulate) {
		BloodSetEvents.ON_BLOOD_FILL.invoker().onFillBlood(obj, amount, simulate);
		if (getBlood() < MAX_BLOOD) {
			if (!simulate) {
				setBlood(Math.min(MAX_BLOOD, getBlood() + amount));
			}
			return true;
		}
		return false;
	}

	public void serverTick() {
	}

	public boolean drainBlood(int amount, boolean simulate) {
		BloodSetEvents.ON_BLOOD_DRAIN.invoker().onDrainBlood(obj, amount, simulate);
		if (getBlood() - amount >= 0) {
			if (!simulate) {
				setBlood(getBlood() - amount);
			}
			return true;
		}
		return false;
	}
}
