package com.ForgeEssentials.core;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.core.commands.CommandFECredits;
import com.ForgeEssentials.core.commands.CommandFEDebug;
import com.ForgeEssentials.core.commands.CommandFEReload;
import com.ForgeEssentials.core.commands.CommandFEVersion;
import com.ForgeEssentials.core.compat.CompatMCStats;
import com.ForgeEssentials.core.compat.DuplicateCommandRemoval;
import com.ForgeEssentials.core.misc.BannedItems;
import com.ForgeEssentials.core.misc.DeathChest;
import com.ForgeEssentials.core.misc.ItemList;
import com.ForgeEssentials.core.misc.LoginMessage;
import com.ForgeEssentials.core.misc.ModListFile;
import com.ForgeEssentials.core.moduleLauncher.ModuleLauncher;
import com.ForgeEssentials.core.network.PacketHandler;
import com.ForgeEssentials.data.ForgeConfigDataDriver;
import com.ForgeEssentials.data.NBTDataDriver;
import com.ForgeEssentials.data.SQLDataDriver;
import com.ForgeEssentials.data.StorageManager;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.MiscEventHandler;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.TickTaskHandler;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;
import com.ForgeEssentials.util.event.ForgeEssentialsEventFactory;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkMod.VersionCheckHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * Main mod class
 */

@NetworkMod(clientSideRequired = false, serverSideRequired = false, serverPacketHandlerSpec = @SidedPacketHandler(channels =
{ "ForgeEssentials" }, packetHandler = PacketHandler.class))
@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = "@VERSION@")
public class ForgeEssentials
{

	@Instance(value = "ForgeEssentials")
	public static ForgeEssentials	instance;

	public static CoreConfig		config;
	public ModuleLauncher			mdlaunch;
	public Localization				localization;
	public static boolean			verCheck	= true;
	public static boolean			preload;

	public static String			modlistLocation;

	public static File				FEDIR;

	public static boolean			mcstats;

	public BannedItems				bannedItems;
	private ItemList				itemList;

	private MiscEventHandler		miscEventHandler;

	public static String			version;
	
	private CompatMCStats mcstatscompat;
	
	private SanityChecker bc;

	@PreInit
	public void preInit(FMLPreInitializationEvent e)
	{
		OutputHandler.init(e.getModLog());
		
		bc = new SanityChecker();
		bc.run();
		
		version = e.getModMetadata().version;
		
		// setup fedir stuff
		if (FMLCommonHandler.instance().getSide().isClient())
			FEDIR = new File(FunctionHelper.getBaseDir(), "ForgeEssentials-CLIENT");
		else
			FEDIR = new File(FunctionHelper.getBaseDir(), "ForgeEssentials");

		config = new CoreConfig();
		mcstatscompat = new CompatMCStats();

		// Data API stuff
		{
			// setup
			DataStorageManager.manager = new StorageManager(config.config);

			// register DataDrivers
			DataStorageManager.registerDriver("ForgeConfig", ForgeConfigDataDriver.class);
			DataStorageManager.registerDriver("NBT", NBTDataDriver.class);
			DataStorageManager.registerDriver("SQL_DB", SQLDataDriver.class);

			// Register saveables..
			DataStorageManager.registerSaveableClass(PlayerInfo.class);
			DataStorageManager.registerSaveableClass(Point.class);
			DataStorageManager.registerSaveableClass(WorldPoint.class);
			DataStorageManager.registerSaveableClass(WarpPoint.class);
		}

		// setup modules AFTER data stuff...
		miscEventHandler = new MiscEventHandler();
		bannedItems = new BannedItems();
		MinecraftForge.EVENT_BUS.register(bannedItems);
		LoginMessage.loadFile();
		mdlaunch = new ModuleLauncher();
		mdlaunch.preLoad(e);

		localization = new Localization();
	}

	@Init
	public void load(FMLInitializationEvent e)
	{
		mdlaunch.load(e);
		localization.load();
		GameRegistry.registerPlayerTracker(new PlayerTracker());
		TickRegistry.registerTickHandler(new TickTaskHandler(), Side.SERVER);

		ForgeEssentialsEventFactory factory = new ForgeEssentialsEventFactory();
		TickRegistry.registerTickHandler(factory, Side.SERVER);
		GameRegistry.registerPlayerTracker(factory);
		MinecraftForge.EVENT_BUS.register(factory);
		
		MinecraftForge.EVENT_BUS.register(new DeathChest());
		
		mcstatscompat.load();
	}

	@PostInit
	public void postLoad(FMLPostInitializationEvent e)
	{
		mdlaunch.postLoad(e);
		bannedItems.postLoad(e);

		itemList = new ItemList();
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent e)
	{
		ModListFile.makeModList();

		// Data API stuff
		((StorageManager) DataStorageManager.manager).setupManager(e);

		// Central TP system
		TickRegistry.registerScheduledTickHandler(new TeleportCenter(), Side.SERVER);

		e.registerServerCommand(new CommandFEVersion());
		e.registerServerCommand(new CommandFECredits());
		e.registerServerCommand(new CommandFEReload());
		e.registerServerCommand(new CommandFEDebug());

		// do modules last... just in case...
		mdlaunch.serverStarting(e);
	}

	@ServerStarted
	public void serverStarted(FMLServerStartedEvent e)
	{
		mdlaunch.serverStarted(e);
		DuplicateCommandRemoval.remove();
		
		CompatMCStats.doMCStats();
	}

	@ServerStopping
	public void serverStopping(FMLServerStoppingEvent e)
	{
		mdlaunch.serverStopping(e);
	}

	@VersionCheckHandler
	public boolean versionCheck(String version)
	{
		return true;
	}

	
}
