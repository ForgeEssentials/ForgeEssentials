package com.ForgeEssentials.client.core;

import java.io.File;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.ProxyCommon;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@SideOnly(value=Side.CLIENT)
public class ProxyClient extends ProxyCommon
{
	public static PlayerInfoClient info;
	public static final File FEDIRC = new File("./ForgeEssentialsClient/");
	
	@Override
	public void PreLoad(FMLPreInitializationEvent e)
	{
		// check directory constants and create...
		if (!FEDIRC.exists() || !FEDIRC.isDirectory())
			FEDIRC.mkdir();
		if (!PlayerInfoClient.FECSAVES.exists() || !PlayerInfoClient.FECSAVES.isDirectory())
			PlayerInfoClient.FECSAVES.mkdir();
	}
	
	@Override
	public void load(FMLInitializationEvent e)
	{
		MinecraftForge.EVENT_BUS.register(new CUIRenderrer());
	}
}
