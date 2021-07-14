package paulevs.betternether.blocks;

import java.util.Collections;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import paulevs.betternether.client.IRenderTypeable;

public class BNPane extends IronBarsBlock implements IRenderTypeable {
	private boolean dropSelf;

	public BNPane(Block block, boolean dropSelf) {
		super(FabricBlockSettings.copyOf(block).strength(0.3F, 0.3F).nonOpaque());
		this.dropSelf = dropSelf;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		if (dropSelf)
			return Collections.singletonList(new ItemStack(this.asItem()));
		else
			return super.getDrops(state, builder);
	}

	@Override
	public BNRenderLayer getRenderLayer() {
		return BNRenderLayer.TRANSLUCENT;
	}

	@Environment(EnvType.CLIENT)
	public boolean skipRendering(BlockState state, BlockState neighbor, Direction facing) {
		if (neighbor.getBlock() == this) {
			if (!facing.getAxis().isHorizontal()) {
				return false;
			}

			if (state.getValue(PROPERTY_BY_DIRECTION.get(facing)) && neighbor.getValue(PROPERTY_BY_DIRECTION.get(facing.getOpposite()))) {
				return true;
			}
		}

		return super.skipRendering(state, neighbor, facing);
	}
}
