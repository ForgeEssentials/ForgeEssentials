package com.ForgeEssentials;

import com.ForgeEssentials.WorldControl.WorldControlMain;
import com.ForgeEssentials.permissions.FEPermissionHandler;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = "0.0.1")
public class Main
{
	@SidedProxy(clientSide = "com.ForgeEssentials.ProxyClient", serverSide = "com.ForgeEssentials.ProxyCommon")
	public static ProxyCommon proxy;
	
	@Instance(value="ForgeEssentials")
	public static Main instance;
	
	public FEPermissionHandler pHandler;
	public WorldControlMain worldControl;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent e)
	{
		worldControl = new WorldControlMain();
	}
	
	@Init
	public void load(FMLInitializationEvent e)
	{
		worldControl.load(e);
	}
	
	@ServerStarted
	public void serverStart(FMLServerStartingEvent e)
	{
		worldControl.serverLoad(e);
		
		pHandler = new FEPermissionHandler();
		MinecraftForge.EVENT_BUS.register(pHandler);
	}

}
