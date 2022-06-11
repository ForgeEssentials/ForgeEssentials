package com.forgeessentials.perftools;


import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleCommonSetupEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

@FEModule(name = "perftools", parentMod = ForgeEssentials.class, defaultModule = false)
public class PerfToolsModule
{
    private MemoryWatchdog watchdog;

    protected static final String PERM_WARN = "fe.core.memUsageMsg";
    public static int percentageWarn;
    public static int checkInterval;
    protected static boolean warn;

    @SubscribeEvent
    public void load(FEModuleCommonSetupEvent e)
    {
        FECommandManager.registerCommand(new CommandServerPerf());
        FECommandManager.registerCommand(new CommandChunkLoaderList());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerStartingEvent e)
    {
        if (warn)
        {
            watchdog = new MemoryWatchdog();
            PermissionAPI.registerNode(PERM_WARN, DefaultPermissionLevel.OP, "Warn server ops when high server resource usage is detected");
            TaskRegistry.scheduleRepeated(watchdog, checkInterval * 60 * 1000);
        }
    }

    public static void load(ForgeConfigSpec.Builder SERVER_BUILDER)
    {
    	SERVER_BUILDER.comment("Configure ForgeEssentials Core.").push(FEConfig.CONFIG_CAT);
    	warn = SERVER_BUILDER.comment("Warn server ops when we detect high memory usage.")
                .define("warnHighMemUsage", true).get();
    	percentageWarn = SERVER_BUILDER.comment("Percentage at which to warn server ops")
    			.defineInRange("percentageWarn", 90, 1, 100).get();
    	percentageWarn = SERVER_BUILDER.comment("Interval in minutes to check memory use.")
    			.defineInRange("checkInterval", 5, 1, 60).get();
        SERVER_BUILDER.pop();
    }
}
