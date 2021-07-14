package paulevs.betternether.structures;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public abstract class StructureObjScatter implements IStructure {
	private static final MutableBlockPos POS = new MutableBlockPos();

	final StructureWorld[] structures;
	final int distance;
	final int manDist;

	public StructureObjScatter(int distance, StructureWorld[] structures) {
		this.distance = distance;
		this.manDist = (int) Math.ceil(distance * 1.5);
		this.structures = structures;
	}

	@Override
	public void generate(ServerLevelAccessor world, BlockPos pos, Random random) {
		if (isGround(world.getBlockState(pos.below())) && isGround(world.getBlockState(pos.below(2))) && noObjNear(world, pos)) {
			StructureWorld tree = structures[random.nextInt(structures.length)];
			tree.generate(world, pos, random);
		}
	}

	protected abstract boolean isStructure(BlockState state);

	protected abstract boolean isGround(BlockState state);

	private boolean noObjNear(LevelAccessor world, BlockPos pos) {
		int x1 = pos.getX() - distance;
		int z1 = pos.getZ() - distance;
		int x2 = pos.getX() + distance;
		int z2 = pos.getZ() + distance;
		POS.setY(pos.getY());
		for (int x = x1; x <= x2; x++) {
			POS.setX(x);
			for (int z = z1; z <= z2; z++) {
				POS.setZ(z);
				if (isInside(x - pos.getX(), z - pos.getZ()) && isStructure(world.getBlockState(POS)))
					return false;
			}
		}
		return true;
	}

	private boolean isInside(int x, int z) {
		return (Math.abs(x) + Math.abs(z)) <= manDist;
	}
}
