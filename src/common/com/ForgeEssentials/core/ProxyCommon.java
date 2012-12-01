package com.ForgeEssentials.core;

import net.minecraft.src.EntityPlayer;

import com.ForgeEssentials.core.network.PacketSelectionUpdate;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class ProxyCommon
{
	public void preLoad(FMLPreInitializationEvent e)
	{
		
	}
	
	public void load(FMLInitializationEvent e)
	{
		
	}
	
	public void updateInfo(PlayerInfo info, EntityPlayer player)
	{
		PacketDispatcher.sendPacketToPlayer((new PacketSelectionUpdate(info)).getPayload(), (Player)player);
	}
}