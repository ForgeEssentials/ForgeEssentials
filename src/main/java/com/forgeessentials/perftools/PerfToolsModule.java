package com.forgeessentials.perftools;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;



@FEModule(name = "perftools", parentMod = ForgeEssentials.class, defaultModule = false)
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
            PermissionManager.registerPermission(PERM_WARN, PermissionLevel.OP);
            TaskRegistry.scheduleRepeated(watchdog, checkInterval * 60 * 1000);
        }
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        warn = config.get(FEConfig.CONFIG_CAT, "warnHighMemUsage", true, "Warn server ops when we detect high memory usage.").getBoolean(true);
        percentageWarn = config.get(FEConfig.CONFIG_CAT, "percentageWarn", 90, "Percentage at which to warn server ops").getInt(90);
        checkInterval = config.get(FEConfig.CONFIG_CAT, "checkInterval", 5, "Interval in minutes to check memory use.").getInt(5);
    }
}
