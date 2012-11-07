package com.ForgeEssentials.core;

import com.ForgeEssentials.client.network.HandlerClient;
import com.ForgeEssentials.network.ConnectionHandler;
import com.ForgeEssentials.network.HandlerServer;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;

@NetworkMod(clientSideRequired = false, serverSideRequired = false, connectionHandler = ConnectionHandler.class, clientPacketHandlerSpec = @SidedPacketHandler(channels = { "ForgeEssentials", "WorldControl" }, packetHandler = HandlerClient.class), serverPacketHandlerSpec = @SidedPacketHandler(channels = { "ForgeEssentials", "WorldControl" }, packetHandler = HandlerServer.class))
@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = "0.0.1")
public class ForgeEssentials
{
	@SidedProxy(clientSide = "com.ForgeEssentials.client.core.ProxyClient", serverSide = "com.ForgeEssentials.core.ProxyCommon")
	public static ProxyCommon proxy;

	@Instance(value = "ForgeEssentials")
	public static ForgeEssentials instance;
    public static Module module;
	
    @PreInit
	public void preInit(FMLPreInitializationEvent e)
	{
		module.preLoad(e);
		FEConfig.loadConfig();
		Version.checkVersion();
	}
	@Init
	public void load(FMLInitializationEvent e)
	{
		module.load(e);
		proxy.load(e);
	}
    @ServerStarting
	public void serverStart(FMLServerStartingEvent e)
	{
		module.serverStarting(e);
	}
	//TODO set a per-player perms config in PERMSDIR
}