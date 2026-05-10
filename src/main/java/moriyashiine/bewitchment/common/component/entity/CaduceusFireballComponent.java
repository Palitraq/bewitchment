/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.component.entity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

public class CaduceusFireballComponent implements Component {
	private boolean fromCaduceus = false;

	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		setFromCaduceus(tag.getBoolean("FromCaduceus"));
	}

	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		tag.putBoolean("FromCaduceus", isFromCaduceus());
	}

	public boolean isFromCaduceus() {
		return fromCaduceus;
	}

	public void setFromCaduceus(boolean fromCaduceus) {
		this.fromCaduceus = fromCaduceus;
	}
}
