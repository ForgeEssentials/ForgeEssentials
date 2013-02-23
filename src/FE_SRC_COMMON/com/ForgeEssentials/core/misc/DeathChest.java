package com.ForgeEssentials.core.misc;

import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermRegister;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;

/**
 * Everyone with the permission "ForgeEssentials.DeathChest" will get a deathchest.
 * Members by default in very zone.
 * 
 * @author Dries007
 *
 */

public class DeathChest
{
	private static final String		PERM	= "ForgeEssentials.DeathChest";
	public static DeathChest 		instance;
	
	public DeathChest()
	{
		instance = this;
	}
	
	@ForgeSubscribe()
	public void deathEvent(PlayerDropsEvent e)
	{
		if(PermissionsAPI.checkPermAllowed(new PermQueryPlayer(e.entityPlayer, PERM)))
		{
			int id = e.entityPlayer.worldObj.getBlockId((int) e.entityPlayer.posX, (int) e.entityPlayer.posY, (int) e.entityPlayer.posZ);
			if(id == 0 || id == Block.snow.blockID || id == Block.tallGrass.blockID || id == Block.deadBush.blockID)
			{
				e.entityPlayer.worldObj.setBlock((int) e.entityPlayer.posX, (int) e.entityPlayer.posY, (int) e.entityPlayer.posZ, Block.chest.blockID);
				TileEntityChest te = (TileEntityChest) Block.chest.createTileEntity(e.entityPlayer.worldObj, e.entityPlayer.worldObj.getBlockMetadata((int) e.entityPlayer.posX, (int) e.entityPlayer.posY, (int) e.entityPlayer.posZ));
				OutputHandler.severe(e.entityPlayer);
				
				for(int i = 0; i < e.drops.size(); i++)
				{
					te.setInventorySlotContents(i,  e.drops.get(i).getEntityItem().copy());
				}
				e.entityPlayer.worldObj.setBlockTileEntity((int) e.entityPlayer.posX, (int) e.entityPlayer.posY, (int) e.entityPlayer.posZ, te);
				e.setCanceled(true);
			}
		}
	}
	
	@PermRegister(ident = "FE-Core-bannedItems")
	public void registerPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(PERM, RegGroup.MEMBERS);
	}
}
