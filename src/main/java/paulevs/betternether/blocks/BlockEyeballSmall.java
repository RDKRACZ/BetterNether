package paulevs.betternether.blocks;

import java.util.Random;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockEyeballSmall extends BlockEyeBase {
	protected static final VoxelShape SHAPE = Block.box(4, 8, 4, 12, 16, 12);

	public BlockEyeballSmall() {
		super(FabricBlockSettings.of(Material.WOOD)
				.mapColor(MaterialColor.COLOR_BROWN)
				.sounds(SoundType.SLIME_BLOCK)
				.hardness(0.5F)
				.resistance(0.5F)
				.nonOpaque());
	}

	public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
		return SHAPE;
	}

	@Environment(EnvType.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
		if (random.nextInt(5) == 0) {
			double x = pos.getX() + random.nextDouble() * 0.5 + 0.25;
			double y = pos.getY() + random.nextDouble() * 0.1 + 0.5;
			double z = pos.getZ() + random.nextDouble() * 0.5 + 0.25;
			world.addParticle(ParticleTypes.DRIPPING_WATER, x, y, z, 0, 0, 0);
		}
	}

	public boolean canSuffocate(BlockState state, BlockGetter view, BlockPos pos) {
		return false;
	}

	public boolean isSimpleFullBlock(BlockState state, BlockGetter view, BlockPos pos) {
		return false;
	}
}
