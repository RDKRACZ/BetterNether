package paulevs.betternether.structures.decorations;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.registry.BlocksRegistry;
import paulevs.betternether.structures.IStructure;

public class StructureGeyser implements IStructure {
	@Override
	public void generate(ServerLevelAccessor world, BlockPos pos, Random random) {
		if (BlocksHelper.isNetherrack(world.getBlockState(pos.below())))
			BlocksHelper.setWithoutUpdate(world, pos, BlocksRegistry.GEYSER.defaultBlockState());
	}
}
