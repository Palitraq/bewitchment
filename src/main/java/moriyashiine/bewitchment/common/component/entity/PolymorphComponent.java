/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.component.entity;

import org.ladysnake.impersonate.Impersonator;
import org.ladysnake.cca.api.v3.component.Component;
import moriyashiine.bewitchment.common.registry.BWStatusEffects;
import moriyashiine.bewitchment.common.statuseffect.PolymorphStatusEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PolymorphComponent implements Component {
	private final Entity obj;
	private UUID uuid;
	private String name;

	public PolymorphComponent(Entity obj) {
		this.obj = obj;
	}

	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		if (tag.contains("UUID")) {
			uuid = tag.getUuid("UUID");
			name = tag.getString("Name");
		}
	}

	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
		if (getUuid() != null) {
			tag.putUuid("UUID", uuid);
			tag.putString("Name", name);
		}
	}

	public void serverTick() {
		if (obj instanceof PlayerEntity player && getUuid() != null && !player.hasStatusEffect(RegistryEntry.of(BWStatusEffects.POLYMORPH))) {
			setUuid(null);
			setName(null);
			Impersonator.get(player).stopImpersonation(PolymorphStatusEffect.IMPERSONATE_IDENTIFIER);
		}
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
