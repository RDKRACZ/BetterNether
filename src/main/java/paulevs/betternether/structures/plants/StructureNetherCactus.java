package paulevs.betternether.structures.plants;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.blocks.BlockNetherCactus;
import paulevs.betternether.registry.BlocksRegistry;
import paulevs.betternether.structures.IStructure;

public class StructureNetherCactus implements IStructure {
	private MutableBlockPos npos = new MutableBlockPos();

	private boolean canPlaceAt(LevelAccessor world, BlockPos pos) {
		return world.getBlockState(pos.below()).getBlock() == Blocks.GRAVEL;
	}

	@Override
	public void generate(ServerLevelAccessor world, BlockPos pos, Random random) {
		if (canPlaceAt(world, pos)) {
			BlockState top = BlocksRegistry.NETHER_CACTUS.defaultBlockState();
			BlockState bottom = BlocksRegistry.NETHER_CACTUS.defaultBlockState().setValue(BlockNetherCactus.TOP, false);
			for (int i = 0; i < 16; i++) {
				int x = pos.getX() + (int) (random.nextGaussian() * 4);
				int z = pos.getZ() + (int) (random.nextGaussian() * 4);
				if (((x + z + pos.getY()) & 1) == 0) {
					if (random.nextBoolean()) {
						x += random.nextBoolean() ? 1 : -1;
					}
					else {
						z += random.nextBoolean() ? 1 : -1;
					}
				}
				int y = pos.getY() + random.nextInt(8);
				for (int j = 0; j < 8; j++) {
					npos.set(x, y - j, z);
					if (world.isEmptyBlock(npos) && canPlaceAt(world, npos)) {
						int h = random.nextInt(3);
						for (int n = 0; n < h; n++)
							BlocksHelper.setWithUpdate(world, npos.above(n), bottom);
						BlocksHelper.setWithUpdate(world, npos.above(h), top);
						break;
					}
				}
			}
		}
	}
}