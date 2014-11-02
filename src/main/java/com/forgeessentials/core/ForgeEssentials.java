package com.forgeessentials.core;

import java.io.File;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.CommandFEDebug;
import com.forgeessentials.core.commands.CommandFEInfo;
import com.forgeessentials.core.commands.HelpFixer;
import com.forgeessentials.core.commands.selections.CommandDeselect;
import com.forgeessentials.core.commands.selections.CommandExpand;
import com.forgeessentials.core.commands.selections.CommandExpandY;
import com.forgeessentials.core.commands.selections.CommandPos;
import com.forgeessentials.core.commands.selections.CommandWand;
import com.forgeessentials.core.commands.selections.WandController;
import com.forgeessentials.core.compat.CommandSetChecker;
import com.forgeessentials.core.compat.Environment;
import com.forgeessentials.core.misc.BlockModListFile;
import com.forgeessentials.core.misc.CoreConfig;
import com.forgeessentials.core.misc.LoginMessage;
import com.forgeessentials.core.misc.RespawnHandler;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.network.S0PacketHandshake;
import com.forgeessentials.core.network.S1PacketSelectionUpdate;
import com.forgeessentials.core.preloader.FEModContainer;
import com.forgeessentials.data.ForgeConfigDataDriver;
import com.forgeessentials.data.NBTDataDriver;
import com.forgeessentials.data.SQLDataDriver;
import com.forgeessentials.data.StorageManager;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.data.typeInfo.TypeInfoItemStack;
import com.forgeessentials.data.typeInfo.TypeInfoNBTCompound;
import com.forgeessentials.data.typeInfo.TypeInfoNBTTagList;
import com.forgeessentials.util.FEChunkLoader;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.MiscEventHandler;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.events.FEModuleEvent;
import com.forgeessentials.util.events.ForgeEssentialsEventFactory;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.WarpPoint;
import com.forgeessentials.util.selections.WorldPoint;
import com.forgeessentials.util.tasks.TaskRegistry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * Main mod class
 */

@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = FEModContainer.version, acceptableRemoteVersions = "*", dependencies = "required-after:Forge@[10.13.1.1219,)")
public class ForgeEssentials {

	@Instance(value = "ForgeEssentials")
	public static ForgeEssentials instance;

	public static CoreConfig config;
	public static boolean verCheck = true;
	public static boolean preload;
	public static String modlistLocation;
	public static File FEDIR;
	public static boolean mcstats;
	public ModuleLauncher mdlaunch;
	private TaskRegistry tasks;

    private RespawnHandler respawnHandler;

	// static FE-module flags / variables
    public static boolean worldEditCompatilityPresent = false;

	public ForgeEssentials()
	{
		tasks = new TaskRegistry();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		FunctionHelper.netHandler = NetworkRegistry.INSTANCE.newSimpleChannel("forgeessentials");
		FunctionHelper.netHandler.registerMessage(S0PacketHandshake.class, S0PacketHandshake.class, 0, Side.SERVER);
        FunctionHelper.netHandler.registerMessage(S1PacketSelectionUpdate.class, S1PacketSelectionUpdate.class, 1, Side.CLIENT);
        
		FEDIR = new File(FunctionHelper.getBaseDir(), "/ForgeEssentials");

		OutputHandler log = new OutputHandler(); // init the logger

		OutputHandler.felog.info("Forge Essentials version " + FEModContainer.version + " loading, reading config from " + FEDIR.getAbsolutePath());

		// setup fedir stuff
		config = new CoreConfig();
		Environment.check();

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
			DataStorageManager.registerSaveableType(TypeInfoNBTTagList.class, new ClassContainer(NBTTagList.class));
		}

		new MiscEventHandler();
		LoginMessage.loadFile();
		mdlaunch = new ModuleLauncher();
        mdlaunch.preLoad(e);
	}

	@EventHandler
	public void load(FMLInitializationEvent e)
	{
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        
		// load up DataAPI
		((StorageManager) DataStorageManager.manager).setupManager();

		// other stuff
		ForgeEssentialsEventFactory factory = new ForgeEssentialsEventFactory();
		FMLCommonHandler.instance().bus().register(factory);
		MinecraftForge.EVENT_BUS.register(factory);
		respawnHandler = new RespawnHandler();

        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModuleInitEvent(e));
	}

	@EventHandler
	public void postLoad(FMLPostInitializationEvent e)
    {
        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModulePostInitEvent(e));
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent e)
	{
		// load up DataAPI
		((StorageManager) DataStorageManager.manager).serverStart(e);

		BlockModListFile.makeModList();

		// commands
		e.registerServerCommand(new HelpFixer());
		
		new CommandFEInfo().register(RegisteredPermValue.OP);
        new CommandFEDebug().register(RegisteredPermValue.OP);

		if (!worldEditCompatilityPresent)
		{
			new CommandPos(1).register(RegisteredPermValue.OP);
			new CommandPos(2).register(RegisteredPermValue.OP);
			new CommandWand().register(RegisteredPermValue.OP);
			new CommandDeselect().register(RegisteredPermValue.OP);
			new CommandExpand().register(RegisteredPermValue.OP);
			new CommandExpandY().register(RegisteredPermValue.OP);
            new WandController();
		}

		tasks.onServerStart();

		ForgeChunkManager.setForcedChunkLoadingCallback(this, new FEChunkLoader());

        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModuleServerInitEvent(e));
	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent e)
	{
		CommandSetChecker.remove();

		FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModuleServerPostInitEvent(e));
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent e)
	{
		tasks.onServerStop();
		PlayerInfo.saveAll();
		PlayerInfo.clear();

        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModuleServerStopEvent(e));
	}

}
