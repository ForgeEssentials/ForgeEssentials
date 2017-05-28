package com.forgeessentials.perftools;

import java.util.TimerTask;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

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
                    LoggingHandler.felog.info("High memory use detected. " + percentage + "% of memory in use.");
                }
                else
                {
                    ChatOutputHandler.sendMessage(server, "[ForgeEssentials] High memory use detected. " + percentage + "% of memory in use.");
                }
                for (EntityPlayerMP player : ServerUtil.getPlayerList())
                    if (PermissionAPI.hasPermission(player, PerfToolsModule.PERM_WARN))
                        ChatOutputHandler.chatNotification(player, "[ForgeEssentials] High memory use detected. " + percentage + "% of memory in use.");
            }
            catch (Exception e)
            {
            }
        }
    }
}
