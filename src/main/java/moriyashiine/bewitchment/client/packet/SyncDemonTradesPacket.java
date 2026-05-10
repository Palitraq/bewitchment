/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.client.packet;

import moriyashiine.bewitchment.client.screen.DemonScreenHandler;
import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.entity.DemonMerchant;
import moriyashiine.bewitchment.common.entity.living.DemonEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;

import java.util.List;

public class SyncDemonTradesPacket implements CustomPayload {
	public static final Id<SyncDemonTradesPacket> ID = new Id<>(Bewitchment.id("sync_demon_trades"));
	public static final PacketCodec<PacketByteBuf, SyncDemonTradesPacket> CODEC = CustomPayload.codecOf(SyncDemonTradesPacket::write, SyncDemonTradesPacket::new);

	private final int syncId;
	private final List<DemonEntity.DemonTradeOffer> offers;
	private final int traderId;
	private final boolean discount;

	public SyncDemonTradesPacket(int syncId, List<DemonEntity.DemonTradeOffer> offers, int traderId, boolean discount) {
		this.syncId = syncId;
		this.offers = offers;
		this.traderId = traderId;
		this.discount = discount;
	}

	public SyncDemonTradesPacket(PacketByteBuf buf) {
		this.syncId = buf.readInt();
		this.offers = DemonEntity.DemonTradeOffer.fromPacket(buf);
		this.traderId = buf.readInt();
		this.discount = buf.readBoolean();
	}

	public static void send(net.minecraft.server.network.ServerPlayerEntity player, DemonMerchant merchant, int syncId) {
		player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket(new SyncDemonTradesPacket(syncId, merchant.getOffers(), merchant.getDemonTrader().getId(), merchant.isDiscount())));
	}

	public static void register() {
		PayloadTypeRegistry.playS2C().register(ID, CODEC);
		ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
			int syncId = payload.syncId;
			List<DemonEntity.DemonTradeOffer> offers = payload.offers;
			int traderId = payload.traderId;
			boolean discount = payload.discount;
			context.client().execute(() -> {
				if (context.client().player != null) {
					ScreenHandler screenHandler = context.client().player.currentScreenHandler;
					if (syncId == screenHandler.syncId && screenHandler instanceof DemonScreenHandler) {
						((DemonScreenHandler) screenHandler).demonMerchant.setCurrentCustomer(context.client().player);
						((DemonScreenHandler) screenHandler).demonMerchant.setOffersClientside(offers);
						((DemonScreenHandler) screenHandler).demonMerchant.setDemonTraderClientside((LivingEntity) context.client().world.getEntityById(traderId));
						((DemonScreenHandler) screenHandler).demonMerchant.setDiscountClientside(discount);
					}
				}
			});
		});
	}

	private void write(PacketByteBuf buf) {
		buf.writeInt(syncId);
		DemonEntity.DemonTradeOffer.toPacket(offers, buf);
		buf.writeInt(traderId);
		buf.writeBoolean(discount);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
