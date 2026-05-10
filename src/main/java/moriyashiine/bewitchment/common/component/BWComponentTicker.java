package moriyashiine.bewitchment.common.component;

import moriyashiine.bewitchment.api.component.*;
import moriyashiine.bewitchment.common.component.entity.*;
import moriyashiine.bewitchment.common.registry.BWComponents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireballEntity;

public class BWComponentTicker {

	public static void init() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (var world : server.getWorlds()) {
				for (PlayerEntity player : world.getPlayers()) {
					BWComponents.CONTRACTS_COMPONENT.maybeGet(player).ifPresent(ContractsComponent::serverTick);
					BWComponents.FORTUNE_COMPONENT.maybeGet(player).ifPresent(FortuneComponent::serverTick);
					BWComponents.MAGIC_COMPONENT.maybeGet(player).ifPresent(MagicComponent::serverTick);
					BWComponents.PLEDGE_COMPONENT.maybeGet(player).ifPresent(PledgeComponent::serverTick);
					BWComponents.TRANSFORMATION_COMPONENT.maybeGet(player).ifPresent(TransformationComponent::serverTick);
					BWComponents.ADDITIONAL_WEREWOLF_DATA_COMPONENT.maybeGet(player).ifPresent(component -> {});
					BWComponents.BROOM_USER_COMPONENT.maybeGet(player).ifPresent(component -> {});
					BWComponents.FULL_INVISIBILITY_COMPONENT.maybeGet(player).ifPresent(FullInvisibilityComponent::serverTick);
					BWComponents.POLYMORPH_COMPONENT.maybeGet(player).ifPresent(PolymorphComponent::serverTick);
					BWComponents.RESPAWN_TIMER_COMPONENT.maybeGet(player).ifPresent(RespawnTimerComponent::serverTick);
					BWComponents.TELEPORT_TIMER_COMPONENT.maybeGet(player).ifPresent(TeleportTimerComponent::serverTick);
				}
				for (Entity entity : world.iterateEntities()) {
					if (entity instanceof LivingEntity living) {
						BWComponents.BLOOD_COMPONENT.maybeGet(living).ifPresent(BloodComponent::serverTick);
						BWComponents.CURSES_COMPONENT.maybeGet(living).ifPresent(CursesComponent::serverTick);
						BWComponents.FAMILIAR_COMPONENT.maybeGet(living).ifPresent(FamiliarComponent::tick);
						if (living instanceof MobEntity mob) {
							BWComponents.MINION_COMPONENT.maybeGet(mob).ifPresent(MinionComponent::serverTick);
							BWComponents.FAKE_MOB_COMPONENT.maybeGet(mob).ifPresent(FakeMobComponent::serverTick);
						}
						if (living instanceof VillagerEntity villager) {
							BWComponents.WEREWOLF_VILLAGER_COMPONENT.maybeGet(villager).ifPresent(WerewolfVillagerComponent::serverTick);
						}
					}
					BWComponents.ADDITIONAL_WATER_DATA_COMPONENT.maybeGet(entity).ifPresent(AdditionalWaterDataComponent::serverTick);
					if (entity instanceof FireballEntity fireball) {
						BWComponents.CADUCEUS_FIREBALL_COMPONENT.maybeGet(fireball).ifPresent(component -> {});
					}
				}
			}
		});
	}
}
