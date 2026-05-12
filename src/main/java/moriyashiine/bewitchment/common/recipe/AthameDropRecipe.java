/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import moriyashiine.bewitchment.common.registry.BWRecipeTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.stream.Stream;
import net.minecraft.world.World;

public class AthameDropRecipe implements Recipe<RecipeInput> {
	private final Identifier identifier;
	public final EntityType<?> entity_type;
	private final ItemStack output;
	public final float chance;

	public AthameDropRecipe(Identifier id, EntityType<?> entity_type, ItemStack output, float chance) {
		this.identifier = id;
		this.entity_type = entity_type;
		this.output = output;
		this.chance = chance;
	}

	@Override
	public boolean matches(RecipeInput input, World world) {
		return false;
	}

	@Override
	public ItemStack craft(RecipeInput input, RegistryWrapper.WrapperLookup lookup) {
		return output;
	}

	@Override
	public boolean fits(int width, int height) {
		return false;
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
		return BWRecipeTypes.ATHAME_DROP_RECIPE_SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return BWRecipeTypes.ATHAME_DROP_RECIPE_TYPE;
	}

	public static class Serializer implements RecipeSerializer<AthameDropRecipe> {
		public AthameDropRecipe read(Identifier id, JsonObject json) {
			return new AthameDropRecipe(id, Registries.ENTITY_TYPE.get(Identifier.tryParse(JsonHelper.getString(json, "entity_type"))), ItemStack.CODEC.parse(JsonOps.INSTANCE, JsonHelper.getObject(json, "result")).getOrThrow(), JsonHelper.getFloat(json, "chance"));
		}

		public AthameDropRecipe read(Identifier id, PacketByteBuf buf) {
			RegistryByteBuf regBuf = (RegistryByteBuf) buf;
			return new AthameDropRecipe(id, Registries.ENTITY_TYPE.get(Identifier.tryParse(regBuf.readString())), ItemStack.OPTIONAL_PACKET_CODEC.decode(regBuf), regBuf.readFloat());
		}

		public void write(PacketByteBuf buf, AthameDropRecipe recipe) {
			RegistryByteBuf regBuf = (RegistryByteBuf) buf;
			regBuf.writeString(Registries.ENTITY_TYPE.getId(recipe.entity_type).toString());
			ItemStack.OPTIONAL_PACKET_CODEC.encode(regBuf, recipe.getResult(null));
			regBuf.writeFloat(recipe.chance);
		}

		@Override
		public MapCodec<AthameDropRecipe> codec() {
			return new MapCodec<>() {
				@Override
				public <T> Stream<T> keys(DynamicOps<T> ops) {
					return Stream.of(
							ops.createString("entity_type"),
							ops.createString("result"),
							ops.createString("chance")
					);
				}

				@Override
				public <T> DataResult<AthameDropRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
					try {
						String entityTypeStr = ops.getStringValue(input.get("entity_type")).getOrThrow();
						EntityType<?> entityType = Registries.ENTITY_TYPE.get(Identifier.tryParse(entityTypeStr));
						ItemStack result = ItemStack.CODEC.parse(ops, input.get("result")).getOrThrow();
						float chance = ops.getNumberValue(input.get("chance")).getOrThrow().floatValue();
						return DataResult.success(new AthameDropRecipe(null, entityType, result, chance));
					} catch (Exception e) {
						return DataResult.error(() -> "Failed to decode AthameDropRecipe: " + e.getMessage());
					}
				}

				@Override
				public <T> RecordBuilder<T> encode(AthameDropRecipe recipe, DynamicOps<T> ops, RecordBuilder<T> prefix) {
					return prefix;
				}
			};
		}

		@Override
		public PacketCodec<RegistryByteBuf, AthameDropRecipe> packetCodec() {
			return PacketCodec.ofStatic(
				(RegistryByteBuf buf, AthameDropRecipe recipe) -> write(buf, recipe),
				(RegistryByteBuf buf) -> read(null, buf)
			);
		}
	}
}
