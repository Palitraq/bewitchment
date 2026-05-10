/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.component.entity;

import moriyashiine.bewitchment.common.registry.BWComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

public class BroomUserComponent implements Component {
	private final PlayerEntity obj;
	private boolean pressingForward = false;

	public BroomUserComponent(PlayerEntity obj) {
		this.obj = obj;
	}

	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		setPressingForward(tag.getBoolean("PressingForward"));
	}

	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		tag.putBoolean("PressingForward", isPressingForward());
	}

	public boolean isPressingForward() {
		return pressingForward;
	}

	public void setPressingForward(boolean pressingForward) {
		this.pressingForward = pressingForward;
		BWComponents.BROOM_USER_COMPONENT.sync(obj);
	}
}
