package paulevs.betternether.structures.plants;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.blocks.BlockLucisMushroom;
import paulevs.betternether.blocks.BlockLucisSpore;
import paulevs.betternether.blocks.BlockProperties.EnumLucisShape;
import paulevs.betternether.registry.BlocksRegistry;
import paulevs.betternether.structures.IStructure;

public class StructureLucis implements IStructure {
	@Override
	public void generate(ServerLevelAccessor world, BlockPos pos, Random random) {
		if (canGenerate(world, pos)) {
			BlockState center = BlocksRegistry.LUCIS_MUSHROOM.defaultBlockState().setValue(BlockLucisMushroom.SHAPE, EnumLucisShape.CENTER);
			BlockState side = BlocksRegistry.LUCIS_MUSHROOM.defaultBlockState().setValue(BlockLucisMushroom.SHAPE, EnumLucisShape.SIDE);
			BlockState corner = BlocksRegistry.LUCIS_MUSHROOM.defaultBlockState().setValue(BlockLucisMushroom.SHAPE, EnumLucisShape.CORNER);

			if (random.nextInt(3) == 0) {
				if (canReplace(world.getBlockState(pos)))
					BlocksHelper.setWithUpdate(world, pos, center);
				if (canReplace(world.getBlockState(pos.north())))
					BlocksHelper.setWithUpdate(world, pos.north(), side.setValue(BlockLucisMushroom.FACING, Direction.NORTH));
				if (canReplace(world.getBlockState(pos.south())))
					BlocksHelper.setWithUpdate(world, pos.south(), side.setValue(BlockLucisMushroom.FACING, Direction.SOUTH));
				if (canReplace(world.getBlockState(pos.east())))
					BlocksHelper.setWithUpdate(world, pos.east(), side.setValue(BlockLucisMushroom.FACING, Direction.EAST));
				if (canReplace(world.getBlockState(pos.west())))
					BlocksHelper.setWithUpdate(world, pos.west(), side.setValue(BlockLucisMushroom.FACING, Direction.WEST));

				if (canReplace(world.getBlockState(pos.north().east())))
					BlocksHelper.setWithUpdate(world, pos.north().east(), corner.setValue(BlockLucisMushroom.FACING, Direction.SOUTH));
				if (canReplace(world.getBlockState(pos.north().west())))
					BlocksHelper.setWithUpdate(world, pos.north().west(), corner.setValue(BlockLucisMushroom.FACING, Direction.EAST));
				if (canReplace(world.getBlockState(pos.south().east())))
					BlocksHelper.setWithUpdate(world, pos.south().east(), corner.setValue(BlockLucisMushroom.FACING, Direction.WEST));
				if (canReplace(world.getBlockState(pos.south().west())))
					BlocksHelper.setWithUpdate(world, pos.south().west(), corner.setValue(BlockLucisMushroom.FACING, Direction.NORTH));
			}
			else {
				BlockState state = world.getBlockState(pos);
				if (state.getBlock() == BlocksRegistry.LUCIS_SPORE) {
					if (state.getValue(BlockLucisSpore.FACING) == Direction.SOUTH) pos = pos.north();
					else if (state.getValue(BlockLucisSpore.FACING) == Direction.WEST) pos = pos.east();
				}
				else {
					if (!world.getBlockState(pos.north()).isAir()) {
						pos = pos.north();
					}
					else if (!world.getBlockState(pos.east()).isAir()) {
						pos = pos.east();
					}
				}

				if (canReplace(world.getBlockState(pos))) BlocksHelper.setWithUpdate(world, pos, corner.setValue(BlockLucisMushroom.FACING, Direction.SOUTH));
				if (canReplace(world.getBlockState(pos.west()))) BlocksHelper.setWithUpdate(world, pos.west(), corner.setValue(BlockLucisMushroom.FACING, Direction.EAST));
				if (canReplace(world.getBlockState(pos.south()))) BlocksHelper.setWithUpdate(world, pos.south(), corner.setValue(BlockLucisMushroom.FACING, Direction.WEST));
				if (canReplace(world.getBlockState(pos.south().west()))) BlocksHelper.setWithUpdate(world, pos.south().west(), corner.setValue(BlockLucisMushroom.FACING, Direction.NORTH));
			}
		}
	}

	private boolean canReplace(BlockState state) {
		return state.getBlock() == BlocksRegistry.LUCIS_SPORE || state.getMaterial().isReplaceable();
	}

	private boolean canGenerate(ServerLevelAccessor world, BlockPos pos) {
		BlockState state;
		for (Direction dir : HorizontalDirectionalBlock.FACING.getPossibleValues())
			if (BlocksHelper.isNetherrack(state = world.getBlockState(pos.relative(dir))) || BlocksRegistry.ANCHOR_TREE.isTreeLog(state.getBlock()))
				return true;
		return false;
	}
}
