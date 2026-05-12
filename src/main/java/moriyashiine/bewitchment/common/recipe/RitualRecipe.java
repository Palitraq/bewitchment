/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moriyashiine.bewitchment.api.registry.RitualFunction;
import moriyashiine.bewitchment.common.registry.BWRecipeTypes;
import moriyashiine.bewitchment.common.registry.BWRegistries;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;

public class RitualRecipe implements Recipe<RecipeInput> {
	private final Identifier identifier;
	public final DefaultedList<Ingredient> input;
	public final String inner, outer;
	public final RitualFunction ritualFunction;
	public final int cost, runningTime;

	public RitualRecipe(Identifier identifier, DefaultedList<Ingredient> input, String inner, String outer, RitualFunction ritualFunction, int cost, int runningTime) {
		this.identifier = identifier;
		this.input = input;
		this.inner = inner;
		this.outer = outer;
		this.ritualFunction = ritualFunction;
		this.cost = cost;
		this.runningTime = runningTime;
	}

	@Override
	public boolean matches(RecipeInput input, World world) {
		return matches(input, this.input);
	}

	@Override
	public ItemStack craft(RecipeInput input, RegistryWrapper.WrapperLookup lookup) {
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
		return BWRecipeTypes.RITUAL_RECIPE_SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return BWRecipeTypes.RITUAL_RECIPE_TYPE;
	}

	public static boolean matches(RecipeInput inv, DefaultedList<Ingredient> input) {
		List<ItemStack> checklist = new ArrayList<>();
		for (int i = 0; i < inv.getSize(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				checklist.add(stack);
			}
		}
		if (input.size() != checklist.size()) {
			return false;
		}
		for (Ingredient ingredient : input) {
			boolean found = false;
			for (ItemStack stack : checklist) {
				if (ingredient.test(stack)) {
					found = true;
					checklist.remove(stack);
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("ConstantConditions")
	public static class Serializer implements RecipeSerializer<RitualRecipe> {
		static {
			System.out.println("RitualRecipe$Serializer.<clinit> called");
		}
		private static final MapCodec<RitualRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").forGetter(r -> r.input),
				Codec.STRING.fieldOf("inner").forGetter(r -> r.inner),
				Codec.STRING.optionalFieldOf("outer", "").forGetter(r -> r.outer),
				Codec.STRING.fieldOf("ritual_function").forGetter(r -> BWRegistries.RITUAL_FUNCTION.getId(r.ritualFunction).toString()),
				Codec.INT.fieldOf("cost").forGetter(r -> r.cost),
				Codec.INT.optionalFieldOf("running_time", 0).forGetter(r -> r.runningTime)
		).apply(instance, (List<Ingredient> ings, String inner, String outer, String ritualFuncStr, Integer cost, Integer runningTime) -> {
			DefaultedList<Ingredient> list = DefaultedList.of();
			for (Ingredient ing : ings) {
				if (!ing.isEmpty()) {
					list.add(ing);
				}
			}
			if (list.isEmpty()) {
				throw new IllegalArgumentException("No ingredients for ritual recipe");
			}
			if (list.size() > 6) {
				throw new IllegalArgumentException("Too many ingredients for ritual recipe");
			}
			if (inner.isEmpty()) {
				throw new IllegalArgumentException("Inner circle is empty");
			}
			RitualFunction func = BWRegistries.RITUAL_FUNCTION.get(Identifier.tryParse(ritualFuncStr));
			if (func == null) {
				throw new IllegalArgumentException("Unknown ritual function: " + ritualFuncStr);
			}
			return new RitualRecipe(null, list, inner, outer, func, cost, runningTime);
		}));

		public RitualRecipe read(Identifier id, JsonObject json) {
			DefaultedList<Ingredient> ingredients = getIngredients(JsonHelper.getArray(json, "ingredients"));
			if (ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients for ritual recipe");
			} else if (ingredients.size() > 6) {
				throw new JsonParseException("Too many ingredients for ritual recipe");
			}
			String inner = JsonHelper.getString(json, "inner");
			if (inner.isEmpty()) {
				throw new JsonParseException("Inner circle is empty");
			}
			return new RitualRecipe(id, ingredients, inner, JsonHelper.getString(json, "outer", ""), BWRegistries.RITUAL_FUNCTION.get(Identifier.tryParse(JsonHelper.getString(json, "ritual_function"))), JsonHelper.getInt(json, "cost"), JsonHelper.getInt(json, "running_time", 0));
		}

		public RitualRecipe read(Identifier id, PacketByteBuf buf) {
			RegistryByteBuf regBuf = (RegistryByteBuf) buf;
			DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(regBuf.readVarInt(), Ingredient.EMPTY);
			defaultedList.replaceAll(ignored -> Ingredient.PACKET_CODEC.decode(regBuf));
			return new RitualRecipe(id, defaultedList, regBuf.readString(), regBuf.readString(), BWRegistries.RITUAL_FUNCTION.get(Identifier.tryParse(regBuf.readString())), regBuf.readInt(), regBuf.readInt());
		}

		public void write(PacketByteBuf buf, RitualRecipe recipe) {
			RegistryByteBuf regBuf = (RegistryByteBuf) buf;
			regBuf.writeVarInt(recipe.input.size());
			for (Ingredient ingredient : recipe.input) {
				Ingredient.PACKET_CODEC.encode(regBuf, ingredient);
			}
			regBuf.writeString(recipe.inner);
			regBuf.writeString(recipe.outer);
			regBuf.writeString(BWRegistries.RITUAL_FUNCTION.getId(recipe.ritualFunction).toString());
			regBuf.writeInt(recipe.cost);
			regBuf.writeInt(recipe.runningTime);
		}

		@Override
		public MapCodec<RitualRecipe> codec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, RitualRecipe> packetCodec() {
			return PacketCodec.ofStatic(
				(RegistryByteBuf buf, RitualRecipe recipe) -> write(buf, recipe),
				(RegistryByteBuf buf) -> read(null, buf)
			);
		}

		public static DefaultedList<Ingredient> getIngredients(JsonArray json) {
			DefaultedList<Ingredient> ingredients = DefaultedList.of();
			for (int i = 0; i < json.size(); i++) {
				Ingredient ingredient = Ingredient.DISALLOW_EMPTY_CODEC.parse(JsonOps.INSTANCE, json.get(i)).getOrThrow();
				if (!ingredient.isEmpty()) {
					ingredients.add(ingredient);
				}
			}
			return ingredients;
		}
	}
}
