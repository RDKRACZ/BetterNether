package paulevs.betternether.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.registry.BlocksRegistry;

public class BlockSoulLilySapling extends BlockCommonSapling {
	public BlockSoulLilySapling() {
		super(BlocksRegistry.SOUL_LILY, MaterialColor.COLOR_ORANGE);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		BlockState ground = world.getBlockState(pos.below());
		return BlocksHelper.isSoulSand(ground) || ground.getBlock() == BlocksRegistry.FARMLAND;
	}
}
