package paulevs.betternether.structures.plants;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.registry.BlocksRegistry;
import paulevs.betternether.structures.IStructure;

public class StructureHookMushroom implements IStructure {
	@Override
	public void generate(ServerLevelAccessor world, BlockPos pos, Random random) {
		if (pos.getY() < 90 || !BlocksHelper.isNetherrack(world.getBlockState(pos.above()))) return;
		BlocksHelper.setWithUpdate(world, pos, BlocksRegistry.HOOK_MUSHROOM.defaultBlockState());
	}
}