package com.forgeessentials.commands.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;

public class PlayerInvChest extends InventoryBasic
{
	public EntityPlayerMP	vieuwer;
	public EntityPlayerMP	owner;
	public boolean			allowUpdate;

	public PlayerInvChest(EntityPlayerMP owner, EntityPlayerMP vieuwer)
	{
		super(owner.username + "'s inventory", false, owner.inventory.mainInventory.length);
		this.owner = owner;
		this.vieuwer = vieuwer;
	}

	@Override
	public void openChest()
	{
		InvSeeMisk.register(this);
		allowUpdate = false;
		for (int id = 0; id < owner.inventory.mainInventory.length; ++id)
		{
			setInventorySlotContents(id, owner.inventory.mainInventory[id]);
		}
		allowUpdate = true;
		super.openChest();
	}

	@Override
	public void closeChest()
	{
		InvSeeMisk.remove(this);
		if (allowUpdate)
		{
			for (int id = 0; id < owner.inventory.mainInventory.length; ++id)
			{
				owner.inventory.mainInventory[id] = getStackInSlot(id);
			}
		}
		onInventoryChanged();
		super.closeChest();
	}

	@Override
	public void onInventoryChanged()
	{
		super.onInventoryChanged();
		if (allowUpdate)
		{
			for (int id = 0; id < owner.inventory.mainInventory.length; ++id)
			{
				owner.inventory.mainInventory[id] = getStackInSlot(id);
			}
		}
	}

	public void update()
	{
		allowUpdate = false;
		for (int id = 0; id < owner.inventory.mainInventory.length; ++id)
		{
			setInventorySlotContents(id, owner.inventory.mainInventory[id]);
		}
		allowUpdate = true;
		onInventoryChanged();
	}
}
