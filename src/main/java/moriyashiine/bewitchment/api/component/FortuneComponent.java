/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.api.component;

import moriyashiine.bewitchment.api.registry.Fortune;
import moriyashiine.bewitchment.common.registry.BWRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.Component;

public class FortuneComponent implements Component {
	private final PlayerEntity obj;
	private Fortune.Instance fortune = null;

	public FortuneComponent(PlayerEntity obj) {
		this.obj = obj;
	}

	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		if (tag.contains("Fortune")) {
			setFortune(new Fortune.Instance(BWRegistries.FORTUNE.get(Identifier.tryParse(tag.getString("Fortune"))), tag.getInt("FortuneDuration")));
		}
	}

	@SuppressWarnings({"ConstantConditions", "NullableProblems"})
	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		if (getFortune() != null) {
			tag.putString("Fortune", BWRegistries.FORTUNE.getId(getFortune().fortune).toString());
			tag.putInt("FortuneDuration", getFortune().duration);
		}
	}

	public void serverTick() {
		if (getFortune() != null) {
			if (getFortune().fortune.tick((ServerWorld) obj.getWorld(), obj)) {
				getFortune().duration = 0;
			} else {
				getFortune().duration--;
			}
			if (getFortune().duration <= 0) {
				if (getFortune().fortune.finish((ServerWorld) obj.getWorld(), obj)) {
					setFortune(null);
				} else {
					getFortune().duration = obj.getRandom().nextInt(120000);
				}
			}
		}
	}

	public Fortune.Instance getFortune() {
		return fortune;
	}

	public void setFortune(Fortune.Instance fortune) {
		this.fortune = fortune;
	}
}
