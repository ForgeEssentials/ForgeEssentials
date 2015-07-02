package com.forgeessentials.perftools;

import java.util.TimerTask;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Warns those with permission when the memory usage passes a certain percentage threshold
 */
public class MemoryWatchdog extends TimerTask
{
    public MemoryWatchdog()
    {
    }

    @Override
    public void run()
    {
        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        long percentage = total * 100L / max;

        if (percentage >= PerfToolsModule.percentageWarn)
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
                    if (PermissionsManager.checkPermission(player, PerfToolsModule.PERM_WARN))
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
}
