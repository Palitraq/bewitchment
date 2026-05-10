package moriyashiine.bewitchment.common.registry;

import moriyashiine.bewitchment.common.Bewitchment;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BWBoatTypes {
	public static void init() {
		register("juniper");
		register("cypress");
		register("elder");
		register("dragons_blood");
	}

	private static void register(String name) {
		Item boatItem = Registry.register(
				Registries.ITEM,
				Bewitchment.id(name + "_boat"),
				new BoatItem(false, BoatEntity.Type.OAK, new Item.Settings().maxCount(1))
		);
		Item chestBoatItem = Registry.register(
				Registries.ITEM,
				Bewitchment.id(name + "_chest_boat"),
				new BoatItem(true, BoatEntity.Type.OAK, new Item.Settings().maxCount(1))
		);
		BWObjects.BOATS.add(boatItem);
		BWObjects.BOATS.add(chestBoatItem);
	}
}
