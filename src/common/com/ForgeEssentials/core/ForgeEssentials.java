package com.ForgeEssentials.core;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.WorldControl.WorldControl;
import com.ForgeEssentials.client.network.HandlerClient;
import com.ForgeEssentials.commands.Commands;
import com.ForgeEssentials.core.commands.CommandFEUpdate;
import com.ForgeEssentials.core.commands.CommandFEVersion;
import com.ForgeEssentials.core.commands.CoreCommands;
import com.ForgeEssentials.core.config.FEConfig;
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
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Main mod class
 */

@NetworkMod(clientSideRequired = false, serverSideRequired = false, clientPacketHandlerSpec = @SidedPacketHandler(channels = { "ForgeEssentials"}, packetHandler = HandlerClient.class), serverPacketHandlerSpec = @SidedPacketHandler(channels = { "ForgeEssentials"}, packetHandler = HandlerServer.class))
@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = "0.0.1")
public class ForgeEssentials
{
	@SidedProxy(clientSide = "com.ForgeEssentials.client.core.ProxyClient", serverSide = "com.ForgeEssentials.core.ProxyCommon")
	public static ProxyCommon proxy;

	@Instance(value = "ForgeEssentials")
	public static ForgeEssentials instance;

	public FEConfig config;
	public FEPermissionHandler pHandler;
	public WorldControl worldcontrol;
	public Commands commands;
	public CoreCommands corecmd;
	public ModuleLauncher mdlaunch;
	public static boolean verCheck;
	
	public static final File FEDIR = new File("./ForgeEssentials/");

	@PreInit
	public void preInit(FMLPreInitializationEvent e)
	{
		// check directory constants and create...
		if (!FEDIR.exists() || !FEDIR.isDirectory())
			FEDIR.mkdir();
		if (!PlayerInfo.FESAVES.exists() || !PlayerInfo.FESAVES.isDirectory())
			PlayerInfo.FESAVES.mkdir();
		
		config = new FEConfig();
		config.loadConfig();
		if (verCheck = true){
		Version.checkVersion();
		}
		mdlaunch = new ModuleLauncher();
		mdlaunch.preLoad(e);
	}

	@Init
	public void load(FMLInitializationEvent e)
	{
		mdlaunch.load(e);
		proxy.load(e);
		GameRegistry.registerPlayerTracker(new PlayerTracker());
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent e)
	{
		mdlaunch.serverStarting(e);
	}

}
