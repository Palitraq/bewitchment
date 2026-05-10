/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import moriyashiine.bewitchment.common.registry.BWRecipeTypes;
import net.minecraft.block.Block;
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

public class AthameStrippingRecipe implements Recipe<RecipeInput> {
	private final Identifier identifier;
	public final Block log, strippedLog;
	private final ItemStack output;

	public AthameStrippingRecipe(Identifier identifier, Block log, Block strippedLog, ItemStack output) {
		this.identifier = identifier;
		this.log = log;
		this.strippedLog = strippedLog;
		this.output = output;
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
		return BWRecipeTypes.ATHAME_STRIPPING_RECIPE_SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return BWRecipeTypes.ATHAME_STRIPPING_RECIPE_TYPE;
	}

	public static class Serializer implements RecipeSerializer<AthameStrippingRecipe> {
		public AthameStrippingRecipe read(Identifier id, JsonObject json) {
			return new AthameStrippingRecipe(id, Registries.BLOCK.get(Identifier.tryParse(JsonHelper.getString(json, "log"))), Registries.BLOCK.get(Identifier.tryParse(JsonHelper.getString(json, "stripped_log"))), ItemStack.CODEC.parse(JsonOps.INSTANCE, JsonHelper.getObject(json, "result")).getOrThrow());
		}

		public AthameStrippingRecipe read(Identifier id, PacketByteBuf buf) {
			RegistryByteBuf regBuf = (RegistryByteBuf) buf;
			return new AthameStrippingRecipe(id, Registries.BLOCK.get(Identifier.tryParse(regBuf.readString())), Registries.BLOCK.get(Identifier.tryParse(regBuf.readString())), ItemStack.OPTIONAL_PACKET_CODEC.decode(regBuf));
		}

		public void write(PacketByteBuf buf, AthameStrippingRecipe recipe) {
			RegistryByteBuf regBuf = (RegistryByteBuf) buf;
			regBuf.writeString(Registries.BLOCK.getId(recipe.log).toString());
			regBuf.writeString(Registries.BLOCK.getId(recipe.strippedLog).toString());
			ItemStack.OPTIONAL_PACKET_CODEC.encode(regBuf, recipe.getResult(null));
		}

		@Override
		public MapCodec<AthameStrippingRecipe> codec() {
			return new MapCodec<>() {
				@Override
				public <T> Stream<T> keys(DynamicOps<T> ops) {
					return Stream.of();
				}

				@Override
				public <T> DataResult<AthameStrippingRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
					return DataResult.error(() -> "Codec not implemented");
				}

				@Override
				public <T> RecordBuilder<T> encode(AthameStrippingRecipe recipe, DynamicOps<T> ops, RecordBuilder<T> prefix) {
					return prefix;
				}
			};
		}

		@Override
		public PacketCodec<RegistryByteBuf, AthameStrippingRecipe> packetCodec() {
			return PacketCodec.ofStatic(
				(RegistryByteBuf buf, AthameStrippingRecipe recipe) -> write(buf, recipe),
				(RegistryByteBuf buf) -> read(null, buf)
			);
		}
	}
}
