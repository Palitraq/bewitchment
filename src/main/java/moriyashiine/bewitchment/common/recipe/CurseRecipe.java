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
import moriyashiine.bewitchment.api.registry.Curse;
import moriyashiine.bewitchment.common.registry.BWObjects;
import moriyashiine.bewitchment.common.registry.BWRecipeTypes;
import moriyashiine.bewitchment.common.registry.BWRegistries;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CurseRecipe implements Recipe<RecipeInput> {
	private final Identifier identifier;
	public final DefaultedList<Ingredient> input;
	public final Curse curse;
	public final int cost;

	public CurseRecipe(Identifier identifier, DefaultedList<Ingredient> input, Curse curse, int cost) {
		this.identifier = identifier;
		this.input = input;
		this.curse = curse;
		this.cost = cost;
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
		return BWRecipeTypes.CURSE_RECIPE_SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return BWRecipeTypes.CURSE_RECIPE_TYPE;
	}

	@SuppressWarnings("ConstantConditions")
	public static class Serializer implements RecipeSerializer<CurseRecipe> {
		private static final ItemStack TAGLOCK = new ItemStack(BWObjects.TAGLOCK);

		public CurseRecipe read(Identifier id, JsonObject json) {
			DefaultedList<Ingredient> ingredients = RitualRecipe.Serializer.getIngredients(JsonHelper.getArray(json, "ingredients"));
			if (ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients for curse recipe");
			} else if (ingredients.size() > 4) {
				throw new JsonParseException("Too many ingredients for curse recipe");
			}
			boolean foundTaglock = false;
			for (Ingredient ingredient : ingredients) {
				if (ingredient.test(TAGLOCK)) {
					foundTaglock = true;
					break;
				}
			}
			if (!foundTaglock) {
				throw new JsonParseException("Taglock not found in curse recipe");
			}
			return new CurseRecipe(id, ingredients, BWRegistries.CURSE.get(Identifier.tryParse(JsonHelper.getString(json, "curse"))), JsonHelper.getInt(json, "cost"));
		}

		public CurseRecipe read(Identifier id, PacketByteBuf buf) {
			RegistryByteBuf regBuf = (RegistryByteBuf) buf;
			DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(regBuf.readVarInt(), Ingredient.EMPTY);
			defaultedList.replaceAll(ignored -> Ingredient.PACKET_CODEC.decode(regBuf));
			return new CurseRecipe(id, defaultedList, BWRegistries.CURSE.get(Identifier.tryParse(regBuf.readString())), regBuf.readInt());
		}

		public void write(PacketByteBuf buf, CurseRecipe recipe) {
			RegistryByteBuf regBuf = (RegistryByteBuf) buf;
			regBuf.writeVarInt(recipe.input.size());
			for (Ingredient ingredient : recipe.input) {
				Ingredient.PACKET_CODEC.encode(regBuf, ingredient);
			}
			regBuf.writeString(BWRegistries.CURSE.getId(recipe.curse).toString());
			regBuf.writeInt(recipe.cost);
		}

		@Override
		public MapCodec<CurseRecipe> codec() {
			return new MapCodec<>() {
				@Override
				public <T> Stream<T> keys(DynamicOps<T> ops) {
					return Stream.of();
				}

				@Override
				public <T> DataResult<CurseRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
					try {
						JsonObject json = new JsonObject();
						input.entries().forEach(pair -> {
							String key = ops.getStringValue(pair.getFirst()).getOrThrow(IllegalStateException::new);
							JsonElement value = ops.convertTo(JsonOps.INSTANCE, pair.getSecond());
							json.add(key, value);
						});
						return DataResult.success(read(null, json));
					} catch (Exception e) {
						return DataResult.error(() -> "CurseRecipe decode failed: " + e.getMessage());
					}
				}

				@Override
				public <T> RecordBuilder<T> encode(CurseRecipe recipe, DynamicOps<T> ops, RecordBuilder<T> prefix) {
					return prefix;
				}
			};
		}

		@Override
		public PacketCodec<RegistryByteBuf, CurseRecipe> packetCodec() {
			return PacketCodec.ofStatic(
				(RegistryByteBuf buf, CurseRecipe recipe) -> write(buf, recipe),
				(RegistryByteBuf buf) -> read(null, buf)
			);
		}
	}
}
