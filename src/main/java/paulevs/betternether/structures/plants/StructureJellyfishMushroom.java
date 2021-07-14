package paulevs.betternether.structures.plants;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.blocks.BlockJellyfishMushroom;
import paulevs.betternether.blocks.BlockProperties.JellyShape;
import paulevs.betternether.blocks.BlockProperties.TripleShape;
import paulevs.betternether.registry.BlocksRegistry;
import paulevs.betternether.structures.IStructure;

public class StructureJellyfishMushroom implements IStructure {
	MutableBlockPos npos = new MutableBlockPos();

	@Override
	public void generate(ServerLevelAccessor world, BlockPos pos, Random random) {
		Block under;
		if (BlockTags.NYLIUM.contains(world.getBlockState(pos.below()).getBlock())) {
			for (int i = 0; i < 10; i++) {
				int x = pos.getX() + (int) (random.nextGaussian() * 2);
				int z = pos.getZ() + (int) (random.nextGaussian() * 2);
				int y = pos.getY() + random.nextInt(6);
				for (int j = 0; j < 6; j++) {
					npos.set(x, y - j, z);
					if (npos.getY() > 31) {
						under = world.getBlockState(npos.below()).getBlock();
						if (BlockTags.NYLIUM.contains(under) && world.isEmptyBlock(npos)) {
							grow(world, npos, random);
						}
					}
					else
						break;
				}
			}
		}
	}

	public void grow(ServerLevelAccessor world, BlockPos pos, Random random) {
		if (random.nextBoolean() && world.isEmptyBlock(pos.above()))
			growMedium(world, pos);
		else
			growSmall(world, pos);
	}

	public void growSmall(ServerLevelAccessor world, BlockPos pos) {
		Block down = world.getBlockState(pos.below()).getBlock();
		JellyShape visual = down == BlocksRegistry.MUSHROOM_GRASS ? JellyShape.NORMAL : down == BlocksRegistry.SEPIA_MUSHROOM_GRASS ? JellyShape.SEPIA : JellyShape.POOR;
		BlocksHelper.setWithUpdate(world, pos, BlocksRegistry.JELLYFISH_MUSHROOM.defaultBlockState().setValue(BlockJellyfishMushroom.SHAPE, TripleShape.BOTTOM).setValue(BlockJellyfishMushroom.VISUAL, visual));
	}

	public void growMedium(ServerLevelAccessor world, BlockPos pos) {
		Block down = world.getBlockState(pos.below()).getBlock();
		JellyShape visual = down == BlocksRegistry.MUSHROOM_GRASS ? JellyShape.NORMAL : down == BlocksRegistry.SEPIA_MUSHROOM_GRASS ? JellyShape.SEPIA : JellyShape.POOR;
		BlocksHelper.setWithUpdate(world, pos, BlocksRegistry.JELLYFISH_MUSHROOM.defaultBlockState().setValue(BlockJellyfishMushroom.SHAPE, TripleShape.MIDDLE).setValue(BlockJellyfishMushroom.VISUAL, visual));
		BlocksHelper.setWithUpdate(world, pos.above(), BlocksRegistry.JELLYFISH_MUSHROOM.defaultBlockState().setValue(BlockJellyfishMushroom.SHAPE, TripleShape.TOP).setValue(BlockJellyfishMushroom.VISUAL, visual));
	}
}