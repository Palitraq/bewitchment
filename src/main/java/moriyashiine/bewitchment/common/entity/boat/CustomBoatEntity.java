package moriyashiine.bewitchment.common.entity.boat;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.world.World;

public class CustomBoatEntity extends BoatEntity {
	public CustomBoatEntity(EntityType<? extends BoatEntity> entityType, World world) {
		super(entityType, world);
	}
}
