package com.forgeessentials.multiworld;

import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.multiworld.command.CommandMultiworld;
import com.forgeessentials.multiworld.command.CommandMultiworldTeleport;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * 
 * @author Olee
 */
@FEModule(name = "Multiworld", parentMod = ForgeEssentials.class, canDisable = true)
public class ModuleMultiworld extends ConfigLoaderBase
{

    public static final String PERM_BASE = "fe.multiworld";
    public static final String PERM_MANAGE = PERM_BASE + ".manage";
    public static final String PERM_DELETE = PERM_BASE + ".delete";
    public static final String PERM_LIST = PERM_BASE + ".list";
    public static final String PERM_TELEPORT = PERM_BASE + ".teleport";

    private static MultiworldManager multiworldManager = new MultiworldManager();

    @SubscribeEvent
    public void postLoad(FEModulePostInitEvent e)
    {
        try
        {
            multiworldManager.loadWorldProviders();
            multiworldManager.loadWorldTypes();

            FECommandManager.registerCommand(new CommandMultiworld());
            FECommandManager.registerCommand(new CommandMultiworldTeleport());
        } catch (java.lang.NoSuchMethodError noSuchMethodError) {
            CrashReport report = CrashReport.makeCrashReport(noSuchMethodError,"MultiWorld Unable to Load, please update Forge or Disable MultiWorld in the main.cfg!");
            report.makeCategory("MultiWorld");
            throw new ReportedException(report);
        }
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        multiworldManager.load();

        PermissionAPI.registerNode(PERM_MANAGE, DefaultPermissionLevel.OP, "Manage multiworlds");
        PermissionAPI.registerNode(PERM_DELETE, DefaultPermissionLevel.OP, "Delete multiworlds");
        PermissionAPI.registerNode(PERM_LIST, DefaultPermissionLevel.ALL, "List multiworlds on the server");
    }

    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent e)
    {
        multiworldManager.serverStopped();
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        // persistenceBackend = config.get(CONFIG_CAT, "persistenceBackend", "flatfile",
        // "Choose a permission persistence backend (flatfile, sql)").getString();
    }

    public static MultiworldManager getMultiworldManager()
    {
        return multiworldManager;
    }

}