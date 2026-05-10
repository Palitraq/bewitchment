/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.client.packet;

import moriyashiine.bewitchment.api.registry.Contract;
import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.registry.BWComponents;
import moriyashiine.bewitchment.common.registry.BWRegistries;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class SyncContractsPacket implements CustomPayload {
	public static final Id<SyncContractsPacket> ID = new Id<>(Bewitchment.id("sync_contracts"));
	public static final PacketCodec<PacketByteBuf, SyncContractsPacket> CODEC = CustomPayload.codecOf(SyncContractsPacket::write, SyncContractsPacket::new);

	private final NbtCompound contracts;

	public SyncContractsPacket(NbtCompound contracts) {
		this.contracts = contracts;
	}

	public SyncContractsPacket(PacketByteBuf buf) {
		this.contracts = buf.readNbt();
	}

	public static void send(net.minecraft.server.network.ServerPlayerEntity player, NbtCompound contracts) {
		player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket(new SyncContractsPacket(contracts)));
	}

	public static void register() {
		PayloadTypeRegistry.playS2C().register(ID, CODEC);
		ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
			NbtCompound contractsCompound = payload.contracts;
			context.client().execute(() -> {
				if (context.client().player != null) {
					BWComponents.CONTRACTS_COMPONENT.maybeGet(context.client().player).ifPresent(contractsComponent -> {
						contractsComponent.getContracts().clear();
						NbtList contractsList = contractsCompound.getList("Contracts", NbtElement.COMPOUND_TYPE);
						for (int i = 0; i < contractsList.size(); i++) {
							NbtCompound contractCompound = contractsList.getCompound(i);
							contractsComponent.addContract(new Contract.Instance(BWRegistries.CONTRACT.get(Identifier.tryParse(contractCompound.getString("Contract"))), contractCompound.getInt("Duration"), contractCompound.getInt("Cost")));
						}
					});
				}
			});
		});
	}

	private void write(PacketByteBuf buf) {
		buf.writeNbt(contracts);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
