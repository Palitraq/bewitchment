package moriyashiine.bewitchment.common.registry;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.WoodType;

public class BWWoodTypes {
	public static final WoodType JUNIPER = WoodType.register(new WoodType("juniper", BlockSetType.OAK));
	public static final WoodType CYPRESS = WoodType.register(new WoodType("cypress", BlockSetType.OAK));
	public static final WoodType ELDER = WoodType.register(new WoodType("elder", BlockSetType.OAK));
	public static final WoodType DRAGONS_BLOOD = WoodType.register(new WoodType("dragons_blood", BlockSetType.OAK));

	public static void init() {
	}
}
