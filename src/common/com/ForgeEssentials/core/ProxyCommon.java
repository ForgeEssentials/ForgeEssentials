package com.ForgeEssentials.core;

import net.minecraft.src.EntityPlayer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ProxyCommon
{
	public void PreLoad(FMLPreInitializationEvent e)
	{
		
	}
	
	public void load(FMLInitializationEvent e)
	{
		
	}
	
	public PlayerInfo getPlayerInfo(EntityPlayer player)
	{
		return PlayerInfo.getPlayerInfo(player);
	}
}
