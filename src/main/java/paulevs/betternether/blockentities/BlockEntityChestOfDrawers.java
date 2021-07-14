package paulevs.betternether.blockentities;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.blocks.BlockChestOfDrawers;
import paulevs.betternether.registry.BlockEntitiesRegistry;

public class BlockEntityChestOfDrawers extends RandomizableContainerBlockEntity {
	private NonNullList<ItemStack> inventory;
	private int watchers = 0;

	public BlockEntityChestOfDrawers(BlockPos pos, BlockState state) {
		super(BlockEntitiesRegistry.CHEST_OF_DRAWERS, pos, state);
		this.inventory = NonNullList.withSize(27, ItemStack.EMPTY);
	}

	@Override
	public int getContainerSize() {
		return 27;
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.inventory;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> list) {
		this.inventory = list;
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent("container.chest_of_drawers", new Object[0]);
	}

	@Override
	protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
		return ChestMenu.threeRows(syncId, playerInventory, this);
	}

	public void load(CompoundTag tag) {
		super.load(tag);
		this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if (!this.tryLoadLootTable(tag)) {
			ContainerHelper.loadAllItems(tag, this.inventory);
		}
	}

	public CompoundTag save(CompoundTag tag) {
		super.save(tag);
		if (!this.trySaveLootTable(tag)) {
			ContainerHelper.saveAllItems(tag, this.inventory);
		}
		return tag;
	}

	public void onInvOpen(Player player) {
		if (!player.isSpectator()) {
			if (this.watchers < 0) {
				this.watchers = 0;
			}
			if (this.watchers == 0) {
				this.playSound(this.getBlockState(), SoundEvents.BARREL_OPEN);
			}

			++this.watchers;
			this.onInvOpenOrClose();
		}
	}

	@Override
	public void stopOpen(Player player) {
		if (!player.isSpectator()) {
			--this.watchers;
			this.onInvOpenOrClose();
		}
	}

	protected void onInvOpenOrClose() {
		BlockState state = this.getBlockState();
		Block block = state.getBlock();
		if (block instanceof BlockChestOfDrawers && !level.isClientSide) {
			if (watchers > 0 && !state.getValue(BlockChestOfDrawers.OPEN)) {
				BlocksHelper.setWithoutUpdate((ServerLevel) level, worldPosition, state.setValue(BlockChestOfDrawers.OPEN, true));
			}
			else if (watchers == 0 && state.getValue(BlockChestOfDrawers.OPEN)) {
				BlocksHelper.setWithoutUpdate((ServerLevel) level, worldPosition, state.setValue(BlockChestOfDrawers.OPEN, false));
			}
		}
	}

	private void playSound(BlockState blockState, SoundEvent soundEvent) {
		Vec3i vec3i = ((Direction) blockState.getValue(BlockChestOfDrawers.FACING)).getNormal();
		double d = (double) this.worldPosition.getX() + 0.5D + (double) vec3i.getX() / 2.0D;
		double e = (double) this.worldPosition.getY() + 0.5D + (double) vec3i.getY() / 2.0D;
		double f = (double) this.worldPosition.getZ() + 0.5D + (double) vec3i.getZ() / 2.0D;
		this.level.playSound((Player) null, d, e, f, soundEvent, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
	}

	public void addItemsToList(List<ItemStack> items) {
		for (ItemStack item : inventory)
			if (item != null)
				items.add(item);
	}
}
