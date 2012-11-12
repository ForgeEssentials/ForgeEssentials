package com.ForgeEssentials.core;

import com.ForgeEssentials.network.PacketSelectionUpdate;

import net.minecraft.src.EntityPlayer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class ProxyCommon
{
	public void PreLoad(FMLPreInitializationEvent e)
	{
		
	}
	
	public void load(FMLInitializationEvent e)
	{
		
	}
	
	public void updateInfo(PlayerInfo info, EntityPlayer player)
	{
		PacketDispatcher.sendPacketToPlayer(new PacketSelectionUpdate(info), (Player)player);
	}
}
