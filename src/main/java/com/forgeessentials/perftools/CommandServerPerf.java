package com.forgeessentials.perftools;

import java.text.DecimalFormat;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.ForgeEssentialsCommandBase;

public class CommandServerPerf extends ForgeEssentialsCommandBase
{

    private static final DecimalFormat formatNumbers = new DecimalFormat("########0.000");

    @Override
    public String getCommandName()
    {
        return "perfstats";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        ChatUtil.chatNotification(sender, "Memory usage:");
        ChatUtil.chatNotification(sender, "Max: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MiB");
        ChatUtil.chatNotification(sender, "Total: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MiB");
        ChatUtil.chatNotification(sender, "Free: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MiB");
        long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        ChatUtil.chatNotification(sender, "Used: " + (used / 1024 / 1024) + " MiB");
        ChatUtil.chatNotification(sender,
                "Average tick time: " + formatNumbers.format(this.func_120035_a(MinecraftServer.getServer().tickTimeArray) * 1.0E-6D) + " ms");
        ChatUtil.chatNotification(sender, "For TPS information, run /forge tps.");
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.perftools.perfstats";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/perfstats Displays server performance stats (memory usage, average tick time).";
    }

    private double func_120035_a(long[] p_120035_1_)
    {
        long i = 0L;

        for (int j = 0; j < p_120035_1_.length; ++j)
        {
            i += p_120035_1_[j];
        }

        return (double) i / (double) p_120035_1_.length;
    }
}
