package com.ForgeEssentials.client.core;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.client.CUI.CUIRenderrer;
import com.ForgeEssentials.client.core.network.ClientConnectionHandler;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.ProxyCommon;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class ProxyClient extends ProxyCommon
{
	private static PlayerInfoClient info;
	
	// needed? probably not...
	public static final File FEDIRC = new File("./ForgeEssentialsClient/");
	
	@Override
	public void preLoad(FMLPreInitializationEvent e)
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

	public static PlayerInfoClient getInfo()
	{
		if (info == null)
			info = new PlayerInfoClient();
		return info;
	}

	public static void setInfo(PlayerInfoClient info)
	{
		ProxyClient.info = info;
	}
}
