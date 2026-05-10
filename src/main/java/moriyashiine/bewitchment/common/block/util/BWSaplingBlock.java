/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.bewitchment.common.block.util;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SaplingGenerator;

public class BWSaplingBlock extends SaplingBlock {
	public BWSaplingBlock(SaplingGenerator generator, Settings settings) {
		super(generator, settings);
	}

	public BWSaplingBlock(Settings settings) {
		super(null, settings);
	}

	@Override
	public MapCodec<? extends SaplingBlock> getCodec() {
		return createCodec(BWSaplingBlock::new);
	}
}
