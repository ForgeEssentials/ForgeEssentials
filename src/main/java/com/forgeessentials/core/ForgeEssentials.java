package com.forgeessentials.core;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.CommandFEInfo;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.commands.HelpFixer;
import com.forgeessentials.core.commands.selections.*;
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
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.WarpPoint;
import com.forgeessentials.util.selections.WorldPoint;
import com.forgeessentials.util.*;
import com.forgeessentials.util.events.ForgeEssentialsEventFactory;
import com.forgeessentials.util.tasks.TaskRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main mod class
 */

@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = FEModContainer.version, acceptableRemoteVersions = "*")
public class ForgeEssentials {

    @Instance(value = "ForgeEssentials")
    public static ForgeEssentials instance;

    public static CoreConfig config;
    public static boolean verCheck = true;
    public static boolean preload;
    public static String modlistLocation;
    public static File FEDIR;
    public static boolean mcstats;
    public static String version;
    public static boolean sanitycheck;
    public ModuleLauncher mdlaunch;
    private TaskRegistry tasks;

    public ForgeEssentials()
    {
        tasks = new TaskRegistry();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        FEDIR = new File(FunctionHelper.getBaseDir(), "/ForgeEssentials");

        OutputHandler log = new OutputHandler(); // init the logger

        OutputHandler.felog.info("Forge Essentials version "
                + FEModContainer.version + " loading, reading config from "
                + FEDIR.getAbsolutePath());

        version = e.getModMetadata().version;

        // setup fedir stuff
        config = new CoreConfig();
        EnvironmentChecker.checkBukkit();
        EnvironmentChecker.checkWorldEdit();

        // Data API stuff
        {
            // setup
            DataStorageManager.manager = new StorageManager(config.config);

            // register DataDrivers
            DataStorageManager.registerDriver("ForgeConfig",
                    ForgeConfigDataDriver.class);
            DataStorageManager.registerDriver("NBT", NBTDataDriver.class);
            DataStorageManager.registerDriver("SQL_DB", SQLDataDriver.class);

            // Register saveables..
            DataStorageManager.registerSaveableType(PlayerInfo.class);

            DataStorageManager.registerSaveableType(Point.class);
            DataStorageManager.registerSaveableType(WorldPoint.class);
            DataStorageManager.registerSaveableType(WarpPoint.class);

            DataStorageManager.registerSaveableType(TypeInfoItemStack.class,
                    new ClassContainer(ItemStack.class));
            DataStorageManager.registerSaveableType(TypeInfoNBTCompound.class,
                    new ClassContainer(NBTTagCompound.class));
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

        mdlaunch.load(e);

        // other stuff
        ForgeEssentialsEventFactory factory = new ForgeEssentialsEventFactory();
        FMLCommonHandler.instance().bus().register(factory);
        MinecraftForge.EVENT_BUS.register(factory);

        MinecraftForge.EVENT_BUS.register(new WandController());
    }

    @EventHandler
    public void postLoad(FMLPostInitializationEvent e)
    {
        mdlaunch.postLoad(e);

        FunctionHelper.netHandler.registerMessage(PacketSelectionUpdate.class, PacketSelectionUpdate.Message.class, 0, Side.SERVER);
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
        }

        for (ForgeEssentialsCommandBase command : commands)
        {
            if (command.getCommandPerm() != null && command.getReggroup() != null)
                {
                    APIRegistry.permReg.registerPermissionLevel(command.getCommandPerm(), command.getReggroup());
                }
                e.registerServerCommand(command);
        }

        tasks.onServerStart();

        // do modules last... just in case...
        mdlaunch.serverStarting(e);

        ForgeChunkManager.setForcedChunkLoadingCallback(this,
                new FEChunkLoader());
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent e)
    {
        CommandSetChecker.remove();

        mdlaunch.serverStarted(e);
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent e)
    {
        mdlaunch.serverStopping(e);
        tasks.onServerStop();
    }

}
