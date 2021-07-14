package paulevs.betternether.blocks;

import java.util.Random;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import paulevs.betternether.BlocksHelper;

public class BlockNetherReed extends BlockBase {
	public static final BooleanProperty TOP = BooleanProperty.create("top");

	public BlockNetherReed() {
		super(FabricBlockSettings.of(Material.PLANT)
				.mapColor(MaterialColor.COLOR_CYAN)
				.sounds(SoundType.CROP)
				.noCollision()
				.breakInstantly()
				.nonOpaque()
				.ticksRandomly());
		this.setRenderLayer(BNRenderLayer.CUTOUT);
		this.registerDefaultState(getStateDefinition().any().setValue(TOP, true));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
		stateManager.add(TOP);
	}

	@Environment(EnvType.CLIENT)
	public float getShadeBrightness(BlockState state, BlockGetter view, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		Block up = world.getBlockState(pos.above()).getBlock();
		BlockState down = world.getBlockState(pos.below());
		if (BlocksHelper.isNetherGround(down)) {
			BlockPos posDown = pos.below();
			boolean lava = BlocksHelper.isLava(world.getBlockState(posDown.north()));
			lava = lava || BlocksHelper.isLava(world.getBlockState(posDown.south()));
			lava = lava || BlocksHelper.isLava(world.getBlockState(posDown.east()));
			lava = lava || BlocksHelper.isLava(world.getBlockState(posDown.west()));
			if (lava) {
				return up == this ? this.defaultBlockState().setValue(TOP, false) : this.defaultBlockState();
			}
			return Blocks.AIR.defaultBlockState();
		}
		else if (down.getBlock() != this)
			return Blocks.AIR.defaultBlockState();
		else if (up != this)
			return this.defaultBlockState();
		else
			return this.defaultBlockState().setValue(TOP, false);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		BlockPos posDown = pos.below();
		BlockState down = world.getBlockState(posDown);
		if (BlocksHelper.isNetherGround(down)) {
			boolean lava = BlocksHelper.isLava(world.getBlockState(posDown.north()));
			lava = lava || BlocksHelper.isLava(world.getBlockState(posDown.south()));
			lava = lava || BlocksHelper.isLava(world.getBlockState(posDown.east()));
			lava = lava || BlocksHelper.isLava(world.getBlockState(posDown.west()));
			return lava;
		}
		else
			return down.getBlock() == this;
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
		if (!canSurvive(state, world, pos)) {
			world.destroyBlock(pos, true);
			return;
		}
		if (state.getValue(TOP).booleanValue()) {
			BlockPos up = pos.above();
			boolean grow = world.isEmptyBlock(up);
			if (grow) {
				int length = BlocksHelper.getLengthDown(world, pos, this);
				boolean isFertile = BlocksHelper.isFertile(world.getBlockState(pos.below(length)));
				if (isFertile)
					length -= 2;
				grow = (length < 3) && (isFertile ? (random.nextInt(8) == 0) : (random.nextInt(16) == 0));
				if (grow) {
					BlocksHelper.setWithUpdate(world, up, defaultBlockState());
					BlocksHelper.setWithUpdate(world, pos, defaultBlockState().setValue(TOP, false));
				}
			}
		}
	}
}
