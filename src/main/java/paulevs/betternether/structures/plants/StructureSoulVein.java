package paulevs.betternether.structures.plants;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.registry.BlocksRegistry;
import paulevs.betternether.structures.IStructure;

public class StructureSoulVein implements IStructure {
	private MutableBlockPos npos = new MutableBlockPos();

	private boolean canPlaceAt(LevelAccessor world, BlockPos pos) {
		return BlocksRegistry.SOUL_VEIN.canSurvive(BlocksRegistry.SOUL_VEIN.defaultBlockState(), world, pos);
	}

	@Override
	public void generate(ServerLevelAccessor world, BlockPos pos, Random random) {
		if (world.isEmptyBlock(pos) && canPlaceAt(world, pos)) {
			BlockState state = BlocksRegistry.SOUL_VEIN.defaultBlockState();
			BlockState sand = BlocksRegistry.VEINED_SAND.defaultBlockState();
			int x1 = pos.getX() - 1;
			int x2 = pos.getX() + 1;
			int z1 = pos.getZ() - 1;
			int z2 = pos.getZ() + 1;
			for (int x = x1; x <= x2; x++)
				for (int z = z1; z <= z2; z++) {
					int y = pos.getY() + 2;
					for (int j = 0; j < 4; j++) {
						npos.set(x, y - j, z);
						if (world.isEmptyBlock(npos) && canPlaceAt(world, npos)) {
							BlocksHelper.setWithoutUpdate(world, npos, state);
							BlocksHelper.setWithoutUpdate(world, npos.below(), sand);
						}
					}
				}
		}
	}
}