/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.mixin.client;

import moriyashiine.bewitchment.common.registry.BWComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class MobEntityRendererMixin<T extends Entity> {
	@Inject(method = "shouldRender", at = @At("RETURN"), cancellable = true)
	private void shouldRender(T entity, Frustum frustum, double d, double e, double f, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (callbackInfo.getReturnValueZ()) {
			BWComponents.FAKE_MOB_COMPONENT.maybeGet(entity).ifPresent(fakeMobComponent -> {
				if (fakeMobComponent.getTarget() != null && !MinecraftClient.getInstance().player.getUuid().equals(fakeMobComponent.getTarget())) {
					callbackInfo.setReturnValue(false);
				}
			});
		}
	}
}
