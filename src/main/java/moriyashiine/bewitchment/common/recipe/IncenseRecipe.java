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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class IncenseRecipe implements Recipe<RecipeInput> {
	private final Identifier identifier;
	public final DefaultedList<Ingredient> input;
	public final StatusEffect effect;
	public final int amplifier;

	public IncenseRecipe(Identifier identifier, DefaultedList<Ingredient> input, StatusEffect effect, int amplifier) {
		this.identifier = identifier;
		this.input = input;
		this.effect = effect;
		this.amplifier = amplifier;
	}

	@Override
	public boolean matches(RecipeInput inv, World world) {
		return RitualRecipe.matches(inv, input);
	}

	@Override
	public ItemStack craft(RecipeInput inventory, RegistryWrapper.WrapperLookup lookup) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
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
		return BWRecipeTypes.INCENSE_RECIPE_SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return BWRecipeTypes.INCENSE_RECIPE_TYPE;
	}

	@SuppressWarnings("ConstantConditions")
	public static class Serializer implements RecipeSerializer<IncenseRecipe> {
		public IncenseRecipe read(Identifier id, JsonObject json) {
			DefaultedList<Ingredient> ingredients = RitualRecipe.Serializer.getIngredients(JsonHelper.getArray(json, "ingredients"));
			if (ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients for incense recipe");
			} else if (ingredients.size() > 4) {
				throw new JsonParseException("Too many ingredients for incense recipe");
			}
			return new IncenseRecipe(id, ingredients, Registries.STATUS_EFFECT.get(Identifier.tryParse(JsonHelper.getString(json, "effect"))), JsonHelper.getInt(json, "amplifier", 0));
		}

		public IncenseRecipe read(Identifier id, PacketByteBuf buf) {
			RegistryByteBuf regBuf = (RegistryByteBuf) buf;
			DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(regBuf.readVarInt(), Ingredient.EMPTY);
			defaultedList.replaceAll(ignored -> Ingredient.PACKET_CODEC.decode(regBuf));
			return new IncenseRecipe(id, defaultedList, Registries.STATUS_EFFECT.get(Identifier.tryParse(regBuf.readString())), regBuf.readInt());
		}

		public void write(PacketByteBuf buf, IncenseRecipe recipe) {
			RegistryByteBuf regBuf = (RegistryByteBuf) buf;
			regBuf.writeVarInt(recipe.input.size());
			for (Ingredient ingredient : recipe.input) {
				Ingredient.PACKET_CODEC.encode(regBuf, ingredient);
			}
			regBuf.writeString(Registries.STATUS_EFFECT.getId(recipe.effect).toString());
			regBuf.writeInt(recipe.amplifier);
		}

		@Override
		public MapCodec<IncenseRecipe> codec() {
			return new MapCodec<>() {
				@Override
				public <T> Stream<T> keys(DynamicOps<T> ops) {
					return Stream.of();
				}

				@Override
				public <T> DataResult<IncenseRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
					try {
						JsonObject json = new JsonObject();
						input.entries().forEach(pair -> {
							String key = ops.getStringValue(pair.getFirst()).getOrThrow(IllegalStateException::new);
							JsonElement value = ops.convertTo(JsonOps.INSTANCE, pair.getSecond());
							json.add(key, value);
						});
						return DataResult.success(read(null, json));
					} catch (Exception e) {
						return DataResult.error(() -> "IncenseRecipe decode failed: " + e.getMessage());
					}
				}

				@Override
				public <T> RecordBuilder<T> encode(IncenseRecipe recipe, DynamicOps<T> ops, RecordBuilder<T> prefix) {
					return prefix;
				}
			};
		}

		@Override
		public PacketCodec<RegistryByteBuf, IncenseRecipe> packetCodec() {
			return PacketCodec.ofStatic(
				(RegistryByteBuf buf, IncenseRecipe recipe) -> write(buf, recipe),
				(RegistryByteBuf buf) -> read(null, buf)
			);
		}
	}
}
