package paulevs.betternether.structures.plants;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.blocks.BlockSoulLily;
import paulevs.betternether.blocks.BlockSoulLily.SoulLilyShape;
import paulevs.betternether.registry.BlocksRegistry;
import paulevs.betternether.structures.IStructure;

public class StructureSoulLily implements IStructure {
	MutableBlockPos npos = new MutableBlockPos();

	@Override
	public void generate(ServerLevelAccessor world, BlockPos pos, Random random) {
		Block under;
		if (world.getBlockState(pos.below()).getBlock() == Blocks.SOUL_SAND) {
			for (int i = 0; i < 10; i++) {
				int x = pos.getX() + (int) (random.nextGaussian() * 2);
				int z = pos.getZ() + (int) (random.nextGaussian() * 2);
				int y = pos.getY() + random.nextInt(6);
				for (int j = 0; j < 6; j++) {
					npos.set(x, y - j, z);
					if (npos.getY() > 31) {
						under = world.getBlockState(npos.below()).getBlock();
						if (under == Blocks.SOUL_SAND && world.isEmptyBlock(npos)) {
							growTree(world, npos, random);
						}
					}
					else
						break;
				}
			}
		}
	}

	private void growTree(ServerLevelAccessor world, BlockPos pos, Random random) {
		if (world.getBlockState(pos.below()).getBlock() == Blocks.SOUL_SAND) {
			if (world.isEmptyBlock(pos.above())) {
				if (world.isEmptyBlock(pos.above(2)) && isAirSides(world, pos.above(2))) {
					growBig(world, pos);
				}
				else
					growMedium(world, pos);
			}
			else
				growSmall(world, pos);
		}
	}

	public void growSmall(LevelAccessor world, BlockPos pos) {
		BlocksHelper.setWithUpdate(world, pos, BlocksRegistry.SOUL_LILY.defaultBlockState());
	}

	public void growMedium(LevelAccessor world, BlockPos pos) {
		BlocksHelper.setWithUpdate(world, pos,
				BlocksRegistry.SOUL_LILY
						.defaultBlockState()
						.setValue(BlockSoulLily.SHAPE, SoulLilyShape.MEDIUM_BOTTOM));
		BlocksHelper.setWithUpdate(world, pos.above(),
				BlocksRegistry.SOUL_LILY
						.defaultBlockState()
						.setValue(BlockSoulLily.SHAPE, SoulLilyShape.MEDIUM_TOP));
	}

	public void growBig(LevelAccessor world, BlockPos pos) {
		BlocksHelper.setWithUpdate(world, pos, BlocksRegistry.SOUL_LILY
				.defaultBlockState()
				.setValue(BlockSoulLily.SHAPE, SoulLilyShape.BIG_BOTTOM));
		BlocksHelper.setWithUpdate(world, pos.above(),
				BlocksRegistry.SOUL_LILY
						.defaultBlockState()
						.setValue(BlockSoulLily.SHAPE, SoulLilyShape.BIG_MIDDLE));
		BlockPos up = pos.above(2);
		BlocksHelper.setWithUpdate(world, up,
				BlocksRegistry.SOUL_LILY
						.defaultBlockState()
						.setValue(BlockSoulLily.SHAPE, SoulLilyShape.BIG_TOP_CENTER));
		BlocksHelper.setWithUpdate(world, up.north(), BlocksRegistry.SOUL_LILY
				.defaultBlockState()
				.setValue(BlockSoulLily.SHAPE, SoulLilyShape.BIG_TOP_SIDE_S));
		BlocksHelper.setWithUpdate(world, up.south(),
				BlocksRegistry.SOUL_LILY
						.defaultBlockState()
						.setValue(BlockSoulLily.SHAPE, SoulLilyShape.BIG_TOP_SIDE_N));
		BlocksHelper.setWithUpdate(world, up.east(),
				BlocksRegistry.SOUL_LILY
						.defaultBlockState()
						.setValue(BlockSoulLily.SHAPE, SoulLilyShape.BIG_TOP_SIDE_W));
		BlocksHelper.setWithUpdate(world, up.west(),
				BlocksRegistry.SOUL_LILY
						.defaultBlockState()
						.setValue(BlockSoulLily.SHAPE, SoulLilyShape.BIG_TOP_SIDE_E));
	}

	private boolean isAirSides(LevelAccessor world, BlockPos pos) {
		return world.isEmptyBlock(pos.north()) && world.isEmptyBlock(pos.south()) && world.isEmptyBlock(pos.east()) && world.isEmptyBlock(pos.west());
	}
}