package moriyashiine.bewitchment.client.render.entity;

import net.minecraft.client.render.entity.BoatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;

public class CustomBoatEntityRenderer extends BoatEntityRenderer {
	private final Identifier texture;

	public CustomBoatEntityRenderer(EntityRendererFactory.Context ctx, Identifier texture) {
		super(ctx, false);
		this.texture = texture;
	}

	@Override
	public Identifier getTexture(BoatEntity entity) {
		return texture;
	}
}
