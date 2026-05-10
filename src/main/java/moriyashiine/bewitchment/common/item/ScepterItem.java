/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.item;

import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.common.Bewitchment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class ScepterItem extends Item {
	public ScepterItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!user.getStackInHand(hand).getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).customEffects().isEmpty()) {
			return ItemUsage.consumeHeldItem(world, user, hand);
		}
		return TypedActionResult.fail(user.getStackInHand(hand));
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.getItem().equals(Items.NETHERITE_INGOT);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (user instanceof PlayerEntity player) {
			if (!world.isClient && BewitchmentAPI.drainMagic(player, 2, false)) {
				PotionEntity potion = new PotionEntity(world, user);
				List<StatusEffectInstance> effects = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).customEffects();
			ItemStack potionStack = new ItemStack(Items.SPLASH_POTION);
			potionStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(), Optional.empty(), effects));
			var potionNbt = potionStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
			potionNbt.putInt("CustomPotionColor", new PotionContentsComponent(Optional.empty(), Optional.empty(), effects).getColor());
			potionStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(potionNbt));
			var stackNbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
				if (stackNbt.contains("PolymorphUUID")) {
					var pnbt = potionStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
					pnbt.putUuid("PolymorphUUID", stackNbt.getUuid("PolymorphUUID"));
					pnbt.putString("PolymorphName", stackNbt.getString("PolymorphName"));
					potionStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(pnbt));
				}
				potion.setItem(potionStack);
				potion.setVelocity(user, user.getPitch(), user.getYaw(), -20, 0.5f, 1);
				world.spawnEntity(potion);
				world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 1, 1);
				if (!player.isCreative()) {
					stackNbt.putInt("PotionUses", stackNbt.getInt("PotionUses") - 1);
					stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(stackNbt));
					var updatedSNbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
					if (updatedSNbt.getInt("PotionUses") <= 0) {
						if (updatedSNbt.contains("PolymorphUUID")) {
							var pnbt = potionStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
							pnbt.remove("PolymorphUUID");
							pnbt.remove("PolymorphName");
							potionStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(pnbt));
						}
						var clearedNbt = new NbtCompound();
						clearedNbt.put("CustomPotionEffects", new NbtList());
						stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(clearedNbt));
					}

					if (player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
						EquipmentSlot slot = user.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
						stack.damage(1, serverPlayer.getServerWorld(), serverPlayer, item -> {});
					}
				}
			}
		}
		return stack;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return !stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).customEffects().isEmpty() ? UseAction.BOW : UseAction.NONE;
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		return 16;
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		int uses = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt().getInt("PotionUses");
		tooltip.add(Text.translatable(Bewitchment.MOD_ID + ".tooltip.uses_left", uses).formatted(Formatting.GRAY));

	}
}
