package com.ForgeEssentials.client.core;

import java.io.File;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.client.WorldControl.CUI.CUIRenderrer;
import com.ForgeEssentials.client.network.ClientConnectionHandler;
import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.ProxyCommon;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@SideOnly(value=Side.CLIENT)
public class ProxyClient extends ProxyCommon
{
	public static PlayerInfoClient info;
	
	// needed? probably not...
	public static final File FEDIRC = new File("./ForgeEssentialsClient/");
	
	@Override
	public void PreLoad(FMLPreInitializationEvent e)
	{
		// check directory constants and create...
		if (!FEDIRC.exists() || !FEDIRC.isDirectory())
			FEDIRC.mkdir();
	}
	
	@Override
	public void load(FMLInitializationEvent e)
	{
		NetworkRegistry.instance().registerConnectionHandler(new ClientConnectionHandler());
		MinecraftForge.EVENT_BUS.register(new CUIRenderrer());
	}
	
	@Override
	public void updateInfo(PlayerInfo infoServer, EntityPlayer player)
	{
		info.setPoint1(infoServer.getPoint1());
		info.setPoint2(infoServer.getPoint2());
	}
}
