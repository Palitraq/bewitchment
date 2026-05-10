package moriyashiine.bewitchment.common.entity.boat;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.world.World;

public class CustomChestBoatEntity extends ChestBoatEntity {
	public CustomChestBoatEntity(EntityType<? extends ChestBoatEntity> entityType, World world) {
		super(entityType, world);
	}
}
