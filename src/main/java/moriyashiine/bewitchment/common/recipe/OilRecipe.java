/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import moriyashiine.bewitchment.common.registry.BWRecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class OilRecipe implements Recipe<RecipeInput> {
	private final Identifier identifier;
	public final DefaultedList<Ingredient> input;
	private final ItemStack output;
	public final int color;

	public OilRecipe(Identifier identifier, DefaultedList<Ingredient> input, ItemStack output, int color) {
		this.identifier = identifier;
		this.input = input;
		this.output = output;
		this.color = color;
	}

	@Override
	public boolean matches(RecipeInput inv, World world) {
		return RitualRecipe.matches(inv, input);
	}

	@Override
	public ItemStack craft(RecipeInput inventory, RegistryWrapper.WrapperLookup lookup) {
		return output;
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getResult(RegistryWrapper.WrapperLookup lookup) {
		return output;
	}

	public Identifier getId() {
		return identifier;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return BWRecipeTypes.OIL_RECIPE_SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return BWRecipeTypes.OIL_RECIPE_TYPE;
	}

	public static class Serializer implements RecipeSerializer<OilRecipe> {
		public OilRecipe read(Identifier id, JsonObject json) {
			DefaultedList<Ingredient> ingredients = RitualRecipe.Serializer.getIngredients(JsonHelper.getArray(json, "ingredients"));
			if (ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients for oil recipe");
			} else if (ingredients.size() > 4) {
				throw new JsonParseException("Too many ingredients for oil recipe");
			}
			return new OilRecipe(id, ingredients, ItemStack.CODEC.parse(JsonOps.INSTANCE, JsonHelper.getObject(json, "result")).getOrThrow(), JsonHelper.getInt(json, "color"));
		}

		public OilRecipe read(Identifier id, PacketByteBuf buf) {
			RegistryByteBuf regBuf = (RegistryByteBuf) buf;
			DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(regBuf.readVarInt(), Ingredient.EMPTY);
			defaultedList.replaceAll(ignored -> Ingredient.PACKET_CODEC.decode(regBuf));
			return new OilRecipe(id, defaultedList, ItemStack.OPTIONAL_PACKET_CODEC.decode(regBuf), regBuf.readInt());
		}

		public void write(PacketByteBuf buf, OilRecipe recipe) {
			RegistryByteBuf regBuf = (RegistryByteBuf) buf;
			regBuf.writeVarInt(recipe.input.size());
			for (Ingredient ingredient : recipe.input) {
				Ingredient.PACKET_CODEC.encode(regBuf, ingredient);
			}
			ItemStack.OPTIONAL_PACKET_CODEC.encode(regBuf, recipe.output);
			regBuf.writeInt(recipe.color);
		}

		@Override
		public MapCodec<OilRecipe> codec() {
			return new MapCodec<>() {
				@Override
				public <T> Stream<T> keys(DynamicOps<T> ops) {
					return Stream.of();
				}

				@Override
				public <T> DataResult<OilRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
					try {
						JsonObject json = new JsonObject();
						input.entries().forEach(pair -> {
							String key = ops.getStringValue(pair.getFirst()).getOrThrow(IllegalStateException::new);
							JsonElement value = ops.convertTo(JsonOps.INSTANCE, pair.getSecond());
							json.add(key, value);
						});
						return DataResult.success(read(null, json));
					} catch (Exception e) {
						return DataResult.error(() -> "OilRecipe decode failed: " + e.getMessage());
					}
				}

				@Override
				public <T> RecordBuilder<T> encode(OilRecipe recipe, DynamicOps<T> ops, RecordBuilder<T> prefix) {
					return prefix;
				}
			};
		}

		@Override
		public PacketCodec<RegistryByteBuf, OilRecipe> packetCodec() {
			return PacketCodec.ofStatic(
				(RegistryByteBuf buf, OilRecipe recipe) -> write(buf, recipe),
				(RegistryByteBuf buf) -> read(null, buf)
			);
		}
	}
}
