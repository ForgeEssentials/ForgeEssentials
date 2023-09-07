package com.forgeessentials.perftools;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.registration.FECommandManager;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoaderBase;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

@FEModule(name = "Perftools", parentMod = ForgeEssentials.class, defaultModule = false)
public class PerfToolsModule extends ConfigLoaderBase
{
    private static ForgeConfigSpec PERF_CONFIG;
    private static final ConfigData data = new ConfigData("Perftools", PERF_CONFIG, new ForgeConfigSpec.Builder());

    private MemoryWatchdog watchdog;

    protected static final String PERM_WARN = "fe.core.memUsageMsg";
    public static int percentageWarn;
    public static int checkInterval;
    protected static boolean warn;

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        FECommandManager.registerCommand(new CommandServerPerf(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandChunkLoaderList(true), event.getDispatcher());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerStartingEvent e)
    {
        if (warn)
        {
            watchdog = new MemoryWatchdog();
            APIRegistry.perms.registerNode(PERM_WARN, DefaultPermissionLevel.OP,
                    "Warn server ops when high server resource usage is detected");
            TaskRegistry.scheduleRepeated(watchdog, (long) checkInterval * 60 * 1000);
        }
    }

    static ForgeConfigSpec.BooleanValue FEwarn;
    static ForgeConfigSpec.IntValue FEpercentageWarn;
    static ForgeConfigSpec.IntValue FEcheckInterval;

    @Override
    public void load(Builder SERVER_BUILDER, boolean isReload)
    {
        SERVER_BUILDER.comment("Configure ForgeEssentials Core.").push(FEConfig.CONFIG_MAIN_CORE);
        FEwarn = SERVER_BUILDER.comment("Warn server ops when we detect high memory usage.").define("warnHighMemUsage",
                true);
        FEpercentageWarn = SERVER_BUILDER.comment("Percentage at which to warn server ops")
                .defineInRange("percentageWarn", 90, 1, 100);
        FEcheckInterval = SERVER_BUILDER.comment("Interval in minutes to check memory use.")
                .defineInRange("checkInterval", 5, 1, 60);
        SERVER_BUILDER.pop();
    }

    @Override
    public void bakeConfig(boolean reload)
    {
        warn = FEwarn.get();
        percentageWarn = FEpercentageWarn.get();
        checkInterval = FEcheckInterval.get();
    }

    @Override
    public ConfigData returnData()
    {
        return data;
    }
}
