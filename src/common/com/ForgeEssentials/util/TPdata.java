package com.ForgeEssentials.util;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ServerConfigurationManager;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class TPdata 
{
	private WarpPoint point;
	private EntityPlayer player;
	int waittime;
	
	public TPdata(WarpPoint point, EntityPlayer player)
	{
		this.point = point;
		this.player = player;
		this.waittime = TeleportCenter.tpWarmup;
	}
	
	public void count()
	{
		TeleportCenter.abort(this);
		
		waittime --;
		if(waittime == 0)
		{
			doTP();
		}
	}
	
	public void doTP()
	{
		ServerConfigurationManager server = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
		if(player.dimension != point.dim)
		{
			server.transferPlayerToDimension((EntityPlayerMP) player, point.dim);
		}
		((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(point.x, point.y, point.z, point.yaw, point.pitch);
		PlayerInfo.getPlayerInfo((EntityPlayer) player).TPcooldown = TeleportCenter.tpCooldown;
	}

	public EntityPlayer getPlayer()
	{
		return player;
	}
}
