/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.api.component;

import moriyashiine.bewitchment.common.registry.BWComponents;
import moriyashiine.bewitchment.common.registry.BWPledges;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

public class PledgeComponent implements Component {
	private final PlayerEntity obj;
	private String pledge = BWPledges.NONE, pledgeNextTick = "";

	public PledgeComponent(PlayerEntity obj) {
		this.obj = obj;
	}

	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		String pledge = tag.getString("Pledge");
		if (pledge.isEmpty()) {
			pledge = BWPledges.NONE;
		}
		setPledge(pledge);
		setPledgeNextTick(tag.getString("PledgeNextTick"));
	}

	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		tag.putString("Pledge", getPledge());
		tag.putString("PledgeNextTick", getPledgeNextTick());
	}

	public void serverTick() {
		if (!getPledgeNextTick().isEmpty()) {
			setPledge(getPledgeNextTick());
			setPledgeNextTick("");
		}
	}

	public String getPledge() {
		return pledge;
	}

	public void setPledge(String pledge) {
		this.pledge = pledge;
		BWComponents.PLEDGE_COMPONENT.sync(obj);
		BWComponents.TRANSFORMATION_COMPONENT.get(obj).updateAttributes();
	}

	public String getPledgeNextTick() {
		return pledgeNextTick;
	}

	public void setPledgeNextTick(String pledgeNextTick) {
		this.pledgeNextTick = pledgeNextTick;
	}
}
