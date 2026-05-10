/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.component.entity;

import moriyashiine.bewitchment.common.registry.BWComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

public class AdditionalWerewolfDataComponent implements Component {
	private final PlayerEntity obj;
	private int variant = 0;
	private boolean forcedTransformation = false;

	public AdditionalWerewolfDataComponent(PlayerEntity obj) {
		this.obj = obj;
	}

	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		setForcedTransformation(tag.getBoolean("ForcedTransformation"));
		setVariant(tag.getInt("WerewolfVariant"));
	}

	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		tag.putBoolean("ForcedTransformation", isForcedTransformation());
		tag.putInt("WerewolfVariant", getVariant());
	}

	public int getVariant() {
		return variant;
	}

	public void setVariant(int variant) {
		this.variant = variant;
		BWComponents.ADDITIONAL_WEREWOLF_DATA_COMPONENT.sync(obj);
	}

	public boolean isForcedTransformation() {
		return forcedTransformation;
	}

	public void setForcedTransformation(boolean forcedTransformation) {
		this.forcedTransformation = forcedTransformation;
		BWComponents.ADDITIONAL_WEREWOLF_DATA_COMPONENT.sync(obj);
	}
}
