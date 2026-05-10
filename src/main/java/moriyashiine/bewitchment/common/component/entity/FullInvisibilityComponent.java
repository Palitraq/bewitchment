/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.component.entity;

import moriyashiine.bewitchment.common.registry.BWComponents;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

public class FullInvisibilityComponent implements Component {
	private final PlayerEntity obj;
	private boolean fullInvisible = false;

	public FullInvisibilityComponent(PlayerEntity obj) {
		this.obj = obj;
	}

	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		setFullInvisible(tag.getBoolean("FullInvisible"));
	}

	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		tag.putBoolean("FullInvisible", isFullInvisible());
	}

	public void serverTick() {
		if (isFullInvisible() && !obj.isSneaking()) {
			setFullInvisible(false);
			obj.setInvisible(false);
			obj.removeStatusEffect(StatusEffects.INVISIBILITY);
		}
	}

	public boolean isFullInvisible() {
		return fullInvisible;
	}

	public void setFullInvisible(boolean fullInvisible) {
		this.fullInvisible = fullInvisible;
		BWComponents.FULL_INVISIBILITY_COMPONENT.sync(obj);
	}
}
