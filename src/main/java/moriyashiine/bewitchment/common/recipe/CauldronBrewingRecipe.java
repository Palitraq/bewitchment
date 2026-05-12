/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import moriyashiine.bewitchment.common.registry.BWRecipeTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.stream.Stream;
import net.minecraft.world.World;

public class CauldronBrewingRecipe implements Recipe<RecipeInput> {
	private final Identifier identifier;
	public final Ingredient input;
	public final StatusEffect output;
	public final int time;

	public CauldronBrewingRecipe(Identifier identifier, Ingredient input, StatusEffect output, int time) {
		this.identifier = identifier;
		this.input = input;
		this.output = output;
		this.time = time;
	}

	@Override
	public boolean matches(RecipeInput inv, World world) {
		return false;
	}

	@Override
	public ItemStack craft(RecipeInput inventory, RegistryWrapper.WrapperLookup lookup) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean fits(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getResult(RegistryWrapper.WrapperLookup lookup) {
		return ItemStack.EMPTY;
	}

	public Identifier getId() {
		return identifier;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return BWRecipeTypes.CAULDRON_BREWING_RECIPE_SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return BWRecipeTypes.CAULDRON_BREWING_RECIPE_TYPE;
	}

	@SuppressWarnings("ConstantConditions")
	public static class Serializer implements RecipeSerializer<CauldronBrewingRecipe> {
		public CauldronBrewingRecipe read(Identifier id, JsonObject json) {
			return new CauldronBrewingRecipe(id, Ingredient.DISALLOW_EMPTY_CODEC.parse(JsonOps.INSTANCE, JsonHelper.getObject(json, "ingredient")).getOrThrow(), Registries.STATUS_EFFECT.get(Identifier.tryParse(JsonHelper.getString(json, "effect"))), JsonHelper.getInt(json, "time"));
		}

		public CauldronBrewingRecipe read(Identifier id, PacketByteBuf buf) {
			RegistryByteBuf regBuf = (RegistryByteBuf) buf;
			return new CauldronBrewingRecipe(id, Ingredient.PACKET_CODEC.decode(regBuf), Registries.STATUS_EFFECT.get(Identifier.tryParse(regBuf.readString())), regBuf.readInt());
		}

		public void write(PacketByteBuf buf, CauldronBrewingRecipe recipe) {
			RegistryByteBuf regBuf = (RegistryByteBuf) buf;
			Ingredient.PACKET_CODEC.encode(regBuf, recipe.input);
			regBuf.writeString(Registries.STATUS_EFFECT.getId(recipe.output).toString());
			regBuf.writeInt(recipe.time);
		}

		@Override
		public MapCodec<CauldronBrewingRecipe> codec() {
			return new MapCodec<>() {
				@Override
				public <T> Stream<T> keys(DynamicOps<T> ops) {
					return Stream.of(
							ops.createString("ingredient"),
							ops.createString("effect"),
							ops.createString("time")
					);
				}

				@Override
				public <T> DataResult<CauldronBrewingRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
					try {
						Ingredient ingredient = Ingredient.DISALLOW_EMPTY_CODEC.parse(ops, input.get("ingredient")).getOrThrow();
						String effectStr = ops.getStringValue(input.get("effect")).getOrThrow();
						StatusEffect effect = Registries.STATUS_EFFECT.get(Identifier.tryParse(effectStr));
						int time = ops.getNumberValue(input.get("time")).getOrThrow().intValue();
						return DataResult.success(new CauldronBrewingRecipe(null, ingredient, effect, time));
					} catch (Exception e) {
						return DataResult.error(() -> "Failed to decode CauldronBrewingRecipe: " + e.getMessage());
					}
				}

				@Override
				public <T> RecordBuilder<T> encode(CauldronBrewingRecipe recipe, DynamicOps<T> ops, RecordBuilder<T> prefix) {
					return prefix;
				}
			};
		}

		@Override
		public PacketCodec<RegistryByteBuf, CauldronBrewingRecipe> packetCodec() {
			return PacketCodec.ofStatic(
				(RegistryByteBuf buf, CauldronBrewingRecipe recipe) -> write(buf, recipe),
				(RegistryByteBuf buf) -> read(null, buf)
			);
		}
	}
}
