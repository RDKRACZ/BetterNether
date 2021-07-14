package paulevs.betternether.mixin.common;

import net.minecraft.tags.Tag;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.betternether.blocks.BNObsidian;
import paulevs.betternether.blocks.BlockObsidianGlass;
import paulevs.betternether.registry.BlocksRegistry;

@Mixin(DiggerItem.class)
public abstract class MiningToolItemMixin extends TieredItem implements Vanishable {
	protected MiningToolItemMixin(float attackDamage, float attackSpeed, Tier material, Tag<Block> effectiveBlocks, Properties settings) {
		super(material, settings);
	}

	@Inject(method = "isCorrectToolForDrops", at = @At(value = "HEAD"), cancellable = true)
	private void effectiveOn(BlockState state, CallbackInfoReturnable<Boolean> info) {
		int level = this.getTier().getLevel();
		if (state.getBlock() == BlocksRegistry.CINCINNASITE_ORE) {
			info.setReturnValue(level >= 1);
			info.cancel();
		}
		else if (state.getBlock() == BlocksRegistry.NETHER_RUBY_ORE) {
			info.setReturnValue(level >= 2);
			info.cancel();
		}
		else if (state.getBlock() instanceof BNObsidian || state.getBlock() instanceof BlockObsidianGlass) {
			info.setReturnValue(level >= 3);
			info.cancel();
		}
	}
}
