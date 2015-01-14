package com.forgeessentials.core.misc;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.config.IConfigLoader;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.tasks.TaskRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.TimerTask;

/**
 * Warns those with permission when the memory usa
 */
public class MemoryWatchdog extends TimerTask implements IConfigLoader
{
    private static final String PERM_WARN = "fe.core.memUsageMsg";
    public static int percentageWarn;
    public static int checkInterval;

    public MemoryWatchdog()
    {
        ForgeEssentials.getConfigManager().registerLoader(ForgeEssentials.getConfigManager().getMainConfigName(), this);
        PermissionsManager.registerPermission(PERM_WARN, RegisteredPermValue.OP);
        TaskRegistry.registerRecurringTask(this, 0, 0, 0, 0, 0, checkInterval, 0, 0);
    }

    @Override
    public void run()
    {
        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();

        long percentage = total * 100L / max;

        if (percentage >= percentageWarn)
        {

            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            try
            {
                if (FMLCommonHandler.instance().getEffectiveSide().isClient())
                {
                    OutputHandler.felog.info("High memory use detected. " + percentage + "% of memory in use.");
                }
                else
                {
                    OutputHandler.sendMessage(server, "[ForgeEssentials] High memory use detected. " + percentage + "% of memory in use.");
                }
                ServerConfigurationManager manager = server.getConfigurationManager();
                for (String username : manager.getAllUsernames())
                {
                    EntityPlayerMP player = manager.func_152612_a(username);
                    if (PermissionsManager.checkPermission(player, PERM_WARN))
                    {
                        OutputHandler.chatNotification(player, "[ForgeEssentials] High memory use detected. " + percentage + "% of memory in use.");
                    }
                }
            }
            catch (Exception e)
            {
            }
        }
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        percentageWarn = config.get(ForgeEssentials.CONFIG_CAT, "percentageWarn", 90, "Percentage at which to warn server ops").getInt(90);
        checkInterval = config.get(ForgeEssentials.CONFIG_CAT, "checkInterval", 5, "Interval in minutes to check memory use.").getInt(5);
    }

    /**
     * dummy methods below for the sake of the interface
     */

    @Override
    public void save(Configuration config){}

    @Override
    public boolean supportsCanonicalConfig()
    {
        return true;
    }
}

