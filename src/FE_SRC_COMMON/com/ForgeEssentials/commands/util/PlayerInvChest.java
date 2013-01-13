package com.ForgeEssentials.commands.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;

public class PlayerInvChest extends InventoryBasic
{
	private EntityPlayerMP owner;

	public PlayerInvChest(EntityPlayerMP player)
	{
		super(player.username + "'s inventory", player.inventory.mainInventory.length);
		owner = player;
	}

	@Override
	public void openChest()
	{
		for (int id = 0; id < getSizeInventory(); ++id)
		{
			setInventorySlotContents(id, owner.inventory.mainInventory[id]);
		}
		super.openChest();
	}

	@Override
	public void closeChest()
	{
		for (int id = 0; id < getSizeInventory(); ++id)
		{
			owner.inventory.mainInventory[id] = getStackInSlot(id);
		}
		super.closeChest();
	}
}
