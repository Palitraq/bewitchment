/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.packet;

import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.api.component.TransformationComponent;
import moriyashiine.bewitchment.client.packet.SpawnSmokeParticlesPacket;
import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.registry.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.world.World;
import virtuoel.pehkui.api.ScaleData;

public class TransformationAbilityPacket implements CustomPayload {
	public static final Id<TransformationAbilityPacket> ID = new Id<>(Bewitchment.id("transformation_ability"));
	public static final PacketCodec<PacketByteBuf, TransformationAbilityPacket> CODEC = CustomPayload.codecOf(TransformationAbilityPacket::write, TransformationAbilityPacket::new);

	public static final AbilitySource VAMPIRE_FLIGHT_SOURCE = Pal.getAbilitySource(Bewitchment.id("vampire_flight"));

	private static final float VAMPIRE_WIDTH = EntityType.BAT.getWidth() / EntityType.PLAYER.getWidth();
	private static final float VAMPIRE_HEIGHT = EntityType.BAT.getHeight() / EntityType.PLAYER.getHeight();
	private static final float WEREWOLF_WIDTH = BWEntityTypes.WEREWOLF.getWidth() / EntityType.PLAYER.getWidth();
	private static final float WEREWOLF_HEIGHT = BWEntityTypes.WEREWOLF.getHeight() / EntityType.PLAYER.getHeight();

	public TransformationAbilityPacket() {}

	public TransformationAbilityPacket(PacketByteBuf buf) {}

	public static void send() {
	}

	private static boolean canUseAbility(PlayerEntity player) {
		TransformationComponent transformationComponent = BWComponents.TRANSFORMATION_COMPONENT.get(player);
		if (transformationComponent.getTransformation() == BWTransformations.VAMPIRE) {
			return true;
		}
		if (transformationComponent.getTransformation() == BWTransformations.WEREWOLF) {
			return !BWComponents.ADDITIONAL_WEREWOLF_DATA_COMPONENT.get(player).isForcedTransformation();
		}
		return false;
	}

	public static void useAbility(PlayerEntity player, boolean forced) {
		BWComponents.TRANSFORMATION_COMPONENT.maybeGet(player).ifPresent(transformationComponent -> {
			World world = player.getWorld();
			boolean isAlternateForm = transformationComponent.isAlternateForm();
			ScaleData width = BWScaleTypes.MODIFY_WIDTH_TYPE.getScaleData(player);
			ScaleData height = BWScaleTypes.MODIFY_HEIGHT_TYPE.getScaleData(player);
			if (transformationComponent.getTransformation() == BWTransformations.VAMPIRE && (forced || (BewitchmentAPI.isPledged(player, BWPledges.LILITH) && BWComponents.BLOOD_COMPONENT.get(player).getBlood() > 0))) {
				PlayerLookup.tracking(player).forEach(trackingPlayer -> SpawnSmokeParticlesPacket.send(trackingPlayer, player));
				SpawnSmokeParticlesPacket.send((net.minecraft.server.network.ServerPlayerEntity) player, player);
				world.playSound(null, player.getBlockPos(), BWSoundEvents.ENTITY_GENERIC_TRANSFORM, player.getSoundCategory(), 1, 1);
				transformationComponent.setAlternateForm(!isAlternateForm);
				if (isAlternateForm) {
					width.setScale(width.getBaseScale() / VAMPIRE_WIDTH);
					height.setScale(height.getBaseScale() / VAMPIRE_HEIGHT);
					VAMPIRE_FLIGHT_SOURCE.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
					VAMPIRE_FLIGHT_SOURCE.revokeFrom(player, VanillaAbilities.FLYING);
				} else {
					width.setScale(width.getBaseScale() * VAMPIRE_WIDTH);
					height.setScale(height.getBaseScale() * VAMPIRE_HEIGHT);
					VAMPIRE_FLIGHT_SOURCE.grantTo(player, VanillaAbilities.ALLOW_FLYING);
					VAMPIRE_FLIGHT_SOURCE.grantTo(player, VanillaAbilities.FLYING);
				}
			} else if (transformationComponent.getTransformation() == BWTransformations.WEREWOLF && (forced || BewitchmentAPI.isPledged(player, BWPledges.HERNE))) {
				PlayerLookup.tracking(player).forEach(trackingPlayer -> SpawnSmokeParticlesPacket.send(trackingPlayer, player));
				SpawnSmokeParticlesPacket.send((net.minecraft.server.network.ServerPlayerEntity) player, player);
				world.playSound(null, player.getBlockPos(), BWSoundEvents.ENTITY_GENERIC_TRANSFORM, player.getSoundCategory(), 1, 1);
				transformationComponent.setAlternateForm(!isAlternateForm);
				if (isAlternateForm) {
					width.setScale(width.getBaseScale() / WEREWOLF_WIDTH);
					height.setScale(height.getBaseScale() / WEREWOLF_HEIGHT);
					if (player.hasStatusEffect(StatusEffects.NIGHT_VISION) && player.getStatusEffect(StatusEffects.NIGHT_VISION).isAmbient()) {
						player.removeStatusEffect(StatusEffects.NIGHT_VISION);
					}
				} else {
					width.setScale(width.getBaseScale() * WEREWOLF_WIDTH);
					height.setScale(height.getBaseScale() * WEREWOLF_HEIGHT);
				}
			}
		});
	}

	public static void register() {
		PayloadTypeRegistry.playC2S().register(ID, CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
			context.server().execute(() -> {
				var player = context.player();
				if (canUseAbility(player)) {
					useAbility(player, false);
				}
			});
		});
	}

	private void write(PacketByteBuf buf) {}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
