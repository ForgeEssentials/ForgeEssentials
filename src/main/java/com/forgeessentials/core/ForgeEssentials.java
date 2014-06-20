package com.forgeessentials.core;

import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.CommandFEInfo;
import com.forgeessentials.core.commands.selections.*;
import com.forgeessentials.core.compat.CompatMCStats;
import com.forgeessentials.core.compat.DuplicateCommandRemoval;
import com.forgeessentials.core.compat.EnvironmentChecker;
import com.forgeessentials.core.misc.*;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.network.FEServerPacketHandler;
import com.forgeessentials.core.preloader.FEModContainer;
import com.forgeessentials.data.ForgeConfigDataDriver;
import com.forgeessentials.data.NBTDataDriver;
import com.forgeessentials.data.SQLDataDriver;
import com.forgeessentials.data.StorageManager;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.data.typeInfo.TypeInfoItemStack;
import com.forgeessentials.data.typeInfo.TypeInfoNBTCompound;
import com.forgeessentials.util.AreaSelector.Point;
import com.forgeessentials.util.AreaSelector.WarpPoint;
import com.forgeessentials.util.AreaSelector.WorldPoint;
import com.forgeessentials.util.*;
import com.forgeessentials.util.events.ForgeEssentialsEventFactory;
import com.forgeessentials.util.tasks.TaskRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkMod.VersionCheckHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

/**
 * Main mod class
 */

@NetworkMod(clientSideRequired = false, serverSideRequired = false,
        serverPacketHandlerSpec = @SidedPacketHandler(channels = { "ForgeEssentials" }, packetHandler = FEServerPacketHandler.class))
@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = FEModContainer.version)
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
    public BannedItems bannedItems;
    private CompatMCStats mcstatscompat;
    private TaskRegistry tasks;

    public ForgeEssentials()
    {
        tasks = new TaskRegistry();
    }

    @PermRegister
    private static void registerPerms(IPermRegisterEvent event)
    {
        if (!EnvironmentChecker.worldEditInstalled)
        {
            event.registerPermissionLevel(
                    "fe.core.info", RegGroup.OWNERS);
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        FEDIR = new File(FunctionHelper.getBaseDir(), "/ForgeEssentials");

        OutputHandler log = new OutputHandler(); // init the logger

        OutputHandler.felog.info("Forge Essentials version "
                + FEModContainer.version + " loading, reading config from "
                + FEDIR.getAbsolutePath());

        // FE MUST BE FIRST!!
        GameRegistry.registerPlayerTracker(new PlayerTracker());

        version = e.getModMetadata().version;

        // setup fedir stuff
        config = new CoreConfig();
        EnvironmentChecker.checkBukkit();
        EnvironmentChecker.checkWorldEdit();

        mcstatscompat = new CompatMCStats();

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
        bannedItems = new BannedItems();
        MinecraftForge.EVENT_BUS.register(bannedItems);
        LoginMessage.loadFile();
        mdlaunch = new ModuleLauncher();
        mdlaunch.preLoad(e);

        FEServerPacketHandler.init();
    }

    @EventHandler
    public void load(FMLInitializationEvent e)
    {
        // load up DataAPI
        ((StorageManager) DataStorageManager.manager).setupManager();

        mdlaunch.load(e);

        // other stuff
        ForgeEssentialsEventFactory factory = new ForgeEssentialsEventFactory();
        TickRegistry.registerTickHandler(factory, Side.SERVER);
        GameRegistry.registerPlayerTracker(factory);
        MinecraftForge.EVENT_BUS.register(factory);

        MinecraftForge.EVENT_BUS.register(new WandController());

        mcstatscompat.load();
    }

    @EventHandler
    public void postLoad(FMLPostInitializationEvent e)
    {
        UnfriendlyItemList.modStep();
        UnfriendlyItemList.output(new File(FEDIR, "UnfriendlyItemList.txt"));

        mdlaunch.postLoad(e);
        bannedItems.postLoad(e);

        new FriendlyItemList();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent e)
    {
        // load up DataAPI
        ((StorageManager) DataStorageManager.manager).serverStart(e);

        ModListFile.makeModList();

        // Central TP system
        TickRegistry.registerScheduledTickHandler(new TeleportCenter(),
                Side.SERVER);

        // commands
        e.registerServerCommand(new CommandFEInfo());

        if (!EnvironmentChecker.worldEditInstalled)
        {
            e.registerServerCommand(new CommandPos(1));
            e.registerServerCommand(new CommandPos(2));
            e.registerServerCommand(new CommandWand());
            e.registerServerCommand(new CommandDeselect());
            e.registerServerCommand(new CommandExpand());
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
        mdlaunch.serverStarted(e);
        DuplicateCommandRemoval.remove();

        CompatMCStats.doMCStats();
    }

    @EventHandler
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
