/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.component.entity;

import moriyashiine.bewitchment.common.registry.BWComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import org.ladysnake.cca.api.v3.component.Component;

public class FamiliarComponent implements Component {
	private final LivingEntity obj;
	private boolean familiar = false;

	public FamiliarComponent(LivingEntity obj) {
		this.obj = obj;
	}

	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		setFamiliar(tag.getBoolean("Familiar"));
	}

	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		tag.putBoolean("Familiar", isFamiliar());
	}

	public void tick() {
		if (isFamiliar()) {
			if (!obj.getWorld().isClient) {
				if (obj.age % 100 == 0) {
					obj.heal(1);
				}
			} else if (obj.getRandom().nextFloat() < 0.25f) {
				obj.getWorld().addParticle(ParticleTypes.ENCHANT, obj.getParticleX(obj.getWidth()), obj.getY() + MathHelper.nextFloat(obj.getRandom(), 0, obj.getHeight()), obj.getParticleZ(obj.getWidth()), 0, 0, 0);
			}
		}
	}

	public boolean isFamiliar() {
		return familiar;
	}

	public void setFamiliar(boolean familiar) {
		this.familiar = familiar;
		BWComponents.FAMILIAR_COMPONENT.sync(obj);
	}
}
