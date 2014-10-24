package com.forgeessentials.core;

import com.forgeessentials.core.commands.CommandFEInfo;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.commands.HelpFixer;
import com.forgeessentials.core.commands.selections.CommandDeselect;
import com.forgeessentials.core.commands.selections.CommandExpand;
import com.forgeessentials.core.commands.selections.CommandExpandY;
import com.forgeessentials.core.commands.selections.CommandPos;
import com.forgeessentials.core.commands.selections.CommandWand;
import com.forgeessentials.core.commands.selections.WandController;
import com.forgeessentials.core.compat.CommandSetChecker;
import com.forgeessentials.core.compat.EnvironmentChecker;
import com.forgeessentials.core.misc.BlockModListFile;
import com.forgeessentials.core.misc.LoginMessage;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.network.PacketSelectionUpdate;
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
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

	public ForgeEssentials()
	{
		tasks = new TaskRegistry();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		FEDIR = new File(FunctionHelper.getBaseDir(), "/ForgeEssentials");

		OutputHandler log = new OutputHandler(); // init the logger

		OutputHandler.felog.info("Forge Essentials version " + FEModContainer.version + " loading, reading config from " + FEDIR.getAbsolutePath());

		// setup fedir stuff
		config = new CoreConfig();
		EnvironmentChecker.checkBukkit();
		EnvironmentChecker.checkWorldEdit();

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
		// load up DataAPI
		((StorageManager) DataStorageManager.manager).setupManager();

		// other stuff
		ForgeEssentialsEventFactory factory = new ForgeEssentialsEventFactory();
		FMLCommonHandler.instance().bus().register(factory);
		MinecraftForge.EVENT_BUS.register(factory);

		MinecraftForge.EVENT_BUS.register(new WandController());

        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModuleInitEvent(e));
	}

	@EventHandler
	public void postLoad(FMLPostInitializationEvent e)
    {
		FunctionHelper.netHandler.registerMessage(PacketSelectionUpdate.class, PacketSelectionUpdate.Message.class, 0, Side.SERVER);
        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModulePostInitEvent(e));
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent e)
	{
		// load up DataAPI
		((StorageManager) DataStorageManager.manager).serverStart(e);

		BlockModListFile.makeModList();

		List<ForgeEssentialsCommandBase> commands = new ArrayList();

		// commands
		commands.add(new CommandFEInfo());
		e.registerServerCommand(new HelpFixer());

		if (!EnvironmentChecker.worldEditInstalled)
		{
			commands.add(new CommandPos(1));
			commands.add(new CommandPos(2));
			commands.add(new CommandWand());
			commands.add(new CommandDeselect());
			commands.add(new CommandExpand());
			commands.add(new CommandExpandY());
		}

		for (ForgeEssentialsCommandBase command : commands)
		{
			if (command.getPermissionNode() != null && command.getDefaultPermission() != null)
			{
				PermissionsManager.registerPermission(command.getPermissionNode(), command.getDefaultPermission());
			}
			e.registerServerCommand(command);
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
