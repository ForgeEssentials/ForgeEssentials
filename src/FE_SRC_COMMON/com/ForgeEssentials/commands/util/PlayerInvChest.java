package com.ForgeEssentials.commands.util;

import java.util.EnumSet;

import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.TickType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerInvChest extends InventoryBasic
{	
	public EntityPlayerMP vieuwer;
	public EntityPlayerMP owner;
	public boolean allowUpdate;

	public PlayerInvChest(EntityPlayerMP owner, EntityPlayerMP vieuwer)
	{
		super(owner.username + "'s inventory", owner.inventory.mainInventory.length);
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
		for (int id = 0; id < owner.inventory.mainInventory.length; ++id)
		{
			owner.inventory.mainInventory[id] = getStackInSlot(id);
		}
		onInventoryChanged();
		super.closeChest();
	}
	
	public void onInventoryChanged()
    {
		super.onInventoryChanged();
		if(allowUpdate)
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
			this.setInventorySlotContents(id, owner.inventory.mainInventory[id]);
		}
		allowUpdate = true;
		onInventoryChanged();
	}
}
