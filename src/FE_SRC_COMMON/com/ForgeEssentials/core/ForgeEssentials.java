package com.ForgeEssentials.core;

import java.io.File;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.core.commands.CoreCommands;
import com.ForgeEssentials.core.commands.selections.WandController;
import com.ForgeEssentials.core.compat.CompatMCStats;
import com.ForgeEssentials.core.compat.DuplicateCommandRemoval;
import com.ForgeEssentials.core.compat.SanityChecker;
import com.ForgeEssentials.core.misc.BannedItems;
import com.ForgeEssentials.core.misc.FriendlyItemList;
import com.ForgeEssentials.core.misc.LoginMessage;
import com.ForgeEssentials.core.misc.ModListFile;
import com.ForgeEssentials.core.misc.UnfriendlyItemList;
import com.ForgeEssentials.core.misc.scripting.ScriptPlayerTracker;
import com.ForgeEssentials.core.moduleLauncher.ModuleLauncher;
import com.ForgeEssentials.core.network.PacketHandler;
import com.ForgeEssentials.core.preloader.FEModContainer;
import com.ForgeEssentials.data.ForgeConfigDataDriver;
import com.ForgeEssentials.data.NBTDataDriver;
import com.ForgeEssentials.data.SQLDataDriver;
import com.ForgeEssentials.data.StorageManager;
import com.ForgeEssentials.data.api.ClassContainer;
import com.ForgeEssentials.data.api.DataStorageManager;
import com.ForgeEssentials.data.typeInfo.TypeInfoItemStack;
import com.ForgeEssentials.data.typeInfo.TypeInfoNBTCompound;
import com.ForgeEssentials.util.FEChunkLoader;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.MiscEventHandler;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;
import com.ForgeEssentials.util.events.ForgeEssentialsEventFactory;
import com.ForgeEssentials.util.tasks.TaskRegistry;

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

@NetworkMod(
		clientSideRequired = false,
		serverSideRequired = false,
		serverPacketHandlerSpec = @SidedPacketHandler(channels = { "ForgeEssentials" }, packetHandler = PacketHandler.class))
@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = FEModContainer.version)
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
	public static String			version;

	private CompatMCStats			mcstatscompat;

	private SanityChecker			bc;
	public static boolean			sanitycheck;

	private CoreCommands			cmds;

	private TaskRegistry			tasks;

	public ForgeEssentials()
	{
        tasks = new TaskRegistry();
	}
	
	@PreInit
	public void preInit(FMLPreInitializationEvent e)
	{
		
		// FE MUST BE FIRST!!
		GameRegistry.registerPlayerTracker(new PlayerTracker());
		
		version = e.getModMetadata().version;

		// setup fedir stuff
		FEDIR = new File(FunctionHelper.getBaseDir(), "ForgeEssentials");

		config = new CoreConfig();
		GameRegistry.registerPlayerTracker(new ScriptPlayerTracker());
		bc = new SanityChecker();
		bc.run();

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
			DataStorageManager.registerSaveableType(PlayerInfo.class);

			DataStorageManager.registerSaveableType(Point.class);
			DataStorageManager.registerSaveableType(WorldPoint.class);
			DataStorageManager.registerSaveableType(WarpPoint.class);

			DataStorageManager.registerSaveableType(TypeInfoItemStack.class, new ClassContainer(ItemStack.class));
			DataStorageManager.registerSaveableType(TypeInfoNBTCompound.class, new ClassContainer(NBTTagCompound.class));
		}

		new MiscEventHandler();
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
		// load up DataAPI
		((StorageManager) DataStorageManager.manager).setupManager();

		mdlaunch.load(e);
		localization.load();

		//other stuff
		ForgeEssentialsEventFactory factory = new ForgeEssentialsEventFactory();
		TickRegistry.registerTickHandler(factory, Side.SERVER);
		GameRegistry.registerPlayerTracker(factory);
		MinecraftForge.EVENT_BUS.register(factory);

		MinecraftForge.EVENT_BUS.register(new WandController());

		mcstatscompat.load();
	}

	@PostInit
	public void postLoad(FMLPostInitializationEvent e)
	{
		UnfriendlyItemList.modStep();
		UnfriendlyItemList.output(new File(FEDIR, "UnfriendlyItemList.txt"));

		mdlaunch.postLoad(e);
		bannedItems.postLoad(e);

		new FriendlyItemList();
	}
	
	@PermRegister
	private static void registerPerms(IPermRegisterEvent event)
	{
		event.registerPermissionLevel("ForgeEssentials.CoreCommands.select.pos", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.CoreCommands.select.wand", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.CoreCommands.select.deselect", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.CoreCommands.fedebug", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.CoreCommands.fereload", RegGroup.OWNERS);
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent e)
	{
		// load up DataAPI
		((StorageManager) DataStorageManager.manager).serverStart(e);

		ModListFile.makeModList();

		// Central TP system
		TickRegistry.registerScheduledTickHandler(new TeleportCenter(), Side.SERVER);

		cmds = new CoreCommands();
		cmds.load(e);

		tasks.onServerStart();

		// do modules last... just in case...
		mdlaunch.serverStarting(e);

		ForgeChunkManager.setForcedChunkLoadingCallback(this, new FEChunkLoader());
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
		tasks.onServerStop();
	}

	@VersionCheckHandler
	public boolean versionCheck(String version)
	{
		return true;
	}

}
