package com.ForgeEssentials.core.misc;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;

import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermRegister;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;

/**
 * Everyone with the permission "ForgeEssentials.DeathChest" will get a deathchest.
 * Members by default in very zone.
 * @author Dries007
 */

public class DeathChest
{
	private static final String	PERM	= "ForgeEssentials.DeathChest";
	public static DeathChest	instance;
	public static boolean		enable;

	public DeathChest()
	{
		instance = this;
	}

	@ForgeSubscribe()
	public void deathEvent(PlayerDropsEvent e)
	{
		if (!enable)
			return;
		if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(e.entityPlayer, PERM)))
		{
			int X = (int) e.entityPlayer.posX, Y = (int) e.entityPlayer.posY, Z = (int) e.entityPlayer.posZ;
			int id = e.entityPlayer.worldObj.getBlockId(X, Y, Z);
			if (id == 0 || id == Block.snow.blockID || id == Block.tallGrass.blockID || id == Block.deadBush.blockID)
			{
				e.entityPlayer.worldObj.setBlock(X, Y, Z, Block.chest.blockID);
				TileEntityChest te = (TileEntityChest) Block.chest.createTileEntity(e.entityPlayer.worldObj, e.entityPlayer.worldObj.getBlockMetadata(X, Y, Z));

				if (e.drops.size() > 27)
				{
					for (int i = 0; i < 27; i++)
					{
						te.setInventorySlotContents(i, e.drops.get(i).getEntityItem().copy());
					}
					check2(e, X, Y, Z);
				}
				else
				{
					for (int i = 0; i < e.drops.size(); i++)
					{
						te.setInventorySlotContents(i, e.drops.get(i).getEntityItem().copy());
					}
				}

				e.entityPlayer.worldObj.setBlockTileEntity(X, Y, Z, te);
				e.setCanceled(true);
			}
		}
	}

	private void check2(PlayerDropsEvent e, int X, int Y, int Z)
	{

		int X2 = X + 1, Y2 = Y, Z2 = Z;
		int id = e.entityPlayer.worldObj.getBlockId(X2, Y2, Z2);
		if (id == 0 || id == Block.snow.blockID || id == Block.tallGrass.blockID || id == Block.deadBush.blockID)
		{
			do2(e, X2, Y2, Z2);
			return;
		}
		X2 = X - 1;
		id = e.entityPlayer.worldObj.getBlockId(X2, Y2, Z2);
		if (id == 0 || id == Block.snow.blockID || id == Block.tallGrass.blockID || id == Block.deadBush.blockID)
		{
			do2(e, X2, Y2, Z2);
			return;
		}
		X2 = X;
		Y2 = Y + 1;
		id = e.entityPlayer.worldObj.getBlockId(X2, Y2, Z2);
		if (id == 0 || id == Block.snow.blockID || id == Block.tallGrass.blockID || id == Block.deadBush.blockID)
		{
			do2(e, X2, Y2, Z2);
			return;
		}
		Y2 = Y - 1;
		id = e.entityPlayer.worldObj.getBlockId(X2, Y2, Z2);
		if (id == 0 || id == Block.snow.blockID || id == Block.tallGrass.blockID || id == Block.deadBush.blockID)
		{
			do2(e, X2, Y2, Z2);
			return;
		}
		Y2 = Y;
		Z2 = Z + 1;
		id = e.entityPlayer.worldObj.getBlockId(X2, Y2, Z2);
		if (id == 0 || id == Block.snow.blockID || id == Block.tallGrass.blockID || id == Block.deadBush.blockID)
		{
			do2(e, X2, Y2, Z2);
			return;
		}
		Z2 = Z - 1;
		id = e.entityPlayer.worldObj.getBlockId(X2, Y2, Z2);
		if (id == 0 || id == Block.snow.blockID || id == Block.tallGrass.blockID || id == Block.deadBush.blockID)
		{
			do2(e, X2, Y2, Z2);
			return;
		}

		/*
		 * If all else fails...
		 */

		for (int i = 27; i < e.drops.size(); i++)
		{
			e.entityPlayer.worldObj.spawnEntityInWorld(e.drops.get(i));
		}
	}

	private void do2(PlayerDropsEvent e, int X2, int Y2, int Z2)
	{
		e.entityPlayer.worldObj.setBlock(X2, Y2, Z2, Block.chest.blockID);
		TileEntityChest te2 = (TileEntityChest) Block.chest.createTileEntity(e.entityPlayer.worldObj, e.entityPlayer.worldObj.getBlockMetadata(X2, Y2, Z2));
		for (int i = 27; i < e.drops.size(); i++)
		{
			te2.setInventorySlotContents(i - 27, e.drops.get(i).getEntityItem().copy());
		}
		e.entityPlayer.worldObj.setBlockTileEntity(X2, Y2, Z2, te2);
	}

	@PermRegister(ident = "FE-Core-bannedItems")
	public void registerPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(PERM, RegGroup.MEMBERS);
	}
}
