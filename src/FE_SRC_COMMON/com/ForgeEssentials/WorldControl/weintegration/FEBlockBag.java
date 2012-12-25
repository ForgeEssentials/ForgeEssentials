package com.ForgeEssentials.WorldControl.weintegration;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.sk89q.worldedit.WorldVector;
import com.sk89q.worldedit.bags.BlockBag;
import com.sk89q.worldedit.bags.BlockBagException;
import com.sk89q.worldedit.bags.OutOfBlocksException;
import com.sk89q.worldedit.bags.OutOfSpaceException;
import com.sk89q.worldedit.blocks.BaseItem;

public class FEBlockBag extends BlockBag {
	private final EntityPlayer player;

	public FEBlockBag(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public void addSingleSourcePosition(WorldVector arg0) {

	}

	@Override
	public void addSourcePosition(WorldVector arg0) {

	}

	@Override
	public void flushChanges() {
		player.inventory.onInventoryChanged();
	}

	@Override
	public void storeItem(BaseItem item) throws BlockBagException {
        if (!player.inventory.addItemStackToInventory(Utils.baseItemStackToStack(item))) {
        	throw new OutOfSpaceException(item.getType());
        }
    }

	@Override
    public void fetchItem(BaseItem item) throws BlockBagException {
		boolean found = false;

		for (int i = 0; i < player.inventory.mainInventory.length; i++) {
			ItemStack stack = player.inventory.mainInventory[i];
			if (stack == null || stack.itemID != item.getType() || (stack.getHasSubtypes() && stack.getItemDamage() != item.getData()) || stack.stackSize < 0 || stack.stackSize > 64) continue;

			if (stack.stackSize > 1) {
				stack.stackSize--;
			} else {
				player.inventory.mainInventory[i] = null;
			}

			found = true;
			break;
		}

		if (!found) throw new OutOfBlocksException();
	}
}