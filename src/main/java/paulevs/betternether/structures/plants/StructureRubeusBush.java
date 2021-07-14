package paulevs.betternether.structures.plants;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.blocks.BlockProperties.TripleShape;
import paulevs.betternether.blocks.RubeusLog;
import paulevs.betternether.registry.BlocksRegistry;
import paulevs.betternether.structures.IStructure;

public class StructureRubeusBush implements IStructure {
	private static final MutableBlockPos POS = new MutableBlockPos();

	@Override
	public void generate(ServerLevelAccessor world, BlockPos pos, Random random) {
		if (!world.isEmptyBlock(pos) || !world.isEmptyBlock(pos.above()) || !world.isEmptyBlock(pos.above(15)))
			return;

		float r = random.nextFloat() * 3 + 1;
		int count = (int) r;

		for (int i = 0; i < count; i++) {
			float fr = r - i;
			int ir = (int) Math.ceil(fr);
			float r2 = fr * fr;

			int x1 = pos.getX() - ir;
			int x2 = pos.getX() + ir;
			int z1 = pos.getZ() - ir;
			int z2 = pos.getZ() + ir;

			POS.setY(pos.getY() + i);

			for (int x = x1; x < x2; x++) {
				POS.setX(x);
				int sqx = x - pos.getX();
				sqx *= sqx;
				for (int z = z1; z < z2; z++) {
					int sqz = z - pos.getZ();
					sqz *= sqz;
					POS.setZ(z);
					if (sqx + sqz < r2 + random.nextFloat() * r) {
						setIfAir(world, POS, BlocksRegistry.RUBEUS_LEAVES.defaultBlockState());
					}
				}
			}
		}

		BlocksHelper.setWithoutUpdate(world, pos, BlocksRegistry.RUBEUS_BARK.defaultBlockState().setValue(RubeusLog.SHAPE, TripleShape.MIDDLE));
		setIfAir(world, pos.above(), BlocksRegistry.RUBEUS_LEAVES.defaultBlockState());
		setIfAir(world, pos.north(), BlocksRegistry.RUBEUS_LEAVES.defaultBlockState());
		setIfAir(world, pos.south(), BlocksRegistry.RUBEUS_LEAVES.defaultBlockState());
		setIfAir(world, pos.east(), BlocksRegistry.RUBEUS_LEAVES.defaultBlockState());
		setIfAir(world, pos.west(), BlocksRegistry.RUBEUS_LEAVES.defaultBlockState());
	}

	private void setIfAir(LevelAccessor world, BlockPos pos, BlockState state) {
		if (world.isEmptyBlock(pos))
			BlocksHelper.setWithoutUpdate(world, pos, state);
	}
}