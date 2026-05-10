/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.mixin.client;

import moriyashiine.bewitchment.api.entity.BroomEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions")
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow
	@Final
	MinecraftClient client;

	@Shadow
	@Final
	private Camera camera;

	@Inject(method = "bobView", at = @At("TAIL"))
	private void bobView(MatrixStack matrices, float tickDelta, CallbackInfo info) {
		if (client.player.getVehicle() instanceof BroomEntity && !camera.isThirdPerson()) {
			matrices.translate(0, -(MathHelper.sin((client.player.getVehicle().age + client.player.getVehicle().getId()) / 4f) / 16f), 0);
		}
	}
}
