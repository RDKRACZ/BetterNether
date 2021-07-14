package paulevs.betternether.structures.plants;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.blocks.BlockProperties.TripleShape;
import paulevs.betternether.blocks.BlockRedLargeMushroom;
import paulevs.betternether.registry.BlocksRegistry;
import paulevs.betternether.structures.IStructure;

public class StructureMedRedMushroom implements IStructure {
	private static final MutableBlockPos POS = new MutableBlockPos();

	@Override
	public void generate(ServerLevelAccessor world, BlockPos pos, Random random) {
		Block under;
		if (world.getBlockState(pos.below()).getBlock() == BlocksRegistry.NETHER_MYCELIUM) {
			for (int i = 0; i < 10; i++) {
				int x = pos.getX() + (int) (random.nextGaussian() * 2);
				int z = pos.getZ() + (int) (random.nextGaussian() * 2);
				if (((x + z) & 1) == 0) {
					if (random.nextBoolean()) {
						x += random.nextBoolean() ? 1 : -1;
					}
					else {
						z += random.nextBoolean() ? 1 : -1;
					}
				}
				int y = pos.getY() + random.nextInt(6);
				for (int j = 0; j < 12; j++) {
					POS.set(x, y - j, z);
					under = world.getBlockState(POS.below()).getBlock();
					if (under == BlocksRegistry.NETHER_MYCELIUM) {
						grow(world, POS, random);
					}
				}
			}
		}
	}

	public void grow(ServerLevelAccessor world, BlockPos pos, Random random) {
		int size = 1 + random.nextInt(4);
		for (int y = 1; y <= size; y++)
			if (!world.isEmptyBlock(pos.above(y))) {
				if (y == 1)
					return;
				size = y - 1;
				break;
			}
		BlockState middle = BlocksRegistry.RED_LARGE_MUSHROOM.defaultBlockState().setValue(BlockRedLargeMushroom.SHAPE, TripleShape.MIDDLE);
		for (int y = 1; y < size; y++)
			BlocksHelper.setWithoutUpdate(world, pos.above(y), middle);
		BlocksHelper.setWithoutUpdate(world, pos.above(size), BlocksRegistry.RED_LARGE_MUSHROOM.defaultBlockState().setValue(BlockRedLargeMushroom.SHAPE, TripleShape.TOP));
		BlocksHelper.setWithUpdate(world, pos, BlocksRegistry.RED_LARGE_MUSHROOM.defaultBlockState().setValue(BlockRedLargeMushroom.SHAPE, TripleShape.BOTTOM));
	}
}
