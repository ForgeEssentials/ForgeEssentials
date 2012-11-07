package com.ForgeEssentials.core;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.WorldControl.WorldControl;
import com.ForgeEssentials.client.network.HandlerClient;
import com.ForgeEssentials.commands.CommandButcher;
import com.ForgeEssentials.commands.CommandHome;
import com.ForgeEssentials.commands.CommandMotd;
import com.ForgeEssentials.commands.CommandSetHome;
import com.ForgeEssentials.core.commands.CommandFEVersion;
import com.ForgeEssentials.network.ConnectionHandler;
import com.ForgeEssentials.network.HandlerServer;
import com.ForgeEssentials.permissions.FEPermissionHandler;

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

	public FEPermissionHandler pHandler;
	public WorldControl worldcontrol;
	public static FEConfig config;

	@PreInit
	public void preInit(FMLPreInitializationEvent e)
	{
		Version.checkVersion();
		worldcontrol = new WorldControl();
		worldcontrol.preLoad(e);
	}

	@Init
	public void load(FMLInitializationEvent e)
	{
		worldcontrol.load(e);
		proxy.load(e);
		pHandler = new FEPermissionHandler();
		MinecraftForge.EVENT_BUS.register(pHandler);
	}

	@ServerStarting
	public void serverStart(FMLServerStartingEvent e)
	{
		// commands
		e.registerServerCommand(new CommandMotd());
		e.registerServerCommand(new CommandButcher());
		e.registerServerCommand(new CommandFEVersion());
		//empty commands
		e.registerServerCommand(new CommandHome());
		e.registerServerCommand(new CommandSetHome());
		worldcontrol.serverStarting(e);
	}

}
