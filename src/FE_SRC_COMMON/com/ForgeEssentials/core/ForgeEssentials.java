package com.ForgeEssentials.core;

import java.io.File;

import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import com.ForgeEssentials.client.core.network.HandlerClient;
import com.ForgeEssentials.core.commands.CommandFECredits;
import com.ForgeEssentials.core.commands.CommandFEUpdate;
import com.ForgeEssentials.core.commands.CommandFEVersion;
import com.ForgeEssentials.core.network.HandlerServer;
import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.data.DataStorageManager;
import com.ForgeEssentials.data.ForgeConfigDataDriver;
import com.ForgeEssentials.data.MySQLDataDriver;
import com.ForgeEssentials.data.NBTDataDriver;
import com.ForgeEssentials.data.SQLiteDataDriver;
import com.ForgeEssentials.util.DataStorage;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.Version;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;

/**
 * Main mod class
 */

@NetworkMod(clientSideRequired = false, serverSideRequired = false, clientPacketHandlerSpec = @SidedPacketHandler(channels = { "ForgeEssentials" }, packetHandler = HandlerClient.class), serverPacketHandlerSpec = @SidedPacketHandler(channels = { "ForgeEssentials" }, packetHandler = HandlerServer.class))
@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = "0.1.0")
public class ForgeEssentials
{
	@SidedProxy(clientSide = "com.ForgeEssentials.client.core.ProxyClient", serverSide = "com.ForgeEssentials.core.ProxyCommon")
	public static ProxyCommon proxy;

	@Instance(value = "ForgeEssentials")
	public static ForgeEssentials instance;

	public static CoreConfig config;
	public ModuleLauncher mdlaunch;
	public Localization localization;
	public static boolean verCheck = true;
	public static boolean preload;

	public static String modlistLocation;
	public static String fedirloc = "./ForgeEssentials/";

	public static final File FEDIR = new File(fedirloc);
	
	public static DataStorageManager dataManager;

	@PreInit
	public void preInit(FMLPreInitializationEvent e)
	{
		config = new CoreConfig();

		if (verCheck)
		{
			Version.checkVersion();
		}

		mdlaunch = new ModuleLauncher();
		mdlaunch.preLoad(e);
		
		// Data API stuff
		{
			// setup
			dataManager = new DataStorageManager(config.config);
			
			// register DataDrivers
			dataManager.registerDriver("ForgeConfig", ForgeConfigDataDriver.class);
			dataManager.registerDriver("NBT", NBTDataDriver.class);
			dataManager.registerDriver("MySQL", MySQLDataDriver.class);
			dataManager.registerDriver("SQLite", SQLiteDataDriver.class);
			
			// Register saveables..
			dataManager.registerClass(PlayerInfo.class);
			dataManager.registerClass(Point.class);
			dataManager.registerClass(WorldPoint.class);
			dataManager.registerClass(WarpPoint.class);
		}

		localization = new Localization();
	}

	@Init
	public void load(FMLInitializationEvent e)
	{
		proxy.load(e);
		mdlaunch.load(e);
		localization.load();
		GameRegistry.registerPlayerTracker(new PlayerTracker());
		
		
	}

	@PostInit
	public void postLoad(FMLPostInitializationEvent e)
	{
		mdlaunch.postLoad(e);
	}
	
	@ServerStarting
	public void serverStarting(FMLServerStartingEvent e)
	{
		ModListFile.makeModList();
		
		// Data API stuff
		if (FMLCommonHandler.instance().getSide().isClient())
			dataManager.clearDrivers(); // clear before fuilling up.. if its the client...
		dataManager.setupManager(e);
		
		//Central TP system
		TickRegistry.registerScheduledTickHandler(new TeleportCenter(), Side.SERVER);
		
		DataStorage.load();
		e.registerServerCommand(new CommandFEVersion());
		e.registerServerCommand(new CommandFEUpdate());
		e.registerServerCommand(new CommandFECredits());
		
		
		// do modules last... just in case...
		mdlaunch.serverStarting(e);
	}
	
	@ServerStarted
	public void serverStarted(FMLServerStartedEvent e)
	{
		mdlaunch.serverStarted(e);
	}
	
	@ServerStopping
	public void serverStopping(FMLServerStoppingEvent e)
	{
		mdlaunch.serverStopping(e);
		
		if (FMLCommonHandler.instance().getSide().isServer())
			dataManager.clearDrivers();
		
		DataStorage.save();
	}
	
	@ForgeSubscribe
	public void chuckSave(WorldEvent.Save event)
	{
		DataStorage.save();
	}
}
