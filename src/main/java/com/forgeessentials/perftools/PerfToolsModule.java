package com.forgeessentials.perftools;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "perftools", parentMod = ForgeEssentials.class)
public class PerfToolsModule extends ConfigLoaderBase
{
    private MemoryWatchdog watchdog;

    protected static final String PERM_WARN = "fe.core.memUsageMsg";
    public static int percentageWarn;
    public static int checkInterval;
    protected static boolean warn;

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        FECommandManager.registerCommand(new CommandServerPerf());
        FECommandManager.registerCommand(new CommandChunkLoaderList());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        if (warn)
        {
            watchdog = new MemoryWatchdog();
            PermissionsManager.registerPermission(PERM_WARN, RegisteredPermValue.OP);
            TaskRegistry.getInstance().scheduleRepeated(watchdog, checkInterval * 60 * 1000);
        }
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        warn = config.get(ForgeEssentials.CONFIG_CAT, "warnHighMemUsage", true, "Warn server ops when we detect high memory usage.").getBoolean(true);
        percentageWarn = config.get(ForgeEssentials.CONFIG_CAT, "percentageWarn", 90, "Percentage at which to warn server ops").getInt(90);
        checkInterval = config.get(ForgeEssentials.CONFIG_CAT, "checkInterval", 5, "Interval in minutes to check memory use.").getInt(5);
    }
}
