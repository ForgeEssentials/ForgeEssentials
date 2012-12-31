package com.ForgeEssentials.commands.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;

public class EventHandler
{
	@ForgeSubscribe()
	public void playerInteractEvent(PlayerInteractEvent e)
	{
		/*
		 * Colorize!
		 */
		
		if(e.entityPlayer.getEntityData().getBoolean("colorize"))
		{
			e.setCanceled(true);
			TileEntity te = e.entityPlayer.worldObj.getBlockTileEntity(e.x, e.y, e.z);
			if(te != null)
			{
				if(te instanceof TileEntitySign)
				{
					String[] signText = ((TileEntitySign)te).signText;
						
					signText[0] = colorize(signText[0]);
					signText[1] = colorize(signText[1]);
					signText[2] = colorize(signText[2]);
					signText[3] = colorize(signText[3]);
						
					((TileEntitySign)te).signText = signText;
					e.entityPlayer.worldObj.setBlockTileEntity(e.x, e.y, e.z, te);
					e.entityPlayer.worldObj.markBlockForUpdate(e.x, e.y, e.z);
				}
				else
				{
					e.entityPlayer.sendChatToPlayer("That is no sign!");
				}
			}
			else
			{
				e.entityPlayer.sendChatToPlayer("That is no sign!");
			}
			
			e.entityPlayer.getEntityData().setBoolean("colorize", false);
		}
	}
	
	private String colorize(String string) 
	{
		return string.replaceAll("&", FEChatFormatCodes.CODE.toString());
	}

	@ForgeSubscribe(priority = EventPriority.LOW)
	public void onPlayerDeath(LivingDeathEvent e)
	{
		if (e.entity instanceof EntityPlayer)
			PlayerInfo.getPlayerInfo((EntityPlayer) e.entity).back = FunctionHelper.getEntityPoint(e.entity);
	}
}
