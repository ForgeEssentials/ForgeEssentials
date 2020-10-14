package com.forgeessentials.perftools;

import java.text.DecimalFormat;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandServerPerf extends ForgeEssentialsCommandBase
{

    private static final DecimalFormat formatNumbers = new DecimalFormat("########0.000");

    @Override
    public String getPrimaryAlias()
    {
        return "perfstats";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        ChatOutputHandler.chatNotification(sender, "Memory usage:");
        ChatOutputHandler.chatNotification(sender, "Max: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MiB");
        ChatOutputHandler.chatNotification(sender, "Total: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MiB");
        ChatOutputHandler.chatNotification(sender, "Free: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MiB");
        long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        ChatOutputHandler.chatNotification(sender, "Used: " + (used / 1024 / 1024) + " MiB");
        ChatOutputHandler.chatNotification(sender,
                "Average tick time: " + formatNumbers.format(this.func_120035_a(server.tickTimeArray) * 1.0E-6D) + " ms");
        ChatOutputHandler.chatNotification(sender, "For TPS information, run /forge tps.");
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
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getUsage(ICommandSender sender)
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
